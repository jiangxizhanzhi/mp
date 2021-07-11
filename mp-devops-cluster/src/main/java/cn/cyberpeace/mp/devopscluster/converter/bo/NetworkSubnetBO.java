package cn.cyberpeace.mp.devopscluster.converter.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NetworkSubnetBO {

    private String id;

    
    private String cidr;

    
    private String gatewayIp;


    
    private Integer freeIps;

    
    private Integer usedIps;
}
