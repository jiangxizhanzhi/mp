package cn.cyberpeace.mp.devopscluster.converter.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NetworkSubnetBO {

    private String id;

    /**
     * 子网CIDR
     */
    private String cidr;

    /**
     * 网关ip
     */
    private String gatewayIp;


    /**
     * 剩余IP数
     */
    private Integer freeIps;

    /**
     * 已使用IP数
     */
    private Integer usedIps;
}
