package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import com.azure.core.management.Region;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.network.models.Network;
import com.azure.resourcemanager.network.models.NetworkSecurityGroup;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AzureVirtualNet {
    private IAzureResourceManager resourceService;

    private AzureResourceManager azureResourceManager;

    private Region region;

    public AzureVirtualNet(IAzureResourceManager resourceService) {
        this.resourceService = resourceService;
        this.azureResourceManager = resourceService.getManager();
        this.region = resourceService.getRegin();
    }

    public void create(CloudScenePluginHandler pluginHandler, CloudPluginSceneElementBO cloudElement, ConcurrentHashMap<String, Boolean> cidrContainerMap) {
//        for (int i = 0; i < cloudElement.getDeriveNetworks().size(); i++) {
        var net = cloudElement.getDeriveNetworks().get(0).getNetwork();
        // azure todo 网络名称用节点 eid+序号
        String netName = HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid(), 0);
        // azure todo 不需要设置网关么?
        String gateway = net.getGateway();
//            String cidr = net.getCidr();
        String groupName = pluginHandler.getSourceSceneId();
        //        nsg设置子网间网络隔离（不包括arkime）
//            创建nsg
        NetworkSecurityGroup nsg = azureResourceManager.networkSecurityGroups()
                .define("nsg")
                .withRegion(this.region)
                .withExistingResourceGroup(groupName)
                .create();

        //  azure 调用微软云api 创建网络
        var createdNet = azureResourceManager.networks()
                .define(netName)
                .withRegion(this.region)
                .withExistingResourceGroup(groupName);
        Set<Map.Entry<String, Boolean>> entrySet = cidrContainerMap.entrySet();
        for (Map.Entry<String, Boolean> entry : entrySet) {
            Boolean value = entry.getValue();
            if (value) {
//                连接容器的子网需要加子网委托
                createdNet
                        .withAddressSpace(entry.getKey())
                        .defineSubnet(IpUtils.extractNetworkPart(entry.getKey()))
                        .withAddressPrefix(entry.getKey())
                        .withDelegation("Microsoft.ContainerInstance/containerGroups")
//                          子网加入nsg
                        .withExistingNetworkSecurityGroup(nsg)
                        .attach();
            } else {
                createdNet
                        .withAddressSpace(entry.getKey())
                        .defineSubnet(IpUtils.extractNetworkPart(entry.getKey()))
                        .withAddressPrefix(entry.getKey())
                        .withExistingNetworkSecurityGroup(nsg)
                        .attach();
            }
        }
//        创建arkime子网
        String unused192Cidr = findUnused192Cidr(cidrContainerMap);
        createdNet
                .withAddressSpace(unused192Cidr)
                .defineSubnet("containerSub")
                .withAddressPrefix(unused192Cidr)
                .withDelegation("Microsoft.ContainerInstance/containerGroups")
                .attach();
        Network network = createdNet.create();


        // azure 回写网络id
        net.setResourceId(network.id());

//            创建入站规则
        NetworkSecurityGroup.Update update = nsg.update();
        ArrayList<String> cidrs = new ArrayList<>(cidrContainerMap.keySet());
        for (int i = 0; i < cidrs.size(); i++) {
            for (int j = i + 1; j < cidrs.size(); j++) {
//                TODO 入站规则
                String ipi = IpUtils.extractNetworkPart(cidrs.get(i)).replaceAll("\\.", "");
                String ipj = IpUtils.extractNetworkPart(cidrs.get(j)).replaceAll("\\.", "");
                update.defineRule(ipi + "deny" + ipj)
                        .denyInbound()
                        .fromAddress(cidrs.get(i))
//                        .fromPortRanges("*")
                        .fromAnyPort()
                        .toAddress(cidrs.get(j))
                        .toAnyPort()
                        .withAnyProtocol()
                        .withPriority(1000 + i * 10 + j)
                        .attach()
                        .defineRule(ipj + "deny" + ipi)
                        .denyInbound()
                        .fromAddress(cidrs.get(j))
                        .fromAnyPort()
                        .toAddress(cidrs.get(i))
                        .toAnyPort()
                        .withAnyProtocol()
                        .withPriority(1000 + j * 10 + i)
                        .attach();
            }
        }
        update.apply();
    }

    public static String findUnused192Cidr(ConcurrentHashMap<String, Boolean> cidrContainerMap) {
        for (int i = 1; i <= 255; i++) {
            for (int j = 0; j <= 255; j++) {
                String cidr = "192.168." + i + "." + j + "/24";
                if (!cidrContainerMap.containsKey(cidr)) {
                    return cidr;
                }
            }
        }
        return null; // 如果没有找到符合条件的CIDR
    }

    public void delete(CloudPluginSceneElementBO cloudElement) {
        azureResourceManager.containerGroups().deleteById(cloudElement.getResourceId());
    }
}
