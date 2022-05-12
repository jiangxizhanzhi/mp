package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CsceneInstanceNodeFlowsActionRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "流量id集合")
    private List<String> flowIds;
    @ApiModelProperty(value = "执行方式：0 串行，1 并行")
    private int runType;
}
