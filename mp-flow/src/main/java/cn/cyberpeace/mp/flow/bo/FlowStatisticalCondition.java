package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description: 流量统计条件
 * @Author: guquan
 * @Date：2023/5/16 11:38
 */
@Data
@ApiModel("流量统计条件")
public class FlowStatisticalCondition {

    /**
     * 开始统计时间
     */
    @ApiModelProperty(value = "开始统计时间")
    private LocalDateTime startTime;

    /**
     * 结束统计时间
     */
    @ApiModelProperty(value = "结束统计时间")
    private LocalDateTime endTime;

    /**
     * 相对于当前时间的偏移值
     */
    @ApiModelProperty(value = "相对于当前时间的偏移值")
    private Integer offsetValue;

    /**
     * 相对于当前时间的偏移单位(h:小时 m:分钟 s:秒)
     */
    @ApiModelProperty(value = "相对于当前时间的偏移单位")
    private String offsetUnit;
}
