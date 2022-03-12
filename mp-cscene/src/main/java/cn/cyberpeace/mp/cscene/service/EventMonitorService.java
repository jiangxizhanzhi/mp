package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.event.ElementEventPercent;
import cn.cyberpeace.mp.cscene.dto.request.EventTrendRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private SceneIdTranslationService sceneIdTranslationService;

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private DataCollectionService dataCollectionService;


    /**
     * 按照事件占比，对场景所有节点进行排序，top5
     * @param
     * @return
     */
    public List<ElementEventPercent> topElementByEventPercent(BaseSceneElementRequest request) {
        log.info("{}", request);

        // 默认时间范围，24小时
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(24);

        BaseRequestUtil.checkBaseRequestWithSceneId(request);
        List<String> sceneIds = CollectionUtil.toList(request.getSceneId());
        List<String> translatedSceneIds = sceneIdTranslationService.translateSceneIds(sceneIds);
        List<SceneElementBO> elementBOs = scenePluginCollectionSceneService.listProbeEnabledElementBOs(translatedSceneIds.get(0));

        List<String> agentNames = elementBOs.stream().map(SceneElementBO::getEid).collect(Collectors.toList());
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setAgentNames(agentNames);
        wazuhEventRequest.setStartTime(startTime);
        wazuhEventRequest.setEndTime(endTime);

        Map<String, Long> aggsMap = dataCollectionService.aggsCountByAgentNamesBucket(wazuhEventRequest);

        Long sum = aggsMap.values().stream().mapToLong(Long::longValue).sum();

        List<ElementEventPercent> eventPercents = new ArrayList<>();
        aggsMap.entrySet().forEach(entry -> {
            String agentName = entry.getKey();
            Long count = entry.getValue();
            Optional<SceneElementBO> elementBOOptional = elementBOs.stream().filter(e -> e.getEid().equals(agentName)).findFirst();

            if (elementBOOptional.isPresent()) {
                String nodeId = elementBOOptional.get().getId();
                String nodeName = elementBOOptional.get().getName();
                BigDecimal bigDecimal = NumberUtil.round(count.doubleValue() / sum.doubleValue(), 2);
                ElementEventPercent eventPercent = new ElementEventPercent(nodeId, nodeName, bigDecimal.doubleValue());
                eventPercents.add(eventPercent);
            }
        });

        return eventPercents;
    }


    /**
     * 查询场景所有总共的采集事件趋势
     *
     * @param trendRequest
     * @return
     */
    public List<HostState> getEventTrend(EventTrendRequest trendRequest) {
        log.info("{}", trendRequest);
        BaseRequestUtil.checkBaseRequestWithSceneId(trendRequest);
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());

        return processEventTrend(sceneIds, null, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }




    private List<HostState> processEventTrend(List<String> sceneIds, List<String> elementIds, LocalDateTime startTime, LocalDateTime endTime, Integer segmentCount) {
        // 默认值
        if (Objects.isNull(endTime)) {
            endTime = LocalDateTime.now();
        }

        if (Objects.isNull(startTime)) {
            // todo 默认时间间隔24小时
            startTime = endTime.minusHours(24);
        }

        if (Objects.isNull(segmentCount)) {
            segmentCount = 10;
        }

        /*List<String> sceneIdList = sceneIdTranslationService.translateSceneIds(sceneIds);
        Long intervalSeconds = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(startTime, endTime, segmentCount);
        String intervalSecondStr = intervalSeconds + "";

        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> cpuResult = scenePluginCollectionSceneService.timeIntervalAvgHostCpuPercent(sceneIdList, elementIds, startTime, endTime, Integer.valueOf(intervalSecondStr));
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> memResult = scenePluginCollectionSceneService.timeIntervalAvgHostMemoryPercent(sceneIdList, elementIds, startTime, endTime, Integer.valueOf(intervalSecondStr));
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> diskResult = scenePluginCollectionSceneService.timeIntervalAvgHostDiskPercent(sceneIdList, elementIds, startTime, endTime, Integer.valueOf(intervalSecondStr));

        List<IntervalSizeBO<LocalDateTime, Double>> intervalCpuPercentBOs = cpuResult.getSource();
        List<IntervalSizeBO<LocalDateTime, Double>> intervalMemPercentBOs = memResult.getSource();
        List<IntervalSizeBO<LocalDateTime, Double>> intervalDiskPercentBOs = diskResult.getSource();

        List<LocalDateTime> timeList = SegmentCount2IntervalSecondsUtil.generateTimeList(startTime, endTime, intervalSeconds);
        Map<LocalDateTime, HostState> hostStateMap = new HashMap<>();
        for (LocalDateTime localDateTime : timeList) {
            HostState hostState = new HostState();
            // 初始化
            hostState.setTime(localDateTime);
            hostState.setCpuPercent(0.0);
            hostState.setMemoryPercent(0.0);
            // todo diskPercent
            hostState.setDiskPercent(0.0);
            hostStateMap.put(localDateTime, hostState);
        }

        // cpu
        if (Objects.nonNull(intervalCpuPercentBOs)) {
            for (IntervalSizeBO<LocalDateTime, Double> bo : intervalCpuPercentBOs) {
                if (Objects.nonNull(hostStateMap.get(bo.getStart()))) {
                    hostStateMap.get(bo.getStart()).setCpuPercent(Objects.nonNull(bo.getSize()) ? NumberUtil.round(bo.getSize(), 2).doubleValue() : 0.0);
                }
            }
        }

        // 内存
        if (Objects.nonNull(intervalMemPercentBOs)) {
            for (IntervalSizeBO<LocalDateTime, Double> bo : intervalMemPercentBOs) {
                if (Objects.nonNull(hostStateMap.get(bo.getStart()))) {
                    hostStateMap.get(bo.getStart()).setMemoryPercent(Objects.nonNull(bo.getSize()) ? NumberUtil.round(bo.getSize(), 2).doubleValue() : 0.0);
                    // todo 在这造磁盘模拟数据
                    hostStateMap.get(bo.getStart()).setDiskPercent(30.0);
                }
            }
        }*/

        // 磁盘
        /*if (Objects.nonNull(intervalDiskPercentBOs)) {
            for (IntervalSizeBO<LocalDateTime, Double> bo : intervalDiskPercentBOs) {
                if (Objects.nonNull(hostStateMap.get(bo.getStart()))) {
                    hostStateMap.get(bo.getStart()).setDiskPercent(Objects.nonNull(bo.getSize()) ? NumberUtil.round(bo.getSize(), 2).doubleValue() : 0.0);
                }
            }
        }*/

        List<HostState> hostStates = new ArrayList<>();
        /*hostStateMap.entrySet().forEach(entry -> {
            hostStates.add(entry.getValue());
        });
        hostStates.sort(Comparator.comparing(HostState::getTime));*/

        return hostStates;
    }




}
