package cn.cyberpeace.mp.behavior.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TimerRes implements Serializable {

    @ApiModelProperty("仪表id")
    private String simulateId;

    @ApiModelProperty("任务信息")
    private List<TaskRes> taskResList;
}
