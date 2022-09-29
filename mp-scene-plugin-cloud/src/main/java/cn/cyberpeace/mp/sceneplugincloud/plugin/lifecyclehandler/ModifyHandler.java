package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementNetworkModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ModifyHandler extends HandlerCommon {

    private ModifyInitiateHandler modifyInitiateHandler;
    private ModifyCreateHandler modifyCreateHandler;
    private ModifyDeleteHandler modifyDeleteHandler;

    public ModifyHandler(CloudScenePluginHandler originPlugin, CloudScenePluginHandler newPlugin) {
        super(newPlugin, CreateTaskPool.getInstance(), SceneModel.Event.MODIFY);
        this.modifyInitiateHandler = new ModifyInitiateHandler(newPlugin, getTaskPool());
        this.modifyCreateHandler = new ModifyCreateHandler(newPlugin, getTaskPool());
        Set<String> originNodeIds = originPlugin.getSceneHandler().getElementMap().keySet();
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

        public ModifyInitiateHandler(CloudScenePluginHandler pluginHandler, TaskPool taskPool) {
            super(pluginHandler);
            setTaskPool(taskPool);
            setEvent(SceneModel.Event.MODIFY);
        }

        @Override
        public void initiate() {
            this.initiateNetwork();
            super.initiate();
            getPluginHandler().getElementMap().values().forEach(e -> {
                if (StringUtils.isNotEmpty(e.getResourceId())) {
                    getHandlerUtils().getCreatedNodes().add(e.getSrcElement().getId());
                }
            });
        }

        @Override
        protected void prepareEventProcess() {
            prepareEventProcess(SceneModel.Event.MODIFY, SceneProcess.class, ElementProcess.class);
        }

        @Override
        protected boolean resetResourceId() {
            return false;
        }

        public void initiateNetwork() {
            List<CloudPluginSceneElementNetworkModel> networks = getPluginHandler().getPluginModelHandler().readNetworks();
            Map<String, ConnectedNetworkBO> networkBOMap = new HashMap<>();
            networks.forEach(network -> {
                CloudPluginSceneElementNetworkBO networkBO = new CloudPluginSceneElementNetworkBO();
                networkBO.fromModel(network);
                ConnectedNetworkBO connectedNetworkBO = new ConnectedNetworkBO(networkBO, getHandlerUtils());
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
        public ModifyCreateHandler(CloudScenePluginHandler pluginHandler, TaskPool taskPool) {
            super(pluginHandler);
            setEvent(SceneModel.Event.MODIFY);
            setTaskPool(taskPool);
        }

        @Override
        protected void prepareEventProcess() {
            prepareEventProcess(SceneModel.Event.MODIFY, SceneProcess.class, ElementProcess.class);
        }

        @Override
        public void createElementProcess(CloudPluginSceneElementBO cloudElement) {
            if (getHandlerUtils().getCreatedNodes().contains(cloudElement.getSrcElement().getId())) {
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
        protected void prepareEventProcess() {
            prepareEventProcess(SceneModel.Event.MODIFY, SceneProcess.class, ElementProcess.class);
        }

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
