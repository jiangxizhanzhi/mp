package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneInstanceNodeCommandActionRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "执行命令")
    private String command;
    @ApiModelProperty(value = "执行命令的用户")
    private String execUser;
    // todo 跟传统云的kali攻击机绑定使用的参数 待开发
//    @ApiModelProperty(value = "云场景id")
//    private String sceneId;
}
