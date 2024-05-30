package icu.yeguo.yeguoapi.model.dto.interfaceInfo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class InterfaceInfoQueryRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1730918751267007184L;
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String method;
    private String url;
    private String responseFormat;
    private Long invokingCount;
    private Long requiredGoldCoins;
}
