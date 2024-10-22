package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualDynamips;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineAttachISOData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RestartTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.util.MultiCloudConfig;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpClient;
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
    private VirtualMachine virtualMachine ;
    private Container container ;
    private VirtualDynamips virtualDynamips ;

    public static class ElementProcess {
        public static final EventProcess RESTART_VIRTUAL_MACHINE = new EventProcess("restart_virtual_machine", "sceneplugincloud.event.restart_virtual_machine");
        public static final EventProcess RESTART_CONTAINER = new EventProcess("restart_container", "sceneplugincloud.event.restart_container");
        public static final EventProcess RESTART_DYNAMIPS = new EventProcess("restart_dynamips", "sceneplugincloud.event.restart_dynamips");
    }

    public RestartHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, RestartTaskPool.getInstance(), event);
        this.initCloudHttpClient();
    }

    private void initCloudHttpClient(){
        String cloudIndex = getPluginHandler().getScene().getCloudIndex();
        CloudHttpClient httpClient = MultiCloudConfig.get(cloudIndex);
        this.virtualMachine = new VirtualMachine(httpClient);
        this.container = new Container(httpClient);
        this.virtualDynamips = new VirtualDynamips(httpClient);
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

    private void attachISO(CloudPluginSceneElementBO cloudElement) {
        VirtualMachineAttachISOData data = new VirtualMachineAttachISOData();
        data.setInstanceUuid(cloudElement.getResourceId());
        data.setFileName(cloudElement.getIsoAttachFilename());
        data.setTmpdirUuid(cloudElement.getIsoTmpdirUuid());
        virtualMachine.attachISO(data);
    }
}
