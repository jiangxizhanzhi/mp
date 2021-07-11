package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.model.BaseObjMappingModel;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.util.InstallerUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.HostRoute;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.ElementTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.config.WazuhLogProbeLocationConfig;
import cn.cyberpeace.mp.sceneplugincollection.config.WazuhModifyEventConfig;
import cn.cyberpeace.mp.sceneplugincollection.config.WazuhProbeConfig;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneProbeRouterPortModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.WazuhCommon;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert.ConvertFileParams;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert.ConvertLogParams;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert.ConvertRegistryParams;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert.ConvertScriptParams;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.cyberpeace.mp.sceneplugincollection.config.WazuhModifyEventConfig.ProbeType.FILE;
import static cn.cyberpeace.mp.sceneplugincollection.config.WazuhModifyEventConfig.ProbeType.REGISTRY;

@Slf4j
public class InitiateHandler extends HandlerCommon {

    private static WazuhLogProbeLocationConfig locationConfig;

    private static WazuhModifyEventConfig wazuhModifyEventConfig;

    private static Map<String, List<WazuhLogProbeLocationConfig.ProbeLocation>> locationMap;

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
        
        if (Boolean.TRUE.equals(CollectionSingleton.collectionAppConfig.getEnableFlowCollect())
                || Boolean.TRUE.equals(scene.getEnableFlowCollect()) || Boolean.TRUE.equals(scene.getEnableFlowMirror())) {
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
                    collectionPort.setEnableFlowCollect(scene.getEnableFlowCollect());
                    collectionPort.setEnableFlowMirror(scene.getEnableFlowMirror());
                });
            });
        }

        
        if (Boolean.TRUE.equals(CollectionSingleton.collectionAppConfig.getEnableProbeCollect())
                || Boolean.TRUE.equals(scene.getEnableProbeCollect())) {
            CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                    .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
            runSceneProgress(SceneProcess.INITIATE_PROBE_COLLECTION, () -> {
                cloudPluginHandler.getElementMap().forEach((nodeId, cloudElement) -> {
                    
                    var virtualType = cloudElement.getVirtualType();
                    if (virtualType == null
                            || (virtualType != BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE &&
                            virtualType != BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
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

                        
                        boolean currentNodeIsVirtualMachineRouter = ElementTypeUtils.isVirtualRouter(cloudElement);
                        if (currentNodeIsVirtualMachineRouter) {
                            if (CollectionUtils.isEmpty(network.getToPorts())) {
                                continue;
                            } else if (network.getToPorts().size() == 1) {
                                boolean connectElementIsVirtualMachineRouter =
                                        ElementTypeUtils.isVirtualRouter(cloudPluginHandler.getElementMap().get(network.getToPorts().get(0).getElement().getId()));
                                if (connectElementIsVirtualMachineRouter) {
                                    continue;
                                }
                            }
                        }

                        var networkInfo = network.getNetwork();
                        List<String> ips = IpUtils.newRandomIps(networkInfo.getCidr(), 1, new ArrayList<>(networkInfo.getExcloudIps()), networkInfo.getAllocationPools(), false);
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

            
            runSceneProgress(SceneProcess.CREATE_PROBE_COLLECTOR, () -> {
                var collectionId = getProbeCollector().createCollector();
                getPluginHandler().getScene().setProbeCollector(collectionId);
            });

            
            var collectionId = getPluginHandler().getScene().getProbeCollector();
            runSceneProgress(SceneProcess.INITIATE_PROBES, () -> {
                CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                        .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
                probeCollectionElements.forEach(collectionElement -> {
                    var srcElement = collectionElement.getSrcElement();
                    var cloudElement = cloudPluginHandler.getElement(srcElement.getId());
                    boolean isContainer = cloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER;
                    WazuhControl.ControlConfigParam controlParam = new WazuhControl.ControlConfigParam();
                    String systemType = SystemTypeUtils.getStandardSystemType(srcElement.getSystemType());
                    if (collectionElement.getProbes() != null && !collectionElement.getProbes().isEmpty()) {
                        controlParam = this.getControlConfigParam(collectionElement.getProbes(), srcElement.getEid(),
                                systemType, this.getLocationMap());
                    }
                    var probeInstallScript = getProbeCollector().generateProbeInstallScript(systemType,
                            srcElement.getEid(), collectionId, controlParam, isContainer);
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
            , Map<String, List<WazuhLogProbeLocationConfig.ProbeLocation>> locationMap) {
        List<WazuhControl.ControlProbeParam> customProbeParams = new ArrayList<>();
        List<WazuhControl.ControlRegistryModifyParam> registryParams = new ArrayList<>();
        List<WazuhControl.ControlFileModifyParam> fileParams = new ArrayList<>();
        List<WazuhControl.ControlLogCollectParam> logParams = new ArrayList<>();
        WazuhControl.ControlConfigParam controlParam = new WazuhControl.ControlConfigParam();
        for (Probe probe : probes) {
            
            if (probe.getExecType() == Probe.ExecType.SCRIPT_PROBE) {
                ConvertScriptParams convertScript = new ConvertScriptParams(probe, agentName);
                customProbeParams.addAll(convertScript.convertParams());
            } else if (probe.getExecType() == Probe.ExecType.REGISTRY_PROBE) {
                
                ConvertRegistryParams convertRegistry = new ConvertRegistryParams(probe);
                registryParams.addAll(convertRegistry.convertParams());
                List<String> collectPath = probe.getCollectPath().stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
                if (Boolean.FALSE.equals(probe.getDisabled()) && CollectionUtils.isNotEmpty(collectPath)) {
                    customProbeParams.add(getModifyProbeParam(agentName, collectPath, REGISTRY));
                }
            } else if (probe.getExecType() == Probe.ExecType.FILE_PROBE) {
                
                ConvertFileParams convertFile = new ConvertFileParams(probe);
                fileParams.addAll(convertFile.convertParams());
                List<String> collectPath = probe.getCollectPath().stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
                if (Boolean.FALSE.equals(probe.getDisabled()) && CollectionUtils.isNotEmpty(collectPath)) {
                    customProbeParams.add(getModifyProbeParam(agentName, collectPath, FILE));
                }
            } else if (probe.getExecType() == Probe.ExecType.LOG_PROBE) {
                
                ConvertLogParams convertLog = new ConvertLogParams(probe, systemType, locationMap);
                logParams.addAll(convertLog.convertParams());
            }
        }
        
        WazuhControl.ControlSyscheckParam syscheckParam = new WazuhControl.ControlSyscheckParam();
        syscheckParam.setFrequency(CollectionSingleton.collectionAppConfig.getSyscheckFrequency());
        syscheckParam.setAction(WazuhControl.CONTROL_ACTION_ADD);
        
        WazuhControl.ControlSyscollectorParam syscollectorParam = new WazuhControl.ControlSyscollectorParam();
        syscollectorParam.setAction(WazuhControl.CONTROL_ACTION_ADD);
        syscollectorParam.setInterval(CollectionSingleton.collectionAppConfig.getSyscollectorInterval());

        controlParam.setSyscheckParam(syscheckParam);
        controlParam.setSyscollectorParam(syscollectorParam);
        controlParam.setProbeParams(customProbeParams);
        controlParam.setRegistryModifyParams(registryParams);
        controlParam.setFileModifyParams(fileParams);
        controlParam.setLogCollectParams(logParams);
        return controlParam;
    }

    
    private Map<String, List<WazuhLogProbeLocationConfig.ProbeLocation>> getLocationMap() {
        if (locationMap == null) {
            locationMap = new HashMap<>();
            if (locationConfig == null) {
                locationConfig = BeanCommon.getBean(WazuhLogProbeLocationConfig.class);
            }
            if (CollectionUtils.isEmpty(locationConfig.getLocations())) {
                return locationMap;
            }
            
            locationConfig.getLocations().forEach(location -> {
                String key = location.getProbeId() + "_" + location.getOsType();
                if (!locationMap.containsKey(key)) {
                    locationMap.put(key, new ArrayList<>());
                }
                locationMap.get(key).add(location);
            });
        }
        return locationMap;
    }

    
    private WazuhControl.ControlProbeParam getModifyProbeParam(String agentName, List<String> collectPath, Integer type) {
        if (wazuhModifyEventConfig == null) {
            wazuhModifyEventConfig = BeanCommon.getBean(WazuhModifyEventConfig.class);
        }
        WazuhControl.ControlProbeParam probeParam = new WazuhControl.ControlProbeParam();
        var modifyProbe = wazuhModifyEventConfig.getModifyProbe();
        if (Objects.isNull(modifyProbe)) {
            return probeParam;
        }
        List<LocalFileMappingModel> mappings = LocalFileMappingCommon.getMappings(WazuhCommon.WAZUH_AGENT_GROUP);
        Map<String, String> nameFileMap = mappings.stream().collect(Collectors.toMap(BaseObjMappingModel::getName,
                LocalFileMappingModel::getRemoteFile));
        String metadataFileServer = SceneSingleton.sceneAppConfig.getMetadataFileServer();
        WazuhProbeConfig.DefaultProbe.NewProbe probe;
        if (type == FILE) {
            probe = modifyProbe.getFileProbe();
        } else {
            probe = modifyProbe.getRegistryProbe();
        }
        String url = StringUtils.joinWith(InstallerUtils.FILE_URL_SEPARATOR, metadataFileServer,
                nameFileMap.getOrDefault(probe.getProbeName(), StringUtils.EMPTY));
        probeParam.setId(probe.getId());
        probeParam.setAction(WazuhControl.CONTROL_ACTION_ADD);
        probeParam.setGroup(agentName);
        probeParam.setScriptUrl(url);
        if (probe.getProbeName().endsWith(WazuhControl.SCRIPT_SUFFIX_BAT)) {
            probeParam.setScriptSuffix(WazuhControl.SCRIPT_SUFFIX_PY);
        } else if (probe.getProbeName().endsWith(WazuhControl.SCRIPT_SUFFIX_PY)) {
            probeParam.setScriptSuffix(WazuhControl.SCRIPT_SUFFIX_PY);
        } else if (probe.getProbeName().endsWith(WazuhControl.SCRIPT_SUFFIX_SH)) {
            probeParam.setScriptSuffix(WazuhControl.SCRIPT_SUFFIX_SH);
        }
        probeParam.setOutputFormat(probe.getOutputFormat());
        probeParam.setInterval(probe.getInterval());
        probeParam.setScriptParam("\"" + StringUtils.join(collectPath, ",") + "\"");
        return probeParam;
    }
}
