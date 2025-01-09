package icu.yeguo.yeguoapi.config;

import icu.yeguo.yeguoapi.Interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册自定义拦截器并指定拦截路径
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/user/verifyCode",
                        "/user/emailRegister",
                        "/user/emailLogin",
                        "/user/forgetPwd/verifyCode",
                        "/user/forgetPwd",
                        "/v3/**",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-ui.html",
                        "/error"
                );

    }
}
