 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowPluginSceneConfigBO.java.
 // File size 1594.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.BaseFlowPluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneConfigModel;
import lombok.Data;

/**
 * 流量插件场景配置业务对象。
 * 该类继承自BaseFlowPluginSceneBO，用于处理与流量插件场景配置相关的业务逻辑。
 * 它特定于流量插件场景的配置模型，提供了创建配置模型实例的方法。
 *
 * @param <T> 表示流量插件场景配置模型的类型。
 * @param <U> 表示本业务对象的类型，用于支持泛型递归。
 * @param <V> 表示场景配置的泛型类型，为通用配置提供支持。
 */
@Data
public class FlowPluginSceneConfigBO
        extends BaseFlowPluginSceneBO<FlowPluginSceneConfigModel, FlowPluginSceneConfigBO, SceneConfigBO> {

    /**
     * 创建一个新的流量插件场景配置模型对象。
     * 该方法用于在业务操作中实例化一个新的FlowPluginSceneConfigModel对象，为后续的业务处理提供数据载体。
     *
     * @return FlowPluginSceneConfigModel的新实例。
     */
    /**
     * 创建流量插件场景配置模型的实例。
     * 该方法用于生成一个新的流量插件场景配置模型对象，供业务逻辑使用。
     *
     * @return 返回一个新的FlowPluginSceneConfigModel实例。
     */
    @Override
    public FlowPluginSceneConfigModel createModel() {
        return new FlowPluginSceneConfigModel();
    }
}
