package cn.cyberpeace.mp.scene.core.plugin.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.ValidationCommon;
import cn.cyberpeace.mp.scene.core.bo.base.BaseScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ClassName BasePluginHandler
 * @Description 基础的场景插件处理类
 * 场景插件处理目前只对网元节点和网元端口做插件配置处理
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Data
public abstract class BasePluginHandler<TSceneBO, TPluginSceneElementBO, TPluginSceneElementPortBO, TContext> {
    private String key;
    private float priority;

    private TContext context;

    // 源场景索引数据
    private String sourceSceneId;
    private final List<String> elementIds = new CopyOnWriteArrayList<>();
    private final List<String> elementPortIds = new CopyOnWriteArrayList<>();

    private TSceneBO scene;
    private final Map<String, TPluginSceneElementBO> elementMap = new ConcurrentHashMap<>();
    private final Map<String, TPluginSceneElementPortBO> elementPortMap = new ConcurrentHashMap<>();

    // 通过ClassFieldNames注解和实现类ClassFieldClasses注解，初始化调用ClassUtils.loadClassFields(this)设置
    protected Class<TSceneBO> sceneBOClass;
    protected Class<TPluginSceneElementBO> elementBOClass;
    protected Class<TPluginSceneElementPortBO> elementPortBOClass;
    protected Class<TContext> contextClass;

    private void loadContext(Object context) {
        if (context == null) {
            return;
        }

        try {
            if (contextClass.equals(context.getClass())) {
                this.context = (TContext) context;
            } else if (context instanceof Map) {
                // TODO: 去除snake依赖
                this.context = JsonCommon.snakeMapToBean((Map<String, Object>) context, contextClass);
            } else {
                this.context = (TContext) context;
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
        Map<String, Object> sceneSrc = pluginData.getScene();
        if (sceneSrc != null) {
            scene = JsonCommon.snakeMapToBean(sceneSrc, sceneBOClass);
        }

        Map<String, Map<String, Object>> elementMapSrc = pluginData.getElements();
        if (elementMapSrc != null) {
            for (Map.Entry<String, Map<String, Object>> entry : elementMapSrc.entrySet()) {
                String nodeId = entry.getKey();
                Map<String, Object> elementData = entry.getValue();
                elementMap.put(nodeId, JsonCommon.snakeMapToBean(elementData, elementBOClass));
            }
        }

        Map<String, Map<String, Object>> elementPortMapSrc = pluginData.getElementPorts();
        if (elementPortMapSrc != null) {
            for (Map.Entry<String, Map<String, Object>> entry : elementPortMapSrc.entrySet()) {
                String portId = entry.getKey();
                Map<String, Object> elementPortData = entry.getValue();
                elementPortMap.put(portId, JsonCommon.snakeMapToBean(elementPortData, elementPortBOClass));
            }
        }

        // 验证数据字段
        BaseScenePluginDataBO<TSceneBO, TPluginSceneElementBO, TPluginSceneElementPortBO> validateData = new BaseScenePluginDataBO<TSceneBO, TPluginSceneElementBO, TPluginSceneElementPortBO>();
        validateData.setScene(scene);
        validateData.setElements(elementMap);
        validateData.setElementPorts(elementPortMap);
        this.validateData(validateData);
    }

    protected void validateData(BaseScenePluginDataBO validateData) {
        Set<ConstraintViolation<BaseScenePluginDataBO>> validateResult = ValidationCommon.validate(validateData);
        ValidationCommon.handleValidateResult(validateResult);
    }

    /**
     * 纠正场景插件数据
     */
    public void correctData() {}

    /**
     * 验证场景插件
     */
    public void validate() {}

    /**
     * 转出场景插件业务数据 TODO: 去除snake依赖
     * @return 场景插件业务数据
     */
    public ScenePluginDataBO toData() {
        ScenePluginDataBO pluginData = new ScenePluginDataBO();
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
        this.initClass();
        PluginKey pluginKeyAnnotation = getClass().getAnnotation(PluginKey.class);
        key = pluginKeyAnnotation.value();
        priority = pluginKeyAnnotation.priority();
    }

    protected abstract void initClass();

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

    public TPluginSceneElementBO getElement(String nodeId) {
        return nodeId != null ? elementMap.getOrDefault(nodeId, null) : null;
    }

    public TPluginSceneElementPortBO getPort(String portId) {
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
