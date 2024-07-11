package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction;

import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction.functionrequest.FirewallBuildRequestBody;
import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction.functionrequest.FirewallPolicyRuleRequestBody;
import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction.functionrequest.RouteTableCreateRequestBody;
import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction.functionresponse.FirewallBuildResponseBody;
import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction.functionresponse.FirewallBuildResult;
import com.azure.core.management.SubResource;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.network.fluent.models.AzureFirewallInner;
import com.azure.resourcemanager.network.fluent.models.FirewallPolicyInner;
import com.azure.resourcemanager.network.fluent.models.FirewallPolicyRuleCollectionGroupInner;
import com.azure.resourcemanager.network.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class FirewallFunction {

    @Autowired
    private RouteTableFunction routeTableFunction;

    // 防火墙创建(防火墙和防火墙策略的SKU用基本,如果需要变动，需要保持一致)
    // 创建前需要将虚拟网络和专有的防火墙子网创建好(防火墙子网名称必须为AzureFirewallSubnet且地址空间后缀必须为/26)
    public FirewallBuildResponseBody createFireWall(Integer cloudType, FirewallBuildRequestBody requestBody) {
        FirewallBuildResponseBody responseBody = new FirewallBuildResponseBody();
        responseBody.setFirewallBuildResult(new FirewallBuildResult());
        // 获取默认订阅Id的资源管理器
        AzureResourceManager azureResourceManager = IAzureResourceManager.getInstance(cloudType).getManager();
        try {
            Network vnet = null;
            if (requestBody.getVirtualNetwork() == null) {
                // 创建虚拟网络及防火墙子网
                vnet = azureResourceManager.networks().define(UUID.randomUUID().toString())
                        .withRegion(requestBody.getRegion())
                        .withExistingResourceGroup(requestBody.getResourceGroup())
                        .withAddressSpace("10.0.0.0/16")  // 设置虚拟网络的地址空间
                        .defineSubnet("default")
                            .withAddressPrefix("10.0.1.0/24")  // 设置子网的地址前缀
                            .attach()
                        .defineSubnet("AzureFirewallManagementSubnet")
                            .withAddressPrefix("10.0.2.0/24")  // 设置子网的地址前缀 
                            .attach()
                        .defineSubnet("AzureFirewallSubnet")
                            .withAddressPrefix("10.0.3.0/26")  // 设置子网的地址前缀
                            .attach()
                        .create();
                responseBody.getFirewallBuildResult().setFirewallSubnetAddressSpace("10.0.3.0/26");
                responseBody.getFirewallBuildResult().setFirewallSubnetName("AzureFirewallSubnet");
                requestBody.setVirtualNetwork(vnet);
                System.out.println("创建虚拟网络及防火墙子网成功!");
            } else {
                vnet = requestBody.getVirtualNetwork();
                // 判断防火墙子网是否存在
                if (vnet.subnets().get("AzureFirewallSubnet") == null || vnet.subnets().get("AzureFirewallManagementSubnet") == null) {
                    responseBody.getFirewallBuildResult().setResult(false);
                    responseBody.getFirewallBuildResult().setErrorMessage("创建防火墙失败：" + "该虚拟网络没有创建专属于防火墙的子网");
                    return responseBody;
                }
            }

            //防火墙子网id
            AtomicReference<SubResource> subnetResource = new AtomicReference<>();
            Map<String, Subnet> subnets = vnet.subnets();
            for (Map.Entry<String, Subnet> entry : subnets.entrySet()) {
                if (entry.getKey().equals("AzureFirewallSubnet")) {
                    Subnet value = entry.getValue();
                    subnetResource.set(new SubResource().withId(value.id()));
                    System.out.println("防火墙子网名称: " + entry.getKey() + " ;防火墙子网id: " + entry.getValue());
                    break;
                }
            }

            //防火墙公网id
            AtomicReference<SubResource> publicIpResource = new AtomicReference<>();
            PublicIpAddress publicIpAddress = azureResourceManager.publicIpAddresses().define(UUID.randomUUID().toString())
                    .withRegion(requestBody.getRegion())
                    .withExistingResourceGroup(requestBody.getResourceGroup())
                    .withSku(PublicIPSkuType.STANDARD)
                    .withStaticIP()
                    .create();
            publicIpResource.set(new SubResource().withId(publicIpAddress.id()));
            System.out.println("防火墙公网名称: " + publicIpAddress.name() + " 防火墙公网id: " + publicIpAddress.id());

            // 创建 AzureFirewallIPConfiguration 对象，用于定义防火墙的 IP 配置
            AzureFirewallIpConfiguration firewallIPConfig = new AzureFirewallIpConfiguration()
                    .withName("firewallIpConfig"+UUID.randomUUID())
                    .withPublicIpAddress(publicIpResource.get())
                    .withSubnet(subnetResource.get()); // 根据防火墙子网，分配对应的静态 IP 地址

            List<AzureFirewallIpConfiguration> firewallIPConfigs = new ArrayList<>();
            firewallIPConfigs.add(firewallIPConfig);
            System.out.println("防火墙的IP配置完成!");

            /**
             *  防火墙策略创建
             */
            if (requestBody.getFirewallPolicy() != null) {
                // 创建防火墙策略
                FirewallPolicyInner firewallPolicyInner = new FirewallPolicyInner()
                        .withLocation(requestBody.getRegion())
                        .withSku(new FirewallPolicySku().withTier(FirewallPolicySkuTier.BASIC));

                // 提交创建防火墙策略的操作
                FirewallPolicyInner createdFirewallPolicy = azureResourceManager.networks().manager().serviceClient()
                        .getFirewallPolicies().createOrUpdate(requestBody.getResourceGroup(), requestBody.getFirewallPolicyName(), firewallPolicyInner);
                System.out.println("创建防火墙策略成功！");

                // 创建防火墙策略的规则集合
                List<FirewallPolicyRuleCollection> ruleCollectionInner = getPolicyRuleCollection(requestBody);

                // 创建规则集合组inner
                FirewallPolicyRuleCollectionGroupInner ruleCollectionGroupInner = new FirewallPolicyRuleCollectionGroupInner()
                        .withName("MyRuleCollectionGroup" + UUID.randomUUID())
                        .withPriority(100)
                        .withRuleCollections(ruleCollectionInner);
                System.out.println("创建规则集合组成功！");

                // 提交创建规则集合组的操作
                azureResourceManager.networks().manager().serviceClient()
                        .getFirewallPolicyRuleCollectionGroups()
                        .createOrUpdate(requestBody.getResourceGroup(), createdFirewallPolicy.name(), ruleCollectionGroupInner.name(), ruleCollectionGroupInner);

                // 提交更新防火墙策略的操作
                azureResourceManager.networks().manager().serviceClient()
                        .getFirewallPolicies()
                        .createOrUpdate(requestBody.getResourceGroup(), createdFirewallPolicy.name(), createdFirewallPolicy);
                System.out.println("更新防火墙策略成功！");
            }

            // 查找刚才创建的防火墙策略的id
            AtomicReference<SubResource> firewallPolicySubResource = new AtomicReference<>();
            if (requestBody.getFirewallPolicy() != null) {
                // 遍历防火墙策略列表
                azureResourceManager.networks().manager().serviceClient().getFirewallPolicies().list().forEach(firewallPolicy -> {
                    if (firewallPolicy.name().equals(requestBody.getFirewallPolicyName())) {
                        firewallPolicySubResource.set(new SubResource().withId(firewallPolicy.id()));
                    }
                });
            }

            //当防火墙SKU为基本时，需要配置管理公共IP地址，且改公共IP的SKU必须为标准
            AtomicReference<SubResource> managePublicIpResource = new AtomicReference<>();
            PublicIpAddress managePublicIp = azureResourceManager.publicIpAddresses().define(UUID.randomUUID().toString())
                    .withRegion(requestBody.getRegion())
                    .withExistingResourceGroup(requestBody.getResourceGroup())
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
                    .withName("managePublicIpResource"+UUID.randomUUID())
                    .withPublicIpAddress(managePublicIpResource.get())
                    .withSubnet(managerSubnetResource.get());

            // 创建防火墙实例
            AzureFirewallInner azureFirewallUpdateInner = new AzureFirewallInner()
                    .withLocation(requestBody.getRegion())
                    .withSku(new AzureFirewallSku()
                            .withName(AzureFirewallSkuName.AZFW_VNET)
                            .withTier(AzureFirewallSkuTier.BASIC))
                    .withFirewallPolicy(firewallPolicySubResource.get())
                    .withIpConfigurations(firewallIPConfigs)
                    .withManagementIpConfiguration(managePublicIpConfig);

            // 提交创建防火墙实例的操作
            AzureFirewallInner create = azureResourceManager.networks().manager().serviceClient()
                    .getAzureFirewalls().createOrUpdate(requestBody.getResourceGroup(), requestBody.getFirewallName(), azureFirewallUpdateInner);
            List<AzureFirewallIpConfiguration> azureFirewallIpConfigurations = create.ipConfigurations();
            String privateIpAddress = azureFirewallIpConfigurations.get(0).privateIpAddress();
            responseBody.getFirewallBuildResult().setFirewallStaticIp(privateIpAddress);
            System.out.println("创建防火墙和绑定防火墙策略成功！");
        } catch (Exception e) {
            System.err.println("创建防火墙失败：" + e.getMessage());
            e.printStackTrace();
            responseBody.getFirewallBuildResult().setResult(false);
            responseBody.getFirewallBuildResult().setErrorMessage("创建防火墙失败：" + e.getMessage());
            return responseBody;
        }
        responseBody.getFirewallBuildResult().setResult(true);
        //创建路由表
        RouteTableCreateRequestBody body = new RouteTableCreateRequestBody();
        body.setName(UUID.randomUUID().toString());
        body.setRegion(requestBody.getRegion());
        body.setFirewallPrivateAddress(responseBody.getFirewallBuildResult().getFirewallStaticIp());
        body.setVirtualNetwork(requestBody.getVirtualNetwork());
        body.setResourceGroup(requestBody.getResourceGroup());
        routeTableFunction.createRouteTable(responseBody,cloudType, body);
        return responseBody;
    }

    // 创建防火墙策略的规则集合
    private List<FirewallPolicyRuleCollection> getPolicyRuleCollection(FirewallBuildRequestBody requestBody) {

        List<FirewallPolicyRuleCollection> filterRuleCollections = new ArrayList<>();

        //根据允许和拒绝分组创建防火墙规则集合
        List<FirewallPolicyRuleRequestBody> allowRequestRules = new ArrayList<>();
        List<FirewallPolicyRuleRequestBody> rejectRequestRules = new ArrayList<>();
        requestBody.getFirewallPolicy().getRules().forEach(rule -> {
            //根据允许和拒绝分组创建防火墙规则集合
            if (rule.getAction().equals("allow")) {
                allowRequestRules.add(rule);
            } else if(rule.getAction().equals("reject")) {
                rejectRequestRules.add(rule);
            }
        });

        FirewallPolicyFilterRuleCollectionActionType allow = FirewallPolicyFilterRuleCollectionActionType.ALLOW;
        collectRuleCollection(allow, allowRequestRules, 100, filterRuleCollections);
        FirewallPolicyFilterRuleCollectionActionType reject = FirewallPolicyFilterRuleCollectionActionType.DENY;
        collectRuleCollection(reject, rejectRequestRules, 200, filterRuleCollections);

        return filterRuleCollections;
    }

    // 创建防火墙规则集合
    private void collectRuleCollection(FirewallPolicyFilterRuleCollectionActionType allow, List<FirewallPolicyRuleRequestBody> allowRequestRules,
                                       Integer ruleGroupPriority, List<FirewallPolicyRuleCollection> filterRuleCollections){
        if(allowRequestRules != null && allowRequestRules.size() > 0){
            List<FirewallPolicyRule> allowRules = new ArrayList<>();
            for(FirewallPolicyRuleRequestBody allowRule : allowRequestRules){
                List<String> destinationIpAddresses = getFilterValueList(allowRule.getDestinationIpAddress(), "normal");
                List<String> destinationPorts = getFilterValueList(allowRule.getDestinationPort(), "normal");
                List<String> sourceIpAddresses = getFilterValueList(allowRule.getSourceIpAddress(), "normal");
                List<FirewallPolicyRuleNetworkProtocol> protocols = getFilterValueList(allowRule.getProtocol(), "protocol");

                // 创建规则(目标类型和源类型默认为IP地址)
                FirewallPolicyRule networkRule = new NetworkRule()
                        .withName(allowRule.getName() != null ? allowRule.getName() : "AllowHTTP" + UUID.randomUUID())
                        .withDescription(allowRule.getDescription() != null ? allowRule.getDescription() : "Allow HTTP traffic" + UUID.randomUUID())
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

    private List getFilterValueList(String value, String type){
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
