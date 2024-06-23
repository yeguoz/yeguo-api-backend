package icu.yeguo.apigateway;

import com.alibaba.fastjson2.JSON;
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

    @DubboReference
    private CommonService commonService;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1、请求日志
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求id:"+request.getId());
        log.info("请求方法:"+request.getMethod());
        log.info("请求接口URI:"+request.getURI());
        log.info("请求路径:"+request.getPath());
        log.info("请求Cookies:"+request.getCookies());
        log.info("请求参数:"+request.getQueryParams());
        log.info("本地网关地址:"+request.getLocalAddress());
        log.info("远程请求地址:"+request.getRemoteAddress());
        log.info("请求头:"+request.getHeaders());
        log.info("请求体:"+request.getBody());
        //2、访问控制-黑白名单
        //3、用户鉴权
        log.info("X-InterfaceInfoId:"+request.getHeaders().getFirst("X-InterfaceInfoId"));
        log.info("X-AccessKey:"+request.getHeaders().getFirst("X-AccessKey"));
        log.info("X-Signature:"+request.getHeaders().getFirst("X-Signature"));
        long x_interfaceInfoId = Long.parseLong(request.getHeaders().getFirst("X-InterfaceInfoId"));
        String x_accessKey = request.getHeaders().getFirst("X-AccessKey");
        String x_signature = request.getHeaders().getFirst("X-Signature");
        // 将accessKey设置为请求头传过来，查询数据库用户，进行验证权限，如此签名可以再加入写用户信息，通过查表对其签名验证
        // 查询数据库 远程调用
        User user = null;
        try {
            user = commonService.getUser(x_accessKey);
        } catch (Exception e) {
            log.error("commonService getUser Error"+e);
        }

        if (user == null) {
            log.info("用户不存在");
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        // 将ak和sk拼接起来进行签名
        String message = user.getAccessKey() + user.getSecretKey();
        String signature = commonService.generateSignature(message);
        log.info("验证签名:"+signature);
        // 验证签名和前端传过来的是否相同
        if (!signature.equals(x_signature)) {
            log.info("签名不一致");
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        // 响应处理
        return handleResponse(exchange, chain,x_interfaceInfoId);

    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain,long interfaceInfoId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = (HttpStatus) originalResponse.getStatusCode();
            //5、响应日志
            log.info("响应日志 ============================================");
            log.info("HttpStatusCode:"+statusCode);
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
                                        if (data.contains("\"code\":200")) {
                                            commonService.invokingCount(interfaceInfoId);
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
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }


    // 数字越小，优先级越高
    @Override
    public int getOrder() {
        return -1;
    }
}