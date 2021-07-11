package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.cscene.request.common.BaseTimeRangeRequest;
import cn.cyberpeace.mp.cscene.dto.event.ElementEventPercent;
import cn.cyberpeace.mp.cscene.dto.event.EventAggr;
import cn.cyberpeace.mp.cscene.dto.event.EventTimeCount;
import cn.cyberpeace.mp.cscene.request.event.EventPercentRequest;
import cn.cyberpeace.mp.cscene.request.event.EventTrendRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.cscene.util.SegmentCount2IntervalSecondsUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.wazuh.SysCollectorService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class EventMonitorService {

    @Autowired
    private CsceneCommonService csceneCommonService;

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private SysCollectorService sysCollectorService;

    @Autowired
    private SceneElementMonitorService elementMonitorService;

    @Value("${mp.cscene.default.hours-range:24}")
    private Integer hoursRange;



    
    public Long sumEvent(BaseTimeRangeRequest request) {
        BaseRequestUtil.checkBaseRequestWithSceneId(request);
        BaseRequestUtil.processDefaultTimeRange(request);
        List<String> agentNames = csceneCommonService.getAgentNamesBySceneIdAndElementIds(request.getSceneId(), request.getElementIds());

        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setAgentNames(agentNames);
        wazuhEventRequest.setStartTime(request.getStartTime());
        wazuhEventRequest.setEndTime(request.getEndTime());
        
        long sumFromES = dataCollectionService.countByAgentNames(wazuhEventRequest);
        return sumFromES;
    }

    
    public List<ElementEventPercent> topElementByEventPercent(EventPercentRequest request) {

        BaseRequestUtil.checkBaseRequestWithSceneId(request);
        BaseRequestUtil.processDefaultTimeRange(request);

        List<String> sceneIds = CollectionUtil.toList(request.getSceneId());
        List<String> translatedSceneIds = csceneCommonService.translateSceneIds(sceneIds);
        List<SceneElementBO> elementBOs = scenePluginCollectionSceneService.listProbeEnabledElementBOs(translatedSceneIds.get(0)).getSource();

        List<String> agentNames = elementBOs.stream().map(SceneElementBO::getEid).collect(Collectors.toList());

        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setAgentNames(agentNames);
        wazuhEventRequest.setStartTime(request.getStartTime());
        wazuhEventRequest.setEndTime(request.getEndTime());
        


        Map<String, Long> aggsMap = new LinkedHashMap<>();
        int connsCnt = 0;
        if (Objects.nonNull(request.getType()) && request.getType().equals(1)) {
            
            log.info("aggregate process");
            aggsMap = aggProcessCount(agentNames);
        } else if (Objects.nonNull(request.getType()) && request.getType().equals(2)) {
            
            log.info("aggregate ports");
            aggsMap = aggPortCount(agentNames);
        } else if (Objects.nonNull(request.getType()) && request.getType().equals(3)) {
            
            log.info("aggregate network connections");
            aggsMap = aggNetConnsCount(agentNames);
        } else if (Objects.nonNull(request.getType()) && request.getType().equals(5)) {
            
            log.info("aggregate system user change count");
            aggsMap = aggUserChangeCount(agentNames);
        } else {
            
            aggsMap = dataCollectionService.aggsCountByAgentNamesBucket(wazuhEventRequest);
        }

        log.info("aggsMap:{}", aggsMap);

        List<ElementEventPercent> eventPercents = new ArrayList<>();
        List<ElementEventPercent> finalEventPercents = eventPercents;
        Long sum = aggsMap.values().stream().mapToLong(Long::longValue).sum();
        Long finalSum = sum + connsCnt;;
        aggsMap.forEach((agentName, count) -> {
            Optional<SceneElementBO> elementBOOptional = elementBOs.stream().filter(e -> e.getEid().equals(agentName)).findFirst();

            if (elementBOOptional.isPresent()) {
                String nodeId = elementBOOptional.get().getId();
                String nodeName = elementBOOptional.get().getName();

                Double percent = toRoundPercent(count, finalSum);

                ElementEventPercent eventPercent = new ElementEventPercent(nodeId, nodeName, percent, count, request.getType());
                finalEventPercents.add(eventPercent);
            }
        });

        log.info("before {}", eventPercents);
        eventPercents = eventPercents.stream().sorted(Comparator.comparing(ElementEventPercent::getPercent).reversed()).collect(Collectors.toList());
        log.info("{}", eventPercents);
        return eventPercents;
    }


    
    public EventAggr aggrByEventType(BaseTimeRangeRequest request) {
        Long sumFromEs = sumEvent(request);

        List<String> agentNames = csceneCommonService.getAgentNamesBySceneIdAndElementIds(request.getSceneId(), request.getElementIds());
        EventAggr eventAggr = new EventAggr();
        if (CollectionUtil.isEmpty(agentNames)) {
            return eventAggr;
        }

        Map<String, Long> aggsProcessesMap = aggProcessCount(agentNames);
        Map<String, Long> aggsPortsMap = aggPortCount(agentNames);
        Map<String, Long> aggsNetConnsMap = aggNetConnsCount(agentNames);
        Map<String, Long> aggsUserChangeMap = aggUserChangeCount(agentNames);

        Long processSum = aggsProcessesMap.values().stream().mapToLong(Long::longValue).sum();
        Long portSum = aggsPortsMap.values().stream().mapToLong(Long::longValue).sum();
        Long netConnsSum = aggsNetConnsMap.values().stream().mapToLong(Long::longValue).sum();
        Long netSysUserSum = aggsUserChangeMap.values().stream().mapToLong(Long::longValue).sum();

        Long sum = sumFromEs + processSum + portSum + netConnsSum;

        eventAggr.setProcess(toRoundPercent(processSum, sum));
        eventAggr.setPort(toRoundPercent(portSum, sum));
        eventAggr.setNetwork(toRoundPercent(netConnsSum, sum));

        
        eventAggr.setFile(toRoundPercent(sumFromEs, sum));
        eventAggr.setUser(toRoundPercent(netSysUserSum, sum));

        return eventAggr;
    }


    
    public List<EventTimeCount> getEventTrend(EventTrendRequest trendRequest) {
        log.info("{}", trendRequest);
        BaseRequestUtil.checkBaseRequestWithSceneId(trendRequest);
        BaseRequestUtil.processDefaultTimeRange(trendRequest);
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());
        if (CollectionUtils.isNotEmpty(trendRequest.getElementIds())) {
            return processEventTrend(sceneIds, trendRequest.getElementIds(), trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
        }
        return processEventTrend(sceneIds, null, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }




    private List<EventTimeCount> processEventTrend(List<String> sceneIds, List<String> elementIds, LocalDateTime startTime, LocalDateTime endTime, Integer segmentCount) {
        
        if (Objects.isNull(segmentCount)) {
            segmentCount = 10;
        }





        List<String> agentNames = csceneCommonService.getAgentNamesBySceneIdAndElementIds(sceneIds.get(0), elementIds);

        Long intervalSeconds = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(startTime, endTime, segmentCount);

        List<EventTimeCount> eventTimeCounts = new ArrayList<>();
        for (int i = 0; i < segmentCount; i++) {
            LocalDateTime queryEndTime = endTime.minusSeconds(intervalSeconds * i);
            LocalDateTime queryStartTime = endTime.minusSeconds(intervalSeconds * (i + 1));

            WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
            wazuhEventRequest.setAgentNames(agentNames);
            wazuhEventRequest.setStartTime(queryStartTime);
            wazuhEventRequest.setEndTime(queryEndTime);
            long sum = dataCollectionService.countByAgentNames(wazuhEventRequest);
            EventTimeCount timeCount = new EventTimeCount(queryEndTime, sum);
            eventTimeCounts.add(timeCount);
        }

        List<EventTimeCount> reversedList = CollectionUtil.reverse(eventTimeCounts);
        log.info("{}", reversedList);
        return reversedList;
    }


    
    private Map<String, Long> aggProcessCount(List<String> agentNames) {
        Map<String, Long> map = new LinkedHashMap<>();
        agentNames.forEach(a -> map.put(a, 0L));

        agentNames.forEach(agentName -> {
            WazuhMonitorEventsRequest wazuhRequest = buildWazuhMonitorEventsRequest(agentName);
            int sum = dataCollectionService.sumProcessesChangedCountByTimeRange(wazuhRequest);
            map.put(agentName, Long.valueOf(sum));
        });
        return map;
    }



    
    private Map<String, Long> aggPortCount(List<String> agentNames) {
        Map<String, Long> map = new LinkedHashMap<>();
        agentNames.forEach(a -> map.put(a, 0L));

        agentNames.forEach(agentName -> {
            if (StringUtils.isNotBlank(agentName)) {
                WazuhMonitorEventsRequest wazuhRequest = buildWazuhMonitorEventsRequest(agentName);
                int sum = dataCollectionService.sumNetConnsChangedCountByTimeRange(wazuhRequest);
                map.put(agentName, Long.valueOf(sum));
            }
        });
        return map;
    }

    
    private Map<String, Long> aggNetConnsCount(List<String> agentNames) {
        Map<String, Long> map = new LinkedHashMap<>();
        agentNames.forEach(a -> map.put(a, 0L));

        agentNames.forEach(agentName -> {
            if (StringUtils.isNotBlank(agentName)) {
                WazuhMonitorEventsRequest wazuhRequest = buildWazuhMonitorEventsRequest(agentName);
                int sum = dataCollectionService.sumNetConnsChangedCountByTimeRange(wazuhRequest);
                map.put(agentName, Long.valueOf(sum));
            }
        });
        return map;
    }

    
    private Map<String, Long> aggUserChangeCount(List<String> agentNames) {
        Map<String, Long> map = new LinkedHashMap<>();
        agentNames.forEach(a -> map.put(a, 0L));

        agentNames.forEach(agentName -> {
            WazuhMonitorEventsRequest wazuhRequest = buildWazuhMonitorEventsRequest(agentName);
            int sum = dataCollectionService.sumSysUsersChangedCountByTimeRange(wazuhRequest);
            map.put(agentName, Long.valueOf(sum));
        });
        return map;
    }

    private WazuhMonitorEventsRequest buildWazuhMonitorEventsRequest(String agentName) {
        WazuhMonitorEventsRequest wazuhRequest = new WazuhMonitorEventsRequest();
        wazuhRequest.setAgentNames(CollectionUtil.toList(agentName));
        wazuhRequest.setPageSize(1);
        
        LocalDateTime endTime = LocalDateTime.now();
        wazuhRequest.setStartTime(endTime.minusHours(hoursRange));
        wazuhRequest.setEndTime(endTime);
        return wazuhRequest;
    }


    
    private double toRoundPercent(Long value, Long sum) {
        BigDecimal bigDecimal = BigDecimal.valueOf(0.0);
        if (sum > 0) {
            bigDecimal = NumberUtil.round(value.doubleValue() * 100 / sum.doubleValue(), 2);
        }
        return bigDecimal.doubleValue();
    }



}
