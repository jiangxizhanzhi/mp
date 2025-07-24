package cn.cyberpeace.mp.scenepluginflow.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.BaseFlowPluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FlowPluginSceneBO extends BaseFlowPluginSceneBO<FlowPluginSceneModel, FlowPluginSceneBO, SceneBO> {

    @ApiModelProperty(value = "流量发生器id")
    private String executor;

    @Override
    public FlowPluginSceneModel createModel() {
        return new FlowPluginSceneModel();
    }
}
