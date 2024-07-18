package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.DevopsLoadSourceModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureContainer;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureVirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.Load;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualDynamips;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineAttachISOData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RestartTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.repository.DevopsLoadSourceMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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
    private final VirtualDynamips virtualDynamips = new VirtualDynamips();

    public static class ElementProcess {
        public static final EventProcess RESTART_VIRTUAL_MACHINE = new EventProcess("restart_virtual_machine", "sceneplugincloud.event.restart_virtual_machine");
        public static final EventProcess RESTART_CONTAINER = new EventProcess("restart_container", "sceneplugincloud.event.restart_container");
        public static final EventProcess RESTART_DYNAMIPS = new EventProcess("restart_dynamips", "sceneplugincloud.event.restart_dynamips");
        public static final EventProcess RESTART_LOAD = new EventProcess("restart_load", "sceneplugincloud.event.restart_load");
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
        //        判断是否是微软云
        Integer cloudType = getPluginHandler().getSceneHandler().getScene().getCloudType();
        if (cloudType != BaseSceneModel.CloudType.DEFAULT) {
            if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.RESTART, Arrays.asList(ElementProcess.RESTART_VIRTUAL_MACHINE));
                runElementProgress(nodeId, SceneElementModel.Event.RESTART, ElementProcess.RESTART_VIRTUAL_MACHINE, () -> {
                    closeAccessModes(element);
                    AzureVirtualMachine azureVirtualMachine = new AzureVirtualMachine(IAzureResourceManager.getInstance(cloudType));
                    azureVirtualMachine.restart(instanceId);
//                    TODO 云场景连接用的公网ip 这里用私有ip测不通 暂去
//                    checkAccessModes(element);

                    openAccessModes(element);
                });
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.RESTART, Arrays.asList(ElementProcess.RESTART_CONTAINER));
                runElementProgress(nodeId, SceneElementModel.Event.RESTART, ElementProcess.RESTART_CONTAINER, () -> {
                    closeAccessModes(element);
                    AzureContainer azureContainer = new AzureContainer(IAzureResourceManager.getInstance(cloudType));
                    azureContainer.restart(instanceId);
                    openAccessModes(element);
//                    container.addRoute(instanceId);
//                    container.executeCommand(instanceId, "cd /opt/wazuha && sh start.sh");
//                    checkAccessModes(element);
                });
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.LOAD)) {
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.RESTART, Arrays.asList(ElementProcess.RESTART_LOAD));
                runElementProgress(nodeId, SceneElementModel.Event.RESTART, ElementProcess.RESTART_LOAD, () -> {
                    DevopsLoadSourceMapper devopsLoadSourceMapper = BeanCommon.getBean(DevopsLoadSourceMapper.class);
                    DevopsLoadSourceModel synchronousDataSource = devopsLoadSourceMapper.getSynchronousDataSource();
                    Load load = new Load(synchronousDataSource);
                    load.restart(element.getLoadResourceInstanceId());
                });
            }

        } else {
            if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.RESTART, Arrays.asList(ElementProcess.RESTART_VIRTUAL_MACHINE));
                runElementProgress(nodeId, SceneElementModel.Event.RESTART, ElementProcess.RESTART_VIRTUAL_MACHINE, () -> {
                    closeAccessModes(element);
                    // 先判断下虚拟机的真实状态
                    var info = virtualMachine.getConsoleInfo(instanceId, element.getHost());
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
                    virtualDynamips.restart(instanceId);
                });
            }
        }
    }

    private void attachISO(CloudPluginSceneElementBO cloudElement) {
        VirtualMachineAttachISOData data = new VirtualMachineAttachISOData();
        data.setInstanceUuid(cloudElement.getResourceId());
        data.setFileName(cloudElement.getIsoAttachFilename());
        data.setTmpdirUuid(cloudElement.getIsoTmpdirUuid());
        virtualMachine.attachISO(data);
    }
}
