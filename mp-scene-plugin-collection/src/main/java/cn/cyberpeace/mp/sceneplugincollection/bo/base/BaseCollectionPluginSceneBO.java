package cn.cyberpeace.mp.sceneplugincollection.bo.base;

import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneBO;
import cn.cyberpeace.mp.sceneplugincollection.model.base.BaseCollectionPluginSceneModel;
import lombok.Data;

@Data
public abstract class BaseCollectionPluginSceneBO<M extends BaseCollectionPluginSceneModel,
        T extends BaseCollectionPluginSceneBO<M, T>> extends BasePluginSceneBO<M, T> {
}
