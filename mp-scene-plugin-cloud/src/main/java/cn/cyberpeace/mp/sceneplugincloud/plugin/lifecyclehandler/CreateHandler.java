package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.CollectionUtils;
import cn.cyberpeace.mp.base.util.basic.IntUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
import cn.cyberpeace.mp.base.util.mptool.ConcurrentHashSet;
import cn.cyberpeace.mp.base.util.mptool.FileNameUtil;
import cn.cyberpeace.mp.common.config.CommonAppConfig;
import cn.cyberpeace.mp.scene.context.SceneContextHolder;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.core.event.base.SceneEventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.util.*;
import cn.cyberpeace.mp.sceneplugincloud.bo.*;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.*;
import cn.cyberpeace.mp.sceneplugincloud.constant.*;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementLogicalSwitchModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.device.Device;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.device.DeviceClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementLogicalSwitchMapper;
import cn.cyberpeace.mp.sceneplugincloud.util.ElementTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
public class CreateHandler extends HandlerCommon {

    private final VirtualSwitch vsClient = new VirtualSwitch();
    private final VirtualRouter vrClient = new VirtualRouter();
    private final VirtualFirewall vfClient = new VirtualFirewall();
    private final VirtualMachine vmClient = new VirtualMachine();
    protected final VirtualPort vpClient = new VirtualPort();
    private final Container containerClient = new Container();
    private final EntityNode enClient = new EntityNode();
    private final Remote remoteClient = new Remote();
    private final Npc npcClient = new Npc();
    private final VmRouter vmRouter = new VmRouter();
    private VirtualDynamips vdClient = new VirtualDynamips();
    private DeviceClient deviceClient = new DeviceClient();

    private final Map<String, BaseNpcData.NetWorkNpcInfo> network2Info = new ConcurrentHashMap<>();

    private final Map<String, LogicalSwitchCreateData> network2LogicalSwitch = new ConcurrentHashMap<>();

    private final Set<EntityNodeCreateData> entityCreateData = new ConcurrentHashSet<>();

    private final Random random = new Random();


    public static class SceneProcess {
        public static final SceneEventProcess CREATE_AUXILIARY_NETWORKS = new SceneEventProcess("create_auxiliary_networks", "sceneplugincloud.event.create_auxiliary_network", "create_network", 1);
        public static final SceneEventProcess CREATE_NODE_TO_EXTERNAL_ROUTER = new SceneEventProcess("create_node_to_external_router", "sceneplugincloud.event.create_node_to_external_router", "create_network", 1);
        public static final SceneEventProcess CREATE_GATEWAYS = new SceneEventProcess("create_gateways", "sceneplugincloud.event.create_gateway", "create_gateway", 1);
        public static final SceneEventProcess CREATE_FIREWALLS = new SceneEventProcess("create_firewalls", "sceneplugincloud.event.create_firewall", "create_gateway", 2);
        public static final SceneEventProcess CREATE_TERMINALS = new SceneEventProcess("create_terminals", "sceneplugincloud.event.create_terminal", "create_instance", 1);

        public static final EventProcess CREATE_LOGICAL_SWITCHES = new EventProcess("create_logical_switches", "sceneplugincloud.event.create_logical_switch");
        public static final EventProcess CREATE_ENTITIES = new EventProcess("bind_physical_port", "sceneplugincloud.event.bind_physical_port");


        public static final SceneEventProcess CREATE_NPCS = new SceneEventProcess("create_npcs", "sceneplugincloud.event.create_npcs", "create_instance", 1);
        public static final SceneEventProcess CONFIGURE = new SceneEventProcess("configure", "sceneplugincloud.event.configure", "config", 1);
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
        public static final EventProcess CREATE_DYNAMIC = new EventProcess("create_dynamic", "sceneplugincloud.event.create_dynamic");
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
                    // 如果未设置端口 则使用一个null的元素,使用统一的一个循环
                    List<String> sourcePorts = Arrays.asList(new String[]{null});
                    List<String> desPorts = Arrays.asList(new String[]{null});
                    if (StringUtils.isNotEmpty(srcRule.getSourcePort())) {
                        String[] portsArray = srcRule.getSourcePort().split(",");
                        if (portsArray != null && portsArray.length > 0) {
                            sourcePorts = Arrays.asList(portsArray);
                        }
                    }
                    if (StringUtils.isNotEmpty(srcRule.getDestinationPort())) {
                        String[] portsArray = srcRule.getDestinationPort().split(",");
                        if (portsArray != null && portsArray.length > 0) {
                            desPorts = Arrays.asList(portsArray);
                        }
                    }
                    for (String sourcePort : sourcePorts) {
                        for (String desPort : desPorts) {
                            String direction = srcRule.getDirection();
                            VirtualFirewallCreateData.Rule rule = new VirtualFirewallCreateData.Rule();
                            rule.setSourceIpAddress(srcRule.getSourceIpAddress());
                            rule.setDestinationIpAddress(srcRule.getDestinationIpAddress());
                            rule.setSourcePort(sourcePort);
                            rule.setDestinationPort(desPort);
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
                        }
                    }
                });
            }
            CloudFirewallRules cloudFirewallRules = new CloudFirewallRules();
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

    protected void createAuxiliaryNetwork(CloudPluginSceneElementBO cloudElement) {
        List<ConnectedNetworkBO> networks = cloudElement.getDeriveNetworks();
        int index = 1;
        for (ConnectedNetworkBO connectedNetwork : networks) {
            if (StringUtils.isNotEmpty(connectedNetwork.network.getResourceId())) {
                connectedNetwork.getFromPorts().forEach(p -> {
                    var pluginPort = getPluginHandler().getPort(p.getId());
                    if (pluginPort != null) {
                        pluginPort.setAuxiliaryNetworkId(connectedNetwork.network.getResourceId());
                    }
                });
                connectedNetwork.getToPorts().forEach(p -> {
                    var pluginPort = getPluginHandler().getPort(p.getId());
                    if (pluginPort != null) {
                        pluginPort.setAuxiliaryNetworkId(connectedNetwork.network.getResourceId());
                    }
                });
                continue;
            }
            var srcElement = cloudElement.getSrcElement();
            var createData = new VirtualSwitchCreateData();
            createData.setName(HandlerUtils.genResourceName(srcElement.getEid(), index));
            createData.setCidr(connectedNetwork.network.getCidr());
            createData.setGateway(TextUtils.emptyNull(connectedNetwork.network.getGateway()));
            createData.setMtu(StringUtils.isEmpty(connectedNetwork.network.getMtu()) ? "1500" : connectedNetwork.network.getMtu());
            createData.setSubnetName(StringUtils.isEmpty(connectedNetwork.network.getSubnetName()) ? TextUtils.uuid() : connectedNetwork.network.getSubnetName());
            createData.setSubnetDns(CollectionUtils.emptyNull(connectedNetwork.network.getDnsNameservers()));

            var virtualSwitch = vsClient.create(createData);

            connectedNetwork.network.setResourceId(virtualSwitch.getId());
            connectedNetwork.network.setSubnetName(virtualSwitch.getSubnetName());
            if (cloudElement.getVirtualType() != null && cloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH) {
                cloudElement.setResourceId(virtualSwitch.getId());
            }
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

            // 以上逻辑为创建一个网络（对应云平台的虚拟交换），并将网络资源的ID设置到当前网元对象和当前连线的两端连接端口上
            // 下面还需要创建路由（非网关类设备）
            createAuxiliaryNetworkRouter(cloudElement, connectedNetwork.network, index++);
        }
    }

    protected void createAuxiliaryNetworkRouter(CloudPluginSceneElementBO cloudElement, CloudPluginSceneElementNetworkBO network, int index) {
        if (!Boolean.TRUE.equals(network.getAuxiliaryNetworkRouterOn())) {
            return;
        }
        if (BaseCloudPluginSceneElementModel.ImageType.VM.equals(cloudElement.getImageType()) &&
                BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH == cloudElement.getRole()) {
            return;
        }
        // 创建辅助路由
        var createData = new VirtualRouterCreateData();
        createData.setName(HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid(), index));
        var virtualRouterNic = new VirtualRouterNic();
        virtualRouterNic.setId(network.getResourceId());
        virtualRouterNic.setFixedIp(TextUtils.emptyNull(network.getAuxiliaryNetworkRouterIp()));
        virtualRouterNic.setCidr(network.getCidr());
        createData.setNetworks(List.of(virtualRouterNic));
        var virtualRouter = vrClient.create(createData);

        // 将网络和虚拟路由关联
        network.setAuxiliaryNetworkRouterResourceId(virtualRouter.getId());
    }

    /**
     * 创建默认路由模拟的三层防火墙
     */
    private void createVirtualRouterFirewall(CloudPluginSceneElementBO cloudElement) {
        SceneElementBO srcElement = cloudElement.getSrcElement();
        List<FirewallRule> srcRules = cloudElement.getFirewallRules();
        // 创建节点全局防火墙，对节点上所有端口生效，增加端口注意同步此数据
        if (srcRules != null && !srcRules.isEmpty()) {
            // 防火墙的虚拟路由端口
            Set<String> targetPorts = new HashSet<>();
            List<CloudPluginSceneElementPortBO> cloudPorts = getPluginHandler().getPorts(cloudElement);
            for (CloudPluginSceneElementPortBO cloudPort : cloudPorts) {
                if (StringUtils.isEmpty(cloudPort.getResourceId())) {
                    continue;
                }
                targetPorts.add(cloudPort.getResourceId());
            }

            if (!targetPorts.isEmpty()) {
                VirtualFirewallCreateData createData = new VirtualFirewallCreateData();
                createData.setName(HandlerUtils.genResourceName(srcElement.getEid()));
                CloudFirewallRules cloudFirewallRules = CloudFirewallRules.fromSrcRules(srcRules);
                createData.setIngressRules(cloudFirewallRules.getIngressRules());
                createData.setEgressRules(cloudFirewallRules.getEgressRules());
                createData.setPorts(CollectionUtils.emptyNull(new ArrayList<>(targetPorts)));

                VirtualFirewallCreateData virtualFirewall = vfClient.create(createData);
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
            CloudPluginSceneElementPortBO cloudPort = getPluginHandler().getPort(srcPort.getId());
            if (cloudPort == null) {
                return;
            }
            cloudPorts.add(cloudPort);
            if (!StringUtils.isEmpty(cloudPort.getResourceId())) {
                targetPorts.add(cloudPort.getResourceId());
            }
            List<FirewallRule> portSrcRules = cloudPort.getFirewallRules();
            if (portSrcRules != null) {
                srcRules.addAll(portSrcRules);
            }
        });
        if (targetPorts.isEmpty() || srcRules == null || srcRules.isEmpty()) {
            return;
        }

        SceneElementBO srcElement = cloudElement.getSrcElement();
        VirtualFirewallCreateData createData = new VirtualFirewallCreateData();
        createData.setName(HandlerUtils.genResourceName(srcElement.getEid()));
        CloudFirewallRules cloudFirewallRules = CloudFirewallRules.fromSrcRules(srcRules);
        createData.setIngressRules(cloudFirewallRules.getIngressRules());
        createData.setEgressRules(cloudFirewallRules.getEgressRules());
        createData.setPorts(CollectionUtils.emptyNull(new ArrayList<>(targetPorts)));

        VirtualFirewallCreateData virtualFirewall = vfClient.create(createData);
        cloudPorts.forEach(cloudPort -> {
            cloudPort.setFirewallResourceId(virtualFirewall.getId());
        });
    }

    /**
     * @param cloudElement
     * @return List<VirtualNic>
     * @description: 生成虚拟网卡
     */
    private List<VirtualNic> genVirutalNics(CloudPluginSceneElementBO cloudElement) {
        List<VirtualNic> virtualNics = new ArrayList<>();
        // 添加管理网卡
        if (Boolean.TRUE.equals(cloudElement.getEnableMgmt())) {
            VirtualNic virtualNic = new VirtualNic();
            virtualNic.setNetworkId(CloudSingleton.cloudAppConfig.getMgmtNetId());
            virtualNics.add(virtualNic);
        }
        //所有网络，包含生成的网络和连接的网络
        List<ConnectedNetworkBO> allNetworks = new ArrayList<>();
        allNetworks.addAll(cloudElement.getDeriveNetworks());
        allNetworks.addAll(cloudElement.getAccessNetworks());
        //节点端口存在
        if (cloudElement.getSrcElement().getPorts() != null && cloudElement.getSrcElement().getPorts().size() > 0) {
            Map<String, ConnectedNetworkBO> portId2Net = new HashMap<>();
            allNetworks.forEach(net -> portId2Net.put(net.getFromPorts().get(0).getId(), net));
            List<ConnectedNetworkBO> orderNetwork = new ArrayList<>();
            // 端口使用前再执行一次排序
            Collections.sort(cloudElement.getSrcElement().getPorts(), (p1, p2) -> {
                if (p1.getOrder() == null || p2.getOrder() == null) {
                    return 0;
                }
                return p1.getOrder() - p2.getOrder();
            });
            for (SceneElementPortBO port : cloudElement.getSrcElement().getPorts()) {
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
            virtualNic.setFwdnsInterfaceIp(getHandlerUtils().getFwdnsInterfaceIp(cloudElement.getSrcElement().getId(), connectedNetwork.getNetwork().getId()));
            virtualNic.setSpecial(false);
            virtualNics.add(virtualNic);
        }
        return virtualNics;
    }

    /**
     * @param connectedNetwork
     * @return VirtualNic
     * @description: 获得连接网络的虚拟网卡相关信息
     */
    private VirtualNic genConnectedNetwork(ConnectedNetworkBO connectedNetwork) {
        var network = connectedNetwork.network;
        List<SceneElementPortBO> fromPorts = connectedNetwork.getFromPorts();
        // 找到第一个有ip值的端口,否则使用第一个端口
        SceneElementPortBO samplePort = fromPorts.get(0);
        for (SceneElementPortBO sceneElementPortBO : fromPorts) {
            if (StringUtils.isNotEmpty(sceneElementPortBO.getIp())) {
                samplePort = sceneElementPortBO;
                break;
            }
        }
        CloudPluginSceneElementPortBO sampleCloudPort = getPluginHandler().getPort(samplePort.getId());
        SceneElementPortConnectionBO portConnection = (SceneElementPortConnectionBO) getPluginHandler().getPortConnection(samplePort.getId());
        var virtualNic = new VirtualNic();
        virtualNic.setNetworkId(network.getResourceId());
        virtualNic.setCidr(network.getCidr());
        virtualNic.setGateway(network.getGateway());
        if (StringUtils.isNotEmpty(sampleCloudPort.getConnectMode())) {
            virtualNic.setVlanMode(sampleCloudPort.getConnectMode());
        }
        virtualNic.setFixedIp(TextUtils.emptyNull(samplePort.getIp()));
        virtualNic.setMacAddress(TextUtils.emptyNull(samplePort.getMacAddr()));
        int egress;
        if (samplePort.getEgress() != null && portConnection != null && portConnection.getBandwidth() != null &&
                samplePort.getEgress() != -1 && portConnection.getBandwidth() != -1) {
            egress = Math.min(samplePort.getEgress(), portConnection.getBandwidth());
        } else {
            egress = portConnection == null || portConnection.getBandwidth() == null ? samplePort.getEgress() : portConnection.getBandwidth();
        }

        int delay = portConnection == null || portConnection.getDelay() == null ? samplePort.getDelay() : portConnection.getDelay();

        int loss = portConnection == null || portConnection.getLoss() == null ? samplePort.getLoss() : portConnection.getLoss();
        if (egress > 0 || delay > 0 || loss > 0) {
            var qosRule = new VirtualNic.QosRule();
            if (egress > 0) {
                qosRule.setRate(egress * 1024);
            }
            if (delay > 0) {
                qosRule.setDelay(delay);
            }
            if (loss > 0) {
                qosRule.setLossPercent(loss);
            }
            virtualNic.setQosRule(qosRule);
        }

        virtualNic.setFirewallId(null);
        return virtualNic;
    }

    /**
     * @description: 生成实体设备网口
     * @param cloudElement
     * @return List<Interface>
     */
    private List<EntityNodeCreateData.Interface> genEntityInterfaces(CloudPluginSceneElementBO cloudElement) {
        List<EntityNodeCreateData.Interface> entityInterfaces = new ArrayList<>();
        //节点信息
        SceneElementBO srcElement = cloudElement.getSrcElement();
        //端口信息
        List<SceneElementPortBO> ports = srcElement.getPorts();
        if (ports == null) {
            return entityInterfaces;
        }
        //网络业务信息
        List<ConnectedNetworkBO> networks = new ArrayList<>();
        networks.addAll(cloudElement.getDeriveNetworks());
        networks.addAll(cloudElement.getAccessNetworks());
        boolean configSnmpFinished = false;
        for (ConnectedNetworkBO connectNetwork : networks) {
            SceneElementPortBO port = connectNetwork.getFromPorts().get(0);
            CloudPluginSceneElementPortBO cloudPort = getPluginHandler().getPort(port.getId());
            // 没有定义的端口补全
            if (cloudPort == null) {
                cloudPort = new CloudPluginSceneElementPortBO();
                getPluginHandler().addPort(port.getId(), cloudPort);
            }
            cloudPort.setAuxiliaryNetworkId(connectNetwork.getNetwork().getResourceId());

            EntityNodeCreateData.Interface entityInterface = new EntityNodeCreateData.Interface();
            entityInterface.setNetworkId(connectNetwork.getNetwork().getResourceId());
            entityInterface.setInterfaceName(port.getName());

            List<CloudAccessMode> accessModes = cloudPort.getAccessModes();
            if (accessModes != null && !accessModes.isEmpty()) {
                entityInterface.setAccessModes(accessModes.stream().map(accessMode -> {
                    EntityNodeCreateData.Interface.AccessMode accessModeParam = new EntityNodeCreateData.Interface.AccessMode();
                    accessModeParam.setProtocol(accessMode.getProtocol());
                    if (accessMode.getPort() == null) {
                        accessModeParam.setPort(AccessMode.Protocol.DEFAULT_PORT_MAP.getOrDefault(accessMode.getProtocol(), null));
                    } else {
                        accessModeParam.setPort(accessMode.getPort());
                    }
                    accessModeParam.setUsername(accessMode.getUsername());
                    accessModeParam.setPassword(accessMode.getPassword());
                    accessModeParam.setEnablePortMapping(accessMode.getProxy());
                    if (StringUtils.isNotBlank(accessMode.getAccessIp())) {
                        accessModeParam.setIpAddress(TextUtils.emptyNull(accessMode.getAccessIp()));
                    } else {
                        accessModeParam.setIpAddress(TextUtils.emptyNull(port.getIp()));
                    }
                    accessModeParam.setMacAddress(TextUtils.emptyNull(port.getMacAddr()));
                    return accessModeParam;
                }).collect(Collectors.toList()));
            }
            //不存在任何接入方式则初始化一个空的
            if(CollectionUtil.isEmpty(entityInterface.getAccessModes())){
                entityInterface.setAccessModes(new ArrayList<>());
            }
            //配置勾选了snmp，则单独处理snmp协议
            SnmpConfig snmpConfig = cloudElement.getSnmpConfig();
            if (!Objects.isNull(snmpConfig) && Boolean.TRUE.equals(snmpConfig.getConfigSwitch()) && !configSnmpFinished) {
                EntityNodeCreateData.Interface.AccessMode accessMode = new EntityNodeCreateData.Interface.AccessMode();
                accessMode.setProtocol(AccessMode.Protocol.SNMP);
                accessMode.setPort(AccessMode.Protocol.DEFAULT_PORT_MAP.get(AccessMode.Protocol.SNMP));
                accessMode.setIpAddress(TextUtils.emptyNull(port.getIp()));
                accessMode.setMacAddress(TextUtils.emptyNull(port.getMacAddr()));
                entityInterface.getAccessModes().add(accessMode);
            }
            entityInterface.setFwdnsInterfaceIp(getHandlerUtils().getFwdnsInterfaceIp(srcElement.getId(), connectNetwork.getNetwork().getId()));
            entityInterfaces.add(entityInterface);
        }

        return entityInterfaces;
    }

    private String combineInitCommand(CloudPluginSceneElementBO cloudElement) {
        SceneElementBO srcElement = cloudElement.getSrcElement();
        List<String> commands = new ArrayList<>();

        commands.addAll(behaviorSupportInitCommand(srcElement));
        commands.addAll(getBasDeviceInitCommand(srcElement));

        String standardSystemType = SystemTypeUtils.getStandardSystemType(srcElement.getSystemType());
        SceneBO srcScene = getPluginHandler().getSrcScene();
        String sceneId = srcScene.getEid();
        String sceneFileUrl = srcScene.getFile();
        // 下载场景文件，什么情况下需要这个？
        if (!StringUtils.isEmpty(sceneFileUrl)) {
            commands.addAll(SceneFileUtils.genSceneFileInitCommand(standardSystemType, sceneFileUrl, sceneId));
        }
        //节点文件，什么情况下需要？固定从169.254.169.254下载文件, 如果功能启用，要修改成mp下载
        String elementFileUrl = srcElement.getFile();
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

        boolean isWindows = SystemTypeCommandUtils.isWindows(standardSystemType);
        if (!isWindows && cloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
            commands.add("echo \"169.254.169.254     res.cr.com\" >>/etc/hosts");
        }


        // TODO: 快速创建安装
        if (Boolean.TRUE.equals(cloudElement.getQuickCreateSupport())) {
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(cloudElement.getQuickCreateParams())) {
                cloudElement.getQuickCreateParams().forEach(p -> {
                    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(p.getResourceList())) {
                        List<InstallerUtils.Installer> installers = p.getResourceList().stream().map(r -> {
                            InstallerUtils.Installer installer = new InstallerUtils.Installer();
                            installer.setFile(r.getFile());
                            installer.setName(r.getUserFilename());
                            installer.setInstallCommand(r.getCommand());
                            return installer;
                        }).collect(Collectors.toList());
                        commands.addAll(InstallerUtils.generateInstallerInstallCommand(standardSystemType, installers));
                    }
                    if (p.getDockerInstallerData() != null && StringUtils.isNotEmpty(p.getDockerInstallerData().getDockerUrl())) {
                        InstallerUtils.Installer installer = new InstallerUtils.Installer();
                        installer.setDocker(true);
                        installer.setFile(p.getDockerInstallerData().getDockerUrl());
                        installer.setName(p.getDockerInstallerData().getUserFilename());
                        installer.setFilePathPrefix(CloudSingleton.cloudAppConfig.getQuickCreateDockerResourceMetaServer());
                        if (StringUtils.isEmpty(installer.getName())) {
                            installer.setName(FileNameUtil.getName(installer.getFile()));
                        }
                        installer.setInstallCommand(DockerInstallCommandUtils.genDockerRunDefaultCmd(p.getDockerInstallerData().getStartUpParameterList()));
                        commands.addAll(InstallerUtils.generateInstallerInstallCommand(standardSystemType, List.of(installer)));
                    }
                });
            }
        }

        CloudInitConfigCommon.Builder builder = CloudInitConfigCommon.builder(standardSystemType);
        if (cloudElement.getSrcElement() != null && cloudElement.getSrcElement().getPorts() != null) {
            Map<String, String> userAndPassword = new HashMap<>();
            cloudElement.getSrcElement().getPorts().forEach(p -> {
                if (p.getAccessModes() != null) {
                    p.getAccessModes().forEach(ac -> {
                        if (StringUtils.isNotEmpty(ac.getUsername()) && StringUtils.isNotEmpty(ac.getPassword()) && CloudInitConfigCommon.canChangeUser(ac.getProtocol())) {
                            userAndPassword.put(ac.getUsername(), ac.getPassword());
                            if (ac.getUsername().equals("root")) {
                                builder.setRootPassword(ac.getPassword());
                            }
                        }
                    });
                }
            });
            userAndPassword.entrySet().forEach(e -> builder.addUser(e.getKey(), e.getValue()));
        }
        builder.addAllInitCommands(commands);
        if (!getHandlerUtils().getDnsIpAndDomains().isEmpty()) {
            builder.addAllDns(getHandlerUtils().getDnsIpAndDomains());
        }
        List<String> cloudInitCommands = builder.build();
        String command = SystemTypeCommandUtils.joinCommands(standardSystemType, cloudInitCommands.toArray(new String[0]));
        if (!StringUtils.isEmpty(command)) {
            if (SystemTypeCommandUtils.isWindows(standardSystemType)) {
                command = SystemTypeCommandUtils.appendBeforeCommand(command, "rem cmd", standardSystemType);
            } else {
                command = SystemTypeCommandUtils.appendBeforeCommand(command, "#!/bin/bash", standardSystemType);
            }
        }

        return command;
    }

    private List<String> behaviorSupportInitCommand(SceneElementBO sceneElementBO) {
        List<String> behaviorInitCommand = new ArrayList<>();
        // 支持行为
        if (Boolean.TRUE.equals(sceneElementBO.getBehaveSupport())) {
            behaviorInitCommand.add(String.format("python C:\\init.py %s %s %s",
                    SceneContextHolder.getCsceneInstanceId(), sceneElementBO.getId(), BeanCommon.getBean(CommonAppConfig.class).getMpIp()));

        }
        return behaviorInitCommand;

    }

    private List<String> getBasDeviceInitCommand(SceneElementBO sceneElementBO) {
        List<String> basDeviceInitCommand = new ArrayList<>();

        if (Boolean.TRUE.equals(sceneElementBO.getAttackMockDeviceFlag())) {
            String standardSystemType = SystemTypeUtils.getStandardSystemType(sceneElementBO.getSystemType());
            if (SystemTypeCommandUtils.isWindows(standardSystemType)) {
                basDeviceInitCommand.add("@echo off");
                basDeviceInitCommand.add("setlocal enabledelayedexpansion");
                basDeviceInitCommand.add("set \"search_ip=server_ip\")");
                basDeviceInitCommand.add(String.format("set \"replace_ip=%s\"", BeanCommon.getBean(CommonAppConfig.class).getBasIp()));
                basDeviceInitCommand.add("set \"search_port=server_port\"");
                basDeviceInitCommand.add(String.format("set \"replace_port=%s\"", BeanCommon.getBean(CommonAppConfig.class).getBasPort()));

                basDeviceInitCommand.add("set \"search_template_id=mp_scene_template_id\"");
                basDeviceInitCommand.add(String.format("set \"replace_template_id=%s\"", SceneContextHolder.getCsceneInstanceId()));
                basDeviceInitCommand.add("set \"input=C:\\cpbas\\client\\application.yml\"");

                basDeviceInitCommand.add("set \"output=C:\\cpbas\\client\\application.yml.tmp\"");
                basDeviceInitCommand.add("(for /f \"tokens=* delims=\" %%i in ('type \"%input%\"') do (");
                basDeviceInitCommand.add("    set \"line=%%i\"");
                basDeviceInitCommand.add("    set \"line=!line:%search_ip%=%replace_ip%!\"");
                basDeviceInitCommand.add("    set \"line=!line:%search_port%=%replace_port%!\"");
                basDeviceInitCommand.add("    set \"line=!line:%search_template_id%=%replace_template_id%!\"");

                basDeviceInitCommand.add("    echo(!line!");
                basDeviceInitCommand.add(")) > \"%output%\"");
                basDeviceInitCommand.add("move /Y \"%output%\" \"%input%\"");
                basDeviceInitCommand.add("endlocal");

            } else if (SystemTypeCommandUtils.isLinux(standardSystemType)) {
                basDeviceInitCommand.add(String.format("sed -i \"/platform_address:/s/server_ip/%s/\" /opt/cpbas/client/application.yml",
                        BeanCommon.getBean(CommonAppConfig.class).getBasIp()));
                basDeviceInitCommand.add(String.format("sed -i \"/platform_port:/s/server_port/%s/\" /opt/cpbas/client/application.yml",
                        BeanCommon.getBean(CommonAppConfig.class).getBasPort()));
                basDeviceInitCommand.add(String.format("sed -i \"/template_id:/s/mp_scene_template_id/%s/\" /opt/cpbas/client/application.yml",
                        SceneContextHolder.getCsceneInstanceId()));

            } else {
                log.error("bas device system not support {}", sceneElementBO);
            }
        }
        return basDeviceInitCommand;
    }

    /**
     * 容器不保证有 curl/unzip 无法正常通过系统命令方式初始化
     * 容器默认linux
     */
    private void execContainerInitCommand(CloudPluginSceneElementBO cloudElement) {
        String containerId = cloudElement.getResourceId();
        SceneElementBO srcElement = cloudElement.getSrcElement();
        String standardSystemType = SystemTypeUtils.getStandardSystemType(srcElement.getSystemType());
        SceneBO srcScene = getPluginHandler().getSrcScene();
        String sceneId = srcScene.getEid();

        List<String> extraCommands = new ArrayList<>();
        String sceneFileUrl = srcScene.getFile();
        String sceneUnzipDir = null;
        if (!StringUtils.isEmpty(sceneFileUrl)) {
            sceneUnzipDir = SceneFileUtils.getSceneFileUnzipDir(standardSystemType, sceneId);
            extraCommands.add(CommandUtils.linuxMkDir(sceneUnzipDir));
        }
        String elementFileUrl = srcElement.getFile();
        String elementUnzipDir = null;
        if (!StringUtils.isEmpty(elementFileUrl)) {
            elementUnzipDir = SceneFileUtils.getElementFileUnzipDir(standardSystemType, sceneId, srcElement.getEid());
            extraCommands.add(CommandUtils.linuxMkDir(elementUnzipDir));
        }

        List<String> commands = new ArrayList<>();
        List<String> initCommands = srcElement.getInitCommands();
        if (initCommands != null) {
            initCommands.forEach(initCommand -> {
                List<String> parsedCommands = SceneCommandUtils.parseCommand(initCommand, sceneId, srcElement.getEid(),
                        standardSystemType);
                if (!parsedCommands.isEmpty()) {
                    String inlineCommand = SystemTypeCommandUtils.inlineJoinCommands(standardSystemType,
                            parsedCommands.toArray(new String[0]));
                    commands.add(inlineCommand);
                }
            });
        }

        if (!StringUtils.isEmpty(sceneFileUrl) || !StringUtils.isEmpty(elementFileUrl) || !commands.isEmpty()) {
            // 准备容器的unzip工具,需要尝试apk的安装,而且容器本身可能自带,所以忽略失败
            try {
                ContainerFileUtils.prepareUnzip(containerClient, containerId, extraCommands);
            } catch (Exception e) {
                log.warn("Scene({}) cloud container({}) element prepare unzip failed: ", sceneId, containerId, e.getMessage());
            }
        }

        if (!StringUtils.isEmpty(sceneFileUrl)) {
            ContainerFileUtils.prepareZipFile(containerClient, containerId, sceneFileUrl, standardSystemType, sceneUnzipDir, true);
        }
        if (!StringUtils.isEmpty(elementFileUrl)) {
            ContainerFileUtils.prepareZipFile(containerClient, containerId, elementFileUrl, standardSystemType, elementUnzipDir, true);
        }

        if (!getHandlerUtils().getDnsIpAndDomains().isEmpty()) {
            for (String ipAndDomain : getHandlerUtils().getDnsIpAndDomains()) {
                commands.add(String.format("echo %s >> %s", ipAndDomain, CloudInitConfigCommon.LINUX_HOSTS_PATH));
            }
        }

        if (commands != null && !commands.isEmpty()) {
            boolean executeOnce = true;
            for (String command : commands) {
                if (command.startsWith(ContainerFileUtils.CONTAINER_COPY_FILE_PREFIX)) {
                    executeOnce = false;
                    break;
                }
            }
            if (executeOnce) {
                List<String> finnalCommands = new ArrayList<>();
                for (String command : commands) {
                    if (StringUtils.isNotEmpty(command)) {
                        String c = command.trim();
                        if (c.endsWith(";")) {
                            finnalCommands.add(c.substring(0, c.length() - 1));
                        } else {
                            finnalCommands.add(c);
                        }
                    }
                }
                containerClient.executeCommand(containerId, SystemTypeCommandUtils.inlineJoinCommands(standardSystemType,
                        finnalCommands.toArray(new String[0])));
            } else {
                for (String command : commands) {
                    if (command.startsWith(ContainerFileUtils.CONTAINER_COPY_FILE_PREFIX)) {
                        String[] path = command.split(StringUtils.SPACE);
                        for (int i = 0; i < 3; i++) {
                            try {
                                ContainerFileUtils.prepareLocalFile(containerClient, containerId, path[1], path[2]);
                                break;
                            } catch (Exception e) {
                                log.error("Scene({}) cloud container({}) element copy file failed", sceneId, containerId, e);
                            }
                        }
                    } else if (command.contains("/opt/wazuha/stop.sh")) {
                        containerClient.executeCommand(containerId, "echo \"" + command + "\" >> /tmp/tmp_installers/install_wazuh.sh");
                        containerClient.executeCommand(containerId, "nohup sh /tmp/tmp_installers/install_wazuh.sh > nohup.out 2>&1 &");

                        try {
                            // 安装探针失败要重试
                            List<String> checks = new ArrayList<>(){{
                                this.add("sleep 180 ;");
                                this.add("if [ -e /var/ossec/bin/tools/09ffcc4a67ef4accb407a1555629d0f8.py ] ;");
                                this.add("then echo \"wazuh ok\"");
                                this.add("else sh install_wazuh.sh ;");
                                this.add("fi");
                            }};
                            containerClient.executeCommand(containerId, "echo \"" + StringUtils.join(checks,"\n") + "\" >>" + " " + "/tmp/tmp_installers" + "/check_wazuh.sh");
                            containerClient.executeCommand(containerId, "nohup sh /tmp/tmp_installers/check_wazuh.sh > nohup.out 2>&1 &");
                        }catch (Exception e){
                            log.error("failed to add wazuh check script ");
                        }
                    } else {
                        containerClient.executeCommand(containerId, command);
                    }
                }
            }
        }
    }

    private void receiveVirtualPortInfo(CloudPluginSceneElementBO cloudElement) {
        String ip = cloudElement.getSrcElement().getPorts().get(0).getIp();
        List<CloudPluginSceneElementPortBO> ports = getPluginHandler().getPorts(cloudElement);
        if (ports != null) {
            for (CloudPluginSceneElementPortBO port : ports) {
                if (port.getAccessModes() != null) {
                    for (CloudAccessMode access : port.getAccessModes()) {
                        access.setAccessIp(ip);
                        access.setAccessPort(access.getPort());
                        access.setAvailable(true);
                    }
                }
            }
        }
    }

    private void receiveVirtualPortInfo(CloudPluginSceneElementBO cloudElement, List<VirtualNic> virtualNics) {
        if (virtualNics == null) {
            return;
        }

        List<CloudPluginSceneElementPortBO> cloudPorts = getPluginHandler().getPorts(cloudElement);
        if (cloudPorts.isEmpty()) {
            return;
        }

        Map<String, List<CloudPluginSceneElementPortBO>> networkIdCloudPortsMap = new HashMap<>();
        List<ConnectedNetworkBO> networks = new ArrayList<>();
        networks.addAll(cloudElement.getDeriveNetworks());
        networks.addAll(cloudElement.getAccessNetworks());
        for (ConnectedNetworkBO network : networks) {
            String auxiliaryNetworkId = network.getNetwork().getResourceId();
            List<CloudPluginSceneElementPortBO> networkIdCloudPorts = networkIdCloudPortsMap.getOrDefault(
                    auxiliaryNetworkId, new ArrayList<>());
            network.getFromPorts().forEach(port -> {
                CloudPluginSceneElementPortBO pluginPort = getPluginHandler().getPort(port.getId());
                networkIdCloudPorts.add(pluginPort);
            });
            networkIdCloudPortsMap.put(auxiliaryNetworkId, networkIdCloudPorts);
        }

        virtualNics.forEach(virtualNic -> {
            String networkId = virtualNic.getNetworkId();
            // 管理网卡判断
            if (Boolean.TRUE.equals(cloudElement.getEnableMgmt())
                    && CloudSingleton.cloudAppConfig.getMgmtNetId().equals(networkId)) {
                cloudElement.setMgmtPortResourceId(virtualNic.getPortId());
                cloudElement.setMgmtIp(virtualNic.getFixedIp());
                cloudElement.setMgmtMacAddr(virtualNic.getMacAddress());
                return;
            }

            List<CloudPluginSceneElementPortBO> networkCloudPorts = networkIdCloudPortsMap.getOrDefault(networkId, null);
            if (networkCloudPorts == null) {
                return;
            }

            // 多个端口连到同一个网络上的时候暂时当做一个端口处理
            networkCloudPorts.forEach(cloudPort -> {
                SceneElementPortBO srcPort = cloudPort.getSrcPort();
                srcPort.setIp(virtualNic.getFixedIp());
                srcPort.setMacAddr(virtualNic.getMacAddress());

                cloudPort.setResourceId(virtualNic.getPortId());
                cloudPort.setQosId(virtualNic.getQosId());
                cloudPort.setFloatingIp(virtualNic.getFloatingip());
                cloudPort.setFloatingIpId(virtualNic.getFloatingipId());
                cloudPort.setAuxiliaryNetworkId(virtualNic.getNetworkId());
                cloudPort.setVnicId(virtualNic.getVnicId());


                List<VirtualNic.AccessMode> accessModesData = virtualNic.getAccessModes();
                if (accessModesData != null && !accessModesData.isEmpty()) {
                    Map<String, VirtualNic.AccessMode> accessModeDataMap = new HashMap<>();
                    for (VirtualNic.AccessMode accessMode : accessModesData) {
                        accessModeDataMap.put(this.genAccessModeKey(accessMode), accessMode);
                    }
                    cloudPort.getAccessModes().forEach(accessMode -> {
                        VirtualNic.AccessMode accessModeData = accessModeDataMap.getOrDefault(genAccessModeKey(accessMode), null);
                        if (accessModeData == null) {
                            return;
                        }

                        VirtualNic.AccessMode.Connection connection = accessModeData.getConnection();
                        if (connection != null) {
                            accessMode.setConnectionUser(TextUtils.emptyNull(getHandlerUtils().getRequestUser()));
                            accessMode.setConnectionId(connection.getConnectionId());
                            accessMode.setConnectionUrl(connection.getUrl());
                        }

                        VirtualNic.AccessMode.PortMapping portMapping = accessModeData.getPortMapping();
                        if (portMapping != null) {
                            accessMode.setProxyId(portMapping.getId());
                            accessMode.setAccessIp(portMapping.getHost());
                            accessMode.setAccessPort(portMapping.getHostPort());
                        } else {
                            accessMode.setAccessIp(accessModeData.getIpAddress());
                            accessMode.setAccessPort(accessModeData.getPort());
                        }
                        if (StringUtils.isEmpty(accessMode.getAccessIp()) && cloudPort.getSrcPort() != null) {
                            accessMode.setAccessIp(cloudPort.getSrcPort().getIp());
                        }
                        if (Boolean.TRUE.equals(accessMode.getProxy()) && CloudSingleton.cloudAppConfig.getPublicNetProxyServer()) {
                            this.trySetPublicNetProxy(accessMode);
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

        List<CloudPluginSceneElementPortBO> cloudPorts = getPluginHandler().getPorts(cloudElement);
        if (cloudPorts.isEmpty()) {
            return;
        }

        Map<String, CloudPluginSceneElementPortBO> networkIdCloudPortMap = new HashMap<>();
        cloudPorts.forEach(cloudPort -> {
            networkIdCloudPortMap.put(cloudPort.getAuxiliaryNetworkId(), cloudPort);
        });

        entityInterfaces.forEach(entityInterface -> {
            CloudPluginSceneElementPortBO cloudPort = networkIdCloudPortMap.getOrDefault(entityInterface.getNetworkId(), null);
            if (cloudPort == null) {
                return;
            }

            List<EntityNodeCreateData.Interface.AccessMode> accessModesData = entityInterface.getAccessModes();
            if (accessModesData != null && !accessModesData.isEmpty()) {
                for (EntityNodeCreateData.Interface.AccessMode accessModeData : accessModesData) {
                    if (AccessMode.Protocol.SNMP.equals(accessModeData.getProtocol())) {
                        EntityNodeCreateData.Interface.AccessMode.PortMapping portMapping = accessModeData.getPortMapping();
                        if (portMapping != null) {
                            cloudElement.getSnmpConfig().setSceneIp(portMapping.getHost());
                            cloudElement.getSnmpConfig().setScenePort(portMapping.getHostPort());
                        } else {
                            cloudElement.getSnmpConfig().setSceneIp(accessModeData.getIpAddress());
                            cloudElement.getSnmpConfig().setScenePort(accessModeData.getPort());
                        }
                        break;
                    }
                }

                Map<String, EntityNodeCreateData.Interface.AccessMode> accessModeDataMap = new HashMap<>();
                for (EntityNodeCreateData.Interface.AccessMode accessModesDatum : accessModesData) {
                    if (accessModeDataMap.put(genAccessModeKey(accessModesDatum), accessModesDatum) != null) {
                        throw new IllegalStateException("Duplicate key");
                    }
                }
                cloudPort.getAccessModes().forEach(accessMode -> {
                    EntityNodeCreateData.Interface.AccessMode accessModeData = accessModeDataMap.getOrDefault(genAccessModeKey(accessMode), null);
                    if (accessModeData == null) {
                        return;
                    }

                    EntityNodeCreateData.Interface.AccessMode.Connection connection = accessModeData.getConnection();
                    if (connection != null) {
                        accessMode.setConnectionUser(TextUtils.emptyNull(getHandlerUtils().getRequestUser()));
                        accessMode.setConnectionId(connection.getConnectionId());
                        accessMode.setConnectionUrl(connection.getUrl());
                    }
                    EntityNodeCreateData.Interface.AccessMode.PortMapping portMapping = accessModeData.getPortMapping();
                    if (portMapping != null) {
                        accessMode.setProxyId(portMapping.getId());
                        accessMode.setAccessIp(portMapping.getHost());
                        accessMode.setAccessPort(portMapping.getHostPort());
                    } else {
                        accessMode.setAccessIp(accessModeData.getIpAddress());
                        accessMode.setAccessPort(accessModeData.getPort());
                    }
                    if (StringUtils.isNotEmpty(accessModeData.getIpAddress()) && cloudPort.getSrcPort() != null) {
                        cloudPort.getSrcPort().setIp(accessModeData.getIpAddress());
                    }
                    if (Boolean.TRUE.equals(accessMode.getProxy()) && CloudSingleton.cloudAppConfig.getPublicNetProxyServer()) {
                        this.trySetPublicNetProxy(accessMode);
                    }
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
            createData.setRouteStatics(staticRoutes);
        }
        List<VirtualNic> virtualNics = this.genVirutalNics(cloudElement);
        createData.setNetwork(virtualNics);
        createData.setNetworks(virtualNics.stream().map(virtualNic -> {
            VirtualRouterNic virtualRouterNic = new VirtualRouterNic();
            virtualRouterNic.setId(virtualNic.getNetworkId());
            virtualRouterNic.setFixedIp(virtualNic.getFixedIp());
            virtualRouterNic.setCidr(virtualNic.getCidr());
            return virtualRouterNic;
        }).collect(Collectors.toList()));

        var virtualRouter = vrClient.create(createData);

        cloudElement.setResourceId(virtualRouter.getId());
        receiveVirtualRouterPortInfo(cloudElement, virtualRouter.getNetworks());
    }

    private void receiveVirtualRouterPortInfo(CloudPluginSceneElementBO cloudElement, List<VirtualRouterNic> virtualRouterNics) {
        if (virtualRouterNics == null) {
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

        virtualRouterNics.forEach(virtualRouterNic -> {
            var networkId = virtualRouterNic.getId();

            var networkCloudPorts = networkIdCloudPortsMap.getOrDefault(networkId, null);
            if (networkCloudPorts == null) {
                return;
            }

            // 多个端口连到同一个网络上的时候暂时当做一个端口处理
            networkCloudPorts.forEach(cloudPort -> {
                var srcPort = cloudPort.getSrcPort();
                // 动态更新时需要端口有ip值
                srcPort.setIp(virtualRouterNic.getFixedIp());
                srcPort.setMacAddr(virtualRouterNic.getNicMac());
            });
        });
    }

    protected void createVirtualFirewall(CloudPluginSceneElementBO cloudElement) {
        SceneElementBO srcElement = cloudElement.getSrcElement();
        VirtualFirewallCreateData createData = new VirtualFirewallCreateData();
        createData.setName(HandlerUtils.genResourceName(srcElement.getEid()));
        List<FirewallRule> srcRules = cloudElement.getFirewallRules();
        if (srcRules != null && !srcRules.isEmpty()) {
            CloudFirewallRules cloudFirewallRules = CloudFirewallRules.fromSrcRules(srcRules);
            createData.setIngressRules(cloudFirewallRules.getIngressRules());
            createData.setEgressRules(cloudFirewallRules.getEgressRules());
        }

        // 防火墙连接的虚拟路由端口
        Set<String> targetPorts = new HashSet<>();
        List<SceneElementPortBO> connectedSrcPorts = getPluginHandler().getElementConnectedSrcPorts(srcElement.getId());
        for (SceneElementPortBO connectedSrcPort : connectedSrcPorts) {
            CloudPluginSceneElementPortBO connectedCloudPort = getPluginHandler().getPort(connectedSrcPort.getId());
            if (connectedCloudPort == null || StringUtils.isEmpty(connectedCloudPort.getResourceId())) {
                continue;
            }
            SceneElementBO connectedSrcElement = getPluginHandler().getPortSrcElement(connectedSrcPort.getId());
            CloudPluginSceneElementBO connectedCloudElement = getPluginHandler().getElement(connectedSrcElement.getId());
            if (connectedCloudElement == null || connectedCloudElement.getVirtualType() == null
                    || !connectedCloudElement.getVirtualType().equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)) {
                continue;
            }
            targetPorts.add(connectedCloudPort.getResourceId());
        }
        createData.setPorts(CollectionUtils.emptyNull(new ArrayList<>(targetPorts)));

        VirtualFirewallCreateData virtualFirewall = vfClient.create(createData);
        cloudElement.setResourceId(virtualFirewall.getId());
    }

    protected void createVirtualMachine(CloudPluginSceneElementBO cloudElement) {
        // 1. 处理外部虚机
        if (ElementTypeUtils.isExternal(cloudElement)) {
            // 外部虚拟机 需要连接网络
            List<VirtualNic> nics = CollectionUtils.emptyNull(genVirutalNics(cloudElement));
            if (nics != null && nics.size() > 0) {
                List<VirtualNic> created = new ArrayList<>();
                nics.forEach(n -> {
                    VirtualMachineAddPortData port = new VirtualMachineAddPortData();
                    port.setFixedIp(n.getFixedIp());
                    port.setNetworkId(n.getNetworkId());
                    VirtualMachineAddPortResult result = vmClient.addPort(cloudElement.getResourceId(), port);
                    VirtualNic virtualNic = new VirtualNic();
                    virtualNic.setPortId(result.getPortId());
                    virtualNic.setNetworkId(result.getNetworkId());
                    virtualNic.setFixedIp(result.getFixedIp());
                    virtualNic.setMacAddress(result.getMacAddress());
                    created.add(virtualNic);
                });
                this.receiveVirtualPortInfo(cloudElement, created);
            }
            return;
        }
        SceneElementBO srcElement = cloudElement.getSrcElement();

        // 2. 准备创建虚机参数
        VirtualMachineCreateData createData = new VirtualMachineCreateData();
        createData.setRequestUser(TextUtils.emptyNull(getHandlerUtils().getRequestUser()));
        createData.setName(HandlerUtils.genResourceName(srcElement.getEid()));
        // 待查询出templateId
        Device device = deviceClient.findByUuid(cloudElement.getImageUuid());

        createData.setTemplateId(device.getExtVm().getTemplateId());
        createData.setCpu(IntUtils.zeroNull(srcElement.getCpu()));
        createData.setRam(IntUtils.zeroNull(srcElement.getRam()));
        createData.setDisk(IntUtils.zeroNull(srcElement.getDisk()));
        // 2.1 虚机的连接信息
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(cloudElement.getSrcElement().getPorts())) {
            List<AccessMode> accessModes = cloudElement.getSrcElement().getPorts().stream().filter(port -> Objects.nonNull(port.getAccessModes()))
                    .flatMap(port -> port.getAccessModes().stream()).collect(Collectors.toList());

            List<String> protocols = accessModes.stream()
                    .map(AccessMode::getProtocol)
                    .filter(StringUtils::isNotBlank)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            if (protocols.contains(CloudElementAccessMode.Protocol.SPICE) && protocols.contains(CloudElementAccessMode.Protocol.VNC)) {
                createData.setDisplayType(ElementAccessModeConstant.SPICE_AND_VNC);
            } else if (protocols.contains(CloudElementAccessMode.Protocol.SPICE)) {
                createData.setDisplayType(ElementAccessModeConstant.SPICE);
            } else if (protocols.contains(CloudElementAccessMode.Protocol.VNC)){
                createData.setDisplayType(ElementAccessModeConstant.VNC);
            }
            // 有com接入方式时开启串口接入
            if (protocols.contains(CloudElementAccessMode.Protocol.COM)) {
                createData.setVirtioConsole(true);
            }

        }
        if (StringUtils.isBlank(createData.getDisplayType())) {
            createData.setDisplayType(ElementAccessModeConstant.SPICE_AND_VNC);
        }

        // 3. 虚机的网卡信息，都设置为非special口，设置上ip信息。
        createData.setNetwork(CollectionUtils.emptyNull(genVirutalNics(cloudElement)));

        if (createData.getNetwork() != null) {
            for (var virtualNic : createData.getNetwork()) {
                if (HandlerUtils.isExternalNetwork(virtualNic.getNetworkId())) {
                    createData.setMainCloud(true);
                    break;
                }
            }
        }

        String systemType = SystemTypeUtils.getStandardSystemType(cloudElement.getSrcElement().getSystemType());
        String osType = SystemTypeCommandUtils.getOsType(systemType);
        createData.setOsType(osType);
        if (StringUtils.isNotEmpty(cloudElement.getHost())) {
            createData.setRunHost(cloudElement.getHost());
        }


        // todo cloud-init不用userdata字段, 用custome_script字段
        if (Boolean.TRUE.equals(cloudElement.getInitSupport())) {
            createData.setCustomScript(TextUtils.emptyNull(combineInitCommand(cloudElement)));
        }
        createData.setNetwork(CollectionUtils.emptyNull(genVirutalNics(cloudElement)));

        //临时加参数
        createData.setExtremeSpeed(true);
        createData.setMainCloud(false);

        VirtualMachineCreateData virtualMachine = vmClient.create(createData);
        cloudElement.setResourceId(virtualMachine.getId());
        cloudElement.setHost(virtualMachine.getRunHost());

        receiveVirtualPortInfo(cloudElement, virtualMachine.getNetwork());

        if (ElementTypeUtils.isCoreRouter(cloudElement)) {
            this.configVyosNetwork(cloudElement);
        }


        List<CloudPluginSceneElementPortBO> cloudPorts = getPluginHandler().getPorts(cloudElement);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(cloudPorts)) {
            Set<String> supportProtocols = new HashSet<>();
            supportProtocols.add(CloudElementAccessMode.Protocol.CONSOLE);
            supportProtocols.add(CloudElementAccessMode.Protocol.COM);
            supportProtocols.add(CloudElementAccessMode.Protocol.VNC);
            supportProtocols.add(CloudElementAccessMode.Protocol.SPICE);
            supportProtocols.add(CloudElementAccessMode.Protocol.NO_VNC);
            AtomicBoolean accessModeEmptyFlag = new AtomicBoolean(true);
            cloudPorts.forEach(port -> {
                port.getAccessModes().forEach(accessMode -> {
                    String protocol = accessMode.getProtocol();
                    if (supportProtocols.contains(protocol)) {
                        accessModeEmptyFlag.set(false);
                    }
                });
            });
            if (accessModeEmptyFlag.get()) {
                CloudPluginSceneElementPortBO cloudPort = cloudPorts.get(0);
                String ip = cloudPort.getSrcPort().getIp();
                List<CloudAccessMode> cloudAccessModes = new ArrayList<>();
                if (BaseCloudPluginSceneElementModel.ImageType.CONTAINER.equals(cloudElement.getImageType())) {
                    CloudAccessMode cloudAccessMode = new CloudAccessMode();
                    cloudAccessMode.setAccessIp(ip);
                    cloudAccessMode.setAvailable(true);
                    cloudAccessMode.setConnectionUrl(remoteClient.getContainerConnectionUrl(new GetConnectionUrlParam()
                            .setConsoleType(CloudElementAccessMode.Protocol.CONSOLE)
                            .setVmId(cloudElement.getResourceId())).getData());
                    cloudAccessMode.setProtocol(CloudElementAccessMode.Protocol.CONSOLE);
                    cloudAccessMode.setId(TextUtils.uuid());
                    cloudAccessModes.add(cloudAccessMode);
                } else if (BaseCloudPluginSceneElementModel.ImageType.VM.equals(cloudElement.getImageType())) {
                    CloudAccessMode spiceAccessMode = new CloudAccessMode();
                    spiceAccessMode.setAccessIp(ip);
                    spiceAccessMode.setConnectionUrl("");
                    spiceAccessMode.setProtocol(CloudElementAccessMode.Protocol.SPICE);
                    spiceAccessMode.setId(TextUtils.uuid());
                    spiceAccessMode.setAvailable(true);
                    cloudAccessModes.add(spiceAccessMode);

                    CloudAccessMode vncAccessMode = new CloudAccessMode();
                    vncAccessMode.setAccessIp(ip);
                    vncAccessMode.setConnectionUrl(remoteClient.getNoVncConnectionUrl(new GetConnectionUrlParam()
                            .setConsoleType(CloudElementAccessMode.Protocol.NO_VNC)
                            .setVmId(cloudElement.getResourceId())).getData());
                    vncAccessMode.setProtocol(CloudElementAccessMode.Protocol.VNC);
                    vncAccessMode.setAvailable(true);
                    vncAccessMode.setId(TextUtils.uuid());
                    cloudAccessModes.add(vncAccessMode);
                }
                cloudPort.setAccessModes(cloudAccessModes);
            }
        }
    }

    protected void createContainer(CloudPluginSceneElementBO cloudElement) {

        SceneElementBO srcElement = cloudElement.getSrcElement();
        ContainerCreateData createData = new ContainerCreateData();
//        createData.setRequestUser(TextUtils.emptyNull(getHandlerUtils().getRequestUser()));
        createData.setName(HandlerUtils.genResourceName(srcElement.getEid()));
        var device = new DeviceClient().findByUuid(cloudElement.getImageUuid());
        String image = device.getExtImageInfo().getBaseImageName();
        String version = device.getExtImageInfo().getVersion();
        createData.setImage(image + ":" + version);

        if (srcElement.getCpu() != null && srcElement.getCpu() > 0) {
            createData.setCpu(srcElement.getCpu());
        }
        if (srcElement.getRam() != null && srcElement.getRam() > 1) {
            createData.setRam(srcElement.getRam());
        }
        if (srcElement.getDisk() != null && srcElement.getDisk() > 1) {
            createData.setDisk(srcElement.getDisk());
        }
        if (StringUtils.isNotEmpty(cloudElement.getHost())) {
            createData.setRunHost(cloudElement.getHost());
        }

        // 容器参数
        if (cloudElement.getDockerParams() != null) {
            DockerParams dockerParams = cloudElement.getDockerParams();
//            createData.setEnvironment(dockerParams.getEnvironment());
            createData.setInteractive(dockerParams.getInteractive());
            createData.setPrivilege(dockerParams.getPrivileged());
            createData.setRun(dockerParams.getRun());
        }

        createData.setNetwork(CollectionUtils.emptyNull(
                this.genVirutalNics(cloudElement)
        ));
        ContainerCreateData container = containerClient.create(createData);
        cloudElement.setResourceId(container.getId());
        cloudElement.setHost(container.getRunHost());

        receiveVirtualPortInfo(cloudElement, container.getNetwork());

        execContainerInitCommand(cloudElement);

        List<CloudPluginSceneElementPortBO> cloudPorts = getPluginHandler().getPorts(cloudElement);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(cloudPorts)) {
            Set<String> supportProtocols = new HashSet<>();
            supportProtocols.add(CloudElementAccessMode.Protocol.CONSOLE);
            supportProtocols.add(CloudElementAccessMode.Protocol.COM);
            supportProtocols.add(CloudElementAccessMode.Protocol.VNC);
            supportProtocols.add(CloudElementAccessMode.Protocol.SPICE);
            supportProtocols.add(CloudElementAccessMode.Protocol.NO_VNC);
            AtomicBoolean accessModeEmptyFlag = new AtomicBoolean(true);
            cloudPorts.forEach(port -> {
                port.getAccessModes().forEach(accessMode -> {
                    String protocol = accessMode.getProtocol();
                    if (supportProtocols.contains(protocol)) {
                        accessModeEmptyFlag.set(false);
                    }
                });
            });
            CloudPluginSceneElementPortBO cloudPort = cloudPorts.get(0);
            String ip = cloudPort.getSrcPort().getIp();
            List<CloudAccessMode> cloudAccessModes = new ArrayList<>();
            if (BaseCloudPluginSceneElementModel.ImageType.CONTAINER.equals(cloudElement.getImageType())) {
                CloudAccessMode cloudAccessMode = new CloudAccessMode();
                cloudAccessMode.setAccessIp(ip);
                cloudAccessMode.setAvailable(true);
                cloudAccessMode.setConnectionUrl(remoteClient.getContainerConnectionUrl(new GetConnectionUrlParam().setConsoleType(CloudElementAccessMode.Protocol.CONSOLE)
                                                                                                .setVmId(cloudElement.getResourceId()))
                                                         .getData());
                cloudAccessMode.setProtocol(CloudElementAccessMode.Protocol.CONSOLE);
                cloudAccessMode.setId(TextUtils.uuid());
                cloudAccessModes.add(cloudAccessMode);
            } else if (BaseCloudPluginSceneElementModel.ImageType.VM.equals(cloudElement.getImageType())) {
                CloudAccessMode spiceAccessMode = new CloudAccessMode();
                spiceAccessMode.setAccessIp(ip);
                spiceAccessMode.setConnectionUrl("");
                spiceAccessMode.setProtocol(CloudElementAccessMode.Protocol.SPICE);
                spiceAccessMode.setId(TextUtils.uuid());
                spiceAccessMode.setAvailable(true);
                cloudAccessModes.add(spiceAccessMode);

                CloudAccessMode vncAccessMode = new CloudAccessMode();
                vncAccessMode.setAccessIp(ip);
                vncAccessMode.setConnectionUrl(remoteClient.getNoVncConnectionUrl(new GetConnectionUrlParam().setConsoleType(CloudElementAccessMode.Protocol.NO_VNC)
                                                                                          .setVmId(cloudElement.getResourceId()))
                                                       .getData());
                vncAccessMode.setProtocol(CloudElementAccessMode.Protocol.VNC);
                vncAccessMode.setAvailable(true);
                vncAccessMode.setId(TextUtils.uuid());
                cloudAccessModes.add(vncAccessMode);
            }
            cloudPort.setAccessModes(cloudAccessModes);
        }
    }

    /**
     * @description: 创建实体设备
     * @param cloudElement 云平台场景节点业务信息
     * @return void
     */
    protected void createEntityNode(CloudPluginSceneElementBO cloudElement) {
        this.genEntityCreateData(cloudElement);
    }

    private void genEntityCreateData(CloudPluginSceneElementBO cloudElement) {
        log.info("CreateHandler.genEntityCreateData begin. cloudElement: {}", cloudElement);
        var srcElement = cloudElement.getSrcElement();
        var ports = srcElement.getPorts();
        log.info("ports: {}", ports);
        if (ports == null) {
            return;
        }

        List<ConnectedNetworkBO> networks = new ArrayList<>();
        log.info("cloudElement.getDeriveNetworks(): {}", cloudElement.getDeriveNetworks());
        networks.addAll(cloudElement.getDeriveNetworks());
        log.info("cloudElement.getAccessNetworks(): {}", cloudElement.getAccessNetworks());
        networks.addAll(cloudElement.getAccessNetworks());
        for (var connectNetwork : networks) {
            var port = connectNetwork.getFromPorts().get(0);
            var cloudPort = getPluginHandler().getPort(port.getId());
            // 没有定义的端口补全
            if (cloudPort == null) {
                cloudPort = new CloudPluginSceneElementPortBO();
                getPluginHandler().addPort(port.getId(), cloudPort);
            }
            String networkId = connectNetwork.getNetwork().getResourceId();
            String physicalSwitchName = cloudPort.getEntityPhysicalSwitchName();
            log.info("physicalSwitchName:{}", physicalSwitchName);
            log.info("entitySwitchPort:{}", cloudPort.getEntitySwitchPort());
            log.info("cloudPort.getEntityPhysicalSwitchName():{}", cloudPort.getEntityPhysicalSwitchName());
            log.info("cloudPort.getEntitySwitchPort():{}", cloudPort.getEntitySwitchPort());
            String key = StringUtils.joinWith(":", networkId, physicalSwitchName);
            LogicalSwitchCreateData data;
            synchronized (network2LogicalSwitch) {
                data = network2LogicalSwitch.get(key);
                if (Objects.isNull(data)) {
                    data = new LogicalSwitchCreateData();
                    data.setLogicalSwitchName(convertFirstAlphaToNumber(TextUtils.uuid()));
                    data.setPhysicalSwitchName(cloudPort.getEntityPhysicalSwitchName());
                    data.setPhysicalPorts(new ArrayList<>());
                    if (StringUtils.isBlank(cloudPort.getEntityPhysicalSwitchName())) {
                        log.error("实体设备没有配置物理交换机名称");
                    } else {
                        network2LogicalSwitch.put(key, data);
                    }
                }
            }
            LogicalSwitchCreateData.PhysicalPort physicalPort = new LogicalSwitchCreateData.PhysicalPort();
            physicalPort.setPort(cloudPort.getEntitySwitchPort());
            physicalPort.setVlan(port.getVlanNumber());
            data.getPhysicalPorts().add(physicalPort);

            EntityNodeCreateData createData = new EntityNodeCreateData();
            createData.setLogicalSwitch(data.getLogicalSwitchName());

            cloudPort.setAuxiliaryNetworkId(networkId);
            createData.setPsPortName(cloudPort.getEntitySwitchPort());
            createData.setPhysicalSwitch(cloudPort.getEntityPhysicalSwitchName());
            createData.setNetworkId(networkId);
            createData.setName(TextUtils.uuid());
            createData.setMac(port.getMacAddr());
            createData.setIpAddr(port.getIp());
            if (StringUtils.isNotBlank(cloudPort.getEntityPhysicalSwitchName())) {
                synchronized (entityCreateData) {
                    entityCreateData.add(createData);
                }
            }
            cloudPort.setEntityLogicalSwitchName(createData.getLogicalSwitch());
            cloudPort.setResourceId(createData.getName());
        }
        // 实体设备没有资源id
        cloudElement.setResourceId(TextUtils.uuid());
    }

    private String convertFirstAlphaToNumber(String logicSwitchName) {
        String suffix = logicSwitchName.substring(1, logicSwitchName.length());
        String prefixNumber = String.valueOf(this.random.nextInt(9));
        return prefixNumber + suffix;
    }


    protected void recordNpcNode(CloudPluginSceneElementBO cloudElement) {
        String npcName = HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid());
        synchronized (network2Info) {
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
    }


    public CreateHandler(CloudScenePluginHandler pluginHandler) {
        super(pluginHandler, CreateTaskPool.getInstance(), SceneModel.Event.CREATE);
    }

    public void create() {
        this.prepareEventProcess();

        // 先创建辅助网络(包含虚拟交换机)
        getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CloudSceneStage.CREATE_NETWORK);
        List<Runnable> createAuxiliaryNetworkTasks = new ArrayList<>();
        for (CloudPluginSceneElementBO cloudPluginSceneElementBO : getPluginHandler().getAuxiliaryNetworkElement()) {
            Runnable runnable = () -> createAuxiliaryNetworkProcess(cloudPluginSceneElementBO);
            createAuxiliaryNetworkTasks.add(runnable);
        }

        if (!createAuxiliaryNetworkTasks.isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_AUXILIARY_NETWORKS, () -> {
                runTasks(createAuxiliaryNetworkTasks);
            });
        }

        runSceneProgress(SceneProcess.CREATE_NODE_TO_EXTERNAL_ROUTER, this::createNodeToExternalRouter);

        getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_NETWORK);

        // todo 先等30秒用于人手动添加采集路由的外网地址
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        // 再创建网关 和 防火墙
        getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CloudSceneStage.CREATE_GATEWAY);

        List<Runnable> createGatewayTasks = new ArrayList<>();
        for (CloudPluginSceneElementBO cloudPluginSceneElementBO : getPluginHandler().getGatewayElements()) {
            Runnable runnable = () -> createElementProcess(cloudPluginSceneElementBO);
            createGatewayTasks.add(runnable);
        }
        List<Runnable> createFirewallTasks = new ArrayList<>();
        for (CloudPluginSceneElementBO cloudPluginSceneElementBO : getPluginHandler().getFirewallElements()) {
            Runnable runnable = () -> createElementProcess(cloudPluginSceneElementBO);
            createFirewallTasks.add(runnable);
        }
        if (!createGatewayTasks.isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_GATEWAYS, () -> {
                runTasks(createGatewayTasks);
                if (createFirewallTasks.isEmpty()) {
                    getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_GATEWAY);
                }
            });
        }
        if (!createFirewallTasks.isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_FIREWALLS, () -> {
                runTasks(createFirewallTasks);
                getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_GATEWAY);
            });
        }
        getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_GATEWAY);


        getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CloudSceneStage.CREATE_INSTANCE);
        // 再创建终端
        List<CloudPluginSceneElementBO> npcNodes = getPluginHandler().getNpcElements();
        List<Runnable> createTerminalTasks = new ArrayList<>();
        for (CloudPluginSceneElementBO cloudPluginSceneElementBO : getPluginHandler().getTerminalExcludeNpcElements()) {
            Runnable runnable = () -> createElementProcess(cloudPluginSceneElementBO);
            createTerminalTasks.add(runnable);
        }
        if (!createTerminalTasks.isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_TERMINALS, () -> {
                runTasks(createTerminalTasks);
                if (npcNodes.isEmpty()) {
                    getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_INSTANCE);
                }
            });
        }
        // 批量创建NPC节点
        if (npcNodes.size() > 0) {
            // npc是一次调用批量创建 所以先记录npc相关参数
            for (CloudPluginSceneElementBO cloudElement : npcNodes) {
                createElementProcess(cloudElement);
            }
            // 批量创建
            runSceneProgress(SceneProcess.CREATE_NPCS, () -> {
                createNpcElements(new ArrayList<>(network2Info.values()));
                getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_INSTANCE);
            });
        }
        getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_INSTANCE);

        // 批量创建逻辑交换机
        createLogicSwitchProcess();
        log.info("end gen part scene connect info");

        // 批量创建实体设备
        createEntityElementProcess();
    }

    protected void createNodeToExternalRouter() {
        String sceneId = getPluginHandler().getSrcScene().getEid();
        VirtualRouter vrClient = new VirtualRouter();
        // 创建辅助路由
        VirtualRouterCreateData createData = new VirtualRouterCreateData();
        createData.setName(HandlerUtils.genResourceName(sceneId));
        List<VirtualNic> virtualNics = new ArrayList<>();
        //路由需要接外部网络，以及路由上面配置NAT，30s暂时不支持

//        VirtualNic externalVirtualNic = new VirtualNic();
//        externalVirtualNic.setNetworkId(CloudSingleton.cloudAppConfig.getExternalNetId());
//        externalVirtualNic.setCidr(CloudSingleton.cloudAppConfig.getExternalNetCidr());
        // cloud-manger 并没有使用该ip
//        externalVirtualNic.setFixedIp(IpUtils.newRandomIps(externalVirtualNic.getCidr(), 1, Collections.emptyList(), null, true).get(0));
//        virtualNics.add(externalVirtualNic);

        CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                .getPluginHandler(PluginConstant.PLUGIN_KEY);
        cloudPluginHandler.getElementMap().values().forEach(elementBO -> {
            List<ConnectedNetworkBO> deriveNetworks = elementBO.getDeriveNetworks();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deriveNetworks)) {
                deriveNetworks.forEach(deriveNet -> {
                    String networkId = deriveNet.getNetwork().getResourceId();
                    String networkCidr = deriveNet.getNetwork().getCidr();
                    VirtualNic virtualNic = new VirtualNic();
                    virtualNic.setNetworkId(networkId);
                    String ip = "";
                    if (StringUtils.isNotBlank(networkCidr)) {
                        String preAllocateIp = getPluginHandler().getSceneHandler().getNetworkCidrToCollectRouterPortIp().get(networkCidr);
                        if (StringUtils.isNotBlank(preAllocateIp)) {
                            ip = preAllocateIp;
                        }
                    }
                    if (StringUtils.isBlank(ip)) {
                        List<String> ips = IpUtils.newRandomIps(deriveNet.getNetwork().getCidr(), 1, new ArrayList<>(deriveNet.getNetwork().getExcloudIps()), deriveNet.getNetwork().getAllocationPools(), Boolean.TRUE.equals(deriveNet.getNetwork().getDhcp()));
                        if (org.apache.commons.collections4.CollectionUtils.isEmpty(ips)) {
                            log.info("ip pool is empty when initate collection,node {}", elementBO.getSrcElement().getName());
                            return;
                        }
                        ip = ips.get(0);
                    }

                    virtualNic.setFixedIp(ip);
                    virtualNic.setCidr(networkCidr);
                    virtualNics.add(virtualNic);

                });

            }
        });
        createData.setNetworks(virtualNics.stream().map(virtualNic -> {
            VirtualRouterNic virtualRouterNic = new VirtualRouterNic();
            virtualRouterNic.setId(virtualNic.getNetworkId());
            virtualRouterNic.setFixedIp(virtualNic.getFixedIp());
            virtualRouterNic.setCidr(virtualNic.getCidr());
            return virtualRouterNic;
        }).collect(Collectors.toList()));
        VirtualRouterCreateData virtualRouter = vrClient.create(createData);
        log.info("collected router {}", virtualRouter.getId());
        getPluginHandler().getSceneHandler().setProbeRouter(virtualRouter.getId());
    }

    protected void createLogicSwitchProcess() {
        if (!network2LogicalSwitch.values().isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_LOGICAL_SWITCHES, () -> createLogicalSwitch(new ArrayList<>(network2LogicalSwitch.values())));
        }
    }

    public void createLogicalSwitch(List<LogicalSwitchCreateData> list) {
        List<CloudPluginSceneElementLogicalSwitchModel> logicalSwitchModels = new ArrayList<>();
        CloudPluginSceneElementLogicalSwitchMapper logicalSwitchMapper = BeanCommon.getBean(CloudPluginSceneElementLogicalSwitchMapper.class);
        list.forEach(createData -> {
            CloudPluginSceneElementLogicalSwitchBO logicalSwitchBO = new CloudPluginSceneElementLogicalSwitchBO();
            logicalSwitchBO.setScene(getPluginHandler().getSourceSceneId());
            logicalSwitchBO.setName(createData.getLogicalSwitchName());
            logicalSwitchBO.setPhysicalPorts(createData.getPhysicalPorts());
            logicalSwitchBO.setPhysicalSwitchName(createData.getPhysicalSwitchName());
            logicalSwitchModels.add(logicalSwitchBO.toModel());
        });
        logicalSwitchMapper.bulkCreate(logicalSwitchModels);
        list.forEach(enClient::createLogicalSwitch);
    }

    protected void createEntityElementProcess() {
        if (!entityCreateData.isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_ENTITIES, () -> createEntityElements(new ArrayList<>(entityCreateData)));
        }
    }

    public void createEntityElements(List<EntityNodeCreateData> list) {
        list.forEach(enClient::create);
    }

    protected void prepareEventProcess() {
        prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);
    }

    public void createNpcElements(List<BaseNpcData.NetWorkNpcInfo> list) {
        NpcCreateData npcCreateData = new NpcCreateData();
        npcCreateData.setNetWorkNpcInfos(list);
        npcClient.create(npcCreateData);
        // todo npc其实没有resourceId
        for (CloudPluginSceneElementBO e : getPluginHandler().getTerminalElementMap().values()) {
            if (e.getVirtualType() != null && e.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.NPC) {
                e.setResourceId(HandlerUtils.genResourceName(e.getSrcElement().getEid()));
            }
        }
    }

    public void createAuxiliaryNetworkProcess(CloudPluginSceneElementBO cloudElement) {
        runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.CREATE_AUXILIARY_NETWORK, () -> createAuxiliaryNetwork(cloudElement));
    }

    public void createElementProcess(CloudPluginSceneElementBO cloudElement) {
        if (Boolean.TRUE.equals(cloudElement.getExternal()) && !ElementTypeUtils.isExternalTerminal(cloudElement)) {
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
            runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.CREATE_NPC, () -> recordNpcNode(cloudElement));
        }
    }


    /**
     * 配置vyos网络
     *
     * @param elementBO
     */
    private void configVyosNetwork(CloudPluginSceneElementBO elementBO) {
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(elementBO.getSrcElement().getPorts())) {
            return;
        }
        List<String> cmds = new ArrayList<>(List.of("configure"));
        cmds.addAll(VyosNetworkCommandUtils.genNetworkCmdList(elementBO, getPluginHandler()));
        cmds.addAll(List.of("commit", "save", "exit"));
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(cmds)) {
            log.error("no network config , element id is {} ", elementBO.getSrcElement().getId());
            return;
        }
        var buildNetwork = new RouterConfigCmdData();
        buildNetwork.setVendor("vyos");
        buildNetwork.setCommands(cmds);

        boolean buildNetworkResult = false;
        int tryTimes = 5;
        Exception exception = null;
        for (int i = 0; i < tryTimes; i++) {
            // 发送拼接网络的命令
            try {
                var startTime = LocalDateTime.now();
                buildNetworkResult = vmRouter.sendCommand(buildNetwork, elementBO.getResourceId());
            } catch (Exception e) {
                log.error("failed to build network , try time {}", i);
                exception = e;
            }
            if (buildNetworkResult) {
                // 设置失败需要重试
                break;
            }
        }
        if (!buildNetworkResult) {
            log.error("build vyos network failed , buildNetwork {} , resource id is {} ", JsonCommon.stringify(buildNetwork), elementBO.getResourceId());
            if (exception != null) {
                log.error("failed to build network ", exception);
            }
        }
    }

    private void waitSeconds(long millSeconds) {
        try {
            Thread.sleep(millSeconds);
        } catch (InterruptedException e) {
        }
    }

    private void trySetPublicNetProxy(CloudAccessMode accessMode) {
        boolean publicNetProxy = accessMode.getPublicNetProxy() != null ? accessMode.getPublicNetProxy() : CloudSingleton.cloudAppConfig.getPublicNetProxy();
        if (publicNetProxy) {
            String ip = StringUtils.isNotEmpty(accessMode.getPublicNetProxyIp()) ? accessMode.getPublicNetProxyIp() : CloudSingleton.cloudAppConfig.getPublicNetProxyIp();
            Integer port = accessMode.getPublicNetProxyPort() != null ? accessMode.getPublicNetProxyPort() : accessMode.getAccessPort();
            accessMode.setPublicNetProxy(true);
            accessMode.setPublicNetProxyIp(ip);
            accessMode.setPublicNetProxyPort(port);
        } else {
            accessMode.setPublicNetProxy(false);
            accessMode.setPublicNetProxyIp(null);
            accessMode.setPublicNetProxyPort(null);
        }
    }

    private void attachISO(CloudPluginSceneElementBO cloudElement) {
        VirtualMachineAttachISOData data = new VirtualMachineAttachISOData();
        data.setInstanceUuid(cloudElement.getResourceId());
        data.setFileName(cloudElement.getIsoAttachFilename());
        data.setTmpdirUuid(cloudElement.getIsoTmpdirUuid());
        vmClient.attachISO(data);
    }

    private void attachVirtualUsbs(CloudPluginSceneElementBO cloudElement) {
        AttachVirtualUsbsData data = new AttachVirtualUsbsData();
        data.setInstanceUuid(cloudElement.getResourceId());
        List<String> usbUuids = new ArrayList<>();
        for (VirtualUsb u : cloudElement.getVirtualUsbs()) {
            String uuid = u.getUuid();
            usbUuids.add(uuid);
        }
        data.setVirtualUsbUuids(usbUuids);
        vmClient.attachVirtualUsbs(data);
    }

    private void attachEntityUsbs(CloudPluginSceneElementBO cloudElement) {
        log.info("CreateHandler.attachEntityUsbs begin. cloudElement:{}", cloudElement);
        AttachEntityUsbsData data = new AttachEntityUsbsData();
        data.setInstanceUuid(cloudElement.getResourceId());
        List<String> usbUuids = new ArrayList<>();
        for (EntityUsb u : cloudElement.getEntityUsbs()) {
            String uuid = u.getUuid();
            usbUuids.add(uuid);
        }
        data.setEntityUsbUuids(usbUuids);
        vmClient.attachEntityUsbs(data);
    }
}
