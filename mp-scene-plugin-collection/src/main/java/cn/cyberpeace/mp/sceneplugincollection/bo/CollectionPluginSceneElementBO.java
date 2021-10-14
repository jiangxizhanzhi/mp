package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.BaseCollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneElementModel;
import lombok.Data;

@Data
public class CollectionPluginSceneElementBO
        extends BaseCollectionPluginSceneElementBO<CollectionPluginSceneElementModel, CollectionPluginSceneElementBO,
        SceneElementBO> {

    @Override
    public CollectionPluginSceneElementModel createModel() {
        return new CollectionPluginSceneElementModel();
    }

}
