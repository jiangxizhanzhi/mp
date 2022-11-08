package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneInstanceNodeActionRequest extends CsceneInstanceActionRequest {
    @ApiModelProperty(value = "节点id")
    private String nodeId;
}
