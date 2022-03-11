package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.basic.BoolUtils;
import cn.cyberpeace.mp.base.util.basic.MapUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.CoreType;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.InitiateTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class InitiateHandler extends HandlerCommon {

    public static class SceneProcess {
        public static final EventProcess INITIATE_BASE_PROPERTIES = new EventProcess("initiate_base_properties", "sceneplugincloud.event.initiate_base_properties");
        public static final EventProcess INITIATE_IP_POOL = new EventProcess("initiate_ip_pool", "sceneplugincloud.event.initiate_ip_pool");
        public static final EventProcess INITIATE_PORTS = new EventProcess("initiate_ports", "sceneplugincloud.event.initiate_ports");
        public static final EventProcess REALLOCATE_IPS = new EventProcess("reallocate_ips", "sceneplugincloud.event.reallocate_ips");
    }

    public static class ElementProcess {
        public static final EventProcess INITIATE_BASE_PROPERTIES = new EventProcess("initiate_base_properties", "sceneplugincloud.event.initiate_base_properties");
        public static final EventProcess INITIATE_IP_POOL = new EventProcess("initiate_ip_pool", "sceneplugincloud.event.initiate_ip_pool");
        public static final EventProcess INITIATE_PORTS = new EventProcess("initiate_ports", "sceneplugincloud.event.initiate_ports");
    }

    @Data
    private static class ReallocateIpInfo {
        private CloudPluginSceneElementBO auxiliaryNetworkCloudElement;
        private String ip;

        public String key() {
            return StringUtils.joinWith(":", auxiliaryNetworkCloudElement.getSrcElement().getId(), ip);
        }
    }

    private List<String> existCidrs;
    private final Map<String, List<String>> networkNodeIdIpPool = new ConcurrentHashMap<>();

    // 辅助变量：需要重新分配的ip地址
    private Map<String, ReallocateIpInfo> reallocateIpInfos = new ConcurrentHashMap<>();

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

        // 没有连接
        if (connectedCloudElement == null) {
            // 存在其它连接不需要辅助网络
            if (getPluginHandler().getSceneHandler().elementHasConnectedPort(cloudElement.getSrcElement().getId())) {
                return false;
            } else {
                return true;
            }
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

        // 虚拟路由连接了虚拟路由
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                && connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
            Integer type = cloudElement.getSrcElement().getType();
            Integer connectedType = connectedCloudElement.getSrcElement().getType();
            // 连接了未定义的网元类型，需要辅助网络
            if (connectedType == null) {
                return true;
            }
            // 自己是未定义的网元类型，不需要辅助网络
            if (type == null) {
                return false;
            }

            // 网元类型交换机，需要辅助网络
            if (type.equals(BaseSceneElementModel.Type.SWITCH)) {
                return true;
            }

            // 网元类型相同，连接节点未开启辅助网络，需要辅助网络
            if (type.equals(connectedType) && !Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
                return true;
            }

            // 网元类型路由器
            if (type.equals(BaseSceneElementModel.Type.ROUTER)) {
                // 连接节点是交换机，不需要辅助网络
                if (connectedType.equals(BaseSceneElementModel.Type.SWITCH)) {
                    return false;
                } else {
                    return true;
                }
            }

            // 网元类型防火墙
            if (type.equals(BaseSceneElementModel.Type.FIREWALL)) {
                // 连接节点是交换机/路由，不需要辅助网络
                if (connectedType.equals(BaseSceneElementModel.Type.SWITCH)
                        || connectedType.equals(BaseSceneElementModel.Type.ROUTER)) {
                    return false;
                } else {
                    return true;
                }
            }

            // 其它普通网元类型, 连接节点开启辅助网络时，不需要辅助网络
            if (Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
                return false;
            } else {
                return true;
            }
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
        var elementHandler = getHandlerUtils().getElementHandler(cloudElement);
        var networks = elementHandler.getDeriveNetworks();
        for (var network : networks) {
            // todo 外部网络
            network.setNetworkId(StringUtils.EMPTY);
            if (StringUtils.isEmpty(network.getCidr())) {
                prepareExistCidrs();
                network.setCidr(allocateCidrs(1).get(0));
            }
        }
        // 重置资源id
        cloudElement.setAuxiliaryNetworkId(StringUtils.EMPTY);
        // 初始化cidr
        if (StringUtils.isEmpty(cloudElement.getAuxiliaryNetworkCidr())) {
            prepareExistCidrs();
            cloudElement.setAuxiliaryNetworkCidr(allocateCidrs(1).get(0));
        }


//        var connectedOpaqueSrcElements = getHandlerUtils().getEleConnectedOpaqueSrcElements(cloudElement.getSrcElement().getId());
//
//        // 是否vlan模式(是否连接了实体节点)
//        var vlan = false;
//        Integer virtualType = cloudElement.getVirtualType();
//        if (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
//            // 本节点是实体节点
//            vlan = true;
//        } else {
//            // 连接节点有实体节点
//            for (var connectedSrcElement : connectedOpaqueSrcElements) {
//                var connectedCloudElement = getPluginHandler().getElement(connectedSrcElement.getId());
//                Integer connectedVirtualType = connectedCloudElement == null ? null : connectedCloudElement.getVirtualType();
//                if (connectedVirtualType != null
//                        && connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
//                    vlan = true;
//                    break;
//                }
//            }
//        }
//        cloudElement.setAuxiliaryNetworkVlan(vlan);
//
//        // 是否开启辅助路由(是否未连接默认路由)
//        var routerOn = true;
//        if (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
//            // 本节点是默认路由
//            routerOn = false;
//        } else {
//            for (var connectedSrcElement : connectedOpaqueSrcElements) {
//                var connectedCloudElement = getPluginHandler().getElement(connectedSrcElement.getId());
//                Integer connectedVirtualType = connectedCloudElement == null ? null : connectedCloudElement.getVirtualType();
//                if (connectedVirtualType != null
//                        && connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
//                    routerOn = false;
//                    break;
//                }
//            }
//        }
//        cloudElement.setAuxiliaryNetworkRouterOn(routerOn);
    }

    /**
     * 初始化辅助网络节点的ip池
     * @param element 辅助网络节点
     */
    private void initIpPool(CloudPluginSceneElementBO element) {
        ElementHandler elementHandler = getHandlerUtils().getElementHandler(element);
        elementHandler.deriveNetworks.forEach(connectedNetwork -> {
            CloudPluginSceneElementNetworkBO network = connectedNetwork.network;
            String cidr = network.getCidr();
            if (StringUtils.isEmpty(cidr)) {
                return;
            }
            Set<String> existIps = new HashSet<>(getHandlerUtils().getOuterExistIps(element));
            // 网络本身需要一个dhcp地址、一个网关地址,网关地址分配给一个路由器
            int requireCount = 0;
            int gatewayCount = 0;

            // 处理自己的端口
            List<SceneElementPortBO> fromPorts = new ArrayList<>();
            List<SceneElementPortBO> toPorts = new ArrayList<>();

            connectedNetwork.connectPorts.forEach(cp -> {
                if (cp.from != null) {
                    fromPorts.add(cp.from);
                }
                if (cp.to != null) {
                    toPorts.add(cp.to);
                }
            });
            if (StringUtils.isNotEmpty(network.getGateway())) {
                // 已设置网关地址
                existIps.add(network.getGateway());
                for (var port : fromPorts) {
                    port.setIp(network.getGateway());
                }
            } else {
                String fromIp = null;
                for (var port : fromPorts) {
                    if (StringUtils.isNotEmpty(port.getIp())) {
                        existIps.add(port.getIp());
                        if (fromIp == null) {
                            fromIp = port.getIp();
                        }
                    }
                }
                if (fromIp != null) {
                    String finalFromIp = fromIp;
                    fromPorts.forEach(port -> port.setIp(finalFromIp));
                    network.setGateway(fromIp);
                } else {
                    if (element.getCoreType() == CoreType.ROUTER) {
                        // 自身是路由
                        gatewayCount++;
                    } else {
                        requireCount++;
                    }
                }
            }
            // 处理对端的端口
            for (var toPort : toPorts) {
                if (toPort != null) {
                    var connectedIp = toPort.getIp();
                    if (!StringUtils.isEmpty(connectedIp)) {
                        existIps.add(connectedIp);
                    }

                    var connectedOpaqueSrcElement = toPort.getElement();
                    var connectedOpaqueCloudElement = getPluginHandler().getElement(connectedOpaqueSrcElement.getId());
                    // 判断连接的端口是否需要ip
                    if (StringUtils.isEmpty(connectedIp)) {
                        if (connectedOpaqueCloudElement.getCoreType() == CoreType.ROUTER || connectedOpaqueCloudElement.getCoreType() == CoreType.NETWORK) {
                            // 连接到路由
                            gatewayCount++;
                        } else {
                            // 连接到虚拟机、容器
                            requireCount++;
                        }
                    }
                }
            }

            // 开始建ip池
            // 网关ip
            List<String> defaultRouterIpPool = IpUtils.allocateDefaultRouterIps(network.getCidr(), gatewayCount, new ArrayList<>(existIps), network.getAllocationPools());
            // 选择最后一个地址设置为初始默认网关
            if (!defaultRouterIpPool.isEmpty() && StringUtils.isEmpty(network.getGateway())) {
                var lastIp = defaultRouterIpPool.get(defaultRouterIpPool.size() - 1);
                existIps.add(lastIp);
                network.setGateway(lastIp);
            }

            // 网关地址之后的第一个地址可能是dhcp地址，需要排除掉
            if (!StringUtils.isEmpty(network.getGateway())) {
                var possibleDhcpIp = IpUtils.nextIp(network.getGateway());
                if (!StringUtils.isEmpty(possibleDhcpIp)) {
                    existIps.add(possibleDhcpIp);
                    // 如果分到了可能的dhcp地址，重新分配
                    if (defaultRouterIpPool.contains(possibleDhcpIp)) {
                        defaultRouterIpPool.remove(possibleDhcpIp);
                        defaultRouterIpPool.addAll(IpUtils.allocateDefaultRouterIps(network.getCidr(), 1, new ArrayList<>(existIps), network.getAllocationPools()));
                    }
                }
            }

            // 分配普通地址
            existIps.addAll(defaultRouterIpPool);
            List<String> ipPool = IpUtils.randomIps(network.getCidr(), requireCount, new ArrayList<>(existIps), network.getAllocationPools(), BoolUtils.bool(network.getDhcp()));
            // 默认路由地址在后
            ipPool.addAll(defaultRouterIpPool);
            network.setIpPool(ipPool);
        });
    }

    /**
     * 初始化虚拟网络端口
     * @param cloudElement 网元节点
     */
    private void initiateVirtualPort(CloudPluginSceneElementBO cloudElement) {
        var connectedNetworks = getHandlerUtils().getElementHandler(cloudElement).deriveNetworks;
        for (ElementHandler.ConnectedNetwork connectedNetwork : connectedNetworks) {
            List<String> ipPool = connectedNetwork.network.getIpPool();
            if (StringUtils.isEmpty(connectedNetwork.network.getGateway())) {
                connectedNetwork.network.setGateway(ipPool.remove(ipPool.size() - 1));
            }
            // 处理自己的端口
            List<SceneElementPortBO> fromPorts = new ArrayList<>();
            List<SceneElementPortBO> toPorts = new ArrayList<>();
            connectedNetwork.connectPorts.forEach(cp -> {
                if (cp.from != null) {
                    fromPorts.add(cp.from);
                }
                if (cp.to != null) {
                    toPorts.add(cp.to);
                }
            });
            for (var port : fromPorts) {
                port.setIp(connectedNetwork.network.getGateway());
            }
            for (var port : toPorts) {
                if (StringUtils.isNotEmpty(port.getIp())) {
                    continue;
                }
                var pluginElement = getPluginHandler().getElement(port.getElement().getId());
                if (pluginElement.getCoreType() == CoreType.ROUTER || pluginElement.getCoreType() == CoreType.NETWORK) {
                    // 并发处理？ todo
                    port.setIp(ipPool.remove(ipPool.size() - 1));
                } else {
                    // 并发处理？ todo
                    port.setIp(ipPool.remove(0));
                }
            }
        }
    }

    /**
     * 获取需要重新分配的端口
     */
    private Map<String, List<SceneElementPortBO>> getReallocateIpPorts() {
        Map<String, List<SceneElementPortBO>> groupSamePortsMap = new HashMap<>();

        reallocateIpInfos.forEach((key, reallocateIpInfo) -> {
            var auxiliaryNetworkCloudElement = reallocateIpInfo.getAuxiliaryNetworkCloudElement();
            var srcPorts = auxiliaryNetworkCloudElement.getSrcElement().getPorts();
            if (srcPorts == null || srcPorts.isEmpty()) {
                return;
            }

            var auxiliaryNetworkKey = auxiliaryNetworkCloudElement.getSrcElement().getEid();
            srcPorts.forEach(srcPort -> {
                var srcPortAuxiliaryNetworkCloudElement = getHandlerUtils().getPortAuxiliaryNetworkCloudElement(srcPort.getId());
                if (auxiliaryNetworkCloudElement.equals(srcPortAuxiliaryNetworkCloudElement)) {
                    if (reallocateIpInfo.getIp().equals(srcPort.getIp())) {
                        MapUtils.setDefault(groupSamePortsMap,
                                StringUtils.joinWith(":", auxiliaryNetworkKey, srcPort.getIp()),
                                new ArrayList<>()).add(srcPort);
                    }
                    var connectedSrcPort = getHandlerUtils().getPortConnectedOpaqueSrcPort(srcPort.getId());
                    if (connectedSrcPort != null && reallocateIpInfo.getIp().equals(connectedSrcPort.getIp())) {
                        MapUtils.setDefault(groupSamePortsMap,
                                StringUtils.joinWith(":", auxiliaryNetworkKey, connectedSrcPort.getIp()),
                                new ArrayList<>()).add(connectedSrcPort);
                    }
                }
            });
        });

        return groupSamePortsMap;
    }

    private void reallocateIpPorts(Map<String, List<SceneElementPortBO>> groupSamePortsMap) {
        groupSamePortsMap.forEach((key, sameSrcPorts) -> {
            var srcPort = sameSrcPorts.get(0);
            var srcPortAuxiliaryNetworkCloudElement = getHandlerUtils().getPortAuxiliaryNetworkCloudElement(srcPort.getId());
            Set<String> existIps = new HashSet<>(getHandlerUtils().getOuterExistIps(srcPortAuxiliaryNetworkCloudElement));
            var attachPorts = getHandlerUtils().getAuxiliaryNetworkCloudElementAttachPorts(srcPortAuxiliaryNetworkCloudElement);
            attachPorts.forEach(attachPort -> {
                if (!StringUtils.isEmpty(attachPort.getIp())) {
                    existIps.add(attachPort.getIp());
                }
            });
            if (!StringUtils.isEmpty(srcPortAuxiliaryNetworkCloudElement.getAuxiliaryNetworkRouterIp())) {
                existIps.add(srcPortAuxiliaryNetworkCloudElement.getAuxiliaryNetworkRouterIp());
            }

            var newIps = IpUtils.randomIps(srcPortAuxiliaryNetworkCloudElement.getAuxiliaryNetworkCidr(), 1,
                    new ArrayList<>(existIps),
                    srcPortAuxiliaryNetworkCloudElement.getAllocationPools(),
                    BoolUtils.bool(srcPortAuxiliaryNetworkCloudElement.getAuxiliaryNetworkDhcp()));
            var newIp = !newIps.isEmpty() ? newIps.get(0) : null;

            sameSrcPorts.forEach(sameSrcPort -> {
                if (newIp != null) {
                    log.info("Port {} reallocate ip {} to {}", sameSrcPort.getId(), sameSrcPort.getIp(), newIp);
                    srcPort.setIp(newIp);
                } else {
                    log.warn("Port {} reallocate ip failed, ip {} may conflict", srcPort.getId(), srcPort.getIp());
                }
            });
        });
    }

    /**
     * 初始化网络端口
     * @param cloudElement 网元节点
     */
    private void initiateElementPorts(CloudPluginSceneElementBO cloudElement) {
        var srcPorts = cloudElement.getSrcElement().getPorts();
        if (srcPorts == null) {
            return;
        }

        Integer virtualType = cloudElement.getVirtualType();
        var isVirtualElement = virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)
                || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER);

        for (var srcPort : srcPorts) {
            var cloudPort = getPluginHandler().getPort(srcPort.getId());
            if (cloudPort != null) {
                // 初始化accessModeId
                var cloudAccessModes = cloudPort.getAccessModes();
                if (cloudAccessModes != null) {
                    cloudAccessModes.forEach(cloudAccessMode -> {
                        if (StringUtils.isEmpty(cloudAccessMode.getId())) {
                            cloudAccessMode.setId(TextUtils.uuid());
                        }
                    });
                }
            }
        }

        if (isVirtualElement) {
            initiateVirtualPort(cloudElement);
        }
    }

    /**
     * 初始化节点
     * @param cloudElement 网元节点
     */
    private void initiateElementBaseProperties(CloudPluginSceneElementBO cloudElement) {
        ElementHandler elementHandler = getHandlerUtils().getElementHandler(cloudElement);
        // 外部网络预先初始化
        var role = cloudElement.getRole();
        if (role != null && role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK)) {
            cloudElement.setExternal(true);
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH);
            cloudElement.setAuxiliaryNetworkOn(true);
            cloudElement.setAuxiliaryNetworkId(CloudSingleton.cloudAppConfig.getExternalNetId());

            CloudPluginSceneElementNetworkBO network = new CloudPluginSceneElementNetworkBO();
            network.setExternal(true);
            network.setNetworkId(CloudSingleton.cloudAppConfig.getExternalNetId());
            elementHandler.deriveNetworks.add(new ElementHandler.ConnectedNetwork(network, getHandlerUtils()));
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
        // 需要的网络
        var ports = cloudElement.getSrcElement().getPorts();
        ElementHandler.ConnectedNetwork defaultConnectedNetwork = null;
        Map<String, ElementHandler.ConnectedNetwork> cidr2ConnectedNetwork = new HashMap<>();
        for (var port : ports) {
            var pluginPort = getPluginHandler().getPort(port.getId());
            var connectPort = getHandlerUtils().getPortConnectedOpaqueSrcPort(port.getId());
            var pluginConnectPort = connectPort == null ? null : getPluginHandler().getPort(connectPort.getId());
            if (connectPort != null) {
                // 有连线 且 对端端口已归属某个网络 则本端口接入即可
                CloudPluginSceneElementNetworkBO network = getHandlerUtils().getNetworkByPort(connectPort);
                if (network != null) {
                    ElementHandler.ConnectedNetwork connectedNetwork = new ElementHandler.ConnectedNetwork(network, getHandlerUtils());
                    connectedNetwork.addPort(port, connectPort);
                    elementHandler.accessNetworks.add(connectedNetwork);
                    continue;
                }
            }
            // 需要自己处理网络的端口
            String cidrDivideIp = null;
            if (StringUtils.isNotEmpty(port.getIp())) {
                // 自己的端口设置了ip,用自己的端口分网络
                cidrDivideIp = port.getIp();
            } else if (connectPort != null && StringUtils.isNotEmpty(connectPort.getIp())) {
                // 自己的端口无ip,但是连线另一端的端口设置了ip,用对端端口ip分网络
                cidrDivideIp = connectPort.getIp();
            }
            if (cidrDivideIp != null) {
                // 根据ip分网络
                String cidr = this.ip2Cidr24(cidrDivideIp);
                ElementHandler.ConnectedNetwork connectedNetwork = cidr2ConnectedNetwork.get(cidr);
                if (connectedNetwork == null) {
                    var network = new CloudPluginSceneElementNetworkBO();
                    network.setCidr(cidr);
                    connectedNetwork = new ElementHandler.ConnectedNetwork(network, getHandlerUtils());
                    cidr2ConnectedNetwork.put(cidr, connectedNetwork);
                }
                connectedNetwork.addPort(port, connectPort);
            } else {
                // 使用默认网络
                if (defaultConnectedNetwork == null) {
                    var network = new CloudPluginSceneElementNetworkBO();
                    this.loadProperties2Network(network, cloudElement);
                    defaultConnectedNetwork = new ElementHandler.ConnectedNetwork(network, getHandlerUtils());
                }
                defaultConnectedNetwork.addPort(port, connectPort);
            }
        }
        if (defaultConnectedNetwork != null) {
            elementHandler.deriveNetworks.add(defaultConnectedNetwork);
        }
        elementHandler.deriveNetworks.addAll(cidr2ConnectedNetwork.values());
        initiateAuxiliaryNetwork(cloudElement);
    }

    public InitiateHandler(CloudScenePluginHandler pluginHandler) {
        super(pluginHandler, InitiateTaskPool.getInstance(), SceneModel.Event.INITIATE);
    }

    public void initiate() {
        prepareEventProcess(SceneModel.Event.INITIATE, SceneProcess.class, ElementProcess.class);

        var cloudElements = new ArrayList<>(getPluginHandler().getElementMap().values());
        cloudElements.forEach(cloudElement -> getHandlerUtils().addElementHandler(cloudElement));
        Collections.sort(cloudElements, Comparator.comparing(CloudPluginSceneElementBO::getCoreType));

        // 初始化节点属性
        var initiateElementTasks = cloudElements.stream()
                .map(cloudElement -> (Runnable) () -> initiateElementBasePropertiesProcess(cloudElement))
                .collect(Collectors.toList());
        if (!initiateElementTasks.isEmpty()) {
            runSceneProgress(SceneProcess.INITIATE_BASE_PROPERTIES, () -> {
                initiateElementTasks.forEach(task -> task.run());
            });
        }

        // 初始化ip池
        var initiateElementIpPoolTasks = cloudElements.stream()
                .filter(cloudElement -> !getHandlerUtils().getElementHandler(cloudElement).deriveNetworks.isEmpty())
                .map(cloudElement -> (Runnable) () -> initiateElementIpPoolProcess(cloudElement))
                .collect(Collectors.toList());
        if (!initiateElementIpPoolTasks.isEmpty()) {
            runSceneProgress(SceneProcess.INITIATE_IP_POOL, () -> {
                initiateElementIpPoolTasks.forEach(tast -> tast.run());
            });
        }

        // 初始化端口, 先初始化路由端口, 再初始化其它端口
        List<Runnable> initiateRouterElementPortsTasks = new ArrayList<>();
        List<Runnable> initiateOtherElementPortsTasks = new ArrayList<>();
        cloudElements.forEach(cloudElement -> {
            Integer virtualType = cloudElement.getVirtualType();
            if (virtualType != null
                    && (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                    || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)
                    || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)
                    || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE))) {
                Runnable task = () -> initiateElementPortsProcess(cloudElement);
                Integer type = cloudElement.getSrcElement().getType();
                // 防火墙也看作路由
                if (type != null && (type.equals(BaseSceneElementModel.Type.ROUTER)
                        || type.equals(BaseSceneElementModel.Type.FIREWALL))) {
                    initiateRouterElementPortsTasks.add(task);
                } else {
                    initiateOtherElementPortsTasks.add(task);
                }
            }
        });
        if (!initiateRouterElementPortsTasks.isEmpty()) {
            runSceneProgress(SceneProcess.INITIATE_PORTS, () -> initiateRouterElementPortsTasks.forEach(task -> task.run()));
        }
        if (!initiateOtherElementPortsTasks.isEmpty()) {
            runSceneProgress(SceneProcess.INITIATE_PORTS, () -> runTasks(initiateOtherElementPortsTasks));
        }

        // 存在需要重新分配的ip，重新分配ip
        var requireReallocateIpPorts = getReallocateIpPorts();
        if (!requireReallocateIpPorts.isEmpty()) {
            runSceneProgress(SceneProcess.REALLOCATE_IPS, () -> {
                reallocateIpPorts(requireReallocateIpPorts);
            });
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
                () -> initiateElementPorts(cloudElement));
    }

    public String ip2Cidr24(String ip) {
        return ip.substring(0, ip.lastIndexOf(".")) + ".0/24";
    }

    private void loadProperties2Network(CloudPluginSceneElementNetworkBO network, CloudPluginSceneElementBO element) {
        network.setAllocationPools(element.getAllocationPools());
        network.setDnsNameservers(element.getDnsNameservers());
        network.setGateway(element.getGateway());
        network.setCidr(element.getCidr());
        network.setHostRoutes(element.getHostRoutes());
        network.setVlan(element.getVlan());
        network.setVlanId(element.getVlanId());
    }
}
