 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name StartHandler.java.
 // File size 7590.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.EntityUsb;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.VirtualUsb;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualDynamips;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.AttachEntityUsbsData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.AttachVirtualUsbsData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineAttachISOData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.StartTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName StartHandler
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see HandlerCommon
 */
@Slf4j
public class StartHandler extends HandlerCommon{

    //虚拟机云平台工具类
    private final VirtualMachine virtualMachine = new VirtualMachine();
    //容器云平台工具类
    private final Container container = new Container();
    private final VirtualDynamips virtualDynamips = new VirtualDynamips();

    public static class SceneProcess {
        public static final EventProcess START_TERMINALS = new EventProcess("start_terminals", "sceneplugincloud.event.start_terminals");
    }

    //节点处理过程
    public static class ElementProcess {
        //启动虚拟机
        public static final EventProcess START_VIRTUAL_MACHINE = new EventProcess("start_virtual_machine", "sceneplugincloud.event.start_virtual_machine");
        //启动容器
        public static final EventProcess START_CONTAINER = new EventProcess("start_container", "sceneplugincloud.event.start_container");
        //启动节点
        public static final EventProcess START_NODE = new EventProcess("start_node", "sceneplugincloud.event.start_node");
        public static final EventProcess START_DYNAMIPS = new EventProcess("start_dynamips", "sceneplugincloud.event.start_dynamips");
    }

    /**
     * StartHandler
     *
     * @param pluginHandler
     * @param event
     */
    public StartHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, StartTaskPool.getInstance(), event);
    }

    public void start() {
        prepareEventProcess(SceneModel.Event.START, PauseHandler.SceneProcess.class, PauseHandler.ElementProcess.class);
        List<Runnable> tasks = new ArrayList<>();
        for (CloudPluginSceneElementBO e : getPluginHandler().getElementMap().values()) {
            if (this.getPluginHandler().getSceneHandler().isHandleNode(e.getSrcElement().getId())) {
                Runnable runnable = () -> this.startElement(e);
                tasks.add(runnable);
            }
        }
        runSceneProgress(SceneProcess.START_TERMINALS, () -> runTasks(tasks));
    }


    /**
     * @description: 节点启动处理类
     * @param element
     * @return void
     */
    public void startElement(CloudPluginSceneElementBO element) {
        //获取节点id
        String nodeId = element.getSrcElement().getId();
        //云平台虚拟化类型
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(),
                element.getSrcElement().getId());
            return;
        }
        //实例id
        String instanceId = element.getResourceId();
        //虚拟机，容器等启动
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            //前置过程
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.START, Arrays.asList(
                StartHandler.ElementProcess.START_VIRTUAL_MACHINE));
            //执行过程
            runElementProgress(nodeId, SceneElementModel.Event.START, StartHandler.ElementProcess.START_VIRTUAL_MACHINE, () -> {
                //启动
                virtualMachine.start(instanceId);
                if (StringUtils.isNotEmpty(element.getIsoAttachFilename()) && StringUtils.isNotEmpty(element.getIsoTmpdirUuid())) {
                    this.attachISO(element);
                }
                //挂载一下usb
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(element.getVirtualUsbs())) {
                    this.attachVirtualUsbs(element);
                }
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(element.getEntityUsbs())) {
                    this.attachEntityUsbs(element);
                }
                //检查接入方式是否可用
                checkAccessModes(element);
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.START, Arrays.asList(
                StartHandler.ElementProcess.START_CONTAINER));
            runElementProgress(nodeId, SceneElementModel.Event.START, StartHandler.ElementProcess.START_CONTAINER, () -> {
                //启动
                container.start(instanceId);
                //检查接入方式是否可用
                checkAccessModes(element);
            });
        }else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.START, Arrays.asList(
                    StartHandler.ElementProcess.START_DYNAMIPS));
            runElementProgress(nodeId, SceneElementModel.Event.START, StartHandler.ElementProcess.START_DYNAMIPS, () -> {
            });
        } else {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.START, Arrays.asList(
                StartHandler.ElementProcess.START_NODE));
            runElementProgress(nodeId, SceneElementModel.Event.START, StartHandler.ElementProcess.START_NODE, () -> {
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

    /**
     * attachVirtualUsbs
     *
     * @param cloudElement
     */
    private void attachVirtualUsbs(CloudPluginSceneElementBO cloudElement) {
        AttachVirtualUsbsData data = new AttachVirtualUsbsData();
        data.setInstanceUuid(cloudElement.getResourceId());
        List<String> usbUuids = new ArrayList<>();
        for (VirtualUsb u : cloudElement.getVirtualUsbs()) {
            String uuid = u.getUuid();
            usbUuids.add(uuid);
        }
        data.setVirtualUsbUuids(usbUuids);
        virtualMachine.attachVirtualUsbs(data);
    }

    /**
     * attachEntityUsbs
     *
     * @param cloudElement
     */
    private void attachEntityUsbs(CloudPluginSceneElementBO cloudElement) {
        AttachEntityUsbsData data = new AttachEntityUsbsData();
        data.setInstanceUuid(cloudElement.getResourceId());
        List<String> usbUuids = new ArrayList<>();
        for (EntityUsb u : cloudElement.getEntityUsbs()) {
            String uuid = u.getUuid();
            usbUuids.add(uuid);
        }
        data.setEntityUsbUuids(usbUuids);
        virtualMachine.attachEntityUsbs(data);
    }

}
