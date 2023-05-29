package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SceneAllElementsQueryRequest {
    @ApiModelProperty(value = "节点名称 模糊查询")
    private String name;
    @ApiModelProperty(value = "设备类型")
    private Integer category;
    @ApiModelProperty(value = "操作系统名称")
    private String systemTypeName;
}
