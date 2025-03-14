package cn.cyberpeace.mp.scene.core.sceneconfig;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.CustomCondition;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.*;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ThirdPartyTopoSceneConfigHandler {

    @Getter
    private List<ThirdPartyElementBO> thirdPartyElementBOs = new ArrayList<>();
    @Getter
    private List<ThirdPartyElementConnectBO> thirdPartyElementConnectBOs = new ArrayList<>();
    private List<ThirdPartyElementPortBO> thirdPartyElementPortBOS = new ArrayList<>();
    @Getter
    private String sceneName;

    private final String DEFAULT_RANGE = "合肥靶场";

    public ThirdPartyTopoSceneConfigHandler(String sceneConfigId) {
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneConfigId, "", null);
        this.sceneName = sceneConfigHandler.getScene().getName();
        this.thirdPartyElementBOs = sceneConfigHandler.getElementMap().values()
                .stream().map(element -> {
                    ThirdPartyElementBO thirdPartyElementBO = new ThirdPartyElementBO();
                    thirdPartyElementBO.setNodeName(element.getName());
                    if (BaseSceneElementModel.Type.TERMINAL == element.getType()) {
                        thirdPartyElementBO.setNodeType(ThirdPartyElementBO.NodeType.END_DEVICE);
                    } else if (BaseSceneElementModel.Type.SWITCH == element.getType()) {
                        thirdPartyElementBO.setNodeType(ThirdPartyElementBO.NodeType.SWITCH);
                    } else if (BaseSceneElementModel.Type.ROUTER == element.getType()) {
                        thirdPartyElementBO.setNodeType(ThirdPartyElementBO.NodeType.ROUTER);
                    } else if (BaseSceneElementModel.Type.FIREWALL == element.getType()) {
                        thirdPartyElementBO.setNodeType(ThirdPartyElementBO.NodeType.FIREWALL);
                    } else {
                        thirdPartyElementBO.setNodeType(ThirdPartyElementBO.NodeType.OTHER);
                    }
                    thirdPartyElementBO.setNodeId(element.getEid());
                    if (StringUtils.isEmpty(element.getRange())) {
                        thirdPartyElementBO.setRange(DEFAULT_RANGE);
                    } else {
                        thirdPartyElementBO.setRange(element.getRange());
                    }
                    return thirdPartyElementBO;
                }).collect(Collectors.toList());

        this.thirdPartyElementConnectBOs = sceneConfigHandler.getElementPortConnectionMap().values()
                .stream().map(connection -> {
                    ThirdPartyElementConnectBO thirdPartyElementConnectBO = new ThirdPartyElementConnectBO();
                    var sourcePort = sceneConfigHandler.getPort(connection.getSource());
                    var targetPort = sceneConfigHandler.getPort(connection.getTarget());
                    thirdPartyElementConnectBO.setFromNodeName(sourcePort.getElement().getName());
                    thirdPartyElementConnectBO.setToNodeName(targetPort.getElement().getName());
                    thirdPartyElementConnectBO.setFromNodeIp(sourcePort.getIp());
                    thirdPartyElementConnectBO.setToNodeIp(targetPort.getIp());
                    thirdPartyElementConnectBO.setFromPortId(sourcePort.getId());
                    thirdPartyElementConnectBO.setToPortId(targetPort.getId());
                    return thirdPartyElementConnectBO;
                }).collect(Collectors.toList());
        genPortRelation();
    }




    public ThirdPartyTopoSceneConfigHandler(String sceneName, List<ThirdPartyElementBO> thirdPartyElementBOs, List<ThirdPartyElementConnectBO> thirdPartyElementConnectBOs) {
        this.sceneName = sceneName;
        this.thirdPartyElementBOs = thirdPartyElementBOs;
        this.thirdPartyElementConnectBOs = thirdPartyElementConnectBOs;
        genPortRelation();

    }

    private void genPortRelation() {
        for (ThirdPartyElementConnectBO thirdPartyElementConnectBO : this.thirdPartyElementConnectBOs) {
            String fromNodeName = thirdPartyElementConnectBO.getFromNodeName();
            String toNodeName = thirdPartyElementConnectBO.getToNodeName();

            ThirdPartyElementBO fromNode = getElementByName(fromNodeName);
            ThirdPartyElementBO toNode = getElementByName(toNodeName);

            ThirdPartyElementPortBO fromNodePort = new ThirdPartyElementPortBO(fromNode, thirdPartyElementConnectBO.getFromNodeIp());
            fromNode.getPorts().add(fromNodePort);

            ThirdPartyElementPortBO toNodePort = new ThirdPartyElementPortBO(toNode, thirdPartyElementConnectBO.getToNodeIp());
            toNode.getPorts().add(toNodePort);

            thirdPartyElementConnectBO.setFromPortId(fromNodePort.getPortId());
            thirdPartyElementConnectBO.setToPortId(toNodePort.getPortId());

            thirdPartyElementPortBOS.add(fromNodePort);
            thirdPartyElementPortBOS.add(toNodePort);
        }
    }

    private ThirdPartyElementBO getElementByName(String name) {
        for (ThirdPartyElementBO thirdPartyElementBO : this.thirdPartyElementBOs) {
            if (thirdPartyElementBO.getNodeName().equals(name)) {
                return thirdPartyElementBO;
            }
        }
        throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("根据节点名称"+name+"获取节点失败"));
    }

    private SceneConfigDataBO toSceneConfigDataBO() {
        SceneConfigDataBO sceneConfigDataBO = new SceneConfigDataBO();
        SceneConfigTopologyBO sceneConfigTopologyBO = new SceneConfigTopologyBO();

        SceneConfigBO sceneConfigBO = new SceneConfigBO();
        sceneConfigBO.setName(this.sceneName);

        sceneConfigTopologyBO.setScene(sceneConfigBO);
        List<SceneConfigElementBO> elements = new ArrayList<>();
        for (ThirdPartyElementBO thirdPartyElementBO : this.thirdPartyElementBOs) {
            SceneConfigElementBO sceneConfigElementBO = new SceneConfigElementBO();
            sceneConfigElementBO.setRange(thirdPartyElementBO.getRange());
            sceneConfigElementBO.setName(thirdPartyElementBO.getNodeName());
            sceneConfigElementBO.setIcon2(thirdPartyElementBO.getDefaultIcon());
            sceneConfigElementBO.setType(thirdPartyElementBO.getDefaultType());
            sceneConfigElementBO.setCurrentScenePartFlag(false);
            sceneConfigElementBO.setCpu(1);
            sceneConfigElementBO.setDisk(1024);
            sceneConfigElementBO.setRam(10);
            sceneConfigElementBO.setDescription("其他靶场节点");
            sceneConfigElementBO.setId(thirdPartyElementBO.getNodeId());
            List<SceneConfigElementPortBO> ports = new ArrayList<>();

            for (int i = 0; i < thirdPartyElementBO.getPorts().size(); i++) {
                SceneConfigElementPortBO sceneConfigElementPortBO = getSceneConfigElementPortBO(thirdPartyElementBO, i, sceneConfigElementBO);
                ports.add(sceneConfigElementPortBO);
            }
            sceneConfigElementBO.setPorts(ports);
            elements.add(sceneConfigElementBO);
        }
        sceneConfigTopologyBO.setElements(elements);

        List<SceneConfigElementPortConnectionBO> connections = new ArrayList<>();
        for (ThirdPartyElementConnectBO thirdPartyElementConnectBO : this.thirdPartyElementConnectBOs) {
            SceneConfigElementPortConnectionBO connection = new SceneConfigElementPortConnectionBO();
            connection.setType("straight");
            connection.setName("{}");
            connection.setId(TextUtils.uuid());
            connection.setSource(thirdPartyElementConnectBO.getFromPortId());
            connection.setTarget(thirdPartyElementConnectBO.getToPortId());

            connections.add(connection);
        }
        sceneConfigTopologyBO.setConnections(connections);

        sceneConfigTopologyBO.setPieces(new ArrayList<>());
        sceneConfigDataBO.setTopology(sceneConfigTopologyBO);


        ScenePluginDataBO cpcloudDataBo = new ScenePluginDataBO();
        Map<String, Object> cpcloudScene = new HashMap<>(8);
        cpcloudScene.put("src_scene", sceneConfigBO.getEid());
        cpcloudScene.put("enable_rate", true);
        cpcloudScene.put("enable_recording", true);

        cpcloudDataBo.setScene(cpcloudScene);

        Map<String, Map<String, Object>> cloudElementMap = new HashMap<>();
        Map<String, Map<String, Object>> cloudElementPortMap = new HashMap<>();
        for (int i = 0; i < elements.size(); i++) {
            var element = elements.get(i);
            Map<String, Object> cpcloudElement = new HashMap<>(8);
            if (BaseSceneElementModel.Type.OTHER == element.getType()) {
                cpcloudElement.put("category", 3);
                cpcloudElement.put("role", 7);
                cpcloudElement.put("virtual_type", 7);
                cpcloudElement.put("category2", 54);
                cpcloudElement.put("category3", 0);
            } else if (BaseSceneElementModel.Type.ROUTER == element.getType()) {
                cpcloudElement.put("category", 3);
                cpcloudElement.put("role", 21);
                cpcloudElement.put("virtual_type", 2);
                cpcloudElement.put("category2", 51);
                cpcloudElement.put("category3", 0);
            } else if (BaseSceneElementModel.Type.FIREWALL == element.getType()) {
                cpcloudElement.put("category", 3);
                cpcloudElement.put("role", 23);
                cpcloudElement.put("virtual_type", 2);
                cpcloudElement.put("category2", 52);
                cpcloudElement.put("category3", 0);
            } else if (BaseSceneElementModel.Type.SWITCH == element.getType()) {
                cpcloudElement.put("category", 3);
                cpcloudElement.put("role", 22);
                cpcloudElement.put("virtual_type", 2);
                cpcloudElement.put("category2", 50);
                cpcloudElement.put("category3", 0);
            } else if (BaseSceneElementModel.Type.TERMINAL == element.getType()) {
                cpcloudElement.put("category", 1);
                cpcloudElement.put("role", 24);
                cpcloudElement.put("virtual_type", 4);
                cpcloudElement.put("category2", 1);
                cpcloudElement.put("category3", 97);
            }
            cpcloudElement.put("wan_port_type", "eth");
            cpcloudElement.put("electric_port_number", element.getPorts().size());
            cpcloudElement.put("lan_port_type", "E");
            cpcloudElement.put("electric_port_type", "E");
            cpcloudElement.put("optical_port_type", "sfp");

            cloudElementMap.put(element.getId(), cpcloudElement);

            for (SceneConfigElementPortBO port : element.getPorts()) {
                Map<String, Object> cloudPort = new HashMap<>(8);
                cloudElementPortMap.put(port.getId(), cloudPort);
            }
        }
        cpcloudDataBo.setElements(cloudElementMap);
        cpcloudDataBo.setElementPorts(cloudElementPortMap);
        sceneConfigDataBO.setPlugins(new HashMap<>());

        sceneConfigDataBO.getPlugins().put("cpcloud", cpcloudDataBo);
        return sceneConfigDataBO;
    }

    private static SceneConfigElementPortBO getSceneConfigElementPortBO(ThirdPartyElementBO thirdPartyElementBO, int i, SceneConfigElementBO sceneConfigElementBO) {
        ThirdPartyElementPortBO thirdPartyPort = thirdPartyElementBO.getPorts().get(i);
        SceneConfigElementPortBO sceneConfigElementPortBO = new SceneConfigElementPortBO();
        sceneConfigElementPortBO.setId(thirdPartyPort.getPortId());
        sceneConfigElementPortBO.setElement(sceneConfigElementBO);
        sceneConfigElementPortBO.setIp(thirdPartyPort.getIp());
        sceneConfigElementPortBO.setName(thirdPartyPort.getPortName());
        sceneConfigElementPortBO.setOrder(i);
        sceneConfigElementPortBO.setType(1);
        sceneConfigElementPortBO.setIsAuxiliary(false);
        return sceneConfigElementPortBO;
    }

    private SceneConfigHandler toSceneConfigHandler() {
        SceneConfigDataBO sceneConfigDataBO = this.toSceneConfigDataBO();
        return new SceneConfigHandler(sceneConfigDataBO, null);
    }

    public SceneConfigDataBO save() {
        SceneConfigHandler sceneConfigHandler = this.toSceneConfigHandler();
        sceneConfigHandler.save();
        return sceneConfigHandler.toData();
    }




}
