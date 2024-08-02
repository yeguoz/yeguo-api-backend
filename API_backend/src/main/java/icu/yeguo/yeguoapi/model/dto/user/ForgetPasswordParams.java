package icu.yeguo.yeguoapi.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class ForgetPasswordParams implements Serializable {
    @Serial
    private static final long serialVersionUID = -6264542478444327908L;
    private String email;
    private String verifyCode;
    private String newPassword;
    private String checkNewPassword;
}
