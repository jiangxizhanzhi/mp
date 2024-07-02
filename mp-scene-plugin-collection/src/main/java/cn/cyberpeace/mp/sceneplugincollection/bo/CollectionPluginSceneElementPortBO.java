package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.BaseCollectionPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneElementPortModel;
import lombok.Data;

/**
 * @ClassName CollectionPluginSceneElementPortBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCollectionPluginSceneElementPortBO
 */
@Data
public class CollectionPluginSceneElementPortBO
        extends BaseCollectionPluginSceneElementPortBO<CollectionPluginSceneElementPortModel,
        CollectionPluginSceneElementPortBO, SceneElementPortBO> {

    @Override
    public CollectionPluginSceneElementPortModel createModel() {
        return new CollectionPluginSceneElementPortModel();
    }

}
