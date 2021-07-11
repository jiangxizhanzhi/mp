package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("流量仿真统计详情")
public class FlowStatisticsDetail {

    
    @ApiModelProperty(value = "统计时间")
    private String statisticalTime;

    
    @ApiModelProperty(value = "相对于当前时间的偏移值")
    private Integer offsetValue;

    
    @ApiModelProperty(value = "相对于当前时间的偏移单位")
    private String offsetUnit;

    
    @ApiModelProperty(value = "统计值")
    private Long value;
}
