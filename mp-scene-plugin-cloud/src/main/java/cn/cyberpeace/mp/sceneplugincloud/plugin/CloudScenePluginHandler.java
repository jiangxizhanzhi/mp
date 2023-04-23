package cn.cyberpeace.mp.sceneplugincloud.plugin;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.BoolUtils;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.constant.SceneErrorCode;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementNetworkModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ConnectedNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.DeleteHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ElementCreateHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ElementInitiateHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ElementModifyHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.InitiateHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ModifyHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.PauseHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.PrepareHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.RecreateHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.RestartHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ResumeHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.SnapshotHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.SuspendHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.UnpauseHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.modelhandler.CloudScenePluginModelHandler;
import cn.cyberpeace.mp.sceneplugincloud.util.ElementTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.TopoReformUtils;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@ClassFieldClasses({
        CloudPluginSceneBO.class,
        CloudPluginSceneElementBO.class,
        CloudPluginSceneElementPortBO.class,
        CloudScenePluginContext.class,
})
@Component
@PluginKey(value = PluginConstant.PLUGIN_KEY, priority = PluginConstant.PLUGIN_PRIORITY)
@Data
public class CloudScenePluginHandler extends ScenePluginHandler<CloudPluginSceneBO,
        CloudPluginSceneElementBO, CloudPluginSceneElementPortBO, CloudScenePluginContext> {

    private CloudScenePluginModelHandler pluginModelHandler;

    private final Map<String, CloudPluginSceneElementBO> gatewayElementMap = new ConcurrentHashMap<>();
    private final Map<String, CloudPluginSceneElementBO> firewallElementMap = new ConcurrentHashMap<>();
    private final Map<String, CloudPluginSceneElementBO> terminalElementMap = new ConcurrentHashMap<>();

    private HandlerUtils handlerUtils;
    private InitiateHandler initiateHandler;
    private CreateHandler createHandler;
    private DeleteHandler deleteHandler;
    private ModifyHandler modifyHandler;
    private PrepareHandler prepareHandler;

    private volatile boolean networksCreated = false;

    private void loadRelated() {
        gatewayElementMap.clear();
        firewallElementMap.clear();
        terminalElementMap.clear();
        getElementMap().forEach((nodeId, element) -> {
            Integer elementType = element.getSrcElement().getType();
            if (elementType == null) {
                terminalElementMap.put(nodeId, element);
            } else if (elementType.equals(BaseSceneElementModel.Type.SWITCH)
                    || elementType.equals(BaseSceneElementModel.Type.ROUTER)) {
                gatewayElementMap.put(nodeId, element);
            } else if (elementType.equals(BaseSceneElementModel.Type.FIREWALL)) {
                firewallElementMap.put(nodeId, element);
            } else {
                terminalElementMap.put(nodeId, element);
            }

            Integer virtualType = element.getVirtualType();
            if (virtualType == null) {
                return;
            }
            if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL)) {
                element.setTransparent(true);
            }
        });
    }

    @Override
    public void fromData(ScenePluginDataBO pluginData) {
        super.fromData(pluginData);
        loadRelated();
    }

    @Override
    public void initBase() {
        super.initBase();
        handlerUtils = new HandlerUtils(this);
        pluginModelHandler = new CloudScenePluginModelHandler(this);
        initiateHandler = new InitiateHandler(this);
        createHandler = new CreateHandler(this);
        deleteHandler = new DeleteHandler(this);
        prepareHandler = new PrepareHandler(this);
    }

    @Override
    public void reload() {
        super.reload();
        loadRelated();
        // 处理网络
        List<CloudPluginSceneElementNetworkModel> networks = pluginModelHandler.readNetworks();
        Map<String, ConnectedNetworkBO> networkBOMap = new HashMap<>();
        networks.forEach(network -> {
            CloudPluginSceneElementNetworkBO networkBO = new CloudPluginSceneElementNetworkBO();
            networkBO.fromModel(network);
            ConnectedNetworkBO connectedNetworkBO = new ConnectedNetworkBO(networkBO, handlerUtils);
            if (this.getElement(networkBO.getSceneElement()) == null) {
                return;
            }
            networkBOMap.put(networkBO.getId(), connectedNetworkBO);
            this.getElement(networkBO.getSceneElement()).getDeriveNetworks().add(connectedNetworkBO);
        });
        // 处理加入的网络
        getElementMap().values().forEach(e -> {
            if (e.getSrcElement().getPorts() != null && e.getSrcElement().getPorts().size() > 0) {
                e.getSrcElement().getPorts().forEach(p -> {
                    var pluginPort = this.getPort(p.getId());
                    var to = getHandlerUtils().getPortConnectedOpaqueSrcPort(p.getId());
                    if (pluginPort != null) {
                        if (StringUtils.isNotEmpty(pluginPort.getLogicNetworkId())) {
                            // 自己构建网络直接加入
                            if (networkBOMap.containsKey(pluginPort.getLogicNetworkId())) {
                                networkBOMap.get(pluginPort.getLogicNetworkId()).addPort(p, to, true);
                            }
                        } else if (to != null) {
                            // 未构建网络看对端端口
                            var pluginToPort = this.getPort(to.getId());
                            if (StringUtils.isNotEmpty(pluginToPort.getLogicNetworkId())) {
                                if (networkBOMap.containsKey(pluginToPort.getLogicNetworkId())) {
                                    ConnectedNetworkBO connectedNetworkBO = new ConnectedNetworkBO(networkBOMap.get(pluginToPort.getLogicNetworkId()).getNetwork(), getHandlerUtils());
                                    connectedNetworkBO.addPort(p, to, false);
                                    e.getAccessNetworks().add(connectedNetworkBO);
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void reloadData() {
        setScene(null);
        getElementMap().clear();
        getElementPortMap().clear();

        pluginModelHandler.loadScene(pluginModelHandler.readScene());
        pluginModelHandler.loadElements(pluginModelHandler.readElements(getElementIds().toArray(new String[0])));
        pluginModelHandler.loadElementPorts(pluginModelHandler.readElementPorts(getElementPortIds()
                .toArray(new String[0])));
    }


    @Override
    public void saveScene() {
        pluginModelHandler.saveScene();
    }

    @Override
    public void deleteScene() {
        pluginModelHandler.deleteScene();
    }

    @Override
    public void createElements(String... elementIds) {
        pluginModelHandler.createElements(elementIds);
    }

    @Override
    public void updateElements(String... elementIds) {
        pluginModelHandler.updateElements(elementIds);
        if (networksCreated) {
            for (String elementId : elementIds) {
                updateElementNetworks(elementId);
            }
        }
    }

    public void createElementNetworks() {
        List<ConnectedNetworkBO> networks = new ArrayList<>();
        getElementMap().values().forEach(e -> {
            for (var net : e.getDeriveNetworks()) {
                if (!getHandlerUtils().getCreatedNetworks().contains(net.getNetwork().getId())) {
                    networks.add(net);
                }
            }
        });
        pluginModelHandler.createElementNetworks(networks);
        this.networksCreated = true;
    }

    public void updateElementNetworks(String elementId) {
        pluginModelHandler.updateElementNetworks(elementId);
    }

    @Override
    public void deleteElements(String... elementIds) {
        pluginModelHandler.deleteElements(elementIds);
    }

    @Override
    public void createElementPorts(String... elementPortIds) {
        pluginModelHandler.createElementPorts(elementPortIds);
    }

    @Override
    public void updateElementPorts(String... elementPortIds) {
        pluginModelHandler.updateElementPorts(elementPortIds);
    }

    @Override
    public void deleteElementPorts(String... elementPortIds) {
        pluginModelHandler.deleteElementPorts(elementPortIds);
    }

    @Override
    public void initiate() {
        initiateHandler.initiate();
    }

    @Override
    public void prepare(boolean partialStart) {
        prepareHandler.prepare(partialStart);
    }

    @Override
    public void create() {
        createHandler.create();
    }

    @Override
    public void delete() {
        deleteHandler.delete();
    }

    @Override
    public void pause() {
        new PauseHandler(this, SceneModel.Event.PAUSE).pause();
    }

    @Override
    public void unpause() {
        new UnpauseHandler(this, SceneModel.Event.UNPAUSE).unpause();
    }

    @Override
    public void suspend() {
        new SuspendHandler(this, SceneModel.Event.SUSPEND).suspend();
    }

    @Override
    public void resume() {
        new ResumeHandler(this, SceneModel.Event.RESUME).resume();
    }

    @Override
    public void initiateElement(String nodeId) {
        CloudPluginSceneElementBO element = getElement(nodeId);
        if (element == null) {
            throw new MpRuntimeException(SceneErrorCode.ELEMENT_NOT_EXIST);
        }
        new ElementInitiateHandler(this, getSceneHandler().getScene().getEvent()).initiateElement(element);
    }

    @Override
    public void createElement(String nodeId) {
        CloudPluginSceneElementBO element = getElement(nodeId);
        if (element == null) {
            throw new MpRuntimeException(SceneErrorCode.ELEMENT_NOT_EXIST);
        }
        new ElementCreateHandler(this, getSceneHandler().getScene().getEvent()).createElement(element);
    }

    @Override
    public void recreateElement(String nodeId) {
        CloudPluginSceneElementBO element = getElement(nodeId);
        if (element == null) {
            throw new MpRuntimeException(SceneErrorCode.ELEMENT_NOT_EXIST);
        }
        new RecreateHandler(this, getSceneHandler().getScene().getEvent()).recreateElement(element);
    }

    @Override
    public void modifyElement(String nodeId, SceneHandler updateHandler) {
        CloudPluginSceneElementBO element = getElement(nodeId);
        if (element == null) {
            throw new MpRuntimeException(SceneErrorCode.ELEMENT_NOT_EXIST);
        }
        CloudScenePluginHandler updateCloudHandler = updateHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        new ElementModifyHandler(this, getSceneHandler().getScene().getEvent()).modifyElement(updateCloudHandler.getElement(nodeId));
    }

    @Override
    public void restartElement(String nodeId) {
        CloudPluginSceneElementBO element = getElement(nodeId);
        if (element == null) {
            throw new MpRuntimeException(SceneErrorCode.ELEMENT_NOT_EXIST);
        }
        new RestartHandler(this, getSceneHandler().getScene().getEvent()).restartElement(element);
    }

    @Override
    public void pauseElement(String nodeId) {
        CloudPluginSceneElementBO element = getElement(nodeId);
        if (element == null) {
            throw new MpRuntimeException(SceneErrorCode.ELEMENT_NOT_EXIST);
        }
        new PauseHandler(this, getSceneHandler().getScene().getEvent()).pauseElement(element);
    }

    @Override
    public void unpauseElement(String nodeId) {
        CloudPluginSceneElementBO element = getElement(nodeId);
        if (element == null) {
            throw new MpRuntimeException(SceneErrorCode.ELEMENT_NOT_EXIST);
        }
        new UnpauseHandler(this, getSceneHandler().getScene().getEvent()).unpauseElement(element);
    }

    @Override
    public void suspendElement(String nodeId) {
        CloudPluginSceneElementBO element = getElement(nodeId);
        if (element == null) {
            throw new MpRuntimeException(SceneErrorCode.ELEMENT_NOT_EXIST);
        }
        new SuspendHandler(this, getSceneHandler().getScene().getEvent()).suspendElement(element);
    }

    @Override
    public void resumeElement(String nodeId) {
        CloudPluginSceneElementBO element = getElement(nodeId);
        if (element == null) {
            throw new MpRuntimeException(SceneErrorCode.ELEMENT_NOT_EXIST);
        }
        new ResumeHandler(this, getSceneHandler().getScene().getEvent()).resumeElement(element);
    }

    @Override
    public void snapshotElement(String nodeId, String snapshotId, String snapshotName, String description) {
        CloudPluginSceneElementBO element = getElement(nodeId);
        if (element == null) {
            throw new MpRuntimeException(SceneErrorCode.ELEMENT_NOT_EXIST);
        }
        new SnapshotHandler(this, getSceneHandler().getScene().getEvent()).snapshotElement(element, snapshotId, snapshotName, description);
    }

    public List<String> allocateOuterIps(String auxiliaryNetworkNodeId, int count) {
        var auxiliaryNetworkCloudElement = getElement(auxiliaryNetworkNodeId);
        var cidr = auxiliaryNetworkCloudElement.getAuxiliaryNetworkCidr();
        if (StringUtils.isEmpty(cidr)) {
            return new ArrayList<>();
        }

        var portIds = handlerUtils.getAuxiliaryNetworkCloudElementPortIds(auxiliaryNetworkCloudElement);
        Set<String> existIps = new HashSet<>(handlerUtils.getOuterExistIps(auxiliaryNetworkCloudElement));
        if (!StringUtils.isEmpty(auxiliaryNetworkCloudElement.getAuxiliaryNetworkRouterIp())) {
            existIps.add(auxiliaryNetworkCloudElement.getAuxiliaryNetworkRouterIp());
        }
        if (portIds != null && !portIds.isEmpty()) {
            portIds.forEach(portId -> {
                var port = getSceneHandler().getPort(portId);
                if (StringUtils.isNotEmpty(port.getIp())) {
                    existIps.add(port.getIp());
                }
            });
        }

        var ips = IpUtils.newRandomIps(cidr, count, new ArrayList<>(existIps),
                auxiliaryNetworkCloudElement.getAllocationPools(),
                BoolUtils.bool(auxiliaryNetworkCloudElement.getAuxiliaryNetworkDhcp()));
        handlerUtils.addOuterExistIps(auxiliaryNetworkCloudElement, ips);
        return ips;
    }

    @Override
    public ScenePluginDataBO toData() {
        getElementPortMap().forEach((portId, cloudPort) -> {
            var auxiliaryNetworkCloudElement = handlerUtils.getPortAuxiliaryNetworkCloudElement(portId);
            if (auxiliaryNetworkCloudElement != null && auxiliaryNetworkCloudElement.getRole() != null && auxiliaryNetworkCloudElement.getRole().equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK)) {
                cloudPort.setConnToExtNet(true);
            } else {
                cloudPort.setConnToExtNet(false);
            }
        });
        return super.toData();
    }

    @Override
    public void reformTopo() {
        TopoReformUtils topoReformUtils = new TopoReformUtils(this);
        var coreRouters = getElementMap().values().stream()
                .filter(e -> ElementTypeUtils.isCoreRouter(e))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(coreRouters)) {
            // 存在核心路由器 需要新增一个隐藏的交换机节点
            var hiddenSwitch = topoReformUtils.addAuxiliarySwitchNode();
            coreRouters.forEach(c -> {
                // 交换机新增一个端口
                var switchPort = topoReformUtils.addAuxiliaryPort(hiddenSwitch.getPluginElement());
                // 核心路由器新增一个端口 端口默认有telnet接入方式
                var coreRouterPort = topoReformUtils.addAuxiliaryPort(c);
                topoReformUtils.addAuxiliaryAccessMode(coreRouterPort.getPluginPort(), AccessMode.Protocol.TELNET);
                // 交换机和核心路由器新增一根连线
                topoReformUtils.addConnect(switchPort.getScenePort(), coreRouterPort.getScenePort());
            });
        }
    }

    /**
     * 向插件和topo中新增一个节点
     * @param nodeId
     * @param pluginElement
     * @param srcElement
     */
    public void addElementAndSrcElement(String nodeId, CloudPluginSceneElementBO pluginElement, SceneElementBO srcElement) {
        pluginElement.setSrcElement(srcElement);
        getElementIds().add(nodeId);
        getElementMap().put(nodeId, pluginElement);
        this.getSceneHandler().getElementMap().put(nodeId, pluginElement.getSrcElement());
        this.getSceneHandler().addElementPlugin(PluginConstant.PLUGIN_KEY, nodeId, JsonCommon.beanToSnakeMap(pluginElement));
    }

    /**
     * 向插件和topo中新增一个端口
     * @param portId
     * @param pluginPort
     * @param srcPort
     */
    public void addPortAndSrcPort(String portId, CloudPluginSceneElementPortBO pluginPort, SceneElementPortBO srcPort) {
        pluginPort.setSrcPort(srcPort);
        getElementPortIds().add(portId);
        getElementPortMap().put(portId, pluginPort);
        this.getSceneHandler().getElementPortMap().put(portId, srcPort);
        this.getSceneHandler().addElementPortPlugin(PluginConstant.PLUGIN_KEY, portId, JsonCommon.beanToSnakeMap(pluginPort));
        // 处理辅助变量
        this.getSceneHandler().addPort(portId, srcPort.getElement());
    }

    @Override
    public void correctData() {
        super.correctData();
        // 纠正 role virtualType
        if (getElementMap() != null) {
            getElementMap().values().forEach(element -> {
                Integer role = element.getRole();
                String imageType = element.getImageType();
                correctElementRoleRelated(element.getSrcElement(), element, role, imageType);
                correctElementImageTypeRelated(element.getSrcElement(), element, imageType);
            });
        }
        
        // 连接外部网络,而且开启公网映射,就打开端口映射
        if (CloudSingleton.cloudAppConfig.getPublicNetProxyServer()) {
            getElementMap().forEach((id, element) -> {
                if (CollectionUtils.isNotEmpty(element.getSrcElement().getPorts())) {
                    element.getSrcElement().getPorts().forEach(p -> {
                        var connectedElement = getPortConnectedSrcElement(p.getId());
                        if (connectedElement != null) {
                            var connectCloudElement = getElement(connectedElement.getId());
                            if (connectCloudElement.getRole() != null &&
                                    BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK == connectCloudElement.getRole()) {
                                if (CollectionUtils.isNotEmpty(p.getAccessModes())) {
                                    Set<String> setedModes = new HashSet<>();
                                    p.getAccessModes().forEach(mode -> {
                                        if (!AccessMode.Protocol.CONSOLE.equals(mode.getProtocol())) {
                                            boolean publicNetProxy = mode.getPublicNetProxy() != null ? mode.getPublicNetProxy() : CloudSingleton.cloudAppConfig.getPublicNetProxy();
                                            if (publicNetProxy) {
                                                String modeKey = this.getModeKey(mode.getProtocol(), String.valueOf(mode.getPort()), mode.getUsername(), mode.getPassword());
                                                setedModes.add(modeKey);
                                                mode.setProxy(true);
                                            }
                                        }
                                    });
                                    var cloudPort = getPort(p.getId());
                                    if (cloudPort != null && CollectionUtils.isNotEmpty(cloudPort.getAccessModes())) {
                                        cloudPort.getAccessModes().forEach(mode -> {
                                            String modeKey = this.getModeKey(mode.getProtocol(), String.valueOf(mode.getPort()), mode.getUsername(), mode.getPassword());
                                            if (setedModes.contains(modeKey)) {
                                                mode.setProxy(true);
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    private String getModeKey(String protocol, String port, String user, String password) {
        return String.format("%s:%s:%s:%s", protocol, AccessMode.Protocol.CONSOLE.equals(protocol) ? StringUtils.EMPTY : port, user, password);
    }

    private void correctElementRoleRelated(SceneElementBO srcElement, CloudPluginSceneElementBO cloudElement,
                                           Integer role, String imageType) {
        if (role == null) {
            return;
        }

        // 网元类型暂时无法准确纠正
        var isForceCorrectElementType = srcElement.getType() == null
                || srcElement.getType().equals(BaseSceneElementModel.Type.OTHER);
        // 虚拟化类型可能无法准确纠正，但必须纠正，不然无法正常虚拟化

        if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY)) {
            if (isForceCorrectElementType) {
                // TODO 暂定 - 虚拟二层网关就是默认交换机
                srcElement.setType(BaseSceneElementModel.Type.SWITCH);
            }
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_GATEWAY)) {
            if (imageType != null && imageType.equals(BaseCloudPluginSceneElementModel.ImageType.SIMPLE)) {
                if (isForceCorrectElementType) {
                    // TODO 暂定 - 简易虚拟三层网关就是默认路由
                    srcElement.setType(BaseSceneElementModel.Type.ROUTER);
                }
                cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER);
            } else {
                if (isForceCorrectElementType) {
                    // TODO 暂定 - 其它虚拟三层网关就是仿真路由
                    srcElement.setType(BaseSceneElementModel.Type.ROUTER);
                }
            }
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK)) {
            // 虚拟外部网络
            srcElement.setType(BaseSceneElementModel.Type.SWITCH);
            cloudElement.setExternal(true);
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL)) {
            srcElement.setType(BaseSceneElementModel.Type.TERMINAL);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TRANSPARENT_GATEWAY)) {
            if (isForceCorrectElementType) {
                // TODO 暂定 - 虚拟透明网关就是默认防火墙
                srcElement.setType(BaseSceneElementModel.Type.FIREWALL);
            }
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL);
            cloudElement.setTransparent(true);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_GATEWAY)) {
            if (isForceCorrectElementType) {
                // TODO 暂定 - 虚拟网关就是仿真路由
                srcElement.setType(BaseSceneElementModel.Type.ROUTER);
            }
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_IMAGE_GATEWAY)) {
            if (isForceCorrectElementType) {
                // TODO 暂定 - 三层镜像网关就是仿真路由
                srcElement.setType(BaseSceneElementModel.Type.ROUTER);
            }
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER)) {
            srcElement.setType(BaseSceneElementModel.Type.ROUTER);
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH)) {
            srcElement.setType(BaseSceneElementModel.Type.SWITCH);
            // 简单三层虚拟交换机用虚拟默认路由实现
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL)) {
            srcElement.setType(BaseSceneElementModel.Type.FIREWALL);
            // 简单三层虚拟防火墙用虚拟默认路由 + 防火墙实现
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.ENTITY_GATEWAY)) {
            if (isForceCorrectElementType) {
                // TODO 暂定 - 实体网关就是实体路由
                srcElement.setType(BaseSceneElementModel.Type.ROUTER);
            }
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.ENTITY_TERMINAL)) {
            srcElement.setType(BaseSceneElementModel.Type.TERMINAL);
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.NPC)) {
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.NPC);
        }
    }

    private void correctElementImageTypeRelated(SceneElementBO srcElement, CloudPluginSceneElementBO cloudElement,
                                                String imageType) {
        if (imageType == null) {
            return;
        }

        if (imageType.equals(BaseCloudPluginSceneElementModel.ImageType.SIMPLE)) {
        } else if (imageType.equals(BaseCloudPluginSceneElementModel.ImageType.VM)) {
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE);
        } else if (imageType.equals(BaseCloudPluginSceneElementModel.ImageType.CONTAINER)) {
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER);
        } else if (imageType.equals(BaseCloudPluginSceneElementModel.ImageType.ENTITY)) {
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE);
        }
    }

    @Override
    public void modify(SceneHandler originSceneHandler, List<String> updateElementIds) {
        new ModifyHandler(originSceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY), this, updateElementIds).modify();
    }

}
