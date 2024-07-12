 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RealEquipmentConnection.java.
 // File size 1044.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName RealEquipmentConnection
 * @Description 实装连接信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("实装登录信息")
public class RealEquipmentConnection {

    @ApiModelProperty("协议")
    private String protocol;

    @ApiModelProperty("端口")
    private Integer port;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("IP")
    private String hostname;

    @ApiModelProperty("链接ID")
    private Integer connectionId;

    @ApiModelProperty("登录URL")
    private String url;

    @ApiModelProperty("内部IP")
    private String internalIp;
}
