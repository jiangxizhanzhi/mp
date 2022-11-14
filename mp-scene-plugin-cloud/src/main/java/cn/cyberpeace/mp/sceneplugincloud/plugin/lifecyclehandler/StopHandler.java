package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.ResumeTaskPool;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/28
 */
@Slf4j
public class StopHandler extends HandlerCommon {

    private final VirtualMachine virtualMachine = new VirtualMachine();

    private final Container container = new Container();

    public static class SceneProcess {
        public static final EventProcess STOP_TERMINALS = new EventProcess("stop_terminals", "sceneplugincloud.event.stop_terminals");
    }

    public static class ElementProcess {
        public static final EventProcess STOP_VIRTUAL_MACHINE = new EventProcess("stop_virtual_machine", "sceneplugincloud.event.stop_virtual_machine");
        public static final EventProcess STOP_CONTAINER = new EventProcess("stop_container", "sceneplugincloud.event.stop_container");
        public static final EventProcess STOP_NODE = new EventProcess("stop_node", "sceneplugincloud.event.stop_node");
    }

    public StopHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, ResumeTaskPool.getInstance(), event);
    }

    public void stopElement(CloudPluginSceneElementBO element) {
        String nodeId = element.getSrcElement().getId();
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(),
                    element.getSrcElement().getId());
            return;
        }
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.STOP, Arrays.asList(ElementProcess.STOP_VIRTUAL_MACHINE));
            runElementProgress(nodeId, SceneElementModel.Event.STOP, ElementProcess.STOP_VIRTUAL_MACHINE, () -> {
                virtualMachine.stop(element.getResourceId());
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.STOP, Arrays.asList(ElementProcess.STOP_CONTAINER));
            runElementProgress(nodeId, SceneElementModel.Event.STOP, ElementProcess.STOP_CONTAINER, () -> {
                container.stop(element.getResourceId());
            });
        } else {
            runElementProgress(nodeId, SceneElementModel.Event.STOP, ElementProcess.STOP_NODE, () -> {

            });
        }
    }
}
