package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction;

import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.functionrequest.FireWallCreateRequestBody;
import com.azure.core.credential.TokenCredential;
import com.azure.resourcemanager.AzureResourceManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FireWallFunction {

    // 防火墙创建
    // 创建前需要将虚拟网络和专有的防火墙子网创建好(子网名称为AzureFirewallManagementSubnet)
    public Boolean createFireWall(Integer cloudType, FireWallCreateRequestBody body) {
        // 获取默认订阅Id的资源管理器和相关认证
        AzureResourceManager resourceManager = IAzureResourceManager.getInstance(cloudType).getManager();
        TokenCredential tokenCredential = IAzureResourceManager.getInstance(cloudType).getTokenCredential();

        // 资源组名称
        String resourceGroupName = body.getResourceGroup();
        // 防火墙名称
        String firewallName = body.getName();
        // 区域
        String region = body.getRegion();





//        resourceManager.networks().list().forEach(network -> {
//            network.subnets().forEach(subnet -> {
//                if (subnet.equals("AzureFirewallManagementSubnet")) {
//                    // 创建防火墙
//                    AzureFirewallInner azureFirewallInner = resourceManager.firewalls().define(firewallName)
//                            .withRegion(region)
//                            .withExistingResourceGroup(resourceGroupName)
//                            .withSku(AzureFirewallSkuTier.STANDARD)
//                            .withExistingNetwork(network)
//                            .withExistingSubnet(subnet);
//                }
//            });
//        });

        return true;
    }



}
