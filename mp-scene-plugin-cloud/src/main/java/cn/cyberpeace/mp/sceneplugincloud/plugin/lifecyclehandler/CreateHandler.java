package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.snmp_device.netgear.CommandTopData;
import cn.cyberpeace.mp.base.snmp_device.netgear.CommonData;
import cn.cyberpeace.mp.base.snmp_device.netgear.JuniperData;
import cn.cyberpeace.mp.base.snmp_device.netgear.NetgearData;
import cn.cyberpeace.mp.base.util.basic.*;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
import cn.cyberpeace.mp.base.util.mptool.ConcurrentHashSet;
import cn.cyberpeace.mp.base.util.mptool.FileNameUtil;
import cn.cyberpeace.mp.base.util.mptool.Ipv4Util;
import cn.cyberpeace.mp.base.util.thread.ThreadUtils;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.core.event.base.SceneEventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import cn.cyberpeace.mp.scene.util.*;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.FirewallRule;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.SnmpConfig;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSceneStage;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.FirewallRuleDirection;
import cn.cyberpeace.mp.sceneplugincloud.constant.QosRuleDirection;
import cn.cyberpeace.mp.sceneplugincloud.model.CveDataModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.repository.CveDataMapper;
import cn.cyberpeace.mp.sceneplugincloud.util.ElementTypeUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.io.CharStreams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class CreateHandler extends HandlerCommon {

    public static final ScheduledThreadPoolExecutor INSERT_FAKE_DATA_POOL = new ScheduledThreadPoolExecutor(10,
            r -> {
        Thread thread = new Thread(r);
        thread.setName("insert-fake-data");
        return thread;
            });
    private static final Random RANDOM = new Random();

    private static final Set<String> NOT_FAKE_EID_SET = new ConcurrentHashSet<>();

    private final VirtualSwitch vsClient = new VirtualSwitch();
    private final VirtualRouter vrClient = new VirtualRouter();
    private final VirtualFirewall vfClient = new VirtualFirewall();
    private final VirtualMachine vmClient = new VirtualMachine();
    protected final VirtualPort vpClient = new VirtualPort();
    private final Container containerClient = new Container();
    private final EntityNode enClient = new EntityNode();
    private final Npc npcClient = new Npc();
    private final VmRouter vmRouter = new VmRouter();
    private VirtualDynamips vdClient = new VirtualDynamips();
    private Network network = new Network();

    private final Map<String, BaseNpcData.NetWorkNpcInfo> network2Info = new ConcurrentHashMap<>();

    private final String[] CLIENT_LANS = {"10.10.0.0", "10.10.1.0", "10.10.2.0", "10.10.3.0", "10.10.4.0",
            "10.10.5.0", "10.10.6.0", "10.10.7.0", "10.10.8.0", "10.10.9.0", "10.10.10.0", "10.10.11.0", "10.10.12.0",
            "10.10.13.0", "10.10.14.0", "10.10.15.0"};

    private class HfdyVpnImage {
        public static final String VPN_SERVER = "hfdy-vpn-server";
        public static final String VPN_CLIENT = "hfdy-vpn-client";
    }

    public static class SceneProcess {
        public static final SceneEventProcess CREATE_AUXILIARY_NETWORKS = new SceneEventProcess("create_auxiliary_networks", "sceneplugincloud.event.create_auxiliary_network", "create_network", 1);
        public static final SceneEventProcess CREATE_GATEWAYS = new SceneEventProcess("create_gateways", "sceneplugincloud.event.create_gateway", "create_gateway", 1);
        public static final SceneEventProcess CREATE_FIREWALLS = new SceneEventProcess("create_firewalls", "sceneplugincloud.event.create_firewall", "create_gateway", 2);
        public static final SceneEventProcess CREATE_TERMINALS = new SceneEventProcess("create_terminals", "sceneplugincloud.event.create_terminal", "create_instance", 1);
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
                    for (var sourcePort : sourcePorts) {
                        for (var desPort : desPorts) {
                            var direction = srcRule.getDirection();
                            var rule = new VirtualFirewallCreateData.Rule();
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
            } else if (BaseCloudPluginSceneElementModel.NetMode.MACVLAN.equals(cloudElement.getNetMode())) {
                continue;
            }
            var srcElement = cloudElement.getSrcElement();
            var createData = new VirtualSwitchCreateData();
            createData.setName(HandlerUtils.genResourceName(srcElement.getEid(), index++));
            createData.setEntityNetwork(connectedNetwork.getNetwork().getVlan());
            if (Boolean.TRUE.equals(connectedNetwork.network.getVlan())) {
                createData.setSegmentationId(IntUtils.zeroNull(connectedNetwork.network.getVlanId()));
            }
            var subnet = new VirtualSwitchCreateData.Subnet();
            subnet.setCidr(connectedNetwork.network.getCidr());
            subnet.setGatewayIp(TextUtils.emptyNull(connectedNetwork.network.getGateway()));
            subnet.setDhcpPortIp(TextUtils.emptyNull(connectedNetwork.network.getDhcpIp()));
            // 默认开启dhcp
            var dhcp = connectedNetwork.network.getDhcp();
            if (Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getDhcpControl())) {
                subnet.setEnableDhcp(dhcp == null || dhcp);
            } else {
                subnet.setEnableDhcp(true);
            }

            var srcAllocationPools = connectedNetwork.network.getAllocationPools();
            if (srcAllocationPools != null && !srcAllocationPools.isEmpty()) {
                List<VirtualSwitchCreateData.Subnet.AllocationPool> allocationPools = new ArrayList<>();
                srcAllocationPools.forEach(srcAllocationPool -> {
                    if (StringUtils.isNotEmpty(srcAllocationPool.getStart()) && StringUtils.isNotEmpty(srcAllocationPool.getEnd())) {
                        allocationPools.add(new VirtualSwitchCreateData.Subnet.AllocationPool(srcAllocationPool.getStart(),
                                srcAllocationPool.getEnd()));
                    }
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
            if (connectedNetwork.network.getNetMode() == null || BaseCloudPluginSceneElementModel.NetMode.NORMAL.equals(connectedNetwork.network.getNetMode())) {
                var virtualSwitch = vsClient.create(createData);
                cloudElement.setVlanId(virtualSwitch.getSegmentationId());
                connectedNetwork.network.setVlanId(virtualSwitch.getSegmentationId());
                connectedNetwork.network.setResourceId(virtualSwitch.getId());
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
                createAuxiliaryNetworkRouter(cloudElement, connectedNetwork.network);
            }
        }
    }

    protected void createAuxiliaryNetworkRouter(CloudPluginSceneElementBO cloudElement, CloudPluginSceneElementNetworkBO network) {
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

    /**
     * @param cloudElement
     * @return List<VirtualNic>
     * @description: 生成虚拟网卡
     */
    private List<VirtualNic> genVirutalNics(CloudPluginSceneElementBO cloudElement) {
        List<VirtualNic> virtualNics = new ArrayList<>();
        // 添加管理网卡
        if (Boolean.TRUE.equals(cloudElement.getEnableMgmt())) {
            var virtualNic = new VirtualNic();
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
            virtualNic.setFwdnsInterfaceIp(getHandlerUtils().getFwdnsInterfaceIp(cloudElement.getSrcElement().getId(), connectedNetwork.getNetwork().getId()));
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
            }
        }
        CloudPluginSceneElementPortBO sampleCloudPort = getPluginHandler().getPort(samplePort.getId());
        var virtualNic = new VirtualNic();
        virtualNic.setNetworkId(network.getResourceId());
        if (StringUtils.isNotEmpty(sampleCloudPort.getConnectMode())) {
            virtualNic.setVlanMode(sampleCloudPort.getConnectMode());
        }
        virtualNic.setFixedIp(TextUtils.emptyNull(samplePort.getIp()));
        virtualNic.setMacAddress(TextUtils.emptyNull(samplePort.getMacAddr()));
        List<VirtualNic.QosRule> qosRules = new ArrayList<>();
        if (samplePort.getIngress() > 0) {
            var qosRule = new VirtualNic.QosRule();
            qosRule.setMaxKbps(samplePort.getIngress());
            qosRule.setDirection(QosRuleDirection.INGRESS);
            qosRules.add(qosRule);
        }
        if (samplePort.getEgress() > 0) {
            var qosRule = new VirtualNic.QosRule();
            qosRule.setMaxKbps(samplePort.getEgress());
            qosRule.setDirection(QosRuleDirection.EGRESS);
            qosRules.add(qosRule);
        }
        //云平台暂不支持上下行带宽和时延丢包率同时设置，时延丢包率通过命令执行
//        if(samplePort.getDelay() > 0){
//            var qosRule = new VirtualNic.QosRule();
//            qosRule.setDelayMs(samplePort.getDelay());
//            qosRules.add(qosRule);
//        }
//        if(samplePort.getLoss() > 0){
//            var qosRule = new VirtualNic.QosRule();
//            qosRule.setLossPercent(samplePort.getLoss());
//            qosRules.add(qosRule);
//        }
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
                if (accessMode.getPort() == null) {
                    accessModeParam.setPort(AccessMode.Protocol.DEFAULT_PORT_MAP.getOrDefault(accessMode.getProtocol(), null));
                } else {
                    accessModeParam.setPort(accessMode.getPort());
                }
                accessModeParam.setUsername(accessMode.getUsername());
                accessModeParam.setPassword(accessMode.getPassword());
                accessModeParam.setEnablePortMapping(accessMode.getProxy());
                accessModeParam.setPortMappingProtocol(TextUtils.emptyNull(accessMode.getBaseProtocol()));
                return accessModeParam;
            }).collect(Collectors.toList()));
        }
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
        var srcElement = cloudElement.getSrcElement();
        //端口信息
        var ports = srcElement.getPorts();
        if (ports == null) {
            return entityInterfaces;
        }
        //网络业务信息
        List<ConnectedNetworkBO> networks = new ArrayList<>();
        networks.addAll(cloudElement.getDeriveNetworks());
        networks.addAll(cloudElement.getAccessNetworks());
        boolean configSnmpFinished = false;
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
                var accessMode = new EntityNodeCreateData.Interface.AccessMode();
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
        var srcElement = cloudElement.getSrcElement();
        List<String> commands = new ArrayList<>();

        addHFDYVpnInitCommand(commands, cloudElement);


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

        boolean isWindows = SystemTypeCommandUtils.isWindows(standardSystemType);
        if (!isWindows && cloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
            commands.add("echo \"169.254.169.254     res.cr.com\" >>/etc/hosts");
        }

        // TODO: 应用安装

        // TODO: 快速创建安装
        if (Boolean.TRUE.equals(cloudElement.getQuickCreateSupport())) {
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(cloudElement.getQuickCreateParams())) {
                cloudElement.getQuickCreateParams().forEach(p -> {
                    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(p.getResourceList())) {
                        var installers = p.getResourceList().stream().map(r -> {
                            InstallerUtils.Installer installer = new InstallerUtils.Installer();
                            installer.setFile(r.getFile());
                            installer.setName(r.getUserFilename());
                            installer.setInstallCommand(r.getCommand());
                            return installer;
                        }).collect(Collectors.toList());
                        commands.addAll(InstallerUtils.generateInstallerInstallCommand(standardSystemType, installers));
                    }
                    if (p.getDockerInstallerData() != null && StringUtils.isNotEmpty(p.getDockerInstallerData().getDockerUrl())) {
                        var installer = new InstallerUtils.Installer();
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
        var command = SystemTypeCommandUtils.joinCommands(standardSystemType, cloudInitCommands.toArray(new String[0]));
        if (!StringUtils.isEmpty(command)) {
            if (SystemTypeCommandUtils.isWindows(standardSystemType)) {
                command = SystemTypeCommandUtils.appendBeforeCommand(command, "rem cmd", standardSystemType);
            } else {
                command = SystemTypeCommandUtils.appendBeforeCommand(command, "#cloud-config", standardSystemType);
            }
        }

        return command;
    }

    void addHFDYVpnInitCommand(List<String> existCommands, CloudPluginSceneElementBO currentCloudElement) {
        if (HfdyVpnImage.VPN_SERVER.equals(currentCloudElement.getImage())) {
            String pushRouteToServerLanCommandTemplate = "push \\\"route %s %s\\\"";


            List<String> pushRouteToServerLanCommands = new ArrayList<>();
            getPluginHandler().getElementMap().forEach((nodeId, cloudElement) -> {
                cloudElement.getDeriveNetworks().forEach(deriveNetwork -> {
                    if (StringUtils.isNotEmpty(deriveNetwork.getNetwork().getCidr())) {
                        String[] cidrArray = deriveNetwork.getNetwork().getCidr().split("/");
                        String maskIp = Ipv4Util.getMaskByMaskBit(Integer.parseInt(cidrArray[1]));
                        String cidrIp = Ipv4Util.longToIpv4(Ipv4Util.ipv4ToLong(cidrArray[0]) & Ipv4Util.ipv4ToLong(maskIp));
                        pushRouteToServerLanCommands.add(String.format(pushRouteToServerLanCommandTemplate, cidrIp, maskIp));
                    }
                });
            });

            String pushRouteToServerLanParameter = String.join("\\n", pushRouteToServerLanCommands);

            String routeToClientLanCommandTemplate = "route %s %s";
            String irouteToClientLanCommandTemplate = "iroute %s %s";
            List<String> routeToClientLans = new ArrayList<>();
            List<String> irouteToClientLans = new ArrayList<>();
            for (String clientLan : CLIENT_LANS) {

                String maskIp = "255.255.255.0";
                String cidrIp = clientLan;
                String singleRouteToClientLan = String.format(routeToClientLanCommandTemplate, cidrIp, maskIp);
                String SingleIrouteToClientLan = String.format(irouteToClientLanCommandTemplate, cidrIp, maskIp);
                routeToClientLans.add(singleRouteToClientLan);
                irouteToClientLans.add(SingleIrouteToClientLan);
            }


            String arkimeIp = getPluginHandler().getSceneHandler().getArkimeIp();

            String vpnServerCommand = String.format("bash /etc/openvpn/start_server.sh \"%s\" \"%s\" \"%s\" \"%s\"", pushRouteToServerLanParameter,
                    String.join("\\n", routeToClientLans), String.join("\\n", irouteToClientLans), arkimeIp);
            log.info("vpn server cloud init command : " + vpnServerCommand);
            //删除连接的非外部网络给的默认路由
            Set<String> innerNetworkGatewayIps = getVpnInnerNetworkGatewayIp(currentCloudElement);
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(innerNetworkGatewayIps)) {
                // 删除默认路由
                innerNetworkGatewayIps.forEach(innerNetworkGatewayIp -> {
                    String delInnerGatewayRouteCommand = String.format("ip route del default via %s", innerNetworkGatewayIp);
                    existCommands.add(delInnerGatewayRouteCommand);
                });


                String defaultInnerGatewayIp = innerNetworkGatewayIps.iterator().next();


                Set<String> asideCidrs = new HashSet<>();
                Set<String> accessCidrs = currentCloudElement.getAccessNetworks().stream().map(ConnectedNetworkBO::getNetwork)
                                .map(CloudPluginSceneElementNetworkBO::getCidr)
                                        .filter(cidr -> !StringUtils.isEmpty(cidr)).collect(Collectors.toSet());
                Set<String> deriveCidrs = currentCloudElement.getDeriveNetworks().stream().map(ConnectedNetworkBO::getNetwork)
                               .map(CloudPluginSceneElementNetworkBO::getCidr)
                                       .filter(cidr ->!StringUtils.isEmpty(cidr)).collect(Collectors.toSet());
                asideCidrs.addAll(accessCidrs);
                asideCidrs.addAll(deriveCidrs);
                String addRouteToOtherLanTemplate = "ip route add %s via " + defaultInnerGatewayIp;

                getPluginHandler().getElementMap().forEach((nodeId, cloudElement) -> {
                    if (cloudElement.getExternal()) {
                        return;
                    }
                    cloudElement.getDeriveNetworks().forEach(deriveNetwork -> {
                        String cidr = deriveNetwork.getNetwork().getCidr();
                        if (StringUtils.isNotEmpty(cidr)) {
                            if (!asideCidrs.contains(cidr)) {
                                existCommands.add(String.format(addRouteToOtherLanTemplate, cidr));
                            }
                        }
                    });
                });
            }

            //先写死，只有10.100.13.1分靶场，后续需要修改
            existCommands.add("ip route add 30.10.254.0/24 via 10.100.13.1");

            existCommands.add(vpnServerCommand);
        } else if (HfdyVpnImage.VPN_CLIENT.equals(currentCloudElement.getImage())) {
            String serverVpnIp = getPluginHandler().getSceneHandler().getScene().getServerVpnIp();
            String mpIp = CloudSingleton.cloudAppConfig.getMpIp();
            String arkimeIp = getPluginHandler().getSceneHandler().getArkimeIp();
            String controllerHostName = getPluginHandler().getSceneHandler().getArkimeRunHost();

            String startVpnClientCommand = String.format("/etc/openvpn/start_client.sh %s", serverVpnIp);
            String configMpIpCommand = String.format("echo \"%s %s %s\" > /etc/arkime-bridge/mp-ip.txt", mpIp, controllerHostName, arkimeIp);
            String configServerIpCommand = String.format("echo %s > /etc/arkime-bridge/server-ip.txt", serverVpnIp);
            String execFileBridgeCommand = String.format("nohup /etc/arkime-bridge/file_bridge > /etc/arkime-bridge/file_bridge.log 2>&1 &");

            Set<String> innerNetworkGatewayIps = getVpnInnerNetworkGatewayIp(currentCloudElement);
            innerNetworkGatewayIps.forEach(innerNetworkGatewayIp -> {
                String delInnerGatewayRouteCommand = String.format("ip route del default via %s", innerNetworkGatewayIp);
                existCommands.add(delInnerGatewayRouteCommand);
            });

            existCommands.add(startVpnClientCommand);
            existCommands.add(configMpIpCommand);
            existCommands.add(configServerIpCommand);
            existCommands.add(execFileBridgeCommand);

        }



    }

    private Set<String> getVpnInnerNetworkGatewayIp(CloudPluginSceneElementBO cloudElement) {
        Set<String> deriveInnerNetworkGatewayIps = cloudElement.getDeriveNetworks().stream().map(ConnectedNetworkBO::getNetwork)
                .filter(network -> !Boolean.TRUE.equals(network.getExternal()))
                .map(CloudPluginSceneElementNetworkBO::getGateway).collect(Collectors.toSet());

        Set<String> accessInnerNetworkGatewayIps = cloudElement.getAccessNetworks().stream().map(ConnectedNetworkBO::getNetwork)
                .filter(network -> !Boolean.TRUE.equals(network.getExternal()))
                .map(CloudPluginSceneElementNetworkBO::getGateway).collect(Collectors.toSet());
        Set<String> innerNetworkGatewayIps = new HashSet<>();
        innerNetworkGatewayIps.addAll(deriveInnerNetworkGatewayIps);
        innerNetworkGatewayIps.addAll(accessInnerNetworkGatewayIps);
        return innerNetworkGatewayIps;
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

        List<String> extraCommands = new ArrayList<>();
        var sceneFileUrl = srcScene.getFile();
        String sceneUnzipDir = null;
        if (!StringUtils.isEmpty(sceneFileUrl)) {
            sceneUnzipDir = SceneFileUtils.getSceneFileUnzipDir(standardSystemType, sceneId);
            extraCommands.add(CommandUtils.linuxMkDir(sceneUnzipDir));
        }
        var elementFileUrl = srcElement.getFile();
        String elementUnzipDir = null;
        if (!StringUtils.isEmpty(elementFileUrl)) {
            elementUnzipDir = SceneFileUtils.getElementFileUnzipDir(standardSystemType, sceneId, srcElement.getEid());
            extraCommands.add(CommandUtils.linuxMkDir(elementUnzipDir));
        }

        List<String> commands = new ArrayList<>();
        List<String> initCommands = srcElement.getInitCommands();
        if (initCommands != null) {
            initCommands.forEach(initCommand -> {
                var parsedCommands = SceneCommandUtils.parseCommand(initCommand, sceneId, srcElement.getEid(),
                        standardSystemType);
                if (!parsedCommands.isEmpty()) {
                    var inlineCommand = SystemTypeCommandUtils.inlineJoinCommands(standardSystemType,
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
            for (var ipAndDomain : getHandlerUtils().getDnsIpAndDomains()) {
                commands.add(String.format("echo %s >> %s", ipAndDomain, CloudInitConfigCommon.LINUX_HOSTS_PATH));
            }
        }

        if (commands != null && !commands.isEmpty()) {
            boolean executeOnce = true;
            for (var command : commands) {
                if (command.startsWith(ContainerFileUtils.CONTAINER_COPY_FILE_PREFIX)) {
                    executeOnce = false;
                    break;
                }
            }
            if (executeOnce) {
                List<String> finnalCommands = new ArrayList<>();
                for (var command : commands) {
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
                for (var command : commands) {
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
        var ports = getPluginHandler().getPorts(cloudElement);
        if (ports != null) {
            for (var port : ports) {
                if (port.getAccessModes() != null) {
                    for (var access : port.getAccessModes()) {
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
                if (srcPort.isUsing()) {
                    srcPort.setIp(virtualNic.getFixedIp());
                    srcPort.setMacAddr(virtualNic.getMacAddress());
                }

                cloudPort.setResourceId(virtualNic.getPortId());
                cloudPort.setQosId(virtualNic.getQosId());
                cloudPort.setFloatingIp(virtualNic.getFloatingip());
                cloudPort.setFloatingIpId(virtualNic.getFloatingipId());
                cloudPort.setAuxiliaryNetworkId(virtualNic.getNetworkId());

                var accessModesData = virtualNic.getAccessModes();
                if (accessModesData != null && !accessModesData.isEmpty()) {
                    Map<String, VirtualNic.AccessMode> accessModeDataMap = new HashMap<>();
                    for (var accessMode : accessModesData) {
                        accessModeDataMap.put(this.genAccessModeKey(accessMode), accessMode);
                    }
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
                for (var accessModeData : accessModesData) {
                    if (AccessMode.Protocol.SNMP.equals(accessModeData.getProtocol())) {
                        var portMapping = accessModeData.getPortMapping();
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
                    var portMapping = accessModeData.getPortMapping();
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
            createData.setRoutes(staticRoutes);
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
        Runnable virtualMachineRunnable = () -> {
            if (ElementTypeUtils.isExternal(cloudElement)) {
                // 外部虚拟机 需要连接网络
                var nics = CollectionUtils.emptyNull(genVirutalNics(cloudElement));
                if (nics != null && nics.size() > 0) {
                    List<VirtualNic> created = new ArrayList<>();
                    nics.forEach(n -> {
                        VirtualMachineAddPortData port = new VirtualMachineAddPortData();
                        port.setFixedIp(n.getFixedIp());
                        port.setNetworkId(n.getNetworkId());
                        var result = vmClient.addPort(cloudElement.getResourceId(), port);
                        VirtualNic virtualNic = new VirtualNic();
                        virtualNic.setPortId(result.getPortId());
                        virtualNic.setNetworkId(result.getNetworkId());
                        virtualNic.setFixedIp(result.getFixedIp());
                        created.add(virtualNic);
                    });
                    this.receiveVirtualPortInfo(cloudElement, created);
                }
                return;
            }
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
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(cloudElement.getEntityUsbs()) && StringUtils.isNotEmpty(cloudElement.getEntityUsbs().get(0).getHost())) {
                createData.setHost(cloudElement.getEntityUsbs().get(0).getHost());
            }
            if (Boolean.TRUE.equals(cloudElement.getInitSupport())) {
                createData.setUserData(TextUtils.emptyNull(combineInitCommand(cloudElement)));
            }
            createData.setNetworks(CollectionUtils.emptyNull(genVirutalNics(cloudElement)));
            if (StringUtils.isNotEmpty(cloudElement.getIsoAttachFilename()) && StringUtils.isNotEmpty(cloudElement.getIsoTmpdirUuid())) {
                createData.setIsoAttach(true);
            }
            var virtualMachine = vmClient.create(createData);
            cloudElement.setResourceId(virtualMachine.getId());
            cloudElement.setHost(virtualMachine.getHost());

            receiveVirtualPortInfo(cloudElement, virtualMachine.getNetworks());

            if (ElementTypeUtils.isCoreRouter(cloudElement)) {
                this.configVyosNetwork(cloudElement);
            }

            if (StringUtils.isNotEmpty(cloudElement.getIsoAttachFilename()) && StringUtils.isNotEmpty(cloudElement.getIsoTmpdirUuid())) {
                this.attachISO(cloudElement);
            }
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(cloudElement.getVirtualUsbs())) {
                this.attachVirtualUsbs(cloudElement);
            }
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(cloudElement.getEntityUsbs())) {
                this.attachEntityUsbs(cloudElement);
            }

            checkAccessModes(cloudElement);
        };
        if (HfdyVpnImage.VPN_CLIENT.equals(cloudElement.getImage()) || HfdyVpnImage.VPN_SERVER.equals(cloudElement.getImage())) {
            this.getPluginHandler().getSceneHandler().setVpnMachineRunnable(virtualMachineRunnable);
        } else {
            virtualMachineRunnable.run();
        }
    }

    protected void createContainer(CloudPluginSceneElementBO cloudElement) {
        List<ConnectedNetworkBO> allNets = new ArrayList<>();
        allNets.addAll(Optional.ofNullable(cloudElement.getAccessNetworks()).orElse(new ArrayList<>()));
        allNets.addAll(Optional.ofNullable(cloudElement.getDeriveNetworks()).orElse(new ArrayList<>()));
        CloudPluginSceneElementNetworkBO macvlanNet = null;
        for (var net : allNets) {
            if (BaseCloudPluginSceneElementModel.NetMode.MACVLAN.equals(net.network.getNetMode())) {
                macvlanNet = net.network;
                break;
            }
        }
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

        if (srcElement.getRam() != null && srcElement.getRam() > 1) {
            createData.setRam(srcElement.getRam());
        }
        if (srcElement.getDisk() != null && srcElement.getDisk() > 1) {
            createData.setDisk(srcElement.getDisk());
        }

        // 容器参数
        if (cloudElement.getDockerParams() != null) {
            var dockerParams = cloudElement.getDockerParams();
            createData.setEnvironment(dockerParams.getEnvironment());
            createData.setInteractive(dockerParams.getInteractive());
            createData.setPrivileged(dockerParams.getPrivileged());
            createData.setRun(dockerParams.getRun());
        }

        // 容器初始化命令通过execContainerInitCommand执行
//        if (Boolean.TRUE.equals(cloudElement.getInitSupport())) {
//            createData.setPostCommand(TextUtils.emptyNull(combineInitCommand(cloudElement)));
//        }
        if (macvlanNet != null) {
            MacvlanContainerCreateData macvlanContainerCreateData = new MacvlanContainerCreateData();
            BeanUtils.copyProperties(createData, macvlanContainerCreateData);
            macvlanContainerCreateData.setIp(cloudElement.getSrcElement().getPorts().get(0).getIp());
            macvlanContainerCreateData.setMacvlanId(macvlanNet.getVlanId());
            macvlanContainerCreateData.setGatewayIp(macvlanNet.getGateway());
            macvlanContainerCreateData.setCidr(macvlanNet.getCidr());
            if (StringUtils.isNotEmpty(cloudElement.getHost())) {
                macvlanContainerCreateData.setHost(cloudElement.getHost());
            }
            var container = containerClient.createMacvlanContainer(macvlanContainerCreateData);
            cloudElement.setResourceId(container.getId());
            cloudElement.setHost(container.getHost());
            execContainerInitCommand(cloudElement);
            receiveVirtualPortInfo(cloudElement);
        } else {
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
    }

    /**
     * @description: 创建实体设备
     * @param cloudElement 云平台场景节点业务信息
     * @return void
     */
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

    protected void createVirtualDynamips(CloudPluginSceneElementBO cloudElement) {
        VirtualDynamicCreateData createData = new VirtualDynamicCreateData();
        List<VirtualDynamicCreateData.Network> networks = new ArrayList<>();
        createData.setNetworks(networks);
        var srcElement = cloudElement.getSrcElement();
        createData.setName(HandlerUtils.genResourceName(srcElement.getEid()));
        createData.setImage(cloudElement.getImage());
        createData.setPortSum(cloudElement.getWanPortNumber());
        Map<String, SceneElementPortBO> portName2Port = new HashMap<>();
        if (srcElement.getPorts() != null) {
            Set<String> netIds = new HashSet<>();
            for (var port : srcElement.getPorts()) {
                portName2Port.put(port.getName(), port);
                var net = getHandlerUtils().getNetworkByPort(port);
                if (net != null && StringUtils.isNotEmpty(net.getResourceId()) && !netIds.contains(net.getResourceId())) {
                    VirtualDynamicCreateData.Network network = new VirtualDynamicCreateData.Network();
                    network.setNetworkId(net.getResourceId());
                    network.setSlotPort(port.getName());
                    network.setFixedIp(port.getIp());
                    networks.add(network);
                    netIds.add(net.getResourceId());
                } else {
                    port.setIp(StringUtils.EMPTY);
                }
            }
        }
        var result = vdClient.create(createData);

        cloudElement.setResourceId(result.getId());
        if (result.getNetworks() != null) {
            for (var networkPort : result.getNetworks()) {
                var port = portName2Port.get(networkPort.getSlotPort());
                if (port != null) {
                    port.setIp(networkPort.getFixedIp());
                    port.setMacAddr(networkPort.getMacAddress());
                    getPluginHandler().getPort(port.getId()).setResourceId(networkPort.getPortId());
                    var cloudPort = getPluginHandler().getPort(port.getId());
                    if (cloudPort != null && cloudPort.getAccessModes() != null) {
                        cloudPort.getAccessModes().forEach(a -> {
                            if (AccessMode.Protocol.TELNET.equals(a.getProtocol())) {
                                a.setAccessIp(result.getTelnetHost());
                                a.setAccessPort(result.getTelnetPort());
                            }
                        });
                    }
                }
            }
        }

        var cloudPorts = getPluginHandler().getPorts(cloudElement);
        // 同样ip、端口可能对应多个接入方式
        if (cloudPorts != null) {
            cloudPorts.forEach(cloudPort -> {
                if (Boolean.TRUE.equals(cloudPort.getSrcPort().getIsAuxiliary())) {
                    return;
                }
                var accessModes = cloudPort.getAccessModes();
                if (accessModes == null) {
                    return;
                }
                for (var accessMode : accessModes) {
                    if (AccessMode.Protocol.TELNET.equals(accessMode.getProtocol())) {
                        accessMode.setAvailable(true);
                    }
                }
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
        var createAuxiliaryNetworkTasks = getPluginHandler().getAuxiliaryNetworkElement().stream()
                .map(cloudElement -> (Runnable) () -> createAuxiliaryNetworkProcess(cloudElement))
                .collect(Collectors.toList());

        if (!createAuxiliaryNetworkTasks.isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_AUXILIARY_NETWORKS, () -> {
                runTasks(createAuxiliaryNetworkTasks);
                getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_NETWORK);
            });
        } else {
            getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_NETWORK);
        }


        // 再创建网关 和 防火墙
        getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CloudSceneStage.CREATE_GATEWAY);

        var createGatewayTasks = getPluginHandler().getGatewayElements().stream()
                .map(cloudElement -> (Runnable) () -> createElementProcess(cloudElement))
                .collect(Collectors.toList());
        var createFirewallTasks = getPluginHandler().getFirewallElements().stream()
                .map(cloudElement -> (Runnable) () -> createElementProcess(cloudElement))
                .collect(Collectors.toList());
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
        var npcNodes = getPluginHandler().getNpcElements();
        var createTerminalTasks = getPluginHandler().getTerminalExcludeNpcElements().stream()
                .map(cloudElement -> (Runnable) () -> createElementProcess(cloudElement)).collect(Collectors.toList());
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
            for (var cloudElement : npcNodes) {
                createElementProcess(cloudElement);
            }
            // 批量创建
            runSceneProgress(SceneProcess.CREATE_NPCS, () -> {
                createNpcElements(new ArrayList<>(network2Info.values()));
                getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_INSTANCE);
            });
        }
        getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_INSTANCE);


        getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CloudSceneStage.CONFIG);
        // 下发配置
        var sendDynamicConfigs = getPluginHandler().getDynamicElements().stream()
                .map(cloudElement -> (Runnable) () -> createConfigDynamipsTask(cloudElement))
                .collect(Collectors.toList());
        if (!sendDynamicConfigs.isEmpty()) {
            runSceneProgress(SceneProcess.CONFIGURE, () -> {
                runTasks(sendDynamicConfigs);
                getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CONFIG);
            });
        } else {
            getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CONFIG);
        }

        //通知云平台需要统计环境总速率的端口
        if (Boolean.TRUE.equals(getPluginHandler().getScene().getEnableRate())) {
            List<String> portIds = new ArrayList<>();
            Map<String, CloudPluginSceneElementPortBO> portMap = getPluginHandler().getElementPortMap();
            portMap.forEach((key,value) -> {
                if (StringUtils.isNotEmpty(value.getResourceId())) {
                    portIds.add(value.getResourceId());
                }
            });
            vpClient.syncFlowPortInfo(portIds, "add");
        }

        for (var e : getPluginHandler().getElementMap().values()) {
            String ip = "";
            if (e.getSrcElement().getPorts() != null) {
                for (var p : e.getSrcElement().getPorts()) {
                    if (StringUtils.isNotEmpty(p.getIp())) {
                        ip = p.getIp();
                    }
                }
            }
            String eid = e.getSrcElement().getEid();
            buildVulnerabilityData(e.getImage(), eid, ip);
        }

        //提交伪造数据
        INSERT_FAKE_DATA_POOL.scheduleWithFixedDelay(ThreadUtils.format(() -> {
            try {
                getPluginHandler().getElementMap().forEach((nodeId, cloudElement) -> {
                    try {
                        setTopData(cloudElement);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String eid = cloudElement.getSrcElement().getEid();
                    if (needFake(eid, getPluginHandler().getScene().getSrcScene().getEid())) {
                        //伪造主机信息数据
                        log.info("start fake eid : {}", cloudElement.getSrcElement().getEid());
                        insertHostInfoData(cloudElement);
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                log.error("fake data failed");
            }
        }), 5000, 15000, TimeUnit.MILLISECONDS);

    }

    public static  Map<String, List<ProcessData>> processDataCache = new ConcurrentHashMap<>();

    public static  Map<String, List<PortData>> portDataCache = new ConcurrentHashMap<>();

    public static  Map<String, List<InteData>> inteDataCache = new ConcurrentHashMap<>();

    public static boolean setTopData(CloudPluginSceneElementBO cloudElement) {
        String ip = null;
        String usename = null;
        String password = null;
        for (var p : cloudElement.getSrcElement().getPorts()) {
            for (var a : p.getAccessModes()) {
                if (AccessMode.Protocol.SSH.equals(a.getProtocol())) {
                    ip = p.getIp();
                    usename = a.getUsername();
                    password = a.getPassword();
                }
            }
        }
        if (ip == null || usename == null || password == null) {
            return false;
        }

        CommandTopData commandTopData = null ;
        if (cloudElement.getImage().contains("netgear")) {
            commandTopData = new NetgearData(ip, usename, password).getData();
        } else if (cloudElement.getImage().contains("junos")) {
            commandTopData = new JuniperData(ip, usename, password).getData();
        } else if (cloudElement.getImage().contains("huawei")) {
            commandTopData = new CommonData(ip, usename, password).getData("huawei.py");
        } else if (cloudElement.getImage().contains("ruijie")) {
            commandTopData = new CommonData(ip, usename, password).getData("ruijie.py");
        } else if (cloudElement.getImage().contains("dell")) {
            commandTopData = new CommonData(ip, usename, password).getData("dell.py");
        } else if (cloudElement.getImage().contains("h3c")) {
            commandTopData = new CommonData(ip, usename, password).getData("h3c.py");
        } else if (cloudElement.getImage().contains("tplink")) {
            commandTopData = new CommonData(ip, usename, password).getData("tplink.py");
        } else if (cloudElement.getImage().contains("dlink")) {
            commandTopData = new CommonData(ip, usename, password).getData("dlink.py");
        }
        if (commandTopData == null) {
            return false;
        }
        doSave(ip, commandTopData, cloudElement);
        return false;
    }

    private static boolean doSave(String ip, CommandTopData data, CloudPluginSceneElementBO cloudElement) {
        String eid = cloudElement.getSrcElement().getEid();
        String hostInfoTemplate = getTemplate("config/host_info.json");
        hostInfoTemplate = hostInfoTemplate.replace("$ip", ip);
        hostInfoTemplate = hostInfoTemplate.replace("$name", cloudElement.getSrcElement().getEid());
        hostInfoTemplate = hostInfoTemplate.replace("$id", eid);

        hostInfoTemplate = hostInfoTemplate.replace("$timestamp", TimeUtils.localDateTimeToUtcString(LocalDateTime.now()));
        hostInfoTemplate = hostInfoTemplate.replace("$@timestamp", TimeUtils.localDateTimeToUtcString(LocalDateTime.now()));
        hostInfoTemplate = hostInfoTemplate.replace("$sic_launch_time", TimeUtils.localDateTimeToUtcString(LocalDateTime.now()));
        hostInfoTemplate = hostInfoTemplate.replace("$memory_used_percent", (data.getAvailableMemory() / cloudElement.getSrcElement()
                .getRam()) + "");
        hostInfoTemplate = hostInfoTemplate.replace("$cpu_used_percent", data.getCpuUsage() + "");
        int ram = Optional.ofNullable(cloudElement.getSrcElement().getRam()).orElse(1);
        hostInfoTemplate = hostInfoTemplate.replace("$memory_free", data.getAvailableMemory() + "");
        hostInfoTemplate = hostInfoTemplate.replace("$memory_total", ram + "");
        int cpu = Optional.ofNullable(cloudElement.getSrcElement().getCpu()).orElse(1);
        hostInfoTemplate = hostInfoTemplate.replace("$cpu_count", cpu + "");
        hostInfoTemplate = hostInfoTemplate.replace("$cpu_percent", data.getCpuUsage() + "");
        hostInfoTemplate = hostInfoTemplate.replace("$cpu_freq", "1861");

        long disk = Optional.ofNullable(cloudElement.getSrcElement().getDisk()).orElse(10) * 1024L * 1024 * 1024;
        hostInfoTemplate = hostInfoTemplate.replace("$disk_total", disk + "");
        hostInfoTemplate = hostInfoTemplate.replace("$disk_used", data.getDiskUsage() * 1024L * 1024 * 1024 + "");
        hostInfoTemplate = hostInfoTemplate.replace("$disk_io_percent", 1 + RANDOM.nextInt(3) + "");


        String suffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String indexName = "wazuh-alerts-4.x-" + suffix;
        IndexRequest request = new IndexRequest(indexName).source(JsonCommon.parseMap(hostInfoTemplate));
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(request);
//            saveDataToEs(bulkRequest);

        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(data.getProcessList())) {
            List<ProcessData> list = new ArrayList<>();
            for (var p : data.getProcessList()) {
                ProcessData processData = new ProcessData();
                processData.setCmd(p.getCommand());
                processData.setPid(Integer.parseInt(p.getPid()));
                processData.setName(p.getCommand());
                processData.setState(p.getState());
                processData.setEuser(p.getUser() == null ? p.getUsername() : p.getUser());
                list.add(processData);
            }
            processDataCache.put(cloudElement.getSrcElement().getEid(), list);
        }

        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(data.getPortList())) {
            List<PortData> list = new ArrayList<>();
            for (var p : data.getPortList()) {
                PortData portData = new PortData();
                portData.setProtocol(p.getProtocol());
                portData.setPid(p.getPid());
                portData.setLocal(new PortData.Local());
                portData.setRemote(new PortData.Remote());
                portData.setState(p.getState());
                if (p.getLocal() != null) {
                    portData.getLocal().setIp(p.getLocal().getIp());
                    portData.getLocal().setPort(p.getLocal().getPort());
                }
                if (p.getRemote() != null) {
                    portData.getRemote().setIp(p.getRemote().getIp());
                    portData.getRemote().setPort(p.getRemote().getPort());
                }
                list.add(portData);
            }
            portDataCache.put(cloudElement.getSrcElement().getEid(), list);
        }

        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(data.getInteList())) {
            List<InteData> list = new ArrayList<>();
            for (var p : data.getInteList()) {
                InteData inteData = new InteData();
                inteData.setIp(p.getIp());
                inteData.setName(p.getName());
                inteData.setMtu(p.getMtu());
                inteData.setMac(p.getMac());
                inteData.setState(p.getState());
                inteData.setType(p.getType());
                list.add(inteData);
            }
            inteDataCache.put(cloudElement.getSrcElement().getEid(), list);
        }
        return true;
    }


    public static void insertHostInfoData(CloudPluginSceneElementBO cloudElement) {
        String eid = cloudElement.getSrcElement().getEid();
        String imageName = cloudElement.getImage();
        String hostInfoTemplate = getTemplate("config/host_info.json");
        String ip = "";
        try {
            ip = cloudElement.getSrcElement().getPorts().get(0).getIp();
        } catch (Exception e) {
            ip = "";
        }
        hostInfoTemplate = hostInfoTemplate.replace("$ip", ip);
        hostInfoTemplate = hostInfoTemplate.replace("$name", eid);
        hostInfoTemplate = hostInfoTemplate.replace("$id", eid);

        hostInfoTemplate = hostInfoTemplate.replace("$timestamp", TimeUtils.localDateTimeToUtcString(LocalDateTime.now()));
        hostInfoTemplate = hostInfoTemplate.replace("$@timestamp", TimeUtils.localDateTimeToUtcString(LocalDateTime.now()));
        hostInfoTemplate = hostInfoTemplate.replace("$sic_launch_time", TimeUtils.localDateTimeToUtcString(LocalDateTime.now()));
        hostInfoTemplate = hostInfoTemplate.replace("$cpu_used_percent", 20 +RANDOM.nextInt(20) + "");
        long ram = Optional.ofNullable(cloudElement.getSrcElement().getRam()).orElse(1) * 1024 * 1024L;
        if (ram == 0) {
            ram = (long) 1024 * 1024;
        }
        long fakeRamUsed = (ram / (6 + RANDOM.nextInt(5)));

        hostInfoTemplate = hostInfoTemplate.replace("$memory_free", ram - fakeRamUsed  + "");
        hostInfoTemplate = hostInfoTemplate.replace("$memory_total", ram + "");
        hostInfoTemplate = hostInfoTemplate.replace("$memory_used_percent", (fakeRamUsed * 100L) / ram + "");

        int cpu = Optional.ofNullable(cloudElement.getSrcElement().getCpu()).orElse(1);
        hostInfoTemplate = hostInfoTemplate.replace("$cpu_count", cpu + "");
        hostInfoTemplate = hostInfoTemplate.replace("$cpu_percent", 1 + RANDOM.nextInt(5) + "");
        hostInfoTemplate = hostInfoTemplate.replace("$cpu_freq", "1861");

        long disk = Optional.ofNullable(cloudElement.getSrcElement().getDisk()).orElse(10) * 1024L * 1024L * 1024L;
        if (disk == 0) {
            disk = 10L * 1024 * 1024 * 1024;
        }
        hostInfoTemplate = hostInfoTemplate.replace("$disk_total", disk + "");
        hostInfoTemplate = hostInfoTemplate.replace("$disk_used", disk / (7 + RANDOM.nextInt(3))  + "");
        hostInfoTemplate = hostInfoTemplate.replace("$disk_io_percent", 1 + RANDOM.nextInt(3) + "");


        String suffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String indexName = "wazuh-alerts-4.x-" + suffix;
        IndexRequest request = new IndexRequest(indexName).source(JsonCommon.parseMap(hostInfoTemplate));
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(request);
        log.info("save to es : {} {}", indexName, hostInfoTemplate);
        saveDataToEs(bulkRequest);
    }

    /**
     * 当前节点id是否需要伪造数据，如果节点存在真实数据就不要伪造了。
     * @param eid
     * @return
     */
    public static boolean needFake(String eid, String sceneInstanceId) {
        //如果当前场景实例关闭了也就需要伪造数据了。
        SceneMapper sceneMapper =  BeanCommon.getBean(SceneMapper.class);
        SceneModel sceneModel = sceneMapper.get(sceneInstanceId);
        if (Objects.isNull(sceneModel)) {
            return false;
        } else {
            if (Objects.nonNull(sceneModel.getDeleted()) && sceneModel.getDeleted()) {
                return false;
            }
        }
        // 1. 使用 BoolQuery 组合多个条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("agent.id", eid))  // 精确匹配 agent.id
                .must(QueryBuilders.termQuery("data.sic_launch_id.keyword", "eda72998ba654c8d8625f61eaa918eb3"))  // 确保字段为 keyword
                .must(QueryBuilders.existsQuery("fake"));  // 存在 fake 字段

// 2. 创建 SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource()
                .query(boolQuery)
                .size(1);  // 仅需判断是否存在，取1条足够

// 3. 构建 SearchRequest

        String suffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String indexName = "wazuh-alerts-4.x-" + suffix;
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.indicesOptions(IndicesOptions.lenientExpandOpen());  // 合并 ignoreUnavailable 和 allowNoIndices
        searchRequest.source(sourceBuilder);

// 4. 执行查询
        RestHighLevelClient restHighLevelClient = (RestHighLevelClient) BeanCommon.getBean("logRestHighLevelClient");
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            boolean needFake = (searchResponse.getHits().getTotalHits().value == 0);  // 无命中则需伪造
            if (!needFake) {
                NOT_FAKE_EID_SET.add(eid);
            }
            return needFake;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return true;
        }
    }



    public static void buildVulnerabilityData(String image, String eid, String ip){
        String templateJson = "";
        String indexName = "" ;
        CveDataMapper cveDataMapper = BeanCommon.getBean(CveDataMapper.class);
        var values = cveDataMapper.list(new HumanCondition().filterEqual(CveDataModel::getDeviceVersion, image));
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(values)) {
            if (StringUtils.isEmpty(templateJson)) {
                templateJson = getTemplate("config/cve_data_template.json");
            }
            if (StringUtils.isEmpty(indexName)) {
                String suffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
                indexName = "wazuh-alerts-4.x-" + suffix;
            }
            BulkRequest bulkRequest = new BulkRequest();
            for (var v : values) {
                Map<String, Object> data = JsonCommon.parseMap(templateJson);
                data.put("@timestamp", TimeUtils.localDateTimeToUtcString(LocalDateTime.now()));
                data.put("timestamp", TimeUtils.localDateTimeToUtcString(LocalDateTime.now()));
                ((Map) data.get("agent")).put("ip",ip);
                ((Map) data.get("agent")).put("name",eid);
                ((Map) data.get("data")).put("vulnerability", JsonCommon.parseMap(v.getContent()));
                IndexRequest request = new IndexRequest(indexName).source(data);
                bulkRequest.add(request);
            }
            saveDataToEs(bulkRequest);

        }
    }

    private static void saveDataToEs(BulkRequest bulkRequest) {
        RestHighLevelClient restHighLevelClient = (RestHighLevelClient) BeanCommon.getBean("logRestHighLevelClient");
        try {
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("failed to buildData ", e);
        }
    }

    public static String getTemplate(String templateFilePath) {
        org.springframework.core.io.Resource resource = new ClassPathResource(templateFilePath);
        String content = null;
        try {
            content = CharStreams.toString(new InputStreamReader(resource.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content;
    }

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    protected void prepareEventProcess() {
        prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);
    }

    public void createNpcElements(List<BaseNpcData.NetWorkNpcInfo> list) {
        NpcCreateData npcCreateData = new NpcCreateData();
        npcCreateData.setNetWorkNpcInfos(list);
        npcClient.create(npcCreateData);
        // todo npc其实没有resourceId
        for (var e : getPluginHandler().getTerminalElementMap().values()) {
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
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS)) {
            runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.CREATE_DYNAMIC, () -> createVirtualDynamips(cloudElement));
        }
    }

    protected void configDynamic(List<CloudPluginSceneElementBO> elementBOS) {
        List<CloudPluginSceneElementBO> dynamicElements = elementBOS.stream()
                .filter(e -> e.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS)
                .collect(Collectors.toList());
        if (dynamicElements.isEmpty()) {
            return;
        }

        List<Runnable> configDynamicIps = dynamicElements.stream().map(e -> (Runnable) () -> {
            createConfigDynamipsTask(e);
        }).collect(Collectors.toList());
        runTasks(configDynamicIps);
    }

    private void createConfigDynamipsTask(CloudPluginSceneElementBO e) {
        List<String> commands = new ArrayList<>();
        commands.addAll(DynamicCommandUtils.genSetIpCmdList(e, getPluginHandler()));
        commands.addAll(DynamicCommandUtils.genNetworkCmdList(e, getPluginHandler()));
        // 下发配置
        vdClient.sendRule(e.getResourceId(), commands);
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
        // 查询串口地址
        var port = vmRouter.queryPort(elementBO.getResourceId());
        if (port == null || StringUtils.isEmpty(port.getPort()) || StringUtils.isEmpty(port.getHost())) {
            log.error("get Serial Port failed , resource id is {} , port result is {}", elementBO.getResourceId(), JsonCommon.stringify(port));
            return;
        }

        // 拼接组网命令
        var defaultPort = getPluginHandler().getPort(elementBO.getSrcElement().getPorts().get(0).getId());
        List<String> cmds = VyosNetworkCommandUtils.genNetworkCmdList(elementBO, getPluginHandler());
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(cmds)) {
            log.error("no network config , element id is {} ", elementBO.getSrcElement().getId());
            return;
        }
        var buildNetwork = new RouterConfigCmdData();
        buildNetwork.setSerialIp(defaultPort.getAccessModes().get(0).getAccessIp());
        buildNetwork.setSerialPort(defaultPort.getAccessModes().get(0).getAccessPort());
        buildNetwork.setCmds(cmds);

        // 开启dhcp
        var openDhcp = new RouterConfigCmdData();
        openDhcp.setSerialIp(port.getHost());
        openDhcp.setSerialPort(Integer.parseInt(port.getPort()));
        openDhcp.setCmds(VyosNetworkCommandUtils.genOpenDhcpCommands(elementBO.getSrcElement().getPorts().size()));
        boolean openDhcpResult = false;
        boolean buildNetworkResult = false;
        int tryTimes = 5;
        Exception exception = null;
        for (int i = 0; i < tryTimes; i++) {
            // 先等2s,等待串口可用
            waitSeconds(2000);
            try {
                openDhcpResult = vmRouter.createConfig(openDhcp);
            } catch (Exception e) {
                log.error("failed to open dhcp , try time {}", i, e);
                openDhcpResult = false;
            }
            // 失败可能是串口仍不可用,需要重试
            if (!openDhcpResult) {
                continue;
            }
            // 发送拼接网络的命令
            try {
                buildNetworkResult = vmRouter.createConfig(buildNetwork);
            } catch (Exception e) {
                log.error("failed to build network , try time {}", i);
                exception = e;
                buildNetworkResult = false;
            }
            if (!buildNetworkResult) {
                // 设置失败需要重试
                continue;
            } else {
                // dhcp设置的ip可用之后 即可执行后续的命令
                break;
            }
        }
        if (openDhcpResult == false || buildNetworkResult == false) {
            log.error("build vyos network failed ,openDhcp {} , buildNetwork {} , resource id is {} , params is {} , ", openDhcp, buildNetwork, elementBO.getResourceId(), JsonCommon.stringify(openDhcp));
            if (exception != null) {
                log.error("failed to build network ", exception);
            }
            return;
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
        var usbUuids = cloudElement.getVirtualUsbs().stream().map(u -> u.getUuid()).collect(Collectors.toList());
        data.setVirtualUsbUuids(usbUuids);
        vmClient.attachVirtualUsbs(data);
    }

    private void attachEntityUsbs(CloudPluginSceneElementBO cloudElement) {
        log.info("CreateHandler.attachEntityUsbs begin. cloudElement:{}", cloudElement);
        AttachEntityUsbsData data = new AttachEntityUsbsData();
        data.setInstanceUuid(cloudElement.getResourceId());
        List<String> usbUuids = cloudElement.getEntityUsbs().stream().map(u -> u.getUuid()).collect(Collectors.toList());
        data.setEntityUsbUuids(usbUuids);
        vmClient.attachEntityUsbs(data);
    }

    @Getter
    @Setter
    public static class ProcessData{

        @ApiModelProperty(value = "真实用户")
        private String ruser;

        @ApiModelProperty(value = "进程驻留大小")
        private Integer resident;

        @ApiModelProperty(value = "开始时间")
        @JSONField(name = "start_time")
        private String startTime;

        @ApiModelProperty(value = "共享内存")
        private Integer share;

        @ApiModelProperty(value = "真实组")
        private String rgroup;

        @ApiModelProperty(value = "文件系统组名")
        private String fgroup;

        @ApiModelProperty(value = "执行的命令")
        private String cmd;

        @ApiModelProperty(value = "保存集用户")
        private String suser;

        @ApiModelProperty(value = "系统代码执行时长")
        private Long stime;

        @ApiModelProperty(value = "优先级")
        private String priority;

        @ApiModelProperty(value = "进程参数")
        private String argvs;

        @ApiModelProperty(value = "保存集组")
        private String sgroup;

        @ApiModelProperty(value = "父进程ID")
        private Integer ppid;

        @ApiModelProperty(value = "进程TTY数")
        private Long tty;

        @ApiModelProperty(value = "处理器编号")
        private Integer processor;

        @ApiModelProperty(value = "用户代码执行时长")
        private Long utime;

        @ApiModelProperty(value = "有效用户")
        private String euser;

        @ApiModelProperty(value = "虚拟内存总大小(KB)")
        @JSONField(name = "vm_size")
        private Long vmSize;

        @ApiModelProperty(value = "线程组ID")
        private Integer tgid;

        @ApiModelProperty(value = "进程组")
        private Integer pgrp;

        @ApiModelProperty(value = "进程大小(KB)")
        private Long size;

        @ApiModelProperty(value = "名称")
        private String name;

        @ApiModelProperty(value = "父进程")
        private Integer pid;

        @ApiModelProperty(value = "会话")
        private Integer session;

        //
        private Integer nice;

        @ApiModelProperty(value = "轻量级进程数")
        private Integer nlwp;

        @ApiModelProperty(value = "线程组 ID")
        private String egroup;

        @ApiModelProperty(value = "状态")
        private String state;

        @ApiModelProperty(value = "代理ID")
        @JSONField(name = "agent_id")
        private String agentId;

        @ApiModelProperty(value = "描述")
        private String description;

        @ApiModelProperty(value = "时间")
        private LocalDateTime time;

        @ApiModelProperty(value = "级别")
        private int level;
    }

    @Getter
    @Setter
    public static class PortData{
        @ApiModelProperty(value = "本地端口")
        private Local local;

        @ApiModelProperty(value = "远程端口")
        private Remote remote;

        @ApiModelProperty(value = "索引节点")
        private Integer inode;

        @ApiModelProperty(value = "父进程ID")
        private Integer pid;

        @ApiModelProperty(value = "接收队列中的数据包")
        @JSONField(name = "rx_queue")
        private Integer rxQueue;

        @ApiModelProperty(value = "待传输的数据包")
        @JSONField(name = "tx_queue")
        private String txQueue;

        @ApiModelProperty(value = "协议")
        private String protocol;

        @ApiModelProperty(value = "状态")
        private String state;

        @ApiModelProperty(value = "进程")
        private String process;

        @ApiModelProperty(value = "代理ID")
        @JSONField(name = "agent_id")
        private String agentId;


        @Data
        @ApiModel("本地端口")
        public static class Local {

            @ApiModelProperty("本地IP")
            private String ip;

            @ApiModelProperty("本地端口")
            private Integer port;
        }

        @Data
        @ApiModel("远程端口")
        public static class Remote {

            @ApiModelProperty("远程IP")
            private String ip;

            @ApiModelProperty("远程端口")
            private Integer port;
        }
    }

    @Getter
    @Setter
    public static class InteData{
        @ApiModelProperty(value = "接收包")
        private X rx;

        @ApiModelProperty(value = "发送包")
        private X tx;

        @ApiModelProperty(value = "类型")
        private String type;

        @ApiModelProperty(value = "mtu")
        private Integer mtu;

        @ApiModelProperty(value = "状态")
        private String state;

        @ApiModelProperty(value = "mac地址")
        private String mac;

        @ApiModelProperty(value = "名称")
        private String name;

        @ApiModelProperty(value = "探针ID")
        @JSONField(name = "agent_id")
        private String agentId;

        @ApiModelProperty(value = "IP地址")
        private String ip;

        /**
         * 接收或者发送的包
         */
        @Data
        @ApiModel("X")
        public static class X {

            @ApiModelProperty(value = "核心数")
            private Integer bytes;

            @ApiModelProperty(value = "丢弃")
            private Integer dropped;

            @ApiModelProperty(value = "错误")
            private String errors;

            @ApiModelProperty(value = "包数量")
            private String packets;
        }
    }
}
