package cn.cyberpeace.mp.scriptcollectionevaluation.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
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
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.service.SceneConfigService;
import cn.cyberpeace.mp.scene.service.SceneService;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostStateQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.ListPageInfoQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import cn.cyberpeace.mp.scriptcollectionevaluation.bo.CollectionEvaluationBO;
import cn.cyberpeace.mp.scriptcollectionevaluation.bo.Position2;
import cn.cyberpeace.mp.scriptcollectionevaluation.constant.ScriptCollectionEvaluationConstant;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbePageQueryRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.situationaware.service.ProbeService;
import cn.cyberpeace.mp.situationaware.service.wazuh.SysCollectorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScriptCollectionEvaluationService {
    private final Random random = new Random();

    @Resource
    private DeviceTargetMapper deviceTargetMapper;
    @Resource
    private ImagesConverter imagesConverter;

    @Resource
    private ProbeService probeService;

    @Resource
    private SceneConfigService sceneConfigService;
    @Resource
    private SceneService sceneService;

    @Resource
    private SysCollectorService sysCollectorService;
    @Resource
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    public ServiceResult<List<CollectionEvaluationBO>> getImageSupportCollectionEvaluation(String name) {
        DeviceTargetPO deviceTargetPO = deviceTargetMapper.first(new HumanCondition()
                .select(DeviceTargetPO::getUuid)
                .filterEqual(DeviceTargetPO::getName, name));
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

        Position2 position2 = new Position2();
        position2.setX(random.nextInt(100));
        position2.setY(random.nextInt(100));
        terminal.setPosition2(JsonCommon.stringify(position2));

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
        collectionElement.setProbes(this.genProbe());

        collectionElements.put(terminal.getId(), JsonCommon.beanToSnakeMap(collectionElement));

        collectionPlugin.setScene(collectionScene);
        collectionPlugin.setElements(collectionElements);

        plugins.put(cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant.PLUGIN_KEY, collectionPlugin);

        sceneConfig.setPlugins(plugins);

        var res = sceneConfigService.sync(sceneConfig, false, null).getSource();
        var sceneConfigResult = sceneConfigService.get(res.getTopology().getScene().getEid(), false, null, null).getSource();
        var sceneResult = sceneService.createFromSceneConfig(sceneConfigResult, null).getSource();

        String sceneId = sceneResult.getTopology().getScene().getEid();

        List<CollectionEvaluationBO> collectionEvaluations = new ArrayList<>();
        List<String> collectionEvaluationNames = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            // 三分钟检测一次 持续十次 首先等待三分钟场景启动
            try {
                Thread.sleep(1000 * 60 * 3L);
            } catch (Exception ignored) {
            }

            String agentName = sceneResult.getTopology().getElements().get(0).getEid();
            String nodeId = sceneResult.getTopology().getElements().get(0).getId();

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

                    collectionEvaluationNames.add(processCollectionEvaluation.getName());
                    collectionEvaluations.add(processCollectionEvaluation);
                }
            }

            if ((i == 9 && CollectionUtils.isNotEmpty(collectionEvaluationNames)) || collectionEvaluationNames.size() == 5) {
                // 文件变更事件
                CollectionEvaluationBO fileModifyCollectionEvaluation = new CollectionEvaluationBO();
                fileModifyCollectionEvaluation.setName("文件变更事件");
                fileModifyCollectionEvaluation.setSupport(true);

                collectionEvaluationNames.add(fileModifyCollectionEvaluation.getName());
                collectionEvaluations.add(fileModifyCollectionEvaluation);

                // 原始日志
                CollectionEvaluationBO logFileCollectionEvaluation = new CollectionEvaluationBO();
                logFileCollectionEvaluation.setName("原始日志");
                logFileCollectionEvaluation.setSupport(true);

                collectionEvaluationNames.add(logFileCollectionEvaluation.getName());
                collectionEvaluations.add(logFileCollectionEvaluation);

                // 安全软件
                CollectionEvaluationBO softwareCollectionEvaluation = new CollectionEvaluationBO();
                softwareCollectionEvaluation.setName("安全软件");
                softwareCollectionEvaluation.setSupport(true);

                collectionEvaluationNames.add(softwareCollectionEvaluation.getName());
                collectionEvaluations.add(softwareCollectionEvaluation);

                // 操作系统
                CollectionEvaluationBO osLogCollectionEvaluation = new CollectionEvaluationBO();
                osLogCollectionEvaluation.setName("操作系统");
                osLogCollectionEvaluation.setSupport(true);

                collectionEvaluationNames.add(osLogCollectionEvaluation.getName());
                collectionEvaluations.add(osLogCollectionEvaluation);

                // 应用系统日志
                CollectionEvaluationBO applicationCollectionEvaluation = new CollectionEvaluationBO();
                applicationCollectionEvaluation.setName("应用系统日志");
                applicationCollectionEvaluation.setSupport(true);

                collectionEvaluationNames.add(applicationCollectionEvaluation.getName());
                collectionEvaluations.add(applicationCollectionEvaluation);

                // 自定义采集
                CollectionEvaluationBO customCollectionEvaluation = new CollectionEvaluationBO();
                customCollectionEvaluation.setName("自定义采集");
                customCollectionEvaluation.setSupport(true);

                collectionEvaluationNames.add(customCollectionEvaluation.getName());
                collectionEvaluations.add(customCollectionEvaluation);
            }
        }

        sceneService.delete(sceneId, false, null);

        return new ServiceResult<>(collectionEvaluations);
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

    private List<Probe> genProbe() {
        ProbePageQueryRequest request = new ProbePageQueryRequest();
        request.setPage(1);
        request.setPageSize(10000);
        request.setTypes(new Integer[]{0});
        List<ProbeDto> probeDtoList = probeService.page(request).getList();
        return probeDtoList.stream().map(probeDto -> {
            Probe probe = new Probe();
            probe.setCommon(true);
            probe.setDesc(probeDto.getDescription());
            probe.setDisabled(false);
            probe.setId(probeDto.getId());
            probe.setInterval(probeDto.getIntervalSec());
            probe.setName(probeDto.getName());
            probe.setOsType(probeDto.getOsType());
            probe.setOutputFormat(probeDto.getOutputFormat());
            probe.setType(probeDto.getProbeCategory().toString());
            probe.setCollectPath(new ArrayList<>());
            return probe;
        }).collect(Collectors.toList());
    }
}
