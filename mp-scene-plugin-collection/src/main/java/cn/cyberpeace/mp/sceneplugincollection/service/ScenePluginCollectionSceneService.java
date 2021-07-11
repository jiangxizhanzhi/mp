package cn.cyberpeace.mp.sceneplugincollection.service;

import cn.cyberpeace.mp.base.bo.IntervalSizeBO;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.FdfsRemoteFileServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudSceneConfigPluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerExecuteResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerFileGetData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincollection.bo.*;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneModel;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginWazuhRuleModel;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginWazuhRuleTypeModel;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginWazuhRuleTypeRelationModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneMapper;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginWazuhRuleMapper;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginWazuhRuleTypeMapper;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginWazuhRuleTypeRelationMapper;
import cn.cyberpeace.mp.sceneplugincollection.service.param.*;
import cn.cyberpeace.mp.sceneplugincollection.util.DatetimeUtil;
import cn.cyberpeace.mp.sceneplugincollection.util.EsHttpUtil;
import cn.cyberpeace.mp.sceneplugincollection.util.FlowPcapExportUtil;
import cn.cyberpeace.mp.situationaware.bo.*;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.model.*;
import cn.cyberpeace.mp.situationaware.model.enums.AggregationTypeEnum;
import cn.cyberpeace.mp.situationaware.model.enums.SeverityEnum;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.SystemUser;
import cn.cyberpeace.mp.situationaware.request.FlowAnalysisRequest;
import cn.cyberpeace.mp.situationaware.request.FlowSimulationRequest;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.FlowAnalysisService;
import cn.cyberpeace.mp.situationaware.service.FlowClassificationsService;
import cn.cyberpeace.mp.situationaware.service.ProbeService;
import cn.cyberpeace.mp.situationaware.service.wazuh.SysCollectorService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.URLUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScenePluginCollectionSceneService {
    private static String LOG_PREFIX = "collection service -> ";

    public static class GroupCountFlowField {
        public static final String LEVEL = "level";
        public static final String TYPE = "type";
        public static final String SOURCE_IP = "source_ip";
        public static final String DEST_IP = "dest_ip";
    }

    public static class GroupCountHostEventField {
        public static final String EVENT_TYPE = "eventType";
        public static final String NODE_NAME = "nodeName";
    }

    @Autowired
    private FlowClassificationsService flowClassificationsService;

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private FlowAnalysisService flowAnalysisService;

    @Autowired
    private SysCollectorService sysCollectorService;

    @Autowired
    private ProbeService probeService;

    @Autowired
    private CollectionPluginWazuhRuleTypeMapper collectionPluginWazuhRuleTypeMapper;

    @Autowired
    private CollectionPluginWazuhRuleTypeRelationMapper collectionPluginWazuhRuleTypeRelationMapper;

    @Autowired
    private CollectionPluginWazuhRuleMapper collectionPluginWazuhRuleMapper;

    @Autowired
    private CollectionAppConfig collectionAppConfig;

    @Autowired
    private CollectionPluginSceneMapper collectionPluginSceneMapper;

    @Autowired
    private FdfsRemoteFileServiceImpl fdfsRemoteFileService;


    public ListObjServiceResult<CollectionFlowBO> listFlows(List<String> sceneIds, FlowQueryParams queryParams) {
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, queryParams.getNodeIds());
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectors())) {
            log.info("{} query time interval sum flow size,sceneIds:{},no collection plugin or no flowCollector", LOG_PREFIX, JsonCommon.stringify(sceneIds));
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectedMacAddrs())) {
            log.info("{} query time interval sum flow size,sceneIds:{},no collection plugin or no flowCollector", LOG_PREFIX, JsonCommon.stringify(sceneIds));
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        if (CollectionUtils.isNotEmpty(queryParams.getSourceNodeIds())) {
            List<String> ips = new ArrayList<>();
            for (String nodeId : queryParams.getSourceNodeIds()) {
                var nodeIps = batchSceneHandler.getIpsByNodeId(nodeId);
                if (CollectionUtils.isNotEmpty(nodeIps)) {
                    ips.addAll(nodeIps);
                }
            }
            if (CollectionUtils.isEmpty(ips)) {
                ips.addAll(queryParams.getSourceNodeIds());
            }
            if (CollectionUtils.isEmpty(queryParams.getSourceIps())) {
                queryParams.setSourceIps(ips);
            }
        }
        if (CollectionUtils.isNotEmpty(queryParams.getTargetNodeIds())) {
            List<String> ips = new ArrayList<>();
            for (String nodeId : queryParams.getTargetNodeIds()) {
                var nodeIps = batchSceneHandler.getIpsByNodeId(nodeId);
                if (CollectionUtils.isNotEmpty(nodeIps)) {
                    ips.addAll(nodeIps);
                }
            }
            if (CollectionUtils.isEmpty(ips)) {
                ips.addAll(queryParams.getTargetNodeIds());
            }
            if (CollectionUtils.isEmpty(queryParams.getTargetIps())) {
                queryParams.setTargetIps(ips);
            }
        }

        FlowAnalysisRequest request = new FlowAnalysisRequest();
        request.setGroupList(batchSceneHandler.getFlowCollectors());
        request.setAttackTypes(queryParams.getAttackTypes());
        request.setAttackSeverities(queryParams.getSeverity());
        if (CollectionUtils.isNotEmpty(queryParams.getSourceIps())) {
            request.setSourceIps(queryParams.getSourceIps());
        } else {

        }

        if (CollectionUtils.isNotEmpty(queryParams.getTargetIps())) {
            request.setDestinationIps(queryParams.getTargetIps());
        } else {
            request.setDestinationIps(batchSceneHandler.getAllIpAddrs());
        }

        request.setSecondsInterval(queryParams.getInterval());
        
        if (StringUtils.isBlank(queryParams.getStartTime())) {
            request.setStartDateTime(batchSceneHandler.getEarliestStartTime());
        } else {
            request.setStartDateTime(TimeUtils.stringToLocalDateTime(queryParams.getStartTime()));
        }
        if (StringUtils.isBlank(queryParams.getEndTime())) {
            request.setEndDateTime(LocalDateTime.now());
        } else {
            request.setEndDateTime(TimeUtils.stringToLocalDateTime(queryParams.getEndTime()));
        }
        if (Objects.nonNull(queryParams.getPage())) {
            request.setPageNum(queryParams.getPage());
        }
        if (Objects.nonNull(queryParams.getPerPage())) {
            request.setPageSize(queryParams.getPerPage());
        }

        if (StringUtils.isNotBlank(queryParams.getSourcePort())) {
            request.setSourcePort(queryParams.getSourcePort());
        }

        if (StringUtils.isNotBlank(queryParams.getTargetPort())) {
            request.setDestinationPort(queryParams.getTargetPort());
        }

        if (StringUtils.isNotBlank(queryParams.getProtocol())) {
            request.setProtocols(CollectionUtil.toList(queryParams.getProtocol()));
        }
        if (StringUtils.isNotBlank(queryParams.getPort())) {
            request.setPort(queryParams.getPort());
        }


        request.setSeverityEnum(SeverityEnum.ALL);
        request.setFilterDestinationIps(queryParams.getFilterDestinationIps());
        ListObjResult<FlowLogBO> flowLogBOSResult = flowAnalysisService.flowLogWithTotal(request);
        if (CollectionUtils.isEmpty(flowLogBOSResult.getObjs())) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        if (flowLogBOSResult.getTotal() != null && flowLogBOSResult.getTotal() > 10000) {
            EsHttpUtil.updateDataMaxQueryNum(batchSceneHandler.getFlowCollectors().get(0), flowLogBOSResult.getTotal());
        }

        var flowLogBOS = flowLogBOSResult.getObjs();
        Map<String, FlowClassificationModel> allCategoryDicMap = flowClassificationsService.findAllCategoryDicMap();
        flowLogBOS.stream().filter(v -> Objects.nonNull(v.getSuricata())).forEach(flowLogBO -> {
            if (MapUtils.isNotEmpty(allCategoryDicMap)){
                List<String> categoryList;
                if (CollectionUtils.isNotEmpty(queryParams.getAttackTypes())) {
                    categoryList = flowLogBO.getSuricata().getCategory().stream()
                            .map(c -> this.globalizedWithAttackWay(queryParams.getAttackTypes().get(0), allCategoryDicMap)).collect(Collectors.toList());
                } else {
                    categoryList = flowLogBO.getSuricata().getCategory().stream()
                            .map(c -> this.globalizedWithAttackWay(c, allCategoryDicMap)).collect(Collectors.toList());
                }

                flowLogBO.getSuricata().setCategory(categoryList);
            }
        });

        List<CollectionFlowBO> list = flowLogBOSResult.getObjs().stream().map(flowLogBO -> this.buildCollectionFlowBOFrom(flowLogBO, batchSceneHandler.getAllMac2ElementMapForNodes(), batchSceneHandler.getAllIp2ElementMapForScene(), batchSceneHandler.getAllMac2SceneIdMap())).collect(Collectors.toList());
        return new ListObjServiceResult<>(new ListObjResult<>(list, flowLogBOSResult.getTotal()));
    }

    private String globalizedWithAttackWay(String attackWay,Map<String, FlowClassificationModel> allCategoryDicMap){
        if (MapUtils.isEmpty(allCategoryDicMap)){
            return attackWay;
        }
        FlowClassificationModel model = allCategoryDicMap.get(attackWay);
        String globalized = attackWay;
        if (Objects.nonNull(model)) {
            globalized = MessageCommon.getOrDefault(SituationalAwareConstant.ERROR_CODE_PREFIX + model.getName(),
                    attackWay);
        }
        return globalized;
    }

    public ListObjServiceResult<CollectionHostStateBO> listHostStates(String sceneId, HostStateQueryParams queryParams) {
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        
        CollectionScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (pluginHandler == null) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        
        List<SceneElementBO> srcElements = getSrcElements(sceneHandler, queryParams.getNodeIds());
        Map<String, SceneElementBO> agentName2Element = new HashMap<>();
        srcElements.forEach(srcElement -> {
            var collectionElement = pluginHandler.getElement(srcElement.getId());
            if (collectionElement == null || CollectionUtils.isEmpty(collectionElement.getProbes())) {
                return;
            }
            if (collectionElement.getEnableProbeCollect() == null || !collectionElement.getEnableProbeCollect()) {
                return;
            }
            agentName2Element.put(srcElement.getEid(), srcElement);
        });
        
        if (agentName2Element.isEmpty()) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        
        var request = new WazuhMonitorEventsRequest();
        request.setPageNum(queryParams.getPage());
        
        request.setPageSize(Optional.ofNullable(queryParams.getPageSize()).orElse(1000));
        request.setAgentNames(new ArrayList<>(agentName2Element.keySet()));
        
        request.setStartTime(Optional.ofNullable(queryParams.getStartTime()).orElse(sceneHandler.getScene().getCreateTime()));
        
        request.setEndTime(Optional.ofNullable(queryParams.getEndTime()).orElse(LocalDateTime.now()));
        return doQueryHostStateEventsWithTimeRange(sceneId, agentName2Element, request);
    }


    public ListObjServiceResult<CollectionHostStateBO> listHostStatesByAgentNames(List<String> agentNames, HostStateQueryParams queryParams) {
        
        if (CollectionUtil.isEmpty(agentNames)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        
        var request = new WazuhMonitorEventsRequest();
        request.setPageNum(queryParams.getPage());
        request.setPageSize(Optional.ofNullable(queryParams.getPageSize()).orElse(1000));
        request.setAgentNames(agentNames);
        
        request.setStartTime(Optional.ofNullable(queryParams.getStartTime()).orElse(LocalDateTime.now().minusHours(24)));
        
        request.setEndTime(Optional.ofNullable(queryParams.getEndTime()).orElse(LocalDateTime.now()));
        return doQueryHostStateEventsWithTimeRange(null, null, request);
    }

    public Map<String, List<CollectionHostStateBO>> mapHostStatesByAgentNames(List<String> agentNames, HostStateQueryParams queryParams) {
        
        if (CollectionUtil.isEmpty(agentNames)) {
            return Collections.emptyMap();
        }
        
        var request = new WazuhMonitorEventsRequest();
        int topSize = queryParams.getPageSize();
        request.setAgentNames(agentNames);
        
        request.setStartTime(Optional.ofNullable(queryParams.getStartTime()).orElse(LocalDateTime.now().minusHours(24)));
        
        request.setEndTime(Optional.ofNullable(queryParams.getEndTime()).orElse(LocalDateTime.now()));
        return doMapHostStateEventsWithTimeRange(null, null, request, queryParams.getPageSize());
    }

    private ListObjServiceResult<CollectionHostStateBO> doQueryHostStateEventsWithTimeRange(String sceneId, Map<String, SceneElementBO> agentName2Element, WazuhMonitorEventsRequest request) {
        
        List<WazuhHostStateModel> result = dataCollectionService.queryHostStateEventsWithTimeRange(request);
        List<CollectionHostStateBO> hostStates = result.stream().map(srcHostState -> {
            return hostStateModel2BO(sceneId, agentName2Element, srcHostState);
        }).collect(Collectors.toList());
        return new ListObjServiceResult<>(new ListObjResult<>(hostStates, result.size()));
    }


    private Map<String, List<CollectionHostStateBO>> doMapHostStateEventsWithTimeRange(String sceneId, Map<String, SceneElementBO> agentName2Element, WazuhMonitorEventsRequest request, int topSize) {
        
        Map<String, List<WazuhHostStateModel>> map = dataCollectionService.mapHostStatesByAgentNamesWithTimeRange(request, topSize);
        Map<String, List<CollectionHostStateBO>> convertedMap = new HashMap<>();
        map.forEach((agentName, result) -> {
            List<CollectionHostStateBO> hostStates = result.stream().map(srcHostState -> {
                return hostStateModel2BO(sceneId, agentName2Element, srcHostState);
            }).collect(Collectors.toList());

            convertedMap.put(agentName, hostStates);
        });

        return convertedMap;
    }


    private CollectionHostStateBO hostStateModel2BO(String sceneId, Map<String, SceneElementBO> agentName2Element, WazuhHostStateModel srcHostState) {
        CollectionHostStateBO hostState = new CollectionHostStateBO();
        hostState.setAgentName(srcHostState.getAgentName());
        hostState.setSceneId(sceneId);
        hostState.setCpuFreq(srcHostState.getCpuFreq());
        hostState.setCpuCount(srcHostState.getCpuCount());
        hostState.setCpuPercent(srcHostState.getCpuTotalPercent());
        
        hostState.setMemoryTotal(this.byte2MB(srcHostState.getMemoryTotal()));
        hostState.setMemoryFree(this.byte2MB(srcHostState.getMemoryFree()));
        hostState.setMemoryPercent(srcHostState.getMemoryPercent());
        
        hostState.setDiskSpace(this.byte2GB(srcHostState.getDiskTotal()));
        hostState.setDiskPercent(srcHostState.getDiskPercent());
        hostState.setDiskUsed(this.byte2GB(srcHostState.getDiskUsed()));
        hostState.setDiskUnused(this.byte2GB(srcHostState.getDiskUnused()));
        hostState.setTime(srcHostState.getTime());
        
        hostState.setDownloadSpeed(srcHostState.getDownloadSpeed());
        hostState.setUploadSpeed(srcHostState.getUploadSpeed());
        hostState.setDiskReadBytes(srcHostState.getDiskReadBytes());
        hostState.setDiskWriteBytes(srcHostState.getDiskWriteBytes());
        hostState.setDiskIoPercent(srcHostState.getDiskIoPercent());
        hostState.setMaxDownloadSpeed(srcHostState.getMaxDownloadSpeed());
        hostState.setMaxUploadSpeed(srcHostState.getMaxUploadSpeed());
        hostState.setMaxNetSpeed(srcHostState.getMaxNetSpeed());
        
        if (CollectionUtil.isNotEmpty(agentName2Element)) {
            var srcElement = agentName2Element.getOrDefault(srcHostState.getAgentName(), null);
            if (srcElement != null) {
                hostState.setNodeId(srcElement.getId());
                hostState.setNodeName(srcElement.getName());
            }
        }

        return hostState;
    }

    
    public ListObjServiceResult<CollectionHostEventBO> listHostEvents(String sceneId, HostEventQueryParams queryParams) {
        log.info("sceneId:{}, scroll:{}", sceneId, queryParams.getScroll());
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        List<SceneElementBO> srcElements = getSrcElements(sceneHandler, queryParams.getNodeIds());
        CollectionScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (pluginHandler == null) {
            return new ListObjServiceResult(new ListObjResult(Collections.emptyList(), 0)).asResult();
        }
        Map<String, SceneElementBO> agentNameElementMap = new HashMap<>();
        srcElements.forEach(srcElement -> {
            var collectionElement = pluginHandler.getElement(srcElement.getId());
            if (collectionElement == null || CollectionUtils.isEmpty(collectionElement.getProbes())) {
                return;
            }
            if (collectionElement.getEnableProbeCollect() == null || !collectionElement.getEnableProbeCollect()) {
                return;
            }
            agentNameElementMap.put(srcElement.getEid(), srcElement);
        });

        if (agentNameElementMap.isEmpty()) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        
        List<String> ruleIdsByCatagories = new ArrayList<>();
        Map<String, String> ruleId2File = getRuleId2FileByCodes(queryParams.getCatagories()).getSource();
        if (CollectionUtils.isNotEmpty(ruleId2File.keySet())) {
            List<String> ruleIds = ruleId2File.keySet().stream().distinct().collect(Collectors.toList());
            ruleIdsByCatagories.addAll(ruleIds);
        }

        
        List<String> ruleIdsByAppNames = new ArrayList<>();
        Map<String, String> ruleId2AppNames = getRuleId2FileByFiles(queryParams.getAppNames());
        if (CollectionUtils.isNotEmpty(ruleId2AppNames.keySet())) {
            List<String> ruleIds = ruleId2AppNames.keySet().stream().distinct().collect(Collectors.toList());
            ruleIdsByAppNames.addAll(ruleIds);
        }

        Map<String, String> ruleId2Files = new HashMap<>(ruleId2File);
        ruleId2Files.putAll(ruleId2AppNames);

        WazuhEventRequest request = genWazuhEventRequest(queryParams, sceneHandler.getScene().getCreateTime(), new ArrayList<>(agentNameElementMap.keySet()));
        if (CollectionUtils.isNotEmpty(ruleIdsByCatagories) && CollectionUtils.isNotEmpty(ruleIdsByAppNames)) {
            
            List<String> intersectionRuleIds = ruleIdsByCatagories.stream().filter(item -> ruleIdsByAppNames.contains(item)).distinct().collect(Collectors.toList());
            request.setRuleIds(intersectionRuleIds);
        } else {
            List<String> ruleIds = new ArrayList<>();
            ruleIds.addAll(ruleIdsByCatagories);
            ruleIds.addAll(ruleIdsByAppNames);
            if (CollectionUtils.isNotEmpty(ruleIds)) {
                request.setRuleIds(ruleIds);
            }
        }
        var result = dataCollectionService.queryFileEvents(request);

        CloudScenePluginHandler cloudScenePluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        List<CollectionHostEventBO> hostEvents = new ArrayList<>();
        result.getObjs().forEach(srcHostEvent -> {
            var hostEvent = new CollectionHostEventBO();
            hostEvent.setSceneId(sceneId);
            hostEvent.setType(srcHostEvent.getFileEventType().name());
            if (StringUtils.isEmpty(srcHostEvent.getAction())) {
                hostEvent.setEventDesc(srcHostEvent.getMessage());
            } else {
                hostEvent.setEventDesc(srcHostEvent.getAction());
            }
            hostEvent.setTime(srcHostEvent.getUpdateTime());
            hostEvent.setDescription(srcHostEvent.getDescription());
            hostEvent.setLevel(srcHostEvent.getLevel());
            hostEvent.setCatagory(srcHostEvent.getCatagory());
            hostEvent.setContent(srcHostEvent.getContent());
            hostEvent.setPath(srcHostEvent.getFilePath());
            hostEvent.setFileSize(srcHostEvent.getSize());
            hostEvent.setMd5Before(srcHostEvent.getMd5Before());
            hostEvent.setMd5After(srcHostEvent.getMd5After());
            hostEvent.setScrollId(srcHostEvent.getScrollId());
            if (CollectionUtils.isNotEmpty(queryParams.getCatagories())) {
                hostEvent.setAppName(ruleFile2AppName(ruleId2Files.getOrDefault(srcHostEvent.getRuleId(), "")).getSource());
            }
            var srcElement = agentNameElementMap.getOrDefault(srcHostEvent.getAgentName(), null);
            if (srcElement != null) {
                hostEvent.setNodeId(srcElement.getId());
                hostEvent.setNodeName(srcElement.getName());
                hostEvent.setSystemType(srcElement.getSystemType());
                var item = SystemTypeUtils.getSystemTypeItem(srcElement.getSystemType());
                hostEvent.setSystemTypeName(item.getName());
                if (cloudScenePluginHandler != null) {
                    var cloudElement = cloudScenePluginHandler.getElement(srcElement.getId());
                    if (cloudElement != null) {
                        hostEvent.setVirtualType(cloudElement.getVirtualType());
                    }
                }
            }
            hostEvents.add(hostEvent);
        });

        return new ListObjServiceResult<>(new ListObjResult<>(hostEvents, result.getTotal()));
    }

    
    public WazuhEventRequest genWazuhEventRequest(HostEventQueryParams queryParams, LocalDateTime sceneCreateTime, List<String> agentNames) {
        WazuhEventRequest request = new WazuhEventRequest();
        request.setPageNum(queryParams.getPage());
        request.setPageSize(queryParams.getPageSize());
        request.setAgentNames(agentNames);

        
        if (StringUtils.isBlank(queryParams.getStartTime())) {
            request.setStartTime(sceneCreateTime);
        } else {
            request.setStartTime(TimeUtils.stringToLocalDateTime(queryParams.getStartTime()));
        }
        if (StringUtils.isBlank(queryParams.getEndTime())) {
            request.setEndTime(LocalDateTime.now());
        } else {
            request.setEndTime(TimeUtils.stringToLocalDateTime(queryParams.getEndTime()));
        }
        if (CollectionUtils.isNotEmpty(queryParams.getLevels())) {
            request.setLevels(queryParams.getLevels());
        }
        if (CollectionUtils.isNotEmpty(queryParams.getKeyword())) {
            request.setKeyword(queryParams.getKeyword());
        }
        if (CollectionUtils.isNotEmpty(queryParams.getRuleGroups())) {
            request.setRuleGroups(queryParams.getRuleGroups());
        }
        if (StringUtils.isNotEmpty(queryParams.getPath())) {
            request.setPath(queryParams.getPath());
        }
        if (Objects.nonNull(queryParams.getScroll()) && queryParams.getScroll()) {
            request.setScroll(true);
        }
        if (Objects.nonNull(queryParams.getType()) && queryParams.getType() != 0) {
            request.setType(queryParams.getType());
        }

        return request;
    }

    
    public ListObjServiceResult<CollectionHostEventBO> scrollHostEvents(String sceneId, String scrollId, HostEventQueryParams queryParams) {
        log.info("sceneId:{}, scrollId:{}", sceneId, scrollId);
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        List<SceneElementBO> srcElements = getSrcElements(sceneHandler, queryParams.getNodeIds());
        CollectionScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (pluginHandler == null) {
            return new ListObjServiceResult(new ListObjResult(Collections.emptyList(), 0)).asResult();
        }
        Map<String, SceneElementBO> agentNameElementMap = new HashMap<>();
        srcElements.forEach(srcElement -> {
            var collectionElement = pluginHandler.getElement(srcElement.getId());
            if (collectionElement == null || CollectionUtils.isEmpty(collectionElement.getProbes())) {
                return;
            }
            if (collectionElement.getEnableProbeCollect() == null || !collectionElement.getEnableProbeCollect()) {
                return;
            }
            agentNameElementMap.put(srcElement.getEid(), srcElement);
        });

        ListObjResult<FileEventModel> result = dataCollectionService.scrollFileEvents(scrollId);

        CloudScenePluginHandler cloudScenePluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        List<CollectionHostEventBO> hostEvents = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(result.getObjs())) {
            result.getObjs().forEach(srcHostEvent -> {
                var hostEvent = new CollectionHostEventBO();
                hostEvent.setSceneId(sceneId);
                hostEvent.setType(srcHostEvent.getFileEventType().name());
                hostEvent.setEventDesc(srcHostEvent.getAction());
                hostEvent.setTime(srcHostEvent.getUpdateTime());
                hostEvent.setDescription(srcHostEvent.getDescription());
                hostEvent.setLevel(srcHostEvent.getLevel());
                hostEvent.setCatagory(srcHostEvent.getCatagory());
                hostEvent.setContent(srcHostEvent.getContent());
                hostEvent.setPath(srcHostEvent.getFilePath());
                hostEvent.setFileSize(srcHostEvent.getSize());
                hostEvent.setMd5Before(srcHostEvent.getMd5Before());
                hostEvent.setMd5After(srcHostEvent.getMd5After());
                hostEvent.setScrollId(srcHostEvent.getScrollId());
                var srcElement = agentNameElementMap.getOrDefault(srcHostEvent.getAgentName(), null);
                if (srcElement != null) {
                    hostEvent.setNodeId(srcElement.getId());
                    hostEvent.setNodeName(srcElement.getName());
                    hostEvent.setSystemType(srcElement.getSystemType());
                    var item = SystemTypeUtils.getSystemTypeItem(srcElement.getSystemType());
                    hostEvent.setSystemTypeName(item.getName());
                    if (cloudScenePluginHandler != null) {
                        var cloudElement = cloudScenePluginHandler.getElement(srcElement.getId());
                        if (cloudElement != null) {
                            hostEvent.setVirtualType(cloudElement.getVirtualType());
                        }
                    }
                }
                hostEvents.add(hostEvent);
            });
        }

        return new ListObjServiceResult<>(new ListObjResult<>(hostEvents, result.getTotal()));
    }

    
    public ServiceResult<Map<String, Integer>> groupCountFlowByLevel(List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime) {
        return groupCountFlow(sceneIds, nodeIds, GroupCountFlowField.LEVEL, startTime, endTime, null, null);
    }

    
    public ServiceResult<Map<String, Integer>> groupCountFlowByType(List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime, List<String> sourceIps, Integer topSize) {
        return groupCountFlow(sceneIds, nodeIds, GroupCountFlowField.TYPE, startTime, endTime, sourceIps, topSize);
    }


    
    public ServiceResult<Map<String, Integer>> groupCountHostEventByEventType(List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime) {
        return groupCountHostEvent(sceneIds, nodeIds, GroupCountHostEventField.EVENT_TYPE, startTime, endTime);
    }

    
    public ServiceResult<Map<String, Integer>> groupCountHostEventByNodeName(List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime) {
        return groupCountHostEvent(sceneIds, nodeIds, GroupCountHostEventField.NODE_NAME, startTime, endTime);
    }

    
    public ServiceResult<List<IntervalSizeBO<LocalDateTime, Long>>> timeIntervalCountFlow(List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime) {
        return new ServiceResult<>(new ArrayList<>());
    }

    
    public ServiceResult<List<IntervalSizeBO<LocalDateTime, Long>>> timeIntervalSumFlowSize(List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime, int intervalSeconds, List<String> sourceIps, Integer type) {
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, nodeIds);
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectors())) {
            log.info("{} query time interval sum flow size,sceneId:{},no collection plugin or no flowCollector", LOG_PREFIX, JsonCommon.stringify(sceneIds));
            return new ServiceResult<>(new ArrayList<>());
        }
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectedMacAddrs())) {
            log.info("{} query time interval sum flow size,sceneIds:{},nodeIds:{},no enabled macAddr", LOG_PREFIX, JsonCommon.stringify(sceneIds), JsonCommon.stringify(nodeIds));
            return new ServiceResult<>(new ArrayList<>());
        }
        
        FlowAnalysisRequest request = new FlowAnalysisRequest();
        request.setGroupList(batchSceneHandler.getFlowCollectors());
        if (CollectionUtils.isEmpty(sourceIps)) {

        } else {
            request.setSourceIps(sourceIps);
        }
        request.setDestinationIps(batchSceneHandler.getAllIpAddrs());
        request.setStartDateTime(Optional.ofNullable(startTime).orElse(batchSceneHandler.getEarliestStartTime()));
        request.setEndDateTime(Optional.ofNullable(endTime).orElse(LocalDateTime.now()));
        request.setSecondsInterval(intervalSeconds);
        request.setSeverityEnum(SeverityEnum.ALL);
        
        List<AttackDateHistogramNetworkBytesBO> bos;
        if (Objects.isNull(type) || NumberUtils.INTEGER_ZERO.equals(type)) {
            bos = flowAnalysisService.sumNetworkBytesByDateHistogramTimestamp(request);
        } else {
            bos = flowAnalysisService.countByDateHistogramTimestamp(request);
        }

        if (CollectionUtils.isEmpty(bos)) {
            return new ServiceResult<>(new ArrayList<>());
        }
        List<IntervalSizeBO<LocalDateTime, Long>> result = bos.stream().map(bo -> {
            LocalDateTime time = TimeUtils.stringToLocalDateTime(bo.getTime(), TimeUtils.tzMillsDateTimeFormatter).atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            return Objects.isNull(type) || NumberUtils.INTEGER_ZERO.equals(type) ? new IntervalSizeBO<>(time, time, bo.getTrafficSize().longValue()) : new IntervalSizeBO<>(time, time, bo.getAttackNumber());
        }).collect(Collectors.toList());
        Collections.sort(result, Comparator.comparing(IntervalSizeBO::getStart));
        return new ServiceResult<>(result);
    }

    
    public ServiceResult simulation(FlowSimulationParams params) {
        List<String> sceneIds = params.getSceneIds();

        List<String> nodeIds = params.getNodeIds();

        String sourceIp = params.getSourceIp();

        List<String> sourceMacs = params.getSourceMacs();

        String destinationIp = params.getDestinationIp();

        List<String> destinationMacs = params.getDestinationMacs();

        LocalDateTime dateTime = params.getDateTime();

        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, nodeIds);
        List<String> flowCollectors = batchSceneHandler.getFlowCollectors();
        if (CollectionUtils.isEmpty(flowCollectors)) {
            log.info("{} query time interval sum flow size,sceneId:{},no collection plugin or no flowCollector", LOG_PREFIX, JsonCommon.stringify(sceneIds));
            return new ServiceResult<>(new ArrayList<>());
        }

        
        FlowSimulationRequest request = new FlowSimulationRequest();
        request.setGroup(flowCollectors.get(0));
        request.setSourceIp(sourceIp);
        if (CollectionUtils.isEmpty(sourceMacs) && StringUtils.isNotBlank(sourceIp)) {
            request.setSourceMacs(Arrays.asList(batchSceneHandler.getMacByIp(sourceIp)));
        } else {
            request.setSourceMacs(sourceMacs);
        }
        request.setDestinationIp(destinationIp);
        if (CollectionUtils.isEmpty(destinationMacs) && StringUtils.isNotBlank(destinationIp)) {
            request.setDestinationMacs(Arrays.asList(batchSceneHandler.getMacByIp(destinationIp)));
        } else {
            request.setDestinationMacs(destinationMacs);
        }
        request.setNetworkBytes(params.getNetworkBytes());
        request.setShuffleCategory(params.getShuffleCategory());
        request.setDateTime(dateTime);

        try {
            flowAnalysisService.addSimulationFlow(request);
        } catch (IOException e) {
            log.error("simulation flow error : {}", e.getMessage(), e);
        }

        return new ServiceResult<>();
    }

    
    public ServiceResult<List<FlowGroupPortWithProtocolBO>> groupByDestinationPortWithProtocol(FlowGroupByDestinationPortParams params) {
        List<String> sceneIds = params.getSceneIds();

        List<String> nodeIds = params.getNodeIds();

        List<String> sourceIps = params.getSourceIps();

        LocalDateTime startTime = params.getStartTime();

        LocalDateTime endTime = params.getEndTime();

        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, nodeIds);
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectors())) {
            log.info("{} query time interval sum flow size,sceneId:{},no collection plugin or no flowCollector", LOG_PREFIX, JsonCommon.stringify(sceneIds));
            return new ServiceResult<>(new ArrayList<>());
        }
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectedMacAddrs())) {
            log.info("{} query time interval sum flow size,sceneIds:{},nodeIds:{},no enabled macAddr", LOG_PREFIX, JsonCommon.stringify(sceneIds), JsonCommon.stringify(nodeIds));
            return new ServiceResult<>(new ArrayList<>());
        }
        
        FlowAnalysisRequest request = new FlowAnalysisRequest();
        request.setGroupList(batchSceneHandler.getFlowCollectors());
        if (CollectionUtils.isEmpty(sourceIps)) {

        } else {
            request.setSourceIps(sourceIps);
        }
        request.setDestinationIps(batchSceneHandler.getAllIpAddrs());
        request.setStartDateTime(Optional.ofNullable(startTime).orElse(batchSceneHandler.getEarliestStartTime()));
        request.setEndDateTime(Optional.ofNullable(endTime).orElse(LocalDateTime.now()));
        request.setSeverityEnum(SeverityEnum.ALL);
        request.setTopSize(params.getTopSize());
        
        List<AttackPortAggregationBO> boList = flowAnalysisService.groupByDestinationPortWithProtocol(request);

        List<FlowGroupPortWithProtocolBO> result = boList.stream().map(v -> {
            FlowGroupPortWithProtocolBO bo = new FlowGroupPortWithProtocolBO();
            bo.setProtocol(v.getProtocol());
            bo.setPort(v.getPort());
            bo.setAttackNumber(v.getAttackNumber());
            bo.setTrafficSize(v.getTrafficSize());
            return bo;
        }).collect(Collectors.toList());
        return new ServiceResult<>(result);
    }

    
    public ServiceResult<List<FlowGroupByDestinationIpAndSeverityBO>> groupByDestinationIpAndSeverity(FlowGroupByDestinationPortParams params) {
        List<String> sceneIds = params.getSceneIds();

        List<String> nodeIds = params.getNodeIds();

        List<String> sourceIps = params.getSourceIps();

        LocalDateTime startTime = params.getStartTime();

        LocalDateTime endTime = params.getEndTime();

        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, nodeIds);
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectors())) {
            log.info("{} query time interval sum flow size,sceneId:{},no collection plugin or no flowCollector", LOG_PREFIX, JsonCommon.stringify(sceneIds));
            return new ServiceResult<>(new ArrayList<>());
        }
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectedMacAddrs())) {
            log.info("{} query time interval sum flow size,sceneIds:{},nodeIds:{},no enabled macAddr", LOG_PREFIX, JsonCommon.stringify(sceneIds), JsonCommon.stringify(nodeIds));
            return new ServiceResult<>(new ArrayList<>());
        }
        
        FlowAnalysisRequest request = new FlowAnalysisRequest();
        request.setGroupList(batchSceneHandler.getFlowCollectors());
        if (CollectionUtils.isNotEmpty(sourceIps)) {
            request.setSourceIps(sourceIps);
        }
        request.setDestinationIps(batchSceneHandler.getAllIpAddrs());
        request.setStartDateTime(Optional.ofNullable(startTime).orElse(batchSceneHandler.getEarliestStartTime()));
        request.setEndDateTime(Optional.ofNullable(endTime).orElse(LocalDateTime.now()));
        request.setSeverityEnum(SeverityEnum.ALL);
        
        List<AttackSeverityAndDestinationIpAggregationBO> boList = flowAnalysisService.groupByDestinationIpAndSeverity(request);

        List<AttackSeverityAndDestinationIpAggregationBO> sortedList = boList.stream().sorted(Comparator.comparing(AttackSeverityAndDestinationIpAggregationBO::getSeverity)).collect(Collectors.toList());

        List<String> destinationIpList = new ArrayList<>();

        List<FlowGroupByDestinationIpAndSeverityBO> resultList = new ArrayList<>();
        for (int i = 0; i < sortedList.size(); i++) {
            FlowGroupByDestinationIpAndSeverityBO result = new FlowGroupByDestinationIpAndSeverityBO();
            AttackSeverityAndDestinationIpAggregationBO bo = boList.get(i);
            result.setSeverity(bo.getSeverity());
            List<String> ipList = bo.getDestinationIpList();
            if (CollectionUtils.isNotEmpty(ipList)) {
                List<String> ips = ipList.stream().filter(v -> !destinationIpList.contains(v)).collect(Collectors.toList());
                result.setDestinationIpList(ips);
                result.setNum(ips.size());
                destinationIpList.addAll(ips);
            }
            resultList.add(result);
        }

        return new ServiceResult<>(resultList);
    }

    
    public ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> timeIntervalAvgHostCpuPercent(List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime, int intervalSeconds) {
        List<IntervalSizeBO<LocalDateTime, Double>> result = this.timeIntervalAvgBy(sceneIds, nodeIds, startTime, endTime, intervalSeconds, (state) -> state.getCpuPercent());
        return new ServiceResult<>(result);
    }

    public ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> timeIntervalLatestHostCpuPercent(List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime, int intervalSeconds) {
        List<IntervalSizeBO<LocalDateTime, Double>> result = this.timeIntervalLatestBy(sceneIds, nodeIds, startTime, endTime, intervalSeconds, (state) -> state.getCpuPercent());
        return new ServiceResult<>(result);
    }

    
    public ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> timeIntervalAvgHostMemoryPercent(List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime, int intervalNumber) {
        List<IntervalSizeBO<LocalDateTime, Double>> result = this.timeIntervalAvgBy(sceneIds, nodeIds, startTime, endTime, intervalNumber, (state) -> state.getMemoryPercent());
        return new ServiceResult<>(result);
    }

    public ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> timeIntervalLatestHostMemoryPercent(List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime, int intervalNumber) {
        List<IntervalSizeBO<LocalDateTime, Double>> result = this.timeIntervalLatestBy(sceneIds, nodeIds, startTime, endTime, intervalNumber, (state) -> state.getMemoryPercent());
        return new ServiceResult<>(result);
    }

    
    public ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> timeIntervalAvgHostDiskPercent(List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime, int intervalNumber) {
        
        List<IntervalSizeBO<LocalDateTime, Double>> result = this.timeIntervalAvgBy(sceneIds, nodeIds, startTime, endTime, intervalNumber, (state) -> state.getDiskPercent());
        return new ServiceResult<>(result);
    }

    public ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> timeIntervalLatestHostDiskPercent(List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime, int intervalNumber) {
        
        List<IntervalSizeBO<LocalDateTime, Double>> result = this.timeIntervalLatestBy(sceneIds, nodeIds, startTime, endTime, intervalNumber, (state) -> state.getDiskPercent());
        return new ServiceResult<>(result);
    }

    
    public ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> timeIntervalAvgPercentByAgentNames(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime, int intervalNumber, Function<CollectionHostStateBO, Double> execCal) {
        List<IntervalSizeBO<LocalDateTime, Double>> result = this.timeIntervalAvgByAgentNames(agentNames, startTime, endTime, intervalNumber, execCal);
        return new ServiceResult<>(result);
    }

    
    public List<IntervalSizeBO<LocalDateTime, Double>> timeIntervalAvgBy(List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime, int intervalSeconds, Function<CollectionHostStateBO, Double> execCal) {
        
        HostStateQueryParams params = new HostStateQueryParams();
        params.setNodeIds(nodeIds);
        
        params.setStartTime(startTime.minus(intervalSeconds, ChronoUnit.SECONDS));
        params.setEndTime(endTime);
        var queryResult = this.listHostStates(sceneIds.get(0), params).getSource();
        return processIntervalSizeBOS(startTime, intervalSeconds, execCal, queryResult, false);
    }

    
    public List<IntervalSizeBO<LocalDateTime, Double>> timeIntervalLatestBy(List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime, int intervalSeconds, Function<CollectionHostStateBO, Double> execCal) {
        HostStateQueryParams params = new HostStateQueryParams();
        params.setNodeIds(nodeIds);
        
        params.setStartTime(startTime.minus(intervalSeconds, ChronoUnit.SECONDS));
        params.setEndTime(endTime);
        var queryResult = this.listHostStates(sceneIds.get(0), params).getSource();
        return processIntervalSizeBOS(startTime, intervalSeconds, execCal, queryResult,true);
    }


    public List<IntervalSizeBO<LocalDateTime, Double>> timeIntervalAvgByAgentNames(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime, int intervalSeconds, Function<CollectionHostStateBO, Double> execCal) {
        
        HostStateQueryParams params = new HostStateQueryParams();
        
        params.setStartTime(startTime.minus(intervalSeconds, ChronoUnit.SECONDS));
        params.setEndTime(endTime);
        var queryResult = this.listHostStatesByAgentNames(agentNames, params).getSource();
        return processIntervalSizeBOS(startTime, intervalSeconds, execCal, queryResult, false);
    }

    private List<IntervalSizeBO<LocalDateTime, Double>> processIntervalSizeBOS(LocalDateTime startTime, int intervalSeconds, Function<CollectionHostStateBO, Double> execCal, ListObjResult<CollectionHostStateBO> queryResult , Boolean latestData) {
        if (queryResult == null || CollectionUtils.isEmpty(queryResult.getObjs())) {
            return new ArrayList<>();
        }
        List<CollectionHostStateBO> list = queryResult.getObjs();
        
        Collections.sort(list, Comparator.comparing(CollectionHostStateBO::getTime));
        LocalDateTime tmpMaxTime = startTime;
        BigDecimal tmpValue = null;
        BigDecimal tmpSize = null;
        List<IntervalSizeBO<LocalDateTime, Double>> result = new ArrayList<>();
        int index = 0;
        BigDecimal tmpLastValue = null;
        BigDecimal tmpLastSize = null;
        while (index < list.size()) {
            CollectionHostStateBO state = list.get(index);
            if (state.getTime().isBefore(tmpMaxTime)) {
                
                index++;
                double value = execCal.apply(state);
                if (Boolean.TRUE.equals(latestData)) {
                    tmpValue = BigDecimal.valueOf(value);
                    tmpSize = BigDecimal.ONE;
                } else {
                    tmpValue = Optional.ofNullable(tmpValue).orElse(BigDecimal.ZERO).add(BigDecimal.valueOf(value));
                    tmpSize = Optional.ofNullable(tmpSize).orElse(BigDecimal.ZERO).add(BigDecimal.ONE);
                }
            } else {
                
                
                if (tmpValue != null) {
                    result.add(new IntervalSizeBO<LocalDateTime, Double>(tmpMaxTime, tmpMaxTime, tmpValue.divide(tmpSize, 10, RoundingMode.HALF_DOWN).doubleValue()));
                } else if (tmpLastValue != null) {
                    result.add(new IntervalSizeBO<LocalDateTime, Double>(tmpMaxTime, tmpMaxTime, tmpLastValue.divide(tmpLastSize, 10, RoundingMode.HALF_DOWN).doubleValue()));
                }
                
                tmpMaxTime = tmpMaxTime.plus(intervalSeconds, ChronoUnit.SECONDS);
                tmpLastSize = tmpSize;
                tmpLastValue = tmpValue;
                tmpSize = null;
                tmpValue = null;
            }
        }
        
        if (tmpValue != null) {
            result.add(new IntervalSizeBO<LocalDateTime, Double>(tmpMaxTime, tmpMaxTime, tmpValue.divide(tmpSize, 10, RoundingMode.HALF_DOWN).doubleValue()));
        }
        return result;
    }

    private List<SceneElementBO> getSrcElements(SceneHandler sceneHandler, List<String> nodeIds) {
        List<SceneElementBO> srcElements;
        if (nodeIds == null || nodeIds.isEmpty()) {
            srcElements = new ArrayList<>(sceneHandler.getElementMap().values());
        } else {
            srcElements = new ArrayList<>();
            nodeIds.forEach(nodeId -> {
                var srcElement = sceneHandler.getElement(nodeId);
                if (srcElement != null) {
                    srcElements.add(srcElement);
                }
            });
        }
        return srcElements;
    }

    private CollectionFlowBO buildCollectionFlowBOFrom(FlowLogBO flowLogBO, Map<String, SceneElementBO> mac2ElementMap, Map<String, SceneElementBO> ipElementMap, Map<String, String> mac2SceneId) {
        CollectionFlowBO flowBO = new CollectionFlowBO();
        if (flowLogBO.getTimestamp() != null) {
            flowBO.setTime(TimeUtils.timestampToLocalDateTime(flowLogBO.getTimestamp()));
            flowBO.setTimestamp(flowLogBO.getTimestamp());
        }
        if (flowLogBO.getSource() != null) {
            String ip = flowLogBO.getSource().getIp();
            flowBO.setSourceIp(ip);
            flowBO.setSourcePort(String.valueOf(flowLogBO.getSource().getPort()));
            flowBO.setSourceMac(flowLogBO.getSource().getMac());
            if (ipElementMap.containsKey(ip)) {
                flowBO.setSourceNodeId(ipElementMap.get(ip).getId());
                flowBO.setSourceNodeName(ipElementMap.get(ip).getName());
                flowBO.setSceneId(mac2SceneId.get(ip));
            }
        }
        if (flowLogBO.getDestination() != null) {
            String ip = flowLogBO.getDestination().getIp();
            flowBO.setTargetPort(String.valueOf(flowLogBO.getDestination().getPort()));
            flowBO.setTargetMac(flowLogBO.getDestination().getMac());
            flowBO.setTargetIp(ip);
            if (ipElementMap.containsKey(ip)) {
                flowBO.setTargetNodeId(ipElementMap.get(ip).getId());
                flowBO.setTargetNodeName(ipElementMap.get(ip).getName());
                flowBO.setSceneId(mac2SceneId.get(ip));
            }











        }
        if (flowLogBO.getSuricata() != null) {
            List<String> category = flowLogBO.getSuricata().getCategory();
            List<Integer> severity = flowLogBO.getSuricata().getSeverity();
            if (CollectionUtils.isNotEmpty(category) && CollectionUtils.isNotEmpty(severity)) {
                int attackInfoNum = category.size() < severity.size() ? category.size() : severity.size();
                List<AttackInfo> attackInfos = new ArrayList<>();
                flowBO.setAttackInfos(attackInfos);
                for (int i = 0; i < attackInfoNum; i++) {
                    attackInfos.add(new AttackInfo(severity.get(i), category.get(i)));
                }
            }
        }
        if (flowLogBO.getNetwork() != null) {
            flowBO.setSize(flowLogBO.getNetwork().getBytes());
        }
        flowBO.setProtocol(flowLogBO.getProtocol());
        return flowBO;
    }

    private List<String> loadMac2ElementBOMapAndGetEnableMac(Map<String, SceneElementBO> mac2ElementMap, List<SceneElementBO> srcElements, CollectionScenePluginHandler pluginHandler) {
        List<String> enableMac = new ArrayList<>();
        srcElements.forEach(srcElement -> {
            var srcPorts = srcElement.getPorts();
            if (srcPorts == null) {
                return;
            }
            srcPorts.forEach(srcPort -> {
                var macAddr = srcPort.getMacAddr();
                if (StringUtils.isEmpty(macAddr)) {
                    return;
                }
                mac2ElementMap.put(macAddr, srcElement);
                var collectionPort = pluginHandler.getPort(srcPort.getId());
                if (collectionPort == null || !Boolean.TRUE.equals(collectionPort.getEnableFlowCollect())) {
                    return;
                }
                enableMac.add(macAddr);
            });
        });
        return enableMac;
    }

    
    private <T> ServiceResult<Map<T, Integer>> groupCountHostEvent(List<String> sceneIds, List<String> nodeIds, String groupField, LocalDateTime startTime, LocalDateTime endTime) {
        return new ServiceResult<>(new HashMap<>());
    }

    
    private <T> ServiceResult<Map<T, Integer>> groupCountFlow(List<String> sceneIds, List<String> nodeIds, String groupField, LocalDateTime startTime, LocalDateTime endTime, List<String> sourceIps, Integer topSize) {
        FlowAnalysisRequest request = new FlowAnalysisRequest();
        if (GroupCountFlowField.LEVEL.equals(groupField)) {
            request.setAggregationTypeEnum(AggregationTypeEnum.SURICATA_SEVERITY);
        } else if (GroupCountFlowField.TYPE.equals(groupField)) {
            request.setAggregationTypeEnum(AggregationTypeEnum.SURICATA_CATEGORY);
        }

        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, nodeIds);
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectors())) {
            return new ServiceResult<>(new HashMap<>());
        }
        request.setGroupList(batchSceneHandler.getFlowCollectors());
        request.setStartDateTime(Optional.ofNullable(startTime).orElse(batchSceneHandler.getEarliestStartTime()));
        request.setEndDateTime(Optional.ofNullable(endTime).orElse(LocalDateTime.now()));
        if (CollectionUtils.isNotEmpty(sourceIps)) {
            request.setSourceIps(sourceIps);
        } else {

        }
        request.setDestinationIps(batchSceneHandler.getAllIpAddrs());
        request.setTopSize(topSize);
        List<AttackWayAggregationBO> result = flowAnalysisService.topByAggregationTypeEnum(request);
        if (CollectionUtils.isEmpty(result)) {
            return new ServiceResult<>(new HashMap<>());
        }
        Map<T, Integer> finalResult = new HashMap<>();
        result.forEach(bo -> finalResult.put((T) bo.getAggregationType(), bo.getAttackNumber().intValue()));
        return new ServiceResult<>(finalResult);
    }

    public ServiceResult<Map<String, Integer>> groupCountFlowWithTopSize(List<String> sceneIds, List<String> ips, LocalDateTime startTime, LocalDateTime endTime) {
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, null);
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectors())) {
            return new ServiceResult<>(new HashMap<>());
        }
        LocalDateTime fStartTime = Optional.ofNullable(startTime).orElse(batchSceneHandler.getEarliestStartTime());
        LocalDateTime fEndTime = Optional.ofNullable(endTime).orElse(LocalDateTime.now());
        Map<String, Integer> result = this.doGroupCountFlowByIp(batchSceneHandler.getFlowCollectors(), ips, ips.size(), fStartTime, fEndTime);
        return new ServiceResult<>(result);
    }

    public ServiceResult<List<FlowAttackCountBO>> groupCountFlowAndNodeInfo(List<String> sceneIds, FlowGroupCountParams params) {
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, null);
        
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectors())) {
            return new ServiceResult<>(new ArrayList<>());
        }
        
        FlowAnalysisRequest request = new FlowAnalysisRequest();
        request.setGroupList(batchSceneHandler.getFlowCollectors());
        
        LocalDateTime fStartTime = Optional.ofNullable(params.getStartTime()).orElse(batchSceneHandler.getEarliestStartTime());
        LocalDateTime fEndTime = Optional.ofNullable(params.getEndTime()).orElse(LocalDateTime.now());
        request.setStartDateTime(fStartTime);
        request.setEndDateTime(fEndTime);
        
        List<String> paramsAddress = params.getQueryAddress();
        Integer queryTopSize = null;
        if (GroupCountFlowField.SOURCE_IP.equals(params.getQueryType())) {
            request.setSourceIps(CollectionUtils.isNotEmpty(paramsAddress) ? paramsAddress : batchSceneHandler.getFlowCollectedIpAddrs());
            queryTopSize = request.getSourceIps().size();
            request.setDestinationIps(batchSceneHandler.getAllIpAddrs());
            request.setAggregationTypeEnum(AggregationTypeEnum.SOURCE_IP);
            request.setSeverityEnum(SeverityEnum.ALL);
        } else if (GroupCountFlowField.DEST_IP.equals(params.getQueryType())) {
            request.setDestinationIps(CollectionUtils.isNotEmpty(paramsAddress) ? paramsAddress : batchSceneHandler.getAllIpAddrs());
            queryTopSize = request.getDestinationIps().size();
            request.setSourceIps(params.getSourceIps());
            request.setAggregationTypeEnum(AggregationTypeEnum.DESTINATION_IP);
            request.setSeverityEnum(SeverityEnum.ALL);
        } else {
            log.error("wrong params for groupCountFlowAndNodeInfo , params is {}", JsonCommon.stringify(params));
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        
        queryTopSize = Optional.ofNullable(params.getTopSize()).orElse(queryTopSize);
        request.setTopSize(queryTopSize);
        List<AttackWayAggregationBO> aggregationBOS = flowAnalysisService.topByAggregationTypeEnum(request);
        if (CollectionUtils.isEmpty(aggregationBOS)) {
            return new ServiceResult<>(new ArrayList<>());
        }
        List<FlowAttackCountBO> result = aggregationBOS.stream().map(v -> {
            String ip = v.getAggregationType();
            int count = v.getAttackNumber().intValue();
            SceneElementBO node = batchSceneHandler.getNodeByIpAddr(ip);
            return FlowAttackCountBO.builder().sceneId(batchSceneHandler.getSceneIdByIpAddr(ip)).nodeId(node == null ? StringUtils.EMPTY : node.getId()).nodeName(node == null ? StringUtils.EMPTY : node.getName()).ip(ip).times(count).build();
        }).collect(Collectors.toList());
        return new ServiceResult<>(result);
    }

    public ServiceResult<TeamAttackBO> groupCountFlow(List<String> sceneIds, List<String> ips, Integer topSize, LocalDateTime startTime, LocalDateTime endTime) {
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, null);
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectors())) {
            TeamAttackBO teamAttackBO = new TeamAttackBO();
            teamAttackBO.setAttackNumber(0L);
            teamAttackBO.setAttackWayList(new ArrayList<>());
            teamAttackBO.setTrafficSize((double) 0);
            return new ServiceResult<>(teamAttackBO);
        }
        LocalDateTime fStartTime = Optional.ofNullable(startTime).orElse(batchSceneHandler.getEarliestStartTime());
        LocalDateTime fEndTime = Optional.ofNullable(endTime).orElse(LocalDateTime.now());
        FlowAnalysisRequest flowAnalysisRequest = FlowAnalysisRequest.builder().groupList(batchSceneHandler.getFlowCollectors()).sourceIps(ips).startDateTime(fStartTime).endDateTime(fEndTime).topSize(topSize).aggregationTypeEnum(AggregationTypeEnum.SURICATA_CATEGORY).destinationIps(batchSceneHandler.getAllIpAddrs()).severityEnum(SeverityEnum.ALL).build();
        flowAnalysisRequest.setGroupList(batchSceneHandler.getFlowCollectors());
        TeamAttackBO teamAttackBO = flowAnalysisService.queryOneTeamAttackSumList(flowAnalysisRequest);
        return new ServiceResult<>(teamAttackBO);
    }

    private <T> Map<T, Integer> doGroupCountFlowByIp(List<String> flowCollectors, List<String> ips, Integer topSize, LocalDateTime startTime, LocalDateTime endTime) {
        FlowAnalysisRequest request = new FlowAnalysisRequest();
        request.setAggregationTypeEnum(AggregationTypeEnum.SOURCE_IP);
        request.setGroupList(flowCollectors);
        request.setStartDateTime(startTime);
        request.setEndDateTime(endTime);
        request.setSourceIps(ips);
        request.setTopSize(topSize);
        List<AttackWayAggregationBO> result = flowAnalysisService.topByAggregationTypeEnum(request);
        if (CollectionUtils.isEmpty(result)) {
            new HashMap<>();
        }
        Map<T, Integer> finalResult = new HashMap<>();
        result.forEach(bo -> finalResult.put((T) bo.getAggregationType(), bo.getAttackNumber().intValue()));
        return finalResult;
    }

    private long byte2MB(long num) {
        return num >> 20;
    }

    private long byte2GB(long num) {
        return num >> 30;
    }

    public List<Port> listPagePorts(String nodeId) {
        CollectorBaseRequest collectorBaseRequest = new CollectorBaseRequest();
        collectorBaseRequest.setAgentName(nodeId);
        collectorBaseRequest.setPage(1);
        collectorBaseRequest.setPageSize(10);
        collectorBaseRequest.setPerPage(10);
        return sysCollectorService.pagePorts(collectorBaseRequest).getSource().getObjs();
    }

    public ListObjServiceResult<Port> listPagePorts(ListPageInfoQueryParams listPageInfoQueryParams) {
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(listPageInfoQueryParams.getSceneIds(), null);
        Map<String, String> nodeId2Eidmap = batchSceneHandler.getProbeEnabledNodeId2EidMap();
        CollectorBaseRequest collectorBaseRequest = new CollectorBaseRequest();
        PageInfo<Port> pageInfo = new PageInfo();
        collectorBaseRequest.setAgentName(nodeId2Eidmap.get(listPageInfoQueryParams.getNodeIds().get(0)));
        collectorBaseRequest.setPage(listPageInfoQueryParams.getPage() == null ? 1 : listPageInfoQueryParams.getPage());
        collectorBaseRequest.setPageSize(listPageInfoQueryParams.getPageSize() == null ? 10 : listPageInfoQueryParams.getPageSize());
        collectorBaseRequest.setPerPage(listPageInfoQueryParams.getPerPage());
        if (Objects.isNull(collectorBaseRequest.getAgentName())) {
            return new ListObjServiceResult(new ListObjResult<>(Collections.emptyList(), 0).asResult()).asResult();
        }
        return sysCollectorService.pagePorts(collectorBaseRequest);
    }

    public ListObjServiceResult<Process> listPageProcess(TimeListPageInfoQueryParams listPageInfoQueryParams) {

        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(listPageInfoQueryParams.getSceneIds(), null);
        Map<String, String> nodeId2Eidmap = batchSceneHandler.getProbeEnabledNodeId2EidMap();

        int pageSize = listPageInfoQueryParams.getPageSize() == null ? 10 : listPageInfoQueryParams.getPageSize();
        int page = listPageInfoQueryParams.getPage() == null ? 1 : listPageInfoQueryParams.getPage();

        WazuhMonitorEventsRequest wazuhRequest = new WazuhMonitorEventsRequest();
        wazuhRequest.setAgentNames(Lists.newArrayList(nodeId2Eidmap.get(listPageInfoQueryParams.getNodeIds().get(0))));
        wazuhRequest.setPageSize(1);
        
        LocalDateTime endTime = LocalDateTime.now();
        if (StringUtils.isNotEmpty(listPageInfoQueryParams.getStartTime())) {
            wazuhRequest.setStartTime(TimeUtils.stringToLocalDateTime(listPageInfoQueryParams.getStartTime()));
        } else {
            wazuhRequest.setStartTime(endTime.minusHours(collectionAppConfig.getHoursRange()));
        }
        if (StringUtils.isNotEmpty(listPageInfoQueryParams.getEndTime())) {
            wazuhRequest.setEndTime(TimeUtils.stringToLocalDateTime(listPageInfoQueryParams.getEndTime()));
        } else {
            wazuhRequest.setEndTime(endTime);
        }
        if (CollectionUtils.isNotEmpty(listPageInfoQueryParams.getLevels())) {
            wazuhRequest.setLevels(listPageInfoQueryParams.getLevels());
        }

        if (wazuhRequest.getAgentNames().contains(null)) {
            return new ListObjServiceResult(new ListObjResult<>(Collections.emptyList(), 0).asResult()).asResult();
        }

        List<WazuhProcessModel> totalList = dataCollectionService.queryProcessesWithTimeRange(wazuhRequest);

        int skip = (page - 1) * pageSize;
        log.info("skip:{}, limit:{}", skip, pageSize);
        List<WazuhProcessModel> resultList = totalList.stream().filter(Objects::nonNull).skip(skip).limit(pageSize).collect(Collectors.toList());

        List<Process> processes = resultList.stream().map(p -> convertWazuhProcess2Process(p)).collect(Collectors.toList());

        return new ListObjServiceResult<>(new ListObjResult<>(processes, totalList.size()).asResult()).asResult();
    }


    public ListObjServiceResult<SystemUser> listPageSystemUserEvents(ListPageInfoQueryParams listPageInfoQueryParams) {

        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(listPageInfoQueryParams.getSceneIds(), null);
        Map<String, String> nodeId2Eidmap = batchSceneHandler.getProbeEnabledNodeId2EidMap();

        int pageSize = listPageInfoQueryParams.getPageSize() == null ? 10 : listPageInfoQueryParams.getPageSize();
        int page = listPageInfoQueryParams.getPage() == null ? 1 : listPageInfoQueryParams.getPage();

        WazuhMonitorEventsRequest wazuhRequest = new WazuhMonitorEventsRequest();
        wazuhRequest.setAgentNames(Lists.newArrayList(nodeId2Eidmap.get(listPageInfoQueryParams.getNodeIds().get(0))));
        wazuhRequest.setPageSize(1);
        
        LocalDateTime endTime = LocalDateTime.now();
        wazuhRequest.setStartTime(endTime.minusHours(collectionAppConfig.getHoursRange()));
        wazuhRequest.setEndTime(endTime);
        wazuhRequest.setLevels(listPageInfoQueryParams.getLevels());

        if (wazuhRequest.getAgentNames().contains(null)) {
            return new ListObjServiceResult(new ListObjResult<>(Collections.emptyList(), 0).asResult()).asResult();
        }

        List<WazuhSystemUserModel> totalList = dataCollectionService.queryUserChangeEventWithTimeRange(wazuhRequest);

        int skip = (page - 1) * pageSize;
        log.info("skip:{}, limit:{}", skip, pageSize);
        List<WazuhSystemUserModel> resultList = totalList.stream().filter(Objects::nonNull).skip(skip).limit(pageSize).collect(Collectors.toList());

        List<SystemUser> systemUsers = resultList.stream().map(u -> {
            SystemUser systemUser = new SystemUser();
            BeanUtils.copyProperties(u, systemUser);
            return systemUser;
        }).collect(Collectors.toList());

        return new ListObjServiceResult<>(new ListObjResult<>(systemUsers, totalList.size()).asResult()).asResult();
    }

    public List<Netiface> listNetifaces(String elementId) {
        CollectorBaseRequest collectorBaseRequest = new CollectorBaseRequest();
        PageInfo<Netiface> pageInfo = new PageInfo<>();
        collectorBaseRequest.setAgentName(elementId);
        collectorBaseRequest.setPage(1);
        collectorBaseRequest.setPageSize(10);
        collectorBaseRequest.setPerPage(10);
        if (Objects.isNull(collectorBaseRequest.getAgentName())) {
            return new ArrayList<>();
        }
        return sysCollectorService.pageNetifaces(collectorBaseRequest).getSource().getObjs();
    }


    public ListObjServiceResult<Netiface> listNetifaces(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(listPageInfoQueryParams.getSceneIds(), null);
        Map<String, String> nodeId2Eidmap = batchSceneHandler.getProbeEnabledNodeId2EidMap();
        CollectorBaseRequest collectorBaseRequest = new CollectorBaseRequest();
        PageInfo<Netiface> pageInfo = new PageInfo<>();
        collectorBaseRequest.setAgentName(nodeId2Eidmap.get(listPageInfoQueryParams.getNodeIds().get(0)));
        collectorBaseRequest.setPage(listPageInfoQueryParams.getPage() == null ? 1 : listPageInfoQueryParams.getPage());
        collectorBaseRequest.setPageSize(listPageInfoQueryParams.getPageSize() == null ? 10 : listPageInfoQueryParams.getPageSize());
        collectorBaseRequest.setPerPage(listPageInfoQueryParams.getPerPage());
        if (Objects.isNull(collectorBaseRequest.getAgentName())) {
            return new ListObjServiceResult(new ListObjResult<>(Collections.emptyList(), 0).asResult()).asResult();
        }
        
        
        return sysCollectorService.pageNetifaces(collectorBaseRequest);
    }


    public ListObjServiceResult<WazuhNetworkConnectionModel> listNetworkConnections(ListPageInfoQueryParams listPageInfoQueryParams) {
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(listPageInfoQueryParams.getSceneIds(), null);
        Map<String, String> nodeId2Eidmap = batchSceneHandler.getProbeEnabledNodeId2EidMap();

        int pageSize = listPageInfoQueryParams.getPageSize() == null ? 10 : listPageInfoQueryParams.getPageSize();
        int page = listPageInfoQueryParams.getPage() == null ? 1 : listPageInfoQueryParams.getPage();

        WazuhMonitorEventsRequest wazuhRequest = new WazuhMonitorEventsRequest();
        wazuhRequest.setAgentNames(Lists.newArrayList(nodeId2Eidmap.get(listPageInfoQueryParams.getNodeIds().get(0))));
        wazuhRequest.setPageSize(1);
        
        LocalDateTime endTime = LocalDateTime.now();
        wazuhRequest.setStartTime(endTime.minusHours(collectionAppConfig.getHoursRange()));
        wazuhRequest.setEndTime(endTime);
        wazuhRequest.setLevels(listPageInfoQueryParams.getLevels());

        if (wazuhRequest.getAgentNames().contains(null)) {
            return new ListObjServiceResult(new ListObjResult<>(Collections.emptyList(), 0).asResult()).asResult();
        }

        List<WazuhNetworkConnectionModel> totalList = dataCollectionService.queryNetConnEventsWithTimeRange(wazuhRequest);

        int skip = (page - 1) * pageSize;
        log.info("skip:{}, limit:{}", skip, pageSize);
        List<WazuhNetworkConnectionModel> resultList = totalList.stream().skip(skip).limit(pageSize).collect(Collectors.toList());

        return new ListObjServiceResult<>(new ListObjResult<>(resultList, totalList.size()).asResult()).asResult();
    }


    public ServiceResult<Integer> sumEnvVarChangedCount(ListPageInfoQueryParams listPageInfoQueryParams) {
        WazuhMonitorEventsRequest wazuhRequest = buildWazuhMonitorEventsRequest(listPageInfoQueryParams);
        int changedCount = dataCollectionService.sumEnvVarChangedCountByTimeRange(wazuhRequest);
        return new ServiceResult<>(changedCount);
    }

    public ServiceResult<Integer> sumProcessesChangedCount(ListPageInfoQueryParams listPageInfoQueryParams) {
        WazuhMonitorEventsRequest wazuhRequest = buildWazuhMonitorEventsRequest(listPageInfoQueryParams);
        int changedCount = dataCollectionService.sumProcessesChangedCountByTimeRange(wazuhRequest);
        return new ServiceResult<>(changedCount);
    }

    public ServiceResult<Integer> sumSysUserChangedCount(ListPageInfoQueryParams listPageInfoQueryParams) {
        WazuhMonitorEventsRequest wazuhRequest = buildWazuhMonitorEventsRequest(listPageInfoQueryParams);
        int changedCount = dataCollectionService.sumSysUsersChangedCountByTimeRange(wazuhRequest);
        return new ServiceResult<>(changedCount);
    }

    
    public ServiceResult<List<SceneElementBO>> listProbeEnabledElementBOs(String sceneId) {
        log.info("sceneId:{}", sceneId);
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(CollectionUtil.toList(sceneId), null);
        
        Map<String, String> nodeId2Eidmap = batchSceneHandler.getProbeEnabledNodeId2EidMap();

        
        List<SceneElementBO> elementBOS = nodeId2Eidmap.values().stream().map(eid -> batchSceneHandler.getNodeByEid(eid)).collect(Collectors.toList());
        return new ServiceResult<>(elementBOS);
    }


    public ServiceResult<List<String>> listIPs(String sceneId, List<String> nodeIds) {
        log.info("sceneId:{}, nodeIds:{}", sceneId, nodeIds);
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(CollectionUtil.toList(sceneId), nodeIds);
        return new ServiceResult<>(batchSceneHandler.getAllIps());
    }

    public ServiceResult<List<SceneAssetsBO>> listSceneAssets(List<String> sceneIds) {
        List<SceneAssetsBO> sceneAssets = new ArrayList<>();
        sceneIds.forEach(s -> {
            BatchSceneHandler batchSceneHandler = new BatchSceneHandler(CollectionUtil.toList(s), null);
            SceneAssetsBO asset = new SceneAssetsBO();
            asset.setSceneId(s);
            asset.setIpCount(batchSceneHandler.getAllIps().size());
            asset.setNodeCount(batchSceneHandler.getAllEid2ElementMap().keySet().size());
            asset.setCategoryGroups(new ArrayList<>(batchSceneHandler.getCategory2CountInfo().values()));
            sceneAssets.add(asset);
        });
        return new ServiceResult<>(sceneAssets);
    }

    public ServiceResult<SceneConfigAssetBO> listSceneConfigAssets(String sceneConfigId) {
        SceneConfigAssetBO sceneConfigAsset = new SceneConfigAssetBO();
        sceneConfigAsset.setSceneId(sceneConfigId);
        Map<Integer, SceneConfigCategoryGroupBO> category2CountInfo = new HashMap<>();
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneConfigId, null, null);
        CloudSceneConfigPluginHandler cloudConfigPlugin = sceneConfigHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        sceneConfigAsset.setNodeCount(sceneConfigHandler.getElementMap().values().size());
        sceneConfigHandler.getElementMap().values().forEach(e -> {
            var cloudElement = cloudConfigPlugin.getElement(e.getId());
            Integer category = cloudElement.getCategory();
            if (!category2CountInfo.containsKey(category)) {
                category2CountInfo.put(category, new SceneConfigCategoryGroupBO(category));
            }
            category2CountInfo.get(category).getElements().add(e);
        });
        sceneConfigAsset.setCategoryGroups(new ArrayList<>(category2CountInfo.values()));
        return new ServiceResult<>(sceneConfigAsset);
    }

    
    public void asyncCleanProbeData(String sceneId) {
        TaskCommon.executeTask(() -> {
            BatchSceneHandler batchSceneHandler = new BatchSceneHandler(Arrays.asList(sceneId), null);

            
            List<String> flowCollectors = batchSceneHandler.getFlowCollectors();
            if (CollectionUtils.isNotEmpty(flowCollectors)) {
                flowAnalysisService.delete(flowCollectors);
                var model = collectionPluginSceneMapper.first(new HumanCondition().filterEqual(CollectionPluginSceneModel::getScene, sceneId));
                if (model != null && StringUtils.isNotEmpty(model.getFlowFile())) {
                    String key = URLUtil.url(model.getFlowFile()).getPath();
                    fdfsRemoteFileService.delete(key);
                }
            }

            
            List<String> probeEnabledEid = new ArrayList<>(batchSceneHandler.getProbeEnabledNodeId2EidMap().values());
            if (CollectionUtils.isNotEmpty(probeEnabledEid)) {
                probeService.cleanProbeData(probeEnabledEid, batchSceneHandler.getEarliestStartTime());
            }

        });
    }

    
    private List<String> getTypeIds(List<String> types) {
        HumanCondition humanCondition = new HumanCondition().currentLink(CollectionPluginWazuhRuleTypeModel.class).filterIn(CollectionPluginWazuhRuleTypeModel::getType, types.toArray());
        var typeModels = collectionPluginWazuhRuleTypeMapper.list(humanCondition);
        return typeModels.stream().map(t -> t.getId()).collect(Collectors.toList());
    }

    
    private List<String> getTypeIdsByCodes(List<String> codes) {
        HumanCondition humanCondition = new HumanCondition().currentLink(CollectionPluginWazuhRuleTypeModel.class).filterIn(CollectionPluginWazuhRuleTypeModel::getCode, codes.toArray());
        var typeModels = collectionPluginWazuhRuleTypeMapper.list(humanCondition);
        return typeModels.stream().map(t -> t.getId()).collect(Collectors.toList());
    }

    
    private List<String> getTypes(List<String> typeIds) {
        HumanCondition humanCondition = new HumanCondition().currentLink(CollectionPluginWazuhRuleTypeModel.class).filterIn(CollectionPluginWazuhRuleTypeModel::getId, typeIds.toArray());
        var typeModels = collectionPluginWazuhRuleTypeMapper.list(humanCondition);
        return typeModels.stream().map(t -> t.getType()).collect(Collectors.toList());
    }

    
    public ServiceResult<String> ruleFile2AppName(String ruleFileName) {
        if (StringUtils.isBlank(ruleFileName)) {
            return new ServiceResult<>(null);
        }
        int firstIndex = StringUtils.indexOf(ruleFileName, "-");
        ruleFileName = StringUtils.substring(ruleFileName, firstIndex + 1);
        ruleFileName = StringUtils.substringBefore(ruleFileName, "_rules.xml");
        return new ServiceResult<>(ruleFileName);
    }

    
    public ServiceResult<Map<String, String>> getRuleId2FileByIds(List<String> ruleIds) {
        if (CollectionUtils.isEmpty(ruleIds)) {
            return new ServiceResult<>(new HashMap<>());
        }
        HumanCondition humanCondition = new HumanCondition().currentLink(CollectionPluginWazuhRuleModel.class).filterIn(CollectionPluginWazuhRuleModel::getId, ruleIds.toArray());
        var ruleModels = collectionPluginWazuhRuleMapper.list(humanCondition);
        return new ServiceResult<>(ruleModels.stream().collect(Collectors.toMap(r -> r.getId(), r -> r.getFile())));
    }

    
    private Map<String, String> getRuleId2FileByFiles(List<String> files) {
        if (CollectionUtils.isEmpty(files)) {
            return new HashMap<>();
        }
        HumanCondition humanCondition = new HumanCondition().currentLink(CollectionPluginWazuhRuleModel.class);
        var a = humanCondition.filterOrStart();
        for (String f : files) {
            a.orContains(CollectionPluginWazuhRuleModel::getFile, f);
        }
        a.filterOrEnd();
        var ruleModels = collectionPluginWazuhRuleMapper.list(humanCondition);
        if (CollectionUtils.isEmpty(ruleModels)) {
            return new HashMap<>();
        }
        return ruleModels.stream().collect(Collectors.toMap(r -> r.getId(), r -> r.getFile()));
    }

    
    public ServiceResult<Map<String, String>> getRuleId2FileByCodes(List<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            return new ServiceResult<>(new HashMap<>());
        }
        List<String> typeIds = getTypeIdsByCodes(codes);
        HumanCondition humanCondition = new HumanCondition().currentLink(CollectionPluginWazuhRuleTypeRelationModel.class).filterIn(CollectionPluginWazuhRuleTypeRelationModel::getTypeId, typeIds.toArray());
        var relationModels = collectionPluginWazuhRuleTypeRelationMapper.list(humanCondition);
        if (CollectionUtils.isEmpty(relationModels)) {
            return new ServiceResult<>(new HashMap<>());
        }
        List<String> ruleIds = relationModels.stream().distinct().map(r -> r.getRuleId()).collect(Collectors.toList());
        return getRuleId2FileByIds(ruleIds);
    }

    public ServiceResult<String> exportFlowPcap(String sceneId, String taskId) {
        var model = collectionPluginSceneMapper.first(new HumanCondition().filterEqual(CollectionPluginSceneModel::getScene, sceneId));
        if (model != null && StringUtils.isNotEmpty(model.getFlowFile())) {
            return new ServiceResult<>(model.getFlowFile());
        }
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(Lists.newArrayList(sceneId), null);
        List<String> flowCollectors = batchSceneHandler.getFlowCollectors();
        if (CollectionUtils.isEmpty(flowCollectors)) {
            log.error("find no flowCollectors by sceneId {}", sceneId);
            return new ServiceResult<>(StringUtils.EMPTY);
        }
        String containName = flowCollectors.get(0);
        var result = this.exportFlowPcap(sceneId, containName, taskId);
        return result;
    }

    public ServiceResult<Boolean> saveFlowPcap(String sceneId, String containName) {
        try {
            String url = this.exportFlowPcap(sceneId, containName).getSource();
            if (StringUtils.isNotEmpty(url)) {
                CollectionPluginSceneModel model = new CollectionPluginSceneModel();
                model.setFlowFile(url);
                this.collectionPluginSceneMapper.filterUpdate(model, new HumanCondition().filterEqual(CollectionPluginSceneModel::getScene, sceneId));
            }
            return new ServiceResult<>(true);
        } catch (Exception e) {
            log.error("faild to save flow pcap", e);
            return new ServiceResult<>(false);
        }
    }

    public ServiceResult<String> exportFlowPcap(String sceneId, String containName, String taskId) {
        
        Container container = new Container();
        var containerResult = container.getContainerByName(containName);
        if (containerResult == null) {
            log.error("find no containerId by containName {}", containName);
            return new ServiceResult<>(StringUtils.EMPTY);
        }
        String containerId = containerResult.getId();
        String host = containerResult.getHost();

        
        ContainerExecuteResult result = container.executeCommand(containerId, String.format("ls /opt/arkime/raw"));
        if (result.getExitCode() != 0) {
            log.error("exec ls cmd fail,sceneId is {}, exit code {}, msg {}", sceneId, result.getExitCode(), result.getOutput());
            return new ServiceResult<>(StringUtils.EMPTY);
        }

        if (StringUtils.isEmpty(result.getOutput())) {
            log.error("exec ls cmd fail, sceneId is {}, exit code {}, result is empty", sceneId, result.getExitCode());
            return new ServiceResult<>(StringUtils.EMPTY);
        }

        FlowPcapExportUtil.updateProcess(taskId, 5);

        
        String dirName = TextUtils.uuid();
        String dir = "/opt/arkime/" + dirName;
        container.executeCommand(containerId, "mkdir -p " + dir);
        
        List<String> commands = new ArrayList<>();

        String[] files = StringUtils.split(result.getOutput(), "\n");
        int step = Math.max(65 / files.length / 3, 1);
        int stepEcho = 0;
        String stepFile = dir + "/step";
        for (int i = 0; i < files.length; i++) {
            String pcapFile = files[i];
            String newFileName = (i + 1) + ".pcap";
            String newFile = dir + "/" + newFileName;
            
            commands.add(String.format("cd /opt/arkime/raw && cp %s %s ", pcapFile, newFile));
            stepEcho = stepEcho + step;
            commands.add(String.format("echo %s >> %s", stepEcho, stepFile));
            
            commands.add(String.format("cd %s && zip %s flow.zip %s ", dir, i == 0 ? "" : "-g", newFileName));
            stepEcho = stepEcho + step * 2;
            commands.add(String.format("echo %s >> %s", stepEcho, stepFile));
            
            commands.add(String.format("rm -rf %s ", newFileName));
        }
        commands.add(String.format("echo success >> %s", stepFile));
        
        File zipSh = null;
        try {
            zipSh = new File(dirName + ".sh");
            FileUtils.writeStringToFile(zipSh, StringUtils.join(commands, "&&"), Charset.defaultCharset());
            container.copyFile(containerId, zipSh.getPath(), dir);
            
            TaskCommon.executeTask(() -> {
                try {
                    container.executeCommand(containerId, String.format("sh %s/%s.sh", dir, dirName));
                } catch (Exception e) {
                    log.error("failed to get command result , container id is ", containerId);
                }
            });
            
            int times = 0;
            int maxTimes = 180;
            int lastStep = -1;
            while (times < maxTimes) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    var stepResult = container.executeCommand(containerId, "cat /opt/arkime/" + dirName + "/step");
                    if (stepResult != null && stepResult.getExitCode() != null && stepResult.getOutput() != null) {
                        if (stepResult.getOutput().contains("success")) {
                            break;
                        } else {
                            String[] steps = stepResult.getOutput().split("\n");
                            if (steps != null && steps.length > 0) {
                                int currentStep = Integer.parseInt(steps[steps.length - 1]);
                                if (currentStep != lastStep) {
                                    lastStep = currentStep;
                                    times = 0;
                                    FlowPcapExportUtil.updateProcess(taskId, currentStep);
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                }
                times++;
            }

            ContainerFileGetData fileGetData = new ContainerFileGetData();
            fileGetData.setFilePath(dir + "/flow.zip");
            fileGetData.setHostname(host);
            fileGetData.setZunContainerId(containerId);
            var object = container.retrieveFile(fileGetData);
            return new ServiceResult<>(object.getUrl());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zipSh != null) {
                zipSh.delete();
            }
            container.executeCommand(containerId, "rm -rf " + dir);
        }
        return new ServiceResult<>(null);
    }

    
    private Map<String, String> getRuleId2FileByTypes(List<String> types) {
        if (CollectionUtils.isEmpty(types)) {
            return new HashMap<>();
        }
        List<String> typeIds = getTypeIds(types);
        HumanCondition humanCondition = new HumanCondition().currentLink(CollectionPluginWazuhRuleTypeRelationModel.class).filterIn(CollectionPluginWazuhRuleTypeRelationModel::getTypeId, typeIds.toArray());
        var relationModels = collectionPluginWazuhRuleTypeRelationMapper.list(humanCondition);
        if (CollectionUtils.isEmpty(relationModels)) {
            return new HashMap<>();
        }
        List<String> ruleIds = relationModels.stream().distinct().map(r -> r.getRuleId()).collect(Collectors.toList());
        return getRuleId2FileByIds(ruleIds).getSource();
    }

    
    private List<String> getTypesByRuleIds(List<String> ruleIds) {
        if (CollectionUtils.isEmpty(ruleIds)) {
            return new ArrayList<>();
        }
        HumanCondition humanCondition = new HumanCondition().currentLink(CollectionPluginWazuhRuleTypeRelationModel.class).filterIn(CollectionPluginWazuhRuleTypeRelationModel::getRuleId, ruleIds.toArray());
        var relationModels = collectionPluginWazuhRuleTypeRelationMapper.list(humanCondition);
        if (CollectionUtils.isEmpty(relationModels)) {
            return new ArrayList<>();
        }
        List<String> typeIds = relationModels.stream().distinct().map(r -> r.getTypeId()).collect(Collectors.toList());
        return getTypes(typeIds);
    }


    private WazuhMonitorEventsRequest buildWazuhMonitorEventsRequest(ListPageInfoQueryParams listPageInfoQueryParams) {
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(listPageInfoQueryParams.getSceneIds(), null);
        Map<String, String> nodeId2Eidmap = batchSceneHandler.getProbeEnabledNodeId2EidMap();

        WazuhMonitorEventsRequest wazuhRequest = new WazuhMonitorEventsRequest();
        wazuhRequest.setAgentNames(Lists.newArrayList(nodeId2Eidmap.get(listPageInfoQueryParams.getNodeIds().get(0))));
        LocalDateTime endTime = LocalDateTime.now();
        wazuhRequest.setStartTime(batchSceneHandler.getEarliestStartTime());
        wazuhRequest.setEndTime(endTime);
        return wazuhRequest;
    }


    private Process convertWazuhProcess2Process(WazuhProcessModel wazuhProcess) {
        if (Objects.isNull(wazuhProcess)) {
            return null;
        }
        Process process = new Process();
        process.setState(wazuhProcess.getStatus());
        process.setPid(StringUtils.isNotBlank(wazuhProcess.getPid()) ? Integer.valueOf(wazuhProcess.getPid()) : null);
        process.setPpid(StringUtils.isNotBlank(wazuhProcess.getParentId()) ? Integer.valueOf(wazuhProcess.getParentId()) : null);
        process.setStartTime(getProcessStartTime(wazuhProcess.getCreateTime()));
        process.setVmSize(StringUtils.isNotBlank(wazuhProcess.getVms()) ? Long.valueOf(wazuhProcess.getVms()) : 0);
        process.setSize(StringUtils.isNotBlank(wazuhProcess.getVms()) ? Long.valueOf(wazuhProcess.getVms()) : 0);
        process.setPriority(wazuhProcess.getPriority());
        process.setCmd(wazuhProcess.getCommand());
        process.setEuser(wazuhProcess.getUsername());
        process.setName(wazuhProcess.getName());
        process.setDescription(wazuhProcess.getDescription());
        process.setTime(wazuhProcess.getTime());
        process.setLevel(wazuhProcess.getLevel());
        return process;
    }

    private String getProcessStartTime(String startTime) {
        if (StringUtils.isBlank(startTime)) {
            return null;
        }
        LocalDateTime localDateTime = DatetimeUtil.parseDateTimeFronLacunchOutput(startTime);
        String dateTime = DatePattern.NORM_DATETIME_FORMAT.getDateTimeFormatter().format(localDateTime);
        return dateTime;
    }


}
