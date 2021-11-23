package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.UnpauseTaskPool;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/28
 */
@Slf4j
public class UnpauseHandler extends HandlerCommon {
    private final VirtualMachine virtualMachine = new VirtualMachine();
    private final Container container = new Container();

    public static class SceneProcess {
        public static final EventProcess UNPAUSE_TERMINALS = new EventProcess("unpause_terminals", "sceneplugincloud.event.unpause_terminals");
    }

    public static class ElementProcess {
        public static final EventProcess UNPAUSE_VIRTUAL_MACHINE = new EventProcess("unpause_virtual_machine", "sceneplugincloud.event.unpause_virtual_machine");
        public static final EventProcess UNPAUSE_CONTAINER = new EventProcess("unpause_container", "sceneplugincloud.event.unpause_container");
    }

    public UnpauseHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, UnpauseTaskPool.getInstance(), event);
    }

    public void unpause() {
        prepareEventProcess(SceneModel.Event.UNPAUSE, SceneProcess.class, ElementProcess.class);
        List<Runnable> tasks = getPluginHandler().getElementMap().values().stream()
                .map(element -> (Runnable) () -> this.unpauseElement(element))
                .collect(Collectors.toList());
        runSceneProgress(SceneProcess.UNPAUSE_TERMINALS, () -> runTasks(tasks));
    }

    public void unpauseElement(CloudPluginSceneElementBO element) {
        String nodeId = element.getSrcElement().getId();
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(),
                    element.getSrcElement().getId());
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
                container.lifeControl(instanceId, "unpause");
                checkAccessModes(element);
            });
        }
    }
}
