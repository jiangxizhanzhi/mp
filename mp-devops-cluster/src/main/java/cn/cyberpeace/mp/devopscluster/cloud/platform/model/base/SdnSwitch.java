 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SdnSwitch.java.
 // File size 734.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName SdnSwitch
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class SdnSwitch {

    @ApiModelProperty("uuid")
    private String uuid;
    @ApiModelProperty("交换机名称")
    private String name;
    @ApiModelProperty("管理ip")
    private String managerIp;
    @ApiModelProperty("tunnelIp")
    private String tunnelIp;

    @ApiModelProperty("平台连接交换机自动获取")
    private Integer portNum;

    @ApiModelProperty("物理交换机实际标识")
    private String physicalSwitchName;
}
