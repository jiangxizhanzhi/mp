package cn.cyberpeace.mp.scenepluginconfig.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scenepluginconfig.bo.base.BaseConfigPluginSceneElementPortBO;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigPluginSceneElementPortModel;
import lombok.Data;

@Data
public class ConfigPluginSceneElementPortBO extends BaseConfigPluginSceneElementPortBO<ConfigPluginSceneElementPortModel,
        ConfigPluginSceneElementPortBO, SceneElementPortBO> {
    @Override
    public ConfigPluginSceneElementPortModel createModel() {
        return new ConfigPluginSceneElementPortModel();
    }
}
