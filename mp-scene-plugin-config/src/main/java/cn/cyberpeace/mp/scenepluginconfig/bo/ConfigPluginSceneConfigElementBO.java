package cn.cyberpeace.mp.scenepluginconfig.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scenepluginconfig.bo.base.BaseConfigPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigPluginSceneConfigElementModel;
import lombok.Data;

@Data
public class ConfigPluginSceneConfigElementBO extends BaseConfigPluginSceneElementBO<ConfigPluginSceneConfigElementModel,
        ConfigPluginSceneConfigElementBO, SceneConfigElementBO> {
    @Override
    public ConfigPluginSceneConfigElementModel createModel() {
        return new ConfigPluginSceneConfigElementModel();
    }
}
