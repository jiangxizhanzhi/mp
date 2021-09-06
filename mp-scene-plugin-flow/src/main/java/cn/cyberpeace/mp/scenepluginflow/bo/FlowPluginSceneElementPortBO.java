package cn.cyberpeace.mp.scenepluginflow.bo;

import cn.cyberpeace.mp.scenepluginflow.bo.base.BaseFlowPluginSceneElementPortBO;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneElementPortModel;
import lombok.Data;

@Data
public class FlowPluginSceneElementPortBO extends BaseFlowPluginSceneElementPortBO<FlowPluginSceneElementPortModel,
        FlowPluginSceneElementPortBO> {
    @Override
    public FlowPluginSceneElementPortModel createModel() {
        return new FlowPluginSceneElementPortModel();
    }
}
