package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.bo.IntervalSizeBO;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneCollectionFlowBO;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.controller.request.SceneAllElementsQueryRequest;
import cn.cyberpeace.mp.cscene.dto.element.ConnectionState;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAnalysisSummary;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackDto;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackRatio;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackTime;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackTypeCount;
import cn.cyberpeace.mp.cscene.dto.flow.FlowRiskCount;
import cn.cyberpeace.mp.cscene.dto.flow.FlowTrend;
import cn.cyberpeace.mp.cscene.dto.flow.TotalFlowAttackRatio;
import cn.cyberpeace.mp.cscene.request.common.AttackCountRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseTimeRangeRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.flow.FlowCommonRequest;
import cn.cyberpeace.mp.cscene.request.flow.FlowSceneSimulationRequest;
import cn.cyberpeace.mp.cscene.request.flow.FlowTrendRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.cscene.util.SegmentCount2IntervalSecondsUtil;
import cn.cyberpeace.mp.cscene.vo.FlowGroupByDestinationIpAndSeverityVO;
import cn.cyberpeace.mp.cscene.vo.FlowGroupPortWithProtocolVO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincollection.bo.AttackInfo;
import cn.cyberpeace.mp.sceneplugincollection.bo.FlowGroupByDestinationIpAndSeverityBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.FlowGroupPortWithProtocolBO;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowGroupByDestinationPortParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowGroupCountParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowSimulationParams;
import cn.cyberpeace.mp.sceneplugincollection.util.FlowPcapExportUtil;
import cn.cyberpeace.mp.situationaware.model.enums.SeverityEnum;
import cn.cyberpeace.mp.situationaware.request.FlowAnalysisRequest;
import cn.cyberpeace.mp.situationaware.service.FlowAnalysisService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.URLUtil;
import com.google.common.collect.Lists;
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


@Slf4j
@Service
public class FlowMonitorService {

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private CsceneCommonService csceneCommonService;

    @Autowired
    private FlowAnalysisService flowAnalysisService;

    @Autowired
    private SceneMonitorService sceneMonitorService;

    @Autowired
    private SceneElementMonitorService elementMonitorService;

    @Autowired
    private CsceneFileService csceneFileService;


    
    public List<FlowTrend> getElementFlowTrend(FlowTrendRequest trendRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(trendRequest);
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());
        List<String> nodeIds = CollectionUtil.toList(trendRequest.getElementIds().get(0));

        return processFlowTrend(sceneIds, nodeIds, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount(), null, trendRequest.getType());
    }


    
    public List<FlowTrend> getSceneFlowTrend(FlowTrendRequest trendRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneId(trendRequest);
        
        List<String> sceneIds = CollectionUtil.toList(StringUtils.split(trendRequest.getSceneId(), ","));

        return processFlowTrend(sceneIds, null, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount(), trendRequest.getSourceIps(), trendRequest.getType());
    }

    
    public ServiceResult simulation(FlowSceneSimulationRequest request) {
        
        List<String> sceneIds = CollectionUtil.toList(StringUtils.split(request.getSceneId(), ","));
        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);

        FlowSimulationParams params = new FlowSimulationParams();
        params.setSceneIds(sceneIdList);
        params.setSourceIp(request.getSourceIp());
        params.setSourceMacs(request.getSourceMacs());
        params.setDestinationIp(request.getDestinationIp());
        params.setDestinationMacs(request.getDestinationMacs());
        params.setNodeIds(request.getNodeIds());
        params.setNetworkBytes(request.getNetworkBytes());
        params.setDateTime(request.getDateTime());
        params.setShuffleCategory(request.getShuffleCategory());
        scenePluginCollectionSceneService.simulation(params);

        return ResultCommon.success();
    }

    
    public List<FlowGroupPortWithProtocolVO> groupByDestinationPortWithProtocol(FlowCommonRequest request) {
        BaseRequestUtil.checkBaseRequestWithSceneId(request);
        
        List<String> sceneIds = CollectionUtil.toList(StringUtils.split(request.getSceneId(), ","));
        LocalDateTime endTime = request.getEndTime();
        LocalDateTime startTime = request.getStartTime();

        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);

        FlowGroupByDestinationPortParams params = new FlowGroupByDestinationPortParams();
        params.setStartTime(startTime);
        params.setEndTime(endTime);
        params.setSourceIps(request.getSourceIps());
        params.setSceneIds(sceneIdList);
        params.setTopSize(request.getTopSize());

        ServiceResult<List<FlowGroupPortWithProtocolBO>> serviceResult = scenePluginCollectionSceneService.groupByDestinationPortWithProtocol(params);

        List<FlowGroupPortWithProtocolBO> boList = serviceResult.getSource();

        if (CollectionUtils.isEmpty(boList)) {
            return Lists.newArrayList();
        }
        return boList.stream().map(v -> {
            FlowGroupPortWithProtocolVO vo = new FlowGroupPortWithProtocolVO();
            vo.setProtocol(v.getProtocol());
            vo.setPort(v.getPort());
            vo.setAttackNumber(v.getAttackNumber());
            vo.setTrafficSize(v.getTrafficSize());
            return vo;
        }).collect(Collectors.toList());
    }


    
    public List<FlowGroupByDestinationIpAndSeverityVO> groupByDestinationIpAndSeverity(FlowCommonRequest request) {
        BaseRequestUtil.checkBaseRequestWithSceneId(request);
        
        List<String> sceneIds = CollectionUtil.toList(StringUtils.split(request.getSceneId(), ","));

        LocalDateTime endTime = request.getEndTime();

        LocalDateTime startTime = request.getStartTime();

        
        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);

        FlowGroupByDestinationPortParams params = new FlowGroupByDestinationPortParams();
        params.setStartTime(startTime);
        params.setEndTime(endTime);
        params.setSourceIps(request.getSourceIps());
        params.setSceneIds(sceneIdList);
        params.setTopSize(request.getTopSize());

        ServiceResult<List<FlowGroupByDestinationIpAndSeverityBO>> serviceResult = scenePluginCollectionSceneService.groupByDestinationIpAndSeverity(params);

        List<FlowGroupByDestinationIpAndSeverityBO> boList = serviceResult.getSource();

        if (CollectionUtils.isEmpty(boList)) {
            boList = Lists.newArrayList();
        }

        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIdList, request.getElementIds());
        
        List<FlowGroupByDestinationIpAndSeverityVO> voList = this.convertSeverityList(boList, batchSceneHandler);

        int allNodeSize = batchSceneHandler.getAllEid2ElementMap().keySet().size();
        int severitySize = voList.stream().map(FlowGroupByDestinationIpAndSeverityVO::getNum).reduce(Integer::sum).orElse(0);
        int normalSize = allNodeSize - severitySize;
        voList.add(new FlowGroupByDestinationIpAndSeverityVO(0, normalSize));
        return voList.stream().sorted(Comparator.comparing(FlowGroupByDestinationIpAndSeverityVO::getSeverity).reversed()).collect(Collectors.toList());
    }

    
    private List<FlowGroupByDestinationIpAndSeverityVO> convertSeverityList(List<FlowGroupByDestinationIpAndSeverityBO> boList, BatchSceneHandler batchSceneHandler) {
        
        Map<String, Integer> severityMap = new HashMap<>();
        
        boList.forEach(bo -> {
            List<String> destinationIpList = bo.getDestinationIpList();
            destinationIpList.forEach(ip -> {
                SceneElementBO nodeByIpAddr = batchSceneHandler.getNodeByIpAddr(ip);
                
                if (severityMap.containsKey(nodeByIpAddr.getId())) {
                    
                    if (severityMap.get(nodeByIpAddr.getId()) < bo.getSeverity()) {
                        severityMap.put(nodeByIpAddr.getId(), bo.getSeverity());
                    }
                } else {
                    severityMap.put(nodeByIpAddr.getId(), bo.getSeverity());
                }
            });
        });

        
        Map<Integer, Integer> severityNumMap = new HashMap<>();
        severityMap.forEach((key, value) -> {
            if (severityNumMap.containsKey(value)) {
                severityNumMap.put(value, severityNumMap.get(value) + 1);
            } else {
                severityNumMap.put(value, 1);
            }
        });

        
        if (boList.size() > severityNumMap.size()) {
            boList.forEach(bo -> {
                if (!severityNumMap.containsKey(bo.getSeverity())) {
                    severityNumMap.put(bo.getSeverity(), 0);
                }
            });
        }

        List<FlowGroupByDestinationIpAndSeverityVO> voList = new ArrayList<>();
        severityNumMap.forEach((key, value) -> {
            FlowGroupByDestinationIpAndSeverityVO severityVO = new FlowGroupByDestinationIpAndSeverityVO();
            severityVO.setSeverity(key);
            severityVO.setNum(value);
            voList.add(severityVO);
        });

        return voList;
    }


    public FlowRiskCount getFlowRiskCount(BaseSceneElementRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);

        BaseRequestUtil.checkBaseRequestWithSceneId(baseRequest);

        List<String> sceneIds = CollectionUtil.toList(baseRequest.getSceneId());

        List<String> nodeIds = null;
        if (!CollectionUtil.isEmpty(baseRequest.getElementIds())) {
            nodeIds = CollectionUtil.toList(baseRequest.getElementIds().get(0));
        }

        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);

        ServiceResult<Map<String, Integer>> mapResult = scenePluginCollectionSceneService.groupCountFlowByLevel(sceneIdList, nodeIds, null, null);
        Map<String, Integer> map = mapResult.getSource();

        
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

        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);

        ServiceResult<Map<String, Integer>> mapResult = scenePluginCollectionSceneService.groupCountFlowByType(sceneIdList, nodeIds, null, null, baseRequest.getSourceIps(), baseRequest.getTopSize());
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
        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);
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
        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);
        String groupField = baseRequest.getIpType() == 0 ?
                ScenePluginCollectionSceneService.GroupCountFlowField.SOURCE_IP :
                ScenePluginCollectionSceneService.GroupCountFlowField.DEST_IP;
        FlowGroupCountParams params = new FlowGroupCountParams(groupField);
        params.setQueryAddress(baseRequest.getIps());
        params.setStartTime(baseRequest.getStartTime());
        params.setEndTime(baseRequest.getEndTime());
        params.setTopSize(baseRequest.getTopSize());
        params.setSourceIps(baseRequest.getSourceIps());
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
        log.info("getAttackRatioCount baseRequest:{}", JsonCommon.stringify(baseRequest));
        BaseRequestUtil.checkBaseRequestWithSceneId(baseRequest);

        List<String> sceneIds = Arrays.asList(StringUtils.split(baseRequest.getSceneId(), ","));
        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);
        var countResult = scenePluginCollectionSceneService.groupCountFlow(sceneIdList, baseRequest.getIps(), baseRequest.getTopSize(), baseRequest.getStartTime(), baseRequest.getEndTime()).getSource();
        TotalFlowAttackRatio totalFlowAttackRatio = new TotalFlowAttackRatio();
        totalFlowAttackRatio.setTotalAttackNumber(countResult.getAttackNumber());
        totalFlowAttackRatio.setTotalTrafficSize(countResult.getTrafficSize());
        totalFlowAttackRatio.setAttackWayList(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(countResult.getAttackWayList())) {
            countResult.getAttackWayList().stream()
                    .forEach(v -> totalFlowAttackRatio.getAttackWayList()
                            .add(FlowAttackRatio.builder()
                                    .attackTrafficPercent(v.getAttackNumberPercent().doubleValue())
                                    .type(v.getAggregationType())
                                    .build())
                    );
        }
        return totalFlowAttackRatio;
    }


    
    public ListObjServiceResult<FlowAttackDto> pageFlow(String id, FlowQueryParams queryParams) {
        if (Objects.isNull(queryParams.getPage())) {
            queryParams.setPage(1);
        }
        if (Objects.isNull(queryParams.getPerPage())) {
            queryParams.setPerPage(100);
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
                
                FlowAttackDto flowAttackDto = new FlowAttackDto();
                BeanUtils.copyProperties(flowBO, flowAttackDto);

                flowAttackDtos.add(flowAttackDto);
            }
        }

        List<FlowAttackDto> finalList = flowAttackDtos;
        if (flowAttackDtos.size() > queryParams.getPageSize()) {
            finalList = flowAttackDtos.subList(0, queryParams.getPageSize());
        }

        return new ListObjServiceResult(new ListObjResult(finalList, flowBOResult.getSource().getTotal()).asResult()).asResult();
    }


    private List<FlowTrend> processFlowTrend(List<String> sceneIds, List<String> elementIds, LocalDateTime startTime, LocalDateTime endTime, Integer segmentCount, List<String> sourceIps, Integer type) {
        
        if (Objects.isNull(endTime)) {
            endTime = LocalDateTime.now();
        }

        if (Objects.isNull(startTime)) {
            startTime = endTime.minusSeconds(BeanCommon.getBean(CsceneAppConfig.class).getFlowSeconds());
        }

        if (Objects.isNull(segmentCount)) {
            segmentCount = BeanCommon.getBean(CsceneAppConfig.class).getFlowSegment();
        }

        
        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);
        Long intervalSeconds = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(startTime, endTime, segmentCount);

        ServiceResult<List<IntervalSizeBO<LocalDateTime, Long>>> flowResult = scenePluginCollectionSceneService.timeIntervalSumFlowSize(sceneIdList, elementIds, startTime, endTime, intervalSeconds.intValue(), sourceIps, type);

        List<IntervalSizeBO<LocalDateTime, Long>> intervalFlowSizeBOs = flowResult.getSource();
        List<FlowTrend> flowTrends = new ArrayList<>();
        if (Objects.isNull(intervalFlowSizeBOs)) {
            return flowTrends;
        }

        List<LocalDateTime> timeList = SegmentCount2IntervalSecondsUtil.generateTimeList(startTime, endTime, intervalSeconds);
        Map<LocalDateTime, FlowTrend> flowTrendMap = new HashMap<>();
        for (LocalDateTime time : timeList) {
            FlowTrend flowTrend = new FlowTrend();
            
            flowTrend.setTime(time);
            flowTrend.setDataSize(0L);
            flowTrendMap.put(time, flowTrend);
        }

        
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

    
    public FlowAnalysisSummary getFlowAnalysisSummary(BaseTimeRangeRequest timeRangeRequest) {
        LocalDateTime now = LocalDateTime.now();
        if (Objects.isNull(timeRangeRequest.getEndTime())) {
            timeRangeRequest.setEndTime(now);
        }

        List<String> translatedSceneIds = csceneCommonService.translateSceneIds(CollectionUtil.toList(timeRangeRequest.getSceneId()));
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(translatedSceneIds, timeRangeRequest.getElementIds());
        if (timeRangeRequest.getStartTime() == null) {
            if (batchSceneHandler.getEarliestStartTime() != null) {
                timeRangeRequest.setStartTime(batchSceneHandler.getEarliestStartTime().minusHours(24));
            } else {
                timeRangeRequest.setStartTime(timeRangeRequest.getEndTime().minusDays(10));
            }
        }

        FlowAnalysisRequest flowAnalysisRequest = FlowAnalysisRequest.builder()
                .groupList(batchSceneHandler.getFlowCollectors())
                .startDateTime(timeRangeRequest.getStartTime())
                .endDateTime(timeRangeRequest.getEndTime()).build();
        Double totalFlowBytes = 0.0;
        Double totalAttackFlowBytes = 0.0;
        Long attackCount = 0L;
        if (flowAnalysisRequest.getGroupList() != null && flowAnalysisRequest.getGroupList().size() > 0) {
            
            totalFlowBytes = flowAnalysisService.calcNetworkBytes(flowAnalysisRequest);

            
            flowAnalysisRequest.setSeverityEnum(SeverityEnum.ALL);
            totalAttackFlowBytes = flowAnalysisService.calcNetworkBytes(flowAnalysisRequest);

            
            FlowRiskCount riskCount = getFlowRiskCount(timeRangeRequest);
            attackCount = (long) (riskCount.getHighCount() + riskCount.getMediumCount() + riskCount.getLowCount());
        }

        
        Integer netConnSize = 0;
        List<ElementMonitorBasicInfo> eles = sceneMonitorService.getAllSceneElements(timeRangeRequest.getSceneId(), false, new SceneAllElementsQueryRequest());

        for (ElementMonitorBasicInfo ele : eles) {
            BaseSceneElementPageRequest request = new BaseSceneElementPageRequest();
            request.setSceneId(timeRangeRequest.getSceneId());
            request.setElementIds(CollectionUtil.toList(ele.getId()));
            ListObjServiceResult<ConnectionState> conns = elementMonitorService.pageNetworkConnections(request);
            int total = conns.getSource().getTotal();
            netConnSize = netConnSize + total;
        }

        
        Double bandwith = 30.0;

        
        long deno = 1024 * 1024 * 1024;
        double totalFlowGB = 0.0;
        if (totalFlowBytes != 0) {
            totalFlowGB = NumberUtil.round(totalFlowBytes / deno, 3).doubleValue();
        }
        double totalAttackFlowGB = 0.0;
        if (totalAttackFlowBytes != 0) {
            totalAttackFlowGB = NumberUtil.round(totalAttackFlowBytes / deno, 3).doubleValue();
        }

        
        if (totalFlowBytes > 0 && totalFlowGB == 0) {
            totalFlowGB = 0.001;
        }

        if (totalAttackFlowBytes > 0 && totalAttackFlowGB == 0) {
            totalAttackFlowGB = 0.001;
        }


        FlowAnalysisSummary summary =
                FlowAnalysisSummary.builder()
                        .totalFlowSize(totalFlowGB)
                        .totalAttackFlowSize(totalAttackFlowGB)
                        .attackCount(attackCount)
                        .netConnSize(netConnSize)
                        .bandwith(bandwith)
                        .build();

        return summary;

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
        return flowTrendMap.get(closestTime);
    }

    
    public String export(String taskId, String csceneId) {
        
        String sceneId = csceneCommonService.translateSceneId(csceneId);
        if (StringUtils.isEmpty(sceneId)) {
            log.error("find no sceneId by csceneId {}", sceneId);
            return StringUtils.EMPTY;
        }
        String url = scenePluginCollectionSceneService.exportFlowPcap(sceneId, taskId).getSource();
        if (StringUtils.isNotEmpty(url)) {
            CloudAppConfig config = BeanCommon.getBean(CloudAppConfig.class);
            if (config.getPublicNetProxy() && config.getPublicNetProxyServer()) {
                if (StringUtils.isNotEmpty(config.getPublicNetProxyIp())) {
                    var u = URLUtil.url(url);
                    url = StringUtils.replaceOnce(url, u.getHost(), config.getPublicNetProxyIp());
                }
            }
        }
        FlowPcapExportUtil.finalProcess(taskId, url);
        return url;
    }
}
