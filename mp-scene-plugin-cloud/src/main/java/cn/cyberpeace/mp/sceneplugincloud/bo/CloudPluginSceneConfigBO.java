package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigModel;
import lombok.Data;

@Data
public class CloudPluginSceneConfigBO extends BaseCloudPluginSceneBO<CloudPluginSceneConfigModel> {
    @Override
    public CloudPluginSceneConfigModel createModel() {
        return new CloudPluginSceneConfigModel();
    }
}
