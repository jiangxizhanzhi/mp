package cn.cyberpeace.mp.devops.cluster.converter.bo;

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
}
