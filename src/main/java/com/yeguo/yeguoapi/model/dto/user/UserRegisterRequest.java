package com.yeguo.yeguoapi.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 4150283845435181444L;
    String username;
    String userAccount;
    String userPassword;
    String checkPassword;

}
