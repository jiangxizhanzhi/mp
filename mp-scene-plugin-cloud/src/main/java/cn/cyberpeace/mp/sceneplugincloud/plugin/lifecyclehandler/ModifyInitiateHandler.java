package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementNetworkModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


public class ModifyInitiateHandler extends InitiateHandler {
    private Set<String> createdNodeIds;
    private CloudScenePluginHandler newPlugin;
    private CloudScenePluginHandler originPlugin;

    public ModifyInitiateHandler(Set<String> createdNodeIds, CloudScenePluginHandler newPluginHandler, CloudScenePluginHandler originPluginHandler, TaskPool taskPool) {
        super(newPluginHandler);
        setTaskPool(taskPool);
        setEvent(SceneModel.Event.MODIFY);
        this.createdNodeIds = createdNodeIds;
        this.newPlugin = newPluginHandler;
        this.originPlugin = originPluginHandler;
    }

    @Override
    public void initiate() {
        super.initiate();
    }

    @Override
    protected void prepareEventProcess() {
        prepareEventProcess(SceneModel.Event.MODIFY, SceneProcess.class, ElementProcess.class);
    }

    @Override
    protected boolean resetResourceId() {
        return false;
    }

    @Override
    public void initiateElementBasePropertiesProcess(CloudPluginSceneElementBO cloudElement) {
        if (this.createdNodeIds.contains(cloudElement.getSrcElement().getId()) && cloudElement.getResourceId() != null) {
            return;
        }
        super.initiateElementBasePropertiesProcess(cloudElement);
    }

    @Override
    protected void afterOrderElements(List<CloudPluginSceneElementBO> allElements) {
        List<CloudPluginSceneElementNetworkModel> networks = getPluginHandler().getPluginModelHandler().readNetworks();
        Map<String, ConnectedNetworkBO> networkBOMap = new HashMap<>();
        networks.forEach(network -> {
            CloudPluginSceneElementNetworkBO networkBO = new CloudPluginSceneElementNetworkBO();
            networkBO.fromModel(network);
            ConnectedNetworkBO connectedNetworkBO = new ConnectedNetworkBO(networkBO, getHandlerUtils());
            if (getPluginHandler().getElement(networkBO.getSceneElement()) == null) {
                return;
            }
            networkBOMap.put(networkBO.getId(), connectedNetworkBO);
            getPluginHandler().getElement(networkBO.getSceneElement()).getDeriveNetworks().add(connectedNetworkBO);
        });
        // 处理加入的网络
        for (CloudPluginSceneElementBO e : allElements) {
            if (e.getSrcElement().getPorts() != null && e.getSrcElement().getPorts().size() > 0) {
                Set<String> findedNetPortIds = new HashSet<>();
                CloudPluginSceneElementPortBO defaultCreatedNetPluginPort = null;
                for (SceneElementPortBO p : e.getSrcElement().getPorts()) {
                    CloudPluginSceneElementPortBO pluginPort = getPluginHandler().getPort(p.getId());
                    SceneElementPortBO to = getHandlerUtils().getPortConnectedOpaqueSrcPort(p.getId());
                    if (pluginPort != null) {
                        boolean handlerd = false ;
                        if (e.getSrcElement().getType() != null && e.getSrcElement().getType() == BaseSceneElementModel.Type.TERMINAL) {
                            // 终端,优先看对端网络
                            if (to != null) {
                                // 未构建网络看对端端口
                                CloudPluginSceneElementPortBO pluginToPort = getPluginHandler().getPort(to.getId());
                                if (StringUtils.isNotEmpty(pluginToPort.getLogicNetworkId())) {
                                    if (networkBOMap.containsKey(pluginToPort.getLogicNetworkId())) {
                                        ConnectedNetworkBO connectedNetworkBO = new ConnectedNetworkBO(networkBOMap.get(pluginToPort.getLogicNetworkId()).getNetwork(), getHandlerUtils());
                                        connectedNetworkBO.addPort(p, to, false);
                                        e.getAccessNetworks().add(connectedNetworkBO);
                                        findedNetPortIds.add(p.getId());
                                        handlerd = true;
                                        pluginPort.setLogicNetworkId(pluginToPort.getLogicNetworkId());
                                        pluginPort.setAuxiliaryNetworkId(pluginToPort.getAuxiliaryNetworkId());
                                    }
                                }
                            } else if (StringUtils.isNotEmpty(pluginPort.getLogicNetworkId())) {
                                // 自己构建网络直接加入
                                if (networkBOMap.containsKey(pluginPort.getLogicNetworkId())) {
                                    networkBOMap.get(pluginPort.getLogicNetworkId()).addPort(p, to, true);
                                    findedNetPortIds.add(p.getId());
                                    defaultCreatedNetPluginPort = pluginPort;
                                    handlerd = true ;
                                }
                            }
                        }
                        if (!handlerd) {
                            if (StringUtils.isNotEmpty(pluginPort.getLogicNetworkId())) {
                                // 自己构建网络直接加入
                                if (networkBOMap.containsKey(pluginPort.getLogicNetworkId())) {
                                    networkBOMap.get(pluginPort.getLogicNetworkId()).addPort(p, to, true);
                                    findedNetPortIds.add(p.getId());
                                    defaultCreatedNetPluginPort = pluginPort;
                                }
                            } else if (to != null) {
                                // 未构建网络看对端端口
                                CloudPluginSceneElementPortBO pluginToPort = getPluginHandler().getPort(to.getId());
                                if (StringUtils.isNotEmpty(pluginToPort.getLogicNetworkId())) {
                                    if (networkBOMap.containsKey(pluginToPort.getLogicNetworkId())) {
                                        ConnectedNetworkBO connectedNetworkBO = new ConnectedNetworkBO(networkBOMap.get(pluginToPort.getLogicNetworkId()).getNetwork(), getHandlerUtils());
                                        connectedNetworkBO.addPort(p, to, false);
                                        e.getAccessNetworks().add(connectedNetworkBO);
                                        findedNetPortIds.add(p.getId());
                                    }
                                }
                            }
                        }
                    }
                }
                if (defaultCreatedNetPluginPort != null) {
                    for (SceneElementPortBO p : e.getSrcElement().getPorts()) {
                        if (findedNetPortIds.contains(p.getId())) {
                            continue;
                        }
                        boolean isAddedPort = getHandlerUtils().isAddedPort(p.getId(), newPlugin, originPlugin);
                        if (isAddedPort && (e.getSrcElement().getType() == BaseSceneElementModel.Type.SWITCH
                                || e.getSrcElement().getType() == BaseSceneElementModel.Type.ROUTER
                                || e.getSrcElement().getType() == BaseSceneElementModel.Type.FIREWALL)) {
                            // 网关设备新增的端口 需要加到原有的网络上
                            SceneElementPortBO to = getHandlerUtils().getPortConnectedOpaqueSrcPort(p.getId());
                            CloudPluginSceneElementPortBO pluginPort = getPluginHandler().getPort(p.getId());
                            pluginPort.setLogicNetworkId(defaultCreatedNetPluginPort.getLogicNetworkId());
                            networkBOMap.get(pluginPort.getLogicNetworkId()).addPort(p, to, true);
                        }
                    }
                }
            }
        }
        for (CloudPluginSceneElementNetworkModel net : networks) {
            getHandlerUtils().getCreatedNetworks().add(net.getId());
        }
    }

}
