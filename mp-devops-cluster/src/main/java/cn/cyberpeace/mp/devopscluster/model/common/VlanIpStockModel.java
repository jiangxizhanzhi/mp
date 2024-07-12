 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VlanIpStockModel.java.
 // File size 722.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName VlanIpStockModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class VlanIpStockModel implements Serializable {

    /**
     * vlan总数
     */
    private Integer totalVlans;

    /**
     * 已使用vlan
     */
    private Integer usedVlans;

    /**
     * IP总数
     */
    private Integer totalIps;

    /**
     * 已使用IP
     */
    private Integer usedIps;


}
