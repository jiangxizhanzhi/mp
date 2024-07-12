 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RestartHandler.java.
 // File size 5362.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualDynamips;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ConsoleInfoGetResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineAttachISOData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RestartTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @ClassName RestartHandler
 * @Description
 * @Author maojun
 * @Date 2021/10/28
 * @Version 1.0.0
 * @see HandlerCommon
 */
@Slf4j
public class RestartHandler extends HandlerCommon {
    private final VirtualMachine virtualMachine = new VirtualMachine();
    private final Container container = new Container();
    private final VirtualDynamips virtualDynamips = new VirtualDynamips();

    public static class ElementProcess {
        public static final EventProcess RESTART_VIRTUAL_MACHINE = new EventProcess("restart_virtual_machine", "sceneplugincloud.event.restart_virtual_machine");
        public static final EventProcess RESTART_CONTAINER = new EventProcess("restart_container", "sceneplugincloud.event.restart_container");
        public static final EventProcess RESTART_DYNAMIPS = new EventProcess("restart_dynamips", "sceneplugincloud.event.restart_dynamips");
    }

    /**
     * RestartHandler
     *
     * @param pluginHandler
     * @param event
     */
    public RestartHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, RestartTaskPool.getInstance(), event);
    }

    /**
     * restartElement
     *
     * @param element
     */
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
                // 先判断下虚拟机的真实状态
                ConsoleInfoGetResult info = virtualMachine.getConsoleInfo(instanceId, element.getHost());
                if (StringUtils.isNotEmpty(info.getPort()) && Integer.parseInt(info.getPort()) < 0) {
                    virtualMachine.stop(instanceId);
                    virtualMachine.start(instanceId);
                } else {
                    virtualMachine.restart(instanceId);
                }
                if (StringUtils.isNotEmpty(element.getIsoAttachFilename()) && StringUtils.isNotEmpty(element.getIsoTmpdirUuid())) {
                    this.attachISO(element);
                }
                checkAccessModes(element);
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.RESTART, Arrays.asList(ElementProcess.RESTART_CONTAINER));
            runElementProgress(nodeId, SceneElementModel.Event.RESTART, ElementProcess.RESTART_CONTAINER, () -> {
                closeAccessModes(element);
                container.restart(instanceId);
                container.addRoute(instanceId);
                container.executeCommand(instanceId, "cd /opt/wazuha && sh start.sh");
                checkAccessModes(element);
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.RESTART, Arrays.asList(ElementProcess.RESTART_DYNAMIPS));
            runElementProgress(nodeId, SceneElementModel.Event.RESTART, ElementProcess.RESTART_DYNAMIPS, () -> {
            });
        }
    }

    /**
     * attachISO
     *
     * @param cloudElement
     */
    private void attachISO(CloudPluginSceneElementBO cloudElement) {
        VirtualMachineAttachISOData data = new VirtualMachineAttachISOData();
        data.setInstanceUuid(cloudElement.getResourceId());
        data.setFileName(cloudElement.getIsoAttachFilename());
        data.setTmpdirUuid(cloudElement.getIsoTmpdirUuid());
        virtualMachine.attachISO(data);
    }
}
