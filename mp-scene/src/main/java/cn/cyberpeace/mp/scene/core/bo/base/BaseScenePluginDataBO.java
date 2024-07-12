 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseScenePluginDataBO.java.
 // File size 1036.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.Map;

/**
 * @ClassName BaseScenePluginDataBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class BaseScenePluginDataBO<S, E, P> {

    /**
     * key: pluginConfig
     */
    @ApiModelProperty(value = "本插件全局配置")
    @Valid
    private S scene;

    /**
     * nodeId: pluginNodeData
     */
    @ApiModelProperty(value = "本插件网元配置")
    @Valid
    private Map<String, E> elements;

    /**
     * portId: pluginPortData
     */
    @ApiModelProperty(value = "本插件端口配置")
    @Valid
    private Map<String, P> elementPorts;

    /**
     * TODO: 这里插件的数据类型无法确定，暂未处理，目前插件实现的业务模型不绑定数据id
     */
    public BaseScenePluginDataBO<S, E, P> export() {
        return this;
    }
}
