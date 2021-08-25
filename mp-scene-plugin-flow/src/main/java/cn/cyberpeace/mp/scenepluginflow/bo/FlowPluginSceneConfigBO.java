package cn.cyberpeace.mp.scenepluginflow.bo;

import cn.cyberpeace.mp.scenepluginflow.bo.base.BaseFlowPluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneConfigModel;
import lombok.Data;

@Data
public class FlowPluginSceneConfigBO
        extends BaseFlowPluginSceneBO<FlowPluginSceneConfigModel, FlowPluginSceneConfigBO> {
    @Override
    public FlowPluginSceneConfigModel createModel() {
        return new FlowPluginSceneConfigModel();
    }
}
