package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
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
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
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
            List<Runnable> createAuxiliaryNetworkTasks = new ArrayList<>();
            for (CloudPluginSceneElementBO element : filteredAuxiliaryNetworkElements) {
                //筛选出所有cidr是否连接容器
                CloudScenePluginHandler pluginHandler = getPluginHandler();
                ConcurrentHashMap<String, Boolean> cidrContainerMap = ElementNetworkUtils.getCidrToContainerMap(pluginHandler, allElements, element, virtualNicsMap);
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
            getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_NETWORK);

            // 再创建网关 和 防火墙
            getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CloudSceneStage.CREATE_GATEWAY);
            getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_GATEWAY);

            /*
            // 先创建辅助网络(包含虚拟交换机)
            // 先创建辅助网络
            List<CloudPluginSceneElementBO> filteredAuxiliaryNetworkElements = this.filterAuxiliaryNetworkElements(allElements);
            List<Runnable> createAuxiliaryNetworkTasks = new ArrayList<>();
            //筛选出所有cidr是否连接容器
            ConcurrentHashMap<String, Boolean> cidrContainerMap = ElementNetworkUtils.getCidrContainerMap(allElements);

            if (!filteredAuxiliaryNetworkElements.isEmpty()) {
                String nodeIdd = filteredAuxiliaryNetworkElements.get(0).getSrcElement().getId();
                // 封装创建网络的任务
                AzureVirtualNetCreateTask taskk = new AzureVirtualNetCreateTask(resourceService, getPluginHandler(), filteredAuxiliaryNetworkElements.get(0), cidrContainerMap);
                // 格式化任务执行过程，添加 runElementProgress
                Runnable formatTaskk = this.formatElementTask(nodeIdd, stepPercentage, CreateHandler.ElementProcess.CREATE_AUXILIARY_NETWORK, taskk);
                // 添加格式化后的任务到列表
                createAuxiliaryNetworkTasks.add(formatTaskk);

                if (!createAuxiliaryNetworkTasks.isEmpty()) {
                    // 执行 创建所有网络
                    runSceneProgress(SceneProcess.CREATE_AUXILIARY_NETWORKS, () -> runTasks(createAuxiliaryNetworkTasks));
                }

//        保存网络id
                String azureNetworkId = filteredAuxiliaryNetworkElements.get(0).getDeriveNetworks().get(0).getNetwork().getResourceId();
                filteredAuxiliaryNetworkElements.stream()
                        .flatMap(cloudElement -> cloudElement.getDeriveNetworks().stream())
                        .forEach(cloudElement -> cloudElement.getNetwork().setResourceId(azureNetworkId));
            }*/


            getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CloudSceneStage.CREATE_INSTANCE);

            // 再创建终端 终端分虚拟机、容器、和web服务


            List<Runnable> createTerminalTasks = new ArrayList<>();
            // 虚拟机
            List<Runnable> createVirtualMachineTasks = new ArrayList<>();
            for (CloudPluginSceneElementBO element : filteredVirtualMachineElements) {
                String nodeId = element.getSrcElement().getId();
                List<VirtualNic> virtualNics = virtualNicsMap.get(nodeId);
//                获取节点对应VNet
                List<ConnectedNetworkBO> accessNetworks = element.getAccessNetworks();
                SceneElementBO vNetElement;
                if (accessNetworks.size() > 0) {
                    vNetElement = accessNetworks.get(0).getConnectPorts().get(0).getTo().getElement();
                    if (null != vNetElement) {
                        CloudPluginSceneElementBO vNetPluginElement = getPluginHandler().getElement(vNetElement.getId());
                        List<String> cidrPools = vNetPluginElement.getCidrPools();
                        if (cidrPools.size() > 0) {
                            virtualNics.forEach(virtualNic -> {
                                String fixedIp = virtualNic.getFixedIp();
                                String cidr = IpUtils.ip2Cidr24(fixedIp);
//                  判断网络是否在VNet子网内
                                boolean contains = cidrPools.contains(cidr);
                                if (!contains) {
//                    不在则更新网络ip 需校验ip未使用+非Azure保留ip
                                    List<String> nodeIds = vNetPluginElement.getDeriveNetworks().stream()
                                            .flatMap(connectedNetworkBO -> connectedNetworkBO.getToPorts().stream())
                                            .map(SceneElementPortBO::getElement)
                                            .map(SceneElementBO::getId)
                                            .distinct()
                                            .collect(Collectors.toList());

                                    Set<String> allFixedIps = nodeIds.stream()
                                            .map(virtualNicsMap::get)
                                            .flatMap(List::stream)
                                            .map(VirtualNic::getFixedIp)
                                            .collect(Collectors.toSet());

                                    String[] specificIPsFromCIDR = IpUtils.getSpecificIPsFromCIDR(cidrPools.get(0));
                                    allFixedIps.addAll(Arrays.asList(specificIPsFromCIDR));
                                    String ip = IpUtils.generateRandomIpNotInList(cidrPools.get(0), allFixedIps);
//                                更新ip
                                    virtualNic.setFixedIp(ip);
                                }
                            });
                        }
                    }
                }
                //            创建虚拟机时不允许扩容
                element.getSrcElement().setExpansionDisk(null);
                element.setVmExpansionDiskType(null);
//                处理云场景无登录按钮、接收虚拟端口信息
                receiveVirtualPortInfo(element, virtualNics);
                AzureVirtualMachineCreateTask task = new AzureVirtualMachineCreateTask(resourceService, getPluginHandler(), element, virtualNics, combineInitCommand(element));
                Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, CreateHandler.ElementProcess.CREATE_VIRTUAL_MACHINE, task);
                createVirtualMachineTasks.add(formatTask);

                /* 修改前
                String nodeId = element.getSrcElement().getId();
                List<VirtualNic> virtualNics = this.genVirutalNics(element);
//            创建虚拟机时不允许扩容
                element.getSrcElement().setExpansionDisk(null);
                element.setVmExpansionDiskType(null);
                receiveVirtualPortInfo(element, virtualNics);
                AzureVirtualMachineCreateTask task = new AzureVirtualMachineCreateTask(resourceService, getPluginHandler(), element, virtualNics, combineInitCommand(element));
                Runnable formatTask = this.formatElementTask(nodeId, stepPercentage, CreateHandler.ElementProcess.CREATE_VIRTUAL_MACHINE, task);
                createVirtualMachineTasks.add(formatTask);*/
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


            getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CREATE_INSTANCE);
            getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CloudSceneStage.CONFIG);
            getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CloudSceneStage.CONFIG);

        } catch (Exception e) {
//            云场景启动失败立即删除云资源
            resourceService.getManager().resourceGroups().deleteByName(groupName);
            throw new MpRuntimeException(BaseErrorCode.ERROR, e);
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

        virtualNics.forEach(virtualNic -> {
            var networkId = virtualNic.getNetworkId();
            // 管理网卡判断
            if (Boolean.TRUE.equals(cloudElement.getEnableMgmt())
                    && CloudSingleton.cloudAppConfig.getMgmtNetId().equals(networkId)) {
                cloudElement.setMgmtPortResourceId(virtualNic.getPortId());
                cloudElement.setMgmtIp(virtualNic.getFixedIp());
                cloudElement.setMgmtMacAddr(virtualNic.getMacAddress());
                return;
            }

            var networkCloudPorts = networkIdCloudPortsMap.getOrDefault(networkId, null);
            if (networkCloudPorts == null) {
                return;
            }

            // 多个端口连到同一个网络上的时候暂时当做一个端口处理
            networkCloudPorts.forEach(cloudPort -> {
                var srcPort = cloudPort.getSrcPort();
                if (srcPort.isUsing()) {
                    srcPort.setIp(virtualNic.getFixedIp());
                    srcPort.setMacAddr(virtualNic.getMacAddress());
                }

                cloudPort.setResourceId(virtualNic.getPortId());
                cloudPort.setQosId(virtualNic.getQosId());
                cloudPort.setFloatingIp(virtualNic.getFloatingip());
                cloudPort.setFloatingIpId(virtualNic.getFloatingipId());
                cloudPort.setAuxiliaryNetworkId(virtualNic.getNetworkId());

                if (null != virtualNic.getAccessModes() && null != virtualNic.getAccessModes().get(0)) {
                    VirtualNic.AccessMode accessMode = virtualNic.getAccessModes().get(0);
                    String protocol = accessMode.getProtocol();
                    String[] protoclos = protocol.split(";");
                    List<CloudAccessMode> accessModes = cloudPort.getAccessModes();
                    accessModes.clear();
                    for (int j = 0; j < protoclos.length; j++) {
                        CloudAccessMode cloudAccessMode = new CloudAccessMode();
//                        TODO 管理网络ip port
                        cloudAccessMode.setAccessIp(virtualNic.getFixedIp());
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
        });
    }
}
