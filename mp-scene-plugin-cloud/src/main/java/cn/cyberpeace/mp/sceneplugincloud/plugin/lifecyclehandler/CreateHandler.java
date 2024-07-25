// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name CreateHandler.java.
// File size 111789.
// File create date Fri Jul 12 12:55:27 CST 2024.
// File update date Fri Jul 12 12:55:27 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.CollectionUtils;
import cn.cyberpeace.mp.base.util.basic.IntUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
import cn.cyberpeace.mp.base.util.mptool.ConcurrentHashSet;
import cn.cyberpeace.mp.base.util.mptool.FileNameUtil;
import cn.cyberpeace.mp.common.config.CommonAppConfig;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.context.SceneContextHolder;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.core.event.base.SceneEventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.util.*;
import cn.cyberpeace.mp.sceneplugincloud.bo.*;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.*;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.constant.*;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementLogicalSwitchModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CsceneElementSoftVersionModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.device.Device;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.device.DeviceClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementLogicalSwitchMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CsceneElementSoftVersionMapper;
import cn.cyberpeace.mp.sceneplugincloud.util.ConfigNetworkUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.ElementTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.StrategyHttpUtil;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils.OS_TYPE_LINUX;

 /**
 * @ClassName CreateHandler
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see HandlerCommon
 */
@Slf4j
public class CreateHandler extends HandlerCommon {

    /**
     * 虚拟交换机客户端实例，用于管理虚拟网络交换机相关操作。
     */
    private final VirtualSwitch vsClient = new VirtualSwitch();

    /**
     * 虚拟路由器客户端实例，用于执行虚拟路由器相关功能。
     */
    private final VirtualRouter vrClient = new VirtualRouter();

    /**
     * 虚拟防火墙客户端实例，用于配置和管理虚拟环境中的防火墙规则。
     */
    private final VirtualFirewall vfClient = new VirtualFirewall();

    /**
     * 虚拟机客户端实例，用于创建、管理和操作虚拟机。
     */
    private final VirtualMachine vmClient = new VirtualMachine();

    /**
     * 虚拟端口客户端实例，用于管理和操作虚拟网络端口。
     */
    protected final VirtualPort vpClient = new VirtualPort();

    /**
     * 容器客户端实例，用于管理和操作容器实例。
     */
    private final Container containerClient = new Container();

    /**
     * 实体节点客户端实例，用于管理和操作实体节点。
     */
    private final EntityNode enClient = new EntityNode();

    /**
     * 远程客户端实例，用于远程访问和管理其他设备或服务。
     */
    private final Remote remoteClient = new Remote();

    /**
     * 非玩家角色客户端实例，可能用于模拟或测试环境中的虚拟角色。
     */
    private final Npc npcClient = new Npc();

    /**
     * 虚拟路由器实例，用于配置和管理虚拟网络中的路由功能。
     */
    private final VmRouter vmRouter = new VmRouter();

    /**
     * 虚拟Dynamips实例，Dynamips是一个开源的路由器模拟器，这里用于模拟路由器功能。
     */
    private VirtualDynamips vdClient = new VirtualDynamips();

    /**
     * 设备客户端实例，用于通用设备的管理和操作。
     */
    private DeviceClient deviceClient = new DeviceClient();

    private final Map<String, BaseNpcData.NetWorkNpcInfo> network2Info = new ConcurrentHashMap<>();

    private final Map<String, LogicalSwitchCreateData> network2LogicalSwitch = new ConcurrentHashMap<>();

    private final Set<EntityNodeCreateData> entityCreateData = new ConcurrentHashSet<>();

    private final Random random = new Random();

    private VirtualRouterCreateData probeRouteInfo = null;

    private Map<CloudPluginSceneElementNetworkBO, VirtualRouterCreateData> auxiliaryNetworkRouters = new ConcurrentHashMap<>();
    /**
     * 场景处理相关的静态常量类。
     * 提供了场景中各种元素创建和配置过程的枚举常量。
     */
    public static class SceneProcess {
        // 创建辅助网络的事件处理过程
        public static final SceneEventProcess CREATE_AUXILIARY_NETWORKS = new SceneEventProcess("create_auxiliary_networks", "sceneplugincloud.event.create_auxiliary_network", "create_network", 1);
        // 创建节点到外部路由器的事件处理过程
        public static final SceneEventProcess CREATE_NODE_TO_EXTERNAL_ROUTER = new SceneEventProcess("create_node_to_external_router", "sceneplugincloud.event.create_node_to_external_router", "create_network", 1);
        // 创建网关的事件处理过程
        public static final SceneEventProcess CREATE_GATEWAYS = new SceneEventProcess("create_gateways", "sceneplugincloud.event.create_gateway", "create_gateway", 1);
        // 创建防火墙的事件处理过程
        public static final SceneEventProcess CREATE_FIREWALLS = new SceneEventProcess("create_firewalls", "sceneplugincloud.event.create_firewall", "create_gateway", 2);
        // 创建终端的事件处理过程
        public static final SceneEventProcess CREATE_TERMINALS = new SceneEventProcess("create_terminals", "sceneplugincloud.event.create_terminal", "create_instance", 1);

        // 创建逻辑交换机的事件处理过程
        public static final EventProcess CREATE_LOGICAL_SWITCHES = new EventProcess("create_logical_switches", "sceneplugincloud.event.create_logical_switch");
        // 创建实体的事件处理过程
        public static final EventProcess CREATE_ENTITIES = new EventProcess("bind_physical_port", "sceneplugincloud.event.bind_physical_port");

        // 创建NPC的事件处理过程
        public static final SceneEventProcess CREATE_NPCS = new SceneEventProcess("create_npcs", "sceneplugincloud.event.create_npcs", "create_instance", 1);
        // 配置场景的事件处理过程
        public static final SceneEventProcess CONFIGURE = new SceneEventProcess("configure", "sceneplugincloud.event.configure", "config", 1);
    }

    /**
     * 元素处理相关的静态常量类。
     * 提供了场景中各种元素创建过程的枚举常量。
     */
    public static class ElementProcess {
        // 创建辅助网络的事件处理过程
        public static final EventProcess CREATE_AUXILIARY_NETWORK = new EventProcess("create_auxiliary_network", "sceneplugincloud.event.create_auxiliary_network");
        // 创建虚拟交换机的事件处理过程
        public static final EventProcess CREATE_VIRTUAL_SWITCH = new EventProcess("create_virtual_switch", "sceneplugincloud.event.create_virtual_switch");
        // 创建虚拟路由器的事件处理过程
        public static final EventProcess CREATE_VIRTUAL_ROUTER = new EventProcess("create_virtual_router", "sceneplugincloud.event.create_virtual_router");
        // 创建虚拟防火墙的事件处理过程
        public static final EventProcess CREATE_VIRTUAL_FIREWALL = new EventProcess("create_virtual_firewall", "sceneplugincloud.event.create_virtual_firewall");
        // 创建虚拟机器的事件处理过程
        public static final EventProcess CREATE_VIRTUAL_MACHINE = new EventProcess("create_virtual_machine", "sceneplugincloud.event.create_virtual_machine");
        // 创建容器的事件处理过程
        public static final EventProcess CREATE_CONTAINER = new EventProcess("create_container", "sceneplugincloud.event.create_container");
        // 创建实体节点的事件处理过程
        public static final EventProcess CREATE_ENTITY_NODE = new EventProcess("create_entity_node", "sceneplugincloud.event.create_entity_node");
        // 创建NPC的事件处理过程
        public static final EventProcess CREATE_NPC = new EventProcess("create_npc", "sceneplugincloud.event.create_npc");
        // 创建动态元素的事件处理过程
        public static final EventProcess CREATE_DYNAMIC = new EventProcess("create_dynamic", "sceneplugincloud.event.create_dynamic");
    }

    /**
     * 云防火墙规则类，用于存储入站和出站防火墙规则。
     *
     * @author
     * @since
     */
    @Data
    public static class CloudFirewallRules {
        /**
         * 入站防火墙规则列表。
         */
        private List<VirtualFirewallCreateData.Rule> ingressRules;
        /**
         * 出站防火墙规则列表。
         */
        private List<VirtualFirewallCreateData.Rule> egressRules;

        /**
         * 根据源防火墙规则列表创建云防火墙规则对象。
         *
         * @param srcRules 源防火墙规则列表。
         * @return CloudFirewallRules 对象，包含入站和出站规则。
         */
        /**
         * fromSrcRules
         *
         * @param srcRules
         * @return
         */
        public static CloudFirewallRules fromSrcRules(List<FirewallRule> srcRules) {
            List<VirtualFirewallCreateData.Rule> ingressRules = new ArrayList<>();
            List<VirtualFirewallCreateData.Rule> egressRules = new ArrayList<>();
            if (srcRules != null) {
                srcRules.forEach(srcRule -> {
                    // 初始化源端口和目标端口列表，如果未指定端口，则使用null占位
                    // 如果未设置端口 则使用一个null的元素,使用统一的一个循环
                    List<String> sourcePorts = Arrays.asList(new String[]{null});
                    List<String> desPorts = Arrays.asList(new String[]{null});

                    // 如果指定了源端口，则将其拆分并存储
                    if (StringUtils.isNotEmpty(srcRule.getSourcePort())) {
                        String[] portsArray = srcRule.getSourcePort().split(",");
                        if (portsArray != null && portsArray.length > 0) {
                            sourcePorts = Arrays.asList(portsArray);
                        }
                    }
                    // 如果指定了目标端口，则将其拆分并存储
                    if (StringUtils.isNotEmpty(srcRule.getDestinationPort())) {
                        String[] portsArray = srcRule.getDestinationPort().split(",");
                        if (portsArray != null && portsArray.length > 0) {
                            desPorts = Arrays.asList(portsArray);
                        }
                    }

                    // 遍历所有组合的源端口和目标端口，创建并添加相应的防火墙规则
                    for (String sourcePort : sourcePorts) {
                        for (String desPort : desPorts) {
                            String direction = srcRule.getDirection();
                            VirtualFirewallCreateData.Rule rule = new VirtualFirewallCreateData.Rule();
                            // 设置规则的属性
                            rule.setSourceIpAddress(srcRule.getSourceIpAddress());
                            rule.setDestinationIpAddress(srcRule.getDestinationIpAddress());
                            rule.setSourcePort(sourcePort);
                            rule.setDestinationPort(desPort);
                            rule.setProtocol(srcRule.getProtocol());
                            rule.setAction(srcRule.getAction());

                            // 根据方向，将规则添加到入站或出站规则列表，如果是双向，则同时添加
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

    /**
     * genAccessModeKey
     *
     * @param protocol
     * @param port
     * @param username
     * @param password
     * @return
     */
    private String genAccessModeKey(String protocol, String port, String username, String password) {
        return String.format("%s:%s:%s:%s", protocol, AccessMode.Protocol.CONSOLE.equals(protocol) ? StringUtils.EMPTY : port, username, password);
    }

    /**
     * genAccessModeKey
     *
     * @param protocol
     * @param port
     * @param username
     * @param password
     * @return
     */
    private String genAccessModeKey(String protocol, Integer port, String username, String password) {
        return genAccessModeKey(protocol, port == null ? StringUtils.EMPTY : port.toString(), username, password);
    }

    /**
     * genAccessModeKey
     *
     * @param accessMode
     * @return
     */
    private String genAccessModeKey(CloudAccessMode accessMode) {
        return genAccessModeKey(accessMode.getProtocol(), accessMode.getPort(), accessMode.getUsername(),
                accessMode.getPassword());
    }

    /**
     * genAccessModeKey
     *
     * @param accessMode
     * @return
     */
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

    /**
     * genAccessModeKey
     *
     * @param accessMode
     * @return
     */
    private String genAccessModeKey(EntityNodeCreateData.Interface.AccessMode accessMode) {
        return genAccessModeKey(accessMode.getProtocol(), accessMode.getPort(), accessMode.getUsername(),
                accessMode.getPassword());
    }

    /**
     * createAuxiliaryNetwork
     *
     * @param cloudElement
     */
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

    /**
     * createAuxiliaryNetworkRouter
     *
     * @param cloudElement
     * @param network
     * @param index
     */
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
        auxiliaryNetworkRouters.put(network, createData);
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

    /**
     * createVirtualRouterGroupPortFirewall
     *
     * @param cloudElement
     */
    private void createVirtualRouterGroupPortFirewall(CloudPluginSceneElementBO cloudElement) {
        // 单独创建端口防火墙
        for (ConnectedNetworkBO network : cloudElement.getDeriveNetworks()) {
            createVirtualRouterPortsFirewall(cloudElement, network.getFromPorts());
        }
        for (ConnectedNetworkBO network : cloudElement.getAccessNetworks()) {
            createVirtualRouterPortsFirewall(cloudElement, network.getFromPorts());
        }
    }

    /**
     * createVirtualRouterPortsFirewall
     *
     * @param cloudElement
     * @param ports
     */
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

    /**
     * combineInitCommand
     *
     * @param cloudElement
     * @return
     */
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

        String versionImage = SceneSingleton.sceneAppConfig.getVersionImage();
        String cloudElementImage = cloudElement.getImage();
        log.info("version image is `{}` , cloud element image is `{}`", versionImage, cloudElementImage);
        if (SceneSingleton.sceneAppConfig.getVersionImage().equals(cloudElement.getImage())) {
            log.info("add upgrade soft check script start");
            List<LocalFileMappingModel> mappings = LocalFileMappingCommon.getMappings("wazuh_agent_installer");
            Map<String, String> nameFileMap = new HashMap<>();
            for (LocalFileMappingModel localFileMappingModel : mappings) {
                nameFileMap.put(localFileMappingModel.getName(), localFileMappingModel.getRemoteFile());
            }
            String downloadUrl = StringUtils.joinWith(
                    InstallerUtils.FILE_URL_SEPARATOR,
                    SceneSingleton.sceneAppConfig.getMetadataFileServer(),
                    nameFileMap.getOrDefault("get_version.sh", StringUtils.EMPTY)
            );
            String pcreDownloadUrl = StringUtils.joinWith(
                    InstallerUtils.FILE_URL_SEPARATOR,
                    SceneSingleton.sceneAppConfig.getMetadataFileServer(),
                    nameFileMap.getOrDefault("pcre3.deb", StringUtils.EMPTY)
            );
            commands.add("cd /tmp");
            commands.add("curl -o /tmp/get_version.sh " + downloadUrl);
            commands.add("curl -o /tmp/pcre3.deb " + pcreDownloadUrl);
            String reportUrl = SceneSingleton.sceneAppConfig.getVersionReportUrl();

            var mapper = BeanCommon.getBean(CsceneElementSoftVersionMapper.class);
            var list = mapper.list(new HumanCondition().filterEqual(CsceneElementSoftVersionModel::getEid, "default"));
            List<String> softNames = new ArrayList<>();
            list.forEach(l -> softNames.add(l.getSoft()));
            String softs = StringUtils.join(softNames, "_and_");

            commands.add("nohup sh /tmp/get_version.sh " + reportUrl + " " + softs + " " + cloudElement.getSrcElement()
                    .getEid() + "  > out.log 2>&1 &");
            log.info("add upgrade soft check script end");
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

    /**
     * behaviorSupportInitCommand
     *
     * @param sceneElementBO
     * @return
     */
    private List<String> behaviorSupportInitCommand(SceneElementBO sceneElementBO) {
        List<String> behaviorInitCommand = new ArrayList<>();
        // 支持行为
        if (Boolean.TRUE.equals(sceneElementBO.getBehaveSupport())) {
            behaviorInitCommand.add(String.format("C:\\Users\\Administrator\\AppData\\Local\\Programs\\Python\\Python312\\python.exe C:\\init.py %s %s %s",
                    SceneContextHolder.getCsceneInstanceId(), sceneElementBO.getId(), BeanCommon.getBean(CommonAppConfig.class).getMpIp()));

        }
        return behaviorInitCommand;

    }

    /**
     * getBasDeviceInitCommand
     *
     * @param sceneElementBO
     * @return
     */
    private List<String> getBasDeviceInitCommand(SceneElementBO sceneElementBO) {
        List<String> basDeviceInitCommand = new ArrayList<>();

        if (Boolean.TRUE.equals(sceneElementBO.getAttackMockDeviceFlag())) {
            String standardSystemType = SystemTypeUtils.getStandardSystemType(sceneElementBO.getSystemType());
            // 虚拟机本身ip，随机选一个
            String clientIp = sceneElementBO.getPorts().get(0).getIp();

            if (SystemTypeCommandUtils.isWindows(standardSystemType)) {
                basDeviceInitCommand.add("@echo off");
                basDeviceInitCommand.add("setlocal enabledelayedexpansion");
                basDeviceInitCommand.add("set \"search_ip=server_ip\"");
                basDeviceInitCommand.add(String.format("set \"replace_ip=%s\"", BeanCommon.getBean(CommonAppConfig.class).getBasIp()));
                basDeviceInitCommand.add("set \"search_port=server_port\"");
                basDeviceInitCommand.add(String.format("set \"replace_port=%s\"", BeanCommon.getBean(CommonAppConfig.class).getBasPort()));
                basDeviceInitCommand.add(String.format("set \"simulator_ip=%s\"", clientIp));

                basDeviceInitCommand.add("set \"search_template_id=mp_scene_template_id\"");
                basDeviceInitCommand.add(String.format("set \"replace_template_id=%s\"", SceneContextHolder.getCsceneInstanceId()));
                basDeviceInitCommand.add("set \"input=C:\\cpbas\\client\\application.yml\"");

                basDeviceInitCommand.add("set \"output=C:\\cpbas\\client\\application.yml.tmp\"");
                basDeviceInitCommand.add("(for /f \"tokens=* delims=\" %%i in ('type \"%input%\"') do (");
                basDeviceInitCommand.add("    set \"line=%%i\"");
                basDeviceInitCommand.add("    set \"line=!line:%search_ip%=%replace_ip%!\"");
                basDeviceInitCommand.add("    set \"line=!line:%search_port%=%replace_port%!\"");
                basDeviceInitCommand.add("    set \"line=!line:%search_template_id%=%replace_template_id%!\"");
                basDeviceInitCommand.add("    set \"line=!line:%client_ip%=%simulator_ip%!\"");

                basDeviceInitCommand.add("    echo(!line!");
                basDeviceInitCommand.add(")) > \"%output%\"");
                basDeviceInitCommand.add("move /Y \"%output%\" \"%input%\"");
                basDeviceInitCommand.add("endlocal");

            } else if (SystemTypeCommandUtils.isLinux(standardSystemType)) {
                basDeviceInitCommand.add(String.format("sed -i \"/platform_address:/s/bas-server/%s/\" /opt/cpbas/client/application.yml",
                        BeanCommon.getBean(CommonAppConfig.class).getBasIp()));
                basDeviceInitCommand.add(String.format("sed -i \"/platform_port:/s/server_port/%s/\" /opt/cpbas/client/application.yml",
                        BeanCommon.getBean(CommonAppConfig.class).getBasPort()));
                basDeviceInitCommand.add(String.format("sed -i \"/template_id:/s/mp_scene_template_id/%s/\" /opt/cpbas/client/application.yml",
                        SceneContextHolder.getCsceneInstanceId()));
                basDeviceInitCommand.add(String.format("sed -i \"/client_ip:/s/simulator_ip/%s/\" /opt/cpbas/client/application.yml",
                        clientIp));


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

    /**
     * receiveVirtualPortInfo
     *
     * @param cloudElement
     */
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

    /**
     * receiveVirtualPortInfo
     *
     * @param cloudElement
     * @param virtualNics
     */
    private void receiveVirtualPortInfo(CloudPluginSceneElementBO cloudElement, List<VirtualNic> virtualNics) {
        if (virtualNics == null) {
            return;
        }

        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(virtualNics)) {
            for (var n : virtualNics) {
                if (StringUtils.equals(n.getNetworkId(), CloudSingleton.cloudAppConfig.getConfigNetId())) {
                    cloudElement.setConfigIp(n.getFixedIp());
                    ConfigNetworkUtils.save(n.getFixedIp(), getPluginHandler().getSrcScene().getEid());
                    break;
                }
            }
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

    /**
     * receiveEntityInterfaceInfo
     *
     * @param cloudElement
     * @param entityInterfaces
     */
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

    /**
     * createVirtualRouter
     *
     * @param cloudElement
     */
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

        // 设定各路由的路由表：网络中的网关，如果遇到发往外部网络的流量时，将流量转发到 ProbeRouter 上的端口
        if (probeRouteInfo != null) {
            for (var net : createData.getNetworks()) {
                boolean addOk = false;
                for (var probeRouteNet : probeRouteInfo.getNetworks()) {
                    if (StringUtils.equals(net.getId(), probeRouteNet.getId())) {
                        String nextHop = probeRouteNet.getFixedIp();
                        String externalNetCidr = CloudSingleton.cloudAppConfig.getExternalNetCidr();
                        if (createData.getRouteStatics() == null) {
                            createData.setRouteStatics(new ArrayList<>());
                        }
                        createData.getRouteStatics()
                                .add(new VirtualRouterCreateData.StaticRoute(externalNetCidr, nextHop));
                        // 只需要设置一条路由即可
                        addOk = true;
                        break;
                    }
                }
                if (addOk) {
                    break;
                }
            }
        }

        var virtualRouter = vrClient.create(createData);

        cloudElement.setResourceId(virtualRouter.getId());
        receiveVirtualRouterPortInfo(cloudElement, virtualRouter.getNetworks());
    }

    /**
     * receiveVirtualRouterPortInfo
     *
     * @param cloudElement
     * @param virtualRouterNics
     */
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

    /**
     * createVirtualFirewall
     *
     * @param cloudElement
     */
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

    /**
     * createVirtualMachine
     *
     * @param cloudElement
     */
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
        this.checkAndAddAccessModes(cloudElement);
    }

    /**
     * createContainer
     *
     * @param cloudElement
     */
    protected void createContainer(CloudPluginSceneElementBO cloudElement) {

        SceneElementBO srcElement = cloudElement.getSrcElement();
        ContainerCreateData createData = new ContainerCreateData();
//        createData.setRequestUser(TextUtils.emptyNull(getHandlerUtils().getRequestUser()));
        createData.setName(HandlerUtils.genResourceName(srcElement.getEid()));
        var device = new DeviceClient().findByUuid(cloudElement.getImageUuid());
        String image = device.getExtImageInfo().getBaseImageName();
        String version = device.getExtImageInfo().getVersion();
        createData.setImage(image + ":" + version);
        createData.setArch(device.getExtSystemInfo().getSystemArchitecture());

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

            if (dockerParams.getEnvironment() != null && !dockerParams.getEnvironment().isEmpty()) {
                List<String> envs = new ArrayList<>();
                for (var e : dockerParams.getEnvironment().entrySet()) {
                    envs.add(e.getKey() + "=" + e.getValue());
                }
                createData.setEnvs(envs);
            }

            createData.setInteractive(dockerParams.getInteractive());
            createData.setPrivileged(dockerParams.getPrivileged());
            createData.setTty(dockerParams.getTty());
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
        this.checkAndAddAccessModes(cloudElement);
    }

    /**
     * @description: 创建实体设备
     * @param cloudElement 云平台场景节点业务信息
     * @return void
     */
    protected void createEntityNode(CloudPluginSceneElementBO cloudElement) {
        this.genEntityCreateData(cloudElement);
    }

    /**
     * genEntityCreateData
     *
     * @param cloudElement
     */
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
            String physicalSwitchName = cloudPort.getEntitySwitchName();
            log.info("physicalSwitchName:{}", physicalSwitchName);
            log.info("entitySwitchPort:{}", cloudPort.getEntitySwitchPort());
            log.info("cloudPort.getEntityPhysicalSwitchName():{}", cloudPort.getEntitySwitchName());
            log.info("cloudPort.getEntitySwitchPort():{}", cloudPort.getEntitySwitchPort());
            String key = StringUtils.joinWith(":", networkId, physicalSwitchName);
            LogicalSwitchCreateData data;
            synchronized (network2LogicalSwitch) {
                data = network2LogicalSwitch.get(key);
                if (Objects.isNull(data)) {
                    data = new LogicalSwitchCreateData();
                    data.setLogicalSwitchName(convertFirstAlphaToNumber(TextUtils.uuid()));
                    data.setPhysicalSwitchName(cloudPort.getEntitySwitchName());
                    data.setPhysicalPorts(new ArrayList<>());
                    if (StringUtils.isBlank(cloudPort.getEntitySwitchName())) {
                        log.error("实体设备没有配置物理交换机名称");
                    } else {
                        network2LogicalSwitch.put(key, data);
                    }
                }
            }
            LogicalSwitchCreateData.PhysicalPort physicalPort = new LogicalSwitchCreateData.PhysicalPort();
            physicalPort.setPort(cloudPort.getEntitySwitchPort());
//            physicalPort.setPort(cloudPort.getEntityPort());
            physicalPort.setVlan(port.getVlanNumber());
            data.getPhysicalPorts().add(physicalPort);

            EntityNodeCreateData createData = new EntityNodeCreateData();
            createData.setLogicalSwitch(data.getLogicalSwitchName());

            cloudPort.setAuxiliaryNetworkId(networkId);
            createData.setPsPortName(cloudPort.getEntitySwitchPort());
//            createData.setPsPortName(cloudPort.getEntityPort());
            createData.setPhysicalSwitch(cloudPort.getEntitySwitchName());
            createData.setNetworkId(networkId);
            createData.setName(TextUtils.uuid());
            createData.setMac(port.getMacAddr());
            createData.setIpAddr(port.getIp());
            if (StringUtils.isNotBlank(cloudPort.getEntitySwitchName())) {
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

    /**
     * convertFirstAlphaToNumber
     *
     * @param logicSwitchName
     * @return
     */
    private String convertFirstAlphaToNumber(String logicSwitchName) {
        String suffix = logicSwitchName.substring(1, logicSwitchName.length());
        String prefixNumber = String.valueOf(this.random.nextInt(9));
        return prefixNumber + suffix;
    }


    /**
     * recordNpcNode
     *
     * @param cloudElement
     */
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


    /**
     * CreateHandler
     *
     * @param pluginHandler
     */
    public CreateHandler(CloudScenePluginHandler pluginHandler) {
        super(pluginHandler, CreateTaskPool.getInstance(), SceneModel.Event.CREATE);
    }

    /**
     * create
     */
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

//        runSceneProgress(SceneProcess.CREATE_NODE_TO_EXTERNAL_ROUTER, this::createNodeToExternalRouter);
        if (!this.auxiliaryNetworkRouters.isEmpty()) {
            for (Map.Entry<CloudPluginSceneElementNetworkBO, VirtualRouterCreateData> a : this.auxiliaryNetworkRouters.entrySet()) {
                var net = a.getKey();
                var createData = a.getValue();
                // 设定各路由的路由表：网络中的网关，如果遇到发往外部网络的流量时，将流量转发到 ProbeRouter 上的端口
                if (probeRouteInfo != null) {
                    for (var probeRouteNet : probeRouteInfo.getNetworks()) {
                        if (StringUtils.equals(net.getResourceId(), probeRouteNet.getId())) {
                            String nextHop = probeRouteNet.getFixedIp();
                            String externalNetCidr = CloudSingleton.cloudAppConfig.getExternalNetCidr();
                            if (createData.getRouteStatics() == null) {
                                createData.setRouteStatics(new ArrayList<>());
                            }
                            createData.getRouteStatics()
                                    .add(new VirtualRouterCreateData.StaticRoute(externalNetCidr, nextHop));
                            // 只需要设置一条路由即可
                            break;
                        }
                    }
                }
                var virtualRouter = vrClient.create(createData);
                // 将网络和虚拟路由关联
                net.setAuxiliaryNetworkRouterResourceId(virtualRouter.getId());
            }
        }

        getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_NETWORK);

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

    /**
     * createNodeToExternalRouter
     */
    protected void createNodeToExternalRouter() {
        String sceneId = getPluginHandler().getSrcScene().getEid();
        VirtualRouter vrClient = new VirtualRouter();
        // 创建辅助路由
        VirtualRouterCreateData createData = new VirtualRouterCreateData();
        createData.setName(HandlerUtils.genResourceName(sceneId));
        List<VirtualNic> virtualNics = new ArrayList<>();

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

        boolean externalNetExists = false;
        for (var n : virtualNics) {
            if (StringUtils.equals(CloudSingleton.cloudAppConfig.getExternalNetId(), n.getNetworkId())) {
                externalNetExists = true;
            }
        }
        if (!externalNetExists) {
            //路由需要接外部网络，以及路由上面配置NAT，30s暂时不支持
            VirtualNic externalVirtualNic = new VirtualNic();
            externalVirtualNic.setNetworkId(CloudSingleton.cloudAppConfig.getExternalNetId());
            externalVirtualNic.setCidr(CloudSingleton.cloudAppConfig.getExternalNetCidr());
            // cloud-manger 并没有使用该ip
            externalVirtualNic.setFixedIp(IpUtils.newRandomIps(externalVirtualNic.getCidr(), 1, Collections.emptyList(), null, true)
                                                  .get(0));
            virtualNics.add(externalVirtualNic);
        }

        createData.setNetworks(virtualNics.stream().map(virtualNic -> {
            VirtualRouterNic virtualRouterNic = new VirtualRouterNic();
            virtualRouterNic.setId(virtualNic.getNetworkId());
            virtualRouterNic.setFixedIp(virtualNic.getFixedIp());
            virtualRouterNic.setCidr(virtualNic.getCidr());
            return virtualRouterNic;
        }).collect(Collectors.toList()));

        VirtualRouterCreateData virtualRouter = vrClient.create(createData);
        probeRouteInfo = createData;
        log.info("collected router {}", virtualRouter.getId());
        getPluginHandler().getSceneHandler().setProbeRouter(virtualRouter.getId());
    }

    /**
     * createLogicSwitchProcess
     */
    protected void createLogicSwitchProcess() {
        if (!network2LogicalSwitch.values().isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_LOGICAL_SWITCHES, () -> createLogicalSwitch(new ArrayList<>(network2LogicalSwitch.values())));
        }
    }

    /**
     * createLogicalSwitch
     *
     * @param list
     */
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

    /**
     * createEntityElementProcess
     */
    protected void createEntityElementProcess() {
        if (!entityCreateData.isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_ENTITIES, () -> createEntityElements(new ArrayList<>(entityCreateData)));
        }

        // 判断节点是否存在实体设备节点
        for (CloudPluginSceneElementBO connectedNetworkBO : getPluginHandler().getElementMap().values()){
            if (BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE == connectedNetworkBO.getVirtualType()){
                // 创建策略下发虚拟机
                String strategyIp = createStrategy();

                // 获取场景实例
                String sceneId = getPluginHandler().getSourceSceneId();

                // 获取节点配置信息
                HumanCondition condition = new HumanCondition().filterEqual(SceneElementModel::getScene, sceneId);
                List<SceneElementModel> sceneElementModels = getSceneElementMapper().list(condition);

                // 调用接口下发配置
                for (SceneElementModel sceneElementModel : sceneElementModels){
                    strategySend(strategyIp, sceneElementModel.getConfigCommand());
                }
                break;
            }
        }
    }

    protected void createPortMappingProxy() {
        var cloudAppConfig = BeanCommon.getBean(CloudAppConfig.class);
        String portMapTemplateId = cloudAppConfig.getPortMapTemplateId();
        if (StringUtils.isEmpty(portMapTemplateId)) {
            return;
        }
        // 收集场景里的网络
        List<ConnectedNetworkBO> allNetworks = new ArrayList<>();
        for (CloudPluginSceneElementBO e : getPluginHandler().getElementMap().values()) {
            allNetworks.addAll(e.getDeriveNetworks());
        }
        // 将网络关联到端口映射代理上
        if (!allNetworks.isEmpty()) {
            VirtualMachineCreateData createData = new VirtualMachineCreateData();
            createData.setRequestUser(TextUtils.emptyNull(getHandlerUtils().getRequestUser()));
            createData.setName(HandlerUtils.genResourceName(getPluginHandler().getSrcScene().getEid()));
            createData.setTemplateId(portMapTemplateId);
            createData.setCpu(IntUtils.zeroNull(cloudAppConfig.getPortMapCpu()));
            createData.setRam(IntUtils.zeroNull(cloudAppConfig.getPortMapRam()));
            createData.setDisk(IntUtils.zeroNull(cloudAppConfig.getPortMapDisk()));
            if (StringUtils.isBlank(createData.getDisplayType())) {
                createData.setDisplayType(ElementAccessModeConstant.SPICE_AND_VNC);
            }

            // 3. 和场景中除外部网络外的所有网络相连
            List<VirtualNic> virtualNics = new ArrayList<>();
            for (ConnectedNetworkBO connectedNetwork : allNetworks) {
                if (HandlerUtils.isExternalNetwork(connectedNetwork.getNetwork().getResourceId())) {
                    // 外部网络跳过
                    continue;
                } else {
                    // 将代理虚拟机连接到其他网络上
                    VirtualNic virtualNic = new VirtualNic();
                    virtualNic.setNetworkId(connectedNetwork.getNetwork().getResourceId());
                    virtualNic.setCidr(connectedNetwork.getNetwork().getCidr());
                    virtualNic.setSpecial(false);
                    virtualNics.add(virtualNic);
                }
            }
            // 加上一个外网
            VirtualNic externalNet = new VirtualNic();
            externalNet.setNetworkId(CloudSingleton.cloudAppConfig.getExternalNetId());
            externalNet.setCidr(CloudSingleton.cloudAppConfig.getExternalNetCidr());
            externalNet.setFixedIp(IpUtils.newRandomIps(externalNet.getCidr(), 1, Collections.emptyList(), null, true)
                                           .get(0));
            // 有外网时需要加上main_cloud = true参数
            createData.setMainCloud(true);
            virtualNics.add(externalNet);

            createData.setNetwork(virtualNics);
            //临时加参数
            createData.setOsType(OS_TYPE_LINUX);
            createData.setExtremeSpeed(true);

            VirtualMachineCreateData virtualMachine = vmClient.create(createData);
            getPluginHandler().getScene().setPortMappingInstanceId(virtualMachine.getId());
        }
    }

    private void strategySend(String sourceIp, String configCommand) {

        JSONObject configCommandJSON = JSONObject.parseObject(configCommand);
        Integer protocol = configCommandJSON.getInteger("protocol");
        if (StringUtils.isBlank(sourceIp) || null == protocol){
            return;
        }

        // 请求参数
        String ip = configCommandJSON.getString("ip");
        String port = configCommandJSON.getString("port");
        // 具体命令按照查到的顺序进行插入
        List<ConfigCommand> commands = getConfigCommand(configCommandJSON.getString("commands"));
        if (StringUtils.isBlank(ip) || StringUtils.isBlank(port)
                || org.apache.commons.collections4.CollectionUtils.isEmpty(commands)){
            return;
        }

        // 具体的网段+IP配置信息
        Map<String, String> commandMap = new HashMap<>();
        String nowDestination = null;
        String nowNexthop = null;
        for(ConfigCommand command : commands){
            // 以防存入的destination和nexthop信息顺序变化，所以先将信息存到对象，两个都存入则转移到map中
            if (StringUtils.isBlank(nowDestination) && "destination".equals(command.getKey())){
                nowDestination = command.getKey();
            }
            if (StringUtils.isBlank(nowNexthop) && "nexthop".equals(command.getKey())){
                nowNexthop = command.getKey();
            }
            if (StringUtils.isNotBlank(nowDestination) && StringUtils.isNotBlank(nowNexthop)){
                commandMap.put(nowDestination, nowNexthop);
                nowDestination = null;
                nowNexthop = null;
            }
        }

        // 请求地址
        String urlRoot = "http://" + sourceIp + ":8080/api/v1/";
        String url;

        // 请求数据
        JSONObject message = new JSONObject();
        // 下面俩配置项目中没用上，先随便传一个
        message.put("task_id", "00000");
        message.put("report_url", "https://www.baidu.com");

        // 安全策略下发
        // 0-SNMP、1-NETCONF、2-RESTCONF
        if (0 == protocol){
            url = urlRoot + "snmp/receiveTask";
            JSONObject accessInfoJSON = new JSONObject();
            accessInfoJSON.put("port", port);
            accessInfoJSON.put("host_ip", ip);
            accessInfoJSON.put("version", "v1");
            accessInfoJSON.put("community", "private");
            message.put("access_info", accessInfoJSON);
            JSONObject configJSON = new JSONObject();
            JSONArray commandsArray = new JSONArray();
            JSONObject commandJSON = new JSONObject();
            JSONArray paramsArray = new JSONArray();
            JSONObject paramsJSON = new JSONObject();
            // todo 具体信息等设备厂商给出MIB文件和YANG模型文件，根据commandMap中的信息配置
            commandJSON.put("command", null); // GET ${aaa}
            paramsJSON.put("code", null); // aaa
            paramsJSON.put("value", null); // .1.3.6.1.2.1.1.1.0
            paramsArray.add(paramsJSON);
            commandJSON.put("params", paramsArray);
            commandsArray.add(commandJSON);
            configJSON.put("commands", commandsArray);
            message.put("config", configJSON);
        }else if (1 == protocol){
            String username = configCommandJSON.getString("username");
            String password = configCommandJSON.getString("password");
            if (StringUtils.isBlank(username) || StringUtils.isBlank(password)){
                return;
            }

            url = urlRoot + "netconf/receiveTask";
            JSONObject accessInfoJSON = new JSONObject();
            accessInfoJSON.put("port", port);
            accessInfoJSON.put("host_ip", ip);
            accessInfoJSON.put("username", username);
            accessInfoJSON.put("password", password);
            message.put("access_info", accessInfoJSON);
            JSONObject configJSON = new JSONObject();
            JSONArray commandsArray = new JSONArray();
            JSONObject commandJSON = new JSONObject();
            JSONArray paramsArray = new JSONArray();
            JSONObject paramsJSON = new JSONObject();
            // todo 具体信息等设备厂商给出MIB文件和YANG模型文件，根据commandMap中的信息配置
            commandJSON.put("command", null); // <get xmlns=${aaa}/>
            paramsJSON.put("code", null); // aaa
            paramsJSON.put("value", null); // urn:ietf:params:xml:ns:netconf:base:1.0
            paramsArray.add(paramsJSON);
            commandJSON.put("params", paramsArray);
            commandsArray.add(commandJSON);
            configJSON.put("commands", commandsArray);
            message.put("config", configJSON);
        }else if (2 == protocol){
            String username = configCommandJSON.getString("username");
            String password = configCommandJSON.getString("password");
            if (StringUtils.isBlank(username) || StringUtils.isBlank(password)){
                return;
            }

            url = urlRoot + "restconf/receiveTask";
            JSONObject accessInfoJSON = new JSONObject();
            accessInfoJSON.put("port", port);
            accessInfoJSON.put("host_ip", ip);
            accessInfoJSON.put("username", username);
            accessInfoJSON.put("password", password);
            // todo 具体信息等设备厂商给出MIB文件和YANG模型文件，根据commandMap中的信息配置
            accessInfoJSON.put("request_type", null);
            accessInfoJSON.put("request_method", null);
            accessInfoJSON.put("url_suffix", null);
            message.put("access_info", accessInfoJSON);
            JSONObject configJSON = new JSONObject();
            JSONArray commandsArray = new JSONArray();
            JSONObject commandJSON = new JSONObject();
            JSONArray paramsArray = new JSONArray();
            JSONObject paramsJSON = new JSONObject();
            // todo 具体信息等设备厂商给出MIB文件和YANG模型文件，根据commandMap中的信息配置
            commandJSON.put("command", null); // <get xmlns=${aaa}/>
            paramsJSON.put("code", null); // aaa
            paramsJSON.put("value", null); // urn:ietf:params:xml:ns:netconf:base:1.0
            paramsArray.add(paramsJSON);
            commandJSON.put("params", paramsArray);
            commandsArray.add(commandJSON);
            configJSON.put("commands", commandsArray);
            message.put("config", configJSON);
        }else {
            return;
        }

        boolean result = StrategyHttpUtil.post(url, message.toJSONString());
        if (!result){
            log.error("strategySend error, sourceIp=[{}], configCommand=[{}]", sourceIp, configCommand);
        }
    }

    private List<ConfigCommand> getConfigCommand(String commandsStr) {
        List<ConfigCommand> result = new ArrayList<>();
        if (StringUtils.isBlank(commandsStr)){
            return result;
        }
        JSONArray array = JSONObject.parseArray(commandsStr);
        for (Object o : array) {
            JSONObject commandJSON = JSONObject.parseObject(o.toString());
            List<ConfigCommand> commands = JSONObject.parseArray(commandJSON.getString("params"), ConfigCommand.class);
            result.addAll(commands);
        }
        return result;
    }

    /**
     * createStrategy
     */
    protected String createStrategy() {

        // 获取实体设备网卡信息列表
        List<VirtualNic> virtualNicList = new ArrayList<>();

        getPluginHandler().getElementMap().values().forEach(e -> {
            e.getDeriveNetworks().forEach(n -> {
                VirtualNic nic = new VirtualNic();
                nic.setNetworkId(n.getNetwork().getResourceId());
                nic.setCidr(n.getNetwork().getCidr());
                nic.setFixedIp(IpUtils.newRandomIps(CloudSingleton.cloudAppConfig.getExternalNetCidr(), 1,
                        Collections.emptyList(), Collections.emptyList(), false).get(0));
                virtualNicList.add(nic);
            });
        });

        // 需要接外部网络，以及上面配置NAT，30s暂时不支持
        VirtualNic externalVirtualNic = new VirtualNic();
        externalVirtualNic.setNetworkId(CloudSingleton.cloudAppConfig.getExternalNetId());
        externalVirtualNic.setCidr(CloudSingleton.cloudAppConfig.getExternalNetCidr());
        externalVirtualNic.setFixedIp(IpUtils.newRandomIps(externalVirtualNic.getCidr(), 1, Collections.emptyList(),
                null, false).get(0));
        virtualNicList.add(externalVirtualNic);

        // 创建虚拟机
        return createStrategyVirtualMachine(virtualNicList);
    }

    /**
     * createStrategyVirtualMachine
     */
    private String createStrategyVirtualMachine(List<VirtualNic> virtualNicList) {

        // 准备创建虚机参数
        VirtualMachineCreateData createData = new VirtualMachineCreateData();
        createData.setRequestUser(TextUtils.emptyNull(getHandlerUtils().getRequestUser()));
        createData.setName("StrategyVM-" + getPluginHandler().getScene().getSrcScene().getEid());

        // 根据镜像名称获取templateId
        Template templateClient = new Template();
        String templateId = templateClient.getTemplateIdByDeviceName(
                CloudSingleton.cloudAppConfig.getStrategyImageName());

        //  配置项
        createData.setTemplateId(templateId);
        createData.setCpu(IntUtils.zeroNull(CloudSingleton.cloudAppConfig.getStrategyCpu()));
        createData.setRam(IntUtils.zeroNull(CloudSingleton.cloudAppConfig.getStrategyRam()));
        createData.setDisk(IntUtils.zeroNull(CloudSingleton.cloudAppConfig.getStrategyDisk()));
        createData.setDisplayType(ElementAccessModeConstant.SPICE_AND_VNC);
        createData.setVirtioConsole(true);
        createData.setExtremeSpeed(true);
        createData.setMainCloud(false);
        createData.setOsType(SystemTypeCommandUtils.OS_TYPE_LINUX);
        createData.setCustomScript(null);
        // 虚机的网卡信息，都设置为非special口，设置上ip信息。
        createData.setNetwork(CollectionUtils.emptyNull(virtualNicList));

        // 创建虚拟机
        VirtualMachineCreateData virtualMachine = vmClient.create(createData);
        // 读取虚机ip地址
        VirtualNic virtualNic = virtualMachine.getNetwork().get(0);
        String ip = virtualNic.getFixedIp();

        // 保存虚拟机信息
        CloudPluginSceneBO scene = getPluginHandler().getScene();
        scene.setStrategyIp(ip);
        scene.setStrategyInstance(virtualMachine.getId());
        getPluginHandler().saveScene();

        return scene.getStrategyIp();
    }

    /**
     * createEntityElements
     *
     * @param list
     */
    public void createEntityElements(List<EntityNodeCreateData> list) {
        list.forEach(enClient::create);
    }

    /**
     * prepareEventProcess
     */
    protected void prepareEventProcess() {
        prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);
    }

    /**
     * createNpcElements
     *
     * @param list
     */
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

    /**
     * createAuxiliaryNetworkProcess
     *
     * @param cloudElement
     */
    public void createAuxiliaryNetworkProcess(CloudPluginSceneElementBO cloudElement) {
        runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.CREATE_AUXILIARY_NETWORK, () -> createAuxiliaryNetwork(cloudElement));
    }

    /**
     * createElementProcess
     *
     * @param cloudElement
     */
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

    /**
     * waitSeconds
     *
     * @param millSeconds
     */
    private void waitSeconds(long millSeconds) {
        try {
            Thread.sleep(millSeconds);
        } catch (InterruptedException e) {
        }
    }

    /**
     * trySetPublicNetProxy
     *
     * @param accessMode
     */
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

    /**
     * attachISO
     *
     * @param cloudElement
     */
    private void attachISO(CloudPluginSceneElementBO cloudElement) {
        VirtualMachineAttachISOData data = new VirtualMachineAttachISOData();
        data.setInstanceUuid(cloudElement.getResourceId());
        data.setFileName(cloudElement.getIsoAttachFilename());
        data.setTmpdirUuid(cloudElement.getIsoTmpdirUuid());
        vmClient.attachISO(data);
    }

    /**
     * attachVirtualUsbs
     *
     * @param cloudElement
     */
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

    /**
     * attachEntityUsbs
     *
     * @param cloudElement
     */
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

    private void checkAndAddAccessModes(CloudPluginSceneElementBO cloudElement) {
        List<CloudPluginSceneElementPortBO> cloudPorts = getPluginHandler().getPorts(cloudElement);
        if (cloudPorts == null || cloudPorts.size() == 0) {
            return;
        }
        Set<String> supportProtocols = new HashSet<>();
        supportProtocols.add(CloudElementAccessMode.Protocol.VNC);
        supportProtocols.add(CloudElementAccessMode.Protocol.SPICE);
        supportProtocols.add(CloudElementAccessMode.Protocol.CONSOLE);

        boolean existsSupportProtocols = false;
        for (CloudPluginSceneElementPortBO port : cloudPorts) {
            if (port.getAccessModes() != null) {
                for (CloudAccessMode accessMode : port.getAccessModes()) {
                    String protocol = accessMode.getProtocol();
                    if (supportProtocols.contains(protocol)) {
                        existsSupportProtocols = true;
                    }
                }
            }
        }
        // 不存在 VNC/SPICE ，则给虚拟机自动加上 VNC和SPICE  ， 给容器自动加上console
        if (!existsSupportProtocols) {
            CloudPluginSceneElementPortBO cloudPort = cloudPorts.get(0);
            if (cloudPort.getAccessModes() == null) {
                cloudPort.setAccessModes(new ArrayList<>());
            }
            List<CloudAccessMode> cloudAccessModes = cloudPort.getAccessModes();
            String ip = cloudPort.getSrcPort().getIp();
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
        }

        // 给所有接入协议先设置联通 使页面显示正常
        for (CloudPluginSceneElementPortBO port : cloudPorts) {
            if (port.getAccessModes() != null) {
                for (CloudAccessMode accessMode : port.getAccessModes()) {
                    accessMode.setAvailable(true);
                }
            }
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ConfigCommand {
        String key;
        String value;
    }
}
