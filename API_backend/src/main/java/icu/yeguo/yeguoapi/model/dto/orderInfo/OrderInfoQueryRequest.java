package icu.yeguo.yeguoapi.model.dto.orderInfo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderInfoQueryRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -2966697823122678504L;
    private String orderId;
    private Long userId;
    private Integer payType;
    private BigDecimal money;
    private Integer payStatus;
}
