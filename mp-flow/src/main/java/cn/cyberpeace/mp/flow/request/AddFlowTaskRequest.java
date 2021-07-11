package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel(value = "新增流量任务请求")
public class AddFlowTaskRequest extends FlowTaskModel {

    
    @ApiModelProperty(value = "网络id")
    private String networkId;

    
    @ApiModelProperty(value = "攻击脚本运行参数")
    private List<FlowScriptParamModel> scriptParamModels;

}
