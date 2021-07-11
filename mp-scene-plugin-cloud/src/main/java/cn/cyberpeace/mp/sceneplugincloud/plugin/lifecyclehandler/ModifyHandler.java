package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementNetworkModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
public class ModifyHandler extends HandlerCommon {

    public static class SceneProcess {
        public static final EventProcess MODIFY_DELETE_NODES = new EventProcess("modify_delete_nodes", "sceneplugincloud.event.modify_delete_node");
        public static final EventProcess MODIFY_CREATE_NODES = new EventProcess("modify_create_nodes", "sceneplugincloud.event.modify_create_node");
        public static final EventProcess MODIFY_NODES = new EventProcess("modify_nodes", "sceneplugincloud.event.modify_node");
    }

    public static class ElementProcess {

    }

    private ModifyInitiateHandler modifyInitiateHandler;
    private ModifyCreateHandler modifyCreateHandler;
    private ModifyDeleteHandler modifyDeleteHandler;
    private ElementModifyHandler elementModifyHandler;
    private Set<String> mayUpdateNodeIds = null;
    private CloudScenePluginHandler newPlugin;
    private CloudScenePluginHandler originPlugin;

    public ModifyHandler(CloudScenePluginHandler originPlugin, CloudScenePluginHandler newPlugin, List<String> updateElementIds) {
        super(newPlugin, CreateTaskPool.getInstance(), SceneModel.Event.MODIFY);
        Set<String> originNodeIds = new HashSet<>(originPlugin.getSceneHandler().getElementMap().keySet());
        Set<String> createdNodeIds = new HashSet<>(originNodeIds);
        this.modifyInitiateHandler = new ModifyInitiateHandler(createdNodeIds, newPlugin, originPlugin, getTaskPool());
        this.modifyCreateHandler = new ModifyCreateHandler(createdNodeIds, newPlugin, getTaskPool());
        Set<String> newNodeIds = newPlugin.getSceneHandler().getElementMap().keySet();

        mayUpdateNodeIds = new HashSet<>(originNodeIds);
        mayUpdateNodeIds.retainAll(newNodeIds);

        originNodeIds.removeAll(newNodeIds);
        if (CollectionUtils.isNotEmpty(updateElementIds)) {
            mayUpdateNodeIds.addAll(updateElementIds);
        }
        this.modifyDeleteHandler = new ModifyDeleteHandler(originNodeIds, originPlugin, getTaskPool());
        this.elementModifyHandler = new ElementModifyHandler(newPlugin, originPlugin, originPlugin.getSceneHandler().getScene().getEvent());
        this.newPlugin = newPlugin;
        this.originPlugin = originPlugin;
    }

    public void modify() {
        prepareEventProcess(SceneModel.Event.MODIFY, SceneProcess.class, ElementProcess.class);

        getPluginHandler().getSceneHandler().reportSceneUpdated();
        runSceneProgress(SceneProcess.MODIFY_DELETE_NODES, () -> {
            getPluginHandler().getSceneHandler().addProgressPercentage(10);
            this.modifyDeleteHandler.delete();
            getPluginHandler().getSceneHandler().addProgressPercentage(10);
        });
        
        Set<String> dynamipsIds = new HashSet<>();
        this.modifyCreateHandler.getPluginHandler().getElementMap().forEach((k, v) -> {
            if (v != null && v.getImageType().equals(BaseCloudPluginSceneElementModel.ImageType.DYNAMIPS)) {
                this.modifyCreateHandler.getPluginHandler().getSceneHandler().addExcludeHandleNode(v.getSrcElement().getId());
                dynamipsIds.add(v.getSrcElement().getId());
            }
        });
        if (this.modifyCreateHandler.getPluginHandler().getScene() != null) {
            this.modifyCreateHandler.getPluginHandler().getScene().setEnableRate(this.modifyDeleteHandler.getPluginHandler().getScene().getEnableRate());
        }
        runSceneProgress(SceneProcess.MODIFY_CREATE_NODES, () -> {
            getPluginHandler().getSceneHandler().addProgressPercentage(10);
            this.modifyInitiateHandler.initiate();
            this.modifyCreateHandler.create();
            getPluginHandler().getSceneHandler().addProgressPercentage(10);
        });
        runSceneProgress(SceneProcess.MODIFY_NODES, () -> {
            getPluginHandler().getSceneHandler().addProgressPercentage(10);
            if (mayUpdateNodeIds != null && !mayUpdateNodeIds.isEmpty()) {
                for (String elementId : mayUpdateNodeIds) {
                    if (getPluginHandler().getSceneHandler().isHandleNode(elementId)) {
                        CloudPluginSceneElementBO newElement = newPlugin.getElement(elementId);
                        if (newElement != null && newElement.getSrcElement() != null && newElement.getSrcElement().getId() != null) {
                            new ElementModifyHandler(newPlugin, originPlugin,
                                    originPlugin.getSceneHandler().getScene().getEvent()).modifyElement(newElement.getSrcElement().getId());
                        }
                    } else if (dynamipsIds.contains(elementId)) {
                        CloudPluginSceneElementBO newElement = newPlugin.getElement(elementId);
                        if (newElement != null && newElement.getSrcElement() != null && newElement.getSrcElement().getId() != null) {
                            new ElementModifyHandler(newPlugin, originPlugin,
                                    originPlugin.getSceneHandler().getScene().getEvent()).modifyElement(newElement.getSrcElement().getId());
                        }
                    }
                }
            }
        });
    }

    private static class ModifyInitiateHandler extends InitiateHandler {
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
            
            for (var e : allElements) {
                if (e.getSrcElement().getPorts() != null && e.getSrcElement().getPorts().size() > 0) {
                    Set<String> findedNetPortIds = new HashSet<>();
                    CloudPluginSceneElementPortBO defaultCreatedNetPluginPort = null;
                    for (var p : e.getSrcElement().getPorts()) {
                        var pluginPort = getPluginHandler().getPort(p.getId());
                        var to = getHandlerUtils().getPortConnectedOpaqueSrcPort(p.getId());
                        if (pluginPort != null) {
                            boolean handlerd = false ;
                            if (e.getSrcElement().getType() != null && e.getSrcElement().getType() == BaseSceneElementModel.Type.TERMINAL) {
                                
                                if (to != null) {
                                    
                                    var pluginToPort = getPluginHandler().getPort(to.getId());
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
                                    
                                    if (networkBOMap.containsKey(pluginPort.getLogicNetworkId())) {
                                        networkBOMap.get(pluginPort.getLogicNetworkId()).addPort(p, to, true);
                                        findedNetPortIds.add(p.getId());
                                        defaultCreatedNetPluginPort = pluginPort;
                                    }
                                } else if (to != null) {
                                    
                                    var pluginToPort = getPluginHandler().getPort(to.getId());
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
                        for (var p : e.getSrcElement().getPorts()) {
                            if (findedNetPortIds.contains(p.getId())) {
                                continue;
                            }
                            boolean isAddedPort = getHandlerUtils().isAddedPort(p.getId(), newPlugin, originPlugin);
                            if (isAddedPort && (e.getSrcElement().getType() == BaseSceneElementModel.Type.SWITCH
                                    || e.getSrcElement().getType() == BaseSceneElementModel.Type.ROUTER
                                    || e.getSrcElement().getType() == BaseSceneElementModel.Type.FIREWALL)) {
                                
                                var to = getHandlerUtils().getPortConnectedOpaqueSrcPort(p.getId());
                                var pluginPort = getPluginHandler().getPort(p.getId());
                                pluginPort.setLogicNetworkId(defaultCreatedNetPluginPort.getLogicNetworkId());
                                networkBOMap.get(pluginPort.getLogicNetworkId()).addPort(p, to, true);
                            }
                        }
                    }
                }
            }
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
            if (this.createdNodeIds.contains(cloudElement.getSrcElement().getId()) && cloudElement.getResourceId() != null) {
                return;
            }
            super.createAuxiliaryNetwork(cloudElement);
        }

        @Override
        protected void prepareEventProcess() {
            prepareEventProcess(SceneModel.Event.MODIFY, SceneProcess.class, ElementProcess.class);
        }

        @Override
        protected void createVirtualRouter(CloudPluginSceneElementBO cloudElement) {
            if (this.createdNodeIds.contains(cloudElement.getSrcElement().getId()) && cloudElement.getResourceId() != null) {
                
                return;
            }
            super.createVirtualRouter(cloudElement);
        }

        @Override
        protected void createVirtualFirewall(CloudPluginSceneElementBO cloudElement) {
            if (this.createdNodeIds.contains(cloudElement.getSrcElement().getId()) && cloudElement.getResourceId() != null) {
                
                return;
            }
            super.createVirtualFirewall(cloudElement);
        }

        @Override
        protected void createVirtualMachine(CloudPluginSceneElementBO cloudElement) {
            if (this.createdNodeIds.contains(cloudElement.getSrcElement().getId()) && cloudElement.getResourceId() != null) {
                
                return;
            }
            super.createVirtualMachine(cloudElement);
        }

        @Override
        protected void createContainer(CloudPluginSceneElementBO cloudElement) {
            if (this.createdNodeIds.contains(cloudElement.getSrcElement().getId()) && cloudElement.getResourceId() != null) {
                
                return;
            }
            super.createContainer(cloudElement);
        }

        @Override
        protected void createEntityNode(CloudPluginSceneElementBO cloudElement) {
            if (this.createdNodeIds.contains(cloudElement.getSrcElement().getId()) && cloudElement.getResourceId() != null) {
                
                return;
            }
            super.createEntityNode(cloudElement);
        }

        @Override
        protected void recordNpcNode(CloudPluginSceneElementBO cloudElement) {
            if (this.createdNodeIds.contains(cloudElement.getSrcElement().getId()) && cloudElement.getResourceId() != null) {
                
                return;
            }
            super.recordNpcNode(cloudElement);
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
        protected void syncFlowPortInfo() {
            log.info("ModifyDeleteHandler.syncFlowPortInfo.");
        }

        @Override
        public void delete() {
            super.delete();
            
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
