 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudPluginSceneBO.java.
 // File size 724.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneModel;
import lombok.Data;

/**
 * @ClassName CloudPluginSceneBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPluginSceneBO
 */
@Data
public class CloudPluginSceneBO extends BaseCloudPluginSceneBO<CloudPluginSceneModel, CloudPluginSceneBO, SceneBO> {
    /**
     * createModel
     *
     * @return
     */
    @Override
    public CloudPluginSceneModel createModel() {
        return new CloudPluginSceneModel();
    }
}
