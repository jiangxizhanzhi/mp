package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.DeleteTaskPool;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DeleteHandler extends HandlerCommon {

    private final VirtualSwitch vsClient = new VirtualSwitch();
    private final VirtualRouter vrClient = new VirtualRouter();
    private final VirtualFirewall vfClient = new VirtualFirewall();
    private final VirtualMachine vmClient = new VirtualMachine();
    private final Container containerClient = new Container();
    private final EntityNode enClient = new EntityNode();

    public static class SceneProcess {
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
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ElementExtraCloudInfo {
        private List<String> qosIds;
        private List<String> connectionIds;
        private List<String> proxyIds;
    }

    private ElementExtraCloudInfo collectElementExtraCloudInfo(CloudPluginSceneElementBO cloudElement) {
        List<String> qosIds = new ArrayList<>();
        List<String> connectionIds = new ArrayList<>();
        List<String> proxyIds = new ArrayList<>();
        var srcPorts = cloudElement.getSrcElement().getPorts();
        srcPorts.forEach(srcPort -> {
            var cloudPort = getPluginHandler().getPort(srcPort.getId());
            if (cloudPort == null) {
                return;
            }

            var qosId = cloudPort.getQosId();
            if (!StringUtils.isEmpty(qosId)) {
                qosIds.add(qosId);
            }

            var accessModes = cloudPort.getAccessModes();
            if (accessModes != null) {
                accessModes.forEach(accessMode -> {
                    var connectionId = accessMode.getConnectionId();
                    if (!StringUtils.isEmpty(connectionId)) {
                        connectionIds.add(connectionId);
                    }

                    var proxyId = accessMode.getProxyId();
                    if (!StringUtils.isEmpty(proxyId)) {
                        proxyIds.add(proxyId);
                    }
                });
            }
        });
        return new ElementExtraCloudInfo(qosIds, connectionIds, proxyIds);
    }

    private boolean deleteAuxiliaryNetwork(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getAuxiliaryNetworkId();
        if (StringUtils.isEmpty(resourceId)) {
            return true;
        }

        var deleteData = new VirtualSwitchDeleteData();
        deleteData.setEntityNetwork(cloudElement.getAuxiliaryNetworkVlan());
        deleteData.setIds(List.of(resourceId));
        return vsClient.delete(deleteData);
    }

    private boolean deleteVirtualRouter(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getResourceId();
        if (StringUtils.isEmpty(resourceId)) {
            return true;
        }

        var deleteData = new VirtualRouterDeleteData();
        deleteData.setIds(List.of(resourceId));
        return vrClient.delete(deleteData);
    }

    private boolean deleteVirtualFirewall(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getResourceId();
        if (StringUtils.isEmpty(resourceId)) {
            return true;
        }

        var deleteData = new VirtualFirewallDeleteData();
        deleteData.setIds(List.of(resourceId));
        return vfClient.delete(deleteData);
    }

    private boolean deleteVirtualMachine(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getResourceId();
        if (StringUtils.isEmpty(resourceId)) {
            return true;
        }

        var deleteData = new VirtualMachineDeleteData();
        deleteData.setVms(List.of(resourceId));
        var extraInfo = collectElementExtraCloudInfo(cloudElement);
        deleteData.setQoses(extraInfo.getQosIds());
        deleteData.setConnections(extraInfo.getConnectionIds());
        deleteData.setPortMappings(extraInfo.getProxyIds());
        return vmClient.delete(deleteData);
    }

    private boolean deleteContainer(CloudPluginSceneElementBO cloudElement) {
        var resourceId = cloudElement.getResourceId();
        if (StringUtils.isEmpty(resourceId)) {
            return true;
        }

        var deleteData = new ContainerDeleteData();
        deleteData.setContainers(List.of(resourceId));
        var extraInfo = collectElementExtraCloudInfo(cloudElement);
        deleteData.setQoses(extraInfo.getQosIds());
        deleteData.setConnections(extraInfo.getConnectionIds());
        deleteData.setPortMappings(extraInfo.getProxyIds());
        return containerClient.delete(deleteData);
    }

    private boolean deleteEntityNode(CloudPluginSceneElementBO cloudElement) {
        var deleteData = new EntityNodeDeleteData();
        List<String> interfaceNames = cloudElement.getSrcElement().getPorts().stream()
                .map(BaseSceneElementPortBO::getName).collect(Collectors.toList());
        deleteData.setInterfaces(interfaceNames);
        var extraInfo = collectElementExtraCloudInfo(cloudElement);
        deleteData.setConnections(extraInfo.getConnectionIds());
        return enClient.delete(deleteData);
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
                    if (!Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn())) {
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

    public void deleteAuxiliaryNetworkProcess(CloudPluginSceneElementBO cloudElement) {
        runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.DELETE_AUXILIARY_NETWORK,
                () -> deleteAuxiliaryNetwork(cloudElement));
    }

    private void deleteElementProcess(CloudPluginSceneElementBO cloudElement) {
        if (Boolean.TRUE.equals(cloudElement.getExternal())) {
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
                    () -> deleteVirtualMachine(cloudElement));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.DELETE_CONTAINER,
                    () -> deleteContainer(cloudElement));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
            runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.DELETE_ENTITY_NODE,
                    () -> deleteEntityNode(cloudElement));
        }
    }
}
