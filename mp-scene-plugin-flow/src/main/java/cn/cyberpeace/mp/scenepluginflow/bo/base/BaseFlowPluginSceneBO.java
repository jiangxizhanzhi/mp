package cn.cyberpeace.mp.scenepluginflow.bo.base;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.model.base.BaseFlowPluginSceneModel;
import lombok.Data;

/**
 * 基础流程插件场景业务对象类
 * 该类是Lombok注解支持的抽象类，用于定义流程插件场景的基本业务行为。
 * 它继承自BasePluginSceneBO，增强了对流程插件场景模型的处理能力。
 * <p>
 * 参数说明：
 * M - 表示流程插件场景模型，继承自BaseFlowPluginSceneModel。
 * T - 表示当前业务对象自身，允许泛型的自我引用，用于实现类似Java中的泛型继承。
 * S - 表示场景业务对象，继承自BaseSceneBO，用于场景的业务处理。
 */
@Data
public abstract class BaseFlowPluginSceneBO<M extends BaseFlowPluginSceneModel,
        T extends BaseFlowPluginSceneBO<M, T, S>, S extends BaseSceneBO<?, S>>
        extends BasePluginSceneBO<M, T, S> {
}
