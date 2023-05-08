package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.StaticRoute;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualRouter;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualSwitch;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CreateFlavorData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CreateFlavorResult;
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

    private VirtualMachine vm = new VirtualMachine();
    private VirtualSwitch vs = new VirtualSwitch();
    private VirtualRouter vr = new VirtualRouter();

    public ElementModifyHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, RecreateTaskPool.getInstance(), event);
    }

    public void modifyElement(CloudPluginSceneElementBO newCloudElement, CloudScenePluginHandler newCloudScenePluginHandler) {
        this.newCloudScenePluginHandler = newCloudScenePluginHandler;
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
            vm.resize(originCloudElement.getResourceId(), flavorId);
        }
    }

    private void modifyContainer(CloudPluginSceneElementBO newCloudElement) {
        CloudPluginSceneElementBO originCloudElement = this.getPluginHandler().getElement(newCloudElement.getSrcElement().getId());
        if (this.isFlavorChange(newCloudElement, originCloudElement)) {
            // 先创建flavor
            String flavorId = this.createFlavor(newCloudElement);
        }
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
        vr.update(originCloudElement.getResourceId(), updateData);
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
        vs.update(currentElementNetwork.getResourceId(), updateData);
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
        if (!NumberUtil.equals(newElement.getCpu(), originElement.getCpu()) ||
                !NumberUtil.equals(newElement.getDisk(), originElement.getDisk()) ||
                !NumberUtil.equals(newElement.getRam(), originElement.getRam())) {
            return true;
        }
        return false;
    }

    private String createFlavor(CloudPluginSceneElementBO newCloudElement) {
        CreateFlavorData createFlavorData = new CreateFlavorData();
        createFlavorData.setName(newCloudElement.getSrcElement().getEid().substring(0, 8));
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
                    this.doQosChange(newPort);
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

    private void doQosChange(CloudPluginSceneElementPortBO newCloudPort) {

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
                    this.doFirewallRuleChange(newPort);
                }
            }
        }
    }

    private boolean isFirewallRuleChange(CloudPluginSceneElementPortBO newCloudPort, CloudPluginSceneElementPortBO originCloudPort) {
        Set<String> newFirewallRules = newCloudPort.getFirewallRules().stream().map(f -> StringUtils.joinWith(
                ":", f.getAction(), f.getProtocol(), f.getDirection(), f.getIpVersion(),
                f.getSourcePort(), f.getSourceIpAddress(),
                f.getDestinationPort(), f.getDestinationIpAddress()
        )).collect(Collectors.toSet());
        Set<String> originFirewallRules = originCloudPort.getFirewallRules().stream().map(f -> StringUtils.joinWith(
                ":", f.getAction(), f.getProtocol(), f.getDirection(), f.getIpVersion(),
                f.getSourcePort(), f.getSourceIpAddress(),
                f.getDestinationPort(), f.getDestinationIpAddress()
        )).collect(Collectors.toSet());
        // 数量不同
        if (newFirewallRules.size() != originFirewallRules.size()) {
            return true;
        }
        // 内容不同
        newFirewallRules.removeAll(originFirewallRules);
        if (newFirewallRules.size() > 0) {
            return true;
        }
        return false;
    }

    private void doFirewallRuleChange(CloudPluginSceneElementPortBO newCloudPort) {

    }
}
