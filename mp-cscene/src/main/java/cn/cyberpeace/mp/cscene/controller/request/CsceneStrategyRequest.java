// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name CsceneInstanceActionRequest.java.
// File size 517.
// File create date Fri Jul 12 12:55:22 CST 2024.
// File update date Fri Jul 12 12:55:22 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName CsceneInstanceActionRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class CsceneStrategyRequest {
    @ApiModelProperty(value = "场景id")
    private String id;
    @ApiModelProperty(value = "节点id")
    private String nodeId;
    @ApiModelProperty(value = "配置命令")
    private Config configCommand;

    @Getter
    @Setter
    public static class Config {
        @ApiModelProperty(value = "协议, 0-SNMP、1-NETCONF、2-RESTCONF")
        private int protocol;
        @ApiModelProperty(value = "ip")
        private String ip;
        @ApiModelProperty(value = "端口")
        private String port;
        @ApiModelProperty(value = "社区")
        private String community;
        @ApiModelProperty(value = "内容")
        private String content;
        @ApiModelProperty(value = "用户")
        private String user;
        @ApiModelProperty(value = "密码")
        private String password;
    }
}
