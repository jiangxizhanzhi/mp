package cn.cyberpeace.mp.aggregation.sceneimport.parse.export;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.AclsInfo;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.InitCommandParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NetElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NodeElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.SceneElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.SceneElementShape;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.StaticRoutesResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2.NetworkElementParseResultV2;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2.YmlParseResultV2;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigAreaBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.FirewallRuleAction;
import cn.cyberpeace.mp.sceneplugincloud.constant.FirewallRuleDirection;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudSceneConfigPluginHandler;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author maojun
 * @Date 2022/8/20
 */
public class SceneParseResourceCenter {

    public Result parseAndLoadInfo(String sceneId) {
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneId, null, false, null);
        YmlParseResultV2 ymlParseResult = new YmlParseResultV2();
        ymlParseResult.setScene(this.getSceneElementParseResult(sceneConfigHandler));
        ymlParseResult.setNodes(new ArrayList<>());
        ymlParseResult.setNetworks(new ArrayList<>());
        ymlParseResult.setShape(new ArrayList<>());

        // 解析节点
        Map<String, NetworkElementParseResultV2> id2Network = new HashMap<>();
        Map<String, NodeElementParseResult> id2Node = new HashMap<>();
        this.parseNodeAndNet(sceneConfigHandler, ymlParseResult.getNetworks(), ymlParseResult.getNodes(), id2Network, id2Node);
        // 建立nodeId和转换成yml之后id的映射关系
        Map<String, String> idConvert = new HashMap<>();
        for (String nodeId : id2Network.keySet()) {
            idConvert.put(nodeId, id2Network.get(nodeId).getId());
        }
        for (String nodeId : id2Node.keySet()) {
            idConvert.put(nodeId, id2Node.get(nodeId).getId());
        }

        // 处理连线
        this.addConnect(sceneConfigHandler, id2Network, id2Node);

        // 处理区域信息
        this.handlerShapes(sceneConfigHandler, ymlParseResult.getShape(), idConvert);

        // 处理节点附件
        Map<String, String> file2Download = new HashMap<>();
        this.handleNodeFile(ymlParseResult.getNodes(), file2Download);

        // 处理节点镜像
        List<Device> devices = new ArrayList<>();
        this.handleNodeImage(sceneConfigHandler, id2Node, devices);

        Result result = new Result();
        result.setYmlResult(ymlParseResult);
        result.setDevices(devices);
        result.setFile2Download(file2Download);
        result.setIdConvert(idConvert);
        return result;
    }


    private void handlerShapes(SceneConfigHandler sceneHandler, List<SceneElementShape> shapes, Map<String, String> idConvert) {
        if (sceneHandler.getAreaMap() == null || sceneHandler.getAreaMap().isEmpty()) {
            return;
        }
        Map<String, SceneElementShape> id2Shape = new HashMap<>();
        for (SceneConfigAreaBO areaBO : sceneHandler.getAreaMap().values()) {
            SceneElementShape shape = new SceneElementShape();
            shape.setId(areaBO.getId());
            shape.setName(areaBO.getName());
            shape.setNodeIds(new ArrayList<>());
            Position position = JsonCommon.parse(areaBO.getPosition2(), Position.class);
            shape.setLength(this.formatDouble(position.getHeight()));
            shape.setWidth(this.formatDouble(position.getWidth()));
            double x = this.formatDouble(position.getOrigin().getX());
            double y = this.formatDouble(position.getOrigin().getY());
            shape.setPosition(x + "," + y);
            id2Shape.put(shape.getId(), shape);
        }
        for (SceneConfigElementBO e : sceneHandler.getElementMap().values()) {
            if (StringUtils.isNotEmpty(e.getArea()) && id2Shape.containsKey(e.getArea())) {
                String nodeId = idConvert.getOrDefault(e.getId(), e.getId());
                id2Shape.get(e.getArea()).getNodeIds().add(nodeId);
            }
        }
        shapes.addAll(id2Shape.values());
    }

    private double formatDouble(double number) {
        return BigDecimal.valueOf(number).setScale(6, RoundingMode.HALF_DOWN).doubleValue();
    }

    private SceneElementParseResult getSceneElementParseResult(SceneConfigHandler sceneHandler) {
        SceneElementParseResult sceneElementParseResult = new SceneElementParseResult();
        sceneElementParseResult.setName(sceneHandler.getScene().getName());
        sceneElementParseResult.setDesc(sceneHandler.getScene().getDescription());
        return sceneElementParseResult;
    }

    private void parseNodeAndNet(SceneConfigHandler sceneHandler, List<NetworkElementParseResultV2> networks, List<NodeElementParseResult> nodes,
                                 Map<String, NetworkElementParseResultV2> id2Network, Map<String, NodeElementParseResult> id2Node) {
        CloudSceneConfigPluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        int networkIndex = 1;
        int serverIndex = 1;
        int routerIndex = 1;
        int firewallIndex = 1;
        for (var e : sceneHandler.getElementMap().values()) {
            var pluginElement = pluginHandler.getElement(e.getId());
            var role = pluginElement.getRole();
            if (role == BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK ||
                    role == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY ||
                    role == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH) {
                var networkElementParseResult = this.buildNetworkElementParseResultV2(e, pluginElement);

                try {
                    if (StringUtils.isNotEmpty(e.getPosition2())) {
                        Origin position = JsonCommon.parse(e.getPosition2(), Origin.class);
                        if (position != null) {
                            networkElementParseResult.setPosition(position.getX() + "," + position.getY());
                        }
                    }
                } catch (Exception exception) {
                    // do nothing
                }

                id2Network.put(e.getId(), networkElementParseResult);
                networks.add(networkElementParseResult);
                if (pluginElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK) {
                    networkElementParseResult.setId("internet-" + networkIndex);
                } else {
                    networkElementParseResult.setId("network-" + networkIndex);
                }
            } else {
                var nodeElementParseResult = this.buildNodeElementParseResult(e, pluginElement);

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

                if (role == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER) {
                    nodeElementParseResult.setId("router-" + routerIndex++);
                } else if (role == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL) {
                    nodeElementParseResult.setId("firewall-" + firewallIndex++);
                } else {
                    nodeElementParseResult.setId("server-" + serverIndex++);
                    if (pluginElement.getRole() == BaseCloudPluginSceneElementModel.Role.NPC) {
                        nodeElementParseResult.setVirtualPlatform("npc");
                    }
                }
                id2Node.put(e.getId(), nodeElementParseResult);
                nodes.add(nodeElementParseResult);
            }
        }
    }

    private void addConnect(SceneConfigHandler sceneHandler, Map<String, NetworkElementParseResultV2> id2Network, Map<String, NodeElementParseResult> id2Node) {
        CloudSceneConfigPluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        for (String nodeId : id2Node.keySet()) {
            var e = sceneHandler.getElement(nodeId);
            var node = id2Node.get(nodeId);
            node.setNet(new ArrayList<>());
            if (CollectionUtils.isNotEmpty(e.getPorts())) {
                e.getPorts().forEach(p -> {
                    var connectElement = sceneHandler.getPortConnectedElement(p.getId());
                    if (connectElement != null) {
                        NetElementParseResult netElementParseResult = new NetElementParseResult();
                        if (id2Network.containsKey(connectElement.getId())) {
                            netElementParseResult.setId(id2Network.get(connectElement.getId()).getId());
                        } else if (id2Node.containsKey(connectElement.getId())) {
                            netElementParseResult.setId(id2Node.get(connectElement.getId()).getId());
                        }
                        netElementParseResult.setIp(p.getIp());
                        netElementParseResult.setAcls(new ArrayList<>());
                        if (node.getNet() == null) {
                            node.setNet(new ArrayList<>());
                        }
                        node.getNet().add(netElementParseResult);
                        var pluginPort = pluginHandler.getPort(p.getId());
                        if (CollectionUtils.isNotEmpty(pluginPort.getFirewallRules())) {
                            pluginPort.getFirewallRules().forEach(r -> {
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
                            });
                        }
                    }
                });
            }
        }
    }

    private void handleNodeFile(List<NodeElementParseResult> nodes, Map<String, String> file2Download) {
        Map<String, Integer> fileName2Index = new HashMap<>();
        fileName2Index.put("file", 1);
        for (var node : nodes) {
            if (StringUtils.isNotEmpty(node.getFile())) {
                String file = node.getFile();
                // 获取name
                int fileNameIndex = node.getFile().lastIndexOf("=");
                if (fileNameIndex > -1) {
                    node.setFile(StringUtils.substring(file, fileNameIndex, file.length()));
                    node.setFile(StringUtils.replace(node.getFile(), "=", ""));
                } else {
                    node.setFile("file");
                }
                if (fileName2Index.containsKey(node.getFile())) {
                    int index = fileName2Index.get(node.getFile());
                    node.setFile(node.getFile() + index);
                    fileName2Index.put(node.getFile(), index + 1);
                } else {
                    fileName2Index.put(node.getFile(), 1);
                }
                file2Download.put(node.getFile(), file);
            }
        }
    }

    private void handleNodeImage(SceneConfigHandler sceneHandler, Map<String, NodeElementParseResult> id2Node, List<Device> devices) {
        CloudSceneConfigPluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        Set<String> existImage = new HashSet<>();
        for (String nodeId : id2Node.keySet()) {
            var node = pluginHandler.getElement(nodeId);
            if (node != null) {
                if (!BaseCloudPluginSceneElementModel.ImageType.VM.equals(node.getImageType())
                        && !BaseCloudPluginSceneElementModel.ImageType.CONTAINER.equals(node.getImageType())) {
                    continue;
                }
                if (StringUtils.isEmpty(node.getImage())) {
                    continue;
                }
                if (!existImage.add(node.getImage())) {
                    continue;
                }
                Device device = new Device();
                device.setImage(node.getImage());
                device.setAccessModes(new ArrayList<>());
                devices.add(device);
                var element = sceneHandler.getElement(nodeId);
                device.setLogo(element.getIcon2());
                if (CollectionUtils.isNotEmpty(element.getPorts())) {
                    for (var p : element.getPorts()) {
                        if (CollectionUtils.isNotEmpty(p.getAccessModes())) {
                            device.getAccessModes().addAll(p.getAccessModes());
                        }
                    }
                }
            }
        }
    }

    private NetworkElementParseResultV2 buildNetworkElementParseResultV2(SceneConfigElementBO e, CloudPluginSceneConfigElementBO pluginElement) {
        NetworkElementParseResultV2 networkElementParseResult = new NetworkElementParseResultV2();
        networkElementParseResult.setName(e.getName());
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
        return networkElementParseResult;
    }

    private NodeElementParseResult buildNodeElementParseResult(SceneConfigElementBO e, CloudPluginSceneConfigElementBO pluginElement) {
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
        return nodeElementParseResult;
    }

    @Getter
    @Setter
    public static class Result {
        private YmlParseResultV2 ymlResult;

        private Map<String, String> file2Download = new HashMap<>();

        private List<Device> devices = new ArrayList<>();

        private Map<String, String> idConvert = new HashMap<>();
    }

    @Setter
    @Getter
    public static class Device {
        private String image;
        private String logo;
        private List<AccessMode> accessModes;
    }

    @Getter
    @Setter
    private static class Position {
        private Origin origin;
        private double height;
        private double width;
    }

    @Getter
    @Setter
    private static class Origin {
        private double x;
        private double y;
    }
}
