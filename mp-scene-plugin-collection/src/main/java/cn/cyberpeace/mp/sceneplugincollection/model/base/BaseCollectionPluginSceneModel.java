package cn.cyberpeace.mp.sceneplugincollection.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneModel;
import lombok.Data;

@Data
public class BaseCollectionPluginSceneModel extends BasePluginSceneModel {
    @DefaultBool(true)
    private Boolean enableFlowCollect;
    @DefaultBool(true)
    private Boolean enableProbeCollect;
}
