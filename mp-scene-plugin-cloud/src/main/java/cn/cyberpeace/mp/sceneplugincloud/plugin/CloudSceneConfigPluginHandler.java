package cn.cyberpeace.mp.sceneplugincloud.plugin;

import cn.cyberpeace.mp.base.util.ClassUtils;
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

    }

    @Override
    public void save() {

    }

}
