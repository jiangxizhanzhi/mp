package cn.cyberpeace.mp.scene.core.plugin.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.ValidationCommon;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldNames;
import cn.cyberpeace.mp.scene.core.bo.base.BaseScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


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

    
    private String sourceSceneId;
    private final List<String> elementIds = new CopyOnWriteArrayList<>();
    private final List<String> elementPortIds = new CopyOnWriteArrayList<>();

    private S scene;
    private final Map<String, E> elementMap = new ConcurrentHashMap<>();
    private final Map<String, P> elementPortMap = new ConcurrentHashMap<>();

    
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
                
                this.context = JsonCommon.snakeMapToBean((Map<String, Object>) context, contextClass);
            } else {
                this.context = (C) context;
            }
        } catch (Exception e) {
            log.error("load plugin({}) context({}) error: {}", key, context, e);
        }
    }

    
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

        
        var validateData = new BaseScenePluginDataBO<S, E, P>();
        validateData.setScene(scene);
        validateData.setElements(elementMap);
        validateData.setElementPorts(elementPortMap);
        this.validateData(validateData);
    }

    protected void validateData(BaseScenePluginDataBO validateData) {
        var validateResult = ValidationCommon.validate(validateData);
        ValidationCommon.handleValidateResult(validateResult);
    }

    
    public void correctData() {}

    
    public void validate() {}

    
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

    
    public void init(String sceneId, ScenePluginDataBO pluginDataBO, Object context) {
        initBase();
        loadContext(context);
        sourceSceneId = sceneId;
        if (pluginDataBO != null) {
            fromData(pluginDataBO);
        }
    }

    
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
        return nodeId != null ? elementMap.getOrDefault(nodeId, null) : null;
    }

    public P getPort(String portId) {
        return portId != null ? elementPortMap.getOrDefault(portId, null) : null;
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
