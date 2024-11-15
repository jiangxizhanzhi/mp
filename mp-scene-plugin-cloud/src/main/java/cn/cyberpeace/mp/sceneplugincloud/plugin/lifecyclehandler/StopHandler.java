package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.auth.model.MicrosoftAzureUserModel;
import cn.cyberpeace.mp.auth.repository.MicrosoftAzureLoginMapper;
import cn.cyberpeace.mp.base.common.BeanCommon;
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
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.StopTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.repository.DevopsLoadSourceMapper;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler
 * @Author: hhuang
 * @CreateTime: 2023/04/27  15:18
 * @Description: 关闭节点处理
 * @Version: 1.0
 */
@Slf4j
public class StopHandler extends HandlerCommon {
    //虚拟机云平台工具类
    private final VirtualMachine virtualMachine = new VirtualMachine();
    //容器云平台工具类
    private final Container container = new Container();
    private final VirtualDynamips virtualDynamips = new VirtualDynamips();

    private String mixAzureManagerUrl;
    private String privateAzureManagerUrl;

    private MicrosoftAzureLoginMapper sceneElementPluginConfigMapper;


    public static class ElementProcess {
        //关闭虚拟机
        public static final EventProcess STOP_VIRTUAL_MACHINE = new EventProcess("stop_virtual_machine", "sceneplugincloud.event.stop_virtual_machine");
        //关闭容器
        public static final EventProcess STOP_CONTAINER = new EventProcess("stop_container", "sceneplugincloud.event.stop_container");
        //关闭节点
        public static final EventProcess STOP_NODE = new EventProcess("stop_node", "sceneplugincloud.event.stop_node");
        public static final EventProcess STOP_DYNAMIPS = new EventProcess("stop_dynamips", "sceneplugincloud.event.stop_dynamips");
        //关闭载荷
        public static final EventProcess STOP_LOAD = new EventProcess("stop_load", "sceneplugincloud.event.stop_load");
    }

    public StopHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, StopTaskPool.getInstance(), event);
        this.mixAzureManagerUrl = BeanCommon.getBean(SituationAwareConfig.class).getMixAzureManagerUrl();
        this.privateAzureManagerUrl = BeanCommon.getBean(SituationAwareConfig.class).getPrivateAzureManagerUrl();
        this.sceneElementPluginConfigMapper = BeanCommon.getBean(MicrosoftAzureLoginMapper.class);
    }

    /**
     * @param element
     * @return void
     * @description: 节点关闭
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
        Integer cloudType = getPluginHandler().getSceneHandler().getScene().getCloudType();
        if (cloudType != BaseSceneModel.CloudType.DEFAULT) {
            if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.STOP, Arrays.asList(
                        StopHandler.ElementProcess.STOP_VIRTUAL_MACHINE));
                runElementProgress(nodeId, SceneElementModel.Event.STOP, StopHandler.ElementProcess.STOP_VIRTUAL_MACHINE, () -> {
                    //关闭接入方式可用
                    closeAccessModes(element);
                    if (cloudType == BaseSceneModel.CloudType.PUBLIC) {
                        AzureVirtualMachine azureVirtualMachine = new AzureVirtualMachine(IAzureResourceManager.getInstance(cloudType));
                        azureVirtualMachine.stop(instanceId);
                    } else {
                        JSONObject jsonObject = new JSONObject();
                        //云类型(1: 微软公有云 2:微软私有云 3:微软混合云)
                        jsonObject.put("cloudType", cloudType);
                        MicrosoftAzureUserModel localAzurer = sceneElementPluginConfigMapper.getLocalAzurer(jsonObject);
                        String azureManagerUrl = mixAzureManagerUrl;
                        if(cloudType == BaseSceneModel.CloudType.PRIVATE){
                            azureManagerUrl = privateAzureManagerUrl;
                        }
                        String url = azureManagerUrl + "/cloud/vm_stop";
                        Map<String, Object> formMap = new HashMap();
                        formMap.put("azureUser", localAzurer);
                        formMap.put("vmId", instanceId);
                        //请求头设置
                        HashMap<String, String> headersMap = new HashMap<>();
                        String body = JsonUtils.stringify(formMap);
                        try {
                            HttpUtils.sendHttpPost(url, body, headersMap);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                });
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.STOP, Arrays.asList(
                        StopHandler.ElementProcess.STOP_CONTAINER));
                runElementProgress(nodeId, SceneElementModel.Event.STOP, StopHandler.ElementProcess.STOP_CONTAINER, () -> {
                    //关闭接入方式可用
                    closeAccessModes(element);
                    AzureContainer azureContainer = new AzureContainer(IAzureResourceManager.getInstance(cloudType));
                    azureContainer.stop(instanceId);
                });
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.LOAD)) {
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.STOP, Arrays.asList(
                        StopHandler.ElementProcess.STOP_CONTAINER));
                runElementProgress(nodeId, SceneElementModel.Event.STOP, ElementProcess.STOP_LOAD, () -> {
                    //关闭接入方式可用
                    closeAccessModes(element);
                    DevopsLoadSourceMapper devopsLoadSourceMapper = BeanCommon.getBean(DevopsLoadSourceMapper.class);
                    DevopsLoadSourceModel synchronousDataSource = devopsLoadSourceMapper.getSynchronousDataSource();
                    Load load = new Load(synchronousDataSource);
                    load.stop(element.getLoadResourceInstanceId());
                });
            }
        } else {
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
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS)) {
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
}
