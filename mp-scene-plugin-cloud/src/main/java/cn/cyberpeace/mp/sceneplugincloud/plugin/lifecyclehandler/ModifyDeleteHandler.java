package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementNetworkModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class ModifyDeleteHandler extends DeleteHandler {
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
        // 删除网络信息
        List<CloudPluginSceneElementNetworkModel> networks = getPluginHandler().getPluginModelHandler().readNetworks();
        List<String> deleteNets = new ArrayList<>();
        for (CloudPluginSceneElementNetworkModel net : networks) {
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

