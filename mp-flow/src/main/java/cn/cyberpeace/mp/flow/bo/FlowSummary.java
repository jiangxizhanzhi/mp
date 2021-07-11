package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("仿真概况")
public class FlowSummary {

    
    @ApiModelProperty(value = "仿真任务数")
    private Integer taskNum;

    
    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    
    @ApiModelProperty(value = "失败次数")
    private Integer errorCount;

    
    @ApiModelProperty(value = "已执行次数")
    private Integer runnedCount;

    
    @ApiModelProperty(value = "目标设备数")
    private Integer totalNode;

    
    @ApiModelProperty(value = "当前启动任务数")
    private Integer runningTask;

    
    @ApiModelProperty(value = "当前执行目标数")
    private Integer runningNode;
}
