package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.event.ElementEventPercent;
import cn.cyberpeace.mp.cscene.dto.event.EventTimeCount;
import cn.cyberpeace.mp.cscene.dto.request.EventTrendRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.cscene.util.SegmentCount2IntervalSecondsUtil;
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

        log.info("{}", eventPercents);
        return eventPercents;
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
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());

        return processEventTrend(sceneIds, null, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }




    private List<EventTimeCount> processEventTrend(List<String> sceneIds, List<String> elementIds, LocalDateTime startTime, LocalDateTime endTime, Integer segmentCount) {
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

        List<String> translatedSceneIds = sceneIdTranslationService.translateSceneIds(sceneIds);
        List<SceneElementBO> elementBOs = scenePluginCollectionSceneService.listProbeEnabledElementBOs(translatedSceneIds.get(0));

        List<String> agentNames = elementBOs.stream().map(SceneElementBO::getEid).collect(Collectors.toList());
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




}
