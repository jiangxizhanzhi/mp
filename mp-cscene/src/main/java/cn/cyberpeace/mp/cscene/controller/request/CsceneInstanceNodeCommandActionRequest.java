package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneInstanceNodeCommandActionRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "执行命令")
    private String command;
    @ApiModelProperty(value = "执行命令的用户")
    private String execUser;
    @ApiModelProperty(value = "载荷节点")
    private String loadNodeId;
    @ApiModelProperty(value = "ojj实例id")
    private String resourceId;
    @ApiModelProperty(value = "场景id")
    private String scene;
}
