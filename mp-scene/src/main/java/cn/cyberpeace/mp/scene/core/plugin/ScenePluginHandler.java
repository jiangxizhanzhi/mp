package cn.cyberpeace.mp.scene.core.plugin;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginHandler;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import lombok.Data;

/**
 * 场景实例插件基础类，场景实例的插件通过继承本类拓展插件功能
 * @param <S> 插件对应配置类
 * @param <E> 网元节点的插件对应配置类
 * @param <P> 网元端口的插件对应配置类
 * @param <C> 插件上下文类
 */
@Data
public abstract class ScenePluginHandler<S, E, P, C> extends BasePluginHandler<S, E, P, C>
        implements PluginSceneHandlerCommon<SceneElementBO, SceneElementPortBO, SceneHandler> {

    private SceneHandler sceneHandler;

    public void progressSceneProcess(int event, String process, String processMessage) {
        getSceneHandler().progressSceneProcess(event, process, processMessage);
    }

    public void progressElementProcess(String nodeId, int event, String process, String processMessage) {
        getSceneHandler().progressElementProcess(nodeId, event, process, processMessage);
    }

    public void overSceneProcess(int event, String processMessage) {
        getSceneHandler().overSceneProcess(event, processMessage);
    }

    public void overElementProcess(String nodeId, int event, String processMessage) {
        getSceneHandler().overElementProcess(nodeId, event, processMessage);
    }

    public void errorSceneProcess(int event, String processMessage) {
        getSceneHandler().errorSceneProcess(event, processMessage);
    }

    public void errorElementProcess(String nodeId, int event, String processMessage) {
        getSceneHandler().errorElementProcess(nodeId, event, processMessage);
    }

    public void initiate() {
    }

    public void create() {

    }

    public void start() {

    }

    public void stop() {

    }

    public void restart() {

    }

    public void pause() {

    }

    public void unpause() {

    }

    public void snapshot() {

    }

    public void delete() {

    }

    /**
     * TODO: 修改场景
     */
    public void modify() {

    }

}
