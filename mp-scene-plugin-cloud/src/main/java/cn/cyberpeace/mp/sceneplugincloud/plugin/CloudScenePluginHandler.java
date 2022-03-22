package cn.cyberpeace.mp.sceneplugincloud.plugin;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.BoolUtils;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.constant.SceneErrorCode;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementNetworkModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ConnectedNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.DeleteHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.InitiateHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.PauseHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.RecreateHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.RestartHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ResumeHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.SuspendHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.UnpauseHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.modelhandler.CloudScenePluginModelHandler;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


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
                                ConnectedNetworkBO connectedNetworkBO = new ConnectedNetworkBO(networkBOMap.get(pluginToPort.getLogicNetworkId()).getNetwork(), getHandlerUtils());
                                connectedNetworkBO.addPort(p, to, false);
                                e.getAccessNetworks().add(connectedNetworkBO);
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
        getElementMap().values().forEach(e -> networks.addAll(e.getDeriveNetworks()));
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
    public void recreateElement(String nodeId) {
        CloudPluginSceneElementBO element = getElement(nodeId);
        if (element == null) {
            throw new MpRuntimeException(SceneErrorCode.ELEMENT_NOT_EXIST);
        }
        new RecreateHandler(this, getSceneHandler().getScene().getEvent()).recreateElement(element);
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
}
