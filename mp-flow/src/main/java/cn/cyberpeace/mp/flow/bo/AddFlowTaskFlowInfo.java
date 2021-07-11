package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel(value = "新增流量任务流量详情")
public class AddFlowTaskFlowInfo {

    
    @ApiModelProperty(value = "流量id")
    private String flowId;

    
    @ApiModelProperty(value = "攻击流量参数配置")
    private List<FlowTaskAttackConf> attackConfs;
}
