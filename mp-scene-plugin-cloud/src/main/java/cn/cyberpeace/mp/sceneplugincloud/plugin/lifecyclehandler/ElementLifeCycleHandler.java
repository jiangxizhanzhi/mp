package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.constant.ElementLifeCycleEnum;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.ILifeCycleControlHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.LifeCycleControlTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/21
 */
@Slf4j
public class ElementLifeCycleHandler extends HandlerCommon {
    private final ILifeCycleControlHandler vmControl = new VirtualMachine();
    private final ILifeCycleControlHandler containerControl = new Container();

    public static class SceneProcess {

    }

    public static class ElementProcess {
        public static final EventProcess RESTART_VIRTUAL_MACHINE = new EventProcess("restart_virtual_machine", "sceneplugincloud.event.restart_virtual_machine");
        public static final EventProcess PAUSE_VIRTUAL_MACHINE = new EventProcess("pause_virtual_machine", "sceneplugincloud.event.pause_virtual_machine");
        public static final EventProcess UNPAUSE_VIRTUAL_MACHINE = new EventProcess("unpause_virtual_machine", "sceneplugincloud.event.unpause_virtual_machine");

        public static final EventProcess RESTART_CONTAINER = new EventProcess("restart_container", "sceneplugincloud.event.restart_container");
        public static final EventProcess PAUSE_CONTAINER = new EventProcess("pause_container", "sceneplugincloud.event.pause_container");
        public static final EventProcess UNPAUSE_CONTAINER = new EventProcess("unpause_container", "sceneplugincloud.event.unpause_container");
    }

    private static final Map<ElementLifeCycleEnum, EventProcess> TERMINAL_ELEMENT_EVENTPROCESS = new HashMap<>() {{
        this.put(ElementLifeCycleEnum.RESTART, ElementProcess.RESTART_VIRTUAL_MACHINE);
        this.put(ElementLifeCycleEnum.PAUSE, ElementProcess.PAUSE_VIRTUAL_MACHINE);
        this.put(ElementLifeCycleEnum.UNPAUSE, ElementProcess.UNPAUSE_VIRTUAL_MACHINE);
    }};

    private static final Map<ElementLifeCycleEnum, EventProcess> CONTAINER_ELEMENT_EVENTPROCESS = new HashMap<>() {{
        this.put(ElementLifeCycleEnum.RESTART, ElementProcess.RESTART_CONTAINER);
        this.put(ElementLifeCycleEnum.PAUSE, ElementProcess.PAUSE_CONTAINER);
        this.put(ElementLifeCycleEnum.UNPAUSE, ElementProcess.UNPAUSE_CONTAINER);
    }};

    public ElementLifeCycleHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, LifeCycleControlTaskPool.getInstance(), event);
    }

    public boolean lifeControl(String nodeId, @NonNull CloudPluginSceneElementBO element, ElementLifeCycleEnum lifeCycle) {
        // 准备参数
        Integer virtrualType = element.getVirtualType();
        EventProcess eventProcess = this.getElementEventProcess(element.getVirtualType(), lifeCycle);
        ILifeCycleControlHandler lifeCycleControlHandler = this.getLifeCycleController(element.getVirtualType());
        // 校验参数
        if (!this.check(nodeId, lifeCycle.getAction(), virtrualType, eventProcess, lifeCycleControlHandler)) {
            return false;
        }
        // 执行事件
        getPluginHandler().prepareElementProcess(nodeId, lifeCycle.getEvent(), Arrays.asList(eventProcess));
        runElementProgress(nodeId, eventProcess, () -> lifeCycleControlHandler.lifeControl(element.getResourceId(), lifeCycle.getAction()));
        return true;
    }

    private boolean check(String nodeId, String aciton, Integer virtrualType, EventProcess eventProcess, ILifeCycleControlHandler lifeCycleControlHandler) {
        if (virtrualType != null && eventProcess != null && lifeCycleControlHandler != null) {
            return true;
        }
        if (virtrualType == null) {
            log.error("no virtrualType for scene:{},node:{}", getPluginHandler().getSceneHandler().getScene().getEid(), nodeId);
        }
        if (eventProcess == null) {
            log.error("no EventProcess for scene:{},node:{},action:{}", getPluginHandler().getSceneHandler().getScene().getEid(), nodeId, aciton);
        }
        if (eventProcess == null) {
            log.error("no LifeCycleControl for scene:{},node:{},action:{}", getPluginHandler().getSceneHandler().getScene().getEid(), nodeId, aciton);
        }
        return false;
    }

    private EventProcess getElementEventProcess(int virtualType, ElementLifeCycleEnum lifeCycle) {
        if (BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE == virtualType) {
            return TERMINAL_ELEMENT_EVENTPROCESS.get(lifeCycle);
        } else if (BaseCloudPluginSceneElementModel.VirtualType.CONTAINER == virtualType) {
            return CONTAINER_ELEMENT_EVENTPROCESS.get(lifeCycle);
        } else {
            return null;
        }
    }

    private ILifeCycleControlHandler getLifeCycleController(int virtualType) {
        if (BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE == virtualType) {
            return this.vmControl;
        } else if (BaseCloudPluginSceneElementModel.VirtualType.CONTAINER == virtualType) {
            return this.containerControl;
        } else {
            return null;
        }
    }

}
