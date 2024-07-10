package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction.functionrequest;

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
public class FirewallBuildRequestBody {

    //资源组名称
    private String resourceGroup;

    //防火墙名称
    private String firewallName;

    //防火墙策略名称
    private String firewallPolicyName;

    //创建区域
    private String region;

    //防火墙策略
    private FirewallPolicyRequestBody firewallPolicy;

    //虚拟网络
    private Network virtualNetwork;
}
