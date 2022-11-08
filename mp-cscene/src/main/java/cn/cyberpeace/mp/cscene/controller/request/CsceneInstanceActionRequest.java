package cn.cyberpeace.mp.cscene.controller.request;

import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneInstanceActionRequest {
    @ApiModelProperty(value = "场景id")
    private String id;
    @ApiModelProperty(value = "场景上下文")
    private CsceneContextBO context;
}
