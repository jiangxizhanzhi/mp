package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("通用配置")
public class FlowTaskCommonConf {

    
    @ApiModelProperty("延时(单位ms)")
    private Integer latency;

    
    @ApiModelProperty("抖动(单位ms)")
    private Integer jitter;

    
    @ApiModelProperty("ISG帧间间隔(单位ms)")
    private Integer isg;

    
    @ApiModelProperty("IPG包间间隔(单位ms)")
    private Integer ipg;

    
    @ApiModelProperty("时长(单位s)")
    private Integer duration;
}
