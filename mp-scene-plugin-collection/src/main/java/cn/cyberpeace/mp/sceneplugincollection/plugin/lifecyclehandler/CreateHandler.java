package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
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
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneProbeRouterPortModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class CreateHandler extends HandlerCommon {

    public static class SceneProcess {
        public static final EventProcess CREATE_FLOW_COLLECTOR = new EventProcess("create_flow_collector", "sceneplugincollection.event.create_flow_collector");
        public static final EventProcess BIND_FLOW_COLLECT_PORTS = new EventProcess("bind_flow_collect_ports", "sceneplugincollection.event.bind_flow_collect_ports");
        public static final EventProcess CREATE_PROBE_ROUTER = new EventProcess("create_probe_router", "sceneplugincollection.event.create_probe_router");
        public static final EventProcess BIND_PROBE_COLLECT_AGENTS = new EventProcess("bind_probe_collect_agents", "sceneplugincollection.event.bind_probe_collect_agents");
    }

    public static class ElementProcess {
        public static final EventProcess BIND_PROBE_COLLECT_AGENT = new EventProcess("bind_probe_collect_agent", "sceneplugincollection.event.bind_probe_collect_agent");
    }

    public CreateHandler(CollectionScenePluginHandler pluginHandler) {
        super(pluginHandler, CreateTaskPool.getInstance(), SceneModel.Event.CREATE);
    }

    protected void bindProbeCollectAgent(CollectionPluginSceneElementBO collectionElement, String probeCollectorId) {
        var srcElement = collectionElement.getSrcElement();
        CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        CloudPluginSceneElementBO cloudElement = cloudPluginHandler.getElement(srcElement.getId());
        if (cloudElement == null) {
            return;
        }

        var cloudResourceId = cloudElement.getResourceId();
        if (cloudResourceId == null) {
            return;
        }
        getProbeCollector().bindAgent(probeCollectorId, cloudResourceId);
    }

    protected void createProbeRouter() {
        var sceneId = getPluginHandler().getSrcScene().getEid();
        var routerPorts = getSceneProbeRouterPortMapper().list(
                new HumanCondition().filterEqual(CollectionPluginSceneProbeRouterPortModel::getScene, sceneId));
        var vrClient = new VirtualRouter();
        
        var createData = new VirtualRouterCreateData();
        createData.setName(HandlerUtils.genResourceName(sceneId));
        List<VirtualNic> virtualNics = new ArrayList<>();
        var externalVirtualNic = new VirtualNic();
        externalVirtualNic.setNetworkId(CloudSingleton.cloudAppConfig.getExternalNetId());
        virtualNics.add(externalVirtualNic);

        CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        routerPorts.forEach(routerPort -> {
            var cloudElement = cloudPluginHandler.getElement(routerPort.getSceneElement());
            String networkId = null;
            if (CollectionUtils.isNotEmpty(cloudElement.getDeriveNetworks())) {
                for (var deriveNet : cloudElement.getDeriveNetworks()) {
                    if (deriveNet.getNetwork().getExcloudIps().contains(routerPort.getIp())) {
                        networkId = deriveNet.getNetwork().getResourceId();
                    }
                }
            }
            if (StringUtils.isEmpty(networkId)) {
                return;
            }
            routerPort.setNetworkId(networkId);
            var virtualNic = new VirtualNic();
            virtualNic.setNetworkId(networkId);
            virtualNic.setFixedIp(routerPort.getIp());
            virtualNics.add(virtualNic);
        });
        getSceneProbeRouterPortMapper().bulkUpdate(routerPorts);
        createData.setNetworks(virtualNics);
        var virtualRouter = vrClient.create(createData);
        getPluginHandler().getScene().setProbeRouter(virtualRouter.getId());
    }

    public void create() {
        prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);

        
        Set<String> collectorVmInstanceIds = new HashSet<>();
        Set<String> collectorContainerInstanceIds = new HashSet<>();
        Set<String> mirrorVmInstanceIds = new HashSet<>();
        Set<String> mirrorContainerInstanceIds = new HashSet<>();
        CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
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
            
            runSceneProgress(SceneProcess.CREATE_FLOW_COLLECTOR, () -> {
                var flowCollectorId = getFlowCollector().createCollector();
                getPluginHandler().getScene().setFlowCollector(flowCollectorId);
            });

            var flowCollectorId = getPluginHandler().getScene().getFlowCollector();
            runSceneProgress(SceneProcess.BIND_FLOW_COLLECT_PORTS, () -> {
                
                getFlowCollector().bindNetworkPorts(sceneId, flowCollectorId, new ArrayList<>(collectorVmInstanceIds),
                        new ArrayList<>(collectorContainerInstanceIds), enableMirror);
            });
        } else if (enableMirror) {
            
            var sceneId = getPluginHandler().getSrcScene().getEid();
            
            getFlowCollector().bindEntityNetworkPorts(sceneId, new ArrayList<>(mirrorVmInstanceIds),
                    new ArrayList<>(mirrorContainerInstanceIds));
        } else if (creatFlowCollector) {
            
            runSceneProgress(SceneProcess.CREATE_FLOW_COLLECTOR, () -> {
                var flowCollectorId = getFlowCollector().createCollector();
                getPluginHandler().getScene().setFlowCollector(flowCollectorId);
            });
        }

        
        var probeCollectorId = getPluginHandler().getScene().getProbeCollector();
        if (!StringUtils.isEmpty(probeCollectorId)) {
            var containerElements = getPluginHandler().getElementMap().values().stream()
                    .filter(e -> Boolean.TRUE.equals(e.getEnableProbeCollect()))
                    .filter(e -> {
                        
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

            runSceneProgress(SceneProcess.CREATE_PROBE_ROUTER, this::createProbeRouter);











        }
    }

    public void bindProbeCollectAgentProcess(CollectionPluginSceneElementBO collectionElement, String probeCollectorId) {
        runElementProgress(collectionElement.getSrcElement().getId(), ElementProcess.BIND_PROBE_COLLECT_AGENT,
                () -> bindProbeCollectAgent(collectionElement, probeCollectorId));
    }
}
