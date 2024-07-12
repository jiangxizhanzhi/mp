 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FirewallRule.java.
 // File size 963.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FirewallRule
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class FirewallRule {
    @ApiModelProperty(value = "源ip")
    private String sourceIpAddress;
    @ApiModelProperty(value = "目标ip")
    private String destinationIpAddress;
    @ApiModelProperty(value = "源端口")
    private String sourcePort;
    @ApiModelProperty(value = "目标端口")
    private String destinationPort;
    @ApiModelProperty(value = "ip版本")
    private Integer ipVersion;
    @ApiModelProperty(value = "协议")
    private String protocol;
    @ApiModelProperty(value = "动作")
    private String action;
    @ApiModelProperty(value = "方向")
    private String direction;
    @ApiModelProperty(value = "优先级")
    private Integer priority;
}
