package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeleteNodeCommand {
    @ApiModelProperty(value = "场景id")
    private String sceneId;
    @ApiModelProperty("节点id,当前场景内唯一")
    private String nodeId;
    @ApiModelProperty("节点命令功能标识，不同功能需不同的key")
    private String businessKey;
}