package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementPortModel;
import lombok.Data;

@Data
public class CloudPluginSceneElementPortBO extends BaseCloudPluginSceneElementPortBO<CloudPluginSceneElementPortModel,
        CloudPluginSceneElementPortBO, SceneElementPortBO> {

    private String logicNetworkId;

    private String vlanMode;

    @Override
    public CloudPluginSceneElementPortModel createModel() {
        return new CloudPluginSceneElementPortModel();
    }

}
