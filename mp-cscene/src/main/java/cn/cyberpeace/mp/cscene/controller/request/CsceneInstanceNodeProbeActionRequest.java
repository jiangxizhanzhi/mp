package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneInstanceNodeProbeActionRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "探针id")
    private String probeId;
}
