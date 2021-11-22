package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.SuspendTaskPool;
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
public class SuspendHandler extends HandlerCommon {
    private final VirtualMachine virtualMachine = new VirtualMachine();

    public static class SceneProcess {
        public static final EventProcess SUSPEND_TERMINALS = new EventProcess("suspend_terminals", "sceneplugincloud.event.suspend_terminals");
    }

    public static class ElementProcess {
        public static final EventProcess SUSPEND_VIRTUAL_MACHINE = new EventProcess("suspend_virtual_machine", "sceneplugincloud.event.suspend_virtual_machine");
    }

    public SuspendHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, SuspendTaskPool.getInstance(), event);
    }

    public void suspend() {
        prepareEventProcess(SceneModel.Event.SUSPEND, SceneProcess.class, ElementProcess.class);
        List<Runnable> tasks = getPluginHandler().getElementMap().values().stream()
                .map(element -> (Runnable) () -> this.suspendElement(element))
                .collect(Collectors.toList());
        runSceneProgress(SceneProcess.SUSPEND_TERMINALS, () -> runTasks(tasks));
    }

    public void suspendElement(CloudPluginSceneElementBO element) {
        String nodeId = element.getSrcElement().getId();
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(),
                    element.getSrcElement().getId());
            return;
        }
        String instanceId = element.getResourceId();
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.SUSPEND, Arrays.asList(ElementProcess.SUSPEND_VIRTUAL_MACHINE));
            runElementProgress(nodeId, SceneElementModel.Event.SUSPEND, ElementProcess.SUSPEND_VIRTUAL_MACHINE, () -> {
                virtualMachine.suspend(instanceId);
                checkAccessModes(element);
            });
        }
    }
}
