package cn.cyberpeace.mp.scene.core.plugin;

import cn.cyberpeace.mp.scene.core.base.BaseSceneHandler;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortConnectionModel;

import java.util.List;

/**
 * 插件基于基础scene handler的通用能力
 * @param <E>
 * @param <P>
 * @param <H>
 */
public interface PluginSceneHandlerCommon<E extends BaseSceneElementBO<?, P, E>,
        P extends BaseSceneElementPortBO<?, P>,
        H extends BaseSceneHandler<?, ?, ?, ?,
                ? extends BaseSceneElementPortConnectionModel, ?, ?, ?, P, E, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, H>> {

    H getSceneHandler();

    /**
     * 获取其它插件全局数据
     * @param pluginKey 插件代号
     */
    default <S9> S9 getPluginScene(String pluginKey) {
        return getSceneHandler().getPluginScene(pluginKey);
    }

    /**
     * 获取网元其它插件数据
     * @param pluginKey 插件代号
     * @param nodeId 网元节点id
     */
    default <E9> E9 getPluginElement(String pluginKey, String nodeId) {
        return getSceneHandler().getPluginElement(pluginKey, nodeId);
    }

    /**
     * 获取端口其它插件数据
     * @param pluginKey 插件代号
     * @param portId 端口id
     */
    default <P9> P9 getPluginPort(String pluginKey, String portId) {
        return getSceneHandler().getPluginPort(pluginKey, portId);
    }

    /**
     * 获取网元数据
     * @param nodeId 网元节点id
     */
    default E getSrcElement(String nodeId) {
        return getSceneHandler().getElement(nodeId);
    }

    /**
     * 获取网元连接的网元
     * @param nodeId 网元节点id
     */
    default List<E> getElementConnectedSrcElements(String nodeId) {
        return getSceneHandler().getElementConnectedElements(nodeId);
    }

    /**
     * 获取网元连接的网元
     * @param nodeId 网元节点id
     */
    default List<P> getElementConnectedSrcPorts(String nodeId) {
        return getSceneHandler().getElementConnectedPorts(nodeId);
    }

    /**
     * 获取端口数据
     * @param portId 端口id
     */
    default P getSrcPort(String portId) {
        return getSceneHandler().getPort(portId);
    }

    /**
     * 获取端口所属网元数据
     * @param portId 端口id
     */
    default E getPortSrcElement(String portId) {
        return getSceneHandler().getPortElement(portId);
    }

    /**
     * 获取端口连接的端口
     * @param portId 端口id
     */
    default P getPortConnectedSrcPort(String portId) {
        return getSceneHandler().getPortConnectedPort(portId);
    }

    /**
     * 获取端口连接的节点
     * @param portId 端口id
     */
    default E getPortConnectedSrcElement(String portId) {
        return getSceneHandler().getPortConnectedElement(portId);
    }

}
