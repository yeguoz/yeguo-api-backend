package icu.yeguo.yeguoapiinterface.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -3481618647530458478L;
    private int code;
    private T result;
    private String msg;
}
