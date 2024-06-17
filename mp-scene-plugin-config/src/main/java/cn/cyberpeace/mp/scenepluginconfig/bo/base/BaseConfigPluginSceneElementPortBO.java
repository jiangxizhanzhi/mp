package cn.cyberpeace.mp.scenepluginconfig.bo.base;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.scenepluginconfig.model.base.BaseConfigPluginSceneElementPortModel;
import lombok.Data;

@Data
public abstract class BaseConfigPluginSceneElementPortBO<M extends BaseConfigPluginSceneElementPortModel,
        T extends BaseConfigPluginSceneElementPortBO<M, T, S>, S extends BaseSceneElementPortBO<?, S, ?>>
        extends BasePluginSceneElementPortBO<M, T, S> {
}
