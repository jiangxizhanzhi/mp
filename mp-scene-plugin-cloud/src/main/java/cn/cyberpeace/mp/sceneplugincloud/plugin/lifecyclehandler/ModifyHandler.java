package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementNetworkModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.*;

@Slf4j
public class ModifyHandler extends HandlerCommon {

    private ModifyInitiateHandler modifyInitiateHandler;
    private ModifyCreateHandler modifyCreateHandler;
    private ModifyDeleteHandler modifyDeleteHandler;

    public ModifyHandler(CloudScenePluginHandler originPlugin, CloudScenePluginHandler newPlugin) {
        super(newPlugin, CreateTaskPool.getInstance(), SceneModel.Event.MODIFY);
        Set<String> originNodeIds = originPlugin.getSceneHandler().getElementMap().keySet();
        Set<String> createdNodeIds = new HashSet<>(originNodeIds);
        this.modifyInitiateHandler = new ModifyInitiateHandler(createdNodeIds, newPlugin, getTaskPool());
        this.modifyCreateHandler = new ModifyCreateHandler(createdNodeIds, newPlugin, getTaskPool());
        Set<String> newNodeIds = newPlugin.getSceneHandler().getElementMap().keySet();
        originNodeIds.removeAll(newNodeIds);
        this.modifyDeleteHandler = new ModifyDeleteHandler(originNodeIds, originPlugin, getTaskPool());
    }

    public void modify() {
        this.modifyDeleteHandler.delete();
        this.modifyInitiateHandler.initiate();
        this.modifyCreateHandler.create();
    }

    private static class ModifyInitiateHandler extends InitiateHandler {
        private Set<String> createdNodeIds;

        public ModifyInitiateHandler(Set<String> createdNodeIds, CloudScenePluginHandler pluginHandler, TaskPool taskPool) {
            super(pluginHandler);
            setTaskPool(taskPool);
            setEvent(SceneModel.Event.MODIFY);
            this.createdNodeIds = createdNodeIds;
        }

        @Override
        public void initiate() {
            this.initiateNetwork();
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
            if (this.createdNodeIds.contains(cloudElement.getSrcElement().getId())) {
                return;
            }
            super.initiateElementBasePropertiesProcess(cloudElement);
        }

        public void initiateNetwork() {
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
            getPluginHandler().getElementMap().values().forEach(e -> {
                if (e.getSrcElement().getPorts() != null && e.getSrcElement().getPorts().size() > 0) {
                    e.getSrcElement().getPorts().forEach(p -> {
                        var pluginPort = getPluginHandler().getPort(p.getId());
                        var to = getHandlerUtils().getPortConnectedOpaqueSrcPort(p.getId());
                        if (pluginPort != null) {
                            if (StringUtils.isNotEmpty(pluginPort.getLogicNetworkId())) {
                                // 自己构建网络直接加入
                                if (networkBOMap.containsKey(pluginPort.getLogicNetworkId())) {
                                    networkBOMap.get(pluginPort.getLogicNetworkId()).addPort(p, to, true);
                                }
                            } else if (to != null) {
                                // 未构建网络看对端端口
                                var pluginToPort = getPluginHandler().getPort(to.getId());
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
            for (var net : networks) {
                getHandlerUtils().getCreatedNetworks().add(net.getId());
            }
        }
    }

    private static class ModifyCreateHandler extends CreateHandler {
        private Set<String> createdNodeIds;

        public ModifyCreateHandler(Set<String> createdNodeIds, CloudScenePluginHandler pluginHandler, TaskPool taskPool) {
            super(pluginHandler);
            setEvent(SceneModel.Event.MODIFY);
            setTaskPool(taskPool);
            this.createdNodeIds = createdNodeIds;
        }

        @Override
        protected void createAuxiliaryNetwork(CloudPluginSceneElementBO cloudElement) {
            if (this.createdNodeIds.contains(cloudElement.getSrcElement().getId())) {
                return;
            }
            super.createAuxiliaryNetwork(cloudElement);
        }

        @Override
        protected void prepareEventProcess() {
            prepareEventProcess(SceneModel.Event.MODIFY, SceneProcess.class, ElementProcess.class);
        }

        @Override
        public void createElementProcess(CloudPluginSceneElementBO cloudElement) {
            if (this.createdNodeIds.contains(cloudElement.getSrcElement().getId())) {
                // 已存在的节点无需再次创建
                return;
            }
            super.createElementProcess(cloudElement);
        }
    }

    private static class ModifyDeleteHandler extends DeleteHandler {
        private Set<String> deletedNodeIds;

        public ModifyDeleteHandler(Set<String> deletedNodeIds, CloudScenePluginHandler pluginHandler, TaskPool taskPool) {
            super(pluginHandler);
            setEvent(SceneModel.Event.MODIFY);
            setTaskPool(taskPool);
            this.deletedNodeIds = deletedNodeIds;
        }

        @Override
        public void delete() {
            super.delete();
            // 删除网络信息
            List<CloudPluginSceneElementNetworkModel> networks = getPluginHandler().getPluginModelHandler().readNetworks();
            List<String> deleteNets = new ArrayList<>();
            for (var net : networks) {
                if (deletedNodeIds.contains(net.getSceneElement())) {
                    deleteNets.add(net.getId());
                }
            }
            if (CollectionUtils.isNotEmpty(deleteNets)) {
                getPluginHandler().getPluginModelHandler().deleteElementNetworks(deleteNets.toArray(new String[]{}));
            }
        }

        @Override
        protected void prepareEventProcess() {
            prepareEventProcess(SceneModel.Event.MODIFY, SceneProcess.class, ElementProcess.class);
        }

        @Override
        protected void deleteElementProcess(CloudPluginSceneElementBO cloudElement) {
            if (deletedNodeIds.contains(cloudElement.getSrcElement().getId())) {
                super.deleteElementProcess(cloudElement);
            }
        }

        @Override
        public void deleteAuxiliaryNetworkProcess(CloudPluginSceneElementBO cloudElement) {
            if (deletedNodeIds.contains(cloudElement.getSrcElement().getId())) {
                super.deleteAuxiliaryNetworkProcess(cloudElement);
            }
        }
    }
}
