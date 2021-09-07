package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitiateHandler extends HandlerCommon {

    private List<String> existCidrs;
    private final Map<String, List<String>> networkNodeIdIpPool = new HashMap<>();

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

    private boolean isTerminalElement(Integer virtualType) {
        return virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)
                || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)
                || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE);
    }

    /**
     * 判断需不需要辅助网络
     * @param srcElement 节点
     * @param connectedSrcElement 连接节点
     * @return 需不需要辅助网络
     */
    private boolean requireAuxiliaryNetwork(SceneElementBO srcElement, SceneElementBO connectedSrcElement) {
        // 自己和自己连接（异常），无需辅助网络
        if (srcElement.getId().equals(connectedSrcElement.getId())) {
            return false;
        }

        var cloudElement = getPluginHandler().getElement(srcElement.getId());
        var connectedCloudElement = getPluginHandler().getElement(connectedSrcElement.getId());
        // 未知连接，无需辅助网络
        if (cloudElement == null || connectedCloudElement == null) {
            return false;
        }

        // 透明节点，继续检查透明节点连接的对方节点
        if (Boolean.TRUE.equals(connectedCloudElement.getTransparent())) {
            var subConnectedSrcElements = getPluginHandler().getElementConnectedSrcElements(
                    connectedSrcElement.getId());
            for (var subConnectedSrcElement : subConnectedSrcElements) {
                if (requireAuxiliaryNetwork(srcElement, subConnectedSrcElement)) {
                    return true;
                }
            }
        }

        Integer virtualType = cloudElement.getVirtualType();
        Integer connectedVirtualType = connectedCloudElement.getVirtualType();
        // 未知类型节点，无需辅助网络
        if (virtualType == null || connectedVirtualType == null) {
            return false;
        }

        // 虚拟路由连接了虚拟路由并且对方未开启辅助网络，需要辅助网络
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                && connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                && !Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
            return true;
        }

        // 虚拟路由连接了虚拟机/容器/实体节点，需要辅助网络
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER) &&
                isTerminalElement(connectedVirtualType)) {
            return true;
        }

        // 虚拟机/容器/实体节点连接了虚拟机/容器/实体节点并且对方未开启辅助网络，需要辅助网络
        if (isTerminalElement(virtualType) && isTerminalElement(connectedVirtualType)
                && !Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
            return true;
        }

        return false;
    }

    /**
     * 判断节点需不需要辅助网络
     * @param srcElement 节点
     * @return 需不需要辅助网络
     */
    private boolean requireAuxiliaryNetwork(SceneElementBO srcElement) {
        var cloudElement = getPluginHandler().getElement(srcElement.getId());
        Integer virtualType = cloudElement.getVirtualType();
        if (virtualType == null) {
            return false;
        }

        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
            return true;
        }

        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL)) {
            return false;
        }

        for (var connectedOpaqueSrcElement : getEleConnectedOpaqueSrcElements(srcElement.getId())) {
            if (requireAuxiliaryNetwork(srcElement, connectedOpaqueSrcElement)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 初始化辅助网络
     * @param srcElement 网元节点
     */
    private void initiateAuxiliaryNetwork(SceneElementBO srcElement) {
        var nodeId = srcElement.getId();
        var cloudElement = getPluginHandler().getElement(nodeId);
        if (!Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn())) {
            return;
        }

        // 重置资源id
        cloudElement.setAuxiliaryNetworkId(StringUtils.EMPTY);
        // 初始化cidr
        if (StringUtils.isEmpty(cloudElement.getAuxiliaryNetworkCidr())) {
            prepareExistCidrs();
            cloudElement.setAuxiliaryNetworkCidr(allocateCidrs(1).get(0));
        }

        // 是否vlan模式(是否连接了实体节点)
        var vlan = false;
        Integer virtualType = cloudElement.getVirtualType();
        if (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
            // 本节点是实体节点
            vlan = true;
        } else {
            // 连接节点有实体节点
            var connectedSrcElements = getPluginHandler().getElementConnectedSrcElements(nodeId);
            for (var connectedSrcElement : connectedSrcElements) {
                var connectedCloudElement = getPluginHandler().getElement(connectedSrcElement.getId());
                Integer connectedVirtualType = connectedCloudElement == null ? null : connectedCloudElement.getVirtualType();
                if (connectedVirtualType != null
                        && connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
                    vlan = true;
                    break;
                }
            }
        }
        cloudElement.setAuxiliaryNetworkVlan(vlan);
    }


    /**
     * 确认连接使用的辅助网络所在节点
     * @param srcElement 源节点
     * @param connectedSrcElement 连接节点
     * @return 辅助网络所在节点
     */
    private SceneElementBO confirmAuxiliaryNetworkSrcElement(SceneElementBO srcElement,
                                                             SceneElementBO connectedSrcElement) {
        var cloudElement = getPluginHandler().getElement(srcElement.getId());
        var connectedCloudElement = getPluginHandler().getElement(connectedSrcElement.getId());
        if (cloudElement == null || connectedCloudElement == null) {
            return null;
        }
        Integer virtualType = cloudElement.getVirtualType();
        Integer connectedVirtualType = connectedCloudElement.getVirtualType();
        if (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
            return srcElement;
        }

        if (connectedVirtualType != null
                && connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
            return connectedSrcElement;
        }

        if (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                && Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn())) {
            return srcElement;
        }

        if (connectedVirtualType != null
                && connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                && Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
            return connectedSrcElement;
        }

        if (Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn())) {
            return srcElement;
        }

        if (Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
            return connectedSrcElement;
        }

        return null;
    }

    /**
     * 初始化辅助网络节点的ip池
     * @param auxiliaryNetworkNodeId 辅助网络节点id
     */
    private void initIpPool(String auxiliaryNetworkNodeId) {
        var auxiliaryNetworkCloudElement = getPluginHandler().getElement(auxiliaryNetworkNodeId);
        var connectedOpaqueSrcPorts = getEleConnectedOpaqueSrcPorts(auxiliaryNetworkNodeId);
        var requireCount = 0;
        List<String> existIps = new ArrayList<>();
        for (var port : connectedOpaqueSrcPorts) {
            var ip = port.getIp();
            if (StringUtils.isEmpty(ip)) {
                requireCount++;
            } else {
                existIps.add(ip);
            }
        }

        List<String> ipPool = IpUtils.randomIps(auxiliaryNetworkCloudElement.getAuxiliaryNetworkCidr(), requireCount,
                existIps, auxiliaryNetworkCloudElement.getAllocationPools());
        networkNodeIdIpPool.put(auxiliaryNetworkNodeId, ipPool);
    }

    /**
     * 初始化虚拟网络端口
     * @param srcElement 网元节点
     * @param srcPort 网元节点端口
     */
    private void initiateVirtualPort(SceneElementBO srcElement, SceneElementPortBO srcPort) {
        if (!StringUtils.isEmpty(srcPort.getIp())) {
            return;
        }

        var portId = srcPort.getId();
        var connectedSrcElement = getPortConnectedOpaqueSrcElement(portId);
        if (connectedSrcElement == null) {
            return;
        }

        var auxiliaryNetworkSrcElement = confirmAuxiliaryNetworkSrcElement(srcElement, connectedSrcElement);
        if (auxiliaryNetworkSrcElement == null) {
            return;
        }

        // 分配ip
        List<String> ipPool = networkNodeIdIpPool.get(auxiliaryNetworkSrcElement.getId());
        if (ipPool == null || ipPool.isEmpty()) {
            // TODO 异常
            return;
        }

        srcPort.setIp(ipPool.remove(0));
    }

    /**
     * 初始化虚拟网络端口
     * @param srcElement 网元节点
     */
    private void initiateVirtualPorts(SceneElementBO srcElement) {
        var srcPorts = srcElement.getPorts();
        if (srcPorts == null) {
            return;
        }

        for (var srcPort : srcPorts) {
            initiateVirtualPort(srcElement, srcPort);
        }
    }

    public InitiateHandler(CloudScenePluginHandler pluginHandler) {
        super(pluginHandler);
    }

    /**
     * 初始化节点
     * @param srcElement 网元节点
     */
    public void initiateElement(SceneElementBO srcElement) {
        var cloudElement = getPluginHandler().getElement(srcElement.getId());
        if (Boolean.TRUE.equals(cloudElement.getExternal())) {
            return;
        }
        Integer virtualType = cloudElement.getVirtualType();
        if (virtualType == null) {
            return;
        }

        // 重置资源id
        cloudElement.setResourceId(StringUtils.EMPTY);
        // 是否需要辅助网络
        cloudElement.setAuxiliaryNetworkOn(requireAuxiliaryNetwork(srcElement));
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
            // 统一使用辅助网络
            cloudElement.setAuxiliaryNetworkCidr(cloudElement.getCidr());
            cloudElement.setAuxiliaryNetworkGateway(cloudElement.getGateway());
            cloudElement.setAuxiliaryNetworkDnsNameservers(cloudElement.getDnsNameservers());
            cloudElement.setAuxiliaryNetworkDhcp(cloudElement.getDhcp());
            cloudElement.setAuxiliaryNetworkAllocationPools(cloudElement.getAllocationPools());
            cloudElement.setAuxiliaryNetworkVlan(cloudElement.getVlan());
            cloudElement.setAuxiliaryNetworkVlanId(cloudElement.getVlanId());
        }

        // 初始化辅助网络
        initiateAuxiliaryNetwork(srcElement);

        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
            // 同步设置辅助网络信息
            cloudElement.setCidr(cloudElement.getAuxiliaryNetworkCidr());
            cloudElement.setGateway(cloudElement.getAuxiliaryNetworkGateway());
            cloudElement.setDnsNameservers(cloudElement.getAuxiliaryNetworkDnsNameservers());
            cloudElement.setDhcp(cloudElement.getAuxiliaryNetworkDhcp());
            cloudElement.setAllocationPools(cloudElement.getAuxiliaryNetworkAllocationPools());
            cloudElement.setVlan(cloudElement.getAuxiliaryNetworkVlan());
            cloudElement.setVlanId(cloudElement.getAuxiliaryNetworkVlanId());
        }
    }

    public void initiate() {
        // 初始化节点属性
        for (var nodeId : getPluginHandler().getElementMap().keySet()) {
            var srcElement = getPluginHandler().getSrcElement(nodeId);
            initiateElement(srcElement);
        }
        // 初始化ip池
        for (var entry : getPluginHandler().getElementMap().entrySet()) {
            var cloudElement = entry.getValue();
            if (Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn())) {
                initIpPool(entry.getKey());
            }
        }
        // 初始化端口
        for (var nodeId : getPluginHandler().getElementMap().keySet()) {
            var srcElement = getPluginHandler().getSrcElement(nodeId);
            initiateVirtualPorts(srcElement);
        }
    }

}
