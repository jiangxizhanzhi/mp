package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.bo.IntervalSizeBO;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneCollectionFlowBO;
import cn.cyberpeace.mp.cscene.dto.common.AttackCountRequest;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackDto;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackRatio;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackTime;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackTypeCount;
import cn.cyberpeace.mp.cscene.dto.flow.FlowRiskCount;
import cn.cyberpeace.mp.cscene.dto.flow.FlowTrend;
import cn.cyberpeace.mp.cscene.dto.flow.FlowTrendRequest;
import cn.cyberpeace.mp.cscene.dto.flow.TotalFlowAttackRatio;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.cscene.util.SegmentCount2IntervalSecondsUtil;
import cn.cyberpeace.mp.sceneplugincollection.bo.AttackInfo;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowGroupCountParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/30 13:41
 */
@Slf4j
@Service
public class FlowMonitorService {

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private SceneIdTranslationService sceneIdTranslationService;


    /**
     * 查询单个节点机器流量趋势
     * @param trendRequest
     * @return
     */
    public List<FlowTrend> getElementFlowTrend(FlowTrendRequest trendRequest) {
        log.info("{}", trendRequest);
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(trendRequest);
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());
        List<String> nodeIds = CollectionUtil.toList(trendRequest.getElementIds().get(0));

        return processFlowTrend(sceneIds, nodeIds, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }


    /**
     * 查询场景机器流量趋势
     * @param trendRequest
     * @return
     */
    public List<FlowTrend> getSceneFlowTrend(FlowTrendRequest trendRequest) {
        log.info("{}", trendRequest);
        BaseRequestUtil.checkBaseRequestWithSceneId(trendRequest);
        // 逗号分割
        List<String> sceneIds = CollectionUtil.toList(StringUtils.split(trendRequest.getSceneId(), ","));

        return processFlowTrend(sceneIds, null, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }


    public FlowRiskCount getFlowRiskCount(BaseSceneElementRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);

        BaseRequestUtil.checkBaseRequestWithSceneId(baseRequest);

        List<String> sceneIds = CollectionUtil.toList(baseRequest.getSceneId());

        List<String> nodeIds = null;
        if (!CollectionUtil.isEmpty(baseRequest.getElementIds())) {
            nodeIds = CollectionUtil.toList(baseRequest.getElementIds().get(0));
        }

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMinutes(10);

        // todo 转好
        List<String> sceneIdList = sceneIdTranslationService.translateSceneIds(sceneIds);

        ServiceResult<Map<String, Integer>> mapResult = scenePluginCollectionSceneService.groupCountFlowByLevel(sceneIdList, nodeIds, startTime, endTime);
        Map<String, Integer> map = mapResult.getSource();

        // 风险等级 1,2,3,4 数字越低，级别越高
        FlowRiskCount riskCount = new FlowRiskCount();
        if (Objects.nonNull(map)) {
            riskCount.setHighCount(map.get("1") == null ? 0 : map.get("1"));
            riskCount.setMediumCount(map.get("2") == null ? 0 : map.get("2"));
            Integer low1 = map.get("3") == null ? 0 : map.get("3");
            Integer low2 = map.get("4") == null ? 0 : map.get("4");
            riskCount.setLowCount(low1 + low2);
        }
        return riskCount;
    }

    public List<FlowAttackTypeCount> getAttackTypeCount(BaseSceneElementRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);

        BaseRequestUtil.checkBaseRequestWithSceneId(baseRequest);

        List<String> sceneIds = Arrays.asList(StringUtils.split(baseRequest.getSceneId(), ","));

        List<String> nodeIds = null;
        if (!CollectionUtil.isEmpty(baseRequest.getElementIds())) {
            nodeIds = CollectionUtil.toList(baseRequest.getElementIds().toArray(new String[]{}));
        }

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMinutes(10);

        List<String> sceneIdList = sceneIdTranslationService.translateSceneIds(sceneIds);

        ServiceResult<Map<String, Integer>> mapResult = scenePluginCollectionSceneService.groupCountFlowByType(sceneIdList, nodeIds, startTime, endTime);
        Map<String, Integer> map = mapResult.getSource();
        List<FlowAttackTypeCount> result = new ArrayList<>();
        if (map != null && !map.isEmpty()) {
            map.forEach((type, num) -> result.add(new FlowAttackTypeCount(type, num)));
            Collections.sort(result, (c1, c2) -> c2.getTimes() - c1.getTimes());
        }
        return result;
    }

    public List<FlowAttackTime> getAttackTimeCountNoName(AttackCountRequest baseRequest) {
        log.info("baseRequest:{}", JsonCommon.stringify(baseRequest));
        BaseRequestUtil.checkBaseRequestWithSceneId(baseRequest);

        List<String> sceneIds = Arrays.asList(StringUtils.split(baseRequest.getSceneId(), ","));
        List<String> sceneIdList = sceneIdTranslationService.translateSceneIds(sceneIds);
        var mapResult = scenePluginCollectionSceneService.groupCountFlowWithTopSize(sceneIdList, baseRequest.getIps(), null, null);
        Map<String, Integer> map = mapResult.getSource();
        List<FlowAttackTime> result = new ArrayList<>();
        if (map != null && !map.isEmpty()) {
            map.forEach((type, num) -> result.add(FlowAttackTime.builder()
                    .ip(type)
                    .times(num)
                    .build()));
            Collections.sort(result, (c1, c2) -> c2.getTimes() - c1.getTimes());
        }
        return result;
    }

    public List<FlowAttackTime> getAttackIpCount(AttackCountRequest baseRequest) {
        log.info("baseRequest:{}", JsonCommon.stringify(baseRequest));
        BaseRequestUtil.checkBaseRequestWithSceneId(baseRequest);

        List<String> sceneIds = Arrays.asList(StringUtils.split(baseRequest.getSceneId(), ","));
        List<String> sceneIdList = sceneIdTranslationService.translateSceneIds(sceneIds);
        String groupField = baseRequest.getIpType() == 0 ?
                ScenePluginCollectionSceneService.GroupCountFlowField.SOURCE_IP :
                ScenePluginCollectionSceneService.GroupCountFlowField.DEST_IP;
        FlowGroupCountParams params = new FlowGroupCountParams(groupField);
        params.setQueryAddress(baseRequest.getIps());
        params.setStartTime(baseRequest.getStartTime());
        params.setEndTime(baseRequest.getEndTime());
        params.setTopSize(baseRequest.getTopSize());
        var result = scenePluginCollectionSceneService.groupCountFlowAndNodeInfo(sceneIdList, params).getSource();
        return result.stream()
                .map(bo -> FlowAttackTime.builder()
                        .times(bo.getTimes())
                        .nodeName(bo.getNodeName())
                        .ip(bo.getIp())
                        .build()
                ).collect(Collectors.toList());
    }

    public TotalFlowAttackRatio getAttackRatioCount(AttackCountRequest baseRequest) {
        log.info("baseRequest:{}", JsonCommon.stringify(baseRequest));
        BaseRequestUtil.checkBaseRequestWithSceneId(baseRequest);

        List<String> sceneIds = Arrays.asList(StringUtils.split(baseRequest.getSceneId(), ","));
        List<String> sceneIdList = sceneIdTranslationService.translateSceneIds(sceneIds);
        var countResult = scenePluginCollectionSceneService.groupCountFlow(sceneIdList, baseRequest.getIps(), baseRequest.getTopSize(), null, null).getSource();
        TotalFlowAttackRatio totalFlowAttackRatio = new TotalFlowAttackRatio();
        totalFlowAttackRatio.setTotalAttackNumber(countResult.getAttackNumber());
        totalFlowAttackRatio.setTotalTrafficSize(countResult.getTrafficSize());
        totalFlowAttackRatio.setAttackWayList(new ArrayList<>());
        if (CollectionUtils.isEmpty(countResult.getAttackWayList())) {
            countResult.getAttackWayList().stream()
                    .forEach(v -> totalFlowAttackRatio.getAttackWayList()
                            .add(FlowAttackRatio.builder()
                                    .attackTrafficPercent(v.getAttackTrafficPercent().doubleValue())
                                    .type(v.getAggregationType())
                                    .build())
                    );
        }
        return totalFlowAttackRatio;
    }


    /**
     * 流量分页接口包装
     * 根据攻击类型拆分
     * @param id
     * @param queryParams
     * @return
     */
    public ListObjServiceResult<FlowAttackDto> pageFlow(String id, FlowQueryParams queryParams) {
        // todo 分页处理方式
        if (Objects.isNull(queryParams.getPage())) {
            queryParams.setPage(1);
        }
        if (Objects.isNull(queryParams.getPerPage())) {
            queryParams.setPerPage(1000);
        }
        queryParams.ensure();

        ListObjServiceResult<CsceneCollectionFlowBO> flowBOResult = csceneInstanceService.listFlows(id, queryParams);
        List<CsceneCollectionFlowBO> flowBOS = flowBOResult.getSource().getObjs();

        List<FlowAttackDto> flowAttackDtos = new ArrayList<>();

        for (CsceneCollectionFlowBO flowBO : flowBOS) {
            if (Objects.nonNull(flowBO.getAttackInfos())) {
                for (AttackInfo attackInfo : flowBO.getAttackInfos()) {
                    FlowAttackDto flowAttackDto = new FlowAttackDto();
                    BeanUtils.copyProperties(flowBO, flowAttackDto);
                    flowAttackDto.setAttackInfo(attackInfo);
                    flowAttackDtos.add(flowAttackDto);
                }
            } else {
                // 没有攻击类型标签，全部设置为默认的低风险
                FlowAttackDto flowAttackDto = new FlowAttackDto();
                BeanUtils.copyProperties(flowBO, flowAttackDto);
                flowAttackDto.setAttackInfo(new AttackInfo(4, "Abnormal links"));
                flowAttackDtos.add(flowAttackDto);
            }
        }

        List<FlowAttackDto> finalList = flowAttackDtos;
        if (flowAttackDtos.size() > queryParams.getPageSize()) {
            finalList = flowAttackDtos.subList(0, queryParams.getPageSize());
        }

        return new ListObjServiceResult(new ListObjResult(finalList, finalList.size()).asResult()).asResult();
    }


    private List<FlowTrend> processFlowTrend(List<String> sceneIds, List<String> elementIds, LocalDateTime startTime, LocalDateTime endTime, Integer segmentCount) {
        // 默认值
        if (Objects.isNull(endTime)) {
            endTime = LocalDateTime.now();
        }

        if (Objects.isNull(startTime)) {
            // todo 默认时间间隔48小时
            startTime = endTime.minusHours(24);
        }

        if (Objects.isNull(segmentCount)) {
            segmentCount = 10;
        }

        // todo 转好
        List<String> sceneIdList = sceneIdTranslationService.translateSceneIds(sceneIds);
        Long intervalSeconds = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(startTime, endTime, segmentCount);
        String intervalSecondStr = intervalSeconds + "";

        ServiceResult<List<IntervalSizeBO<LocalDateTime, Long>>> flowResult = scenePluginCollectionSceneService.timeIntervalSumFlowSize(sceneIdList, elementIds, startTime, endTime, Integer.valueOf(intervalSecondStr));

        List<IntervalSizeBO<LocalDateTime, Long>> intervalFlowSizeBOs = flowResult.getSource();
        List<FlowTrend> flowTrends = new ArrayList<>();
        if (Objects.isNull(intervalFlowSizeBOs)) {
            return flowTrends;
        }

        List<LocalDateTime> timeList = SegmentCount2IntervalSecondsUtil.generateTimeList(startTime, endTime, intervalSeconds);
        Map<LocalDateTime, FlowTrend> flowTrendMap = new HashMap<>();
        for (LocalDateTime time : timeList) {
            FlowTrend flowTrend = new FlowTrend();
            // 初始化
            flowTrend.setTime(time);
            flowTrend.setDataSize(0L);
            flowTrendMap.put(time, flowTrend);
        }

        // flow
        for (IntervalSizeBO<LocalDateTime, Long> bo : intervalFlowSizeBOs) {
            if (Objects.nonNull(bo.getStart())) {
                FlowTrend flowTrend = getClosest(bo.getStart(), flowTrendMap);
                if (Objects.nonNull(flowTrend)) {
                    flowTrend.setDataSize(bo.getSize());
                }
            }
        }

        flowTrendMap.entrySet().forEach(entry -> {
            flowTrends.add(entry.getValue());
        });
        flowTrends.sort(Comparator.comparing(FlowTrend::getTime));

        return flowTrends;
    }


    private FlowTrend getClosest(LocalDateTime time, Map<LocalDateTime, FlowTrend> flowTrendMap) {
        LocalDateTime closestTime = null;
        long durationSeconds = Long.MAX_VALUE;
        for (Map.Entry<LocalDateTime, FlowTrend> entry : flowTrendMap.entrySet()) {
            Duration duration = Duration.between(entry.getKey(), time);
            long duraSecs = Math.abs(duration.toSeconds());
            if (durationSeconds > duraSecs) {
                durationSeconds = duraSecs;
                closestTime = entry.getKey();
            }
        }
        log.info("time:{}, closestTime:{}", time, closestTime);
        return flowTrendMap.get(closestTime);
    }

}
