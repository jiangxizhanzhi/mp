package cn.cyberpeace.mp.aggregation.sceneimport.parse.export;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.AclsInfo;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.InitCommandParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NetElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NodeElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.SceneElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.StaticRoutesResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2.NetworkElementParseResultV2;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2.YmlParseResultV2;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.core.event.base.ProcessStatus;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.FirewallRuleAction;
import cn.cyberpeace.mp.sceneplugincloud.constant.FirewallRuleDirection;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ConnectedNetworkBO;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author maojun
 * @Date 2022/8/20
 */
public class SceneParseResourceCenterV1 extends BaseSceneParseResourceCenter {

    public Result parseAndLoadInfo(String sceneId) {
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneId, null, false, null);
        SceneHandler sceneHandler = new InitiateSceneHandler(sceneConfigHandler.toData().exportToScene());
        sceneHandler.initiate(false);
        YmlParseResultV2 ymlParseResult = new YmlParseResultV2();
        ymlParseResult.setScene(this.getSceneElementParseResult(sceneConfigHandler));
        ymlParseResult.setNodes(new ArrayList<>());
        ymlParseResult.setNetworks(new ArrayList<>());

        // 解析节点
        Map<String, NetworkElementParseResultV2> id2Network = new HashMap<>();
        Map<String, NodeElementParseResult> id2Node = new HashMap<>();
        this.parseNodeAndNet(sceneHandler, ymlParseResult.getNetworks(), ymlParseResult.getNodes(), id2Network, id2Node);
        // 建立nodeId和转换成yml之后id的映射关系
        Map<String, String> idConvert = new HashMap<>();
        for (String nodeId : id2Network.keySet()) {
            idConvert.put(nodeId, id2Network.get(nodeId).getId());
        }
        for (String nodeId : id2Node.keySet()) {
            idConvert.put(nodeId, id2Node.get(nodeId).getId());
        }

        // 处理连线
        this.addConnect(sceneHandler, id2Network, id2Node);

        // 处理节点附件
        Map<String, String> file2Download = new HashMap<>();
        this.handleNodeFile(ymlParseResult.getNodes(), file2Download);

        // 处理节点镜像
        List<SceneParseResourceCenterV2.Device> devices = new ArrayList<>();
        this.handleNodeImage(sceneConfigHandler, id2Node, devices);

        Result result = new Result();
        result.setYmlResult(ymlParseResult);
        result.setDevices(devices);
        result.setFile2Download(file2Download);
        result.setIdConvert(idConvert);
        return result;
    }

    private SceneElementParseResult getSceneElementParseResult(SceneConfigHandler sceneHandler) {
        SceneElementParseResult sceneElementParseResult = new SceneElementParseResult();
        sceneElementParseResult.setName(sceneHandler.getScene().getName());
        sceneElementParseResult.setDesc(sceneHandler.getScene().getDescription());
        return sceneElementParseResult;
    }

    private void parseNodeAndNet(SceneHandler sceneHandler, List<NetworkElementParseResultV2> networks, List<NodeElementParseResult> nodes,
                                 Map<String, NetworkElementParseResultV2> id2Network, Map<String, NodeElementParseResult> id2Node) {
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        Map<ConnectedNetworkBO, CloudPluginSceneElementBO> deriveNetwork2Element = new HashMap<>();
        pluginHandler.getElementMap().values().forEach(e -> {
            if (CollectionUtils.isNotEmpty(e.getDeriveNetworks())) {
                for (var net : e.getDeriveNetworks()) {
                    deriveNetwork2Element.put(net, e);
                }
            }
        });
        int networkIndex = 1;
        for (var net : deriveNetwork2Element.keySet()) {
            var pluginElement = deriveNetwork2Element.get(net);
            var element = pluginElement.getSrcElement();
            var networkElementParseResult = this.buildNetworkElementParseResultV2(element, pluginElement);
            if (StringUtils.isNotEmpty(net.getNetwork().getGateway())) {
                networkElementParseResult.setGateway(net.getNetwork().getGateway());
            }
            if (net.getNetwork().getDhcp() != null) {
                networkElementParseResult.setDhcp(net.getNetwork().getDhcp().toString());
            }
            if (StringUtils.isNotEmpty(net.getNetwork().getCidr())) {
                networkElementParseResult.setCidr(net.getNetwork().getCidr());
            }
            networks.add(networkElementParseResult);
            int index = networkIndex++;
            if (pluginElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK) {
                networkElementParseResult.setId("internet-" + index);
            } else {
                networkElementParseResult.setId("network-" + index);
            }
            networkElementParseResult.setName("网络" + index);
            id2Network.put(net.getNetwork().getId(), networkElementParseResult);
        }
        int serverIndex = 1;
        int routerIndex = 1;
        int firewallIndex = 1;
        for (var pluginElement : pluginHandler.getElementMap().values()) {
            var element = pluginElement.getSrcElement();
            if (BaseSceneElementModel.Type.SWITCH == element.getType()) {
                if (pluginElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH) {
                    // 二层交换机不用处理
                    continue;
                } else if (pluginElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER) {
                    // 三层当二层处理
                    continue;
                } else if (pluginElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE
                        || pluginElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER
                        || pluginElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                    var nodeElementParseResult = this.buildNodeElementParseResult(element, pluginElement);
                    nodeElementParseResult.setId("server-" + serverIndex++);
                    id2Node.put(element.getId(), nodeElementParseResult);
                    nodes.add(nodeElementParseResult);
                }
            } else {
                var nodeElementParseResult = this.buildNodeElementParseResult(element, pluginElement);
                if (element.getType() == BaseSceneElementModel.Type.ROUTER) {
                    nodeElementParseResult.setId("router-" + routerIndex++);
                } else if (element.getType() == BaseSceneElementModel.Type.FIREWALL) {
                    nodeElementParseResult.setId("firewall-" + firewallIndex++);
                } else {
                    nodeElementParseResult.setId("server-" + serverIndex++);
                    if (pluginElement.getRole() == BaseCloudPluginSceneElementModel.Role.NPC) {
                        nodeElementParseResult.setVirtualPlatform("npc");
                    }
                }
                id2Node.put(element.getId(), nodeElementParseResult);
                nodes.add(nodeElementParseResult);
            }
        }
    }

    private void addConnect(SceneHandler sceneHandler, Map<String, NetworkElementParseResultV2> id2Network, Map<String, NodeElementParseResult> id2Node) {
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        for (String nodeId : id2Node.keySet()) {
            var node = id2Node.get(nodeId);
            node.setNet(new ArrayList<>());

            var element = sceneHandler.getElement(nodeId);
            var pluginElement = pluginHandler.getElement(nodeId);
            if (CollectionUtils.isNotEmpty(pluginElement.getDeriveNetworks())) {
                for (var net : pluginElement.getDeriveNetworks()) {
                    var netParseResult = id2Network.get(net.getNetwork().getId());
                    NetElementParseResult netElementParseResult = new NetElementParseResult();
                    netElementParseResult.setId(netParseResult.getId());
                    var port = net.getFromPorts().get(0);
                    if (pluginHandler.getHandlerUtils().isManualConfigIpPort(port)) {
                        netElementParseResult.setIp(port.getIp());
                    }
                    var pluginPort = pluginHandler.getPort(port.getId());
                    netElementParseResult.setAcls(this.buildAclsInfos(pluginPort));
                    node.getNet().add(netElementParseResult);
                }
            }
            if (CollectionUtils.isNotEmpty(pluginElement.getAccessNetworks())) {
                for (var net : pluginElement.getAccessNetworks()) {
                    var netParseResult = id2Network.get(net.getNetwork().getId());
                    NetElementParseResult netElementParseResult = new NetElementParseResult();
                    netElementParseResult.setId(netParseResult.getId());
                    netElementParseResult.setAcls(new ArrayList<>());
                    SceneElementPortBO port = net.getFromPorts().get(0);
                    if (pluginHandler.getHandlerUtils().isManualConfigIpPort(port)) {
                        netElementParseResult.setIp(port.getIp());
                    }
                    var pluginPort = pluginHandler.getPort(port.getId());
                    netElementParseResult.setAcls(this.buildAclsInfos(pluginPort));
                    node.getNet().add(netElementParseResult);
                }
            }
        }
    }

    private NetworkElementParseResultV2 buildNetworkElementParseResultV2(SceneElementBO e, CloudPluginSceneElementBO pluginElement) {
        NetworkElementParseResultV2 networkElementParseResult = new NetworkElementParseResultV2();
        if (BaseSceneElementModel.Type.SWITCH == e.getType()) {
            networkElementParseResult.setDhcp(Boolean.TRUE.equals(pluginElement.getDhcp()) ? "on" : "off");
            networkElementParseResult.setCidr(pluginElement.getCidr());
            networkElementParseResult.setGateway(pluginElement.getGateway());
            networkElementParseResult.setDns(pluginElement.getDnsNameservers());
            if (CollectionUtils.isNotEmpty(pluginElement.getAllocationPools())) {
                var list = pluginElement.getAllocationPools().stream()
                        .map(p -> p.getStart() + "-" + p.getEnd())
                        .collect(Collectors.toList());
                networkElementParseResult.setDhcpPool(StringUtils.join(list, ","));
            } else {
                networkElementParseResult.setDhcpPool(StringUtils.EMPTY);
            }
        }

        try {
            if (StringUtils.isNotEmpty(e.getPosition2())) {
                Origin position = JsonCommon.parse(e.getPosition2(), Origin.class);
                if (position != null) {
                    networkElementParseResult.setPosition(position.getX() + "," + position.getY());
                    Integer role = pluginElement.getRole();
                    if (role != null) {
                        if (role != BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH) {
                            networkElementParseResult.setPosition((position.getX() + 80) + "," + (position.getY() + 80));
                        }
                    }
                }
            }
        } catch (Exception exception) {
            // do nothing
        }

        return networkElementParseResult;
    }

    private NodeElementParseResult buildNodeElementParseResult(SceneElementBO e, CloudPluginSceneElementBO pluginElement) {
        NodeElementParseResult nodeElementParseResult = new NodeElementParseResult();
        nodeElementParseResult.setFile(e.getFile());
        nodeElementParseResult.setImage(pluginElement.getImage());
        nodeElementParseResult.setName(e.getName());
        nodeElementParseResult.setInitCommands(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(e.getInitCommands())) {
            e.getInitCommands().forEach(c -> {
                InitCommandParseResult result = new InitCommandParseResult();
                if (c.startsWith("o:")) {
                    result.setO(StringUtils.replaceOnce(c, "o:", "").trim());
                } else if (c.startsWith("s:")) {
                    result.setS(StringUtils.replaceOnce(c, "s:", "").trim());
                } else if (c.startsWith("n:")) {
                    result.setN(StringUtils.replaceOnce(c, "n:", "").trim());
                }
                nodeElementParseResult.getInitCommands().add(result);
            });
        }
        if (StringUtils.isNotEmpty(e.getNodeCommand())) {
            InitCommandParseResult result = new InitCommandParseResult();
            result.setN(e.getNodeCommand());
            nodeElementParseResult.getInitCommands().add(result);
        }
        nodeElementParseResult.setStaticRoutes(new ArrayList<>());
        nodeElementParseResult.setStaticRouters(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(pluginElement.getStaticRoutes())) {
            pluginElement.getStaticRoutes().forEach(s -> {
                StaticRoutesResult result = new StaticRoutesResult();
                result.setGateway(s.getNexthop());
                result.setDestination(s.getDestination());
                nodeElementParseResult.getStaticRoutes().add(result);
                nodeElementParseResult.getStaticRouters().add(result);
            });
        }
        try {
            if (StringUtils.isNotEmpty(e.getPosition2())) {
                Origin position = JsonCommon.parse(e.getPosition2(), Origin.class);
                if (position != null) {
                    nodeElementParseResult.setPosition(position.getX() + "," + position.getY());
                }
            }
        } catch (Exception exception) {
            // do nothing
        }
        return nodeElementParseResult;
    }

    private List<AclsInfo> buildAclsInfos(CloudPluginSceneElementPortBO pluginPort) {
        List<AclsInfo> aclsInfos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(pluginPort.getFirewallRules())) {
            for (var r : pluginPort.getFirewallRules()) {
                AclsInfo aclsInfo = new AclsInfo();
                aclsInfo.setProtocol(r.getProtocol());
                aclsInfo.setTargetPort(r.getDestinationPort());
                aclsInfo.setTargetIP(r.getDestinationIpAddress());
                aclsInfo.setSourcePort(r.getSourcePort());
                aclsInfo.setSourceIP(r.getSourceIpAddress());
                aclsInfo.setAction(r.getAction());
                if (FirewallRuleDirection.EGRESS.equals(r.getDirection())) {
                    aclsInfo.setDirection("up");
                } else if (FirewallRuleDirection.INGRESS.equals(r.getDirection())) {
                    aclsInfo.setDirection("down");
                } else if (FirewallRuleDirection.BOTH.equals(r.getDirection())) {
                    aclsInfo.setDirection("two-way");
                }
                if (FirewallRuleAction.DENY.equals(r.getAction())) {
                    aclsInfo.setAction("deny");
                } else if (FirewallRuleAction.ALLOW.equals(r.getAction())) {
                    aclsInfo.setAction("allow");
                } else if (FirewallRuleAction.REJECT.equals(r.getAction())) {
                    aclsInfo.setAction("reject");
                }
                aclsInfos.add(aclsInfo);
            }
        }
        return aclsInfos;
    }

    @Getter
    @Setter
    public static class Result {
        private YmlParseResultV2 ymlResult;

        private Map<String, String> file2Download = new HashMap<>();

        private List<SceneParseResourceCenterV2.Device> devices = new ArrayList<>();

        private Map<String, String> idConvert = new HashMap<>();
    }


    private static class InitiateSceneHandler extends SceneHandler {

        private String sceneId;

        public InitiateSceneHandler(SceneDataBO sceneDataBO) {
            super(sceneDataBO, null);
            this.sceneId = TextUtils.uuid();
            getScene().setEid(TextUtils.uuid());
            getScene().setEvent(SceneModel.Event.DEFAULT);
            getScene().setEventStatus(EventStatus.INITIAL);
            getScene().setProcessStatus(ProcessStatus.INITIAL);
            getElementMap().values().forEach(e -> {
                e.setEvent(SceneElementModel.Event.DEFAULT);
                e.setEventStatus(SceneElementModel.Event.DEFAULT);
                e.setProcessStatus(SceneElementModel.Event.DEFAULT);
            });
        }

        @Override
        public String getSceneId() {
            return sceneId;
        }


        @Override
        public void asyncAckElementChanged(String nodeId, boolean forceSave, boolean withPlugins) {
            // do nothing
        }

        @Override
        public void asyncAckSceneChanged(boolean forceSave, boolean withPlugins) {
            // do nothing
        }
    }

    @Getter
    @Setter
    private static class Origin {
        private double x;
        private double y;
    }
}
