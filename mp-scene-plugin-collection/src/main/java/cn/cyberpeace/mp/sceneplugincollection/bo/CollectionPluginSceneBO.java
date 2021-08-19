package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.sceneplugincollection.bo.base.BaseCollectionPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneModel;
import lombok.Data;

@Data
public class CollectionPluginSceneBO extends BaseCollectionPluginSceneBO<CollectionPluginSceneModel> {
    @Override
    public CollectionPluginSceneModel createModel() {
        return new CollectionPluginSceneModel();
    }
}
