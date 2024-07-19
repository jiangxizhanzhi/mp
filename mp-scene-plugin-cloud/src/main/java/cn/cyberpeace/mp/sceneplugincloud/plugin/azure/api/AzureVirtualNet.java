package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import com.azure.core.management.Region;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.network.models.Network;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AzureVirtualNet {
    private IAzureResourceManager resourceService;

    private AzureResourceManager azureResourceManager;

    private Region region;

    public AzureVirtualNet(IAzureResourceManager resourceService) {
        this.resourceService = resourceService;
        this.azureResourceManager = resourceService.getManager();
        this.region = resourceService.getRegin();
    }

    public void create(CloudScenePluginHandler pluginHandler, CloudPluginSceneElementBO cloudElement) {
        for (int i = 0; i < cloudElement.getDeriveNetworks().size(); i++) {
            var net = cloudElement.getDeriveNetworks().get(i).getNetwork();
            // azure todo 网络名称用节点 eid+序号
            String netName = HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid(), i);
            // azure todo 不需要设置网关么?
            String gateway = net.getGateway();
            String cidr = net.getCidr();
            String groupName = pluginHandler.getSourceSceneId();
            //  azure todo 调用微软云api 创建网络
            var createdNet = azureResourceManager.networks()
                    .define(netName)
                    .withRegion(this.region)
                    .withExistingResourceGroup(groupName)
                    .withAddressSpace(cidr)
                    .withSubnet("sub", cidr)
                    .create();
            // azure todo 回写网络id
            net.setResourceId(createdNet.id());
        }
    }

    public void create(
            CloudScenePluginHandler pluginHandler, CloudPluginSceneElementBO cloudElement,
            ConcurrentHashMap<String, Integer> cidrContainerMap, HashMap<String, List<VirtualNic>> virtualNicsMap) {
        // azure todo 网络名称用节点 eid+序号
        String netName = HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid(), 0);
        // azure todo 不需要设置网关么?
//        String gateway = net.getGateway();
//            String cidr = net.getCidr();
        String groupName = pluginHandler.getSourceSceneId();

        //  azure 调用微软云api 创建网络
        var createdNet = azureResourceManager.networks()
                .define(netName)
                .withRegion(this.region)
                .withExistingResourceGroup(groupName);
        Set<Map.Entry<String, Integer>> entrySet = cidrContainerMap.entrySet();
        for (Map.Entry<String, Integer> entry : entrySet) {
            Integer value = entry.getValue();
            if (value == 1) {
//                连接容器的子网需要加子网委托
                createdNet
                        .withAddressSpace(entry.getKey())
                        .defineSubnet(IpUtils.extractNetworkPart(entry.getKey()))
                        .withAddressPrefix(entry.getKey())
                        .withDelegation("Microsoft.ContainerInstance/containerGroups")
                        .attach();
            } else if (value == 0) {
                createdNet
                        .withAddressSpace(entry.getKey())
                        .defineSubnet(IpUtils.extractNetworkPart(entry.getKey()))
                        .withAddressPrefix(entry.getKey())
                        .attach();
            } else if (value == 2) {
                createdNet
                        .withAddressSpace(entry.getKey())
                        .defineSubnet("AzureFirewallManagementSubnet")
                        .withAddressPrefix(entry.getKey())
                        .attach();
            } else if (value == 3) {
                createdNet
                        .withAddressSpace(entry.getKey())
                        .defineSubnet("AzureFirewallSubnet")
                        .withAddressPrefix(entry.getKey())
                        .attach();
            }
        }

        Network network = createdNet.create();
        cloudElement.setResourceId(network.id());

        // azure 回写网络id
        cloudElement.getDeriveNetworks().stream().forEach(networkBO -> {
            CloudPluginSceneElementNetworkBO net = networkBO.getNetwork();
            net.setResourceId(network.id());
        });
        List<String> nodeIds = null;
        if (BaseCloudPluginSceneElementModel.ImageType.VNET.equals(cloudElement.getImageType())) {
            nodeIds = cloudElement.getDeriveNetworks().stream()
                    .flatMap(connectedNetworkBO -> connectedNetworkBO.getToPorts().stream())
                    .map(SceneElementPortBO::getElement)
                    .map(SceneElementBO::getId)
                    .distinct()
                    .collect(Collectors.toList());


        } else {
            nodeIds = new ArrayList<>();
            nodeIds.add(cloudElement.getSrcElement().getId());
        }

        nodeIds.stream().forEach(nodeId -> {
            List<VirtualNic> virtualNics = virtualNicsMap.get(nodeId);
            if (virtualNics != null) {
                virtualNics.forEach(virtualNic -> {
                    virtualNic.setNetworkId(network.id());
                });
            }
            virtualNicsMap.put(nodeId, virtualNics);
        });


    }

    public void delete(CloudPluginSceneElementBO cloudElement) {
        azureResourceManager.containerGroups().deleteById(cloudElement.getResourceId());
    }
}
