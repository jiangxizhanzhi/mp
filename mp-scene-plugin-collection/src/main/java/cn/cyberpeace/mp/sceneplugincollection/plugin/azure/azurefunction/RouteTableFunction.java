package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction;

import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction.functionrequest.RouteTableCreateRequestBody;
import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction.functionresponse.FirewallBuildResponseBody;
import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction.functionresponse.RouteTableBuildResult;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.network.fluent.models.SubnetInner;
import com.azure.resourcemanager.network.models.Network;
import com.azure.resourcemanager.network.models.RouteTable;
import com.azure.resourcemanager.network.models.Subnet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RouteTableFunction {

    // 路由表创建
    public FirewallBuildResponseBody createRouteTable(FirewallBuildResponseBody responseBody, Integer cloudType, RouteTableCreateRequestBody body) {
        responseBody.setRouteTableBuildResult(new RouteTableBuildResult());
        try {
            // 获取默认订阅Id的资源管理器
            AzureResourceManager azureResourceManager = IAzureResourceManager.getInstance(cloudType).getManager();

            String resourceGroupName = body.getResourceGroup();
            String routeTableName = body.getName();
            String region = body.getRegion();

            // 创建路由表
            RouteTable routeTable = azureResourceManager.routeTables().define(routeTableName)
                    .withRegion(region)
                    .withExistingResourceGroup(resourceGroupName)
                    .defineRoute("defaultRoute").withDestinationAddressPrefix("0.0.0.0/0").withNextHopToVirtualAppliance(body.getFirewallPrivateAddress()).attach()
                    .create();

            // 将子网关联到路由表
            Network network = azureResourceManager.networks().getByResourceGroup(resourceGroupName, body.getVirtualNetwork().name());
            Map<String, Subnet> subnets = network.subnets();
            for (Map.Entry<String, Subnet> entry : subnets.entrySet()) {
                if (!entry.getKey().equals("AzureFirewallSubnet")) {
                    // 获取子网关联的现有路由表
                    Subnet subnet = entry.getValue();
                    RouteTable subnetRouteTable = subnet.getRouteTable();
                    // 如果子网没有关联路由表，或者关联的路由表不是指定的 routeTable，则更新子网的路由表
                    if (subnetRouteTable == null || !subnetRouteTable.id().equalsIgnoreCase(routeTable.id())) {
                        if(subnetRouteTable == null){
                            // 如果子网没有关联路由表，则创建新的路由表
                            SubnetInner subnetInner = subnet.innerModel().withRouteTable(routeTable.innerModel());
                            azureResourceManager.networks().manager().serviceClient().getSubnets().createOrUpdate(resourceGroupName, network.name(), subnet.name(), subnetInner);
                        }else{
                            subnetRouteTable.update().defineRoute("defaultRoute")
                                    .withDestinationAddressPrefix("0.0.0.0/0")
                                    .withNextHopToVirtualAppliance(body.getFirewallPrivateAddress())
                                    .attach();
                            System.out.println("子网 " + entry.getKey() + " 已成功关联到路由表 " + routeTableName);
                        }
                    } else {
                        System.out.println("子网 " + entry.getKey() + " 已经关联到路由表 " + routeTableName);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("创建路由表失败：" + e.getMessage());
            responseBody.getRouteTableBuildResult().setResult(false);
            responseBody.getRouteTableBuildResult().setErrorMessage("创建路由表失败：" + e.getMessage());
            e.printStackTrace();
            return responseBody;
        }
        responseBody.getRouteTableBuildResult().setResult(true);
        return responseBody;
    }


}
