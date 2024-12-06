package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CsceneFlowTaskRequest {
    @ApiModelProperty(value = "场景模板id")
    private String id;
    @ApiModelProperty(value = "节点id")
    private String nodeId;
    @ApiModelProperty(value = "流量任务列表")
    private List<FlowTaskRequest> tasks;
}
