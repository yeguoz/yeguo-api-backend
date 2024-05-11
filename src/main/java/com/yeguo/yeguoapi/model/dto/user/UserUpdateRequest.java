package com.yeguo.yeguoapi.model.dto.user;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserUpdateRequest implements Serializable {

    private static final long serialVersionUID = -7460748138426337554L;
    private Long id;
    private String username;
    private String userAccount;
    private String avatarUrl;
    private Integer gender;
    private String phone;
    private String email;
    private Long goldCoin;
    private Integer userStatus;
    private Integer userRole;

}
