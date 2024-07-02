package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.BaseCollectionPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneConfigModel;
import lombok.Data;

/**
 * @ClassName CollectionPluginSceneConfigBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCollectionPluginSceneBO
 */
@Data
public class CollectionPluginSceneConfigBO extends BaseCollectionPluginSceneBO<CollectionPluginSceneConfigModel,
        CollectionPluginSceneConfigBO, SceneConfigBO> {
    @Override
    public CollectionPluginSceneConfigModel createModel() {
        return new CollectionPluginSceneConfigModel();
    }
}
