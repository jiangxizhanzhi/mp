package cn.cyberpeace.mp.flow.bo;

import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("流量仿真统计临时存放数据")
public class FlowStatisticsTemp extends FlowTaskRecordModel {

    
    @ApiModelProperty(value = "统计时间")
    private String statisticalTime;

    
    @ApiModelProperty(value = "相对于当前时间的偏移值")
    private Integer offsetValue;

    
    @ApiModelProperty(value = "相对于当前时间的偏移单位")
    private String offsetUnit;

}
