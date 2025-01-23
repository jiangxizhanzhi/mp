package cn.cyberpeace.mp.scene.core.base;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.ValidationCommon;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.basic.MapUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.define.ListTreeNode;
import cn.cyberpeace.mp.base.util.mptool.ConcurrentHashSet;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.base.*;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
import cn.cyberpeace.mp.scene.core.exception.SceneUpdateException;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortConnectionModel;
import cn.cyberpeace.mp.scene.model.base.*;
import cn.cyberpeace.mp.scene.repository.base.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 基础的场景处理类，场景模板和场景实例的拓扑数据操作都是一样的
 * 对于场景拓扑分为全拓扑操作、区域拓扑操作
 * 全拓扑操作可视作特殊的区域拓扑操作，其处理逻辑都是一样的，都是对节点、连线的数据处理
 * 区域拓扑操作需要特别考虑边界连线(连线的另一端涉及到其他区域的节点)的数据处理，不能遗漏
 *
 * TODO: 部分节点模式，只对部分节点进行有限的处理
 *
 * @param <TSceneModel> 表模型类   场景模板/场景实例
 * @param <TSceneAreaModel> 表模型类   区域节点
 * @param <TSceneElementModel> 表模型类   网元节点
 * @param <TSceneElementPortModel> 表模型类   网元端口
 * @param <TSceneElementPortConnectionModel> 表模型类   网元端口连接（也代表节点之间连接）
 * @param <TSceneElementPluginModel> 表模型类   网元节点插件关系
 * @param <TScenePieceModel> 表模型类   碎片节点
 * @param <TSceneBO>  业务模型类  场景模板/场景实例
 * @param <TSceneAreaBO>  业务模型类  区域节点
 * @param <TSceneElementPortBO>  业务模型类  网元端口
 * @param <TSceneElementBO>  业务模型类  网元节点
 * @param <TSceneElementPortConnectionBO>  业务模型类  网元端口连接（也代表节点之间连接）
 * @param <TScenePieceBO> 业务模型类  碎片节点
 * @param <TSceneTopologyBO>  业务模型类  场景拓扑数据
 * @param <TSceneDataBO>  业务模型类  场景数据（包含拓扑数据、插件数据）
 * @param <TSceneContextBO> 场景上下文
 * @param <TScenePluginHandler> 场景插件处理接口类
 * @param <TSceneHandler> 场景处理实现类
 */
@Getter
@Slf4j
public abstract class BaseSceneHandler<
        TSceneModel extends BaseSceneModel,
        TSceneAreaModel extends BaseSceneAreaModel,
        TSceneElementModel extends BaseSceneElementModel,
        TSceneElementPortModel extends BaseSceneElementPortModel,
        TSceneElementPortConnectionModel extends BaseSceneElementPortConnectionModel,
        TSceneAreaNodeConnectionModel extends BaseSceneAreaNodeConnectionModel,
        TSceneElementPluginModel extends BaseSceneElementPluginModel,
        TScenePieceModel extends BaseScenePieceModel,
        TSceneBO extends BaseSceneBO<TSceneModel, TSceneBO>,
        TSceneAreaBO extends BaseSceneAreaBO<TSceneAreaModel, TSceneAreaBO>,
        TSceneElementPortBO extends BaseSceneElementPortBO<TSceneElementPortModel, TSceneElementPortBO, TSceneElementBO>,
        TSceneElementBO extends BaseSceneElementBO<TSceneElementModel, TSceneElementPortBO, TSceneElementBO>,
        TSceneElementPortConnectionBO extends BaseSceneElementPortConnectionBO<TSceneElementPortConnectionModel, TSceneElementPortConnectionBO>,
        TSceneAreaNodeConnectionBO extends BaseSceneAreaNodeConnectionBO<TSceneAreaNodeConnectionModel,TSceneAreaNodeConnectionBO>,
        TScenePieceBO extends BaseScenePieceBO<TScenePieceModel, TScenePieceBO>,
        TSceneTopologyBO extends BaseSceneTopologyBO<TSceneModel, TSceneAreaModel, TSceneElementModel, TSceneElementPortModel, TSceneElementPortConnectionModel, TScenePieceModel, TSceneAreaNodeConnectionModel, TSceneBO, TSceneAreaBO, TSceneElementPortBO, TSceneElementBO, TSceneElementPortConnectionBO, TSceneAreaNodeConnectionBO,TScenePieceBO, TSceneTopologyBO>,
        TSceneDataBO extends BaseSceneDataBO<TSceneModel, TSceneAreaModel, TSceneElementModel, TSceneElementPortModel, TSceneElementPortConnectionModel,TSceneAreaNodeConnectionModel, TScenePieceModel, TSceneBO, TSceneAreaBO, TSceneElementPortBO, TSceneElementBO, TSceneElementPortConnectionBO, TSceneAreaNodeConnectionBO,TScenePieceBO, TSceneTopologyBO, TSceneDataBO>,
        TSceneContextBO extends BaseSceneContextBO,
        TScenePluginHandler extends BasePluginHandler<?, ?, ?, ?>,
        TSceneHandler extends BaseSceneHandler<TSceneModel, TSceneAreaModel, TSceneElementModel, TSceneElementPortModel, TSceneElementPortConnectionModel,TSceneAreaNodeConnectionModel, TSceneElementPluginModel, TScenePieceModel, TSceneBO, TSceneAreaBO, TSceneElementPortBO, TSceneElementBO, TSceneElementPortConnectionBO,TSceneAreaNodeConnectionBO, TScenePieceBO, TSceneTopologyBO, TSceneDataBO, TSceneContextBO, TScenePluginHandler, TSceneHandler>> {

    /** 上下文 */
    private TSceneContextBO context;
    /** 是否区域模式 */
    private boolean isAreaMode;
    /** 场景模板/场景实例id */
    private String sceneId;
    /** 区域模式下，当前区域id */
    private String areaId;
    /** 场景模板/场景实例 */
    private TSceneBO scene;
    /** 碎片节点集合 */
    private final Map<String, TScenePieceBO> pieceMap = new ConcurrentHashMap<>();
    /** 区域节点集合 */
    protected final Map<String, TSceneAreaBO> areaMap = new ConcurrentHashMap<>();
    /** 网元节点集合 */
    protected final Map<String, TSceneElementBO> elementMap = new ConcurrentHashMap<>();
    /** 网元端口集合 */
    protected final Map<String, TSceneElementPortBO> elementPortMap = new ConcurrentHashMap<>();
    /** 网元端口连接集合 */
    private final Map<String, TSceneElementPortConnectionBO> elementPortConnectionMap = new ConcurrentHashMap<>();
    /** 区域外连接端口id集合 */
    private final Set<String> outerConnectionPortIds = new CopyOnWriteArraySet<>();
    /** 场景插件数据，key为插件代号 */
    protected final Map<String, ScenePluginDataBO> scenePluginData = new ConcurrentHashMap<>();

    protected final Map<String, TSceneAreaNodeConnectionBO> areaNodeConnectionMap = new ConcurrentHashMap<>();
    protected final Set<String> outerConnectionNodeIds = new CopyOnWriteArraySet<>();
    protected final Map<String, String> nodeIdConnectedNodeIdMap = new ConcurrentHashMap<>();

    private final Set<String> existsOuterNodeIds = new ConcurrentHashSet<>();
    private final Set<String> existsOuterEids = new ConcurrentHashSet<>();

    // 辅助变量
    /** 端口所属节点映射 */
    protected final Map<String, TSceneElementBO> portIdElementMap = new ConcurrentHashMap<>();
    /** 端口连接端口映射 */
    private final Map<String, String> portIdConnectedPortIdMap = new ConcurrentHashMap<>();

    // 通过ClassFieldNames注解和实现类ClassFieldClasses注解，初始化调用ClassUtils.loadClassFields(this)设置
    protected Class<TSceneDataBO> sceneDataBOClass;
    protected Class<TSceneTopologyBO> sceneTopologyBOClass;
    protected Class<TSceneBO> sceneBOClass;
    protected Class<TSceneAreaBO> sceneAreaBOClass;
    protected Class<TSceneElementBO> sceneElementBOClass;
    protected Class<TSceneElementPortBO> sceneElementPortBOClass;
    protected Class<TSceneElementPortConnectionBO> sceneElementPortConnectionBOClass;
    protected Class<TSceneAreaNodeConnectionBO> sceneNodeConnectionBOClass;
    protected Class<TScenePieceBO> scenePieceBOClass;
    protected Class<TSceneModel> sceneModelClass;
    protected Class<TSceneAreaModel> sceneAreaModelClass;
    protected Class<TSceneElementModel> sceneElementModelClass;
    protected Class<TSceneElementPortModel> sceneElementPortModelClass;
    protected Class<TSceneElementPortConnectionModel> sceneElementPortConnectionModelClass;
    protected Class<TSceneElementPluginModel> sceneElementPluginModelClass;
    protected Class<TScenePieceModel> scenePieceModelClass;
    protected Class<TScenePluginHandler> scenePluginHandlerInterface;
    protected Class<TSceneHandler> sceneHandlerClass;

    protected BaseSceneMapper<TSceneModel> sceneMapper;
    protected BaseSceneAreaMapper<TSceneAreaModel> sceneAreaMapper;
    protected BaseSceneElementMapper<TSceneElementModel> sceneElementMapper;
    protected BaseSceneElementPortMapper<TSceneElementPortModel> sceneElementPortMapper;
    protected BaseSceneElementPortConnectionMapper<TSceneElementPortConnectionModel> sceneElementPortConnectionMapper;
    protected BaseSceneElementPluginMapper<TSceneElementPluginModel> sceneElementPluginMapper;
    protected BaseScenePieceMapper<TScenePieceModel> scenePieceMapper;
    protected BaseSceneNodeConnectionMapper sceneNodeConnectionMapper;

    /** 插件数据处理对象集合 */
    private final List<TScenePluginHandler> plugins = new CopyOnWriteArrayList<>();
    private final Map<String, TScenePluginHandler> pluginMap = new ConcurrentHashMap<>();
    /** 网元对应节点插件关系，一个网元节点对于多个插件 */
    protected final Map<String, List<TSceneElementPluginModel>> elementIdPluginsMap = new ConcurrentHashMap<>();

    private Object getPluginContext(String pluginKey) {
        if (context == null) {
            return null;
        }

        Map<String, Object> pluginsContexts = context.getPlugins();
        if (pluginsContexts == null) {
            return null;
        }

        return pluginsContexts.getOrDefault(pluginKey, null);
    }

    /**
     * 初始化插件集合
     */
    @SuppressWarnings("unchecked")
    private void initPlugins(Map<String, ScenePluginDataBO> scenePluginData) {
        plugins.clear();
        pluginMap.clear();
        BeanCommon.getBeanType(scenePluginHandlerInterface).forEach((name, pluginInstance) -> {
            Class<?> pluginClass = pluginInstance.getClass();
            PluginKey pluginKeyAnnotation = pluginClass.getAnnotation(PluginKey.class);
            if (pluginKeyAnnotation == null) {
                return;
            }

            String pluginKey = pluginKeyAnnotation.value();
            ScenePluginDataBO pluginData = scenePluginData.getOrDefault(pluginKey, null);
            if (pluginData == null) {
                return;
            }

            TScenePluginHandler plugin = (TScenePluginHandler) ClassUtils.createInstance(pluginClass);
            beforeInitPlugin(plugin);
            plugin.init(sceneId, pluginData, getPluginContext(pluginKey));
            afterInitPlugin(plugin);
            plugins.add(plugin);
            pluginMap.put(pluginKey, plugin);
        });
        plugins.sort(Comparator.comparing(BasePluginHandler::getPriority));
    }

    /**
     * 初始化插件集合
     */
    @SuppressWarnings("unchecked")
    private void initPlugins(Map<String, List<String>> pluginKeyElementIds,
                             Map<String, List<String>> pluginKeyElementPortIds) {
        plugins.clear();
        pluginMap.clear();
        BeanCommon.getBeanType(scenePluginHandlerInterface).forEach((name, pluginInstance) -> {
            Class<?> pluginClass = pluginInstance.getClass();
            PluginKey pluginKeyAnnotation = pluginClass.getAnnotation(PluginKey.class);
            if (pluginKeyAnnotation == null) {
                return;
            }

            String pluginKey = pluginKeyAnnotation.value();
            List<String> elementIds = pluginKeyElementIds.getOrDefault(pluginKey, null);
            List<String> elementPortIds = pluginKeyElementPortIds.getOrDefault(pluginKey, null);
            TScenePluginHandler plugin = (TScenePluginHandler) ClassUtils.createInstance(pluginClass);
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

    /**
     * 过滤无效的区域数据
     */
    private void filterAreaData() {
        // 根节点
        ListTreeNode<TSceneAreaBO> areaNodeTree = new ListTreeNode<>();

        Map<String, ListTreeNode<TSceneAreaBO>> areaNodeMap = new HashMap<>();
        if (StringUtils.isEmpty(areaId)) {
            areaNodeMap.put(null, areaNodeTree);
            areaNodeMap.put(StringUtils.EMPTY, areaNodeTree);
        } else {
            areaNodeMap.put(areaId, areaNodeTree);
        }
        areaMap.forEach((areaNodeId, area) -> areaNodeMap.put(areaNodeId, new ListTreeNode<>(area)));

        areaNodeMap.forEach((areaNodeId, areaNode) -> {
            TSceneAreaBO area = areaNode.get();
            if (area == null) {
                return;
            }

            // 去除父节点不在此区域的节点
            ListTreeNode<TSceneAreaBO> parentAreaNode = areaNodeMap.getOrDefault(area.getArea(), null);
            if (parentAreaNode == null) {
                return;
            }
            parentAreaNode.addChild(areaNode);
        });

        areaMap.clear();
        filterAreaTree(areaNodeTree, areaMap);
    }

    /**
     * 过滤关闭的区域数据
     */
    private void filterAreaTree(ListTreeNode<TSceneAreaBO> areaNode, Map<String, TSceneAreaBO> targetAreaMap) {
        TSceneAreaBO area = areaNode.get();
        if (area != null) {
            targetAreaMap.put(area.getId(), area);
            // 只保留未关闭的区域
            if (!Boolean.TRUE.equals(area.getOpen())) {
                return;
            }
        }

        areaNode.getChildren().forEach(areaChildNode -> filterAreaTree(areaChildNode, targetAreaMap));
    }

    /**
     * 判断节点是否是区域内
     * @param node 节点
     * @return boolean 节点是否是区域内
     */
    @Deprecated
    private <N extends BaseSceneNodeBO<?, N>> boolean isValidAreaNode(N node) {
        return true;
    }

    /**
     * 网元端口连接的唯一标识(场景内唯一) 源端口id:目的端口id
     * @param elementPortConnection 表模型-网元端口连接
     * @return 连接的唯一标识
     */
    private String elementPortConnectionKey(TSceneElementPortConnectionModel elementPortConnection) {
        return StringUtils.joinWith(":", elementPortConnection.getSourcePortId(),
                elementPortConnection.getTargetPortId());
    }

    /**
     * 网元端口连接的唯一标识(场景内唯一) 源端口id:目的端口id
     * @param elementPortConnection 业务模型-网元端口连接
     * @return 连接的唯一标识
     */
    private String elementPortConnectionKey(TSceneElementPortConnectionBO elementPortConnection) {
        return StringUtils.joinWith(":", elementPortConnection.getSource(),
                elementPortConnection.getTarget());
    }

    /**
     * 网元节点插件的唯一标识 网元id:插件代号
     * @param sceneElement 网元节点id
     * @param pluginKey 插件代号
     * @return 网元节点插件的唯一标识
     */
    private String elementPluginKey(String sceneElement, String pluginKey) {
        return StringUtils.joinWith(":", sceneElement, pluginKey);
    }

    /**
     * 网元节点插件的唯一标识 网元id:插件代号
     * @param elementPlugin 表模型-网元节点插件
     * @return 网元节点插件的唯一标识
     */
    private String elementPluginKey(TSceneElementPluginModel elementPlugin) {
        return elementPluginKey(elementPlugin.getSceneElement(), elementPlugin.getPluginKey());
    }

    /**
     * 载入场景基础数据
     * @param scene 业务模型-场景基础数据
     */
    private void loadSceneBO(TSceneBO scene) {
        if (scene == null) {
            return;
        }

        this.scene = scene;
    }

    /**
     * 载入区域节点数据
     * @param areas 业务模型-区域节点
     */
    private void loadAreaBOs(List<TSceneAreaBO> areas) {
        if (areas == null) {
            return;
        }

        areas.forEach(area -> areaMap.put(area.getId(), area));
        if (isAreaMode) {
            filterAreaData();
        }
    }

    /**
     * 载入碎片节点数据
     * @param pieces 业务模型-碎片节点
     */
    private void loadPieceBOs(List<TScenePieceBO> pieces) {
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

    /**
     * 载入网元节点数据
     * @param elements 业务模型-网元节点
     */
    private void loadElementBOs(List<TSceneElementBO> elements) {
        if (elements == null) {
            return;
        }

        elements.forEach(element -> {
            if (!isValidAreaNode(element)) {
                return;
            }

            elementMap.put(element.getId(), element);

            List<TSceneElementPortBO> elementPorts = element.getPorts();
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

    /**
     * 载入网元端口连接数据
     * @param connections 业务模型-网元端口连接
     */
    private void loadConnectionBOs(List<TSceneElementPortConnectionBO> connections) {
        if (connections == null) {
            return;
        }

        Map<String, List<String>> outerConnectionPortIdConnectionsMap = new HashMap<>();
        Map<String, TSceneElementPortConnectionBO> validConnectionMap = new HashMap<>();
        connections.forEach(connection -> {
            String sourcePortId = connection.getSource();
            String targetPortId = connection.getTarget();
            if (!elementPortMap.containsKey(sourcePortId)
                    && !elementPortMap.containsKey(targetPortId)) {
                return;
            }

            String connectionKey = elementPortConnectionKey(connection);
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

        // 外部连接端口（其它区域端口）
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

    /**
     * 从场景业务数据初始化
     * @param sceneDataBO 场景业务数据
     */
    public void initPart(TSceneDataBO sceneDataBO, boolean isAreaMode) {
        this.initMapper();
        this.initClass();
        this.isAreaMode = isAreaMode;
        outerFromData(sceneDataBO);
    }

    /**
     * 更新外部网元节点
     * @param existElementMap 已存在的外部网元节点
     * @return 更新成功的数量
     */
    private int updateOuterElements(Map<String, TSceneElementModel> existElementMap) {
        // 目前只保存其他区域里的坐标属性
        Map<String, TSceneElementModel> savingElementMap = existElementMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    TSceneElementModel element = entry.getValue();
                    TSceneElementBO elementBO = elementMap.get(element.getNodeId());
                    element.setOuterPosition2(JsonCommon.stringify(elementBO.getOuterPosition2()));
                    return element;
                }));
        int res = sceneElementMapper.bulkUpdate(new ArrayList<>(savingElementMap.values())).getSuccessCount();
        return res;
    }

    /**
     * 从场景业务数据载入外部场景元素
     */
    private void outerFromData(TSceneDataBO sceneDataBO) {
        var topology = sceneDataBO.getTopology();
        var srcScene = (topology == null || topology.getScene() == null) ? ClassUtils.createInstance(sceneBOClass)
                : topology.getScene();
        loadSceneBO(srcScene);
        sceneId = scene.getEid();

        if (topology != null) {
            // 取出当前区域和外部节点的连线

            // 更新不属于当前层的节点
            // 更新不属于当前层的节点的端口
            // 更新连线
            // 节点和区域的连线
            // 插件
            areaId = topology.getAreaId();
            loadOuterElementAndPortBOs(topology.getElements());
            loadOuterPortConnectionBOs(topology.getConnections());
        }
    }

    /**
     * 载入外部网元节点数据
     * @param elements 业务模型-网元节点
     */
    private void loadOuterElementAndPortBOs(List<TSceneElementBO> elements) {
        if (elements == null) {
            return;
        }
        // 取出不属于当前层的节点
        List<TSceneElementBO> outerElements = new ArrayList<>();
        for (TSceneElementBO element : elements) {
            if (!isValidAreaNode(element)) {
                outerElements.add(element);
            }
        }
        for (TSceneElementBO element : outerElements) {
            if (element.getOuterPosition2() == null) {
                element.setOuterPosition2(new HashMap<>());
            }
            element.getOuterPosition2().put(areaId == null ? StringUtils.EMPTY : areaId, element.getPosition2());
            elementMap.put(element.getId(), element);
            // 加载端口
            element.getPorts().forEach(p -> elementPortMap.put(p.getId(), p));
        }
    }

    private void loadOuterPortConnectionBOs(List<TSceneElementPortConnectionBO> connections) {
        if(CollectionUtils.isEmpty(connections)){
            return;
        }
        for (TSceneElementPortConnectionBO connection : connections) {
            if (elementPortMap.containsKey(connection.getSource()) || elementPortMap.containsKey(connection.getTarget())) {
                elementPortConnectionMap.put(elementPortConnectionKey(connection), connection);
                portIdConnectedPortIdMap.put(connection.getSource(), connection.getTarget());
                portIdConnectedPortIdMap.put(connection.getTarget(), connection.getSource());
            }
        }
    }

    private void loadOuterNodeConnectionBOs(List<TSceneAreaNodeConnectionBO> connections) {
        if (CollectionUtils.isEmpty(connections)) {
            return;
        }
        for (TSceneAreaNodeConnectionBO connection : connections) {
            String targetNodeId = parseNodeId(connection.getTargetNodeId());
            String sourceNodeId = parseNodeId(connection.getSourceNodeId());

        }
    }

    /**
     * 载入场景插件数据
     * @param scenePluginData 插件代号：插件数据
     */
    private void loadScenePluginData(Map<String, ScenePluginDataBO> scenePluginData) {
        if (scenePluginData == null) {
            return;
        }
        scenePluginData.forEach(this.scenePluginData::put);
    }

    /**
     * 载入场景基础数据
     * @param scene 表模型-场景基础数据
     */
    private void loadScene(TSceneModel scene) {
        if (scene == null) {
            return;
        }

        TSceneBO TSceneBO = ClassUtils.createInstance(sceneBOClass);
        TSceneBO.fromModel(scene);
        this.scene = TSceneBO;
    }

    /**
     * 载入区域节点数据
     * @param areas 表模型-区域节点
     */
    private void loadAreas(List<TSceneAreaModel> areas) {
        if (areas == null) {
            return;
        }

        areas.forEach(area -> {
            TSceneAreaBO areaBO = ClassUtils.createInstance(sceneAreaBOClass);
            areaBO.fromModel(area);
            areaMap.put(areaBO.getId(), areaBO);
        });
    }

    /**
     * 载入碎片节点数据
     * @param pieces 表模型-碎片节点
     */
    private void loadPieces(List<TScenePieceModel> pieces) {
        if (pieces == null) {
            return;
        }

        pieces.forEach(piece -> {
            TScenePieceBO pieceBO = ClassUtils.createInstance(scenePieceBOClass);
            pieceBO.fromModel(piece);
            pieceMap.put(pieceBO.getId(), pieceBO);
        });
    }

    /**
     * 载入网元节点数据
     * @param elements 表模型-网元节点
     */
    private void loadElements(List<TSceneElementModel> elements) {
        if (elements == null) {
            return;
        }
        elements.forEach(element -> {
            TSceneElementBO elementBO = ClassUtils.createInstance(sceneElementBOClass);
            elementBO.fromModel(element);
            if (isAreaMode && !elementBO.getOuterPosition2().isEmpty() &&
                    !isValidAreaNode(elementBO)) {
                elementBO.setPosition2(elementBO.getOuterPosition2().get(areaId));
            }
            elementMap.put(elementBO.getId(), elementBO);
        });
    }

    /**
     * 载入网元端口数据
     * @param elementPorts 表模型-网元端口
     */
    private void loadElementPorts(List<TSceneElementPortModel> elementPorts) {
        if (elementPorts == null) {
            return;
        }
        Map<String, TSceneElementBO> elementIdElementBOMap = new HashMap<>();
        for (TSceneElementBO TSceneElementBO : elementMap.values()) {
            if (elementIdElementBOMap.put(TSceneElementBO.getEid(), TSceneElementBO) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        Collections.sort(elementPorts, Comparator.comparingInt(BaseSceneElementPortModel::getOrder));
        elementPorts.forEach(elementPort -> {
            TSceneElementBO elementBO = elementIdElementBOMap.getOrDefault(elementPort.getSceneElement(), null);
            if (elementBO == null) {
                return;
            }

            TSceneElementPortBO elementPortBO = ClassUtils.createInstance(sceneElementPortBOClass);
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

    /**
     * 载入网元端口连接数据
     * @param connections 表模型-网元端口连接
     */
    private void loadConnections(List<TSceneElementPortConnectionModel> connections) {
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

            TSceneElementPortConnectionBO elementPortConnectionBO = ClassUtils.createInstance(sceneElementPortConnectionBOClass);
            elementPortConnectionBO.fromModel(connection);
            elementPortConnectionMap.put(elementPortConnectionKey(elementPortConnectionBO), elementPortConnectionBO);
            portIdConnectedPortIdMap.put(elementPortConnectionBO.getSource(), elementPortConnectionBO.getTarget());
            portIdConnectedPortIdMap.put(elementPortConnectionBO.getTarget(), elementPortConnectionBO.getSource());
        });
    }

    /**
     * 载入网元插件关系数据
     * @param elementPlugins 表模型-网元插件关系
     */
    private void loadElementPlugins(List<TSceneElementPluginModel> elementPlugins) {
        elementPlugins.forEach(elementPlugin -> {
            String elementId = elementPlugin.getSceneElement();
            List<TSceneElementPluginModel> plugins = elementIdPluginsMap.getOrDefault(elementId, new ArrayList<>());
            plugins.add(elementPlugin);
            elementIdPluginsMap.put(elementId, plugins);
        });
    }

    /**
     * 从数据库读取场景
     * @return 表模型-场景对象
     */
    private TSceneModel readScene() {
        return sceneMapper.get(sceneId);
    }

    /**
     * 从数据库读取区域节点
     * @param areaIds 父区域节点
     * @return 表模型-区域节点
     */
    @Deprecated
    private List<TSceneAreaModel> readAreas(String... areaIds) {
        HumanCondition condition = new HumanCondition().filterEqual(BaseSceneAreaModel::getScene, sceneId);

        String[] filterAreaIds = areaIds;
        if (filterAreaIds.length == 0 && isAreaMode) {
            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            filterAreaIds = new String[]{currentAreaId};
        }
        if (filterAreaIds.length > 0) {
            condition.filterIn(BaseSceneAreaModel::getArea, filterAreaIds);
        }

        List<TSceneAreaModel> areas = sceneAreaMapper.list(condition);

        if (isAreaMode) {
            List<String> list = new ArrayList<>();
            for (TSceneAreaModel area : areas) {
                if (!isLayerArea(area)) {
                    String nodeId = area.getNodeId();
                    list.add(nodeId);
                }
            }
            String[] openAreaIds = list.toArray(new String[0]);
            if (openAreaIds.length > 0) {
                areas.addAll(readAreas(openAreaIds));
            }
        }

        return areas;
    }

    /**
     * 从数据库读取碎片节点
     * @param areaIds 父区域节点
     * @return 表模型-碎片节点
     */
    private List<TScenePieceModel> readPieces(String... areaIds) {
        List<TScenePieceModel> pieces;
        HumanCondition condition = new HumanCondition()
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

    /**
     * 从数据库读取网元节点
     * @param areaIds 父区域节点
     * @return 表模型-网元节点
     */
    private List<TSceneElementModel> readElements(String... areaIds) {
        List<TSceneElementModel> elements;
        HumanCondition condition = new HumanCondition()
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

    /**
     * 从数据库读取网元端口
     * @param areaIds 父区域节点
     * @return 表模型-网元端口
     */
    private List<TSceneElementPortModel> readElementPorts(String... areaIds) {
        HumanCondition condition = new HumanCondition();

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

    private List<TSceneElementPortConnectionModel> readElementPortConnectionsByNodeEids(List<String> nodeIds) {
        var list = sceneElementPortConnectionMapper.list(new HumanCondition().filterEqual(BaseSceneElementPortConnectionModel::getScene, sceneId)
                                                      .filterOrStart()
                                                      .orIn(BaseSceneElementPortConnectionModel::getSourceElement, nodeIds.toArray(new String[]{}))
                                                      .orIn(BaseSceneElementPortConnectionModel::getTargetElement, nodeIds.toArray(new String[]{}))
                                                      .filterOrEnd());
        List<TSceneElementPortConnectionModel> result = new ArrayList<>();
        for(TSceneElementPortConnectionModel l:list){
            if (nodeIds.contains(l.getSourceElement()) && nodeIds.contains(l.getTargetElement())) {
                continue;
            }
            result.add(l);
        }
        return result;
    }

    /**
     * 从数据库读取网元端口连接
     * @param areaIds 父区域节点
     * @return 表模型-网元端口连接
     */
    private List<TSceneElementPortConnectionModel> readElementPortConnections(String... areaIds) {
        HumanCondition condition = new HumanCondition();

        String[] filterAreaIds = areaIds;
        if (filterAreaIds.length == 0 && isAreaMode) {
            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            filterAreaIds = new String[]{currentAreaId};
        }
        if (filterAreaIds.length > 0) {

            var elements = sceneElementMapper.list(new HumanCondition()
                                                           .filterEqual(BaseSceneElementModel::getScene,sceneId)
                                                           .filterIn(BaseSceneElementModel::getArea, filterAreaIds));
            List<String> nodeIds = new ArrayList<>();
            elements.forEach(e -> nodeIds.add(e.getId()));

            List<TSceneElementPortConnectionModel> connections = new ArrayList<>();
            if (!nodeIds.isEmpty()) {
                connections = sceneElementPortConnectionMapper.list(new HumanCondition().filterEqual(BaseSceneElementPortConnectionModel::getScene, sceneId)
                                                                            .filterIn(BaseSceneElementPortConnectionModel::getSourceElement, nodeIds.toArray(new String[]{}))
                                                                            .filterIn(BaseSceneElementPortConnectionModel::getTargetElement, nodeIds.toArray(new String[]{})));
            }
            return connections;


//            condition.link(sceneElementPortConnectionModelClass, sceneElementModelClass)
//                    .filterOrStart()
//                    .orLink(sceneElementPortConnectionModelClass, BaseSceneElementPortConnectionModel::getSourceElement,
//                            sceneElementModelClass, BaseSceneElementModel::getId)
//                    .orLink(sceneElementPortConnectionModelClass, BaseSceneElementPortConnectionModel::getTargetElement,
//                            sceneElementModelClass, BaseSceneElementModel::getId)
//                    .filterOrEnd()
//                    .currentLink(sceneElementPortConnectionModelClass)
//                    .filterEqual(BaseSceneElementPortConnectionModel::getScene, sceneId)
//                    .currentLink(sceneElementModelClass)
//                    .filterIn(BaseSceneElementModel::getArea, filterAreaIds)
//                    .distinct();
        } else {
            condition.filterEqual(BaseSceneElementPortConnectionModel::getScene, sceneId);
        }
        return sceneElementPortConnectionMapper.list(condition);
    }

    /**
     * 从数据库读取网元插件关系
     * @param areaIds 父区域节点
     * @return 表模型-网元插件关系
     */
    private List<TSceneElementPluginModel> readElementPlugins(String... areaIds) {
        HumanCondition condition = new HumanCondition();

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

    /**
     * 对比需要更新的场景元素
     * @param targetMap 待更新的场景元素
     * @param existTargetMap 已存在的场景元素
     * @param <M> 目标元素表模型类
     * @return 需要更新的场景元素结果集，包含待添加的元素、待更新属性的元素、待删除的元素
     */
    private <M extends BaseModel> DiffTargets<M> diffTargets(Map<String, M> targetMap,
                                                             Map<String, M> existTargetMap) {
        DiffTargets<M> diff = new DiffTargets<M>();
        targetMap.forEach((key, target) -> {
            M existTarget = existTargetMap.getOrDefault(key, null);
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

    /**
     * 保存需要更新的场景元素结果集，包含待添加的元素、待更新属性的元素、待删除的元素
     * @param modelMapper 目标元素的表mapper
     * @param diff 需要更新的区域元素结果集
     * @param <M> 目标元素的表模型类
     * @param <O> 目标元素的表mapper类
     */
    private <M extends BaseModel, O extends BaseModelMapper<M>> void saveDiff(O modelMapper, DiffTargets<M> diff) {
        List<String> list = new ArrayList<>();
        for (M m : diff.getRemovingTargets()) {
            String id = m.getId();
            list.add(id);
        }
        String[] removingTargetIds = list.toArray(new String[0]);
        int deleteResult = modelMapper.bulkLogicDelete(removingTargetIds).getSuccessCount();
        if (deleteResult < removingTargetIds.length) {
            throw new SceneUpdateException("Scene diff delete failed");
        }

        int createResult = modelMapper.bulkCreate(diff.getAddingTargets()).getSuccessCount();
        if (createResult < diff.getAddingTargets().size()) {
            throw new SceneUpdateException("Scene diff create failed");
        }

        int updateResult = modelMapper.bulkUpdate(diff.getUpdatingTargets()).getSuccessCount();
//        if (updateResult < diff.getUpdatingTargets().size()) {
//            throw new SceneUpdateException("Scene diff update failed");
//        }
    }

    /**
     * 保存场景属性数据
     */
    private boolean saveScene() {
        TSceneModel sceneObj = scene.toModel();
        var isCreate = StringUtils.isEmpty(sceneObj.getId());
        boolean result = sceneMapper.save(sceneObj);
        sceneId = sceneObj.getId();
        if (isCreate) {
            scene.fromModel(sceneObj);
        }

        plugins.forEach(plugin -> plugin.setSourceSceneId(sceneId));

        return result;
    }

    /**
     * 保存区域节点
     * @param existAreaMap 已存在的区域节点
     * @return 更新的区域节点，包含添加的、更新属性的、删除的
     */
    private DiffTargets<TSceneAreaModel> saveAreas(Map<String, TSceneAreaModel> existAreaMap) {
        Map<String, TSceneAreaModel> savingAreaMap = areaMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    TSceneAreaModel area = entry.getValue().toModel();
                    area.setScene(sceneId);
                    return area;
                }));
        DiffTargets<TSceneAreaModel> diff = diffTargets(savingAreaMap, existAreaMap);
        if (areaId != null) {
            // 删除闭合的子区域
            for (TSceneAreaModel removingArea: diff.getRemovingTargets()) {
                if (isLayerArea(removingArea)) {
                    TSceneHandler TSceneHandler = ClassUtils.createInstance(sceneHandlerClass);
                    TSceneHandler.init(sceneId, removingArea.getNodeId(), isAreaMode, context);
                    TSceneHandler.deleteTopologyV2();
                }
            }
        }

        saveDiff(sceneAreaMapper, diff);
        ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets())
                .forEach(area -> areaMap.get(area.getNodeId()).fromModel(area));
        return diff;
    }

    /**
     * 保存碎片节点
     * @param existPieceMap 已存在的碎片节点
     * @return 更新的碎片节点，包含添加的、更新属性的、删除的
     */
    private DiffTargets<TScenePieceModel> savePieces(Map<String, TScenePieceModel> existPieceMap) {
        Map<String, TScenePieceModel> savingPieceMap = pieceMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    TScenePieceModel piece = entry.getValue().toModel();
                    piece.setScene(sceneId);
                    return piece;
                }));
        DiffTargets<TScenePieceModel> diff = diffTargets(savingPieceMap, existPieceMap);
        saveDiff(scenePieceMapper, diff);
        ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets())
                .forEach(piece -> pieceMap.get(piece.getNodeId()).fromModel(piece));
        return diff;
    }

    /**
     * 保存网元节点
     * @param existElementMap 已存在的网元节点
     * @return 更新的网元节点，包含添加的、更新属性的、删除的
     */
    private DiffTargets<TSceneElementModel> saveElements(Map<String, TSceneElementModel> existElementMap) {
        Map<String, TSceneElementModel> savingElementMap = new HashMap<>();
        for (var e : elementMap.entrySet()) {
            var v= e.getValue();
            TSceneElementModel element = v.toModel();
            element.setScene(sceneId);
            element.setUpdateTime(LocalDateTime.now());
            savingElementMap.put(e.getKey(), element);
        }
        DiffTargets<TSceneElementModel> diff = diffTargets(savingElementMap, existElementMap);
        saveDiff(sceneElementMapper, diff);
        ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets())
                .forEach(element -> elementMap.get(element.getNodeId()).fromModel(element));
        return diff;
    }

    /**
     * 保存网元端口
     * @param existElementPortMap 已存在的网元端口
     * @return 更新的网元端口，包含添加的、更新属性的、删除的
     */
    private DiffTargets<TSceneElementPortModel> saveElementPorts(Map<String, TSceneElementPortModel> existElementPortMap) {
        Map<String, TSceneElementPortModel> savingElementPortMap = new HashMap<>();
        elementMap.values().forEach(element -> {
            List<TSceneElementPortBO> elementPorts = element.getPorts();
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
                TSceneElementPortModel elementPort = elementPortBO.toModel();
                elementPort.setScene(sceneId);
                elementPort.setSceneElement(elementId);
                savingElementPortMap.put(elementPort.getPortId(), elementPort);
            });
        });

        DiffTargets<TSceneElementPortModel> diff = diffTargets(savingElementPortMap, existElementPortMap);
        saveDiff(sceneElementPortMapper, diff);
        ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets())
                .forEach(elementPort -> elementPortMap.get(elementPort.getPortId()).fromModel(elementPort));
        return diff;
    }

    /**
     * 保存网元端口连接
     * @param existElementPortConnectionMap 已存在的网元端口连接
     * @return 更新的网元端口连接，包含添加的、更新属性的、删除的
     */
    private DiffTargets<TSceneElementPortConnectionModel> saveElementPortConnections(Map<String, TSceneElementPortConnectionModel> existElementPortConnectionMap) {
        Map<String, TSceneElementPortModel> outerElementPortMap = new HashMap<>();
        if (!outerConnectionPortIds.isEmpty()) {
            sceneElementPortMapper.list(new HumanCondition()
                    .select(BaseSceneElementPortModel::getId, BaseSceneElementPortModel::getSceneElement,
                            BaseSceneElementPortModel::getPortId)
                    .filterEqual(BaseSceneElementPortModel::getScene, sceneId)
                    .filterIn(BaseSceneElementPortModel::getPortId, outerConnectionPortIds.toArray(new String[0])))
                    .forEach(elementPort -> outerElementPortMap.put(elementPort.getPortId(), elementPort));
        }

        Map<String, TSceneElementBO> portIdElementMap = getPortIdElementMap();
        Map<String, TSceneElementPortConnectionModel> savingElementPortConnectionMap = new HashMap<>();
        elementPortConnectionMap.forEach((key, elementPortConnectionBO) -> {
            TSceneElementPortConnectionModel elementPortConnection = elementPortConnectionBO.toModel();
            elementPortConnection.setScene(sceneId);
            TSceneElementPortBO sourcePortBO = elementPortMap.getOrDefault(elementPortConnection.getSourcePortId(), null);
            String sourcePortId;
            String sourceElementId;
            if (sourcePortBO == null) {
                TSceneElementPortModel sourcePort = outerElementPortMap.getOrDefault(elementPortConnection.getSourcePortId(), null);
                sourcePortId = sourcePort == null ? null : sourcePort.getId();
                sourceElementId = sourcePort == null ? null : sourcePort.getSceneElement();
            } else {
                sourcePortId = sourcePortBO.getEid();
                TSceneElementBO sourceElement = portIdElementMap.getOrDefault(sourcePortBO.getId(), null);
                sourceElementId = sourceElement == null ? null : sourceElement.getEid();
            }
            TSceneElementPortBO targetPortBO = elementPortMap.getOrDefault(elementPortConnection.getTargetPortId(), null);
            String targetPortId;
            String targetElementId;
            if (targetPortBO == null) {
                TSceneElementPortModel targetPort = outerElementPortMap.getOrDefault(elementPortConnection.getTargetPortId(), null);
                targetPortId = targetPort == null ? null : targetPort.getId();
                targetElementId = targetPort == null ? null : targetPort.getSceneElement();
            } else {
                targetPortId = targetPortBO.getEid();
                TSceneElementBO targetElement = portIdElementMap.getOrDefault(targetPortBO.getId(), null);
                targetElementId = targetElement == null ? null : targetElement.getEid();
            }
            if (sourcePortId == null) {
                var a = sceneElementPortMapper.first(new HumanCondition().filterEqual(BaseSceneElementPortModel::getScene, sceneId)
                                                             .filterEqual(BaseSceneElementPortModel::getPortId, elementPortConnection.getSourcePortId()));
                sourceElementId = a.getSceneElement();
                sourcePortId = a.getId();
            }

            if(targetPortId == null){
                var a = sceneElementPortMapper.first(new HumanCondition().filterEqual(BaseSceneElementPortModel::getScene, sceneId)
                                                             .filterEqual(BaseSceneElementPortModel::getPortId, elementPortConnection.getTargetPortId()));
                targetElementId = a.getSceneElement();
                targetPortId = a.getId();
            }
            elementPortConnection.setSourceElement(sourceElementId);
            elementPortConnection.setTargetElement(targetElementId);
            elementPortConnection.setSourcePort(sourcePortId);
            elementPortConnection.setTargetPort(targetPortId);

            savingElementPortConnectionMap.put(key, elementPortConnection);
        });

        DiffTargets<TSceneElementPortConnectionModel> diff = diffTargets(savingElementPortConnectionMap, existElementPortConnectionMap);
        try {
            saveDiff(sceneElementPortConnectionMapper, diff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets())
                .forEach(elementPortConnection -> elementPortConnectionMap
                        .get(elementPortConnectionKey(elementPortConnection)).fromModel(elementPortConnection));
        return diff;
    }

    /**
     * 保存网元插件关系
     * @param existElementPluginMap 已存在的网元插件关系
     * @return 更新的网元插件关系，包含添加的、更新属性的、删除的
     */
    private DiffTargets<TSceneElementPluginModel> saveElementPlugins(Map<String, TSceneElementPluginModel> existElementPluginMap) {
        Map<String, TSceneElementBO> portIdElementMap = getPortIdElementMap();
        Map<String, TSceneElementPluginModel> savingElementPluginMap = new HashMap<>();
        scenePluginData.forEach((pluginKey, pluginData) -> {
            Map<String, Map<String, Object>> pluginElements = pluginData.getElements();
            if (pluginElements != null) {
                pluginElements.forEach((nodeId, nodePluginData) -> {
                    TSceneElementBO element = elementMap.getOrDefault(nodeId, null);
                    if (element == null) {
                        return;
                    }

                    String key = elementPluginKey(element.getEid(), pluginKey);
                    if (!savingElementPluginMap.containsKey(key)) {
                        TSceneElementPluginModel elementPlugin = ClassUtils.createInstance(sceneElementPluginModelClass);
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
                    TSceneElementBO element = portIdElementMap.getOrDefault(portId, null);
                    if (element == null) {
                        return;
                    }

                    String key = elementPluginKey(element.getEid(), pluginKey);
                    if (!savingElementPluginMap.containsKey(key)) {
                        TSceneElementPluginModel elementPlugin = ClassUtils.createInstance(sceneElementPluginModelClass);
                        elementPlugin.setScene(sceneId);
                        elementPlugin.setSceneElement(element.getEid());
                        elementPlugin.setPluginKey(pluginKey);
                        savingElementPluginMap.put(key, elementPlugin);
                    }
                });
            }
        });

        DiffTargets<TSceneElementPluginModel> diff = diffTargets(savingElementPluginMap, existElementPluginMap);
        saveDiff(sceneElementPluginMapper, diff);
        loadElementPlugins(ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets()));
        return diff;
    }

    /**
     * 获取端口id和端口所属网元映射
     * @return 端口id和端口所属网元映射
     */
    private Map<String, TSceneElementBO> getPortIdElementMap() {
        Map<String, TSceneElementBO> portIdElementMap = new HashMap<>();
        elementMap.forEach((elementId, elementBO) -> {
            List<TSceneElementPortBO> elementPorts = elementBO.getPorts();
            if (elementPorts == null) {
                return;
            }
            elementPorts.forEach(elementPort -> {
                portIdElementMap.put(elementPort.getId(), elementBO);
            });
        });
        return portIdElementMap;
    }

    /**
     * 从场景业务数据载入场景元素
     * @param TSceneDataBO 场景业务数据
     */
    private void fromData(TSceneDataBO TSceneDataBO) {
        // 验证数据字段
        Set<ConstraintViolation<TSceneDataBO>> validateResult = ValidationCommon.validate(TSceneDataBO);
        ValidationCommon.handleValidateResult(validateResult);

        TSceneTopologyBO topology = TSceneDataBO.getTopology();
        TSceneBO srcScene = (topology == null || topology.getScene() == null) ? ClassUtils.createInstance(sceneBOClass)
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
            loadAreaNodeConnectionBOs(topology.getNodeConnections());
        }

        loadScenePluginData(TSceneDataBO.getPlugins());
    }

    /**
     * 纠正场景数据
     */
    protected void correctData() {
        plugins.forEach(plugin -> plugin.correctData());
    }

    /**
     * 从场景业务数据初始化
     * @param TSceneDataBO 场景业务数据
     * @param isAreaMode 是否区域模式
     */
    public void init(TSceneDataBO TSceneDataBO, boolean isAreaMode, TSceneContextBO context) {
        this.initMapper();
        this.initClass();
        this.context = context;
        this.isAreaMode = isAreaMode;
        fromData(TSceneDataBO);
        if (!StringUtils.isEmpty(sceneId) && readScene() == null) {
            throw new SceneNotExistException("Scene " + sceneId + " not exist");
        }
        initPlugins(scenePluginData);

        // 场景业务数据需要纠正
        correctData();
    }

    protected abstract void initMapper();

    protected abstract void initClass();

    /**
     * 从场景id初始化
     * @param sceneId 场景id
     * @param areaId 区域id
     * @param isAreaMode 是否区域模式
     */
    public void init(String sceneId, String areaId, boolean isAreaMode, TSceneContextBO context) {
        this.initMapper();
        this.initClass();
        this.context = context;
        this.sceneId = sceneId;
        this.areaId = areaId;
        this.isAreaMode = isAreaMode;
        reload();
        if (scene == null) {
            throw new SceneNotExistException("Scene " + sceneId + " not exist");
        }
    }

    /**
     * 验证场景
     */
    public void validate() {
        for (TScenePluginHandler plugin : plugins) {
            plugin.validate();
        }
    }

    public void beforeInitPlugin(TScenePluginHandler plugin) {}
    public void afterInitPlugin(TScenePluginHandler plugin) {}

    /**
     * 切换区域
     * @param areaId 区域id
     */
    public void switchArea(String areaId) {
        if (!isAreaMode) {
            return;
        }

        this.areaId = areaId;
        reload();
    }

    /**
     * 输出场景业务数据
     */
    public TSceneDataBO toData() {
        TSceneTopologyBO topologyBO = ClassUtils.createInstance(sceneTopologyBOClass);
        topologyBO.setScene(scene);
        topologyBO.setAreaId(areaId);
        topologyBO.setAreas(new ArrayList<>(areaMap.values()));
        topologyBO.setPieces(new ArrayList<>(pieceMap.values()));
        topologyBO.setElements(new ArrayList<>(elementMap.values()));
        topologyBO.setConnections(new ArrayList<>(elementPortConnectionMap.values()));
        topologyBO.setNodeConnections(new ArrayList<>(areaNodeConnectionMap.values()));

        TSceneDataBO dataBO = ClassUtils.createInstance(sceneDataBOClass);
        dataBO.setTopology(topologyBO);
        dataBO.setPlugins(scenePluginData);
        return dataBO;
    }

    public TSceneDataBO toDataFor3D() {
        TSceneDataBO dataBO = this.toData();
        TSceneTopologyBO topologyBO = dataBO.getTopology();

        //处理topo connection异常多的问题
        if (CollectionUtils.isNotEmpty(topologyBO.getConnections())) {
            Set<String> allPorts = topologyBO.getElements().stream().flatMap(element -> Optional.of(element).map(BaseSceneElementBO::getPorts).orElse(new ArrayList<>())
                    .stream()).map(BaseSceneElementPortBO::getId).collect(Collectors.toSet());
            Iterator<TSceneElementPortConnectionBO> connectionBOIterator = topologyBO.getConnections().iterator();
            while (connectionBOIterator.hasNext()) {
                TSceneElementPortConnectionBO connection = connectionBOIterator.next();
                boolean connectPortsExistFlag = allPorts.contains(connection.getSource()) && allPorts.contains(connection.getTarget());
                if (!connectPortsExistFlag) {
                    connectionBOIterator.remove();
                }
            }
        }

        //将node connect转为connect
        List<TSceneAreaNodeConnectionBO> tSceneAreaNodeConnectionBOS = topologyBO.getNodeConnections();
        if (CollectionUtils.isNotEmpty(tSceneAreaNodeConnectionBOS)) {
            tSceneAreaNodeConnectionBOS.forEach(nodeConnection -> {
                if (Objects.isNull(topologyBO.getConnections())) {
                    topologyBO.setConnections(new ArrayList<>());

                }
                topologyBO.getConnections().add(nodeConnection.convertAreaNodeConnectToConnection());
            });
        }

        List<BaseSceneAreaNodeConnectionBO> nodeConnections = new ArrayList<>(tSceneAreaNodeConnectionBOS);


        topologyBO.getElements().forEach(element -> {
            try {
                if (CollectionUtils.isNotEmpty(nodeConnections)) {
                    for (BaseSceneAreaNodeConnectionBO nodeConnection : nodeConnections) {
                        if (nodeConnection.getSourceNodeId().equals(element.getId())) {
                            TSceneElementPortBO sceneElementPortBO = sceneElementPortBOClass.getDeclaredConstructor().newInstance();
                            sceneElementPortBO.setElement(element);
                            sceneElementPortBO.setEid(TextUtils.uuid());
                            sceneElementPortBO.setId(nodeConnection.getSource());
                            sceneElementPortBO.setName(nodeConnection.getSourceNodeConnectPortName());
                            if (Objects.isNull(element.getPorts())) {
                                element.setPorts(new ArrayList<>());
                            }
                            Set<String> existPorts = element.getPorts().stream().map(port -> port.getId()).collect(Collectors.toSet());
                            if (!existPorts.contains(sceneElementPortBO.getId())) {
                                element.getPorts().add(sceneElementPortBO);
                            }

                        } else if (nodeConnection.getTargetNodeId().equals(element.getId())) {
                            TSceneElementPortBO sceneElementPortBO = sceneElementPortBOClass.getDeclaredConstructor().newInstance();
                            sceneElementPortBO.setElement(element);
                            sceneElementPortBO.setEid(TextUtils.uuid());
                            sceneElementPortBO.setId(nodeConnection.getTarget());
                            sceneElementPortBO.setName(nodeConnection.getTargetNodeConnectPortName());
                            if (Objects.isNull(element.getPorts())) {
                                element.setPorts(new ArrayList<>());
                            }
                            Set<String> existPorts = element.getPorts().stream().map(port -> port.getId()).collect(Collectors.toSet());
                            if (!existPorts.contains(sceneElementPortBO.getId())) {
                                element.getPorts().add(sceneElementPortBO);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });

        //将layer area转为element
        List<TSceneAreaBO> tSceneAreaBOS = topologyBO.getAreas();
        List<TSceneAreaBO> sceneAreaWithoutLayerAreas = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tSceneAreaBOS)) {
            tSceneAreaBOS.forEach(area -> {
                if (BaseSceneAreaModel.Type.LAYER == area.getType()) {
                    if (Objects.isNull(topologyBO.getElements())) {
                        topologyBO.setElements(new ArrayList<>());
                    }
                    topologyBO.getElements().add(area.convertSceneAreaToElement(nodeConnections));
                } else {
                    sceneAreaWithoutLayerAreas.add(area);
                }
            });
        }
        topologyBO.setAreas(sceneAreaWithoutLayerAreas);


        return dataBO;
    }

    /**
     * 清空场景数据，从数据库重新载入
     */
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
        areaNodeConnectionMap.clear();
        outerConnectionNodeIds.clear();

        loadScene(readScene());
        if (scene == null) {
            return;
        }

        var existModel = readExistsModels();
        loadAreas(existModel.existsAreas);
        loadPieces(existModel.existsPieces);
        loadNodeConnections(existModel.existsAreaNodeConnections);
        loadElements(existModel.existsElements);
        loadElementPorts(existModel.existsElementPorts);
        loadConnections(existModel.existsElementPortConnections);
        loadElementPlugins(existModel.existsElementPlugins);

        Map<String, List<String>> pluginKeyElementIds = new HashMap<>();
        Map<String, List<String>> pluginKeyElementPortIds = new HashMap<>();
        Map<String, TSceneElementBO> elementEidElementBOMap = new HashMap<>();
        elementMap.forEach((elementBOId, elementBO) -> {
            elementEidElementBOMap.put(elementBO.getEid(), elementBO);
        });
        elementIdPluginsMap.forEach((elementId, elementPlugins) -> {
            TSceneElementBO element = elementEidElementBOMap.getOrDefault(elementId, null);
            if (element == null) {
                return;
            }

            List<TSceneElementPortBO> elementPorts = element.getPorts();

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

    private ExistsModel readExistsModels() {
        List<TSceneAreaModel> existsAreas = readExistsAreas();
        List<String> commonAreas = new ArrayList<>();
        List<String> layerAreas = new ArrayList<>();
        for (var area : existsAreas) {
            if (BaseSceneAreaModel.Type.COMMON == area.getType()) {
                commonAreas.add(area.getNodeId());
            } else {
                layerAreas.add(area.getNodeId());
            }
        }
        List<TScenePieceModel> existsPieces = readExistsPieces();
        List<TSceneAreaNodeConnectionModel> existsAreaNodeConnections = readExistsAreaNodeConnections(layerAreas);
        List<TSceneElementModel> existsElements = readExistsElements(commonAreas);
        List<TSceneElementPortModel> existsElementPorts = readExistsElementPorts(existsElements);
        List<TSceneElementPortConnectionModel> existsElementPortConnections = readExistElementPortConnections(existsElementPorts);
        List<TSceneElementPluginModel> existsElementPlugins = readExistsElementPlugins(existsElements);

        ExistsModel existsModel = new ExistsModel();
        existsModel.existsAreas = existsAreas;
        existsModel.existsPieces = existsPieces;
        existsModel.existsAreaNodeConnections = existsAreaNodeConnections;
        existsModel.existsElements = existsElements;
        existsModel.existsElementPorts = existsElementPorts;
        existsModel.existsElementPortConnections = existsElementPortConnections;
        existsModel.existsElementPlugins = existsElementPlugins;
        return existsModel;
    }

    private List<TSceneAreaModel> readExistsAreas() {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(BaseSceneAreaModel::getScene, sceneId);
        if (areaId == null) {
            return sceneAreaMapper.list(condition);
        }
        List<String> filterAreaIds = new ArrayList<>();
        filterAreaIds.add(areaId);
        // 查询当前层内部的区块
        var areas = sceneAreaMapper.list(new HumanCondition().filterEqual(BaseSceneAreaModel::getScene, sceneId)
                                                 .filterEqual(BaseSceneAreaModel::getArea, areaId)
                                                 .filterEqual(BaseSceneAreaModel::getType, BaseSceneAreaModel.Type.COMMON));
        if (CollectionUtils.isNotEmpty(areas)) {
            for (TSceneAreaModel a : areas) {
                filterAreaIds.add(a.getId());
            }
        }
        condition.filterIn(BaseSceneAreaModel::getArea, filterAreaIds.toArray(new String[]{}));
        return sceneAreaMapper.list(condition);
    }

    private List<TScenePieceModel> readExistsPieces() {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(BaseScenePieceModel::getScene, sceneId);
        if (areaId == null) {
            return scenePieceMapper.list(condition);
        }
        List<String> filterAreaIds = new ArrayList<>();
        filterAreaIds.add(areaId);
        if (!areaMap.isEmpty()) {
            areaMap.values().forEach(a -> {
                if (a.getType() == BaseSceneAreaModel.Type.COMMON) {
                    filterAreaIds.add(a.getId());
                }
            });
        }
        condition.filterIn(BaseScenePieceModel::getArea, filterAreaIds.toArray(new String[]{}));
        return scenePieceMapper.list(condition);
    }

    private List<TSceneAreaNodeConnectionModel> readExistsAreaNodeConnections(List<String> layerAreas){
        var condition = new HumanCondition();
        condition.filterEqual(BaseSceneAreaNodeConnectionModel::getScene, sceneId);
        if (areaId == null) {
            return sceneNodeConnectionMapper.list(condition);
        }
        Set<String> filterAreaIds = new HashSet<>();
        filterAreaIds.add(areaId);
        filterAreaIds.addAll(layerAreas);
        List<TSceneAreaNodeConnectionModel> connections = sceneNodeConnectionMapper.list(condition);
        List<TSceneAreaNodeConnectionModel> result = new ArrayList<>();
        for (TSceneAreaNodeConnectionModel c : connections) {
            String sourceNodeId = parseNodeId(c.getSourceNodeId());
            String targetNodeId = parseNodeId(c.getTargetNodeId());
            if (filterAreaIds.contains(sourceNodeId)) {
                if (sourceNodeId.equals(areaId)) {
                    existsOuterNodeIds.add(targetNodeId);
                }else{
                    result.add(c);
                }
            } else if (filterAreaIds.contains(targetNodeId)) {
                if (targetNodeId.equals(areaId)) {
                    existsOuterNodeIds.add(sourceNodeId);
                }else{
                    result.add(c);
                }
            }
        }
        return result;
    }

    private List<TSceneElementModel> readExistsElements(List<String> commonAreas){
        var condition = new HumanCondition();
        condition.filterEqual(BaseSceneElementModel::getScene, sceneId);
        if (areaId == null) {
            return sceneElementMapper.list(condition);
        }

        List<TSceneElementModel> innerElements, outerElements;
        // 在当前层内部的节点
        List<String> filterAreaIds = new ArrayList<>();
        filterAreaIds.add(areaId);
        filterAreaIds.addAll(commonAreas);
        var innerCondition = new HumanCondition();
        innerCondition.filterEqual(BaseSceneElementModel::getScene, sceneId);
        innerCondition.filterIn(BaseSceneElementModel::getArea, filterAreaIds.toArray(new String[]{}));
        innerElements = sceneElementMapper.list(innerCondition);

        // 和当前层相连的节点
        if (existsOuterNodeIds.isEmpty()) {
            outerElements = new ArrayList<>();
        } else {
            var outerCondition = new HumanCondition();
            outerCondition.filterEqual(BaseSceneElementModel::getScene, sceneId);
            outerCondition.filterIn(BaseSceneElementModel::getNodeId, existsOuterNodeIds.toArray(new String[]{}));
            outerElements = sceneElementMapper.list(outerCondition);
            for (var outerElement : outerElements) {
                existsOuterEids.add(outerElement.getId());
            }
        }

        List<TSceneElementModel> result = new ArrayList<>();
        result.addAll(innerElements);
        result.addAll(outerElements);
        return result;
    }

    private List<TSceneElementPortModel> readExistsElementPorts(List<TSceneElementModel> elements){
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(BaseSceneElementPortModel::getScene, sceneId);
        if (areaId == null) {
            return sceneElementPortMapper.list(condition);
        }
        if (elements.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> elementDbIds = new ArrayList<>();
        for (TSceneElementModel e : elements) {
            elementDbIds.add(e.getId());
        }
        condition.filterIn(BaseSceneElementPortModel::getSceneElement, elementDbIds.toArray(new String[]{}));
        return sceneElementPortMapper.list(condition);
    }

    private List<TSceneElementPortConnectionModel> readExistElementPortConnections(List<TSceneElementPortModel> existsPorts) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(BaseSceneElementPortConnectionModel::getScene, sceneId);
        if (areaId == null) {
            return sceneElementPortConnectionMapper.list(condition);
        }
        if(existsPorts.isEmpty()){
            return new ArrayList<>();
        }
        List<String> portIds = new ArrayList<>();
        for (TSceneElementPortModel e : existsPorts) {
            portIds.add(e.getPortId());
        }
        condition.filterOrStart()
                .orIn(BaseSceneElementPortConnectionModel::getSourcePortId, portIds.toArray(new String[]{}))
                .orIn(BaseSceneElementPortConnectionModel::getTargetPortId, portIds.toArray(new String[]{}))
                .filterOrEnd();
        return sceneElementPortConnectionMapper.list(condition);
    }

    private List<TSceneElementPluginModel> readExistsElementPlugins(List<TSceneElementModel> elements) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(BaseSceneElementPluginModel::getScene, sceneId);
        if (areaId == null) {
            return sceneElementPluginMapper.list(condition);
        }
        if (elements.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> elementDbIds = new ArrayList<>();
        for (TSceneElementModel e : elements) {
            elementDbIds.add(e.getId());
        }
        condition.filterIn(BaseSceneElementPluginModel::getSceneElement, elementDbIds.toArray(new String[]{}));
        return sceneElementPluginMapper.list(condition);
    }

    private void loadNodeConnections(List<TSceneAreaNodeConnectionModel> nodeConnections) {
        if (nodeConnections == null) {
            return;
        }

        nodeConnections.forEach(nodeConnection -> {
            String sourceNodeId = nodeConnection.getSourceNodeId();
            sourceNodeId = parseNodeId(sourceNodeId);
            String targetNodeId = nodeConnection.getTargetNodeId();
            targetNodeId = parseNodeId(targetNodeId);
            if (!elementMap.containsKey(sourceNodeId) && !elementMap.containsKey(targetNodeId) &&
                    !areaMap.containsKey(sourceNodeId) && !areaMap.containsKey(targetNodeId)) {
                return;
            }

            if (!elementMap.containsKey(sourceNodeId) && !areaMap.containsKey(sourceNodeId)) {
                outerConnectionNodeIds.add(sourceNodeId);
            }
            if (!elementMap.containsKey(targetNodeId) && !areaMap.containsKey(targetNodeId)) {
                outerConnectionNodeIds.add(targetNodeId);
            }

            var nodeConnectionBO = ClassUtils.createInstance(sceneNodeConnectionBOClass);
            nodeConnectionBO.fromModel(nodeConnection);
            areaNodeConnectionMap.put(nodeConnectionKey(nodeConnectionBO), nodeConnectionBO);
            nodeIdConnectedNodeIdMap.put(nodeConnectionBO.getSourceNodeId(), nodeConnectionBO.getTarget());
            nodeIdConnectedNodeIdMap.put(nodeConnectionBO.getTargetNodeId(), nodeConnectionBO.getSource());
        });
    }

    /**
     * 保存当前场景数据
     */
    public void save() {
        boolean isCreate = StringUtils.isEmpty(sceneId);
        saveScene();

        Map<String, TSceneAreaModel> existAreaMap = new HashMap<>();
        Map<String, TScenePieceModel> existPieceMap = new HashMap<>();
        Map<String, TSceneAreaNodeConnectionModel> existNodeConnectionMap = new HashMap<>();
        Map<String, TSceneElementModel> existElementMap = new HashMap<>();
        Map<String, TSceneElementPortModel> existElementPortMap = new HashMap<>();
        Map<String, TSceneElementPortConnectionModel> existElementPortConnectionMap = new HashMap<>();
        Map<String, TSceneElementPluginModel> existElementPluginMap = new HashMap<>();
        if (!isCreate) {
            ExistsModel existsModel = readExistsModels();
            // 从数据库加载各元素数据
            for (TSceneAreaModel area : existsModel.existsAreas) {
                existAreaMap.put(area.getNodeId(), area);
            }
            for (TScenePieceModel piece : existsModel.existsPieces) {
                existPieceMap.put(piece.getNodeId(), piece);
            }
            for (TSceneAreaNodeConnectionModel areaNodeConnection : existsModel.existsAreaNodeConnections) {
                existNodeConnectionMap.put(this.nodeConnectionKey(areaNodeConnection), areaNodeConnection);
            }
            for (TSceneElementModel element : existsModel.existsElements) {
                existElementMap.put(element.getNodeId(), element);
            }
            for (TSceneElementPortModel port : existsModel.existsElementPorts) {
                existElementPortMap.put(port.getPortId(), port);
            }
            for (TSceneElementPortConnectionModel connection : existsModel.existsElementPortConnections) {
                existElementPortConnectionMap.put(this.elementPortConnectionKey(connection), connection);
            }
            for (TSceneElementPluginModel pluginModel : existsModel.existsElementPlugins) {
                existElementPluginMap.put(this.elementPluginKey(pluginModel), pluginModel);
            }
        }

        // 保存区域，可能会跨区域连接
        DiffTargets<TSceneAreaModel> diffAreas = saveAreas(existAreaMap);
        DiffTargets<TScenePieceModel> diffPieces = savePieces(existPieceMap);

        // 删除掉区块时 原本属于区块的节点应该要变成当前层下的节点
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(BaseSceneAreaModel::getScene, sceneId);
        var allAreas = sceneAreaMapper.list(condition);
        Set<String> allAreaIds = new HashSet<>();
        // 第一层为空字符串
        allAreaIds.add("");
        for (TSceneAreaModel a : allAreas) {
            allAreaIds.add(a.getNodeId());
        }
        for (TSceneAreaBO tSceneAreaBO : getAreaMap().values()) {
            allAreaIds.add(tSceneAreaBO.getId());
        }
        for (TSceneElementBO v : getElementMap().values()) {
            if (!allAreaIds.contains(v.getArea())) {
                v.setArea(areaId == null ? "" : areaId);
            }
        }

        DiffTargets<TSceneElementModel> diffElements = saveElements(existElementMap);
        DiffTargets<TSceneElementPortModel> diffElementPorts = saveElementPorts(existElementPortMap);
        // 此时可能存在已被删除的跨区域连接，可优化
        if (isAreaMode && !existElementPortConnectionMap.isEmpty()) {
            List<String> list = new ArrayList<>();
            for (TSceneElementPortConnectionModel c01 : existElementPortConnectionMap.values()) {
                String id = c01.getId();
                list.add(id);
            }
            Map<String, TSceneElementPortConnectionModel> map = new HashMap<>();
            for (TSceneElementPortConnectionModel TSceneElementPortConnectionModel : sceneElementPortConnectionMapper.list(new HumanCondition()
                    .filterIn(cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel::getId, list.toArray(new String[0]))
            )) {
                if (map.put(this.elementPortConnectionKey(TSceneElementPortConnectionModel), TSceneElementPortConnectionModel) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }
            existElementPortConnectionMap = map;
        }
        DiffTargets<TSceneElementPortConnectionModel> diffElementPortConnections = saveElementPortConnections(existElementPortConnectionMap);
        // elementPlugin 由于通过服务端id映射，在拓扑保存后才能获取
        DiffTargets<TSceneElementPluginModel> diffElementPlugins = saveElementPlugins(existElementPluginMap);
        DiffTargets<TSceneAreaNodeConnectionModel> diffNodeConnections = saveNodeConnections(existNodeConnectionMap);

        // 保存插件数据
        for (TScenePluginHandler plugin : plugins) {
            plugin.saveScene();
            List<String> list = new ArrayList<>();
            for (TSceneElementModel TSceneElementModel01 : diffElements.getAddingTargets()) {
                String nodeId = TSceneElementModel01.getNodeId();
                list.add(nodeId);
            }
            plugin.createElements(list.toArray(new String[0]));
            List<String> result = new ArrayList<>();
            for (TSceneElementModel TSceneElementModel01 : diffElements.getUpdatingTargets()) {
                String nodeId = TSceneElementModel01.getNodeId();
                result.add(nodeId);
            }
            plugin.updateElements(result.toArray(new String[0]));
            List<String> list1 = new ArrayList<>();
            for (TSceneElementModel TSceneElementModel : diffElements.getRemovingTargets()) {
                String nodeId = TSceneElementModel.getNodeId();
                list1.add(nodeId);
            }
            plugin.deleteElements(list1.toArray(new String[0]));
            List<String> result1 = new ArrayList<>();
            for (TSceneElementPortModel TSceneElementPortModel01 : diffElementPorts.getAddingTargets()) {
                String portId = TSceneElementPortModel01.getPortId();
                result1.add(portId);
            }
            plugin.createElementPorts(result1.toArray(new String[0]));
            List<String> list2 = new ArrayList<>();
            for (TSceneElementPortModel TSceneElementPortModel01 : diffElementPorts.getUpdatingTargets()) {
                String portId = TSceneElementPortModel01.getPortId();
                list2.add(portId);
            }
            plugin.updateElementPorts(list2.toArray(new String[0]));
            List<String> result2 = new ArrayList<>();
            for (TSceneElementPortModel TSceneElementPortModel : diffElementPorts.getRemovingTargets()) {
                String portId = TSceneElementPortModel.getPortId();
                result2.add(portId);
            }
            plugin.deleteElementPorts(result2.toArray(new String[0]));
        }
    }

    /**
     * 删除场景数据
     */
    public boolean delete() {
        deleteTopologyV2();

        // 删除插件数据
        plugins.forEach(plugin -> plugin.deleteScene());

        if (areaId == null) {
            return sceneMapper.delete(sceneId);
        } else {
            return true;
        }
    }

    public void deleteTopologyV2() {
        var existModel = readExistsModels();
        List<TSceneAreaModel> areas = existModel.existsAreas;
        if (areaId != null) {
            for (TSceneAreaModel area : areas) {
                if (isLayerArea(area)) {
                    TSceneHandler sceneHandler = ClassUtils.createInstance(sceneHandlerClass);
                    sceneHandler.init(sceneId, area.getNodeId(), true, context);
                    sceneHandler.deleteTopologyV2();
                }
            }
        }
        List<TScenePieceModel> pieces = existModel.existsPieces;
        List<TSceneElementModel> elements = existModel.existsElements;
        List<TSceneElementPortModel> elementPorts = existModel.existsElementPorts;
        List<TSceneElementPluginModel> elementPlugins = existModel.existsElementPlugins;
        List<TSceneElementPortConnectionModel> elementPortConnections = existModel.existsElementPortConnections;
        List<TSceneAreaNodeConnectionModel> areaNodeConnectionModels = existModel.existsAreaNodeConnections;

        if (CollectionUtils.isNotEmpty(areas)) {
            List<String> list = new ArrayList<>();
            for (TSceneAreaModel TSceneAreaModel : areas) {
                String id = TSceneAreaModel.getId();
                list.add(id);
            }
            sceneAreaMapper.bulkDelete(list.toArray(new String[0]));
        }
        if (CollectionUtils.isNotEmpty(pieces)) {
            List<String> list = new ArrayList<>();
            for (TScenePieceModel TScenePieceModel : pieces) {
                String id = TScenePieceModel.getId();
                list.add(id);
            }
            scenePieceMapper.bulkDelete(list.toArray(new String[0]));
        }
        if (CollectionUtils.isNotEmpty(elements)) {
            List<String> list = new ArrayList<>();
            for (TSceneElementModel TSceneElementModel : elements) {
                String id = TSceneElementModel.getId();
                if (!existsOuterNodeIds.contains(TSceneElementModel.getNodeId())) {
                    list.add(id);
                }
            }
            if (CollectionUtils.isNotEmpty(list)) {
                sceneElementMapper.bulkDelete(list.toArray(new String[0]));
            }
        }
        if (CollectionUtils.isNotEmpty(elementPorts)) {
            List<String> list = new ArrayList<>();
            for (TSceneElementPortModel TSceneElementPortModel : elementPorts) {
                if (!existsOuterEids.contains(TSceneElementPortModel.getSceneElement())) {
                    String id = TSceneElementPortModel.getId();
                    list.add(id);
                }
            }
            if (CollectionUtils.isNotEmpty(list)) {
                sceneElementPortMapper.bulkDelete(list.toArray(new String[0]));
            }
        }
        if (CollectionUtils.isNotEmpty(elementPlugins)) {
            List<String> list = new ArrayList<>();
            for (TSceneElementPluginModel TSceneElementPluginModel : elementPlugins) {
                if (!existsOuterEids.contains(TSceneElementPluginModel.getSceneElement())) {
                    String id = TSceneElementPluginModel.getId();
                    list.add(id);
                }
            }
            if (CollectionUtils.isNotEmpty(list)) {
                sceneElementPluginMapper.bulkDelete(list.toArray(new String[0]));
            }
        }
        if (CollectionUtils.isNotEmpty(elementPortConnections)) {
            List<String> list = new ArrayList<>();
            for (TSceneElementPortConnectionModel TSceneElementPortConnectionModel : elementPortConnections) {
                String id = TSceneElementPortConnectionModel.getId();
                list.add(id);
            }
            sceneElementPortConnectionMapper.bulkDelete(list.toArray(new String[0]));
        }
        if (CollectionUtils.isNotEmpty(areaNodeConnectionModels)) {
            List<String> list = new ArrayList<>();
            for (TSceneAreaNodeConnectionModel TSceneElementPortConnectionModel : areaNodeConnectionModels) {
                String id = TSceneElementPortConnectionModel.getId();
                list.add(id);
            }
            sceneNodeConnectionMapper.bulkDelete(list.toArray(new String[0]));
        }
        // 删除插件数据
        plugins.forEach(plugin -> {
            List<String> list = new ArrayList<>();
            for (TSceneElementModel TSceneElementModel : elements) {
                String nodeId = TSceneElementModel.getNodeId();
                list.add(nodeId);
            }
            plugin.deleteElements(list.toArray(new String[0]));
            List<String> result = new ArrayList<>();
            for (TSceneElementPortModel TSceneElementPortModel : elementPorts) {
                String portId = TSceneElementPortModel.getPortId();
                result.add(portId);
            }
            plugin.deleteElementPorts(result.toArray(new String[0]));
        });
    }

    /**
     * 逻辑删除场景数据
     */
    public boolean logicDelete() {
        return sceneMapper.logicDelete(sceneId);
    }

    /**
     * 单独更新基础场景数据
     */
    public void updateScene() {
        saveScene();
    }

    /**
     * 单独更新基础场景数据及其插件数据
     */
    public void updateSceneWithPlugins() {
        updateScene();
        plugins.forEach(plugin -> {
            plugin.saveScene();
        });
    }

    /**
     * 单独保存基础场景节点数据
     */
    public List<String> updateSceneElement(String nodeId) {
        TSceneElementBO elementBO = getElement(nodeId);
        TSceneElementModel element = elementBO.toModel();
        element.setScene(sceneId);
        sceneElementMapper.update(element);

        List<TSceneElementPortBO> elementPortBOS = elementBO.getPorts();
        if (elementPortBOS != null && !elementPortBOS.isEmpty()) {
            List<TSceneElementPortModel> addPorts = new ArrayList<>();
            List<TSceneElementPortModel> updatePorts = new ArrayList<>();
            for (TSceneElementPortBO elementPortBO : elementPortBOS) {
                TSceneElementPortModel port = elementPortBO.toModel();
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
            // 此时说明把端口都删除了
            List<TSceneElementPortModel> list = sceneElementPortMapper
                    .list(new HumanCondition().filterEqual(cn.cyberpeace.mp.scene.model.SceneElementPortModel::getScene, sceneId)
                    .filterEqual(cn.cyberpeace.mp.scene.model.SceneElementPortModel::getSceneElement, element.getId()));
            // 连线也删除一下
            int connections = sceneElementPortConnectionMapper
                    .filterDelete(new HumanCondition()
                            .filterEqual(cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel::getScene, sceneId)
                            .filterOrStart()
                            .orEqual(cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel::getSourceElement, element.getId())
                            .orEqual(cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel::getTargetElement, element.getId())
                            .filterOrEnd());
            if (CollectionUtils.isEmpty(list)) {
                return new ArrayList<>();
            }
            List<String> result = new ArrayList<>();
            for (TSceneElementPortModel TSceneElementPortModel : list) {
                String portId = TSceneElementPortModel.getPortId();
                result.add(portId);
            }
            List<String> deleteIds = new ArrayList<>();
            for (TSceneElementPortModel p : list) {
                String id = p.getId();
                deleteIds.add(id);
            }
            sceneElementPortMapper.bulkDelete(deleteIds.toArray(new String[]{}));
            return result;
        }
    }

    /**
     * 单独保存基础场景节点数据及其插件数据
     */
    public void updateSceneElementWithPlugins(String nodeId) {
        List<String> list = updateSceneElement(nodeId);

        List<TSceneElementPortBO> elementPortBOS = getElement(nodeId).getPorts();
        for (TScenePluginHandler plugin : plugins) {
            plugin.updateElements(nodeId);
            if (elementPortBOS != null && !elementPortBOS.isEmpty()) {
                List<String> result = new ArrayList<>();
                for (TSceneElementPortBO TSceneElementPortBO : elementPortBOS) {
                    String id = TSceneElementPortBO.getId();
                    result.add(id);
                }
                plugin.updateElementPorts(result.toArray(new String[0]));
            }else if(CollectionUtils.isNotEmpty(list)){
                plugin.deleteElementPorts(list.toArray(new String[]{}));
            }
        }
    }

    /**
     * 获取插件handler
     * @param pluginKey 插件代号
     */
    public <H9> H9 getPluginHandler(String pluginKey) {
        TScenePluginHandler plugin = pluginKey != null ? pluginMap.getOrDefault(pluginKey, null) : null;
        return plugin == null ? null : (H9) plugin;
    }

    /**
     * 获取插件全局数据
     * @param pluginKey 插件代号
     */
    public <S9> S9 getPluginScene(String pluginKey) {
        TScenePluginHandler plugin = pluginKey != null ? pluginMap.getOrDefault(pluginKey, null) : null;
        if (plugin == null) {
            return null;
        }

        Object pluginScene = plugin.getScene();
        if (pluginScene == null) {
            return null;
        }

        return (S9) pluginScene;
    }

    /**
     * 获取网元插件数据
     * @param pluginKey 插件代号
     * @param nodeId 网元节点id
     */
    public <E9> E9 getPluginElement(String pluginKey, String nodeId) {
        TScenePluginHandler plugin = pluginKey != null ? pluginMap.getOrDefault(pluginKey, null) : null;
        if (plugin == null) {
            return null;
        }

        Object pluginElement = plugin.getElement(nodeId);
        if (pluginElement == null) {
            return null;
        }

        return (E9) pluginElement;
    }

    /**
     * 获取端口插件数据
     * @param pluginKey 插件代号
     * @param portId 端口id
     */
    public <P9> P9 getPluginPort(String pluginKey, String portId) {
        TScenePluginHandler plugin = pluginKey != null ? pluginMap.getOrDefault(pluginKey, null) : null;
        if (plugin == null) {
            return null;
        }

        Object pluginPort = plugin.getPort(portId);
        if (pluginPort == null) {
            return null;
        }

        return (P9) pluginPort;
    }

    /**
     * 获取网元数据
     * @param nodeId 网元节点id
     */
    public TSceneElementBO getElement(String nodeId) {
        return nodeId != null ? elementMap.getOrDefault(nodeId, null) : null;
    }

    /**
     * 获取网元连接的网元
     * @param nodeId 网元节点id
     */
    public List<TSceneElementBO> getElementConnectedElements(String nodeId) {
        List<TSceneElementPortBO> connectedPorts = getElementConnectedPorts(nodeId);
        Map<String, TSceneElementBO> connectedElementMap = new HashMap<>();
        connectedPorts.forEach(port -> {
            TSceneElementBO element = getPortElement(port.getId());
            if (element == null) {
                return;
            }
            connectedElementMap.put(element.getId(), element);
        });
        return new ArrayList<>(connectedElementMap.values());
    }

    /**
     * 获取网元连接的网元
     * @param nodeId 网元节点id
     */
    public List<TSceneElementPortBO> getElementConnectedPorts(String nodeId) {
        TSceneElementBO ele = getElement(nodeId);
        List<TSceneElementPortBO> ports = ele.getPorts();
        if (ports == null) {
            return new ArrayList<>();
        }
        List<TSceneElementPortBO> connectedPorts = new ArrayList<>();
        ports.forEach(port -> {
            TSceneElementPortBO connectedPort = getPortConnectedPort(port.getId());
            if (connectedPort == null) {
                return;
            }
            connectedPorts.add(connectedPort);
        });
        return connectedPorts;
    }

    /**
     * 获取网元是否存在连接
     * @param nodeId 网元节点id
     */
    public boolean elementHasConnectedPort(String nodeId) {
        TSceneElementBO ele = getElement(nodeId);
        List<TSceneElementPortBO> ports = ele.getPorts();
        if (ports == null) {
            return false;
        }
        for (TSceneElementPortBO port: ports) {
            String connectedPortId = portIdConnectedPortIdMap.getOrDefault(port.getId(), null);
            if (connectedPortId != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取端口数据
     * @param portId 端口id
     */
    public TSceneElementPortBO getPort(String portId) {
        return portId != null ? elementPortMap.getOrDefault(portId, null) : null;
    }

    /**
     * 获取端口所属网元数据
     * @param portId 端口id
     */
    public TSceneElementBO getPortElement(String portId) {
        return portId != null ? portIdElementMap.getOrDefault(portId, null) : null;
    }

    /**
     * 获取端口连接的端口
     * @param portId 端口id
     */
    public TSceneElementPortBO getPortConnectedPort(String portId) {
        String connectedPortId = portId != null ? portIdConnectedPortIdMap.getOrDefault(portId, null) : null;
        return getPort(connectedPortId);
    }

    /**
     * 获取端口连接的节点
     * @param portId 端口id
     */
    public TSceneElementBO getPortConnectedElement(String portId) {
        String connectedPortId = portId != null ? portIdConnectedPortIdMap.getOrDefault(portId, null) : null;
        return getPortElement(connectedPortId);
    }

    /**
     * 获取源端口所属的连线
     * @param portId 端口id
     */
    public TSceneElementPortConnectionBO getPortBelongConnection(String portId) {
        SceneConfigElementPortConnectionModel connectionModel = portId != null
                ? (SceneConfigElementPortConnectionModel) sceneElementPortConnectionMapper.first(new HumanCondition()
                .filterEqual(cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel::getScene, sceneId)
                .filterOrStart()
                .orEqual(cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel::getSourcePortId, portId)
                .orEqual(cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel::getTargetPortId, portId)
                .filterOrEnd())
                : null;
        BaseSceneElementPortConnectionBO baseSceneElementPortConnectionBO = new SceneConfigElementPortConnectionBO();
        if (connectionModel != null) {
            baseSceneElementPortConnectionBO.fromModel(connectionModel);
        } else {
            baseSceneElementPortConnectionBO = null;
        }
        return (TSceneElementPortConnectionBO) baseSceneElementPortConnectionBO;
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

    public void addPort(String portId, TSceneElementBO element) {
        this.portIdElementMap.put(portId, element);
    }

    public void reloadOut() {
        areaMap.clear();
        elementMap.clear();
        elementPortMap.clear();
        portIdElementMap.clear();
        scenePluginData.clear();
        elementIdPluginsMap.clear();
        elementPortConnectionMap.clear();

        var outNodeConnection = readAreaNodeConnectionsByNodeIds(areaId);
        String[] outNodeIds = new String[outNodeConnection.size()];
        for (int i = 0; i < outNodeConnection.size(); i++) {
            String source = parseNodeId(outNodeConnection.get(i).getSourceNodeId());
            String target = parseNodeId(outNodeConnection.get(i).getTargetNodeId());
            if (source.equals(areaId)) {
                outNodeIds[i] = target;
            } else {
                outNodeIds[i] = source;
            }
        }
        if (outNodeIds.length == 0) {
            return;
        }

        loadAreas(readAreasByNodeIds(outNodeIds));
        loadElements(readElementsByNodeIds(outNodeIds));
        loadElementPorts(readElementPortsByNodeIds(outNodeIds));
        loadElementPlugins(readElementPluginsByNodeIds(outNodeIds));
        List<String> eids = new ArrayList<>();
        elementMap.values().forEach(e -> eids.add(e.getEid()));
        loadConnections(readElementPortConnectionsByNodeEids(eids));
        Map<String, List<String>> pluginKeyElementIds = new HashMap<>();
        Map<String, List<String>> pluginKeyElementPortIds = new HashMap<>();
        Map<String, TSceneElementBO> elementEidElementBOMap = new HashMap<>();
        elementMap.forEach((elementBOId, elementBO) -> elementEidElementBOMap.put(elementBO.getEid(), elementBO));
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

    private List<TSceneAreaNodeConnectionModel> readAreaNodeConnectionsByNodeIds(String... nodeIds) {
        if (nodeIds.length == 0) {
            return new ArrayList<>();
        }

        List<TSceneAreaNodeConnectionModel> list = sceneNodeConnectionMapper.list(new HumanCondition().filterEqual(BaseSceneAreaNodeConnectionModel::getScene, sceneId));
        List<TSceneAreaNodeConnectionModel> result = new ArrayList<>();
        for (var l : list) {
            for(String nodeId:nodeIds){
                if (StringUtils.isEmpty(nodeId)) {
                    continue;
                }
                if (l.getSourceNodeId().contains(nodeId) || l.getTargetNodeId().contains(nodeId)) {
                    result.add(l);
                    break;
                }
            }
        }
        return result;
//        var condition = new HumanCondition()
//                .filterEqual(BaseSceneAreaNodeConnectionModel::getScene, sceneId)
//                .filterOrStart()
//                .orIn(BaseSceneAreaNodeConnectionModel::getSourceNodeId, nodeIds)
//                .orIn(BaseSceneAreaNodeConnectionModel::getTargetNodeId, nodeIds)
//                .filterOrEnd();
//
//        return sceneNodeConnectionMapper.list(condition);
    }

    private List<TSceneAreaModel> readAreasByNodeIds(String... nodeIds) {
        if (nodeIds.length == 0) {
            return new ArrayList<>();
        }
        var condition = new HumanCondition()
                .filterEqual(BaseSceneAreaModel::getScene, sceneId)
                .filterIn(BaseSceneAreaModel::getNodeId, nodeIds);

        return sceneAreaMapper.list(condition);
    }

    private List<TSceneElementModel> readElementsByNodeIds(String... nodeIds) {
        if (nodeIds.length == 0) {
            return new ArrayList<>();
        }
        var condition = new HumanCondition()
                .filterEqual(BaseSceneElementModel::getScene, sceneId)
                .filterIn(BaseSceneElementModel::getNodeId, nodeIds);

        return sceneElementMapper.list(condition);
    }

    private List<TSceneElementPortModel> readElementPortsByNodeIds(String... nodeIds) {
        if (nodeIds.length == 0) {
            return new ArrayList<>();
        }
        var condition = new HumanCondition()
                .link(sceneElementPortModelClass, sceneElementModelClass)
                .filterLink(sceneElementPortModelClass, BaseSceneElementPortModel::getSceneElement,
                            sceneElementModelClass, BaseSceneElementModel::getId)
                .currentLink(sceneElementPortModelClass)
                .filterEqual(BaseSceneElementPortModel::getScene, sceneId)
                .currentLink(sceneElementModelClass)
                .filterIn(BaseSceneElementModel::getNodeId, nodeIds);

        return sceneElementPortMapper.list(condition);
    }

    private List<TSceneElementPluginModel> readElementPluginsByNodeIds(String... nodeIds) {
        if (nodeIds.length == 0) {
            return new ArrayList<>();
        }
        var condition = new HumanCondition()
                .link(sceneElementPluginModelClass, sceneElementModelClass)
                .filterLink(sceneElementPluginModelClass, BaseSceneElementPluginModel::getScene,
                            sceneElementModelClass, BaseSceneElementModel::getScene)
                .filterLink(sceneElementPluginModelClass, BaseSceneElementPluginModel::getSceneElement,
                            sceneElementModelClass, BaseSceneElementModel::getId)
                .currentLink(sceneElementPluginModelClass)
                .filterEqual(BaseSceneElementPluginModel::getScene, sceneId)
                .currentLink(sceneElementModelClass)
                .filterIn(BaseSceneElementModel::getNodeId, nodeIds);

        return sceneElementPluginMapper.list(condition);
    }

    public void initLazy(String sceneId, String areaId, boolean isAreaMode) {
        this.sceneId = sceneId;
        this.areaId = areaId;
        this.isAreaMode = isAreaMode;
        this.initMapper();
        this.initClass();
    }

    private String nodeConnectionKey(TSceneAreaNodeConnectionBO nodeConnection) {
        return StringUtils.joinWith(":", nodeConnection.getSourceNodeId(),
                                    nodeConnection.getTargetNodeId());
    }

    private String nodeConnectionKey(TSceneAreaNodeConnectionModel nodeConnection) {
        String sourceNodeId = nodeConnection.getSourceNodeId();
        sourceNodeId = parseNodeId(sourceNodeId);
        String targetNodeId = nodeConnection.getTargetNodeId();
        targetNodeId = parseNodeId(targetNodeId);
        return StringUtils.joinWith(":", sourceNodeId,
                                    targetNodeId);
    }

    private DiffTargets<TSceneAreaNodeConnectionModel> saveNodeConnections(Map<String, TSceneAreaNodeConnectionModel> existNodeConnectionMap) {
        Map<String, TSceneElementModel> outerElementMap = new HashMap<>();
        Map<String, TSceneAreaModel> outerAreaMap = new HashMap<>();
        if (!outerConnectionNodeIds.isEmpty()) {
            sceneElementMapper.list(new HumanCondition()
                                            .select(BaseSceneElementModel::getId, BaseSceneElementModel::getNodeId)
                                            .filterEqual(BaseSceneElementModel::getScene, sceneId)
                                            .filterIn(BaseSceneElementModel::getNodeId, outerConnectionNodeIds.toArray()))
                    .forEach(element -> outerElementMap.put(element.getNodeId(), element));
            sceneAreaMapper.list(new HumanCondition()
                                         .select(BaseSceneAreaModel::getId, BaseSceneAreaModel::getNodeId)
                                         .filterEqual(BaseSceneAreaModel::getScene, sceneId)
                                         .filterIn(BaseSceneAreaModel::getNodeId, outerConnectionNodeIds.toArray()))
                    .forEach(area -> outerAreaMap.put(area.getNodeId(), area));
        }

        Map<String, TSceneAreaNodeConnectionModel> savingNodeConnectionMap = new HashMap<>();
        areaNodeConnectionMap.forEach((key, nodeConnectionBO) -> {
            TSceneAreaNodeConnectionModel nodeConnection = nodeConnectionBO.toModel();
            nodeConnection.setScene(sceneId);

            String sourceNodeId = nodeConnection.getSourceNodeId();
            sourceNodeId = parseNodeId(sourceNodeId);
            String sourceNodeEid = getNodeEid(sourceNodeId, outerElementMap, outerAreaMap);

            String targetNodeId = nodeConnection.getTargetNodeId();
            targetNodeId = parseNodeId(targetNodeId);
            String targetNodeEid = getNodeEid(targetNodeId, outerElementMap, outerAreaMap);

            nodeConnection.setSourceNode(sourceNodeEid);
            nodeConnection.setTargetNode(targetNodeEid);

            savingNodeConnectionMap.put(key, nodeConnection);
        });

        DiffTargets<TSceneAreaNodeConnectionModel> diff = diffTargets(savingNodeConnectionMap, existNodeConnectionMap);
        saveDiff(sceneNodeConnectionMapper, diff);
        ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets())
                .forEach(nodeConnection -> areaNodeConnectionMap
                        .get(nodeConnectionKey(nodeConnection)).fromModel(nodeConnection));
        return diff;
    }

    private DiffTargets<TSceneAreaNodeConnectionModel> saveOuterNodeConnections(Map<String, TSceneAreaNodeConnectionModel> existNodeConnectionMap) {
        Map<String, TSceneAreaNodeConnectionModel> savingNodeConnectionMap = new HashMap<>();
        for (Map.Entry<String, TSceneAreaNodeConnectionBO> entry : areaNodeConnectionMap.entrySet()) {
            String e = entry.getKey();
            TSceneAreaNodeConnectionBO v = entry.getValue();
            savingNodeConnectionMap.put(e, v.toModel());
        }
        DiffTargets<TSceneAreaNodeConnectionModel> diff = diffTargets(savingNodeConnectionMap, existNodeConnectionMap);
        saveDiff(sceneNodeConnectionMapper, diff);
        ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets())
                .forEach(nodeConnection -> areaNodeConnectionMap.get(nodeConnectionKey(nodeConnection))
                        .fromModel(nodeConnection));
        return diff;
    }

    private DiffTargets<TSceneElementPortConnectionModel> saveOutPortConnections(Map<String, TSceneElementPortConnectionModel> savingNodeConnectionMap, Map<String, TSceneElementPortConnectionModel> existNodeConnectionMap) {
        DiffTargets<TSceneElementPortConnectionModel> diff = diffTargets(savingNodeConnectionMap, existNodeConnectionMap);
        saveDiff(sceneElementPortConnectionMapper, diff);
        ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets())
                .forEach(elementPortConnection -> elementPortConnectionMap.get(elementPortConnectionKey(elementPortConnection))
                        .fromModel(elementPortConnection));
        return diff;
    }

    private String parseNodeIdFromPort(String input){
        String regex = "port_(.+)_.+_[0-9]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String extracted = matcher.group(1);
            return extracted;
        }
        return input;
    }

    private String parseNodeId(String input){
        String regex = "port_(.+)_layer_[0-9]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String extracted = matcher.group(1);
            return extracted;
        }
        return input;
    }

    private String getNodeEid(String nodeId, Map<String, TSceneElementModel> outerElementMap, Map<String, TSceneAreaModel> outerAreaMap) {
        var elementBO = elementMap.getOrDefault(nodeId, null);
        var areaBO = areaMap.getOrDefault(nodeId, null);
        String nodeEid;
        if (elementBO != null || areaBO != null) {
            nodeEid = elementBO == null ? areaBO.getEid() : elementBO.getEid();
        } else {
            TSceneElementModel targetElement = outerElementMap.getOrDefault(nodeId, null);
            TSceneAreaModel targetArea = outerAreaMap.getOrDefault(nodeId, null);
            if (targetElement != null || targetArea != null) {
                nodeEid = targetElement == null ? targetArea.getId() : targetElement.getId();
            } else {
                nodeEid = null;
            }
        }
        return nodeEid;
    }

    private List<TSceneAreaNodeConnectionModel> readAreaNodeConnections(String... areaIds) {
        var condition = new HumanCondition();

        String[] filterAreaIds = areaIds;
        if (filterAreaIds.length == 0 && isAreaMode) {
            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            filterAreaIds = new String[]{currentAreaId};
        }
        if (filterAreaIds.length > 0) {
            HumanCondition queryContainedAreas = new HumanCondition()
                    .filterEqual(BaseSceneAreaModel::getScene,sceneId)
                    .filterIn(BaseSceneAreaModel::getArea,filterAreaIds);
            List<TSceneAreaModel> containedAreas = sceneAreaMapper.list(queryContainedAreas);
            if (containedAreas.isEmpty()) {
                return new ArrayList<>();
            }
            List<String> containedAreaIds = new ArrayList<>();
            containedAreas.forEach(a -> containedAreaIds.add(a.getNodeId()));
//            condition.filterEqual(BaseSceneAreaNodeConnectionModel::getScene, sceneId)
//                    .filterOrStart()
//                    .orIn(BaseSceneAreaNodeConnectionModel::getSourceNodeId, containedAreaIds.toArray(new String[]{}))
//                    .orIn(BaseSceneAreaNodeConnectionModel::getTargetNodeId, containedAreaIds.toArray(new String[]{}))
//                    .filterOrEnd();
            List<TSceneAreaNodeConnectionModel> list = sceneNodeConnectionMapper.list(new HumanCondition().filterEqual(BaseSceneAreaNodeConnectionModel::getScene, sceneId));
            List<TSceneAreaNodeConnectionModel> result = new ArrayList<>();
            for (var l : list) {
                for (String areaId : containedAreaIds) {
                    if (l.getSourceNodeId().contains(areaId) || l.getTargetNodeId().contains(areaId)) {
                        result.add(l);
                        break;
                    }
                }
            }
            return result;
        } else {
            condition.filterEqual(BaseSceneAreaNodeConnectionModel::getScene, sceneId);
        }
        return sceneNodeConnectionMapper.list(condition);
    }

    private void loadAreaNodeConnectionBOs(List<TSceneAreaNodeConnectionBO> nodeConnections) {
        if (nodeConnections == null) {
            return;
        }

        Map<String, List<String>> outerConnectionNodeIdConnectionsMap = new HashMap<>();
        Map<String, TSceneAreaNodeConnectionBO> validConnectionMap = new HashMap<>();
        nodeConnections.forEach(nodeConnection -> {
            String sourceNodeId = nodeConnection.getSourceNodeId();
            String targetNodeId = nodeConnection.getTargetNodeId();
            if (!elementMap.containsKey(sourceNodeId) && !elementMap.containsKey(targetNodeId) &&
                    !areaMap.containsKey(sourceNodeId) && !areaMap.containsKey(targetNodeId)) {
                return;
            }

            var connectionKey = nodeConnectionKey(nodeConnection);
            if (!elementMap.containsKey(sourceNodeId) && !areaMap.containsKey(sourceNodeId)) {
                MapUtils.setDefault(outerConnectionNodeIdConnectionsMap, sourceNodeId, new ArrayList<>())
                        .add(connectionKey);
            }
            if (!elementMap.containsKey(targetNodeId) && !areaMap.containsKey(targetNodeId)) {
                MapUtils.setDefault(outerConnectionNodeIdConnectionsMap, targetNodeId, new ArrayList<>())
                        .add(connectionKey);
            }
            validConnectionMap.put(connectionKey, nodeConnection);
        });

        // 外部连接节点（其它区域节点）
        if (!outerConnectionNodeIdConnectionsMap.isEmpty()) {
            Set<String> currentOuterConnectionNodeIds = outerConnectionNodeIdConnectionsMap.keySet();
            if (StringUtils.isNotEmpty(sceneId)) {
                Set<String> existOuterConnectionNodeIds = new HashSet<>();
                sceneElementMapper.list(new HumanCondition()
                                                .select(BaseSceneElementModel::getNodeId)
                                                .filterEqual(BaseSceneElementModel::getScene, sceneId)
                                                .filterIn(BaseSceneElementModel::getNodeId, currentOuterConnectionNodeIds.toArray()))
                        .forEach(element -> existOuterConnectionNodeIds.add(element.getNodeId()));
                sceneAreaMapper.list(new HumanCondition()
                                             .select(BaseSceneAreaModel::getNodeId)
                                             .filterEqual(BaseSceneElementModel::getScene, sceneId)
                                             .filterIn(BaseSceneElementModel::getNodeId, currentOuterConnectionNodeIds.toArray()))
                        .forEach(area -> existOuterConnectionNodeIds.add(area.getNodeId()));
                outerConnectionNodeIds.addAll(existOuterConnectionNodeIds);
                currentOuterConnectionNodeIds.removeAll(existOuterConnectionNodeIds);
            }
            currentOuterConnectionNodeIds.forEach(notExistOuterConnectionNodeId ->
                                                          outerConnectionNodeIdConnectionsMap.get(notExistOuterConnectionNodeId)
                                                                  .forEach(validConnectionMap::remove));
        }

        validConnectionMap.forEach((key, connection) -> {
            areaNodeConnectionMap.put(key, connection);
            nodeIdConnectedNodeIdMap.put(connection.getSourceNodeId(), connection.getTargetNodeId());
            nodeIdConnectedNodeIdMap.put(connection.getTargetNodeId(), connection.getSourceNodeId());
        });
    }

    private boolean isLayerArea(TSceneAreaModel model) {
        return model != null && model.getType() != null && model.getType() == BaseSceneAreaModel.Type.LAYER;
    }

    private class ExistsModel {
        List<TSceneAreaModel> existsAreas;
        List<TScenePieceModel> existsPieces;
        List<TSceneAreaNodeConnectionModel> existsAreaNodeConnections;
        List<TSceneElementModel> existsElements;
        List<TSceneElementPortModel> existsElementPorts;
        List<TSceneElementPortConnectionModel> existsElementPortConnections;
        List<TSceneElementPluginModel> existsElementPlugins;
    }
}
