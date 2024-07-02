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
