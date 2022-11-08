package cn.cyberpeace.mp.sceneplugincollection.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneModel;
import lombok.Data;

@Data
public class BaseCollectionPluginSceneModel extends BasePluginSceneModel {
    @DefaultBool
    private Boolean enableFlowCollect;
    @DefaultBool
    private Boolean enableProbeCollect;
}
