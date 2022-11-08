package cn.cyberpeace.mp.sceneplugincollection.bo.base;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneBO;
import cn.cyberpeace.mp.sceneplugincollection.model.base.BaseCollectionPluginSceneModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public abstract class BaseCollectionPluginSceneBO<M extends BaseCollectionPluginSceneModel,
        T extends BaseCollectionPluginSceneBO<M, T, S>, S extends BaseSceneBO<?, S>>
        extends BasePluginSceneBO<M, T, S> {
    @ApiModelProperty(value = "是否开启流量采集")
    private Boolean enableFlowCollect;
    @ApiModelProperty(value = "是否开启探针采集")
    private Boolean enableProbeCollect;
}
