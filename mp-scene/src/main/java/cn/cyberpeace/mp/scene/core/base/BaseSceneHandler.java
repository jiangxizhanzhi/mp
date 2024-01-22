package cn.cyberpeace.mp.scene.core.base;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.ValidationCommon;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.basic.MapUtils;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldNames;
import cn.cyberpeace.mp.base.util.define.ListTreeNode;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneAreaBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneContextBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneDataBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneNodeBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseScenePieceBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneTopologyBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
import cn.cyberpeace.mp.scene.core.exception.SceneUpdateException;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortConnectionModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPluginModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortConnectionModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseScenePieceModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneAreaMapper;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneElementMapper;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneElementPluginMapper;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneElementPortConnectionMapper;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneElementPortMapper;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneMapper;
import cn.cyberpeace.mp.scene.repository.base.BaseScenePieceMapper;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * 基础的场景处理类，场景模板和场景实例的拓扑数据操作都是一样的
 * 对于场景拓扑分为全拓扑操作、区域拓扑操作
 * 全拓扑操作可视作特殊的区域拓扑操作，其处理逻辑都是一样的，都是对节点、连线的数据处理
 * 区域拓扑操作需要特别考虑边界连线(连线的另一端涉及到其他区域的节点)的数据处理，不能遗漏
 *
 * TODO: 部分节点模式，只对部分节点进行有限的处理
 *
 * @param <SceneModel> 表模型类   场景模板/场景实例
 * @param <SceneAreaModel> 表模型类   区域节点
 * @param <SceneElementModel> 表模型类   网元节点
 * @param <SceneElementPortModel> 表模型类   网元端口
 * @param <SceneElementPortConnectionModel> 表模型类   网元端口连接（也代表节点之间连接）
 * @param <SceneElementPluginModel> 表模型类   网元节点插件关系
 * @param <ScenePieceModel> 表模型类   碎片节点
 * @param <SceneBO>  业务模型类  场景模板/场景实例
 * @param <SceneAreaBO>  业务模型类  区域节点
 * @param <SceneElementPortBO>  业务模型类  网元端口
 * @param <SceneElementBO>  业务模型类  网元节点
 * @param <SceneElementPortConnectionBO>  业务模型类  网元端口连接（也代表节点之间连接）
 * @param <ScenePieceBO> 业务模型类  碎片节点
 * @param <SceneTopologyBO>  业务模型类  场景拓扑数据
 * @param <SceneDataBO>  业务模型类  场景数据（包含拓扑数据、插件数据）
 * @param <SceneContextBO> 场景上下文
 * @param <ScenePluginHandler> 场景插件处理接口类
 * @param <SceneHandler> 场景处理实现类
 */
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
@Getter
public abstract class BaseSceneHandler<
        SceneModel extends BaseSceneModel,
        SceneAreaModel extends BaseSceneAreaModel,
        SceneElementModel extends BaseSceneElementModel,
        SceneElementPortModel extends BaseSceneElementPortModel,
        SceneElementPortConnectionModel extends BaseSceneElementPortConnectionModel,
        SceneElementPluginModel extends BaseSceneElementPluginModel,
        ScenePieceModel extends BaseScenePieceModel,
        SceneBO extends BaseSceneBO<SceneModel, SceneBO>,
        SceneAreaBO extends BaseSceneAreaBO<SceneAreaModel, SceneAreaBO>,
        SceneElementPortBO extends BaseSceneElementPortBO<SceneElementPortModel, SceneElementPortBO, SceneElementBO>,
        SceneElementBO extends BaseSceneElementBO<SceneElementModel, SceneElementPortBO, SceneElementBO>,
        SceneElementPortConnectionBO extends BaseSceneElementPortConnectionBO<SceneElementPortConnectionModel, SceneElementPortConnectionBO>,
        ScenePieceBO extends BaseScenePieceBO<ScenePieceModel, ScenePieceBO>,
        SceneTopologyBO extends BaseSceneTopologyBO<SceneModel, SceneAreaModel, SceneElementModel, SceneElementPortModel, SceneElementPortConnectionModel, ScenePieceModel, SceneBO, SceneAreaBO, SceneElementPortBO, SceneElementBO, SceneElementPortConnectionBO, ScenePieceBO, SceneTopologyBO>,
        SceneDataBO extends BaseSceneDataBO<SceneModel, SceneAreaModel, SceneElementModel, SceneElementPortModel, SceneElementPortConnectionModel, ScenePieceModel, SceneBO, SceneAreaBO, SceneElementPortBO, SceneElementBO, SceneElementPortConnectionBO, ScenePieceBO, SceneTopologyBO, SceneDataBO>,
        SceneContextBO extends BaseSceneContextBO,
        ScenePluginHandler extends BasePluginHandler<?, ?, ?, ?>,
        SceneHandler extends BaseSceneHandler<SceneModel, SceneAreaModel, SceneElementModel, SceneElementPortModel, SceneElementPortConnectionModel, SceneElementPluginModel, ScenePieceModel,
                SceneBO, SceneAreaBO, SceneElementPortBO, SceneElementBO, SceneElementPortConnectionBO, ScenePieceBO, SceneTopologyBO, SceneDataBO, SceneContextBO, ScenePluginHandler, SceneHandler>> {

    /** 上下文 */
    private SceneContextBO context;
    /** 是否区域模式 */
    private boolean isAreaMode;
    /** 场景模板/场景实例id */
    private String sceneId;
    /** 区域模式下，当前区域id */
    private String areaId;
    /** 场景模板/场景实例 */
    private SceneBO scene;
    /** 碎片节点集合 */
    private final Map<String, ScenePieceBO> pieceMap = new ConcurrentHashMap<>();
    /** 区域节点集合 */
    private final Map<String, SceneAreaBO> areaMap = new ConcurrentHashMap<>();
    /** 网元节点集合 */
    private final Map<String, SceneElementBO> elementMap = new ConcurrentHashMap<>();
    /** 网元端口集合 */
    private final Map<String, SceneElementPortBO> elementPortMap = new ConcurrentHashMap<>();
    /** 网元端口连接集合 */
    private final Map<String, SceneElementPortConnectionBO> elementPortConnectionMap = new ConcurrentHashMap<>();
    /** 区域外连接端口id集合 */
    private final Set<String> outerConnectionPortIds = new CopyOnWriteArraySet<>();
    /** 场景插件数据，key为插件代号 */
    private final Map<String, ScenePluginDataBO> scenePluginData = new ConcurrentHashMap<>();

    // 辅助变量
    /** 端口所属节点映射 */
    private final Map<String, SceneElementBO> portIdElementMap = new ConcurrentHashMap<>();
    /** 端口连接端口映射 */
    private final Map<String, String> portIdConnectedPortIdMap = new ConcurrentHashMap<>();

    // 通过ClassFieldNames注解和实现类ClassFieldClasses注解，初始化调用ClassUtils.loadClassFields(this)设置
    private Class<SceneDataBO> sceneDataBOClass;
    private Class<SceneTopologyBO> sceneTopologyBOClass;
    private Class<SceneBO> sceneBOClass;
    private Class<SceneAreaBO> sceneAreaBOClass;
    private Class<SceneElementBO> sceneElementBOClass;
    private Class<SceneElementPortBO> sceneElementPortBOClass;
    private Class<SceneElementPortConnectionBO> sceneElementPortConnectionBOClass;
    private Class<ScenePieceBO> scenePieceBOClass;
    private Class<SceneModel> sceneModelClass;
    private Class<SceneAreaModel> sceneAreaModelClass;
    private Class<SceneElementModel> sceneElementModelClass;
    private Class<SceneElementPortModel> sceneElementPortModelClass;
    private Class<SceneElementPortConnectionModel> sceneElementPortConnectionModelClass;
    private Class<SceneElementPluginModel> sceneElementPluginModelClass;
    private Class<ScenePieceModel> scenePieceModelClass;
    private Class<ScenePluginHandler> scenePluginHandlerInterface;
    private Class<SceneHandler> sceneHandlerClass;

    protected BaseSceneMapper<SceneModel> sceneMapper;
    protected BaseSceneAreaMapper<SceneAreaModel> sceneAreaMapper;
    protected BaseSceneElementMapper<SceneElementModel> sceneElementMapper;
    protected BaseSceneElementPortMapper<SceneElementPortModel> sceneElementPortMapper;
    protected BaseSceneElementPortConnectionMapper<SceneElementPortConnectionModel> sceneElementPortConnectionMapper;
    protected BaseSceneElementPluginMapper<SceneElementPluginModel> sceneElementPluginMapper;
    protected BaseScenePieceMapper<ScenePieceModel> scenePieceMapper;

    /** 插件数据处理对象集合 */
    private final List<ScenePluginHandler> plugins = new CopyOnWriteArrayList<>();
    private final Map<String, ScenePluginHandler> pluginMap = new ConcurrentHashMap<>();
    /** 网元对应节点插件关系，一个网元节点对于多个插件 */
    private final Map<String, List<SceneElementPluginModel>> elementIdPluginsMap = new ConcurrentHashMap<>();

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

            ScenePluginHandler plugin = (ScenePluginHandler) ClassUtils.createInstance(pluginClass);
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
            ScenePluginHandler plugin = (ScenePluginHandler) ClassUtils.createInstance(pluginClass);
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
        ListTreeNode<SceneAreaBO> areaNodeTree = new ListTreeNode<>();

        Map<String, ListTreeNode<SceneAreaBO>> areaNodeMap = new HashMap<>();
        if (StringUtils.isEmpty(areaId)) {
            areaNodeMap.put(null, areaNodeTree);
            areaNodeMap.put(StringUtils.EMPTY, areaNodeTree);
        } else {
            areaNodeMap.put(areaId, areaNodeTree);
        }
        areaMap.forEach((areaNodeId, area) -> areaNodeMap.put(areaNodeId, new ListTreeNode<>(area)));

        areaNodeMap.forEach((areaNodeId, areaNode) -> {
            SceneAreaBO area = areaNode.get();
            if (area == null) {
                return;
            }

            // 去除父节点不在此区域的节点
            ListTreeNode<SceneAreaBO> parentAreaNode = areaNodeMap.getOrDefault(area.getArea(), null);
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
    private void filterAreaTree(ListTreeNode<SceneAreaBO> areaNode, Map<String, SceneAreaBO> targetAreaMap) {
        SceneAreaBO area = areaNode.get();
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

        // 节点所属区域也在区域内并且是展开的
        SceneAreaBO parentArea = nodeArea != null ? areaMap.getOrDefault(nodeArea, null) : null;
        return parentArea != null && parentArea.getOpen();
    }

    /**
     * 网元端口连接的唯一标识(场景内唯一) 源端口id:目的端口id
     * @param elementPortConnection 表模型-网元端口连接
     * @return 连接的唯一标识
     */
    private String elementPortConnectionKey(SceneElementPortConnectionModel elementPortConnection) {
        return StringUtils.joinWith(":", elementPortConnection.getSourcePortId(),
                elementPortConnection.getTargetPortId());
    }

    /**
     * 网元端口连接的唯一标识(场景内唯一) 源端口id:目的端口id
     * @param elementPortConnection 业务模型-网元端口连接
     * @return 连接的唯一标识
     */
    private String elementPortConnectionKey(SceneElementPortConnectionBO elementPortConnection) {
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
    private String elementPluginKey(SceneElementPluginModel elementPlugin) {
        return elementPluginKey(elementPlugin.getSceneElement(), elementPlugin.getPluginKey());
    }

    /**
     * 载入场景基础数据
     * @param scene 业务模型-场景基础数据
     */
    private void loadSceneBO(SceneBO scene) {
        if (scene == null) {
            return;
        }

        this.scene = scene;
    }

    /**
     * 载入区域节点数据
     * @param areas 业务模型-区域节点
     */
    private void loadAreaBOs(List<SceneAreaBO> areas) {
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
    private void loadPieceBOs(List<ScenePieceBO> pieces) {
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
    private void loadElementBOs(List<SceneElementBO> elements) {
        if (elements == null) {
            return;
        }

        elements.forEach(element -> {
            if (!isValidAreaNode(element)) {
                return;
            }

            elementMap.put(element.getId(), element);

            List<SceneElementPortBO> elementPorts = element.getPorts();
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
    private void loadConnectionBOs(List<SceneElementPortConnectionBO> connections) {
        if (connections == null) {
            return;
        }

        Map<String, List<String>> outerConnectionPortIdConnectionsMap = new HashMap<>();
        Map<String, SceneElementPortConnectionBO> validConnectionMap = new HashMap<>();
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
    private void loadScene(SceneModel scene) {
        if (scene == null) {
            return;
        }

        SceneBO SceneBO = ClassUtils.createInstance(sceneBOClass);
        SceneBO.fromModel(scene);
        this.scene = SceneBO;
    }

    /**
     * 载入区域节点数据
     * @param areas 表模型-区域节点
     */
    private void loadAreas(List<SceneAreaModel> areas) {
        if (areas == null) {
            return;
        }

        areas.forEach(area -> {
            SceneAreaBO areaBO = ClassUtils.createInstance(sceneAreaBOClass);
            areaBO.fromModel(area);
            areaMap.put(areaBO.getId(), areaBO);
        });
    }

    /**
     * 载入碎片节点数据
     * @param pieces 表模型-碎片节点
     */
    private void loadPieces(List<ScenePieceModel> pieces) {
        if (pieces == null) {
            return;
        }

        pieces.forEach(piece -> {
            ScenePieceBO pieceBO = ClassUtils.createInstance(scenePieceBOClass);
            pieceBO.fromModel(piece);
            pieceMap.put(pieceBO.getId(), pieceBO);
        });
    }

    /**
     * 载入网元节点数据
     * @param elements 表模型-网元节点
     */
    private void loadElements(List<SceneElementModel> elements) {
        if (elements == null) {
            return;
        }
        elements.forEach(element -> {
            SceneElementBO elementBO = ClassUtils.createInstance(sceneElementBOClass);
            elementBO.fromModel(element);
            elementMap.put(elementBO.getId(), elementBO);
        });
    }

    /**
     * 载入网元端口数据
     * @param elementPorts 表模型-网元端口
     */
    private void loadElementPorts(List<SceneElementPortModel> elementPorts) {
        if (elementPorts == null) {
            return;
        }
        Map<String, SceneElementBO> elementIdElementBOMap = new HashMap<>();
        for (SceneElementBO SceneElementBO : elementMap.values()) {
            if (elementIdElementBOMap.put(SceneElementBO.getEid(), SceneElementBO) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        Collections.sort(elementPorts, Comparator.comparingInt(BaseSceneElementPortModel::getOrder));
        elementPorts.forEach(elementPort -> {
            SceneElementBO elementBO = elementIdElementBOMap.getOrDefault(elementPort.getSceneElement(), null);
            if (elementBO == null) {
                return;
            }

            SceneElementPortBO elementPortBO = ClassUtils.createInstance(sceneElementPortBOClass);
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
    private void loadConnections(List<SceneElementPortConnectionModel> connections) {
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

            SceneElementPortConnectionBO elementPortConnectionBO = ClassUtils.createInstance(sceneElementPortConnectionBOClass);
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
    private void loadElementPlugins(List<SceneElementPluginModel> elementPlugins) {
        elementPlugins.forEach(elementPlugin -> {
            String elementId = elementPlugin.getSceneElement();
            List<SceneElementPluginModel> plugins = elementIdPluginsMap.getOrDefault(elementId, new ArrayList<>());
            plugins.add(elementPlugin);
            elementIdPluginsMap.put(elementId, plugins);
        });
    }

    /**
     * 从数据库读取场景
     * @return 表模型-场景对象
     */
    private SceneModel readScene() {
        return sceneMapper.get(sceneId);
    }

    /**
     * 从数据库读取区域节点
     * @param areaIds 父区域节点
     * @return 表模型-区域节点
     */
    private List<SceneAreaModel> readAreas(String... areaIds) {
        HumanCondition condition = new HumanCondition().filterEqual(BaseSceneAreaModel::getScene, sceneId);

        String[] filterAreaIds = areaIds;
        if (filterAreaIds.length == 0 && isAreaMode) {
            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            filterAreaIds = new String[]{currentAreaId};
        }
        if (filterAreaIds.length > 0) {
            condition.filterIn(BaseSceneAreaModel::getArea, filterAreaIds);
        }

        List<SceneAreaModel> areas = sceneAreaMapper.list(condition);

        if (isAreaMode) {
            List<String> list = new ArrayList<>();
            for (SceneAreaModel area : areas) {
                if (area.getOpen()) {
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
    private List<ScenePieceModel> readPieces(String... areaIds) {
        List<ScenePieceModel> pieces;
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
    private List<SceneElementModel> readElements(String... areaIds) {
        List<SceneElementModel> elements;
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
    private List<SceneElementPortModel> readElementPorts(String... areaIds) {
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

    /**
     * 从数据库读取网元端口连接
     * @param areaIds 父区域节点
     * @return 表模型-网元端口连接
     */
    private List<SceneElementPortConnectionModel> readElementPortConnections(String... areaIds) {
        HumanCondition condition = new HumanCondition();

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

    /**
     * 从数据库读取网元插件关系
     * @param areaIds 父区域节点
     * @return 表模型-网元插件关系
     */
    private List<SceneElementPluginModel> readElementPlugins(String... areaIds) {
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
        if (updateResult < diff.getUpdatingTargets().size()) {
            throw new SceneUpdateException("Scene diff update failed");
        }
    }

    /**
     * 保存场景属性数据
     */
    private boolean saveScene() {
        SceneModel sceneObj = scene.toModel();
        boolean result = sceneMapper.save(sceneObj);
        sceneId = sceneObj.getId();
        scene.fromModel(sceneObj);

        plugins.forEach(plugin -> plugin.setSourceSceneId(sceneId));

        return result;
    }

    /**
     * 保存区域节点
     * @param existAreaMap 已存在的区域节点
     * @return 更新的区域节点，包含添加的、更新属性的、删除的
     */
    private DiffTargets<SceneAreaModel> saveAreas(Map<String, SceneAreaModel> existAreaMap) {
        Map<String, SceneAreaModel> savingAreaMap = areaMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    SceneAreaModel area = entry.getValue().toModel();
                    area.setScene(sceneId);
                    return area;
                }));
        DiffTargets<SceneAreaModel> diff = diffTargets(savingAreaMap, existAreaMap);
        if (isAreaMode) {
            // 删除闭合的子区域
            for (SceneAreaModel removingArea: diff.getRemovingTargets()) {
                if (!Boolean.TRUE.equals(removingArea.getOpen())) {
                    SceneHandler SceneHandler = ClassUtils.createInstance(sceneHandlerClass);
                    SceneHandler.init(sceneId, removingArea.getNodeId(), isAreaMode, context);
                    SceneHandler.deleteTopology();
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
    private DiffTargets<ScenePieceModel> savePieces(Map<String, ScenePieceModel> existPieceMap) {
        Map<String, ScenePieceModel> savingPieceMap = pieceMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    ScenePieceModel piece = entry.getValue().toModel();
                    piece.setScene(sceneId);
                    return piece;
                }));
        DiffTargets<ScenePieceModel> diff = diffTargets(savingPieceMap, existPieceMap);
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
    private DiffTargets<SceneElementModel> saveElements(Map<String, SceneElementModel> existElementMap) {
        Map<String, SceneElementModel> savingElementMap = elementMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    SceneElementModel element = entry.getValue().toModel();
                    element.setScene(sceneId);
                    return element;
                }));
        DiffTargets<SceneElementModel> diff = diffTargets(savingElementMap, existElementMap);
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
    private DiffTargets<SceneElementPortModel> saveElementPorts(Map<String, SceneElementPortModel> existElementPortMap) {
        Map<String, SceneElementPortModel> savingElementPortMap = new HashMap<>();
        elementMap.values().forEach(element -> {
            List<SceneElementPortBO> elementPorts = element.getPorts();
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
                SceneElementPortModel elementPort = elementPortBO.toModel();
                elementPort.setScene(sceneId);
                elementPort.setSceneElement(elementId);
                savingElementPortMap.put(elementPort.getPortId(), elementPort);
            });
        });

        DiffTargets<SceneElementPortModel> diff = diffTargets(savingElementPortMap, existElementPortMap);
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
    private DiffTargets<SceneElementPortConnectionModel> saveElementPortConnections(Map<String, SceneElementPortConnectionModel> existElementPortConnectionMap) {
        Map<String, SceneElementPortModel> outerElementPortMap = new HashMap<>();
        if (!outerConnectionPortIds.isEmpty()) {
            sceneElementPortMapper.list(new HumanCondition()
                    .select(BaseSceneElementPortModel::getId, BaseSceneElementPortModel::getSceneElement,
                            BaseSceneElementPortModel::getPortId)
                    .filterEqual(BaseSceneElementPortModel::getScene, sceneId)
                    .filterIn(BaseSceneElementPortModel::getPortId, outerConnectionPortIds.toArray(new String[0])))
                    .forEach(elementPort -> outerElementPortMap.put(elementPort.getPortId(), elementPort));
        }

        Map<String, SceneElementBO> portIdElementMap = getPortIdElementMap();
        Map<String, SceneElementPortConnectionModel> savingElementPortConnectionMap = new HashMap<>();
        elementPortConnectionMap.forEach((key, elementPortConnectionBO) -> {
            SceneElementPortConnectionModel elementPortConnection = elementPortConnectionBO.toModel();
            elementPortConnection.setScene(sceneId);
            SceneElementPortBO sourcePortBO = elementPortMap.getOrDefault(elementPortConnection.getSourcePortId(), null);
            String sourcePortId;
            String sourceElementId;
            if (sourcePortBO == null) {
                SceneElementPortModel sourcePort = outerElementPortMap.getOrDefault(elementPortConnection.getSourcePortId(), null);
                sourcePortId = sourcePort == null ? null : sourcePort.getId();
                sourceElementId = sourcePort == null ? null : sourcePort.getSceneElement();
            } else {
                sourcePortId = sourcePortBO.getEid();
                SceneElementBO sourceElement = portIdElementMap.getOrDefault(sourcePortBO.getId(), null);
                sourceElementId = sourceElement == null ? null : sourceElement.getEid();
            }
            SceneElementPortBO targetPortBO = elementPortMap.getOrDefault(elementPortConnection.getTargetPortId(), null);
            String targetPortId;
            String targetElementId;
            if (targetPortBO == null) {
                SceneElementPortModel targetPort = outerElementPortMap.getOrDefault(elementPortConnection.getTargetPortId(), null);
                targetPortId = targetPort == null ? null : targetPort.getId();
                targetElementId = targetPort == null ? null : targetPort.getSceneElement();
            } else {
                targetPortId = targetPortBO.getEid();
                SceneElementBO targetElement = portIdElementMap.getOrDefault(targetPortBO.getId(), null);
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

        DiffTargets<SceneElementPortConnectionModel> diff = diffTargets(savingElementPortConnectionMap, existElementPortConnectionMap);
        saveDiff(sceneElementPortConnectionMapper, diff);
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
    private DiffTargets<SceneElementPluginModel> saveElementPlugins(Map<String, SceneElementPluginModel> existElementPluginMap) {
        Map<String, SceneElementBO> portIdElementMap = getPortIdElementMap();
        Map<String, SceneElementPluginModel> savingElementPluginMap = new HashMap<>();
        scenePluginData.forEach((pluginKey, pluginData) -> {
            Map<String, Map<String, Object>> pluginElements = pluginData.getElements();
            if (pluginElements != null) {
                pluginElements.forEach((nodeId, nodePluginData) -> {
                    SceneElementBO element = elementMap.getOrDefault(nodeId, null);
                    if (element == null) {
                        return;
                    }

                    String key = elementPluginKey(element.getEid(), pluginKey);
                    if (!savingElementPluginMap.containsKey(key)) {
                        SceneElementPluginModel elementPlugin = ClassUtils.createInstance(sceneElementPluginModelClass);
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
                    SceneElementBO element = portIdElementMap.getOrDefault(portId, null);
                    if (element == null) {
                        return;
                    }

                    String key = elementPluginKey(element.getEid(), pluginKey);
                    if (!savingElementPluginMap.containsKey(key)) {
                        SceneElementPluginModel elementPlugin = ClassUtils.createInstance(sceneElementPluginModelClass);
                        elementPlugin.setScene(sceneId);
                        elementPlugin.setSceneElement(element.getEid());
                        elementPlugin.setPluginKey(pluginKey);
                        savingElementPluginMap.put(key, elementPlugin);
                    }
                });
            }
        });

        DiffTargets<SceneElementPluginModel> diff = diffTargets(savingElementPluginMap, existElementPluginMap);
        saveDiff(sceneElementPluginMapper, diff);
        loadElementPlugins(ListUtils.union(diff.getAddingTargets(), diff.getUpdatingTargets()));
        return diff;
    }

    /**
     * 获取端口id和端口所属网元映射
     * @return 端口id和端口所属网元映射
     */
    private Map<String, SceneElementBO> getPortIdElementMap() {
        Map<String, SceneElementBO> portIdElementMap = new HashMap<>();
        elementMap.forEach((elementId, elementBO) -> {
            List<SceneElementPortBO> elementPorts = elementBO.getPorts();
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
     * @param SceneDataBO 场景业务数据
     */
    private void fromData(SceneDataBO SceneDataBO) {
        // 验证数据字段
        Set<ConstraintViolation<SceneDataBO>> validateResult = ValidationCommon.validate(SceneDataBO);
        ValidationCommon.handleValidateResult(validateResult);

        SceneTopologyBO topology = SceneDataBO.getTopology();
        SceneBO srcScene = (topology == null || topology.getScene() == null) ? ClassUtils.createInstance(sceneBOClass)
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

        loadScenePluginData(SceneDataBO.getPlugins());
    }

    /**
     * 纠正场景数据
     */
    protected void correctData() {
        plugins.forEach(plugin -> plugin.correctData());
    }

    /**
     * 从场景业务数据初始化
     * @param SceneDataBO 场景业务数据
     * @param isAreaMode 是否区域模式
     */
    public void init(SceneDataBO SceneDataBO, boolean isAreaMode, SceneContextBO context) {
        ClassUtils.loadClassFields(this);
        this.initMapper();
        this.context = context;
        this.isAreaMode = isAreaMode;
        fromData(SceneDataBO);
        if (!StringUtils.isEmpty(sceneId) && readScene() == null) {
            throw new SceneNotExistException("Scene " + sceneId + " not exist");
        }
        initPlugins(scenePluginData);

        // 场景业务数据需要纠正
        correctData();
    }

    protected abstract void initMapper();

    /**
     * 从场景id初始化
     * @param sceneId 场景id
     * @param areaId 区域id
     * @param isAreaMode 是否区域模式
     */
    public void init(String sceneId, String areaId, boolean isAreaMode, SceneContextBO context) {
        ClassUtils.loadClassFields(this);
        this.initMapper();
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
        for (ScenePluginHandler plugin : plugins) {
            plugin.validate();
        }
    }

    public void beforeInitPlugin(ScenePluginHandler plugin) {}
    public void afterInitPlugin(ScenePluginHandler plugin) {}

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
    public SceneDataBO toData() {
        SceneTopologyBO topologyBO = ClassUtils.createInstance(sceneTopologyBOClass);
        topologyBO.setScene(scene);
        topologyBO.setAreaId(areaId);
        topologyBO.setAreas(new ArrayList<>(areaMap.values()));
        topologyBO.setPieces(new ArrayList<>(pieceMap.values()));
        topologyBO.setElements(new ArrayList<>(elementMap.values()));
        topologyBO.setConnections(new ArrayList<>(elementPortConnectionMap.values()));

        SceneDataBO dataBO = ClassUtils.createInstance(sceneDataBOClass);
        dataBO.setTopology(topologyBO);
        dataBO.setPlugins(scenePluginData);
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

        loadScene(readScene());
        if (scene == null) {
            return;
        }
        loadAreas(readAreas());
        String[] filterAreaIds = new String[0];
        if (isAreaMode) {
            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            List<String> list = new ArrayList<>();
            for (SceneAreaBO SceneAreaBO : areaMap.values()) {
                if (SceneAreaBO.getOpen()) {
                    String id = SceneAreaBO.getId();
                    list.add(id);
                }
            }
            filterAreaIds = ArrayUtils.addAll(list.toArray(new String[0]), currentAreaId);
        }
        loadPieces(readPieces(filterAreaIds));
        loadElements(readElements(filterAreaIds));
        loadElementPorts(readElementPorts(filterAreaIds));
        loadConnections(readElementPortConnections(filterAreaIds));
        loadElementPlugins(readElementPlugins(filterAreaIds));
        Map<String, List<String>> pluginKeyElementIds = new HashMap<>();
        Map<String, List<String>> pluginKeyElementPortIds = new HashMap<>();
        Map<String, SceneElementBO> elementEidElementBOMap = new HashMap<>();
        elementMap.forEach((elementBOId, elementBO) -> {
            elementEidElementBOMap.put(elementBO.getEid(), elementBO);
        });
        elementIdPluginsMap.forEach((elementId, elementPlugins) -> {
            SceneElementBO element = elementEidElementBOMap.getOrDefault(elementId, null);
            if (element == null) {
                return;
            }

            List<SceneElementPortBO> elementPorts = element.getPorts();

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

    /**
     * 保存当前场景数据
     */
    public void save() {
        boolean isCreate = StringUtils.isEmpty(sceneId);
        saveScene();

        Map<String, SceneAreaModel> existAreaMap;
        if (isCreate) {
            existAreaMap = new HashMap<>();
        } else {
            Map<String, SceneAreaModel> map = new HashMap<>();
            for (SceneAreaModel x : readAreas()) {
                if (map.put(x.getNodeId(), x) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }
            existAreaMap = map;
        }
        String[] filterAreaIds = new String[0];
        if (isAreaMode) {
            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            List<String> list = new ArrayList<>();
            for (SceneAreaModel SceneAreaModel : existAreaMap.values()) {
                if (SceneAreaModel.getOpen()) {
                    String nodeId = SceneAreaModel.getNodeId();
                    list.add(nodeId);
                }
            }
            filterAreaIds = ArrayUtils.addAll(list.toArray(new String[0]), currentAreaId);
        }
        Map<String, ScenePieceModel> existPieceMap;
        if (isCreate) {
            existPieceMap = new HashMap<>();
        } else {
            Map<String, ScenePieceModel> map = new HashMap<>();
            for (ScenePieceModel ScenePieceModel : readPieces(filterAreaIds)) {
                if (map.put(ScenePieceModel.getNodeId(), ScenePieceModel) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }
            existPieceMap = map;
        }
        Map<String, SceneElementModel> existElementMap;
        if (isCreate) {
            existElementMap = new HashMap<>();
        } else {
            Map<String, SceneElementModel> map = new HashMap<>();
            for (SceneElementModel SceneElementModel : readElements(filterAreaIds)) {
                if (map.put(SceneElementModel.getNodeId(), SceneElementModel) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }
            existElementMap = map;
        }
        Map<String, SceneElementPortModel> existElementPortMap;
        if (isCreate) {
            existElementPortMap = new HashMap<>();
        } else {
            Map<String, SceneElementPortModel> map = new HashMap<>();
            for (SceneElementPortModel SceneElementPortModel : readElementPorts(filterAreaIds)) {
                if (map.put(SceneElementPortModel.getPortId(), SceneElementPortModel) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }
            existElementPortMap = map;
        }
        Map<String, SceneElementPortConnectionModel> existElementPortConnectionMap;
        if (isCreate) {
            existElementPortConnectionMap = new HashMap<>();
        } else {
            Map<String, SceneElementPortConnectionModel> map = new HashMap<>();
            for (SceneElementPortConnectionModel SceneElementPortConnectionModel : readElementPortConnections(filterAreaIds)) {
                if (map.put(this.elementPortConnectionKey(SceneElementPortConnectionModel), SceneElementPortConnectionModel) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }
            existElementPortConnectionMap = map;
        }
        Map<String, SceneElementPluginModel> existElementPluginMap;
        if (isCreate) {
            existElementPluginMap = new HashMap<>();
        } else {
            Map<String, SceneElementPluginModel> map = new HashMap<>();
            for (SceneElementPluginModel SceneElementPluginModel : readElementPlugins(filterAreaIds)) {
                if (map.put(this.elementPluginKey(SceneElementPluginModel), SceneElementPluginModel) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }
            existElementPluginMap = map;
        }

        // 保存区域，可能会跨区域连接
        DiffTargets<SceneAreaModel> diffAreas = saveAreas(existAreaMap);
        DiffTargets<ScenePieceModel> diffPieces = savePieces(existPieceMap);
        DiffTargets<SceneElementModel> diffElements = saveElements(existElementMap);
        DiffTargets<SceneElementPortModel> diffElementPorts = saveElementPorts(existElementPortMap);
        // 此时可能存在已被删除的跨区域连接，可优化
        if (isAreaMode && !existElementPortConnectionMap.isEmpty()) {
            List<String> list = new ArrayList<>();
            for (SceneElementPortConnectionModel c01 : existElementPortConnectionMap.values()) {
                String id = c01.getId();
                list.add(id);
            }
            Map<String, SceneElementPortConnectionModel> map = new HashMap<>();
            for (SceneElementPortConnectionModel SceneElementPortConnectionModel : sceneElementPortConnectionMapper.list(new HumanCondition()
                    .filterIn(cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel::getId, list.toArray(new String[0]))
            )) {
                if (map.put(this.elementPortConnectionKey(SceneElementPortConnectionModel), SceneElementPortConnectionModel) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }
            existElementPortConnectionMap = map;
        }
        DiffTargets<SceneElementPortConnectionModel> diffElementPortConnections = saveElementPortConnections(existElementPortConnectionMap);
        // elementPlugin 由于通过服务端id映射，在拓扑保存后才能获取
        DiffTargets<SceneElementPluginModel> diffElementPlugins = saveElementPlugins(existElementPluginMap);

        // 保存插件数据
        for (ScenePluginHandler plugin : plugins) {
            plugin.saveScene();
            List<String> list = new ArrayList<>();
            for (SceneElementModel SceneElementModel01 : diffElements.getAddingTargets()) {
                String nodeId = SceneElementModel01.getNodeId();
                list.add(nodeId);
            }
            plugin.createElements(list.toArray(new String[0]));
            List<String> result = new ArrayList<>();
            for (SceneElementModel SceneElementModel01 : diffElements.getUpdatingTargets()) {
                String nodeId = SceneElementModel01.getNodeId();
                result.add(nodeId);
            }
            plugin.updateElements(result.toArray(new String[0]));
            List<String> list1 = new ArrayList<>();
            for (SceneElementModel SceneElementModel : diffElements.getRemovingTargets()) {
                String nodeId = SceneElementModel.getNodeId();
                list1.add(nodeId);
            }
            plugin.deleteElements(list1.toArray(new String[0]));
            List<String> result1 = new ArrayList<>();
            for (SceneElementPortModel SceneElementPortModel01 : diffElementPorts.getAddingTargets()) {
                String portId = SceneElementPortModel01.getPortId();
                result1.add(portId);
            }
            plugin.createElementPorts(result1.toArray(new String[0]));
            List<String> list2 = new ArrayList<>();
            for (SceneElementPortModel SceneElementPortModel01 : diffElementPorts.getUpdatingTargets()) {
                String portId = SceneElementPortModel01.getPortId();
                list2.add(portId);
            }
            plugin.updateElementPorts(list2.toArray(new String[0]));
            List<String> result2 = new ArrayList<>();
            for (SceneElementPortModel SceneElementPortModel : diffElementPorts.getRemovingTargets()) {
                String portId = SceneElementPortModel.getPortId();
                result2.add(portId);
            }
            plugin.deleteElementPorts(result2.toArray(new String[0]));
        }
    }

    /**
     * 删除场景数据
     */
    public boolean delete() {
        deleteTopology();

        // 删除插件数据
        plugins.forEach(plugin -> plugin.deleteScene());

        return sceneMapper.delete(sceneId);
    }

    /**
     * 删除拓扑数据
     */
    public void deleteTopology() {
        List<SceneAreaModel> areas = readAreas();
        String[] filterAreaIds = new String[0];
        if (isAreaMode) {
            // 递归删除闭合的子区域
            for (SceneAreaModel removingArea: areas) {
                if (!Boolean.TRUE.equals(removingArea.getOpen())) {
                    SceneHandler SceneHandler = ClassUtils.createInstance(sceneHandlerClass);
                    SceneHandler.init(sceneId, removingArea.getNodeId(), isAreaMode, context);
                    SceneHandler.deleteTopology();
                }
            }

            String currentAreaId = areaId == null ? StringUtils.EMPTY : areaId;
            List<String> list = new ArrayList<>();
            for (SceneAreaModel SceneAreaModel : areas) {
                if (SceneAreaModel.getOpen()) {
                    String nodeId = SceneAreaModel.getNodeId();
                    list.add(nodeId);
                }
            }
            filterAreaIds = ArrayUtils.addAll(list.toArray(new String[0]), currentAreaId);
        }
        List<ScenePieceModel> pieces = readPieces(filterAreaIds);
        List<SceneElementModel> elements = readElements(filterAreaIds);
        List<SceneElementPortModel> elementPorts = readElementPorts(filterAreaIds);
        List<SceneElementPluginModel> elementPlugins = readElementPlugins(filterAreaIds);
        if (!areas.isEmpty()) {
            List<String> list = new ArrayList<>();
            for (SceneAreaModel SceneAreaModel : areas) {
                String id = SceneAreaModel.getId();
                list.add(id);
            }
            sceneAreaMapper.bulkDelete(list.toArray(new String[0]));
        }
        if (!pieces.isEmpty()) {
            List<String> list = new ArrayList<>();
            for (ScenePieceModel ScenePieceModel : pieces) {
                String id = ScenePieceModel.getId();
                list.add(id);
            }
            scenePieceMapper.bulkDelete(list.toArray(new String[0]));
        }
        if (!elements.isEmpty()) {
            List<String> list = new ArrayList<>();
            for (SceneElementModel SceneElementModel : elements) {
                String id = SceneElementModel.getId();
                list.add(id);
            }
            sceneElementMapper.bulkDelete(list.toArray(new String[0]));
        }
        if (!elementPorts.isEmpty()) {
            List<String> list = new ArrayList<>();
            for (SceneElementPortModel SceneElementPortModel : elementPorts) {
                String id = SceneElementPortModel.getId();
                list.add(id);
            }
            sceneElementPortMapper.bulkDelete(list.toArray(new String[0]));
        }
        if (!elementPlugins.isEmpty()) {
            List<String> list = new ArrayList<>();
            for (SceneElementPluginModel SceneElementPluginModel : elementPlugins) {
                String id = SceneElementPluginModel.getId();
                list.add(id);
            }
            sceneElementPluginMapper.bulkDelete(list.toArray(new String[0]));
        }

        List<SceneElementPortConnectionModel> elementPortConnections = readElementPortConnections(filterAreaIds);
        if (!elementPortConnections.isEmpty()) {
            List<String> list = new ArrayList<>();
            for (SceneElementPortConnectionModel SceneElementPortConnectionModel : elementPortConnections) {
                String id = SceneElementPortConnectionModel.getId();
                list.add(id);
            }
            sceneElementPortConnectionMapper.bulkDelete(list.toArray(new String[0]));
        }

        // 删除插件数据
        plugins.forEach(plugin -> {
            List<String> list = new ArrayList<>();
            for (SceneElementModel SceneElementModel : elements) {
                String nodeId = SceneElementModel.getNodeId();
                list.add(nodeId);
            }
            plugin.deleteElements(list.toArray(new String[0]));
            List<String> result = new ArrayList<>();
            for (SceneElementPortModel SceneElementPortModel : elementPorts) {
                String portId = SceneElementPortModel.getPortId();
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
        SceneElementBO elementBO = getElement(nodeId);
        SceneElementModel element = elementBO.toModel();
        element.setScene(sceneId);
        sceneElementMapper.update(element);

        List<SceneElementPortBO> elementPortBOS = elementBO.getPorts();
        if (elementPortBOS != null && !elementPortBOS.isEmpty()) {
            List<SceneElementPortModel> addPorts = new ArrayList<>();
            List<SceneElementPortModel> updatePorts = new ArrayList<>();
            for (SceneElementPortBO elementPortBO : elementPortBOS) {
                SceneElementPortModel port = elementPortBO.toModel();
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
            List<SceneElementPortModel> list = sceneElementPortMapper
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
            for (SceneElementPortModel SceneElementPortModel : list) {
                String portId = SceneElementPortModel.getPortId();
                result.add(portId);
            }
            List<String> deleteIds = new ArrayList<>();
            for (SceneElementPortModel p : list) {
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

        List<SceneElementPortBO> elementPortBOS = getElement(nodeId).getPorts();
        for (ScenePluginHandler plugin : plugins) {
            plugin.updateElements(nodeId);
            if (elementPortBOS != null && !elementPortBOS.isEmpty()) {
                List<String> result = new ArrayList<>();
                for (SceneElementPortBO SceneElementPortBO : elementPortBOS) {
                    String id = SceneElementPortBO.getId();
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
        ScenePluginHandler plugin = pluginKey != null ? pluginMap.getOrDefault(pluginKey, null) : null;
        return plugin == null ? null : (H9) plugin;
    }

    /**
     * 获取插件全局数据
     * @param pluginKey 插件代号
     */
    public <S9> S9 getPluginScene(String pluginKey) {
        ScenePluginHandler plugin = pluginKey != null ? pluginMap.getOrDefault(pluginKey, null) : null;
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
        ScenePluginHandler plugin = pluginKey != null ? pluginMap.getOrDefault(pluginKey, null) : null;
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
        ScenePluginHandler plugin = pluginKey != null ? pluginMap.getOrDefault(pluginKey, null) : null;
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
    public SceneElementBO getElement(String nodeId) {
        return nodeId != null ? elementMap.getOrDefault(nodeId, null) : null;
    }

    /**
     * 获取网元连接的网元
     * @param nodeId 网元节点id
     */
    public List<SceneElementBO> getElementConnectedElements(String nodeId) {
        List<SceneElementPortBO> connectedPorts = getElementConnectedPorts(nodeId);
        Map<String, SceneElementBO> connectedElementMap = new HashMap<>();
        connectedPorts.forEach(port -> {
            SceneElementBO element = getPortElement(port.getId());
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
    public List<SceneElementPortBO> getElementConnectedPorts(String nodeId) {
        SceneElementBO ele = getElement(nodeId);
        List<SceneElementPortBO> ports = ele.getPorts();
        if (ports == null) {
            return new ArrayList<>();
        }
        List<SceneElementPortBO> connectedPorts = new ArrayList<>();
        ports.forEach(port -> {
            SceneElementPortBO connectedPort = getPortConnectedPort(port.getId());
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
        SceneElementBO ele = getElement(nodeId);
        List<SceneElementPortBO> ports = ele.getPorts();
        if (ports == null) {
            return false;
        }
        for (SceneElementPortBO port: ports) {
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
    public SceneElementPortBO getPort(String portId) {
        return portId != null ? elementPortMap.getOrDefault(portId, null) : null;
    }

    /**
     * 获取端口所属网元数据
     * @param portId 端口id
     */
    public SceneElementBO getPortElement(String portId) {
        return portId != null ? portIdElementMap.getOrDefault(portId, null) : null;
    }

    /**
     * 获取端口连接的端口
     * @param portId 端口id
     */
    public SceneElementPortBO getPortConnectedPort(String portId) {
        String connectedPortId = portId != null ? portIdConnectedPortIdMap.getOrDefault(portId, null) : null;
        return getPort(connectedPortId);
    }

    /**
     * 获取端口连接的节点
     * @param portId 端口id
     */
    public SceneElementBO getPortConnectedElement(String portId) {
        String connectedPortId = portId != null ? portIdConnectedPortIdMap.getOrDefault(portId, null) : null;
        return getPortElement(connectedPortId);
    }

    /**
     * 获取源端口所属的连线
     * @param portId 端口id
     */
    public SceneElementPortConnectionBO getPortBelongConnection(String portId) {
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
        return (SceneElementPortConnectionBO) baseSceneElementPortConnectionBO;
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

    public void addPort(String portId, SceneElementBO element) {
        this.portIdElementMap.put(portId, element);
    }
}
