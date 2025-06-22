package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel(description = "下发场景模板数据")
@Data
public class ReleaseCsceneBO implements Serializable {
    @ApiModelProperty(value = "任务ID")
    private String taskId;
    @ApiModelProperty(value = "节点信息")
    private List<ReleaseCsceneNodeBO> nodes;

    @ApiModelProperty(value = "连线信息")
    private List<ReleaseCsceneLinkBO> links;

}
