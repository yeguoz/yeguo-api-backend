package icu.yeguo.apigateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * 全局过滤器
 */
@Component
@Slf4j
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // todo 请求
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
        log.info("X-Signature:"+request.getHeaders().getFirst("X-Signature"));
        // 将accessKey设置为请求头传过来，查询数据库用户，进行验证权限，如此签名可以再加入写用户信息，通过查表对其签名验证
        //4、请求接口
        // todo 响应
        //5、响应日志
        //6、接口调用计数
        // 放行
        return chain.filter(exchange);
    }

    // 数字越小，优先级越高
    @Override
    public int getOrder() {
        return -1;
    }
}