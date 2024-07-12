 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ConnectionState.java.
 // File size 1060.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.element;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/2 15:39
 * 端口：端口号   协议  pid
 * 网络连接：  协议  本机地址  远程地址 状态
 */
@Data
public class ConnectionState {

    @ApiModelProperty("进程ID")
    private String pid;

    @ApiModelProperty("协议")
    private String protocol;

    @ApiModelProperty("本地地址")
    private String localAddress;

    @ApiModelProperty("本地端口")
    private String localPort;

    @ApiModelProperty("远程地址")
    private String foreignAddress;

    @ApiModelProperty("远程端口")
    private String foreignPort;

    @ApiModelProperty("状态")
    private String state;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("级别")
    private int level;
}
