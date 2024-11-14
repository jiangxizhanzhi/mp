package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.auth.model.MicrosoftAzureUserModel;
import cn.cyberpeace.mp.auth.repository.MicrosoftAzureLoginMapper;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
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
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.AttachEntityUsbsData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.AttachVirtualUsbsData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineAttachISOData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.StartTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.repository.DevopsLoadSourceMapper;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler
 * @Author: hhuang
 * @CreateTime: 2023/04/27  11:07
 * @Description: 节点启动处理
 * @Version: 1.0
 */
@Slf4j
public class StartHandler extends HandlerCommon {

    //虚拟机云平台工具类
    private final VirtualMachine virtualMachine = new VirtualMachine();
    //容器云平台工具类
    private final Container container = new Container();
    private final VirtualDynamips virtualDynamips = new VirtualDynamips();

    private String azureManagerUrl;

    private MicrosoftAzureLoginMapper sceneElementPluginConfigMapper;


    //节点处理过程
    public static class ElementProcess {
        //启动虚拟机
        public static final EventProcess START_VIRTUAL_MACHINE = new EventProcess("start_virtual_machine", "sceneplugincloud.event.start_virtual_machine");
        //启动容器
        public static final EventProcess START_CONTAINER = new EventProcess("start_container", "sceneplugincloud.event.start_container");
        //启动节点
        public static final EventProcess START_NODE = new EventProcess("start_node", "sceneplugincloud.event.start_node");
        public static final EventProcess START_DYNAMIPS = new EventProcess("start_dynamips", "sceneplugincloud.event.start_dynamips");
        public static final EventProcess START_LOAD = new EventProcess("start_load", "sceneplugincloud.event.start_load");
    }

    public StartHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, StartTaskPool.getInstance(), event);
        this.azureManagerUrl = BeanCommon.getBean(SituationAwareConfig.class).getAzureManagerUrl();
        this.sceneElementPluginConfigMapper = BeanCommon.getBean(MicrosoftAzureLoginMapper.class);
    }


    /**
     * @param element
     * @return void
     * @description: 节点启动处理类
     */
    public void startElement(CloudPluginSceneElementBO element) {        //获取节点id
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

        Integer cloudType = getPluginHandler().getSceneHandler().getScene().getCloudType();
        if (cloudType != BaseSceneModel.CloudType.DEFAULT) {
            if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.START, Arrays.asList(
                        StartHandler.ElementProcess.START_VIRTUAL_MACHINE));
                runElementProgress(nodeId, SceneElementModel.Event.START, StartHandler.ElementProcess.START_VIRTUAL_MACHINE, () -> {
                    //启动
                        if (cloudType == BaseSceneModel.CloudType.PUBLIC) {
                        AzureVirtualMachine azureVirtualMachine = new AzureVirtualMachine(IAzureResourceManager.getInstance(cloudType));
                        azureVirtualMachine.start(element);
                    } else {
                        JSONObject jsonObject = new JSONObject();
                        //云类型(1: 微软公有云 2:微软私有云 3:微软混合云)
                        jsonObject.put("cloudType", cloudType);
                        MicrosoftAzureUserModel localAzurer = sceneElementPluginConfigMapper.getLocalAzurer(jsonObject);
                        String url = azureManagerUrl + "/cloud/vm_start";
                        Map<String, Object> formMap = new HashMap();
                        formMap.put("azureUser", localAzurer);
                        formMap.put("isPublicIp",element.getIsPublicIp());
                        formMap.put("vmId", element.getResourceId());
                        //请求头设置
                        HashMap<String, String> headersMap = new HashMap<>();
                        String body = JsonUtils.stringify(formMap);
                        String result;
                        try {
                            result = HttpUtils.sendHttpPost(url, body, headersMap);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        Map<String, Object> dataMap = JsonUtils.parseMap(result);
                        if (BaseErrorCode.SUCCESS.getCode().equals(dataMap.get("code"))) {
                            Map<String, Object> data = JsonUtils.beanToSnakeMap(dataMap.get("data"));
                            Object publicIp = data.get("public_ip");
                            if (element.getIsPublicIp()) {
                                element.setPublicIpAddress(String.valueOf(publicIp));
                            }
                        }
                    }
                    openAccessModes(element);
                });

            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.START, Arrays.asList(
                        StartHandler.ElementProcess.START_CONTAINER));
                runElementProgress(nodeId, SceneElementModel.Event.START, StartHandler.ElementProcess.START_CONTAINER, () -> {
                    //启动
                    AzureContainer azureContainer = new AzureContainer(IAzureResourceManager.getInstance(cloudType));
                    azureContainer.start(instanceId);
                    //检查接入方式是否可用
//                    checkAccessModes(element);
                    openAccessModes(element);
                });
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.LOAD)) {
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.START, Arrays.asList(
                        StartHandler.ElementProcess.START_LOAD));
                runElementProgress(nodeId, SceneElementModel.Event.START, StartHandler.ElementProcess.START_LOAD, () -> {
                    //启动
                    DevopsLoadSourceMapper devopsLoadSourceMapper = BeanCommon.getBean(DevopsLoadSourceMapper.class);
                    DevopsLoadSourceModel synchronousDataSource = devopsLoadSourceMapper.getSynchronousDataSource();
                    Load load = new Load(synchronousDataSource);

                    load.start(element.getLoadResourceInstanceId());
                });
            }
        } else {
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
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS)) {
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
