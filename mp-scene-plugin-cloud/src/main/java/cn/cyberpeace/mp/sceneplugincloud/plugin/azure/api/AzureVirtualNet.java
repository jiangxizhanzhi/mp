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
import cn.cyberpeace.mp.sceneplugincloud.util.ElementNetworkUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import com.azure.core.management.Region;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.VirtualMachine;
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

    public void create(
            CloudScenePluginHandler pluginHandler, CloudPluginSceneElementBO cloudElement,
            ConcurrentHashMap<String, ElementNetworkUtils.CidrType> cidrContainerMap,
            HashMap<String, List<VirtualNic>> virtualNicsMap) {
        // azure todo 网络名称用节点 eid+序号
        String netName = HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid(), 0);
        // azure todo 不需要设置网关么?
//        String gateway = net.getGateway();
//            String cidr = net.getCidr();
        String groupName = pluginHandler.getSourceSceneId();

        //  azure 调用微软云api 创建网络
        Network.DefinitionStages.WithCreate createdNet = azureResourceManager.networks()
                .define(netName)
                .withRegion(this.region)
                .withExistingResourceGroup(groupName);
        Set<Map.Entry<String, ElementNetworkUtils.CidrType>> entrySet = cidrContainerMap.entrySet();
        String mangeementSubName = null;
        for (Map.Entry<String, ElementNetworkUtils.CidrType> entry : entrySet) {
            String addressSpace = entry.getKey();
            ElementNetworkUtils.CidrType value = entry.getValue();

            switch (value) {
                case CONTAINER:
                    addSubnetWithDelegation(createdNet, addressSpace);
                    break;
                case VM:
                    addStandardSubnet(createdNet, addressSpace);
                    break;
                case FIRE_WALL_MANAGEMENT:
                    addFirewallManagementSubnet(createdNet, addressSpace);
                    break;
                case FIRE_WALL:
                    addFirewallSubnet(createdNet, addressSpace);
                    break;
                case MANAGEMENT:
                    addStandardSubnet(createdNet, addressSpace);
                    mangeementSubName = IpUtils.extractNetworkPart(addressSpace);
                    break;
            }

            /*if (value == ElementNetworkUtils.CidrType.CONTAINER) {
//                连接容器的子网需要加子网委托
                createdNet
                        .withAddressSpace(entry.getKey())
                        .defineSubnet(IpUtils.extractNetworkPart(entry.getKey()))
                        .withAddressPrefix(entry.getKey())
                        .withDelegation("Microsoft.ContainerInstance/containerGroups")
                        .attach();
            } else if (value == ElementNetworkUtils.CidrType.VM) {
                createdNet
                        .withAddressSpace(entry.getKey())
                        .defineSubnet(IpUtils.extractNetworkPart(entry.getKey()))
                        .withAddressPrefix(entry.getKey())
                        .attach();
            } else if (value == ElementNetworkUtils.CidrType.FIRE_WALL_MANAGEMENT) {
                createdNet
                        .withAddressSpace(entry.getKey())
                        .defineSubnet("AzureFirewallManagementSubnet")
                        .withAddressPrefix(entry.getKey())
                        .attach();
            } else if (value == ElementNetworkUtils.CidrType.FIRE_WALL) {
                createdNet
                        .withAddressSpace(entry.getKey())
                        .defineSubnet("AzureFirewallSubnet")
                        .withAddressPrefix(entry.getKey())
                        .attach();
            }*/
        }

        Network network = createdNet.create();
        cloudElement.setResourceId(network.id());

//        创建管理虚拟机用于远程连接 TODO虚拟机未准备好暂不走此逻辑
        mangeementSubName = null;
        if (null != mangeementSubName) {
            AzureVirtualMachine azureVirtualMachine = new AzureVirtualMachine(resourceService);
            VirtualMachine mangeementVm = azureVirtualMachine.createManagementVM(groupName, "ManagementVM_" + netName, network, "subName");
            cloudElement.setMgmtIp(mangeementVm.getPrimaryPublicIPAddress().ipAddress());
        }

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
//            查找连接的节点 管理网络字段赋值
            if (null != mangeementSubName) {
                propagateMgmtIpToElements(pluginHandler, cloudElement, null);
            }


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

    /**
     * 添加具有委派的子网到网络资源创建阶段。
     * <p>
     * 该方法在给定的网络创建阶段中定义并添加一个子网，该子网具有特定的地址空间和委派给容器实例的权限。
     * 使用提供的地址空间来定义子网的地址前缀，并为子网配置特定的委派，允许在该子网中创建容器实例。
     *
     * @param createdNet   网络资源的创建阶段，用于定义和配置子网。
     * @param addressSpace 子网的地址空间，用于定义子网的地址范围。
     */
    private void addSubnetWithDelegation(Network.DefinitionStages.WithCreate createdNet, String addressSpace) {
        createdNet
                .withAddressSpace(addressSpace)
                .defineSubnet(IpUtils.extractNetworkPart(addressSpace))
                .withAddressPrefix(addressSpace)
                .withDelegation("Microsoft.ContainerInstance/containerGroups")
                .attach();
    }

    /**
     * 添加标准子网到网络创建阶段。
     * <p>
     * 此方法用于在创建网络的过程中，根据给定的地址空间添加一个标准子网。
     * 它首先定义了网络的地址空间，然后根据地址空间提取网络部分，用于定义子网的名称，
     * 最后将该子网附加到正在创建的网络上。
     *
     * @param createdNet   网络的创建阶段接口，用于链式调用定义网络属性。
     * @param addressSpace 网络的地址空间，用于定义子网的地址前缀。
     */
    private void addStandardSubnet(Network.DefinitionStages.WithCreate createdNet, String addressSpace) {
        createdNet
                .withAddressSpace(addressSpace)
                .defineSubnet(IpUtils.extractNetworkPart(addressSpace))
                .withAddressPrefix(addressSpace)
                .attach();
    }

    /**
     * 添加防火墙管理子网。
     * <p>
     * 该方法用于在创建网络时添加一个特定于Azure防火墙管理的子网。防火墙管理子网是一个专用的子网，
     * 用于承载Azure防火墙实例及其相关的管理通信。通过指定地址空间，确保防火墙管理子网在虚拟网络中独占一席之地。
     *
     * @param createdNet   网络创建阶段的实例，用于定义和配置新网络。
     * @param addressSpace 防火墙管理子网的地址空间。这个地址空间应该在虚拟网络的地址空间内，并且应该是唯一的。
     */
    private void addFirewallManagementSubnet(Network.DefinitionStages.WithCreate createdNet, String addressSpace) {
        createdNet
                .withAddressSpace(addressSpace)
                .defineSubnet("AzureFirewallManagementSubnet")
                .withAddressPrefix(addressSpace)
                .attach();
    }

    /**
     * 添加防火墙子网到网络配置中。
     * <p>
     * 该方法用于在创建新网络时定义并添加一个名为“AzureFirewallSubnet”的子网。
     * 子网的地址前缀与传入的地址空间相同，这是为了确保防火墙设备能够正常工作在指定的地址空间内。
     *
     * @param createdNet   网络创建阶段的实例，用于配置新网络。
     * @param addressSpace 新网络的地址空间，同时也是防火墙子网的地址前缀。
     */
    private void addFirewallSubnet(Network.DefinitionStages.WithCreate createdNet, String addressSpace) {
        createdNet
                .withAddressSpace(addressSpace)
                .defineSubnet("AzureFirewallSubnet")
                .withAddressPrefix(addressSpace)
                .attach();
    }

    /**
     * 在场景中的元素间传播管理IP。
     * 此方法递归调用，用于将管理IP从VNet元素传播至与其相连的LAAS_VM或VNet元素。
     * 它检查端口连接的元素类型，如果连接的是LAAS_VM，则直接设置管理IP；如果是VNET，则递归调用此方法。
     *
     * @param pluginHandler       插件处理器，用于获取场景元素和其他相关信息。
     * @param element             当前处理的场景元素，其源元素是VNet类型。
     * @param processedElementIds 已处理元素的ID列表，用于避免重复处理。
     */
    private void propagateMgmtIpToElements(CloudScenePluginHandler pluginHandler, CloudPluginSceneElementBO element, List<String> processedElementIds) {
        processedElementIds = processedElementIds == null ? new ArrayList<>() : processedElementIds;
        processedElementIds.add(element.getSrcElement().getId());
        for (SceneElementPortBO port : element.getSrcElement().getPorts()) {
            SceneElementPortBO portConnectedPort = pluginHandler.getSceneHandler().getPortConnectedPort(port.getId());
            if (null != portConnectedPort) {
                String id = portConnectedPort.getElement().getId();
                CloudPluginSceneElementBO cloudPluginSceneConfigElementBO = pluginHandler.getElement(id);
                if (BaseCloudPluginSceneElementModel.ImageType.LAAS_VM.equals(cloudPluginSceneConfigElementBO.getImageType())) {
                    cloudPluginSceneConfigElementBO.setMgmtIp(element.getMgmtIp());
                } else if (BaseCloudPluginSceneElementModel.ImageType.VNET.equals(cloudPluginSceneConfigElementBO.getImageType())) {
                    if (processedElementIds.contains(cloudPluginSceneConfigElementBO.getSrcElement().getId()))
                        continue;
                    propagateMgmtIpToElements(pluginHandler, cloudPluginSceneConfigElementBO, processedElementIds);
                }
            }
        }
    }
}
