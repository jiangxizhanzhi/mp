package cn.cyberpeace.mp.sceneplugincollection.plugin;

import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.plugin.SceneConfigPluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.plugin.modelhandler.CollectionSceneConfigPluginModelHandler;
import org.springframework.stereotype.Component;

import java.util.List;


@ClassFieldClasses({
        CollectionPluginSceneConfigBO.class,
        CollectionPluginSceneConfigElementBO.class,
        CollectionPluginSceneConfigElementPortBO.class,
})
@Component
@PluginKey(PluginConstant.PLUGIN_KEY)
public class CollectionSceneConfigPluginHandler extends SceneConfigPluginHandler<CollectionPluginSceneConfigBO,
        CollectionPluginSceneConfigElementBO, CollectionPluginSceneConfigElementPortBO> {

    private CollectionSceneConfigPluginModelHandler pluginModelHandler;

    @Override
    public void init(String sceneId, ScenePluginDataBO pluginDataBO) {
        pluginModelHandler = new CollectionSceneConfigPluginModelHandler(this);
        super.init(sceneId, pluginDataBO);
    }

    @Override
    public void init(String sceneId, List<String> elementIds, List<String> elementPortIds) {
        pluginModelHandler = new CollectionSceneConfigPluginModelHandler(this);
        super.init(sceneId, elementIds, elementPortIds);
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
