package cn.cyberpeace.mp.scene.core.plugin;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginHandler;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;

import java.util.List;

/**
 * 场景实例插件基础类，场景实例的插件通过继承本类拓展插件功能
 * @param <S> 插件对应配置类
 * @param <E> 网元节点的插件对应配置类
 * @param <P> 网元端口的插件对应配置类
 */
public abstract class ScenePluginHandler<S, E, P> extends BasePluginHandler<S, E, P, ScenePluginContext> {

    private SceneHandler getSceneHandler() {
        SceneHandler sceneHandler = null;
        var context = getContext();
        if (context != null) {
            sceneHandler = context.getSceneHandler();
        }

        if (sceneHandler == null) {
            throw new MpRuntimeException("Scene plugin has no valid scene handler");
        }

        return sceneHandler;
    }

    /**
     * 获取其它插件全局数据
     * @param pluginKey 插件代号
     */
    public <S9> S9 getPluginScene(String pluginKey) {
        return getSceneHandler().getPluginScene(pluginKey);
    }

    /**
     * 获取网元其它插件数据
     * @param pluginKey 插件代号
     * @param nodeId 网元节点id
     */
    public <E9> E9 getPluginEle(String pluginKey, String nodeId) {
        return getSceneHandler().getPluginEle(pluginKey, nodeId);
    }

    /**
     * 获取端口其它插件数据
     * @param pluginKey 插件代号
     * @param portId 端口id
     */
    public <P9> P9 getPluginPort(String pluginKey, String portId) {
        return getSceneHandler().getPluginPort(pluginKey, portId);
    }

    /**
     * 获取网元数据
     * @param nodeId 网元节点id
     */
    public SceneElementBO getEle(String nodeId) {
        return getSceneHandler().getEle(nodeId);
    }

    /**
     * 获取网元连接的网元
     * @param nodeId 网元节点id
     */
    public List<SceneElementBO> getEleConnectedEles(String nodeId) {
        return getSceneHandler().getEleConnectedEles(nodeId);
    }

    /**
     * 获取网元连接的网元
     * @param nodeId 网元节点id
     */
    public List<SceneElementPortBO> getEleConnectedPorts(String nodeId) {
        return getSceneHandler().getEleConnectedPorts(nodeId);
    }

    /**
     * 获取端口数据
     * @param portId 端口id
     */
    public SceneElementPortBO getPort(String portId) {
        return getSceneHandler().getPort(portId);
    }

    /**
     * 获取端口所属网元数据
     * @param portId 端口id
     */
    public SceneElementBO getPortEle(String portId) {
        return getSceneHandler().getPortEle(portId);
    }

    /**
     * 获取端口连接的端口
     * @param portId 端口id
     */
    public SceneElementPortBO getPortConnectedPort(String portId) {
        return getSceneHandler().getPortConnectedPort(portId);
    }

    /**
     * 获取端口连接的节点
     * @param portId 端口id
     */
    public SceneElementBO getPortConnectedEle(String portId) {
        return getSceneHandler().getPortConnectedEle(portId);
    }

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
