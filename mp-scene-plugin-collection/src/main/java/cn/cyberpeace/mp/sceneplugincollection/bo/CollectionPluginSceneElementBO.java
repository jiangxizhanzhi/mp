package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.BaseCollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneElementModel;
import lombok.Data;

/**
 * @ClassName CollectionPluginSceneElementBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCollectionPluginSceneElementBO
 */
@Data
public class CollectionPluginSceneElementBO
        extends BaseCollectionPluginSceneElementBO<CollectionPluginSceneElementModel, CollectionPluginSceneElementBO,
        SceneElementBO> {

    @Override
    public CollectionPluginSceneElementModel createModel() {
        return new CollectionPluginSceneElementModel();
    }

}
