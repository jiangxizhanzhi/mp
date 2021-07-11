package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.StaticRoute;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.constant.QosRuleDirection;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.VyosNetworkCommandUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RecreateTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.util.ElementTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class ElementModifyHandler extends HandlerCommon {

    public static class ElementProcess {
        public static final EventProcess UPDATE_NODE = new EventProcess("update_node", "sceneplugincloud.event.update_node");
    }

    private CloudScenePluginHandler newCloudScenePluginHandler;
    private CloudScenePluginHandler originCloudScenePluginHandler;

    private VirtualMachine virtualMachine = new VirtualMachine();
    private Container container = new Container();
    private VirtualSwitch virtualSwitch = new VirtualSwitch();
    private VirtualRouter virtualRouter = new VirtualRouter();
    private VirtualFirewall virtualFirewall = new VirtualFirewall();
    private VirtualPort virtualPort = new VirtualPort();
    private VmRouter vmRouter = new VmRouter();

    private static final String TCP = "tcp";

    public ElementModifyHandler(CloudScenePluginHandler newHandler, CloudScenePluginHandler originHandler, int event) {
        super(newHandler, RecreateTaskPool.getInstance(), event);
        this.newCloudScenePluginHandler = newHandler;
        this.originCloudScenePluginHandler = originHandler;
    }

    public void modifyElement(String nodeId) {
        var originCloudElement = this.originCloudScenePluginHandler.getElement(nodeId);
        var newCloudElement = this.newCloudScenePluginHandler.getElement(nodeId);

        this.newCloudScenePluginHandler.getSceneHandler().prepareElementProcess(originCloudElement.getSrcElement().getId(), SceneElementModel.Event.MODIFY, Arrays.asList(ElementProcess.UPDATE_NODE));

        runElementProgress(originCloudElement.getSrcElement().getId(), ElementProcess.UPDATE_NODE, () -> {
            if (originCloudElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH
                    || originCloudElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY) {
                
                if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH) {
                    this.modifyVirtualSwitch(newCloudElement, originCloudElement);
                } else if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER) {
                    this.modifyVirtualSwitch(newCloudElement, originCloudElement);
                }
            } else if (originCloudElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER) {
                
                if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER) {
                    this.modifyVirtualRouter(newCloudElement, originCloudElement);
                } else if (ElementTypeUtils.isCoreRouter(originCloudElement)) {
                    this.modifyCoreRoute(newCloudElement, originCloudElement);
                }
            } else if (originCloudElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL) {
                
                this.modifyVirtualFirewall(newCloudElement, originCloudElement);
            } else if (originCloudElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL) {
                if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                    
                    this.modifyVirtualMachine(newCloudElement, originCloudElement);
                } else if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER) {
                    
                    this.modifyContainer(newCloudElement, originCloudElement);
                }
            }
        });
    }

    private void modifyCoreRoute(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        
        {
            var ports = newCloudElement.getSrcElement().getPorts();
            SceneElementPortBO defaultPort = null;
            List<SceneElementPortBO> orderedPorts = new ArrayList<>();
            for (var p : ports) {
                if (Boolean.TRUE.equals(p.getIsAuxiliary())) {
                    defaultPort = p;
                } else {
                    orderedPorts.add(p);
                }
            }
            if (defaultPort != null) {
                orderedPorts.add(0, defaultPort);
            }
            for (int i = 0; i < orderedPorts.size(); i++) {
                orderedPorts.get(i).setOrder(i + 1);
            }
            newCloudElement.getSrcElement().setPorts(orderedPorts);
        }

        {
            var ports = originCloudElement.getSrcElement().getPorts();
            SceneElementPortBO defaultPort = null;
            List<SceneElementPortBO> orderedPorts = new ArrayList<>();
            for (var p : ports) {
                if (Boolean.TRUE.equals(p.getIsAuxiliary())) {
                    defaultPort = p;
                } else {
                    orderedPorts.add(p);
                }
            }
            if (defaultPort != null) {
                orderedPorts.add(0, defaultPort);
            }
            for (int i = 0; i < orderedPorts.size(); i++) {
                orderedPorts.get(i).setOrder(i + 1);
            }
            originCloudElement.getSrcElement().setPorts(orderedPorts);
        }

        if (org.apache.commons.collections4.CollectionUtils.isEmpty(newCloudElement.getSrcElement().getPorts())) {
            return;
        }

        boolean staticRouteChange = this.isStaticRouteChange(newCloudElement.getStaticRoutes(), originCloudElement.getStaticRoutes());
        boolean protocolChange = VyosNetworkCommandUtils.isProtocolsChange(originCloudElement, originCloudScenePluginHandler, newCloudElement, newCloudScenePluginHandler);
        boolean qosChange = VyosNetworkCommandUtils.isQosChange(originCloudElement, originCloudScenePluginHandler, newCloudElement, newCloudScenePluginHandler);
        boolean isChange = staticRouteChange || protocolChange || qosChange;
        if (!isChange) {
            return;
        }
        
        var defaultPort = this.originCloudScenePluginHandler.getPort(originCloudElement.getSrcElement().getPorts().get(0).getId());
        for (var p : originCloudElement.getSrcElement().getPorts()) {
            if (Boolean.TRUE.equals(p.getIsAuxiliary())) {
                defaultPort = this.originCloudScenePluginHandler.getPort(p.getId());
            }
        }
        if (CollectionUtils.isNotEmpty(newCloudElement.getSrcElement().getPorts())) {
            for (int i = 0; i < newCloudElement.getSrcElement().getPorts().size(); i++) {
                newCloudElement.getSrcElement().getPorts().get(i).setOrder(i + 1);
            }
        }
        List<String> cmds = VyosNetworkCommandUtils.genModifyNetworkCmdList(newCloudElement, this.newCloudScenePluginHandler);
        var buildNetwork = new RouterConfigCmdData();
        buildNetwork.setSerialIp(defaultPort.getAccessModes().get(0).getAccessIp());
        buildNetwork.setSerialPort(defaultPort.getAccessModes().get(0).getAccessPort());
        buildNetwork.setCmds(cmds);

        var clearNetwork = new RouterConfigCmdData();
        clearNetwork.setSerialIp(defaultPort.getAccessModes().get(0).getAccessIp());
        clearNetwork.setSerialPort(defaultPort.getAccessModes().get(0).getAccessPort());
        clearNetwork.setCmds(Arrays.asList("delete protocols"));

        var clearTraffic = new RouterConfigCmdData();
        clearTraffic.setSerialIp(defaultPort.getAccessModes().get(0).getAccessIp());
        clearTraffic.setSerialPort(defaultPort.getAccessModes().get(0).getAccessPort());
        clearTraffic.setCmds(Arrays.asList("delete traffic-policy"));

        boolean buildNetworkResult = false;
        int tryTimes = 7;
        Exception exception = null;
        for (int i = 0; i < tryTimes; i++) {
            
            try {
                vmRouter.createConfig(clearNetwork);

                if (CollectionUtils.isNotEmpty(buildNetwork.getCmds())) {
                    buildNetworkResult = vmRouter.createConfig(buildNetwork);
                } else {
                    buildNetworkResult = true;
                }
            } catch (Exception e) {
                log.error("failed to build network , try time {}", i);
                exception = e;
                buildNetworkResult = false;
                if (i >= 5) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                }
            }
            if (buildNetworkResult) {
                break;
            }
        }
        if (buildNetworkResult == false) {
            log.error("build vyos network failed , buildNetwork {} , resource id is {} ", buildNetwork, originCloudElement.getResourceId());
            if (exception != null) {
                log.error("failed to build network ", exception);
            }
        }
    }

    private void modifyVirtualMachine(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        if (this.isFlavorChange(newCloudElement, originCloudElement)) {
            
            String flavorId = this.createFlavor(newCloudElement);
            String host = virtualMachine.resize(originCloudElement.getResourceId(), flavorId);
            newCloudElement.setHost(host);
            originCloudElement.setHost(host);
        }
        this.changeConnect(newCloudElement, originCloudElement);
        this.qosRuleChange(newCloudElement, originCloudElement);
    }

    private void modifyContainer(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        if (this.isFlavorChange(newCloudElement, originCloudElement)) {
            ContainerResizeData resizeData = new ContainerResizeData();
            resizeData.setCpu(newCloudElement.getSrcElement().getCpu());
            resizeData.setRam(newCloudElement.getSrcElement().getRam());
            resizeData.setDisk(newCloudElement.getSrcElement().getDisk());
            container.resize(originCloudElement.getResourceId(), resizeData);
        }
        this.changeConnect(newCloudElement, originCloudElement);
        this.qosRuleChange(newCloudElement, originCloudElement);
    }

    private void modifyVirtualSwitch(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        if (CollectionUtils.isEmpty(originCloudElement.getDeriveNetworks())) {
            return;
        }
        var currentElementNetwork = originCloudElement.getDeriveNetworks().stream().filter(n -> n.getNetwork().getCidr().equals(originCloudElement.getCidr())).findFirst().orElse(new ConnectedNetworkBO(null, null)).network;
        if (currentElementNetwork == null) {
            return;
        }

        
        boolean dhcpChange = !currentElementNetwork.getDhcp().equals(newCloudElement.getDhcp());
        boolean dhcpPoolsChange = this.isDhcpPoolsChange(currentElementNetwork, newCloudElement);
        
        boolean dnsChange = this.isDnsChange(currentElementNetwork, newCloudElement);
        
        if (dhcpChange || dhcpPoolsChange || dnsChange) {
            this.doVsChange(currentElementNetwork, newCloudElement, originCloudElement);
        }
    }

    private void modifyVirtualRouter(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        
        this.staticRouteChange(newCloudElement, originCloudElement);
        
        this.qosRuleChange(newCloudElement, originCloudElement);
    }

    private void modifyVirtualFirewall(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        
        this.staticRouteChange(newCloudElement, originCloudElement);
        
        this.qosRuleChange(newCloudElement, originCloudElement);
        
        this.firewallRuleChange(newCloudElement, originCloudElement);
    }

    private void staticRouteChange(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        if (this.isStaticRouteChange(newCloudElement.getStaticRoutes(), originCloudElement.getStaticRoutes())) {
            this.doStaticRouteChange(newCloudElement, originCloudElement);
        }
    }

    private boolean isStaticRouteChange(List<StaticRoute> newStaticRoutes, List<StaticRoute> originStaticRoutes) {
        
        if (newStaticRoutes.size() != originStaticRoutes.size()) {
            return true;
        }
        
        Set<String> newContent = newStaticRoutes.stream().map(h -> h.getDestination() + "_" + h.getNexthop()).collect(Collectors.toSet());
        Set<String> originContent = originStaticRoutes.stream().map(h -> h.getDestination() + "_" + h.getNexthop()).collect(Collectors.toSet());
        newContent.removeAll(originContent);
        if (newContent.size() != 0) {
            return true;
        }
        return false;
    }

    private void doStaticRouteChange(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        VirtualRouterUpdateData updateData = new VirtualRouterUpdateData();
        var staticRoutes = newCloudElement.getStaticRoutes().stream().map(h -> {
            var staticRoute = new VirtualRouterCreateData.StaticRoute();
            staticRoute.setDestination(h.getDestination());
            staticRoute.setNexthop(h.getNexthop());
            return staticRoute;
        }).collect(Collectors.toList());
        updateData.setRoutes(staticRoutes);
        virtualRouter.update(originCloudElement.getResourceId(), updateData);
        originCloudElement.setStaticRoutes(newCloudElement.getStaticRoutes());
    }

    private boolean isDhcpPoolsChange(CloudPluginSceneElementNetworkBO currentElementNetwork, CloudPluginSceneElementBO newCloudElement) {
        boolean dhcpPoolsChange = false;
        if (currentElementNetwork.getAllocationPools().size() != newCloudElement.getAllocationPools().size()) {
            dhcpPoolsChange = true;
        } else {
            Set<String> newDhcpPools = newCloudElement.getAllocationPools().stream().map(p -> p.getStart() + "_" + p.getEnd()).collect(Collectors.toSet());
            Set<String> originDhcpPools = currentElementNetwork.getAllocationPools().stream().map(p -> p.getStart() + "_" + p.getEnd()).collect(Collectors.toSet());
            newDhcpPools.removeAll(originDhcpPools);
            if (newDhcpPools.size() != 0) {
                dhcpPoolsChange = true;
            }
        }
        return dhcpPoolsChange;
    }

    private boolean isDnsChange(CloudPluginSceneElementNetworkBO currentElementNetwork, CloudPluginSceneElementBO newCloudElement) {
        
        boolean dnsChange = false;
        if (newCloudElement.getDnsNameservers().size() != currentElementNetwork.getDnsNameservers().size()) {
            dnsChange = true;
        } else {
            Set<String> newDns = new HashSet<>(newCloudElement.getDnsNameservers());
            Set<String> originDns = new HashSet<>(currentElementNetwork.getDnsNameservers());
            newDns.removeAll(originDns);
            if (newDns.size() != 0) {
                dnsChange = true;
            }
        }
        return dnsChange;
    }

    private void doVsChange(CloudPluginSceneElementNetworkBO currentElementNetwork, CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        VirtualSwitchUpdateData updateData = new VirtualSwitchUpdateData();
        VirtualSwitchCreateData.Subnet subnet = new VirtualSwitchCreateData.Subnet();
        subnet.setAllocationPools(newCloudElement.getAllocationPools().stream().map(a -> {
            var poll = new VirtualSwitchCreateData.Subnet.AllocationPool();
            poll.setStart(a.getStart());
            poll.setEnd(a.getEnd());
            return poll;
        }).collect(Collectors.toList()));
        subnet.setDnsNameservers(newCloudElement.getDnsNameservers());
        subnet.setEnableDhcp(newCloudElement.getDhcp());
        updateData.setSubnet(subnet);
        virtualSwitch.update(currentElementNetwork.getResourceId(), updateData);
        originCloudElement.setDhcp(newCloudElement.getDhcp());
        originCloudElement.setAllocationPools(newCloudElement.getAllocationPools());
        originCloudElement.setDnsNameservers(newCloudElement.getDnsNameservers());
        currentElementNetwork.setDhcp(newCloudElement.getDhcp());
        currentElementNetwork.setAllocationPools(newCloudElement.getAllocationPools());
        currentElementNetwork.setDnsNameservers(newCloudElement.getDnsNameservers());
        this.originCloudScenePluginHandler.updateElementNetworks(newCloudElement.getSrcElement().getId());
    }

    private boolean isFlavorChange(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        var newElement = newCloudElement.getSrcElement();
        var originElement = originCloudElement.getSrcElement();
        if (!NumberUtil.equals(newElement.getCpu(), originElement.getCpu()) || !NumberUtil.equals(newElement.getDisk(), originElement.getDisk()) || !NumberUtil.equals(newElement.getRam(), originElement.getRam())) {
            return true;
        }
        return false;
    }

    private String createFlavor(CloudPluginSceneElementBO newCloudElement) {
        CreateFlavorData createFlavorData = new CreateFlavorData();
        createFlavorData.setCpu(newCloudElement.getSrcElement().getCpu());
        createFlavorData.setRam(newCloudElement.getSrcElement().getRam());
        createFlavorData.setDisk(newCloudElement.getSrcElement().getDisk());
        var result = new Remote().cloudPost(CloudUrlConstant.CREATE_FLAVOR, createFlavorData, null, new TypeReference<CloudResponse<CreateFlavorResult>>() {
        }, true);
        return result.getData().getId();
    }

    private void qosRuleChange(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        Set<String> mayUpdatePortIds = new ConcurrentHashSet<>();
        Set<String> originPortIds = new ConcurrentHashSet<>();
        if (originCloudElement != null && originCloudElement.getSrcElement() != null && originCloudElement.getSrcElement().getPorts() != null) {
            originPortIds = originCloudElement.getSrcElement().getPorts().stream().map(p -> p.getId()).collect(Collectors.toSet());
        }
        if (newCloudElement != null && newCloudElement.getSrcElement() != null && newCloudElement.getSrcElement().getPorts() != null) {
            mayUpdatePortIds = newCloudElement.getSrcElement().getPorts().stream().map(p -> p.getId()).collect(Collectors.toSet());
        }
        mayUpdatePortIds.retainAll(originPortIds);
        if (CollectionUtils.isNotEmpty(mayUpdatePortIds)) {
            for (String portId : mayUpdatePortIds) {
                var originPort = this.originCloudScenePluginHandler.getPort(portId);
                var newPort = this.newCloudScenePluginHandler.getPort(portId);
                if (this.isQosRuleChange(newPort, originPort)) {
                    this.doQosChange(newPort, originPort);
                }
            }
        }
    }

    private boolean isQosRuleChange(CloudPluginSceneElementPortBO newCloudPort, CloudPluginSceneElementPortBO originCloudPort) {
        var newSrcPort = newCloudPort.getSrcPort();
        if (newSrcPort.getEgress() == null) {
            newSrcPort.setEgress(-1);
        }
        if (newSrcPort.getIngress() == null) {
            newSrcPort.setIngress(-1);
        }
        if (newSrcPort.getDelay() == null) {
            newSrcPort.setDelay(-1);
        }
        if (newSrcPort.getLoss() == null) {
            newSrcPort.setLoss(-1);
        }
        boolean eGressChange = false;
        {
            int newEgress = (newCloudPort.getSrcPort().getEgress() != null && newCloudPort.getSrcPort().getEgress() > 0) ? newCloudPort.getSrcPort().getEgress() : 0;
            int originEgress = (originCloudPort.getSrcPort().getEgress() != null && originCloudPort.getSrcPort().getEgress() > 0) ? originCloudPort.getSrcPort().getEgress() : 0;
            if (originEgress != newEgress) {
                eGressChange = true;
            }
        }

        boolean inGressChange = false;
        {
            int newInGress = (newCloudPort.getSrcPort().getIngress() != null && newCloudPort.getSrcPort().getIngress() > 0) ? newCloudPort.getSrcPort().getIngress() : 0;
            int originInGress = (originCloudPort.getSrcPort().getIngress() != null && originCloudPort.getSrcPort().getIngress() > 0) ? originCloudPort.getSrcPort().getIngress() : 0;
            if (newInGress != originInGress) {
                inGressChange = true;
            }
        }

        boolean delayChange = false;
        {
            int newDelay = (newCloudPort.getSrcPort().getDelay() != null && newCloudPort.getSrcPort().getDelay() > 0) ? newCloudPort.getSrcPort().getDelay() : 0;
            int originDelay = (originCloudPort.getSrcPort().getDelay() != null && originCloudPort.getSrcPort().getDelay() > 0) ? originCloudPort.getSrcPort().getDelay() : 0;
            if (newDelay != originDelay) {
                delayChange = true;
            }
        }

        boolean lossChange = false;
        {
            int newLoss = (newCloudPort.getSrcPort().getLoss() != null && newCloudPort.getSrcPort().getLoss() > 0) ? newCloudPort.getSrcPort().getLoss() : 0;
            int originLoss = (originCloudPort.getSrcPort().getLoss() != null && originCloudPort.getSrcPort().getLoss() > 0) ? originCloudPort.getSrcPort().getLoss() : 0;
            if (newLoss != originLoss) {
                lossChange = true;
            }
        }

        if (eGressChange || inGressChange || delayChange || lossChange) {
            return true;
        }
        return false;
    }

    private void doQosChange(CloudPluginSceneElementPortBO newCloudPort, CloudPluginSceneElementPortBO originCloudPort) {
        UpdateQosRuleData data = new UpdateQosRuleData();
        data.setQosId(originCloudPort.getQosId());
        data.setQosRules(new ArrayList<>());
        var newSrcPort = newCloudPort.getSrcPort();
        if (newSrcPort.getEgress() == null) {
            newSrcPort.setEgress(-1);
        }
        if (newSrcPort.getIngress() == null) {
            newSrcPort.setIngress(-1);
        }
        if (newSrcPort.getDelay() == null) {
            newSrcPort.setDelay(0);
        }
        if (newSrcPort.getLoss() == null) {
            newSrcPort.setLoss(0);
        }
        if (newSrcPort.getEgress() > 0 || newSrcPort.getDelay() > 0 || newSrcPort.getLoss() > 0) {
            VirtualNic.QosRule qosRule = new VirtualNic.QosRule();
            if (newSrcPort.getEgress() > 0) {
                qosRule.setMaxKbps(newSrcPort.getEgress());
            }
            if (newSrcPort.getDelay() > 0) {
                qosRule.setDelayMs(newSrcPort.getDelay());
            }
            if (newSrcPort.getLoss() > 0) {
                qosRule.setLossPercent(newSrcPort.getLoss());
            }
            qosRule.setDirection(QosRuleDirection.EGRESS);
            data.getQosRules().add(qosRule);
        }
        if (newSrcPort.getIngress() > 0 || newSrcPort.getDelay() > 0 || newSrcPort.getLoss() > 0) {
            VirtualNic.QosRule qosRule = new VirtualNic.QosRule();
            if (newSrcPort.getIngress() > 0) {
                qosRule.setMaxKbps(newSrcPort.getIngress());
            }
            if (newSrcPort.getDelay() > 0) {
                qosRule.setDelayMs(newSrcPort.getDelay());
            }
            if (newSrcPort.getLoss() > 0) {
                qosRule.setLossPercent(newSrcPort.getLoss());
            }
            qosRule.setDirection(QosRuleDirection.INGRESS);
            data.getQosRules().add(qosRule);
        }
        if (StringUtils.isNotEmpty(data.getQosId())) {
            virtualPort.updateQosRules(data);
        } else {
            try {
                var net = this.originCloudScenePluginHandler.getHandlerUtils().getNetworkByPort(originCloudPort.getSrcPort());
                if (net != null && StringUtils.isNotEmpty(net.getResourceId())) {
                    var createData = new CreateQosRuleData();
                    createData.setName("MP_SCENE_" + originCloudPort.getSrcPort().getEid());
                    createData.setQosRules(data.getQosRules());
                    createData.setPortId(originCloudPort.getResourceId());
                    createData.setNetworkId(net.getResourceId());
                    var result = virtualPort.createQosRules(createData);
                    newCloudPort.setQosId(result.getQosId());
                    originCloudPort.setQosId(result.getQosId());
                } else {
                    log.error("no net when add qos , portId {}", newSrcPort.getId());
                }
            } catch (Exception e) {
                log.error("failed to create qos , portId {} , exception is ", newSrcPort.getId(), ExceptionUtils.getStackTrace(e));
            }
        }
    }

    private void firewallRuleChange(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        Set<String> mayUpdatePortIds = newCloudElement.getSrcElement().getPorts().stream().map(p -> p.getId()).collect(Collectors.toSet());
        Set<String> originPortIds = originCloudElement.getSrcElement().getPorts().stream().map(p -> p.getId()).collect(Collectors.toSet());
        mayUpdatePortIds.retainAll(originPortIds);
        if (CollectionUtils.isNotEmpty(mayUpdatePortIds)) {
            for (String portId : mayUpdatePortIds) {
                var originPort = this.originCloudScenePluginHandler.getPort(portId);
                var newPort = this.newCloudScenePluginHandler.getPort(portId);
                if (this.isFirewallRuleChange(newPort, originPort)) {
                    this.doFirewallRuleChange(newPort, originPort, originCloudElement);
                }
            }
        }
    }

    private boolean isFirewallRuleChange(CloudPluginSceneElementPortBO newCloudPort, CloudPluginSceneElementPortBO originCloudPort) {
        Set<String> newFirewallRules = newCloudPort.getFirewallRules().stream().map(f -> StringUtils.joinWith(":", f.getAction(), f.getProtocol(), f.getDirection(), f.getIpVersion(), f.getSourcePort(), f.getSourceIpAddress(), f.getDestinationPort(), f.getDestinationIpAddress())).collect(Collectors.toSet());
        Set<String> originFirewallRules = originCloudPort.getFirewallRules().stream().map(f -> StringUtils.joinWith(":", f.getAction(), f.getProtocol(), f.getDirection(), f.getIpVersion(), f.getSourcePort(), f.getSourceIpAddress(), f.getDestinationPort(), f.getDestinationIpAddress())).collect(Collectors.toSet());
        
        if (newFirewallRules.size() != originFirewallRules.size()) {
            return true;
        }
        
        newFirewallRules.removeAll(originFirewallRules);
        return !newFirewallRules.isEmpty();
    }

    private void doFirewallRuleChange(CloudPluginSceneElementPortBO newCloudPort, CloudPluginSceneElementPortBO originCloudPort, CloudPluginSceneElementBO originCloudElement) {
        var updateData = new VirtualFirewallUpdateData();
        updateData.setName(HandlerUtils.genResourceName(originCloudElement.getSrcElement().getEid()));
        if (StringUtils.isNotEmpty(originCloudPort.getFirewallResourceId())) {
            updateData.setId(originCloudPort.getFirewallResourceId());
        } else {
            Set<String> targetPorts = new HashSet<>();
            if (StringUtils.isNotEmpty(newCloudPort.getResourceId())) {
                targetPorts.add(newCloudPort.getResourceId());
            }
            updateData.setPorts(cn.cyberpeace.mp.base.util.basic.CollectionUtils.emptyNull(new ArrayList<>(targetPorts)));
        }
        var portSrcRules = newCloudPort.getFirewallRules();
        if (portSrcRules != null && !portSrcRules.isEmpty()) {
            var cloudFirewallRules = CreateHandler.CloudFirewallRules.fromSrcRules(portSrcRules);
            updateData.setIngressRules(cloudFirewallRules.getIngressRules());
            updateData.setEgressRules(cloudFirewallRules.getEgressRules());
        }
        if (StringUtils.isNotEmpty(originCloudPort.getFirewallResourceId())) {
            virtualFirewall.update(updateData);
        } else {
            var virtualFirewallCreateData = virtualFirewall.create(updateData);
            newCloudPort.setFirewallResourceId(virtualFirewallCreateData.getId());
        }
    }

    private void changeConnect(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        Set<String> newPortIds;
        Set<String> originPortIds;
        if (newCloudElement != null && newCloudElement.getSrcElement() != null && newCloudElement.getSrcElement().getPorts() != null) {
            newPortIds = newCloudElement.getSrcElement().getPorts().stream().map(p -> p.getId()).collect(Collectors.toSet());
        } else {
            newPortIds = new ConcurrentHashSet<>();
        }
        if (originCloudElement != null && originCloudElement.getSrcElement() != null && originCloudElement.getSrcElement().getPorts() != null) {
            originPortIds = originCloudElement.getSrcElement().getPorts().stream().map(p -> p.getId()).collect(Collectors.toSet());
        } else {
            originPortIds = new ConcurrentHashSet<>();
        }
        
        Set<String> addPortIds = new HashSet<>(newPortIds);
        addPortIds.removeAll(originPortIds);

        
        Set<String> delPortIds = new HashSet<>(originPortIds);
        delPortIds.removeAll(newPortIds);

        
        Set<String> mayUpdatePortIds = new HashSet<>(newPortIds);
        mayUpdatePortIds.retainAll(originPortIds);

        if (newCloudElement.getSrcElement() != null && CollectionUtils.isNotEmpty(newCloudElement.getSrcElement().getPorts())) {
            for (var p : newCloudElement.getSrcElement().getPorts()) {
                if (Boolean.TRUE.equals(p.isChange())) {
                    mayUpdatePortIds.add(p.getId());
                }
            }
        }

        log.debug("newCloudElement is {}, originCloudElement is {}, addPortIds is {}, delPortIds is {}, mayUpdatePortIds is {}",
                JsonCommon.stringify(newCloudElement), JsonCommon.stringify(originCloudElement),
                JsonCommon.stringify(addPortIds), JsonCommon.stringify(delPortIds), JsonCommon.stringify(mayUpdatePortIds));
        for (String addPortId : addPortIds) {
            var addCloudPort = this.newCloudScenePluginHandler.getPort(addPortId);
            var addPort = this.newCloudScenePluginHandler.getPort(addPortId).getSrcPort();
            var net = this.newCloudScenePluginHandler.getHandlerUtils().getNetworkByPort(addPort);
            if (net != null) {
                
                if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                    var result = this.vmAddPort(originCloudElement.getResourceId(), net.getResourceId(), addPort.getIp(), null);
                    addPort.setMacAddr(result.getMacAddress());
                    addCloudPort.setResourceId(result.getPortId());
                } else {
                    var result = this.containerAddPort(originCloudElement.getResourceId(), net.getResourceId(), addPort.getIp());
                    addPort.setMacAddr(result.getMacAddress());
                    addCloudPort.setResourceId(result.getPortId());
                }
                addCloudPort.setAuxiliaryNetworkId(net.getResourceId());
                
                if (CollectionUtils.isNotEmpty(addCloudPort.getAccessModes())) {
                    for (var a : addCloudPort.getAccessModes()) {
                        if (StringUtils.isNotEmpty(a.getId())) {
                            a.setId(TextUtils.uuid());
                        }
                        if (AccessMode.Protocol.CONSOLE.equals(a.getProtocol())) {
                            a.setAccessIp(addPort.getIp());
                            continue;
                        }
                        PortMappingCreateData portMappingCreateData = new PortMappingCreateData();
                        portMappingCreateData.setProto(TCP);
                        portMappingCreateData.setHost(originCloudElement.getHost());
                        portMappingCreateData.setInstanceIp(addPort.getIp());
                        portMappingCreateData.setInstancePort(a.getPort());
                        portMappingCreateData.setNetworkId(addCloudPort.getAuxiliaryNetworkId());
                        portMappingCreateData.setFwdnsInterfaceIp(this.newCloudScenePluginHandler.getHandlerUtils().getFwdnsInterfaceIp(
                                newCloudElement.getSrcElement().getId(), net.getId()
                        ));
                        var portMapping = virtualSwitch.createPortMapping(portMappingCreateData);
                        a.setAvailable(true);
                        a.setAccessIp(portMapping.getHost());
                        a.setAccessPort(portMapping.getHostPort());
                        a.setFwdId(portMapping.getId());
                    }
                    checkAccessModes(newCloudElement);
                }
            }
        }

        for (String delPortId : delPortIds) {
            
            if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                var deleteCloudPort = this.originCloudScenePluginHandler.getPort(delPortId);
                if (deleteCloudPort != null && StringUtils.isNotEmpty(deleteCloudPort.getResourceId())) {
                    virtualMachine.deletePort(originCloudElement.getResourceId(), deleteCloudPort.getResourceId());
                }
            } else {
                var deleteCloudPort = this.originCloudScenePluginHandler.getPort(delPortId);
                if (deleteCloudPort != null && StringUtils.isNotEmpty(deleteCloudPort.getResourceId())) {
                    var deletePort = deleteCloudPort.getSrcPort();
                    if (deletePort != null) {
                        var originNet = this.getPluginHandler().getHandlerUtils().getNetworkByPort(deletePort);
                        if (originNet != null) {
                            this.containerDeletePort(originCloudElement.getResourceId(), originNet.getResourceId(), deleteCloudPort.getResourceId(), deleteCloudPort);
                        } else {
                            var deletePortConnectedPort = originCloudScenePluginHandler.getSceneHandler().getPortConnectedPort(delPortId);
                            if (deletePortConnectedPort != null) {
                                var originNet1 = this.getPluginHandler().getHandlerUtils().getNetworkByPort(deletePortConnectedPort);
                                if (originNet1 != null) {
                                    this.containerDeletePort(originCloudElement.getResourceId(), originNet1.getResourceId(), deleteCloudPort.getResourceId(), deleteCloudPort);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (String mayUpdatePortId : mayUpdatePortIds) {
            var newCloudPort = this.newCloudScenePluginHandler.getPort(mayUpdatePortId);
            var newPort = newCloudPort.getSrcPort();
            var originCloudPort = this.originCloudScenePluginHandler.getPort(mayUpdatePortId);
            var originPort = originCloudPort.getSrcPort();

            
            String newIp = newPort.getIp();
            String originIp = originPort.getIp();
            boolean ipChange = !StringUtils.equals(newIp, originIp);
            var newNet = this.newCloudScenePluginHandler.getHandlerUtils().getNetworkByPort(newPort);
            var originNet = this.originCloudScenePluginHandler.getHandlerUtils().getNetworkByPort(originPort);
            if ((newNet == null && originNet == null) || (StringUtils.equals(newIp, originIp))) {
                
            } else if (newNet == null) {
                
                
                if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                    virtualMachine.deletePort(originCloudElement.getResourceId(), originCloudPort.getResourceId());
                } else {
                    this.containerDeletePort(originCloudElement.getResourceId(), originNet.getResourceId(), originCloudPort.getResourceId(), originCloudPort);
                }
            } else if (originNet == null) {
                
                if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                    
                    VirtualPortCreateData portCreateData = new VirtualPortCreateData();
                    portCreateData.setFixedIp(newIp);
                    portCreateData.setMacAddress(StringUtils.isEmpty(originPort.getMacAddr()) ? null : originPort.getMacAddr());
                    portCreateData.setNetworkId(newNet.getResourceId());
                    var newPortResult = virtualPort.create(portCreateData);
                    var result = this.vmAddPort(originCloudElement.getResourceId(), newNet.getResourceId(), newIp, newPortResult.getId());
                    
                    newPort.setIp(result.getFixedIp());
                    newPort.setMacAddr(result.getMacAddress());
                    if (CollectionUtils.isNotEmpty(newCloudPort.getAccessModes())) {
                        for (var a : newCloudPort.getAccessModes()) {
                            a.setAccessIp(newIp);
                        }
                    }
                    newCloudPort.setResourceId(result.getPortId());
                } else {
                    var result = this.containerAddPort(originCloudElement.getResourceId(), newNet.getResourceId(), newPort.getIp());

                    newPort.setIp(newPort.getIp());
                    newPort.setMacAddr(result.getMacAddress());
                    newCloudPort.setResourceId(result.getPortId());
                }
                newCloudPort.setAuxiliaryNetworkId(newNet.getResourceId());

                
                if (CollectionUtils.isNotEmpty(newCloudPort.getAccessModes())) {
                    for (var a : newCloudPort.getAccessModes()) {
                        if (StringUtils.isNotEmpty(a.getId())) {
                            a.setId(TextUtils.uuid());
                        }
                        if (AccessMode.Protocol.CONSOLE.equals(a.getProtocol())) {
                            a.setAccessIp(newPort.getIp());
                            continue;
                        }
                        PortMappingCreateData portMappingCreateData = new PortMappingCreateData();
                        portMappingCreateData.setProto(TCP);
                        portMappingCreateData.setHost(originCloudElement.getHost());
                        portMappingCreateData.setInstanceIp(newPort.getIp());
                        portMappingCreateData.setInstancePort(a.getPort());
                        portMappingCreateData.setNetworkId(newCloudPort.getAuxiliaryNetworkId());
                        portMappingCreateData.setFwdnsInterfaceIp(this.newCloudScenePluginHandler.getHandlerUtils().getFwdnsInterfaceIp(
                                newCloudElement.getSrcElement().getId(), newNet.getId()
                        ));
                        var portMapping = virtualSwitch.createPortMapping(portMappingCreateData);
                        a.setAvailable(true);
                        a.setAccessIp(portMapping.getHost());
                        a.setAccessPort(portMapping.getHostPort());
                    }
                    checkAccessModes(newCloudElement);
                }

            } else if (!StringUtils.equals(newNet.getResourceId(), originNet.getResourceId())) {
                
                if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                    
                    virtualMachine.deletePort(originCloudElement.getResourceId(), originCloudPort.getResourceId());
                    
                    VirtualPortCreateData portCreateData = new VirtualPortCreateData();
                    portCreateData.setFixedIp(newIp);
                    portCreateData.setMacAddress(StringUtils.isEmpty(originPort.getMacAddr()) ? null : originPort.getMacAddr());
                    portCreateData.setNetworkId(newNet.getResourceId());
                    var newPortResult = virtualPort.create(portCreateData);
                    var result = this.vmAddPort(originCloudElement.getResourceId(), newNet.getResourceId(), newIp, newPortResult.getId());
                    
                    newPort.setIp(result.getFixedIp());
                    newPort.setMacAddr(result.getMacAddress());
                    newCloudPort.setResourceId(result.getPortId());
                    newCloudPort.setAuxiliaryNetworkId(newNet.getResourceId());
                    newCloudPort.setLogicNetworkId(newNet.getId());
                } else {
                    
                    this.containerDeletePort(originCloudElement.getResourceId(), originNet.getResourceId(), originCloudPort.getResourceId(), originCloudPort);
                    
                    var result = this.containerAddPort(originCloudElement.getResourceId(), newNet.getResourceId(), newPort.getIp());

                    newPort.setIp(newPort.getIp());
                    newPort.setMacAddr(result.getMacAddress());
                    newCloudPort.setResourceId(result.getPortId());
                    newCloudPort.setAuxiliaryNetworkId(newNet.getResourceId());
                    newCloudPort.setLogicNetworkId(newNet.getId());
                }

                
                if (CollectionUtils.isNotEmpty(newCloudPort.getAccessModes())) {
                    for (var a : newCloudPort.getAccessModes()) {
                        if (StringUtils.isNotEmpty(a.getId())) {
                            a.setId(TextUtils.uuid());
                        }
                        if (AccessMode.Protocol.CONSOLE.equals(a.getProtocol())) {
                            a.setAccessIp(newPort.getIp());
                            continue;
                        }
                        PortMappingCreateData portMappingCreateData = new PortMappingCreateData();
                        portMappingCreateData.setProto(TCP);
                        portMappingCreateData.setHost(originCloudElement.getHost());
                        portMappingCreateData.setInstanceIp(newPort.getIp());
                        portMappingCreateData.setInstancePort(a.getPort());
                        portMappingCreateData.setNetworkId(newCloudPort.getAuxiliaryNetworkId());
                        portMappingCreateData.setFwdnsInterfaceIp(this.newCloudScenePluginHandler.getHandlerUtils().getFwdnsInterfaceIp(
                                newCloudElement.getSrcElement().getId(), newNet.getId()
                        ));
                        var portMapping = virtualSwitch.createPortMapping(portMappingCreateData);
                        a.setAvailable(true);
                        a.setAccessIp(portMapping.getHost());
                        a.setAccessPort(portMapping.getHostPort());

                    }
                    checkAccessModes(newCloudElement);
                }

            } else if (ipChange) {
                ModifyInstanceIpData data = new ModifyInstanceIpData();
                data.setPortId(originCloudPort.getResourceId());
                data.setInstanceId(originCloudElement.getResourceId());
                data.setFixedIp(newIp);
                if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                    data.setInstanceType(BaseCloudPluginSceneElementModel.ImageType.VM);
                } else {
                    data.setInstanceType(BaseCloudPluginSceneElementModel.ImageType.CONTAINER);
                }
                virtualPort.modifyInstanceIp(data);
                if (CollectionUtils.isNotEmpty(newCloudPort.getAccessModes())) {
                    for (var a : newCloudPort.getAccessModes()) {
                        if (StringUtils.equals(a.getAccessIp(), originIp)) {
                            a.setAccessIp(newIp);
                        }
                    }
                }

                
                if (CollectionUtils.isNotEmpty(newCloudPort.getAccessModes())) {
                    for (var a : newCloudPort.getAccessModes()) {
                        if (StringUtils.isEmpty(a.getId())) {
                            a.setId(TextUtils.uuid());
                        }
                        if (AccessMode.Protocol.CONSOLE.equals(a.getProtocol())) {
                            a.setAccessIp(newPort.getIp());
                            continue;
                        }
                        PortMappingCreateData portMappingCreateData = new PortMappingCreateData();
                        portMappingCreateData.setHost(originCloudElement.getHost());
                        portMappingCreateData.setInstanceIp(newPort.getIp());
                        portMappingCreateData.setInstancePort(a.getPort());
                        portMappingCreateData.setNetworkId(originCloudPort.getAuxiliaryNetworkId());
                        portMappingCreateData.setFwdnsInterfaceIp(this.newCloudScenePluginHandler.getHandlerUtils().getFwdnsInterfaceIp(
                                newCloudElement.getSrcElement().getId(), newNet.getId()
                        ));
                        var portMapping = virtualSwitch.createPortMapping(portMappingCreateData);
                        a.setAvailable(true);
                        a.setAccessIp(portMapping.getHost());
                        a.setAccessPort(portMapping.getHostPort());
                    }
                    checkAccessModes(newCloudElement);
                }

            }
        }
    }

    private VirtualMachineAddPortResult vmAddPort(String elementResourceId, String networkResourceId, String fixedIp, String portResourceId) {
        VirtualMachineAddPortData port = new VirtualMachineAddPortData();
        port.setFixedIp(fixedIp);
        port.setPortId(portResourceId);
        port.setNetworkId(networkResourceId);
        return virtualMachine.addPort(elementResourceId, port);
    }

    private ContainerAddPortData containerAddPort(String elementResourceId, String networkResourceId, String fixedIp) {
        ContainerAddPortData addPortData = new ContainerAddPortData();
        addPortData.setContainerId(elementResourceId);
        addPortData.setNetworkId(networkResourceId);
        addPortData.setFixedIp(fixedIp);
        return container.addPort(addPortData);
    }

    private boolean containerDeletePort(String elementResourceId, String networkResourceId, String portResourceId, CloudPluginSceneElementPortBO cloudPluginSceneElementPortBO) {
        ContainerDeletePortData deletePortData = new ContainerDeletePortData();
        deletePortData.setContainerId(elementResourceId);
        deletePortData.setNetworkId(networkResourceId);
        deletePortData.setPortId(portResourceId);
        return container.deletePort(deletePortData, cloudPluginSceneElementPortBO);
    }
}
