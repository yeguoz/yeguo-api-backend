package com.yeguo.yeguoapi.common;

import java.io.Serializable;

public class ResultUtils<T> implements Serializable {

    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    public static <T> Result<T> error (int code, String message,String description){
        return new Result<T>(code,message,description);
    }

    public static <T> Result<T> error (String message){
        return new Result<T>(message);
    }
}
