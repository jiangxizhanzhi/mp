 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudPluginSceneConfigElementPortBO.java.
 // File size 928.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementPortModel;
import lombok.Data;

/**
 * @ClassName CloudPluginSceneConfigElementPortBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPluginSceneElementPortBO
 */
@Data
public class CloudPluginSceneConfigElementPortBO
        extends BaseCloudPluginSceneElementPortBO<CloudPluginSceneConfigElementPortModel,
        CloudPluginSceneConfigElementPortBO, SceneConfigElementPortBO> {
    /**
     * createModel
     *
     * @return
     */
    @Override
    public CloudPluginSceneConfigElementPortModel createModel() {
        return new CloudPluginSceneConfigElementPortModel();
    }
}
