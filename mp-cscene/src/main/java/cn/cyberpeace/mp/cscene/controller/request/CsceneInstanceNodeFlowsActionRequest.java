package cn.cyberpeace.mp.cscene.controller.request;

import cn.cyberpeace.mp.scenepluginflow.plugin.params.FlowIdWithSourceIp;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CsceneInstanceNodeFlowsActionRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "流量集合")
    private List<FlowIdWithSourceIp> flows;
    @ApiModelProperty(value = "执行方式：0 串行，1 并行")
    private int runType;
    @ApiModelProperty(value = "是否启动节点全部流量")
    private Boolean isAll;
}
