package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneModel;
import lombok.Data;

@Data
public class CloudPluginSceneBO extends BaseCloudPluginSceneBO<CloudPluginSceneModel, CloudPluginSceneBO, SceneBO> {
    @Override
    public CloudPluginSceneModel createModel() {
        return new CloudPluginSceneModel();
    }
}
