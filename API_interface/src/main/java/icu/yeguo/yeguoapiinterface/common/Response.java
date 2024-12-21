package icu.yeguo.yeguoapiinterface.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Response<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -3481618647530458478L;
    private int code;
    private T result;
    private String msg;
}
