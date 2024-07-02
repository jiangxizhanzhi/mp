package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementPortModel;
import lombok.Data;

/**
 * @ClassName CloudPluginSceneConfigElementPortBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPluginSceneElementPortBO
 */
@Data
public class CloudPluginSceneConfigElementPortBO
        extends BaseCloudPluginSceneElementPortBO<CloudPluginSceneConfigElementPortModel,
        CloudPluginSceneConfigElementPortBO, SceneConfigElementPortBO> {
    /**
     * createModel
     *
     * @return
     */
    @Override
    public CloudPluginSceneConfigElementPortModel createModel() {
        return new CloudPluginSceneConfigElementPortModel();
    }
}
