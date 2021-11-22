package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.ResumeTaskPool;
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
public class ResumeHandler extends HandlerCommon {
    private final VirtualMachine virtualMachine = new VirtualMachine();

    public static class SceneProcess {
        public static final EventProcess RESUME_TERMINALS = new EventProcess("resume_terminals", "sceneplugincloud.event.resume_terminals");
    }

    public static class ElementProcess {
        public static final EventProcess RESUME_VIRTUAL_MACHINE = new EventProcess("resume_virtual_machine", "sceneplugincloud.event.resume_virtual_machine");
    }

    public ResumeHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, ResumeTaskPool.getInstance(), event);
    }

    public void resume() {
        prepareEventProcess(SceneModel.Event.RESUME, SceneProcess.class, ElementProcess.class);
        List<Runnable> tasks = getPluginHandler().getElementMap().values().stream()
                .map(element -> (Runnable) () -> this.resumeElement(element))
                .collect(Collectors.toList());
        runSceneProgress(SceneProcess.RESUME_TERMINALS, () -> runTasks(tasks));
    }

    public void resumeElement(CloudPluginSceneElementBO element) {
        String nodeId = element.getSrcElement().getId();
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(),
                    element.getSrcElement().getId());
            return;
        }
        String instanceId = element.getResourceId();
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.RESUME, Arrays.asList(ElementProcess.RESUME_VIRTUAL_MACHINE));
            runElementProgress(nodeId, SceneElementModel.Event.RESUME, ElementProcess.RESUME_VIRTUAL_MACHINE, () -> {
                virtualMachine.resume(instanceId);
                checkAccessModes(element);
            });
        }
    }
}
