 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HostRoute.java.
 // File size 438.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName HostRoute
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class HostRoute {
    @ApiModelProperty(value = "目的地址(cidr)")
    private String destination;
    @ApiModelProperty(value = "下一跳地址")
    private String nexthop;
}
