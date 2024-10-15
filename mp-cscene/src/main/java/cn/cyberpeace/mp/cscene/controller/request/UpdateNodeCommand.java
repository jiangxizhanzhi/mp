package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateNodeCommand {
    @ApiModelProperty(value = "场景id")
    private String sceneId;
    @ApiModelProperty("节点id,当前场景内唯一")
    private String nodeId;
    @ApiModelProperty("节点命令功能标识，不同功能需不同的key")
    private String businessKey;
    @ApiModelProperty("zip格式的附件地址")
    private String file;
    @ApiModelProperty("需要执行的命令，如果有zip附件，则会进入到解压之后的目录再执行该命令")
    private String command;
}