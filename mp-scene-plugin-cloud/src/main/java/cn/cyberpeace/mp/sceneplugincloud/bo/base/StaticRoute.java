package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StaticRoute {
    @ApiModelProperty(value = "目的地址")
    private String destination;
    @ApiModelProperty(value = "下一跳地址(cidr)")
    private String nexthop;
}
