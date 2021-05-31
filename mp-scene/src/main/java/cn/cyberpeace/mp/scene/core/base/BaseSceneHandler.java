package cn.cyberpeace.mp.scene.core.base;

import cn.cyberpeace.mp.base.annotation.BeanFieldNames;
import cn.cyberpeace.mp.base.annotation.ClassFieldNames;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.ListTreeNode;
import cn.cyberpeace.mp.scene.core.base.stereotype.PluginKey;
import cn.cyberpeace.mp.scene.core.bo.base.*;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
import cn.cyberpeace.mp.scene.model.base.*;
import cn.cyberpeace.mp.scene.repository.base.*;
import lombok.Getter;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@ClassFieldNames({
        "sceneDataBOClass",
        "sceneTopologyBOClass",
        "sceneBOClass",
        "sceneAreaBOClass",
        "sceneElementBOClass",
        "sceneElementPortBOClass",
        "sceneElementPortConnectionBOClass",
        "sceneModelClass",
        "sceneAreaModelClass",
        "sceneElementModelClass",
        "sceneElementPortModelClass",
        "sceneElementPortConnectionModelClass",
        "scenePluginHandlerInterface",
})
@BeanFieldNames({
        "sceneMapper",
        "sceneAreaMapper",
        "sceneElementMapper",
        "sceneElementPortMapper",
        "sceneElementPortConnectionMapper",
})
@Getter
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
        M5 extends BaseSceneElementPortConnectionMapper<C0>,
        P1 extends BaseScenePluginHandler> {

    private boolean isAreaMode;
    private S scene;
    private String areaId;
    private final Map<String, A> areaMap = new HashMap<>();
    private final Map<String, E> elementMap = new HashMap<>();
    private final Map<String, P> elementPortMap = new HashMap<>();
    private final Map<String, E> elementPortIdElementMap = new HashMap<>();
    private final Map<String, C> elementPortConnectionMap = new HashMap<>();
    private final Set<String> outerConnectionPortIds = new HashSet<>();

    private Class<D> sceneDataBOClass;
    private Class<T> sceneTopologyBOClass;
    private Class<S> sceneBOClass;
    private Class<A> sceneAreaBOClass;
    private Class<E> sceneElementBOClass;
    private Class<P> sceneElementPortBOClass;
    private Class<C> sceneElementPortConnectionBOClass;

    private Class<S0> sceneModelClass;
    private Class<A0> sceneAreaModelClass;
    private Class<E0> sceneElementModelClass;
    private Class<P0> sceneElementPortModelClass;
    private Class<C0> sceneElementPortConnectionModelClass;

    private M1 sceneMapper;
    private M2 sceneAreaMapper;
    private M3 sceneElementMapper;
    private M4 sceneElementPortMapper;
    private M5 sceneElementPortConnectionMapper;

    private Class<P1> scenePluginHandlerInterface;

    private Map<String, P1> pluginMap;

    @SuppressWarnings("unchecked")
    private void initPluginMap() {
        Map<String, P1> pluginKeyMap = new HashMap<>();
        BeanUtils.getBeanType(scenePluginHandlerInterface).forEach((name, pluginInstance) -> {
            Class<?> pluginClass = pluginInstance.getClass();
            var pluginKeyAnnotation = pluginClass.getAnnotation(PluginKey.class);
            if (pluginKeyAnnotation == null) {
                return;
            }
            pluginKeyMap.put(pluginKeyAnnotation.value(), (P1) ClassUtils.createInstance(pluginClass));
        });
        pluginMap = pluginKeyMap;
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
        if (!isAreaMode) {
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
        if (isAreaMode) {
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

    private void loadAreas(List<A0> areas) {
        if (areas == null) {
            return;
        }

        areas.forEach(area -> {
            var areaBO = ClassUtils.createInstance(sceneAreaBOClass);
            areaBO.fromModel(area);
            areaMap.put(areaBO.getId(), areaBO);
        });
    }

    private void loadElements(List<E0> elements) {
        if (elements == null) {
            return;
        }
        elements.forEach(element -> {
            var elementBO = ClassUtils.createInstance(sceneElementBOClass);
            elementBO.fromModel(element);
            elementMap.put(elementBO.getId(), elementBO);
        });
    }

    private void loadElementPorts(List<P0> elementPorts) {
        if (elementPorts == null) {
            return;
        }
        elementPorts.forEach(elementPort -> {
            var elementBO = elementMap.getOrDefault(elementPort.getSceneElement(), null);
            if (elementBO == null) {
                return;
            }

            var elementPortBO = ClassUtils.createInstance(sceneElementPortBOClass);
            elementPortBO.fromModel(elementPort);
            elementPortMap.put(elementPortBO.getId(), elementPortBO);
            elementPortIdElementMap.put(elementPortBO.getId(), elementBO);
        });
    }

    private void loadConnections(List<C0> connections) {
        if (connections == null) {
            return;
        }

        connections.forEach(connection -> {
            String sourcePortId = connection.getSourcePortId();
            String targetPortId = connection.getTargetPortId();
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

            var elementPortConnectionBO = ClassUtils.createInstance(sceneElementPortConnectionBOClass);
            elementPortConnectionBO.fromModel(connection);
            elementPortConnectionMap.put(elementPortConnectionKey(elementPortConnectionBO), elementPortConnectionBO);
        });
    }

    private S0 readScene(String sceneId) {
        return sceneMapper.logicGet(sceneId);
    }

    private List<A0> readAreas(String... areaIds) {
        var condition = new HumanCondition().filterEqual(BaseSceneAreaModel::getScene, scene.getEid());
        if (areaIds.length > 0) {
            condition.filterIn(BaseSceneAreaModel::getArea, areaIds);
        }
        List<A0> areas = sceneAreaMapper.list(condition);

        if (isAreaMode) {
            String[] openAreaIds = areas.stream()
                    .filter(area -> Boolean.TRUE.equals(area.getOpen()))
                    .map(BaseSceneNodeModel::getNodeId)
                    .toArray(String[]::new);
            if (openAreaIds.length > 0) {
                areas.addAll(readAreas(openAreaIds));
            }
        }

        return areas;
    }

    private List<E0> readElements(String... areaIds) {
        List<E0> elements;
        var condition = new HumanCondition()
                .filterEqual(BaseSceneElementModel::getScene, scene.getEid());
        if (areaIds.length > 0) {
            condition.filterIn(BaseSceneElementModel::getArea, areaIds);
        }
        elements = sceneElementMapper.list(condition);
        return elements;
    }

    private List<P0> readElementPorts(String... areaIds) {
        var condition = new HumanCondition()
                .link(sceneElementPortModelClass, sceneElementModelClass)
                .filterLink(sceneElementPortModelClass, BaseSceneElementPortModel::getSceneElement,
                        sceneElementModelClass, BaseSceneElementModel::getId)
                .currentLink(sceneElementModelClass)
                .filterEqual(BaseSceneElementModel::getScene, scene.getEid());
        if (areaIds.length > 0) {
            condition.currentLink(sceneElementModelClass)
                    .filterIn(BaseSceneElementModel::getArea, areaIds);
        }
        return sceneElementPortMapper.list(condition);
    }

    private List<C0> readElementPortConnections(String... areaIds) {
        var condition = new HumanCondition()
                .link(sceneElementPortConnectionModelClass, sceneElementPortModelClass, sceneElementModelClass)
                .filterOrStart()
                .orLink(sceneElementPortConnectionModelClass, BaseSceneElementPortConnectionModel::getSourcePort,
                        sceneElementPortModelClass, BaseSceneElementPortModel::getId)
                .orLink(sceneElementPortConnectionModelClass, BaseSceneElementPortConnectionModel::getTargetPort,
                        sceneElementPortModelClass, BaseSceneElementPortModel::getId)
                .filterOrEnd()
                .filterLink(sceneElementPortModelClass, BaseSceneElementPortModel::getSceneElement,
                        sceneElementModelClass, BaseSceneElementModel::getId)
                .currentLink(sceneElementPortConnectionModelClass)
                .filterEqual(BaseSceneElementPortConnectionModel::getScene, scene.getEid());
        if (areaIds.length > 0) {
            condition.currentLink(sceneElementModelClass)
                    .filterIn(BaseSceneElementModel::getArea, areaIds);
        }
        return sceneElementPortConnectionMapper.list(condition);
    }

    private <M extends BaseModel> DiffTargets<M> diffTargets(Map<String, M> targetMap,
                                                             Map<String, M> existTargetMap) {
        var diff = new DiffTargets<M>();
        targetMap.forEach((key, target) -> {
            var existTarget = existTargetMap.getOrDefault(key, null);
            if (existTarget == null) {
                diff.getAddingTargets().add(target);
            } else {
                existTargetMap.remove(key);
                target.setId(existTarget.getId());
                diff.getUpdatingTargets().add(target);
            }
        });
        existTargetMap.forEach((key, node) -> diff.getRemovingTargets().add(node));

        return diff;
    }

    private <M extends BaseModel, O extends BaseModelMapper<M>> void saveDiff(O modelMapper, DiffTargets<M> diff) {
        String[] removingTargetIds = diff.getRemovingTargets().stream()
                .map(BaseModel::getId)
                .toArray(String[]::new);
        modelMapper.bulkLogicDelete(removingTargetIds);
        modelMapper.bulkCreate(diff.getAddingTargets());
        modelMapper.bulkUpdate(diff.getUpdatingTargets());
    }

    private void saveScene() {
        S0 sceneObj = scene.toModel();
        sceneMapper.save(sceneObj);
        scene.fromModel(sceneObj);
    }

    private DiffTargets<A0> saveAreas(Map<String, A0> existAreaMap) {
        String sceneId = scene.getEid();
        Map<String, A0> savingAreaMap = areaMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    A0 area = entry.getValue().toModel();
                    area.setScene(sceneId);
                    return area;
                }));
        DiffTargets<A0> diff = diffTargets(savingAreaMap, existAreaMap);
        saveDiff(sceneAreaMapper, diff);
        ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets())
                .forEach(area -> areaMap.get(area.getNodeId()).fromModel(area));
        return diff;
    }

    private DiffTargets<E0> saveElements(Map<String, E0> existElementMap) {
        String sceneId = scene.getEid();
        Map<String, E0> savingElementMap = elementMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    E0 element = entry.getValue().toModel();
                    element.setScene(sceneId);
                    return element;
                }));
        DiffTargets<E0> diff = diffTargets(savingElementMap, existElementMap);
        saveDiff(sceneElementMapper, diff);
        ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets())
                .forEach(element -> elementMap.get(element.getNodeId()).fromModel(element));
        return diff;
    }

    private DiffTargets<P0> saveElementPorts(Map<String, P0> existElementPortMap) {
        String sceneId = scene.getEid();
        Map<String, P0> savingElementPortMap = new HashMap<>();
        elementMap.values().forEach(element -> {
            List<P> elementPorts = element.getPorts();
            if (elementPorts == null) {
                return;
            }

            String elementId = element.getEid();
            elementPorts.forEach(elementPortBO -> {
                P0 elementPort = elementPortBO.toModel();
                elementPort.setScene(sceneId);
                elementPort.setSceneElement(elementId);
                savingElementPortMap.put(elementPort.getPortId(), elementPort);
            });
        });

        DiffTargets<P0> diff = diffTargets(savingElementPortMap, existElementPortMap);
        saveDiff(sceneElementPortMapper, diff);
        ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets())
                .forEach(elementPort -> elementPortMap.get(elementPort.getPortId()).fromModel(elementPort));
        return diff;
    }

    private DiffTargets<C0> saveElementPortConnections(Map<String, C0> existElementPortConnectionMap) {
        Map<String, P0> outerElementPortMap = new HashMap<>();
        if (!outerConnectionPortIds.isEmpty()) {
            sceneElementPortMapper.list(new HumanCondition()
                    .select("id", "portId")
                    .filterIn("portId", outerConnectionPortIds.toArray(new String[0])))
                    .forEach(elementPort -> outerElementPortMap.put(elementPort.getPortId(), elementPort));
        }
        String sceneId = scene.getEid();
        Map<String, C0> savingElementPortConnectionMap = new HashMap<>();
        elementPortConnectionMap.forEach((key, elementPortConnectionBO) -> {
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

            savingElementPortConnectionMap.put(key, elementPortConnection);
        });

        DiffTargets<C0> diff = diffTargets(savingElementPortConnectionMap, existElementPortConnectionMap);
        saveDiff(sceneElementPortConnectionMapper, diff);
        ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets())
                .forEach(elementPortConnection -> elementPortConnectionMap
                        .get(elementPortConnectionKey(elementPortConnection)).fromModel(elementPortConnection));
        return diff;
    }

    private void fromData(D sceneDataBO) {
        var topology = sceneDataBO.getTopology();
        scene = topology.getScene();
        areaId = topology.getAreaId();
        loadAreaBOs(topology.getAreas());
        loadElementBOs(topology.getElements());
        loadConnectionBOs(topology.getConnections());
    }

    public void init(D sceneDataBO, boolean isAreaMode) throws SceneNotExistException {
        ClassUtils.loadClassFields(this);
        BeanUtils.loadBeanFields(this);
        this.isAreaMode = isAreaMode;
        fromData(sceneDataBO);
        String sceneId = scene.getEid();
        if (!StringUtils.isEmpty(sceneId) && readScene(sceneId) == null) {
            throw new SceneNotExistException("Scene " + sceneId + " not exist");
        }
        initPluginMap();
    }

    public void init(String sceneId, String areaId, boolean isAreaMode) throws SceneNotExistException {
        ClassUtils.loadClassFields(this);
        BeanUtils.loadBeanFields(this);
        S0 sceneObj = readScene(sceneId);
        if (sceneObj == null) {
            throw new SceneNotExistException("Scene " + sceneId + " not exist");
        }
        var sceneBO = ClassUtils.createInstance(sceneBOClass);
        sceneBO.fromModel(sceneObj);
        this.scene = sceneBO;
        this.areaId = areaId;
        this.isAreaMode = isAreaMode;
        reload();
        initPluginMap();
    }

    public D toData() {
        var topologyBO = ClassUtils.createInstance(sceneTopologyBOClass);
        topologyBO.setScene(scene);
        topologyBO.setAreaId(areaId);
        topologyBO.setAreas(new ArrayList<>(areaMap.values()));
        topologyBO.setElements(new ArrayList<>(elementMap.values()));
        topologyBO.setConnections(new ArrayList<>(elementPortConnectionMap.values()));

        var dataBO = ClassUtils.createInstance(sceneDataBOClass);
        dataBO.setTopology(topologyBO);
        return dataBO;
    }

    public void reload() {
        areaMap.clear();
        elementMap.clear();
        elementPortMap.clear();
        elementPortIdElementMap.clear();
        elementPortConnectionMap.clear();

        loadAreas(readAreas());
        var filterAreaIds = new String[0];
        if (isAreaMode) {
            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            filterAreaIds = ArrayUtils.addAll(areaMap.keySet().toArray(new String[0]), currentAreaId);
        }
        loadElements(readElements(filterAreaIds));
        loadElementPorts(readElementPorts(filterAreaIds));
        loadConnections(readElementPortConnections(filterAreaIds));
    }

    public void save() {
        Map<String, A0> existAreaMap;
        Map<String, E0> existElementMap;
        Map<String, P0> existElementPortMap;
        Map<String, C0> existElementPortConnectionMap;

        if (!StringUtils.isEmpty(scene.getEid())) {
            existAreaMap = readAreas().stream()
                    .collect(Collectors.toMap(BaseSceneAreaModel::getNodeId, x -> x));
            var filterAreaIds = new String[0];
            if (isAreaMode) {
                String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
                filterAreaIds = ArrayUtils.addAll(existAreaMap.keySet().toArray(new String[0]), currentAreaId);
            }
            existElementMap = readElements(filterAreaIds).stream()
                    .collect(Collectors.toMap(BaseSceneElementModel::getNodeId, x -> x));
            existElementPortMap = readElementPorts(filterAreaIds).stream()
                    .collect(Collectors.toMap(BaseSceneElementPortModel::getPortId, x -> x));
            existElementPortConnectionMap = readElementPortConnections(filterAreaIds).stream()
                    .collect(Collectors.toMap(this::elementPortConnectionKey, x -> x));
        } else {
            existAreaMap = new HashMap<>();
            existElementMap = new HashMap<>();
            existElementPortMap = new HashMap<>();
            existElementPortConnectionMap = new HashMap<>();
        }

        saveScene();
        DiffTargets<A0> diffAreas = saveAreas(existAreaMap);
        DiffTargets<E0> diffElements = saveElements(existElementMap);
        DiffTargets<P0> diffElementPorts = saveElementPorts(existElementPortMap);
        DiffTargets<C0> diffElementPortConnections = saveElementPortConnections(existElementPortConnectionMap);
    }

    public void delete() {
        String sceneId = scene.getEid();
//        sceneMapper.logicDelete(sceneId);

        List<A0> areas = readAreas();
        var filterAreaIds = new String[0];
        if (isAreaMode) {
            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            filterAreaIds = ArrayUtils.addAll(areas.stream()
                    .map(area -> area.getNodeId()).toArray(String[]::new), currentAreaId);
        }
        List<E0> elements = readElements(filterAreaIds);
        List<P0> elementPorts = readElementPorts(filterAreaIds);
        List<C0> elementPortConnections = readElementPortConnections(filterAreaIds);

        sceneMapper.delete(sceneId);
        if (!areas.isEmpty()) {
            sceneAreaMapper.bulkDelete(areas.stream().map(BaseModel::getId).toArray(String[]::new));
        }
        if (!elements.isEmpty()) {
            sceneElementMapper.bulkDelete(elements.stream().map(BaseModel::getId).toArray(String[]::new));
        }
        if (!elementPorts.isEmpty()) {
            sceneElementPortMapper.bulkDelete(elementPorts.stream().map(BaseModel::getId).toArray(String[]::new));
        }
        if (!elementPortConnections.isEmpty()) {
            sceneElementPortConnectionMapper.bulkDelete(elementPortConnections.stream().map(BaseModel::getId).toArray(String[]::new));
        }
    }
}
