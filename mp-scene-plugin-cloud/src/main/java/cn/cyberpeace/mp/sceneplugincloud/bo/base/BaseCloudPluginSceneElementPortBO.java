package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementPortModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public abstract class BaseCloudPluginSceneElementPortBO<M extends BaseCloudPluginSceneElementPortModel,
        T extends BaseCloudPluginSceneElementPortBO<M, T>> extends BasePluginSceneElementPortBO<M, T> {
    @ApiModelProperty(value = "云平台虚拟化资源id")
    private String resourceId;
    @ApiModelProperty(value = "是否nat外网")
    private Boolean nat;
}
