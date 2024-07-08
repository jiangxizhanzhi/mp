package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.functionrequest;

import com.azure.resourcemanager.network.models.Network;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 防火墙创建请求体
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FireWallCreateRequestBody {

    //资源组名称
    private String resourceGroup;

    //防火墙名称
    private String name;

    //创建区域
    private String region;

    //防火墙SKU
    private String sku;

    //防火墙策略
    private FirewallPolicy firewallPolicy;

    //虚拟网络
    private Network virtualNetwork;
}
