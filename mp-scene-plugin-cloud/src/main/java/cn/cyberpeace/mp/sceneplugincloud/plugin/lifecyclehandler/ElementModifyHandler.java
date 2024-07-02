package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.mptool.ConcurrentHashSet;
import cn.cyberpeace.mp.base.util.mptool.NumberUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.AllocationPool;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.FirewallRule;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.StaticRoute;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.constant.QosRuleDirection;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.VyosNetworkCommandUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RecreateTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.util.ElementTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ElementModifyHandler
 * @Description
 * @Author maojun
 * @Date 2021/10/25
 * @Version 1.0.0
 * @see HandlerCommon
 */
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

    /**
     * ElementModifyHandler
     *
     * @param newHandler
     * @param originHandler
     * @param event
     */
    public ElementModifyHandler(CloudScenePluginHandler newHandler, CloudScenePluginHandler originHandler, int event) {
        super(newHandler, RecreateTaskPool.getInstance(), event);
        this.newCloudScenePluginHandler = newHandler;
        this.originCloudScenePluginHandler = originHandler;
    }

    /**
     * modifyElement
     *
     * @param nodeId
     */
    public void modifyElement(String nodeId) {
        CloudPluginSceneElementBO originCloudElement = this.originCloudScenePluginHandler.getElement(nodeId);
        CloudPluginSceneElementBO newCloudElement = this.newCloudScenePluginHandler.getElement(nodeId);

        this.newCloudScenePluginHandler.getSceneHandler().prepareElementProcess(originCloudElement.getSrcElement().getId(), SceneElementModel.Event.MODIFY, Arrays.asList(ElementProcess.UPDATE_NODE));

        runElementProgress(originCloudElement.getSrcElement().getId(), ElementProcess.UPDATE_NODE, () -> {
            if (originCloudElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH
                    || originCloudElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY) {
                // 更新网络
                if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH) {
                    this.modifyVirtualSwitch(newCloudElement, originCloudElement);
                } else if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER) {
                    this.modifyVirtualSwitch(newCloudElement, originCloudElement);
                }
            } else if (originCloudElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER) {
                // 更新虚拟路由
                if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER) {
                    this.modifyVirtualRouter(newCloudElement, originCloudElement);
                } else if (ElementTypeUtils.isCoreRouter(originCloudElement)) {
                    this.modifyCoreRoute(newCloudElement, originCloudElement);
                }
            } else if (originCloudElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL) {
                // 更新虚拟防护墙
                this.modifyVirtualFirewall(newCloudElement, originCloudElement);
            } else if (originCloudElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL) {
                if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                    // 更新虚拟机终端
                    this.modifyVirtualMachine(newCloudElement, originCloudElement);
                } else if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER) {
                    // 更新容器终端
                    this.modifyContainer(newCloudElement, originCloudElement);
                }
            }
        });
    }

    /**
     * modifyCoreRoute
     *
     * @param newCloudElement
     * @param originCloudElement
     */
    private void modifyCoreRoute(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        // 核心路由的端口要注意排序 辅助网络的端口排第一个
        {
            List<SceneElementPortBO> ports = newCloudElement.getSrcElement().getPorts();
            SceneElementPortBO defaultPort = null;
            List<SceneElementPortBO> orderedPorts = new ArrayList<>();
            for (SceneElementPortBO p : ports) {
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
            List<SceneElementPortBO> ports = originCloudElement.getSrcElement().getPorts();
            SceneElementPortBO defaultPort = null;
            List<SceneElementPortBO> orderedPorts = new ArrayList<>();
            for (SceneElementPortBO p : ports) {
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
        // 拼接组网命令
        CloudPluginSceneElementPortBO defaultPort = this.originCloudScenePluginHandler.getPort(originCloudElement.getSrcElement().getPorts().get(0).getId());
        for (SceneElementPortBO p : originCloudElement.getSrcElement().getPorts()) {
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
        RouterConfigCmdData buildNetwork = new RouterConfigCmdData();
        buildNetwork.setSerialIp(defaultPort.getAccessModes().get(0).getAccessIp());
        buildNetwork.setSerialPort(defaultPort.getAccessModes().get(0).getAccessPort());
        buildNetwork.setCmds(cmds);

        RouterConfigCmdData clearNetwork = new RouterConfigCmdData();
        clearNetwork.setSerialIp(defaultPort.getAccessModes().get(0).getAccessIp());
        clearNetwork.setSerialPort(defaultPort.getAccessModes().get(0).getAccessPort());
        clearNetwork.setCmds(Arrays.asList("delete protocols"));

        RouterConfigCmdData clearTraffic = new RouterConfigCmdData();
        clearTraffic.setSerialIp(defaultPort.getAccessModes().get(0).getAccessIp());
        clearTraffic.setSerialPort(defaultPort.getAccessModes().get(0).getAccessPort());
        clearTraffic.setCmds(Arrays.asList("delete traffic-policy"));

        boolean buildNetworkResult = false;
        int tryTimes = 7;
        Exception exception = null;
        for (int i = 0; i < tryTimes; i++) {
            // 发送拼接网络的命令
            try {
                vmRouter.createConfig(clearNetwork);
//                vmRouter.createConfig(clearTraffic);
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

    /**
     * modifyVirtualMachine
     *
     * @param newCloudElement
     * @param originCloudElement
     */
    private void modifyVirtualMachine(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        if (this.isFlavorChange(newCloudElement, originCloudElement)) {
            // 先创建flavor
            String flavorId = this.createFlavor(newCloudElement);
            String host = virtualMachine.resize(originCloudElement.getResourceId(), flavorId);
            newCloudElement.setHost(host);
            originCloudElement.setHost(host);
        }
        this.changeConnect(newCloudElement, originCloudElement);
        this.qosRuleChange(newCloudElement, originCloudElement);
    }

    /**
     * modifyContainer
     *
     * @param newCloudElement
     * @param originCloudElement
     */
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

    /**
     * modifyVirtualSwitch
     *
     * @param newCloudElement
     * @param originCloudElement
     */
    private void modifyVirtualSwitch(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        if (CollectionUtils.isEmpty(originCloudElement.getDeriveNetworks())) {
            return;
        }
        ConnectedNetworkBO found = new ConnectedNetworkBO(null, null);
        for (ConnectedNetworkBO n : originCloudElement.getDeriveNetworks()) {
            if (n.getNetwork().getCidr().equals(originCloudElement.getCidr())) {
                found = n;
                break;
            }
        }
        CloudPluginSceneElementNetworkBO currentElementNetwork = found.network;
        if (currentElementNetwork == null) {
            return;
        }

        // dhcp 属性是否变化
        boolean dhcpChange = !currentElementNetwork.getDhcp().equals(newCloudElement.getDhcp());
        boolean dhcpPoolsChange = this.isDhcpPoolsChange(currentElementNetwork, newCloudElement);
        // dns 属性是否变化
        boolean dnsChange = this.isDnsChange(currentElementNetwork, newCloudElement);
        // 属性有变化则更新
        if (dhcpChange || dhcpPoolsChange || dnsChange) {
            this.doVsChange(currentElementNetwork, newCloudElement, originCloudElement);
        }
    }

    /**
     * modifyVirtualRouter
     *
     * @param newCloudElement
     * @param originCloudElement
     */
    private void modifyVirtualRouter(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        // 静态路由变更
        this.staticRouteChange(newCloudElement, originCloudElement);
        // 端口qos变更
        this.qosRuleChange(newCloudElement, originCloudElement);
    }

    /**
     * modifyVirtualFirewall
     *
     * @param newCloudElement
     * @param originCloudElement
     */
    private void modifyVirtualFirewall(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        // 静态路由变更
        this.staticRouteChange(newCloudElement, originCloudElement);
        // 端口qos变更
        this.qosRuleChange(newCloudElement, originCloudElement);
        // 防火墙规则变更
        this.firewallRuleChange(newCloudElement, originCloudElement);
    }

    /**
     * staticRouteChange
     *
     * @param newCloudElement
     * @param originCloudElement
     */
    private void staticRouteChange(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        if (this.isStaticRouteChange(newCloudElement.getStaticRoutes(), originCloudElement.getStaticRoutes())) {
            this.doStaticRouteChange(newCloudElement, originCloudElement);
        }
    }

    /**
     * isStaticRouteChange
     *
     * @param newStaticRoutes
     * @param originStaticRoutes
     * @return boolean
     */
    private boolean isStaticRouteChange(List<StaticRoute> newStaticRoutes, List<StaticRoute> originStaticRoutes) {
        // 数量不同
        if (newStaticRoutes.size() != originStaticRoutes.size()) {
            return true;
        }
        // 内容不同
        Set<String> newContent = new HashSet<>();
        for (StaticRoute newStaticRoute : newStaticRoutes) {
            String s = newStaticRoute.getDestination() + "_" + newStaticRoute.getNexthop();
            newContent.add(s);
        }
        Set<String> originContent = new HashSet<>();
        for (StaticRoute h : originStaticRoutes) {
            String s = h.getDestination() + "_" + h.getNexthop();
            originContent.add(s);
        }
        newContent.removeAll(originContent);
        if (newContent.size() != 0) {
            return true;
        }
        return false;
    }

    /**
     * doStaticRouteChange
     *
     * @param newCloudElement
     * @param originCloudElement
     */
    private void doStaticRouteChange(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        VirtualRouterModifyData updateData = new VirtualRouterModifyData();
        List<VirtualRouterModifyData.StaticRoute> staticRoutes = newCloudElement.getStaticRoutes().stream().map(h -> {
            VirtualRouterModifyData.StaticRoute staticRoute = new VirtualRouterModifyData.StaticRoute();
            staticRoute.setDestination(h.getDestination());
            staticRoute.setNexthop(h.getNexthop());
            return staticRoute;
        }).collect(Collectors.toList());
        updateData.setRoutes(staticRoutes);
        virtualRouter.modify(updateData, originCloudElement.getResourceId());
        originCloudElement.setStaticRoutes(newCloudElement.getStaticRoutes());
    }

    /**
     * isDhcpPoolsChange
     *
     * @param currentElementNetwork
     * @param newCloudElement
     * @return boolean
     */
    private boolean isDhcpPoolsChange(CloudPluginSceneElementNetworkBO currentElementNetwork, CloudPluginSceneElementBO newCloudElement) {
        boolean dhcpPoolsChange = false;
        if (currentElementNetwork.getAllocationPools().size() != newCloudElement.getAllocationPools().size()) {
            dhcpPoolsChange = true;
        } else {
            Set<String> newDhcpPools = new HashSet<>();
            for (AllocationPool allocationPool : newCloudElement.getAllocationPools()) {
                String s = allocationPool.getStart() + "_" + allocationPool.getEnd();
                newDhcpPools.add(s);
            }
            Set<String> originDhcpPools = new HashSet<>();
            for (AllocationPool p : currentElementNetwork.getAllocationPools()) {
                String s = p.getStart() + "_" + p.getEnd();
                originDhcpPools.add(s);
            }
            newDhcpPools.removeAll(originDhcpPools);
            if (newDhcpPools.size() != 0) {
                dhcpPoolsChange = true;
            }
        }
        return dhcpPoolsChange;
    }

    /**
     * isDnsChange
     *
     * @param currentElementNetwork
     * @param newCloudElement
     * @return boolean
     */
    private boolean isDnsChange(CloudPluginSceneElementNetworkBO currentElementNetwork, CloudPluginSceneElementBO newCloudElement) {
        // dns属性有变化
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

    /**
     * doVsChange
     *
     * @param currentElementNetwork
     * @param newCloudElement
     * @param originCloudElement
     */
    private void doVsChange(CloudPluginSceneElementNetworkBO currentElementNetwork, CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        VirtualSwitchUpdateData updateData = new VirtualSwitchUpdateData();
        VirtualSwitchCreateData.Subnet subnet = new VirtualSwitchCreateData.Subnet();
        subnet.setAllocationPools(newCloudElement.getAllocationPools().stream().map(a -> {
            VirtualSwitchCreateData.Subnet.AllocationPool poll = new VirtualSwitchCreateData.Subnet.AllocationPool();
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

    /**
     * isFlavorChange
     *
     * @param newCloudElement
     * @param originCloudElement
     * @return boolean
     */
    private boolean isFlavorChange(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        SceneElementBO newElement = newCloudElement.getSrcElement();
        SceneElementBO originElement = originCloudElement.getSrcElement();
        if (!NumberUtil.equals(newElement.getCpu(), originElement.getCpu()) || !NumberUtil.equals(newElement.getDisk(), originElement.getDisk()) || !NumberUtil.equals(newElement.getRam(), originElement.getRam())) {
            return true;
        }
        return false;
    }

    /**
     * createFlavor
     *
     * @param newCloudElement
     * @return
     */
    private String createFlavor(CloudPluginSceneElementBO newCloudElement) {
        CreateFlavorData createFlavorData = new CreateFlavorData();
        createFlavorData.setCpu(newCloudElement.getSrcElement().getCpu());
        createFlavorData.setRam(newCloudElement.getSrcElement().getRam());
        createFlavorData.setDisk(newCloudElement.getSrcElement().getDisk());
        CloudResponse<CreateFlavorResult> result = new Remote().cloudPost(CloudUrlConstant.CREATE_FLAVOR, createFlavorData, null, new TypeReference<CloudResponse<CreateFlavorResult>>() {
        }, true);
        return result.getData().getId();
    }

    /**
     * qosRuleChange
     *
     * @param newCloudElement
     * @param originCloudElement
     */
    private void qosRuleChange(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        Set<String> mayUpdatePortIds = new ConcurrentHashSet<>();
        Set<String> originPortIds = new ConcurrentHashSet<>();
        if (originCloudElement != null && originCloudElement.getSrcElement() != null && originCloudElement.getSrcElement().getPorts() != null) {
            Set<String> set = new HashSet<>();
            for (SceneElementPortBO p : originCloudElement.getSrcElement().getPorts()) {
                String id = p.getId();
                set.add(id);
            }
            originPortIds = set;
        }
        if (newCloudElement != null && newCloudElement.getSrcElement() != null && newCloudElement.getSrcElement().getPorts() != null) {
            Set<String> set = new HashSet<>();
            for (SceneElementPortBO p : newCloudElement.getSrcElement().getPorts()) {
                String id = p.getId();
                set.add(id);
            }
            mayUpdatePortIds = set;
        }
        mayUpdatePortIds.retainAll(originPortIds);
        if (CollectionUtils.isNotEmpty(mayUpdatePortIds)) {
            for (String portId : mayUpdatePortIds) {
                CloudPluginSceneElementPortBO originPort = this.originCloudScenePluginHandler.getPort(portId);
                CloudPluginSceneElementPortBO newPort = this.newCloudScenePluginHandler.getPort(portId);
                if (this.isQosRuleChange(newPort, originPort)) {
                    this.doQosChange(newPort, originPort);
                }
            }
        }
    }

    /**
     * isQosRuleChange
     *
     * @param newCloudPort
     * @param originCloudPort
     * @return boolean
     */
    private boolean isQosRuleChange(CloudPluginSceneElementPortBO newCloudPort, CloudPluginSceneElementPortBO originCloudPort) {
        SceneElementPortBO newSrcPort = newCloudPort.getSrcPort();
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

    /**
     * doQosChange
     *
     * @param newCloudPort
     * @param originCloudPort
     */
    private void doQosChange(CloudPluginSceneElementPortBO newCloudPort, CloudPluginSceneElementPortBO originCloudPort) {
        UpdateQosRuleData data = new UpdateQosRuleData();
        data.setQosId(originCloudPort.getQosId());
        data.setQosRules(new ArrayList<>());
        SceneElementPortBO newSrcPort = newCloudPort.getSrcPort();
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
                CloudPluginSceneElementNetworkBO net = this.originCloudScenePluginHandler.getHandlerUtils().getNetworkByPort(originCloudPort.getSrcPort());
                if (net != null && StringUtils.isNotEmpty(net.getResourceId())) {
                    CreateQosRuleData createData = new CreateQosRuleData();
                    createData.setName("MP_SCENE_" + originCloudPort.getSrcPort().getEid());
                    createData.setQosRules(data.getQosRules());
                    createData.setPortId(originCloudPort.getResourceId());
                    createData.setNetworkId(net.getResourceId());
                    CreateQosRuleData result = virtualPort.createQosRules(createData);
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

    /**
     * firewallRuleChange
     *
     * @param newCloudElement
     * @param originCloudElement
     */
    private void firewallRuleChange(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        Set<String> mayUpdatePortIds = new HashSet<>();
        for (SceneElementPortBO sceneElementPortBO : newCloudElement.getSrcElement().getPorts()) {
            String id = sceneElementPortBO.getId();
            mayUpdatePortIds.add(id);
        }
        Set<String> originPortIds = new HashSet<>();
        for (SceneElementPortBO p : originCloudElement.getSrcElement().getPorts()) {
            String id = p.getId();
            originPortIds.add(id);
        }
        mayUpdatePortIds.retainAll(originPortIds);
        if (CollectionUtils.isNotEmpty(mayUpdatePortIds)) {
            for (String portId : mayUpdatePortIds) {
                CloudPluginSceneElementPortBO originPort = this.originCloudScenePluginHandler.getPort(portId);
                CloudPluginSceneElementPortBO newPort = this.newCloudScenePluginHandler.getPort(portId);
                if (this.isFirewallRuleChange(newPort, originPort)) {
                    this.doFirewallRuleChange(newPort, originPort, originCloudElement);
                }
            }
        }
    }

    /**
     * isFirewallRuleChange
     *
     * @param newCloudPort
     * @param originCloudPort
     * @return boolean
     */
    private boolean isFirewallRuleChange(CloudPluginSceneElementPortBO newCloudPort, CloudPluginSceneElementPortBO originCloudPort) {
        Set<String> newFirewallRules = new HashSet<>();
        for (FirewallRule firewallRule : newCloudPort.getFirewallRules()) {
            String s = StringUtils.joinWith(":", firewallRule.getAction(), firewallRule.getProtocol(), firewallRule.getDirection(), firewallRule.getIpVersion(), firewallRule.getSourcePort(), firewallRule.getSourceIpAddress(), firewallRule.getDestinationPort(), firewallRule.getDestinationIpAddress());
            newFirewallRules.add(s);
        }
        Set<String> originFirewallRules = new HashSet<>();
        for (FirewallRule f : originCloudPort.getFirewallRules()) {
            String s = StringUtils.joinWith(":", f.getAction(), f.getProtocol(), f.getDirection(), f.getIpVersion(), f.getSourcePort(), f.getSourceIpAddress(), f.getDestinationPort(), f.getDestinationIpAddress());
            originFirewallRules.add(s);
        }
        // 数量不同
        if (newFirewallRules.size() != originFirewallRules.size()) {
            return true;
        }
        // 内容不同
        newFirewallRules.removeAll(originFirewallRules);
        return !newFirewallRules.isEmpty();
    }

    /**
     * doFirewallRuleChange
     *
     * @param newCloudPort
     * @param originCloudPort
     * @param originCloudElement
     */
    private void doFirewallRuleChange(CloudPluginSceneElementPortBO newCloudPort, CloudPluginSceneElementPortBO originCloudPort, CloudPluginSceneElementBO originCloudElement) {
        VirtualFirewallUpdateData updateData = new VirtualFirewallUpdateData();
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
        List<FirewallRule> portSrcRules = newCloudPort.getFirewallRules();
        if (portSrcRules != null && !portSrcRules.isEmpty()) {
            CreateHandler.CloudFirewallRules cloudFirewallRules = CreateHandler.CloudFirewallRules.fromSrcRules(portSrcRules);
            updateData.setIngressRules(cloudFirewallRules.getIngressRules());
            updateData.setEgressRules(cloudFirewallRules.getEgressRules());
        }
        if (StringUtils.isNotEmpty(originCloudPort.getFirewallResourceId())) {
            virtualFirewall.update(updateData);
        } else {
            VirtualFirewallCreateData virtualFirewallCreateData = virtualFirewall.create(updateData);
            newCloudPort.setFirewallResourceId(virtualFirewallCreateData.getId());
        }
    }

    /**
     * changeConnect
     *
     * @param newCloudElement
     * @param originCloudElement
     */
    private void changeConnect(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        Set<String> newPortIds;
        Set<String> originPortIds;
        if (newCloudElement != null && newCloudElement.getSrcElement() != null && newCloudElement.getSrcElement().getPorts() != null) {
            Set<String> set = new HashSet<>();
            for (SceneElementPortBO p : newCloudElement.getSrcElement().getPorts()) {
                String id = p.getId();
                set.add(id);
            }
            newPortIds = set;
        } else {
            newPortIds = new ConcurrentHashSet<>();
        }
        if (originCloudElement != null && originCloudElement.getSrcElement() != null && originCloudElement.getSrcElement().getPorts() != null) {
            Set<String> set = new HashSet<>();
            for (SceneElementPortBO p : originCloudElement.getSrcElement().getPorts()) {
                String id = p.getId();
                set.add(id);
            }
            originPortIds = set;
        } else {
            originPortIds = new ConcurrentHashSet<>();
        }
        // 本次新增的端口
        Set<String> addPortIds = new HashSet<>(newPortIds);
        addPortIds.removeAll(originPortIds);

        // 本次删除的端口
        Set<String> delPortIds = new HashSet<>(originPortIds);
        delPortIds.removeAll(newPortIds);

        // 本次可能更新的端口
        Set<String> mayUpdatePortIds = new HashSet<>(newPortIds);
        mayUpdatePortIds.retainAll(originPortIds);

        if (newCloudElement.getSrcElement() != null && CollectionUtils.isNotEmpty(newCloudElement.getSrcElement().getPorts())) {
            for (SceneElementPortBO p : newCloudElement.getSrcElement().getPorts()) {
                if (Boolean.TRUE.equals(p.isChange())) {
                    mayUpdatePortIds.add(p.getId());
                }
            }
        }

        log.debug("newCloudElement is {}, originCloudElement is {}, addPortIds is {}, delPortIds is {}, mayUpdatePortIds is {}",
                JsonCommon.stringify(newCloudElement), JsonCommon.stringify(originCloudElement),
                JsonCommon.stringify(addPortIds), JsonCommon.stringify(delPortIds), JsonCommon.stringify(mayUpdatePortIds));
        for (String addPortId : addPortIds) {
            CloudPluginSceneElementPortBO addCloudPort = this.newCloudScenePluginHandler.getPort(addPortId);
            SceneElementPortBO addPort = this.newCloudScenePluginHandler.getPort(addPortId).getSrcPort();
            CloudPluginSceneElementNetworkBO net = this.newCloudScenePluginHandler.getHandlerUtils().getNetworkByPort(addPort);
            if (net != null) {
                // 虚拟机容器分开处理
                if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                    VirtualMachineAddPortResult result = this.vmAddPort(originCloudElement.getResourceId(), net.getResourceId(), addPort.getIp(), null, originCloudElement, addCloudPort);
                    addPort.setMacAddr(result.getMacAddress());
                    addCloudPort.setResourceId(result.getPortId());
                } else {
                    ContainerAddPortData result = this.containerAddPort(originCloudElement.getResourceId(), net.getResourceId(), addPort.getIp());
                    addPort.setMacAddr(result.getMacAddress());
                    addCloudPort.setResourceId(result.getPortId());
                }
                addCloudPort.setAuxiliaryNetworkId(net.getResourceId());
                // 新增端口映射
                if (CollectionUtils.isNotEmpty(addCloudPort.getAccessModes())) {
                    for (CloudAccessMode a : addCloudPort.getAccessModes()) {
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
                        PortMappingCreateResult portMapping = virtualSwitch.createPortMapping(portMappingCreateData);
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
            // 虚拟机容器分开处理
            if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                CloudPluginSceneElementPortBO deleteCloudPort = this.originCloudScenePluginHandler.getPort(delPortId);
                if (deleteCloudPort != null && StringUtils.isNotEmpty(deleteCloudPort.getResourceId())) {
                    this.deletePort(originCloudElement.getResourceId(), deleteCloudPort.getResourceId(), originCloudElement, deleteCloudPort);
                }
            } else {
                CloudPluginSceneElementPortBO deleteCloudPort = this.originCloudScenePluginHandler.getPort(delPortId);
                if (deleteCloudPort != null && StringUtils.isNotEmpty(deleteCloudPort.getResourceId())) {
                    SceneElementPortBO deletePort = deleteCloudPort.getSrcPort();
                    if (deletePort != null) {
                        CloudPluginSceneElementNetworkBO originNet = this.getPluginHandler().getHandlerUtils().getNetworkByPort(deletePort);
                        if (originNet != null) {
                            this.containerDeletePort(originCloudElement.getResourceId(), originNet.getResourceId(), deleteCloudPort.getResourceId(), deleteCloudPort);
                        } else {
                            SceneElementPortBO deletePortConnectedPort = originCloudScenePluginHandler.getSceneHandler().getPortConnectedPort(delPortId);
                            if (deletePortConnectedPort != null) {
                                CloudPluginSceneElementNetworkBO originNet1 = this.getPluginHandler().getHandlerUtils().getNetworkByPort(deletePortConnectedPort);
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
            CloudPluginSceneElementPortBO newCloudPort = this.newCloudScenePluginHandler.getPort(mayUpdatePortId);
            SceneElementPortBO newPort = newCloudPort.getSrcPort();
            CloudPluginSceneElementPortBO originCloudPort = this.originCloudScenePluginHandler.getPort(mayUpdatePortId);
            SceneElementPortBO originPort = originCloudPort.getSrcPort();

            // 如果端口ip和连接的网络都没变,则不需要更新
            String newIp = newPort.getIp();
            String originIp = originPort.getIp();
            boolean ipChange = !StringUtils.equals(newIp, originIp);
            CloudPluginSceneElementNetworkBO newNet = this.newCloudScenePluginHandler.getHandlerUtils().getNetworkByPort(newPort);
            CloudPluginSceneElementNetworkBO originNet = this.originCloudScenePluginHandler.getHandlerUtils().getNetworkByPort(originPort);
            if ((newNet == null && originNet == null) || (StringUtils.equals(newIp, originIp))) {
                // do nothing
            } else if (newNet == null) {
                // 虚拟机容器分开处理
                // 去掉旧的
                if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                    this.deletePort(originCloudElement.getResourceId(), originCloudPort.getResourceId(), originCloudElement, originCloudPort);
                } else {
                    this.containerDeletePort(originCloudElement.getResourceId(), originNet.getResourceId(), originCloudPort.getResourceId(), originCloudPort);
                }
            } else if (originNet == null) {
                // 虚拟机容器分开处理
                if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                    // 添加新的
                    VirtualPortCreateData portCreateData = new VirtualPortCreateData();
                    portCreateData.setFixedIp(newIp);
                    portCreateData.setMacAddress(StringUtils.isEmpty(originPort.getMacAddr()) ? null : originPort.getMacAddr());
                    portCreateData.setNetworkId(newNet.getResourceId());
                    VirtualPortCreateResult newPortResult = virtualPort.create(portCreateData);
                    VirtualMachineAddPortResult result = this.vmAddPort(originCloudElement.getResourceId(), newNet.getResourceId(), newIp, newPortResult.getId(), originCloudElement, newCloudPort);
                    // 保存
                    newPort.setIp(result.getFixedIp());
                    newPort.setMacAddr(result.getMacAddress());
                    if (CollectionUtils.isNotEmpty(newCloudPort.getAccessModes())) {
                        for (CloudAccessMode a : newCloudPort.getAccessModes()) {
                            a.setAccessIp(newIp);
                        }
                    }
                    newCloudPort.setResourceId(result.getPortId());
                } else {
                    ContainerAddPortData result = this.containerAddPort(originCloudElement.getResourceId(), newNet.getResourceId(), newPort.getIp());

                    newPort.setIp(newPort.getIp());
                    newPort.setMacAddr(result.getMacAddress());
                    newCloudPort.setResourceId(result.getPortId());
                }
                newCloudPort.setAuxiliaryNetworkId(newNet.getResourceId());

                // 新增端口映射
                if (CollectionUtils.isNotEmpty(newCloudPort.getAccessModes())) {
                    for (CloudAccessMode a : newCloudPort.getAccessModes()) {
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
                        PortMappingCreateResult portMapping = virtualSwitch.createPortMapping(portMappingCreateData);
                        a.setAvailable(true);
                        a.setAccessIp(portMapping.getHost());
                        a.setAccessPort(portMapping.getHostPort());
                    }
                    checkAccessModes(newCloudElement);
                }

            } else if (!StringUtils.equals(newNet.getResourceId(), originNet.getResourceId())) {
                // 虚拟机容器分开处理
                if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                    // 去掉旧的
                    this.deletePort(originCloudElement.getResourceId(), originCloudPort.getResourceId(), originCloudElement, originCloudPort);
                    // 添加新的
                    VirtualPortCreateData portCreateData = new VirtualPortCreateData();
                    portCreateData.setFixedIp(newIp);
                    portCreateData.setMacAddress(StringUtils.isEmpty(originPort.getMacAddr()) ? null : originPort.getMacAddr());
                    portCreateData.setNetworkId(newNet.getResourceId());
                    VirtualPortCreateResult newPortResult = virtualPort.create(portCreateData);
                    VirtualMachineAddPortResult result = this.vmAddPort(originCloudElement.getResourceId(), newNet.getResourceId(), newIp, newPortResult.getId(), originCloudElement, newCloudPort);
                    // 保存
                    newPort.setIp(result.getFixedIp());
                    newPort.setMacAddr(result.getMacAddress());
                    newCloudPort.setResourceId(result.getPortId());
                    newCloudPort.setAuxiliaryNetworkId(newNet.getResourceId());
                    newCloudPort.setLogicNetworkId(newNet.getId());
                } else {
                    // 去掉旧的
                    this.containerDeletePort(originCloudElement.getResourceId(), originNet.getResourceId(), originCloudPort.getResourceId(), originCloudPort);
                    // 添加新的
                    ContainerAddPortData result = this.containerAddPort(originCloudElement.getResourceId(), newNet.getResourceId(), newPort.getIp());

                    newPort.setIp(newPort.getIp());
                    newPort.setMacAddr(result.getMacAddress());
                    newCloudPort.setResourceId(result.getPortId());
                    newCloudPort.setAuxiliaryNetworkId(newNet.getResourceId());
                    newCloudPort.setLogicNetworkId(newNet.getId());
                }

                // 新增端口映射
                if (CollectionUtils.isNotEmpty(newCloudPort.getAccessModes())) {
                    for (CloudAccessMode a : newCloudPort.getAccessModes()) {
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
                        PortMappingCreateResult portMapping = virtualSwitch.createPortMapping(portMappingCreateData);
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
                    for (CloudAccessMode a : newCloudPort.getAccessModes()) {
                        if (StringUtils.equals(a.getAccessIp(), originIp)) {
                            a.setAccessIp(newIp);
                        }
                    }
                }

                // 新增端口映射
                if (CollectionUtils.isNotEmpty(newCloudPort.getAccessModes())) {
                    for (CloudAccessMode a : newCloudPort.getAccessModes()) {
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
                        PortMappingCreateResult portMapping = virtualSwitch.createPortMapping(portMappingCreateData);
                        a.setAvailable(true);
                        a.setAccessIp(portMapping.getHost());
                        a.setAccessPort(portMapping.getHostPort());
                    }
                    checkAccessModes(newCloudElement);
                }

            }
        }
    }

    /**
     * vmAddPort
     *
     * @param elementResourceId
     * @param networkResourceId
     * @param fixedIp
     * @param portResourceId
     * @param originElement
     * @param newPort
     * @return
     */
    private VirtualMachineAddPortResult vmAddPort(String elementResourceId, String networkResourceId, String fixedIp, String portResourceId, CloudPluginSceneElementBO originElement, CloudPluginSceneElementPortBO newPort) {
        VirtualMachineAddPortData port = new VirtualMachineAddPortData();
        port.setFixedIp(fixedIp);
        port.setPortId(portResourceId);
        port.setNetworkId(networkResourceId);
        VirtualMachineAddPortResult result = virtualMachine.addPort(elementResourceId, port);

        if (SystemTypeCommandUtils.isWindows(SystemTypeUtils.getStandardSystemType(originElement.getSrcElement().getSystemType()))) {

            LinkStatusParams params = new LinkStatusParams();
            params.setInstanceId(originElement.getResourceId());
            params.setNetworkId(networkResourceId);
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // do nothing
                }
                CloudResponse<LinkStatusResultData> linkStatus = new Remote().cloudGet(CloudUrlConstant.LINK_STATUS, params, new TypeReference<CloudResponse<LinkStatusResultData>>() {
                }, true);
                if (linkStatus.isSuccess() && Boolean.TRUE.equals(linkStatus.getData().getInstanceLinkStatus())) {
                    break;
                }
            }

            CloudPluginSceneElementNetworkBO net = this.newCloudScenePluginHandler.getHandlerUtils().getNetworkByPort(newPort.getSrcPort());
            String ipWithCidr = IpUtils.getIpWithCidr(newPort.getSrcPort().getIp(), net != null ? net.getCidr() : StringUtils.EMPTY, "24");

            String afterDeleteCmd = "ip netns exec qdhcp-%s " +
                    "ip addr del %s dev " +
                    "$(ip netns exec qdhcp-%s ifconfig|grep tap|awk -F: '{print $1}')";
            String cmd = String.format(afterDeleteCmd, networkResourceId, ipWithCidr, networkResourceId);
            try {
                virtualMachine.execControllerCmd(cmd);
            } catch (Exception e) {
                log.error("exec cmd failed , cmd is {}", cmd);
            }
        }

        return result;
    }

    /**
     * containerAddPort
     *
     * @param elementResourceId
     * @param networkResourceId
     * @param fixedIp
     * @return
     */
    private ContainerAddPortData containerAddPort(String elementResourceId, String networkResourceId, String fixedIp) {
        ContainerAddPortData addPortData = new ContainerAddPortData();
        addPortData.setContainerId(elementResourceId);
        addPortData.setNetworkId(networkResourceId);
        addPortData.setFixedIp(fixedIp);
        return container.addPort(addPortData);
    }

    /**
     * containerDeletePort
     *
     * @param elementResourceId
     * @param networkResourceId
     * @param portResourceId
     * @param cloudPluginSceneElementPortBO
     * @return boolean
     */
    private boolean containerDeletePort(String elementResourceId, String networkResourceId, String portResourceId, CloudPluginSceneElementPortBO cloudPluginSceneElementPortBO) {
        ContainerDeletePortData deletePortData = new ContainerDeletePortData();
        deletePortData.setContainerId(elementResourceId);
        deletePortData.setNetworkId(networkResourceId);
        deletePortData.setPortId(portResourceId);
        return container.deletePort(deletePortData, cloudPluginSceneElementPortBO);
    }

    /**
     * deletePort
     *
     * @param instanceId
     * @param portId
     * @param element
     * @param port
     */
    private void deletePort(String instanceId, String portId, CloudPluginSceneElementBO element, CloudPluginSceneElementPortBO port) {
        virtualMachine.deletePort(instanceId, portId);
        if (SystemTypeCommandUtils.isWindows(SystemTypeUtils.getStandardSystemType(element.getSrcElement().getSystemType()))) {
            String afterDeleteCmd = "ip netns exec qdhcp-%s " +
                    "ip addr add %s dev " +
                    "$(ip netns exec qdhcp-%s ifconfig|grep tap|awk -F: '{print $1}')";

            CloudPluginSceneElementNetworkBO net = this.originCloudScenePluginHandler.getHandlerUtils().getNetworkByPort(port.getSrcPort());
            String ipWithCidr = IpUtils.getIpWithCidr(port.getSrcPort().getIp(), net != null ? net.getCidr() : StringUtils.EMPTY, "24");

            String cmd = String.format(afterDeleteCmd, port.getAuxiliaryNetworkId(), ipWithCidr, port.getAuxiliaryNetworkId());
            virtualMachine.execControllerCmd(cmd);
        }
    }

}
