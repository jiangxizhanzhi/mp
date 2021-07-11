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


@Slf4j
public class StopHandler extends HandlerCommon{
    
    private final VirtualMachine virtualMachine = new VirtualMachine();
    
    private final Container container = new Container();
    private final VirtualDynamips virtualDynamips = new VirtualDynamips();


    public static class ElementProcess {
        
        public static final EventProcess STOP_VIRTUAL_MACHINE = new EventProcess("stop_virtual_machine", "sceneplugincloud.event.stop_virtual_machine");
        
        public static final EventProcess STOP_CONTAINER = new EventProcess("stop_container", "sceneplugincloud.event.stop_container");
        
        public static final EventProcess STOP_NODE = new EventProcess("stop_node", "sceneplugincloud.event.stop_node");
        public static final EventProcess STOP_DYNAMIPS = new EventProcess("stop_dynamips", "sceneplugincloud.event.stop_dynamips");
    }

    public StopHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, StopTaskPool.getInstance(), event);
    }

    
    public void stopElement(CloudPluginSceneElementBO element) {
        
        String nodeId = element.getSrcElement().getId();
        
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(),
                element.getSrcElement().getId());
            return;
        }
        
        String instanceId = element.getResourceId();
        
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.STOP, Arrays.asList(
                StopHandler.ElementProcess.STOP_VIRTUAL_MACHINE));
            runElementProgress(nodeId, SceneElementModel.Event.STOP, StopHandler.ElementProcess.STOP_VIRTUAL_MACHINE, () -> {
                
                closeAccessModes(element);
                virtualMachine.stop(instanceId);
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.STOP, Arrays.asList(
                StopHandler.ElementProcess.STOP_CONTAINER));
            runElementProgress(nodeId, SceneElementModel.Event.STOP, StopHandler.ElementProcess.STOP_CONTAINER, () -> {
                
                closeAccessModes(element);
                container.stop(instanceId);
            });
        }else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.STOP, Arrays.asList(
                    StopHandler.ElementProcess.STOP_DYNAMIPS));
            runElementProgress(nodeId, SceneElementModel.Event.STOP, StopHandler.ElementProcess.STOP_DYNAMIPS, () -> {
                
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
