package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class StartHandler extends HandlerCommon{

    
    private final VirtualMachine virtualMachine = new VirtualMachine();
    
    private final Container container = new Container();
    private final VirtualDynamips virtualDynamips = new VirtualDynamips();


    
    public static class ElementProcess {
        
        public static final EventProcess START_VIRTUAL_MACHINE = new EventProcess("start_virtual_machine", "sceneplugincloud.event.start_virtual_machine");
        
        public static final EventProcess START_CONTAINER = new EventProcess("start_container", "sceneplugincloud.event.start_container");
        
        public static final EventProcess START_NODE = new EventProcess("start_node", "sceneplugincloud.event.start_node");
        public static final EventProcess START_DYNAMIPS = new EventProcess("start_dynamips", "sceneplugincloud.event.start_dynamips");
    }

    public StartHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, StartTaskPool.getInstance(), event);
    }


    
    public void startElement(CloudPluginSceneElementBO element) {
        
        String nodeId = element.getSrcElement().getId();
        
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(),
                element.getSrcElement().getId());
            return;
        }
        
        String instanceId = element.getResourceId();
        
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.START, Arrays.asList(
                StartHandler.ElementProcess.START_VIRTUAL_MACHINE));
            
            runElementProgress(nodeId, SceneElementModel.Event.START, StartHandler.ElementProcess.START_VIRTUAL_MACHINE, () -> {
                
                virtualMachine.start(instanceId);
                if (StringUtils.isNotEmpty(element.getIsoAttachFilename()) && StringUtils.isNotEmpty(element.getIsoTmpdirUuid())) {
                    this.attachISO(element);
                }
                
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(element.getVirtualUsbs())) {
                    this.attachVirtualUsbs(element);
                }
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(element.getEntityUsbs())) {
                    this.attachEntityUsbs(element);
                }
                
                checkAccessModes(element);
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.START, Arrays.asList(
                StartHandler.ElementProcess.START_CONTAINER));
            runElementProgress(nodeId, SceneElementModel.Event.START, StartHandler.ElementProcess.START_CONTAINER, () -> {
                
                container.start(instanceId);
                
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

    private void attachISO(CloudPluginSceneElementBO cloudElement) {
        VirtualMachineAttachISOData data = new VirtualMachineAttachISOData();
        data.setInstanceUuid(cloudElement.getResourceId());
        data.setFileName(cloudElement.getIsoAttachFilename());
        data.setTmpdirUuid(cloudElement.getIsoTmpdirUuid());
        virtualMachine.attachISO(data);
    }

    private void attachVirtualUsbs(CloudPluginSceneElementBO cloudElement) {
        AttachVirtualUsbsData data = new AttachVirtualUsbsData();
        data.setInstanceUuid(cloudElement.getResourceId());
        var usbUuids = cloudElement.getVirtualUsbs().stream().map(u -> u.getUuid()).collect(Collectors.toList());
        data.setVirtualUsbUuids(usbUuids);
        virtualMachine.attachVirtualUsbs(data);
    }

    private void attachEntityUsbs(CloudPluginSceneElementBO cloudElement) {
        AttachEntityUsbsData data = new AttachEntityUsbsData();
        data.setInstanceUuid(cloudElement.getResourceId());
        List<String> usbUuids = cloudElement.getEntityUsbs().stream().map(u -> u.getUuid()).collect(Collectors.toList());
        data.setEntityUsbUuids(usbUuids);
        virtualMachine.attachEntityUsbs(data);
    }

}
