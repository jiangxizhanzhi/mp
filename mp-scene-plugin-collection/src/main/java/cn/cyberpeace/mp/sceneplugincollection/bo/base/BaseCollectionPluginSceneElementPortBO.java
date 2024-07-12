 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseCollectionPluginSceneElementPortBO.java.
 // File size 1044.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.bo.base;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.model.base.BaseCollectionPluginSceneElementPortModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName BaseCollectionPluginSceneElementPortBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePluginSceneElementPortBO
 */
@Data
public abstract class BaseCollectionPluginSceneElementPortBO<M extends BaseCollectionPluginSceneElementPortModel,
        T extends BaseCollectionPluginSceneElementPortBO<M, T, S>, S extends BaseSceneElementPortBO<?, S, ?>>
        extends BasePluginSceneElementPortBO<M, T, S> {
    @ApiModelProperty(value = "是否开启流量采集")
    private Boolean enableFlowCollect;
    @ApiModelProperty(value = "是否开启流量镜像")
    private Boolean enableFlowMirror;
}
