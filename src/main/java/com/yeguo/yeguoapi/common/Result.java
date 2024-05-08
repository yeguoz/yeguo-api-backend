package com.yeguo.yeguoapi.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private int code;
    private T data;
    private String message;

    public Result(T data) {
        this.code = 20000;
        this.data = data;
        this.message = "成功";
    }

    public Result(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }


}
