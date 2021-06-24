package cn.cyberpeace.mp.scene.core.plugin.base;

import cn.cyberpeace.mp.base.annotation.ClassFieldNames;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;

import java.util.HashMap;
import java.util.Map;

/**
 * 基础的场景插件处理类
 * 场景插件处理目前只对网元节点和网元端口做插件配置处理
 *
 * @param <E> 网元节点的插件对应配置类
 * @param <P> 网元端口的插件对应配置类
 */
@ClassFieldNames({
        "elementBOClass",
        "elementPortBOClass",
})
public abstract class BaseScenePluginHandler<E, P> {
    private Map<String, E> elementMap = new HashMap<>();
    private Map<String, P> elementPortMap = new HashMap<>();

    // 通过ClassFieldNames注解和实现类ClassFieldClasses注解，初始化调用ClassUtils.loadClassFields(this)设置
    private Class<E> elementBOClass;
    private Class<P> elementPortBOClass;

    /**
     * 从场景插件业务数据载入
     * @param pluginData 场景插件业务数据
     */
    private void fromData(ScenePluginDataBO pluginData) {
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
        Map<String, Map<String, Object>> elements = new HashMap<>();
        elementMap.forEach((nodeId, elementBO) -> {
            elements.put(nodeId, JsonUtils.beanToMap(elementBO));
        });
        pluginData.setElements(elements);

        Map<String, Map<String, Object>> elementPorts = new HashMap<>();
        elementPortMap.forEach((nodeId, elementPortBO) -> {
            elementPorts.put(nodeId, JsonUtils.beanToMap(elementPortBO));
        });
        pluginData.setElementPorts(elementPorts);

        return pluginData;
    }

    /**
     * 从场景插件业务数据初始化
     * @param pluginDataBO 场景插件业务数据
     */
    public void init(ScenePluginDataBO pluginDataBO) {
        ClassUtils.loadClassFields(this);
        if (pluginDataBO != null) {
            fromData(pluginDataBO);
        }
    }

    public abstract void save();
}
