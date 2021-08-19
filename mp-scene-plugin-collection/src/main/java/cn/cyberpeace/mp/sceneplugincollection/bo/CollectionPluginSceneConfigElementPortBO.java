package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.sceneplugincollection.bo.base.BaseCollectionPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneConfigElementPortModel;
import lombok.Data;

@Data
public class CollectionPluginSceneConfigElementPortBO
        extends BaseCollectionPluginSceneElementPortBO<CollectionPluginSceneConfigElementPortModel> {
    @Override
    public CollectionPluginSceneConfigElementPortModel createModel() {
        return new CollectionPluginSceneConfigElementPortModel();
    }
}
