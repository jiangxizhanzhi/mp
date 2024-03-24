package cn.cyberpeace.mp.cscene.controller.request;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneActionRequest implements SnakeHttpData {
    @ApiModelProperty(value = "场景模板id")
    private String id;
    @ApiModelProperty(value = "场景上下文")
    private CsceneContextBO context;
}
