package cn.cyberpeace.mp.sceneplugingns3.bo.base;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneBO;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneModel;
import lombok.Data;

@Data
public abstract class BaseGns3PluginSceneBO<M extends BasePluginSceneModel,
        T extends BasePluginSceneBO<M, T, S>, S extends BaseSceneBO<?, S>>
        extends BasePluginSceneBO<M, T, S> {
}
