package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementPortModel;
import lombok.Data;

/**
 * @ClassName CloudPluginSceneElementPortBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPluginSceneElementPortBO
 */
@Data
public class CloudPluginSceneElementPortBO extends BaseCloudPluginSceneElementPortBO<CloudPluginSceneElementPortModel,
        CloudPluginSceneElementPortBO, SceneElementPortBO> {

    private String logicNetworkId;

    private String distributedPortMappingIp;

    /**
     * createModel
     *
     * @return
     */
    @Override
    public CloudPluginSceneElementPortModel createModel() {
        return new CloudPluginSceneElementPortModel();
    }

}
