package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneInstanceNodeAccessModeActionRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "接入方式id")
    private String accessModeId;
}
