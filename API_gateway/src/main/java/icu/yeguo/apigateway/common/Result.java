package icu.yeguo.apigateway.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -3481618647530458478L;
    private int code;
    private T result;
    private String msg;
}
