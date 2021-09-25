package cn.cyberpeace.mp.scene.core.plugin;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginHandler;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import lombok.Data;

import java.util.List;

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

    public void prepareSceneProcess(int event, List<EventProcess> eventProcesses) {
        getSceneHandler().prepareSceneProcess(event, eventProcesses);
    }

    public void prepareElementProcess(String nodeId, int event, List<EventProcess> eventProcesses) {
        getSceneHandler().prepareElementProcess(nodeId, event, eventProcesses);
    }

    public void progressSceneProcess(int event, String process) {
        getSceneHandler().progressSceneProcess(event, process);
    }

    public void progressElementProcess(String nodeId, int event, String process) {
        getSceneHandler().progressElementProcess(nodeId, event, process);
    }

    public void overSceneProcess(int event) {
        getSceneHandler().overSceneProcess(event);
    }

    public void overElementProcess(String nodeId, int event) {
        getSceneHandler().overElementProcess(nodeId, event);
    }

    public void errorScene(int event, String errorMessage) {
        getSceneHandler().errorScene(event, errorMessage);
    }

    public void errorElement(String nodeId, int event, String errorMessage) {
        getSceneHandler().errorElement(nodeId, event, errorMessage);
    }

    public void initiate() throws Exception {
    }

    public void create() throws Exception {

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

    public void delete() throws Exception {

    }

    /**
     * TODO: 修改场景
     */
    public void modify() {

    }

}
