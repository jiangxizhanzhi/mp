package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class InitiateHandler extends HandlerCommon {

    private List<String> existCidrs;

    private void prepareExistCidrs() {
        if (existCidrs == null) {
            existCidrs = statExistCidrs();
        }
    }

    private synchronized List<String> allocateCidrs(int count) {
        var cidrs = randomCidrs(count, existCidrs);
        existCidrs.addAll(cidrs);
        return cidrs;
    }

    /**
     * 判断虚拟路由需不需要辅助网络
     * @param nodeId 虚拟路由节点id
     * @param connectedEle 虚拟路由连接节点
     * @return 虚拟路由需不需要辅助网络
     */
    private boolean virtualRouterRequireAuxiliaryNetwork(String nodeId, SceneElementBO connectedEle) {
        // 自己和自己连接（异常），无需辅助网络
        if (nodeId.equals(connectedEle.getId())) {
            return false;
        }

        var connectedElement = getPluginHandler().getElement(connectedEle.getId());
        Integer connectedVirtualType = connectedElement == null ? null : connectedElement.getVirtualType();
        // 未知连接，无需辅助网络
        if (connectedVirtualType == null) {
            return false;
        }

        // 连接了虚拟交换机，无需辅助网络
        if (connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
            return false;
        }

        // 连接了虚拟路由并且对方未开启辅助网络，需要辅助网络
        if (connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                && !Boolean.TRUE.equals(connectedElement.getAuxiliaryNetworkOn())) {
            return true;
        }

        // 连接了虚拟机/容器/实体节点，需要辅助网络
        if (connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)
                || connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)
                || connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
            return true;
        }

        // 连接了虚拟防火墙，继续检查防火墙连接的对方节点
        if (connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL)) {
            var firewallConnectedEles = getPluginHandler().getEleConnectedEles(connectedEle.getId());
            for (var firewallConnectedEle : firewallConnectedEles) {
                if (virtualRouterRequireAuxiliaryNetwork(nodeId, firewallConnectedEle)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断虚拟路由需不需要辅助网络
     * @param nodeId 虚拟路由节点id
     * @return 虚拟路由需不需要辅助网络
     */
    private boolean virtualRouterRequireAuxiliaryNetwork(String nodeId) {
        var connectedEles = getPluginHandler().getEleConnectedEles(nodeId);
        for (var ele : connectedEles) {
            if (virtualRouterRequireAuxiliaryNetwork(nodeId, ele)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断终端（虚拟机，容器，实体终端）需不需要辅助网络
     * @param nodeId 终端节点id
     * @param connectedEle 虚拟路由连接节点
     * @return 虚拟路由需不需要辅助网络
     */
    private boolean terminalRequireAuxiliaryNetwork(String nodeId, SceneElementBO connectedEle) {
        // 自己和自己连接（异常），无需辅助网络
        if (nodeId.equals(connectedEle.getId())) {
            return false;
        }

        var connectedElement = getPluginHandler().getElement(connectedEle.getId());
        Integer connectedVirtualType = connectedElement == null ? null : connectedElement.getVirtualType();
        // 未知连接，无需辅助网络
        if (connectedVirtualType == null) {
            return false;
        }

        // 连接了虚拟交换机，无需辅助网络
        if (connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
            return false;
        }

        // 连接了虚拟路由，无需辅助网络
        if (connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
            return false;
        }

        // 连接了虚拟机/容器/实体节点并且对方未开启辅助网络，需要辅助网络
        if ((connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)
                || connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)
                || connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)
                && !Boolean.TRUE.equals(connectedElement.getAuxiliaryNetworkOn()))) {
            return true;
        }

        // 连接了虚拟防火墙，继续检查防火墙连接的对方节点
        if (connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL)) {
            var firewallConnectedEles = getPluginHandler().getEleConnectedEles(connectedEle.getId());
            for (var firewallConnectedEle : firewallConnectedEles) {
                if (terminalRequireAuxiliaryNetwork(nodeId, firewallConnectedEle)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断终端（虚拟机，容器，实体终端）需不需要辅助网络
     * @param nodeId 终端节点id
     * @return 虚拟路由需不需要辅助网络
     */
    private boolean terminalRequireAuxiliaryNetwork(String nodeId) {
        var connectedEles = getPluginHandler().getEleConnectedEles(nodeId);
        for (var ele : connectedEles) {
            if (terminalRequireAuxiliaryNetwork(nodeId, ele)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 初始化辅助网络
     * @param nodeId 网元节点id
     */
    private void initiateAuxiliaryNetwork(String nodeId) {
        var element = getPluginHandler().getElement(nodeId);
        // 重置资源id
        element.setAuxiliaryNetworkId(StringUtils.EMPTY);
        // 初始化cidr
        if (StringUtils.isEmpty(element.getAuxiliaryNetworkCidr())) {
            prepareExistCidrs();
            element.setCidr(allocateCidrs(1).get(0));
        }

        // 是否vlan模式(是否连接了实体节点)
        var vlan = false;
        Integer virtualType = element.getVirtualType();
        if (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
            // 本节点是实体节点
            vlan = true;
        } else {
            // 连接节点有实体节点
            var connectedEles = getPluginHandler().getEleConnectedEles(nodeId);
            for (var ele : connectedEles) {
                var connectedElement = getPluginHandler().getElement(ele.getId());
                Integer connectedVirtualType = connectedElement == null ? null : connectedElement.getVirtualType();
                if (connectedVirtualType != null
                        && connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
                    vlan = true;
                    break;
                }
            }
        }
        element.setAuxiliaryNetworkVlan(vlan);
    }

    public InitiateHandler(CloudScenePluginHandler pluginHandler) {
        super(pluginHandler);
    }

    /**
     * 单独初始化虚拟交换机
     * @param nodeId 网元节点id
     */
    public void initiateVirtualSwitch(String nodeId) {
        var element = getPluginHandler().getElement(nodeId);
        if (Boolean.TRUE.equals(element.getExternal())) {
            return;
        }

        // 重置资源id
        element.setResourceId(StringUtils.EMPTY);
        // 统一使用辅助网络
        element.setAuxiliaryNetworkOn(true);
        element.setAuxiliaryNetworkCidr(element.getCidr());
        element.setAuxiliaryNetworkGateway(element.getGateway());
        element.setAuxiliaryNetworkDnsNameservers(element.getDnsNameservers());
        element.setAuxiliaryNetworkDhcp(element.getDhcp());
        element.setAuxiliaryNetworkAllocationPools(element.getAllocationPools());
        element.setAuxiliaryNetworkVlan(element.getVlan());
        element.setAuxiliaryNetworkVlanId(element.getVlanId());
        // 初始化辅助网络
        initiateAuxiliaryNetwork(nodeId);
        // 同步设置辅助网络信息
        element.setCidr(element.getAuxiliaryNetworkCidr());
        element.setGateway(element.getAuxiliaryNetworkGateway());
        element.setDnsNameservers(element.getAuxiliaryNetworkDnsNameservers());
        element.setDhcp(element.getAuxiliaryNetworkDhcp());
        element.setAllocationPools(element.getAuxiliaryNetworkAllocationPools());
        element.setVlan(element.getAuxiliaryNetworkVlan());
        element.setVlanId(element.getAuxiliaryNetworkVlanId());
    }

    /**
     * 单独初始化虚拟路由
     * @param nodeId 网元节点id
     */
    public void initiateVirtualRouter(String nodeId) {
        var element = getPluginHandler().getElement(nodeId);
        if (Boolean.TRUE.equals(element.getExternal())) {
            return;
        }

        // 重置资源id
        element.setResourceId(StringUtils.EMPTY);
        // 是否需要辅助网络
        element.setAuxiliaryNetworkOn(virtualRouterRequireAuxiliaryNetwork(nodeId));
        if (Boolean.TRUE.equals(element.getAuxiliaryNetworkOn())) {
            // 初始化辅助网络
            initiateAuxiliaryNetwork(nodeId);
        }
    }

    /**
     * 单独初始化虚拟防火墙
     * @param nodeId 网元节点id
     */
    public void initiateVirtualFirewall(String nodeId) {
        var element = getPluginHandler().getElement(nodeId);
        if (Boolean.TRUE.equals(element.getExternal())) {
            return;
        }

        // 重置资源id
        element.setResourceId(StringUtils.EMPTY);
    }

    /**
     * 单独初始化虚拟机
     * @param nodeId 网元节点id
     */
    public void initiateVirtualMachine(String nodeId) {
        var element = getPluginHandler().getElement(nodeId);
        if (Boolean.TRUE.equals(element.getExternal())) {
            return;
        }

        // 重置资源id
        element.setResourceId(StringUtils.EMPTY);
        // 是否需要辅助网络
        element.setAuxiliaryNetworkOn(terminalRequireAuxiliaryNetwork(nodeId));
        if (Boolean.TRUE.equals(element.getAuxiliaryNetworkOn())) {
            // 初始化辅助网络
            initiateAuxiliaryNetwork(nodeId);
        }
    }

    /**
     * 单独初始化容器
     * @param nodeId 网元节点id
     */
    public void initiateContainer(String nodeId) {
        var element = getPluginHandler().getElement(nodeId);
        if (Boolean.TRUE.equals(element.getExternal())) {
            return;
        }

        // 重置资源id
        element.setResourceId(StringUtils.EMPTY);
        // 是否需要辅助网络
        element.setAuxiliaryNetworkOn(terminalRequireAuxiliaryNetwork(nodeId));
        if (Boolean.TRUE.equals(element.getAuxiliaryNetworkOn())) {
            // 初始化辅助网络
            initiateAuxiliaryNetwork(nodeId);
        }
    }

    /**
     * 单独初始化实体节点
     * @param nodeId 网元节点id
     */
    public void initiateEntityNode(String nodeId) {
        var element = getPluginHandler().getElement(nodeId);
        if (Boolean.TRUE.equals(element.getExternal())) {
            return;
        }

        // 重置资源id
        element.setResourceId(StringUtils.EMPTY);
        // 是否需要辅助网络
        element.setAuxiliaryNetworkOn(terminalRequireAuxiliaryNetwork(nodeId));
        if (Boolean.TRUE.equals(element.getAuxiliaryNetworkOn())) {
            // 初始化辅助网络
            initiateAuxiliaryNetwork(nodeId);
        }
    }

}
