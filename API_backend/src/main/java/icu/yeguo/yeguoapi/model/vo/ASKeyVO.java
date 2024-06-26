package icu.yeguo.yeguoapi.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class ASKeyVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6886813340858415944L;
    private String accessKey;
    private String secretKey;
    public ASKeyVO(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }
}
