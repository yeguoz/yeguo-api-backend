package icu.yeguo.apigateway;

import icu.yeguo.apicommon.model.entity.User;
import icu.yeguo.apicommon.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局过滤器
 */
@Component
@Slf4j
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    private static final String GATEWAY = "http://localhost:8082";
    @DubboReference
    private CommonService commonService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1、请求日志
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求id:" + request.getId());
        log.info("请求方法:" + request.getMethod());
        log.info("请求接口URI:" + request.getURI());
        log.info("请求路径:" + request.getPath());
        log.info("请求Cookies:" + request.getCookies());
        log.info("请求参数:" + request.getQueryParams());
        log.info("本地网关地址:" + request.getLocalAddress());
        log.info("远程请求地址:" + request.getRemoteAddress());
        log.info("请求头:" + request.getHeaders());
        log.info("请求体:" + request.getBody());

        String requestUrl = GATEWAY+request.getPath();
        log.info("请求接口URL:" + requestUrl);
        Long interfaceInfoId = commonService.getInterfaceInfoId(requestUrl);
        if (interfaceInfoId == null) {
            log.error("接口不存在");
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            return exchange.getResponse().setComplete();
        }
        // 2、请求参数处理，日志记录
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        String accessKey = queryParams.getFirst("accessKey");
        String signature = queryParams.getFirst("signature");

        log.info("accessKey:" + accessKey);
        log.info("signature:" + signature);
        // 3、访问控制-黑白名单
        // 4、用户鉴权将accessKey设置为请求参数传过来，查询数据库用户，进行验证权限，如此签名可以再加入写用户信息，通过查表对其签名验证
        // 查询数据库，查询用户sk， 远程调用
        User user = commonService.getUser(accessKey);
        if (user == null) {
            log.info("用户不存在");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 将ak和sk拼接起来进行签名
        String message = user.getAccessKey() + user.getSecretKey();
        String generatedSignature = commonService.generateSignature(message);
        log.info("服务端验证签名:" + generatedSignature);

        // 验证签名和前端传过来的是否相同
        if (!generatedSignature.equals(signature)) {
            log.info("签名不一致");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 响应处理
        return handleResponse(exchange, chain, interfaceInfoId);
    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = (HttpStatus) originalResponse.getStatusCode();
            //5、响应日志
            log.info("响应日志 ============================================");
            log.info("HttpStatusCode:" + statusCode);
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        //6、调用成功，接口调用计数
                                        //todo 这里可以优化
                                        if (data.contains("\"code\":200")) {
                                            Long count = commonService.invokingCount(interfaceInfoId);
                                            if (count < 0) {
                                                log.warn("接口调用次数更新失败====接口id为：" + interfaceInfoId);
                                            }
                                            log.info("接口"+interfaceInfoId+"调用次数："+count);
                                            // todo 用户调用成功后，扣除相应金币
                                        }
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常", e);
            return chain.filter(exchange);
        }
    }

    // 数字越小，优先级越高
    @Override
    public int getOrder() {
        return -1;
    }
}
