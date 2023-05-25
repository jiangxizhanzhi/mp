package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualDynamips;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.StartTaskPool;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler
 * @Author: hhuang
 * @CreateTime: 2023/04/27  11:07
 * @Description: 节点启动处理
 * @Version: 1.0
 */
@Slf4j
public class StartHandler extends HandlerCommon{

    //虚拟机云平台工具类
    private final VirtualMachine virtualMachine = new VirtualMachine();
    //容器云平台工具类
    private final Container container = new Container();
    private final VirtualDynamips virtualDynamips = new VirtualDynamips();


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

    public StartHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, StartTaskPool.getInstance(), event);
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
                virtualDynamips.start(instanceId);
            });
        } else {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.START, Arrays.asList(
                StartHandler.ElementProcess.START_NODE));
            runElementProgress(nodeId, SceneElementModel.Event.START, StartHandler.ElementProcess.START_NODE, () -> {
            });
        }
    }


}
