package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementPortModel;
import lombok.Data;

@Data
public class CloudPluginSceneConfigElementPortBO
        extends BaseCloudPluginSceneElementPortBO<CloudPluginSceneConfigElementPortModel,
        CloudPluginSceneConfigElementPortBO, SceneConfigElementPortBO> {
    @Override
    public CloudPluginSceneConfigElementPortModel createModel() {
        return new CloudPluginSceneConfigElementPortModel();
    }
}
