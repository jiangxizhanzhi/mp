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
public class StartHandler extends HandlerCommon {

    private final VirtualMachine virtualMachine = new VirtualMachine();

    private final Container container = new Container();

    public static class SceneProcess {
        public static final EventProcess START_TERMINALS = new EventProcess("start_terminals", "sceneplugincloud.event.start_terminals");
    }

    public static class ElementProcess {
        public static final EventProcess START_VIRTUAL_MACHINE = new EventProcess("start_virtual_machine", "sceneplugincloud.event.start_virtual_machine");
        public static final EventProcess START_CONTAINER = new EventProcess("start_container", "sceneplugincloud.event.start_container");
        public static final EventProcess START_NODE = new EventProcess("start_node", "sceneplugincloud.event.start_node");
    }

    public StartHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, ResumeTaskPool.getInstance(), event);
    }

    public void startElement(CloudPluginSceneElementBO element) {
        String nodeId = element.getSrcElement().getId();
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(),
                    element.getSrcElement().getId());
            return;
        }
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.START, Arrays.asList(ElementProcess.START_VIRTUAL_MACHINE));
            runElementProgress(nodeId, SceneElementModel.Event.START, ElementProcess.START_VIRTUAL_MACHINE, () -> {
                virtualMachine.start(element.getResourceId());
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.START, Arrays.asList(ElementProcess.START_CONTAINER));
            runElementProgress(nodeId, SceneElementModel.Event.START, ElementProcess.START_CONTAINER, () -> {
                container.start(element.getResourceId());
            });
        } else {
            runElementProgress(nodeId, SceneElementModel.Event.START, ElementProcess.START_NODE, () -> {

            });
        }
    }
}
