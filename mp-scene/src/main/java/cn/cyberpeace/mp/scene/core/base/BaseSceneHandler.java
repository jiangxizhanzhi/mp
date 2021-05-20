package cn.cyberpeace.mp.scene.core.base;

import cn.cyberpeace.mp.base.annotation.BeanFieldNames;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.scene.core.bo.base.*;
import cn.cyberpeace.mp.scene.model.base.*;
import cn.cyberpeace.mp.scene.repository.base.*;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@BeanFieldNames({
        "sceneMapper",
        "sceneAreaMapper",
        "sceneElementMapper",
        "sceneElementPortMapper",
        "sceneElementPortConnectionMapper"
})
public abstract class BaseSceneHandler<
        S0 extends BaseSceneModel,
        A0 extends BaseSceneAreaModel,
        E0 extends BaseSceneElementModel,
        P0 extends BaseSceneElementPortModel,
        C0 extends BaseSceneElementPortConnectionModel,
        S extends BaseSceneBO<S0>,
        A extends BaseSceneAreaBO<A0>,
        P extends BaseSceneElementPortBO<P0>,
        E extends BaseSceneElementBO<E0, P>,
        C extends BaseSceneElementPortConnectionBO<C0>,
        T extends BaseSceneTopologyBO<S0, A0, E0, P0, C0, S, A, P, E, C>,
        D extends BaseSceneDataBO<T>,
        M1 extends BaseSceneMapper<S0>,
        M2 extends BaseSceneAreaMapper<A0>,
        M3 extends BaseSceneElementMapper<E0>,
        M4 extends BaseSceneElementPortMapper<P0>,
        M5 extends BaseSceneElementPortConnectionMapper<C0>> {

    @Getter
    private S scene;
    @Getter
    private String areaId;
    @Getter
    private final Map<String, A> areaMap = new HashMap<>();
    @Getter
    private final Map<String, E> elementMap = new HashMap<>();
    @Getter
    private final Map<String, P> elementPortMap = new HashMap<>();
    @Getter
    private final Map<String, E> elementPortIdElementMap = new HashMap<>();
    @Getter
    private final Map<String, C> elementPortConnectionMap = new HashMap<>();

    private M1 sceneMapper;
    private M2 sceneAreaMapper;
    private M3 sceneElementMapper;
    private M4 sceneElementPortMapper;
    private M5 sceneElementPortConnectionMapper;

    private HumanCondition areaCondition(String sceneId, String areaId) {
        return new HumanCondition().filterEqual("scene", sceneId).filterEqual("area", areaId);
    }

    private String elementPortConnectionKey(C0 elementPortConnection) {
        return StringUtils.joinWith(":", elementPortConnection.getSourcePortId(),
                elementPortConnection.getTargetPortId());
    }

    private String elementPortConnectionKey(C elementPortConnection) {
        return StringUtils.joinWith(":", elementPortConnection.getSource(),
                elementPortConnection.getTarget());
    }

    private <M extends BaseSceneNodeModel, N extends BaseSceneNodeBO<M>,
            O extends BaseModelMapper<M>> DiffTargets<M> saveSceneNodes(O modelMapper, Map<String, N> nodeBOMap) {
        String sceneId = scene.getEid();
        String filterAreaId = StringUtils.isEmpty(this.areaId) ? StringUtils.EMPTY : this.areaId;
        List<M> existNodes = modelMapper.list(areaCondition(sceneId, filterAreaId));
        Map <String, M> existNodeMap = new HashMap<>();
        existNodes.forEach(node -> existNodeMap.put(node.getNodeId(), node));

        var diff = new DiffTargets<M>();
        Map<String, M> nodeMap = new HashMap<>();
        nodeBOMap.forEach((nodeId, nodeBO) -> {
            var node = nodeBO.toModel();
            node.setScene(sceneId);

            nodeMap.put(nodeId, node);
            if (existNodeMap.containsKey(nodeId)) {
                existNodeMap.remove(nodeId);
                diff.getUpdatingTargets().add(node);
            } else {
                diff.getAddingTargets().add(node);
            }
        });

        List<String> removingNodeIds = new ArrayList<>();
        existNodeMap.forEach((nodeId, node) -> {
            removingNodeIds.add(node.getId());
            diff.getRemovingTargets().add(node);
        });

        modelMapper.bulkLogicDelete(removingNodeIds.toArray(new String[0]));
        modelMapper.bulkCreate(diff.getAddingTargets());
        modelMapper.bulkUpdate(diff.getUpdatingTargets());

        nodeMap.forEach((nodeId, node) -> nodeBOMap.get(nodeId).fromModel(node));

        return diff;
    }

    private void saveScene() {
        S0 sceneObj = scene.toModel();
        sceneMapper.save(sceneObj);
        scene.fromModel(sceneObj);
    }

    private DiffTargets<A0> saveSceneAreas() {
        return saveSceneNodes(sceneAreaMapper, areaMap);
    }

    private DiffTargets<E0> saveSceneElements() {
        return saveSceneNodes(sceneElementMapper, elementMap);
    }

    private DiffTargets<P0> saveSceneElementPorts(DiffTargets<E0> diffElements) {
        String[] originElementIds = ArrayUtils.addAll(diffElements.getRemovingTargets().stream()
                .map(BaseModel::getId).toArray(String[]::new), diffElements.getUpdatingTargets().stream()
                .map(BaseModel::getId).toArray(String[]::new));
        Map<String, P0> existElementPortMap = new HashMap<>();
        if (originElementIds.length > 0) {
            sceneElementPortMapper.list(new HumanCondition()
                    .select("id", "scene", "port_id")
                    .filterIn("scene_element", originElementIds)).forEach(elementPort -> {
                existElementPortMap.put(elementPort.getPortId(), elementPort);
            });
        }

        String sceneId = scene.getEid();
        DiffTargets<P0> diffElementPorts = new DiffTargets<>();
        Map<String, P0> elementPortObjMap = new HashMap<>();
        this.elementPortMap.forEach((portId, elementPortBO) -> {
            String sceneElementId = elementPortIdElementMap.get(portId).getEid();
            P0 elementPort = elementPortBO.toModel();
            elementPort.setScene(sceneId);
            elementPort.setSceneElement(sceneElementId);
            elementPortObjMap.put(portId, elementPort);
            if (existElementPortMap.containsKey(portId)) {
                existElementPortMap.remove(portId);
                diffElementPorts.getUpdatingTargets().add(elementPort);
            } else {
                diffElementPorts.getAddingTargets().add(elementPort);
            }
        });

        List<String> removingElementPortIds = new ArrayList<>();
        existElementPortMap.forEach((portId, elementPort) -> {
            removingElementPortIds.add(elementPort.getId());
            diffElementPorts.getRemovingTargets().add(elementPort);
        });

        sceneElementPortMapper.bulkLogicDelete(removingElementPortIds.toArray(new String[0]));
        sceneElementPortMapper.bulkCreate(diffElementPorts.getAddingTargets());
        sceneElementPortMapper.bulkUpdate(diffElementPorts.getUpdatingTargets());

        elementPortObjMap.forEach((portId, elementPort) -> this.elementPortMap.get(portId).fromModel(elementPort));

        return diffElementPorts;
    }

    private DiffTargets<C0> saveSceneElementPortConnections(DiffTargets<P0> diffElementPorts) {
        String[] originElementPortIds = ArrayUtils.addAll(diffElementPorts.getRemovingTargets().stream()
                .map(BaseModel::getId).toArray(String[]::new), diffElementPorts.getUpdatingTargets().stream()
                .map(BaseModel::getId).toArray(String[]::new));

        Map<String, C0> existElementPortConnectionMap = new HashMap<>();
        if (originElementPortIds.length > 0) {
            sceneElementPortConnectionMapper.list(new HumanCondition()
                    .select("id", "source_port_id", "target_port_id")
                    .filterOrStart()
                    .orIn("source_port", originElementPortIds)
                    .orIn("target_port", originElementPortIds)
                    .filterOrEnd()).forEach(elementPortConnection -> existElementPortConnectionMap.put(
                            elementPortConnectionKey(elementPortConnection), elementPortConnection));
        }

        String sceneId = scene.getEid();
        DiffTargets<C0> diffElementPortConnections = new DiffTargets<>();
        Map<String, C0> elementPortConnectionObjMap = new HashMap<>();
        this.elementPortConnectionMap.forEach((connectionKey, elementPortConnectionBO) -> {
            C0 elementPortConnection = elementPortConnectionBO.toModel();
            elementPortConnection.setScene(sceneId);
            elementPortConnection.setSourcePort(elementPortMap.get(elementPortConnection.getSourcePortId()).getEid());
            elementPortConnection.setTargetPort(elementPortMap.get(elementPortConnection.getTargetPortId()).getEid());
            elementPortConnectionObjMap.put(connectionKey, elementPortConnection);
            if (existElementPortConnectionMap.containsKey(connectionKey)) {
                existElementPortConnectionMap.remove(connectionKey);
                diffElementPortConnections.getUpdatingTargets().add(elementPortConnection);
            } else {
                diffElementPortConnections.getAddingTargets().add(elementPortConnection);
            }
        });

        List<String> removingElementPortConnectionIds = new ArrayList<>();
        existElementPortConnectionMap.forEach((connectionKey, elementPortConnection) -> {
            removingElementPortConnectionIds.add(elementPortConnection.getId());
            diffElementPortConnections.getRemovingTargets().add(elementPortConnection);
        });

        sceneElementPortConnectionMapper.bulkLogicDelete(removingElementPortConnectionIds.toArray(new String[0]));
        sceneElementPortConnectionMapper.bulkCreate(diffElementPortConnections.getAddingTargets());
        sceneElementPortConnectionMapper.bulkUpdate(diffElementPortConnections.getUpdatingTargets());

        elementPortConnectionObjMap.forEach((connectionKey, elementPortConnection) ->
                this.elementPortConnectionMap.get(connectionKey).fromModel(elementPortConnection));

        return diffElementPortConnections;
    }

    public void initFromSceneDataBO(D sceneDataBO) {
        var topology = sceneDataBO.getTopology();
        scene = topology.getScene();
        areaId = topology.getAreaId();
        List<A> areas = topology.getAreas();
        if (areas != null) {
            areas.forEach(area -> areaMap.put(area.getId(), area));
        }
        List<E> elements = topology.getElements();
        if (elements != null) {
            elements.forEach(element -> {
                elementMap.put(element.getId(), element);
                List<P> elementPorts = element.getPorts();
                if (elementPorts == null) {
                    return;
                }
                elementPorts.forEach(elementPort -> {
                    elementPortMap.put(elementPort.getId(), elementPort);
                    elementPortIdElementMap.put(elementPort.getId(), element);
                });
            });
        }
        List<C> connections = topology.getConnections();
        if (connections != null) {
            connections.forEach(connection -> elementPortConnectionMap.put(elementPortConnectionKey(connection), connection));
        }
    }

    public void save() {
        saveScene();
        saveSceneAreas();
        DiffTargets<E0> diffElements = saveSceneElements();
        DiffTargets<P0> diffElementPorts = saveSceneElementPorts(diffElements);
        DiffTargets<C0> diffElementPortConnections = saveSceneElementPortConnections(diffElementPorts);
    }

}
