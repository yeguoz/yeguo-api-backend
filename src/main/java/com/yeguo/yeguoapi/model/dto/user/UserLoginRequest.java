package com.yeguo.yeguoapi.model.dto.user;


import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -6846646179385156192L;
    String userAccount;
    String userPassword;
}
