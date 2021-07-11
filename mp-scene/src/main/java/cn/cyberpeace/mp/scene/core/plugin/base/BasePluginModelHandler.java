package cn.cyberpeace.mp.scene.core.plugin.base;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.stereotype.BeanFieldNames;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldNames;
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
        S9 extends BaseSceneBO<?, S9>,
        E9 extends BaseSceneElementBO<?, P9, E9>,
        P9 extends BaseSceneElementPortBO<?, P9, E9>,
        S0 extends BasePluginSceneModel,
        E0 extends BasePluginSceneElementModel,
        P0 extends BasePluginSceneElementPortModel,
        S extends BasePluginSceneBO<S0, S, S9>,
        E extends BasePluginSceneElementBO<E0, E, E9>,
        P extends BasePluginSceneElementPortBO<P0, P, P9>,
        M1 extends BasePluginSceneMapper<S0>,
        M2 extends BasePluginSceneElementMapper<E0>,
        M3 extends BasePluginSceneElementPortMapper<P0>,
        H extends BasePluginHandler<S, E, P, ?>> {
    private H scenePluginHandler;

    
    private Class<S> pluginSceneBOClass;
    private Class<E> pluginSceneElementBOClass;
    private Class<P> pluginSceneElementPortBOClass;
    private Class<S0> pluginSceneModelClass;
    private Class<E0> pluginSceneElementModelClass;
    private Class<P0> pluginSceneElementPortModelClass;

    
    private M1 pluginSceneMapper;
    private M2 pluginSceneElementMapper;
    private M3 pluginSceneElementPortMapper;

    
    public void loadScene(S0 scene) {
        if (scene == null) {
            return;
        }
        var sceneBO = ClassUtils.createInstance(pluginSceneBOClass);
        sceneBO.fromModel(scene);
        scenePluginHandler.setScene(sceneBO);
    }

    
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

    
    public S0 readScene() {
        var sceneId = scenePluginHandler.getSourceSceneId();
        return pluginSceneMapper.first(new HumanCondition().filterEqual(BasePluginSceneModel::getScene, sceneId));
    }

    
    public List<E0> readElements(String... elementIds) {
        if (elementIds.length == 0) {
            return new ArrayList<>();
        }

        var sceneId = scenePluginHandler.getSourceSceneId();
        return pluginSceneElementMapper.list(new HumanCondition()
                .filterEqual(BasePluginSceneElementModel::getScene, sceneId)
                .filterIn(BasePluginSceneElementModel::getSceneElement, elementIds));
    }

    
    public List<P0> readElementPorts(String... elementPortIds) {
        if (elementPortIds.length == 0) {
            return new ArrayList<>();
        }

        var sceneId = scenePluginHandler.getSourceSceneId();
        return pluginSceneElementPortMapper.list(new HumanCondition()
                .filterEqual(BasePluginSceneElementPortModel::getScene, sceneId)
                .filterIn(BasePluginSceneElementPortModel::getSceneElementPort, elementPortIds));
    }

    
    public boolean saveScene() {
        var scene = scenePluginHandler.getScene();
        if (scene == null) {
            return true;
        }

        var existScene = readScene();
        var sceneId = scenePluginHandler.getSourceSceneId();
        S0 sceneObj = scene.toModel();
        sceneObj.setScene(sceneId);
        if (existScene != null) {
            sceneObj.setId(existScene.getId());
        }
        var result = pluginSceneMapper.save(sceneObj);
        scene.fromModel(sceneObj);
        return result;
    }

    
    public boolean deleteScene() {
        var scene = scenePluginHandler.getScene();
        if (scene == null) {
            return true;
        }

        var sceneId = scenePluginHandler.getSourceSceneId();
        S0 sceneObj = scene.toModel();
        sceneObj.setScene(sceneId);
        var result = pluginSceneMapper.filterDelete(new HumanCondition()
                .filterEqual(BasePluginSceneModel::getScene, sceneId));

        return result > 0;
    }

    
    public int createElements(String... elementIds) {
        var sceneId = scenePluginHandler.getSourceSceneId();

        
        deleteElements(elementIds);

        
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
        return pluginSceneElementMapper.bulkCreate(elements).getSuccessCount();
    }

    
    public int updateElements(String... elementIds) {
        var elementMap = scenePluginHandler.getElementMap();
        List<E0> updatingElements = new ArrayList<>();
        List<String> creatingElementIds = new ArrayList<>(Arrays.asList(elementIds));
        
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
        var updateCount = pluginSceneElementMapper.bulkUpdate(updatingElements).getSuccessCount();

        
        var createCount = createElements(creatingElementIds.toArray(new String[0]));

        return updateCount + createCount;
    }

    
    public int deleteElements(String... elementIds) {
        if (elementIds.length == 0) {
            return 0;
        }
        var sceneId = scenePluginHandler.getSourceSceneId();
        return pluginSceneElementMapper.filterDelete(new HumanCondition()
                .filterEqual(BasePluginSceneElementModel::getScene, sceneId)
                .filterIn(BasePluginSceneElementModel::getSceneElement, elementIds));
    }

    
    public int createElementPorts(String... elementPortIds) {
        var sceneId = scenePluginHandler.getSourceSceneId();

        
        deleteElementPorts(elementPortIds);

        
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
        return pluginSceneElementPortMapper.bulkCreate(elementPorts).getSuccessCount();
    }

    
    public int updateElementPorts(String... elementPortIds) {
        var elementPortMap = scenePluginHandler.getElementPortMap();
        List<P0> updatingElementPorts = new ArrayList<>();
        List<String> creatingElementPortIds = new ArrayList<>(Arrays.asList(elementPortIds));
        
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
        var updateCount = pluginSceneElementPortMapper.bulkUpdate(updatingElementPorts).getSuccessCount();

        
        var createCount = createElementPorts(creatingElementPortIds.toArray(new String[0]));

        return updateCount + createCount;
    }

    
    public int deleteElementPorts(String... elementPortIds) {
        if (elementPortIds.length == 0) {
            return 0;
        }

        var sceneId = scenePluginHandler.getSourceSceneId();
        return pluginSceneElementPortMapper.filterDelete(new HumanCondition()
                .filterEqual(BasePluginSceneElementPortModel::getScene, sceneId)
                .filterIn(BasePluginSceneElementPortModel::getSceneElementPort, elementPortIds));
    }

    
    public void init(H scenePluginHandler) {
        ClassUtils.loadClassFields(this);
        BeanCommon.loadBeanFields(this);

        this.scenePluginHandler = scenePluginHandler;
    }
}
