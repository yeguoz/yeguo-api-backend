package icu.yeguo.yeguoapi.model.dto.interfaceInfo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class InterfaceInfoRegisterRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -3231791236112426143L;
    private String name;
    private String description;
    private String method;
    private String url;
    private String requestParams;
    private String requestHeader;
    private String responseHeader;
    private String responseFormat;
    private String requestExample;
    private Integer interfaceStatus;
    private Long invokingCount;
    private String avatarUrl;
    private Long requiredGoldCoins;
}
