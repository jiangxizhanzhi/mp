package cn.cyberpeace.mp.sceneplugingns3.plugin;


import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.plugin.SceneConfigPluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.sceneplugingns3.bo.Gns3PluginSceneConfigBO;
import cn.cyberpeace.mp.sceneplugingns3.bo.Gns3PluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugingns3.bo.Gns3PluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugingns3.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugingns3.plugin.modelhandler.Gns3SceneConfigPluginModelHandler;
import org.springframework.stereotype.Component;

@ClassFieldClasses({
        Gns3PluginSceneConfigBO.class,
        Gns3PluginSceneConfigElementBO.class,
        Gns3PluginSceneConfigElementPortBO.class,
        Object.class,
})
@Component
@PluginKey(value = PluginConstant.PLUGIN_KEY, priority = PluginConstant.PLUGIN_PRIORITY)
public class Gn3SceneConfigPluginHandler extends SceneConfigPluginHandler<Gns3PluginSceneConfigBO,
        Gns3PluginSceneConfigElementBO, Gns3PluginSceneConfigElementPortBO, Object> {

    private Gns3SceneConfigPluginModelHandler pluginModelHandler;

    @Override
    public void initBase() {
        super.initBase();
        pluginModelHandler = new Gns3SceneConfigPluginModelHandler(this);
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
}
