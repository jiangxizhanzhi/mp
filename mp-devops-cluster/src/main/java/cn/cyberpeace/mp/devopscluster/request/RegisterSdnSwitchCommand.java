 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RegisterSdnSwitchCommand.java.
 // File size 886.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @ClassName RegisterSdnSwitchCommand
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class RegisterSdnSwitchCommand {
    @ApiModelProperty("交换机名称")
    private String name;
    @ApiModelProperty("管理ip")
    private String managerIp;
    @ApiModelProperty("tunnelIp")
    private String tunnelIp;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("密码，需要base64 encode后存入")
    private String password;


    public void base64EncodePw() {
        this.password = Base64.getEncoder().encodeToString(this.password.getBytes(StandardCharsets.UTF_8));
    }
}
