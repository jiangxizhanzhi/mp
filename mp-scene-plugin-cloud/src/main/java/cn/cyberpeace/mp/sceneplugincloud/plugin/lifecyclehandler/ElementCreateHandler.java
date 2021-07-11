package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.basic.CollectionUtils;
import cn.cyberpeace.mp.base.util.basic.IntUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualSwitch;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RecreateTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.util.SceneInstanceLinkCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
public class ElementCreateHandler extends HandlerCommon {

    private final VirtualSwitch vsClient = new VirtualSwitch();

    public static class ElementProcess {
        public static final EventProcess START_VIRTUAL_ROUTER = new EventProcess("start_virtual_router", "sceneplugincloud.event.start_virtual_router");
        public static final EventProcess START_VIRTUAL_FIREWALL = new EventProcess("start_virtual_firewall", "sceneplugincloud.event.start_virtual_firewall");
        public static final EventProcess START_VIRTUAL_MACHINE = new EventProcess("start_virtual_machine", "sceneplugincloud.event.start_virtual_machine");
        public static final EventProcess START_CONTAINER = new EventProcess("start_container", "sceneplugincloud.event.start_container");
        public static final EventProcess START_ENTITY_NODE = new EventProcess("start_entity_node", "sceneplugincloud.event.start_entity_node");
    }

    private CreateHandler createHandler;

    public ElementCreateHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, RecreateTaskPool.getInstance(), event);
        this.createHandler = new CreateHandler(pluginHandler);
    }

    public void createElement(CloudPluginSceneElementBO element) {
        String nodeId = element.getSrcElement().getId();
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(),
                    element.getSrcElement().getId());
            return;
        }
        this.prepareElementProcess(nodeId, element.getVirtualType());
        List<ConnectedNetworkBO> networks = new ArrayList<>();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(element.getDeriveNetworks())) {
            networks.addAll(element.getDeriveNetworks());
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(element.getAccessNetworks())) {
            networks.addAll(element.getAccessNetworks());
        }
        this.createAuxiliaryNetwork(element, networks);
        getPluginHandler().updateElementNetworks(element.getSrcElement().getId());
        this.createHandler.createElementProcess(element);
        getPluginHandler().updateElements(element.getSrcElement().getId());
        SceneInstanceLinkCacheUtil.refresh(getPluginHandler().getSourceSceneId());
    }

    protected void createAuxiliaryNetwork(CloudPluginSceneElementBO cloudElement, List<ConnectedNetworkBO> networks) {
        int index = 1;
        for (ConnectedNetworkBO connectedNetwork : networks) {
            if (StringUtils.isNotEmpty(connectedNetwork.network.getResourceId())) {
                connectedNetwork.getFromPorts().forEach(p -> {
                    var pluginPort = getPluginHandler().getPort(p.getId());
                    if (pluginPort != null) {
                        pluginPort.setAuxiliaryNetworkId(connectedNetwork.network.getResourceId());
                    }
                });
                connectedNetwork.getToPorts().forEach(p -> {
                    var pluginPort = getPluginHandler().getPort(p.getId());
                    if (pluginPort != null) {
                        pluginPort.setAuxiliaryNetworkId(connectedNetwork.network.getResourceId());
                    }
                });
                continue;
            }
            var srcElement = cloudElement.getSrcElement();
            var createData = new VirtualSwitchCreateData();
            createData.setName(HandlerUtils.genResourceName(srcElement.getEid(), index++));
            createData.setEntityNetwork(connectedNetwork.getNetwork().getVlan());
            if (Boolean.TRUE.equals(connectedNetwork.network.getVlan())) {
                createData.setSegmentationId(IntUtils.zeroNull(connectedNetwork.network.getVlanId()));
            }
            var subnet = new VirtualSwitchCreateData.Subnet();
            subnet.setCidr(connectedNetwork.network.getCidr());
            subnet.setGatewayIp(TextUtils.emptyNull(connectedNetwork.network.getGateway()));
            subnet.setDhcpPortIp(TextUtils.emptyNull(connectedNetwork.network.getDhcpIp()));
            
            var dhcp = connectedNetwork.network.getDhcp();
            subnet.setEnableDhcp(dhcp == null || dhcp);

            var srcAllocationPools = connectedNetwork.network.getAllocationPools();
            if (srcAllocationPools != null && !srcAllocationPools.isEmpty()) {
                List<VirtualSwitchCreateData.Subnet.AllocationPool> allocationPools = new ArrayList<>();
                srcAllocationPools.forEach(srcAllocationPool -> {
                    allocationPools.add(new VirtualSwitchCreateData.Subnet.AllocationPool(srcAllocationPool.getStart(),
                            srcAllocationPool.getEnd()));
                });
                subnet.setAllocationPools(allocationPools);
            }

            var srcHostRoutes = connectedNetwork.network.getHostRoutes();
            if (srcHostRoutes != null && !srcHostRoutes.isEmpty()) {
                List<VirtualSwitchCreateData.Subnet.HostRoute> hostRoutes = new ArrayList<>();
                srcHostRoutes.forEach(srcHostRoute -> {
                    hostRoutes.add(new VirtualSwitchCreateData.Subnet.HostRoute(srcHostRoute.getDestination(),
                            srcHostRoute.getNexthop()));
                });
                subnet.setHostRoutes(hostRoutes);
            }
            subnet.setDnsNameservers(CollectionUtils.emptyNull(connectedNetwork.network.getDnsNameservers()));
            createData.setSubnet(subnet);
            var virtualSwitch = vsClient.create(createData);
            cloudElement.setVlanId(virtualSwitch.getSegmentationId());
            connectedNetwork.network.setVlanId(virtualSwitch.getSegmentationId());
            connectedNetwork.network.setResourceId(virtualSwitch.getId());
            if (cloudElement.getVirtualType() != null && cloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH) {
                cloudElement.setResourceId(virtualSwitch.getId());
            }
            
            connectedNetwork.getFromPorts().forEach(p -> {
                var pluginPort = getPluginHandler().getPort(p.getId());
                if (pluginPort != null) {
                    pluginPort.setAuxiliaryNetworkId(virtualSwitch.getId());
                }
            });
            connectedNetwork.getToPorts().forEach(p -> {
                var pluginPort = getPluginHandler().getPort(p.getId());
                if (pluginPort != null) {
                    pluginPort.setAuxiliaryNetworkId(virtualSwitch.getId());
                }
            });
            this.createHandler.createAuxiliaryNetworkRouter(cloudElement, connectedNetwork.network);
        }
    }

    private void prepareElementProcess(String nodeId, Integer virtualType) {
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.CREATE, Arrays.asList(ElementProcess.START_VIRTUAL_ROUTER));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.CREATE, Arrays.asList(ElementProcess.START_VIRTUAL_FIREWALL));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.CREATE, Arrays.asList(ElementProcess.START_VIRTUAL_MACHINE));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.CREATE, Arrays.asList(ElementProcess.START_CONTAINER));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.CREATE, Arrays.asList(ElementProcess.START_ENTITY_NODE));
        }
    }
}
