package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import cn.cyberpeace.mp.base.validation.stereotype.Cidr;
import cn.cyberpeace.mp.base.validation.stereotype.IpAddress;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StaticRoute {
    @ApiModelProperty(value = "目的地址(cidr)")
    @Cidr(message = "{sceneplugincloud.validation.cidr}")
    private String destination;
    @ApiModelProperty(value = "下一跳地址")
    @IpAddress(message = "{sceneplugincloud.validation.ip_address}")
    private String nexthop;
    @ApiModelProperty(value = "下一跳接口Id")
    private String nexthopPortId;
}
