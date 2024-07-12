 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectionPluginSceneConfigElementBO.java.
 // File size 1015.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.BaseCollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneConfigElementModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName CollectionPluginSceneConfigElementBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCollectionPluginSceneElementBO
 */
@Data
public class CollectionPluginSceneConfigElementBO
        extends BaseCollectionPluginSceneElementBO<CollectionPluginSceneConfigElementModel,
        CollectionPluginSceneConfigElementBO, SceneConfigElementBO> {

    @ApiModelProperty(value = "是否编辑过")
    private Boolean edited;

    @Override
    public CollectionPluginSceneConfigElementModel createModel() {
        return new CollectionPluginSceneConfigElementModel();
    }
}
