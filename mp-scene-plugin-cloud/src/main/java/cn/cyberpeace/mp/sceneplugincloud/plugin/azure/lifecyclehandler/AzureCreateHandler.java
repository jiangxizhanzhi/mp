package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureResourceGroup;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.createtasks.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AzureCreateHandler extends CreateHandler {

    public Integer cloudType;

    public static class ElementProcess {
        public static final EventProcess CREATE_WEBAPP = new EventProcess("create_webapp", "sceneplugincloud.event" + ".CREATE_WEBAPP");
    }

    private IAzureResourceManager resourceService;

    public AzureCreateHandler(int cloudType, CloudScenePluginHandler pluginHandler) {
        super(pluginHandler);
        this.cloudType = cloudType;
        this.resourceService = IAzureResourceManager.getInstance(cloudType);
    }

    public void reCreateHandler() {

    }

    @Override
    public void create() {
        this.prepareEventProcess();

        int stepPercentage = this.getStepPercentage();
        List<CloudPluginSceneElementBO> allElements = new ArrayList<>(getPluginHandler().getElementMap().values());

        // 创建微软云的资源组
        String groupName = getPluginHandler().getSourceSceneId();
        new AzureResourceGroup(resourceService).create(groupName);

        // 先创建辅助网络
        List<CloudPluginSceneElementBO> filteredAuxiliaryNetworkElements = this.filterAuxiliaryNetworkElements(allElements);
        List<Runnable> createAuxiliaryNetworkTasks = new ArrayList<>();
        List<String> cidrList = filteredAuxiliaryNetworkElements.stream()
                .flatMap(cloudElement -> cloudElement.getDeriveNetworks().stream())
                .map(cloudElement -> cloudElement.getNetwork().getCidr())
                .collect(Collectors.toList());

        String nodeIdd = filteredAuxiliaryNetworkElements.get(0).getSrcElement().getId();
        // 封装创建网络的任务
        AzureVirtualNetCreateTask taskk = new AzureVirtualNetCreateTask(resourceService, getPluginHandler(), filteredAuxiliaryNetworkElements.get(0), cidrList);
        // 格式化任务执行过程，添加 runElementProgress
        Runnable formatTaskk = this.formatElementTask(nodeIdd, stepPercentage, CreateHandler.ElementProcess.CREATE_AUXILIARY_NETWORK, taskk);
        // 添加格式化后的任务到列表
        createAuxiliaryNetworkTasks.add(formatTaskk);

        /*for (CloudPluginSceneElementBO element : filteredAuxiliaryNetworkElements) {
//            筛选出所有cidr

            String nodeId = element.getSrcElement().getId();
            // 封装创建网络的任务
            AzureVirtualNetCreateTask task = new AzureVirtualNetCreateTask(resourceService, getPluginHandler(), element);
            // 格式化任务执行过程，添加 runElementProgress
            Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, CreateHandler.ElementProcess.CREATE_AUXILIARY_NETWORK, task);
            // 添加格式化后的任务到列表
            createAuxiliaryNetworkTasks.add(formatTask);
        }*/
        if (!createAuxiliaryNetworkTasks.isEmpty()) {
            // 执行 创建所有网络
            runSceneProgress(SceneProcess.CREATE_AUXILIARY_NETWORKS, () -> runTasks(createAuxiliaryNetworkTasks));
        }
//        保存网络id
        String azureNetworkId = filteredAuxiliaryNetworkElements.get(0).getDeriveNetworks().get(0).getNetwork().getResourceId();
        filteredAuxiliaryNetworkElements.stream()
                .flatMap(cloudElement -> cloudElement.getDeriveNetworks().stream())
                .forEach(cloudElement -> cloudElement.getNetwork().setResourceId(azureNetworkId));


        // 再创建终端 终端分虚拟机、容器、和web服务
        List<Runnable> createTerminalTasks = new ArrayList<>();
        // 虚拟机
        List<CloudPluginSceneElementBO> filteredVirtualMachineElements = this.filterVirtualMachineElements(allElements);
        List<Runnable> createVirtualMachineTasks = new ArrayList<>();
        for (CloudPluginSceneElementBO element : filteredVirtualMachineElements) {
            String nodeId = element.getSrcElement().getId();
            List<VirtualNic> virtualNics = this.genVirutalNics(element);
            AzureVirtualMachineCreateTask task = new AzureVirtualMachineCreateTask(resourceService, getPluginHandler(), element, virtualNics);
            Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, CreateHandler.ElementProcess.CREATE_VIRTUAL_MACHINE, task);
            createVirtualMachineTasks.add(formatTask);
        }
        createTerminalTasks.addAll(createVirtualMachineTasks);

        // 容器
        List<CloudPluginSceneElementBO> filteredContainerElements = this.filterContainerElements(allElements);
        List<Runnable> createContainerTasks = new ArrayList<>();
        for (CloudPluginSceneElementBO element : filteredContainerElements) {
            String nodeId = element.getSrcElement().getId();
            List<VirtualNic> virtualNics = this.genVirutalNics(element);
            AzureContainerCreateTask task = new AzureContainerCreateTask(resourceService, getPluginHandler(), element, virtualNics);
            Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, CreateHandler.ElementProcess.CREATE_CONTAINER, task);
            createContainerTasks.add(formatTask);
        }
        createTerminalTasks.addAll(createContainerTasks);

        // 载荷
        List<CloudPluginSceneElementBO> filteredLoadElements = this.filterLoadElements(allElements);
        List<Runnable> createLoadTasks = new ArrayList<>();
        for (CloudPluginSceneElementBO element : filteredLoadElements) {
            String nodeId = element.getSrcElement().getId();
            LoadCreateTask task = new LoadCreateTask(element);
            Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, CreateHandler.ElementProcess.CREATE_LOAD, task);
            createLoadTasks.add(formatTask);
        }
        createTerminalTasks.addAll(createLoadTasks);

        // webapp
        List<CloudPluginSceneElementBO> filteredWebAppElements = this.filterWebAppElements(allElements);
        List<Runnable> createWebAppTasks = new ArrayList<>();
        for (CloudPluginSceneElementBO element : filteredWebAppElements) {
            String nodeId = element.getSrcElement().getId();
            List<VirtualNic> virtualNics = this.genVirutalNics(element);
            AzureWebAppCreateTask task = new AzureWebAppCreateTask(resourceService, getPluginHandler(), element, virtualNics);
            Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, ElementProcess.CREATE_WEBAPP, task);
            createWebAppTasks.add(formatTask);
        }
        createTerminalTasks.addAll(createWebAppTasks);

        if (!createTerminalTasks.isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_TERMINALS, () -> runTasks(createTerminalTasks));
        }
    }

    private List<CloudPluginSceneElementBO> filterLoadElements(List<CloudPluginSceneElementBO> allElements) {
        List<CloudPluginSceneElementBO> filterElements = new ArrayList<>();
        for (var c : allElements) {
            if (BaseCloudPluginSceneElementModel.VirtualType.LOAD == c.getVirtualType()) {
                filterElements.add(c);
            }
        }
        return filterElements;
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
            if (BaseCloudPluginSceneElementModel.ImageType.SIMPLE.equals(c.getImageType()) && BaseCloudPluginSceneElementModel.VirtualType.WEBAPP == c.getVirtualType()) {
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
