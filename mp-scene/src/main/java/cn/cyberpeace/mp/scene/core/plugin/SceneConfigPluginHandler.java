package cn.cyberpeace.mp.scene.core.plugin;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginHandler;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import lombok.Data;

/**
 * 场景模板插件基础类，场景模板的插件通过继承本类拓展插件功能
 * @param <S> 插件对应配置类
 * @param <E> 网元节点的插件对应配置类
 * @param <P> 网元端口的插件对应配置类
 * @param <C> 插件上下文类
 */
@Data
public abstract class SceneConfigPluginHandler<S, E, P, C>
        extends BasePluginHandler<S, E, P, C>
        implements PluginSceneHandlerCommon<SceneConfigElementBO, SceneConfigElementPortBO, SceneConfigHandler> {

    private SceneConfigHandler sceneHandler;

}
