package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.core.event.base.ProcessStatus;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
     *
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
     * 为实现某些功能(比如核心路由器配置下发),增加外部网络节点
     *
     * @return
     */
    public ElementWrapper addExternalNetworkNode() {
        ElementWrapper wrapper = createExternalNetworkNode();
        String nodeId = wrapper.getSceneElement().getId();
        var element = wrapper.getSceneElement();
        var pluginElement = wrapper.getPluginElement();
        this.pluginHandler.addElementAndSrcElement(nodeId, pluginElement, element);
        return wrapper;
    }

    public ElementWrapper addKaliNode() {
        ElementWrapper wrapper = createKaliNode();
        String nodeId = wrapper.getSceneElement().getId();
        var element = wrapper.getSceneElement();
        var pluginElement = wrapper.getPluginElement();
        this.pluginHandler.addElementAndSrcElement(nodeId, pluginElement, element);
        return wrapper;
    }

    /**
     * 为实现某些功能(比如核心路由器配置下发),增加辅助端口
     *
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

    public PortWrapper addExternalNetworkPort(CloudPluginSceneElementBO element) {
        PortWrapper wrapper = createExternalNetworkPort(element.getSrcElement());
        String portId = wrapper.getScenePort().getId();
        var port = wrapper.getScenePort();
        var pluginPort = wrapper.getPluginPort();
        this.pluginHandler.addPortAndSrcPort(portId, pluginPort, port);
        element.getSrcElement().getPorts().add(port);
        return wrapper;
    }

    /**
     * 增加一个接入协议
     *
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
     *
     * @param from
     * @param to
     */
    public void addConnect(SceneElementPortBO from, SceneElementPortBO to) {
        this.pluginHandler.getSceneHandler().getPortIdConnectedPortIdMap().put(from.getId(), to.getId());
        this.pluginHandler.getSceneHandler().getPortIdConnectedPortIdMap().put(to.getId(), from.getId());
        SceneElementPortConnectionBO sceneElementPortConnectionBO = new SceneElementPortConnectionBO();
        sceneElementPortConnectionBO.setSource(from.getId());
        sceneElementPortConnectionBO.setTarget(to.getId());
        sceneElementPortConnectionBO.setName(TextUtils.uuid());
        sceneElementPortConnectionBO.setId(TextUtils.uuid());
        this.pluginHandler.getSceneHandler().getElementPortConnectionMap().put(from.getId() + ":" + to.getId(), sceneElementPortConnectionBO);
    }

    private static ElementWrapper createAuxiliarySwitchNode() {
        SceneElementBO sceneElementBO = new SceneElementBO();
        sceneElementBO.setPorts(new ArrayList<>());
        sceneElementBO.setId(UUID.randomUUID().toString().toLowerCase());
        sceneElementBO.setEvent(SceneElementModel.Event.DEFAULT);
        sceneElementBO.setEventStatus(EventStatus.INITIAL);
        sceneElementBO.setProcess(StringUtils.EMPTY);
        sceneElementBO.setProcessStatus(ProcessStatus.INITIAL);
        sceneElementBO.setIsAuxiliary(true);
        CloudPluginSceneElementBO cloudPluginSceneElementBO = new CloudPluginSceneElementBO();
        cloudPluginSceneElementBO.setRole(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER_VNET);
        cloudPluginSceneElementBO.setDhcp(true);
        cloudPluginSceneElementBO.setSrcElement(sceneElementBO);
        cloudPluginSceneElementBO.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH);

        sceneElementBO.setType(BaseSceneElementModel.Type.SWITCH);
        // azure
        sceneElementBO.setArea("cloudUUID");
        sceneElementBO.setName("default VNet");
        sceneElementBO.setIcon2("/api/mp/file/177/108/39ae20d4c7de4ab7a9aa9de270c1d143.png");


        cloudPluginSceneElementBO.setImageType(CloudPluginSceneElementModel.ImageType.VNET);
        cloudPluginSceneElementBO.setElectricPortNumber(1);
        cloudPluginSceneElementBO.setLanPortNumber(1);
        cloudPluginSceneElementBO.setWanPortNumber(1);
        cloudPluginSceneElementBO.setOpticalPortNumber(1);

        cloudPluginSceneElementBO.setServiceType("basicNetwork");
        cloudPluginSceneElementBO.setCloudServiceType("1");

        return new ElementWrapper(sceneElementBO, cloudPluginSceneElementBO);
    }

    private static ElementWrapper createExternalNetworkNode() {
        SceneElementBO sceneElementBO = new SceneElementBO();
        sceneElementBO.setPorts(new ArrayList<>());
        sceneElementBO.setId(UUID.randomUUID().toString().toLowerCase());
        sceneElementBO.setEvent(SceneElementModel.Event.DEFAULT);
        sceneElementBO.setEventStatus(EventStatus.INITIAL);
        sceneElementBO.setProcess(StringUtils.EMPTY);
        sceneElementBO.setProcessStatus(ProcessStatus.INITIAL);
        sceneElementBO.setIsAuxiliary(false);
        sceneElementBO.setType(BaseSceneElementModel.Type.SWITCH);
        sceneElementBO.setArea("");
        sceneElementBO.setName("外部网络");
        sceneElementBO.setIcon2("823/081/0f5db39031e0283b1048681ddd4213c1.png");
        sceneElementBO.setSystemType("other");
        sceneElementBO.setSystemArchitecture("other");
        sceneElementBO.setSystemLanguage("other");
        sceneElementBO.setPosition2("{\"x\":360,\"y\":140}");


        CloudPluginSceneElementBO cloudPluginSceneElementBO = new CloudPluginSceneElementBO();
        cloudPluginSceneElementBO.setRole(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK);
        cloudPluginSceneElementBO.setDhcp(true);
        cloudPluginSceneElementBO.setSrcElement(sceneElementBO);
//        cloudPluginSceneElementBO.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH);
        cloudPluginSceneElementBO.setImageType(CloudPluginSceneElementModel.ImageType.SIMPLE);
        cloudPluginSceneElementBO.setElectricPortNumber(4);
        cloudPluginSceneElementBO.setLanPortNumber(0);
        cloudPluginSceneElementBO.setWanPortNumber(0);
        cloudPluginSceneElementBO.setOpticalPortNumber(0);

//        cloudPluginSceneElementBO.setServiceType("1");
//        cloudPluginSceneElementBO.setCloudServiceType("1");
        cloudPluginSceneElementBO.setInitSupport(false);
        cloudPluginSceneElementBO.setIsPublicIp(false);
//        cloudPluginSceneElementBO.setExternal(true);
        cloudPluginSceneElementBO.setIsBuildIn(true);
//        cloudPluginSceneElementBO.setNetMode(0);
        cloudPluginSceneElementBO.setLoadToolId("4");
        cloudPluginSceneElementBO.setElectricPortType("E");
        cloudPluginSceneElementBO.setOpticalPortType("sfp");
        cloudPluginSceneElementBO.setWanPortType("E");
        cloudPluginSceneElementBO.setLanPortType("E");
//        cloudPluginSceneElementBO.setGatewayOff(false);
        cloudPluginSceneElementBO.setDhcp(true);
//        cloudPluginSceneElementBO.setVlan(false);
        cloudPluginSceneElementBO.setImage("Internet");
        cloudPluginSceneElementBO.setImageUuid("5dfe83ac-4689-478d-8257-89bb118ef105");
        cloudPluginSceneElementBO.setCategory(3);
        cloudPluginSceneElementBO.setCategory2(53);
        cloudPluginSceneElementBO.setCategory3(0);

        return new ElementWrapper(sceneElementBO, cloudPluginSceneElementBO);
    }

    private static ElementWrapper createKaliNode() {
        SceneElementBO sceneElementBO = new SceneElementBO();
        sceneElementBO.setPorts(new ArrayList<>());
        sceneElementBO.setId(UUID.randomUUID().toString().toLowerCase());
        sceneElementBO.setEvent(SceneElementModel.Event.DEFAULT);
        sceneElementBO.setEventStatus(EventStatus.INITIAL);
        sceneElementBO.setProcess(StringUtils.EMPTY);
        sceneElementBO.setProcessStatus(ProcessStatus.INITIAL);
        sceneElementBO.setIsAuxiliary(false);
        sceneElementBO.setType(BaseSceneElementModel.Type.TERMINAL);
        sceneElementBO.setArea("");
        sceneElementBO.setName("kali");
        sceneElementBO.setIcon2("727/701/a4aade52f418bc39965d76b6b6dca305.png");
        sceneElementBO.setSystemType("304");
        sceneElementBO.setSystemArchitecture("64");
        sceneElementBO.setSystemLanguage("cn");
        sceneElementBO.setPosition2("{\"x\":460,\"y\":180}");
        sceneElementBO.setCpu(2);
        sceneElementBO.setRam(2048);
        sceneElementBO.setDisk(40);
        List<String> commands = Arrays.asList("s:");
        sceneElementBO.setInitCommands(commands);
        sceneElementBO.setMaxPortNumber(1);


        CloudPluginSceneElementBO cloudPluginSceneElementBO = new CloudPluginSceneElementBO();
        cloudPluginSceneElementBO.setRole(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL);
        cloudPluginSceneElementBO.setDhcp(true);
        cloudPluginSceneElementBO.setSrcElement(sceneElementBO);
        cloudPluginSceneElementBO.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE);
        cloudPluginSceneElementBO.setImageType(CloudPluginSceneElementModel.ImageType.VM);
        cloudPluginSceneElementBO.setElectricPortNumber(0);
        cloudPluginSceneElementBO.setLanPortNumber(0);
        cloudPluginSceneElementBO.setWanPortNumber(0);
        cloudPluginSceneElementBO.setOpticalPortNumber(0);

        cloudPluginSceneElementBO.setServiceType("1");
        cloudPluginSceneElementBO.setCloudServiceType("1");

        cloudPluginSceneElementBO.setExternal(false);
        cloudPluginSceneElementBO.setIsBuildIn(false);
        cloudPluginSceneElementBO.setNetMode(0);
        cloudPluginSceneElementBO.setLoadToolId("34");
        cloudPluginSceneElementBO.setElectricPortType("E");
        cloudPluginSceneElementBO.setOpticalPortType("sfp");
        cloudPluginSceneElementBO.setWanPortType("E");
        cloudPluginSceneElementBO.setLanPortType("E");
        cloudPluginSceneElementBO.setGatewayOff(false);
        cloudPluginSceneElementBO.setDhcp(true);
        cloudPluginSceneElementBO.setVlan(false);
        cloudPluginSceneElementBO.setImage("ubuntu_22.04");
        cloudPluginSceneElementBO.setImageUuid("843be965-2237-4e0a-85fd-cd343f929eb1");
        cloudPluginSceneElementBO.setCategory(1);
        cloudPluginSceneElementBO.setCategory2(2);
        cloudPluginSceneElementBO.setCategory3(304);
        cloudPluginSceneElementBO.setDefaultLaunchImageName("ubuntu_22.04");
        cloudPluginSceneElementBO.setInitSupport(true);


        return new ElementWrapper(sceneElementBO, cloudPluginSceneElementBO);
    }

    private static PortWrapper createAuxiliaryPort(SceneElementBO element) {
        SceneElementPortBO port = new SceneElementPortBO();
        port.setElement(element);

        int existPortNum = element.getPorts().size();
        Integer type = element.getType();
        String nodeId = element.getId();
        String portId = "";
        if (type != null && type == BaseSceneElementModel.Type.TERMINAL) {
            portId = "port_" + nodeId + "_" + (existPortNum + 1);
        } else {
            portId = "port_" + nodeId + "_electrical_" + (existPortNum + 1);
        }
        port.setId(portId);

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

    private static PortWrapper createExternalNetworkPort(SceneElementBO element) {
        SceneElementPortBO port = new SceneElementPortBO();
        port.setElement(element);

        int existPortNum = element.getPorts().size();
        Integer type = element.getType();
        String nodeId = element.getId();
        String portId = "";
        if (type != null && type == BaseSceneElementModel.Type.TERMINAL) {
            portId = "port_" + nodeId + "_" + (existPortNum + 1);
        } else {
            portId = "port_" + nodeId + "_electrical_" + (existPortNum + 1);
        }
        port.setId(portId);
        port.setName("外部网络");
        port.setUsable(false);

        CloudPluginSceneElementPortBO pluginPort = new CloudPluginSceneElementPortBO();
        pluginPort.setSrcPort(port);
        pluginPort.setConnToExtNet(true);
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
