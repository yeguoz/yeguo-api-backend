package icu.yeguo.yeguoapi.model.dto.interfaceInfo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class InvokingRequestParams implements Serializable {
    @Serial
    private static final long serialVersionUID = 5865897477172175107L;
    private Long id;
    private Integer index;
    private String name;
    private String value;
}
