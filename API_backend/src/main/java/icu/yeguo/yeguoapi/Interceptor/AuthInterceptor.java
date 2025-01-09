package icu.yeguo.yeguoapi.Interceptor;

import icu.yeguo.yeguoapi.common.ResponseCode;
import icu.yeguo.yeguoapi.constant.UserConstant;
import icu.yeguo.yeguoapi.exception.BusinessException;
import icu.yeguo.yeguoapi.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {
        // 在处理器方法执行前执行的逻辑
        log.info("Login Interceptor PreHandle: 请求路径 -> {}", request.getRequestURI());

        HttpSession session = request.getSession();
        UserVO currentUser = (UserVO)session.getAttribute(UserConstant.USER_LOGIN_STATE);
        if (currentUser == null) throw new BusinessException(ResponseCode.NOT_LOGIN_ERROR, "您当前未登录");

        return true;
    }
}
