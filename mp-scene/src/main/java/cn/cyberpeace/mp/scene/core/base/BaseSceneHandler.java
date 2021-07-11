package cn.cyberpeace.mp.scene.core.base;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.ValidationCommon;
import cn.cyberpeace.mp.base.common.stereotype.BeanFieldNames;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.basic.MapUtils;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldNames;
import cn.cyberpeace.mp.base.util.define.ListTreeNode;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.base.*;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
import cn.cyberpeace.mp.scene.core.exception.SceneUpdateException;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortConnectionModel;
import cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel;
import cn.cyberpeace.mp.scene.model.SceneElementPortModel;
import cn.cyberpeace.mp.scene.model.base.*;
import cn.cyberpeace.mp.scene.repository.base.*;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;


@ClassFieldNames({
        "sceneDataBOClass",
        "sceneTopologyBOClass",
        "sceneBOClass",
        "sceneAreaBOClass",
        "sceneElementBOClass",
        "sceneElementPortBOClass",
        "sceneElementPortConnectionBOClass",
        "scenePieceBOClass",
        "sceneModelClass",
        "sceneAreaModelClass",
        "sceneElementModelClass",
        "sceneElementPortModelClass",
        "sceneElementPortConnectionModelClass",
        "sceneElementPluginModelClass",
        "scenePieceModelClass",
        "scenePluginHandlerInterface",
        "sceneHandlerClass",
})
@BeanFieldNames({
        "sceneMapper",
        "sceneAreaMapper",
        "sceneElementMapper",
        "sceneElementPortMapper",
        "sceneElementPortConnectionMapper",
        "sceneElementPluginMapper",
        "scenePieceMapper",
})
@Getter
public abstract class BaseSceneHandler<
        S0 extends BaseSceneModel,
        A0 extends BaseSceneAreaModel,
        E0 extends BaseSceneElementModel,
        P0 extends BaseSceneElementPortModel,
        C0 extends BaseSceneElementPortConnectionModel,
        P1 extends BaseSceneElementPluginModel,
        P3 extends BaseScenePieceModel,
        S extends BaseSceneBO<S0, S>,
        A extends BaseSceneAreaBO<A0, A>,
        P extends BaseSceneElementPortBO<P0, P, E>,
        E extends BaseSceneElementBO<E0, P, E>,
        C extends BaseSceneElementPortConnectionBO<C0, C>,
        P4 extends BaseScenePieceBO<P3, P4>,
        T extends BaseSceneTopologyBO<S0, A0, E0, P0, C0, P3, S, A, P, E, C, P4, T>,
        D extends BaseSceneDataBO<S0, A0, E0, P0, C0, P3, S, A, P, E, C, P4, T, D>,
        M1 extends BaseSceneMapper<S0>,
        M2 extends BaseSceneAreaMapper<A0>,
        M3 extends BaseSceneElementMapper<E0>,
        M4 extends BaseSceneElementPortMapper<P0>,
        M5 extends BaseSceneElementPortConnectionMapper<C0>,
        M6 extends BaseSceneElementPluginMapper<P1>,
        M7 extends BaseScenePieceMapper<P3>,
        C2 extends BaseSceneContextBO,
        P2 extends BasePluginHandler<?, ?, ?, ?>,
        H extends BaseSceneHandler<S0, A0, E0, P0, C0, P1, P3,
                S, A, P, E, C, P4, T, D,
                M1, M2, M3, M4, M5, M6, M7, C2, P2, H>> {

    
    private C2 context;
    
    private boolean isAreaMode;
    
    private String sceneId;
    
    private String areaId;
    
    private S scene;
    
    private final Map<String, P4> pieceMap = new ConcurrentHashMap<>();
    
    private final Map<String, A> areaMap = new ConcurrentHashMap<>();
    
    private final Map<String, E> elementMap = new ConcurrentHashMap<>();
    
    private final Map<String, P> elementPortMap = new ConcurrentHashMap<>();
    
    private final Map<String, C> elementPortConnectionMap = new ConcurrentHashMap<>();
    
    private final Set<String> outerConnectionPortIds = new CopyOnWriteArraySet<>();
    
    private final Map<String, ScenePluginDataBO> scenePluginData = new ConcurrentHashMap<>();

    
    
    private final Map<String, E> portIdElementMap = new ConcurrentHashMap<>();
    
    private final Map<String, String> portIdConnectedPortIdMap = new ConcurrentHashMap<>();

    
    private Class<D> sceneDataBOClass;
    private Class<T> sceneTopologyBOClass;
    private Class<S> sceneBOClass;
    private Class<A> sceneAreaBOClass;
    private Class<E> sceneElementBOClass;
    private Class<P> sceneElementPortBOClass;
    private Class<C> sceneElementPortConnectionBOClass;
    private Class<P4> scenePieceBOClass;
    private Class<S0> sceneModelClass;
    private Class<A0> sceneAreaModelClass;
    private Class<E0> sceneElementModelClass;
    private Class<P0> sceneElementPortModelClass;
    private Class<C0> sceneElementPortConnectionModelClass;
    private Class<P1> sceneElementPluginModelClass;
    private Class<P3> scenePieceModelClass;
    private Class<P2> scenePluginHandlerInterface;
    private Class<H> sceneHandlerClass;

    
    private M1 sceneMapper;
    private M2 sceneAreaMapper;
    private M3 sceneElementMapper;
    private M4 sceneElementPortMapper;
    private M5 sceneElementPortConnectionMapper;
    private M6 sceneElementPluginMapper;
    private M7 scenePieceMapper;

    
    private final List<P2> plugins = new CopyOnWriteArrayList<>();
    private final Map<String, P2> pluginMap = new ConcurrentHashMap<>();
    
    private final Map<String, List<P1>> elementIdPluginsMap = new ConcurrentHashMap<>();

    private Object getPluginContext(String pluginKey) {
        if (context == null) {
            return null;
        }

        var pluginsContexts = context.getPlugins();
        if (pluginsContexts == null) {
            return null;
        }

        return pluginsContexts.getOrDefault(pluginKey, null);
    }

    
    @SuppressWarnings("unchecked")
    private void initPlugins(Map<String, ScenePluginDataBO> scenePluginData) {
        plugins.clear();
        pluginMap.clear();
        BeanCommon.getBeanType(scenePluginHandlerInterface).forEach((name, pluginInstance) -> {
            Class<?> pluginClass = pluginInstance.getClass();
            var pluginKeyAnnotation = pluginClass.getAnnotation(PluginKey.class);
            if (pluginKeyAnnotation == null) {
                return;
            }

            String pluginKey = pluginKeyAnnotation.value();
            ScenePluginDataBO pluginData = scenePluginData.getOrDefault(pluginKey, null);
            if (pluginData == null) {
                return;
            }

            P2 plugin = (P2) ClassUtils.createInstance(pluginClass);
            beforeInitPlugin(plugin);
            plugin.init(sceneId, pluginData, getPluginContext(pluginKey));
            afterInitPlugin(plugin);
            plugins.add(plugin);
            pluginMap.put(pluginKey, plugin);
        });
        plugins.sort(Comparator.comparing(BasePluginHandler::getPriority));
    }

    
    @SuppressWarnings("unchecked")
    private void initPlugins(Map<String, List<String>> pluginKeyElementIds,
                             Map<String, List<String>> pluginKeyElementPortIds) {
        plugins.clear();
        pluginMap.clear();
        BeanCommon.getBeanType(scenePluginHandlerInterface).forEach((name, pluginInstance) -> {
            Class<?> pluginClass = pluginInstance.getClass();
            var pluginKeyAnnotation = pluginClass.getAnnotation(PluginKey.class);
            if (pluginKeyAnnotation == null) {
                return;
            }

            String pluginKey = pluginKeyAnnotation.value();
            List<String> elementIds = pluginKeyElementIds.getOrDefault(pluginKey, null);
            List<String> elementPortIds = pluginKeyElementPortIds.getOrDefault(pluginKey, null);
            P2 plugin = (P2) ClassUtils.createInstance(pluginClass);
            beforeInitPlugin(plugin);
            plugin.init(sceneId, elementIds, elementPortIds, getPluginContext(pluginKey));
            afterInitPlugin(plugin);
            if (elementIds == null && elementPortIds == null && plugin.getScene() == null) {
                return;
            }
            scenePluginData.put(pluginKey, plugin.toData());
            plugins.add(plugin);
            pluginMap.put(pluginKey, plugin);
        });
        plugins.sort(Comparator.comparing(BasePluginHandler::getPriority));
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

    
    private <N extends BaseSceneNodeBO<?, N>> boolean isValidAreaNode(N node) {
        if (!isAreaMode) {
            return true;
        }

        String nodeArea = node.getArea();
        if (StringUtils.isEmpty(areaId) && StringUtils.isEmpty(nodeArea)) {
            return true;
        }

        if (StringUtils.equals(areaId, nodeArea)) {
            return true;
        }

        
        var parentArea = nodeArea != null ? areaMap.getOrDefault(nodeArea, null) : null;
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

    
    private String elementPluginKey(String sceneElement, String pluginKey) {
        return StringUtils.joinWith(":", sceneElement, pluginKey);
    }

    
    private String elementPluginKey(P1 elementPlugin) {
        return elementPluginKey(elementPlugin.getSceneElement(), elementPlugin.getPluginKey());
    }

    
    private void loadSceneBO(S scene) {
        if (scene == null) {
            return;
        }

        this.scene = scene;
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

    
    private void loadPieceBOs(List<P4> pieces) {
        if (pieces == null) {
            return;
        }

        pieces.forEach(piece -> {
            if (!isValidAreaNode(piece)) {
                return;
            }

            pieceMap.put(piece.getId(), piece);
        });
    }

    
    private void loadElementBOs(List<E> elements) {
        if (elements == null) {
            return;
        }

        elements.forEach(element -> {
            if (!isValidAreaNode(element)) {
                return;
            }

            elementMap.put(element.getId(), element);

            List<P> elementPorts = element.getPorts();
            if (elementPorts == null) {
                return;
            }
            elementPorts.forEach(elementPort -> {
                elementPort.setElement(element);
                elementPortMap.put(elementPort.getId(), elementPort);
                portIdElementMap.put(elementPort.getId(), element);
            });
        });
    }

    
    private void loadConnectionBOs(List<C> connections) {
        if (connections == null) {
            return;
        }

        Map<String, List<String>> outerConnectionPortIdConnectionsMap = new HashMap<>();
        Map<String, C> validConnectionMap = new HashMap<>();
        connections.forEach(connection -> {
            String sourcePortId = connection.getSource();
            String targetPortId = connection.getTarget();
            if (!elementPortMap.containsKey(sourcePortId)
                    && !elementPortMap.containsKey(targetPortId)) {
                return;
            }

            var connectionKey = elementPortConnectionKey(connection);
            if (!elementPortMap.containsKey(sourcePortId)) {
                MapUtils.setDefault(outerConnectionPortIdConnectionsMap, sourcePortId, new ArrayList<>())
                        .add(connectionKey);
            }
            if (!elementPortMap.containsKey(targetPortId)) {
                MapUtils.setDefault(outerConnectionPortIdConnectionsMap, targetPortId, new ArrayList<>())
                        .add(connectionKey);
            }
            validConnectionMap.put(connectionKey, connection);
        });

        
        if (!outerConnectionPortIdConnectionsMap.isEmpty()) {
            Set<String> currentOuterConnectionPortIds = outerConnectionPortIdConnectionsMap.keySet();
            if (!StringUtils.isEmpty(sceneId)) {
                Set<String> existOuterConnectionPortIds = new HashSet<>();
                sceneElementPortMapper.list(new HumanCondition()
                        .select(BaseSceneElementPortModel::getPortId)
                        .filterEqual(BaseSceneElementPortModel::getScene, sceneId)
                        .filterIn(BaseSceneElementPortModel::getPortId,
                                outerConnectionPortIdConnectionsMap.keySet().toArray(new String[0])))
                        .forEach(elementPort -> existOuterConnectionPortIds.add(elementPort.getPortId()));
                currentOuterConnectionPortIds.removeAll(existOuterConnectionPortIds);
            }
            currentOuterConnectionPortIds.forEach(notExistOuterConnectionPortId -> {
                outerConnectionPortIdConnectionsMap.get(notExistOuterConnectionPortId)
                        .forEach(validConnectionMap::remove);
            });
        }

        validConnectionMap.forEach((key, connection) -> {
            elementPortConnectionMap.put(key, connection);
            portIdConnectedPortIdMap.put(connection.getSource(), connection.getTarget());
            portIdConnectedPortIdMap.put(connection.getTarget(), connection.getSource());
        });
    }

    
    private void loadScenePluginData(Map<String, ScenePluginDataBO> scenePluginData) {
        if (scenePluginData == null) {
            return;
        }
        scenePluginData.forEach(this.scenePluginData::put);
    }

    
    private void loadScene(S0 scene) {
        if (scene == null) {
            return;
        }

        var sceneBO = ClassUtils.createInstance(sceneBOClass);
        sceneBO.fromModel(scene);
        this.scene = sceneBO;
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

    
    private void loadPieces(List<P3> pieces) {
        if (pieces == null) {
            return;
        }

        pieces.forEach(piece -> {
            var pieceBO = ClassUtils.createInstance(scenePieceBOClass);
            pieceBO.fromModel(piece);
            pieceMap.put(pieceBO.getId(), pieceBO);
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
        Map<String, E> elementIdElementBOMap = elementMap.values().stream().collect(
                Collectors.toMap(EidBO::getEid, element -> element));
        Collections.sort(elementPorts, Comparator.comparingInt(BaseSceneElementPortModel::getOrder));
        elementPorts.forEach(elementPort -> {
            var elementBO = elementIdElementBOMap.getOrDefault(elementPort.getSceneElement(), null);
            if (elementBO == null) {
                return;
            }

            var elementPortBO = ClassUtils.createInstance(sceneElementPortBOClass);
            elementPortBO.fromModel(elementPort);
            elementPortBO.setElement(elementBO);
            elementPortMap.put(elementPortBO.getId(), elementPortBO);
            portIdElementMap.put(elementPortBO.getId(), elementBO);

            if (elementBO.getPorts() == null) {
                elementBO.setPorts(new ArrayList<>());
            }
            elementBO.getPorts().add(elementPortBO);
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
            portIdConnectedPortIdMap.put(elementPortConnectionBO.getSource(), elementPortConnectionBO.getTarget());
            portIdConnectedPortIdMap.put(elementPortConnectionBO.getTarget(), elementPortConnectionBO.getSource());
        });
    }

    
    private void loadElementPlugins(List<P1> elementPlugins) {
        elementPlugins.forEach(elementPlugin -> {
            String elementId = elementPlugin.getSceneElement();
            List<P1> plugins = elementIdPluginsMap.getOrDefault(elementId, new ArrayList<>());
            plugins.add(elementPlugin);
            elementIdPluginsMap.put(elementId, plugins);
        });
    }

    
    private S0 readScene() {
        return sceneMapper.get(sceneId);
    }

    
    private List<A0> readAreas(String... areaIds) {
        var condition = new HumanCondition().filterEqual(BaseSceneAreaModel::getScene, sceneId);

        String[] filterAreaIds = areaIds;
        if (filterAreaIds.length == 0 && isAreaMode) {
            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            filterAreaIds = new String[]{currentAreaId};
        }
        if (filterAreaIds.length > 0) {
            condition.filterIn(BaseSceneAreaModel::getArea, filterAreaIds);
        }

        List<A0> areas = sceneAreaMapper.list(condition);

        if (isAreaMode) {
            String[] openAreaIds = areas.stream()
                    .filter(BaseSceneAreaModel::getOpen)
                    .map(BaseSceneNodeModel::getNodeId)
                    .toArray(String[]::new);
            if (openAreaIds.length > 0) {
                areas.addAll(readAreas(openAreaIds));
            }
        }

        return areas;
    }

    
    private List<P3> readPieces(String... areaIds) {
        List<P3> pieces;
        var condition = new HumanCondition()
                .filterEqual(BaseScenePieceModel::getScene, sceneId);

        String[] filterAreaIds = areaIds;
        if (filterAreaIds.length == 0 && isAreaMode) {
            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            filterAreaIds = new String[]{currentAreaId};
        }
        if (filterAreaIds.length > 0) {
            condition.filterIn(BaseSceneElementModel::getArea, filterAreaIds);
        }

        pieces = scenePieceMapper.list(condition);
        return pieces;
    }

    
    private List<E0> readElements(String... areaIds) {
        List<E0> elements;
        var condition = new HumanCondition()
                .filterEqual(BaseSceneElementModel::getScene, sceneId);

        String[] filterAreaIds = areaIds;
        if (filterAreaIds.length == 0 && isAreaMode) {
            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            filterAreaIds = new String[]{currentAreaId};
        }
        if (filterAreaIds.length > 0) {
            condition.filterIn(BaseSceneElementModel::getArea, filterAreaIds);
        }

        elements = sceneElementMapper.list(condition);
        return elements;
    }

    
    private List<P0> readElementPorts(String... areaIds) {
        var condition = new HumanCondition();

        String[] filterAreaIds = areaIds;
        if (filterAreaIds.length == 0 && isAreaMode) {
            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            filterAreaIds = new String[]{currentAreaId};
        }
        if (filterAreaIds.length > 0) {
            condition.link(sceneElementPortModelClass, sceneElementModelClass)
                    .filterLink(sceneElementPortModelClass, BaseSceneElementPortModel::getSceneElement,
                            sceneElementModelClass, BaseSceneElementModel::getId)
                    .currentLink(sceneElementPortModelClass)
                    .filterEqual(BaseSceneElementPortModel::getScene, sceneId)
                    .currentLink(sceneElementModelClass)
                    .filterIn(BaseSceneElementModel::getArea, filterAreaIds);
        } else {
            condition.filterEqual(BaseSceneElementPortModel::getScene, sceneId);
        }
        return sceneElementPortMapper.list(condition);
    }

    
    private List<C0> readElementPortConnections(String... areaIds) {
        var condition = new HumanCondition();

        String[] filterAreaIds = areaIds;
        if (filterAreaIds.length == 0 && isAreaMode) {
            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            filterAreaIds = new String[]{currentAreaId};
        }
        if (filterAreaIds.length > 0) {
            condition.link(sceneElementPortConnectionModelClass, sceneElementModelClass)
                    .filterOrStart()
                    .orLink(sceneElementPortConnectionModelClass, BaseSceneElementPortConnectionModel::getSourceElement,
                            sceneElementModelClass, BaseSceneElementModel::getId)
                    .orLink(sceneElementPortConnectionModelClass, BaseSceneElementPortConnectionModel::getTargetElement,
                            sceneElementModelClass, BaseSceneElementModel::getId)
                    .filterOrEnd()
                    .currentLink(sceneElementPortConnectionModelClass)
                    .filterEqual(BaseSceneElementPortConnectionModel::getScene, sceneId)
                    .currentLink(sceneElementModelClass)
                    .filterIn(BaseSceneElementModel::getArea, filterAreaIds)
                    .distinct();
        } else {
            condition.filterEqual(BaseSceneElementPortConnectionModel::getScene, sceneId);
        }
        return sceneElementPortConnectionMapper.list(condition);
    }

    
    private List<P1> readElementPlugins(String... areaIds) {
        var condition = new HumanCondition();

        String[] filterAreaIds = areaIds;
        if (filterAreaIds.length == 0 && isAreaMode) {
            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            filterAreaIds = new String[]{currentAreaId};
        }
        if (filterAreaIds.length > 0) {
            condition.link(sceneElementPluginModelClass, sceneElementModelClass)
                    .filterLink(sceneElementPluginModelClass, BaseSceneElementPluginModel::getSceneElement,
                            sceneElementModelClass, BaseSceneElementModel::getId)
                    .currentLink(sceneElementPluginModelClass)
                    .filterEqual(BaseSceneElementPluginModel::getScene, sceneId)
                    .currentLink(sceneElementModelClass)
                    .filterIn(BaseSceneElementModel::getArea, filterAreaIds);
        } else {
            condition.filterEqual(BaseSceneElementPluginModel::getScene, sceneId);
        }

        return sceneElementPluginMapper.list(condition);
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
        var deleteResult = modelMapper.bulkLogicDelete(removingTargetIds).getSuccessCount();
        if (deleteResult < removingTargetIds.length) {
            throw new SceneUpdateException("Scene diff delete failed");
        }

        var createResult = modelMapper.bulkCreate(diff.getAddingTargets()).getSuccessCount();
        if (createResult < diff.getAddingTargets().size()) {
            throw new SceneUpdateException("Scene diff create failed");
        }

        var updateResult = modelMapper.bulkUpdate(diff.getUpdatingTargets()).getSuccessCount();
        if (updateResult < diff.getUpdatingTargets().size()) {
            throw new SceneUpdateException("Scene diff update failed");
        }
    }

    
    private boolean saveScene() {
        S0 sceneObj = scene.toModel();
        var isCreate = StringUtils.isEmpty(sceneObj.getId());
        var result = sceneMapper.save(sceneObj);
        sceneId = sceneObj.getId();
        if (isCreate) {
            scene.fromModel(sceneObj);
        }

        plugins.forEach(plugin -> plugin.setSourceSceneId(sceneId));

        return result;
    }

    
    private DiffTargets<A0> saveAreas(Map<String, A0> existAreaMap) {
        Map<String, A0> savingAreaMap = areaMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    A0 area = entry.getValue().toModel();
                    area.setScene(sceneId);
                    return area;
                }));
        DiffTargets<A0> diff = diffTargets(savingAreaMap, existAreaMap);
        if (isAreaMode) {
            
            for (var removingArea: diff.getRemovingTargets()) {
                if (!Boolean.TRUE.equals(removingArea.getOpen())) {
                    var sceneHandler = ClassUtils.createInstance(sceneHandlerClass);
                    sceneHandler.init(sceneId, removingArea.getNodeId(), isAreaMode, context);
                    sceneHandler.deleteTopology();
                }
            }
        }

        saveDiff(sceneAreaMapper, diff);
        ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets())
                .forEach(area -> areaMap.get(area.getNodeId()).fromModel(area));
        return diff;
    }

    
    private DiffTargets<P3> savePieces(Map<String, P3> existPieceMap) {
        Map<String, P3> savingPieceMap = pieceMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    P3 piece = entry.getValue().toModel();
                    piece.setScene(sceneId);
                    return piece;
                }));
        DiffTargets<P3> diff = diffTargets(savingPieceMap, existPieceMap);
        saveDiff(scenePieceMapper, diff);
        ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets())
                .forEach(piece -> pieceMap.get(piece.getNodeId()).fromModel(piece));
        return diff;
    }

    
    private DiffTargets<E0> saveElements(Map<String, E0> existElementMap) {
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
        Map<String, P0> savingElementPortMap = new HashMap<>();
        elementMap.values().forEach(element -> {
            List<P> elementPorts = element.getPorts();
            if (elementPorts == null) {
                return;
            }
            for (int i = 0; i < elementPorts.size(); i++) {
                if (elementPorts.get(i).getOrder() == null) {
                    elementPorts.get(i).setOrder(i + 1);
                }
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
                    .select(BaseSceneElementPortModel::getId, BaseSceneElementPortModel::getSceneElement,
                            BaseSceneElementPortModel::getPortId)
                    .filterEqual(BaseSceneElementPortModel::getScene, sceneId)
                    .filterIn(BaseSceneElementPortModel::getPortId, outerConnectionPortIds.toArray(new String[0])))
                    .forEach(elementPort -> outerElementPortMap.put(elementPort.getPortId(), elementPort));
        }

        Map<String, E> portIdElementMap = getPortIdElementMap();
        Map<String, C0> savingElementPortConnectionMap = new HashMap<>();
        elementPortConnectionMap.forEach((key, elementPortConnectionBO) -> {
            C0 elementPortConnection = elementPortConnectionBO.toModel();
            elementPortConnection.setScene(sceneId);
            var sourcePortBO = elementPortMap.getOrDefault(elementPortConnection.getSourcePortId(), null);
            String sourcePortId;
            String sourceElementId;
            if (sourcePortBO == null) {
                P0 sourcePort = outerElementPortMap.getOrDefault(elementPortConnection.getSourcePortId(), null);
                sourcePortId = sourcePort == null ? null : sourcePort.getId();
                sourceElementId = sourcePort == null ? null : sourcePort.getSceneElement();
            } else {
                sourcePortId = sourcePortBO.getEid();
                var sourceElement = portIdElementMap.getOrDefault(sourcePortBO.getId(), null);
                sourceElementId = sourceElement == null ? null : sourceElement.getEid();
            }
            var targetPortBO = elementPortMap.getOrDefault(elementPortConnection.getTargetPortId(), null);
            String targetPortId;
            String targetElementId;
            if (targetPortBO == null) {
                P0 targetPort = outerElementPortMap.getOrDefault(elementPortConnection.getTargetPortId(), null);
                targetPortId = targetPort == null ? null : targetPort.getId();
                targetElementId = targetPort == null ? null : targetPort.getSceneElement();
            } else {
                targetPortId = targetPortBO.getEid();
                var targetElement = portIdElementMap.getOrDefault(targetPortBO.getId(), null);
                targetElementId = targetElement == null ? null : targetElement.getEid();
            }
            if (sourcePortId == null || targetPortId == null) {
                return;
            }
            elementPortConnection.setSourceElement(sourceElementId);
            elementPortConnection.setTargetElement(targetElementId);
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

    
    private DiffTargets<P1> saveElementPlugins(Map<String, P1> existElementPluginMap) {
        Map<String, E> portIdElementMap = getPortIdElementMap();
        Map<String, P1> savingElementPluginMap = new HashMap<>();
        scenePluginData.forEach((pluginKey, pluginData) -> {
            Map<String, Map<String, Object>> pluginElements = pluginData.getElements();
            if (pluginElements != null) {
                pluginElements.forEach((nodeId, nodePluginData) -> {
                    var element = elementMap.getOrDefault(nodeId, null);
                    if (element == null) {
                        return;
                    }

                    String key = elementPluginKey(element.getEid(), pluginKey);
                    if (!savingElementPluginMap.containsKey(key)) {
                        P1 elementPlugin = ClassUtils.createInstance(sceneElementPluginModelClass);
                        elementPlugin.setScene(sceneId);
                        elementPlugin.setSceneElement(element.getEid());
                        elementPlugin.setPluginKey(pluginKey);
                        savingElementPluginMap.put(key, elementPlugin);
                    }
                });
            }

            Map<String, Map<String, Object>> pluginElementPorts = pluginData.getElementPorts();
            if (pluginElementPorts != null) {
                pluginElementPorts.forEach((portId, portPluginData) -> {
                    var element = portIdElementMap.getOrDefault(portId, null);
                    if (element == null) {
                        return;
                    }

                    String key = elementPluginKey(element.getEid(), pluginKey);
                    if (!savingElementPluginMap.containsKey(key)) {
                        P1 elementPlugin = ClassUtils.createInstance(sceneElementPluginModelClass);
                        elementPlugin.setScene(sceneId);
                        elementPlugin.setSceneElement(element.getEid());
                        elementPlugin.setPluginKey(pluginKey);
                        savingElementPluginMap.put(key, elementPlugin);
                    }
                });
            }
        });

        DiffTargets<P1> diff = diffTargets(savingElementPluginMap, existElementPluginMap);
        saveDiff(sceneElementPluginMapper, diff);
        loadElementPlugins(ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets()));
        return diff;
    }

    
    private Map<String, E> getPortIdElementMap() {
        Map<String, E> portIdElementMap = new HashMap<>();
        elementMap.forEach((elementId, elementBO) -> {
            List<P> elementPorts = elementBO.getPorts();
            if (elementPorts == null) {
                return;
            }
            elementPorts.forEach(elementPort -> {
                portIdElementMap.put(elementPort.getId(), elementBO);
            });
        });
        return portIdElementMap;
    }

    
    private void fromData(D sceneDataBO) {
        
        var validateResult = ValidationCommon.validate(sceneDataBO);
        ValidationCommon.handleValidateResult(validateResult);

        var topology = sceneDataBO.getTopology();
        var srcScene = (topology == null || topology.getScene() == null) ? ClassUtils.createInstance(sceneBOClass)
                : topology.getScene();
        loadSceneBO(srcScene);
        sceneId = scene.getEid();

        if (topology != null) {
            loadSceneBO(topology.getScene());
            areaId = topology.getAreaId();
            loadAreaBOs(topology.getAreas());
            loadPieceBOs(topology.getPieces());
            loadElementBOs(topology.getElements());
            loadConnectionBOs(topology.getConnections());
        }

        loadScenePluginData(sceneDataBO.getPlugins());
    }

    
    protected void correctData() {
        plugins.forEach(plugin -> plugin.correctData());
    }

    
    public void init(D sceneDataBO, boolean isAreaMode, C2 context) {
        ClassUtils.loadClassFields(this);
        BeanCommon.loadBeanFields(this);
        this.context = context;
        this.isAreaMode = isAreaMode;
        fromData(sceneDataBO);
        if (!StringUtils.isEmpty(sceneId) && readScene() == null) {
            throw new SceneNotExistException("Scene " + sceneId + " not exist");
        }
        initPlugins(scenePluginData);

        
        correctData();
    }

    
    public void init(String sceneId, String areaId, boolean isAreaMode, C2 context) {
        ClassUtils.loadClassFields(this);
        BeanCommon.loadBeanFields(this);
        this.context = context;
        this.sceneId = sceneId;
        this.areaId = areaId;
        this.isAreaMode = isAreaMode;
        reload();
        if (scene == null) {
            throw new SceneNotExistException("Scene " + sceneId + " not exist");
        }
    }

    
    public void validate() {
        for (var plugin : plugins) {
            plugin.validate();
        }
    }

    public void beforeInitPlugin(P2 plugin) {}
    public void afterInitPlugin(P2 plugin) {}

    
    public void switchArea(String areaId) {
        if (!isAreaMode) {
            return;
        }

        this.areaId = areaId;
        reload();
    }

    
    public D toData() {
        var topologyBO = ClassUtils.createInstance(sceneTopologyBOClass);
        topologyBO.setScene(scene);
        topologyBO.setAreaId(areaId);
        topologyBO.setAreas(new ArrayList<>(areaMap.values()));
        topologyBO.setPieces(new ArrayList<>(pieceMap.values()));
        topologyBO.setElements(new ArrayList<>(elementMap.values()));
        topologyBO.setConnections(new ArrayList<>(elementPortConnectionMap.values()));

        var dataBO = ClassUtils.createInstance(sceneDataBOClass);
        dataBO.setTopology(topologyBO);
        dataBO.setPlugins(scenePluginData);
        return dataBO;
    }

    
    public void reload() {
        scene = null;
        areaMap.clear();
        pieceMap.clear();
        elementMap.clear();
        elementPortMap.clear();
        portIdElementMap.clear();
        elementPortConnectionMap.clear();
        portIdConnectedPortIdMap.clear();
        outerConnectionPortIds.clear();
        scenePluginData.clear();
        elementIdPluginsMap.clear();
        plugins.clear();
        pluginMap.clear();

        loadScene(readScene());
        if (scene == null) {
            return;
        }
        loadAreas(readAreas());
        var filterAreaIds = new String[0];
        if (isAreaMode) {
            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            filterAreaIds = ArrayUtils.addAll(areaMap.values().stream().filter(BaseSceneAreaBO::getOpen)
                    .map(BaseSceneAreaBO::getId).toArray(String[]::new), currentAreaId);
        }
        loadPieces(readPieces(filterAreaIds));
        loadElements(readElements(filterAreaIds));
        loadElementPorts(readElementPorts(filterAreaIds));
        loadConnections(readElementPortConnections(filterAreaIds));
        loadElementPlugins(readElementPlugins(filterAreaIds));
        Map<String, List<String>> pluginKeyElementIds = new HashMap<>();
        Map<String, List<String>> pluginKeyElementPortIds = new HashMap<>();
        Map<String, E> elementEidElementBOMap = new HashMap<>();
        elementMap.forEach((elementBOId, elementBO) -> {
            elementEidElementBOMap.put(elementBO.getEid(), elementBO);
        });
        elementIdPluginsMap.forEach((elementId, elementPlugins) -> {
            var element = elementEidElementBOMap.getOrDefault(elementId, null);
            if (element == null) {
                return;
            }

            var elementPorts = element.getPorts();

            elementPlugins.forEach(elementPlugin -> {
                String pluginKey = elementPlugin.getPluginKey();

                List<String> elementIds = pluginKeyElementIds.getOrDefault(pluginKey, new ArrayList<>());
                elementIds.add(element.getId());
                pluginKeyElementIds.put(pluginKey, elementIds);

                if (elementPorts != null) {
                    elementPorts.forEach(elementPort -> {
                        List<String> elementPortIds = pluginKeyElementPortIds.getOrDefault(pluginKey, new ArrayList<>());
                        elementPortIds.add(elementPort.getId());
                        pluginKeyElementPortIds.put(pluginKey, elementPortIds);
                    });
                }
            });
        });
        initPlugins(pluginKeyElementIds, pluginKeyElementPortIds);
    }

    
    public void save() {
        boolean isCreate = StringUtils.isEmpty(sceneId);
        saveScene();

        Map<String, A0> existAreaMap = isCreate ? new HashMap<>() : readAreas().stream()
                .collect(Collectors.toMap(BaseSceneAreaModel::getNodeId, x -> x));
        var filterAreaIds = new String[0];
        if (isAreaMode) {
            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            filterAreaIds = ArrayUtils.addAll(existAreaMap.values().stream().filter(BaseSceneAreaModel::getOpen)
                    .map(BaseSceneAreaModel::getNodeId).toArray(String[]::new), currentAreaId);
        }
        Map<String, P3> existPieceMap = isCreate ? new HashMap<>() : readPieces(filterAreaIds).stream()
                .collect(Collectors.toMap(BaseScenePieceModel::getNodeId, x -> x));
        Map<String, E0> existElementMap = isCreate ? new HashMap<>() : readElements(filterAreaIds).stream()
                .collect(Collectors.toMap(BaseSceneElementModel::getNodeId, x -> x));
        Map<String, P0> existElementPortMap = isCreate ? new HashMap<>() : readElementPorts(filterAreaIds).stream()
                .collect(Collectors.toMap(BaseSceneElementPortModel::getPortId, x -> x));
        Map<String, C0> existElementPortConnectionMap = isCreate ? new HashMap<>() : readElementPortConnections(filterAreaIds).stream()
                .collect(Collectors.toMap(this::elementPortConnectionKey, x -> x));
        Map<String, P1> existElementPluginMap = isCreate ? new HashMap<>() : readElementPlugins(filterAreaIds).stream()
                .collect(Collectors.toMap(this::elementPluginKey, x -> x));

        
        DiffTargets<A0> diffAreas = saveAreas(existAreaMap);
        DiffTargets<P3> diffPieces = savePieces(existPieceMap);
        DiffTargets<E0> diffElements = saveElements(existElementMap);
        DiffTargets<P0> diffElementPorts = saveElementPorts(existElementPortMap);
        
        if (isAreaMode && !existElementPortConnectionMap.isEmpty()) {
            existElementPortConnectionMap = sceneElementPortConnectionMapper.list(new HumanCondition()
                    .filterIn(SceneElementPortConnectionModel::getId, existElementPortConnectionMap.values()
                            .stream().map(BaseModel::getId).toArray(String[]::new))
            ).stream().collect(Collectors.toMap(this::elementPortConnectionKey, x -> x));
        }
        DiffTargets<C0> diffElementPortConnections = saveElementPortConnections(existElementPortConnectionMap);
        
        DiffTargets<P1> diffElementPlugins = saveElementPlugins(existElementPluginMap);

        
        plugins.forEach(plugin -> {
            plugin.saveScene();
            plugin.createElements(diffElements.getAddingTargets().stream().map(BaseSceneNodeModel::getNodeId)
                    .toArray(String[]::new));
            plugin.updateElements(diffElements.getUpdatingTargets().stream().map(BaseSceneNodeModel::getNodeId)
                    .toArray(String[]::new));
            plugin.deleteElements(diffElements.getRemovingTargets().stream().map(BaseSceneNodeModel::getNodeId)
                    .toArray(String[]::new));
            plugin.createElementPorts(diffElementPorts.getAddingTargets().stream()
                    .map(BaseSceneElementPortModel::getPortId).toArray(String[]::new));
            plugin.updateElementPorts(diffElementPorts.getUpdatingTargets().stream()
                    .map(BaseSceneElementPortModel::getPortId).toArray(String[]::new));
            plugin.deleteElementPorts(diffElementPorts.getRemovingTargets().stream()
                    .map(BaseSceneElementPortModel::getPortId).toArray(String[]::new));
        });
    }

    
    public boolean delete() {
        deleteTopology();

        
        plugins.forEach(plugin -> plugin.deleteScene());

        return sceneMapper.delete(sceneId);
    }

    
    public void deleteTopology() {
        List<A0> areas = readAreas();
        var filterAreaIds = new String[0];
        if (isAreaMode) {
            
            for (var removingArea: areas) {
                if (!Boolean.TRUE.equals(removingArea.getOpen())) {
                    var sceneHandler = ClassUtils.createInstance(sceneHandlerClass);
                    sceneHandler.init(sceneId, removingArea.getNodeId(), isAreaMode, context);
                    sceneHandler.deleteTopology();
                }
            }

            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            filterAreaIds = ArrayUtils.addAll(areas.stream().filter(BaseSceneAreaModel::getOpen)
                    .map(BaseSceneNodeModel::getNodeId).toArray(String[]::new), currentAreaId);
        }
        List<P3> pieces = readPieces(filterAreaIds);
        List<E0> elements = readElements(filterAreaIds);
        List<P0> elementPorts = readElementPorts(filterAreaIds);
        List<P1> elementPlugins = readElementPlugins(filterAreaIds);
        if (!areas.isEmpty()) {
            sceneAreaMapper.bulkDelete(areas.stream().map(BaseModel::getId).toArray(String[]::new));
        }
        if (!pieces.isEmpty()) {
            scenePieceMapper.bulkDelete(pieces.stream().map(BaseModel::getId).toArray(String[]::new));
        }
        if (!elements.isEmpty()) {
            sceneElementMapper.bulkDelete(elements.stream().map(BaseModel::getId).toArray(String[]::new));
        }
        if (!elementPorts.isEmpty()) {
            sceneElementPortMapper.bulkDelete(elementPorts.stream().map(BaseModel::getId).toArray(String[]::new));
        }
        if (!elementPlugins.isEmpty()) {
            sceneElementPluginMapper.bulkDelete(elementPlugins.stream().map(BaseModel::getId).toArray(String[]::new));
        }

        List<C0> elementPortConnections = readElementPortConnections(filterAreaIds);
        if (!elementPortConnections.isEmpty()) {
            sceneElementPortConnectionMapper.bulkDelete(elementPortConnections.stream().map(BaseModel::getId).toArray(String[]::new));
        }

        
        plugins.forEach(plugin -> {
            plugin.deleteElements(elements.stream().map(BaseSceneNodeModel::getNodeId).toArray(String[]::new));
            plugin.deleteElementPorts(elementPorts.stream().map(BaseSceneElementPortModel::getPortId)
                    .toArray(String[]::new));
        });
    }

    
    public boolean logicDelete() {
        return sceneMapper.logicDelete(sceneId);
    }


    
    public void updateScene() {
        saveScene();
    }

    
    public void updateSceneWithPlugins() {
        updateScene();
        plugins.forEach(plugin -> {
            plugin.saveScene();
        });
    }

    
    public List<String> updateSceneElement(String nodeId) {
        var elementBO = getElement(nodeId);
        E0 element = elementBO.toModel();
        element.setScene(sceneId);
        sceneElementMapper.update(element);

        List<P> elementPortBOs = elementBO.getPorts();
        if (elementPortBOs != null && !elementPortBOs.isEmpty()) {
            List<P0> addPorts = new ArrayList<>();
            List<P0> updatePorts = new ArrayList<>();
            for (var elementPortBO : elementPortBOs) {
                P0 port = elementPortBO.toModel();
                port.setScene(sceneId);
                port.setSceneElement(element.getId());
                if (port.getId() == null) {
                    addPorts.add(port);
                } else {
                    updatePorts.add(port);
                }
            }
            if (CollectionUtils.isNotEmpty(addPorts)) {
                sceneElementPortMapper.bulkCreate(addPorts);
            }
            if (CollectionUtils.isNotEmpty(updatePorts)) {
                sceneElementPortMapper.bulkUpdate(updatePorts);
            }
            return new ArrayList<>();
        } else {
            
            var list = sceneElementPortMapper
                    .list(new HumanCondition().filterEqual(SceneElementPortModel::getScene, sceneId)
                    .filterEqual(SceneElementPortModel::getSceneElement, element.getId()));
            
            var connections = sceneElementPortConnectionMapper
                    .filterDelete(new HumanCondition()
                            .filterEqual(SceneElementPortConnectionModel::getScene, sceneId)
                            .filterOrStart()
                            .orEqual(SceneElementPortConnectionModel::getSourceElement, element.getId())
                            .orEqual(SceneElementPortConnectionModel::getTargetElement, element.getId())
                            .filterOrEnd());
            if (CollectionUtils.isEmpty(list)) {
                return new ArrayList<>();
            }
            var result = list.stream().map(p -> p.getPortId()).collect(Collectors.toList());
            var deleteIds = list.stream().map(p -> p.getId()).collect(Collectors.toList());
            sceneElementPortMapper.bulkDelete(deleteIds.toArray(new String[]{}));
            return result;
        }
    }

    
    public void updateSceneElementWithPlugins(String nodeId) {
        var list = updateSceneElement(nodeId);

        List<P> elementPortBOs = getElement(nodeId).getPorts();
        for (var plugin : plugins) {
            plugin.updateElements(nodeId);
            if (elementPortBOs != null && !elementPortBOs.isEmpty()) {
                plugin.updateElementPorts(elementPortBOs.stream().map(BaseSceneElementPortBO::getId).toArray(String[]::new));
            }else if(CollectionUtils.isNotEmpty(list)){
                plugin.deleteElementPorts(list.toArray(new String[]{}));
            }
        }
    }

    
    public <H9> H9 getPluginHandler(String pluginKey) {
        var plugin = pluginKey != null ? pluginMap.getOrDefault(pluginKey, null) : null;
        return plugin == null ? null : (H9) plugin;
    }

    
    public <S9> S9 getPluginScene(String pluginKey) {
        var plugin = pluginKey != null ? pluginMap.getOrDefault(pluginKey, null) : null;
        if (plugin == null) {
            return null;
        }

        var pluginScene = plugin.getScene();
        if (pluginScene == null) {
            return null;
        }

        return (S9) pluginScene;
    }

    
    public <E9> E9 getPluginElement(String pluginKey, String nodeId) {
        var plugin = pluginKey != null ? pluginMap.getOrDefault(pluginKey, null) : null;
        if (plugin == null) {
            return null;
        }

        var pluginElement = plugin.getElement(nodeId);
        if (pluginElement == null) {
            return null;
        }

        return (E9) pluginElement;
    }

    
    public <P9> P9 getPluginPort(String pluginKey, String portId) {
        var plugin = pluginKey != null ? pluginMap.getOrDefault(pluginKey, null) : null;
        if (plugin == null) {
            return null;
        }

        var pluginPort = plugin.getPort(portId);
        if (pluginPort == null) {
            return null;
        }

        return (P9) pluginPort;
    }


    
    public E getElement(String nodeId) {
        return nodeId != null ? elementMap.getOrDefault(nodeId, null) : null;
    }

    
    public List<E> getElementConnectedElements(String nodeId) {
        var connectedPorts = getElementConnectedPorts(nodeId);
        Map<String, E> connectedElementMap = new HashMap<>();
        connectedPorts.forEach(port -> {
            var element = getPortElement(port.getId());
            if (element == null) {
                return;
            }
            connectedElementMap.put(element.getId(), element);
        });
        return new ArrayList<>(connectedElementMap.values());
    }

    
    public List<P> getElementConnectedPorts(String nodeId) {
        var ele = getElement(nodeId);
        var ports = ele.getPorts();
        if (ports == null) {
            return new ArrayList<>();
        }
        List<P> connectedPorts = new ArrayList<>();
        ports.forEach(port -> {
            var connectedPort = getPortConnectedPort(port.getId());
            if (connectedPort == null) {
                return;
            }
            connectedPorts.add(connectedPort);
        });
        return connectedPorts;
    }

    
    public boolean elementHasConnectedPort(String nodeId) {
        var ele = getElement(nodeId);
        var ports = ele.getPorts();
        if (ports == null) {
            return false;
        }
        for (var port: ports) {
            var connectedPortId = portIdConnectedPortIdMap.getOrDefault(port.getId(), null);
            if (connectedPortId != null) {
                return true;
            }
        }
        return false;
    }

    
    public P getPort(String portId) {
        return portId != null ? elementPortMap.getOrDefault(portId, null) : null;
    }

    
    public E getPortElement(String portId) {
        return portId != null ? portIdElementMap.getOrDefault(portId, null) : null;
    }

    
    public P getPortConnectedPort(String portId) {
        var connectedPortId = portId != null ? portIdConnectedPortIdMap.getOrDefault(portId, null) : null;
        return getPort(connectedPortId);
    }

    
    public E getPortConnectedElement(String portId) {
        var connectedPortId = portId != null ? portIdConnectedPortIdMap.getOrDefault(portId, null) : null;
        return getPortElement(connectedPortId);
    }

    
    public C getPortBelongConnection(String portId) {
        var connectionModel = portId != null
                ? (SceneConfigElementPortConnectionModel) sceneElementPortConnectionMapper.first(new HumanCondition()
                .filterEqual(SceneElementPortConnectionModel::getScene, sceneId)
                .filterOrStart()
                .orEqual(SceneElementPortConnectionModel::getSourcePortId, portId)
                .orEqual(SceneElementPortConnectionModel::getTargetPortId, portId)
                .filterOrEnd())
                : null;
        BaseSceneElementPortConnectionBO baseSceneElementPortConnectionBO = new SceneConfigElementPortConnectionBO();
        if (connectionModel != null) {
            baseSceneElementPortConnectionBO.fromModel(connectionModel);
        } else {
            baseSceneElementPortConnectionBO = null;
        }
        return (C) baseSceneElementPortConnectionBO;
    }

    public void addElementPlugin(String pluginKey, String nodeId, Map<String, Object> pluginElement) {
        ScenePluginDataBO scenePluginDataBO = scenePluginData.getOrDefault(pluginKey, null);
        if (scenePluginDataBO == null) {
            return;
        }
        if (scenePluginDataBO.getElements() == null) {
            scenePluginDataBO.setElements(new HashMap<>());
        }
        scenePluginDataBO.getElements().put(nodeId, pluginElement);
    }

    public void addElementPortPlugin(String pluginKey, String portId, Map<String, Object> pluginPort) {
        ScenePluginDataBO scenePluginDataBO = scenePluginData.getOrDefault(pluginKey, null);
        if (scenePluginDataBO == null) {
            return;
        }
        if (scenePluginDataBO.getElementPorts() == null) {
            scenePluginDataBO.setElementPorts(new HashMap<>());
        }
        scenePluginDataBO.getElementPorts().put(portId, pluginPort);
    }

    public void addPort(String portId, E element) {
        this.portIdElementMap.put(portId, element);
    }
}
