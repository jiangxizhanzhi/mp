package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.basic.BoolUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.CoreType;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RecreateTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.util.ElementTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
public class ElementInitiateHandler extends HandlerCommon {

    public ElementInitiateHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, RecreateTaskPool.getInstance(), event);
    }

    public void initiateElement(CloudPluginSceneElementBO element) {
        getPluginHandler().getSceneHandler().addExcludeHandleNode(element.getSrcElement().getId());
        this.initiate();
    }

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

            
            if (StringUtils.isEmpty(network.getGateway())) {
                String gatewayIp = null;
                if (ElementTypeUtils.isVirtualGateway(element) || ElementTypeUtils.isCoreRouter(element)) {
                    for (var p : connectedNetwork.getFromPorts()) {
                        if (StringUtils.isNotEmpty(p.getIp())) {
                            gatewayIp = p.getIp();
                            break;
                        }
                    }
                }
                if (gatewayIp == null) {
                    String gateway = null;
                    List<String> excludeIps = new ArrayList<>(existIps);
                    for (int i = 0; i < 5; i++) {
                        gateway = IpUtils.newAllocateDefaultRouterIps(network.getCidr(), 1, excludeIps, network.getAllocationPools()).get(0);
                        excludeIps.add(gateway);
                        List<String> possibleDhcpIps = this.getPossibleDhcpIps(gateway, network.getCidr());
                        if (Boolean.TRUE.equals(CloudSingleton.cloudAppConfig.getDhcpIpAllocate())) {
                            possibleDhcpIps = new ArrayList<>();
                        }
                        boolean conflict = false;
                        for (String possibleDhcpIp : possibleDhcpIps) {
                            if (existIps.contains(possibleDhcpIp)) {
                                conflict = true;
                                break;
                            }
                        }
                        if (!conflict) {
                            break;
                        }
                    }
                    network.setGateway(gateway);
                } else {
                    network.setGateway(gatewayIp);
                }
            }
            existIps.add(network.getGateway());
            if (Boolean.TRUE.equals(CloudSingleton.cloudAppConfig.getDhcpIpAllocate()) && Boolean.TRUE.equals(network.getDhcp())) {
                List<String> possibleDhcpIps = this.getPossibleDhcpIps(network.getGateway(), network.getCidr());
                for (String possibleDhcpIp : possibleDhcpIps) {
                    if (!existIps.contains(possibleDhcpIp) && !possibleDhcpIp.endsWith("255")) {
                        network.setDhcpIp(possibleDhcpIp);
                        existIps.add(possibleDhcpIp);
                        break;
                    }
                }
                if (StringUtils.isEmpty(network.getDhcpIp())) {
                    List<String> ipPool = IpUtils.newRandomIps(network.getCidr(), 1, new ArrayList<>(existIps), new ArrayList<>(), BoolUtils.bool(network.getDhcp()));
                    if (CollectionUtils.isNotEmpty(ipPool)) {
                        network.setDhcpIp(ipPool.get(0));
                        existIps.add(ipPool.get(0));
                    }
                }
            } else {
                existIps.add(IpUtils.nextIp(network.getGateway()));
            }

            Set<String> portIps = new HashSet<>();
            int requireCount = 0;
            int gatewayCount = 0;
            boolean fromPortsAdd = false;
            
            for (var port : connectedNetwork.getFromPorts()) {
                if (StringUtils.isEmpty(port.getIp())) {
                    if (!fromPortsAdd) {
                        if (ElementTypeUtils.isVirtualGateway(element)) {
                            gatewayCount++;
                        } else {
                            requireCount++;
                        }
                    }
                    fromPortsAdd = true;
                } else {
                    portIps.add(port.getIp());
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
            List<String> gatewayIpPool = IpUtils.newAllocateDefaultRouterIps(network.getCidr(), gatewayCount, new ArrayList<>(existIps), network.getAllocationPools());
            if (Boolean.TRUE.equals(connectedNetwork.network.getAuxiliaryNetworkRouterOn())) {
                connectedNetwork.network.setAuxiliaryNetworkRouterIp(connectedNetwork.getNetwork().getGateway());
            }
            if (CollectionUtils.isNotEmpty(gatewayIpPool)) {
                existIps.addAll(gatewayIpPool);
            }
            List<String> ipPool = IpUtils.newRandomIps(network.getCidr(), requireCount, new ArrayList<>(existIps), network.getAllocationPools(), BoolUtils.bool(network.getDhcp()));
            ipPool.addAll(gatewayIpPool);
            if (!portIps.contains(network.getGateway())) {
                
                if (!Boolean.TRUE.equals(network.getGatewayOff())) {
                    ipPool.add(network.getGateway());
                }
            }
            network.getExcloudIps().addAll(existIps);
            network.getExcloudIps().addAll(ipPool);
            network.setIpPool(ipPool);
            if (element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH) {
                connectedNetwork.getFromPorts().forEach(port -> port.setIp(network.getGateway()));
            }
        });
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
                        if (getPluginHandler().getSceneHandler().isHandleNode(port.getElement().getId())) {
                            port.setIp(ipPool.remove(ipPool.size() - 1));
                        }
                    }
                } else {
                    synchronized (ipPool) {
                        if (getPluginHandler().getSceneHandler().isHandleNode(port.getElement().getId())) {
                            port.setIp(ipPool.remove(0));
                        }
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
                        if (ElementTypeUtils.isVirtualGateway(pluginElement) || ElementTypeUtils.isCoreRouter(cloudElement)) {
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
                
                Integer virtualType = cloudElement.getVirtualType();
                if (virtualType != null && (virtualType == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER || virtualType == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
                    
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

    private boolean isCoreRouter(CloudPluginSceneElementBO cloudElement) {
        boolean isCoreRouter = (cloudElement.getRole() != null && cloudElement.getRole().equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER)) && BaseCloudPluginSceneElementModel.ImageType.VM.equals(cloudElement.getImageType());
        return isCoreRouter;
    }

    public void initCoreType(CloudPluginSceneElementBO element) {
        Integer role = element.getRole();
        CoreType coreType = CoreType.NETWORK;
        if (role != null) {
            if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY) || role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK)) {
                coreType = CoreType.NETWORK;
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH)) {
                if (BaseCloudPluginSceneElementModel.ImageType.SIMPLE.equals(element.getImageType())) {
                    coreType = CoreType.NETWORK;
                } else {
                    coreType = CoreType.NETWORK_L2;
                }
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER)) {
                if (BaseCloudPluginSceneElementModel.ImageType.SIMPLE.equals(element.getImageType())) {
                    coreType = CoreType.ROUTER;
                } else {
                    coreType = CoreType.ROUTER_L2;
                }
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL)) {
                coreType = CoreType.FIREWALL;
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
        
        if (getPluginHandler().getElementPortMap() != null) {
            getPluginHandler().getElementPortMap().values().forEach(p -> {
                if (p.getSrcPort() != null && StringUtils.isNotEmpty(p.getSrcPort().getIp())) {
                    getHandlerUtils().addManualConfigIpPort(p.getSrcPort());
                }
            });
        }

        var cloudElements = new ArrayList<>(getPluginHandler().getElementMap().values());
        
        
        Collections.sort(cloudElements, (a, b) -> {
            if (a.getSrcElement().getName() == null || b.getSrcElement().getName() == null) {
                return 0;
            }
            return a.getSrcElement().getName().compareTo(b.getSrcElement().getName());
        });

        cloudElements.forEach(cloudElement -> this.initCoreType(cloudElement));
        
        Collections.sort(cloudElements, Comparator.comparing(CloudPluginSceneElementBO::getCoreType));

        getPluginHandler().createElementNetworks();

        
        var initiateElementIpPoolTasks = cloudElements.stream().filter(cloudElement -> !cloudElement.getDeriveNetworks().isEmpty()).map(cloudElement -> (Runnable) () -> initiateElementIpPoolProcess(cloudElement)).collect(Collectors.toList());
        if (!initiateElementIpPoolTasks.isEmpty()) {
            initiateElementIpPoolTasks.forEach(task -> task.run());
        }

        
        List<Runnable> initiateRouterElementPortsTasks = new ArrayList<>();
        List<Runnable> initiateOtherElementPortsTasks = new ArrayList<>();
        cloudElements.forEach(cloudElement -> {
            Integer virtualType = cloudElement.getVirtualType();
            if (virtualType != null && (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER) || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER) || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE) || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH))) {
                Runnable task = () -> initiateElementPortsProcess(cloudElement);
                Integer type = cloudElement.getSrcElement().getType();
                
                if (type != null && (type.equals(BaseSceneElementModel.Type.ROUTER) || type.equals(BaseSceneElementModel.Type.FIREWALL))) {
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
    }

    protected void prepareEventProcess() {
        prepareEventProcess(SceneModel.Event.INITIATE, cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.InitiateHandler.SceneProcess.class, cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.InitiateHandler.ElementProcess.class);
    }

    public void initiateElementIpPoolProcess(CloudPluginSceneElementBO cloudElement) {
        initIpPool(cloudElement);
    }

    public void initiateElementPortsProcess(CloudPluginSceneElementBO cloudElement) {
        initiateElementPorts(cloudElement);
    }

    private List<String> getPossibleDhcpIps(String gateway, String cidr) {
        Set<String> possibleDhcpIps = new HashSet<>();
        if (StringUtils.isNotEmpty(cidr)) {
            String firstIp = IpUtils.beginIp(cidr);
            possibleDhcpIps.add(IpUtils.nextIp(firstIp));
        }
        possibleDhcpIps.add(IpUtils.nextIp(gateway));
        return new ArrayList<>(possibleDhcpIps);
    }

}
