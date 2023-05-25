package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: 流量任务配置
 * @Author: guquan
 * @Date：2023/5/9 16:03
 */
@Data
@ApiModel("流量任务配置")
public class FlowTaskConf {

    /**
     * 通用配置
     */
    @ApiModelProperty(value = "通用配置")
    private FlowTaskCommonConf commonConf;

    /**
     * 速率配置
     */
    @ApiModelProperty(value = "速率配置")
    private FlowTaskRateConf rateConf;

    /**
     * 流配置
     */
    @ApiModelProperty(value = "流配置")
    private FlowTaskFlowConf flowConf;

    /**
     * 攻击流量参数配置
     */
    @ApiModelProperty(value = "攻击流量参数配置")
    private List<FlowTaskAttackConf> attackConfs;
}
