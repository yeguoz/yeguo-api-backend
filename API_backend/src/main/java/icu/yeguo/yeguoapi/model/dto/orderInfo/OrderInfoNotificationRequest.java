package icu.yeguo.yeguoapi.model.dto.orderInfo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderInfoNotificationRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 5117872805040424128L;
    private String orderId;
    private Long userId;
    private String commodityContent;
    private BigDecimal money;
    private Integer payType;
    private Date expireTime;
}
