package cn.cyberpeace.mp.devopscluster.converter.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VlanIpStockBO {

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
