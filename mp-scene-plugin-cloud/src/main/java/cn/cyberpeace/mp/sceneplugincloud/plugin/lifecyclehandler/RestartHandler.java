package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RestartTaskPool;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/28
 */
@Slf4j
public class RestartHandler extends HandlerCommon {
    private final VirtualMachine virtualMachine = new VirtualMachine();
    private final Container container = new Container();

    public static class ElementProcess {
        public static final EventProcess RESTART_VIRTUAL_MACHINE = new EventProcess("restart_virtual_machine", "sceneplugincloud.event.restart_virtual_machine");
        public static final EventProcess RESTART_CONTAINER = new EventProcess("restart_container", "sceneplugincloud.event.restart_container");
    }

    public RestartHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, RestartTaskPool.getInstance(), event);
    }

    public void restartElement(CloudPluginSceneElementBO element) {
        String nodeId = element.getSrcElement().getId();
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(),
                    element.getSrcElement().getId());
            return;
        }
        String instanceId = element.getResourceId();
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.RESTART, Arrays.asList(ElementProcess.RESTART_VIRTUAL_MACHINE));
            runElementProgress(nodeId, SceneElementModel.Event.RESTART, ElementProcess.RESTART_VIRTUAL_MACHINE, () -> {
                closeAccessModes(element);
                virtualMachine.lifeControl(instanceId, "reboot");
                checkAccessModes(element);
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.RESTART, Arrays.asList(ElementProcess.RESTART_CONTAINER));
            runElementProgress(nodeId, SceneElementModel.Event.RESTART, ElementProcess.RESTART_CONTAINER, () -> {
                closeAccessModes(element);
                container.lifeControl(instanceId, "reboot");
                checkAccessModes(element);
            });
        }
    }
}
