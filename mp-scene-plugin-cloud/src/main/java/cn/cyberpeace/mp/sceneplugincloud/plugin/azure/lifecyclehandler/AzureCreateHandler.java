package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.bo.AlertRuleBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.AlertRuleModel;
import cn.cyberpeace.mp.scene.model.InstanceModel;
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
import com.azure.resourcemanager.network.models.Network;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class AzureCreateHandler extends CreateHandler {

    public Integer cloudType;

    public static class ElementProcess {
        public static final EventProcess CREATE_WEBAPP = new EventProcess("create_webapp", "sceneplugincloud.event" + ".CREATE_WEBAPP");
        public static final EventProcess CREATE_FIRE_WALL = new EventProcess("create_fire_wall", "sceneplugincloud.event.create_fire_wall");
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

        //  TODO 测试载荷用 start
        // 再创建终端 终端分虚拟机、容器、和web服务
        /*List<Runnable> createTerminalTasks = new ArrayList<>();
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

        if (!createTerminalTasks.isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_TERMINALS, () -> runTasks(createTerminalTasks));
        }*/
        //  TODO 测试载荷用 end
        getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CloudSceneStage.CREATE_NETWORK);
        // 创建微软云的资源组
        String groupName = getPluginHandler().getSourceSceneId();
        try {
            new AzureResourceGroup(resourceService).create(groupName);
            // 先创建辅助网络
            List<CloudPluginSceneElementBO> allFilteredElements = new ArrayList<>();
            List<CloudPluginSceneElementBO> filteredVirtualMachineElements = this.filterVirtualMachineElements(allElements);

            List<CloudPluginSceneElementBO> filteredContainerElements = this.filterContainerElements(allElements);
            allFilteredElements.addAll(filteredVirtualMachineElements);
            allFilteredElements.addAll(filteredContainerElements);

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
            HashSet<String> manageElementIdSet = new HashSet<>();
            for (int i = 0; i < filteredAuxiliaryNetworkElements.size(); i++) {
                boolean isManage = false;
                CloudPluginSceneElementBO element = filteredAuxiliaryNetworkElements.get(i);
                List<String> vNetPeeringIdList = element.getSrcElement().getPorts().stream()
                        .map(port -> this.getPluginHandler().getSceneHandler().getPortConnectedPort(port.getId()))
                        .filter(Objects::nonNull)
                        .map(portConnectedPort -> vnetElementById.get(portConnectedPort.getElement().getId()))
                        .filter(Objects::nonNull)
                        .map(CloudPluginSceneElementBO::getSrcElement)
                        .map(SceneElementBO::getId)
                        .collect(Collectors.toList());
                if (vNetPeeringIdList.stream().noneMatch(manageElementIdSet::contains)) {
                    isManage = true;
                    manageElementIdSet.add(element.getSrcElement().getId());
                }

                //筛选出所有cidr是否连接容器
                CloudScenePluginHandler pluginHandler = getPluginHandler();
                ConcurrentHashMap<String, ElementNetworkUtils.CidrType> cidrContainerMap = ElementNetworkUtils.getCidrToContainerMap(pluginHandler, allElements, element, virtualNicsMap, isManage);

                String nodeId = element.getSrcElement().getId();
                // 封装创建网络的任务
                AzureVirtualNetCreateTask task = new AzureVirtualNetCreateTask(resourceService, getPluginHandler(), element, cidrContainerMap, virtualNicsMap);
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
                            createVNetPeering(resourceId, resourceId1, calledPairs);
                        }
                    }
                });
            }
            getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_NETWORK);

            // 再创建网关 和 防火墙
            getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CloudSceneStage.CREATE_GATEWAY);
            getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_GATEWAY);


            // 再创建终端 终端分虚拟机、容器、和web服务
            getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CloudSceneStage.CREATE_INSTANCE);

            List<Runnable> createTerminalTasks = new ArrayList<>();
            // 虚拟机
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
            List<Runnable> createVirtualMachineTasks = new ArrayList<>();
            for (CloudPluginSceneElementBO element : filteredVirtualMachineElements) {
                String nodeId = element.getSrcElement().getId();
                List<AlertRuleBO> alertRuleBOListByNodeId = alertRuleBOList
                        .stream()
                        .filter(alertRuleBO -> alertRuleBO.getHostList().contains(nodeId))
                        .collect(Collectors.toList());

                List<VirtualNic> virtualNics = virtualNicsMap.get(nodeId);
                //            创建虚拟机时不允许扩容
                element.getSrcElement().setExpansionDisk(null);
                element.setVmExpansionDiskType(null);
//                处理云场景无登录按钮、接收虚拟端口信息
                receiveVirtualPortInfo(element, virtualNics);
                AzureVirtualMachineCreateTask task = new AzureVirtualMachineCreateTask(resourceService, getPluginHandler(), element, virtualNics, combineInitCommand(element),alertRuleBOListByNodeId);
                Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, CreateHandler.ElementProcess.CREATE_VIRTUAL_MACHINE, task);
                createVirtualMachineTasks.add(formatTask);
            }
            createTerminalTasks.addAll(createVirtualMachineTasks);
            // 容器
            List<Runnable> createContainerTasks = new ArrayList<>();
            for (CloudPluginSceneElementBO element : filteredContainerElements) {
                String nodeId = element.getSrcElement().getId();
                List<VirtualNic> virtualNics = virtualNicsMap.get(nodeId);
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
                LoadCreateTask task = new LoadCreateTask(getPluginHandler(), element);
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

            // 防火墙
            List<CloudPluginSceneElementBO> filteredFireWallAppElements = this.filterFireWallElements(allElements);
            List<Runnable> createFireWallTasks = new ArrayList<>();
            for (CloudPluginSceneElementBO element : filteredFireWallAppElements) {
                String nodeId = element.getSrcElement().getId();
                List<VirtualNic> virtualNics = this.genVirutalNics(element);
                AzureFireWallCreateTask task = new AzureFireWallCreateTask(resourceService, getPluginHandler(), element, virtualNics);
                Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, ElementProcess.CREATE_FIRE_WALL, task);
                createFireWallTasks.add(formatTask);
            }
            createTerminalTasks.addAll(createFireWallTasks);

            if (!createTerminalTasks.isEmpty()) {
                runSceneProgress(SceneProcess.CREATE_TERMINALS, () -> runTasks(createTerminalTasks));
            }

            getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_INSTANCE);
            getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CloudSceneStage.CONFIG);
            getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CONFIG);

        } catch (Exception e) {
            e.printStackTrace();
//            云场景启动失败立即删除云资源
//            resourceService.getManager().resourceGroups().deleteByName(groupName);
            throw new MpRuntimeException(BaseErrorCode.ERROR, e);
        }
    }

    /**
     * 创建虚拟网络之间的对等互连。
     * 该方法用于检查两个虚拟网络是否已经建立了对等互连，如果没有，则建立对等互连。
     * 对等互连的标识通过两个资源ID的哈希值最小值和最大值组合而成，以确保唯一性。
     *
     * @param resourceId  第一个虚拟网络的资源ID。
     * @param resourceId1 第二个虚拟网络的资源ID。
     * @param calledPairs 已经建立对等互连的虚拟网络对的集合，用于避免重复建立互连。
     */
    private void createVNetPeering(String resourceId, String resourceId1, Set<String> calledPairs) {
        // 创建一个基于参数对的唯一标识符
        String pairId = Math.min(resourceId.hashCode(), resourceId1.hashCode()) + ":" + Math.max(resourceId.hashCode(), resourceId1.hashCode());

        if (!calledPairs.contains(pairId)) {
            Network network = resourceService.getManager().networks().getById(resourceId);
            Network network1 = resourceService.getManager().networks().getById(resourceId1);

            // 对 vNet1 创建到 vNet2 的对等互联
            network.peerings().define(resourceId.substring(resourceId1.length() - 10) + "To" + resourceId1.substring(resourceId1.length() - 10) + "VNet2Peering")
                    .withRemoteNetwork(network1)
                    .create();
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
            if (sceneHandler.isHandleNode(c.getSrcElement().getId()) && !c.getDeriveNetworks().isEmpty()) {
                filterElements.add(c);
            } else if (StringUtils.isNotEmpty(c.getImageType()) && BaseCloudPluginSceneElementModel.ImageType.VNET.equals(c.getImageType())) {
//    筛选出所有VNet节点
                filterElements.add(c);
            }
        }

        return filterElements;
    }


    private List<CloudPluginSceneElementBO> filterVirtualMachineElements(List<CloudPluginSceneElementBO> allElements) {
        List<CloudPluginSceneElementBO> filterElements = new ArrayList<>();
        for (var c : allElements) {
            if (BaseCloudPluginSceneElementModel.ImageType.LAAS_VM.equals(c.getImageType()) && BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE == c.getVirtualType()) {
                filterElements.add(c);
            }
        }
        return filterElements;
    }

    private List<CloudPluginSceneElementBO> filterContainerElements(List<CloudPluginSceneElementBO> allElements) {
        List<CloudPluginSceneElementBO> filterElements = new ArrayList<>();
        for (var c : allElements) {
            if (BaseCloudPluginSceneElementModel.ImageType.CONTAINER_REGISTRY.equals(c.getImageType()) && BaseCloudPluginSceneElementModel.VirtualType.CONTAINER == c.getVirtualType()) {
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
