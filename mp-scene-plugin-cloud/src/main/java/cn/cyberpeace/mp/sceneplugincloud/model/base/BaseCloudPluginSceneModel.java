package cn.cyberpeace.mp.sceneplugincloud.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneModel;
import lombok.Data;

@Data
public class BaseCloudPluginSceneModel extends BasePluginSceneModel {
    @DefaultBool
    private Boolean enableRecording;
}
