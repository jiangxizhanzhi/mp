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
