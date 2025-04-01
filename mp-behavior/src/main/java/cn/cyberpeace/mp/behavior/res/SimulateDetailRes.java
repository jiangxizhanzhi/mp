package cn.cyberpeace.mp.behavior.res;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SimulateDetailRes implements Serializable {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("实验任务名称")
    private String sceneName;

    @ApiModelProperty("节点ip")
    private String ip;

//    @ApiModelProperty("开始时间")
//    private String startTime;
//
//    @ApiModelProperty("结束时间")
//    private String endTime;

    @ApiModelProperty("行为列表")
    private List<TaskPageRes> behaviorList;
}
