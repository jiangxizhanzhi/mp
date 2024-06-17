package cn.cyberpeace.mp.scenepluginconfig.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scenepluginconfig.bo.base.BaseConfigPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigPluginSceneElementModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ConfigPluginSceneElementBO extends BaseConfigPluginSceneElementBO<ConfigPluginSceneElementModel,
        ConfigPluginSceneElementBO, SceneElementBO> {
    @ApiModelProperty(value = "创建消耗时间(秒)")
    private Long createConsumeSeconds;

    @Override
    public ConfigPluginSceneElementModel createModel() {
        return new ConfigPluginSceneElementModel();
    }
}
