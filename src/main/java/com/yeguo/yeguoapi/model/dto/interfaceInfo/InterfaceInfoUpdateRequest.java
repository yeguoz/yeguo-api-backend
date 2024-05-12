package com.yeguo.yeguoapi.model.dto.interfaceInfo;

import lombok.Data;

import java.io.Serializable;

@Data
public class InterfaceInfoUpdateRequest implements Serializable {

    private static final long serialVersionUID = 860450932653624217L;
    private Long id;
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
