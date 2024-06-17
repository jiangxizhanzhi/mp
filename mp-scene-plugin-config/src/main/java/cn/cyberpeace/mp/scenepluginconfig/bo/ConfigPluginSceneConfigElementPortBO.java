package cn.cyberpeace.mp.scenepluginconfig.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scenepluginconfig.bo.base.BaseConfigPluginSceneElementPortBO;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigPluginSceneConfigElementPortModel;
import lombok.Data;

@Data
public class ConfigPluginSceneConfigElementPortBO
        extends BaseConfigPluginSceneElementPortBO<ConfigPluginSceneConfigElementPortModel,
        ConfigPluginSceneConfigElementPortBO, SceneConfigElementPortBO> {
    @Override
    public ConfigPluginSceneConfigElementPortModel createModel() {
        return new ConfigPluginSceneConfigElementPortModel();
    }
}
