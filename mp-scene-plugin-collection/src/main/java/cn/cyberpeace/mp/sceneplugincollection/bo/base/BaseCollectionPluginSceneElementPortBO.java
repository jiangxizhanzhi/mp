package cn.cyberpeace.mp.sceneplugincollection.bo.base;

import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.model.base.BaseCollectionPluginSceneElementPortModel;
import lombok.Data;

@Data
public abstract class BaseCollectionPluginSceneElementPortBO<M extends BaseCollectionPluginSceneElementPortModel>
        extends BasePluginSceneElementPortBO<M> {
}
