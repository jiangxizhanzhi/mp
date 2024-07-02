package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName CsceneInstanceNodeCommandActionRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CsceneInstanceNodeActionRequest
 */
@Data
public class CsceneInstanceNodeCommandActionRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "执行命令")
    private String command;
    @ApiModelProperty(value = "执行命令的用户")
    private String execUser;
}
