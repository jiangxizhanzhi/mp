package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("流量任务配置")
public class FlowTaskConf {

    
    @ApiModelProperty(value = "通用配置")
    private FlowTaskCommonConf commonConf;

    
    @ApiModelProperty(value = "速率配置")
    private FlowTaskRateConf rateConf;

    
    @ApiModelProperty(value = "流配置")
    private FlowTaskFlowConf flowConf;

    
    @ApiModelProperty(value = "攻击流量参数配置")
    private List<FlowTaskAttackConf> attackConfs;
}
