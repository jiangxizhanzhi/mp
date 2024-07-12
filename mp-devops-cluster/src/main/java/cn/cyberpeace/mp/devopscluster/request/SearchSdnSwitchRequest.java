 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SearchSdnSwitchRequest.java.
 // File size 788.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.base.annotation.ParamName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName SearchSdnSwitchRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class SearchSdnSwitchRequest {
    @ApiModelProperty("页码")
    @ParamName("offset")
    private Integer page = 1;
    @ApiModelProperty("页码大小")
    @ParamName("limit")
    private Integer perPage = 10;

    @ApiModelProperty("交换机标识")
    private String name;
    @ApiModelProperty("物理交换机的实际标识")
    private String physicalSwitchName;
    @ApiModelProperty("物理交换机的管理ip")
    private String managerIp;
}
