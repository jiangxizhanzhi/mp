package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.core.event.base.ProcessStatus;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * @Description 用于动态更新topo和插件结构
 * @Author maojun
 * @Date 2022/5/9
 */
public class TopoReformUtils {

    private CloudScenePluginHandler pluginHandler;

    public TopoReformUtils(CloudScenePluginHandler pluginHandler) {
        this.pluginHandler = pluginHandler;
    }

    /**
     * 为实现某些功能(比如核心路由器配置下发),增加辅助交换机节点
     * @return
     */
    public ElementWrapper addAuxiliarySwitchNode() {
        ElementWrapper wrapper = createAuxiliarySwitchNode();
        String nodeId = wrapper.getSceneElement().getId();
        var element = wrapper.getSceneElement();
        var pluginElement = wrapper.getPluginElement();
        this.pluginHandler.addElementAndSrcElement(nodeId, pluginElement, element);
        return wrapper;
    }

    /**
     * 为实现某些功能(比如核心路由器配置下发),增加辅助端口
     * @param element
     * @return
     */
    public PortWrapper addAuxiliaryPort(CloudPluginSceneElementBO element) {
        PortWrapper wrapper = createAuxiliaryPort(element.getSrcElement());
        String portId = wrapper.getScenePort().getId();
        var port = wrapper.getScenePort();
        var pluginPort = wrapper.getPluginPort();
        this.pluginHandler.addPortAndSrcPort(portId, pluginPort, port);
        element.getSrcElement().getPorts().add(port);
        return wrapper;
    }

    /**
     * 增加一个接入协议
     * @param port
     * @param protocol
     */
    public void addAuxiliaryAccessMode(CloudPluginSceneElementPortBO port, String protocol) {
        if (port.getSrcPort().getAccessModes() == null) {
            port.getSrcPort().setAccessModes(new ArrayList<>());
        }
        AccessMode mode = new AccessMode();
        mode.setProxy(true);
        mode.setProtocol(protocol);
        mode.setPort(AccessMode.Protocol.DEFAULT_PORT_MAP.get(protocol));
        port.getSrcPort().getAccessModes().add(mode);

        if (port.getAccessModes() == null) {
            port.setAccessModes(new ArrayList<>());
        }
        CloudAccessMode pluginMode = new CloudAccessMode();
        pluginMode.setProxy(true);
        pluginMode.setProtocol(mode.getProtocol());
        pluginMode.setPort(mode.getPort());
        port.getAccessModes().add(pluginMode);
    }

    /**
     * 增加连线关系
     * @param from
     * @param to
     */
    public void addConnect(SceneElementPortBO from, SceneElementPortBO to) {
        this.pluginHandler.getSceneHandler().getPortIdConnectedPortIdMap().put(from.getId(), to.getId());
        this.pluginHandler.getSceneHandler().getPortIdConnectedPortIdMap().put(to.getId(), from.getId());
    }

    private static ElementWrapper createAuxiliarySwitchNode() {
        SceneElementBO sceneElementBO = new SceneElementBO();
        sceneElementBO.setPorts(new ArrayList<>());
        sceneElementBO.setId(TextUtils.uuid());
        sceneElementBO.setEvent(SceneElementModel.Event.DEFAULT);
        sceneElementBO.setEventStatus(EventStatus.INITIAL);
        sceneElementBO.setProcess(StringUtils.EMPTY);
        sceneElementBO.setProcessStatus(ProcessStatus.INITIAL);
        sceneElementBO.setIsAuxiliary(true);
        CloudPluginSceneElementBO cloudPluginSceneElementBO = new CloudPluginSceneElementBO();
        cloudPluginSceneElementBO.setRole(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH);
        cloudPluginSceneElementBO.setDhcp(true);
        cloudPluginSceneElementBO.setSrcElement(sceneElementBO);
        cloudPluginSceneElementBO.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH);
        return new ElementWrapper(sceneElementBO, cloudPluginSceneElementBO);
    }

    private static PortWrapper createAuxiliaryPort(SceneElementBO element) {
        SceneElementPortBO port = new SceneElementPortBO();
        port.setElement(element);
        port.setId(TextUtils.uuid());
        // todo
        port.setOrder(0);
        port.setIsAuxiliary(true);

        CloudPluginSceneElementPortBO pluginPort = new CloudPluginSceneElementPortBO();
        pluginPort.setSrcPort(port);
        if (element.getPorts() == null) {
            element.setPorts(new ArrayList<>());
        }
        return new PortWrapper(port, pluginPort);
    }

    @Getter
    public static class PortWrapper {
        SceneElementPortBO scenePort;
        CloudPluginSceneElementPortBO pluginPort;

        public PortWrapper(SceneElementPortBO scenePort, CloudPluginSceneElementPortBO pluginPort) {
            this.scenePort = scenePort;
            this.pluginPort = pluginPort;
        }
    }

    @Getter
    public static class ElementWrapper {
        SceneElementBO sceneElement;
        CloudPluginSceneElementBO pluginElement;

        public ElementWrapper(SceneElementBO sceneElement, CloudPluginSceneElementBO pluginElement) {
            this.sceneElement = sceneElement;
            this.pluginElement = pluginElement;
        }
    }
}
