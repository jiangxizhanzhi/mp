package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.StaticRoute;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.constant.QosRuleDirection;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualFirewall;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualPort;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualRouter;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualSwitch;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerAddPortData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerDeletePortData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerResizeData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CreateFlavorData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CreateFlavorResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ModifyInstanceIpData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.UpdateQosRuleData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualFirewallUpdateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineAddPortData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualPortCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterUpdateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchUpdateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RecreateTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/25
 */
@Slf4j
public class ElementModifyHandler extends HandlerCommon {

    private CloudScenePluginHandler newCloudScenePluginHandler;

    private VirtualMachine virtualMachine = new VirtualMachine();
    private Container container = new Container();
    private VirtualSwitch virtualSwitch = new VirtualSwitch();
    private VirtualRouter virtualRouter = new VirtualRouter();
    private VirtualFirewall virtualFirewall = new VirtualFirewall();
    private VirtualPort virtualPort = new VirtualPort();

    public ElementModifyHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, RecreateTaskPool.getInstance(), event);
    }

    public void modifyElement(CloudPluginSceneElementBO newCloudElement, CloudScenePluginHandler newCloudScenePluginHandler) {
        this.newCloudScenePluginHandler = newCloudScenePluginHandler;
        // todo 优化 去掉role这个业务字段
        if (newCloudElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH) {
            // 更新网络
            this.modifyVirtualSwitch(newCloudElement);
        } else if (newCloudElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER) {
            // 更新虚拟路由
            this.modifyVirtualRouter(newCloudElement);
        } else if (newCloudElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL) {
            // 更新虚拟防护墙
            this.modifyVirtualFirewall(newCloudElement);
        } else if (newCloudElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL) {
            if (newCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                // 更新虚拟机终端
                this.modifyVirtualMachine(newCloudElement);
            } else if (newCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER) {
                // 更新容器终端
                this.modifyContainer(newCloudElement);
            }
        }
    }

    private void modifyVirtualMachine(CloudPluginSceneElementBO newCloudElement) {
        CloudPluginSceneElementBO originCloudElement = this.getPluginHandler().getElement(newCloudElement.getSrcElement().getId());
        if (this.isFlavorChange(newCloudElement, originCloudElement)) {
            // 先创建flavor
            String flavorId = this.createFlavor(newCloudElement);
            virtualMachine.resize(originCloudElement.getResourceId(), flavorId);
        }
        this.changeConnect(newCloudElement, originCloudElement);
    }

    private void modifyContainer(CloudPluginSceneElementBO newCloudElement) {
        CloudPluginSceneElementBO originCloudElement = this.getPluginHandler().getElement(newCloudElement.getSrcElement().getId());
        if (this.isFlavorChange(newCloudElement, originCloudElement)) {
            ContainerResizeData resizeData = new ContainerResizeData();
            resizeData.setCpu(newCloudElement.getSrcElement().getCpu());
            resizeData.setRam(newCloudElement.getSrcElement().getRam());
            resizeData.setDisk(newCloudElement.getSrcElement().getDisk());
            container.resize(originCloudElement.getResourceId(), resizeData);
        }
        this.changeConnect(newCloudElement, originCloudElement);
    }

    private void modifyVirtualSwitch(CloudPluginSceneElementBO newCloudElement) {
        CloudPluginSceneElementBO originCloudElement = this.getPluginHandler().getElement(newCloudElement.getSrcElement().getId());
        if (CollectionUtils.isEmpty(originCloudElement.getDeriveNetworks())) {
            return;
        }
        var currentElementNetwork = originCloudElement.getDeriveNetworks().stream().filter(n -> n.getNetwork().getCidr().equals(originCloudElement.getCidr())).findFirst().orElse(new ConnectedNetworkBO(null, null)).network;
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

    private void modifyVirtualRouter(CloudPluginSceneElementBO newCloudElement) {
        CloudPluginSceneElementBO originCloudElement = this.getPluginHandler().getElement(newCloudElement.getSrcElement().getId());
        // 静态路由变更
        this.staticRouteChange(newCloudElement, originCloudElement);
        // 端口qos变更
        this.qosRuleChange(newCloudElement, originCloudElement);
    }

    private void modifyVirtualFirewall(CloudPluginSceneElementBO newCloudElement) {
        CloudPluginSceneElementBO originCloudElement = this.getPluginHandler().getElement(newCloudElement.getSrcElement().getId());
        // 静态路由变更
        this.staticRouteChange(newCloudElement, originCloudElement);
        // 端口qos变更
        this.qosRuleChange(newCloudElement, originCloudElement);
        // 防火墙规则变更
        this.firewallRuleChange(newCloudElement, originCloudElement);
    }

    private void staticRouteChange(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        if (this.isStaticRouteChange(newCloudElement.getStaticRoutes(), originCloudElement.getStaticRoutes())) {
            this.doStaticRouteChange(newCloudElement, originCloudElement);
        }
    }

    private boolean isStaticRouteChange(List<StaticRoute> newStaticRoutes, List<StaticRoute> originStaticRoutes) {
        // 数量不同
        if (newStaticRoutes.size() != originStaticRoutes.size()) {
            return true;
        }
        // 内容不同
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
        getPluginHandler().updateElementNetworks(newCloudElement.getSrcElement().getId());
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
        Set<String> mayUpdatePortIds = newCloudElement.getSrcElement().getPorts().stream().map(p -> p.getId()).collect(Collectors.toSet());
        Set<String> originPortIds = originCloudElement.getSrcElement().getPorts().stream().map(p -> p.getId()).collect(Collectors.toSet());
        mayUpdatePortIds.retainAll(originPortIds);
        if (CollectionUtils.isNotEmpty(mayUpdatePortIds)) {
            for (String portId : mayUpdatePortIds) {
                var originPort = this.getPluginHandler().getPort(portId);
                var newPort = this.newCloudScenePluginHandler.getPort(portId);
                if (this.isQosRuleChange(newPort, originPort)) {
                    this.doQosChange(newPort, originPort);
                }
            }
        }
    }

    private boolean isQosRuleChange(CloudPluginSceneElementPortBO newCloudPort, CloudPluginSceneElementPortBO originCloudPort) {
        boolean eGressChange = newCloudPort.getSrcPort().getEgress() != originCloudPort.getSrcPort().getEgress();
        boolean inGressChange = newCloudPort.getSrcPort().getIngress() != originCloudPort.getSrcPort().getIngress();
        boolean delayChange = newCloudPort.getSrcPort().getDelay() != originCloudPort.getSrcPort().getDelay();
        boolean lossChange = newCloudPort.getSrcPort().getLoss() != originCloudPort.getSrcPort().getLoss();
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
        virtualPort.updateQosRules(data);
    }

    private void firewallRuleChange(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        Set<String> mayUpdatePortIds = newCloudElement.getSrcElement().getPorts().stream().map(p -> p.getId()).collect(Collectors.toSet());
        Set<String> originPortIds = originCloudElement.getSrcElement().getPorts().stream().map(p -> p.getId()).collect(Collectors.toSet());
        mayUpdatePortIds.retainAll(originPortIds);
        if (CollectionUtils.isNotEmpty(mayUpdatePortIds)) {
            for (String portId : mayUpdatePortIds) {
                var originPort = this.getPluginHandler().getPort(portId);
                var newPort = this.newCloudScenePluginHandler.getPort(portId);
                if (this.isFirewallRuleChange(newPort, originPort)) {
                    this.doFirewallRuleChange(newPort, originPort);
                }
            }
        }
    }

    private boolean isFirewallRuleChange(CloudPluginSceneElementPortBO newCloudPort, CloudPluginSceneElementPortBO originCloudPort) {
        Set<String> newFirewallRules = newCloudPort.getFirewallRules().stream().map(f -> StringUtils.joinWith(":", f.getAction(), f.getProtocol(), f.getDirection(), f.getIpVersion(), f.getSourcePort(), f.getSourceIpAddress(), f.getDestinationPort(), f.getDestinationIpAddress())).collect(Collectors.toSet());
        Set<String> originFirewallRules = originCloudPort.getFirewallRules().stream().map(f -> StringUtils.joinWith(":", f.getAction(), f.getProtocol(), f.getDirection(), f.getIpVersion(), f.getSourcePort(), f.getSourceIpAddress(), f.getDestinationPort(), f.getDestinationIpAddress())).collect(Collectors.toSet());
        // 数量不同
        if (newFirewallRules.size() != originFirewallRules.size()) {
            return true;
        }
        // 内容不同
        newFirewallRules.removeAll(originFirewallRules);
        return !newFirewallRules.isEmpty();
    }

    private void doFirewallRuleChange(CloudPluginSceneElementPortBO newCloudPort, CloudPluginSceneElementPortBO originCloudPort) {
        var updateData = new VirtualFirewallUpdateData();
        updateData.setId(originCloudPort.getFirewallResourceId());
        var portSrcRules = newCloudPort.getFirewallRules();
        if (portSrcRules != null && !portSrcRules.isEmpty()) {
            var cloudFirewallRules = CreateHandler.CloudFirewallRules.fromSrcRules(portSrcRules);
            updateData.setIngressRules(cloudFirewallRules.getIngressRules());
            updateData.setEgressRules(cloudFirewallRules.getEgressRules());
        }
        virtualFirewall.update(updateData);
    }

    private void changeConnect(CloudPluginSceneElementBO newCloudElement, CloudPluginSceneElementBO originCloudElement) {
        Set<String> newPortIds = newCloudElement.getSrcElement().getPorts().stream().map(p -> p.getId()).collect(Collectors.toSet());
        Set<String> originPortIds = originCloudElement.getSrcElement().getPorts().stream().map(p -> p.getId()).collect(Collectors.toSet());
        // 本次新增的端口
        Set<String> addPortIds = new HashSet<>(newPortIds);
        addPortIds.removeAll(originPortIds);

        // 本次删除的端口
        Set<String> delPortIds = new HashSet<>(originPortIds);
        delPortIds.removeAll(newPortIds);

        // 本次可能更新的端口
        Set<String> mayUpdatePortIds = new HashSet<>(newPortIds);
        mayUpdatePortIds.retainAll(originPortIds);

        for (String addPortId : addPortIds) {
            var addCloudPort = this.newCloudScenePluginHandler.getPort(addPortId);
            var addPort = this.newCloudScenePluginHandler.getPort(addPortId).getSrcPort();
            var net = this.newCloudScenePluginHandler.getHandlerUtils().getNetworkByPort(addPort);
            if (net != null) {
                if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                    VirtualMachineAddPortData port = new VirtualMachineAddPortData();
                    port.setFixedIp(addPort.getIp());
                    port.setNetworkId(net.getResourceId());
                    var result = virtualMachine.addPort(originCloudElement.getResourceId(), port);

                    addPort.setIp(addPort.getIp());
                    addPort.setMacAddr(result.getMacAddress());
                    addCloudPort.setResourceId(result.getPortId());
                    addCloudPort.setAuxiliaryNetworkId(net.getResourceId());
                } else {
                    ContainerAddPortData addPortData = new ContainerAddPortData();
                    addPortData.setContainerId(originCloudElement.getResourceId());
                    addPortData.setNetworkId(net.getResourceId());
                    addPortData.setFixedIp(addPort.getIp());
                    var result = container.addPort(addPortData);

                    addPort.setIp(addPort.getIp());
                    addPort.setMacAddr(result.getMacAddress());
                    addCloudPort.setResourceId(result.getPortId());
                    addCloudPort.setAuxiliaryNetworkId(net.getResourceId());
                }
            }
        }

        for (String delPortId : delPortIds) {
            if (originCloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                virtualMachine.deletePort(originCloudElement.getResourceId(), delPortId);
            } else {
                var deleteCloudPort = this.getPluginHandler().getPort(delPortId);
                var deletePort = deleteCloudPort.getSrcPort();
                var originNet = this.getPluginHandler().getHandlerUtils().getNetworkByPort(deletePort);

                ContainerDeletePortData deletePortData = new ContainerDeletePortData();
                deletePortData.setContainerId(originCloudElement.getResourceId());
                deletePortData.setNetworkId(originNet.getResourceId());
                deletePortData.setPortId(deleteCloudPort.getResourceId());
                container.deletePort(deletePortData);
            }
        }

        for (String mayUpdatePortId : mayUpdatePortIds) {
            var newCloudPort = this.newCloudScenePluginHandler.getPort(mayUpdatePortId);
            var newPort = newCloudPort.getSrcPort();
            var originCloudPort = this.getPluginHandler().getPort(mayUpdatePortId);
            var originPort = originCloudPort.getSrcPort();

            // 如果端口ip和连接的网络都没变,则不需要更新
            String newIp = newPort.getIp();
            String originIp = originPort.getIp();
            boolean ipChange = !StringUtils.equals(newIp, originIp);
            var newNet = this.newCloudScenePluginHandler.getHandlerUtils().getNetworkByPort(newPort);
            var originNet = this.getPluginHandler().getHandlerUtils().getNetworkByPort(originPort);
            if (newNet == null || originNet == null) {
                // do nothing
            } else if ((newNet == null && originNet != null)) {
                // 去掉旧的
                virtualMachine.deletePort(originCloudElement.getResourceId(), originCloudPort.getResourceId());
            } else if (newNet != null && originNet == null) {
                // 添加新的
                VirtualPortCreateData portCreateData = new VirtualPortCreateData();
                portCreateData.setFixedIp(newIp);
                portCreateData.setMacAddress(originPort.getMacAddr());
                portCreateData.setNetworkId(newNet.getResourceId());
                var newPortResult = virtualPort.create(portCreateData);
                VirtualMachineAddPortData port = new VirtualMachineAddPortData();
                port.setFixedIp(newIp);
                port.setPortId(newPortResult.getId());
                port.setNetworkId(newNet.getResourceId());
                var result = virtualMachine.addPort(originCloudElement.getResourceId(), port);
                // 保存
                newPort.setIp(result.getFixedIp());
                newPort.setMacAddr(result.getMacAddress());
                newCloudPort.setResourceId(result.getPortId());
            } else if (!StringUtils.equals(newNet.getResourceId(), originNet.getResourceId())) {
                // 去掉旧的
                virtualMachine.deletePort(originCloudElement.getResourceId(), originCloudPort.getResourceId());
                // 添加新的
                VirtualPortCreateData portCreateData = new VirtualPortCreateData();
                portCreateData.setFixedIp(newIp);
                portCreateData.setMacAddress(originPort.getMacAddr());
                portCreateData.setNetworkId(newNet.getResourceId());
                var newPortResult = virtualPort.create(portCreateData);
                VirtualMachineAddPortData port = new VirtualMachineAddPortData();
                port.setFixedIp(newIp);
                port.setPortId(newPortResult.getId());
                port.setNetworkId(newNet.getResourceId());
                var result = virtualMachine.addPort(originCloudElement.getResourceId(), port);
                // 保存
                newPort.setIp(result.getFixedIp());
                newPort.setMacAddr(result.getMacAddress());
                newCloudPort.setResourceId(result.getPortId());
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
            }
        }
    }
}
