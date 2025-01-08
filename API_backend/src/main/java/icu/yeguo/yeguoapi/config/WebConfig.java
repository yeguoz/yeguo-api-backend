package icu.yeguo.yeguoapi.config;

import icu.yeguo.yeguoapi.Interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册自定义拦截器并指定拦截路径
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register",
                        "/api/user/verifyCode",
                        "/api/user/emailRegister",
                        "/api/user/emailLogin",
                        "/api/user/forgetPwd/verifyCode",
                        "/api/user/forgetPwd",
                        "/doc.html",          // 去掉 /api，Swagger 文档路径通常是直接 /doc.html
                        "/v3/**",              // Swagger 的接口文档通常在 /v3/** 下
                        "/api/error",
                        "/error"
                );
    }
}
