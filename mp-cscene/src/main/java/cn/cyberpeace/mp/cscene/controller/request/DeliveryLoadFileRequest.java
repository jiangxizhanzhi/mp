package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeliveryLoadFileRequest {

    @ApiModelProperty(value = "启动场景id")
    private String sceneId;

    @ApiModelProperty(value = "载荷节点id")
    private String loadNodeId;

    @ApiModelProperty(value = "目标节点id")
    private String targetNodeId;
}
