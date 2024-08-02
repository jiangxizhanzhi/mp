package cn.cyberpeace.mp.agent.snmp.controller.request;

import cn.cyberpeace.mp.agent.snmp.service.BasicAccessInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SnmpAccessInfoTest extends BasicAccessInfo {

    @ApiModelProperty("ip")
    private String hostIp;

    @ApiModelProperty("oid")
    private String oid;
}
