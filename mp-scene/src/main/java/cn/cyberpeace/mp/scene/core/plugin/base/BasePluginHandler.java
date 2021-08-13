package cn.cyberpeace.mp.scene.core.plugin.base;

import cn.cyberpeace.mp.base.annotation.ClassFieldNames;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import lombok.Data;

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
@ClassFieldNames({
        "sceneBOClass",
        "elementBOClass",
        "elementPortBOClass",
})
@Data
public abstract class BasePluginHandler<S, E, P> {
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

    /**
     * 从场景插件业务数据载入
     * @param pluginData 场景插件业务数据
     */
    private void fromData(ScenePluginDataBO pluginData) {
        var sceneSrc = pluginData.getScene();
        if (sceneSrc != null) {
            scene = JsonUtils.mapToBean(sceneSrc, sceneBOClass);
        }

        var elementMapSrc = pluginData.getElements();
        if (elementMapSrc != null) {
            elementMapSrc.forEach((nodeId, elementData) -> {
                elementMap.put(nodeId, JsonUtils.mapToBean(elementData, elementBOClass));
            });
        }

        var elementPortMapSrc = pluginData.getElementPorts();
        if (elementPortMapSrc != null) {
            elementPortMapSrc.forEach((portId, elementPortData) -> {
                elementPortMap.put(portId, JsonUtils.mapToBean(elementPortData, elementPortBOClass));
            });
        }
    }

    /**
     * 转出场景插件业务数据
     * @return 场景插件业务数据
     */
    public ScenePluginDataBO toData() {
        var pluginData = new ScenePluginDataBO();
        if (scene != null) {
            pluginData.setScene(JsonUtils.beanToMap(scene));
        }

        Map<String, Map<String, Object>> elements = new HashMap<>();
        elementMap.forEach((nodeId, elementBO) -> {
            elements.put(nodeId, JsonUtils.beanToMap(elementBO));
            elementIds.add(nodeId);
        });
        pluginData.setElements(elements);

        Map<String, Map<String, Object>> elementPorts = new HashMap<>();
        elementPortMap.forEach((portId, elementPortBO) -> {
            elementPorts.put(portId, JsonUtils.beanToMap(elementPortBO));
            elementPortIds.add(portId);
        });
        pluginData.setElementPorts(elementPorts);

        return pluginData;
    }

    /**
     * 从场景插件业务数据初始化
     * @param pluginDataBO 场景插件业务数据
     */
    public void init(String sceneId, ScenePluginDataBO pluginDataBO) {
        ClassUtils.loadClassFields(this);
        sourceSceneId = sceneId;
        if (pluginDataBO != null) {
            fromData(pluginDataBO);
        }
    }

    /**
     * 从场景插件索引数据初始化
     * @param sceneId 场景ID
     */
    public void init(String sceneId, List<String> elementIds, List<String> elementPortIds) {
        ClassUtils.loadClassFields(this);
        sourceSceneId = sceneId;
        this.elementIds.addAll(elementIds);
        this.elementPortIds.addAll(elementPortIds);
        reload();
    }

    public abstract void reload();

    public abstract void save();
}
