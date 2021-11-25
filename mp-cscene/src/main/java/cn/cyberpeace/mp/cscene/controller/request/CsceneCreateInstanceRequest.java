package cn.cyberpeace.mp.cscene.controller.request;

import cn.cyberpeace.mp.cscene.bo.CsceneInstanceCreateContextBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneCreateInstanceRequest extends CsceneActionRequest{
    @ApiModelProperty(value = "场景创建上下文")
    private CsceneInstanceCreateContextBO context;
}
