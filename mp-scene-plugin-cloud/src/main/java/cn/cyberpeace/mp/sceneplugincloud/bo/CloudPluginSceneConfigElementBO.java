package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementModel;
import lombok.Data;

@Data
public class CloudPluginSceneConfigElementBO extends BaseCloudPluginSceneElementBO<CloudPluginSceneConfigElementModel,
        CloudPluginSceneConfigElementBO, SceneConfigElementBO> {
    @Override
    public CloudPluginSceneConfigElementModel createModel() {
        return new CloudPluginSceneConfigElementModel();
    }
}
