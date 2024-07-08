package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.functionrequest;

import com.azure.resourcemanager.network.models.Network;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 路由表创建请求体
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteTableCreateRequestBody {

    //资源组名称
    private String resourceGroup;

    //路由表名称
    private String name;

    //创建区域
    private String region;

    //虚拟网络
    private Network virtualNetwork;

    //防火墙私有地址
    private String firewallPrivateAddress;
}
