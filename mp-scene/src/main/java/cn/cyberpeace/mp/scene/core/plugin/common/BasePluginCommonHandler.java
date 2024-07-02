package cn.cyberpeace.mp.scene.core.plugin.common;

import cn.cyberpeace.mp.scene.core.base.BaseSceneHandler;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaNodeConnectionModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortConnectionModel;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName BasePluginCommonHandler
 * @Description 基础的场景插件处理类, 基于通用bo处理
 * 场景插件处理目前只对网元节点和网元端口做插件配置处理
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePluginHandler
 */
public abstract class BasePluginCommonHandler<
        S9 extends BaseSceneBO<?, S9>,
        E9 extends BaseSceneElementBO<?, P9, E9>,
        P9 extends BaseSceneElementPortBO<?, P9, E9>,
        S extends BasePluginSceneBO<?, S, S9>,
        E extends BasePluginSceneElementBO<?, E, E9>,
        P extends BasePluginSceneElementPortBO<?, P, P9>,
        C,
        H extends BaseSceneHandler<?, ?, ?, ?, ? extends BaseSceneElementPortConnectionModel,? extends BaseSceneAreaNodeConnectionModel, ?, ?,
                S9, ?, P9, E9, ?, ?, ?, ?, ?, ?,
                ?, H>>
        extends BasePluginHandler<S, E, P, C> {

    public void syncSrcData() {
        S scene = getScene();
        if (scene != null) {
            scene.setSrcScene(getSrcScene());
        }

        getElementMap().forEach((nodeId, element) -> {
            E9 srcElement = getSrcElement(nodeId);
            element.setSrcElement(srcElement);
        });
        getElementPortMap().forEach((portId, port) -> {
            P9 srcPort = getSrcPort(portId);
            port.setSrcPort(srcPort);
        });
    }

    public List<P> getPorts(E pluginElement) {
        return getPorts(pluginElement.getSrcElement());
    }

    public List<P> getPorts(E9 srcElement) {
        List<P9> srcPorts = srcElement.getPorts();
        List<P> pluginPorts = new ArrayList<>();
        if (srcPorts == null) {
            return pluginPorts;
        }
        srcPorts.forEach(srcPort -> {
            P pluginPort = getPort(srcPort.getId());
            if (pluginPort != null) {
                pluginPorts.add(pluginPort);
            }
        });
        return pluginPorts;
    }

    public void addElement(String nodeId, E pluginElement) {
        getElementIds().add(nodeId);
        getElementMap().put(nodeId, pluginElement);

        E9 srcElement = getSrcElement(nodeId);
        pluginElement.setSrcElement(srcElement);
    }

    public void addPort(String portId, P pluginPort) {
        getElementPortIds().add(portId);
        getElementPortMap().put(portId, pluginPort);

        P9 srcPort = getSrcPort(portId);
        pluginPort.setSrcPort(srcPort);
    }

    @Override
    public void fromData(ScenePluginDataBO pluginData) {
        super.fromData(pluginData);
        syncSrcData();
    }

    @Override
    public void reload() {
        reloadData();
        syncSrcData();
    }

    public abstract void reloadData();

    public abstract H getSceneHandler();


    /**
     * 获取其它插件全局数据
     *
     * @param pluginKey 插件代号
     */
    public <S9> S9 getPluginScene(String pluginKey) {
        return getSceneHandler().getPluginScene(pluginKey);
    }

    /**
     * 获取网元其它插件数据
     *
     * @param pluginKey 插件代号
     * @param nodeId    网元节点id
     */
    public <E9> E9 getPluginElement(String pluginKey, String nodeId) {
        return getSceneHandler().getPluginElement(pluginKey, nodeId);
    }

    /**
     * 获取端口其它插件数据
     *
     * @param pluginKey 插件代号
     * @param portId    端口id
     */
    public <P9> P9 getPluginPort(String pluginKey, String portId) {
        return getSceneHandler().getPluginPort(pluginKey, portId);
    }

    /**
     * 获取网元数据
     *
     * @param nodeId 网元节点id
     */
    public E9 getSrcElement(String nodeId) {
        return getSceneHandler().getElement(nodeId);
    }

    /**
     * 获取网元连接的网元
     *
     * @param nodeId 网元节点id
     */
    public List<E9> getElementConnectedSrcElements(String nodeId) {
        return getSceneHandler().getElementConnectedElements(nodeId);
    }

    /**
     * 获取网元连接的网元
     *
     * @param nodeId 网元节点id
     */
    public List<P9> getElementConnectedSrcPorts(String nodeId) {
        return getSceneHandler().getElementConnectedPorts(nodeId);
    }

    /**
     * 获取场景基础数据
     */
    public  S9 getSrcScene() {
        return getSceneHandler().getScene();
    }

    /**
     * 获取端口数据
     *
     * @param portId 端口id
     */
    public P9 getSrcPort(String portId) {
        return getSceneHandler().getPort(portId);
    }

    /**
     * 获取端口所属网元数据
     *
     * @param portId 端口id
     */
    public E9 getPortSrcElement(String portId) {
        return getSceneHandler().getPortElement(portId);
    }

    /**
     * 获取端口连接的端口
     *
     * @param portId 端口id
     */
    public P9 getPortConnectedSrcPort(String portId) {
        return getSceneHandler().getPortConnectedPort(portId);
    }

    /**
     * 获取端口连接的节点
     *
     * @param portId 端口id
     */
    public E9 getPortConnectedSrcElement(String portId) {
        return getSceneHandler().getPortConnectedElement(portId);
    }

    public Object getPortConnection(String portId) {
        var portConnections = new ArrayList<>();
        getSceneHandler().getElementPortConnectionMap().forEach((k, v) -> {
            if (k.contains(portId)) {
                portConnections.add(v);
            }
        });
        return CollectionUtils.isEmpty(portConnections) ? null : portConnections.get(0);
    }

}
