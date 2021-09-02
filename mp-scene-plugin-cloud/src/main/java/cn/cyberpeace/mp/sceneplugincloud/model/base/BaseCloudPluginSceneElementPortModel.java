package cn.cyberpeace.mp.sceneplugincloud.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementPortModel;
import lombok.Data;

@Data
public class BaseCloudPluginSceneElementPortModel extends BasePluginSceneElementPortModel {

    @DefaultBool
    private Boolean nat;

}
