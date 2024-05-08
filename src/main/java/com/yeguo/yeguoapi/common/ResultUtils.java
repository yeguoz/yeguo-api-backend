package com.yeguo.yeguoapi.common;

import java.io.Serializable;

public class ResultUtils<T> implements Serializable {

    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }
}
