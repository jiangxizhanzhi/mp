package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName AreaUpdateRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class AreaUpdateRequest {
    @ApiModelProperty(value = "场景id")
    private String sceneId;
    @ApiModelProperty(value = "区域id")
    private String areaId;
    @ApiModelProperty(value = "显示参数")
    private String displayContext;
}
