package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import lombok.Data;

@Data
public class CloudPluginSceneElementBO extends BaseCloudPluginSceneElementBO<CloudPluginSceneElementModel> {
    @Override
    public CloudPluginSceneElementModel createModel() {
        return new CloudPluginSceneElementModel();
    }
}
