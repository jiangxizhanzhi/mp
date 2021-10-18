package cn.cyberpeace.mp.sceneplugincollection.bo.base;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.model.base.BaseCollectionPluginSceneElementPortModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public abstract class BaseCollectionPluginSceneElementPortBO<M extends BaseCollectionPluginSceneElementPortModel,
        T extends BaseCollectionPluginSceneElementPortBO<M, T, S>, S extends BaseSceneElementPortBO<?, S>>
        extends BasePluginSceneElementPortBO<M, T, S> {
    @ApiModelProperty(value = "是否开启流量采集")
    private Boolean enableFlowCollect;
}
