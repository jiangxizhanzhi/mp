package cn.cyberpeace.mp.scene.core.plugin.common;

import cn.cyberpeace.mp.scene.core.base.BaseSceneHandler;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortConnectionModel;

import java.util.List;


public interface PluginSceneHandlerCommon<S extends BaseSceneBO<?, S>, E extends BaseSceneElementBO<?, P, E>,
        P extends BaseSceneElementPortBO<?, P, E>,
        H extends BaseSceneHandler<?, ?, ?, ?, ? extends BaseSceneElementPortConnectionModel, ?, ?,
                S, ?, P, E, ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, ?, ?, H>> {

    H getSceneHandler();

    
    default <S9> S9 getPluginScene(String pluginKey) {
        return getSceneHandler().getPluginScene(pluginKey);
    }

    
    default <E9> E9 getPluginElement(String pluginKey, String nodeId) {
        return getSceneHandler().getPluginElement(pluginKey, nodeId);
    }

    
    default <P9> P9 getPluginPort(String pluginKey, String portId) {
        return getSceneHandler().getPluginPort(pluginKey, portId);
    }

    
    default E getSrcElement(String nodeId) {
        return getSceneHandler().getElement(nodeId);
    }

    
    default List<E> getElementConnectedSrcElements(String nodeId) {
        return getSceneHandler().getElementConnectedElements(nodeId);
    }

    
    default List<P> getElementConnectedSrcPorts(String nodeId) {
        return getSceneHandler().getElementConnectedPorts(nodeId);
    }

    
    default S getSrcScene() {
        return getSceneHandler().getScene();
    }

    
    default P getSrcPort(String portId) {
        return getSceneHandler().getPort(portId);
    }

    
    default E getPortSrcElement(String portId) {
        return getSceneHandler().getPortElement(portId);
    }

    
    default P getPortConnectedSrcPort(String portId) {
        return getSceneHandler().getPortConnectedPort(portId);
    }

    
    default E getPortConnectedSrcElement(String portId) {
        return getSceneHandler().getPortConnectedElement(portId);
    }

}
