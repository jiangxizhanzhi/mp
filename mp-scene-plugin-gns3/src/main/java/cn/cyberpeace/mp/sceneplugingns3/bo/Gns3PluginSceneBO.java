package cn.cyberpeace.mp.sceneplugingns3.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.sceneplugingns3.bo.base.BaseGns3PluginSceneBO;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Gns3PluginSceneBO extends BaseGns3PluginSceneBO<Gns3PluginSceneModel, Gns3PluginSceneBO, SceneBO> {

    @ApiModelProperty(value = "gns3平台的拓扑资源id")
    private String resourceId;

    @ApiModelProperty(value = "gns3平台和其他网络连接的网桥资源id")
    private String bridgeResourceId;

    @Override
    public Gns3PluginSceneModel createModel() {
        return new Gns3PluginSceneModel();
    }
}