package cn.cyberpeace.mp.scene.core.plugin.base;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementModel;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementPortModel;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneModel;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementMapper;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementPortMapper;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BasePluginModelHandler
 * @Description 场景插件model实现处理基类
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public abstract class BasePluginModelHandler<
        TSceneBO extends BaseSceneBO<?, TSceneBO>,
        TSceneElementBO extends BaseSceneElementBO<?, TSceneElementPortBO, TSceneElementBO>,
        TSceneElementPortBO extends BaseSceneElementPortBO<?, TSceneElementPortBO, TSceneElementBO>,
        TPluginSceneModel extends BasePluginSceneModel,
        TPluginSceneElementModel extends BasePluginSceneElementModel,
        TPluginSceneElementPortModel extends BasePluginSceneElementPortModel,
        TPluginSceneBO extends BasePluginSceneBO<TPluginSceneModel, TPluginSceneBO, TSceneBO>,
        TPluginSceneElementBO extends BasePluginSceneElementBO<TPluginSceneElementModel, TPluginSceneElementBO, TSceneElementBO>,
        TPluginSceneElementPortBO extends BasePluginSceneElementPortBO<TPluginSceneElementPortModel, TPluginSceneElementPortBO, TSceneElementPortBO>,
        TPluginSceneMapper extends BasePluginSceneMapper<TPluginSceneModel>,
        TPluginSceneElementMapper extends BasePluginSceneElementMapper<TPluginSceneElementModel>,
        TPluginSceneElementPortMapper extends BasePluginSceneElementPortMapper<TPluginSceneElementPortModel>,
        TPluginHandler extends BasePluginHandler<TPluginSceneBO, TPluginSceneElementBO, TPluginSceneElementPortBO, ?>> {
    private TPluginHandler sceneTPluginHandler;

    // 通过ClassFieldNames注解和实现类ClassFieldClasses注解，初始化调用ClassUtils.loadClassFields(this)设置
    protected Class<TPluginSceneBO> pluginSceneBOClass;
    protected Class<TPluginSceneElementBO> pluginSceneElementBOClass;
    protected Class<TPluginSceneElementPortBO> pluginSceneElementPortBOClass;
    protected Class<TPluginSceneModel> pluginSceneModelClass;
    protected Class<TPluginSceneElementModel> pluginSceneElementModelClass;
    protected Class<TPluginSceneElementPortModel> pluginSceneElementPortModelClass;

    // 通过BeanFieldNames注解和实现类BeanFieldClasses注解，初始化调用BeanUtils.loadBeanFields(this)设置
    protected TPluginSceneMapper pluginSceneMapper;
    protected TPluginSceneElementMapper pluginSceneElementMapper;
    protected TPluginSceneElementPortMapper pluginSceneElementPortMapper;

    /**
     * 载入场景基础
     * @param scene 表模型-场景基础
     */
    public void loadScene(TPluginSceneModel scene) {
        if (scene == null) {
            return;
        }
        TPluginSceneBO sceneBO = ClassUtils.createInstance(pluginSceneBOClass);
        sceneBO.fromModel(scene);
        sceneTPluginHandler.setScene(sceneBO);
    }

    /**
     * 载入网元节点
     * @param elements 表模型-网元节点
     */
    public void loadElements(List<TPluginSceneElementModel> elements) {
        if (elements == null) {
            return;
        }
        Map<String, TPluginSceneElementBO> elementMap = sceneTPluginHandler.getElementMap();
        elements.forEach(element -> {
            TPluginSceneElementBO elementBO = ClassUtils.createInstance(pluginSceneElementBOClass);
            elementBO.fromModel(element);
            elementMap.put(element.getSceneElement(), elementBO);
        });
    }

    /**
     * 载入网元端口数据
     * @param elementPorts 表模型-网元端口
     */
    public void loadElementPorts(List<TPluginSceneElementPortModel> elementPorts) {
        if (elementPorts == null) {
            return;
        }
        Map<String, TPluginSceneElementPortBO> elementPortMap = sceneTPluginHandler.getElementPortMap();
        elementPorts.forEach(elementPort -> {
            TPluginSceneElementPortBO elementPortBO = ClassUtils.createInstance(pluginSceneElementPortBOClass);
            elementPortBO.fromModel(elementPort);
            elementPortMap.put(elementPort.getSceneElementPort(), elementPortBO);
        });
    }

    /**
     * 从数据库读取场景
     * @return 表模型-场景对象
     */
    public TPluginSceneModel readScene() {
        String sceneId = sceneTPluginHandler.getSourceSceneId();
        return pluginSceneMapper.first(new HumanCondition().filterEqual(BasePluginSceneModel::getScene, sceneId));
    }

    /**
     * 从数据库读取网元节点
     * @param elementIds 网元节点id
     * @return 表模型-网元节点
     */
    public List<TPluginSceneElementModel> readElements(String... elementIds) {
        if (elementIds.length == 0) {
            return new ArrayList<>();
        }

        String sceneId = sceneTPluginHandler.getSourceSceneId();
        return pluginSceneElementMapper.list(new HumanCondition()
                .filterEqual(BasePluginSceneElementModel::getScene, sceneId)
                .filterIn(BasePluginSceneElementModel::getSceneElement, elementIds));
    }

    /**
     * 从数据库读取网元端口
     * @param elementPortIds 网元节点端口id
     * @return 表模型-网元端口
     */
    public List<TPluginSceneElementPortModel> readElementPorts(String... elementPortIds) {
        if (elementPortIds.length == 0) {
            return new ArrayList<>();
        }

        String sceneId = sceneTPluginHandler.getSourceSceneId();
        return pluginSceneElementPortMapper.list(new HumanCondition()
                .filterEqual(BasePluginSceneElementPortModel::getScene, sceneId)
                .filterIn(BasePluginSceneElementPortModel::getSceneElementPort, elementPortIds));
    }

    /**
     * 保存场景属性数据
     */
    public boolean saveScene() {
        TPluginSceneBO scene = sceneTPluginHandler.getScene();
        if (scene == null) {
            return true;
        }

        TPluginSceneModel existScene = readScene();
        String sceneId = sceneTPluginHandler.getSourceSceneId();
        TPluginSceneModel sceneObj = scene.toModel();
        sceneObj.setScene(sceneId);
        if (existScene != null) {
            sceneObj.setId(existScene.getId());
        }
        boolean result = pluginSceneMapper.save(sceneObj);
        scene.fromModel(sceneObj);
        return result;
    }

    /**
     * 删除场景属性数据
     */
    public boolean deleteScene() {
        TPluginSceneBO scene = sceneTPluginHandler.getScene();
        if (scene == null) {
            return true;
        }

        String sceneId = sceneTPluginHandler.getSourceSceneId();
        TPluginSceneModel sceneObj = scene.toModel();
        sceneObj.setScene(sceneId);
        int result = pluginSceneMapper.filterDelete(new HumanCondition()
                .filterEqual(BasePluginSceneModel::getScene, sceneId));

        return result > 0;
    }

    /**
     * 创建网元节点
     * @param elementIds 网元节点id
     */
    public int createElements(String... elementIds) {
        String sceneId = sceneTPluginHandler.getSourceSceneId();

        // 删除存在的
        deleteElements(elementIds);

        // 创建
        Map<String, TPluginSceneElementBO> elementMap = sceneTPluginHandler.getElementMap();
        List<TPluginSceneElementModel> elements = new ArrayList<>();
        for (String elementId : elementIds) {
            TPluginSceneElementBO elementBO = elementMap.getOrDefault(elementId, null);
            if (elementBO == null) {
                continue;
            }

            TPluginSceneElementModel element = elementBO.toModel();
            element.setScene(sceneId);
            element.setSceneElement(elementId);
            elements.add(element);
        }
        return pluginSceneElementMapper.bulkCreate(elements).getSuccessCount();
    }

    /**
     * 更新网元节点
     * @param elementIds 网元节点id
     */
    public int updateElements(String... elementIds) {
        Map<String, TPluginSceneElementBO> elementMap = sceneTPluginHandler.getElementMap();
        List<TPluginSceneElementModel> updatingElements = new ArrayList<>();
        List<String> creatingElementIds = new ArrayList<>(Arrays.asList(elementIds));
        // 更新已存在的
        List<TPluginSceneElementModel> existElements = readElements(elementIds);
        existElements.forEach(existElement -> {
            String elementId = existElement.getSceneElement();
            creatingElementIds.remove(elementId);
            TPluginSceneElementBO elementBO = elementMap.getOrDefault(elementId, null);
            if (elementBO == null) {
                return;
            }
            TPluginSceneElementModel element = elementBO.toModel();
            element.setId(existElement.getId());
            updatingElements.add(element);
        });
        int updateCount = pluginSceneElementMapper.bulkUpdate(updatingElements).getSuccessCount();

        // 创建不存在的
        int createCount = createElements(creatingElementIds.toArray(new String[0]));

        return updateCount + createCount;
    }

    /**
     * 删除网元节点
     * @param elementIds 网元节点id
     */
    public int deleteElements(String... elementIds) {
        if (elementIds.length == 0) {
            return 0;
        }
        String sceneId = sceneTPluginHandler.getSourceSceneId();
        return pluginSceneElementMapper.filterDelete(new HumanCondition()
                .filterEqual(BasePluginSceneElementModel::getScene, sceneId)
                .filterIn(BasePluginSceneElementModel::getSceneElement, elementIds));
    }

    /**
     * 创建网元端口
     * @param elementPortIds 网元端口id
     */
    public int createElementPorts(String... elementPortIds) {
        String sceneId = sceneTPluginHandler.getSourceSceneId();

        // 删除存在的
        deleteElementPorts(elementPortIds);

        // 创建
        Map<String, TPluginSceneElementPortBO> elementPortMap = sceneTPluginHandler.getElementPortMap();
        List<TPluginSceneElementPortModel> elementPorts = new ArrayList<>();
        for (String elementPortId : elementPortIds) {
            TPluginSceneElementPortBO elementPortBO = elementPortMap.getOrDefault(elementPortId, null);
            if (elementPortBO == null) {
                continue;
            }

            TPluginSceneElementPortModel elementPort = elementPortBO.toModel();
            elementPort.setScene(sceneId);
            elementPort.setSceneElementPort(elementPortId);
            elementPorts.add(elementPort);
        }
        return pluginSceneElementPortMapper.bulkCreate(elementPorts).getSuccessCount();
    }

    /**
     * 更新网元端口
     * @param elementPortIds 网元端口id
     */
    public int updateElementPorts(String... elementPortIds) {
        Map<String, TPluginSceneElementPortBO> elementPortMap = sceneTPluginHandler.getElementPortMap();
        List<TPluginSceneElementPortModel> updatingElementPorts = new ArrayList<>();
        List<String> creatingElementPortIds = new ArrayList<>(Arrays.asList(elementPortIds));
        // 更新已存在的
        List<TPluginSceneElementPortModel> existElementPorts = readElementPorts(elementPortIds);
        existElementPorts.forEach(existElementPort -> {
            String elementPortId = existElementPort.getSceneElementPort();
            creatingElementPortIds.remove(elementPortId);
            TPluginSceneElementPortBO elementPortBO = elementPortMap.getOrDefault(elementPortId, null);
            if (elementPortBO == null) {
                return;
            }
            TPluginSceneElementPortModel elementPort = elementPortBO.toModel();
            elementPort.setId(existElementPort.getId());
            updatingElementPorts.add(elementPort);
        });
        int updateCount = pluginSceneElementPortMapper.bulkUpdate(updatingElementPorts).getSuccessCount();

        // 创建不存在的
        int createCount = createElementPorts(creatingElementPortIds.toArray(new String[0]));

        return updateCount + createCount;
    }

    /**
     * 删除网元端口
     * @param elementPortIds 网元端口id
     */
    public int deleteElementPorts(String... elementPortIds) {
        if (elementPortIds.length == 0) {
            return 0;
        }

        String sceneId = sceneTPluginHandler.getSourceSceneId();
        return pluginSceneElementPortMapper.filterDelete(new HumanCondition()
                .filterEqual(BasePluginSceneElementPortModel::getScene, sceneId)
                .filterIn(BasePluginSceneElementPortModel::getSceneElementPort, elementPortIds));
    }

    /**
     * 初始化
     */
    public void init(TPluginHandler sceneTPluginHandler) {
        this.initClass();
        this.initMapper();

        this.sceneTPluginHandler = sceneTPluginHandler;
    }

    protected abstract void initClass() ;


    protected abstract void initMapper() ;

}
