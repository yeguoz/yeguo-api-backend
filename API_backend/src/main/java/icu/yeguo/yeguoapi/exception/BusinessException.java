package icu.yeguo.yeguoapi.exception;


import icu.yeguo.yeguoapi.common.ResponseCode;
import lombok.Getter;

/**
 * 业务异常类
 * @author yeguo
 */
@Getter
public class BusinessException extends RuntimeException{

    private final int code;
    private final String description;

    public BusinessException(int code, String message, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ResponseCode responseCode, String description) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
        this.description = description;
    }

}
