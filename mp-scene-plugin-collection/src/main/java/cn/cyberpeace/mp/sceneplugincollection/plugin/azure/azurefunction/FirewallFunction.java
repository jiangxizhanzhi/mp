package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction;

import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.functionrequest.FirewallCreateRequestBody;
import com.azure.core.management.SubResource;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.network.NetworkManager;
import com.azure.resourcemanager.network.fluent.NetworkManagementClient;
import com.azure.resourcemanager.network.fluent.models.AzureFirewallInner;
import com.azure.resourcemanager.network.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class FirewallFunction {

    // 防火墙创建
    // 创建前需要将虚拟网络和专有的防火墙子网创建好(防火墙子网名称必须为AzureFirewallSubnet且地址空间后缀必须为/26)
    public Boolean createFireWall(Integer cloudType, FirewallCreateRequestBody body) {
        // 获取默认订阅Id的资源管理器
        AzureResourceManager azureResourceManager = IAzureResourceManager.getInstance(cloudType).getManager();
        NetworkManager networkManager = azureResourceManager.networks().manager();
        // 资源组名称
        String resourceGroupName = body.getResourceGroup();
        // 区域
        String region = body.getRegion();

        try {
            Network firewallNet = null;
            if (body.getVirtualNetwork() == null) {
                // 创建虚拟网络及防火墙子网
                firewallNet = azureResourceManager.networks().define(UUID.randomUUID().toString())
                        .withRegion(region)
                        .withExistingResourceGroup(resourceGroupName)
                        .withAddressSpace("10.0.0.0/16")  // 设置虚拟网络的地址空间
                        .defineSubnet("default")
                            .withAddressPrefix("10.0.1.0/24")  // 设置子网的地址前缀
                            .attach()
                        .defineSubnet("AzureFirewallSubnet")
                            .withAddressPrefix("10.0.2.0/26")  // 设置子网的地址前缀
                            .attach()
                        .create();
            }else{
                firewallNet = body.getVirtualNetwork();
                // 判断防火墙子网是否存在
                if (firewallNet.subnets().get("AzureFirewallSubnet") == null) {
                    //生成一个新的地址空间
                    String randomNewAddressSpace = getRandomNewAddressSpace(firewallNet);
                    // 创建防火墙子网
                    firewallNet = azureResourceManager.networks().define(firewallNet.name())
                            .withRegion(region)
                            .withExistingResourceGroup(resourceGroupName)
                            .withAddressSpace(randomNewAddressSpace)  // 设置虚拟网络的地址空间
                            .defineSubnet("AzureFirewallSubnet")
                            .withAddressPrefix(randomNewAddressSpace)  // 设置子网的地址前缀
                            .attach()
                            .create();
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //防火墙子网
            AtomicReference<SubResource> subResource = new AtomicReference<>();
            Map<String, Subnet> subnets = firewallNet.subnets();
            for (Map.Entry<String, Subnet> entry : subnets.entrySet()) {
                System.out.println("key: " + entry.getKey() + " value: " + entry.getValue());
                if (entry.getKey().equals("AzureFirewallSubnet")) {
                    Subnet value = entry.getValue();
                    subResource.set(new SubResource().withId(value.id()));
                }
            }

            //防火墙公网ip
            AtomicReference<SubResource> publicIpResource = new AtomicReference<>();
            PublicIpAddress publicIpAddress = azureResourceManager.publicIpAddresses().define(UUID.randomUUID().toString())
                    .withRegion(region)
                    .withExistingResourceGroup(resourceGroupName)
                    .withSku(PublicIPSkuType.STANDARD)
                    .withStaticIP()
                    .create();
            publicIpResource.set(new SubResource().withId(publicIpAddress.id()));

            // 创建 AzureFirewallIPConfiguration 对象，用于定义防火墙的 IP 配置
            AzureFirewallIpConfiguration firewallIPConfig = new AzureFirewallIpConfiguration()
                    .withName("firewallIpConfig")
                    .withPublicIpAddress(publicIpResource.get())
                    .withSubnet(subResource.get()); // 根据防火墙子网，分配对应的静态 IP 地址
            List<AzureFirewallIpConfiguration> firewallIPConfigs = new ArrayList<>();
            firewallIPConfigs.add(firewallIPConfig);

            // 创建防火墙实例
            AzureFirewallInner azureFirewallInner = new AzureFirewallInner()
                    .withLocation(region)
                    .withSku(new AzureFirewallSku()
                            .withName(AzureFirewallSkuName.AZFW_VNET)
                            .withTier(body.getSku() != null ? AzureFirewallSkuTier.fromString(body.getSku()) : AzureFirewallSkuTier.STANDARD))
                    .withFirewallPolicy(null)
                    .withIpConfigurations(firewallIPConfigs);

            // 应用防火墙实例
            NetworkManagementClient networkManagementClient = networkManager.serviceClient();
            AzureFirewallInner newInner = networkManagementClient.getAzureFirewalls().createOrUpdate(resourceGroupName, body.getName(), azureFirewallInner);
            System.out.println("创建防火墙成功！");
        } catch (Exception e) {
            System.err.println("创建防火墙失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // 生成不同于addressSpaces的新地址空间作为虚拟网络的新地址空间和防火墙子网
    private static String getRandomNewAddressSpace(Network firewallNet) {
        List<String> addressSpaces = firewallNet.addressSpaces();
        String addressSpace = null;
        if(addressSpaces == null || addressSpaces.size() == 0){
            Random random = new Random();
            // 生成前三部分地址
            int part1 = random.nextInt(256);
            int part2 = random.nextInt(256);
            int part3 = random.nextInt(256);
            // 最后一部分固定为 0/26
            addressSpace = part1 + "." + part2 + "." + part3 + ".0/26";
            return addressSpace;
        }

        List<Integer> partOne = new ArrayList<>();
        List<Integer> partTwo = new ArrayList<>();
        List<Integer> partThree = new ArrayList<>();
        for(String address : addressSpaces){
            String[] parts = address.split("\\.");
            if(parts.length == 4){
                int part1 = Integer.parseInt(parts[0]);
                int part2 = Integer.parseInt(parts[1]);
                int part3 = Integer.parseInt(parts[2]);
                partOne.add(part1);
                partTwo.add(part2);
                partThree.add(part3);
            }
        }
        //生成一个不在partOne、partTwo、partThree中的地址空间，且后缀为/26
        addressSpace = generateUniqueAddressSpace(partOne, partTwo, partThree);
        return addressSpace;
    }

    //生成一个不在partOne、partTwo、partThree中的地址空间，且后缀为/26
    private static String generateUniqueAddressSpace(List<Integer> partOne, List<Integer> partTwo, List<Integer> partThree) {
        Random random = new Random();
        int part1, part2, part3;
        do {
            part1 = random.nextInt(256);
        } while (partOne.contains(part1));
        do {
            part2 = random.nextInt(256);
        } while (partTwo.contains(part2));
        do {
            part3 = random.nextInt(256);
        } while (partThree.contains(part3));
        return part1 + "." + part2 + "." + part3 + ".0/26";
    }

}
