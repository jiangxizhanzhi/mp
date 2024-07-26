package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
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
        // 更新时不处理DYNAMIPS节点
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
}
