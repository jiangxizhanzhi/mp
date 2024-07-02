package cn.cyberpeace.mp.devopscluster.converter.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName NetworkSubnetBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
