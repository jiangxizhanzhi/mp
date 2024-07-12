 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowPluginSceneConfigElementPortBO.java.
 // File size 675.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.BaseFlowPluginSceneElementPortBO;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneConfigElementPortModel;
import lombok.Data;

@Data
public class FlowPluginSceneConfigElementPortBO
        extends BaseFlowPluginSceneElementPortBO<FlowPluginSceneConfigElementPortModel,
        FlowPluginSceneConfigElementPortBO, SceneConfigElementPortBO> {
    @Override
    public FlowPluginSceneConfigElementPortModel createModel() {
        return new FlowPluginSceneConfigElementPortModel();
    }
}
