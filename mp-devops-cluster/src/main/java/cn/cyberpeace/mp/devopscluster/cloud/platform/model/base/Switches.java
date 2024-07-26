 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Switches.java.
 // File size 1191.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName Switches
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Switches implements Serializable {

    @ApiModelProperty("交换机名称")
    private String name;

    @ApiModelProperty("物理交换机名称")
    private String physicalSwitchName;

    @ApiModelProperty("管理IP")
    private String managementIp;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("接口数量")
    private Integer interfaceNums;

    @ApiModelProperty("启用中的接口明细")
    private List<String> interfaceUpDetails;

    @ApiModelProperty("启用中接口数量")
    private Integer interfaceUpNums;

    @ApiModelProperty("vlan数量")
    private Integer vlanNums;

    @ApiModelProperty("接口明细")
    private List<InterfaceDetail> interfaceDetails;
}
