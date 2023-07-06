package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SceneAllElementsQueryRequest {
    @ApiModelProperty(value = "节点名称 模糊查询")
    private String name;
    @ApiModelProperty(value = "设备类型")
    private Integer category;
    @ApiModelProperty(value = "操作系统名称")
    private String systemTypeName;
    @ApiModelProperty(value = "节点id集合")
    private List<String> elementIds;
    @ApiModelProperty(value = "是否返回场景全部节点（包括简易型）")
    private Boolean isAll;
    @ApiModelProperty(value = "是否采集")
    private Boolean isCollect;
    @ApiModelProperty(value = "是否包含删除中的节点")
    private Boolean includeDeleting;
}
