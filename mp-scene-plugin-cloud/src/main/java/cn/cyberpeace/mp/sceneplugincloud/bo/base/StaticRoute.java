 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name StaticRoute.java.
 // File size 779.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import cn.cyberpeace.mp.base.validation.stereotype.Cidr;
import cn.cyberpeace.mp.base.validation.stereotype.IpAddress;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName StaticRoute
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
