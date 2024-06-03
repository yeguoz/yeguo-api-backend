package icu.yeguo.yeguoapi.utils;

import icu.yeguo.yeguoapi.common.ResponseCode;
import icu.yeguo.yeguoapi.constant.UserConstant;
import icu.yeguo.yeguoapi.exception.BusinessException;
import icu.yeguo.yeguoapi.model.vo.UserVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


public class IsAdminUtil {

    public static boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession();
        UserVO currentUser = (UserVO)session.getAttribute(UserConstant.USER_LOGIN_STATE);
        if (currentUser == null) throw new BusinessException(ResponseCode.NOT_LOGIN_ERROR, "您当前未登录");
        return currentUser.getUserRole() == UserConstant.ADMIN_ROLE;
    }

}
