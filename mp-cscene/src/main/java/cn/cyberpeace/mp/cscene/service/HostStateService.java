package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.bo.IntervalSizeBO;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.CscenePluginDataRecordService;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.constant.CsceneBasei18nCode;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.request.agent.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.hoststate.HostStateTrendRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.cscene.util.SegmentCount2IntervalSecondsUtil;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostStateBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneDataModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostStateQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.ListPageInfoQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.model.WazuhHostStateModel;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.SystemUser;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.wazuh.AgentService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
public class HostStateService {


    @Value("${mp.cscene.default.hours-range:24}")
    private Integer hoursRange;

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private CsceneCommonService csceneCommonService;

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private CsceneSpecialService csceneSpecialService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private CsceneAppConfig csceneAppConfig;

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    @Autowired
    private CscenePluginDataRecordService dataRecordService;


    public List<HostState> getHostStatesFromWazuh(String sceneId, String agentName, LocalDateTime startTime, LocalDateTime endTime) {
        List<CollectionHostStateBO> hostStateBOS = foo(agentName, startTime, endTime);

        List<HostState> hostStates = hostStateBOS.stream().map(hostStateBO -> {
            HostState hs = new HostState();
            hs.setAgentName(hostStateBO.getAgentName());
            hs.setCpuCount(hostStateBO.getCpuCount());
            hs.setCpuPercent(Objects.nonNull(hostStateBO.getCpuPercent()) ? NumberUtil.round(hostStateBO.getCpuPercent(), 2).doubleValue() : hostStateBO.getCpuPercent());
            hs.setMemoryFree(hostStateBO.getMemoryFree());
            hs.setMemoryTotal(hostStateBO.getMemoryTotal());
            

            hs.setDiskSpace(hostStateBO.getDiskSpace());
            hs.setDiskPercent(hostStateBO.getDiskPercent());
            
            hs.setDiskUnused(hostStateBO.getDiskUnused());
            hs.setSceneId(sceneId);

            hs.setTime(hostStateBO.getTime());
            return hs;
        }).collect(Collectors.toList());

        return hostStates;
    }


    private List<CollectionHostStateBO> foo(String agentName, LocalDateTime startTime, LocalDateTime endTime) {
        
        var request = new WazuhMonitorEventsRequest();
        request.setPageNum(1);
        
        request.setPageSize(situationAwareConfig.getWazuhEsMaxPageSize());
        request.setAgentNames(Arrays.asList(agentName));
        
        request.setStartTime(startTime);
        
        request.setEndTime(endTime);

        
        List<WazuhHostStateModel> result = dataCollectionService.queryHostStateEventsWithTimeRange(request);
        List<CollectionHostStateBO> hostStateBOs = result.stream().map(srcHostState -> {
            CollectionHostStateBO hostStateBO = new CollectionHostStateBO();
            hostStateBO.setCpuCount(srcHostState.getCpuCount());
            hostStateBO.setCpuPercent(srcHostState.getCpuTotalPercent());
            
            hostStateBO.setMemoryTotal(this.byte2MB(srcHostState.getMemoryTotal()));
            hostStateBO.setMemoryFree(this.byte2MB(srcHostState.getMemoryFree()));
            
            hostStateBO.setDiskSpace(this.byte2GB(srcHostState.getDiskTotal()));
            hostStateBO.setMemoryPercent(srcHostState.getMemoryPercent());
            hostStateBO.setTime(srcHostState.getTime());
            





            return hostStateBO;
        }).collect(Collectors.toList());

        return hostStateBOs;
    }

    private long byte2MB(long num) {
        return num >> 20;
    }

    private long byte2GB(long num) {
        return num >> 30;
    }

    
    public List<HostState> getElementHostStateTrend(HostStateTrendRequest trendRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(trendRequest);
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());
        List<String> nodeIds = CollectionUtil.toList(trendRequest.getElementIds().get(0));

        return processHostStateTrend(sceneIds, nodeIds, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }


    
    public List<HostState> getSceneHostStateTrend(HostStateTrendRequest trendRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneId(trendRequest);
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());

        return processHostStateTrend(sceneIds, null, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }


    
    public List<HostState> getClusterHostStateTrend(HostStateTrendRequest trendRequest) {
        if (StringUtils.isBlank(csceneAppConfig.getCpIp()) || StringUtils.isBlank(csceneAppConfig.getCpPort())) {
            log.error("cp cloud ip or port is null...");
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }
        List<String> agentNames = getAgentNames().stream().filter(name -> Objects.nonNull(name) && name.contains("controller")).collect(Collectors.toList());
        return processHostStateTrendByAgentNames(agentNames, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }

    private List<HostState> processHostStateTrend(List<String> sceneIds, List<String> elementIds, LocalDateTime startTime, LocalDateTime endTime, Integer segmentCount) {
        
        LocalDateTime sceneStartTime = null ;
        if (Objects.isNull(endTime)) {
            endTime = LocalDateTime.now();
            if (CollectionUtils.isNotEmpty(sceneIds)) {
                var result = csceneSpecialService.getSceneStateInfo(sceneIds.get(0));
                if (!result.getSource().isRunning() && result.getSource().getSceneModel() != null) {
                    endTime = result.getSource().getSceneModel().getUpdateTime();
                }
                if (result.getSource().getSceneModel() != null) {
                    sceneStartTime = result.getSource().getSceneModel().getCreateTime();
                }
            }
        }

        if (Objects.isNull(startTime)) {
            
            startTime = endTime.minusHours(hoursRange);
            if (sceneStartTime != null && sceneStartTime.isAfter(startTime)) {
                startTime = sceneStartTime;
            }
        }

        if (Objects.isNull(segmentCount)) {
            segmentCount = 10;
        }

        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);
        Long intervalSeconds = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(startTime, endTime, segmentCount);
        List<LocalDateTime> timeList = SegmentCount2IntervalSecondsUtil.generateTimeList(startTime, endTime, intervalSeconds);

        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> cpuResult = scenePluginCollectionSceneService.timeIntervalLatestHostCpuPercent(sceneIdList, elementIds, startTime, endTime, intervalSeconds.intValue());
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> memResult = scenePluginCollectionSceneService.timeIntervalLatestHostMemoryPercent(sceneIdList, elementIds, startTime, endTime, intervalSeconds.intValue());
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> diskResult = scenePluginCollectionSceneService.timeIntervalLatestHostDiskPercent(sceneIdList, elementIds, startTime, endTime, intervalSeconds.intValue());

        return listHostStates(timeList, cpuResult, memResult, diskResult);
    }

    private List<HostState> processHostStateTrendByAgentNames(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime, Integer segmentCount) {
        
        if (Objects.isNull(endTime)) {
            endTime = LocalDateTime.now();
        }

        if (Objects.isNull(startTime)) {
            
            startTime = endTime.minusHours(hoursRange);
        }

        if (Objects.isNull(segmentCount)) {
            segmentCount = 10;
        }

        Long intervalSeconds = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(startTime, endTime, segmentCount);
        List<LocalDateTime> timeList = SegmentCount2IntervalSecondsUtil.generateTimeList(startTime, endTime, intervalSeconds);

        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> cpuResult = scenePluginCollectionSceneService.timeIntervalAvgPercentByAgentNames(agentNames, startTime, endTime, intervalSeconds.intValue(), (state) -> state.getCpuPercent());
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> memResult = scenePluginCollectionSceneService.timeIntervalAvgPercentByAgentNames(agentNames, startTime, endTime, intervalSeconds.intValue(), (state) -> state.getMemoryPercent());
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> diskResult = scenePluginCollectionSceneService.timeIntervalAvgPercentByAgentNames(agentNames, startTime, endTime, intervalSeconds.intValue(), (state) -> state.getDiskPercent());

        return listHostStates(timeList, cpuResult, memResult, diskResult);
    }


    
    
    public List<HostState> listHostStates(BaseSceneElementPageRequest pageRequest) {
        pageRequest.ensure();
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(pageRequest);
        String sceneId = pageRequest.getSceneId();
        List<String> elementIds = pageRequest.getElementIds();
        HostStateQueryParams queryParams = new HostStateQueryParams();
        queryParams.setNodeIds(elementIds);
        queryParams.setPage(pageRequest.getPage());
        queryParams.setPageSize(pageRequest.getPageSize());

        ListObjServiceResult<CollectionHostStateBO> listObjServiceResult = csceneInstanceService.listHostStates(sceneId, queryParams);

        List<CollectionHostStateBO> hostStateBOS = listObjServiceResult.getSource().getObjs();

        return collectionHostStateBOs2HostStates(hostStateBOS);
    }

    public List<HostState> listHostStatesByAgentName(AgentNameBasedRequest request) {
        request.ensure();
        if (CollectionUtil.isEmpty(request.getAgentNames())) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.AGENT_NAME)));
        }
        HostStateQueryParams queryParams = new HostStateQueryParams();
        queryParams.setPage(request.getPage());
        queryParams.setPageSize(request.getPageSize());

        ListObjServiceResult<CollectionHostStateBO> listObjServiceResult = csceneInstanceService.listHostStatesByAgentNames(request.getAgentNames(), queryParams);

        List<CollectionHostStateBO> hostStateBOS = listObjServiceResult.getSource().getObjs();

        return collectionHostStateBOs2HostStates(hostStateBOS);
    }

    public Map<String, List<HostState>> mapHostStatesByAgentName(AgentNameBasedRequest request) {
        request.ensure();
        if (CollectionUtil.isEmpty(request.getAgentNames())) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.AGENT_NAME)));
        }
        HostStateQueryParams queryParams = new HostStateQueryParams();
        queryParams.setPage(request.getPage());
        queryParams.setPageSize(request.getPageSize());
        Map<String, List<CollectionHostStateBO>> map = csceneInstanceService.mapHostStatesByAgentNames(request.getAgentNames(), queryParams);
        Map<String, List<HostState>> convertedMap = new HashMap<>();
        map.forEach((k, v) -> {
            List<HostState> hostStates = collectionHostStateBOs2HostStates(v);
            convertedMap.put(k, hostStates);
        });

        return convertedMap;
    }


    private List<HostState> collectionHostStateBOs2HostStates(List<CollectionHostStateBO> hostStateBOS) {
        List<HostState> hostStates = hostStateBOS.stream().map(hostStateBO -> {
            HostState hs = new HostState();
            hs.setAgentName(hostStateBO.getAgentName());
            hs.setCpuFreq(hostStateBO.getCpuFreq());
            hs.setCpuCount(hostStateBO.getCpuCount());
            hs.setCpuPercent(Objects.nonNull(hostStateBO.getCpuPercent()) ? NumberUtil.round(hostStateBO.getCpuPercent(), 2).doubleValue() : hostStateBO.getCpuPercent());

            hs.setMemoryFree(hostStateBO.getMemoryFree());
            hs.setMemoryTotal(hostStateBO.getMemoryTotal());
            hs.setMemoryPercent(Objects.nonNull(hostStateBO.getMemoryPercent()) ? NumberUtil.round(hostStateBO.getMemoryPercent(), 2).doubleValue() : hostStateBO.getMemoryPercent());

            hs.setDiskSpace(hostStateBO.getDiskSpace());
            hs.setDiskPercent(hostStateBO.getDiskPercent());
            
            hs.setDiskUnused(hostStateBO.getDiskUnused());

            hs.setSceneId(hostStateBO.getSceneId());
            hs.setElementId(hostStateBO.getNodeId());
            hs.setTime(hostStateBO.getTime());

            hs.setDownloadSpeed(hostStateBO.getDownloadSpeed());
            hs.setUploadSpeed(hostStateBO.getUploadSpeed());
            hs.setMaxDownloadSpeed(hostStateBO.getMaxDownloadSpeed());
            hs.setMaxUploadSpeed(hostStateBO.getMaxUploadSpeed());
            hs.setMaxNetSpeed(hostStateBO.getMaxNetSpeed());
            hs.setDiskReadBytes(hostStateBO.getDiskReadBytes());
            hs.setDiskWriteBytes(hostStateBO.getDiskWriteBytes());
            hs.setDiskIoPercent(hostStateBO.getDiskIoPercent());
            return hs;
        }).collect(Collectors.toList());

        return hostStates;
    }


    private List<HostState> listHostStates(List<LocalDateTime> timeList, ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> cpuResult, ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> memResult, ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> diskResult) {
        List<IntervalSizeBO<LocalDateTime, Double>> intervalCpuPercentBOs = cpuResult.getSource();
        List<IntervalSizeBO<LocalDateTime, Double>> intervalMemPercentBOs = memResult.getSource();
        List<IntervalSizeBO<LocalDateTime, Double>> intervalDiskPercentBOs = diskResult.getSource();

        Map<LocalDateTime, HostState> hostStateMap = new LinkedHashMap<>();
        for (LocalDateTime time : timeList) {
            HostState hostState = new HostState();
            
            hostState.setTime(time);
            hostState.setCpuPercent(0.0);
            hostState.setMemoryPercent(0.0);
            
            hostState.setDiskPercent(0.0);
            hostStateMap.put(time, hostState);
        }

        
        if (Objects.nonNull(intervalCpuPercentBOs)) {
            for (IntervalSizeBO<LocalDateTime, Double> bo : intervalCpuPercentBOs) {
                if (Objects.nonNull(hostStateMap.get(bo.getStart()))) {
                    hostStateMap.get(bo.getStart()).setCpuPercent(Objects.nonNull(bo.getSize()) ? NumberUtil.round(bo.getSize(), 2).doubleValue() : 0.0);
                }
            }
        }

        
        if (Objects.nonNull(intervalMemPercentBOs)) {
            for (IntervalSizeBO<LocalDateTime, Double> bo : intervalMemPercentBOs) {
                if (Objects.nonNull(hostStateMap.get(bo.getStart()))) {
                    hostStateMap.get(bo.getStart()).setMemoryPercent(Objects.nonNull(bo.getSize()) ? NumberUtil.round(bo.getSize(), 2).doubleValue() : 0.0);
                }
            }
        }

        
        if (Objects.nonNull(intervalDiskPercentBOs)) {
            for (IntervalSizeBO<LocalDateTime, Double> bo : intervalDiskPercentBOs) {
                if (Objects.nonNull(hostStateMap.get(bo.getStart()))) {
                    hostStateMap.get(bo.getStart()).setDiskPercent(Objects.nonNull(bo.getSize()) ? NumberUtil.round(bo.getSize(), 2).doubleValue() : 0.0);
                }
            }
        }

        List<HostState> hostStates = new ArrayList<>();
        hostStateMap.entrySet().forEach(entry -> {
            hostStates.add(entry.getValue());
        });
        hostStates.sort(Comparator.comparing(HostState::getTime));

        return hostStates;
    }

    public ListObjServiceResult<Port> pagePorts(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        listPageInfoQueryParams.ensure();
        checkListPageParams(listPageInfoQueryParams);
        if (Objects.isNull(listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) || !listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) {
            if (!csceneCommonService.isCsceneInstanceRunning(listPageInfoQueryParams.getSceneIds().get(0))) {
                return new ListObjServiceResult(new ListObjResult()).asResult();
            }
        }

        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(listPageInfoQueryParams.getSceneIds()));

        if (listPageInfoQueryParams.getStartTime() != null || listPageInfoQueryParams.getEndTime() != null) {
            BatchSceneHandler batchSceneHandler = new BatchSceneHandler(listPageInfoQueryParams.getSceneIds(), null);
            LocalDateTime startTime = null;
            if (StringUtils.isNotEmpty(listPageInfoQueryParams.getStartTime())) {
                startTime = TimeUtils.stringToLocalDateTime(listPageInfoQueryParams.getStartTime());
            } else {
                startTime = batchSceneHandler.getEarliestStartTime();
            }
            LocalDateTime endTime = null;
            if (StringUtils.isNotEmpty(listPageInfoQueryParams.getEndTime())) {
                endTime = TimeUtils.stringToLocalDateTime(listPageInfoQueryParams.getEndTime());
            } else {
                endTime = LocalDateTime.now();
            }
            var dataModels = dataRecordService.getData(listPageInfoQueryParams, CollectionPluginSceneDataModel.Type.PORT, startTime, endTime);
            var ports = dataModels.stream().map(d -> JsonCommon.parse(d.getData(), Port.class)).collect(Collectors.toList());
            return new ListObjServiceResult(new ListObjResult<>(ports, ports.size()).asResult()).asResult();
        } else {
            return scenePluginCollectionSceneService.listPagePorts(listPageInfoQueryParams).asResult();
        }
    }

    public ListObjServiceResult<Process> pageProcesses(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        listPageInfoQueryParams.ensure();
        checkListPageParams(listPageInfoQueryParams);
        if (Objects.isNull(listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) || !listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) {
            if (!csceneCommonService.isCsceneInstanceRunning(listPageInfoQueryParams.getSceneIds().get(0))) {
                return new ListObjServiceResult(new ListObjResult()).asResult();
            }
        }

        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(listPageInfoQueryParams.getSceneIds()));
        ListObjServiceResult<Process> listResult = scenePluginCollectionSceneService.listPageProcess(listPageInfoQueryParams).asResult();
        List<Process> processes = listResult.getSource().getList();
        
        processes.forEach(p -> {
            if (Objects.nonNull(p.getSize())) {
                Long megabytes = DataSize.ofKilobytes(p.getSize()).toMegabytes();
                p.setSize(megabytes);
            }

            if (Objects.nonNull(p.getVmSize())) {
                Long megabytes = DataSize.ofKilobytes(p.getVmSize()).toMegabytes();
                p.setVmSize(megabytes);
            }
        });

        return listResult;
    }


    public ListObjServiceResult<SystemUser> pageSystemUserEvents(ListPageInfoQueryParams listPageInfoQueryParams) {
        listPageInfoQueryParams.ensure();
        checkListPageParams(listPageInfoQueryParams);
        if (Objects.isNull(listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) || !listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) {
            if (!csceneCommonService.isCsceneInstanceRunning(listPageInfoQueryParams.getSceneIds().get(0))) {
                return new ListObjServiceResult(new ListObjResult()).asResult();
            }
        }

        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(listPageInfoQueryParams.getSceneIds()));
        ListObjServiceResult<SystemUser> listResult = scenePluginCollectionSceneService.listPageSystemUserEvents(listPageInfoQueryParams).asResult();
        return listResult;
    }


    
    public ListObjServiceResult<Netiface> pageNetifaces(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        listPageInfoQueryParams.ensure();
        checkListPageParams(listPageInfoQueryParams);
        if (Objects.isNull(listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) || !listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) {
            if (!csceneCommonService.isCsceneInstanceRunning(listPageInfoQueryParams.getSceneIds().get(0))) {
                return new ListObjServiceResult(new ListObjResult()).asResult();
            }
        }

        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(listPageInfoQueryParams.getSceneIds()));
        if (listPageInfoQueryParams.getStartTime() != null || listPageInfoQueryParams.getEndTime() != null) {
            BatchSceneHandler batchSceneHandler = new BatchSceneHandler(listPageInfoQueryParams.getSceneIds(), null);
            LocalDateTime startTime = null;
            if (StringUtils.isNotEmpty(listPageInfoQueryParams.getStartTime())) {
                startTime = TimeUtils.stringToLocalDateTime(listPageInfoQueryParams.getStartTime());
            } else {
                startTime = batchSceneHandler.getEarliestStartTime();
            }
            LocalDateTime endTime = null;
            if (StringUtils.isNotEmpty(listPageInfoQueryParams.getEndTime())) {
                endTime = TimeUtils.stringToLocalDateTime(listPageInfoQueryParams.getEndTime());
            } else {
                endTime = LocalDateTime.now();
            }
            var dataModels = dataRecordService.getData(listPageInfoQueryParams, CollectionPluginSceneDataModel.Type.NETIFACE, startTime, endTime);
            var netifaces = dataModels.stream().map(d -> {
                return JsonCommon.parse(d.getData(), Netiface.class);
            }).collect(Collectors.toList());
            return new ListObjServiceResult(new ListObjResult<>(netifaces, netifaces.size()).asResult()).asResult();
        } else {
            ListObjServiceResult<Netiface> netifacesResult = scenePluginCollectionSceneService.listNetifaces(listPageInfoQueryParams).asResult();
            return netifacesResult;
        }
    }

    private void checkListPageParams(ListPageInfoQueryParams params) {
        List<String> sceneIds = params.getSceneIds();
        if (CollectionUtil.isEmpty(sceneIds)) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.SCENE_ID)));
        }

        List<String> nodeIds = params.getNodeIds();
        if (CollectionUtil.isEmpty(nodeIds)) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.ELEMENT_LIST)));
        }
    }

    private List<String> getAgentNames() {
        String url = "http://" + csceneAppConfig.getCpIp() + ":" + csceneAppConfig.getCpPort() + "/v1/hypervisors";
        String result = HttpRequest.get(url).addHeaders(wrapperHeaders(new HashMap<>())).execute().body();
        if (StringUtils.isBlank(result)) {
            return new ArrayList<>();
        }
        Map<String, Object> resultMap = JsonCommon.parseMap(result);
        Map<String, Object> data = (Map<String, Object>) resultMap.get("data");
        if (Objects.isNull(data) || !data.containsKey("list")) {
            return new ArrayList<>();
        }
        List<Map<String, String>> list = (List<Map<String, String>>) data.get("list");
        List<String> agentNames = list.stream().map(map -> map.getOrDefault("hostname", "")).collect(Collectors.toList());
        return agentNames;
    }

    
    private Map<String, String> wrapperHeaders(Map<String, String> headers) {
        if (MapUtils.isEmpty(headers)) {
            headers = new HashMap<>();
        }
        headers.put("Accept-Language", ServletUtils.getAcceptLanguage());
        return headers;
    }

}
