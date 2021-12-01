package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.PauseTaskPool;
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
public class PauseHandler extends HandlerCommon {
    private final VirtualMachine virtualMachine = new VirtualMachine();
    private final Container container = new Container();

    public static class SceneProcess {
        public static final EventProcess PAUSE_TERMINALS = new EventProcess("pause_terminals", "sceneplugincloud.event.pause_terminals");
    }

    public static class ElementProcess {
        public static final EventProcess PAUSE_VIRTUAL_MACHINE = new EventProcess("pause_virtual_machine", "sceneplugincloud.event.pause_virtual_machine");
        public static final EventProcess PAUSE_CONTAINER = new EventProcess("pause_container", "sceneplugincloud.event.pause_container");
    }

    public PauseHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, PauseTaskPool.getInstance(), event);
    }

    public void pause() {
        prepareEventProcess(SceneModel.Event.PAUSE, SceneProcess.class, ElementProcess.class);
        List<Runnable> tasks = getPluginHandler().getElementMap().values().stream()
                .map(element -> (Runnable) () -> this.pauseElement(element))
                .collect(Collectors.toList());
        runSceneProgress(SceneProcess.PAUSE_TERMINALS, () -> runTasks(tasks));
    }

    public void pauseElement(CloudPluginSceneElementBO element) {
        String nodeId = element.getSrcElement().getId();
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(),
                    element.getSrcElement().getId());
            return;
        }
        String instanceId = element.getResourceId();
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.PAUSE, Arrays.asList(ElementProcess.PAUSE_VIRTUAL_MACHINE));
            runElementProgress(nodeId, SceneElementModel.Event.PAUSE, ElementProcess.PAUSE_VIRTUAL_MACHINE, () -> {
                closeAccessModes(element);
                virtualMachine.pause(instanceId);
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.PAUSE, Arrays.asList(ElementProcess.PAUSE_CONTAINER));
            runElementProgress(nodeId, SceneElementModel.Event.PAUSE, ElementProcess.PAUSE_CONTAINER, () -> {
                closeAccessModes(element);
                container.pause(instanceId);
            });
        }
    }
}
