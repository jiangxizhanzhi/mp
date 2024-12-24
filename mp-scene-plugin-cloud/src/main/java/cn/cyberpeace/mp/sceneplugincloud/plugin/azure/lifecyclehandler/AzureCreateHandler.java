package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler;

import cn.cyberpeace.mp.auth.model.MicrosoftAzureUserModel;
import cn.cyberpeace.mp.auth.repository.MicrosoftAzureLoginMapper;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.scene.bo.AlertRuleBO;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.AlertRuleModel;
import cn.cyberpeace.mp.scene.model.InstanceModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import cn.cyberpeace.mp.scene.repository.AlertRuleMapper;
import cn.cyberpeace.mp.scene.repository.InstanceMapper;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSceneStage;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureResourceGroup;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.createtasks.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ConnectedNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler;
import cn.cyberpeace.mp.sceneplugincloud.util.ElementNetworkUtils;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import com.alibaba.fastjson.JSONObject;
import com.azure.core.http.rest.Response;
import com.azure.core.management.Region;
import com.azure.core.util.Context;
import com.azure.resourcemanager.loganalytics.models.Workspace;
import com.azure.resourcemanager.loganalytics.models.WorkspaceSku;
import com.azure.resourcemanager.loganalytics.models.WorkspaceSkuNameEnum;
import com.azure.resourcemanager.monitor.fluent.models.DataCollectionRuleResourceInner;
import com.azure.resourcemanager.monitor.models.*;
import com.azure.resourcemanager.network.models.Network;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class AzureCreateHandler extends CreateHandler {

    private String groupNamePrefix;
    private String mixAzureManagerUrl;
    private String privateAzureManagerUrl;

    private MicrosoftAzureLoginMapper sceneElementPluginConfigMapper;

    public static class ElementProcess {
        public static final EventProcess CREATE_WEBAPP = new EventProcess("create_webapp", "sceneplugincloud.event" + ".CREATE_WEBAPP");
        public static final EventProcess CREATE_FIRE_WALL = new EventProcess("create_fire_wall", "sceneplugincloud.event.create_fire_wall");
    }

    private IAzureResourceManager resourceService;

    public AzureCreateHandler(int cloudType, CloudScenePluginHandler pluginHandler) {
        super(pluginHandler);
        this.resourceService = IAzureResourceManager.getInstance(cloudType);
        this.groupNamePrefix = BeanCommon.getBean(SituationAwareConfig.class).getGroupNamePrefix();
        this.mixAzureManagerUrl = BeanCommon.getBean(SituationAwareConfig.class).getMixAzureManagerUrl();
        this.privateAzureManagerUrl = BeanCommon.getBean(SituationAwareConfig.class).getPrivateAzureManagerUrl();
        this.sceneElementPluginConfigMapper = BeanCommon.getBean(MicrosoftAzureLoginMapper.class);

    }

    @Override
    public void create() {
        this.prepareEventProcess();
        int stepPercentage = this.getStepPercentage();
        List<CloudPluginSceneElementBO> allElements = new ArrayList<>(getPluginHandler().getElementMap().values());
        List<CloudPluginSceneElementBO> allFilteredElements = new ArrayList<>();
        List<CloudPluginSceneElementBO> filteredVirtualMachineElements = this.filterVirtualMachineElements(allElements);

//        创建外部网络+kali TODO 云场景使用传统kali机方案开发
        var createExternalNetworkTasks = getPluginHandler().getAuxiliaryNetworkElement().stream()
                .map(cloudElement -> (Runnable) () -> createAuxiliaryNetworkProcess(cloudElement))
                .collect(Collectors.toList());

        if (!createExternalNetworkTasks.isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_AUXILIARY_NETWORKS, () -> {
                runTasks(createExternalNetworkTasks);
            });
        }

        var createGatewayTasks = getPluginHandler().getGatewayElements().stream()
                .map(cloudElement -> (Runnable) () -> createElementProcess(cloudElement))
                .collect(Collectors.toList());

        if (!createGatewayTasks.isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_GATEWAYS, () -> {
                runTasks(createGatewayTasks);
            });
        }

        var createKaliTasks = getPluginHandler().getTerminalExcludeNpcElements().stream()
                .map(cloudElement -> (Runnable) () -> createElementProcess(cloudElement)).collect(Collectors.toList());
        if (!createKaliTasks.isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_TERMINALS, () -> {
                runTasks(createKaliTasks);
            });
        }

        List<CloudPluginSceneElementBO> filteredContainerElements = this.filterContainerElements(allElements);
        allFilteredElements.addAll(filteredVirtualMachineElements);
        allFilteredElements.addAll(filteredContainerElements);

        List<CloudPluginSceneElementBO> filteredNetworkEquipmentElements = this.filterNetworkEquipmentElements(allElements);

        Integer cloudType = getPluginHandler().getSceneHandler().getScene().getCloudType();
        JSONObject jsonObject = new JSONObject();
        //云类型(1: 微软公有云 2:微软私有云 3:微软混合云)
        jsonObject.put("cloudType", cloudType);
        MicrosoftAzureUserModel localAzurer = sceneElementPluginConfigMapper.getLocalAzurer(jsonObject);

        getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CloudSceneStage.CREATE_NETWORK);
        // 创建微软云的资源组
        String azureManagerUrl = mixAzureManagerUrl;
        if(cloudType == BaseSceneModel.CloudType.PRIVATE){
            azureManagerUrl = privateAzureManagerUrl;
        }
        String groupName = getPluginHandler().getSourceSceneId();
        groupName = StringUtils.isNotBlank(groupNamePrefix) ? groupNamePrefix + "-" + groupName : groupName;
        try {
            SceneBO scene = getPluginHandler().getSceneHandler().getScene();

            // 先创建辅助网络
            List<Runnable> createTerminalTasks = new ArrayList<>();
//            空场景或不涉及云靶标的场景不启动资源组
            if (!allFilteredElements.isEmpty()) {
                String groupId = "";
                if (cloudType == BaseSceneModel.CloudType.PUBLIC) {
                    groupId = new AzureResourceGroup(resourceService).create(groupName);
                } else {
                    String url = azureManagerUrl + "/cloud/create_group_name";
                    Map<String, Object> formMap = new HashMap();
                    formMap.put("azureUser", localAzurer);
                    formMap.put("groupName", groupName);
                    //请求头设置
                    HashMap<String, String> headersMap = new HashMap<>();
                    String body = JsonUtils.stringify(formMap);
                    String result = HttpUtils.sendHttpPost(url, body, headersMap);
                    Map<String, Object> dataMap = JsonUtils.parseMap(result);
                    if (BaseErrorCode.SUCCESS.getCode().equals(dataMap.get("code"))) {
                        groupId = dataMap.get("data").toString();
                    }
                }
                scene.setResourceGroupId(groupId);

                HashMap<String, List<VirtualNic>> virtualNicsMap = new HashMap<>();
                allFilteredElements.forEach(element -> {
                    List<VirtualNic> virtualNics = this.genVirutalNics(element);
                    virtualNicsMap.put(element.getSrcElement().getId(), virtualNics);
                });

                List<CloudPluginSceneElementBO> filteredAuxiliaryNetworkElements = this.filterAuxiliaryNetworkElements(allElements);
                Map<String, CloudPluginSceneElementBO> vnetElementById = filteredAuxiliaryNetworkElements.stream()
                        .collect(Collectors.toMap(
                                element -> element.getSrcElement().getId(),
                                element -> element,
                                (existing, replacement) -> existing
                        ));

                List<Runnable> createAuxiliaryNetworkTasks = new ArrayList<>();

//            集合存已加管理网络节点id 用于判断互联节点是否已加过管理网络
                for (int i = 0; i < filteredAuxiliaryNetworkElements.size(); i++) {
                    boolean isManage = false;
                    CloudPluginSceneElementBO element = filteredAuxiliaryNetworkElements.get(i);
                    Optional<CloudPluginSceneElementBO> any = element.getSrcElement().getPorts().stream()
                            .filter(port -> null != this.getPluginHandler().getSceneHandler().getPortConnectedPort(port.getId()))
                            .map(port ->
                                    allElements.stream()
                                            .filter(element1 -> element1.getSrcElement().getId().equals(this.getPluginHandler().getSceneHandler().getPortConnectedPort(port.getId()).getElement().getId()))
                                            .findAny()
                                            .get()
                            )
                            .filter(Objects::nonNull)
                            .filter(pluginSceneElementBO -> !pluginSceneElementBO.getIsPublicIp() || pluginSceneElementBO.getImageType().equals(BaseCloudPluginSceneElementModel.ImageType.CONTAINER_REGISTRY))
                            .findAny();
                    if (any.isPresent()) {
                        isManage = true;
                    }
                    if (!BaseCloudPluginSceneElementModel.ImageType.VNET.equals(element.getImageType()) && !element.getIsPublicIp()) {
                        isManage = true;
                    }

                    //筛选出所有cidr是否连接容器
                    CloudScenePluginHandler pluginHandler = getPluginHandler();
                    ConcurrentHashMap<String, ElementNetworkUtils.CidrType> cidrContainerMap = ElementNetworkUtils.getCidrToContainerMap(pluginHandler, allElements, element, virtualNicsMap, isManage);

                    String nodeId = element.getSrcElement().getId();
                    // 封装创建网络的任务
                    AzureVirtualNetCreateTask task = new AzureVirtualNetCreateTask(resourceService, getPluginHandler(), element,
                            cidrContainerMap, virtualNicsMap, groupName, azureManagerUrl, localAzurer);
                    // 格式化任务执行过程，添加 runElementProgress
                    Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, CreateHandler.ElementProcess.CREATE_AUXILIARY_NETWORK, task);
                    // 添加格式化后的任务到列表
                    createAuxiliaryNetworkTasks.add(formatTask);
                }
                if (!createAuxiliaryNetworkTasks.isEmpty()) {
                    // 执行 创建所有网络
                    runSceneProgress(SceneProcess.CREATE_AUXILIARY_NETWORKS, () -> runTasks(createAuxiliaryNetworkTasks));
                }
//            对等互联
                Set<String> calledPairs = new HashSet<>();
                filteredAuxiliaryNetworkElements.stream().collect(Collectors.groupingBy(e -> e.getSrcElement().getId()));
                for (int i = 0; i < filteredAuxiliaryNetworkElements.size(); i++) {
//                找出连接VNet的VNet节点
                    CloudPluginSceneElementBO element = filteredAuxiliaryNetworkElements.get(i);
                    element.getSrcElement().getPorts().forEach(port -> {
                        SceneElementPortBO portConnectedPort = this.getPluginHandler().getSceneHandler().getPortConnectedPort(port.getId());
                        if (null != portConnectedPort) {
                            String id = portConnectedPort.getElement().getId();
                            CloudPluginSceneElementBO elementBO = vnetElementById.get(id);
                            if (elementBO != null) {
                                String resourceId = element.getResourceId();
                                String resourceId1 = elementBO.getResourceId();
                                createVNetPeering(cloudType,resourceId, resourceId1, calledPairs, localAzurer);
                            }
                        }
                    });
                }
                getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_NETWORK);

//                查需要创建的所有报警规则
                String eid = getPluginHandler().getSrcScene().getEid();
                InstanceMapper instanceMapper = BeanCommon.getBean(InstanceMapper.class);
                HumanCondition instanceHumanCondition = new HumanCondition()
                        .filterEqual(InstanceModel::getScene, eid);
                InstanceModel instanceModel = instanceMapper.first(instanceHumanCondition);

                AlertRuleMapper alertRuleMapper = BeanCommon.getBean(AlertRuleMapper.class);
                HumanCondition alertRuleHumanCondition = new HumanCondition()
                        .filterEqual(AlertRuleModel::getSceneId, instanceModel.getCscene());
                List<AlertRuleModel> list = alertRuleMapper.list(alertRuleHumanCondition);
                List<AlertRuleBO> alertRuleBOList = list.stream().map(alertRuleModel -> {
                            AlertRuleBO alertRuleBO = new AlertRuleBO();
                            BeanUtils.copyProperties(alertRuleModel, alertRuleBO);
                            alertRuleBO.parseHosts();
                            alertRuleBO.parseAlertConditions();
                            return alertRuleBO;
                        })
                        .collect(Collectors.toList());
//                创建日志区/数据收集规则
                String dataCollectionRulesId = null;
                if (!alertRuleBOList.isEmpty()) {
                    Response<DataCollectionRuleResourceInner> workspaceAndDataCollectionRules = createWorkspaceAndDataCollectionRules(groupName);
                    dataCollectionRulesId = workspaceAndDataCollectionRules.getValue().id();
                }

                // 再创建网关 和 防火墙
                getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CloudSceneStage.CREATE_GATEWAY);
                getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_GATEWAY);


                // 再创建终端 终端分虚拟机、容器、和web服务
                getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CloudSceneStage.CREATE_INSTANCE);


                // 虚拟机
                List<Runnable> createVirtualMachineTasks = new ArrayList<>();
                for (CloudPluginSceneElementBO element : filteredVirtualMachineElements) {
                    String nodeId = element.getSrcElement().getId();
                    List<AlertRuleBO> alertRuleBOListByNodeId = alertRuleBOList
                            .stream()
                            .filter(alertRuleBO -> null != alertRuleBO.getHosts() && alertRuleBO.getHostList().contains(nodeId))
                            .collect(Collectors.toList());

                    List<VirtualNic> virtualNics = virtualNicsMap.get(nodeId);
                    //            创建虚拟机时不允许扩容
                    element.getSrcElement().setExpansionDisk(null);
                    element.setVmExpansionDiskType(null);
//                处理云场景无登录按钮、接收虚拟端口信息
                    receiveVirtualPortInfo(element, virtualNics);
                    AzureVirtualMachineCreateTask task = new AzureVirtualMachineCreateTask(resourceService, getPluginHandler(), element,
                            virtualNics, combineInitCommand(element), alertRuleBOListByNodeId, dataCollectionRulesId, groupName,
                            azureManagerUrl, localAzurer);
                    Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, CreateHandler.ElementProcess.CREATE_VIRTUAL_MACHINE, task);
                    createVirtualMachineTasks.add(formatTask);
                }
                createTerminalTasks.addAll(createVirtualMachineTasks);
                // 容器
                List<Runnable> createContainerTasks = new ArrayList<>();
                for (CloudPluginSceneElementBO element : filteredContainerElements) {
                    String nodeId = element.getSrcElement().getId();
                    List<AlertRuleBO> alertRuleBOListByNodeId = alertRuleBOList
                            .stream()
                            .filter(alertRuleBO -> null != alertRuleBO.getHosts() && alertRuleBO.getHostList().contains(nodeId))
                            .collect(Collectors.toList());
                    List<VirtualNic> virtualNics = virtualNicsMap.get(nodeId);
                    AzureContainerCreateTask task = new AzureContainerCreateTask(resourceService, getPluginHandler(), element, virtualNics, alertRuleBOListByNodeId, dataCollectionRulesId, groupName);
                    Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, CreateHandler.ElementProcess.CREATE_CONTAINER, task);
                    createContainerTasks.add(formatTask);
                }
                createTerminalTasks.addAll(createContainerTasks);

                // webapp
                List<CloudPluginSceneElementBO> filteredWebAppElements = this.filterWebAppElements(allElements);
                List<Runnable> createWebAppTasks = new ArrayList<>();
                for (CloudPluginSceneElementBO element : filteredWebAppElements) {
                    String nodeId = element.getSrcElement().getId();
                    List<VirtualNic> virtualNics = this.genVirutalNics(element);
                    AzureWebAppCreateTask task = new AzureWebAppCreateTask(resourceService, getPluginHandler(), element, virtualNics, groupName);
                    Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, ElementProcess.CREATE_WEBAPP, task);
                    createWebAppTasks.add(formatTask);
                }
                createTerminalTasks.addAll(createWebAppTasks);

                // 防火墙
                List<CloudPluginSceneElementBO> filteredFireWallAppElements = this.filterFireWallElements(allElements);
                List<Runnable> createFireWallTasks = new ArrayList<>();
                for (CloudPluginSceneElementBO element : filteredFireWallAppElements) {
                    String nodeId = element.getSrcElement().getId();
                    List<VirtualNic> virtualNics = this.genVirutalNics(element);
                    AzureFireWallCreateTask task = new AzureFireWallCreateTask(resourceService, getPluginHandler(), element, virtualNics, groupName);
                    Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, ElementProcess.CREATE_FIRE_WALL, task);
                    createFireWallTasks.add(formatTask);
                }
                createTerminalTasks.addAll(createFireWallTasks);
            }

            // 载荷
            List<CloudPluginSceneElementBO> filteredLoadElements = this.filterLoadElements(allElements);
            List<Runnable> createLoadTasks = new ArrayList<>();
            for (CloudPluginSceneElementBO element : filteredLoadElements) {
                String nodeId = element.getSrcElement().getId();
                LoadCreateTask task = new LoadCreateTask(getPluginHandler(), element, groupName);
                Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, CreateHandler.ElementProcess.CREATE_LOAD, task);
                createLoadTasks.add(formatTask);
            }
            createTerminalTasks.addAll(createLoadTasks);

            List<Runnable> createNetworkEquipmentTasks = new ArrayList<>();
            for (CloudPluginSceneElementBO element : filteredNetworkEquipmentElements) {
                String nodeId = element.getSrcElement().getId();
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
//                        边界设备无需处理
                    }
                };
                Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, CreateHandler.ElementProcess.CREATE_NETWORK_EQUIPMENT, task);
                createNetworkEquipmentTasks.add(formatTask);
            }
            createTerminalTasks.addAll(createNetworkEquipmentTasks);

            if (!createTerminalTasks.isEmpty()) {
                runSceneProgress(SceneProcess.CREATE_TERMINALS, () -> runTasks(createTerminalTasks));
            }

            getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_INSTANCE);
            getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CloudSceneStage.CONFIG);
            getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CONFIG);

        } catch (Exception e) {
            log.error("创建场景失败", e);
//            云场景启动失败立即删除云资源
            if (cloudType == BaseSceneModel.CloudType.PUBLIC) {
                resourceService.getManager().resourceGroups().deleteByName(groupName);
            } else {
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
            throw new MpRuntimeException(BaseErrorCode.ERROR, e);
        }
    }

    /**
     * 创建workspace / data collection rules
     *
     * @param groupName
     */
    private Response<DataCollectionRuleResourceInner> createWorkspaceAndDataCollectionRules(String groupName) {
        Workspace workspace = resourceService
                .getLogAnalyticsManager()
                .workspaces().define("Workspace-" + groupName)
                .withRegion(Region.ASIA_EAST)
                .withExistingResourceGroup(groupName)
                .withSku(new WorkspaceSku().withName(WorkspaceSkuNameEnum.PER_GB2018))
                .create();

//        设置数据收集规则
        DataCollectionRuleResourceInner dataCollectionRuleResourceInner = new DataCollectionRuleResourceInner();
        DataCollectionRuleDataSources dataCollectionRuleDataSources = new DataCollectionRuleDataSources();
        PerfCounterDataSource perfSource1 = new PerfCounterDataSource()
                .withName("perfCounterDataSource")
                .withCounterSpecifiers(Arrays.asList("\\VmInsights\\DetailedMetrics"))
                .withStreams(Arrays.asList(KnownPerfCounterDataSourceStreams.MICROSOFT_INSIGHTS_METRICS))
                .withSamplingFrequencyInSeconds(60);// 性能计数器源

        dataCollectionRuleDataSources.withPerformanceCounters(Arrays.asList(perfSource1));

        DataCollectionRuleDestinations dataCollectionRuleDestinations = new DataCollectionRuleDestinations();
        LogAnalyticsDestination logAnalyticsDestination = new LogAnalyticsDestination()
                .withWorkspaceResourceId(workspace.id())
                .withName("LogAnalyticsDestination");// 将数据发送到 Log Analytics
        dataCollectionRuleDestinations.withLogAnalytics(Arrays.asList(logAnalyticsDestination));

        dataCollectionRuleResourceInner.withLocation(Region.ASIA_EAST.name())  // 设置数据收集规则的位置
                .withDataSources(dataCollectionRuleDataSources)
                .withDestinations(dataCollectionRuleDestinations)
                .withDataFlows(
                        Arrays.asList(
                                new DataFlow()
                                        .withStreams(Arrays.asList(KnownDataFlowStreams.MICROSOFT_INSIGHTS_METRICS, KnownDataFlowStreams.MICROSOFT_PERF))
                                        .withDestinations(Arrays.asList("LogAnalyticsDestination"))
                        )
                );

        return resourceService
                .getMonitorManager()
                .serviceClient()
                .getDataCollectionRules()
                .createWithResponse(groupName,
                        "MSVMI-" + groupName,
                        dataCollectionRuleResourceInner,
                        Context.NONE);
    }

    /**
     * 创建虚拟网络之间的对等互连。
     * 该方法用于检查两个虚拟网络是否已经建立了对等互连，如果没有，则建立对等互连。
     * 对等互连的标识通过两个资源ID的哈希值最小值和最大值组合而成，以确保唯一性。
     *
     * @param cloudType
     * @param resourceId  第一个虚拟网络的资源ID。
     * @param resourceId1 第二个虚拟网络的资源ID。
     * @param calledPairs 已经建立对等互连的虚拟网络对的集合，用于避免重复建立互连。
     * @param localAzurer
     */
    private void createVNetPeering(Integer cloudType, String resourceId, String resourceId1, Set<String> calledPairs, MicrosoftAzureUserModel localAzurer){
        // 创建一个基于参数对的唯一标识符
        String pairId = Math.min(resourceId.hashCode(), resourceId1.hashCode()) + ":" + Math.max(resourceId.hashCode(), resourceId1.hashCode());

        if (!calledPairs.contains(pairId)) {
            if (cloudType == BaseSceneModel.CloudType.PUBLIC) {

            Network network = resourceService.getManager().networks().getById(resourceId);
            Network network1 = resourceService.getManager().networks().getById(resourceId1);

            // 对 vNet1 创建到 vNet2 的对等互联
            network.peerings().define(resourceId.substring(resourceId1.length() - 10) + "To" + resourceId1.substring(resourceId1.length() - 10) + "VNet2Peering")
                    .withRemoteNetwork(network1)
                    .create();
            } else {
                String azureManagerUrl = mixAzureManagerUrl;
                if(cloudType == BaseSceneModel.CloudType.PRIVATE){
                    azureManagerUrl = privateAzureManagerUrl;
                }
                String url = azureManagerUrl + "/cloud/connection";
                Map<String, Object> formMap = new HashMap();
                formMap.put("azureUser", localAzurer);
                formMap.put("networkId", resourceId);
                formMap.put("connectionNetworkId", resourceId1);

                //请求头设置
                HashMap<String, String> headersMap = new HashMap<>();
                String body = JsonUtils.stringify(formMap);
                String result = null;
                try {
                    result = HttpUtils.sendHttpPost(url, body, headersMap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Map<String, Object> dataMap = JsonUtils.parseMap(result);
                if (!BaseErrorCode.SUCCESS.getCode().equals(dataMap.get("code"))) {
                    throw new MpRuntimeException("Peering failure");
                }
            }
            calledPairs.add(pairId);
        }
    }


    private List<CloudPluginSceneElementBO> filterLoadElements(List<
            CloudPluginSceneElementBO> allElements) {
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
            if (StringUtils.isNotEmpty(c.getImageType()) && (BaseCloudPluginSceneElementModel.ImageType.LOAD.equals(c.getImageType()) ||
                    BaseCloudPluginSceneElementModel.ImageType.NETWORK_EQUIPMENT.equals(c.getImageType()) ||
                    BaseCloudPluginSceneElementModel.ImageType.FIRE_WALL.equals(c.getImageType()))) {

            } else if (sceneHandler.isHandleNode(c.getSrcElement().getId()) && !c.getDeriveNetworks().isEmpty()) {
                filterElements.add(c);
            } else if (StringUtils.isNotEmpty(c.getImageType()) && BaseCloudPluginSceneElementModel.ImageType.VNET.equals(c.getImageType())) {
//    筛选出所有VNet节点
                filterElements.add(c);
            }
        }

        return filterElements;
    }


    /**
     * 筛选虚拟机节点
     *
     * @param allElements
     * @return
     */
    private List<CloudPluginSceneElementBO> filterVirtualMachineElements(List<CloudPluginSceneElementBO> allElements) {
        List<CloudPluginSceneElementBO> filterElements = new ArrayList<>();
        for (var c : allElements) {
            if (BaseCloudPluginSceneElementModel.ImageType.LAAS_VM.equals(c.getImageType()) && BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE == c.getVirtualType()) {
                filterElements.add(c);
            }
        }
        return filterElements;
    }

    /**
     * 筛选容器节点
     *
     * @param allElements
     * @return
     */
    private List<CloudPluginSceneElementBO> filterContainerElements(List<CloudPluginSceneElementBO> allElements) {
        List<CloudPluginSceneElementBO> filterElements = new ArrayList<>();
        for (var c : allElements) {
            if (BaseCloudPluginSceneElementModel.ImageType.CONTAINER_REGISTRY.equals(c.getImageType()) && BaseCloudPluginSceneElementModel.VirtualType.CONTAINER == c.getVirtualType()) {
                filterElements.add(c);
            }
        }
        return filterElements;
    }

    /**
     * 筛选边界设备
     *
     * @param allElements
     * @return
     */
    private List<CloudPluginSceneElementBO> filterNetworkEquipmentElements(List<CloudPluginSceneElementBO> allElements) {
        List<CloudPluginSceneElementBO> filterElements = new ArrayList<>();
        for (var c : allElements) {
            if (BaseCloudPluginSceneElementModel.ImageType.NETWORK_EQUIPMENT.equals(c.getImageType())) {
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

    private List<CloudPluginSceneElementBO> filterFireWallElements(List<CloudPluginSceneElementBO> allElements) {
        List<CloudPluginSceneElementBO> filterElements = new ArrayList<>();
        for (var c : allElements) {
            if (BaseCloudPluginSceneElementModel.ImageType.FIRE_WALL.equals(c.getImageType()) && BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER == c.getVirtualType()) {
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

    /**
     * 处理云场景无登录按钮、接收虚拟端口信息，用于云节点和虚拟网络接口的绑定及配置管理。
     *
     * @param cloudElement 云端场景节点，包含云端设备的配置信息。
     * @param virtualNics  虚拟网络接口列表，用于和云端设备进行网络连接。
     */
    public void receiveVirtualPortInfo(CloudPluginSceneElementBO cloudElement, List<VirtualNic> virtualNics) {
        if (virtualNics == null) {
            return;
        }

        var cloudPorts = getPluginHandler().getPorts(cloudElement);
        if (cloudPorts.isEmpty()) {
            return;
        }

        Map<String, List<CloudPluginSceneElementPortBO>> networkIdCloudPortsMap = new HashMap<>();
        List<ConnectedNetworkBO> networks = new ArrayList<>();
        networks.addAll(cloudElement.getDeriveNetworks());
        networks.addAll(cloudElement.getAccessNetworks());
        for (ConnectedNetworkBO network : networks) {
            var auxiliaryNetworkId = network.getNetwork().getResourceId();
            var networkIdCloudPorts = networkIdCloudPortsMap.getOrDefault(
                    auxiliaryNetworkId, new ArrayList<>());
            network.getFromPorts().forEach(port -> {
                var pluginPort = getPluginHandler().getPort(port.getId());
                networkIdCloudPorts.add(pluginPort);
            });
            networkIdCloudPortsMap.put(auxiliaryNetworkId, networkIdCloudPorts);
        }
        VirtualNic virtualNic = virtualNics.get(0);
        var networkId = virtualNic.getNetworkId();
        // 管理网卡判断
        if (Boolean.TRUE.equals(cloudElement.getEnableMgmt())
                && CloudSingleton.cloudAppConfig.getMgmtNetId().equals(networkId)) {
            cloudElement.setMgmtPortResourceId(virtualNic.getPortId());
            cloudElement.setMgmtIp(virtualNic.getFixedIp());
            cloudElement.setMgmtMacAddr(virtualNic.getMacAddress());
            return;
        }

        var networkCloudPorts = networkIdCloudPortsMap.getOrDefault(virtualNics.get(0).getNetworkId(), null);
        if (networkCloudPorts == null) {
            return;
        }

        // 多个端口连到同一个网络上的时候暂时当做一个端口处理
        networkCloudPorts.forEach(cloudPort -> {
            var srcPort = cloudPort.getSrcPort();
            VirtualNic virtualNic1;
            if (virtualNics.size() == 1) {
                virtualNic1 = virtualNics.get(0);
                srcPort.setIp(virtualNic.getFixedIp());
                srcPort.setMacAddr(virtualNic.getMacAddress());
            } else {
                virtualNic1 = virtualNics
                        .stream()
                        .filter(v -> v.getFixedIp().equals(srcPort.getIp()))
                        .findFirst()
                        .get();
            }


            cloudPort.setResourceId(virtualNic1.getPortId());
            cloudPort.setQosId(virtualNic1.getQosId());
            cloudPort.setFloatingIp(virtualNic1.getFloatingip());
            cloudPort.setFloatingIpId(virtualNic1.getFloatingipId());
            cloudPort.setAuxiliaryNetworkId(virtualNic1.getNetworkId());

            if (null != virtualNic1.getAccessModes() && virtualNic1.getAccessModes().size() > 0 && null != virtualNic1.getAccessModes().get(0)) {
                VirtualNic.AccessMode accessMode = virtualNic1.getAccessModes().get(0);
                String protocol = accessMode.getProtocol();
                String[] protoclos = protocol.split(";");
                List<CloudAccessMode> accessModes = cloudPort.getAccessModes();
                accessModes.clear();
                for (int j = 0; j < protoclos.length; j++) {
                    CloudAccessMode cloudAccessMode = new CloudAccessMode();
//                        TODO 管理网络ip port
                    cloudAccessMode.setAccessIp(virtualNic1.getFixedIp());
                    cloudAccessMode.setProtocol(protoclos[j]);
                    cloudAccessMode.setAccessPort(AccessMode.Protocol.DEFAULT_PORT_MAP.get(protoclos[j].toLowerCase()));
                    cloudAccessMode.setUsername(cloudElement.getUsername());
                    cloudAccessMode.setPassword(cloudElement.getPassword());
                    cloudAccessMode.setAvailable(true);
                    cloudAccessMode.setProxy(true);
                    cloudAccessMode.setId(TextUtils.uuid());

                    if (StringUtils.isEmpty(cloudAccessMode.getAccessIp()) && cloudPort.getSrcPort() != null) {
                        cloudAccessMode.setAccessIp(cloudPort.getSrcPort().getIp());
                    }
                    accessModes.add(cloudAccessMode);
                }
            }
        });
    }
}
