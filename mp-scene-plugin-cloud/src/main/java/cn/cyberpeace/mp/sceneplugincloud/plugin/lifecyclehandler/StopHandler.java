package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualDynamips;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.StopTaskPool;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler
 * @Author: hhuang
 * @CreateTime: 2023/04/27  15:18
 * @Description: 关闭节点处理
 * @Version: 1.0
 */
@Slf4j
public class StopHandler extends HandlerCommon{
    //虚拟机云平台工具类
    private final VirtualMachine virtualMachine = new VirtualMachine();
    //容器云平台工具类
    private final Container container = new Container();
    private final VirtualDynamips virtualDynamips = new VirtualDynamips();


    public static class ElementProcess {
        //关闭虚拟机
        public static final EventProcess STOP_VIRTUAL_MACHINE = new EventProcess("stop_virtual_machine", "sceneplugincloud.event.stop_virtual_machine");
        //关闭容器
        public static final EventProcess STOP_CONTAINER = new EventProcess("stop_container", "sceneplugincloud.event.stop_container");
        //关闭节点
        public static final EventProcess STOP_NODE = new EventProcess("stop_node", "sceneplugincloud.event.stop_node");
        public static final EventProcess STOP_DYNAMIPS = new EventProcess("stop_dynamips", "sceneplugincloud.event.stop_dynamips");
    }

    public StopHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, StopTaskPool.getInstance(), event);
    }

    /**
     * @description: 节点关闭
     * @param element
     * @return void
     */
    public void stopElement(CloudPluginSceneElementBO element) {
        //获取节点id
        String nodeId = element.getSrcElement().getId();
        //获取虚拟化类型
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(),
                element.getSrcElement().getId());
            return;
        }
        //实例id
        String instanceId = element.getResourceId();
        //处理虚拟机，容器等
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.STOP, Arrays.asList(
                StopHandler.ElementProcess.STOP_VIRTUAL_MACHINE));
            runElementProgress(nodeId, SceneElementModel.Event.STOP, StopHandler.ElementProcess.STOP_VIRTUAL_MACHINE, () -> {
                //关闭接入方式可用
                closeAccessModes(element);
                virtualMachine.stop(instanceId);
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.STOP, Arrays.asList(
                StopHandler.ElementProcess.STOP_CONTAINER));
            runElementProgress(nodeId, SceneElementModel.Event.STOP, StopHandler.ElementProcess.STOP_CONTAINER, () -> {
                //关闭接入方式可用
                closeAccessModes(element);
                container.stop(instanceId);
            });
        }else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.STOP, Arrays.asList(
                    StopHandler.ElementProcess.STOP_DYNAMIPS));
            runElementProgress(nodeId, SceneElementModel.Event.STOP, StopHandler.ElementProcess.STOP_DYNAMIPS, () -> {
                //关闭接入方式可用
                virtualDynamips.stop(instanceId);
            });
        } else {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.STOP, Arrays.asList(
                StopHandler.ElementProcess.STOP_NODE));
            runElementProgress(nodeId, SceneElementModel.Event.STOP, StopHandler.ElementProcess.STOP_NODE, () -> {
            });
        }
    }
}
