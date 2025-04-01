package cn.cyberpeace.mp.behavior.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TaskPageRes implements Serializable {

    @ApiModelProperty("任务id")
    private String id;

    @ApiModelProperty("行为名称")
    private String name;

    @ApiModelProperty("行为描述")
    private String description;

    @ApiModelProperty("目标服务")
    private String target;

    @ApiModelProperty("步骤")
    private Integer step;

    @ApiModelProperty("运行时间")
    private String runTime;
}
