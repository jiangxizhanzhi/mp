package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.FirewallRule;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import com.azure.core.management.Region;
import com.azure.core.management.SubResource;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.network.fluent.models.AzureFirewallInner;
import com.azure.resourcemanager.network.fluent.models.FirewallPolicyInner;
import com.azure.resourcemanager.network.fluent.models.FirewallPolicyRuleCollectionGroupInner;
import com.azure.resourcemanager.network.fluent.models.SubnetInner;
import com.azure.resourcemanager.network.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class AzureFireWall {
    private IAzureResourceManager resourceService;

    private AzureResourceManager azureResourceManager;

    private Region region;

    public AzureFireWall(IAzureResourceManager resourceService) {
        this.resourceService = resourceService;
        this.azureResourceManager = resourceService.getManager();
        this.region = resourceService.getRegin();
    }

    public void create(CloudScenePluginHandler pluginHandler, CloudPluginSceneElementBO cloudElement, List<VirtualNic> virtualNics) {
        String fwName = HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid());
        String groupName = pluginHandler.getSourceSceneId();
        String networkId = virtualNics.get(0).getNetworkId();
        Network vnet = azureResourceManager.networks().getById(networkId);
        //防火墙公网id,该公网Ip的SKU必须为标准
        AtomicReference<SubResource> publicIpResource = new AtomicReference<>();
        PublicIpAddress publicIpAddress = azureResourceManager
                .publicIpAddresses()
                .define("fireWall-publicIp" + fwName)
                .withRegion(region)
                .withExistingResourceGroup(groupName)
                .withSku(PublicIPSkuType.STANDARD)
                .withStaticIP()
                .create();
        publicIpResource.set(new SubResource().withId(publicIpAddress.id()));
        System.out.println("防火墙公网名称: " + publicIpAddress.name() + " 防火墙公网id: " + publicIpAddress.id());

        Subnet azureFirewallSubnet = vnet.subnets().get("AzureFirewallSubnet");
        SubResource subResource = new SubResource().withId(azureFirewallSubnet.id());
        // 创建 AzureFirewallIPConfiguration 对象，用于定义防火墙的 IP 配置
        AzureFirewallIpConfiguration firewallIPConfig = new AzureFirewallIpConfiguration()
                .withName("firewallIpConfig" + UUID.randomUUID())
                .withPublicIpAddress(publicIpResource.get())
                .withSubnet(subResource); // 根据防火墙子网，分配对应的静态 IP 地址

        List<AzureFirewallIpConfiguration> firewallIPConfigs = new ArrayList<>();
        firewallIPConfigs.add(firewallIPConfig);
        System.out.println("防火墙的IP配置完成!");

        /**
         *  防火墙策略创建
         */
        // 查找刚才创建的防火墙策略的id
        AtomicReference<SubResource> firewallPolicySubResource = new AtomicReference<>();
        if (cloudElement.getFirewallRules().size() > 0) {
            FirewallPolicySkuTier firewallPolicySkuTier = FirewallPolicySkuTier.fromString("Basic");

            // 创建防火墙策略
            FirewallPolicyInner firewallPolicyInner = new FirewallPolicyInner()
                    .withLocation(region.name())
                    .withSku(new FirewallPolicySku().withTier(firewallPolicySkuTier));

            // 提交创建防火墙策略的操作
            FirewallPolicyInner createdFirewallPolicy = azureResourceManager
                    .networks()
                    .manager()
                    .serviceClient()
                    .getFirewallPolicies()
                    .createOrUpdate(groupName, "firewallPolicyName-" + fwName, firewallPolicyInner);
            String firewallPolicyName = createdFirewallPolicy.name();
            System.out.println("创建防火墙策略成功！"+firewallPolicyName);


            // 创建防火墙策略的规则集合
            List<FirewallPolicyRuleCollection> ruleCollectionInner = getPolicyRuleCollection(cloudElement.getFirewallRules());

            // 创建规则集合组inner
            FirewallPolicyRuleCollectionGroupInner ruleCollectionGroupInner = new FirewallPolicyRuleCollectionGroupInner()
                    .withName("MyRuleCollectionGroup-" + fwName)
                    .withPriority(100)
                    .withRuleCollections(ruleCollectionInner);
            System.out.println("创建规则集合组成功！");

            // 提交创建规则集合组的操作
            azureResourceManager.networks().manager().serviceClient()
                    .getFirewallPolicyRuleCollectionGroups()
                    .createOrUpdate(groupName, firewallPolicyName, ruleCollectionGroupInner.name(), ruleCollectionGroupInner);

            // 提交更新防火墙策略的操作
            azureResourceManager.networks().manager().serviceClient()
                    .getFirewallPolicies()
                    .createOrUpdate(groupName, firewallPolicyName, createdFirewallPolicy);
            System.out.println("更新防火墙策略成功！");
            // 遍历防火墙策略列表
            azureResourceManager.networks().manager().serviceClient().getFirewallPolicies().list().forEach(firewallPolicy -> {
                if (firewallPolicy.name().equals(firewallPolicyName)) {
                    firewallPolicySubResource.set(new SubResource().withId(firewallPolicy.id()));
                }
            });
        }


        //当防火墙SKU为基本时，必须配置管理公共IP地址，且该公共IP的SKU必须为标准
        AtomicReference<SubResource> managePublicIpResource = new AtomicReference<>();
        PublicIpAddress managePublicIp = azureResourceManager
                .publicIpAddresses()
                .define("fireWall-managementPublicIp" + fwName)
                .withRegion(region)
                .withExistingResourceGroup(groupName)
                .withSku(PublicIPSkuType.STANDARD)
                .withStaticIP()
                .create();
        managePublicIpResource.set(new SubResource().withId(managePublicIp.id()));
        System.out.println("管理公共IP地址名称: " + managePublicIp.name() + " 管理公共IP地址id: " + managePublicIp.id());

        //防火墙管理Ip配置子网id
        AtomicReference<SubResource> managerSubnetResource = new AtomicReference<>();
        Map<String, Subnet> managerSubnets = vnet.subnets();
        for (Map.Entry<String, Subnet> entry : managerSubnets.entrySet()) {
            if (entry.getKey().equals("AzureFirewallManagementSubnet")) {
                Subnet value = entry.getValue();
                managerSubnetResource.set(new SubResource().withId(value.id()));
                System.out.println("防火墙管理Ip配置子网名称: " + entry.getKey() + " ;防火墙管理Ip配置子网id: " + entry.getValue());
                break;
            }
        }

        // 创建 AzureFirewallIPConfiguration 对象，用于定义防火墙的管理公共IP地址配置
        AzureFirewallIpConfiguration managePublicIpConfig = new AzureFirewallIpConfiguration()
                .withName("managePublicIpResource-" + fwName)
                .withPublicIpAddress(managePublicIpResource.get())
                .withSubnet(managerSubnetResource.get());
        AzureFirewallSkuTier azureFirewallSkuTier = AzureFirewallSkuTier.fromString("Basic");

        // 创建防火墙实例
        AzureFirewallInner azureFirewallUpdateInner = new AzureFirewallInner()
                .withLocation(region.name())
                .withSku(new AzureFirewallSku()
                        .withName(AzureFirewallSkuName.AZFW_VNET)
                        .withTier(azureFirewallSkuTier))
                .withFirewallPolicy(firewallPolicySubResource.get())
                .withIpConfigurations(firewallIPConfigs)
                .withManagementIpConfiguration(managePublicIpConfig);

        // 提交创建防火墙实例的操作
        AzureFirewallInner create = azureResourceManager
                .networks()
                .manager()
                .serviceClient()
                .getAzureFirewalls()
                .createOrUpdate(groupName, fwName, azureFirewallUpdateInner);
        List<AzureFirewallIpConfiguration> azureFirewallIpConfigurations = create.ipConfigurations();
        String privateIpAddress = azureFirewallIpConfigurations.get(0).privateIpAddress();
        System.out.println("创建防火墙和绑定防火墙策略成功！");

//            创建路由表
        RouteTable routeTable = azureResourceManager.routeTables().define("routeTable-" + fwName)
                .withRegion(region)
                .withExistingResourceGroup(groupName)
                .defineRoute("defaultRoute")
                .withDestinationAddressPrefix("0.0.0.0/0")
                .withNextHopToVirtualAppliance(privateIpAddress).attach()
                .create();

        // 将子网关联到路由表

        Map<String, Subnet> subnets = vnet.subnets();

        // 需要绑定到路由表的子网名称
        List<String> needRouteTableAddressNames = new ArrayList<>();
        needRouteTableAddressNames.add("AzureFirewallSubnet");
        needRouteTableAddressNames.add("AzureFirewallManagementSubnet");
        for (Map.Entry<String, Subnet> entry : subnets.entrySet()) {
            if (!needRouteTableAddressNames.contains(entry.getKey())) {
                // 获取子网关联的现有路由表
                Subnet subnet = entry.getValue();
                RouteTable subnetRouteTable = subnet.getRouteTable();
                // 如果子网没有关联路由表，或者关联的路由表不是指定的 routeTable，则更新子网的路由表
                if (subnetRouteTable == null || !subnetRouteTable.id().equalsIgnoreCase(routeTable.id())) {
                    if (subnetRouteTable == null) {
                        // 如果子网没有关联路由表，则创建新的路由表
                        SubnetInner subnetInner = subnet.innerModel().withRouteTable(routeTable.innerModel());
                        azureResourceManager
                                .networks()
                                .manager()
                                .serviceClient()
                                .getSubnets()
                                .createOrUpdate(groupName, vnet.name(), subnet.name(), subnetInner);
                    } else {
                        subnetRouteTable.update().defineRoute("defaultRoute")
                                .withDestinationAddressPrefix("0.0.0.0/0")
                                .withNextHopToVirtualAppliance(privateIpAddress)
                                .attach();
                    }
                }
            }
        }
    }

    // 创建防火墙策略的规则集合
    private List<FirewallPolicyRuleCollection> getPolicyRuleCollection(List<FirewallRule> firewallRuleList) {

        List<FirewallPolicyRuleCollection> filterRuleCollections = new ArrayList<>();

        //根据允许和拒绝分组创建防火墙规则集合
        List<FirewallRule> allowRequestRules = new ArrayList<>();
        List<FirewallRule> rejectRequestRules = new ArrayList<>();
        firewallRuleList.forEach(rule -> {
            //根据允许和拒绝分组创建防火墙规则集合
            if (rule.getAction().equals("allow")) {
                allowRequestRules.add(rule);
            } else if (rule.getAction().equals("reject")) {
                rejectRequestRules.add(rule);
            }
        });

        //暂定 拒绝规则组的优先级为100，允许规则组的优先级为200
        FirewallPolicyFilterRuleCollectionActionType allow = FirewallPolicyFilterRuleCollectionActionType.ALLOW;
        collectRuleCollection(allow, allowRequestRules, 200, filterRuleCollections);
        FirewallPolicyFilterRuleCollectionActionType reject = FirewallPolicyFilterRuleCollectionActionType.DENY;
        collectRuleCollection(reject, rejectRequestRules, 100, filterRuleCollections);

        return filterRuleCollections;
    }

    private void collectRuleCollection(FirewallPolicyFilterRuleCollectionActionType allow, List<FirewallRule> allowRequestRules,
                                       Integer ruleGroupPriority, List<FirewallPolicyRuleCollection> filterRuleCollections) {
        if (allowRequestRules != null && allowRequestRules.size() > 0) {
            List<FirewallPolicyRule> allowRules = new ArrayList<>();
            for (FirewallRule allowRule : allowRequestRules) {
                List<String> destinationIpAddresses = getFilterValueList(allowRule.getDestinationIpAddress(), "normal");
                List<String> destinationPorts = getFilterValueList(allowRule.getDestinationPort(), "normal");
                List<String> sourceIpAddresses = getFilterValueList(allowRule.getSourceIpAddress(), "normal");
                List<FirewallPolicyRuleNetworkProtocol> protocols = getFilterValueList(allowRule.getProtocol(), "protocol");

                // 创建规则(目标类型和源类型默认为IP地址)
                FirewallPolicyRule networkRule = new NetworkRule()
                        .withName("AllowHTTP" + UUID.randomUUID())
                        .withDescription("Allow HTTP traffic" + UUID.randomUUID())
                        .withDestinationAddresses(destinationIpAddresses) //目标地址 *,10.0.0.1,10.1.0.0/16
                        .withDestinationPorts(destinationPorts) //目标端口 80,8000-9000
                        .withSourceAddresses(sourceIpAddresses) //源 *,10.0.0.1,10.1.0.0/16
                        .withIpProtocols(protocols); // 协议 ANY,TCP,UDP,ICMP
                allowRules.add(networkRule);
            }

            // 创建防火墙规则集合(规则集合类型 根据withRules方法内传的参数决定 分为三类 网络,应用程序,DNAT,皆继承FirewallPolicyRule类)
            FirewallPolicyFilterRuleCollection ruleCollectionInner = new FirewallPolicyFilterRuleCollection()
                    .withName("MyRuleCollection" + UUID.randomUUID())
                    .withPriority(ruleGroupPriority)
                    .withAction(new FirewallPolicyFilterRuleCollectionAction().withType(FirewallPolicyFilterRuleCollectionActionType.ALLOW))
                    .withRules(allowRules);
            filterRuleCollections.add(ruleCollectionInner);
        }
    }

    private List getFilterValueList(String value, String type) {
        List<Object> valueList = new ArrayList<>();
        if (value == null || value.isEmpty()) {
            return valueList;
        }
        String[] split = value.split(",");
        for (String s : split) {
            if ("protocol".equals(type)) {
                switch (s.toUpperCase()) {
                    case "ANY":
                        valueList.add(FirewallPolicyRuleNetworkProtocol.ANY);
                        break;
                    case "TCP":
                        valueList.add(FirewallPolicyRuleNetworkProtocol.TCP);
                        break;
                    case "UDP":
                        valueList.add(FirewallPolicyRuleNetworkProtocol.UDP);
                        break;
                    case "ICMP":
                        valueList.add(FirewallPolicyRuleNetworkProtocol.ICMP);
                        break;
                    default:
                        break;
                }
            } else {
                valueList.add(s);
            }
        }
        return valueList;
    }

}
