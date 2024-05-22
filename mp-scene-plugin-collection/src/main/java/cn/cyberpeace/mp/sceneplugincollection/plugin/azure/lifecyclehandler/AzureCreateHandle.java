package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.lifecyclehandler;

import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ConnectedNetworkBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSceneStage;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.flowcollect.AzureFlowCollector;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.CreateHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class AzureCreateHandle extends CreateHandler {
    public Integer cloudType;

    private IAzureResourceManager resourceService;

    public AzureCreateHandle(int cloudType, CollectionScenePluginHandler pluginHandler) {
        super(pluginHandler);
        this.cloudType = cloudType;
        this.resourceService = IAzureResourceManager.getInstance(cloudType);
    }

    public void create() {
        prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);

        List<CollectionPluginSceneElementBO> allElements = new ArrayList<>(getPluginHandler().getElementMap().values());
        getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CollectionSceneStage.CREATE_FLOW_COLLECT);
        // TODO 流量镜像应该是基于网络端口的, 这里是基于节点的
        Set<String> collectorVmInstanceIds = new HashSet<>();
        Set<String> collectorContainerInstanceIds = new HashSet<>();
        Set<String> mirrorVmInstanceIds = new HashSet<>();
        Set<String> mirrorContainerInstanceIds = new HashSet<>();
        CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);

        //                    获取网络ID
        String netWorkId = cloudPluginHandler.getElementMap().values()
                .stream()
                .filter(c -> getPluginHandler().getSceneHandler().isHandleNode(c.getSrcElement().getId()) && !c.getDeriveNetworks().isEmpty())
                .flatMap(c -> c.getDeriveNetworks().stream())
                .map(d -> d.getNetwork().getResourceId())
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .get();


        boolean creatFlowCollector = false;
        for (Map.Entry<String, CollectionPluginSceneElementPortBO> entry : getPluginHandler().getElementPortMap().entrySet()) {
            var portId = entry.getKey();
            var collectionPort = entry.getValue();
            if (Boolean.TRUE.equals(collectionPort.getEnableFlowCollect())) {
                creatFlowCollector = true;
                var srcPort = collectionPort.getSrcPort();
                var srcElement = srcPort.getElement();
                var cloudElement = cloudPluginHandler.getElement(srcElement.getId());
                if (cloudElement != null && cloudElement.getVirtualType() != null
                        && !StringUtils.isEmpty(cloudElement.getResourceId())) {
                    if (cloudElement.getVirtualType().equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                        collectorVmInstanceIds.add(cloudElement.getResourceId());
                    } else if (cloudElement.getVirtualType().equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                        collectorContainerInstanceIds.add(cloudElement.getResourceId());
                    }
                }
            }
            if (Boolean.TRUE.equals(collectionPort.getEnableFlowMirror())) {
                var srcPort = collectionPort.getSrcPort();
                var srcElement = srcPort.getElement();
                var cloudElement = cloudPluginHandler.getElement(srcElement.getId());
                if (cloudElement != null && cloudElement.getVirtualType() != null
                        && !StringUtils.isEmpty(cloudElement.getResourceId())) {
                    if (cloudElement.getVirtualType().equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                        mirrorVmInstanceIds.add(cloudElement.getResourceId());
                    } else if (cloudElement.getVirtualType().equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                        mirrorContainerInstanceIds.add(cloudElement.getResourceId());
                    }
                }
            }
        }
        boolean enableCollector = !collectorVmInstanceIds.isEmpty() || !collectorContainerInstanceIds.isEmpty();
        boolean enableMirror = !mirrorVmInstanceIds.isEmpty() || !mirrorContainerInstanceIds.isEmpty();
        if (enableCollector) {
            var sceneId = getPluginHandler().getSrcScene().getEid();
            // 创建流量采集器
            String finalNetWorkId = netWorkId;
            runSceneProgress(SceneProcess.CREATE_FLOW_COLLECTOR, () -> {
//                var flowCollectorId = getFlowCollector().createCollector();
//                创建AzureArkime
                var flowCollectorId = new AzureFlowCollector(getFlowCollector(), resourceService, getPluginHandler(), finalNetWorkId)
                        .createCollector();
                getPluginHandler().getScene().setFlowCollector(flowCollectorId);
            });

            var flowCollectorId = getPluginHandler().getScene().getFlowCollector();
            runSceneProgress(SceneProcess.BIND_FLOW_COLLECT_PORTS, () -> {
                // 绑定网络端口到流量采集器
                getFlowCollector().bindNetworkPorts(sceneId, flowCollectorId, new ArrayList<>(collectorVmInstanceIds),
                        new ArrayList<>(collectorContainerInstanceIds), enableMirror);
            });
        } else if (enableMirror) {
            // 镜像流量
            var sceneId = getPluginHandler().getSrcScene().getEid();
            // 绑定网络端口到流量采集器
            getFlowCollector().bindEntityNetworkPorts(sceneId, new ArrayList<>(mirrorVmInstanceIds),
                    new ArrayList<>(mirrorContainerInstanceIds));
        } else if (creatFlowCollector) {
            // 只创建流量采集器
            runSceneProgress(SceneProcess.CREATE_FLOW_COLLECTOR, () -> {
                var flowCollectorId = getFlowCollector().createCollector();
                getPluginHandler().getScene().setFlowCollector(flowCollectorId);
            });
        }
        getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CollectionSceneStage.CREATE_FLOW_COLLECT);

        // 绑定探针agent到探针采集器
        var probeCollectorId = getPluginHandler().getScene().getProbeCollector();
        if (!StringUtils.isEmpty(probeCollectorId)) {
            var containerElements = getPluginHandler().getElementMap().values().stream()
                    .filter(e -> Boolean.TRUE.equals(e.getEnableProbeCollect()))
                    .filter(e -> {
                        // 非macvlan模式下的容器 且 有探针安装需要的可以加路由
                        var cloudElement = cloudPluginHandler.getElement(e.getSrcElement().getId());
                        if (cloudElement == null || cloudElement.getVirtualType() == null) {
                            return false;
                        }
                        if (cloudElement.getVirtualType() != BaseCloudPluginSceneElementModel.VirtualType.CONTAINER) {
                            return false;
                        }
                        List<ConnectedNetworkBO> allNets = new ArrayList<>();
                        allNets.addAll(Optional.ofNullable(cloudElement.getAccessNetworks()).orElse(new ArrayList<>()));
                        allNets.addAll(Optional.ofNullable(cloudElement.getDeriveNetworks()).orElse(new ArrayList<>()));
                        boolean normalNet = true;
                        for (var net : allNets) {
                            net.getNetwork().getNetMode();
                            if (BaseCloudPluginSceneElementModel.NetMode.MACVLAN.equals(net.getNetwork().getNetMode())) {
                                normalNet = false;
                                break;
                            }
                        }
                        return normalNet;
                    })
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(containerElements)) {
                Container container = new Container();
                var addRouterTasks = containerElements.stream().filter(c -> {
                            var cloudElement = cloudPluginHandler.getElement(c.getSrcElement().getId());
                            if (cloudElement != null && StringUtils.isNotEmpty(cloudElement.getResourceId())) {
                                return true;
                            } else {
                                return false;
                            }
                        })
                        .map(e -> {
                            var cloudElement = cloudPluginHandler.getElement(e.getSrcElement().getId());
                            return (Runnable) () -> container.addRoute(cloudElement.getResourceId());
                        })
                        .collect(Collectors.toList());
                runTasks(addRouterTasks);
            }

            getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CollectionSceneStage.CREATE_DATA_COLLECT);

            runSceneProgress(SceneProcess.CREATE_PROBE_ROUTER, this::createProbeRouter);

            getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CollectionSceneStage.CREATE_DATA_COLLECT);
//            var probeCollectionElements = getPluginHandler().getElementMap().values()
//                    .stream()
//                    .filter(collectionElement -> !CollectionUtils.isEmpty(collectionElement.getProbes()))
//                    .collect(Collectors.toList());
//            var bindProbeCollectAgentTasks = probeCollectionElements.stream()
//                    .map(collectionElement -> (Runnable)() -> bindProbeCollectAgentProcess(collectionElement,
//                            probeCollectorId))
//                    .collect(Collectors.toList());
//            if (!bindProbeCollectAgentTasks.isEmpty()) {
//                runSceneProgress(SceneProcess.BIND_PROBE_COLLECT_AGENTS, () -> runTasks(bindProbeCollectAgentTasks));
//            }
        }
    }
}
