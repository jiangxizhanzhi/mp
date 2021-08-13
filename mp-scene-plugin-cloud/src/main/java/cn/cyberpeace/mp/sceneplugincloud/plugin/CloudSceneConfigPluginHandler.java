package cn.cyberpeace.mp.sceneplugincloud.plugin;

import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.plugin.SceneConfigPluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.modelhandler.CloudSceneConfigPluginModelHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@PluginKey(PluginConstant.PLUGIN_KEY)
public class CloudSceneConfigPluginHandler extends SceneConfigPluginHandler<CloudPluginSceneConfigBO,
        CloudPluginSceneConfigElementBO, CloudPluginSceneConfigElementPortBO> {

    private CloudSceneConfigPluginModelHandler pluginModelHandler;

    @Override
    public void init(String sceneId, ScenePluginDataBO pluginDataBO) {
        super.init(sceneId, pluginDataBO);
        pluginModelHandler = new CloudSceneConfigPluginModelHandler(this);
    }

    @Override
    public void init(String sceneId, List<String> elementIds, List<String> elementPortIds) {
        super.init(sceneId, elementIds, elementPortIds);
        pluginModelHandler = new CloudSceneConfigPluginModelHandler(this);
    }

    @Override
    public void reload() {
        setScene(null);
        getElementMap().clear();
        getElementPortMap().clear();
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
