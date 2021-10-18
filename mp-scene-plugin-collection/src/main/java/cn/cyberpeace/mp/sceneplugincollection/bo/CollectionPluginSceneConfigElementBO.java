package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.BaseCollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneConfigElementModel;
import lombok.Data;

@Data
public class CollectionPluginSceneConfigElementBO
        extends BaseCollectionPluginSceneElementBO<CollectionPluginSceneConfigElementModel,
        CollectionPluginSceneConfigElementBO, SceneConfigElementBO> {
    @Override
    public CollectionPluginSceneConfigElementModel createModel() {
        return new CollectionPluginSceneConfigElementModel();
    }
}
