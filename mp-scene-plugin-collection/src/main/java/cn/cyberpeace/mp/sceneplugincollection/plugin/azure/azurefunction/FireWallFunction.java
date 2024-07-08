package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction;

import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.functionrequest.FireWallCreateRequestBody;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.network.fluent.NetworkManagementClient;
import com.azure.resourcemanager.network.fluent.models.AzureFirewallInner;
import com.azure.resourcemanager.network.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class FireWallFunction {

    // 防火墙创建
    // 创建前需要将虚拟网络和专有的防火墙子网创建好(子网名称为AzureFirewallManagementSubnet)
    public Boolean createFireWall(Integer cloudType, FireWallCreateRequestBody body) {
        // 获取默认订阅Id的资源管理器
        AzureResourceManager azureResourceManager = IAzureResourceManager.getInstance(cloudType).getManager();
        // 资源组名称
        String resourceGroupName = body.getResourceGroup();
        // 区域
        String region = body.getRegion();

        try {
//            // 创建虚拟网络和对应的防火墙子网
//            Network.DefinitionStages.WithCreateAndSubnet azureFirewallManagementSubnet = azureResourceManager.networks().define(firewallName)
//                    .withRegion(region)
//                    .withExistingResourceGroup(resourceGroupName)
//                    .withAddressSpace(null)
//                    .defineSubnet("AzureFirewallManagementSubnet")
//                    .withAddressPrefix(null)
////                    .disableNetworkPoliciesOnPrivateEndpoint()
////                    .withExistingNetworkSecurityGroup("")
//                    .attach();

            // 创建防火墙实例
            AzureFirewallInner azureFirewallInner = new AzureFirewallInner()
                    .withLocation(region)
                    .withSku(new AzureFirewallSku()
                            .withName(AzureFirewallSkuName.AZFW_VNET)
                            .withTier(AzureFirewallSkuTier.STANDARD))
                    .withApplicationRuleCollections(Arrays.asList(
                            new AzureFirewallApplicationRuleCollection()
                                    .withName("appRuleCollection1")
                                    .withPriority(100)
                                    .withAction(new AzureFirewallRCAction().withType(AzureFirewallRCActionType.DENY))
                                    .withRules(Arrays.asList(
                                            new AzureFirewallApplicationRule()
                                                    .withName("appRule1")
                                                    .withSourceAddresses(Arrays.asList("10.0.0.0/24"))
                                                    .withFqdnTags(Arrays.asList("WindowsVirtualDesktop"))
                                                    .withProtocols(Arrays.asList(
                                                            new AzureFirewallApplicationRuleProtocol()
                                                                    .withProtocolType(AzureFirewallApplicationRuleProtocolType.HTTPS)
                                                                    .withPort(443))
                                                    )
                                    ))
                    ));

            // 应用防火墙实例
            NetworkManagementClient networkManagementClient = azureResourceManager.networks().manager().serviceClient();
            networkManagementClient.getAzureFirewalls().createOrUpdate(resourceGroupName, body.getName(), azureFirewallInner);
            System.out.println("Initiated create or update for Azure Firewall.");
        } catch (Exception e) {
            System.err.println("Error creating or updating Azure Firewall: " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }


}
