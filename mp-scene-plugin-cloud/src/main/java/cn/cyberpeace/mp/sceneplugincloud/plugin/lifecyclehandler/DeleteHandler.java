package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConnectionModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.EntityNode;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Npc;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualFirewall;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualRouter;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualSwitch;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.EntityNodeDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.BaseNpcData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualFirewallDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.DeleteTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConnectionMapper;
import cn.cyberpeace.mp.sceneplugincloud.util.ElementTypeUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class DeleteHandler extends HandlerCommon {

    private final VirtualSwitch vsClient = new VirtualSwitch();
    private final VirtualRouter vrClient = new VirtualRouter();
    private final VirtualFirewall vfClient = new VirtualFirewall();
    private final VirtualMachine vmClient = new VirtualMachine();
    protected final Container containerClient = new Container();
    private final EntityNode enClient = new EntityNode();
    private final Npc npcClient = new Npc();

    private Map<String, BaseNpcData.NetWorkNpcInfo> network2Info = new ConcurrentHashMap<>();

    public static class SceneProcess {
        public static final EventProcess DELETE_NPCS = new EventProcess("delete_npcs", "sceneplugincloud.event.delete_npcs");
        public static final EventProcess DELETE_TERMINALS = new EventProcess("delete_terminals", "sceneplugincloud.event.delete_terminal");
        public static final EventProcess DELETE_FIREWALLS = new EventProcess("delete_firewalls", "sceneplugincloud.event.delete_firewall");
        public static final EventProcess DELETE_GATEWAYS = new EventProcess("delete_gateways", "sceneplugincloud.event.delete_gateway");
        public static final EventProcess DELETE_AUXILIARY_NETWORKS = new EventProcess("delete_auxiliary_networks", "sceneplugincloud.event.delete_auxiliary_network");

    }

    public static class ElementProcess {
        public static final EventProcess DELETE_AUXILIARY_NETWORK = new EventProcess("delete_auxiliary_network", "sceneplugincloud.event.delete_auxiliary_network");
        public static final EventProcess DELETE_VIRTUAL_SWITCH = new EventProcess("delete_virtual_switch", "sceneplugincloud.event.delete_virtual_switch");
        public static final EventProcess DELETE_VIRTUAL_ROUTER = new EventProcess("delete_virtual_router", "sceneplugincloud.event.delete_virtual_router");
        public static final EventProcess DELETE_VIRTUAL_FIREWALL = new EventProcess("delete_virtual_firewall", "sceneplugincloud.event.delete_virtual_firewall");
        public static final EventProcess DELETE_VIRTUAL_MACHINE = new EventProcess("delete_virtual_machine", "sceneplugincloud.event.delete_virtual_machine");
        public static final EventProcess DELETE_CONTAINER = new EventProcess("delete_container", "sceneplugincloud.event.delete_container");
        public static final EventProcess DELETE_ENTITY_NODE = new EventProcess("delete_entity_node", "sceneplugincloud.event.delete_entity_node");
        public static final EventProcess DELETE_NPC = new EventProcess("delete_npc", "sceneplugincloud.event.delete_npc");
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ElementExtraCloudInfo {
        private List<String> qosIds;
        private List<String> connectionIds;
        private List<String> proxyIds;
        private List<String> floatingIpIds;
    }

    private ElementExtraCloudInfo collectElementExtraCloudInfo(CloudPluginSceneElementBO cloudElement) {
        Set<String> qosIds = new HashSet<>();
        Set<String> proxyIds = new HashSet<>();
        Set<String> floatingIpIds = new HashSet<>();
        var cloudPorts = getPluginHandler().getPorts(cloudElement);
        cloudPorts.forEach(cloudPort -> {
            var qosId = cloudPort.getQosId();
            if (!StringUtils.isEmpty(qosId)) {
                qosIds.add(qosId);
            }

            var accessModes = cloudPort.getAccessModes();
            if (accessModes != null) {
                accessModes.forEach(accessMode -> {
                    var proxyId = accessMode.getProxyId();
                    if (!StringUtils.isEmpty(proxyId)) {
                        proxyIds.add(proxyId);
                    }
                });
            }

            String floatingIpId = cloudPort.getFloatingIpId();
            if (!StringUtils.isEmpty(floatingIpId)) {
                floatingIpIds.add(floatingIpId);
            }
        });
        Set<String> connectionIds = this.getConnectionIds(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
        return new ElementExtraCloudInfo(new ArrayList<>(qosIds), new ArrayList<>(connectionIds), new ArrayList<>(proxyIds), new ArrayList<>(floatingIpIds));
    }

    private boolean deleteAuxiliaryNetwork(CloudPluginSceneElementBO cloudElement) {
        var flag = true;
        cloudElement.getDeriveNetworks().forEach(n -> {
            // 先删除辅助路由 再删除网络
            if (StringUtils.isNotEmpty(n.network.getAuxiliaryNetworkRouterResourceId())) {
                var virtualRouterDeleteData = new VirtualRouterDeleteData();
                virtualRouterDeleteData.setIds(List.of(n.network.getAuxiliaryNetworkRouterResourceId()));
                vrClient.delete(virtualRouterDeleteData);
            }
            var deleteData = new VirtualSwitchDeleteData();
            deleteData.setEntityNetwork(n.getNetwork().getVlan());
            deleteData.setIds(List.of(n.getNetwork().getResourceId()));
            vsClient.delete(deleteData);
        });
//        var resourceId = cloudElement.getAuxiliaryNetworkId();
//        if (!StringUtils.isEmpty(resourceId)) {
//            var deleteData = new VirtualSwitchDeleteData();
//            deleteData.setEntityNetwork(cloudElement.getAuxiliaryNetworkVlan());
//            deleteData.setIds(List.of(resourceId));
//            flag = vsClient.delete(deleteData);
//        }
//
//        flag = flag && deleteAuxiliaryNetworkRouter(cloudElement);
        return flag;
    }

    private boolean deleteAuxiliaryNetworkRouter(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getAuxiliaryNetworkRouterResourceId();
        if (StringUtils.isEmpty(resourceId)) {
            return true;
        }
        var deleteData = new VirtualRouterDeleteData();
        deleteData.setIds(List.of(resourceId));
        return vrClient.delete(deleteData);
    }

    private boolean deleteVirtualRouterFirewall(CloudPluginSceneElementBO cloudElement) {
        Set<String> firewallResourceIds = new HashSet<>();
        if (!StringUtils.isEmpty(cloudElement.getFirewallResourceId())) {
            firewallResourceIds.add(cloudElement.getFirewallResourceId());
        }
        getPluginHandler().getPorts(cloudElement).forEach(cloudPort -> {
            if (!StringUtils.isEmpty(cloudPort.getFirewallResourceId())) {
                firewallResourceIds.add(cloudPort.getFirewallResourceId());
            }
        });

        if (firewallResourceIds.isEmpty()) {
            return true;
        }

        var deleteData = new VirtualFirewallDeleteData();
        deleteData.setIds(new ArrayList<>(firewallResourceIds));
        return vfClient.delete(deleteData);
    }

    protected boolean deleteVirtualRouter(CloudPluginSceneElementBO cloudElement) {
        boolean flag = deleteVirtualRouterFirewall(cloudElement);
        var resourceId = cloudElement.getResourceId();
        if (!StringUtils.isEmpty(resourceId)) {
            var deleteData = new VirtualRouterDeleteData();
            deleteData.setIds(List.of(resourceId));
            flag = flag && vrClient.delete(deleteData);
        }
        return flag;
    }

    protected boolean deleteVirtualFirewall(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getResourceId();
        if (StringUtils.isEmpty(resourceId)) {
            return true;
        }

        var deleteData = new VirtualFirewallDeleteData();
        deleteData.setIds(List.of(resourceId));
        return vfClient.delete(deleteData);
    }

    protected boolean deleteVirtualMachine(CloudPluginSceneElementBO cloudElement) {
        if (ElementTypeUtils.isExternal(cloudElement)) {
            if (cloudElement.getSrcElement().getPorts() != null) {
                cloudElement.getSrcElement().getPorts().forEach(p -> {
                    var pluginPort = getPluginHandler().getPort(p.getId());
                    if (pluginPort != null && StringUtils.isNotEmpty(pluginPort.getResourceId())) {
                        vmClient.deletePort(cloudElement.getResourceId(), pluginPort.getResourceId());
                    }
                });
            }
            return true;
        }
        var resourceId = cloudElement.getResourceId();
        if (StringUtils.isEmpty(resourceId)) {
            return true;
        }

        closeAccessModes(cloudElement);

        var deleteData = new VirtualMachineDeleteData();
        deleteData.setVms(List.of(resourceId));
        var extraInfo = collectElementExtraCloudInfo(cloudElement);
        deleteData.setQoses(extraInfo.getQosIds());
        deleteData.setConnections(extraInfo.getConnectionIds());
        deleteData.setPortMappings(extraInfo.getProxyIds());
        deleteData.setFloatingIps(extraInfo.getFloatingIpIds());
        return vmClient.delete(deleteData);
    }

    protected boolean deleteContainer(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getResourceId();
        if (StringUtils.isEmpty(resourceId)) {
            return true;
        }

        closeAccessModes(cloudElement);

        var deleteData = new ContainerDeleteData();
        deleteData.setContainers(List.of(resourceId));
        var extraInfo = collectElementExtraCloudInfo(cloudElement);
        deleteData.setQoses(extraInfo.getQosIds());
        deleteData.setConnections(extraInfo.getConnectionIds());
        deleteData.setPortMappings(extraInfo.getProxyIds());
        deleteData.setFloatingIps(extraInfo.getFloatingIpIds());
        return containerClient.delete(deleteData);
    }

    protected boolean deleteEntityNode(CloudPluginSceneElementBO cloudElement) {
        closeAccessModes(cloudElement);

        var deleteData = new EntityNodeDeleteData();
        List<String> interfaceNames = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(cloudElement.getSrcElement().getPorts())) {
            interfaceNames = cloudElement.getSrcElement().getPorts().stream()
                    .map(BaseSceneElementPortBO::getName).collect(Collectors.toList());
        }
        deleteData.setInterfaces(interfaceNames);
        var extraInfo = collectElementExtraCloudInfo(cloudElement);
        deleteData.setConnections(extraInfo.getConnectionIds());
        return enClient.delete(deleteData);
    }

    protected void recordNpcNode(CloudPluginSceneElementBO cloudElement) {
        String npcName = HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid());
        cloudElement.getAccessNetworks().forEach(net -> {
            String networkId = net.network.getResourceId();
            if (!network2Info.containsKey(networkId)) {
                network2Info.put(networkId, new BaseNpcData.NetWorkNpcInfo(networkId));
            }
            net.getFromPorts().forEach(port -> {
                network2Info.get(networkId).addNpcInfo(npcName, port.getIp());
            });
        });
        cloudElement.getDeriveNetworks().forEach(net -> {
            String networkId = net.network.getResourceId();
            if (!network2Info.containsKey(networkId)) {
                network2Info.put(networkId, new BaseNpcData.NetWorkNpcInfo(networkId));
            }
            net.getFromPorts().forEach(port -> {
                network2Info.get(networkId).addNpcInfo(npcName, port.getIp());
            });
        });
    }

    public DeleteHandler(CloudScenePluginHandler pluginHandler) {
        super(pluginHandler, DeleteTaskPool.getInstance(), SceneModel.Event.DELETE);
    }

    public void delete() {
        prepareEventProcess(SceneModel.Event.DELETE, SceneProcess.class, ElementProcess.class);

        // 删除终端
        var deleteTerminalTasks = getPluginHandler().getTerminalElementMap().values().stream()
                .map(cloudElement -> (Runnable) () -> deleteElementProcess(cloudElement)).collect(Collectors.toList());
        if (!deleteTerminalTasks.isEmpty()) {
            runSceneProgress(SceneProcess.DELETE_TERMINALS, () -> runTasks(deleteTerminalTasks));
        }

        // 删除npc节点
        if (network2Info.values().size() > 0) {
            runSceneProgress(SceneProcess.DELETE_NPCS, () -> deleteNpcElements(new ArrayList<>(network2Info.values())));
        }

        // 删除防火墙
        var deleteFirewallTasks = getPluginHandler().getFirewallElementMap().values().stream()
                .map(cloudElement -> (Runnable) () -> deleteElementProcess(cloudElement)).collect(Collectors.toList());
        if (!deleteFirewallTasks.isEmpty()) {
            runSceneProgress(SceneProcess.DELETE_FIREWALLS, () -> runTasks(deleteFirewallTasks));
        }

        // 删除网关
        var deleteGatewayTasks = getPluginHandler().getGatewayElementMap().values().stream()
                .map(cloudElement -> (Runnable) () -> deleteElementProcess(cloudElement)).collect(Collectors.toList());
        if (!deleteGatewayTasks.isEmpty()) {
            runSceneProgress(SceneProcess.DELETE_GATEWAYS, () -> runTasks(deleteGatewayTasks));
        }

        // 删除辅助网络(包含虚拟交换机)
        var deleteAuxiliaryNetworkTasks = getPluginHandler().getElementMap().values().stream()
                .filter(cloudElement -> {
                    if (cloudElement.getDeriveNetworks().isEmpty()) {
                        return false;
                    }

                    // 外部虚拟交换机不能删除
                    Integer virtualType = cloudElement.getVirtualType();
                    if (virtualType != null
                            && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)
                            && Boolean.TRUE.equals(cloudElement.getExternal())) {
                        return false;
                    }

                    return true;
                })
                .map(cloudElement -> (Runnable) () -> deleteAuxiliaryNetworkProcess(cloudElement))
                .collect(Collectors.toList());
        if (!deleteAuxiliaryNetworkTasks.isEmpty()) {
            runSceneProgress(SceneProcess.DELETE_AUXILIARY_NETWORKS, () -> runTasks(deleteAuxiliaryNetworkTasks));
        }
    }

    private void deleteNpcElements(List<BaseNpcData.NetWorkNpcInfo> list) {
        NpcDeleteData npcDeleteData = new NpcDeleteData();
        npcDeleteData.setNetWorkNpcInfos(list);
        npcClient.delete(npcDeleteData);
    }

    public void deleteAuxiliaryNetworkProcess(CloudPluginSceneElementBO cloudElement) {
        runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.DELETE_AUXILIARY_NETWORK,
                () -> deleteAuxiliaryNetwork(cloudElement));
    }

    private void deleteElementProcess(CloudPluginSceneElementBO cloudElement) {
        if (Boolean.TRUE.equals(cloudElement.getExternal()) && !ElementTypeUtils.isExternalTerminal(cloudElement)) {
            return;
        }
        Integer virtualType = cloudElement.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", cloudElement.getSrcElement().getEid(),
                    cloudElement.getSrcElement().getId());
            return;
        }

        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
            runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.DELETE_VIRTUAL_ROUTER,
                    () -> deleteVirtualRouter(cloudElement));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL)) {
            runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.DELETE_VIRTUAL_FIREWALL,
                    () -> deleteVirtualFirewall(cloudElement));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.DELETE_VIRTUAL_MACHINE,
                    () -> {
                        deleteVirtualMachine(cloudElement);
                        this.disableConnectUrl(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
                    });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.DELETE_CONTAINER,
                    () -> {
                        deleteContainer(cloudElement);
                        this.disableConnectUrl(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
                    });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
            runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.DELETE_ENTITY_NODE,
                    () -> {
                        deleteEntityNode(cloudElement);
                        this.disableConnectUrl(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
                    });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.NPC)) {
            runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.DELETE_NPC,
                    () -> {
                        recordNpcNode(cloudElement);
                    });
        }
    }

    protected void disableConnectUrl(String scene, String sceneElement) {
        CloudPluginSceneConnectionMapper mapper = BeanCommon.getBean(CloudPluginSceneConnectionMapper.class);
        CloudPluginSceneConnectionModel updateModel = new CloudPluginSceneConnectionModel();
        updateModel.setDisable(true);
        mapper.filterUpdate(updateModel, new HumanCondition()
                .filterEqual(CloudPluginSceneConnectionModel::getScene, scene)
                .filterEqual(CloudPluginSceneConnectionModel::getSceneElement, sceneElement)
                .filterEqual(CloudPluginSceneConnectionModel::getDisable, false)
        );
    }

    private Set<String> getConnectionIds(String scene, String sceneElement) {
        CloudPluginSceneConnectionMapper mapper = BeanCommon.getBean(CloudPluginSceneConnectionMapper.class);
        var connectionModels = mapper.list(new HumanCondition()
                .filterEqual(CloudPluginSceneConnectionModel::getScene, scene)
                .filterEqual(CloudPluginSceneConnectionModel::getSceneElement, sceneElement)
        );
        Set<String> result = new HashSet<>();
        if (CollectionUtils.isNotEmpty(connectionModels)) {
            List<String> connectionIds = connectionModels.stream()
                    .filter(connectionModel -> connectionModel.getConnectionId() != null)
                    .map(connectionModel -> connectionModel.getConnectionId())
                    .collect(Collectors.toList());
            result.addAll(connectionIds);
        }
        return result;
    }
}
