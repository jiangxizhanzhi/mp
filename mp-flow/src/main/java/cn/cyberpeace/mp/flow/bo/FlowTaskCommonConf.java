 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowTaskCommonConf.java.
 // File size 958.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FlowTaskCommonConf
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@ApiModel("通用配置")
public class FlowTaskCommonConf {

    /**
     * 延时(单位ms)
     */
    @ApiModelProperty("延时(单位ms)")
    private Integer latency;

    /**
     * 抖动(单位ms)
     */
    @ApiModelProperty("抖动(单位ms)")
    private Integer jitter;

    /**
     * ISG帧间间隔(单位ms)
     */
    @ApiModelProperty("ISG帧间间隔(单位ms)")
    private Integer isg;

    /**
     * IPG包间间隔(单位ms)
     */
    @ApiModelProperty("IPG包间间隔(单位ms)")
    private Integer ipg;

    /**
     * 时长(单位s)
     */
    @ApiModelProperty("时长(单位s)")
    private Integer duration;
}
