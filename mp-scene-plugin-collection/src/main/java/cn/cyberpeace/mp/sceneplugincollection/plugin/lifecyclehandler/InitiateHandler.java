package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.HostRoute;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneLogProbeLocationModel;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneProbeRouterPortModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert.ConvertFileParams;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert.ConvertLogParams;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert.ConvertRegistryParams;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert.ConvertScriptParams;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneLogProbeLocationMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class InitiateHandler extends HandlerCommon {

    public static class SceneProcess {
        public static final EventProcess INITIATE_FLOW_COLLECTION = new EventProcess("initiate_flow_collection", "sceneplugincollection.event.initiate_flow_collection");
        public static final EventProcess INITIATE_PROBE_COLLECTION = new EventProcess("initiate_probe_collection", "sceneplugincollection.event.initiate_probe_collection");
        public static final EventProcess CREATE_PROBE_COLLECTOR = new EventProcess("create_probe_collector", "sceneplugincollection.event.create_probe_collector");
        public static final EventProcess INITIATE_PROBE_ROUTER = new EventProcess("initiate_probe_router", "sceneplugincollection.event.initiate_probe_router");
        public static final EventProcess INITIATE_PROBES = new EventProcess("initiate_probes", "sceneplugincollection.event.initiate_probes");
    }

    public static class ElementProcess {
    }

    public InitiateHandler(CollectionScenePluginHandler pluginHandler) {
        super(pluginHandler, CreateTaskPool.getInstance(), SceneModel.Event.INITIATE);
    }

    public void initiate() {
        prepareEventProcess(SceneModel.Event.INITIATE, SceneProcess.class, ElementProcess.class);

        var scene = getPluginHandler().getScene();
        // 全局开启流量采集
        if (Boolean.TRUE.equals(CollectionSingleton.collectionAppConfig.getEnableFlowCollect())
                || Boolean.TRUE.equals(scene.getEnableFlowCollect())) {
            CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                    .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
            runSceneProgress(SceneProcess.INITIATE_FLOW_COLLECTION, () -> {
                cloudPluginHandler.getElementPortMap().forEach((portId, srcPort) -> {
                    String nodeId = srcPort.getSrcPort().getElement().getId();
                    CollectionPluginSceneElementBO collectionElement = getPluginHandler().getElement(nodeId);
                    if (collectionElement == null) {
                        collectionElement = new CollectionPluginSceneElementBO();
                        collectionElement.setEnableProbeCollect(false);
                        getPluginHandler().addElement(nodeId, collectionElement);
                        getPluginHandler().getSceneHandler().addElementPlugin(PluginConstant.PLUGIN_KEY, nodeId, JsonCommon.beanToSnakeMap(collectionElement));
                    }
                    var collectionPort = getPluginHandler().getPort(portId);
                    if (collectionPort == null) {
                        collectionPort = new CollectionPluginSceneElementPortBO();
                        getPluginHandler().addPort(portId, collectionPort);
                        getPluginHandler().getSceneHandler().addElementPortPlugin(PluginConstant.PLUGIN_KEY, portId, JsonCommon.beanToSnakeMap(collectionPort));
                    }
                    collectionPort.setEnableFlowCollect(true);
                });
            });
        }

        // 全局开启探针采集
        if (Boolean.TRUE.equals(CollectionSingleton.collectionAppConfig.getEnableProbeCollect())
                || Boolean.TRUE.equals(scene.getEnableProbeCollect())) {
            CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                    .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
            runSceneProgress(SceneProcess.INITIATE_PROBE_COLLECTION, () -> {
                cloudPluginHandler.getElementMap().forEach((nodeId, cloudElement) -> {
                    // TODO: 只支持虚拟机
                    var virtualType = cloudElement.getVirtualType();
                    if (virtualType == null
                            || virtualType != BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                        return;
                    }

                    var collectionElement = getPluginHandler().getElement(nodeId);
                    if (collectionElement == null) {
                        collectionElement = new CollectionPluginSceneElementBO();
                        getPluginHandler().addElement(nodeId, collectionElement);
                    }
                    collectionElement.setEnableProbeCollect(true);
                });
            });
        }

        var probeCollectionElements = getPluginHandler().getElementMap().values()
                .stream()
                .filter(collectionElement -> Boolean.TRUE.equals(collectionElement.getEnableProbeCollect()))
                .collect(Collectors.toList());
        if (!probeCollectionElements.isEmpty()) {
            // 准备采集路由
            runSceneProgress(SceneProcess.INITIATE_PROBE_ROUTER, () -> {
                final String externalNetCidr = CloudSingleton.cloudAppConfig.getExternalNetCidr();
                if (StringUtils.isEmpty(externalNetCidr)) {
                    return;
                }

                CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                        .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
                List<CollectionPluginSceneProbeRouterPortModel> routerPorts = new ArrayList<>();
                cloudPluginHandler.getElementMap().forEach((nodeId, cloudElement) -> {
                    if (cloudElement.getDeriveNetworks().isEmpty()) {
                        return;
                    }
                    if (Boolean.TRUE.equals(cloudElement.getExternal())) {
                        return;
                    }
                    for (var network : cloudElement.getDeriveNetworks()) {
                        var networkInfo = network.getNetwork();
                        List<String> ips = IpUtils.newRandomIps(networkInfo.getCidr(), 1, new ArrayList<>(networkInfo.getExcloudIps()), networkInfo.getAllocationPools(), Boolean.TRUE.equals(networkInfo.getDhcp()));
                        if (CollectionUtils.isEmpty(ips)) {
                            log.info("ip pool is empty when initate collection,node {}", cloudElement.getSrcElement().getName());
                            return;
                        }
                        var ip = ips.get(0);
                        networkInfo.getExcloudIps().add(ip);
                        var hostRoutes = network.getNetwork().getHostRoutes();
                        if (hostRoutes == null) {
                            hostRoutes = new ArrayList<>();
                        }
                        var hostRoute = new HostRoute();
                        hostRoute.setDestination(externalNetCidr);
                        hostRoute.setNexthop(ip);
                        hostRoutes.add(hostRoute);
                        network.getNetwork().setHostRoutes(hostRoutes);

                        var routerPort = new CollectionPluginSceneProbeRouterPortModel();
                        routerPort.setScene(getPluginHandler().getSrcScene().getEid());
                        routerPort.setSceneElement(nodeId);
                        routerPort.setIp(ip);
                        routerPorts.add(routerPort);
                    }
                });
                if (!routerPorts.isEmpty()) {
                    getSceneProbeRouterPortMapper().bulkCreate(routerPorts);
                }
            });

            // 创建探针采集器
            runSceneProgress(SceneProcess.CREATE_PROBE_COLLECTOR, () -> {
                var collectionId = getProbeCollector().createCollector();
                getPluginHandler().getScene().setProbeCollector(collectionId);
            });

            // 初始化安装探针脚本
            var collectionId = getPluginHandler().getScene().getProbeCollector();
            runSceneProgress(SceneProcess.INITIATE_PROBES, () -> {
                Map<String, List<CollectionPluginSceneLogProbeLocationModel>> locationMap = this.getLocationMap();
                probeCollectionElements.forEach(collectionElement -> {
                    var srcElement = collectionElement.getSrcElement();
                    WazuhControl.ControlConfigParam controlParam = new WazuhControl.ControlConfigParam();
                    String systemType = SystemTypeUtils.getStandardSystemType(srcElement.getSystemType());
                    if (collectionElement.getProbes() != null && !collectionElement.getProbes().isEmpty()) {
                        controlParam = this.getControlConfigParam(collectionElement.getProbes(), srcElement.getEid(),
                                systemType, locationMap);
                    }
                    var probeInstallScript = getProbeCollector().generateProbeInstallScript(systemType,
                            srcElement.getEid(), collectionId, controlParam);
                    var initCommands = srcElement.getInitCommands();
                    if (initCommands == null) {
                        initCommands = new ArrayList<>();
                    }
                    if (CollectionUtils.isNotEmpty(probeInstallScript)) {
                        initCommands.addAll(probeInstallScript);
                    }
                    srcElement.setInitCommands(initCommands);
                });
            });
        }

    }

    private WazuhControl.ControlConfigParam getControlConfigParam(List<Probe> probes, String agentName, String systemType
            , Map<String, List<CollectionPluginSceneLogProbeLocationModel>> locationMap) {
        List<WazuhControl.ControlProbeParam> customProbeParams = new ArrayList<>();
        List<WazuhControl.ControlRegistryModifyParam> registryParams = new ArrayList<>();
        List<WazuhControl.ControlFileModifyParam> fileParams = new ArrayList<>();
        List<WazuhControl.ControlLogCollectParam> logParams = new ArrayList<>();
        WazuhControl.ControlConfigParam controlParam = new WazuhControl.ControlConfigParam();
        for (Probe probe : probes) {
            if (probe.getExecType() == Probe.ExecType.SCRIPT_PROBE) {
                ConvertScriptParams convertScript = new ConvertScriptParams(probe, agentName);
                customProbeParams.addAll(convertScript.convertParams());
            }
            if (probe.getExecType() == Probe.ExecType.REGISTRY_PROBE) {
                ConvertRegistryParams convertRegistry = new ConvertRegistryParams(probe);
                registryParams.addAll(convertRegistry.convertParams());
            }
            if (probe.getExecType() == Probe.ExecType.FILE_PROBE) {
                ConvertFileParams convertFile = new ConvertFileParams(probe);
                fileParams.addAll(convertFile.convertParams());
            }
            if (probe.getExecType() == Probe.ExecType.LOG_PROBE) {
                ConvertLogParams convertLog = new ConvertLogParams(probe, systemType, locationMap);
                logParams.addAll(convertLog.convertParams());
            }
        }
        controlParam.setProbeParams(customProbeParams);
        controlParam.setRegistryModifyParams(registryParams);
        controlParam.setFileModifyParams(fileParams);
        controlParam.setLogCollectParams(logParams);
        return controlParam;
    }

    private Map<String, List<CollectionPluginSceneLogProbeLocationModel>> getLocationMap() {
        var locationMapper = BeanCommon.getBean(CollectionPluginSceneLogProbeLocationMapper.class);
        List<CollectionPluginSceneLogProbeLocationModel> locationModels = locationMapper.list(new HumanCondition());
        if (locationModels == null || locationModels.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, List<CollectionPluginSceneLogProbeLocationModel>> locationMap = new HashMap<>();
        locationModels.forEach(locationModel -> {
            String key = locationModel.getProbeId() + "_" + locationModel.getOsType();
            if (!locationMap.containsKey(key)) {
                locationMap.put(key, new ArrayList<>());
            }
            locationMap.get(key).add(locationModel);
        });
        return locationMap;
    }
}
