package cn.cyberpeace.mp.agent.netconf.service;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasicAccessInfo {
    @ApiModelProperty("接入协议服务端口")
    private String port;

    @ApiModelProperty("目标节点ip")
    private String hostIp;

}
