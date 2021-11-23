package cn.cyberpeace.mp.scenepluginflow.bo.base;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.scenepluginflow.model.base.BaseFlowPluginSceneElementPortModel;
import lombok.Data;

@Data
public abstract class BaseFlowPluginSceneElementPortBO<M extends BaseFlowPluginSceneElementPortModel,
        T extends BaseFlowPluginSceneElementPortBO<M, T, S>, S extends BaseSceneElementPortBO<?, S, ?>>
        extends BasePluginSceneElementPortBO<M, T, S> {
}
