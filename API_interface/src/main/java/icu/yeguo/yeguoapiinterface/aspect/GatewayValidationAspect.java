package icu.yeguo.yeguoapiinterface.aspect;

import cn.hutool.json.JSONUtil;
import icu.yeguo.yeguoapiinterface.common.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import static icu.yeguo.yeguoapiinterface.constant.commonConstant.SOURCE;
import static icu.yeguo.yeguoapiinterface.constant.commonConstant.YEGUO_API_GATEWAY;

@Slf4j
@Aspect
@Component
@RequestScope // 确保每次请求都会有一个新的 HttpServletRequest 实例
public class GatewayValidationAspect {

    @Autowired
    private HttpServletRequest request;

    @Around("execution(* icu.yeguo.yeguoapiinterface.controller..*.*(..))")
    public Object validateRequestSource(ProceedingJoinPoint joinPoint) throws Throwable {
        String source = request.getHeader(SOURCE);
        log.info("请求源校验：{}", source);
        if (source == null || !source.equals(YEGUO_API_GATEWAY)) {
            log.info("请求源不合法：{}", source);
            Response<Object> resp = new Response<>(HttpServletResponse.SC_BAD_REQUEST, null, "请求来源不合法");
            return JSONUtil.toJsonStr(resp);
        }
        log.info("请求源合法：{}", source);
        return joinPoint.proceed();
    }
}
