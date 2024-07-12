 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseFlowPluginSceneElementPortBO.java.
 // File size 1570.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.bo.base;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.scenepluginflow.model.base.BaseFlowPluginSceneElementPortModel;
import lombok.Data;

/**
 * 基础流程插件场景元素端口业务对象的抽象类。
 * 该类作为流程插件场景元素端口的抽象表示，定义了插件场景元素端口的基本行为和属性。
 * 它使用泛型来实现类型安全的继承和操作，允许具体的场景元素端口业务对象在继承时指定自己的模型和场景元素端口类型。
 *
 * @param <M> 扩展自BaseFlowPluginSceneElementPortModel的泛型参数，表示插件场景元素端口的模型类型。
 * @param <T> 扩展自BaseFlowPluginSceneElementPortBO的泛型参数，表示插件场景元素端口的自身类型，用于支持泛型的递归定义。
 * @param <S> 扩展自BaseSceneElementPortBO的泛型参数，表示场景元素端口的泛型类型，用于定义插件场景元素端口和场景元素端口之间的关系。
 * @extends BasePluginSceneElementPortBO 插件场景元素端口的基础类，提供了插件场景元素端口的基本操作和属性。
 */
@Data
public abstract class BaseFlowPluginSceneElementPortBO<M extends BaseFlowPluginSceneElementPortModel,
        T extends BaseFlowPluginSceneElementPortBO<M, T, S>, S extends BaseSceneElementPortBO<?, S, ?>>
        extends BasePluginSceneElementPortBO<M, T, S> {
}
