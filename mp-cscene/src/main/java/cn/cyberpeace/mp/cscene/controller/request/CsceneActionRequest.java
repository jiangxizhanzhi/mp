package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneActionRequest {
    @ApiModelProperty(value = "场景模板id")
    private String id;
//    @ApiModelProperty(value = "场景上下文")
//    private CsceneContextBO context;
}
