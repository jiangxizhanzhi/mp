 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SingleIpResponse.java.
 // File size 691.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName SingleIpResponse
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("单ip配置")
public class SingleIpResponse {

    @ApiModelProperty(value = "是否开启单ip")
    private Boolean publicNetProxy;

    @ApiModelProperty(value = "单ip模式下配置的ip地址")
    private String publicNetProxyIp;
}
