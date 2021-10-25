package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.CollectionUtils;
import cn.cyberpeace.mp.base.util.IntUtils;
import cn.cyberpeace.mp.base.util.TextUtils;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.util.CommandUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.FirewallRuleDirection;
import cn.cyberpeace.mp.sceneplugincloud.constant.QosRuleDirection;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.EntityNode;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualFirewall;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualRouter;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualSwitch;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.EntityNodeCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualFirewallCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class CreateHandler extends HandlerCommon {

    private final VirtualSwitch vsClient = new VirtualSwitch();
    private final VirtualRouter vrClient = new VirtualRouter();
    private final VirtualFirewall vfClient = new VirtualFirewall();
    private final VirtualMachine vmClient = new VirtualMachine();
    private final Container containerClient = new Container();
    private final EntityNode enClient = new EntityNode();

    public static class SceneProcess {
        public static final EventProcess CREATE_AUXILIARY_NETWORKS = new EventProcess("create_auxiliary_networks", "sceneplugincloud.event.create_auxiliary_network");
        public static final EventProcess CREATE_GATEWAYS = new EventProcess("create_gateways", "sceneplugincloud.event.create_gateway");
        public static final EventProcess CREATE_FIREWALLS = new EventProcess("create_firewalls", "sceneplugincloud.event.create_firewall");
        public static final EventProcess CREATE_TERMINALS = new EventProcess("create_terminals", "sceneplugincloud.event.create_terminal");
    }

    public static class ElementProcess {
        public static final EventProcess CREATE_AUXILIARY_NETWORK = new EventProcess("create_auxiliary_network", "sceneplugincloud.event.create_auxiliary_network");
        public static final EventProcess CREATE_VIRTUAL_SWITCH = new EventProcess("create_virtual_switch", "sceneplugincloud.event.create_virtual_switch");
        public static final EventProcess CREATE_VIRTUAL_ROUTER = new EventProcess("create_virtual_router", "sceneplugincloud.event.create_virtual_router");
        public static final EventProcess CREATE_VIRTUAL_FIREWALL = new EventProcess("create_virtual_firewall", "sceneplugincloud.event.create_virtual_firewall");
        public static final EventProcess CREATE_VIRTUAL_MACHINE = new EventProcess("create_virtual_machine", "sceneplugincloud.event.create_virtual_machine");
        public static final EventProcess CREATE_CONTAINER = new EventProcess("create_container", "sceneplugincloud.event.create_container");
        public static final EventProcess CREATE_ENTITY_NODE = new EventProcess("create_entity_node", "sceneplugincloud.event.create_entity_node");
    }

    private void addCloudPort(String portId, CloudPluginSceneElementPortBO cloudPort) {
        getPluginHandler().getElementIds().add(portId);
        getPluginHandler().getElementPortMap().put(portId, cloudPort);

        var srcPort = getPluginHandler().getSrcPort(portId);
        cloudPort.setSrcPort(srcPort);
    }

    private String genAccessModeKey(String protocol, String port, String username, String password) {
        return String.format("%s:%s:%s:%s", protocol, port, username, password);
    }

    private String genAccessModeKey(String protocol, Integer port, String username, String password) {
        return genAccessModeKey(protocol, port.toString(), username, password);
    }

    private String genAccessModeKey(CloudAccessMode accessMode) {
        return genAccessModeKey(accessMode.getProtocol(), accessMode.getPort(), accessMode.getUsername(),
                accessMode.getPassword());
    }

    private String genAccessModeKey(VirtualNic.AccessMode accessMode) {
        String port;
        if (Boolean.TRUE.equals(accessMode.getEnablePortMapping()) && accessMode.getPortMapping() != null) {
            port = accessMode.getPortMapping().getInstancePort();
        } else {
            port = accessMode.getPort() == null ? "0" : accessMode.getPort().toString();
        }
        return genAccessModeKey(accessMode.getProtocol(), port, accessMode.getUsername(),
                accessMode.getPassword());
    }

    private String genAccessModeKey(EntityNodeCreateData.Interface.AccessMode accessMode) {
        return genAccessModeKey(accessMode.getProtocol(), accessMode.getPort(), accessMode.getUsername(),
                accessMode.getPassword());
    }

    private void createAuxiliaryNetwork(CloudPluginSceneElementBO cloudElement) {
        if (!StringUtils.isEmpty(cloudElement.getAuxiliaryNetworkId())) {
            return;
        }

        var srcElement = cloudElement.getSrcElement();
        var createData = new VirtualSwitchCreateData();
        createData.setName(genResourceName(srcElement.getEid()));
        createData.setEntityNetwork(cloudElement.getAuxiliaryNetworkVlan());
        createData.setSegmentationId(IntUtils.zeroNull(cloudElement.getAuxiliaryNetworkVlanId()));
        var subnet = new VirtualSwitchCreateData.Subnet();
        subnet.setCidr(cloudElement.getAuxiliaryNetworkCidr());
        subnet.setGatewayIp(TextUtils.emptyNull(cloudElement.getAuxiliaryNetworkGateway()));
        // 默认开启dhcp
        var dhcp = cloudElement.getAuxiliaryNetworkDhcp();
        subnet.setEnableDhcp(dhcp == null || dhcp);
        var srcAllocationPools = cloudElement.getAuxiliaryNetworkAllocationPools();
        if (srcAllocationPools != null && !srcAllocationPools.isEmpty()) {
            List<VirtualSwitchCreateData.Subnet.AllocationPool> allocationPools = new ArrayList<>();
            srcAllocationPools.forEach(srcAllocationPool -> {
                allocationPools.add(new VirtualSwitchCreateData.Subnet.AllocationPool(srcAllocationPool.getStart(),
                        srcAllocationPool.getEnd()));
            });
            subnet.setAllocationPools(allocationPools);
        }
        subnet.setDnsNameservers(CollectionUtils.emptyNull(cloudElement.getAuxiliaryNetworkDnsNameservers()));
        createData.setSubnet(subnet);
        var virtualSwitch = vsClient.create(createData);
        cloudElement.setAuxiliaryNetworkId(virtualSwitch.getId());
    }

    private List<VirtualNic> genVirutalNics(CloudPluginSceneElementBO cloudElement) {
        List<VirtualNic> virtualNics = new ArrayList<>();
        // 添加管理网卡
        if (Boolean.TRUE.equals(cloudElement.getEnableMgmt())) {
            var virtualNic = new VirtualNic();
            virtualNic.setNetworkId(CloudSingleton.cloudAppConfig.getMgmtNetId());
            virtualNics.add(virtualNic);
        }

        var srcElement = cloudElement.getSrcElement();
        var ports = srcElement.getPorts();
        if (ports == null) {
            return virtualNics;
        }

        // TODO: 多个端口复用
        Map<String, List<CloudPluginSceneElementPortBO>> networkIdCloudPortsMap = new HashMap<>();
        for (var port : ports) {
            var cloudPort = getPluginHandler().getPort(port.getId());
            var connectedSrcElement = getPluginHandler().getPortConnectedSrcElement(port.getId());
            var connectedCloudElement = connectedSrcElement == null ? null
                    : getPluginHandler().getElement(connectedSrcElement.getId());
            var auxiliaryNetworkCloudElement = getHandlerUtils().confirmAuxiliaryNetworkCloudElement(
                    cloudElement, connectedCloudElement);
            if (auxiliaryNetworkCloudElement == null) {
                continue;
            }

            // 没有定义的端口补全
            if (cloudPort == null) {
                cloudPort = new CloudPluginSceneElementPortBO();
                addCloudPort(port.getId(), cloudPort);
            }
            var auxiliaryNetworkId = auxiliaryNetworkCloudElement.getAuxiliaryNetworkId();
            cloudPort.setAuxiliaryNetworkId(auxiliaryNetworkId);
            var networkIdCloudPorts = networkIdCloudPortsMap.getOrDefault(
                    auxiliaryNetworkId, new ArrayList<>());
            networkIdCloudPorts.add(cloudPort);
            networkIdCloudPortsMap.put(auxiliaryNetworkId, networkIdCloudPorts);
            // TODO: 多个端口连到同一个网络上的时候暂时当做一个端口处理
            if (networkIdCloudPorts.size() > 1) {
                continue;
            }

            var virtualNic = new VirtualNic();
            virtualNic.setNetworkId(auxiliaryNetworkCloudElement.getAuxiliaryNetworkId());
            virtualNic.setFixedIp(TextUtils.emptyNull(port.getIp()));
            virtualNic.setMacAddress(TextUtils.emptyNull(port.getMacAddr()));

            List<VirtualNic.QosRule> qosRules = new ArrayList<>();
            if (port.getIngress() > 0 || port.getDelay() > 0 || port.getLoss() > 0) {
                var qosRule = new VirtualNic.QosRule();
                qosRule.setMaxKbps(port.getIngress());
                qosRule.setDirection(QosRuleDirection.INGRESS);
                qosRule.setDelayMs(port.getDelay());
                qosRule.setLossPercent(port.getLoss());
                qosRules.add(qosRule);
            }
            if (port.getEgress() > 0 || port.getDelay() > 0 || port.getLoss() > 0) {
                var qosRule = new VirtualNic.QosRule();
                qosRule.setMaxKbps(port.getEgress());
                qosRule.setDirection(QosRuleDirection.EGRESS);
                qosRule.setDelayMs(port.getDelay());
                qosRule.setLossPercent(port.getLoss());
                qosRules.add(qosRule);
            }
            virtualNic.setQosRules(CollectionUtils.emptyNull(qosRules));
            virtualNic.setEnableExternal(cloudPort.getNat());
            virtualNic.setFirewallId(null);

            var accessModes = cloudPort.getAccessModes();
            if (accessModes != null && !accessModes.isEmpty()) {
                virtualNic.setAccessModes(accessModes.stream().map(accessMode -> {
                    var accessModeParam = new VirtualNic.AccessMode();
                    accessModeParam.setProtocol(accessMode.getProtocol());
                    accessModeParam.setPort(accessMode.getPort());
                    accessModeParam.setUsername(accessMode.getUsername());
                    accessModeParam.setPassword(accessMode.getPassword());
                    accessModeParam.setEnablePortMapping(accessMode.getProxy());
                    accessModeParam.setPortMappingProtocol(TextUtils.emptyNull(accessMode.getBaseProtocol()));
                    return accessModeParam;
                }).collect(Collectors.toList()));
            }
            virtualNics.add(virtualNic);
        }

        return virtualNics;
    }

    private List<EntityNodeCreateData.Interface> genEntityInterfaces(CloudPluginSceneElementBO cloudElement) {
        List<EntityNodeCreateData.Interface> entityInterfaces = new ArrayList<>();
        var srcElement = cloudElement.getSrcElement();
        var ports = srcElement.getPorts();
        if (ports == null) {
            return entityInterfaces;
        }

        for (var port : ports) {
            var connectedSrcElement = getPluginHandler().getPortConnectedSrcElement(port.getId());
            if (connectedSrcElement == null) {
                continue;
            }
            var connectedCloudElement = getPluginHandler().getElement(connectedSrcElement.getId());
            var auxiliaryNetworkCloudElement = getHandlerUtils().confirmAuxiliaryNetworkCloudElement(cloudElement, connectedCloudElement);
            if (auxiliaryNetworkCloudElement == null) {
                continue;
            }

            var cloudPort = getPluginHandler().getPort(port.getId());
            // 没有定义的端口补全
            if (cloudPort == null) {
                cloudPort = new CloudPluginSceneElementPortBO();
                addCloudPort(port.getId(), cloudPort);
            }
            cloudPort.setAuxiliaryNetworkId(auxiliaryNetworkCloudElement.getAuxiliaryNetworkId());

            var entityInterface = new EntityNodeCreateData.Interface();
            entityInterface.setNetworkId(auxiliaryNetworkCloudElement.getAuxiliaryNetworkId());
            entityInterface.setInterfaceName(port.getName());

            var accessModes = cloudPort.getAccessModes();
            if (accessModes != null && !accessModes.isEmpty()) {
                entityInterface.setAccessModes(accessModes.stream().map(accessMode -> {
                    var accessModeParam = new EntityNodeCreateData.Interface.AccessMode();
                    accessModeParam.setProtocol(accessMode.getProtocol());
                    accessModeParam.setPort(accessMode.getPort());
                    accessModeParam.setUsername(accessMode.getUsername());
                    accessModeParam.setPassword(accessMode.getPassword());
                    accessModeParam.setIpAddress(TextUtils.emptyNull(port.getIp()));
                    accessModeParam.setMacAddress(TextUtils.emptyNull(port.getMacAddr()));
                    return accessModeParam;
                }).collect(Collectors.toList()));
            }
            entityInterfaces.add(entityInterface);
        }

        return entityInterfaces;
    }

    private String combineInitCommand(CloudPluginSceneElementBO cloudElement) {
        var srcElement = cloudElement.getSrcElement();
        List<String> commands = new ArrayList<>();
        List<String> initCommands = srcElement.getInitCommands();
        if (initCommands != null) {
            commands.addAll(initCommands);
        }

        // TODO: 应用安装

        // TODO: 快速创建安装
//        if (Boolean.TRUE.equals(cloudElement.getQuickCreateSupport())) {
//
//        }

        return CommandUtils.joinCommands(commands.toArray(new String[0]), srcElement.getSystemType());
    }

    private void receiveVirtualPortInfo(CloudPluginSceneElementBO cloudElement, List<VirtualNic> virtualNics) {
        if (virtualNics == null) {
            return;
        }

        var srcPorts = cloudElement.getSrcElement().getPorts();
        Map<String, List<CloudPluginSceneElementPortBO>> networkIdCloudPortsMap = new HashMap<>();
        if (srcPorts != null) {
            srcPorts.forEach(srcPort -> {
                var cloudPort = getPluginHandler().getPort(srcPort.getId());
                var auxiliaryNetworkId = cloudPort.getAuxiliaryNetworkId();
                var networkIdCloudPorts = networkIdCloudPortsMap.getOrDefault(
                        auxiliaryNetworkId, new ArrayList<>());
                networkIdCloudPorts.add(cloudPort);
                networkIdCloudPortsMap.put(auxiliaryNetworkId, networkIdCloudPorts);
            });
        }

        virtualNics.forEach(virtualNic -> {
            var networkId = virtualNic.getNetworkId();
            // 管理网卡判断
            if (Boolean.TRUE.equals(cloudElement.getEnableMgmt())
                    && CloudSingleton.cloudAppConfig.getMgmtNetId().equals(networkId)) {
                cloudElement.setMgmtPortResourceId(virtualNic.getPortId());
                cloudElement.setMgmtIp(virtualNic.getFixedIp());
                cloudElement.setMgmtMacAddr(virtualNic.getMacAddress());
                return;
            }

            var cloudPorts = networkIdCloudPortsMap.getOrDefault(networkId, null);
            if (cloudPorts == null) {
                return;
            }

            // TODO: 多个端口连到同一个网络上的时候暂时当做一个端口处理
            cloudPorts.forEach(cloudPort -> {
                var srcPort = cloudPort.getSrcPort();
                srcPort.setIp(virtualNic.getFixedIp());
                srcPort.setMacAddr(virtualNic.getMacAddress());

                cloudPort.setResourceId(virtualNic.getPortId());
                cloudPort.setQosId(virtualNic.getQosId());

                var accessModesData = virtualNic.getAccessModes();
                if (accessModesData != null && !accessModesData.isEmpty()) {
                    Map<String, VirtualNic.AccessMode> accessModeDataMap = accessModesData.stream()
                            .collect(Collectors.toMap(this::genAccessModeKey, accessMode -> accessMode));
                    cloudPort.getAccessModes().forEach(accessMode -> {
                        var accessModeData = accessModeDataMap.getOrDefault(genAccessModeKey(accessMode), null);
                        if (accessModeData == null) {
                            return;
                        }

                        var connection = accessModeData.getConnection();
                        if (connection != null) {
                            accessMode.setConnectionUser(TextUtils.emptyNull(getRequestUser()));
                            accessMode.setConnectionId(connection.getConnectionId());
                            accessMode.setConnectionUrl(connection.getUrl());
                        }

                        var portMapping = accessModeData.getPortMapping();
                        if (portMapping != null) {
                            accessMode.setProxyId(portMapping.getId());
                            accessMode.setAccessIp(portMapping.getHost());
                            accessMode.setAccessPort(portMapping.getHostPort());
                        } else {
                            accessMode.setAccessIp(accessModeData.getIpAddress());
                            accessMode.setAccessPort(accessModeData.getPort());
                        }
                    });
                }
            });
        });
    }

    private void receiveEntityInterfaceInfo(CloudPluginSceneElementBO cloudElement,
                                            List<EntityNodeCreateData.Interface> entityInterfaces) {
        if (entityInterfaces == null) {
            return;
        }

        var srcPorts = cloudElement.getSrcElement().getPorts();
        if (srcPorts == null) {
            return;
        }

        Map<String, CloudPluginSceneElementPortBO> networkIdCloudPortMap = new HashMap<>();
        srcPorts.forEach(srcPort -> {
            var cloudPort = getPluginHandler().getPort(srcPort.getId());
            networkIdCloudPortMap.put(cloudPort.getAuxiliaryNetworkId(), cloudPort);
        });

        entityInterfaces.forEach(entityInterface -> {
            var cloudPort = networkIdCloudPortMap.getOrDefault(entityInterface.getNetworkId(), null);
            if (cloudPort == null) {
                return;
            }

            var accessModesData = entityInterface.getAccessModes();
            if (accessModesData != null && !accessModesData.isEmpty()) {
                Map<String, EntityNodeCreateData.Interface.AccessMode> accessModeDataMap = accessModesData.stream()
                        .collect(Collectors.toMap(this::genAccessModeKey, accessMode -> accessMode));
                cloudPort.getAccessModes().forEach(accessMode -> {
                    var accessModeData = accessModeDataMap.getOrDefault(genAccessModeKey(accessMode), null);
                    if (accessModeData == null) {
                        return;
                    }

                    var connection = accessModeData.getConnection();
                    if (connection != null) {
                        accessMode.setConnectionUser(TextUtils.emptyNull(getRequestUser()));
                        accessMode.setConnectionId(connection.getConnectionId());
                        accessMode.setConnectionUrl(connection.getUrl());
                    }
                    accessMode.setAccessIp(accessModeData.getIpAddress());
                    accessMode.setAccessPort(accessModeData.getPort());
                });
            }
        });
    }

    protected void createVirtualRouter(CloudPluginSceneElementBO cloudElement) {
        var createData = new VirtualRouterCreateData();
        createData.setName(genResourceName(cloudElement.getSrcElement().getEid()));
        var srcStaticRoutes = cloudElement.getStaticRoutes();
        if (srcStaticRoutes != null && !srcStaticRoutes.isEmpty()) {
            List<VirtualRouterCreateData.StaticRoute> staticRoutes = new ArrayList<>();
            srcStaticRoutes.forEach(srcStaticRoute -> {
                staticRoutes.add(new VirtualRouterCreateData.StaticRoute(srcStaticRoute.getDestination(),
                        srcStaticRoute.getNexthop()));
            });
            createData.setRoutes(staticRoutes);
        }
        createData.setNetworks(CollectionUtils.emptyNull(genVirutalNics(cloudElement)));
        var virtualRouter = vrClient.create(createData);
        cloudElement.setResourceId(virtualRouter.getId());

        receiveVirtualPortInfo(cloudElement, virtualRouter.getNetworks());
    }

    protected void createVirtualFirewall(CloudPluginSceneElementBO cloudElement) {
        var srcElement = cloudElement.getSrcElement();
        var createData = new VirtualFirewallCreateData();
        createData.setName(genResourceName(srcElement.getEid()));
        var srcRules = cloudElement.getFirewallRules();
        if (srcRules != null && !srcRules.isEmpty()) {
            List<VirtualFirewallCreateData.Rule> ingressRules = new ArrayList<>();
            List<VirtualFirewallCreateData.Rule> egressRules = new ArrayList<>();
            srcRules.forEach(srcRule -> {
                var direction = srcRule.getDirection();
                var rule = new VirtualFirewallCreateData.Rule(srcRule.getSourceIpAddress(),
                        srcRule.getDestinationIpAddress(), srcRule.getSourcePort(),
                        srcRule.getDestinationPort(), null, srcRule.getProtocol(),
                        srcRule.getAction());
                if (FirewallRuleDirection.INGRESS.equals(direction)) {
                    ingressRules.add(rule);
                } else if (FirewallRuleDirection.EGRESS.equals(direction)) {
                    egressRules.add(rule);
                } else if (FirewallRuleDirection.BOTH.equals(direction)) {
                    ingressRules.add(rule);
                    egressRules.add(rule);
                }

            });
            createData.setIngressRules(ingressRules);
            createData.setEgressRules(egressRules);
        }

        // 防火墙连接的虚拟路由端口
        List<String> targetPorts = new ArrayList<>();
        var connectedSrcPorts = getPluginHandler().getElementConnectedSrcPorts(srcElement.getId());
        for (var connectedSrcPort : connectedSrcPorts) {
            var connectedCloudPort = getPluginHandler().getPort(connectedSrcPort.getId());
            if (connectedCloudPort == null || StringUtils.isEmpty(connectedCloudPort.getResourceId())) {
                continue;
            }
            var connectedSrcElement = getPluginHandler().getPortSrcElement(connectedSrcPort.getId());
            var connectedCloudElement = getPluginHandler().getElement(connectedSrcElement.getId());
            if (connectedCloudElement == null || connectedCloudElement.getVirtualType() == null
                    || !connectedCloudElement.getVirtualType().equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
                continue;
            }
            targetPorts.add(connectedCloudPort.getResourceId());
        }
        createData.setPorts(CollectionUtils.emptyNull(targetPorts));

        var virtualFirewall = vfClient.create(createData);
        cloudElement.setResourceId(virtualFirewall.getId());
    }

    protected void createVirtualMachine(CloudPluginSceneElementBO cloudElement) {
        var srcElement = cloudElement.getSrcElement();
        var createData = new VirtualMachineCreateData();
        createData.setRequestUser(TextUtils.emptyNull(getRequestUser()));
        createData.setName(genResourceName(srcElement.getEid()));
        createData.setImage(cloudElement.getImage());
        createData.setCpu(IntUtils.zeroNull(srcElement.getCpu()));
        createData.setRam(IntUtils.zeroNull(srcElement.getRam()));
        createData.setDisk(IntUtils.zeroNull(srcElement.getDisk()));
        if (Boolean.TRUE.equals(cloudElement.getInitSupport())) {
            createData.setUserData(TextUtils.emptyNull(combineInitCommand(cloudElement)));
        }
        createData.setNetworks(CollectionUtils.emptyNull(genVirutalNics(cloudElement)));
        var virtualMachine = vmClient.create(createData);
        cloudElement.setResourceId(virtualMachine.getId());

        receiveVirtualPortInfo(cloudElement, virtualMachine.getNetworks());
    }

    protected void createContainer(CloudPluginSceneElementBO cloudElement) {
        var srcElement = cloudElement.getSrcElement();
        var createData = new ContainerCreateData();
        createData.setRequestUser(TextUtils.emptyNull(getRequestUser()));
        createData.setName(genResourceName(srcElement.getEid()));
        createData.setImage(cloudElement.getImage());
        createData.setCpu(IntUtils.zeroNull(srcElement.getCpu()));
        createData.setRam(IntUtils.zeroNull(srcElement.getRam()));
        createData.setDisk(IntUtils.zeroNull(srcElement.getDisk()));
        if (Boolean.TRUE.equals(cloudElement.getInitSupport())) {
            createData.setPostCommand(TextUtils.emptyNull(combineInitCommand(cloudElement)));
        }
        createData.setNetworks(CollectionUtils.emptyNull(genVirutalNics(cloudElement)));
        var container = containerClient.create(createData);
        cloudElement.setResourceId(container.getId());

        receiveVirtualPortInfo(cloudElement, container.getNetworks());
    }

    protected void createEntityNode(CloudPluginSceneElementBO cloudElement) {
        var createData = new EntityNodeCreateData();
        createData.setRequestUser(TextUtils.emptyNull(getRequestUser()));
        createData.setName(cloudElement.getImage());
        createData.setInterfaces(CollectionUtils.emptyNull(genEntityInterfaces(cloudElement)));
        var entityNode = enClient.create(createData);

        receiveEntityInterfaceInfo(cloudElement, entityNode.getInterfaces());
    }

    public CreateHandler(CloudScenePluginHandler pluginHandler) {
        super(pluginHandler, CreateTaskPool.getInstance(), SceneModel.Event.CREATE);
    }

    public void create() {
        prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);

        // 先创建辅助网络(包含虚拟交换机)
        var createAuxiliaryNetworkTasks = getPluginHandler().getElementMap().values().stream()
                .filter(cloudElement -> Boolean.TRUE.equals(cloudElement.getAuxiliaryNetworkOn()))
                .map(cloudElement -> (Runnable) () -> createAuxiliaryNetworkProcess(cloudElement))
                .collect(Collectors.toList());
        if (!createAuxiliaryNetworkTasks.isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_AUXILIARY_NETWORKS, () -> runTasks(createAuxiliaryNetworkTasks));
        }

        // 再创建网关
        var createGatewayTasks = getPluginHandler().getGatewayElementMap().values().stream()
                .map(cloudElement -> (Runnable) () -> createElementProcess(cloudElement)).collect(Collectors.toList());
        if (!createGatewayTasks.isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_GATEWAYS, () -> runTasks(createGatewayTasks));
        }

        // 再创建防火墙
        var createFirewallTasks = getPluginHandler().getFirewallElementMap().values().stream()
                .map(cloudElement -> (Runnable) () -> createElementProcess(cloudElement)).collect(Collectors.toList());
        if (!createFirewallTasks.isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_FIREWALLS, () -> runTasks(createFirewallTasks));
        }

        // 再创建终端
        var createTerminalTasks = getPluginHandler().getTerminalElementMap().values().stream()
                .map(cloudElement -> (Runnable) () -> createElementProcess(cloudElement)).collect(Collectors.toList());
        if (!createTerminalTasks.isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_TERMINALS, () -> runTasks(createTerminalTasks));
        }
    }

    public void createAuxiliaryNetworkProcess(CloudPluginSceneElementBO cloudElement) {
        runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.CREATE_AUXILIARY_NETWORK,
                () -> createAuxiliaryNetwork(cloudElement));
    }

    public void createElementProcess(CloudPluginSceneElementBO cloudElement) {
        if (Boolean.TRUE.equals(cloudElement.getExternal())) {
            return;
        }

        Integer virtualType = cloudElement.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", cloudElement.getSrcElement().getEid(),
                    cloudElement.getSrcElement().getId());
            return;
        }

        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
            runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.CREATE_VIRTUAL_SWITCH,
                    () -> cloudElement.setResourceId(cloudElement.getAuxiliaryNetworkId()));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
            runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.CREATE_VIRTUAL_ROUTER,
                    () -> createVirtualRouter(cloudElement));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL)) {
            runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.CREATE_VIRTUAL_FIREWALL,
                    () -> createVirtualFirewall(cloudElement));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.CREATE_VIRTUAL_MACHINE,
                    () -> createVirtualMachine(cloudElement));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.CREATE_CONTAINER,
                    () -> createContainer(cloudElement));
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
            runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.CREATE_ENTITY_NODE,
                    () -> createEntityNode(cloudElement));
        }
    }

}
