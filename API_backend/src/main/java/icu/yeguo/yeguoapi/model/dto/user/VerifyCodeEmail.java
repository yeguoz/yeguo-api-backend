package icu.yeguo.yeguoapi.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class VerifyCodeEmail implements Serializable {

    @Serial
    private static final long serialVersionUID = -3496860085474757788L;
    private String email;
}
