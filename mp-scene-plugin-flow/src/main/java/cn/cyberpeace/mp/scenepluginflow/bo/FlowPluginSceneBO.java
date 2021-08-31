package cn.cyberpeace.mp.scenepluginflow.bo;

import cn.cyberpeace.mp.scenepluginflow.bo.base.BaseFlowPluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneModel;
import lombok.Data;

@Data
public class FlowPluginSceneBO extends BaseFlowPluginSceneBO<FlowPluginSceneModel, FlowPluginSceneBO> {
    @Override
    public FlowPluginSceneModel createModel() {
        return new FlowPluginSceneModel();
    }
}
