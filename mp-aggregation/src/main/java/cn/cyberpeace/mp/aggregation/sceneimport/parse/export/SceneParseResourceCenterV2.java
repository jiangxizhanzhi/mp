package cn.cyberpeace.mp.aggregation.sceneimport.parse.export;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.AclsInfo;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.InitCommandParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NetElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NodeElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.ExtNodeInfo;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.SceneElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.SceneElementShape;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.StaticRoutesResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2.NetworkElementParseResultV2;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2.YmlParseResultV2;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigAreaBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.FirewallRuleAction;
import cn.cyberpeace.mp.sceneplugincloud.constant.FirewallRuleDirection;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudSceneConfigPluginHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class SceneParseResourceCenterV2 extends BaseSceneParseResourceCenter {

    public Result parseAndLoadInfo(String sceneId) {
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneId, null, false, null);
        YmlParseResultV2 ymlParseResult = new YmlParseResultV2();
        ymlParseResult.setScene(this.getSceneElementParseResult(sceneConfigHandler));
        ymlParseResult.setNodes(new ArrayList<>());
        ymlParseResult.setNetworks(new ArrayList<>());
        ymlParseResult.setShape(new ArrayList<>());

        
        Map<String, NetworkElementParseResultV2> id2Network = new HashMap<>();
        Map<String, NodeElementParseResult> id2Node = new HashMap<>();
        this.parseNodeAndNet(sceneConfigHandler, ymlParseResult.getNetworks(), ymlParseResult.getNodes(), id2Network, id2Node);
        
        Map<String, String> idConvert = new HashMap<>();
        for (String nodeId : id2Node.keySet()) {
            idConvert.put(nodeId, id2Node.get(nodeId).getId());
        }
        for (String nodeId : id2Network.keySet()) {
            idConvert.put(nodeId, id2Network.get(nodeId).getId());
        }

        
        this.addConnect(sceneConfigHandler, id2Network, id2Node);

        
        this.handlerShapes(sceneConfigHandler, ymlParseResult.getShape(), idConvert);

        
        Map<String, String> file2Download = new HashMap<>();
        this.handleNodeFile(ymlParseResult.getNodes(), file2Download);

        
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
            shape.setDisplayContext(areaBO.getDisplayContext());
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
        if (StringUtils.isNotEmpty(sceneHandler.getScene().getDescription())) {
            sceneElementParseResult.setDesc(sceneHandler.getScene().getDescription());
        } else {
            sceneElementParseResult.setDesc(SPECIAL_STR_EMPTY);
        }
        sceneElementParseResult.setDisplayContext(sceneHandler.getScene().getDisplayContext());
        return sceneElementParseResult;
    }

    private void parseNodeAndNet(SceneConfigHandler sceneHandler, List<NetworkElementParseResultV2> networks, List<NodeElementParseResult> nodes, Map<String, NetworkElementParseResultV2> id2Network, Map<String, NodeElementParseResult> id2Node) {
        CloudSceneConfigPluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        int networkIndex = 1;
        int serverIndex = 1;
        int routerIndex = 1;
        int firewallIndex = 1;
        for (var e : sceneHandler.getElementMap().values()) {
            var pluginElement = pluginHandler.getElement(e.getId());
            var role = pluginElement.getRole();
            if (role == BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK || role == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY) {
                var networkElementParseResult = this.buildNetworkElementParseResultV2(e, pluginElement);

                try {
                    if (StringUtils.isNotEmpty(e.getPosition2())) {
                        Origin position = JsonCommon.parse(e.getPosition2(), Origin.class);
                        if (position != null) {
                            networkElementParseResult.setPosition(position.getX() + "," + position.getY());
                        }
                    }
                } catch (Exception exception) {
                    
                }

                id2Network.put(e.getId(), networkElementParseResult);
                networks.add(networkElementParseResult);
                if (pluginElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK) {
                    networkElementParseResult.setId("internet-" + networkIndex++);
                } else {
                    networkElementParseResult.setId("network-" + networkIndex++);
                }
            } else if (role == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH) {
                Integer virtualType = pluginElement.getVirtualType();
                if (virtualType == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER) {
                    var networkElementParseResult = this.buildNetworkElementParseResultV2(e, pluginElement);

                    try {
                        if (StringUtils.isNotEmpty(e.getPosition2())) {
                            Origin position = JsonCommon.parse(e.getPosition2(), Origin.class);
                            if (position != null) {
                                networkElementParseResult.setPosition(position.getX() + "," + position.getY());
                            }
                        }
                    } catch (Exception exception) {
                        
                    }

                    id2Network.put(e.getId(), networkElementParseResult);
                    networks.add(networkElementParseResult);
                    if (pluginElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK) {
                        networkElementParseResult.setId("internet-" + networkIndex++);
                    } else {
                        networkElementParseResult.setId("network-" + networkIndex++);
                    }

                    var nodeElementParseResult = new NodeElementParseResult();
                    nodeElementParseResult.setId("router-" + networkElementParseResult.getId());
                    id2Node.put(e.getId(), nodeElementParseResult);
                    nodes.add(nodeElementParseResult);
                } else {
                    var nodeElementParseResult = this.buildNodeElementParseResult(sceneHandler, e, pluginElement);
                    try {
                        if (StringUtils.isNotEmpty(e.getPosition2())) {
                            Origin position = JsonCommon.parse(e.getPosition2(), Origin.class);
                            if (position != null) {
                                nodeElementParseResult.setPosition(position.getX() + "," + position.getY());
                            }
                        }
                    } catch (Exception exception) {
                        
                    }
                    nodeElementParseResult.setId("server-" + serverIndex++);
                    id2Node.put(e.getId(), nodeElementParseResult);
                    nodes.add(nodeElementParseResult);
                }
            } else {
                var nodeElementParseResult = this.buildNodeElementParseResult(sceneHandler, e, pluginElement);

                try {
                    if (StringUtils.isNotEmpty(e.getPosition2())) {
                        Origin position = JsonCommon.parse(e.getPosition2(), Origin.class);
                        if (position != null) {
                            nodeElementParseResult.setPosition(position.getX() + "," + position.getY());
                        }
                    }
                } catch (Exception exception) {
                    
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
                            netElementParseResult.setAcls(this.buildAclsInfos(pluginPort));
                        }
                    }
                });
            }
        }
    }

    private List<AclsInfo> buildAclsInfos(CloudPluginSceneConfigElementPortBO pluginPort) {
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

    private NetworkElementParseResultV2 buildNetworkElementParseResultV2(SceneConfigElementBO e, CloudPluginSceneConfigElementBO pluginElement) {
        NetworkElementParseResultV2 networkElementParseResult = new NetworkElementParseResultV2();
        networkElementParseResult.setName(e.getName());
        networkElementParseResult.setDisplayContext(e.getDisplayContext());
        if (BaseSceneElementModel.Type.SWITCH == e.getType()) {
            networkElementParseResult.setDhcp(Boolean.TRUE.equals(pluginElement.getDhcp()) ? SPECIAL_STR_ON : SPECIAL_STR_OFF);
            networkElementParseResult.setCidr(pluginElement.getCidr());
            networkElementParseResult.setGateway(pluginElement.getGateway());
            networkElementParseResult.setDns(pluginElement.getDnsNameservers());
            if (CollectionUtils.isNotEmpty(pluginElement.getAllocationPools())) {
                var list = pluginElement.getAllocationPools().stream().map(p -> p.getStart() + "-" + p.getEnd()).collect(Collectors.toList());
                networkElementParseResult.setDhcpPool(StringUtils.join(list, ","));
            } else {
                networkElementParseResult.setDhcpPool(StringUtils.EMPTY);
            }
        }
        return networkElementParseResult;
    }

    private NodeElementParseResult buildNodeElementParseResult(SceneConfigHandler sceneHandler, SceneConfigElementBO e, CloudPluginSceneConfigElementBO pluginElement) {
        NodeElementParseResult nodeElementParseResult = new NodeElementParseResult();
        nodeElementParseResult.setFile(e.getFile());
        nodeElementParseResult.setImage(pluginElement.getImage());
        nodeElementParseResult.setName(e.getName());
        nodeElementParseResult.setInitCommands(new ArrayList<>());
        
        nodeElementParseResult.setDisplayContext(e.getDisplayContext());
        if (CollectionUtils.isNotEmpty(e.getInitCommands())) {
            e.getInitCommands().forEach(c -> {
                InitCommandParseResult result = new InitCommandParseResult();
                if (c.startsWith("o:")) {
                    result.setO(StringUtils.replaceOnce(c, "o:", "").trim());
                } else if (c.startsWith("s:")) {
                    result.setS(StringUtils.replaceOnce(c, "s:", "").trim());
                } else if (c.startsWith("n:")) {
                    result.setN(StringUtils.replaceOnce(c, "n:", "").trim());
                } else {
                    return;
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

                String netxtHopNode = null;
                if (StringUtils.isNotEmpty(s.getNexthopPortId())) {
                    
                    
                    var connectElement = sceneHandler.getPortConnectedElement(s.getNexthopPortId());
                    if (connectElement != null) {
                        netxtHopNode = connectElement.getId();
                    }
                }

                
                StaticRoutesResult resulta = new StaticRoutesResult();
                resulta.setGateway(s.getNexthop());
                resulta.setDestination(s.getDestination());
                resulta.setNexthopNode(netxtHopNode);
                nodeElementParseResult.getStaticRoutes().add(resulta);

                StaticRoutesResult result = new StaticRoutesResult();
                result.setGateway(s.getNexthop());
                result.setDestination(s.getDestination());
                result.setNexthopNode(netxtHopNode);
                nodeElementParseResult.getStaticRouters().add(result);
            });
        }
        ExtNodeInfo protocolExtNodeInfo = new ExtNodeInfo();
        protocolExtNodeInfo.setRipLoopback(pluginElement.getRipLoopback());
        protocolExtNodeInfo.setOspfRouterId(pluginElement.getOspfRouterId());
        protocolExtNodeInfo.setPortProtocols(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(e.getPorts())) {
            CloudSceneConfigPluginHandler cloudSceneConfigPluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
            for (var p : e.getPorts()) {
                var cloudPort = cloudSceneConfigPluginHandler.getPort(p.getId());
                ExtNodeInfo.PortProtocolExportInfo exportInfo = new ExtNodeInfo.PortProtocolExportInfo();
                
                var connection = sceneHandler.getPortBelongConnection(p.getId());
                if (connection != null) {
                    exportInfo.setVertices(JsonCommon.stringify(connection.getVertices()));
                    exportInfo.setDisplayContext(connection.getDisplayContext());
                    exportInfo.setType(connection.getType());
                }
                if (p.getDelay() != null && p.getDelay() > 0) {
                    exportInfo.setDelay(p.getDelay());
                }
                if (p.getLoss() != null && p.getLoss() > 0) {
                    exportInfo.setLoss(p.getLoss());
                }
                if (p.getEgress() != null && p.getEgress() > 0) {
                    exportInfo.setEgress(p.getEgress());
                }
                if (p.getIngress() != null && p.getIngress() > 0) {
                    exportInfo.setIngress(p.getIngress());
                }
                if (StringUtils.isNotEmpty(cloudPort.getConnectMode())) {
                    exportInfo.setConnectMode(cloudPort.getConnectMode());
                }
                exportInfo.setBgpAs(cloudPort.getBgpAs());
                exportInfo.setBgpIp(cloudPort.getBgpIp());
                exportInfo.setBgpPeer(cloudPort.getBgpPeer());
                exportInfo.setBgpPeerAs(cloudPort.getBgpPeerAs());
                exportInfo.setBgpAs(cloudPort.getBgpAs());
                exportInfo.setOspfIp(cloudPort.getOspfIp());
                exportInfo.setOspfArea(cloudPort.getOspfArea());
                exportInfo.setRipIp(cloudPort.getRipIp());
                exportInfo.setRipPear(cloudPort.getRipPear());
                if (sceneHandler.getPortConnectedElement(p.getId()) != null) {
                    exportInfo.setConnectNode(sceneHandler.getPortConnectedElement(p.getId()).getId());
                }
                if (exportInfo.isNotEmpty()) {
                    protocolExtNodeInfo.getPortProtocols().add(exportInfo);
                }
            }
        }
        if (protocolExtNodeInfo.isNotEmpty()) {
            nodeElementParseResult.setExtInfo(protocolExtNodeInfo);
        } else {
            nodeElementParseResult.setExtInfo(new ExtNodeInfo());
        }
        return nodeElementParseResult;
    }

}
