package cn.cyberpeace.mp.sceneplugincollection.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneModel;
import lombok.Data;

import java.util.Set;

@Data
public class BaseCollectionPluginSceneModel extends BasePluginSceneModel {
    @DefaultBool
    private Boolean enableFlowCollect;
    @DefaultBool
    private Boolean enableProbeCollect;
    @DefaultBool
    private Boolean enableFlowMirror;
    @DefaultStr
    private String flowCollectNodes;
    @DefaultStr
    private String customeMirror;


}
