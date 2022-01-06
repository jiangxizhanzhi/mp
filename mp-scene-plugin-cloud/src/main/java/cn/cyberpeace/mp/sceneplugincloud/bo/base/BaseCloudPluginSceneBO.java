package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public abstract class BaseCloudPluginSceneBO<M extends BaseCloudPluginSceneModel,
        T extends BaseCloudPluginSceneBO<M, T, S>, S extends BaseSceneBO<?, S>> extends BasePluginSceneBO<M, T, S> {
    @ApiModelProperty(value = "是否开启录屏")
    private Boolean enableRecording;
}
