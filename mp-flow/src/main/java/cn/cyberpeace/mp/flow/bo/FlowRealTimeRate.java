package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("实时速率统计对象")
public class FlowRealTimeRate {

    
    @ApiModelProperty(value = "任务id")
    private String taskId;

    
    @ApiModelProperty(value = "统计时间")
    private String statisticalTime;

    
    @ApiModelProperty(value = "相对于当前时间的秒数")
    private Integer second;

    
    @ApiModelProperty(value = "实时速率（单位byte/s）")
    private Double txBps;

}
