package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AreaUpdateRequest {
    @ApiModelProperty(value = "场景id")
    private String sceneId;
    @ApiModelProperty(value = "区域id")
    private String areaId;
    @ApiModelProperty(value = "显示参数")
    private String displayContext;
}
