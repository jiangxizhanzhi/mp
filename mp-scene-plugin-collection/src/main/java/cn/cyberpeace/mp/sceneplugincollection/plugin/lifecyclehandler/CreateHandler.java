package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.core.event.base.SceneEventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualRouter;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ConnectedNetworkBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSceneStage;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneProbeRouterPortModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class CreateHandler extends HandlerCommon {

    public static class SceneProcess {
        public static final SceneEventProcess CREATE_FLOW_COLLECTOR = new SceneEventProcess("create_flow_collector", "sceneplugincollection.event.create_flow_collector", "create_flow_collect", 1);
        public static final SceneEventProcess BIND_FLOW_COLLECT_PORTS = new SceneEventProcess("bind_flow_collect_ports", "sceneplugincollection.event.bind_flow_collect_ports", "create_flow_collect", 2);
        public static final SceneEventProcess CREATE_PROBE_ROUTER = new SceneEventProcess("create_probe_router", "sceneplugincollection.event.create_probe_router", "create_data_collect", 1);
    }

    public static class ElementProcess {
        public static final EventProcess BIND_PROBE_COLLECT_AGENT = new EventProcess("bind_probe_collect_agent", "sceneplugincollection.event.bind_probe_collect_agent");
    }

    public CreateHandler(CollectionScenePluginHandler pluginHandler) {
        super(pluginHandler, CreateTaskPool.getInstance(), SceneModel.Event.CREATE);
    }

    protected void bindProbeCollectAgent(CollectionPluginSceneElementBO collectionElement, String probeCollectorId) {
        SceneElementBO srcElement = collectionElement.getSrcElement();
        CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        CloudPluginSceneElementBO cloudElement = cloudPluginHandler.getElement(srcElement.getId());
        if (cloudElement == null) {
            return;
        }

        String cloudResourceId = cloudElement.getResourceId();
        if (cloudResourceId == null) {
            return;
        }
        getProbeCollector().bindAgent(probeCollectorId, cloudResourceId);
    }

    protected void createProbeRouter() {
        String sceneId = getPluginHandler().getSrcScene().getEid();
        List<CollectionPluginSceneProbeRouterPortModel> routerPorts = getSceneProbeRouterPortMapper().list(
                new HumanCondition().filterEqual(CollectionPluginSceneProbeRouterPortModel::getScene, sceneId));
        // 将业务网络分组 保证每个组内没有重复的网段
        List<Map<String, CollectionPluginSceneProbeRouterPortModel>> groupPort = new ArrayList<>();
        for (var rp : routerPorts) {
            String cidr = rp.getIp().substring(0, rp.getIp().lastIndexOf("."));
            boolean addNew = true;
            for (var gp : groupPort) {
                if (!gp.containsKey(cidr)) {
                    gp.put(cidr, rp);
                    addNew = false;
                    break;
                }
            }
            if (addNew) {
                var gp = new HashMap<String, CollectionPluginSceneProbeRouterPortModel>();
                gp.put(cidr, rp);
                groupPort.add(gp);
            }
        }
        List<String> probeRouters = new ArrayList<>();
        for (var gp : groupPort) {
            VirtualRouterCreateData virtualRouter = getVirtualRouterCreateData(sceneId, new ArrayList<>(gp.values()));
            probeRouters.add(virtualRouter.getId());
        }
        getPluginHandler().getScene().setProbeRouter(StringUtils.joinWith(",", probeRouters));
    }

    private VirtualRouterCreateData getVirtualRouterCreateData(String sceneId, List<CollectionPluginSceneProbeRouterPortModel> routerPorts) {
        VirtualRouter vrClient = new VirtualRouter();
        // 创建辅助路由
        VirtualRouterCreateData createData = new VirtualRouterCreateData();
        createData.setName(HandlerUtils.genResourceName(sceneId));
        List<VirtualNic> virtualNics = new ArrayList<>();
        VirtualNic externalVirtualNic = new VirtualNic();
        externalVirtualNic.setNetworkId(CloudSingleton.cloudAppConfig.getExternalNetId());
        virtualNics.add(externalVirtualNic);

        CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        routerPorts.forEach(routerPort -> {
            CloudPluginSceneElementBO cloudElement = cloudPluginHandler.getElement(routerPort.getSceneElement());
            String networkId = null;
            if (CollectionUtils.isNotEmpty(cloudElement.getDeriveNetworks())) {
                for (ConnectedNetworkBO deriveNet : cloudElement.getDeriveNetworks()) {
                    if (deriveNet.getNetwork().getExcloudIps().contains(routerPort.getIp())) {
                        networkId = deriveNet.getNetwork().getResourceId();
                    }
                }
            }
            if (StringUtils.isEmpty(networkId)) {
                return;
            }
            routerPort.setNetworkId(networkId);
            VirtualNic virtualNic = new VirtualNic();
            virtualNic.setNetworkId(networkId);
            virtualNic.setFixedIp(routerPort.getIp());
            virtualNics.add(virtualNic);
        });
        getSceneProbeRouterPortMapper().bulkUpdate(routerPorts);
        createData.setNetworks(virtualNics);
        VirtualRouterCreateData virtualRouter = vrClient.create(createData);
        return virtualRouter;
    }

    public void create() {
        prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);

        getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CollectionSceneStage.CREATE_FLOW_COLLECT);
        // TODO 流量镜像应该是基于网络端口的, 这里是基于节点的
        Set<String> collectorVmInstanceIds = new HashSet<>();
        Set<String> collectorContainerInstanceIds = new HashSet<>();
        Set<String> mirrorVmInstanceIds = new HashSet<>();
        Set<String> mirrorContainerInstanceIds = new HashSet<>();
        CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        boolean creatFlowCollector = false;
        for (Map.Entry<String, CollectionPluginSceneElementPortBO> entry : getPluginHandler().getElementPortMap().entrySet()) {
            String portId = entry.getKey();
            CollectionPluginSceneElementPortBO collectionPort = entry.getValue();
            if (Boolean.TRUE.equals(collectionPort.getEnableFlowCollect())) {
                creatFlowCollector = true;
                SceneElementPortBO srcPort = collectionPort.getSrcPort();
                SceneElementBO srcElement = srcPort.getElement();
                CloudPluginSceneElementBO cloudElement = cloudPluginHandler.getElement(srcElement.getId());
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
                SceneElementPortBO srcPort = collectionPort.getSrcPort();
                SceneElementBO srcElement = srcPort.getElement();
                CloudPluginSceneElementBO cloudElement = cloudPluginHandler.getElement(srcElement.getId());
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
            String sceneId = getPluginHandler().getSrcScene().getEid();
            // 创建流量采集器
            runSceneProgress(SceneProcess.CREATE_FLOW_COLLECTOR, () -> {
                String flowCollectorId = getFlowCollector().createCollector();
                getPluginHandler().getScene().setFlowCollector(flowCollectorId);
            });

            String flowCollectorId = getPluginHandler().getScene().getFlowCollector();
            runSceneProgress(SceneProcess.BIND_FLOW_COLLECT_PORTS, () -> {
                // 绑定网络端口到流量采集器
                getFlowCollector().bindNetworkPorts(sceneId, flowCollectorId, new ArrayList<>(collectorVmInstanceIds),
                        new ArrayList<>(collectorContainerInstanceIds), enableMirror);
            });
        } else if (enableMirror) {
            // 镜像流量
            String sceneId = getPluginHandler().getSrcScene().getEid();
            // 绑定网络端口到流量采集器
            getFlowCollector().bindEntityNetworkPorts(sceneId, new ArrayList<>(mirrorVmInstanceIds),
                    new ArrayList<>(mirrorContainerInstanceIds));
        } else if (creatFlowCollector) {
            // 只创建流量采集器
            runSceneProgress(SceneProcess.CREATE_FLOW_COLLECTOR, () -> {
                String flowCollectorId = getFlowCollector().createCollector();
                getPluginHandler().getScene().setFlowCollector(flowCollectorId);
            });
        }
        getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CollectionSceneStage.CREATE_FLOW_COLLECT);

        // 绑定探针agent到探针采集器
        String probeCollectorId = getPluginHandler().getScene().getProbeCollector();
        if (!StringUtils.isEmpty(probeCollectorId)) {
            List<CollectionPluginSceneElementBO> containerElements = getPluginHandler().getElementMap().values().stream()
                    .filter(e -> Boolean.TRUE.equals(e.getEnableProbeCollect()))
                    .filter(e -> {
                        // 非macvlan模式下的容器 且 有探针安装需要的可以加路由
                        CloudPluginSceneElementBO cloudElement = cloudPluginHandler.getElement(e.getSrcElement().getId());
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
                        for (ConnectedNetworkBO net : allNets) {
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
                List<Runnable> addRouterTasks = containerElements.stream().filter(c -> {
                    CloudPluginSceneElementBO cloudElement = cloudPluginHandler.getElement(c.getSrcElement().getId());
                            if (cloudElement != null && StringUtils.isNotEmpty(cloudElement.getResourceId())) {
                                return true;
                            } else {
                                return false;
                            }
                        })
                        .map(e -> {
                            CloudPluginSceneElementBO cloudElement = cloudPluginHandler.getElement(e.getSrcElement().getId());
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

    public void bindProbeCollectAgentProcess(CollectionPluginSceneElementBO collectionElement, String probeCollectorId) {
        runElementProgress(collectionElement.getSrcElement().getId(), ElementProcess.BIND_PROBE_COLLECT_AGENT,
                () -> bindProbeCollectAgent(collectionElement, probeCollectorId));
    }
}
