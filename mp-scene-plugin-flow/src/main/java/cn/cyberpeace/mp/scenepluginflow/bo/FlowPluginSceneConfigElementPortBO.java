package cn.cyberpeace.mp.scenepluginflow.bo;

import cn.cyberpeace.mp.scenepluginflow.bo.base.BaseFlowPluginSceneElementPortBO;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneConfigElementPortModel;
import lombok.Data;

@Data
public class FlowPluginSceneConfigElementPortBO
        extends BaseFlowPluginSceneElementPortBO<FlowPluginSceneConfigElementPortModel,
        FlowPluginSceneConfigElementPortBO> {
    @Override
    public FlowPluginSceneConfigElementPortModel createModel() {
        return new FlowPluginSceneConfigElementPortModel();
    }
}
