 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowRealTimeRate.java.
 // File size 889.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FlowRealTimeRate
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@ApiModel("实时速率统计对象")
public class FlowRealTimeRate {

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id")
    private String taskId;

    /**
     * 统计时间
     */
    @ApiModelProperty(value = "统计时间")
    private String statisticalTime;

    /**
     * 相对于当前时间的秒数
     */
    @ApiModelProperty(value = "相对于当前时间的秒数")
    private Integer second;

    /**
     * 实时速率（单位byte/s）
     */
    @ApiModelProperty(value = "实时速率（单位byte/s）")
    private Double txBps;

}
