package icu.yeguo.yeguoapi.model.dto.orderInfo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CreateOrderInfoRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -7354193020430505213L;
    private Long userId;
    private Integer payType;
    private BigDecimal money;
    private String commodityContent;
}
