package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSceneStage;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.CreateHandler;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class AzureCreateHandler extends CreateHandler {
    public Integer cloudType;
    private String groupNamePrefix;
    private IAzureResourceManager resourceService;

    public AzureCreateHandler(int cloudType, CollectionScenePluginHandler pluginHandler) {
        super(pluginHandler);
        this.cloudType = cloudType;
        this.resourceService = IAzureResourceManager.getInstance(cloudType);
        this.groupNamePrefix = BeanCommon.getBean(SituationAwareConfig.class).getGroupNamePrefix();
    }

    public void create() {
        prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);
        getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CollectionSceneStage.CREATE_FLOW_COLLECT);
        getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CollectionSceneStage.CREATE_FLOW_COLLECT);

        getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(CollectionSceneStage.CREATE_DATA_COLLECT);
        getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CollectionSceneStage.CREATE_DATA_COLLECT);

        try {
            CloudScenePluginHandler pluginHandler = getPluginHandler().getSceneHandler().getPluginHandler(PluginConstant.PLUGIN_KEY);

            List<CloudPluginSceneElementBO> terminalExcludeNpcElements = pluginHandler.getTerminalExcludeNpcElements();
            if (terminalExcludeNpcElements.size() > 0) {
                super.create();
            }


//            TODO arkime暂停开发
            /*prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);

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
//                通过判断ResourceId来确定节点是否启动成功 筛选出来用于绑定流量镜像
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
            String finalNetWorkId = netWorkId;
            if (enableCollector) {
                var sceneId = getPluginHandler().getSrcScene().getEid();
                // 创建流量采集器
                runSceneProgress(SceneProcess.CREATE_FLOW_COLLECTOR, () -> {
//                创建AzureArkime
                    var flowCollectorId = new AzureFlowCollector(getFlowCollector(), resourceService, getPluginHandler(), finalNetWorkId)
                            .createCollector();
                    getPluginHandler().getScene().setFlowCollector(flowCollectorId);
                });

                var flowCollectorId = getPluginHandler().getScene().getFlowCollector();
                runSceneProgress(SceneProcess.BIND_FLOW_COLLECT_PORTS, () -> {
                    // 绑定网络端口到流量采集器  场景绑定流量镜像
                    getFlowCollector().bindNetworkPorts(sceneId, flowCollectorId, new ArrayList<>(collectorVmInstanceIds),
                            new ArrayList<>(collectorContainerInstanceIds), enableMirror);
                });
            } else if (enableMirror) {
                // 镜像流量
                var sceneId = getPluginHandler().getSrcScene().getEid();
                // 绑定网络端口到流量采集器  场景绑定流量镜像
                getFlowCollector().bindEntityNetworkPorts(sceneId, new ArrayList<>(mirrorVmInstanceIds),
                        new ArrayList<>(mirrorContainerInstanceIds));
            } else if (creatFlowCollector) {
                // 只创建流量采集器
                runSceneProgress(SceneProcess.CREATE_FLOW_COLLECTOR, () -> {
                    var flowCollectorId = new AzureFlowCollector(getFlowCollector(), resourceService, getPluginHandler(), finalNetWorkId)
                            .createCollector();
                    getPluginHandler().getScene().setFlowCollector(flowCollectorId);
                });
            }
//        arkime创建绑定流量镜像结束
            getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CollectionSceneStage.CREATE_FLOW_COLLECT);

//        增加容器节点辅助路由
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

                // runSceneProgress(SceneProcess.CREATE_PROBE_ROUTER, this::createProbeRouter);

                getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(CollectionSceneStage.CREATE_DATA_COLLECT);

            }*/
        } catch (Exception e) {
//            云场景启动失败立即删除云资源
            String groupName = getPluginHandler().getSourceSceneId();
            groupName = StringUtils.isNotBlank(groupNamePrefix) ? groupNamePrefix + "-" + groupName : groupName;
            resourceService.getManager().resourceGroups().deleteByName(groupName);
        }
    }
}
