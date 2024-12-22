package icu.yeguo.apigateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import icu.yeguo.apicommon.model.entity.User;
import icu.yeguo.apicommon.service.CommonService;
import icu.yeguo.apigateway.common.Result;
import icu.yeguo.apigateway.common.ResultUtil;
import icu.yeguo.apigateway.util.NonceUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static icu.yeguo.apicommon.constant.common.*;

@Component
@Slf4j
@Order(-1)
public class CustomGlobalFilter implements GlobalFilter {
    @Value("${yeguo.gateway.base-url}")
    private String baseUrl;
    private static final Pattern SVG_PATTERN = Pattern.compile(
            "<svg[^>]*xmlns=\"http://www.w3.org/2000/svg\"[^>]*>",
            Pattern.CASE_INSENSITIVE
    );
    @DubboReference
    private CommonService commonService;
    @Autowired
    private NonceUtil nonceUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ServerHttpRequest request = exchange.getRequest();
            String accessKey = request.getHeaders().getFirst(X_ACCESS_KEY);
            String signature = request.getHeaders().getFirst(X_SIGNATURE);
            // 日志记录
            logRequestDetails(request);
            // 校验请求接口是否存在
            String requestUrl = baseUrl + request.getPath();
            log.info("请求接口URL:{}", requestUrl);
            Long interfaceInfoId = commonService.getInterfaceInfoId(requestUrl);
            if (interfaceInfoId == null) {
                log.error("接口不存在");
                exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                return exchange.getResponse().setComplete();
            }

            // POST 请求处理
            if (HttpMethod.POST.equals(request.getMethod())) {
                return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    String bodyString = new String(bytes, StandardCharsets.UTF_8);
                    log.info("requestBody = {}", bodyString.substring(0, 200));

                    if (accessKey == null || signature == null) {
                        log.error("POST请求头中缺少X_ACCESS_KEY或X_SIGNATURE");
                        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                        return exchange.getResponse().setComplete();
                    }
                    log.info("POST请求请求头：X_Access_Key : {}", accessKey);
                    log.info("POST请求请求头：X_signature : {}", signature);
                    ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        @NonNull
                        public Flux<DataBuffer> getBody() {
                            return Flux.defer(() -> Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
                        }
                    };

                    return processRequest(exchange.mutate().request(mutatedRequest).build(), chain, accessKey,
                            signature, interfaceInfoId);
                });
            } else {
                // GET请求处理
                log.info("GET请求请求头：X_Access_Key : {}", accessKey);
                log.info("GET请求请求头：X_signature : {}", signature);
                return processRequest(exchange, chain, accessKey, signature, interfaceInfoId);
            }
        } catch (Exception e) {
            log.error("过滤器处理请求时发生异常", e);
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }
    }

    private void logRequestDetails(ServerHttpRequest request) {
        log.info("新的请求 =======================================================================================");
        log.info("请求id:{}", request.getId());
        log.info("请求方法:{}", request.getMethod());
        log.info("请求接口URI:{}", request.getURI());
        log.info("请求路径:{}", request.getPath());
        log.info("请求Cookies:{}", request.getCookies());
        log.info("请求参数:{}", request.getQueryParams());
        log.info("本地网关地址:{}", request.getLocalAddress());
        log.info("远程请求地址:{}", request.getRemoteAddress());
        log.info("请求头:{}", request.getHeaders());
    }

    private Mono<Void> processRequest(ServerWebExchange exchange, GatewayFilterChain chain,
                                      String accessKey, String signature, Long interfaceInfoId) {
        log.info("accessKey:{}", accessKey);
        log.info("signature:{}", signature);

        ServerHttpRequest request = exchange.getRequest();
        String X_Online_Invoking = request.getHeaders().getFirst(X_ONLINE_INVOKING);
        String timestamp = request.getHeaders().getFirst(X_TIMESTAMP);
        String nonce = request.getHeaders().getFirst(X_NONCE);

        try {
            User user = commonService.getUser(accessKey);
            if (user == null) {
                log.info("用户不存在");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // 校验时间戳是否过期
            if (timestamp != null && System.currentTimeMillis() > (Long.parseLong(timestamp) + 1000 * 60 * 5)) {
                log.info("请求已过期!(timestamp)");
                Result<Object> errorObj = ResultUtil.error(org.apache.http.HttpStatus.SC_REQUEST_TIMEOUT,
                        "该请求已超时！");
                exchange.getResponse().setStatusCode(HttpStatus.REQUEST_TIMEOUT);
                return exchange
                        .getResponse()
                        .writeWith(Mono.just(exchange.getResponse()
                                .bufferFactory()
                                // jackson Object--》byte[]
                                .wrap(new ObjectMapper().writeValueAsBytes(errorObj))));
            }

            // 校验nonce
            if (nonceUtil.isExist(nonce)) {
                log.info("nonce已被使用!");
                Result<Object> errorObj = ResultUtil.error(org.apache.http.HttpStatus.SC_REQUEST_TIMEOUT,
                        "该请求已失效！");
                exchange.getResponse().setStatusCode(HttpStatus.REQUEST_TIMEOUT);
                return exchange
                        .getResponse()
                        .writeWith(Mono.just(exchange.getResponse()
                                .bufferFactory()
                                // jackson Object--》byte[]
                                .wrap(new ObjectMapper().writeValueAsBytes(errorObj))));
            } else {
                // 将nonce存入redis 设置过期时间
                Boolean b = nonceUtil.setValue(nonce, 6 * 60);
                log.info("Has nonce been set?:{}", b);
            }

            // 构建签名串
            String message = request.getMethod() + "\n" +
                    request.getPath() + "\n" +
                    X_ACCESS_KEY + ":" + accessKey + "\n" +
                    X_TIMESTAMP + ":" + timestamp + "\n" +
                    X_NONCE + ":" + nonce;
            log.info("\n签名串：\n {}", message);

            String generatedSignature = commonService.generateSignature(message, user);
            log.info("服务端验证签名:{}", generatedSignature);

            if (!generatedSignature.equals(signature)) {
                log.info("签名不一致");
                Result<Object> errorObj = ResultUtil.error(org.apache.http.HttpStatus.SC_UNAUTHORIZED,
                        "签名不匹配，请求可能被篡改！");
                // 设置响应的内容
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange
                        .getResponse()
                        .writeWith(Mono.just(exchange.getResponse()
                                .bufferFactory()
                                // jackson Object--》byte[]
                                .wrap(new ObjectMapper().writeValueAsBytes(errorObj))));
            }

            // 没有该请求头 不是在线调用 扣金币
            if (X_Online_Invoking == null) {
                boolean success = deductGoldCoin(interfaceInfoId, user);
                // 失败
                if (!success) {
                    Result<Object> errorObj = ResultUtil.error(org.apache.http.HttpStatus.SC_UNAUTHORIZED,
                            "果币不足，请充值！");
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange
                            .getResponse()
                            .writeWith(Mono.just(exchange.getResponse()
                                    .bufferFactory()
                                    // jackson Object--》byte[]
                                    .wrap(new ObjectMapper().writeValueAsBytes(errorObj))));
                }
            }
            return handleResponse(exchange, chain, interfaceInfoId, user);
        } catch (Exception e) {
            log.error("处理请求时发生异常", e);
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }
    }

    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain,
                                      Long interfaceInfoId, User user) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            HttpStatus statusCode = (HttpStatus) originalResponse.getStatusCode();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();

            log.info("响应日志 =====================================================================================");
            log.info("HttpStatusCode:{}", statusCode);

            if (statusCode == HttpStatus.OK) {
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<DataBuffer> fluxBody = (Flux<DataBuffer>) body;

                            return DataBufferUtils
                                    .join(fluxBody)
                                    .flatMap(dataBuffer -> {
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);
                                        // 对响应进行处理和响应后操作
                                        String data = new String(content, StandardCharsets.UTF_8);
                                        log.info("响应结果：{}", data);
                                        // 接口计数，并扣费
                                        invoking(interfaceInfoId);
                                        return super.writeWith(Mono.just(bufferFactory.wrap(content)));
                                    });
                        } else {
                            return super.writeWith(body);
                        }
                    }

                };
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("处理响应时发生异常", e);
            return chain.filter(exchange);
        }
    }

    private boolean deductGoldCoin(Long interfaceInfoId, User user) {
        User updatedUser = null;
        try {
            log.info("扣除金币前：{}", user.getGoldCoin());
            updatedUser = commonService.deductGoldCoin(interfaceInfoId, user);
            if (updatedUser == null) {
                log.warn("{}:扣除金币失败", user.getId());
                return false;
            }
        } catch (Exception e) {
            log.error("扣除金币时发生异常", e);
        }
        if (updatedUser != null) {
            log.info("扣除金币后：{}", updatedUser.getGoldCoin());
        }
        return true;
    }

    private void invoking(Long interfaceInfoId) {
        try {
            Long count = commonService.invokingCount(interfaceInfoId);
            if (count < 0) {
                log.warn("接口调用次数更新失败====接口id为：{}", interfaceInfoId);
            }
            log.info("接口{}调用次数：{}", interfaceInfoId, count);
        } catch (Exception e) {
            log.error("调用接口时发生异常", e);
        }
    }

    private boolean isSvg(String content) {
        if (content == null) {
            return false;
        }
        // 使用正则表达式匹配内容
        Matcher matcher = SVG_PATTERN.matcher(content.trim());
        return matcher.find();
    }
}
