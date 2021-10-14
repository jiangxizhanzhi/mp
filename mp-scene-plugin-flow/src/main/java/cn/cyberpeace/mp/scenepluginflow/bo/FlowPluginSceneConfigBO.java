package cn.cyberpeace.mp.scenepluginflow.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.BaseFlowPluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneConfigModel;
import lombok.Data;

@Data
public class FlowPluginSceneConfigBO
        extends BaseFlowPluginSceneBO<FlowPluginSceneConfigModel, FlowPluginSceneConfigBO, SceneConfigBO> {
    @Override
    public FlowPluginSceneConfigModel createModel() {
        return new FlowPluginSceneConfigModel();
    }
}
