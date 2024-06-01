package icu.yeguo.yeguoapi.model.dto.interfaceInfo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class InvokingRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -3812707519447161679L;
    private String method;
    private ReqParams[] pl;

}
