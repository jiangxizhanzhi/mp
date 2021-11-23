package cn.cyberpeace.mp.scene.core.plugin.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldNames;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础的场景插件处理类
 * 场景插件处理目前只对网元节点和网元端口做插件配置处理
 *
 * @param <S> 插件对应配置类
 * @param <E> 网元节点的插件对应配置类
 * @param <P> 网元端口的插件对应配置类
 */
@Slf4j
@ClassFieldNames({
        "sceneBOClass",
        "elementBOClass",
        "elementPortBOClass",
        "contextClass",
})
@Data
public abstract class BasePluginHandler<S, E, P, C> {
    private String key;
    private float priority;

    private C context;

    // 源场景索引数据
    private String sourceSceneId;
    private final List<String> elementIds = new ArrayList<>();
    private final List<String> elementPortIds = new ArrayList<>();

    private S scene;
    private final Map<String, E> elementMap = new HashMap<>();
    private final Map<String, P> elementPortMap = new HashMap<>();

    // 通过ClassFieldNames注解和实现类ClassFieldClasses注解，初始化调用ClassUtils.loadClassFields(this)设置
    private Class<S> sceneBOClass;
    private Class<E> elementBOClass;
    private Class<P> elementPortBOClass;
    private Class<C> contextClass;

    private void loadContext(Object context) {
        if (context == null) {
            return;
        }

        try {
            if (contextClass.equals(context.getClass())) {
                this.context = (C) context;
            } else if (context instanceof Map) {
                // TODO: 去除snake依赖
                this.context = JsonCommon.snakeMapToBean((Map<String, Object>) context, contextClass);
            } else {
                this.context = (C) context;
            }
        } catch (Exception e) {
            log.error("load plugin({}) context({}) error: {}", key, context, e);
        }
    }

    /**
     * 从场景插件业务数据载入 TODO: 去除snake依赖
     * @param pluginData 场景插件业务数据
     */
    public void fromData(ScenePluginDataBO pluginData) {
        var sceneSrc = pluginData.getScene();
        if (sceneSrc != null) {
            scene = JsonCommon.snakeMapToBean(sceneSrc, sceneBOClass);
        }

        var elementMapSrc = pluginData.getElements();
        if (elementMapSrc != null) {
            elementMapSrc.forEach((nodeId, elementData) -> {
                elementMap.put(nodeId, JsonCommon.snakeMapToBean(elementData, elementBOClass));
            });
        }

        var elementPortMapSrc = pluginData.getElementPorts();
        if (elementPortMapSrc != null) {
            elementPortMapSrc.forEach((portId, elementPortData) -> {
                elementPortMap.put(portId, JsonCommon.snakeMapToBean(elementPortData, elementPortBOClass));
            });
        }
    }

    /**
     * 纠正场景插件数据
     */
    public void correctData() {}

    /**
     * 转出场景插件业务数据 TODO: 去除snake依赖
     * @return 场景插件业务数据
     */
    public ScenePluginDataBO toData() {
        var pluginData = new ScenePluginDataBO();
        if (scene != null) {
            pluginData.setScene(JsonCommon.beanToSnakeMap(scene));
        }

        Map<String, Map<String, Object>> elements = new HashMap<>();
        elementMap.forEach((nodeId, elementBO) -> {
            elements.put(nodeId, JsonCommon.beanToSnakeMap(elementBO));
            elementIds.add(nodeId);
        });
        pluginData.setElements(elements);

        Map<String, Map<String, Object>> elementPorts = new HashMap<>();
        elementPortMap.forEach((portId, elementPortBO) -> {
            elementPorts.put(portId, JsonCommon.beanToSnakeMap(elementPortBO));
            elementPortIds.add(portId);
        });
        pluginData.setElementPorts(elementPorts);

        return pluginData;
    }

    public void initBase() {
        ClassUtils.loadClassFields(this);
        var pluginKeyAnnotation = getClass().getAnnotation(PluginKey.class);
        key = pluginKeyAnnotation.value();
        priority = pluginKeyAnnotation.priority();
    }

    /**
     * 从场景插件业务数据初始化
     * @param sceneId 场景ID
     * @param pluginDataBO 场景插件业务数据
     */
    public void init(String sceneId, ScenePluginDataBO pluginDataBO, Object context) {
        initBase();
        loadContext(context);
        sourceSceneId = sceneId;
        if (pluginDataBO != null) {
            fromData(pluginDataBO);
        }
    }

    /**
     * 从场景插件索引数据初始化
     * @param sceneId 场景ID
     * @param elementIds 网元ID
     * @param elementPortIds 端口ID
     */
    public void init(String sceneId, List<String> elementIds, List<String> elementPortIds, Object context) {
        initBase();
        loadContext(context);
        sourceSceneId = sceneId;
        if (elementIds != null) {
            this.elementIds.addAll(elementIds);
        }
        if (elementPortIds != null) {
            this.elementPortIds.addAll(elementPortIds);
        }
        reload();
    }

    public E getElement(String nodeId) {
        return elementMap.getOrDefault(nodeId, null);
    }

    public P getPort(String portId) {
        return elementPortMap.getOrDefault(portId, null);
    }

    public abstract void reload();

    public abstract void saveScene();

    public abstract void deleteScene();

    public abstract void createElements(String... elementIds);

    public abstract void updateElements(String... elementIds);

    public abstract void deleteElements(String... elementIds);

    public abstract void createElementPorts(String... elementPortIds);

    public abstract void updateElementPorts(String... elementPortIds);

    public abstract void deleteElementPorts(String... elementPortIds);
}
