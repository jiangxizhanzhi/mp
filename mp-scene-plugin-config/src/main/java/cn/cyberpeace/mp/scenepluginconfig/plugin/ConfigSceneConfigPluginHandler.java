package cn.cyberpeace.mp.scenepluginconfig.plugin;

import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.plugin.SceneConfigPluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scenepluginconfig.bo.ConfigPluginSceneConfigBO;
import cn.cyberpeace.mp.scenepluginconfig.bo.ConfigPluginSceneConfigElementBO;
import cn.cyberpeace.mp.scenepluginconfig.bo.ConfigPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.scenepluginconfig.constant.PluginConstant;
import cn.cyberpeace.mp.scenepluginconfig.plugin.modelhandler.ConfigSceneConfigPluginModelHandler;
import org.springframework.stereotype.Component;

@ClassFieldClasses({
        ConfigPluginSceneConfigBO.class,
        ConfigPluginSceneConfigElementBO.class,
        ConfigPluginSceneConfigElementPortBO.class,
        Object.class,
})
@Component
@PluginKey(value = PluginConstant.PLUGIN_KEY, priority = PluginConstant.PLUGIN_PRIORITY)
public class ConfigSceneConfigPluginHandler extends SceneConfigPluginHandler<ConfigPluginSceneConfigBO,
        ConfigPluginSceneConfigElementBO, ConfigPluginSceneConfigElementPortBO, Object> {

    private ConfigSceneConfigPluginModelHandler pluginModelHandler;

    @Override
    public void initBase() {
        super.initBase();
        pluginModelHandler = new ConfigSceneConfigPluginModelHandler(this);
    }

    @Override
    public void reloadData() {
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
