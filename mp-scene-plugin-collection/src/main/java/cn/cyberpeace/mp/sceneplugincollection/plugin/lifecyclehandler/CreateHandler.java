package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.core.event.base.SceneEventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CreateMirrorData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ConnectedNetworkBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.ArkimeCreateResBo;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.FlowMirrorCreateRes;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSceneStage;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneProbeRouterPortModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
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
                .getPluginHandler(PluginConstant.PLUGIN_KEY);
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

    protected void updateProbeRouterPort() {
        String sceneId = getPluginHandler().getSrcScene().getEid();
        List<CollectionPluginSceneProbeRouterPortModel> routerPorts = getSceneProbeRouterPortMapper().list(
                new HumanCondition().filterEqual(CollectionPluginSceneProbeRouterPortModel::getScene, sceneId));
        CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                .getPluginHandler(PluginConstant.PLUGIN_KEY);
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
        });
        getSceneProbeRouterPortMapper().bulkUpdate(routerPorts);
    }

    public void create() {
        prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);

        getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CollectionSceneStage.CREATE_FLOW_COLLECT);

        // TODO 流量镜像应该是基于网络端口的, 这里是基于节点的

        CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                .getPluginHandler(PluginConstant.PLUGIN_KEY);

        // 1. 统计需要流量镜像的节点
        List<CreateMirrorData.Param> totalMirrorParams = new ArrayList<>();
        cloudPluginHandler.getElementMap().keySet().forEach(nodeId -> {
            List<CreateMirrorData.Param> params = getNodeFlowMirrorParam(nodeId);
            totalMirrorParams.addAll(params);
        });

        // 2. 创建arkime 和流量镜像
        runSceneProgress(SceneProcess.CREATE_FLOW_COLLECTOR, () -> {
            try {
                ArkimeCreateResBo arkimeCreateResBo = getFlowCollector().createCollector();
                getPluginHandler().getScene().setFlowCollector(arkimeCreateResBo.getResourceId());

                FlowMirrorCreateRes flowMirrorCreateRes = getFlowCollector().createFlowMirror(arkimeCreateResBo.getIp(),
                        getPluginHandler().getSceneHandler().getSceneId(), totalMirrorParams);

                getPluginHandler().getScene().setFlowMirrorId(flowMirrorCreateRes.getFlowMirrorId());
                getPluginHandler().getScene().setOptionKey(Collections.singletonList(flowMirrorCreateRes.getOptionKey()));
            } catch (Exception e) {
                log.error("创建arkime流量采集器失败");
                log.error(e.getMessage(), e);
            }
        });



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

            runSceneProgress(SceneProcess.CREATE_PROBE_ROUTER, this::updateProbeRouterPort);

            getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CollectionSceneStage.CREATE_DATA_COLLECT);




        }
        getPluginHandler().getScene().setProbeRouter(getPluginHandler().getSceneHandler().getProbeRouter());
    }

    public void bindProbeCollectAgentProcess(CollectionPluginSceneElementBO collectionElement, String probeCollectorId) {
        runElementProgress(collectionElement.getSrcElement().getId(), ElementProcess.BIND_PROBE_COLLECT_AGENT,
                () -> bindProbeCollectAgent(collectionElement, probeCollectorId));
    }

    public List<CreateMirrorData.Param> getNodeFlowMirrorParam(String nodeId) {
        CloudScenePluginHandler cloudPlugin = getPluginHandler().getSceneHandler().getPluginHandler(PluginConstant.PLUGIN_KEY);

        var cloudElement = cloudPlugin.getElement(nodeId);
        if (cloudElement == null) {
            log.error("flow collect cloudElement is null");
            return Collections.emptyList();
        }
        if (BaseCloudPluginSceneElementModel.ImageType.ENTITY.equals(cloudElement.getImageType())) {
            return Collections.emptyList();
        }
        if (cloudElement.getSrcElement() == null) {
            log.error("flow collect src element is null");
            return Collections.emptyList();
        }
        if (BaseSceneElementModel.Type.SWITCH == cloudElement.getSrcElement().getType()) {
            var deriveNetworks = cloudElement.getDeriveNetworks();
            if (CollectionUtils.isEmpty(deriveNetworks)) {
                return Collections.emptyList();
            }
            Set<String> networkIds = deriveNetworks.stream()
                    .map(connectedNetworkBO -> connectedNetworkBO.getNetwork().getResourceId())
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toSet());
            List<CreateMirrorData.Param> params = new ArrayList<>();
            for (String networkId : networkIds) {
                CreateMirrorData.Param param = new CreateMirrorData.Param();
                param.setId(networkId);
                param.setType(CreateMirrorData.Type.NETWORK);
                params.add(param);
            }
            return params;
        }
        if (!BaseCloudPluginSceneElementModel.ImageType.VM.equals(cloudElement.getImageType())
                && !BaseCloudPluginSceneElementModel.ImageType.CONTAINER.equals(cloudElement.getImageType())) {
            return Collections.emptyList();
        }
        return this.genCreateMirrorParam(cloudPlugin, cloudElement);
    }


    private List<CreateMirrorData.Param> genCreateMirrorParam(CloudScenePluginHandler cloudPlugin, CloudPluginSceneElementBO cloudElement) {
        List<CreateMirrorData.Param> srcParam = new ArrayList<>();
        var cloudPorts = cloudPlugin.getPorts(cloudElement);
        if (CollectionUtils.isEmpty(cloudPorts)) {
            return srcParam;
        }
        cloudPorts.forEach(cloudPort -> {
            if (StringUtils.isEmpty(cloudPort.getVnicId())) {
                return;
            }
            CreateMirrorData.Param param = new CreateMirrorData.Param();
            param.setId(cloudElement.getResourceId());
            param.setNicId(cloudPort.getVnicId());
            if (BaseCloudPluginSceneElementModel.ImageType.VM.equals(cloudElement.getImageType())) {
                param.setType(CreateMirrorData.Type.VM);
            } else {
                param.setType(CreateMirrorData.Type.DOCKER);
            }
            srcParam.add(param);
        });
        return srcParam;
    }




}
