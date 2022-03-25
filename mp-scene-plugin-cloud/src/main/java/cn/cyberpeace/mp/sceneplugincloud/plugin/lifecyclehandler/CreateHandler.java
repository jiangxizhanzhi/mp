package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.basic.CollectionUtils;
import cn.cyberpeace.mp.base.util.basic.IntUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.util.SceneCommandUtils;
import cn.cyberpeace.mp.scene.util.SceneFileUtils;
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.FirewallRule;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.FirewallRuleDirection;
import cn.cyberpeace.mp.sceneplugincloud.constant.QosRuleDirection;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.EntityNode;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Npc;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualFirewall;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualRouter;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualSwitch;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.BaseNpcData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.EntityNodeCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualFirewallCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.ContainerFileUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class CreateHandler extends HandlerCommon {

    private final VirtualSwitch vsClient = new VirtualSwitch();
    private final VirtualRouter vrClient = new VirtualRouter();
    private final VirtualFirewall vfClient = new VirtualFirewall();
    private final VirtualMachine vmClient = new VirtualMachine();
    private final Container containerClient = new Container();
    private final EntityNode enClient = new EntityNode();
    private final Npc npcClient = new Npc();

    private Map<String, BaseNpcData.NetWorkNpcInfo> network2Info = new ConcurrentHashMap<>();

    public static class SceneProcess {
        public static final EventProcess CREATE_AUXILIARY_NETWORKS = new EventProcess("create_auxiliary_networks", "sceneplugincloud.event.create_auxiliary_network");
        public static final EventProcess CREATE_GATEWAYS = new EventProcess("create_gateways", "sceneplugincloud.event.create_gateway");
        public static final EventProcess CREATE_FIREWALLS = new EventProcess("create_firewalls", "sceneplugincloud.event.create_firewall");
        public static final EventProcess CREATE_TERMINALS = new EventProcess("create_terminals", "sceneplugincloud.event.create_terminal");
        public static final EventProcess CREATE_NPCS = new EventProcess("create_npcs", "sceneplugincloud.event.create_npcs");
    }

    public static class ElementProcess {
        public static final EventProcess CREATE_AUXILIARY_NETWORK = new EventProcess("create_auxiliary_network", "sceneplugincloud.event.create_auxiliary_network");
        public static final EventProcess CREATE_VIRTUAL_SWITCH = new EventProcess("create_virtual_switch", "sceneplugincloud.event.create_virtual_switch");
        public static final EventProcess CREATE_VIRTUAL_ROUTER = new EventProcess("create_virtual_router", "sceneplugincloud.event.create_virtual_router");
        public static final EventProcess CREATE_VIRTUAL_FIREWALL = new EventProcess("create_virtual_firewall", "sceneplugincloud.event.create_virtual_firewall");
        public static final EventProcess CREATE_VIRTUAL_MACHINE = new EventProcess("create_virtual_machine", "sceneplugincloud.event.create_virtual_machine");
        public static final EventProcess CREATE_CONTAINER = new EventProcess("create_container", "sceneplugincloud.event.create_container");
        public static final EventProcess CREATE_ENTITY_NODE = new EventProcess("create_entity_node", "sceneplugincloud.event.create_entity_node");
        public static final EventProcess CREATE_NPC = new EventProcess("create_npc", "sceneplugincloud.event.create_npc");
    }

    @Data
    public static class CloudFirewallRules {
        private List<VirtualFirewallCreateData.Rule> ingressRules;
        private List<VirtualFirewallCreateData.Rule> egressRules;

        public static CloudFirewallRules fromSrcRules(List<FirewallRule> srcRules) {
            List<VirtualFirewallCreateData.Rule> ingressRules = new ArrayList<>();
            List<VirtualFirewallCreateData.Rule> egressRules = new ArrayList<>();
            if (srcRules != null) {
                srcRules.forEach(srcRule -> {
                    var direction = srcRule.getDirection();
                    var rule = new VirtualFirewallCreateData.Rule();
                    rule.setSourceIpAddress(srcRule.getSourceIpAddress());
                    rule.setDestinationIpAddress(srcRule.getDestinationIpAddress());
                    rule.setSourcePort(srcRule.getSourcePort());
                    rule.setDestinationPort(srcRule.getDestinationPort());
                    rule.setProtocol(srcRule.getProtocol());
                    rule.setAction(srcRule.getAction());
                    if (FirewallRuleDirection.INGRESS.equals(direction)) {
                        ingressRules.add(rule);
                    } else if (FirewallRuleDirection.EGRESS.equals(direction)) {
                        egressRules.add(rule);
                    } else if (FirewallRuleDirection.BOTH.equals(direction)) {
                        ingressRules.add(rule);
                        egressRules.add(rule);
                    }
                });
            }
            var cloudFirewallRules = new CloudFirewallRules();
            cloudFirewallRules.setIngressRules(ingressRules);
            cloudFirewallRules.setEgressRules(egressRules);
            return cloudFirewallRules;
        }
    }

    private String genAccessModeKey(String protocol, String port, String username, String password) {
        return String.format("%s:%s:%s:%s", protocol, AccessMode.Protocol.CONSOLE.equals(protocol) ? StringUtils.EMPTY : port, username, password);
    }

    private String genAccessModeKey(String protocol, Integer port, String username, String password) {
        return genAccessModeKey(protocol, port == null ? StringUtils.EMPTY : port.toString(), username, password);
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
        List<ConnectedNetworkBO> networks = cloudElement.getDeriveNetworks();
        int index = 1;
        for (ConnectedNetworkBO connectedNetwork : networks) {
            if (StringUtils.isNotEmpty(connectedNetwork.network.getResourceId())) {
                return;
            }
            var srcElement = cloudElement.getSrcElement();
            var createData = new VirtualSwitchCreateData();
            createData.setName(HandlerUtils.genResourceName(srcElement.getEid(), index++));
            createData.setEntityNetwork(cloudElement.getAuxiliaryNetworkVlan());
            if (Boolean.TRUE.equals(connectedNetwork.network.getVlan())) {
                createData.setSegmentationId(IntUtils.zeroNull(connectedNetwork.network.getVlanId()));
            }
            var subnet = new VirtualSwitchCreateData.Subnet();
            subnet.setCidr(connectedNetwork.network.getCidr());
            subnet.setGatewayIp(TextUtils.emptyNull(connectedNetwork.network.getGateway()));
            // 默认开启dhcp
            var dhcp = connectedNetwork.network.getDhcp();
            subnet.setEnableDhcp(dhcp == null || dhcp);

            var srcAllocationPools = connectedNetwork.network.getAllocationPools();
            if (srcAllocationPools != null && !srcAllocationPools.isEmpty()) {
                List<VirtualSwitchCreateData.Subnet.AllocationPool> allocationPools = new ArrayList<>();
                srcAllocationPools.forEach(srcAllocationPool -> {
                    allocationPools.add(new VirtualSwitchCreateData.Subnet.AllocationPool(srcAllocationPool.getStart(),
                            srcAllocationPool.getEnd()));
                });
                subnet.setAllocationPools(allocationPools);
            }

            var srcHostRoutes = connectedNetwork.network.getHostRoutes();
            if (srcHostRoutes != null && !srcHostRoutes.isEmpty()) {
                List<VirtualSwitchCreateData.Subnet.HostRoute> hostRoutes = new ArrayList<>();
                srcHostRoutes.forEach(srcHostRoute -> {
                    hostRoutes.add(new VirtualSwitchCreateData.Subnet.HostRoute(srcHostRoute.getDestination(),
                            srcHostRoute.getNexthop()));
                });
                subnet.setHostRoutes(hostRoutes);
            }
            subnet.setDnsNameservers(CollectionUtils.emptyNull(connectedNetwork.network.getDnsNameservers()));
            createData.setSubnet(subnet);
            var virtualSwitch = vsClient.create(createData);
            connectedNetwork.network.setResourceId(virtualSwitch.getId());
            // 向端口回写网络id
            connectedNetwork.getFromPorts().forEach(p -> {
                var pluginPort = getPluginHandler().getPort(p.getId());
                if (pluginPort != null) {
                    pluginPort.setAuxiliaryNetworkId(virtualSwitch.getId());
                }
            });
            connectedNetwork.getToPorts().forEach(p -> {
                var pluginPort = getPluginHandler().getPort(p.getId());
                if (pluginPort != null) {
                    pluginPort.setAuxiliaryNetworkId(virtualSwitch.getId());
                }
            });
            createAuxiliaryNetworkRouter(cloudElement, connectedNetwork.network);
        }
    }

    private void createAuxiliaryNetworkRouter(CloudPluginSceneElementBO cloudElement, CloudPluginSceneElementNetworkBO network) {
        if (!Boolean.TRUE.equals(network.getAuxiliaryNetworkRouterOn())) {
            return;
        }
        // 创建辅助路由
        var createData = new VirtualRouterCreateData();
        createData.setName(HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid()));
        var virtualNic = new VirtualNic();
        virtualNic.setNetworkId(network.getResourceId());
        virtualNic.setFixedIp(TextUtils.emptyNull(network.getAuxiliaryNetworkRouterIp()));
        createData.setNetworks(List.of(virtualNic));
        var virtualRouter = vrClient.create(createData);
        network.setAuxiliaryNetworkRouterResourceId(virtualRouter.getId());
        var routerVirtualNics = virtualRouter.getNetworks();
        if (routerVirtualNics != null) {
            routerVirtualNics.forEach(routerVirtualNic -> {
                network.setAuxiliaryNetworkRouterIp(routerVirtualNic.getFixedIp());
            });
        }
    }

    /**
     * 创建默认路由模拟的三层防火墙
     */
    private void createVirtualRouterFirewall(CloudPluginSceneElementBO cloudElement) {
        var srcElement = cloudElement.getSrcElement();
        List<FirewallRule> srcRules = cloudElement.getFirewallRules();
        // 创建节点全局防火墙，对节点上所有端口生效，增加端口注意同步此数据
        if (srcRules != null && !srcRules.isEmpty()) {
            // 防火墙的虚拟路由端口
            Set<String> targetPorts = new HashSet<>();
            var cloudPorts = getPluginHandler().getPorts(cloudElement);
            for (var cloudPort : cloudPorts) {
                if (StringUtils.isEmpty(cloudPort.getResourceId())) {
                    continue;
                }
                targetPorts.add(cloudPort.getResourceId());
            }

            if (!targetPorts.isEmpty()) {
                var createData = new VirtualFirewallCreateData();
                createData.setName(HandlerUtils.genResourceName(srcElement.getEid()));
                var cloudFirewallRules = CloudFirewallRules.fromSrcRules(srcRules);
                createData.setIngressRules(cloudFirewallRules.getIngressRules());
                createData.setEgressRules(cloudFirewallRules.getEgressRules());
                createData.setPorts(CollectionUtils.emptyNull(new ArrayList<>(targetPorts)));

                var virtualFirewall = vfClient.create(createData);
                cloudElement.setFirewallResourceId(virtualFirewall.getId());
            }
        }

        createVirtualRouterGroupPortFirewall(cloudElement);
    }

    private void createVirtualRouterGroupPortFirewall(CloudPluginSceneElementBO cloudElement) {
        // 单独创建端口防火墙
        for (ConnectedNetworkBO network : cloudElement.getDeriveNetworks()) {
            createVirtualRouterPortsFirewall(cloudElement, network.getFromPorts());
        }
        for (ConnectedNetworkBO network : cloudElement.getAccessNetworks()) {
            createVirtualRouterPortsFirewall(cloudElement, network.getFromPorts());
        }
    }

    public void createVirtualRouterPortsFirewall(CloudPluginSceneElementBO cloudElement, List<SceneElementPortBO> ports) {
        // 防火墙的虚拟路由端口
        Set<String> targetPorts = new HashSet<>();
        List<FirewallRule> srcRules = new ArrayList<>();
        List<CloudPluginSceneElementPortBO> cloudPorts = new ArrayList<>();
        ports.forEach(srcPort -> {
            var cloudPort = getPluginHandler().getPort(srcPort.getId());
            if (cloudPort == null) {
                return;
            }
            cloudPorts.add(cloudPort);
            if (!StringUtils.isEmpty(cloudPort.getResourceId())) {
                targetPorts.add(cloudPort.getResourceId());
            }
            var portSrcRules = cloudPort.getFirewallRules();
            if (portSrcRules != null) {
                srcRules.addAll(portSrcRules);
            }
        });
        if (targetPorts.isEmpty() || srcRules == null || srcRules.isEmpty()) {
            return;
        }

        var srcElement = cloudElement.getSrcElement();
        var createData = new VirtualFirewallCreateData();
        createData.setName(HandlerUtils.genResourceName(srcElement.getEid()));
        var cloudFirewallRules = CloudFirewallRules.fromSrcRules(srcRules);
        createData.setIngressRules(cloudFirewallRules.getIngressRules());
        createData.setEgressRules(cloudFirewallRules.getEgressRules());
        createData.setPorts(CollectionUtils.emptyNull(new ArrayList<>(targetPorts)));

        var virtualFirewall = vfClient.create(createData);
        cloudPorts.forEach(cloudPort -> {
            cloudPort.setFirewallResourceId(virtualFirewall.getId());
        });
    }

    private List<VirtualNic> genVirutalNics(CloudPluginSceneElementBO cloudElement) {
        List<VirtualNic> virtualNics = new ArrayList<>();
        // 添加管理网卡
        if (Boolean.TRUE.equals(cloudElement.getEnableMgmt())) {
            var virtualNic = new VirtualNic();
            virtualNic.setNetworkId(CloudSingleton.cloudAppConfig.getMgmtNetId());
            virtualNics.add(virtualNic);
        }
        List<ConnectedNetworkBO> allNetworks = new ArrayList<>();
        allNetworks.addAll(cloudElement.getDeriveNetworks());
        allNetworks.addAll(cloudElement.getAccessNetworks());
        if (cloudElement.getSrcElement().getPorts() != null && cloudElement.getSrcElement().getPorts().size() > 0) {
            Map<String, ConnectedNetworkBO> portId2Net = new HashMap<>();
            allNetworks.forEach(net -> portId2Net.put(net.getFromPorts().get(0).getId(), net));
            List<ConnectedNetworkBO> orderNetwork = new ArrayList<>();
            for (var port : cloudElement.getSrcElement().getPorts()) {
                if (portId2Net.containsKey(port.getId())) {
                    orderNetwork.add(portId2Net.remove(port.getId()));
                }
            }
            if (!portId2Net.isEmpty()) {
                orderNetwork.addAll(portId2Net.values());
            }
            allNetworks = orderNetwork;
        }
        for (ConnectedNetworkBO connectedNetwork : allNetworks) {
            VirtualNic virtualNic = this.genConnectedNetwork(connectedNetwork);
            virtualNics.add(virtualNic);
        }
        return virtualNics;
    }

    private VirtualNic genConnectedNetwork(ConnectedNetworkBO connectedNetwork) {
        var network = connectedNetwork.network;
        List<SceneElementPortBO> fromPorts = connectedNetwork.getFromPorts();
        SceneElementPortBO samplePort = fromPorts.get(0);
        CloudPluginSceneElementPortBO sampleCloudPort = getPluginHandler().getPort(samplePort.getId());
        var virtualNic = new VirtualNic();
        virtualNic.setNetworkId(network.getResourceId());
        virtualNic.setFixedIp(TextUtils.emptyNull(samplePort.getIp()));
        virtualNic.setMacAddress(TextUtils.emptyNull(samplePort.getMacAddr()));
        List<VirtualNic.QosRule> qosRules = new ArrayList<>();
        if (samplePort.getIngress() > 0 || samplePort.getDelay() > 0 || samplePort.getLoss() > 0) {
            var qosRule = new VirtualNic.QosRule();
            qosRule.setMaxKbps(samplePort.getIngress());
            qosRule.setDirection(QosRuleDirection.INGRESS);
            qosRule.setDelayMs(samplePort.getDelay());
            qosRule.setLossPercent(samplePort.getLoss());
            qosRules.add(qosRule);
        }
        if (samplePort.getEgress() > 0 || samplePort.getDelay() > 0 || samplePort.getLoss() > 0) {
            var qosRule = new VirtualNic.QosRule();
            qosRule.setMaxKbps(samplePort.getEgress());
            qosRule.setDirection(QosRuleDirection.EGRESS);
            qosRule.setDelayMs(samplePort.getDelay());
            qosRule.setLossPercent(samplePort.getLoss());
            qosRules.add(qosRule);
        }
        virtualNic.setQosRules(CollectionUtils.emptyNull(qosRules));

        if (!HandlerUtils.isExternalNetwork(network.getResourceId())) {
            virtualNic.setEnableExternal(sampleCloudPort.getNat());
        }

        virtualNic.setFirewallId(null);

        var accessModes = sampleCloudPort.getAccessModes();
        if (accessModes != null && !accessModes.isEmpty()) {
            var legalAccessModes = accessModes.stream()
                    .filter(m -> StringUtils.isNotEmpty(m.getProtocol()))
                    .collect(Collectors.toList());
            virtualNic.setAccessModes(legalAccessModes.stream().map(accessMode -> {
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
        return virtualNic;
    }

    private List<EntityNodeCreateData.Interface> genEntityInterfaces(CloudPluginSceneElementBO cloudElement) {
        List<EntityNodeCreateData.Interface> entityInterfaces = new ArrayList<>();
        var srcElement = cloudElement.getSrcElement();
        var ports = srcElement.getPorts();
        if (ports == null) {
            return entityInterfaces;
        }

        List<ConnectedNetworkBO> networks = new ArrayList<>();
        networks.addAll(cloudElement.getDeriveNetworks());
        networks.addAll(cloudElement.getAccessNetworks());
        for (var connectNetwork : networks) {
            var port = connectNetwork.getFromPorts().get(0);
            var cloudPort = getPluginHandler().getPort(port.getId());
            // 没有定义的端口补全
            if (cloudPort == null) {
                cloudPort = new CloudPluginSceneElementPortBO();
                getPluginHandler().addPort(port.getId(), cloudPort);
            }
            cloudPort.setAuxiliaryNetworkId(connectNetwork.getNetwork().getResourceId());

            var entityInterface = new EntityNodeCreateData.Interface();
            entityInterface.setNetworkId(connectNetwork.getNetwork().getResourceId());
            entityInterface.setInterfaceName(port.getName());

            var accessModes = cloudPort.getAccessModes();
            if (accessModes != null && !accessModes.isEmpty()) {
                entityInterface.setAccessModes(accessModes.stream().map(accessMode -> {
                    var accessModeParam = new EntityNodeCreateData.Interface.AccessMode();
                    accessModeParam.setProtocol(accessMode.getProtocol());
                    accessModeParam.setPort(accessMode.getPort());
                    accessModeParam.setUsername(accessMode.getUsername());
                    accessModeParam.setPassword(accessMode.getPassword());
                    if (StringUtils.isNotBlank(accessMode.getAccessIp())) {
                        accessModeParam.setIpAddress(TextUtils.emptyNull(accessMode.getAccessIp()));
                    } else {
                        accessModeParam.setIpAddress(TextUtils.emptyNull(port.getIp()));
                    }
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

        var standardSystemType = SystemTypeUtils.getStandardSystemType(srcElement.getSystemType());
        var srcScene = getPluginHandler().getSrcScene();
        var sceneId = srcScene.getEid();
        var sceneFileUrl = srcScene.getFile();
        if (!StringUtils.isEmpty(sceneFileUrl)) {
            commands.addAll(SceneFileUtils.genSceneFileInitCommand(standardSystemType, sceneFileUrl, sceneId));
        }

        var elementFileUrl = srcElement.getFile();
        if (!StringUtils.isEmpty(elementFileUrl)) {
            commands.addAll(SceneFileUtils.genElementFileInitCommand(standardSystemType, elementFileUrl, sceneId,
                    srcElement.getEid()));
        }

        List<String> initCommands = srcElement.getInitCommands();
        if (initCommands != null) {
            initCommands.forEach(initCommand -> {
                commands.addAll(SceneCommandUtils.parseCommand(initCommand, sceneId, srcElement.getEid(),
                        standardSystemType));
            });
        }

        // TODO: 应用安装

        // TODO: 快速创建安装
//        if (Boolean.TRUE.equals(cloudElement.getQuickCreateSupport())) {
//
//        }

        var command = SystemTypeCommandUtils.joinCommands(standardSystemType, commands.toArray(new String[0]));
        if (!StringUtils.isEmpty(command)) {
            if (SystemTypeCommandUtils.isWindows(standardSystemType)) {
                command = SystemTypeCommandUtils.appendBeforeCommand(command, "rem cmd", standardSystemType);
            } else {
                command = SystemTypeCommandUtils.appendBeforeCommand(command, "#!/bin/sh", standardSystemType);
            }
        }

        return command;
    }

    /**
     * 容器不保证有 curl/unzip 无法正常通过系统命令方式初始化
     * 容器默认linux
     */
    private void execContainerInitCommand(CloudPluginSceneElementBO cloudElement) {
        var containerId = cloudElement.getResourceId();
        var srcElement = cloudElement.getSrcElement();
        var standardSystemType = SystemTypeUtils.getStandardSystemType(srcElement.getSystemType());
        var srcScene = getPluginHandler().getSrcScene();
        var sceneId = srcScene.getEid();

        // 准备容器的unzip工具，容器本身可能自带，所以忽略失败
        try {
            ContainerFileUtils.prepareUnzip(containerClient, containerId);
        } catch (Exception e) {
            log.warn("Scene({}) cloud container({}) element prepare unzip failed: ", sceneId, containerId, e);
        }

        var sceneFileUrl = srcScene.getFile();
        if (!StringUtils.isEmpty(sceneFileUrl)) {
            var unzipDir = SceneFileUtils.getSceneFileUnzipDir(standardSystemType, sceneId);
            ContainerFileUtils.prepareZipFile(containerClient, containerId, sceneFileUrl, unzipDir);
        }

        var elementFileUrl = srcElement.getFile();
        if (!StringUtils.isEmpty(elementFileUrl)) {
            var unzipDir = SceneFileUtils.getElementFileUnzipDir(standardSystemType, sceneId, srcElement.getEid());
            ContainerFileUtils.prepareZipFile(containerClient, containerId, elementFileUrl, unzipDir);
        }

        List<String> commands = new ArrayList<>();
        List<String> initCommands = srcElement.getInitCommands();
        if (initCommands != null) {
            initCommands.forEach(initCommand -> {
                var parsedCommands = SceneCommandUtils.parseCommand(initCommand, sceneId, srcElement.getEid(),
                        standardSystemType);
                var inlineCommand = SystemTypeCommandUtils.inlineJoinCommands(standardSystemType,
                        parsedCommands.toArray(new String[0]));
                commands.add(inlineCommand);
            });
        }

        for (var command : commands) {
            containerClient.executeCommand(containerId, command);
        }
    }

    private void receiveVirtualPortInfo(CloudPluginSceneElementBO cloudElement, List<VirtualNic> virtualNics) {
        if (virtualNics == null) {
            return;
        }

        var cloudPorts = getPluginHandler().getPorts(cloudElement);
        if (cloudPorts.isEmpty()) {
            return;
        }

        Map<String, List<CloudPluginSceneElementPortBO>> networkIdCloudPortsMap = new HashMap<>();
        List<ConnectedNetworkBO> networks = new ArrayList<>();
        networks.addAll(cloudElement.getDeriveNetworks());
        networks.addAll(cloudElement.getAccessNetworks());
        for (ConnectedNetworkBO network : networks) {
            var auxiliaryNetworkId = network.getNetwork().getResourceId();
            var networkIdCloudPorts = networkIdCloudPortsMap.getOrDefault(
                    auxiliaryNetworkId, new ArrayList<>());
            network.getFromPorts().forEach(port -> {
                var pluginPort = getPluginHandler().getPort(port.getId());
                networkIdCloudPorts.add(pluginPort);
            });
            networkIdCloudPortsMap.put(auxiliaryNetworkId, networkIdCloudPorts);
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

            var networkCloudPorts = networkIdCloudPortsMap.getOrDefault(networkId, null);
            if (networkCloudPorts == null) {
                return;
            }

            // 多个端口连到同一个网络上的时候暂时当做一个端口处理
            networkCloudPorts.forEach(cloudPort -> {
                var srcPort = cloudPort.getSrcPort();
                srcPort.setIp(virtualNic.getFixedIp());
                srcPort.setMacAddr(virtualNic.getMacAddress());

                cloudPort.setResourceId(virtualNic.getPortId());
                cloudPort.setQosId(virtualNic.getQosId());
                cloudPort.setFloatingIp(virtualNic.getFloatingip());
                cloudPort.setFloatingIpId(virtualNic.getFloatingipId());

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
                            accessMode.setConnectionUser(TextUtils.emptyNull(getHandlerUtils().getRequestUser()));
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

        var cloudPorts = getPluginHandler().getPorts(cloudElement);
        if (cloudPorts.isEmpty()) {
            return;
        }

        Map<String, CloudPluginSceneElementPortBO> networkIdCloudPortMap = new HashMap<>();
        cloudPorts.forEach(cloudPort -> {
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
                        accessMode.setConnectionUser(TextUtils.emptyNull(getHandlerUtils().getRequestUser()));
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
        createData.setName(HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid()));
        var srcStaticRoutes = cloudElement.getStaticRoutes();
        if (srcStaticRoutes != null && !srcStaticRoutes.isEmpty()) {
            List<VirtualRouterCreateData.StaticRoute> staticRoutes = new ArrayList<>();
            srcStaticRoutes.forEach(srcStaticRoute -> {
                staticRoutes.add(new VirtualRouterCreateData.StaticRoute(srcStaticRoute.getDestination(),
                        srcStaticRoute.getNexthop()));
            });
            createData.setRoutes(staticRoutes);
        }
        List<VirtualNic> virtualNics = new ArrayList<>();
        // 添加管理网卡
        if (Boolean.TRUE.equals(cloudElement.getEnableMgmt())) {
            var virtualNic = new VirtualNic();
            virtualNic.setNetworkId(CloudSingleton.cloudAppConfig.getMgmtNetId());
            virtualNics.add(virtualNic);
        }
        createData.setNetworks(CollectionUtils.emptyNull(
                this.genVirutalNics(cloudElement)
        ));

        var virtualRouter = vrClient.create(createData);

        cloudElement.setResourceId(virtualRouter.getId());
        receiveVirtualPortInfo(cloudElement, virtualRouter.getNetworks());
        createVirtualRouterFirewall(cloudElement);
    }

    protected void createVirtualFirewall(CloudPluginSceneElementBO cloudElement) {
        var srcElement = cloudElement.getSrcElement();
        var createData = new VirtualFirewallCreateData();
        createData.setName(HandlerUtils.genResourceName(srcElement.getEid()));
        var srcRules = cloudElement.getFirewallRules();
        if (srcRules != null && !srcRules.isEmpty()) {
            var cloudFirewallRules = CloudFirewallRules.fromSrcRules(srcRules);
            createData.setIngressRules(cloudFirewallRules.getIngressRules());
            createData.setEgressRules(cloudFirewallRules.getEgressRules());
        }

        // 防火墙连接的虚拟路由端口
        Set<String> targetPorts = new HashSet<>();
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
        createData.setPorts(CollectionUtils.emptyNull(new ArrayList<>(targetPorts)));

        var virtualFirewall = vfClient.create(createData);
        cloudElement.setResourceId(virtualFirewall.getId());
    }

    protected void createVirtualMachine(CloudPluginSceneElementBO cloudElement) {
        var srcElement = cloudElement.getSrcElement();
        var createData = new VirtualMachineCreateData();
        createData.setRequestUser(TextUtils.emptyNull(getHandlerUtils().getRequestUser()));
//        createData.setDepartment(Optional.ofNullable(getHandlerUtils().getRequestUserOrg()).orElse(StringUtils.EMPTY));
//        createData.setScene(Optional.ofNullable(getPluginHandler().getSrcScene().getName()).orElse(StringUtils.EMPTY));
        createData.setName(HandlerUtils.genResourceName(srcElement.getEid()));
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
        cloudElement.setHost(virtualMachine.getHost());

        receiveVirtualPortInfo(cloudElement, virtualMachine.getNetworks());

        checkAccessModes(cloudElement);
    }

    protected void createContainer(CloudPluginSceneElementBO cloudElement) {
        var srcElement = cloudElement.getSrcElement();
        var createData = new ContainerCreateData();
        createData.setRequestUser(TextUtils.emptyNull(getHandlerUtils().getRequestUser()));
//        createData.setDepartment(Optional.ofNullable(getHandlerUtils().getRequestUserOrg()).orElse(StringUtils.EMPTY));
//        createData.setScene(Optional.ofNullable(getPluginHandler().getSrcScene().getName()).orElse(StringUtils.EMPTY));
        createData.setName(HandlerUtils.genResourceName(srcElement.getEid()));
        createData.setImage(cloudElement.getImage());
        // todo 暂时不传
//        createData.setCpu(IntUtils.zeroNull(srcElement.getCpu()));
//        createData.setRam(IntUtils.zeroNull(srcElement.getRam()));
//        createData.setDisk(IntUtils.zeroNull(srcElement.getDisk()));
        // 容器初始化命令通过execContainerInitCommand执行
//        if (Boolean.TRUE.equals(cloudElement.getInitSupport())) {
//            createData.setPostCommand(TextUtils.emptyNull(combineInitCommand(cloudElement)));
//        }
        createData.setNetworks(CollectionUtils.emptyNull(
                this.genVirutalNics(cloudElement)
        ));
        var container = containerClient.create(createData);
        cloudElement.setResourceId(container.getId());
        cloudElement.setHost(container.getHost());

        receiveVirtualPortInfo(cloudElement, container.getNetworks());

        execContainerInitCommand(cloudElement);

        checkAccessModes(cloudElement);
    }

    protected void createEntityNode(CloudPluginSceneElementBO cloudElement) {
        var createData = new EntityNodeCreateData();
        createData.setRequestUser(TextUtils.emptyNull(getHandlerUtils().getRequestUser()));
//        createData.setDepartment(Optional.ofNullable(getHandlerUtils().getRequestUserOrg()).orElse(StringUtils.EMPTY));
//        createData.setScene(Optional.ofNullable(getPluginHandler().getSrcScene().getName()).orElse(StringUtils.EMPTY));
        createData.setName(cloudElement.getImage());
        createData.setInterfaces(CollectionUtils.emptyNull(genEntityInterfaces(cloudElement)));
        var entityNode = enClient.create(createData);

        receiveEntityInterfaceInfo(cloudElement, entityNode.getInterfaces());

        checkAccessModes(cloudElement);
    }

    protected void recordNpcNode(CloudPluginSceneElementBO cloudElement) {
        String npcName = HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid());
        cloudElement.getAccessNetworks().forEach(net -> {
            String networkId = net.network.getResourceId();
            if (!network2Info.containsKey(networkId)) {
                network2Info.put(networkId, new BaseNpcData.NetWorkNpcInfo(networkId));
            }
            net.getFromPorts().forEach(port -> {
                network2Info.get(networkId).addNpcInfo(npcName, port.getIp());
            });
        });
        cloudElement.getDeriveNetworks().forEach(net -> {
            String networkId = net.network.getResourceId();
            if (!network2Info.containsKey(networkId)) {
                network2Info.put(networkId, new BaseNpcData.NetWorkNpcInfo(networkId));
            }
            net.getFromPorts().forEach(port -> {
                network2Info.get(networkId).addNpcInfo(npcName, port.getIp());
            });
        });
    }

    public CreateHandler(CloudScenePluginHandler pluginHandler) {
        super(pluginHandler, CreateTaskPool.getInstance(), SceneModel.Event.CREATE);
    }

    public void create() {
        prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);

        // 先创建辅助网络(包含虚拟交换机)
        var createAuxiliaryNetworkTasks = getPluginHandler().getElementMap().values().stream()
                .filter(cloudElement -> !cloudElement.getDeriveNetworks().isEmpty())
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
        // 批量创建NPC节点
        if (network2Info.values().size() > 0) {
            runSceneProgress(SceneProcess.CREATE_NPCS, () -> createNpcElements(new ArrayList<>(network2Info.values())));
        }
    }

    public void createNpcElements(List<BaseNpcData.NetWorkNpcInfo> list) {
        NpcCreateData npcCreateData = new NpcCreateData();
        npcCreateData.setNetWorkNpcInfos(list);
        npcClient.create(npcCreateData);
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
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.NPC)) {
            runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.CREATE_NPC,
                    () -> recordNpcNode(cloudElement));
        }
    }

}
