package icu.yeguo.yeguoapi.model.dto.orderInfo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LeavePay {
    private int code;
    private String msg;
    private String type;
    private BigDecimal money;
    private String url;
    private String qrcode;
    private String orderid;
}
