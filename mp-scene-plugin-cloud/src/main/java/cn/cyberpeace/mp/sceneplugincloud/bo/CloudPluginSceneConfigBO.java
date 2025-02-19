package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigModel;
import lombok.Data;

@Data
public class CloudPluginSceneConfigBO extends BaseCloudPluginSceneBO<CloudPluginSceneConfigModel,
        CloudPluginSceneConfigBO, SceneConfigBO> {
    @Override
    public CloudPluginSceneConfigModel createModel() {
        return new CloudPluginSceneConfigModel();
    }
}
