package cn.cyberpeace.mp.scene.core.plugin;

import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginHandler;

/**
 * 场景模板插件基础类，场景模板的插件通过继承本类拓展插件功能
 * @param <S> 插件对应配置类
 * @param <E> 网元节点的插件对应配置类
 * @param <P> 网元端口的插件对应配置类
 */
public abstract class SceneConfigPluginHandler<S, E, P> extends BasePluginHandler<S, E, P> {

}
