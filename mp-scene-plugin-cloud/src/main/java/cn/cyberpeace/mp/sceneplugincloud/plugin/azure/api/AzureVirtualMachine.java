package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.scene.bo.AlertRuleBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.model.AlarmConditionModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.NetworkSecurityGroupUtils;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.Region;
import com.azure.core.util.Context;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.fluent.models.ResourceSkuInner;
import com.azure.resourcemanager.compute.models.*;
import com.azure.resourcemanager.monitor.MonitorManager;
import com.azure.resourcemanager.monitor.fluent.DataCollectionRuleAssociationsClient;
import com.azure.resourcemanager.monitor.fluent.MonitorClient;
import com.azure.resourcemanager.monitor.fluent.ScheduledQueryRulesClient;
import com.azure.resourcemanager.monitor.fluent.models.DataCollectionRuleAssociationProxyOnlyResourceInner;
import com.azure.resourcemanager.monitor.fluent.models.ScheduledQueryRuleResourceInner;
import com.azure.resourcemanager.monitor.models.*;
import com.azure.resourcemanager.network.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class AzureVirtualMachine {
    private IAzureResourceManager resourceService;

    private AzureResourceManager azureResourceManager;

    private MonitorManager monitorManager;

    private Region region;

    private SituationAwareConfig situationAwareConfig;

    public AzureVirtualMachine(IAzureResourceManager resourceService) {
        this.resourceService = resourceService;
        this.azureResourceManager = resourceService.getManager();
        this.monitorManager = resourceService.getMonitorManager();
        this.region = resourceService.getRegin();
        this.situationAwareConfig = BeanCommon.getBean(SituationAwareConfig.class);
    }

    public void VirtualMachinelist() throws IOException {
        PagedIterable<VirtualMachineImage> virtualMachineImages = azureResourceManager.virtualMachineImages().listByRegion(Region.ASIA_EAST);
//        PagedIterable<VirtualMachinePublisher> virtualMachinePublishers = azureResourceManager.virtualMachineImages().publishers().listByRegion(Region.ASIA_EAST);
        VirtualMachinePublisher chosenPublisher;
        PrintWriter writer = new PrintWriter(new FileWriter("C:\\Users\\liyah\\Desktop\\output2.txt", true));
        virtualMachineImages.forEach(virtualMachineImage -> {
            writer.println("Image - " + virtualMachineImage.publisherName() + "/"
                    + virtualMachineImage.offer() + "/"
                    + virtualMachineImage.sku());
        });
        writer.close();
        System.out.println("end------------------------------------------------");

    }

    public List<Map<String, String>> listsAllSkusWithCapabilities(ComputeResourceType type) throws IOException {
        ArrayList<Map<String, String>> dataMaps = new ArrayList<>();
        PagedIterable<ResourceSkuInner> list = azureResourceManager
                .virtualMachines()
                .manager()
                .serviceClient()
                .getResourceSkus()
                .list();

        // 使用 Set 来存储已经出现过的 sku.name()，保证不重复
        for (ResourceSkuInner sku : list) {
            if (type.toString().equals(sku.resourceType())) {
                String name = sku.name();
//                过滤非系统磁盘
                if ("PremiumV2_LRS".equals(name) || "UltraSSD_LRS".equals(name)) continue;

                List<String> locations = sku.locations();
                if ((locations.contains(Region.ASIA_EAST.label()) || locations.contains(Region.ASIA_EAST.name()))) {
                    // 将 capabilities 转换为一个 Map，并直接添加到 dataMap 中
                    Map<String, String> capabilityMap = sku.capabilities().stream()
                            .collect(Collectors.toMap(ResourceSkuCapabilities::name, ResourceSkuCapabilities::value));
                    // 添加 name 和 capabilityMap 到 dataMap
                    capabilityMap.put("name", name);
                    capabilityMap.put("size", sku.size());
//                    统一格式 秒->分
                    String maxBurstDurationInMin = capabilityMap.get("MaxBurstDurationInMin");
                    String maxSizeGiB = capabilityMap.get("MaxSizeGiB");
                    if (null != maxBurstDurationInMin) {
                        capabilityMap.put("MaxBurstDurationInMin", String.valueOf(Integer.parseInt(maxBurstDurationInMin) / 60));
                    }

                    if (null != maxSizeGiB) {
                        boolean b = Integer.parseInt(maxSizeGiB) < 1024;
                        if (b) {
                            capabilityMap.put("MaxSize", maxSizeGiB);
                            capabilityMap.put("MaxSizeUnit", "GB");
                        } else {
                            capabilityMap.put("MaxSize", String.valueOf(Integer.parseInt(maxSizeGiB) / 1024));
                            capabilityMap.put("MaxSizeUnit", "TB");
                        }
                    }
//                    管理网络占用网卡数 网卡数-1
                    Pattern pattern = Pattern.compile("-?\\d+");
                    if (null != capabilityMap.get("MaxNetworkInterfaces") && pattern.matcher(capabilityMap.get("MaxNetworkInterfaces")).matches()) {
                        capabilityMap.put("MaxNetworkInterfaces", String.valueOf(Integer.parseInt(capabilityMap.get("MaxNetworkInterfaces")) - 1));
                    }


                    dataMaps.add(capabilityMap);
                }
            }
        }
        return dataMaps;
    }

    public VirtualMachine createManagementVM(Integer cloudType, String groupName, String vmName, Network network, String subName) {
        String customImageId = null;
        switch (cloudType) {
            case BaseSceneModel.CloudType.PUBLIC:
                customImageId = situationAwareConfig.getPublicManagementImageId();
                break;
            case BaseSceneModel.CloudType.PRIVATE:
                customImageId = situationAwareConfig.getPrivateManagementImageId();
                break;
            case BaseSceneModel.CloudType.MIX:
                customImageId = situationAwareConfig.getMixManagementImageId();
                break;
        }
        PublicIpAddress publicIpAddress = azureResourceManager.publicIpAddresses().define("PublicIP" + vmName)
                .withRegion(region)
                .withExistingResourceGroup(groupName)
                .withDynamicIP()
                .create();

        VirtualMachine virtualMachine = azureResourceManager.virtualMachines()
                .define(vmName)
                .withRegion(region)
                .withNewResourceGroup(groupName)
                .withExistingPrimaryNetwork(network)
                .withSubnet(subName)
                .withPrimaryPrivateIPAddressDynamic()
                .withExistingPrimaryPublicIPAddress(publicIpAddress)
                .withGeneralizedLinuxCustomImage(customImageId)
                .withRootUsername(situationAwareConfig.getManagementUsername())
                .withRootPassword(situationAwareConfig.getManagementPassword())
                .withOSDiskStorageAccountType(StorageAccountTypes.STANDARD_LRS)
                .withOSDiskSizeInGB(30)
                .withSize(VirtualMachineSizeTypes.STANDARD_B1S)
                .create();
        NetworkInterface primaryNetworkInterface = virtualMachine.getPrimaryNetworkInterface();

//        开放9200、8200端口
        NetworkSecurityGroup nsg = azureResourceManager.networkSecurityGroups()
                .define("nsg-" + vmName)
                .withRegion(this.region)
                .withExistingResourceGroup(groupName)
                .defineRule("allow-8200")
                .allowInbound()
                .fromAnyAddress()
                .fromAnyPort()
                .toAnyAddress()
                .toPort(9200)
                .withProtocol(SecurityRuleProtocol.ASTERISK)
                .withPriority(3200)
                .attach()
                .defineRule("allow-9200")
                .allowInbound()
                .fromAnyAddress()
                .fromAnyPort()
                .toAnyAddress()
                .toPort(8200)
                .withProtocol(SecurityRuleProtocol.ASTERISK)
                .withPriority(3300)
                .attach()
                .defineRule("allow-22")
                .allowInbound()
                .fromAnyAddress()
                .fromAnyPort()
                .toAnyAddress()
                .toPort(22)
                .withProtocol(SecurityRuleProtocol.ASTERISK)
                .withPriority(3400)
                .attach()
                .create();
        primaryNetworkInterface.update().withExistingNetworkSecurityGroup(nsg).apply();

        return virtualMachine;
    }

    public void create(CloudScenePluginHandler pluginHandler, CloudPluginSceneElementBO cloudElement,
                       List<VirtualNic> virtualNics, String userData, List<AlertRuleBO> alertRuleBOList, String dataCollectionRulesId) {
//        cloudElement.setResourceId("1233");
//        cloudElement.setPublicIpAddress("20.30.40.5");

//        TODO 资源不浪费

        String groupName = pluginHandler.getSourceSceneId();

        // 调用微软云api 创建虚拟机 回写虚拟机id
        VirtualMachine vm = createVirtualMachine(cloudElement, virtualNics, userData, groupName);
        cloudElement.setResourceId(vm.id());
        if (cloudElement.getIsPublicIp()) {
            cloudElement.setPublicIpAddress(vm.getPrimaryPublicIPAddress().ipAddress());
        }

//        创建网络安全组
//        绑定网络接口
//        设置入站规则
//        更新网络安全组

        createNsg(cloudElement, groupName);

        configureMonitoringForVirtualMachine(cloudElement, dataCollectionRulesId, vm, groupName);

        createAlertRules(alertRuleBOList, vm, groupName);

    }

    /**
     * 创建网络安全组
     *
     * @param cloudElement
     * @param groupName
     */
    private void createNsg(CloudPluginSceneElementBO cloudElement, String groupName) {
        SceneElementBO srcElement = cloudElement.getSrcElement();
        for (SceneElementPortBO port : srcElement.getPorts()) {
            List<AccessMode> accessModes = port.getAccessModes();
            String ip = port.getIp();
//            匹配网络接口
            if (StringUtils.isNotBlank(ip)) {
                PagedIterable<NetworkInterface> networkInterfaces = azureResourceManager.networkInterfaces().list();

                Optional<NetworkInterface> first = networkInterfaces.stream().filter(networkInterface -> ip.equals(networkInterface.primaryPrivateIP())).findFirst();
                if (first.isPresent()) {
                    NetworkInterface networkInterface = first.get();
                    //            创建nsg
                    NetworkSecurityGroup nsg = azureResourceManager.networkSecurityGroups()
                            .define("ssh-nsg-" + networkInterface.name())
                            .withRegion(this.region)
                            .withExistingResourceGroup(groupName)
                            .create();
                    NetworkSecurityGroup.Update nsgUpdate = nsg.update();
                    for (int i = 0; i < accessModes.size(); i++) {
                        AccessMode accessMode = accessModes.get(i);
                        String protocol = accessMode.getProtocol();
                        if (null == protocol) continue;
                        String[] protoclos = protocol.split(";");
                        for (int j = 0; j < protoclos.length; j++) {
                            NetworkSecurityGroupUtils.addInRule(protoclos[j], nsgUpdate, 3100 + i * 200 + j * 10);
                        }
                    }
                    nsgUpdate.apply();
                    networkInterface.update().withExistingNetworkSecurityGroup(nsg).apply();
                }
            }
        }
    }

    /**
     * 创建报警规则
     *
     * @param alertRuleBOList
     * @param vm
     * @param groupName
     */
    private void createAlertRules(List<AlertRuleBO> alertRuleBOList, VirtualMachine vm, String groupName) {
        if (alertRuleBOList.size() > 0) {
            for (AlertRuleBO alertRuleBO : alertRuleBOList) {
                List<AlarmConditionModel> alarmConditionList = alertRuleBO.getAlarmConditionList();
                AlarmConditionModel alarmConditionModel = alarmConditionList.get(0);
                if (alarmConditionModel != null && alarmConditionModel.getType().equals("Custom Log Search")) {
                    ArrayList<Condition> conditions = new ArrayList<>();
                    Condition condition = new Condition()
                            .withQuery(alarmConditionModel.getCondition())
//                默认计数
                            .withTimeAggregation(TimeAggregation.COUNT)
                            .withOperator(ConditionOperator.fromString(alarmConditionModel.getOperator()))
                            .withThreshold(alarmConditionModel.getThreshold());
                    conditions.add(condition);

                    ScheduledQueryRuleCriteria criteria = new ScheduledQueryRuleCriteria();
                    criteria.withAllOf(conditions);

//                      TODO 暂默认30分钟
                    long windowSize = 1800;
                    Duration frequency = Duration.parse(alarmConditionModel.getFrequency());
                    long frequencyMin = frequency.getSeconds();
                    if (frequencyMin > windowSize) {
                        windowSize = frequencyMin;
                    }

                    ScheduledQueryRuleResourceInner rule = new ScheduledQueryRuleResourceInner();
                    rule.withCriteria(criteria);
                    rule.withSeverity(AlertSeverity.fromLong(alertRuleBO.getAlarmLevel()));
//        聚合粒度
                    rule.withWindowSize(Duration.ofSeconds(windowSize));
//        评估频率
                    rule.withEvaluationFrequency(Duration.parse(alarmConditionModel.getFrequency()));
                    rule.withLocation(Region.ASIA_EAST.name());
                    rule.withScopes(Arrays.asList(vm.id()));
                    MonitorClient monitorClient = monitorManager.serviceClient();
                    ScheduledQueryRulesClient scheduledQueryRules = monitorClient.getScheduledQueryRules();
                    try {
                        scheduledQueryRules.createOrUpdate(groupName, alertRuleBO.getName() + "-" + vm.name(), rule);
                    } catch (Exception e) {
                        log.error(" ScheduledQueryAlert create erro,ScheduledQueryAlertName: {}", alertRuleBO.getName());
                    }

                } else if (alarmConditionModel != null) {

                    // 创建监视警报
                    MetricAlert.DefinitionStages.WithCriteriaDefinition withCriteriaDefinition = azureResourceManager.alertRules().metricAlerts()
//                预警规则名称
                            .define(alertRuleBO.getName() + "-" + vm.name())
//                资源组
                            .withExistingResourceGroup(groupName)
//                虚拟机id
                            .withTargetResource(vm.id())
//                回溯期 5分钟
                            .withPeriod(Duration.ofMinutes(15))
//                检查间隔
                            .withFrequency(Duration.ofMinutes(5))
//                严重性，预警规则说明
                            .withAlertDetails(alertRuleBO.getAlarmLevel(), alertRuleBO.getAlarmDescription())
//                选择操作组
//                .withActionGroups("/subscriptions/C87BA926-F7C2-4D53-8B8E-89245DC92E26/resourceGroups/6b987034b1f8490abc635ccdf9753c02/providers/microsoft.insights/actionGroups/RecommendedAlertRules-AG-1")
                            .withActionGroups();
                    MetricAlert.DefinitionStages.WithCreate attach = withCriteriaDefinition
//                定义预警条件
                            .defineAlertCriteria(alarmConditionModel.getType())
//                设置要监控的度量标准名称，即 "Available Memory Bytes"（可用内存字节）
                            .withMetricName(alarmConditionModel.getType())
//                设置预警条件：
//                - 使用的时间聚合类型为 "MINIMUM"（最小值）
//                - 条件是小于293,601,280字节（约280MB）的内存
                            .withCondition(MetricAlertRuleTimeAggregation.fromString(alarmConditionModel.getAggregationType()),
                                    MetricAlertRuleCondition.fromString(alarmConditionModel.getOperator()),
                                    alarmConditionModel.getThreshold())
                            .attach();
                    if (alarmConditionList.size() > 1) {
                        for (int i = 1; i < alarmConditionList.size(); i++) {
                            alarmConditionModel = alarmConditionList.get(i);
                            attach
                                    .defineAlertCriteria(alarmConditionModel.getType())
                                    .withMetricName(alarmConditionModel.getType())
                                    .withCondition(MetricAlertRuleTimeAggregation.fromString(alarmConditionModel.getAggregationType()),
                                            MetricAlertRuleCondition.fromString(alarmConditionModel.getOperator()),
                                            alarmConditionModel.getThreshold())
                                    .attach();
                        }
                    }
                    try {
                        attach.create();
                    } catch (Exception e) {
                        log.error(" MetricAlert create erro,MetricAlertName: {}", alertRuleBO.getName());
                    }
                }
            }
        }
    }

    /**
     * 配置虚拟机的监控
     *
     * @param cloudElement
     * @param dataCollectionRulesId
     * @param vm
     * @param groupName
     */
    private void configureMonitoringForVirtualMachine(CloudPluginSceneElementBO cloudElement, String dataCollectionRulesId, VirtualMachine vm, String groupName) {
        if (StringUtils.isNotBlank(dataCollectionRulesId)) {

            //        数据收集规则与虚拟机绑定
            DataCollectionRuleAssociationProxyOnlyResourceInner inner = new DataCollectionRuleAssociationProxyOnlyResourceInner();
            inner.withDataCollectionRuleId(dataCollectionRulesId);

            DataCollectionRuleAssociationsClient dataCollectionRuleAssociations = monitorManager.serviceClient().getDataCollectionRuleAssociations();
            dataCollectionRuleAssociations
                    .createWithResponse(vm.id(),
                            vm.name(),
                            inner,
                            Context.NONE);

//        虚拟机启用系统分配的托管标识
            azureResourceManager
                    .virtualMachines()
                    .getByResourceGroup(groupName, vm.name())
                    .update()
                    .withSystemAssignedManagedServiceIdentity()
                    .apply();

//        安装监视器扩展
            if (BaseSceneElementModel.SystemType.WINDOWS.equals(cloudElement.getVmType().toLowerCase())) {
                azureResourceManager
                        .virtualMachines()
                        .getByResourceGroup(groupName, vm.name())
                        .update()
                        .defineNewExtension("AzureMonitorWindowsAgent")
                        .withPublisher("Microsoft.Azure.Monitor")
                        .withType("AzureMonitorWindowsAgent")
                        .withVersion("1.0")
                        .withMinorVersionAutoUpgrade()
                        .attach()
                        .apply();
            } else {
                azureResourceManager
                        .virtualMachines()
                        .getByResourceGroup(groupName, vm.name())
                        .update()
                        .defineNewExtension("AzureMonitorLinuxAgent")
                        .withPublisher("Microsoft.Azure.Monitor")
                        .withType("AzureMonitorLinuxAgent")
                        .withVersion("1.0")
                        .withMinorVersionAutoUpgrade()
                        .attach()
                        .apply();
            }
        }
    }

    public void delete(CloudPluginSceneElementBO cloudElement) {
        try {
            VirtualMachine virtualMachine = azureResourceManager.virtualMachines().getById(cloudElement.getResourceId());

            String osDiskId = virtualMachine.osDiskId();
            String networkInterfaceId = virtualMachine.getPrimaryNetworkInterface().id();
            PublicIpAddress primaryPublicIPAddress = virtualMachine.getPrimaryPublicIPAddress();
            //        删除虚拟机及绑定资源
            azureResourceManager.virtualMachines().deleteById(cloudElement.getResourceId());
            azureResourceManager.disks().deleteById(osDiskId);
            azureResourceManager.networkInterfaces().deleteById(networkInterfaceId);
            if (null != primaryPublicIPAddress) {
                azureResourceManager.publicIpAddresses().deleteById(primaryPublicIPAddress.id());
            }
        } catch (Exception e) {
            log.error("The VM failed to be deleted.", e);
        }
    }

    public void execCommand(String group, String vmName, List<String> commands) {
        azureResourceManager.virtualMachines().runShellScript(group, vmName, commands, new ArrayList<>());
    }

    public void execCoand(String group, String vmName, List<String> commands) {
        azureResourceManager.virtualMachines().runShellScript(group, vmName, commands, new ArrayList<>());
    }

    public void pause(String instanceId) {
        VirtualMachine virtualMachine = azureResourceManager.virtualMachines().getById(instanceId);

        virtualMachine.powerOff();
    }

    public void restart(CloudPluginSceneElementBO element) {
        VirtualMachine virtualMachine = azureResourceManager.virtualMachines().getById(element.getResourceId());
//        关机无法重启 先开机
        PowerState powerState = virtualMachine.powerState();
        if (PowerState.RUNNING != powerState) {
            virtualMachine.start();
        }
        virtualMachine.restart();
        if (element.getIsPublicIp()) {
            element.setPublicIpAddress(virtualMachine.getPrimaryPublicIPAddress().ipAddress());
        }
    }

    public void start(CloudPluginSceneElementBO element) {
        VirtualMachine virtualMachine = azureResourceManager.virtualMachines().getById(element.getResourceId());
        virtualMachine.start();
        if (element.getIsPublicIp()) {
            element.setPublicIpAddress(virtualMachine.getPrimaryPublicIPAddress().ipAddress());
        }
    }

    public void unpause(String instanceId) {
        VirtualMachine virtualMachine = azureResourceManager.virtualMachines().getById(instanceId);
        virtualMachine.start();
    }

    public void stop(String instanceId) {
        VirtualMachine virtualMachine = azureResourceManager.virtualMachines().getById(instanceId);
        virtualMachine.deallocate();
    }

    public void expansion(CloudPluginSceneElementBO cloudElement) {
        VirtualMachine virtualMachine = azureResourceManager.virtualMachines().getById(cloudElement.getResourceId());
        VirtualMachine.Update update = virtualMachine.update().withSize(cloudElement.getVmSizeType());
        Integer expansionDisk = cloudElement.getSrcElement().getExpansionDisk();
        if (!cloudElement.getExpansion() &&
                expansionDisk != null &&
                expansionDisk > 0 &&
                StringUtils.isNotBlank(cloudElement.getVmExpansionDiskType())) {
            update.withDataDiskDefaultStorageAccountType(StorageAccountTypes.fromString(cloudElement.getVmExpansionDiskType()))
                    .withNewDataDisk(expansionDisk);
        }
        update.apply();
    }

    /**
     * 创建虚拟机的方法
     *
     * @param cloudElement 云插件场景元素
     * @param userData     用户数据
     * @param groupName    资源组名称
     * @return 创建的虚拟机对象
     */
    private VirtualMachine createVirtualMachine(CloudPluginSceneElementBO cloudElement, List<VirtualNic> virtualNics, String userData, String groupName) {
        // 生成虚拟机名称
        String vmName = HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid());

        // 获取磁盘信息
        Integer disk = cloudElement.getSrcElement().getDisk();
        Integer expansionDisk = cloudElement.getSrcElement().getExpansionDisk();
        String vmExpansionDiskType = cloudElement.getVmExpansionDiskType();

        // 定义虚拟机大小和存储账户类型
        VirtualMachineSizeTypes flavor = StringUtils.isNotBlank(cloudElement.getVmSizeType()) ? VirtualMachineSizeTypes.fromString(cloudElement.getVmSizeType()) : VirtualMachineSizeTypes.STANDARD_B1S;
        StorageAccountTypes storageAccountTypes = StringUtils.isNotBlank(cloudElement.getVmOsDiskType()) ? StorageAccountTypes.fromString(cloudElement.getVmOsDiskType()) : StorageAccountTypes.STANDARD_SSD_LRS;

        VirtualMachine.DefinitionStages.WithFromImageCreateOptionsManaged creator;
        // 取第一块网卡
        // azure todo 调整 root name
        VirtualNic virtualNic = virtualNics.get(0);
        String netId = virtualNic.getNetworkId();
        if (BaseSceneElementModel.SystemType.WINDOWS.equals(cloudElement.getVmType().toLowerCase())) {
            creator = createWindowsVM(cloudElement, userData, groupName, azureResourceManager, region, vmName, netId, virtualNic, disk, expansionDisk, vmExpansionDiskType, storageAccountTypes, flavor);
        } else {
            creator = createLinuxVM(cloudElement, userData, groupName, azureResourceManager, region, vmName, netId, virtualNic, disk, expansionDisk, vmExpansionDiskType, storageAccountTypes, flavor);
        }
//        TODO 绑定管理网络 回写管理网络信息
//        element.getMgmtIp()

        // 添加其他网卡
        for (int i = 1; i < virtualNics.size(); i++) {
            VirtualNic nic = virtualNics.get(i);
            NetworkInterface ni = createNetworkInterface(azureResourceManager, region, groupName, nic, netId);
            creator.withExistingSecondaryNetworkInterface(ni);
        }

        // 创建虚拟机
        return creator.create();
    }

    /**
     * 创建Windows虚拟机
     *
     * @param cloudElement         云插件场景元素
     * @param userData             用户数据
     * @param groupName            资源组名称
     * @param azureResourceManager Azure资源管理器
     * @param region               地区
     * @param vmName               虚拟机名称
     * @param netId                网络ID
     * @param virtualNic           虚拟网卡
     * @param disk                 磁盘大小
     * @param expansionDisk        扩展磁盘大小
     * @param vmExpansionDiskType  扩展磁盘类型
     * @param storageAccountTypes  存储账户类型
     * @param flavor               虚拟机规格
     * @return 虚拟机定义阶段对象
     */
    private VirtualMachine.DefinitionStages.WithFromImageCreateOptionsManaged createWindowsVM(
            CloudPluginSceneElementBO cloudElement, String userData, String groupName, AzureResourceManager azureResourceManager,
            Region region, String vmName, String netId, VirtualNic virtualNic, Integer disk, Integer expansionDisk, String vmExpansionDiskType,
            StorageAccountTypes storageAccountTypes, VirtualMachineSizeTypes flavor) {

        VirtualMachine.DefinitionStages.WithProximityPlacementGroup withProximityPlacementGroup = createVMWithProximityPlacementGroup(cloudElement, groupName, azureResourceManager, region, vmName, netId, virtualNic);

        VirtualMachine.DefinitionStages.WithFromImageCreateOptionsManaged creator;

        if (cloudElement.getSourceType() == 2) {
            creator = withProximityPlacementGroup
                    .withGeneralizedWindowsCustomImage(cloudElement.getUploadImageId())
                    .withAdminUsername(cloudElement.getUsername())
                    .withAdminPassword(cloudElement.getPassword())
                    .withCustomData(Base64.getEncoder().encodeToString(userData.getBytes()));
        } else {
            creator = withProximityPlacementGroup
                    .withLatestWindowsImage(cloudElement.getPublisher(), cloudElement.getOffer(), cloudElement.getSku())
                    .withAdminUsername(cloudElement.getUsername())
                    .withAdminPassword(cloudElement.getPassword())
                    .withCustomData(Base64.getEncoder().encodeToString(userData.getBytes()));
        }

        configureDisks(creator, expansionDisk, vmExpansionDiskType, storageAccountTypes, flavor, disk);

        return creator;
    }

    /**
     * 创建Linux虚拟机
     *
     * @param cloudElement         云插件场景元素
     * @param userData             用户数据
     * @param groupName            资源组名称
     * @param azureResourceManager Azure资源管理器
     * @param region               地区
     * @param vmName               虚拟机名称
     * @param netId                网络ID
     * @param virtualNic           虚拟网卡
     * @param disk                 磁盘大小
     * @param expansionDisk        扩展磁盘大小
     * @param vmExpansionDiskType  扩展磁盘类型
     * @param storageAccountTypes  存储账户类型
     * @param flavor               虚拟机规格
     * @return 虚拟机定义阶段对象
     */
    private VirtualMachine.DefinitionStages.WithFromImageCreateOptionsManaged createLinuxVM(
            CloudPluginSceneElementBO cloudElement, String userData, String groupName, AzureResourceManager azureResourceManager,
            Region region, String vmName, String netId, VirtualNic virtualNic, Integer disk, Integer expansionDisk, String vmExpansionDiskType,
            StorageAccountTypes storageAccountTypes, VirtualMachineSizeTypes flavor) {
        VirtualMachine.DefinitionStages.WithProximityPlacementGroup withProximityPlacementGroup =
                createVMWithProximityPlacementGroup(cloudElement, groupName, azureResourceManager, region, vmName, netId, virtualNic);

        VirtualMachine.DefinitionStages.WithFromImageCreateOptionsManaged creator;

        if (cloudElement.getSourceType() == 2) {
            creator = withProximityPlacementGroup
                    .withGeneralizedLinuxCustomImage(cloudElement.getUploadImageId())
                    .withRootUsername(cloudElement.getUsername())
                    .withRootPassword(cloudElement.getPassword())
                    .withCustomData(Base64.getEncoder().encodeToString(userData.getBytes()));
        } else {
            creator = withProximityPlacementGroup
                    .withLatestLinuxImage(cloudElement.getPublisher(), cloudElement.getOffer(), cloudElement.getSku())
                    .withRootUsername(cloudElement.getUsername())
                    .withRootPassword(cloudElement.getPassword())
                    .withCustomData(Base64.getEncoder().encodeToString(userData.getBytes()));
        }

        configureDisks(creator, expansionDisk, vmExpansionDiskType, storageAccountTypes, flavor, disk);

        return creator;
    }

    /**
     * 创建带有ProximityPlacementGroup配置的虚拟机定义阶段。
     *
     * @param cloudElement         包含虚拟机配置的CloudPluginSceneElementBO对象
     * @param groupName            资源组名称
     * @param azureResourceManager Azure资源管理器
     * @param region               虚拟机所在的区域
     * @param vmName               虚拟机名称
     * @param netId                网络ID
     * @param virtualNic           虚拟网卡信息
     * @return 带有ProximityPlacementGroup配置的虚拟机定义阶段
     */
    private static VirtualMachine.DefinitionStages.WithProximityPlacementGroup createVMWithProximityPlacementGroup(CloudPluginSceneElementBO cloudElement, String groupName, AzureResourceManager azureResourceManager, Region region, String vmName, String netId, VirtualNic virtualNic) {
        VirtualMachine.DefinitionStages.WithProximityPlacementGroup withProximityPlacementGroup;
        VirtualMachine.DefinitionStages.WithPublicIPAddress withPublicIPAddress = azureResourceManager.virtualMachines()
                .define(vmName)
                .withRegion(region)
                .withNewResourceGroup(groupName)
                .withExistingPrimaryNetwork(azureResourceManager.networks().getById(netId))
                .withSubnet(IpUtils.extractNetworkPart(virtualNic.getFixedIp()))
                .withPrimaryPrivateIPAddressStatic(virtualNic.getFixedIp());

        if (cloudElement.getIsPublicIp()) {
            PublicIpAddress publicIpAddress = azureResourceManager.publicIpAddresses().define("PublicIP" + vmName)
                    .withRegion(region)
                    .withExistingResourceGroup(groupName)
                    .withDynamicIP()
                    .create();
            withProximityPlacementGroup = withPublicIPAddress.withExistingPrimaryPublicIPAddress(publicIpAddress);
        } else {
            withProximityPlacementGroup = withPublicIPAddress.withoutPrimaryPublicIPAddress();
        }
        return withProximityPlacementGroup;
    }

    /**
     * 配置磁盘
     *
     * @param creator             虚拟机定义阶段对象
     * @param expansionDisk       扩展磁盘大小
     * @param vmExpansionDiskType 扩展磁盘类型
     * @param storageAccountTypes 存储账户类型
     * @param flavor              虚拟机规格
     * @param disk                系统磁盘大小
     */
    private void configureDisks(VirtualMachine.DefinitionStages.WithFromImageCreateOptionsManaged creator, Integer expansionDisk, String vmExpansionDiskType, StorageAccountTypes storageAccountTypes, VirtualMachineSizeTypes flavor, Integer disk) {
        if (expansionDisk != null && expansionDisk > 0) {
            creator.withDataDiskDefaultStorageAccountType(StorageAccountTypes.fromString(vmExpansionDiskType))
                    .withNewDataDisk(expansionDisk);
        }
        creator.withOSDiskStorageAccountType(storageAccountTypes)
                .withOSDiskSizeInGB(disk)
                .withSize(flavor);
    }

    /**
     * 创建网络接口
     *
     * @param azureResourceManager Azure资源管理器
     * @param region               地区
     * @param groupName            资源组名称
     * @param nic                  虚拟网卡
     * @param netId                网络ID
     * @return 创建的网络接口对象
     */
    private NetworkInterface createNetworkInterface(AzureResourceManager azureResourceManager, Region region, String groupName, VirtualNic nic, String netId) {
        Network azureNetwork = azureResourceManager.networks().getById(netId);
        return azureResourceManager.networkInterfaces()
                .define("nic" + nic.getFixedIp())
                .withRegion(region)
                .withNewResourceGroup(groupName)
                .withExistingPrimaryNetwork(azureNetwork)
                .withSubnet(IpUtils.extractNetworkPart(nic.getFixedIp()))
                .withPrimaryPrivateIPAddressStatic(nic.getFixedIp())
                .create();
    }

}

