package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneInstanceNodeFlowActionRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "流量id")
    private String flowId;
    @ApiModelProperty(value = "源ip")
    private String sourceIp;
}
