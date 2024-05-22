package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import com.azure.core.management.Region;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.network.models.Network;

import java.util.Map;
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
        String vmType = cloudElement.getVmType();
//        for (int i = 0; i < cloudElement.getDeriveNetworks().size(); i++) {
        var net = cloudElement.getDeriveNetworks().get(0).getNetwork();
        // azure todo 网络名称用节点 eid+序号
        String netName = HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid(), 0);
        // azure todo 不需要设置网关么?
        String gateway = net.getGateway();
//            String cidr = net.getCidr();
        String groupName = pluginHandler.getSourceSceneId();
        //  azure todo 调用微软云api 创建网络
        var createdNet = azureResourceManager.networks()
                .define(netName)
                .withRegion(this.region)
                .withExistingResourceGroup(groupName);

        for (Map.Entry<String, Boolean> entry : cidrContainerMap.entrySet()) {
            Boolean value = entry.getValue();
            if (value) {
//                连接容器的子网需要加子网委托
                createdNet
                        .withAddressSpace(entry.getKey())
                        .defineSubnet(IpUtils.extractNetworkPart(entry.getKey()))
                        .withAddressPrefix(entry.getKey())
                        .withDelegation("Microsoft.ContainerInstance/containerGroups")
                        .attach();
            } else {
                createdNet
                        .withAddressSpace(entry.getKey())
                        .withSubnet(IpUtils.extractNetworkPart(entry.getKey()), entry.getKey());
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


        // azure todo 回写网络id
        net.setResourceId(network.id());
//        }
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
