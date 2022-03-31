package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.cscene.dto.common.BaseTimeRangeRequest;
import cn.cyberpeace.mp.cscene.dto.event.ElementEventPercent;
import cn.cyberpeace.mp.cscene.dto.event.EventAggr;
import cn.cyberpeace.mp.cscene.dto.event.EventTimeCount;
import cn.cyberpeace.mp.cscene.dto.request.EventPercentRequest;
import cn.cyberpeace.mp.cscene.dto.request.EventTrendRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.cscene.util.SegmentCount2IntervalSecondsUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhNetworkConnectionModel;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.wazuh.SysCollectorService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/3/11 14:57
 */
@Slf4j
@Service
public class EventMonitorService {

    @Autowired
    private SceneTranslationService sceneTranslationService;

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private SysCollectorService sysCollectorService;

    @Autowired
    private SceneElementMonitorService elementMonitorService;


    /**
     * 统计一段时间内的事件总数
     * @param request
     * @return
     */
    public Long sumEvent(BaseTimeRangeRequest request) {
        log.info("{}", request);
        BaseRequestUtil.checkBaseRequestWithSceneId(request);
        BaseRequestUtil.processDefaultTimeRange(request);
        List<String> agentNames = sceneTranslationService.getAgentNamesBySceneIdAndElementIds(request.getSceneId(), request.getElementIds());

        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setAgentNames(agentNames);
        wazuhEventRequest.setStartTime(request.getStartTime());
        wazuhEventRequest.setEndTime(request.getEndTime());
        // es中的事件总数，暂不包括进程，端口等system_inventory中的数据
        long sumFromES = dataCollectionService.countByAgentNames(wazuhEventRequest);
        return sumFromES;
    }

    /**
     * 按照事件占比，对场景所有节点进行排序，top5
     * @param
     * @return
     */
    public List<ElementEventPercent> topElementByEventPercent(EventPercentRequest request) {
        log.info("{}", request);

        BaseRequestUtil.checkBaseRequestWithSceneId(request);
        BaseRequestUtil.processDefaultTimeRange(request);

        List<String> sceneIds = CollectionUtil.toList(request.getSceneId());
        List<String> translatedSceneIds = sceneTranslationService.translateSceneIds(sceneIds);
        List<SceneElementBO> elementBOs = scenePluginCollectionSceneService.listProbeEnabledElementBOs(translatedSceneIds.get(0));

        List<String> agentNames = elementBOs.stream().map(SceneElementBO::getEid).collect(Collectors.toList());

        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setAgentNames(agentNames);
        wazuhEventRequest.setStartTime(request.getStartTime());
        wazuhEventRequest.setEndTime(request.getEndTime());
        // TODO 处理事件类型
//        wazuhEventRequest.setCatagories(null);

        Map<String, Long> aggsMap = new LinkedHashMap<>();
        int connsCnt = 0;
        if (Objects.nonNull(request.getType()) && request.getType().equals(1)) {
            // 进程
            log.info("aggregate process");
            aggsMap = aggProcessCount(agentNames);
        } else if (Objects.nonNull(request.getType()) && request.getType().equals(2)) {
            // 端口
            log.info("aggregate ports");
            aggsMap = aggPortCount(agentNames);
        } else if (Objects.nonNull(request.getType()) && request.getType().equals(3)) {
            // 网络连接
            log.info("aggregate network connections");
            aggsMap = aggNetConnsCount(agentNames);
        } else {
            // todo 其他，包括类型：所有，文件，用户
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


    /**
     * 根据事件类型，统计百分比
     * @param request
     * @return
     */
    public EventAggr aggrByEventType(BaseTimeRangeRequest request) {
        Long sumFromEs = sumEvent(request);

        List<String> agentNames = sceneTranslationService.getAgentNamesBySceneIdAndElementIds(request.getSceneId(), request.getElementIds());

        Map<String, Long> aggsProcessesMap = aggProcessCount(agentNames);
        Map<String, Long> aggsPortsMap = aggPortCount(agentNames);
        Map<String, Long> aggsNetConnsMap = aggNetConnsCount(agentNames);

        Long processSum = aggsProcessesMap.values().stream().mapToLong(Long::longValue).sum();
        Long portSum = aggsPortsMap.values().stream().mapToLong(Long::longValue).sum();
        Long netConnsSum = aggsNetConnsMap.values().stream().mapToLong(Long::longValue).sum();

        Long sum = sumFromEs + processSum + portSum + netConnsSum;

        EventAggr eventAggr = new EventAggr();
        eventAggr.setProcess(toRoundPercent(processSum, sum));
        // todo 文件和用户类型事件区分
        eventAggr.setFile(22.62);
        eventAggr.setPort(toRoundPercent(portSum, sum));
        eventAggr.setNetwork(toRoundPercent(netConnsSum, sum));
        eventAggr.setUser(16.67);

        return eventAggr;
    }


    /**
     * 查询场景所有总共的采集事件趋势
     *
     * @param trendRequest
     * @return
     */
    public List<EventTimeCount> getEventTrend(EventTrendRequest trendRequest) {
        log.info("{}", trendRequest);
        BaseRequestUtil.checkBaseRequestWithSceneId(trendRequest);
        BaseRequestUtil.processDefaultTimeRange(trendRequest);
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());

        return processEventTrend(sceneIds, null, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }




    private List<EventTimeCount> processEventTrend(List<String> sceneIds, List<String> elementIds, LocalDateTime startTime, LocalDateTime endTime, Integer segmentCount) {
        // 默认值
        if (Objects.isNull(segmentCount)) {
            segmentCount = 10;
        }

//        List<String> translatedSceneIds = sceneTranslationService.translateSceneIds(sceneIds);
//        List<SceneElementBO> elementBOs = scenePluginCollectionSceneService.listProbeEnabledElementBOs(translatedSceneIds.get(0));
//
//        List<String> agentNames = elementBOs.stream().map(SceneElementBO::getEid).collect(Collectors.toList());
        List<String> agentNames = sceneTranslationService.getAgentNamesBySceneIdAndElementIds(sceneIds.get(0), elementIds);

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


    /**
     * 查询各个节点端口总数
     * @return
     */
    private Map<String, Long> aggPortCount(List<String> agentNames) {
        Map<String, Long> map = new LinkedHashMap<>();
        agentNames.forEach(a -> map.put(a, 0L));

        agentNames.forEach(agentName -> {
            CollectorBaseRequest collectorBaseRequest = new CollectorBaseRequest();
            collectorBaseRequest.setAgentName(agentName);
            PageInfo pageInfo = sysCollectorService.pagePorts(collectorBaseRequest);
            Long total = pageInfo.getTotal();
            map.put(agentName, total);
        });
        return map;
    }


    /**
     * 查询各个节点进程总数
     * @param agentName
     * @return
     */
    /**
     * 查询各个节点端口总数
     * @return
     */
    private Map<String, Long> aggProcessCount(List<String> agentNames) {
        Map<String, Long> map = new LinkedHashMap<>();
        agentNames.forEach(a -> map.put(a, 0L));

        agentNames.forEach(agentName -> {
            CollectorBaseRequest collectorBaseRequest = new CollectorBaseRequest();
            collectorBaseRequest.setAgentName(agentName);
            PageInfo pageInfo = sysCollectorService.pageProcesses(collectorBaseRequest);
            Long total = pageInfo.getTotal();
            map.put(agentName, total);
        });
        return map;
    }



    private Map<String, Long> aggNetConnsCount(List<String> agentNames) {
        Map<String, Long> map = new LinkedHashMap<>();
        agentNames.forEach(a -> map.put(a, 0L));

        agentNames.forEach(agentName -> {

            if (StringUtils.isNotBlank(agentName)) {
                WazuhMonitorEventsRequest wazuhRequest = new WazuhMonitorEventsRequest();
                wazuhRequest.setAgentNames(CollectionUtil.toList(agentName));
                wazuhRequest.setPageSize(1);
                // 默认24小时
                LocalDateTime endTime = LocalDateTime.now();
                wazuhRequest.setStartTime(endTime.minusHours(24));
                wazuhRequest.setEndTime(endTime);

                List<WazuhNetworkConnectionModel> totalList = dataCollectionService.queryNetConnEventsWithTimeRange(wazuhRequest);
                map.put(agentName, Long.valueOf(totalList.size()));
            }

        });
        return map;
    }


    /**
     * 计算百分比
     * @param value
     * @param sum
     * @return
     */
    private double toRoundPercent(Long value, Long sum) {
        BigDecimal bigDecimal = BigDecimal.valueOf(0.0);
        if (sum > 0) {
            bigDecimal = NumberUtil.round(value.doubleValue() * 100 / sum.doubleValue(), 2);
        }
        return bigDecimal.doubleValue();
    }



}
