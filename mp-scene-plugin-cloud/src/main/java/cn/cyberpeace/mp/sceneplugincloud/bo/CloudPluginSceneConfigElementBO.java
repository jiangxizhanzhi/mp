package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementModel;
import lombok.Data;

/**
 * @ClassName CloudPluginSceneConfigElementBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPluginSceneElementBO
 */
@Data
public class CloudPluginSceneConfigElementBO extends BaseCloudPluginSceneElementBO<CloudPluginSceneConfigElementModel,
        CloudPluginSceneConfigElementBO, SceneConfigElementBO> {
    /**
     * createModel
     *
     * @return
     */
    @Override
    public CloudPluginSceneConfigElementModel createModel() {
        return new CloudPluginSceneConfigElementModel();
    }
}
