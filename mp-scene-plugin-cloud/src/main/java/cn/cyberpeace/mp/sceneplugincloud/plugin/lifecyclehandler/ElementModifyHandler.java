package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.StaticRoute;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualRouter;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualSwitch;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterUpdateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchUpdateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RecreateTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

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

    private VirtualMachine vm = new VirtualMachine();
    private VirtualSwitch vs = new VirtualSwitch();
    private VirtualRouter vr = new VirtualRouter();

    public ElementModifyHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, RecreateTaskPool.getInstance(), event);
    }

    public void modifyElement(CloudPluginSceneElementBO newCloudElement) {
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
            }
        }
    }

    private void modifyVirtualMachine(CloudPluginSceneElementBO newCloudElement) {
        CloudPluginSceneElementBO originCloudElement = this.getPluginHandler().getElement(newCloudElement.getSrcElement().getId());
        if (this.isFlavorChange(newCloudElement, originCloudElement)) {
            // 先创建flavor
        }
    }

    private void modifyVirtualSwitch(CloudPluginSceneElementBO newCloudElement) {
        CloudPluginSceneElementBO originCloudElement = this.getPluginHandler().getElement(newCloudElement.getSrcElement().getId());
        if (CollectionUtils.isEmpty(originCloudElement.getDeriveNetworks())) {
            return;
        }
        var currentElementNetwork = originCloudElement.getDeriveNetworks().stream()
                .filter(n -> n.getNetwork().getCidr().equals(originCloudElement.getCidr()))
                .findFirst().orElse(new ConnectedNetworkBO(null, null))
                .network;
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
    }

    private void modifyVirtualFirewall(CloudPluginSceneElementBO newCloudElement) {
        CloudPluginSceneElementBO originCloudElement = this.getPluginHandler().getElement(newCloudElement.getSrcElement().getId());
        // 静态路由变更
        this.staticRouteChange(newCloudElement, originCloudElement);
        // 防火墙规则变更
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
        if (newElement.getCpu() != originElement.getCpu() || newElement.getRam() != originElement.getRam() || newElement.getDisk() != originElement.getDisk()) {
            return true;
        }
        return false;
    }
}
