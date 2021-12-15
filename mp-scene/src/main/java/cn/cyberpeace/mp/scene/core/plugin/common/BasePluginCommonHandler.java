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
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortConnectionModel;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础的场景插件处理类, 基于通用bo处理
 * 场景插件处理目前只对网元节点和网元端口做插件配置处理
 *
 * @param <S> 插件对应配置类
 * @param <E> 网元节点的插件对应配置类
 * @param <P> 网元端口的插件对应配置类
 */
public abstract class BasePluginCommonHandler<
        S9 extends BaseSceneBO<?, S9>,
        E9 extends BaseSceneElementBO<?, P9, E9>,
        P9 extends BaseSceneElementPortBO<?, P9, E9>,
        S extends BasePluginSceneBO<?, S, S9>,
        E extends BasePluginSceneElementBO<?, E, E9>,
        P extends BasePluginSceneElementPortBO<?, P, P9>,
        C,
        H extends BaseSceneHandler<?, ?, ?, ?, ? extends BaseSceneElementPortConnectionModel, ?, ?,
                S9, ?, P9, E9, ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, ?, ?, H>>
        extends BasePluginHandler<S, E, P, C>
        implements PluginSceneHandlerCommon<S9, E9, P9, H> {

    public void syncSrcData() {
        var scene = getScene();
        if (scene != null) {
            scene.setSrcScene(getSrcScene());
        }

        getElementMap().forEach((nodeId, element) -> {
            var srcElement = getSrcElement(nodeId);
            element.setSrcElement(srcElement);
        });
        getElementPortMap().forEach((portId, port) -> {
            var srcPort = getSrcPort(portId);
            port.setSrcPort(srcPort);
        });
    }

    public List<P> getPorts(E pluginElement) {
        return getPorts(pluginElement.getSrcElement());
    }

    public List<P> getPorts(E9 srcElement) {
        var srcPorts = srcElement.getPorts();
        List<P> pluginPorts = new ArrayList<>();
        if (srcPorts == null) {
            return pluginPorts;
        }
        srcPorts.forEach(srcPort -> {
            var pluginPort = getPort(srcPort.getId());
            if (pluginPort != null) {
                pluginPorts.add(pluginPort);
            }
        });
        return pluginPorts;
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
}
