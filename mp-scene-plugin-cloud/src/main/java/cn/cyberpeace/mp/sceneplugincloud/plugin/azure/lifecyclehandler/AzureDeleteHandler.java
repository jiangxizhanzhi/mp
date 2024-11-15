package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler;

import cn.cyberpeace.mp.auth.model.MicrosoftAzureUserModel;
import cn.cyberpeace.mp.auth.repository.MicrosoftAzureLoginMapper;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.deletetasks.LoadDeleteTask;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.DeleteHandler;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class AzureDeleteHandler extends DeleteHandler {

    private String groupNamePrefix;

    private String mixAzureManagerUrl;
    private String privateAzureManagerUrl;

    private MicrosoftAzureLoginMapper sceneElementPluginConfigMapper;
    public static class ElementProcess {
        public static final EventProcess DELETE_WEBAPP = new EventProcess("delete_webapp", "sceneplugincloud.event" + ".delete_webapp");
    }

    private IAzureResourceManager resourceService;

    public AzureDeleteHandler(int cloudType, CloudScenePluginHandler pluginHandler) {
        super(pluginHandler);
        this.resourceService = IAzureResourceManager.getInstance(cloudType);
        this.groupNamePrefix = BeanCommon.getBean(SituationAwareConfig.class).getGroupNamePrefix();
        this.mixAzureManagerUrl = BeanCommon.getBean(SituationAwareConfig.class).getMixAzureManagerUrl();
        this.privateAzureManagerUrl = BeanCommon.getBean(SituationAwareConfig.class).getPrivateAzureManagerUrl();
        this.sceneElementPluginConfigMapper = BeanCommon.getBean(MicrosoftAzureLoginMapper.class);
    }


    @Override
    public void delete() {
        this.prepareEventProcess();

        int stepPercentage = this.getStepPercentage();
//        List<CloudPluginSceneElementBO> allElements = new ArrayList<>(getPluginHandler().getElementMap().values());
//
//        // 再删除终端 终端分虚拟机、容器、和web服务
//        List<Runnable> deleteTerminalTasks = new ArrayList<>();
//        // 虚拟机
//        List<CloudPluginSceneElementBO> filteredVirtualMachineElements = this.filterVirtualMachineElements(allElements);
//        List<Runnable> deleteVirtualMachineTasks = new ArrayList<>();
//        for (CloudPluginSceneElementBO element : filteredVirtualMachineElements) {
//            String nodeId = element.getSrcElement().getId();
//            AzureVirtualMachineDeleteTask task = new AzureVirtualMachineDeleteTask(resourceService, element);
//            Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, DeleteHandler.ElementProcess.DELETE_VIRTUAL_MACHINE, task);
//            deleteVirtualMachineTasks.add(formatTask);
//        }
//        deleteTerminalTasks.addAll(deleteVirtualMachineTasks);
//
//        // 容器
//        List<CloudPluginSceneElementBO> filteredContainerElements = this.filterContainerElements(allElements);
//        List<Runnable> deleteContainerTasks = new ArrayList<>();
//        for (CloudPluginSceneElementBO element : filteredContainerElements) {
//            String nodeId = element.getSrcElement().getId();
//            AzureContainerDeleteTask task = new AzureContainerDeleteTask(resourceService, element);
//            Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, DeleteHandler.ElementProcess.DELETE_CONTAINER, task);
//            deleteContainerTasks.add(formatTask);
//        }
//        deleteTerminalTasks.addAll(deleteContainerTasks);
//
//        // webapp
//        List<CloudPluginSceneElementBO> filteredWebAppElements = this.filterWebAppElements(allElements);
//        List<Runnable> deleteWebAppTasks = new ArrayList<>();
//        for (CloudPluginSceneElementBO element : filteredWebAppElements) {
//            String nodeId = element.getSrcElement().getId();
//            AzureWebAppDeleteTask task = new AzureWebAppDeleteTask(resourceService, element);
//            Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, ElementProcess.DELETE_WEBAPP, task);
//            deleteWebAppTasks.add(formatTask);
//        }
//        deleteTerminalTasks.addAll(deleteWebAppTasks);
//
//        if (!deleteTerminalTasks.isEmpty()) {
//            runSceneProgress(SceneProcess.DELETE_TERMINALS, () -> runTasks(deleteTerminalTasks));
//        }
//
//        // 删除辅助网络
//        List<CloudPluginSceneElementBO> filteredAuxiliaryNetworkElements = this.filterAuxiliaryNetworkElements(allElements);
//        List<Runnable> deleteAuxiliaryNetworkTasks = new ArrayList<>();
//        for (CloudPluginSceneElementBO element : filteredAuxiliaryNetworkElements) {
//            String nodeId = element.getSrcElement().getId();
//            AzureVirtualNetDeleteTask task = new AzureVirtualNetDeleteTask(resourceService, element);
//            Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, DeleteHandler.ElementProcess.DELETE_AUXILIARY_NETWORK, task);
//            deleteAuxiliaryNetworkTasks.add(formatTask);
//        }
//        if (!deleteAuxiliaryNetworkTasks.isEmpty()) {
//            runSceneProgress(SceneProcess.DELETE_AUXILIARY_NETWORKS, () -> runTasks(deleteAuxiliaryNetworkTasks));
//        }

//        删除载荷
        List<CloudPluginSceneElementBO> allElements = new ArrayList<>(getPluginHandler().getElementMap().values());
        List<CloudPluginSceneElementBO> filteredLoadElements = filterLoadElements(allElements);
        List<Runnable> deleteLoadTasks = new ArrayList<>();
        for (CloudPluginSceneElementBO element : filteredLoadElements) {
            String nodeId = element.getSrcElement().getId();
            LoadDeleteTask task = new LoadDeleteTask(element);
            Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, DeleteHandler.ElementProcess.DELETE_LOAD, task);
            deleteLoadTasks.add(formatTask);
        }
        if (!deleteLoadTasks.isEmpty()) {
            runSceneProgress(SceneProcess.DELETE_TERMINALS, () -> runTasks(deleteLoadTasks));
        }

        // 最后删除资源组 TODO 测试载荷暂时去除
        String groupName = getPluginHandler().getSourceSceneId();
        groupName = StringUtils.isNotBlank(groupNamePrefix)?groupNamePrefix +"-"+ groupName:groupName;

        Integer cloudType = getPluginHandler().getSceneHandler().getScene().getCloudType();
        JSONObject jsonObject = new JSONObject();
        //云类型(1: 微软公有云 2:微软私有云 3:微软混合云)
        jsonObject.put("cloudType", cloudType);
        MicrosoftAzureUserModel localAzurer = sceneElementPluginConfigMapper.getLocalAzurer(jsonObject);
        if (cloudType == BaseSceneModel.CloudType.PUBLIC) {
            resourceService.getManager().resourceGroups().deleteByName(groupName);
        } else {
            String azureManagerUrl = mixAzureManagerUrl;
            if(cloudType == BaseSceneModel.CloudType.PRIVATE){
                azureManagerUrl = privateAzureManagerUrl;
            }
            String url = azureManagerUrl + "/cloud/delete_group";
            Map<String, Object> formMap = new HashMap();
            formMap.put("azureUser", localAzurer);
            formMap.put("groupName", groupName);
            //请求头设置
            HashMap<String, String> headersMap = new HashMap<>();
            String body = JsonUtils.stringify(formMap);
            try {
                HttpUtils.sendHttpPost(url, body, headersMap);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private Runnable formatElementTask(String nodeId, int stepPercentage, EventProcess elementProcess, Runnable action) {
        return () -> {
            runElementProgress(nodeId, elementProcess, action);
            getPluginHandler().getSceneHandler().addProgressPercentage(stepPercentage);
        };
    }

    private List<CloudPluginSceneElementBO> filterAuxiliaryNetworkElements(List<CloudPluginSceneElementBO> allElements) {
        List<CloudPluginSceneElementBO> filterElements = new ArrayList<>();
        var sceneHandler = this.getPluginHandler().getSceneHandler();
        for (var c : allElements) {
            if (sceneHandler.isHandleNode(c.getSrcElement().getId()) && !c.getDeriveNetworks().isEmpty()) {
                filterElements.add(c);
            }
        }
        return filterElements;
    }

    private List<CloudPluginSceneElementBO> filterLoadElements(List<CloudPluginSceneElementBO> allElements) {
        List<CloudPluginSceneElementBO> filterElements = new ArrayList<>();
        for (var c : allElements) {
            if (BaseCloudPluginSceneElementModel.ImageType.LOAD.equals(c.getImageType()) && BaseCloudPluginSceneElementModel.VirtualType.LOAD == c.getVirtualType()) {
                filterElements.add(c);
            }
        }
        return filterElements;
    }

    private List<CloudPluginSceneElementBO> filterVirtualMachineElements(List<CloudPluginSceneElementBO> allElements) {
        List<CloudPluginSceneElementBO> filterElements = new ArrayList<>();
        for (var c : allElements) {
            if (BaseCloudPluginSceneElementModel.ImageType.VM.equals(c.getImageType()) && BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE == c.getVirtualType()) {
                filterElements.add(c);
            }
        }
        return filterElements;
    }

    private List<CloudPluginSceneElementBO> filterContainerElements(List<CloudPluginSceneElementBO> allElements) {
        List<CloudPluginSceneElementBO> filterElements = new ArrayList<>();
        for (var c : allElements) {
            if (BaseCloudPluginSceneElementModel.ImageType.CONTAINER.equals(c.getImageType()) && BaseCloudPluginSceneElementModel.VirtualType.CONTAINER == c.getVirtualType()) {
                filterElements.add(c);
            }
        }
        return filterElements;
    }

    private List<CloudPluginSceneElementBO> filterWebAppElements(List<CloudPluginSceneElementBO> allElements) {
        List<CloudPluginSceneElementBO> filterElements = new ArrayList<>();
        for (var c : allElements) {
            if (BaseCloudPluginSceneElementModel.ImageType.VNET.equals(c.getImageType()) && BaseCloudPluginSceneElementModel.VirtualType.WEBAPP == c.getVirtualType()) {
                filterElements.add(c);
            }
        }
        return filterElements;
    }

    private int getStepPercentage() {
        long networkTasks = getPluginHandler().getElementMap().values().stream().filter(cloudElement -> !cloudElement.getDeriveNetworks().isEmpty()).count();
        long gatewayTasks = getPluginHandler().getGatewayElementMap().values().size();
        long firewallTasks = getPluginHandler().getFirewallElementMap().values().size();
        long terminalTasks = getPluginHandler().getTerminalElementMap().values().size();
        int stepPercentage = (int) (100 / Math.max(networkTasks + gatewayTasks + firewallTasks + terminalTasks, 1));
        return stepPercentage;
    }
}
