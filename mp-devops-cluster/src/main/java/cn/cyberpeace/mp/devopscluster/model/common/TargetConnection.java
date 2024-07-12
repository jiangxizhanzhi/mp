 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TargetConnection.java.
 // File size 1407.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.model.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName TargetConnection
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("靶标登录信息")
@Accessors(chain = true)
public class TargetConnection implements Serializable {

    @ApiModelProperty("协议")
    private String protocol;

    @ApiModelProperty("端口")
    private Integer port;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("IP：实装")
    private String hostname;

    @ApiModelProperty("链接ID")
    private Integer connectionId;

    @ApiModelProperty("登录URL")
    private String url;

    @ApiModelProperty("内部IP:实装")
    private String internalIp;

    @ApiModelProperty("内部IP:镜像")
    private String ipAddress;

    @ApiModelProperty("连接状态，0 是默认的，1是端口通了，2 是端口不通或者超时了，超时时间五分钟 ")
    private Integer connectionState;
}
