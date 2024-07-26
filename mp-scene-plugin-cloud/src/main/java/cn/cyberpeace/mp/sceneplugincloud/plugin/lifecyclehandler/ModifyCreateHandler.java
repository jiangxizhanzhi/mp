package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;

import java.util.Set;


public class ModifyCreateHandler extends CreateHandler {
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
            // 已存在的节点无需再次创建
            return;
        }
        super.createVirtualRouter(cloudElement);
    }

    @Override
    protected void createVirtualFirewall(CloudPluginSceneElementBO cloudElement) {
        if (this.createdNodeIds.contains(cloudElement.getSrcElement().getId()) && cloudElement.getResourceId() != null) {
            // 已存在的节点无需再次创建
            return;
        }
        super.createVirtualFirewall(cloudElement);
    }

    @Override
    protected void createVirtualMachine(CloudPluginSceneElementBO cloudElement) {
        if (this.createdNodeIds.contains(cloudElement.getSrcElement().getId()) && cloudElement.getResourceId() != null) {
            // 已存在的节点无需再次创建
            return;
        }
        super.createVirtualMachine(cloudElement);
    }

    @Override
    protected void createContainer(CloudPluginSceneElementBO cloudElement) {
        if (this.createdNodeIds.contains(cloudElement.getSrcElement().getId()) && cloudElement.getResourceId() != null) {
            // 已存在的节点无需再次创建
            return;
        }
        super.createContainer(cloudElement);
    }

    @Override
    protected void createEntityNode(CloudPluginSceneElementBO cloudElement) {
        if (this.createdNodeIds.contains(cloudElement.getSrcElement().getId()) && cloudElement.getResourceId() != null) {
            // 已存在的节点无需再次创建
            return;
        }
        super.createEntityNode(cloudElement);
    }

    @Override
    protected void recordNpcNode(CloudPluginSceneElementBO cloudElement) {
        if (this.createdNodeIds.contains(cloudElement.getSrcElement().getId()) && cloudElement.getResourceId() != null) {
            // 已存在的节点无需再次创建
            return;
        }
        super.recordNpcNode(cloudElement);
    }
}
