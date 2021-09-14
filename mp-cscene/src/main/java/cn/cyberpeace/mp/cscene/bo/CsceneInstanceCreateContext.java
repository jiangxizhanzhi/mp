package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class CsceneInstanceCreateContext {

    @ApiModelProperty(value = "场景存活时间")
    private Integer sceneTtl;

    @ApiModelProperty(value = "场景属性")
    private String sceneAttr;

    @ApiModelProperty(value = "透明上下文")
    private Map<String, Object> context;
}
