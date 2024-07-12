 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EventAggr.java.
 // File size 658.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.event;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName EventAggr
 * @Description 采集事件分类
 * @Author zhouxinghuai
 * @Date 2021/11/22 17:35
 * @Version 1.0.0
 */
@Data
public class EventAggr {

    @ApiModelProperty("进程百分比")
    private double process;

    @ApiModelProperty("文件百分比")
    private double file;

    @ApiModelProperty("端口百分比")
    private double port;

    @ApiModelProperty("网络连接百分比")
    private double network;

    @ApiModelProperty("用户百分比")
    private double user;
}
