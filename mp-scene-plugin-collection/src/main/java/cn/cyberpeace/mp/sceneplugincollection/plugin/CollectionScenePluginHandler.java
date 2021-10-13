package cn.cyberpeace.mp.sceneplugincollection.plugin;

import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.plugin.modelhandler.CollectionScenePluginModelHandler;
import org.springframework.stereotype.Component;


@ClassFieldClasses({
        CollectionPluginSceneBO.class,
        CollectionPluginSceneElementBO.class,
        CollectionPluginSceneElementPortBO.class,
        Object.class,
})
@Component
@PluginKey(value = PluginConstant.PLUGIN_KEY, priority = PluginConstant.PLUGIN_PRIORITY)
public class CollectionScenePluginHandler extends ScenePluginHandler<CollectionPluginSceneBO,
        CollectionPluginSceneElementBO, CollectionPluginSceneElementPortBO, Object> {

    private CollectionScenePluginModelHandler pluginModelHandler;

    private void loadRelated() {
        getElementMap().forEach((nodeId, element) -> {
            var srcElement = getSrcElement(nodeId);
            element.setSrcElement(srcElement);
        });
    }

    @Override
    public void fromData(ScenePluginDataBO pluginData) {
        super.fromData(pluginData);
        loadRelated();
    }

    @Override
    public void correctData() {
        if (getScene() == null) {
            setScene(ClassUtils.createInstance(getSceneBOClass()));
        }
    }

    @Override
    public void initBase() {
        super.initBase();
        pluginModelHandler = new CollectionScenePluginModelHandler(this);
    }

    @Override
    public void reload() {
        setScene(null);
        getElementMap().clear();
        getElementPortMap().clear();

        pluginModelHandler.loadScene(pluginModelHandler.readScene());
        pluginModelHandler.loadElements(pluginModelHandler.readElements(getElementIds().toArray(new String[0])));
        pluginModelHandler.loadElementPorts(pluginModelHandler.readElementPorts(getElementPortIds()
                .toArray(new String[0])));
    }

    @Override
    public void saveScene() {
        pluginModelHandler.saveScene();
    }

    @Override
    public void deleteScene() {
        pluginModelHandler.deleteScene();
    }

    @Override
    public void createElements(String... elementIds) {
        pluginModelHandler.createElements(elementIds);
    }

    @Override
    public void updateElements(String... elementIds) {
        pluginModelHandler.updateElements(elementIds);
    }

    @Override
    public void deleteElements(String... elementIds) {
        pluginModelHandler.deleteElements(elementIds);
    }

    @Override
    public void createElementPorts(String... elementPortIds) {
        pluginModelHandler.createElementPorts(elementPortIds);
    }

    @Override
    public void updateElementPorts(String... elementPortIds) {
        pluginModelHandler.updateElementPorts(elementPortIds);
    }

    @Override
    public void deleteElementPorts(String... elementPortIds) {
        pluginModelHandler.deleteElementPorts(elementPortIds);
    }

}
