package cn.cyberpeace.mp.scenepluginconfig.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementModel;
import lombok.Data;

@Data
public class BaseConfigPluginSceneElementModel extends BasePluginSceneElementModel {
    @DefaultStr
    private String configs;
    @DefaultStr
    private String custom;
    @DefaultStr
    private String xdConfig;
    @DefaultStr
    private String timeConfig;
}
