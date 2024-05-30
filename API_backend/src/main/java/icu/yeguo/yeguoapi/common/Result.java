package icu.yeguo.yeguoapi.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private int code;
    private T data;
    private String message;
    private String description;

    public Result(T data) {
        this.code = ResponseCode.SUCCESS.getCode();
        this.data = data;
        this.message = ResponseCode.SUCCESS.getMessage();
    }

    public Result(int code, T data, String message,String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public Result(int code,String message,String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(String message) {
        this.message = message;
    }
}
