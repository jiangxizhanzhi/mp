package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@ApiModel("流量统计条件")
public class FlowStatisticalCondition {

    
    @ApiModelProperty(value = "开始统计时间")
    private LocalDateTime startTime;

    
    @ApiModelProperty(value = "结束统计时间")
    private LocalDateTime endTime;

    
    @ApiModelProperty(value = "相对于当前时间的偏移值")
    private Integer offsetValue;

    
    @ApiModelProperty(value = "相对于当前时间的偏移单位")
    private String offsetUnit;
}
