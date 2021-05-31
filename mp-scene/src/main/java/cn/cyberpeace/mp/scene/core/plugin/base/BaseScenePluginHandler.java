package cn.cyberpeace.mp.scene.core.plugin.base;

import cn.cyberpeace.mp.base.annotation.ClassFieldNames;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;

import java.util.Map;

@ClassFieldNames({
        "elementBOClass",
        "elementPortBOClass",
})
public abstract class BaseScenePluginHandler<E, P> {
    private Map<String, E> elementMap;
    private Map<String, P> elementPortMap;

    private Class<E> elementBOClass;
    private Class<P> elementPortBOClass;

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

    public void init(ScenePluginDataBO pluginDataBO) {
        ClassUtils.loadClassFields(this);
        fromData(pluginDataBO);
    }

    public abstract void save();
}
