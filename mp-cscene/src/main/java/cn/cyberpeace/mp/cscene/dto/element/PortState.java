 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PortState.java.
 // File size 877.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.element;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/2 15:39
 * 端口：端口号   协议  pid
 * 网络连接：  协议  本机地址  远程地址 状态
 */
@Data
public class PortState {

    @ApiModelProperty("进程号")
    private String pid;

    @ApiModelProperty("进程名称")
    private String process;

    @ApiModelProperty("协议")
    private String protocol;

    @ApiModelProperty("本地地址")
    private String localIp;

    @ApiModelProperty("端口号")
    private String port;

    @ApiModelProperty("远程地址")
    private String remoteIp;

    @ApiModelProperty("远程端口")
    private String remotePort;

    @ApiModelProperty("状态")
    private String state;

}
