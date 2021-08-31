package cn.cyberpeace.mp.sceneplugincollection.bo.base;

import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.model.base.BaseCollectionPluginSceneElementModel;
import lombok.Data;

@Data
public abstract class BaseCollectionPluginSceneElementBO<M extends BaseCollectionPluginSceneElementModel,
        T extends BaseCollectionPluginSceneElementBO<M, T>> extends BasePluginSceneElementBO<M, T> {
}
