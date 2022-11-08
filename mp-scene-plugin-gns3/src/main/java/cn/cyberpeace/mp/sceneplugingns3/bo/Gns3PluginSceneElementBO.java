package cn.cyberpeace.mp.sceneplugingns3.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugingns3.bo.base.BaseGns3PluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneElementModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Gns3PluginSceneElementBO extends BaseGns3PluginSceneElementBO<Gns3PluginSceneElementModel,
        Gns3PluginSceneElementBO, SceneElementBO> {

    @ApiModelProperty(value = "gns3平台中的资源id")
    private String resourceId;

    @ApiModelProperty(value = "身份id")
    private Integer dynamicId;

    @Override
    public Gns3PluginSceneElementModel createModel() {
        return new Gns3PluginSceneElementModel();
    }
}
