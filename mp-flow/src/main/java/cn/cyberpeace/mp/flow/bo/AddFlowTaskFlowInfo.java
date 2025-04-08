 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AddFlowTaskFlowInfo.java.
 // File size 667.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AddFlowTaskFlowInfo
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@ApiModel(value = "新增流量任务流量详情")
public class AddFlowTaskFlowInfo {

    @ApiModelProperty(value = "任务id")
    private String taskId;
    /**
     * 流量id
     */
    @ApiModelProperty(value = "流量id")
    private String flowId;

    @ApiModelProperty("背景流量速率(单位 mbp/s)")
    private Integer rate;

    @ApiModelProperty("带宽(单位 mbp/s)")
    private Integer bandwidth;

    /**
     * 攻击流量参数配置
     */
    @ApiModelProperty(value = "攻击流量参数配置")
    private List<FlowTaskAttackConf> attackConfs;
}
