package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneSyncRequest {
    @ApiModelProperty(value = "场景拓扑id")
    private String id;
    @ApiModelProperty(value = "区域id")
    private String areaId;
    @ApiModelProperty(value = "场景模板id")
    private String templateId;
}
