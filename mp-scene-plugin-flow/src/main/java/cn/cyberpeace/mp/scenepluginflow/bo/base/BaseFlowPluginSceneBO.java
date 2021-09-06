package cn.cyberpeace.mp.scenepluginflow.bo.base;

import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.model.base.BaseFlowPluginSceneModel;
import lombok.Data;

@Data
public abstract class BaseFlowPluginSceneBO<M extends BaseFlowPluginSceneModel, T extends BaseFlowPluginSceneBO<M, T>>
        extends BasePluginSceneBO<M, T> {
}
