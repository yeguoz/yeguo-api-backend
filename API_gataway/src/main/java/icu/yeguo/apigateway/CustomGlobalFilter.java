package icu.yeguo.apigateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // todo 请求
        //1、请求日志
        //2、访问控制-黑白名单
        //3、用户鉴权
        //4、请求接口
        // todo 响应
        //5、响应日志
        //6、接口调用计数
        log.info("custom global filter");
        return chain.filter(exchange);
    }

    // 数字越小，优先级越高
    @Override
    public int getOrder() {
        return -1;
    }
}