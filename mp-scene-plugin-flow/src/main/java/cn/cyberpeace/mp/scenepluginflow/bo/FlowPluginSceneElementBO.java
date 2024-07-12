 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowPluginSceneElementBO.java.
 // File size 582.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.BaseFlowPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneElementModel;
import lombok.Data;

@Data
public class FlowPluginSceneElementBO extends BaseFlowPluginSceneElementBO<FlowPluginSceneElementModel,
        FlowPluginSceneElementBO, SceneElementBO> {

    @Override
    public FlowPluginSceneElementModel createModel() {
        return new FlowPluginSceneElementModel();
    }

}
