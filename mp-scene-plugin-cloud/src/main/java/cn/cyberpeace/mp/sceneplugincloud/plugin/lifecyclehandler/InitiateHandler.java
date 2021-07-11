package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.BoolUtils;
import cn.cyberpeace.mp.base.util.basic.MapUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.AllocationPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.CoreType;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.InitiateTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.util.ElementTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.hutool.core.net.Ipv4Util;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
        public static final EventProcess PORT_CHECK = new EventProcess("port_check", "sceneplugincloud.event.port_check");
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

    
    private boolean requireAuxiliaryNetwork(CloudPluginSceneElementBO cloudElement,
                                            CloudPluginSceneElementBO connectedCloudElement) {
        if (cloudElement == null) {
            return false;
        }

        
        if (connectedCloudElement == null) {
            
            if (getPluginHandler().getSceneHandler().elementHasConnectedPort(cloudElement.getSrcElement().getId())) {
                return false;
            } else {
                return true;
            }
        }

        
        if (cloudElement.getSrcElement().getId().equals(connectedCloudElement.getSrcElement().getId())) {
            return false;
        }

        
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
        
        if (virtualType == null || connectedVirtualType == null) {
            return false;
        }

        
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                && connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
            Integer type = cloudElement.getSrcElement().getType();
            Integer connectedType = connectedCloudElement.getSrcElement().getType();
            
            if (connectedType == null) {
                return true;
            }
            
            if (type == null) {
                return false;
            }

            
            if (type.equals(BaseSceneElementModel.Type.SWITCH)) {
                return true;
            }

            
            if (type.equals(connectedType) && !Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
                return true;
            }

            
            if (type.equals(BaseSceneElementModel.Type.ROUTER)) {
                
                if (connectedType.equals(BaseSceneElementModel.Type.SWITCH)) {
                    return false;
                } else {
                    return true;
                }
            }

            
            if (type.equals(BaseSceneElementModel.Type.FIREWALL)) {
                
                if (connectedType.equals(BaseSceneElementModel.Type.SWITCH)
                        || connectedType.equals(BaseSceneElementModel.Type.ROUTER)) {
                    return false;
                } else {
                    return true;
                }
            }

            
            if (Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
                return false;
            } else {
                return true;
            }
        }

        
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER) &&
                isTerminalElement(connectedVirtualType)) {
            return true;
        }

        
        if (isTerminalElement(virtualType) && isTerminalElement(connectedVirtualType)
                && !Boolean.TRUE.equals(connectedCloudElement.getAuxiliaryNetworkOn())) {
            return true;
        }

        return false;
    }

    
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

    
    private void initiateAuxiliaryNetwork(CloudPluginSceneElementBO cloudElement) {
        var networks = cloudElement.getDeriveNetworks().stream().map(n -> n.network).collect(Collectors.toList());
        for (var network : networks) {
            Integer role = cloudElement.getRole();
            if (Boolean.TRUE.equals(network.getExternal())) {
                
                continue;
            } else if (Boolean.TRUE.equals(cloudElement.getExternal()) && role != null && role == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY) {
                
                network.setResourceId(cloudElement.getResourceId());
                continue;
            } else if (getHandlerUtils().getCreatedNetworks().contains(network.getId())) {
                continue;
            }
            network.setResourceId(StringUtils.EMPTY);
            if (StringUtils.isEmpty(network.getCidr())) {
                prepareExistCidrs();
                network.setCidr(allocateCidrs(1).get(0));
            }
        }
        Integer virtualType = cloudElement.getVirtualType();
        for (var connectNetwork : cloudElement.getDeriveNetworks()) {
            if (!Boolean.TRUE.equals(connectNetwork.network.getVlan())) {
                
                var vlan = false;
                if (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
                    
                    vlan = true;
                } else {
                    
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
            }

            
            if (!Boolean.TRUE.equals(cloudElement.getAuxiliaryRouterOff())) {
                
                var routerOn = true;
                if (this.isGateway(cloudElement.getCoreType(), cloudElement.getVirtualType(), cloudElement.getSrcElement().getType())) {
                    routerOn = false;
                } else if (ElementTypeUtils.isEntityGateway(cloudElement)) {
                    routerOn = false;
                } else {
                    for (var p : connectNetwork.getFromPorts()) {
                        var connectedCloudElement = getHandlerUtils().getPortConnectedOpaqueSrcElement(p.getId());
                        var pluginConnectedCloudElement = connectedCloudElement == null ? null : getPluginHandler().getElement(connectedCloudElement.getId());
                        if (pluginConnectedCloudElement != null) {
                            if (this.isGateway(pluginConnectedCloudElement.getCoreType(), pluginConnectedCloudElement.getVirtualType(), pluginConnectedCloudElement.getSrcElement().getType())) {
                                routerOn = false;
                                break;
                            }
                        }
                    }
                }
                connectNetwork.network.setAuxiliaryNetworkRouterOn(routerOn);
            } else {
                connectNetwork.network.setAuxiliaryNetworkRouterOn(false);
            }
        }
    }

    private boolean isGateway(CoreType coreType, Integer virtualType, Integer type) {
        if (this.isRouter(coreType, virtualType)) {
            return true;
        } else if (type != null) {
            String gatewayTypes = CloudSingleton.cloudAppConfig.getGatewayTypes();
            if (StringUtils.isNotEmpty(gatewayTypes)) {
                String[] gatewayTypeStr = gatewayTypes.split(",");
                for (String gatewayType : gatewayTypeStr) {
                    if (type.toString().equals(gatewayType)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isRouter(CoreType coreType, Integer virtualType) {
        return (coreType != null && CoreType.isRouter(coreType)) || (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER));
    }

    
    private void initIpPool(CloudPluginSceneElementBO element) {
        element.getDeriveNetworks().forEach(connectedNetwork -> {
            CloudPluginSceneElementNetworkBO network = connectedNetwork.network;
            if (!network.cidrValidFlag()) {
                return;
            }

            Set<String> existIps = getNetworkExistIp(connectedNetwork);

            
            setNetworkGateway(connectedNetwork, element, existIps);
            existIps.add(network.getGateway());

            
            boolean dhcpOpenAndNeedMpAllocateDhcpFlag = Boolean.TRUE.equals(CloudSingleton.cloudAppConfig.getDhcpIpAllocate())
                    && !Boolean.FALSE.equals(element.getDhcp());

            boolean onlyVirtualRouterConnectFlag = onlyVirtualRouterConnectFlag(connectedNetwork);
            if (onlyVirtualRouterConnectFlag) {
                network.setDhcp(false);
            }

            if (dhcpOpenAndNeedMpAllocateDhcpFlag && !onlyVirtualRouterConnectFlag) {
                if (StringUtils.isEmpty(network.getDhcpIp())) {
                    List<String> ipPool = IpUtils.newRandomIps(network.getCidr(), 1, new ArrayList<>(existIps), new ArrayList<>(), BoolUtils.bool(element.getDhcp()));
                    if (CollectionUtils.isNotEmpty(ipPool)) {
                        network.setDhcpIp(ipPool.get(0));
                        existIps.add(ipPool.get(0));
                    }
                }
            }

            Set<String> portIps = new HashSet<>();
            int requireCount = 0;
            int gatewayCount = 0;
            
            String fromPortIp = null;
            for (var port : connectedNetwork.getFromPorts()) {
                if (StringUtils.isNotEmpty(port.getIp())) {
                    fromPortIp = port.getIp();
                    break;
                }
            }
            if (StringUtils.isNotEmpty(fromPortIp)) {
                
                for (var port : connectedNetwork.getFromPorts()) {
                    port.setIp(fromPortIp);
                }
            } else {
                
                if (ElementTypeUtils.isVirtualGateway(element)) {
                    gatewayCount++;
                } else {
                    requireCount++;
                }
            }
            for (var port : connectedNetwork.getToPorts()) {
                if (StringUtils.isEmpty(port.getIp())) {
                    if (ElementTypeUtils.isVirtualGateway(getPluginHandler().getElement(port.getElement().getId()))) {
                        gatewayCount++;
                    } else {
                        requireCount++;
                    }
                } else {
                    portIps.add(port.getIp());
                }
            }
            if (Boolean.TRUE.equals(connectedNetwork.network.getAuxiliaryNetworkRouterOn())) {
                gatewayCount++;
            }

            List<String> ipPool = IpUtils.newRandomIps(network.getCidr(), requireCount, new ArrayList<>(existIps), element.getAllocationPools(), false);
            boolean gatewayIpNotAllocatedFlag = false;
            if (!portIps.contains(network.getGateway())) {
                
                if (!Boolean.TRUE.equals(network.getGatewayOff())) {
                    gatewayCount--;
                    gatewayIpNotAllocatedFlag = true;
                }
            }
            List<String> gatewayIpPool = IpUtils.newAllocateDefaultRouterIps(network.getCidr(), gatewayCount, new ArrayList<>(existIps), element.getAllocationPools());

            if (Boolean.TRUE.equals(connectedNetwork.network.getAuxiliaryNetworkRouterOn())) {
                connectedNetwork.network.setAuxiliaryNetworkRouterIp(connectedNetwork.getNetwork().getGateway());
            }
            if (CollectionUtils.isNotEmpty(gatewayIpPool)) {
                existIps.addAll(gatewayIpPool);
            }
            ipPool.addAll(gatewayIpPool);
            if (gatewayIpNotAllocatedFlag) {
                ipPool.add(network.getGateway());
            }
            network.getExcloudIps().addAll(existIps);
            network.getExcloudIps().addAll(ipPool);
            network.setIpPool(ipPool);
            if (element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH) {
                connectedNetwork.getFromPorts().forEach(port -> port.setIp(network.getGateway()));
            }
        });
    }

    private Set<String> getNetworkExistIp(ConnectedNetworkBO connectedNetworkBO) {
        Set<String> existIps = new HashSet<>();
        connectedNetworkBO.getFromPorts().forEach(port -> {
            if (StringUtils.isNotEmpty(port.getIp())) {
                existIps.add(port.getIp());
            }
        });
        connectedNetworkBO.getToPorts().forEach(port -> {
            if (StringUtils.isNotEmpty(port.getIp())) {
                existIps.add(port.getIp());
            }
        });
        return existIps;
    }

    private boolean onlyVirtualRouterConnectFlag(ConnectedNetworkBO connectedNetworkBO) {
        List<SceneElementPortBO> fromPorts = connectedNetworkBO.getFromPorts();
        List<SceneElementPortBO> toPorts = connectedNetworkBO.getToPorts();

        if (CollectionUtils.isNotEmpty(fromPorts) && CollectionUtils.isNotEmpty(toPorts)) {
            if (fromPorts.size() == toPorts.size() && fromPorts.size() == 1) {
                if (ElementTypeUtils.isVirtualRouter(
                        getPluginHandler().getElement(fromPorts.get(0).getElement().getId())) &&
                        ElementTypeUtils.isVirtualRouter(
                                getPluginHandler().getElement(toPorts.get(0).getElement().getId())
                        )) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setNetworkGateway(ConnectedNetworkBO connectedNetworkBO, CloudPluginSceneElementBO element, Set<String> existIps) {

        CloudPluginSceneElementNetworkBO network = connectedNetworkBO.network;
        if (StringUtils.isEmpty(network.getGateway())) {
            String gatewayIp = null;

            
            if (elementIsGatewayTypeFlag(element)) {
                for (var p : connectedNetworkBO.getFromPorts()) {
                    if (StringUtils.isNotEmpty(p.getIp())) {
                        gatewayIp = p.getIp();
                        break;
                    }
                }
            }

            if (StringUtils.isEmpty(gatewayIp)) {
                for (SceneElementPortBO toPort : connectedNetworkBO.getToPorts()) {
                    if (StringUtils.isNotEmpty(toPort.getIp())) {
                        CloudPluginSceneElementBO cloudPluginSceneElementBO =
                                getPluginHandler().getElement(toPort.getElement().getId());
                        if (elementIsGatewayTypeFlag(cloudPluginSceneElementBO)) {
                            gatewayIp = toPort.getIp();
                            break;
                        }
                    }

                }
            }

            
            if (StringUtils.isEmpty(gatewayIp)) {
                List<String> excludeIps = new ArrayList<>(existIps);

                gatewayIp = IpUtils.newAllocateDefaultRouterIps(network.getCidr(), 1,
                                                                excludeIps, element.getAllocationPools()).get(0);

            }

            connectedNetworkBO.network.setGateway(gatewayIp);
        }
    }

    private boolean elementIsGatewayTypeFlag(CloudPluginSceneElementBO elementBO) {
        return ElementTypeUtils.isVirtualGateway(elementBO)
                || ElementTypeUtils.isCoreRouter(elementBO) || ElementTypeUtils.isEntityGateway(elementBO);
    }

    
    private void initiateVirtualPort(CloudPluginSceneElementBO cloudElement) {
        var connectedNetworks = cloudElement.getDeriveNetworks();
        for (ConnectedNetworkBO connectedNetwork : connectedNetworks) {
            List<String> ipPool = connectedNetwork.network.getIpPool();
            
            List<SceneElementPortBO> toPorts = connectedNetwork.getToPorts();
            for (var port : toPorts) {
                if (StringUtils.isNotEmpty(port.getIp())) {
                    continue;
                }
                var pluginElement = getPluginHandler().getElement(port.getElement().getId());
                if (ElementTypeUtils.isVirtualGateway(pluginElement)) {
                    synchronized (ipPool) {
                        port.setIp(ipPool.remove(ipPool.size() - 1));
                    }
                } else {
                    synchronized (ipPool) {
                        port.setIp(ipPool.remove(0));
                    }
                }
            }

            
            List<ConnectedNetworkBO.ConnectPort> connectPorts = connectedNetwork.connectPorts;
            String fromIp = null;
            boolean noConnect = true;
            for (var connectPort : connectPorts) {
                var port = connectPort.from;
                if (connectPort.to != null) {
                    noConnect = false;
                }
                if (StringUtils.isNotEmpty(port.getIp())) {
                    continue;
                } else {
                    if (fromIp == null) {
                        var pluginElement = getPluginHandler().getElement(port.getElement().getId());
                        if (ElementTypeUtils.isVirtualGateway(pluginElement) || this.isCoreRouter(cloudElement)) {
                            synchronized (ipPool) {
                                if (fromIp == null) {
                                    fromIp = ipPool.remove(ipPool.size() - 1);
                                }
                            }
                        } else {
                            synchronized (ipPool) {
                                if (fromIp == null) {
                                    fromIp = ipPool.remove(0);
                                }
                            }
                        }
                    }
                    if (connectPort.to != null) {
                        port.setIp(fromIp);
                    }
                }
            }
            if (noConnect) {
                
                if (ElementTypeUtils.isVirtualGateway(cloudElement)) {
                    
                    for (var port : connectedNetwork.getFromPorts()) {
                        if (StringUtils.isEmpty(port.getIp())) {
                            port.setIp(fromIp);
                        }
                    }
                } else {
                    
                    for (var port : connectedNetwork.getFromPorts()) {
                        if (StringUtils.isEmpty(port.getIp())) {
                            port.setIp(fromIp);
                            break;
                        }
                    }
                }
            }
            for (var port : connectedNetwork.getFromPorts()) {
                if (StringUtils.isEmpty(port.getIp())) {
                    port.setUsing(false);
                }
            }
        }
    }

    private void initiateVirtualToPort(CloudPluginSceneElementBO cloudElement) {
        var connectedNetworks = cloudElement.getDeriveNetworks();
        for (ConnectedNetworkBO connectedNetwork : connectedNetworks) {
            List<String> ipPool = connectedNetwork.network.getIpPool();
            
            List<SceneElementPortBO> toPorts = connectedNetwork.getToPorts();
            for (var port : toPorts) {
                if (StringUtils.isNotEmpty(port.getIp())) {
                    continue;
                }
                var pluginElement = getPluginHandler().getElement(port.getElement().getId());
                if (ElementTypeUtils.isVirtualGateway(pluginElement)) {
                    synchronized (ipPool) {
                        if (ipPool.size() > 0) {
                            port.setIp(ipPool.remove(ipPool.size() - 1));
                        } else {
                            log.error("failed to get ip from ip pool , port eid:{}", port.getEid());
                        }
                    }
                } else {
                    synchronized (ipPool) {
                        if (ipPool.size() > 0) {
                            port.setIp(ipPool.remove(0));
                        } else {
                            log.error("failed to get ip from ip pool , port eid:{}", port.getEid());
                        }
                    }
                }
            }
        }
    }

    
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

    
    private void initiateElementPorts(CloudPluginSceneElementBO cloudElement) {
        var srcPorts = cloudElement.getSrcElement().getPorts();
        if (srcPorts == null) {
            return;
        }

        Integer virtualType = cloudElement.getVirtualType();
        var isVirtualElement = ElementTypeUtils.isVirtualNode(cloudElement);

        for (var srcPort : srcPorts) {
            var cloudPort = getPluginHandler().getPort(srcPort.getId());
            if (cloudPort != null) {
                
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
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
            
            initiateVirtualToPort(cloudElement);
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
            
            if (cloudElement.getRole() != null && cloudElement.getRole() != BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK) {
                initiateVirtualToPort(cloudElement);
            }
        }
    }

    public InitiateHandler(CloudScenePluginHandler pluginHandler) {
        super(pluginHandler, InitiateTaskPool.getInstance(), SceneModel.Event.INITIATE);
    }

    
    protected void initiateElementBaseProperties(CloudPluginSceneElementBO cloudElement) {
        
        var role = cloudElement.getRole();
        if (role != null && role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK)) {
            cloudElement.setExternal(true);
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH);
            cloudElement.setAuxiliaryNetworkOn(true);
            cloudElement.setAuxiliaryNetworkId(CloudSingleton.cloudAppConfig.getExternalNetId());

            CloudPluginSceneElementNetworkBO network = new CloudPluginSceneElementNetworkBO(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
            network.setExternal(true);
            network.setResourceId(CloudSingleton.cloudAppConfig.getExternalNetId());
            var connectNetwork = new ConnectedNetworkBO(network, getHandlerUtils());
            var ports = Optional
                    .ofNullable(cloudElement.getSrcElement().getPorts())
                    .orElse(new ArrayList<>());
            for (var port : ports) {
                var connectPort = getHandlerUtils().getPortConnectedOpaqueSrcPort(port.getId());
                connectNetwork.addPort(port, connectPort, true);
            }
            cloudElement.getDeriveNetworks().add(connectNetwork);
        } else if (Boolean.TRUE.equals(cloudElement.getExternal()) && role != null && role == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY) {
            CloudPluginSceneElementNetworkBO network = new CloudPluginSceneElementNetworkBO(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
            this.loadProperties2Network(network, cloudElement);
            network.setExternal(true);
            network.setResourceId(cloudElement.getResourceId());
            var connectNetwork = new ConnectedNetworkBO(network, getHandlerUtils());
            var ports = Optional
                    .ofNullable(cloudElement.getSrcElement().getPorts())
                    .orElse(new ArrayList<>());
            for (var port : ports) {
                var connectPort = getHandlerUtils().getPortConnectedOpaqueSrcPort(port.getId());
                connectNetwork.addPort(port, connectPort, true);
            }
            cloudElement.getDeriveNetworks().add(connectNetwork);
        }

        if (Boolean.TRUE.equals(cloudElement.getExternal()) && !ElementTypeUtils.isExternalTerminal(cloudElement)) {
            return;
        }

        Integer virtualType = cloudElement.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", cloudElement.getSrcElement().getEid(),
                    cloudElement.getSrcElement().getId());
            return;
        }

        
        if (ElementTypeUtils.isCoreRouter(cloudElement)) {
            if (CollectionUtils.isNotEmpty(cloudElement.getSrcElement().getPorts())) {
                cloudElement.getSrcElement().getPorts().forEach(p -> {
                    if (StringUtils.isNotEmpty(p.getIp())) {
                        return;
                    }
                    var pluginPort = getPluginHandler().getPort(p.getId());
                    if (pluginPort == null) {
                        return;
                    }
                    if (StringUtils.isNotEmpty(pluginPort.getOspfIp())) {
                        p.setIp(pluginPort.getOspfIp().split("/")[0]);
                    } else if (StringUtils.isNotEmpty(pluginPort.getBgpIp())) {
                        p.setIp(pluginPort.getBgpIp().split("/")[0]);
                    } else if (StringUtils.isNotEmpty(pluginPort.getRipIp())) {
                        p.setIp(pluginPort.getRipIp().split("/")[0]);
                    }
                });
            }
        }

        
        if (!Boolean.TRUE.equals(cloudElement.getExternal())) {
            if (this.resetResourceId()) {
                cloudElement.setResourceId(StringUtils.EMPTY);
            }
        }
        
        var network = new CloudPluginSceneElementNetworkBO(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
        this.loadProperties2Network(network, cloudElement);
        ConnectedNetworkBO defaultConnectedNetwork = new ConnectedNetworkBO(network, getHandlerUtils());
        
        var ports = Optional
                .ofNullable(cloudElement.getSrcElement().getPorts())
                .orElse(new ArrayList<>());

        
        boolean defaultNetwordIsDerive = true;
        for (var port : ports) {
            var connectPort = getHandlerUtils().getPortConnectedOpaqueSrcPort(port.getId());
            if (connectPort != null) {
                
                CloudPluginSceneElementNetworkBO connectNetwork = getHandlerUtils().getNetworkByPort(connectPort);
                if (connectNetwork != null) {
                    if (StringUtils.equals(connectNetwork.getCidr(), defaultConnectedNetwork.getNetwork().getCidr())) {
                        defaultConnectedNetwork = new ConnectedNetworkBO(connectNetwork, getHandlerUtils());
                        defaultNetwordIsDerive = false;
                    }
                }
            }
        }

        Map<String, ConnectedNetworkBO> cidr2ConnectedNetwork = new HashMap<>();
        List<ConnectedNetworkBO> noCidrNetwork = new ArrayList<>();
        for (var port : ports) {
            var connectPort = getHandlerUtils().getPortConnectedOpaqueSrcPort(port.getId());
            if (connectPort != null) {
                
                CloudPluginSceneElementNetworkBO connectNetwork = getHandlerUtils().getNetworkByPort(connectPort);
                if (connectNetwork != null) {
                    ConnectedNetworkBO connectedNetwork = new ConnectedNetworkBO(connectNetwork, getHandlerUtils());
                    connectedNetwork.addPort(port, connectPort, false);
                    cloudElement.getAccessNetworks().add(connectedNetwork);
                    continue;
                }
            }
            
            String cidrDivideIp = getConnectCidrDivideIp(port, connectPort);
            String connectCidrByManualSettingInPort = getConnectCidrByManualSettingInPort(port, connectPort);
            boolean connectCidrNotManualSetFlag = StringUtils.isBlank(cidrDivideIp) && StringUtils.isBlank(connectCidrByManualSettingInPort);
            if (connectCidrNotManualSetFlag) {
                
                boolean isCoreRouter = this.isCoreRouter(cloudElement);
                boolean isDynamips = this.isDynamips(cloudElement);
                if (isCoreRouter) {
                    
                    var createNetwork = new CloudPluginSceneElementNetworkBO(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
                    var connectedNetwork = new ConnectedNetworkBO(createNetwork, getHandlerUtils());
                    connectedNetwork.addPort(port, connectPort, true);
                    noCidrNetwork.add(connectedNetwork);
                } else if (isDynamips && connectPort != null) {
                    
                    var createNetwork = new CloudPluginSceneElementNetworkBO(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
                    var connectedNetwork = new ConnectedNetworkBO(createNetwork, getHandlerUtils());
                    connectedNetwork.addPort(port, connectPort, true);
                    noCidrNetwork.add(connectedNetwork);
                } else {
                    
                    defaultConnectedNetwork.addPort(port, connectPort, true);
                }
            } else {
                String cidr;
                if (StringUtils.isEmpty(connectCidrByManualSettingInPort)) {
                    cidr = this.ip2Cidr24(cidrDivideIp);
                } else {
                    cidr = connectCidrByManualSettingInPort;
                }
                if (cidr.equals(defaultConnectedNetwork.getNetwork().getCidr())) {
                    
                    defaultConnectedNetwork.addPort(port, connectPort, true);
                } else if (this.isInCidr(defaultConnectedNetwork.getNetwork().getCidr(), cidrDivideIp)) {
                    defaultConnectedNetwork.addPort(port, connectPort, true);
                } else {
                    
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
        if (this.isCoreRouter(cloudElement)) {
            
            if (CollectionUtils.isNotEmpty(defaultConnectedNetwork.getFromPorts())) {
                if (defaultNetwordIsDerive) {
                    cloudElement.getDeriveNetworks().add(defaultConnectedNetwork);
                }
            }
            cloudElement.getDeriveNetworks().addAll(cidr2ConnectedNetwork.values());
            cloudElement.getDeriveNetworks().addAll(noCidrNetwork);
        } else {
            
            List<ConnectedNetworkBO> deriveNetworks = new ArrayList<>();
            for (ConnectedNetworkBO n : noCidrNetwork) {
                if (CollectionUtils.isNotEmpty(n.getToPorts())) {
                    deriveNetworks.add(n);
                }
            }
            for (ConnectedNetworkBO n : cidr2ConnectedNetwork.values()) {
                if (CollectionUtils.isNotEmpty(n.getToPorts())) {
                    deriveNetworks.add(n);
                }
            }
            if (CollectionUtils.isNotEmpty(defaultConnectedNetwork.getToPorts())) {
                if (defaultNetwordIsDerive) {
                    deriveNetworks.add(defaultConnectedNetwork);
                }
            }
            
            if (CollectionUtils.isEmpty(cloudElement.getAccessNetworks()) && CollectionUtils.isEmpty(deriveNetworks)) {
                if (CollectionUtils.isNotEmpty(defaultConnectedNetwork.getFromPorts())) {
                    if (defaultNetwordIsDerive) {
                        deriveNetworks.add(defaultConnectedNetwork);
                    }
                }
                
                if (CollectionUtils.isEmpty(deriveNetworks) && cloudElement.getVirtualType() != null &&
                        (
                                cloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE
                                        || cloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER
                        )
                ) {
                    for (ConnectedNetworkBO n : cidr2ConnectedNetwork.values()) {
                        if (CollectionUtils.isNotEmpty(n.getFromPorts())) {
                            deriveNetworks.add(n);
                            break;
                        }
                    }
                }
            }
            cloudElement.setDeriveNetworks(deriveNetworks);
        }

        initiateAuxiliaryNetwork(cloudElement);
    }

    private boolean isCoreRouter(CloudPluginSceneElementBO cloudElement) {
        boolean isCoreRouter = (cloudElement.getRole() != null && cloudElement.getRole().equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER))
                && BaseCloudPluginSceneElementModel.ImageType.VM.equals(cloudElement.getImageType());
        return isCoreRouter;
    }

    private boolean isDynamips(CloudPluginSceneElementBO cloudElement) {
        boolean isDynamips = (BaseCloudPluginSceneElementModel.ImageType.DYNAMIPS.equals(cloudElement.getImageType())
                && cloudElement.getCoreType() != null
                && !cloudElement.getCoreType().equals(CoreType.TERMINAL));
        return isDynamips;
    }

    public void initCoreType(CloudPluginSceneElementBO element) {
        Integer role = element.getRole();
        CoreType coreType = CoreType.NETWORK;
        if (role != null) {
            if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY)
                    || role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK)
            ) {
                coreType = CoreType.NETWORK;
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH)) {
                if (BaseCloudPluginSceneElementModel.ImageType.ENTITY.equals(element.getImageType())) {
                    coreType = CoreType.NETWORK_0;
                } else if (BaseCloudPluginSceneElementModel.ImageType.SIMPLE.equals(element.getImageType())) {
                    coreType = CoreType.NETWORK;
                } else {
                    coreType = CoreType.NETWORK_L2;
                }
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER)) {
                if (BaseCloudPluginSceneElementModel.ImageType.ENTITY.equals(element.getImageType())) {
                    coreType = CoreType.NETWORK_0;
                } else if (BaseCloudPluginSceneElementModel.ImageType.SIMPLE.equals(element.getImageType())) {
                    coreType = CoreType.ROUTER;
                } else {
                    coreType = CoreType.ROUTER_L2;
                }
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL)) {
                if (BaseCloudPluginSceneElementModel.ImageType.ENTITY.equals(element.getImageType())) {
                    coreType = CoreType.NETWORK_0;
                } else {
                    coreType = CoreType.FIREWALL;
                }
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL)) {
                coreType = CoreType.TERMINAL;
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.NPC)) {
                coreType = CoreType.TERMINAL;
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.ENTITY_TERMINAL)) {
                coreType = CoreType.TERMINAL;
            }
        }
        element.setCoreType(coreType);
    }

    public void initiate() {
        this.prepareEventProcess();

        getPluginHandler().getSceneHandler().addProgressPercentage(5);

        
        if (getPluginHandler().getElementPortMap() != null) {
            getPluginHandler().getElementPortMap().values().forEach(p -> {
                if (p.getSrcPort() != null && StringUtils.isNotEmpty(p.getSrcPort().getIp())) {
                    getHandlerUtils().addManualConfigIpPort(p.getSrcPort());
                }
            });
        }

        List<CloudPluginSceneElementBO> allCloudElements = new ArrayList<>(getPluginHandler().getElementMap().values())
                .stream()
                .collect(Collectors.toList());

        
        
        Collections.sort(allCloudElements, (a, b) -> {
            if (a.getSrcElement().getName() == null || b.getSrcElement().getName() == null) {
                return 0;
            }
            return a.getSrcElement().getName().compareTo(b.getSrcElement().getName());
        });

        allCloudElements.forEach(cloudElement -> this.initCoreType(cloudElement));
        
        Collections.sort(allCloudElements, Comparator.comparing(CloudPluginSceneElementBO::getCoreType));

        this.afterOrderElements(allCloudElements);

        List<CloudPluginSceneElementBO> cloudElements = new ArrayList<>();
        List<CloudPluginSceneElementBO> noEventElements = new ArrayList<>();
        for (var e : allCloudElements) {
            if (getPluginHandler().getSceneHandler().isHandleNode(e.getSrcElement().getId())) {
                cloudElements.add(e);
            } else {
                noEventElements.add(e);
            }
        }

        
        var initiateElementTasks = cloudElements.stream()
                .map(cloudElement -> (Runnable) () -> initiateElementBasePropertiesProcess(cloudElement))
                .collect(Collectors.toList());
        if (!initiateElementTasks.isEmpty()) {
            runSceneProgress(SceneProcess.INITIATE_BASE_PROPERTIES, () -> {
                initiateElementTasks.forEach(task -> task.run());
            });
        }

        getPluginHandler().createElementNetworks();

        
        var initiateElementIpPoolTasks = cloudElements.stream()
                .filter(cloudElement -> !cloudElement.getDeriveNetworks().isEmpty())
                .map(cloudElement -> (Runnable) () -> initiateElementIpPoolProcess(cloudElement))
                .collect(Collectors.toList());
        if (!initiateElementIpPoolTasks.isEmpty()) {
            runSceneProgress(SceneProcess.INITIATE_IP_POOL, () -> {
                initiateElementIpPoolTasks.forEach(task -> task.run());
            });
        }

        
        List<Runnable> initiateRouterElementPortsTasks = new ArrayList<>();
        List<Runnable> initiateOtherElementPortsTasks = new ArrayList<>();
        cloudElements
                .stream()
                .forEach(cloudElement -> {
                    Integer virtualType = cloudElement.getVirtualType();
                    if (virtualType != null
                            && (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                            || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)
                            || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)
                            || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)
                            || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)
                            || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS))) {
                        Runnable task = () -> initiateElementPortsProcess(cloudElement);
                        Integer type = cloudElement.getSrcElement().getType();
                        
                        if (type != null && (type.equals(BaseSceneElementModel.Type.ROUTER)
                                || type.equals(BaseSceneElementModel.Type.FIREWALL))) {
                            initiateRouterElementPortsTasks.add(task);
                        } else {
                            initiateOtherElementPortsTasks.add(task);
                        }
                    } else if (Boolean.TRUE.equals(cloudElement.getSrcElement().getIsAuxiliary())) {
                        
                        initiateOtherElementPortsTasks.add(() -> initiateVirtualPort(cloudElement));
                    }
                });
        if (!initiateRouterElementPortsTasks.isEmpty()) {
            runSceneProgress(SceneProcess.INITIATE_PORTS, () -> runTasks(initiateRouterElementPortsTasks));
        }
        if (!initiateOtherElementPortsTasks.isEmpty()) {
            runSceneProgress(SceneProcess.INITIATE_PORTS, () -> runTasks(initiateOtherElementPortsTasks));
        }

        







        
        List<CloudPluginSceneElementBO> networkElements = cloudElements.stream()
                .filter(c -> CollectionUtils.isNotEmpty(c.getDeriveNetworks()))
                .collect(Collectors.toList());
        if (!Boolean.TRUE.equals(CloudSingleton.cloudAppConfig.getEnableReallocateIpPorts())) {
            if (CollectionUtils.isNotEmpty(networkElements)) {
                runSceneProgress(SceneProcess.PORT_CHECK, () -> {
                    portCheck(networkElements);
                });
            }
        }
        if (CollectionUtils.isNotEmpty(networkElements)) {
            runSceneProgress(SceneProcess.REALLOCATE_IPS, () -> {
                reallocateIpPorts(networkElements);
            });
        }

        
        for (var element : cloudElements) {
            Integer virtualType = element.getVirtualType();
            if (virtualType == null) {
                continue;
            }
            if (virtualType == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE ||
                    virtualType == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER ||
                    virtualType == BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE) {
                List<ConnectedNetworkBO> allnets = new ArrayList<>();
                allnets.addAll(element.getAccessNetworks());
                allnets.addAll(element.getDeriveNetworks());
                for (var net : allnets) {
                    if (Boolean.TRUE.equals(net.getNetwork().getExternal()) || StringUtils.isEmpty(net.getNetwork().getCidr())) {
                        continue;
                    }
                    
                    if (CollectionUtils.isNotEmpty(net.getFromPorts())) {
                        var srcPort = net.getFromPorts().get(0);
                        var cloudPort = getPluginHandler().getPort(srcPort.getId());
                        if (cloudPort != null) {
                            if (CollectionUtils.isNotEmpty(cloudPort.getAccessModes())) {
                                List<String> randomIps = new ArrayList<>();

                                for (var accessMode : cloudPort.getAccessModes()) {
                                    
                                    if (!AccessMode.Protocol.CONSOLE.equals(accessMode.getProtocol()) && Boolean.TRUE.equals(accessMode.getProxy())) {
                                        if (CollectionUtils.isEmpty(randomIps)) {
                                            try {
                                                randomIps = IpUtils.newRandomIps(net.getNetwork().getCidr(), 1, new ArrayList<>(net.getNetwork().getExcloudIps()), new ArrayList<>(), false);
                                            } catch (MpRuntimeException e) {
                                                log.info("failed to allocate ip for fwdnsInterfaceIp , element is {}",srcPort.getElement().getId());
                                            }
                                        }

                                        if (CollectionUtils.isNotEmpty(randomIps)) {
                                            cloudPort.setDistributedPortMappingIp(randomIps.get(0));
                                            getHandlerUtils().addFwdnsInterfaceIp(element.getSrcElement().getId(), net.getNetwork().getId(), randomIps.get(0));
                                            net.getNetwork().getExcloudIps().add(randomIps.get(0));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        List<String> randomIps = IpUtils.newRandomIps(net.getNetwork().getCidr(), 1, new ArrayList<>(net.getNetwork().getExcloudIps()), new ArrayList<>(), false);

                        if (CollectionUtils.isNotEmpty(randomIps)) {
                            getHandlerUtils().addFwdnsInterfaceIp(element.getSrcElement().getId(), net.getNetwork().getId(), randomIps.get(0));
                            net.getNetwork().getExcloudIps().add(randomIps.get(0));
                        }
                    }
                }
            }
        }
        this.initiateForSingleNode(noEventElements);
    }

    public void initiateForSingleNode(List<CloudPluginSceneElementBO> noEventElements) {
        var cloudElements = noEventElements
                .stream()
                .filter(e -> {
                    var connectElements = getHandlerUtils().getEleConnectedOpaqueSrcElements(e.getSrcElement().getId());
                    if (CollectionUtils.isEmpty(connectElements)) {
                        return true;
                    } else {
                        boolean hasConnectNet = false;
                        for (var connectElement : connectElements) {
                            var cloudElement = getPluginHandler().getElement(connectElement.getId());
                            if (cloudElement != null && CoreType.TERMINAL != cloudElement.getCoreType()) {
                                hasConnectNet = true;
                            }
                        }
                        return !hasConnectNet;
                    }
                })
                .collect(Collectors.toList());

        
        
        Collections.sort(cloudElements, (a, b) -> {
            if (a.getSrcElement().getName() == null || b.getSrcElement().getName() == null) {
                return 0;
            }
            return a.getSrcElement().getName().compareTo(b.getSrcElement().getName());
        });

        cloudElements.forEach(cloudElement -> this.initCoreType(cloudElement));
        
        Collections.sort(cloudElements, Comparator.comparing(CloudPluginSceneElementBO::getCoreType));

        
        var initiateElementTasks = cloudElements.stream()
                .map(cloudElement -> (Runnable) () -> initiateElementBaseProperties(cloudElement))
                .collect(Collectors.toList());
        if (!initiateElementTasks.isEmpty()) {
            initiateElementTasks.forEach(task -> task.run());
        }

        try {
            getPluginHandler().createElementNetworks();
        } catch (Exception e) {
            log.error("failed to create network ", e);
        }

        
        var initiateElementIpPoolTasks = cloudElements.stream()
                .filter(cloudElement -> !cloudElement.getDeriveNetworks().isEmpty())
                .map(cloudElement -> (Runnable) () -> initIpPool(cloudElement))
                .collect(Collectors.toList());
        if (!initiateElementIpPoolTasks.isEmpty()) {
            initiateElementIpPoolTasks.forEach(task -> task.run());
        }

        
        List<Runnable> initiateRouterElementPortsTasks = new ArrayList<>();
        List<Runnable> initiateOtherElementPortsTasks = new ArrayList<>();
        cloudElements
                .stream()
                .forEach(cloudElement -> {
                    Integer virtualType = cloudElement.getVirtualType();
                    if (virtualType != null
                            && (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                            || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)
                            || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)
                            || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)
                            || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH))) {
                        Runnable task = () -> initiateElementPorts(cloudElement);
                        Integer type = cloudElement.getSrcElement().getType();
                        
                        if (type != null && (type.equals(BaseSceneElementModel.Type.ROUTER)
                                || type.equals(BaseSceneElementModel.Type.FIREWALL))) {
                            initiateRouterElementPortsTasks.add(task);
                        } else {
                            initiateOtherElementPortsTasks.add(task);
                        }
                    } else if (Boolean.TRUE.equals(cloudElement.getSrcElement().getIsAuxiliary())) {
                        
                        initiateOtherElementPortsTasks.add(() -> initiateVirtualPort(cloudElement));
                    }
                });
        if (!initiateRouterElementPortsTasks.isEmpty()) {
            runTasks(initiateRouterElementPortsTasks);
        }
        if (!initiateOtherElementPortsTasks.isEmpty()) {
            runTasks(initiateOtherElementPortsTasks);
        }

        
        List<CloudPluginSceneElementBO> networkElements = cloudElements.stream()
                .filter(c -> CollectionUtils.isNotEmpty(c.getDeriveNetworks()))
                .collect(Collectors.toList());
        if (!Boolean.TRUE.equals(CloudSingleton.cloudAppConfig.getEnableReallocateIpPorts())) {
            if (CollectionUtils.isNotEmpty(networkElements)) {
                portCheck(networkElements);
            }
        }
        if (CollectionUtils.isNotEmpty(networkElements)) {
            reallocateIpPorts(networkElements);
        }

        
        for (var element : cloudElements) {
            Integer virtualType = element.getVirtualType();
            if (virtualType == null) {
                continue;
            }
            if (virtualType == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE ||
                    virtualType == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER ||
                    virtualType == BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE) {
                List<ConnectedNetworkBO> allnets = new ArrayList<>();
                allnets.addAll(element.getAccessNetworks());
                allnets.addAll(element.getDeriveNetworks());
                for (var net : allnets) {
                    if (Boolean.TRUE.equals(net.getNetwork().getExternal()) || StringUtils.isEmpty(net.getNetwork().getCidr())) {
                        continue;
                    }
                    List<String> randomIps = IpUtils.newRandomIps(net.getNetwork().getCidr(), 1, new ArrayList<>(net.getNetwork().getExcloudIps()), new ArrayList<>(), true);
                    if (CollectionUtils.isNotEmpty(randomIps)) {
                        
                        String distributedPortMappingIp = randomIps.get(0);
                        if (CollectionUtils.isNotEmpty(net.getFromPorts())) {
                            var srcPort = net.getFromPorts().get(0);
                            var cloudPort = getPluginHandler().getPort(srcPort.getId());
                            if (cloudPort != null) {
                                if (CollectionUtils.isNotEmpty(cloudPort.getAccessModes())) {
                                    for (var accessMode : cloudPort.getAccessModes()) {
                                        
                                        if (!AccessMode.Protocol.CONSOLE.equals(accessMode.getProtocol()) && Boolean.TRUE.equals(accessMode.getProxy())) {
                                            cloudPort.setDistributedPortMappingIp(distributedPortMappingIp);
                                            getHandlerUtils().addFwdnsInterfaceIp(element.getSrcElement().getId(), net.getNetwork().getId(), distributedPortMappingIp);
                                            net.getNetwork().getExcloudIps().add(distributedPortMappingIp);
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            getHandlerUtils().addFwdnsInterfaceIp(element.getSrcElement().getId(), net.getNetwork().getId(), distributedPortMappingIp);
                            net.getNetwork().getExcloudIps().add(distributedPortMappingIp);
                        }
                    }
                }
            }
        }
    }

    protected void prepareEventProcess() {
        prepareEventProcess(SceneModel.Event.INITIATE, SceneProcess.class, ElementProcess.class);
    }

    protected boolean resetResourceId() {
        return true;
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
        network.setGatewayOff(element.getGatewayOff());
        network.setCidr(element.getCidr());
        network.setHostRoutes(element.getHostRoutes());
        network.setVlan(element.getVlan());
        network.setVlanId(element.getVlanId());
        network.setDhcp(element.getDhcp());
        network.setNetMode(element.getNetMode());
    }

    protected void afterOrderElements(List<CloudPluginSceneElementBO> allElements) {

    }

    private void reallocateIpPorts(List<CloudPluginSceneElementBO> networkElements) {
        for (CloudPluginSceneElementBO element : networkElements) {
            for (ConnectedNetworkBO network : element.getDeriveNetworks()) {
                try {
                    if (Boolean.TRUE.equals(network.getNetwork().getExternal())) {
                        continue;
                    }
                    List<SceneElementPortBO> ports = new ArrayList<>();
                    ports.addAll(network.getFromPorts());
                    ports.addAll(network.getToPorts());
                    if (CollectionUtils.isEmpty(ports)) {
                        continue;
                    }
                    List<String> possibleDhcpIps = this.getPossibleDhcpIps(network.getNetwork().getGateway(), network.getNetwork().getAllocationPools(),network.getNetwork().getCidr());
                    if (Boolean.TRUE.equals(CloudSingleton.cloudAppConfig.getDhcpIpAllocate())) {
                        possibleDhcpIps = new ArrayList<>();
                    }
                    Map<String, String> changeIp = new HashMap<>();
                    List<String> excludeIps = new ArrayList<>(network.getNetwork().getExcloudIps());
                    excludeIps.addAll(possibleDhcpIps);
                    for (var port : ports) {
                        if (StringUtils.isNotEmpty(port.getIp()) && possibleDhcpIps.contains(port.getIp())) {
                            
                            String newIp = changeIp.get(port.getIp());
                            if (StringUtils.isEmpty(newIp)) {
                                var portElement = getPluginHandler().getElement(port.getElement().getId());
                                if (portElement != null) {
                                    newIp = this.getNewReallocateIp(portElement, network, excludeIps);
                                }
                            }
                            if (StringUtils.isNotEmpty(newIp)) {
                                log.info("Port {} reallocate ip {} to {}", port.getId(), port.getIp(), newIp);
                                changeIp.put(port.getIp(), newIp);
                                excludeIps.add(newIp);
                                port.setIp(newIp);
                            }
                        }
                    }
                    if (ElementTypeUtils.isCoreRouter(element)) {
                        
                        if (CollectionUtils.isEmpty(element.getDeriveNetworks())) {
                            continue;
                        }
                        element.getDeriveNetworks().forEach(net -> {
                            if (CollectionUtils.isNotEmpty(net.getFromPorts())) {
                                String gatewayIp = null;
                                for (var p : net.getFromPorts()) {
                                    if (StringUtils.isNotEmpty(p.getIp())) {
                                        gatewayIp = p.getIp();
                                        break;
                                    }
                                }
                                if (gatewayIp != null) {
                                    net.network.setGateway(gatewayIp);
                                }
                            }
                        });
                    }
                } catch (MpRuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    log.error("reallocate ip error , network is {} ", JsonCommon.stringify(network), e);
                }
            }
        }
    }

    private void portCheck(List<CloudPluginSceneElementBO> networkElements) {
        for (CloudPluginSceneElementBO element : networkElements) {
            for (ConnectedNetworkBO network : element.getDeriveNetworks()) {
                try {
                    if (Boolean.TRUE.equals(network.getNetwork().getExternal())) {
                        continue;
                    }
                    List<SceneElementPortBO> ports = new ArrayList<>();
                    ports.addAll(network.getFromPorts());
                    ports.addAll(network.getToPorts());
                    if (CollectionUtils.isEmpty(ports)) {
                        continue;
                    }
                    List<String> possibleDhcpIps = this.getPossibleDhcpIps(network.getNetwork().getGateway(), network.getNetwork().getAllocationPools(), network.getNetwork().getCidr());
                    if (Boolean.TRUE.equals(CloudSingleton.cloudAppConfig.getDhcpIpAllocate())) {
                        possibleDhcpIps = new ArrayList<>();
                    }
                    for (var port : ports) {
                        if (StringUtils.isNotEmpty(port.getIp()) && possibleDhcpIps.contains(port.getIp())) {
                            if (getHandlerUtils().isManualConfigIpPort(port)) {
                                
                                String nodeName = port.getElement().getName();
                                String ip = port.getIp();
                                String conflictIps = StringUtils.join(new HashSet<>(possibleDhcpIps), ",");
                                throw new MpRuntimeException(CloudErrorCode.IP_CONFLICT.format(nodeName, ip, conflictIps));
                            }
                        }
                    }
                } catch (MpRuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    log.error("reallocate ip error , network is {} ", JsonCommon.stringify(network), e);
                }
            }
        }
    }

    private String getNewReallocateIp(CloudPluginSceneElementBO element, ConnectedNetworkBO network, List<String> excloudIps) {
        
        if (element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH
                || element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER) {
            List<String> newIps = IpUtils.newAllocateDefaultRouterIps(network.getNetwork().getCidr(), 1,
                    excloudIps,
                    network.getNetwork().getAllocationPools());
            if (CollectionUtils.isNotEmpty(newIps)) {
                return newIps.get(0);
            }
        } else {
            List<String> newIps = IpUtils.newRandomIps(network.getNetwork().getCidr(), 1,
                    excloudIps,
                    network.getNetwork().getAllocationPools(),
                    Boolean.TRUE.equals(network.getNetwork().getDhcp()));
            if (CollectionUtils.isNotEmpty(newIps)) {
                return newIps.get(0);
            }
        }
        return null;
    }


    private List<String> getPossibleDhcpIps(String gateway, List<AllocationPool> allocationPools, String cidr) {
        List<String> possibleDhcpIps = this.getPossibleDhcpIps(gateway, cidr);
        if (CollectionUtils.isNotEmpty(allocationPools)) {
            allocationPools.forEach(p -> {
                if (StringUtils.isNotEmpty(p.getStart())) {
                    possibleDhcpIps.add(p.getStart());
                }
            });
        }
        return possibleDhcpIps;
    }

    private List<String> getPossibleDhcpIps(String gateway, String cidr) {
        Set<String> possibleDhcpIps = new HashSet<>();
        if (StringUtils.isNotEmpty(cidr)) {
            possibleDhcpIps.add(IpUtils.cidrFirstCanUseIp(cidr));
        }

        possibleDhcpIps.add(IpUtils.nextIp(gateway));
        return new ArrayList<>(possibleDhcpIps);
    }

    private boolean isInCidr(String cidr, String ip) {
        if (StringUtils.isEmpty(cidr) || StringUtils.isEmpty(ip)) {
            return false;
        }
        if (!cidr.contains(Ipv4Util.IP_MASK_SPLIT_MARK) && !cidr.contains(Ipv4Util.IP_SPLIT_MARK)) {
            return false;
        }
        return IpUtils.newIsInCidr(ip, cidr);
    }

    private String getConnectCidrByManualSettingInPort(SceneElementPortBO fromPort, SceneElementPortBO toPort) {
        String fromPortCidr = fromPort.getPortConnectNetCidr();
        if (Objects.isNull(toPort)) {
            return fromPortCidr;
        }
        String toPortCidr = toPort.getPortConnectNetCidr();
        if (StringUtils.isNoneBlank(fromPortCidr)) {
            if (fromPortCidr.equals(toPortCidr)) {
                return fromPortCidr;
            } else if (StringUtils.isNoneBlank(toPortCidr)) {
                throw new MpRuntimeException(String.format("节点%s端口%s配置的网段和其对端端口冲突", fromPort.getElement().getName(), fromPort.getName()));
            } else {
                return fromPortCidr;
            }
        } else {
            return toPortCidr;
        }
    }

    private String getConnectCidrDivideIp(SceneElementPortBO fromPort, SceneElementPortBO toPort) {
        if (StringUtils.isNotEmpty(fromPort.getIp())) {
            
            return fromPort.getIp();
        } else if (toPort != null && StringUtils.isNotEmpty(toPort.getIp())) {
            
            return toPort.getIp();
        }
        return null;
    }
}
