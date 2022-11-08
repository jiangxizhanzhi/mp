package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneInstanceNodeConnectionActionRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "连接id")
    private String connectionId;
}
