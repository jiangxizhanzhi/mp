package cn.cyberpeace.mp.flow.request.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutputCsceneInstanceFlowTaskRequest {
    @ApiModelProperty(value = "场景实例id")
    private String id;
    @ApiModelProperty(value = "场景节点id")
    private String nodeId;
    @ApiModelProperty(value = "页码")
    private Integer page;
    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;
}
