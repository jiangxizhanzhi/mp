 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowStatisticalCondition.java.
 // File size 1026.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName FlowStatisticalCondition
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
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
