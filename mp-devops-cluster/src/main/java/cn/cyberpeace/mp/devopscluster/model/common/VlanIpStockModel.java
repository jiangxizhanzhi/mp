package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
