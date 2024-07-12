 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudPluginSceneConfigBO.java.
 // File size 787.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigModel;
import lombok.Data;

/**
 * @ClassName CloudPluginSceneConfigBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPluginSceneBO
 */
@Data
public class CloudPluginSceneConfigBO extends BaseCloudPluginSceneBO<CloudPluginSceneConfigModel,
        CloudPluginSceneConfigBO, SceneConfigBO> {
    /**
     * createModel
     *
     * @return
     */
    @Override
    public CloudPluginSceneConfigModel createModel() {
        return new CloudPluginSceneConfigModel();
    }
}
