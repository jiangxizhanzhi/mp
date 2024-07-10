package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction;

import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction.functionrequest.RouteTableCreateRequestBody;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.network.models.Network;
import com.azure.resourcemanager.network.models.RouteNextHopType;
import com.azure.resourcemanager.network.models.RouteTable;
import com.azure.resourcemanager.network.models.Subnet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteTableFunction {

    // 路由表创建
    public Boolean createRouteTable(Integer cloudType, RouteTableCreateRequestBody body) {
        try {
            // 获取默认订阅Id的资源管理器
            AzureResourceManager azureResourceManager = IAzureResourceManager.getInstance(cloudType).getManager();

            String resourceGroupName = body.getResourceGroup();
            String routeTableName = body.getName();
            String region = body.getRegion();

            // 创建路由表并添加默认路由
            RouteTable routeTable = azureResourceManager.routeTables().define(routeTableName)
                    .withRegion(region)
                    .withExistingResourceGroup(resourceGroupName)
                    .defineRoute("default-route")
                        .withDestinationAddressPrefix("0.0.0.0/0")
                        .withNextHop(RouteNextHopType.VIRTUAL_APPLIANCE)
                        .attach()
                    .create();

            // 将路由表关联到防火墙所在的子网
            Network network = azureResourceManager.networks().getByResourceGroup(resourceGroupName, body.getVirtualNetwork().name());
            Subnet subnet = network.subnets().get("AzureFirewallSubnet");
            subnet.listAvailablePrivateIPAddresses().forEach(ip -> {

            });
            subnet.getRouteTable().update().updateRoute(routeTableName)
                    .withDestinationAddressPrefix("0.0.0.0/0")
                    .withNextHopToVirtualAppliance(body.getFirewallPrivateAddress())
                    .parent()
                    .apply();

        } catch (Exception e) {
            System.err.println("创建防火墙失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }




}
