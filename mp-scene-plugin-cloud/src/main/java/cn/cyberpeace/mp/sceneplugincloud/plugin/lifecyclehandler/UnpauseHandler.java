 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UnpauseHandler.java.
 // File size 5377.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualDynamips;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.UnpauseTaskPool;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName UnpauseHandler
 * @Description
 * @Author maojun
 * @Date 2021/10/28
 * @Version 1.0.0
 * @see HandlerCommon
 */
@Slf4j
public class UnpauseHandler extends HandlerCommon {
    private final VirtualMachine virtualMachine = new VirtualMachine();
    private final Container container = new Container();
    private final VirtualDynamips virtualDynamips = new VirtualDynamips();

    public static class SceneProcess {
        public static final EventProcess UNPAUSE_TERMINALS = new EventProcess("unpause_terminals", "sceneplugincloud.event.unpause_terminals");
    }

    public static class ElementProcess {
        public static final EventProcess UNPAUSE_VIRTUAL_MACHINE = new EventProcess("unpause_virtual_machine", "sceneplugincloud.event.unpause_virtual_machine");
        public static final EventProcess UNPAUSE_CONTAINER = new EventProcess("unpause_container", "sceneplugincloud.event.unpause_container");
        public static final EventProcess UNPAUSE_NODE = new EventProcess("unpause_node", "sceneplugincloud.event.unpause_node");
        public static final EventProcess UNPAUSE_DYNAMIPS = new EventProcess("unpause_dynamips", "sceneplugincloud.event.unpause_dynamips");
    }

    /**
     * UnpauseHandler
     *
     * @param pluginHandler
     * @param event
     */
    public UnpauseHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, UnpauseTaskPool.getInstance(), event);
    }

    /**
     * unpause
     */
    public void unpause() {
        prepareEventProcess(SceneModel.Event.UNPAUSE, SceneProcess.class, ElementProcess.class);
        List<Runnable> tasks = new ArrayList<>();
        for (CloudPluginSceneElementBO e : getPluginHandler().getElementMap().values()) {
            if (this.getPluginHandler().getSceneHandler().isHandleNode(e.getSrcElement().getId())) {
                Runnable runnable = () -> this.unpauseElement(e);
                tasks.add(runnable);
            }
        }
        runSceneProgress(SceneProcess.UNPAUSE_TERMINALS, () -> runTasks(tasks));
    }

    /**
     * unpauseElement
     *
     * @param element
     */
    public void unpauseElement(CloudPluginSceneElementBO element) {
        String nodeId = element.getSrcElement().getId();
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(),
                    element.getSrcElement().getId());
            return;
        }
        if (element.getSrcElement().getEvent() != null && element.getSrcElement().getEvent() == SceneElementModel.Event.DEFAULT) {
            // 未启动状态无需暂停
            return;
        }
        String instanceId = element.getResourceId();
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.UNPAUSE, Arrays.asList(ElementProcess.UNPAUSE_VIRTUAL_MACHINE));
            runElementProgress(nodeId, SceneElementModel.Event.UNPAUSE, ElementProcess.UNPAUSE_VIRTUAL_MACHINE, () -> {
                virtualMachine.unpause(instanceId);
                checkAccessModes(element);
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.UNPAUSE, Arrays.asList(ElementProcess.UNPAUSE_CONTAINER));
            runElementProgress(nodeId, SceneElementModel.Event.UNPAUSE, ElementProcess.UNPAUSE_CONTAINER, () -> {
                container.unPause(instanceId);
                checkAccessModes(element);
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.UNPAUSE, Arrays.asList(ElementProcess.UNPAUSE_DYNAMIPS));
            runElementProgress(nodeId, SceneElementModel.Event.UNPAUSE, ElementProcess.UNPAUSE_DYNAMIPS, () -> {
            });
        } else {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.UNPAUSE, Arrays.asList(ElementProcess.UNPAUSE_NODE));
            runElementProgress(nodeId, SceneElementModel.Event.UNPAUSE, ElementProcess.UNPAUSE_NODE, () -> {
            });
        }
    }
}
