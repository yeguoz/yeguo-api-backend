package icu.yeguo.yeguoapi.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -2138796121058592185L;
    private Long id;
    private String username;
    private String userAccount;
    private String avatarUrl;
    private Integer gender;
    private String phone;
    private String email;
    private Long goldCoin;
    private String accessKey;
    private String secretKey;
    private Integer userStatus;
    private Integer userRole;
    private Date createTime;


}
