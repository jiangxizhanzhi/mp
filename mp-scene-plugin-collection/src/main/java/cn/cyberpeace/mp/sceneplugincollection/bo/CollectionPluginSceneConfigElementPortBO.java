package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.BaseCollectionPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneConfigElementPortModel;
import lombok.Data;

/**
 * @ClassName CollectionPluginSceneConfigElementPortBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCollectionPluginSceneElementPortBO
 */
@Data
public class CollectionPluginSceneConfigElementPortBO
        extends BaseCollectionPluginSceneElementPortBO<CollectionPluginSceneConfigElementPortModel,
        CollectionPluginSceneConfigElementPortBO, SceneConfigElementPortBO> {
    @Override
    public CollectionPluginSceneConfigElementPortModel createModel() {
        return new CollectionPluginSceneConfigElementPortModel();
    }
}
