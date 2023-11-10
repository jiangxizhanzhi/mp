package cn.cyberpeace.mp.scriptcollectionevaluation.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
import cn.cyberpeace.mp.cscene.constant.CsceneBizConstant;
import cn.cyberpeace.mp.cscene.service.LogFileService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.AccessProtocol;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtDocker;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtVm;
import cn.cyberpeace.mp.devopscluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devopscluster.converter.model.ImageConvertModel;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigTopologyBO;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import cn.cyberpeace.mp.scene.service.SceneConfigService;
import cn.cyberpeace.mp.scene.service.SceneService;
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.service.CloudCommonConfigService;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.constant.ProbeConstant;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostStateQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.ListPageInfoQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import cn.cyberpeace.mp.scriptcollectionevaluation.bo.CollectionEvaluationBO;
import cn.cyberpeace.mp.scriptcollectionevaluation.bo.CollectionEvaluationResult;
import cn.cyberpeace.mp.scriptcollectionevaluation.constant.ScriptCollectionEvaluationConstant;
import cn.cyberpeace.mp.scriptcollectionevaluation.request.ImageCollectionEvaluationRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbePageQueryRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.ProbeService;
import cn.cyberpeace.mp.situationaware.service.wazuh.SysCollectorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScriptCollectionEvaluationService {
    private final Map<String, SceneDataBO> imageName2Scene = new ConcurrentHashMap<>();
    private final Map<String, CollectionEvaluationResult> imageName2EvaluationResult = new ConcurrentHashMap<>();

    @Resource
    private DeviceTargetMapper deviceTargetMapper;
    @Resource
    private ImagesConverter imagesConverter;

    @Resource
    private LogFileService logFileService;

    @Resource
    private SceneConfigService sceneConfigService;
    @Resource
    private SceneService sceneService;
    @Resource
    private SceneMapper sceneMapper;

    @Resource
    private ProbeService probeService;
    @Resource
    private SysCollectorService sysCollectorService;
    @Resource
    private DataCollectionService dataCollectionService;

    @Resource
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Resource
    private CloudCommonConfigService cloudCommonConfigService;

    public ServiceResult<Boolean> startImageCollectionEvaluation(ImageCollectionEvaluationRequest request) {
        DeviceTargetPO deviceTargetPO = deviceTargetMapper.first(new HumanCondition()
                .select(DeviceTargetPO::getUuid)
                .filterEqual(DeviceTargetPO::getName, request.getName()));
        if (deviceTargetPO == null) {
            throw new MpRuntimeException("不存在此名称的镜像!");
        }
        ImageConvertModel imageConvertModel = imagesConverter.findByUuid(deviceTargetPO.getUuid());

        SceneConfigDataBO sceneConfig = new SceneConfigDataBO();
        Map<String, ScenePluginDataBO> plugins = new HashMap<>();
        SceneConfigTopologyBO topology = new SceneConfigTopologyBO();

        SceneConfigBO scene = new SceneConfigBO();
        scene.setName("采集能力评估场景");
        topology.setScene(scene);

        ScenePluginDataBO cloudPlugin = new ScenePluginDataBO();
        Map<String, Map<String, Object>> cloudElements = new HashMap<>();
        Map<String, Map<String, Object>> cloudPorts = new HashMap<>();

        ScenePluginDataBO collectionPlugin = new ScenePluginDataBO();
        Map<String, Map<String, Object>> collectionElements = new HashMap<>();

        List<SceneConfigElementBO> elements = new ArrayList<>();

        SceneConfigElementBO terminal = new SceneConfigElementBO();
        terminal.setId(TextUtils.uuid());
        terminal.setName(imageConvertModel.getName());
        terminal.setIcon2(imageConvertModel.getLogo());
        terminal.setMaxPortNumber(1);
        terminal.setType(ScriptCollectionEvaluationConstant.ROLE_TO_TYPE.get(imageConvertModel.getRole()));

        if (imageConvertModel.getExtSystemInfo() != null) {
            terminal.setSystemArchitecture(imageConvertModel.getExtSystemInfo().getSystemArchitecture());
            terminal.setSystemLanguage(imageConvertModel.getExtSystemInfo().getLanguage());
            terminal.setSystemType(imageConvertModel.getExtSystemInfo().getSystemType().toString());
        }

        SceneConfigElementPortBO terminalPort = new SceneConfigElementPortBO();
        terminalPort.setId("port_" + terminal.getId() + "_1");
        terminalPort.setName("eth0/1");

        CloudPluginSceneConfigElementBO cloudElement = new CloudPluginSceneConfigElementBO();
        cloudElement.setCategory(imageConvertModel.getMainCategory());
        cloudElement.setCategory2(imageConvertModel.getSubCategory());
        cloudElement.setCategory3(imageConvertModel.getThirdCategory());
        cloudElement.setDhcp(true);
        cloudElement.setImage(imageConvertModel.getName());
        cloudElement.setImageUuid(imageConvertModel.getUuid());
        cloudElement.setIsBuildIn(imageConvertModel.getIsInternal());
        cloudElement.setRole(imageConvertModel.getRole());

        if (imageConvertModel.getExtVm() != null) {
            ExtVm extVm = imageConvertModel.getExtVm();
            terminal.setCpu(extVm.getCpu());
            terminal.setRam(extVm.getRam());
            terminal.setDisk(extVm.getDisk());

            terminalPort.setAccessModes(this.convertAccessMode(extVm.getAccessProtocols()));

            cloudElement.setImageType(BaseCloudPluginSceneElementModel.ImageType.VM);
            cloudElement.setInitSupport(extVm.getInitSupport());
        }

        if (imageConvertModel.getExtDocker() != null) {
            ExtDocker extDocker = imageConvertModel.getExtDocker();
            terminalPort.setAccessModes(this.convertAccessMode(extDocker.getAccessProtocols()));

            cloudElement.setImageType(BaseCloudPluginSceneElementModel.ImageType.CONTAINER);
            cloudElement.setInitSupport(extDocker.getInitSupport());
        }

        terminal.setPorts(List.of(terminalPort));
        elements.add(terminal);

        topology.setElements(elements);
        topology.setPieces(new ArrayList<>());
        topology.setConnections(new ArrayList<>());
        topology.setAreas(new ArrayList<>());
        sceneConfig.setTopology(topology);

        CloudPluginSceneConfigElementPortBO cloudPort = new CloudPluginSceneConfigElementPortBO();
        cloudPort.setAccessModes(this.convertCloudAccessMode(terminalPort.getAccessModes()));

        CloudPluginSceneConfigBO cloudPluginScene = new CloudPluginSceneConfigBO();
        cloudPluginScene.setDnsNameservers(new ArrayList<>());
        cloudPluginScene.setEnableRate(false);
        cloudPluginScene.setEnableRecording(false);

        cloudElements.put(terminal.getId(), JsonCommon.beanToSnakeMap(cloudElement));
        cloudPorts.put(terminalPort.getId(), JsonCommon.beanToSnakeMap(cloudPort));
        Map<String, Object> cloudScene = new HashMap<>(JsonCommon.beanToSnakeMap(cloudPluginScene));

        cloudPlugin.setElements(cloudElements);
        cloudPlugin.setElementPorts(cloudPorts);
        cloudPlugin.setScene(cloudScene);

        plugins.put(PluginConstant.PLUGIN_KEY, cloudPlugin);

        CollectionPluginSceneConfigBO collectionPluginScene = new CollectionPluginSceneConfigBO();
        collectionPluginScene.setEnableFlowCollect(false);
        collectionPluginScene.setEnableFlowMirror(false);
        collectionPluginScene.setEnableProbeCollect(false);
        Map<String, Object> collectionScene = new HashMap<>(JsonCommon.beanToSnakeMap(collectionPluginScene));

        CollectionPluginSceneConfigElementBO collectionElement = new CollectionPluginSceneConfigElementBO();
        collectionElement.setEnableProbeCollect(true);
        collectionElement.setProbes(this.genProbe(terminal.getSystemType()));

        List<String> customProbeIds = collectionElement.getProbes().stream()
                .filter(probe -> Boolean.FALSE.equals(probe.getCommon()))
                .map(Probe::getId)
                .collect(Collectors.toList());

        collectionElements.put(terminal.getId(), JsonCommon.beanToSnakeMap(collectionElement));

        collectionPlugin.setScene(collectionScene);
        collectionPlugin.setElements(collectionElements);

        plugins.put(cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant.PLUGIN_KEY, collectionPlugin);

        sceneConfig.setPlugins(plugins);

        var res = sceneConfigService.sync(sceneConfig, false, null).getSource();
        var sceneConfigResult = sceneConfigService.get(res.getTopology().getScene().getEid(), false, null, null).getSource();
        var sceneResult = sceneService.createFromSceneConfig(sceneConfigResult, null).getSource();
        imageName2Scene.put(request.getName(), sceneResult);

        String sceneId = sceneResult.getTopology().getScene().getEid();
        LocalDateTime sceneCreateTime = sceneResult.getTopology().getScene().getCreateTime();

        CollectionEvaluationResult result = new CollectionEvaluationResult();
        result.setStatus("评估中");
        imageName2EvaluationResult.put(request.getName(), result);
        TaskCommon.executeTask(() -> {
            List<CollectionEvaluationBO> collectionEvaluations = new ArrayList<>();
            List<String> collectionEvaluationNames = new ArrayList<>();
            for (int i = 0; i < request.getFrequency(); i++) {
                // 根据传参设定检测次数和检测间隔
                try {
                    Thread.sleep(1000 * 60 * request.getInterval());
                } catch (Exception ignored) {
                }

                String agentName = sceneResult.getTopology().getElements().get(0).getEid();
                String nodeId = sceneResult.getTopology().getElements().get(0).getId();
                String systemType = sceneResult.getTopology().getElements().get(0).getSystemType();

                if (!collectionEvaluationNames.contains("硬件信息")) {
                    HostStateQueryParams params = new HostStateQueryParams();
                    params.setPage(1);
                    params.setPageSize(1);
                    params.setNodeIds(List.of(nodeId));
                    var hostStateResult = scenePluginCollectionSceneService.listHostStates(sceneId, params).getSource().getObjs();
                    if (CollectionUtils.isNotEmpty(hostStateResult)) {
                        CollectionEvaluationBO hostStateCollectionEvaluation = new CollectionEvaluationBO();
                        hostStateCollectionEvaluation.setName("硬件信息");
                        hostStateCollectionEvaluation.setSupport(true);
                        hostStateCollectionEvaluation.setResult(hostStateResult);

                        collectionEvaluationNames.add(hostStateCollectionEvaluation.getName());
                        collectionEvaluations.add(hostStateCollectionEvaluation);
                    }
                }

                if (!collectionEvaluationNames.contains("操作系统信息")) {
                    List<OS> osList = sysCollectorService.getOS(agentName);
                    if (CollectionUtil.isNotEmpty(osList)) {
                        CollectionEvaluationBO osCollectionEvaluation = new CollectionEvaluationBO();
                        osCollectionEvaluation.setName("操作系统信息");
                        osCollectionEvaluation.setSupport(true);
                        osCollectionEvaluation.setResult(osList);

                        collectionEvaluationNames.add(osCollectionEvaluation.getName());
                        collectionEvaluations.add(osCollectionEvaluation);
                    }
                }

                if (!collectionEvaluationNames.contains("网络接口信息")) {
                    ListPageInfoQueryParams params = new ListPageInfoQueryParams();
                    params.setPage(1);
                    params.setPageSize(1);
                    params.setSceneIds(List.of(sceneId));
                    params.setNodeIds(List.of(nodeId));
                    var netResult = scenePluginCollectionSceneService.listNetworkConnections(params).getSource().getObjs();
                    if (CollectionUtils.isNotEmpty(netResult)) {
                        CollectionEvaluationBO netCollectionEvaluation = new CollectionEvaluationBO();
                        netCollectionEvaluation.setName("网络接口信息");
                        netCollectionEvaluation.setSupport(true);
                        netCollectionEvaluation.setResult(netResult);

                        collectionEvaluationNames.add(netCollectionEvaluation.getName());
                        collectionEvaluations.add(netCollectionEvaluation);
                    }
                }

                if (!collectionEvaluationNames.contains("端口信息")) {
                    CollectorBaseRequest collectorBaseRequest = new CollectorBaseRequest();
                    collectorBaseRequest.setAgentName(nodeId);
                    collectorBaseRequest.setPage(1);
                    collectorBaseRequest.setPageSize(1);
                    collectorBaseRequest.setPerPage(1);
                    var portResult = sysCollectorService.pagePorts(collectorBaseRequest).getSource().getObjs();
                    if (CollectionUtils.isNotEmpty(portResult)) {
                        CollectionEvaluationBO portCollectionEvaluation = new CollectionEvaluationBO();
                        portCollectionEvaluation.setName("端口信息");
                        portCollectionEvaluation.setSupport(true);
                        portCollectionEvaluation.setResult(portResult);

                        collectionEvaluationNames.add(portCollectionEvaluation.getName());
                        collectionEvaluations.add(portCollectionEvaluation);
                    }
                }

                if (!collectionEvaluationNames.contains("进程信息")) {
                    TimeListPageInfoQueryParams params = new TimeListPageInfoQueryParams();
                    params.setNodeIds(List.of(nodeId));
                    params.setSceneIds(List.of(sceneId));
                    var processResult = scenePluginCollectionSceneService.listPageProcess(params).getSource().getObjs();
                    if (CollectionUtils.isNotEmpty(processResult)) {
                        CollectionEvaluationBO processCollectionEvaluation = new CollectionEvaluationBO();
                        processCollectionEvaluation.setName("进程信息");
                        processCollectionEvaluation.setSupport(true);
                        processCollectionEvaluation.setResult(processResult);

                        collectionEvaluationNames.add(processCollectionEvaluation.getName());
                        collectionEvaluations.add(processCollectionEvaluation);
                    }
                }

                if (!collectionEvaluationNames.contains("文件变更信息")) {
                    HostEventQueryParams params = new HostEventQueryParams();
                    params.setNodeIds(List.of(nodeId));
                    params.setPage(1);
                    params.setPageSize(1);
                    params.setRuleGroups(CsceneBizConstant.FILE_MODIFY_GROUP);
                    var fileResult = scenePluginCollectionSceneService.listHostEvents(sceneId, params).getSource().getObjs();
                    if (CollectionUtils.isNotEmpty(fileResult)) {
                        CollectionEvaluationBO fileCollectionEvaluation = new CollectionEvaluationBO();
                        fileCollectionEvaluation.setName("文件变更信息");
                        fileCollectionEvaluation.setSupport(true);
                        fileCollectionEvaluation.setResult(fileResult);

                        collectionEvaluationNames.add(fileCollectionEvaluation.getName());
                        collectionEvaluations.add(fileCollectionEvaluation);
                    }
                }

                if (!collectionEvaluationNames.contains("注册表变更信息")) {
                    HostEventQueryParams params = new HostEventQueryParams();
                    params.setNodeIds(List.of(nodeId));
                    params.setPage(1);
                    params.setPageSize(1);
                    params.setRuleGroups(CsceneBizConstant.REGISTRY_MODIFY_GROUP);
                    var registryResult = scenePluginCollectionSceneService.listHostEvents(sceneId, params).getSource().getObjs();
                    if (CollectionUtils.isNotEmpty(registryResult)) {
                        CollectionEvaluationBO registryCollectionEvaluation = new CollectionEvaluationBO();
                        registryCollectionEvaluation.setName("注册表变更信息");
                        registryCollectionEvaluation.setSupport(true);
                        registryCollectionEvaluation.setResult(registryResult);

                        collectionEvaluationNames.add(registryCollectionEvaluation.getName());
                        collectionEvaluations.add(registryCollectionEvaluation);
                    }
                }

                if (!collectionEvaluationNames.contains("原始日志")) {
                    var logResult = logFileService.queryLogCollectFiles(agentName, nodeId, systemType);
                    if (CollectionUtils.isNotEmpty(logResult)) {
                        CollectionEvaluationBO logCollectionEvaluation = new CollectionEvaluationBO();
                        logCollectionEvaluation.setName("原始日志");
                        logCollectionEvaluation.setSupport(true);
                        logCollectionEvaluation.setResult(logResult);

                        collectionEvaluationNames.add(logCollectionEvaluation.getName());
                        collectionEvaluations.add(logCollectionEvaluation);
                    }
                }

                if (!collectionEvaluationNames.contains("安全软件")) {
                    HostEventQueryParams params = new HostEventQueryParams();
                    params.setNodeIds(List.of(nodeId));
                    params.setPage(1);
                    params.setPageSize(1);
                    params.setCatagories(CsceneBizConstant.SECURITY_CATEGORY);
                    var securityResult = scenePluginCollectionSceneService.listHostEvents(sceneId, params).getSource().getObjs();
                    if (CollectionUtils.isNotEmpty(securityResult)) {
                        CollectionEvaluationBO securityCollectionEvaluation = new CollectionEvaluationBO();
                        securityCollectionEvaluation.setName("安全软件");
                        securityCollectionEvaluation.setSupport(true);
                        securityCollectionEvaluation.setResult(securityResult);

                        collectionEvaluationNames.add(securityCollectionEvaluation.getName());
                        collectionEvaluations.add(securityCollectionEvaluation);
                    }
                }

                if (!collectionEvaluationNames.contains("操作系统")) {
                    HostEventQueryParams params = new HostEventQueryParams();
                    params.setNodeIds(List.of(nodeId));
                    params.setPage(1);
                    params.setPageSize(1);
                    params.setCatagories(CsceneBizConstant.OS_CATEGORY);
                    var osResult = scenePluginCollectionSceneService.listHostEvents(sceneId, params).getSource().getObjs();
                    if (CollectionUtils.isNotEmpty(osResult)) {
                        CollectionEvaluationBO osCollectionEvaluation = new CollectionEvaluationBO();
                        osCollectionEvaluation.setName("操作系统");
                        osCollectionEvaluation.setSupport(true);
                        osCollectionEvaluation.setResult(osResult);

                        collectionEvaluationNames.add(osCollectionEvaluation.getName());
                        collectionEvaluations.add(osCollectionEvaluation);
                    }
                }

                if (!collectionEvaluationNames.contains("应用系统日志")) {
                    HostEventQueryParams params = new HostEventQueryParams();
                    params.setNodeIds(List.of(nodeId));
                    params.setPage(1);
                    params.setPageSize(1);
                    params.setCatagories(CsceneBizConstant.APPLICATION_CATEGORY);
                    var applicationResult = scenePluginCollectionSceneService.listHostEvents(sceneId, params).getSource().getObjs();
                    if (CollectionUtils.isNotEmpty(applicationResult)) {
                        CollectionEvaluationBO applicationCollectionEvaluation = new CollectionEvaluationBO();
                        applicationCollectionEvaluation.setName("应用系统日志");
                        applicationCollectionEvaluation.setSupport(true);
                        applicationCollectionEvaluation.setResult(applicationResult);

                        collectionEvaluationNames.add(applicationCollectionEvaluation.getName());
                        collectionEvaluations.add(applicationCollectionEvaluation);
                    }
                }

                if (!collectionEvaluationNames.contains("自定义采集")) {
                    WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
                    wazuhEventRequest.setStartTime(sceneCreateTime);
                    wazuhEventRequest.setEndTime(LocalDateTime.now());
                    wazuhEventRequest.setPageNum(1);
                    wazuhEventRequest.setPageSize(1);
                    wazuhEventRequest.setAgentNames(List.of(agentName));
                    var customResult = dataCollectionService.queryCustomProbeData(wazuhEventRequest, customProbeIds).getObjs();
                    if (CollectionUtils.isNotEmpty(customResult)) {
                        CollectionEvaluationBO customCollectionEvaluation = new CollectionEvaluationBO();
                        customCollectionEvaluation.setName("自定义采集");
                        customCollectionEvaluation.setSupport(true);
                        customCollectionEvaluation.setResult(customResult);

                        collectionEvaluationNames.add(customCollectionEvaluation.getName());
                        collectionEvaluations.add(customCollectionEvaluation);
                    }
                }

                if (collectionEvaluationNames.size() == 12) {
                    break;
                }
            }

            result.setStatus("评估结束");
            result.setCollectionEvaluations(collectionEvaluations);
            imageName2EvaluationResult.put(request.getName(), result);
            sceneService.delete(sceneId, false, null);
        });

        return new ServiceResult<>(true);
    }

    public ServiceResult<CollectionEvaluationResult> getImageCollectionEvaluationResult(String name) {
        SceneDataBO scene = imageName2Scene.get(name);
        if (scene == null) {
            throw new MpRuntimeException("此镜像未进行采集评估或评估已结束!");
        }
        CollectionEvaluationResult result = new CollectionEvaluationResult();
        String sceneId = scene.getTopology().getScene().getEid();
        SceneModel sceneModel = sceneMapper.get(sceneId);
        if (sceneModel != null && EventStatus.ABNORMAL == sceneModel.getEventStatus()) {
            sceneService.delete(sceneId, false, null);
            result.setStatus("场景启动失败,请重新进行评估");
            imageName2Scene.remove(name);
            return new ServiceResult<>(result);
        }
        if (sceneModel != null && (SceneModel.Event.CREATE != sceneModel.getEvent() || EventStatus.OVER != sceneModel.getEventStatus())) {
            result.setStatus("场景启动中,请稍等");
            return new ServiceResult<>(result);
        }
        result = imageName2EvaluationResult.get(name);
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> clearImageCollectionEvaluationResult(String name) {
        imageName2Scene.remove(name);
        imageName2EvaluationResult.remove(name);
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> deleteScene(String sceneId) {
        return sceneService.delete(sceneId, false, null);
    }

    private List<AccessMode> convertAccessMode(List<AccessProtocol> accessProtocols) {
        return accessProtocols.stream().map(accessProtocol -> {
            AccessMode accessMode = new AccessMode();
            accessMode.setProtocol(accessProtocol.getProtocol());
            accessMode.setPort(accessProtocol.getPort());
            accessMode.setPrivateKeyEnable(accessProtocol.getPrivateKeyEnable());
            accessMode.setProxy(true);
            accessMode.setUsername(accessProtocol.getUsername());
            accessMode.setPassword(accessProtocol.getPassword());
            return accessMode;
        }).collect(Collectors.toList());
    }

    private List<CloudAccessMode> convertCloudAccessMode(List<AccessMode> accessModes) {
        return accessModes.stream().map(accessMode -> {
            CloudAccessMode cloudAccessMode = new CloudAccessMode();
            cloudAccessMode.setProxy(true);
            cloudAccessMode.setProtocol(accessMode.getProtocol());
            cloudAccessMode.setPort(accessMode.getPort());
            cloudAccessMode.setPrivateKeyEnable(accessMode.getPrivateKeyEnable());
            cloudAccessMode.setUsername(accessMode.getUsername());
            cloudAccessMode.setPassword(accessMode.getPassword());
            return cloudAccessMode;
        }).collect(Collectors.toList());
    }

    private List<Probe> genProbe(String systemType) {
        Integer osType = this.getOsType(systemType);
        ProbePageQueryRequest request = new ProbePageQueryRequest();
        request.setPage(1);
        request.setPageSize(10000);
        request.setOsTypes(new Integer[]{osType});
        List<ProbeDto> probeDtoList = probeService.page(request).getList();
        return probeDtoList.stream().map(probeDto -> {
            Probe probe = new Probe();
            probe.setCommon(probeDto.getType() == 0);
            probe.setDesc(probeDto.getDescription());
            probe.setDisabled(false);
            probe.setId(probeDto.getId());
            probe.setInterval(probeDto.getIntervalSec());
            probe.setName(probeDto.getName());
            probe.setOsType(probeDto.getOsType());
            probe.setOutputFormat(probeDto.getOutputFormat());
            probe.setType(probeDto.getProbeCategory().toString());
            if (ProbeConstant.ProbeId.FILE_MODIFY.equals(probeDto.getId())) {
                if (SystemTypeCommandUtils.isWindows(systemType)) {
                    probe.setCollectPath(List.of("C:\\ossec-agent\\ossec.log"));
                } else {
                    probe.setCollectPath(List.of("/var/ossec/logs/ossec.log"));
                }
            } else if (ProbeConstant.ProbeId.REGISTRY_MODIFY.equals(probeDto.getId())) {
                if (SystemTypeCommandUtils.isWindows(systemType)) {
                    probe.setCollectPath(List.of("HKEY_LOCAL_MACHINE\\SOFTWARE\\test"));
                } else {
                    probe.setCollectPath(new ArrayList<>());
                }
            } else if (ProbeConstant.ProbeId.LOG_FILE.equals(probeDto.getId())) {
                if (SystemTypeCommandUtils.isWindows(systemType)) {
                    probe.setCollectPath(List.of("C:\\ossec-agent\\ossec.log"));
                } else {
                    probe.setCollectPath(List.of("/var/ossec/logs/ossec.log"));
                }
            } else {
                probe.setCollectPath(new ArrayList<>());
            }
            probe.setScriptUrl(probeDto.getScriptUrl());
            probe.setScriptType(probeDto.getScriptType());
            return probe;
        }).collect(Collectors.toList());
    }

    private Integer getOsType(String systemType) {
        ServiceResult<Map<String, Object>> result = cloudCommonConfigService.getSystemTypeToAgentSupport();
        Map<String, Object> map = result.getSource();
        Object osTypeCode = map.get(systemType);
        return Integer.parseInt(osTypeCode.toString());
    }
}
