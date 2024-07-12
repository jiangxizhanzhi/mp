 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VlanIpStockVO.java.
 // File size 753.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName VlanIpStockVO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class VlanIpStockVO implements Serializable {

    @ApiModelProperty("vlan总数")
    private Integer totalVlans;

    @ApiModelProperty("已使用vlan")
    private Integer usedVlans;

    @ApiModelProperty("IP总数")
    private Integer totalIps;

    @ApiModelProperty("已使用IP")
    private Integer usedIps;

}
