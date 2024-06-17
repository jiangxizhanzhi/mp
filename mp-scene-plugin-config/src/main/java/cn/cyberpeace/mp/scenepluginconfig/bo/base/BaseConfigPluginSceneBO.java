package cn.cyberpeace.mp.scenepluginconfig.bo.base;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneBO;
import cn.cyberpeace.mp.scenepluginconfig.model.base.BaseConfigPluginSceneModel;
import lombok.Data;

@Data
public abstract class BaseConfigPluginSceneBO<M extends BaseConfigPluginSceneModel,
        T extends BaseConfigPluginSceneBO<M, T, S>, S extends BaseSceneBO<?, S>>
        extends BasePluginSceneBO<M, T, S> {
}
