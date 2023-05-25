package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 仿真概况
 * @Author: guquan
 * @Date：2023/5/16 16:45
 */
@Data
@ApiModel("仿真概况")
public class FlowSummary {

    /**
     * 仿真任务数
     */
    @ApiModelProperty(value = "仿真任务数")
    private Integer taskNum;

    /**
     * 成功次数
     */
    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    /**
     * 失败次数
     */
    @ApiModelProperty(value = "失败次数")
    private Integer errorCount;

    /**
     * 已执行次数
     */
    @ApiModelProperty(value = "已执行次数")
    private Integer runnedCount;

    /**
     * 目标设备数
     */
    @ApiModelProperty(value = "目标设备数")
    private Integer totalNode;

    /**
     * 当前启动任务数
     */
    @ApiModelProperty(value = "当前启动任务数")
    private Integer runningTask;

    /**
     * 当前执行目标数
     */
    @ApiModelProperty(value = "当前执行目标数")
    private Integer runningNode;
}
