package cn.cyberpeace.mp.scene.core.plugin.base;

import cn.cyberpeace.mp.base.annotation.BeanFieldNames;
import cn.cyberpeace.mp.base.annotation.ClassFieldNames;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.ClassUtils;
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

/**
 * 场景插件model实现处理基类
 *
 * @param <S0> 表模型类   场景模板/场景实例
 * @param <E0> 表模型类   网元节点
 * @param <P0> 表模型类   网元端口
 * @param <S> 业务模型类   场景模板/场景实例
 * @param <E> 业务模型类   网元节点
 * @param <P> 业务模型类   网元端口
 * @param <M1> mapper类  场景模板/场景实例
 * @param <M2> mapper类  网元节点
 * @param <M3> mapper类  网元端口
 * @param <H> 场景插件处理类
 */
@ClassFieldNames({
    "pluginSceneBOClass",
    "pluginSceneElementBOClass",
    "pluginSceneElementPortBOClass",
    "pluginSceneModelClass",
    "pluginSceneElementModelClass",
    "pluginSceneElementPortModelClass",
})
@BeanFieldNames({
    "pluginSceneMapper",
    "pluginSceneElementMapper",
    "pluginSceneElementPortMapper",
})
public class BasePluginModelHandler<
        S0 extends BasePluginSceneModel,
        E0 extends BasePluginSceneElementModel,
        P0 extends BasePluginSceneElementPortModel,
        S extends BasePluginSceneBO<S0>,
        E extends BasePluginSceneElementBO<E0>,
        P extends BasePluginSceneElementPortBO<P0>,
        M1 extends BasePluginSceneMapper<S0>,
        M2 extends BasePluginSceneElementMapper<E0>,
        M3 extends BasePluginSceneElementPortMapper<P0>,
        H extends BasePluginHandler<S, E, P>> {
    private H scenePluginHandler;

    // 通过ClassFieldNames注解和实现类ClassFieldClasses注解，初始化调用ClassUtils.loadClassFields(this)设置
    private Class<S> pluginSceneBOClass;
    private Class<E> pluginSceneElementBOClass;
    private Class<P> pluginSceneElementPortBOClass;
    private Class<S0> pluginSceneModelClass;
    private Class<E0> pluginSceneElementModelClass;
    private Class<P0> pluginSceneElementPortModelClass;

    // 通过BeanFieldNames注解和实现类BeanFieldClasses注解，初始化调用BeanUtils.loadBeanFields(this)设置
    private M1 pluginSceneMapper;
    private M2 pluginSceneElementMapper;
    private M3 pluginSceneElementPortMapper;

    /**
     * 载入场景基础
     * @param scene 表模型-场景基础
     */
    public void loadScene(S0 scene) {
        if (scene == null) {
            return;
        }
        var sceneBO = ClassUtils.createInstance(pluginSceneBOClass);
        sceneBO.fromModel(scene);
        scenePluginHandler.setScene(sceneBO);
    }

    /**
     * 载入网元节点
     * @param elements 表模型-网元节点
     */
    public void loadElements(List<E0> elements) {
        if (elements == null) {
            return;
        }
        var elementMap = scenePluginHandler.getElementMap();
        elements.forEach(element -> {
            var elementBO = ClassUtils.createInstance(pluginSceneElementBOClass);
            elementBO.fromModel(element);
            elementMap.put(element.getSceneElement(), elementBO);
        });
    }

    /**
     * 载入网元端口数据
     * @param elementPorts 表模型-网元端口
     */
    public void loadElementPorts(List<P0> elementPorts) {
        if (elementPorts == null) {
            return;
        }
        var elementPortMap = scenePluginHandler.getElementPortMap();
        elementPorts.forEach(elementPort -> {
            var elementPortBO = ClassUtils.createInstance(pluginSceneElementPortBOClass);
            elementPortBO.fromModel(elementPort);
            elementPortMap.put(elementPort.getSceneElementPort(), elementPortBO);
        });
    }

    /**
     * 从数据库读取场景
     * @return 表模型-场景对象
     */
    public S0 readScene() {
        var sceneId = scenePluginHandler.getSourceSceneId();
        return pluginSceneMapper.first(new HumanCondition().filterEqual(BasePluginSceneModel::getScene, sceneId));
    }

    /**
     * 从数据库读取网元节点
     * @param elementIds 网元节点id
     * @return 表模型-网元节点
     */
    public List<E0> readElements(String... elementIds) {
        var sceneId = scenePluginHandler.getSourceSceneId();
        return pluginSceneElementMapper.list(new HumanCondition()
                .filterEqual(BasePluginSceneElementModel::getScene, sceneId)
                .filterIn(BasePluginSceneElementModel::getSceneElement, elementIds));
    }

    /**
     * 从数据库读取网元端口
     * @param elementPortIds 网元节点端口id
     * @return 表模型-网元端口
     */
    public List<P0> readElementPorts(String... elementPortIds) {
        var sceneId = scenePluginHandler.getSourceSceneId();
        return pluginSceneElementPortMapper.list(new HumanCondition()
                .filterEqual(BasePluginSceneElementPortModel::getScene, sceneId)
                .filterIn(BasePluginSceneElementPortModel::getSceneElementPort, elementPortIds));
    }

    /**
     * 保存场景属性数据
     */
    public void saveScene() {
        var sceneId = scenePluginHandler.getSourceSceneId();
        var scene = scenePluginHandler.getScene();
        S0 sceneObj = scene.toModel();
        sceneObj.setScene(sceneId);
        pluginSceneMapper.save(sceneObj);
        scene.fromModel(sceneObj);
    }

    /**
     * 删除场景属性数据
     */
    public void deleteScene() {
        var sceneId = scenePluginHandler.getSourceSceneId();
        var scene = scenePluginHandler.getScene();
        S0 sceneObj = scene.toModel();
        sceneObj.setScene(sceneId);
        pluginSceneMapper.filterDelete(new HumanCondition().filterEqual(BasePluginSceneModel::getScene, sceneId));
    }

    /**
     * 创建网元节点
     * @param elementIds 网元节点id
     */
    public void createElements(String... elementIds) {
        var sceneId = scenePluginHandler.getSourceSceneId();

        // 删除存在的
        deleteElements(elementIds);

        // 创建
        var elementMap = scenePluginHandler.getElementMap();
        List<E0> elements = new ArrayList<>();
        for (String elementId : elementIds) {
            var elementBO = elementMap.getOrDefault(elementId, null);
            if (elementBO == null) {
                continue;
            }

            E0 element = elementBO.toModel();
            element.setScene(sceneId);
            element.setSceneElement(elementId);
            elements.add(element);
        }
        pluginSceneElementMapper.bulkCreate(elements);
    }

    /**
     * 更新网元节点
     * @param elementIds 网元节点id
     */
    public void updateElements(String... elementIds) {
        var elementMap = scenePluginHandler.getElementMap();
        List<E0> updatingElements = new ArrayList<>();
        List<String> creatingElementIds = Arrays.asList(elementIds);
        // 更新已存在的
        List<E0> existElements = readElements(elementIds);
        existElements.forEach(existElement -> {
            var elementId = existElement.getSceneElement();
            creatingElementIds.remove(elementId);
            var elementBO = elementMap.getOrDefault(elementId, null);
            if (elementBO == null) {
                return;
            }
            var element = elementBO.toModel();
            element.setId(existElement.getId());
            updatingElements.add(element);
        });
        pluginSceneElementMapper.bulkUpdate(updatingElements);

        // 创建不存在的
        createElements(creatingElementIds.toArray(new String[0]));
    }

    /**
     * 删除网元节点
     * @param elementIds 网元节点id
     */
    public void deleteElements(String... elementIds) {
        var sceneId = scenePluginHandler.getSourceSceneId();
        pluginSceneElementMapper.filterDelete(new HumanCondition()
                .filterEqual(BasePluginSceneElementModel::getScene, sceneId)
                .filterIn(BasePluginSceneElementModel::getSceneElement, elementIds));
    }

    /**
     * 创建网元端口
     * @param elementPortIds 网元端口id
     */
    public void createElementPorts(String... elementPortIds) {
        var sceneId = scenePluginHandler.getSourceSceneId();

        // 删除存在的
        deleteElementPorts(elementPortIds);

        // 创建
        var elementPortMap = scenePluginHandler.getElementPortMap();
        List<P0> elementPorts = new ArrayList<>();
        for (String elementPortId : elementPortIds) {
            var elementPortBO = elementPortMap.getOrDefault(elementPortId, null);
            if (elementPortBO == null) {
                continue;
            }

            P0 elementPort = elementPortBO.toModel();
            elementPort.setScene(sceneId);
            elementPort.setSceneElementPort(elementPortId);
            elementPorts.add(elementPort);
        }
        pluginSceneElementPortMapper.bulkCreate(elementPorts);
    }

    /**
     * 更新网元端口
     * @param elementPortIds 网元端口id
     */
    public void updateElementPorts(String... elementPortIds) {
        var elementPortMap = scenePluginHandler.getElementPortMap();
        List<P0> updatingElementPorts = new ArrayList<>();
        List<String> creatingElementPortIds = Arrays.asList(elementPortIds);
        // 更新已存在的
        List<P0> existElementPorts = readElementPorts(elementPortIds);
        existElementPorts.forEach(existElementPort -> {
            var elementPortId = existElementPort.getSceneElementPort();
            creatingElementPortIds.remove(elementPortId);
            var elementPortBO = elementPortMap.getOrDefault(elementPortId, null);
            if (elementPortBO == null) {
                return;
            }
            var elementPort = elementPortBO.toModel();
            elementPort.setId(existElementPort.getId());
            updatingElementPorts.add(elementPort);
        });
        pluginSceneElementPortMapper.bulkUpdate(updatingElementPorts);

        // 创建不存在的
        createElementPorts(creatingElementPortIds.toArray(new String[0]));
    }

    /**
     * 删除网元端口
     * @param elementPortIds 网元端口id
     */
    public void deleteElementPorts(String... elementPortIds) {
        var sceneId = scenePluginHandler.getSourceSceneId();
        pluginSceneElementPortMapper.filterDelete(new HumanCondition()
                .filterEqual(BasePluginSceneElementPortModel::getScene, sceneId)
                .filterIn(BasePluginSceneElementPortModel::getSceneElementPort, elementPortIds));
    }

    /**
     * 初始化
     */
    public void init(H scenePluginHandler) {
        ClassUtils.loadClassFields(this);
        BeanUtils.loadBeanFields(this);

        this.scenePluginHandler = scenePluginHandler;
    }
}
