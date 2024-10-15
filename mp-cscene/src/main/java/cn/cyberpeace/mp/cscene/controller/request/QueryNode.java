package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryNode {
    @ApiModelProperty(value = "场景id")
    private String sceneId;
    @ApiModelProperty("节点id,当前场景内唯一")
    private String nodeId;
}