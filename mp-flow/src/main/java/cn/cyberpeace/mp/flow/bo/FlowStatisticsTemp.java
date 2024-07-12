 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowStatisticsTemp.java.
 // File size 964.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.bo;

import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FlowStatisticsTemp
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see FlowTaskRecordModel
 */
@Data
@ApiModel("流量仿真统计临时存放数据")
public class FlowStatisticsTemp extends FlowTaskRecordModel {

    /**
     * 统计时间
     */
    @ApiModelProperty(value = "统计时间")
    private String statisticalTime;

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
