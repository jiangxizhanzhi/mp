package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CloudPluginSceneElementBO extends BaseCloudPluginSceneElementBO<CloudPluginSceneElementModel,
        CloudPluginSceneElementBO, SceneElementBO> {

    @Override
    public CloudPluginSceneElementModel createModel() {
        return new CloudPluginSceneElementModel();
    }

}
