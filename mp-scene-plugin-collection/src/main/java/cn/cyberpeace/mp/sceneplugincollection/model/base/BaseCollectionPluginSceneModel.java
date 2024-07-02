package cn.cyberpeace.mp.sceneplugincollection.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneModel;
import lombok.Data;

/**
 * @ClassName BaseCollectionPluginSceneModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePluginSceneModel
 */
@Data
public class BaseCollectionPluginSceneModel extends BasePluginSceneModel {
    @DefaultBool
    private Boolean enableFlowCollect;
    @DefaultBool
    private Boolean enableProbeCollect;
    @DefaultBool
    private Boolean enableFlowMirror;
}
