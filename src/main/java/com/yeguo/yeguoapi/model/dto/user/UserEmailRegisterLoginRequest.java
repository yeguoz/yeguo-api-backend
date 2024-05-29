package com.yeguo.yeguoapi.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class UserEmailRegisterLoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -4931638841591643277L;
    private String email;
    private String verifyCode;
}
