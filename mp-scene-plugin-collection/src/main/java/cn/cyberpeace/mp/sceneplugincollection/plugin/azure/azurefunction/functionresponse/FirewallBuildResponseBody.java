package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction.functionresponse;

import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction.functionrequest.FirewallPolicyRequestBody;
import com.azure.resourcemanager.network.models.Network;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 防火墙创建结果
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirewallBuildResponseBody {

    //防火墙创建结果
    private FirewallBuildResult firewallBuildResult;

    //路由表创建结果
    private RouteTableBuildResult routeTableBuildResult;
}
