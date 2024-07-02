package cn.cyberpeace.mp.cscene.controller.request;

import cn.cyberpeace.mp.scenepluginflow.plugin.params.FlowIdWithSourceIp;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName CsceneInstanceNodeFlowsActionRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CsceneInstanceNodeActionRequest
 */
@Data
public class CsceneInstanceNodeFlowsActionRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "流量集合")
    private List<FlowIdWithSourceIp> flows;
    @ApiModelProperty(value = "执行方式：0 串行，1 并行")
    private int runType;
}
