package icu.yeguo.yeguoapi.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderInfoVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 3489615638288632219L;
    private String orderId;
    private Long userId;
    private Integer payType;
    private BigDecimal money;
    private Integer payStatus;
    private Date createTime;
}
