package com.yeguo.yeguoapi.common;

public enum ResponseCode {

    SUCCESS(20000,"成功"),
    PARAMS_ERROR(40000, "请求参数错误"),
    USER_EXIST_ERROR(40001, "请求参数错误"),
    NOT_LOGIN_ERROR(40100,"未登录"),
    NO_AUTH_ERROR(40101,"无权限"),
    FORBIDDEN_ERROR(40300,"禁止访问"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败");
    ;
    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
