package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.auth.model.MicrosoftAzureUserModel;
import cn.cyberpeace.mp.auth.repository.MicrosoftAzureLoginMapper;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.DevopsLoadSourceModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureVirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.Load;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualDynamips;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.PauseTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.repository.DevopsLoadSourceMapper;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/28
 */
@Slf4j
public class PauseHandler extends HandlerCommon {
    private final VirtualMachine virtualMachine = new VirtualMachine();
    private final Container container = new Container();
    private final VirtualDynamips virtualDynamips = new VirtualDynamips();

    private String azureManagerUrl;

    private MicrosoftAzureLoginMapper sceneElementPluginConfigMapper;
    public static class SceneProcess {
        public static final EventProcess PAUSE_TERMINALS = new EventProcess("pause_terminals", "sceneplugincloud.event.pause_terminals");
    }

    public static class ElementProcess {
        public static final EventProcess PAUSE_VIRTUAL_MACHINE = new EventProcess("pause_virtual_machine", "sceneplugincloud.event.pause_virtual_machine");
        public static final EventProcess PAUSE_CONTAINER = new EventProcess("pause_container", "sceneplugincloud.event.pause_container");
        public static final EventProcess PAUSE_NODE = new EventProcess("pause_node", "sceneplugincloud.event.pause_node");
        public static final EventProcess PAUSE_DYNAMIPS = new EventProcess("pause_dynamips", "sceneplugincloud.event.pause_dynamips");
        public static final EventProcess PAUSE_LOAD = new EventProcess("pause_load", "sceneplugincloud.event.pause_load");
    }

    public PauseHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, PauseTaskPool.getInstance(), event);
        this.azureManagerUrl = BeanCommon.getBean(SituationAwareConfig.class).getAzureManagerUrl();
        this.sceneElementPluginConfigMapper = BeanCommon.getBean(MicrosoftAzureLoginMapper.class);
    }

    public void pause() {
        prepareEventProcess(SceneModel.Event.PAUSE, SceneProcess.class, ElementProcess.class);
        List<Runnable> tasks = getPluginHandler().getElementMap().values().stream()
                .filter(e -> this.getPluginHandler().getSceneHandler().isHandleNode(e.getSrcElement().getId()))
                .map(element -> (Runnable) () -> this.pauseElement(element))
                .collect(Collectors.toList());
        runSceneProgress(SceneProcess.PAUSE_TERMINALS, () -> runTasks(tasks));
    }

    public void pauseElement(CloudPluginSceneElementBO element) {
        String nodeId = element.getSrcElement().getId();
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(),
                    element.getSrcElement().getId());
            return;
        }
        if (element.getSrcElement().getEvent() != null && element.getSrcElement().getEvent() == SceneElementModel.Event.DEFAULT) {
            // 未启动状态无需暂停
            return;
        }
        String instanceId = element.getResourceId();
//        判断是否是微软云 容器无暂停操作
        Integer cloudType = getPluginHandler().getSceneHandler().getScene().getCloudType();
        if (cloudType != BaseSceneModel.CloudType.DEFAULT) {
            if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.PAUSE, Arrays.asList(ElementProcess.PAUSE_VIRTUAL_MACHINE));
                runElementProgress(nodeId, SceneElementModel.Event.PAUSE, ElementProcess.PAUSE_VIRTUAL_MACHINE, () -> {
                    closeAccessModes(element);
                    if (cloudType == BaseSceneModel.CloudType.PUBLIC) {
                        AzureVirtualMachine azureVirtualMachine = new AzureVirtualMachine(IAzureResourceManager.getInstance(cloudType));
                        azureVirtualMachine.pause(instanceId);
                    } else {
                        JSONObject jsonObject = new JSONObject();
                        //云类型(1: 微软公有云 2:微软私有云 3:微软混合云)
                        jsonObject.put("cloudType", cloudType);
                        MicrosoftAzureUserModel localAzurer = sceneElementPluginConfigMapper.getLocalAzurer(jsonObject);
                        String url = azureManagerUrl + "/cloud/vm_pause";
                        Map<String, Object> formMap = new HashMap();
                        formMap.put("azureUser", localAzurer);
                        formMap.put("vmId", element.getResourceId());
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
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.LOAD)) {
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.PAUSE, Arrays.asList(ElementProcess.PAUSE_LOAD));
                runElementProgress(nodeId, SceneElementModel.Event.PAUSE, ElementProcess.PAUSE_LOAD, () -> {
                    DevopsLoadSourceMapper devopsLoadSourceMapper = BeanCommon.getBean(DevopsLoadSourceMapper.class);
                    DevopsLoadSourceModel synchronousDataSource = devopsLoadSourceMapper.getSynchronousDataSource();
                    Load load = new Load(synchronousDataSource);
                    load.pause(element.getLoadResourceInstanceId());
                });
            }
        } else {
            if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.PAUSE, Arrays.asList(ElementProcess.PAUSE_VIRTUAL_MACHINE));
                runElementProgress(nodeId, SceneElementModel.Event.PAUSE, ElementProcess.PAUSE_VIRTUAL_MACHINE, () -> {
                    closeAccessModes(element);
                    virtualMachine.pause(instanceId);
                });
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.PAUSE, Arrays.asList(ElementProcess.PAUSE_CONTAINER));
                runElementProgress(nodeId, SceneElementModel.Event.PAUSE, ElementProcess.PAUSE_CONTAINER, () -> {
                    closeAccessModes(element);
                    container.pause(instanceId);
                });
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS)) {
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.PAUSE, Arrays.asList(ElementProcess.PAUSE_DYNAMIPS));
                runElementProgress(nodeId, SceneElementModel.Event.PAUSE, ElementProcess.PAUSE_DYNAMIPS, () -> {
                    virtualDynamips.pause(instanceId);
                });
            } else {
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.PAUSE, Arrays.asList(ElementProcess.PAUSE_NODE));
                runElementProgress(nodeId, SceneElementModel.Event.PAUSE, ElementProcess.PAUSE_NODE, () -> {
                });
            }
        }
    }
}
