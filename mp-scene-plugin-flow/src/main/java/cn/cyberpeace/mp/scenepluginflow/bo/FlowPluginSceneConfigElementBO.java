 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowPluginSceneConfigElementBO.java.
 // File size 626.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.BaseFlowPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneConfigElementModel;
import lombok.Data;

@Data
public class FlowPluginSceneConfigElementBO extends BaseFlowPluginSceneElementBO<FlowPluginSceneConfigElementModel,
        FlowPluginSceneConfigElementBO, SceneConfigElementBO> {
    @Override
    public FlowPluginSceneConfigElementModel createModel() {
        return new FlowPluginSceneConfigElementModel();
    }
}
