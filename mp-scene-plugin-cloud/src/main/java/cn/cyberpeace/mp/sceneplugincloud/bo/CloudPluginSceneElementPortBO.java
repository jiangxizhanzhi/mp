 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudPluginSceneElementPortBO.java.
 // File size 955.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementPortModel;
import lombok.Data;

/**
 * @ClassName CloudPluginSceneElementPortBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPluginSceneElementPortBO
 */
@Data
public class CloudPluginSceneElementPortBO extends BaseCloudPluginSceneElementPortBO<CloudPluginSceneElementPortModel,
        CloudPluginSceneElementPortBO, SceneElementPortBO> {

    private String logicNetworkId;

    private String distributedPortMappingIp;

    /**
     * createModel
     *
     * @return
     */
    @Override
    public CloudPluginSceneElementPortModel createModel() {
        return new CloudPluginSceneElementPortModel();
    }

}
