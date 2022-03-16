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
import org.apache.commons.collections4.CollectionUtils;
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
        var networks = cloudElement.getDeriveNetworks().stream().map(n -> n.network).collect(Collectors.toList());
        for (var network : networks) {
            // todo 外部网络
            network.setNetworkId(StringUtils.EMPTY);
            if (StringUtils.isEmpty(network.getCidr())) {
                prepareExistCidrs();
                network.setCidr(allocateCidrs(1).get(0));
            }
        }
        Integer virtualType = cloudElement.getVirtualType();
        for (var connectNetwork : cloudElement.getDeriveNetworks()) {
            if (!Boolean.TRUE.equals(connectNetwork.network.getVlan())) {
                // 是否vlan模式(是否连接了实体节点)
                var vlan = false;
                if (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
                    // 本节点是实体节点
                    vlan = true;
                } else {
                    // 连接节点有实体节点
                    for (var p : connectNetwork.getFromPorts()) {
                        var connectedCloudElement = getHandlerUtils().getPortConnectedOpaqueSrcElement(p.getId());
                        var pluginConnectedCloudElement = connectedCloudElement == null ? null : getPluginHandler().getElement(connectedCloudElement.getId());
                        Integer connectedVirtualType = pluginConnectedCloudElement == null ? null : pluginConnectedCloudElement.getVirtualType();
                        if (connectedVirtualType != null
                                && connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
                            vlan = true;
                            break;
                        }
                    }
                }
                connectNetwork.network.setVlan(vlan);

                // 是否开启辅助路由(是否未连接默认路由)
                var routerOn = true;
                if (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
                    // 本节点是默认路由
                    routerOn = false;
                } else {
                    for (var p : connectNetwork.getFromPorts()) {
                        var connectedCloudElement = getHandlerUtils().getPortConnectedOpaqueSrcElement(p.getId());
                        var pluginConnectedCloudElement = connectedCloudElement == null ? null : getPluginHandler().getElement(connectedCloudElement.getId());
                        Integer connectedVirtualType = pluginConnectedCloudElement == null ? null : pluginConnectedCloudElement.getVirtualType();
                        if (connectedVirtualType != null
                                && connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
                            routerOn = true;
                            break;
                        }
                    }
                }
                connectNetwork.network.setAuxiliaryNetworkRouterOn(true);
            }
        }
    }

    /**
     * 初始化辅助网络节点的ip池
     * @param element 辅助网络节点
     */
    private void initIpPool(CloudPluginSceneElementBO element) {
        element.getDeriveNetworks().forEach(connectedNetwork -> {
            CloudPluginSceneElementNetworkBO network = connectedNetwork.network;
            String cidr = network.getCidr();
            if (StringUtils.isEmpty(cidr)) {
                return;
            }
            Set<String> existIps = new HashSet<>();
            connectedNetwork.getFromPorts().forEach(port -> {
                if (StringUtils.isNotEmpty(port.getIp())) {
                    existIps.add(port.getIp());
                }
            });
            connectedNetwork.getToPorts().forEach(port -> {
                if (StringUtils.isNotEmpty(port.getIp())) {
                    existIps.add(port.getIp());
                }
            });

            // 处理网关
            if (StringUtils.isEmpty(network.getGateway())) {
                String firstPortIp = null;
                for (var p : connectedNetwork.getFromPorts()) {
                    if (StringUtils.isNotEmpty(p.getIp())) {
                        firstPortIp = p.getIp();
                        break;
                    }
                }
                if (firstPortIp == null) {
                    String gateway = null;
                    String possibleDhcpIp = null;
                    List<String> excludeIps = new ArrayList<>(existIps);
                    for (int i = 0; i < 5; i++) {
                        gateway = IpUtils.newAllocateDefaultRouterIps(network.getCidr(), 1, excludeIps, network.getAllocationPools()).get(0);
                        excludeIps.add(gateway);
                        possibleDhcpIp = IpUtils.nextIp(gateway);
                        if (!existIps.contains(possibleDhcpIp)) {
                            break;
                        }
                    }
                    network.setGateway(gateway);
                } else {
                    network.setGateway(firstPortIp);
                }
            }
            existIps.add(network.getGateway());
            existIps.add(IpUtils.nextIp(network.getGateway()));
            if (element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH || element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER) {
                connectedNetwork.getFromPorts().forEach(port -> port.setIp(network.getGateway()));
            }

            int requireCount = 0;
            int gatewayCount = 0;
            for (var port : connectedNetwork.getFromPorts()) {
                if (StringUtils.isEmpty(port.getIp())) {
                    requireCount++;
                }
            }
            for (var port : connectedNetwork.getToPorts()) {
                if (StringUtils.isEmpty(port.getIp())) {
                    Integer virtualType = getPluginHandler().getElement(port.getElement().getId()).getVirtualType();
                    if (virtualType == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH || virtualType == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER) {
                        gatewayCount++;
                    } else {
                        requireCount++;
                    }
                }
            }
            if (connectedNetwork.network.getAuxiliaryNetworkRouterOn()) {
                gatewayCount++;
            }
            List<String> gatewayIpPool = IpUtils.newAllocateDefaultRouterIps(network.getCidr(), gatewayCount, new ArrayList<>(existIps), network.getAllocationPools());
            if (connectedNetwork.network.getAuxiliaryNetworkRouterOn()) {
                connectedNetwork.network.setAuxiliaryNetworkRouterIp(gatewayIpPool.remove(0));
            }
            List<String> ipPool = IpUtils.randomIps(network.getCidr(), requireCount, new ArrayList<>(existIps), network.getAllocationPools(), BoolUtils.bool(network.getDhcp()));
            ipPool.addAll(gatewayIpPool);
            network.setIpPool(ipPool);
        });
    }

    /**
     * 初始化虚拟网络端口
     * @param cloudElement 网元节点
     */
    private void initiateVirtualPort(CloudPluginSceneElementBO cloudElement) {
        var connectedNetworks = cloudElement.getDeriveNetworks();
        for (ConnectedNetworkBO connectedNetwork : connectedNetworks) {
            List<String> ipPool = connectedNetwork.network.getIpPool();
            // 处理自己的端口
            List<SceneElementPortBO> fromPorts = connectedNetwork.getFromPorts();
            for (var port : fromPorts) {
                if (StringUtils.isNotEmpty(port.getIp())) {
                    continue;
                }
                port.setIp(ipPool.remove(0));
            }
            List<SceneElementPortBO> toPorts = connectedNetwork.getToPorts();
            for (var port : toPorts) {
                if (StringUtils.isNotEmpty(port.getIp())) {
                    continue;
                }
                var pluginElement = getPluginHandler().getElement(port.getElement().getId());
                Integer toVirtualType = pluginElement.getVirtualType();
                if (toVirtualType == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH || toVirtualType == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER) {
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

            var newIps = IpUtils.newRandomIps(srcPortAuxiliaryNetworkCloudElement.getAuxiliaryNetworkCidr(), 1,
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

    public InitiateHandler(CloudScenePluginHandler pluginHandler) {
        super(pluginHandler, InitiateTaskPool.getInstance(), SceneModel.Event.INITIATE);
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

            CloudPluginSceneElementNetworkBO network = new CloudPluginSceneElementNetworkBO(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
            network.setExternal(true);
            network.setNetworkId(CloudSingleton.cloudAppConfig.getExternalNetId());
            cloudElement.getDeriveNetworks().add(new ConnectedNetworkBO(network, getHandlerUtils()));
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
        // defaultConnectedNetwork
        var network = new CloudPluginSceneElementNetworkBO(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
        this.loadProperties2Network(network, cloudElement);
        ConnectedNetworkBO defaultConnectedNetwork = new ConnectedNetworkBO(network, getHandlerUtils());
        // 需要的网络
        var ports = cloudElement.getSrcElement().getPorts();
        Map<String, ConnectedNetworkBO> cidr2ConnectedNetwork = new HashMap<>();
        for (var port : ports) {
            var connectPort = getHandlerUtils().getPortConnectedOpaqueSrcPort(port.getId());
            if (connectPort != null) {
                // 有连线 且 对端端口已归属某个网络 则本端口接入即可
                CloudPluginSceneElementNetworkBO connectNetwork = getHandlerUtils().getNetworkByPort(connectPort);
                if (connectNetwork != null) {
                    ConnectedNetworkBO connectedNetwork = new ConnectedNetworkBO(connectNetwork, getHandlerUtils());
                    connectedNetwork.addPort(port, connectPort, false);
                    cloudElement.getAccessNetworks().add(connectedNetwork);
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
            if (StringUtils.isEmpty(cidrDivideIp)) {
                // 未设置ip值,使用默认网段的网络
                defaultConnectedNetwork.addPort(port, connectPort, true);
            } else {
                // 设置了ip值,根据ip值计算网段
                String cidr = this.ip2Cidr24(cidrDivideIp);
                if (cidr.equals(defaultConnectedNetwork.getNetwork().getCidr())) {
                    // 和默认网段相同,则也加入默认网络
                    defaultConnectedNetwork.addPort(port, connectPort, true);
                } else {
                    // 和默认网段不同,构建网络
                    ConnectedNetworkBO connectedNetwork = cidr2ConnectedNetwork.get(cidr);
                    if (connectedNetwork == null) {
                        var createNetwork = new CloudPluginSceneElementNetworkBO(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
                        createNetwork.setCidr(cidr);
                        connectedNetwork = new ConnectedNetworkBO(createNetwork, getHandlerUtils());
                        cidr2ConnectedNetwork.put(cidr, connectedNetwork);
                    }
                    connectedNetwork.addPort(port, connectPort, true);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(defaultConnectedNetwork.getFromPorts())) {
            cloudElement.getDeriveNetworks().add(defaultConnectedNetwork);
        }
        cloudElement.getDeriveNetworks().addAll(cidr2ConnectedNetwork.values());
        initiateAuxiliaryNetwork(cloudElement);
    }

    public void initCoreType(CloudPluginSceneElementBO element) {
        Integer role = element.getRole();
        CoreType coreType = CoreType.NETWORK;
        if (role != null) {
            if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY)
                    || role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK)
                    || role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH)
            ) {
                coreType = CoreType.NETWORK;
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER)) {
                coreType = CoreType.ROUTER;
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL)) {
                coreType = CoreType.FIREWALL;
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL)) {
                coreType = CoreType.TERMINAL;
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.NPC)) {
                coreType = CoreType.TERMINAL;
            }
        }
        element.setCoreType(coreType);
    }

    public void initiate() {
        prepareEventProcess(SceneModel.Event.INITIATE, SceneProcess.class, ElementProcess.class);

        var cloudElements = new ArrayList<>(getPluginHandler().getElementMap().values());
        cloudElements.forEach(cloudElement -> this.initCoreType(cloudElement));
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

        getPluginHandler().createElementNetworks();

        // 初始化ip池
        var initiateElementIpPoolTasks = cloudElements.stream()
                .filter(cloudElement -> !cloudElement.getDeriveNetworks().isEmpty())
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
            runSceneProgress(SceneProcess.INITIATE_PORTS, () -> initiateOtherElementPortsTasks.forEach(task -> task.run()));
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
