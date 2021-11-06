package cn.cyberpeace.mp.scenepluginflow.plugin;

import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.plugin.SceneConfigPluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneConfigBO;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneConfigElementBO;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.scenepluginflow.constant.PluginConstant;
import cn.cyberpeace.mp.scenepluginflow.plugin.modelhandler.FlowSceneConfigPluginModelHandler;
import org.springframework.stereotype.Component;


@ClassFieldClasses({
        FlowPluginSceneConfigBO.class,
        FlowPluginSceneConfigElementBO.class,
        FlowPluginSceneConfigElementPortBO.class,
        Object.class,
})
@Component
@PluginKey(value = PluginConstant.PLUGIN_KEY, priority = PluginConstant.PLUGIN_PRIORITY)
public class FlowSceneConfigPluginHandler extends SceneConfigPluginHandler<FlowPluginSceneConfigBO,
        FlowPluginSceneConfigElementBO, FlowPluginSceneConfigElementPortBO, Object> {

    private FlowSceneConfigPluginModelHandler pluginModelHandler;

    @Override
    public void initBase() {
        super.initBase();
        pluginModelHandler = new FlowSceneConfigPluginModelHandler(this);
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
