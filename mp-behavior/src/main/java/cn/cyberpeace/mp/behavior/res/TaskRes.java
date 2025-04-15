package cn.cyberpeace.mp.behavior.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TaskRes implements Serializable {

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("队列名称")
    private String queueName;

    @ApiModelProperty("时间")
    private String time;
}
