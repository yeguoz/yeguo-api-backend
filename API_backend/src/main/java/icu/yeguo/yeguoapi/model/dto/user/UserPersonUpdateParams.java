package icu.yeguo.yeguoapi.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class UserPersonUpdateParams implements Serializable {
    @Serial
    private static final long serialVersionUID = 3494663072571467870L;
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String avatarUrl;
}
