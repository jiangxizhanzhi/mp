package cn.cyberpeace.mp.scene.core.base;

import cn.cyberpeace.mp.base.annotation.BeanFieldNames;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.ListTreeNode;
import cn.cyberpeace.mp.scene.core.bo.base.*;
import cn.cyberpeace.mp.scene.model.base.*;
import cn.cyberpeace.mp.scene.repository.base.*;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

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

    private Boolean isAreaMode;
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
    @Getter
    private final Set<String> outerConnectionPortIds = new HashSet<>();

    private M1 sceneMapper;
    private M2 sceneAreaMapper;
    private M3 sceneElementMapper;
    private M4 sceneElementPortMapper;
    private M5 sceneElementPortConnectionMapper;

    public boolean isAreaMode() {
        return Boolean.TRUE.equals(isAreaMode);
    }

    private void filterAreaData() {
        ListTreeNode<A> areaNodeTree = new ListTreeNode<>();
        Map<String, ListTreeNode<A>> areaNodeMap = new HashMap<>();
        if (StringUtils.isEmpty(areaId)) {
            areaNodeMap.put(null, areaNodeTree);
            areaNodeMap.put(StringUtils.EMPTY, areaNodeTree);
        } else {
            areaNodeMap.put(areaId, areaNodeTree);
        }

        areaMap.forEach((areaNodeId, area) -> areaNodeMap.put(areaNodeId, new ListTreeNode<>(area)));
        areaNodeMap.forEach((areaNodeId, areaNode) -> {
            var area = areaNode.get();
            if (area == null) {
                return;
            }
            ListTreeNode<A> parentAreaNode = areaNodeMap.getOrDefault(area.getArea(), null);
            if (parentAreaNode == null) {
                return;
            }
            parentAreaNode.addChild(areaNode);
        });

        areaMap.clear();
        filterAreaTree(areaNodeTree, areaMap);
    }

    private void filterAreaTree(ListTreeNode<A> areaNode, Map<String, A> targetAreaMap) {
        var area = areaNode.get();
        if (area != null) {
            targetAreaMap.put(area.getId(), area);
            if (!Boolean.TRUE.equals(area.getOpen())) {
                return;
            }
        }

        areaNode.getChildren().forEach(areaChildNode -> filterAreaTree(areaChildNode, targetAreaMap));
    }

    private boolean isValidAreaElement(E element) {
        if (!isAreaMode()) {
            return true;
        }

        String elementArea = element.getArea();
        if (StringUtils.isEmpty(areaId) && StringUtils.isEmpty(elementArea)) {
            return true;
        }

        if (StringUtils.equals(areaId, elementArea)) {
            return true;
        }

        var parentArea = areaMap.getOrDefault(elementArea, null);
        return parentArea != null && parentArea.getOpen();
    }

    private String elementPortConnectionKey(C0 elementPortConnection) {
        return StringUtils.joinWith(":", elementPortConnection.getSourcePortId(),
                elementPortConnection.getTargetPortId());
    }

    private String elementPortConnectionKey(C elementPortConnection) {
        return StringUtils.joinWith(":", elementPortConnection.getSource(),
                elementPortConnection.getTarget());
    }

    private void loadAreaBOs(List<A> areas) {
        if (areas == null) {
            return;
        }

        areas.forEach(area -> areaMap.put(area.getId(), area));
        if (isAreaMode()) {
            filterAreaData();
        }
    }

    private void loadElementBOs(List<E> elements) {
        if (elements == null) {
            return;
        }

        elements.forEach(element -> {
            if (!isValidAreaElement(element)) {
                return;
            }

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

    private void loadConnectionBOs(List<C> connections) {
        if (connections == null) {
            return;
        }

        connections.forEach(connection -> {
            String sourcePortId = connection.getSource();
            String targetPortId = connection.getTarget();
            if (!elementPortMap.containsKey(sourcePortId)
                    && !elementPortMap.containsKey(targetPortId)) {
                return;
            }

            if (!elementPortMap.containsKey(sourcePortId)) {
                outerConnectionPortIds.add(sourcePortId);
            }
            if (!elementPortMap.containsKey(targetPortId)) {
                outerConnectionPortIds.add(targetPortId);
            }

            elementPortConnectionMap.put(elementPortConnectionKey(connection), connection);
        });
    }

    private void fromData(D sceneDataBO) {
        var topology = sceneDataBO.getTopology();
        scene = topology.getScene();
        areaId = topology.getAreaId();
        loadAreaBOs(topology.getAreas());
        loadElementBOs(topology.getElements());
        loadConnectionBOs(topology.getConnections());
    }

    private HumanCondition areaCondition(String sceneId, String... areaIds) {
        var humanCondition = new HumanCondition().filterEqual("scene", sceneId);
        var orCondition = humanCondition.filterOrStart();
        for (String filterAreaId: areaIds) {
            orCondition.orEqual("area", filterAreaId);
        }
        return orCondition.filterOrEnd();
    }

    private List<A0> listSceneAreas(String sceneId, String... areaIds) {
        List<A0> sceneAreas = sceneAreaMapper.list(areaCondition(sceneId, areaIds));
        String[] openSceneAreaIds = sceneAreas.stream()
                .filter(area -> Boolean.TRUE.equals(area.getOpen()))
                .map(BaseSceneNodeModel::getNodeId)
                .toArray(String[]::new);
        if (openSceneAreaIds.length > 0) {
            sceneAreas.addAll(listSceneAreas(sceneId, openSceneAreaIds));
        }
        return sceneAreas;
    }

    private List<E0> listSceneElements(String sceneId, String... areaIds) {
        return sceneElementMapper.list(areaCondition(sceneId, areaIds));
    }

    private <M extends BaseSceneNodeModel, N extends BaseSceneNodeBO<M>,
            O extends BaseModelMapper<M>> DiffTargets<M> saveSceneNodes(O modelMapper, Map<String, N> nodeBOMap,
                                                                        List<M> existNodes) {
        String sceneId = scene.getEid();
        Map <String, M> existNodeMap = new HashMap<>();
        existNodes.forEach(node -> existNodeMap.put(node.getNodeId(), node));

        var diff = new DiffTargets<M>();
        Map<String, M> nodeMap = new HashMap<>();
        nodeBOMap.forEach((nodeId, nodeBO) -> {
            var node = nodeBO.toModel();
            node.setScene(sceneId);
            nodeMap.put(nodeId, node);

            var existNode = existNodeMap.getOrDefault(nodeId, null);
            if (existNode == null) {
                diff.getAddingTargets().add(node);
            } else {
                existNodeMap.remove(nodeId);
                node.setId(existNode.getId());
                diff.getUpdatingTargets().add(node);
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
        List<A0> existSceneAreas;
        if (isAreaMode()) {
            String filterAreaId = StringUtils.isEmpty(areaId) ? StringUtils.EMPTY : areaId;
            existSceneAreas = listSceneAreas(scene.getEid(), filterAreaId);
        } else {
            existSceneAreas = listSceneAreas(scene.getEid());
        }
        return saveSceneNodes(sceneAreaMapper, areaMap, existSceneAreas);
    }

    private DiffTargets<E0> saveSceneElements() {
        List<E0> existSceneElements;
        if (isAreaMode()) {
            String mainAreaId = StringUtils.isEmpty(areaId) ? StringUtils.EMPTY : areaId;
            String[] openAreaIds = areaMap.values().stream()
                    .filter(area -> Boolean.TRUE.equals(area.getOpen()))
                    .map(BaseSceneNodeBO::getId)
                    .toArray(String[]::new);
            existSceneElements = listSceneElements(scene.getEid(),
                    ArrayUtils.addAll(new String[]{mainAreaId}, openAreaIds));
        } else {
            existSceneElements = listSceneElements(scene.getEid());
        }
        return saveSceneNodes(sceneElementMapper, elementMap, existSceneElements);
    }

    private DiffTargets<P0> saveSceneElementPorts(DiffTargets<E0> diffElements) {
        String[] originElementIds = ArrayUtils.addAll(diffElements.getRemovingTargets().stream()
                .map(BaseModel::getId).toArray(String[]::new), diffElements.getUpdatingTargets().stream()
                .map(BaseModel::getId).toArray(String[]::new));
        Map<String, P0> existElementPortMap = new HashMap<>();
        if (originElementIds.length > 0) {
            sceneElementPortMapper.list(new HumanCondition()
                    .select("id", "scene", "port_id")
                    .filterIn("scene_element", originElementIds))
                    .forEach(elementPort -> existElementPortMap.put(elementPort.getPortId(), elementPort));
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

            var existElementPort = existElementPortMap.getOrDefault(portId, null);
            if (existElementPort == null) {
                diffElementPorts.getAddingTargets().add(elementPort);
            } else {
                existElementPortMap.remove(portId);
                elementPort.setId(existElementPort.getId());
                diffElementPorts.getUpdatingTargets().add(elementPort);
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

        Map<String, P0> outerElementPortMap = new HashMap<>();
        if (!outerConnectionPortIds.isEmpty()) {
            sceneElementPortMapper.list(new HumanCondition()
                    .select("id", "port_id")
                    .filterIn("port_id", outerConnectionPortIds.toArray(new String[0])))
                    .forEach(elementPort -> outerElementPortMap.put(elementPort.getPortId(), elementPort));
        }

        String sceneId = scene.getEid();
        DiffTargets<C0> diffElementPortConnections = new DiffTargets<>();
        Map<String, C0> elementPortConnectionObjMap = new HashMap<>();
        this.elementPortConnectionMap.forEach((connectionKey, elementPortConnectionBO) -> {
            C0 elementPortConnection = elementPortConnectionBO.toModel();
            elementPortConnection.setScene(sceneId);
            var sourcePortBO = elementPortMap.getOrDefault(elementPortConnection.getSourcePortId(), null);
            String sourcePortId;
            if (sourcePortBO == null) {
                P0 sourcePort = outerElementPortMap.getOrDefault(elementPortConnection.getSourcePortId(), null);
                sourcePortId = sourcePort == null ? null : sourcePort.getId();
            } else {
                sourcePortId = sourcePortBO.getEid();
            }
            var targetPortBO = elementPortMap.getOrDefault(elementPortConnection.getTargetPortId(), null);
            String targetPortId;
            if (targetPortBO == null) {
                P0 targetPort = outerElementPortMap.getOrDefault(elementPortConnection.getTargetPortId(), null);
                targetPortId = targetPort == null ? null : targetPort.getId();
            } else {
                targetPortId = targetPortBO.getEid();
            }
            if (sourcePortId == null || targetPortId == null) {
                return;
            }
            elementPortConnection.setSourcePort(sourcePortId);
            elementPortConnection.setTargetPort(targetPortId);
            elementPortConnectionObjMap.put(connectionKey, elementPortConnection);

            var existElementPortConnection = existElementPortConnectionMap.getOrDefault(connectionKey, null);
            if (existElementPortConnection == null) {
                diffElementPortConnections.getAddingTargets().add(elementPortConnection);
            } else {
                existElementPortConnectionMap.remove(connectionKey);
                elementPortConnection.setId(existElementPortConnection.getId());
                diffElementPortConnections.getUpdatingTargets().add(elementPortConnection);
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

    public abstract D buildDataBO();

    public abstract T buildTopologyBO();

    public void init(D sceneDataBO, boolean isAreaMode) {
        this.isAreaMode = isAreaMode;
        BeanUtils.loadBeanFields(this);
        fromData(sceneDataBO);
    }

    public D toData() {
        var topologyBO = buildTopologyBO();
        topologyBO.setScene(scene);
        topologyBO.setAreaId(areaId);
        topologyBO.setAreas(new ArrayList<>(areaMap.values()));
        topologyBO.setElements(new ArrayList<>(elementMap.values()));
        topologyBO.setConnections(new ArrayList<>(elementPortConnectionMap.values()));

        var dataBO = buildDataBO();
        dataBO.setTopology(topologyBO);
        return dataBO;
    }

    public void save() {
        saveScene();
        saveSceneAreas();
        DiffTargets<E0> diffElements = saveSceneElements();
        DiffTargets<P0> diffElementPorts = saveSceneElementPorts(diffElements);
        DiffTargets<C0> diffElementPortConnections = saveSceneElementPortConnections(diffElementPorts);
    }

}
