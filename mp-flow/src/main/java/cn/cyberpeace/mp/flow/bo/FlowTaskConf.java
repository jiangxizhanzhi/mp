 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowTaskConf.java.
 // File size 907.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName FlowTaskConf
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
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
