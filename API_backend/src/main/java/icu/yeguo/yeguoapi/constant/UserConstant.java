package icu.yeguo.yeguoapi.constant;

public interface UserConstant {
    /**
     * 用户登录态key
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     * 用户邮箱注册验证码
     */
    String VERIFY_CODE = "userRegisterVerifyCode";


    /**
     * 用户邮箱注册验证码过期时间
     */
    String VERIFY_CODE_EXPIRATION_TIME = "expirationTime";

    /**
     * 普通用户
     */
    int DEFAULT_ROLE = 0;

    /**
     * 管理员
     */
    int ADMIN_ROLE = 1;
}
