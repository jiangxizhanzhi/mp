package cn.cyberpeace.mp.scenepluginconfig.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.scenepluginconfig.bo.base.BaseConfigPluginSceneBO;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigPluginSceneConfigModel;
import lombok.Data;

@Data
public class ConfigPluginSceneConfigBO
        extends BaseConfigPluginSceneBO<ConfigPluginSceneConfigModel, ConfigPluginSceneConfigBO, SceneConfigBO> {
    @Override
    public ConfigPluginSceneConfigModel createModel() {
        return new ConfigPluginSceneConfigModel();
    }
}
