package com.yeguo.yeguoapi.exception;


import com.yeguo.yeguoapi.common.ErrorCode;

/**
 * 业务异常类
 * @author yeguo
 */
public class BusinessException extends RuntimeException{

    private final int code;
    private final String description;

    public BusinessException(int code, String message, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

}
