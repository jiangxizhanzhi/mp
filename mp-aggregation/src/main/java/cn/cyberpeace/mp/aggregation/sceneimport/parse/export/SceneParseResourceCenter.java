package cn.cyberpeace.mp.aggregation.sceneimport.parse.export;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.AclsInfo;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.InitCommandParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NetElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NodeElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.SceneElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.StaticRoutesResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2.NetworkElementParseResultV2;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2.YmlParseResultV2;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
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
public class SceneParseResourceCenter {

    public Result parseAndLoadInfo(String sceneId) {
        SceneConfigHandler sceneHandler = new SceneConfigHandler(sceneId, null, false, null);
        YmlParseResultV2 ymlParseResult = new YmlParseResultV2();
        ymlParseResult.setScene(this.getSceneElementParseResult(sceneHandler));
        ymlParseResult.setNodes(new ArrayList<>());
        ymlParseResult.setNetworks(new ArrayList<>());

        // 解析节点
        Map<String, NetworkElementParseResultV2> id2Network = new HashMap<>();
        Map<String, NodeElementParseResult> id2Node = new HashMap<>();
        this.parseNodeAndNet(sceneHandler, ymlParseResult.getNetworks(), ymlParseResult.getNodes(), id2Network, id2Node);

        // 处理连线
        this.addConnect(sceneHandler, id2Network, id2Node);

        // 处理节点附件
        Map<String, String> file2Download = new HashMap<>();
        this.handleNodeFile(ymlParseResult.getNodes(), file2Download);

        // 处理节点镜像
        List<Device> devices = new ArrayList<>();
        this.handleNodeImage(sceneHandler, id2Node, devices);

        Result result = new Result();
        result.setYmlResult(ymlParseResult);
        result.setDevices(devices);
        result.setFile2Download(file2Download);
        return result;
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
                id2Network.put(e.getId(), networkElementParseResult);
                networks.add(networkElementParseResult);
                if (pluginElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK) {
                    networkElementParseResult.setId("internet-" + networkIndex);
                } else {
                    networkElementParseResult.setId("network-" + networkIndex);
                }
            } else {
                var nodeElementParseResult = this.buildNodeElementParseResult(e, pluginElement);
                if (role == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER) {
                    nodeElementParseResult.setId("router-" + routerIndex++);
                } else if (role == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL) {
                    nodeElementParseResult.setId("firewall-" + firewallIndex++);
                } else {
                    nodeElementParseResult.setId("server-" + serverIndex++);
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
                    if (connectElement != null && id2Network.containsKey(connectElement.getId())) {
                        var net = id2Network.get(connectElement.getId());
                        NetElementParseResult netElementParseResult = new NetElementParseResult();
                        netElementParseResult.setId(net.getId());
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
        networkElementParseResult.setName(e.getName());
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
    }

    @Setter
    @Getter
    public static class Device {
        private String image;
        private String logo;
        private List<AccessMode> accessModes;
    }


}
