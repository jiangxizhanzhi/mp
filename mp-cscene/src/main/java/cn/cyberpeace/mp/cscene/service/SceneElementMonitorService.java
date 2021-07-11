package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.constant.CsceneBizConstant;
import cn.cyberpeace.mp.cscene.dto.element.ConnectionState;
import cn.cyberpeace.mp.cscene.dto.element.ElementHistogramInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementPort;
import cn.cyberpeace.mp.cscene.dto.element.ElementRTInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementVulnerabilityCountInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementVulnerabilityInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementVulnerabilityPieChartInfo;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.scene.UserNodeSessionInfo;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.element.ElementMonitorRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.cscene.util.SegmentCount2IntervalSecondsUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.ListPageInfoQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.constant.SicLaunchConfig;
import cn.cyberpeace.mp.situationaware.model.FileEventModel;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhNetworkConnectionModel;
import cn.cyberpeace.mp.situationaware.model.WazuhVulnerabilityInfoCountModel;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.wazuh.SysCollectorService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
public class SceneElementMonitorService {

    private static final String CPCLOUD_PLUGIN = "cpcloud";

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private HostStateService hostStateService;

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private CsceneSpecialService csceneSpecialService;

    @Autowired
    private CsceneCommonService csceneCommonService;

    @Autowired
    private SysCollectorService sysCollectorService;

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private SceneMapper sceneMapper;

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    private Integer getWazuhVulnerabilityScanInterval() {
        return CollectionSingleton.collectionAppConfig.getWazuhVulnerabilityScanInterval();
    }

    
    public ElementMonitorBasicInfo getElementMonitorInfo(BaseSceneElementRequest baseRequest, boolean fetchRealTimeHostState) {
        log.info("baseRequest:{}", baseRequest);
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);
        String sceneId = baseRequest.getSceneId();
        String elementId = baseRequest.getElementIds().get(0);

        var result = csceneInstanceService.getById(sceneId, null);
        var csceneInstanceBO = result.getSource();

        ElementMonitorBasicInfo elementMonitorBasicInfo = new ElementMonitorBasicInfo();
        elementMonitorBasicInfo.setSceneId(sceneId);
        processTopologyElement(elementMonitorBasicInfo, csceneInstanceBO, elementId, fetchRealTimeHostState);
        processPluginElement(elementMonitorBasicInfo, csceneInstanceBO, elementId);

        
        ElementRTInfo rtInfo = processRTInfo(baseRequest);
        elementMonitorBasicInfo.setRtInfo(rtInfo);
        return elementMonitorBasicInfo;
    }


    public ListObjServiceResult<ConnectionState> pageNetworkConnections(BaseSceneElementPageRequest pageRequest) {
        pageRequest.ensure();
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(pageRequest);

        if (Objects.isNull(pageRequest.getFetchIfSceneInstanceShutdown()) || !pageRequest.getFetchIfSceneInstanceShutdown()) {
            if (!csceneCommonService.isCsceneInstanceRunning(pageRequest.getSceneId())) {
                return new ListObjServiceResult(new ListObjResult()).asResult();
            }
        }

        ListPageInfoQueryParams listPageInfoQueryParams = new ListPageInfoQueryParams();
        listPageInfoQueryParams.setPage(pageRequest.getPage());
        listPageInfoQueryParams.setPageSize(pageRequest.getPageSize());
        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(CollUtil.toList(pageRequest.getSceneId())));
        listPageInfoQueryParams.setNodeIds(pageRequest.getElementIds());
        listPageInfoQueryParams.setLevels(pageRequest.getLevels());
        ListObjServiceResult<WazuhNetworkConnectionModel> netConns = scenePluginCollectionSceneService.listNetworkConnections(listPageInfoQueryParams).asResult();

        List<ConnectionState> connectionStates = new ArrayList<>();
        if (Objects.nonNull(netConns.getSource()) && CollUtil.isNotEmpty(netConns.getSource().getList())) {
            List<WazuhNetworkConnectionModel> list = netConns.getSource().getList();
            connectionStates = list.stream().map(model -> convertWazuhNetConn2ConnState(model)).collect(Collectors.toList());
        }

        return new ListObjServiceResult<>(new ListObjResult<>(connectionStates, netConns.getSource().getTotal()).asResult()).asResult();
    }

    
    public OS getOSInfo(BaseSceneElementRequest baseRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);
        List<String> agentNames = csceneCommonService.getAgentNamesBySceneIdAndElementIds(baseRequest.getSceneId(), baseRequest.getElementIds());
        if (CollectionUtil.isEmpty(agentNames)) {
            return null;
        }
        String agentName = agentNames.get(0);
        List<OS> osList = sysCollectorService.getOS(agentName);
        if (CollectionUtil.isEmpty(osList)) {
            return null;
        }

        return osList.get(0);
    }


    public int sumEnvVarChangedCount(BaseSceneElementRequest baseRequest) {
        ListPageInfoQueryParams listPageInfoQueryParams = buildListPageInfoQueryParams(baseRequest);
        int sum = scenePluginCollectionSceneService.sumEnvVarChangedCount(listPageInfoQueryParams).getSource();
        return sum;
    }

    public int sumProcessesChangedCount(BaseSceneElementRequest baseRequest) {
        ListPageInfoQueryParams listPageInfoQueryParams = buildListPageInfoQueryParams(baseRequest);
        int sum = scenePluginCollectionSceneService.sumProcessesChangedCount(listPageInfoQueryParams).getSource();
        return sum;
    }


    public int sumSysUserChangedCount(BaseSceneElementRequest baseRequest) {
        ListPageInfoQueryParams listPageInfoQueryParams = buildListPageInfoQueryParams(baseRequest);
        int sum = scenePluginCollectionSceneService.sumSysUserChangedCount(listPageInfoQueryParams).getSource();
        return sum;
    }


    
    public ElementRTInfo getElementRTInfo(BaseSceneElementRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);
        ElementRTInfo rtInfo = processRTInfo(baseRequest);

        return rtInfo;
    }


    public void processTopologyElement(ElementMonitorBasicInfo basicInfo, CsceneInstanceBO csceneInstanceBO, String elementId, boolean fetchRealTimeHostState) {
        if (Objects.isNull(csceneInstanceBO)
                || Objects.isNull(csceneInstanceBO.getScene())
                || Objects.isNull(csceneInstanceBO.getScene().getTopology())) {
            return;
        }

        var topology = csceneInstanceBO.getScene().getTopology();
        var topologyElements = topology.getElements();
        Optional<SceneElementBO> tpoEleOptional = topologyElements.stream().filter(e -> Objects.equals(elementId, e.getId())).findFirst();

        if (tpoEleOptional.isPresent()) {
            SceneElementBO tpoEleBO = tpoEleOptional.get();
            sceneElementBO2ElementMonitorBasicInfo(basicInfo, tpoEleBO, csceneInstanceBO, fetchRealTimeHostState);
        }
    }

    public void processPluginElement(ElementMonitorBasicInfo basicInfo, CsceneInstanceBO csceneInstanceBO, String elementId) {
        if (Objects.isNull(csceneInstanceBO)
                || Objects.isNull(csceneInstanceBO.getScene())
                || Objects.isNull(csceneInstanceBO.getScene().getPlugins())
                || Objects.isNull(csceneInstanceBO.getScene().getPlugins().get(CPCLOUD_PLUGIN))) {
            return;
        }

        var scenePluginDataBO = csceneInstanceBO.getScene().getPlugins().get(CPCLOUD_PLUGIN);
        var pluginElements = scenePluginDataBO.getElements();
        var pluginEleOptional = pluginElements.entrySet().stream().filter(entry -> Objects.equals(entry.getKey(), elementId)).map(Map.Entry::getValue).findFirst();

        if (pluginEleOptional.isPresent()) {
            CloudPluginSceneElementBO cloudPluginSceneElementBO = JsonCommon.snakeMapToBean(pluginEleOptional.get(), CloudPluginSceneElementBO.class);
            cpSceneElementBO2ElementMonitorBasicInfo(basicInfo, cloudPluginSceneElementBO);
        }
    }


    public void sceneElementBO2ElementMonitorBasicInfo(ElementMonitorBasicInfo basicInfo, SceneElementBO sceneElementBO, CsceneInstanceBO csceneInstanceBO, boolean fetchRealTimeHostState) {
        log.info("fetchHostState:{}", fetchRealTimeHostState);
        
        if (fetchRealTimeHostState) {
            HostState resource = basicInfo.getHostState();
            if (Objects.isNull(resource)) {
                resource = new HostState();
            }

            BaseSceneElementPageRequest request = new BaseSceneElementPageRequest();
            request.setSceneId(basicInfo.getSceneId());
            request.setElementIds(Arrays.asList(sceneElementBO.getId()));
            request.setPage(1);
            request.setPageSize(1);
            List<HostState> hostStates = hostStateService.listHostStates(request);
            if (CollectionUtil.isNotEmpty(hostStates)) {
                HostState hostState = hostStates.get(0);
                resource.setAgentName(hostState.getAgentName());
                resource.setCpuCount(hostState.getCpuCount());
                resource.setMemoryTotal(hostState.getMemoryTotal());
                resource.setDiskSpace(hostState.getDiskSpace());
                
                resource.setDiskUnused(hostState.getDiskUnused());

                resource.setCpuPercent(Objects.nonNull(hostState.getCpuPercent()) ? NumberUtil.round(hostState.getCpuPercent(), 2).doubleValue() : 0.0);
                resource.setMemoryPercent(Objects.nonNull(hostState.getMemoryPercent()) ? NumberUtil.round(hostState.getMemoryPercent(), 2).doubleValue() : 0.0);
                resource.setMemoryFree(hostState.getMemoryFree());
                resource.setDiskPercent(Objects.nonNull(hostState.getDiskPercent()) ? NumberUtil.round(hostState.getDiskPercent(), 2).doubleValue() : 0.0);

                resource.setDownloadSpeed(hostState.getDownloadSpeed());
                resource.setUploadSpeed(hostState.getUploadSpeed());
                resource.setDiskReadBytes(hostState.getDiskReadBytes());
                resource.setMaxNetSpeed(hostState.getMaxNetSpeed());
                resource.setDiskWriteBytes(hostState.getDiskWriteBytes());
                resource.setDiskIoPercent(hostState.getDiskIoPercent());
                resource.setDiskIoPercent(hostState.getDiskIoPercent());
                resource.setDiskIoPercent(hostState.getDiskIoPercent());
            } else {
                
                resource.setCpuCount(sceneElementBO.getCpu());
                resource.setMemoryTotal(sceneElementBO.getRam().longValue());
                resource.setDiskSpace(sceneElementBO.getDisk().longValue());
            }
            basicInfo.setHostState(resource);
        }

        



        
        basicInfo.setId(sceneElementBO.getId());
        basicInfo.setEid(sceneElementBO.getEid());
        
        basicInfo.setName(sceneElementBO.getName());
        
        basicInfo.setIcon2(sceneElementBO.getIcon2());

        basicInfo.setEvent(sceneElementBO.getEvent());
        basicInfo.setEventStatus(sceneElementBO.getEventStatus());
        basicInfo.setType(sceneElementBO.getType());

        
        
        if (StringUtils.isNotBlank(sceneElementBO.getSystemType())) {
            basicInfo.setSystemType(sceneElementBO.getSystemType());
            var item = SystemTypeUtils.getSystemTypeItem(sceneElementBO.getSystemType());
            basicInfo.setSystemTypeName(item.getName());
        }

        
        var ports = sceneElementBO.getPorts();
        List<ElementPort> ePorts = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ports)) {
            ePorts = ports.stream().map(p -> {
                ElementPort port = new ElementPort();
                port.setIp(p.getIp());
                port.setName(p.getName());
                port.setId(p.getId());
                port.setAccessModes(p.getAccessModes());
                return port;
            }).collect(Collectors.toList());
        }
        basicInfo.setPorts(ePorts);

    }

    public void cpSceneElementBO2ElementMonitorBasicInfo(ElementMonitorBasicInfo basicInfo, CloudPluginSceneElementBO cpSceneElementBO) {
        
        String imageName = null;
        if(StringUtils.contains(cpSceneElementBO.getImage(), cpSceneElementBO.getImageUuid())) {
            imageName = cpSceneElementBO.getImage().replaceFirst(cpSceneElementBO.getImageUuid(), "");
        } else {
            imageName = cpSceneElementBO.getImage();
        }
        basicInfo.setImageName(imageName);
        basicInfo.setImageType(cpSceneElementBO.getImageType());
        basicInfo.setRole(cpSceneElementBO.getRole());
        basicInfo.setIsBuildIn(cpSceneElementBO.getIsBuildIn());

        
        basicInfo.setHost(cpSceneElementBO.getHost());

        
        basicInfo.setVirtualType(cpSceneElementBO.getVirtualType());
        basicInfo.setExternal(cpSceneElementBO.getExternal());

        
        basicInfo.setCategory(cpSceneElementBO.getCategory());
    }

    public ListObjServiceResult<CollectionHostEventBO> getModifyData(ElementMonitorRequest pageRequest, List<String> ruleGroups,
                                                                     String path) {
        BaseRequestUtil.checkBaseRequestWithSceneId(pageRequest);
        var sceneId = csceneInstanceService.getSceneId(pageRequest.getSceneId());
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        
        if (pageRequest.getElementIds() == null || pageRequest.getElementIds().isEmpty()) {
            pageRequest.setElementIds(sceneHandler.getElementMap().values().stream().map(e -> e.getId()).collect(Collectors.toList()));
        }
        processRequest(pageRequest, sceneHandler);
        HostEventQueryParams hostEventQueryParams = new HostEventQueryParams();
        hostEventQueryParams.setNodeIds(pageRequest.getElementIds());
        hostEventQueryParams.setStartTime(pageRequest.getStartTime());
        hostEventQueryParams.setEndTime(pageRequest.getEndTime());
        hostEventQueryParams.setLevels(pageRequest.getLevels());
        hostEventQueryParams.setRuleGroups(ruleGroups);
        hostEventQueryParams.setPath(path);
        hostEventQueryParams.setPage(pageRequest.getPage());
        hostEventQueryParams.setPageSize(pageRequest.getPageSize());
        ListObjServiceResult<CollectionHostEventBO> hostEventsBos = csceneInstanceService.listHostEvents(pageRequest.getSceneId(), hostEventQueryParams);
        mergeModifyData(hostEventsBos, hostEventQueryParams, pageRequest, sceneHandler);

        return hostEventsBos;

    }

    
    private void mergeModifyData(ListObjServiceResult<CollectionHostEventBO> hostEventsBos,
                                 HostEventQueryParams hostEventQueryParams, ElementMonitorRequest pageRequest, SceneHandler sceneHandler) {
        
        if (hostEventQueryParams.getPage() == null) {
            return;
        }

        List<CollectionHostEventBO> bos = hostEventsBos.getSource().getObjs();
        List<String> ruleGroups = hostEventQueryParams.getRuleGroups();
        String sicLaunchId = null;
        if (ruleGroups == CsceneBizConstant.FILE_MODIFY_GROUP) {
            sicLaunchId = SicLaunchConfig.LAUNCH_ID_FILE;
        } else if (ruleGroups == CsceneBizConstant.REGISTRY_MODIFY_GROUP) {
            sicLaunchId = SicLaunchConfig.LAUNCH_ID_REGISTRY;
        } else {
            return;
        }

        WazuhEventRequest request = scenePluginCollectionSceneService.genWazuhEventRequest(hostEventQueryParams,
                sceneHandler.getScene().getCreateTime(), getAgentNames(pageRequest, sceneHandler));
        request.setSicLaunchId(sicLaunchId);
        request.setRuleGroups(null);

        
        if (StringUtils.isNotBlank(hostEventQueryParams.getPath())) {
            request.setPath(null);
        }

        Map<String, SceneElementBO> eidMap = sceneHandler.getElementMap().values().stream()
                .collect(Collectors.toMap(SceneElementBO::getEid, Function.identity(), (v1, v2) -> v2));
        ListObjResult<FileEventModel> fileEventModels = dataCollectionService.queryFileOrRegisterEvents(request);
        if (CollectionUtils.isNotEmpty(fileEventModels.getObjs())) {
            List<FileEventModel> list = new ArrayList<>();
            Set<String> exist = new HashSet<>();
            for (var x : fileEventModels.getObjs()) {
                if (!exist.contains(x.getAgentId() + x.getFilePath())) {
                    if (StringUtils.isBlank(hostEventQueryParams.getPath())) {
                        list.add(x);
                        exist.add(x.getAgentId() + x.getFilePath());
                    } else if (StringUtils.contains(x.getFilePath(), hostEventQueryParams.getPath())) {
                        list.add(x);
                        exist.add(x.getAgentId() + x.getFilePath());
                    }
                }
            }
            fileEventModels = new ListObjResult<>(list, list.size());
        }

        
        int alreadyHasNum = hostEventsBos.getSource().getTotal();
        hostEventsBos.getSource().setTotal(alreadyHasNum + fileEventModels.getTotal());

        
        int pageSize = hostEventQueryParams.getPageSize();
        if (bos.size() == pageSize) {
            return;
        }

        
        int totalNeedNum = pageSize * hostEventQueryParams.getPage();
        int stillNeedNum = totalNeedNum - alreadyHasNum;
        int startIndex = stillNeedNum <= pageSize ? 0 : stillNeedNum - pageSize;
        List<FileEventModel> fEventModels = fileEventModels.getObjs();
        if (startIndex >= fEventModels.size()) {
            return;
        }
        fEventModels = fEventModels.subList(startIndex, Math.min(fEventModels.size(), stillNeedNum));

        for (FileEventModel file : fEventModels) {
            CollectionHostEventBO bo = new CollectionHostEventBO();
            SceneElementBO ele = eidMap.get(file.getAgentId());
            bo.setNodeId(ele.getId());
            bo.setNodeName(ele.getName());
            if (Objects.nonNull(file.getLevel())) {
                bo.setLevel(file.getLevel());
            } else {
                bo.setLevel(-1);
            }
            if (StringUtils.isNotEmpty(file.getFilePath()) && file.getFilePath().endsWith("---file_not_exists---")) {
                bo.setPath(StringUtils.replace(file.getFilePath(), "---file_not_exists---", ""));
                bo.setDescription("-");
            } else if (StringUtils.isNotEmpty(file.getFilePath()) && file.getFilePath().contains("---file_size---")) {
                String[] fileSize = file.getFilePath().split("---file_size---");
                if (fileSize.length > 0) {
                    bo.setPath(fileSize[0]);
                }
                if (fileSize.length > 1) {
                    if (NumberUtil.isNumber(fileSize[1])) {
                        bo.setFileSize(fileSize[1]);
                    }
                }
                bo.setDescription(StringUtils.EMPTY);
            } else {
                bo.setPath(file.getFilePath());
            }
            bo.setTime(file.getUpdateTime());
            bos.add(bo);
            if (bos.size() == hostEventQueryParams.getPageSize()) {
                return;
            }
        }
    }

    
    public List<ElementHistogramInfo> listEventHistogram(ElementMonitorRequest pageRequest, List<String> ruleGroups, Integer intervalMinutes, Integer bucketSize) {
        if (Objects.isNull(intervalMinutes) || Objects.isNull(bucketSize)) {
            log.error("intervalMinutes:{}, bucketSize:{} should not be null!", intervalMinutes, bucketSize);
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        var agentNames = csceneCommonService.getAgentNamesBySceneIdAndElementIds(pageRequest.getSceneId(), pageRequest.getElementIds());
        if (CollectionUtil.isEmpty(agentNames)) {
            return Collections.emptyList();
        }

        List<ElementHistogramInfo> infos = new ArrayList<>();
        var wazuhEventRequest = buildWazuhRequest(pageRequest);
        wazuhEventRequest.setAgentNames(agentNames);
        wazuhEventRequest.setRuleGroups(ruleGroups);
        wazuhEventRequest.setLevels(pageRequest.getLevels());
        wazuhEventRequest.setPageSize(0);

        String minBounds = TimeUtils.localTime2yyyyMMddHHmm(wazuhEventRequest.getEndTime().minusMinutes((long) intervalMinutes * (bucketSize)));
        String maxBounds = TimeUtils.localTime2yyyyMMddHHmm(wazuhEventRequest.getEndTime().minusMinutes(1));

        var resultMap = dataCollectionService.aggsDateHistogram(wazuhEventRequest, intervalMinutes + "m", minBounds, maxBounds);

        if (CollectionUtil.isNotEmpty(resultMap)) {
            resultMap.forEach((key, value) -> {
                infos.add(ElementHistogramInfo.builder().time(key).count(Objects.isNull(value) ? 0 : value.intValue()).build());
            });
        }

        
        
        
        
        
        
        
        List<ElementHistogramInfo> finalList = infos.stream().limit(bucketSize).collect(Collectors.toList());
        Collections.reverse(finalList);
        return finalList;
    }

    @Deprecated
    public List<ElementHistogramInfo> getModifyHistogram(ElementMonitorRequest pageRequest, List<String> ruleGroups, Integer segmentCount) {
        BaseRequestUtil.checkBaseRequestWithSceneId(pageRequest);
        var sceneId = csceneInstanceService.getSceneId(pageRequest.getSceneId());
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        LocalDateTime startTime;
        LocalDateTime endTime;
        processRequest(pageRequest, sceneHandler);
        startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
        
        pageRequest.setPageSize(10000);
        var result = getModifyData(pageRequest, ruleGroups, null);
        if (result == null || result.getSource() == null || CollUtil.isEmpty(result.getSource().getObjs())) {
            return new ArrayList<>();
        }
        if (segmentCount == null) {
            
            segmentCount = 15;
        }
        long intervalSeconds = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(startTime, endTime, segmentCount);
        List<ElementHistogramInfo> histogramInfos = new ArrayList<>();
        for (int i = 0; i < segmentCount; i++) {
            ElementHistogramInfo histogramInfo = new ElementHistogramInfo();
            histogramInfo.setLocalDateTime(startTime.plusSeconds(intervalSeconds * i));
            histogramInfo.setTime(TimeUtils.localDateTimeToString(histogramInfo.getLocalDateTime()));
            histogramInfo.setCount(0);
            histogramInfos.add(histogramInfo);
        }
        result.getSource().getObjs().sort(Comparator.comparing(r -> r.getTime()));
        int currentHistogramInfo = 0;
        for (int i = 0; i < result.getSource().getObjs().size(); ) {
            var collectionData = result.getSource().getObjs().get(i);
            var histogramInfo = histogramInfos.get(currentHistogramInfo);
            
            if ((currentHistogramInfo == histogramInfos.size() - 1) || collectionData.getTime().isBefore(histogramInfo.getLocalDateTime())) {
                histogramInfo.setCount(histogramInfo.getCount() + 1);
                i++;
            } else {
                currentHistogramInfo = Math.min(currentHistogramInfo + 1, histogramInfos.size() - 1);
            }
        }
        return histogramInfos;
    }

    public ListObjResult<ElementVulnerabilityInfo> getVulnerabilityInfo(ElementMonitorRequest pageRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneId(pageRequest);
        if (CollUtil.isEmpty(pageRequest.getElementIds())) {
            return new ListObjResult<>();
        }
        var sceneId = csceneInstanceService.getSceneId(pageRequest.getSceneId());
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        HumanCondition humanCondition = new HumanCondition().currentLink(SceneModel.class)
                .filterEqual(SceneModel::getId, sceneId);
        var sceneModel = sceneMapper.list(humanCondition);
        boolean isRunning = csceneCommonService.isCsceneInstanceRunning(pageRequest.getSceneId());
        processVulnerabilityRequest(pageRequest, sceneModel.get(0), isRunning);
        LocalDateTime startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        LocalDateTime endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
        Map<String, String> elementEid2Name = sceneHandler.getElementMap().values().stream().collect(Collectors.toMap(e -> e.getEid(), e -> e.getName()));
        List<String> agentNames = getAgentNames(pageRequest, sceneHandler);
        if (startTime.plusHours(getWazuhVulnerabilityScanInterval()).isEqual(endTime)) {
            processTimeRangeForVulnerabilityInfo(pageRequest, agentNames);
            startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        }
        WazuhEventRequest request = new WazuhEventRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setPageNum(pageRequest.getPage());
        request.setPageSize(pageRequest.getPageSize());
        request.setAgentNames(agentNames);
        request.setRuleGroups(CsceneBizConstant.VULNERABILITY_GROUP);
        var result = dataCollectionService.queryVulnerabilityInfo(request, true);
        List<ElementVulnerabilityInfo> vulnerabilityInfos = new ArrayList<>();
        result.getObjs().forEach(r -> {
            ElementVulnerabilityInfo vulnerabilityInfo = new ElementVulnerabilityInfo();
            vulnerabilityInfo.setNodeName(elementEid2Name.get(r.getAgentName()));
            vulnerabilityInfo.setCve(r.getVulnerability().getCve());
            
            vulnerabilityInfo.setSeverity(r.getVulnerability().getSeverity().toLowerCase());
            vulnerabilityInfo.setTitle(r.getVulnerability().getTitle());
            vulnerabilityInfo.setTime(r.getTime());
            vulnerabilityInfo.setPublished(convertDate(r.getVulnerability().getPublished()));
            vulnerabilityInfo.setUpdate(r.getVulnerability().getUpdated());
            vulnerabilityInfo.setTime(r.getTime());
            vulnerabilityInfo.setReferences(r.getVulnerability().getReferences());
            vulnerabilityInfo.setRationale(r.getVulnerability().getRationale());
            vulnerabilityInfos.add(vulnerabilityInfo);
        });
        return new ListObjResult<>(vulnerabilityInfos, result.getTotal());
    }

    private void processTimeRangeForVulnerabilityInfo(ElementMonitorRequest pageRequest, List<String> agentNames) {
        LocalDateTime startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        LocalDateTime endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
        WazuhEventRequest request = new WazuhEventRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setPageNum(1);
        request.setPageSize(1);
        request.setAgentNames(agentNames);
        request.setRuleGroups(CsceneBizConstant.VULNERABILITY_GROUP);
        int halfScanTime = getWazuhVulnerabilityScanInterval() / 2;
        var first = dataCollectionService.queryVulnerabilityInfo(request, false);
        var last = dataCollectionService.queryVulnerabilityInfo(request, true);
        if (CollUtil.isEmpty(first.getObjs()) || CollUtil.isEmpty(last.getObjs())) {
            
            pageRequest.setStartTime(TimeUtils.localDateTimeToString(startTime.plusHours(-halfScanTime)));
            return;
        }
        var firstTime = first.getObjs().get(0).getTime();
        var lastTime = last.getObjs().get(0).getTime();
        if (firstTime.plusHours(halfScanTime).isBefore(lastTime)) {
            pageRequest.setStartTime(TimeUtils.localDateTimeToString(lastTime.plusHours(-halfScanTime)));
        } else if (firstTime.plusHours(halfScanTime).isAfter(lastTime)) {
            pageRequest.setStartTime(TimeUtils.localDateTimeToString(firstTime.plusHours(-halfScanTime)));
        }
    }

    public List<ElementVulnerabilityPieChartInfo> getVulnerabilityPieChartInfo(ElementMonitorRequest pageRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneId(pageRequest);
        if (CollUtil.isEmpty(pageRequest.getElementIds())) {
            return new ArrayList<>();
        }
        var sceneId = csceneInstanceService.getSceneId(pageRequest.getSceneId());
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        HumanCondition humanCondition = new HumanCondition().currentLink(SceneModel.class)
                .filterEqual(SceneModel::getId, sceneId);
        var sceneModel = sceneMapper.list(humanCondition);
        boolean isRunning = csceneCommonService.isCsceneInstanceRunning(pageRequest.getSceneId());
        processVulnerabilityRequest(pageRequest, sceneModel.get(0), isRunning);
        List<String> agentNames = getAgentNames(pageRequest, sceneHandler);
        LocalDateTime startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        LocalDateTime endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
        if (startTime.plusHours(getWazuhVulnerabilityScanInterval()).isEqual(endTime)) {
            processTimeRangeForVulnerabilityInfo(pageRequest, agentNames);
            startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        }
        WazuhEventRequest request = new WazuhEventRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setAgentNames(agentNames);
        request.setRuleGroups(CsceneBizConstant.VULNERABILITY_GROUP);
        var result = dataCollectionService.queryVulnerabilityCountInfo(request,
                false, "data.vulnerability.severity", "severity");
        if (CollUtil.isEmpty(result)) {
            return new ArrayList<>();
        }
        result = result.stream()
                .filter(countModel -> !countModel.getFieldName().equals("-"))
                .collect(Collectors.toList());
        long i = 0;
        for (WazuhVulnerabilityInfoCountModel countModel : result) {
            i = countModel.getCount() + i;
        }
        long sum = i;
        List<ElementVulnerabilityPieChartInfo> pieChartInfos = new ArrayList<>();
        result.forEach(r -> {
            ElementVulnerabilityPieChartInfo pieChartInfo = new ElementVulnerabilityPieChartInfo();
            
            pieChartInfo.setSeverity(r.getFieldName().toLowerCase());
            
            if ("low".equals(pieChartInfo.getSeverity())) {
                pieChartInfo.setSeverityLevel(0);
            } else if ("medium".equals(pieChartInfo.getSeverity())) {
                pieChartInfo.setSeverityLevel(1);
            } else if ("high".equals(pieChartInfo.getSeverity())) {
                pieChartInfo.setSeverityLevel(2);
            } else if ("critical".equals(pieChartInfo.getSeverity())) {
                pieChartInfo.setSeverityLevel(3);
            }
            
            pieChartInfo.setPercent(BigDecimal.valueOf(r.getCount()).divide(BigDecimal.valueOf(sum), 4, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(100)).doubleValue());
            pieChartInfos.add(pieChartInfo);
        });
        pieChartInfos.sort(Comparator.comparing(ElementVulnerabilityPieChartInfo::getSeverityLevel));
        return pieChartInfos;
    }

    public List<ElementVulnerabilityCountInfo> getVulnerabilityCountInfo(ElementMonitorRequest pageRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneId(pageRequest);
        if (CollUtil.isEmpty(pageRequest.getElementIds())) {
            return new ArrayList<>();
        }
        var sceneId = csceneInstanceService.getSceneId(pageRequest.getSceneId());
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        HumanCondition humanCondition = new HumanCondition().currentLink(SceneModel.class)
                .filterEqual(SceneModel::getId, sceneId);
        var sceneModel = sceneMapper.list(humanCondition);
        boolean isRunning = csceneCommonService.isCsceneInstanceRunning(pageRequest.getSceneId());
        processVulnerabilityRequest(pageRequest, sceneModel.get(0), isRunning);
        List<String> agentNames = getAgentNames(pageRequest, sceneHandler);
        LocalDateTime startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        LocalDateTime endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
        if (startTime.plusHours(getWazuhVulnerabilityScanInterval()).isEqual(endTime)) {
            processTimeRangeForVulnerabilityInfo(pageRequest, agentNames);
            startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        }
        WazuhEventRequest request = new WazuhEventRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setAgentNames(agentNames);
        request.setRuleGroups(CsceneBizConstant.VULNERABILITY_GROUP);
        var result = dataCollectionService.queryVulnerabilityCountInfo(request,
                true, "data.vulnerability.package.name", "package_name");
        List<ElementVulnerabilityCountInfo> countInfos = new ArrayList<>();
        result.forEach(r -> {
            ElementVulnerabilityCountInfo countInfo = new ElementVulnerabilityCountInfo();
            countInfo.setPackageName(r.getFieldName());
            countInfo.setCount((int) r.getCount());
            countInfos.add(countInfo);
        });
        return countInfos;
    }

    private void processRequest(ElementMonitorRequest pageRequest, SceneHandler sceneHandler) {
        if (StringUtils.isEmpty(pageRequest.getStartTime())) {
            pageRequest.setStartTime(TimeUtils.localDateTimeToString(sceneHandler.getScene().getCreateTime()));
        }
        if (StringUtils.isEmpty(pageRequest.getEndTime())) {
            pageRequest.setEndTime(TimeUtils.localDateTimeToString(LocalDateTime.now()));
        }
    }

    private void processVulnerabilityRequest(ElementMonitorRequest pageRequest, SceneModel sceneModel, boolean isRunning) {
        LocalDateTime endTime;
        if (isRunning) {
            endTime = LocalDateTime.now();
        } else {
            endTime = sceneModel.getUpdateTime();
        }
        if (StringUtils.isEmpty(pageRequest.getStartTime())) {
            
            if (endTime.plusHours(-getWazuhVulnerabilityScanInterval()).isBefore(sceneModel.getCreateTime())) {
                pageRequest.setStartTime(TimeUtils.localDateTimeToString(sceneModel.getCreateTime()));
            } else {
                
                pageRequest.setStartTime(TimeUtils.localDateTimeToString(endTime.plusHours(-getWazuhVulnerabilityScanInterval())));
            }
        }
        if (StringUtils.isEmpty(pageRequest.getEndTime())) {
            pageRequest.setEndTime(TimeUtils.localDateTimeToString(endTime));
        }
    }

    private List<String> getAgentNames(ElementMonitorRequest pageRequest, SceneHandler sceneHandler) {
        List<String> agentNames;
        agentNames = sceneHandler.getElementMap().values().stream()
                .filter(e -> pageRequest.getElementIds().contains(e.getId()))
                .map(e -> e.getEid())
                .collect(Collectors.toList());
        return agentNames;
    }

    private ConnectionState convertWazuhNetConn2ConnState(WazuhNetworkConnectionModel model) {
        ConnectionState connectionState = new ConnectionState();
        connectionState.setState(model.getStatus());
        connectionState.setProtocol(model.getProto());
        connectionState.setPid(model.getPid());

        connectionState.setLocalAddress(model.getLaddrIp());
        connectionState.setLocalPort(model.getLaddrPort());

        connectionState.setForeignAddress(model.getRaddrIp());
        connectionState.setForeignPort(model.getRaddrPort());

        connectionState.setDescription(model.getDescription());
        connectionState.setTime(model.getTime());
        connectionState.setLevel(model.getLevel());

        return connectionState;
    }

    
    private ElementRTInfo processRTInfo(BaseSceneElementRequest baseRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);
        ElementRTInfo rtInfo = new ElementRTInfo();

        
        rtInfo.setUserCount(countNodeSessionUsers(baseRequest));

        TimeListPageInfoQueryParams portsParams = new TimeListPageInfoQueryParams();
        portsParams.setSceneIds(CollectionUtil.toList(baseRequest.getSceneId()));
        portsParams.setNodeIds(baseRequest.getElementIds());
        ListObjServiceResult<Port> portsResult = hostStateService.pagePorts(portsParams);

        
        TimeListPageInfoQueryParams processParams = new TimeListPageInfoQueryParams();
        processParams.setSceneIds(CollectionUtil.toList(baseRequest.getSceneId()));
        processParams.setNodeIds(baseRequest.getElementIds());
        ListObjServiceResult<Process> processesResult = hostStateService.pageProcesses(processParams);

        BaseSceneElementPageRequest pageRequest = new BaseSceneElementPageRequest();
        pageRequest.setSceneId(baseRequest.getSceneId());
        pageRequest.setElementIds(baseRequest.getElementIds());
        ListObjServiceResult<ConnectionState> conns = pageNetworkConnections(pageRequest);

        if (Objects.nonNull(conns.getSource().getTotal())) {
            
            rtInfo.setConnectionCount(conns.getSource().getTotal());
        }

        if (Objects.nonNull(portsResult.getSource().getTotal())) {
            
            rtInfo.setPortsCount(portsResult.getSource().getTotal());
        }

        if (Objects.nonNull(processesResult.getSource().getTotal())) {
            
            rtInfo.setProcessCount(processesResult.getSource().getTotal());
        }

        
        HostEventQueryParams hostEventQueryParams = new HostEventQueryParams();
        hostEventQueryParams.setNodeIds(baseRequest.getElementIds());
        ListObjServiceResult<CollectionHostEventBO> fileResult = csceneInstanceService.listHostEvents(baseRequest.getSceneId(), hostEventQueryParams);
        if (CollectionUtil.isNotEmpty(fileResult.getSource().getObjs())) {
            
            rtInfo.setFileOpCount(fileResult.getSource().getTotal());
        }

        
        hostEventQueryParams.setRuleGroups(CollectionUtil.toList("syscheck_registry"));
        ListObjServiceResult<CollectionHostEventBO> registryResult = csceneInstanceService.listHostEvents(baseRequest.getSceneId(), hostEventQueryParams);
        if (CollectionUtil.isNotEmpty(fileResult.getSource().getObjs())) {
            
            rtInfo.setRegistryOpCount(registryResult.getSource().getTotal());
        }

        
        
        int envVarChangedCount = sumEnvVarChangedCount(baseRequest);
        rtInfo.setEnvOpCount(envVarChangedCount);

        return rtInfo;
    }

    
    private int countNodeSessionUsers(BaseSceneElementRequest baseSceneElementRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneId(baseSceneElementRequest);
        ServiceResult<List<UserNodeSessionInfo>> result = csceneInstanceService.getNodeSessions(CollectionUtil.toList(baseSceneElementRequest.getSceneId()), baseSceneElementRequest.getElementIds(), null, null);
        if (CollectionUtil.isNotEmpty(result.getSource())) {
            return result.getSource().size();
        } else {
            return 0;
        }
    }

    private ListPageInfoQueryParams buildListPageInfoQueryParams(BaseSceneElementRequest baseRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);
        ListPageInfoQueryParams listPageInfoQueryParams = new ListPageInfoQueryParams();
        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(CollectionUtil.toList(baseRequest.getSceneId())));
        listPageInfoQueryParams.setNodeIds(baseRequest.getElementIds());
        return listPageInfoQueryParams;
    }


    private LocalDate convertDate(String date) {
        try {
            return TimeUtils.stringToLocalDate(date);
        } catch (Exception e) {
            log.warn("date parse error! {}", date);
        }

        try {
            LocalDateTime localDateTime = TimeUtils.utcToLocalDateTime(date);
            return localDateTime.toLocalDate();
        } catch (Exception e) {
            log.warn("date parse error! {}", date);
        }

        return null;
    }

    private List<ElementHistogramInfo> processHistogramList(LocalDateTime endTime, int bucketSize, int invervalMinutes, Map<String, Long> resultMap) {
        List<ElementHistogramInfo> infos = new ArrayList<>();
        var calculatedTime = LocalDateTime.of(endTime.toLocalDate(), LocalTime.of(endTime.getHour(), endTime.getMinute() / invervalMinutes * invervalMinutes));

        
        
        
        

        for (int i = 0; i < bucketSize; i++) {
            String timeStr = TimeUtils.localTime2yyyyMMddHHmm(calculatedTime);
            var info = new ElementHistogramInfo();
            if (Objects.nonNull(resultMap) && Objects.nonNull(resultMap.get(timeStr))) {
                info.setTime(timeStr);
                info.setCount(resultMap.get(timeStr).intValue());
            } else {
                info.setTime(timeStr);
                info.setCount(0);
            }
            infos.add(info);
            calculatedTime = calculatedTime.minusMinutes(invervalMinutes);
        }
        return infos;
    }


    private WazuhEventRequest buildWazuhRequest(ElementMonitorRequest pageRequest) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (StringUtils.isNotBlank(pageRequest.getStartTime())) {
            startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        }

        if (StringUtils.isNotBlank(pageRequest.getEndTime())) {
            endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
        }

        if (Objects.isNull(startTime)) {
            String translatedScendId = csceneCommonService.translateSceneId(pageRequest.getSceneId());
            startTime = csceneCommonService.getSceneCreateTime(translatedScendId);
        }

        if (Objects.isNull(startTime)) {
            startTime = LocalDateTime.now().minusHours(24);
        }

        if (Objects.isNull(endTime)) {
            endTime = LocalDateTime.now();
        }

        wazuhEventRequest.setStartTime(startTime);
        wazuhEventRequest.setEndTime(endTime);
        return wazuhEventRequest;
    }


}
