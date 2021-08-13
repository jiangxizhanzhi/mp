package cn.cyberpeace.mp.sceneplugincloud.plugin;

import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.modelhandler.CloudScenePluginModelHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@PluginKey(PluginConstant.PLUGIN_KEY)
public class CloudScenePluginHandler extends ScenePluginHandler<CloudPluginSceneBO,
        CloudPluginSceneElementBO, CloudPluginSceneElementPortBO> {

    private CloudScenePluginModelHandler pluginModelHandler;

    @Override
    public void init(String sceneId, ScenePluginDataBO pluginDataBO) {
        super.init(sceneId, pluginDataBO);
        pluginModelHandler = new CloudScenePluginModelHandler(this);
    }

    @Override
    public void init(String sceneId, List<String> elementIds, List<String> elementPortIds) {
        super.init(sceneId, elementIds, elementPortIds);
        pluginModelHandler = new CloudScenePluginModelHandler(this);
    }

    @Override
    public void reload() {

    }

    @Override
    public void save() {

    }

}
