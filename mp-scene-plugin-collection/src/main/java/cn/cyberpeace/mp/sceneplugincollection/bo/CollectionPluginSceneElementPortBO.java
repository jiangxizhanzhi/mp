 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectionPluginSceneElementPortBO.java.
 // File size 881.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
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
