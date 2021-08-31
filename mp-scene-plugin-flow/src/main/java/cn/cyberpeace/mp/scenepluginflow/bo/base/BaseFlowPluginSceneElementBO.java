package cn.cyberpeace.mp.scenepluginflow.bo.base;

import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.model.base.BaseFlowPluginSceneElementModel;
import lombok.Data;

@Data
public abstract class BaseFlowPluginSceneElementBO<M extends BaseFlowPluginSceneElementModel,
        T extends BaseFlowPluginSceneElementBO<M, T>> extends BasePluginSceneElementBO<M, T> {
}
