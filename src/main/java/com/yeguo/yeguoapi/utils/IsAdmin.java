package com.yeguo.yeguoapi.utils;

import com.yeguo.yeguoapi.common.ResponseCode;
import com.yeguo.yeguoapi.constant.UserConstant;
import com.yeguo.yeguoapi.exception.BusinessException;
import com.yeguo.yeguoapi.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class IsAdmin {

    public static boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession();
        UserVO currentUser = (UserVO)session.getAttribute(UserConstant.USER_LOGIN_STATE);
        if (currentUser == null) throw new BusinessException(ResponseCode.NOT_LOGIN_ERROR, "您当前未登录");
        return currentUser.getUserRole() == UserConstant.ADMIN_ROLE;
    }

}
