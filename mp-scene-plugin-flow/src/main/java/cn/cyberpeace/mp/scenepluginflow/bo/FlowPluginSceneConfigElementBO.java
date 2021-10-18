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
