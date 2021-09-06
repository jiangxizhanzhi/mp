package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
    @ApiModelProperty(value = "协议")
    private String protocol;
    @ApiModelProperty(value = "动作")
    private String action;
}
