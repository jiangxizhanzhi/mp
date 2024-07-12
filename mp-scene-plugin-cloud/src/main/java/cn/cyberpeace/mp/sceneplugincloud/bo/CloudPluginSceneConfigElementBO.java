 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudPluginSceneConfigElementBO.java.
 // File size 871.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementModel;
import lombok.Data;

/**
 * @ClassName CloudPluginSceneConfigElementBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPluginSceneElementBO
 */
@Data
public class CloudPluginSceneConfigElementBO extends BaseCloudPluginSceneElementBO<CloudPluginSceneConfigElementModel,
        CloudPluginSceneConfigElementBO, SceneConfigElementBO> {
    /**
     * createModel
     *
     * @return
     */
    @Override
    public CloudPluginSceneConfigElementModel createModel() {
        return new CloudPluginSceneConfigElementModel();
    }
}
