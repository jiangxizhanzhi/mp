package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeleteNode {
    @ApiModelProperty(value = "场景id")
    private String sceneId;

    @ApiModelProperty("节点id")
    private String id;
}