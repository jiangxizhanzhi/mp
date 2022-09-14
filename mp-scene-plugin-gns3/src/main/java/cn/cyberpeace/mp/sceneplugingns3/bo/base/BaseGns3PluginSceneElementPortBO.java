package cn.cyberpeace.mp.sceneplugingns3.bo.base;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementPortModel;
import lombok.Data;

@Data
public abstract class BaseGns3PluginSceneElementPortBO<M extends BasePluginSceneElementPortModel,
        T extends BaseGns3PluginSceneElementPortBO<M, T, S>, S extends BaseSceneElementPortBO<?, S, ?>>
        extends BasePluginSceneElementPortBO<M, T, S> {
}
