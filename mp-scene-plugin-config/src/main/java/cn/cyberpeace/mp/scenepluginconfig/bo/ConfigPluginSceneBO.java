package cn.cyberpeace.mp.scenepluginconfig.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scenepluginconfig.bo.base.BaseConfigPluginSceneBO;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigPluginSceneModel;
import lombok.Data;

@Data
public class ConfigPluginSceneBO extends BaseConfigPluginSceneBO<ConfigPluginSceneModel, ConfigPluginSceneBO, SceneBO> {

    @Override
    public ConfigPluginSceneModel createModel() {
        return new ConfigPluginSceneModel();
    }
}
