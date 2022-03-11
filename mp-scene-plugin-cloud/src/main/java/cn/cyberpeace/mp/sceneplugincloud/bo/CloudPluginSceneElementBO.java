package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.CoreType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class CloudPluginSceneElementBO extends BaseCloudPluginSceneElementBO<CloudPluginSceneElementModel,
        CloudPluginSceneElementBO, SceneElementBO> {

    @JsonIgnore
    private CoreType coreType;

    @Override
    public CloudPluginSceneElementModel createModel() {
        return new CloudPluginSceneElementModel();
    }

}
