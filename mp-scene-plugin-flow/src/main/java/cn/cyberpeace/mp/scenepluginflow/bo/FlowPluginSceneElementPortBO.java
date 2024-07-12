 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowPluginSceneElementPortBO.java.
 // File size 618.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.BaseFlowPluginSceneElementPortBO;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneElementPortModel;
import lombok.Data;

@Data
public class FlowPluginSceneElementPortBO extends BaseFlowPluginSceneElementPortBO<FlowPluginSceneElementPortModel,
        FlowPluginSceneElementPortBO, SceneElementPortBO> {
    @Override
    public FlowPluginSceneElementPortModel createModel() {
        return new FlowPluginSceneElementPortModel();
    }
}
