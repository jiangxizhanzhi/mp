package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.InitiateTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class InitiateHandler extends HandlerCommon {

    public static class SceneProcess {
        public static final EventProcess INITIATE_BASE_PROPERTIES = new EventProcess("initiate_base_properties", "sceneplugincloud.event.initiate_base_properties");
        public static final EventProcess INITIATE_IP_POOL = new EventProcess("initiate_ip_pool", "sceneplugincloud.event.initiate_ip_pool");
        public static final EventProcess INITIATE_PORTS = new EventProcess("initiate_ports", "sceneplugincloud.event.initiate_ports");
    }

    public static class ElementProcess {
        public static final EventProcess INITIATE_BASE_PROPERTIES = new EventProcess("initiate_base_properties", "sceneplugincloud.event.initiate_base_properties");
        public static final EventProcess INITIATE_IP_POOL = new EventProcess("initiate_ip_pool", "sceneplugincloud.event.initiate_ip_pool");
        public static final EventProcess INITIATE_PORTS = new EventProcess("initiate_ports", "sceneplugincloud.event.initiate_ports");
    }

    private List<String> existCidrs;
    private final Map<String, List<String>> networkNodeIdIpPool = new HashMap<>();

    private void prepareExistCidrs() {
        if (existCidrs == null) {
            existCidrs = getHandlerUtils().statExistCidrs();
        }
    }

    private synchronized List<String> allocateCidrs(int count) {
        var cidrs = getHandlerUtils().randomCidrs(count, existCidrs);
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
     * @param cloudElement 节点
     * @param connectedCloudElement 连接节点
     * @return 需不需要辅助网络
     */
    private boolean requireAuxiliaryNetwork(CloudPluginSceneElementBO cloudElement,
                                            CloudPluginSceneElementBO connectedCloudElement) {
        if (cloudElement == null) {
            return false;
        }

        // 没有连接，需要辅助网络
        if (connectedCloudElement == null) {
            return true;
        }

        // 自己和自己连接（异常），无需辅助网络
        if (cloudElement.getSrcElement().getId().equals(connectedCloudElement.getSrcElement().getId())) {
            return false;
        }

        // 透明节点，继续检查透明节点连接的对方节点
        if (Boolean.TRUE.equals(connectedCloudElement.getTransparent())) {
            var subConnectedSrcElements = getPluginHandler().getElementConnectedSrcElements(
                    connectedCloudElement.getSrcElement().getId());
            for (var subConnectedSrcElement : subConnectedSrcElements) {
                if (requireAuxiliaryNetwork(cloudElement,
                        getPluginHandler().getElement(subConnectedSrcElement.getId()))) {
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
     * @param cloudElement 节点
     * @return 需不需要辅助网络
     */
    private boolean requireAuxiliaryNetwork(CloudPluginSceneElementBO cloudElement) {
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

        var connectedOpaqueSrcElements = getHandlerUtils().getEleConnectedOpaqueSrcElements(
                cloudElement.getSrcElement().getId());
        if (connectedOpaqueSrcElements.isEmpty()) {
            return true;
        }
        for (var connectedOpaqueSrcElement : connectedOpaqueSrcElements) {
            var connectedOpaqueCloudElement = getPluginHandler().getElement(connectedOpaqueSrcElement.getId());
            if (requireAuxiliaryNetwork(cloudElement, connectedOpaqueCloudElement)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 初始化辅助网络
     * @param cloudElement 网元节点
     */
    private void initiateAuxiliaryNetwork(CloudPluginSceneElementBO cloudElement) {
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
            var connectedSrcElements = getPluginHandler().getElementConnectedSrcElements(cloudElement.getSrcElement().getId());
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
     * 初始化辅助网络节点的ip池
     * @param auxiliaryNetworkCloudElement 辅助网络节点
     */
    private void initIpPool(CloudPluginSceneElementBO auxiliaryNetworkCloudElement) {
        var cidr = auxiliaryNetworkCloudElement.getAuxiliaryNetworkCidr();
        if (StringUtils.isEmpty(cidr)) {
            return;
        }

        var auxiliaryNetworkNodeId = auxiliaryNetworkCloudElement.getSrcElement().getId();
        var connectedOpaqueSrcPorts = getHandlerUtils().getEleConnectedOpaqueSrcPorts(auxiliaryNetworkNodeId);
        Integer virtualType = auxiliaryNetworkCloudElement.getVirtualType();
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)
                || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            var auxiliaryNetworkSrcElement = getPluginHandler().getSrcElement(auxiliaryNetworkNodeId);
            var auxiliaryNetworkSrcPorts = auxiliaryNetworkSrcElement.getPorts();
            if (auxiliaryNetworkSrcPorts != null) {
                connectedOpaqueSrcPorts.addAll(auxiliaryNetworkSrcPorts);
            }
        }

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

        List<String> ipPool = IpUtils.randomIps(cidr, requireCount, existIps,
                auxiliaryNetworkCloudElement.getAllocationPools());
        networkNodeIdIpPool.put(auxiliaryNetworkNodeId, ipPool);
    }

    /**
     * 初始化虚拟网络端口
     * @param cloudElement 网元节点
     * @param srcPort 网元节点端口
     */
    private void initiateVirtualPort(CloudPluginSceneElementBO cloudElement, SceneElementPortBO srcPort) {
        if (!StringUtils.isEmpty(srcPort.getIp())) {
            return;
        }

        Integer virtualType = cloudElement.getVirtualType();
        if (virtualType == null) {
            return;
        }
        if (!virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                && !virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)
                && !virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            return;
        }

        var portId = srcPort.getId();
        var connectedSrcElement = getHandlerUtils().getPortConnectedOpaqueSrcElement(portId);
        var connectedCloudElement = connectedSrcElement == null ? null
                : getPluginHandler().getElement(connectedSrcElement.getId());
        var auxiliaryNetworkCloudElement = getHandlerUtils().confirmAuxiliaryNetworkCloudElement(cloudElement,
                connectedCloudElement);
        if (auxiliaryNetworkCloudElement == null) {
            return;
        }

        // 分配ip
        List<String> ipPool = networkNodeIdIpPool.get(auxiliaryNetworkCloudElement.getSrcElement().getId());
        if (ipPool == null || ipPool.isEmpty()) {
            return;
        }

        srcPort.setIp(ipPool.remove(0));
    }

    /**
     * 初始化虚拟网络端口
     * @param cloudElement 网元节点
     */
    private void initiateVirtualPorts(CloudPluginSceneElementBO cloudElement) {
        var srcPorts = cloudElement.getSrcElement().getPorts();
        if (srcPorts == null) {
            return;
        }

        for (var srcPort : srcPorts) {
            initiateVirtualPort(cloudElement, srcPort);
        }
    }

    /**
     * 初始化节点
     * @param cloudElement 网元节点
     */
    private void initiateElementBaseProperties(CloudPluginSceneElementBO cloudElement) {
        // 外部网络预先初始化
        var role = cloudElement.getRole();
        if (role != null && role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK)) {
            cloudElement.setExternal(true);
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH);
            cloudElement.setAuxiliaryNetworkOn(true);
            cloudElement.setAuxiliaryNetworkId(CloudSingleton.cloudAppConfig.getExternalNetId());
        }

        if (Boolean.TRUE.equals(cloudElement.getExternal())) {
            return;
        }

        Integer virtualType = cloudElement.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", cloudElement.getSrcElement().getEid(),
                    cloudElement.getSrcElement().getId());
            return;
        }

        // 重置资源id
        cloudElement.setResourceId(StringUtils.EMPTY);
        // 是否需要辅助网络
        cloudElement.setAuxiliaryNetworkOn(requireAuxiliaryNetwork(cloudElement));
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
        initiateAuxiliaryNetwork(cloudElement);

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

        // 虚拟机/容器 默认开启管理网络
//        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)
//                || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
//            cloudElement.setEnableMgmt(true);
//        }
    }

    public InitiateHandler(CloudScenePluginHandler pluginHandler) {
        super(pluginHandler, InitiateTaskPool.getInstance(), SceneModel.Event.INITIATE);
    }

    public void initiate() {
        prepareEventProcess(SceneModel.Event.INITIATE, SceneProcess.class, ElementProcess.class);

        var cloudElements = getPluginHandler().getElementMap().values();

        // 初始化节点属性
        var initiateElementTasks = cloudElements.stream()
                .map(cloudElement -> (Runnable) () -> initiateElementBasePropertiesProcess(cloudElement))
                .collect(Collectors.toList());
        if (!initiateElementTasks.isEmpty()) {
            runSceneProgress(SceneProcess.INITIATE_BASE_PROPERTIES, () -> runTasks(initiateElementTasks));
        }

        // 初始化ip池
        var initiateElementIpPoolTasks = cloudElements.stream()
                .filter(cloudElement -> Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn()))
                .map(cloudElement -> (Runnable) () -> initiateElementIpPoolProcess(cloudElement))
                .collect(Collectors.toList());
        if (!initiateElementIpPoolTasks.isEmpty()) {
            runSceneProgress(SceneProcess.INITIATE_IP_POOL, () -> runTasks(initiateElementIpPoolTasks));
        }

        // 初始化端口
        var initiateElementPortsTasks = cloudElements.stream()
                .filter(cloudElement -> {
                    Integer virtualType = cloudElement.getVirtualType();
                    return virtualType != null
                            && (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                            || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)
                            || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER));
                })
                .map(cloudElement -> (Runnable) () -> initiateElementPortsProcess(cloudElement))
                .collect(Collectors.toList());
        if (!initiateElementPortsTasks.isEmpty()) {
            runSceneProgress(SceneProcess.INITIATE_PORTS, () -> runTasks(initiateElementPortsTasks));
        }
    }

    public void initiateElementBasePropertiesProcess(CloudPluginSceneElementBO cloudElement) {
        runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.INITIATE_BASE_PROPERTIES,
                () -> initiateElementBaseProperties(cloudElement));
    }

    public void initiateElementIpPoolProcess(CloudPluginSceneElementBO cloudElement) {
        runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.INITIATE_IP_POOL,
                () -> initIpPool(cloudElement));
    }

    public void initiateElementPortsProcess(CloudPluginSceneElementBO cloudElement) {
        runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.INITIATE_PORTS,
                () -> initiateVirtualPorts(cloudElement));
    }

}
