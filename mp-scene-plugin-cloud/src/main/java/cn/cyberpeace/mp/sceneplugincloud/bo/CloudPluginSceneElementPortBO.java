package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementPortModel;
import lombok.Data;

@Data
public class CloudPluginSceneElementPortBO extends BaseCloudPluginSceneElementPortBO<CloudPluginSceneElementPortModel> {
    @Override
    public CloudPluginSceneElementPortModel createModel() {
        return new CloudPluginSceneElementPortModel();
    }
}
