package cn.cyberpeace.mp.scene.core.base;

import cn.cyberpeace.mp.base.annotation.BeanFieldNames;
import cn.cyberpeace.mp.base.annotation.ClassFieldNames;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.ListTreeNode;
import cn.cyberpeace.mp.scene.core.bo.base.*;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
import cn.cyberpeace.mp.scene.core.exception.SceneUpdateException;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel;
import cn.cyberpeace.mp.scene.model.base.*;
import cn.cyberpeace.mp.scene.repository.base.*;
import lombok.Getter;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基础的场景处理类，场景模板和场景实例的拓扑数据操作都是一样的
 * 对于场景拓扑分为全拓扑操作、区域拓扑操作
 * 全拓扑操作可视作特殊的区域拓扑操作，其处理逻辑都是一样的，都是对节点、连线的数据处理
 * 区域拓扑操作需要特别考虑边界连线(连线的另一端涉及到其他区域的节点)的数据处理，不能遗漏
 *
 * @param <S0> 表模型类   场景模板/场景实例
 * @param <A0> 表模型类   区域节点
 * @param <E0> 表模型类   网元节点
 * @param <P0> 表模型类   网元端口
 * @param <C0> 表模型类   网元端口连接（也代表节点之间连接）
 * @param <P1> 表模型类   网元节点插件关系
 * @param <S>  业务模型类  场景模板/场景实例
 * @param <A>  业务模型类  区域节点
 * @param <P>  业务模型类  网元端口
 * @param <E>  业务模型类  网元节点
 * @param <C>  业务模型类  网元端口连接（也代表节点之间连接）
 * @param <T>  业务模型类  场景拓扑数据
 * @param <D>  业务模型类  场景数据（包含拓扑数据、插件数据）
 * @param <M1> mapper类  场景模板/场景实例
 * @param <M2> mapper类  区域节点
 * @param <M3> mapper类  网元节点
 * @param <M4> mapper类  网元端口
 * @param <M5> mapper类  网元端口连接（也代表节点之间连接）
 * @param <M6> mapper类  网元节点插件关系
 * @param <P2> 场景插件处理接口类
 * @param <H> 场景处理实现类
 */
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
        "sceneElementPluginModelClass",
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
})
@Getter
public abstract class BaseSceneHandler<
        S0 extends BaseSceneModel,
        A0 extends BaseSceneAreaModel,
        E0 extends BaseSceneElementModel,
        P0 extends BaseSceneElementPortModel,
        C0 extends BaseSceneElementPortConnectionModel,
        P1 extends BaseSceneElementPluginModel,
        S extends BaseSceneBO<S0, S>,
        A extends BaseSceneAreaBO<A0, A>,
        P extends BaseSceneElementPortBO<P0, P>,
        E extends BaseSceneElementBO<E0, P, E>,
        C extends BaseSceneElementPortConnectionBO<C0, C>,
        T extends BaseSceneTopologyBO<S0, A0, E0, P0, C0, S, A, P, E, C, T>,
        D extends BaseSceneDataBO<S0, A0, E0, P0, C0, S, A, P, E, C, T, D>,
        M1 extends BaseSceneMapper<S0>,
        M2 extends BaseSceneAreaMapper<A0>,
        M3 extends BaseSceneElementMapper<E0>,
        M4 extends BaseSceneElementPortMapper<P0>,
        M5 extends BaseSceneElementPortConnectionMapper<C0>,
        M6 extends BaseSceneElementPluginMapper<P1>,
        C2 extends BaseSceneContextBO,
        P2 extends BasePluginHandler<?, ?, ?, ?>,
        H extends BaseSceneHandler<S0, A0, E0, P0, C0, P1, S, A, P, E, C, T, D, M1, M2, M3, M4, M5, M6, C2, P2, H>> {

    /** 上下文 */
    private C2 context;
    /** 是否区域模式 */
    private boolean isAreaMode;
    /** 场景模板/场景实例id */
    private String sceneId;
    /** 区域模式下，当前区域id */
    private String areaId;
    /** 场景模板/场景实例 */
    private S scene;
    /** 区域节点集合 */
    private final Map<String, A> areaMap = new HashMap<>();
    /** 网元节点集合 */
    private final Map<String, E> elementMap = new HashMap<>();
    /** 网元端口集合 */
    private final Map<String, P> elementPortMap = new HashMap<>();
    /** 网元端口连接集合 */
    private final Map<String, C> elementPortConnectionMap = new HashMap<>();
    /** 区域外连接端口id集合 */
    private final Set<String> outerConnectionPortIds = new HashSet<>();
    /** 场景插件数据，key为插件代号 */
    private final Map<String, ScenePluginDataBO> scenePluginData = new HashMap<>();

    // 辅助变量
    /** 端口所属节点映射 */
    private final Map<String, E> portIdElementMap = new HashMap<>();
    /** 端口连接端口映射 */
    private final Map<String, String> portIdConnectedPortIdMap = new HashMap<>();

    // 通过ClassFieldNames注解和实现类ClassFieldClasses注解，初始化调用ClassUtils.loadClassFields(this)设置
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
    private Class<P1> sceneElementPluginModelClass;
    private Class<P2> scenePluginHandlerInterface;
    private Class<H> sceneHandlerClass;

    // 通过BeanFieldNames注解和实现类BeanFieldClasses注解，初始化调用BeanUtils.loadBeanFields(this)设置
    private M1 sceneMapper;
    private M2 sceneAreaMapper;
    private M3 sceneElementMapper;
    private M4 sceneElementPortMapper;
    private M5 sceneElementPortConnectionMapper;
    private M6 sceneElementPluginMapper;

    /** 插件数据处理对象集合 */
    private final List<P2> plugins = new ArrayList<>();
    private final Map<String, P2> pluginMap = new HashMap<>();
    /** 网元对应节点插件关系，一个网元节点对于多个插件 */
    private final Map<String, List<P1>> elementIdPluginsMap = new HashMap<>();

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

    /**
     * 初始化插件集合
     */
    @SuppressWarnings("unchecked")
    private void initPlugins(Map<String, ScenePluginDataBO> scenePluginData) {
        plugins.clear();
        pluginMap.clear();
        BeanUtils.getBeanType(scenePluginHandlerInterface).forEach((name, pluginInstance) -> {
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

    /**
     * 初始化插件集合
     */
    @SuppressWarnings("unchecked")
    private void initPlugins(Map<String, List<String>> pluginKeyElementIds,
                             Map<String, List<String>> pluginKeyElementPortIds) {
        plugins.clear();
        pluginMap.clear();
        BeanUtils.getBeanType(scenePluginHandlerInterface).forEach((name, pluginInstance) -> {
            Class<?> pluginClass = pluginInstance.getClass();
            var pluginKeyAnnotation = pluginClass.getAnnotation(PluginKey.class);
            if (pluginKeyAnnotation == null) {
                return;
            }

            String pluginKey = pluginKeyAnnotation.value();
            List<String> elementIds = pluginKeyElementIds.getOrDefault(pluginKey, null);
            List<String> elementPortIds = pluginKeyElementPortIds.getOrDefault(pluginKey, null);
            if (elementIds == null && elementPortIds == null) {
                return;
            }

            P2 plugin = (P2) ClassUtils.createInstance(pluginClass);
            beforeInitPlugin(plugin);
            plugin.init(sceneId, elementIds, elementPortIds, getPluginContext(pluginKey));
            afterInitPlugin(plugin);
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

            // 去除父节点不在此区域的节点
            ListTreeNode<A> parentAreaNode = areaNodeMap.getOrDefault(area.getArea(), null);
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
    private void filterAreaTree(ListTreeNode<A> areaNode, Map<String, A> targetAreaMap) {
        var area = areaNode.get();
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
     * @param element 节点
     * @return boolean 节点是否是区域内
     */
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

        // 节点所属区域也在区域内并且是展开的
        var parentArea = areaMap.getOrDefault(elementArea, null);
        return parentArea != null && parentArea.getOpen();
    }

    /**
     * 网元端口连接的唯一标识(场景内唯一) 源端口id:目的端口id
     * @param elementPortConnection 表模型-网元端口连接
     * @return 连接的唯一标识
     */
    private String elementPortConnectionKey(C0 elementPortConnection) {
        return StringUtils.joinWith(":", elementPortConnection.getSourcePortId(),
                elementPortConnection.getTargetPortId());
    }

    /**
     * 网元端口连接的唯一标识(场景内唯一) 源端口id:目的端口id
     * @param elementPortConnection 业务模型-网元端口连接
     * @return 连接的唯一标识
     */
    private String elementPortConnectionKey(C elementPortConnection) {
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
    private String elementPluginKey(P1 elementPlugin) {
        return elementPluginKey(elementPlugin.getSceneElement(), elementPlugin.getPluginKey());
    }

    /**
     * 载入场景基础数据
     * @param scene 业务模型-场景基础数据
     */
    private void loadSceneBO(S scene) {
        if (scene == null) {
            return;
        }

        this.scene = scene;
    }

    /**
     * 载入区域节点数据
     * @param areas 业务模型-区域节点
     */
    private void loadAreaBOs(List<A> areas) {
        if (areas == null) {
            return;
        }

        areas.forEach(area -> areaMap.put(area.getId(), area));
        if (isAreaMode) {
            filterAreaData();
        }
    }

    /**
     * 载入网元节点数据
     * @param elements 业务模型-网元节点
     */
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
                portIdElementMap.put(elementPort.getId(), element);
            });
        });
    }

    /**
     * 载入网元端口连接数据
     * @param connections 业务模型-网元端口连接
     */
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
    private void loadScene(S0 scene) {
        if (scene == null) {
            return;
        }

        var sceneBO = ClassUtils.createInstance(sceneBOClass);
        sceneBO.fromModel(scene);
        this.scene = sceneBO;
    }

    /**
     * 载入区域节点数据
     * @param areas 表模型-区域节点
     */
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

    /**
     * 载入网元节点数据
     * @param elements 表模型-网元节点
     */
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

    /**
     * 载入网元端口数据
     * @param elementPorts 表模型-网元端口
     */
    private void loadElementPorts(List<P0> elementPorts) {
        if (elementPorts == null) {
            return;
        }
        Map<String, E> elementIdElementBOMap = elementMap.values().stream().collect(
                Collectors.toMap(EidBO::getEid, element -> element));
        elementPorts.forEach(elementPort -> {
            var elementBO = elementIdElementBOMap.getOrDefault(elementPort.getSceneElement(), null);
            if (elementBO == null) {
                return;
            }

            var elementPortBO = ClassUtils.createInstance(sceneElementPortBOClass);
            elementPortBO.fromModel(elementPort);
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

    /**
     * 载入网元插件关系数据
     * @param elementPlugins 表模型-网元插件关系
     */
    private void loadElementPlugins(List<P1> elementPlugins) {
        elementPlugins.forEach(elementPlugin -> {
            String elementId = elementPlugin.getSceneElement();
            List<P1> plugins = elementIdPluginsMap.getOrDefault(elementId, new ArrayList<>());
            plugins.add(elementPlugin);
            elementIdPluginsMap.put(elementId, plugins);
        });
    }

    /**
     * 从数据库读取场景
     * @return 表模型-场景对象
     */
    private S0 readScene() {
        return sceneMapper.logicGet(sceneId);
    }

    /**
     * 从数据库读取区域节点
     * @param areaIds 父区域节点
     * @return 表模型-区域节点
     */
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

    /**
     * 从数据库读取网元节点
     * @param areaIds 父区域节点
     * @return 表模型-网元节点
     */
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

    /**
     * 从数据库读取网元端口
     * @param areaIds 父区域节点
     * @return 表模型-网元端口
     */
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

    /**
     * 从数据库读取网元端口连接
     * @param areaIds 父区域节点
     * @return 表模型-网元端口连接
     */
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

    /**
     * 从数据库读取网元插件关系
     * @param areaIds 父区域节点
     * @return 表模型-网元插件关系
     */
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

    /**
     * 对比需要更新的场景元素
     * @param targetMap 待更新的场景元素
     * @param existTargetMap 已存在的场景元素
     * @param <M> 目标元素表模型类
     * @return 需要更新的场景元素结果集，包含待添加的元素、待更新属性的元素、待删除的元素
     */
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

    /**
     * 保存需要更新的场景元素结果集，包含待添加的元素、待更新属性的元素、待删除的元素
     * @param modelMapper 目标元素的表mapper
     * @param diff 需要更新的区域元素结果集
     * @param <M> 目标元素的表模型类
     * @param <O> 目标元素的表mapper类
     */
    private <M extends BaseModel, O extends BaseModelMapper<M>> void saveDiff(O modelMapper, DiffTargets<M> diff) {
        String[] removingTargetIds = diff.getRemovingTargets().stream()
                .map(BaseModel::getId)
                .toArray(String[]::new);
        var deleteResult = modelMapper.bulkLogicDelete(removingTargetIds);
        if (deleteResult < removingTargetIds.length) {
            throw new SceneUpdateException("Scene diff delete failed");
        }

        var createResult = modelMapper.bulkCreate(diff.getAddingTargets());
        if (createResult < diff.getAddingTargets().size()) {
            throw new SceneUpdateException("Scene diff create failed");
        }

        var updateResult = modelMapper.bulkUpdate(diff.getUpdatingTargets());
        if (updateResult < diff.getUpdatingTargets().size()) {
            throw new SceneUpdateException("Scene diff update failed");
        }
    }

    /**
     * 保存场景属性数据
     */
    private boolean saveScene() {
        S0 sceneObj = scene.toModel();
        var result = sceneMapper.save(sceneObj);
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
    private DiffTargets<A0> saveAreas(Map<String, A0> existAreaMap) {
        Map<String, A0> savingAreaMap = areaMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    A0 area = entry.getValue().toModel();
                    area.setScene(sceneId);
                    return area;
                }));
        DiffTargets<A0> diff = diffTargets(savingAreaMap, existAreaMap);
        if (isAreaMode) {
            // 删除闭合的子区域
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

    /**
     * 保存网元节点
     * @param existElementMap 已存在的网元节点
     * @return 更新的网元节点，包含添加的、更新属性的、删除的
     */
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

    /**
     * 保存网元端口
     * @param existElementPortMap 已存在的网元端口
     * @return 更新的网元端口，包含添加的、更新属性的、删除的
     */
    private DiffTargets<P0> saveElementPorts(Map<String, P0> existElementPortMap) {
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

    /**
     * 保存网元端口连接
     * @param existElementPortConnectionMap 已存在的网元端口连接
     * @return 更新的网元端口连接，包含添加的、更新属性的、删除的
     */
    private DiffTargets<C0> saveElementPortConnections(Map<String, C0> existElementPortConnectionMap) {
        Map<String, P0> outerElementPortMap = new HashMap<>();
        if (!outerConnectionPortIds.isEmpty()) {
            sceneElementPortMapper.list(new HumanCondition()
                    .select(BaseSceneElementPortModel::getId, BaseSceneElementPortModel::getSceneElement,
                            BaseSceneElementPortModel::getPortId)
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

    /**
     * 保存网元插件关系
     * @param existElementPluginMap 已存在的网元插件关系
     * @return 更新的网元插件关系，包含添加的、更新属性的、删除的
     */
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

    /**
     * 获取端口id和端口所属网元映射
     * @return 端口id和端口所属网元映射
     */
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

    /**
     * 从场景业务数据载入场景元素
     * @param sceneDataBO 场景业务数据
     */
    private void fromData(D sceneDataBO) {
        var topology = sceneDataBO.getTopology();
        loadSceneBO(topology.getScene());
        sceneId = scene.getEid();
        areaId = topology.getAreaId();
        loadAreaBOs(topology.getAreas());
        loadElementBOs(topology.getElements());
        loadConnectionBOs(topology.getConnections());
        loadScenePluginData(sceneDataBO.getPlugins());
    }

    /**
     * 纠正场景数据
     */
    private void correctData() {
        plugins.forEach(plugin -> plugin.correctData());
    }

    /**
     * 从场景业务数据初始化
     * @param sceneDataBO 场景业务数据
     * @param isAreaMode 是否区域模式
     */
    public void init(D sceneDataBO, boolean isAreaMode, C2 context) {
        ClassUtils.loadClassFields(this);
        BeanUtils.loadBeanFields(this);
        this.context = context;
        this.isAreaMode = isAreaMode;
        fromData(sceneDataBO);
        if (!StringUtils.isEmpty(sceneId) && readScene() == null) {
            throw new SceneNotExistException("Scene " + sceneId + " not exist");
        }
        initPlugins(scenePluginData);

        // 新的场景数据需要纠正
        if (StringUtils.isEmpty(sceneId)) {
            correctData();
        }
    }

    /**
     * 从场景id初始化
     * @param sceneId 场景id
     * @param areaId 区域id
     * @param isAreaMode 是否区域模式
     */
    public void init(String sceneId, String areaId, boolean isAreaMode, C2 context) {
        ClassUtils.loadClassFields(this);
        BeanUtils.loadBeanFields(this);
        this.context = context;
        this.sceneId = sceneId;
        this.areaId = areaId;
        this.isAreaMode = isAreaMode;
        reload();
        if (scene == null) {
            throw new SceneNotExistException("Scene " + sceneId + " not exist");
        }
    }

    public void beforeInitPlugin(P2 plugin) {}
    public void afterInitPlugin(P2 plugin) {}

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
    public D toData() {
        var topologyBO = ClassUtils.createInstance(sceneTopologyBOClass);
        topologyBO.setScene(scene);
        topologyBO.setAreaId(areaId);
        topologyBO.setAreas(new ArrayList<>(areaMap.values()));
        topologyBO.setElements(new ArrayList<>(elementMap.values()));
        topologyBO.setConnections(new ArrayList<>(elementPortConnectionMap.values()));

        var dataBO = ClassUtils.createInstance(sceneDataBOClass);
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

    /**
     * 保存当前场景数据
     */
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
        Map<String, E0> existElementMap = isCreate ? new HashMap<>() : readElements(filterAreaIds).stream()
                .collect(Collectors.toMap(BaseSceneElementModel::getNodeId, x -> x));
        Map<String, P0> existElementPortMap = isCreate ? new HashMap<>() : readElementPorts(filterAreaIds).stream()
                .collect(Collectors.toMap(BaseSceneElementPortModel::getPortId, x -> x));
        Map<String, C0> existElementPortConnectionMap = isCreate ? new HashMap<>() : readElementPortConnections(filterAreaIds).stream()
                .collect(Collectors.toMap(this::elementPortConnectionKey, x -> x));
        Map<String, P1> existElementPluginMap = isCreate ? new HashMap<>() : readElementPlugins(filterAreaIds).stream()
                .collect(Collectors.toMap(this::elementPluginKey, x -> x));

        // 保存区域，可能会跨区域连接
        DiffTargets<A0> diffAreas = saveAreas(existAreaMap);
        DiffTargets<E0> diffElements = saveElements(existElementMap);
        DiffTargets<P0> diffElementPorts = saveElementPorts(existElementPortMap);
        // 此时可能存在已被删除的跨区域连接，可优化
        if (isAreaMode && !existElementPortConnectionMap.isEmpty()) {
            existElementPortConnectionMap = sceneElementPortConnectionMapper.list(new HumanCondition()
                    .filterIn(SceneElementPortConnectionModel::getId, existElementPortConnectionMap.values()
                            .stream().map(BaseModel::getId).toArray(String[]::new))
            ).stream().collect(Collectors.toMap(this::elementPortConnectionKey, x -> x));
        }
        DiffTargets<C0> diffElementPortConnections = saveElementPortConnections(existElementPortConnectionMap);
        // elementPlugin 由于通过服务端id映射，在拓扑保存后才能获取
        DiffTargets<P1> diffElementPlugins = saveElementPlugins(existElementPluginMap);

        // 保存插件数据
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
        List<A0> areas = readAreas();
        var filterAreaIds = new String[0];
        if (isAreaMode) {
            // 递归删除闭合的子区域
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
        List<E0> elements = readElements(filterAreaIds);
        List<P0> elementPorts = readElementPorts(filterAreaIds);
        List<P1> elementPlugins = readElementPlugins(filterAreaIds);
        if (!areas.isEmpty()) {
            sceneAreaMapper.bulkDelete(areas.stream().map(BaseModel::getId).toArray(String[]::new));
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

        // 删除插件数据
        plugins.forEach(plugin -> {
            plugin.deleteElements(elements.stream().map(BaseSceneNodeModel::getNodeId).toArray(String[]::new));
            plugin.deleteElementPorts(elementPorts.stream().map(BaseSceneElementPortModel::getPortId)
                    .toArray(String[]::new));
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
    public void updateSceneElement(String nodeId) {
        var elementBO = getElement(nodeId);
        E0 element = elementBO.toModel();
        element.setScene(sceneId);
        sceneElementMapper.update(element);

        List<P> elementPortBOs = elementBO.getPorts();
        if (elementPortBOs != null && !elementPortBOs.isEmpty()) {
            List<P0> ports = elementPortBOs.stream().map(elementPortBO -> {
                P0 port = elementPortBO.toModel();
                port.setScene(sceneId);
                port.setSceneElement(element.getId());
                return port;
            }).collect(Collectors.toList());
            sceneElementPortMapper.bulkUpdate(ports);
        }
    }

    /**
     * 单独保存基础场景节点数据及其插件数据
     */
    public void updateSceneElementWithPlugins(String nodeId) {
        updateSceneElement(nodeId);

        List<P> elementPortBOs = getElement(nodeId).getPorts();
        for (var plugin : plugins) {
            plugin.updateElements(nodeId);
            if (elementPortBOs != null && !elementPortBOs.isEmpty()) {
                plugin.updateElementPorts(elementPortBOs.stream().map(EidBO::getEid).toArray(String[]::new));
            }
        }
    }

    /**
     * 获取插件handler
     * @param pluginKey 插件代号
     */
    public <H9> H9 getPluginHandler(String pluginKey) {
        var plugin = pluginMap.getOrDefault(pluginKey, null);
        return (H9) plugin;
    }

    /**
     * 获取插件全局数据
     * @param pluginKey 插件代号
     */
    public <S9> S9 getPluginScene(String pluginKey) {
        var plugin = pluginMap.getOrDefault(pluginKey, null);
        if (plugin == null) {
            return null;
        }

        var pluginScene = plugin.getScene();
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
        var plugin = pluginMap.getOrDefault(pluginKey, null);
        if (plugin == null) {
            return null;
        }

        var pluginElement = plugin.getElement(nodeId);
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
        var plugin = pluginMap.getOrDefault(pluginKey, null);
        if (plugin == null) {
            return null;
        }

        var pluginPort = plugin.getPort(portId);
        if (pluginPort == null) {
            return null;
        }

        return (P9) pluginPort;
    }


    /**
     * 获取网元数据
     * @param nodeId 网元节点id
     */
    public E getElement(String nodeId) {
        return elementMap.getOrDefault(nodeId, null);
    }

    /**
     * 获取网元连接的网元
     * @param nodeId 网元节点id
     */
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

    /**
     * 获取网元连接的网元
     * @param nodeId 网元节点id
     */
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

    /**
     * 获取端口数据
     * @param portId 端口id
     */
    public P getPort(String portId) {
        return elementPortMap.getOrDefault(portId, null);
    }

    /**
     * 获取端口所属网元数据
     * @param portId 端口id
     */
    public E getPortElement(String portId) {
        return portIdElementMap.getOrDefault(portId, null);
    }

    /**
     * 获取端口连接的端口
     * @param portId 端口id
     */
    public P getPortConnectedPort(String portId) {
        var connectedPortId = portIdConnectedPortIdMap.getOrDefault(portId, null);
        return getPort(connectedPortId);
    }

    /**
     * 获取端口连接的节点
     * @param portId 端口id
     */
    public E getPortConnectedElement(String portId) {
        var connectedPortId = portIdConnectedPortIdMap.getOrDefault(portId, null);
        return getPortElement(connectedPortId);
    }

}
