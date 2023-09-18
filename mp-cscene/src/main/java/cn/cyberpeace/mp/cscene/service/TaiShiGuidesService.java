package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.bo.IntervalSizeBO;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackTypeCount;
import cn.cyberpeace.mp.cscene.dto.flow.FlowTrend;
import cn.cyberpeace.mp.cscene.dto.scene.FlowEventData;
import cn.cyberpeace.mp.cscene.dto.scene.TaiShiEventInfo;
import cn.cyberpeace.mp.cscene.dto.scene.TaiShiTotalCount;
import cn.cyberpeace.mp.cscene.dto.scene.WazuhEventData;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.common.TaiShiHostRequest;
import cn.cyberpeace.mp.cscene.request.common.TaiShiRequest;
import cn.cyberpeace.mp.cscene.request.flow.AttackTypeTopRequest;
import cn.cyberpeace.mp.cscene.request.flow.FlowTrendRequest;
import cn.cyberpeace.mp.cscene.util.SegmentCount2IntervalSecondsUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaiShiGuidesService {

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private DataCollectionService dataCollectionService;

    @Resource
    private CsceneCommonService csceneCommonService;

    @Resource
    private FlowMonitorService flowMonitorService;

    @Resource
    private HostStateService hostStateService;

    @Resource
    private CsceneInstanceService csceneInstanceService;

    public ListObjServiceResult<TaiShiEventInfo> getAttCkEventList(TaiShiRequest request) {
        var sceneIds = csceneCommonService.translateSceneIds(Arrays.asList(request.getSceneId()));
        if (CollectionUtils.isEmpty(sceneIds)) {
            return new ListObjServiceResult<>();
        }
        BatchSceneHandler sceneHandler = new BatchSceneHandler(sceneIds, new ArrayList<>());
        CollectionScenePluginHandler handler = sceneHandler.getCollectionScenePluginHandler(sceneIds.get(0));
        if (handler == null) {
            return new ListObjServiceResult<>();
        }

        if (!Boolean.TRUE.equals(request.getHost()) && !Boolean.TRUE.equals(request.getFlow())) {
            return new ListObjServiceResult<>();
        }

        Set<String> indexes = new HashSet<>();
        var mergeQuery = QueryBuilders.boolQuery();

        if (Boolean.TRUE.equals(request.getHost())) {
            EsEventRequestParsedResult hostDefenseResult = this.getHostEsEventRequestParsedResult(sceneHandler, request.getHost() == null ? new TaiShiRequest.EventRequest() : request.getDefense());
            EsEventRequestParsedResult hostAttackResult = this.getHostEsEventRequestParsedResult(sceneHandler, request.getAttack() == null ? new TaiShiRequest.EventRequest() : request.getAttack());
            if (hostDefenseResult != null) {
                indexes.addAll(hostDefenseResult.getIndexes());
                mergeQuery.should(hostDefenseResult.getQuery());
            }
            if (hostAttackResult != null) {
                indexes.addAll(hostAttackResult.getIndexes());
                mergeQuery.should(hostAttackResult.getQuery());
            }
        }

        if (Boolean.TRUE.equals(request.getFlow())) {
            EsEventRequestParsedResult flowDefenseResult = this.getFlowEsEventRequestParsedResult(sceneIds.get(0), sceneHandler, request.getDefense() == null ? new TaiShiRequest.EventRequest() : request.getDefense());
            EsEventRequestParsedResult flowAttackResult = this.getFlowEsEventRequestParsedResult(sceneIds.get(0), sceneHandler, request.getAttack() == null ? new TaiShiRequest.EventRequest() : request.getAttack());
            if (flowDefenseResult != null) {
                indexes.addAll(flowDefenseResult.getIndexes());
                mergeQuery.should(flowDefenseResult.getQuery());
            }
            if (flowAttackResult != null) {
                indexes.addAll(flowAttackResult.getIndexes());
                mergeQuery.should(flowAttackResult.getQuery());
            }
        }

        return getResult(request.getPage(), request.getPageSize(), sceneHandler, indexes, mergeQuery);
    }

    public ListObjServiceResult<TaiShiEventInfo> getHostEventList(TaiShiHostRequest request) {
        var sceneIds = csceneCommonService.translateSceneIds(Arrays.asList(request.getSceneId()));
        if (CollectionUtils.isEmpty(sceneIds)) {
            return new ListObjServiceResult<>();
        }
        BatchSceneHandler sceneHandler = new BatchSceneHandler(sceneIds, new ArrayList<>());
        CollectionScenePluginHandler handler = sceneHandler.getCollectionScenePluginHandler(sceneIds.get(0));
        if (handler == null) {
            return new ListObjServiceResult<>();
        }

        Set<String> indexes = new HashSet<>();
        var mergeQuery = QueryBuilders.boolQuery();

        TaiShiRequest.EventRequest r = new TaiShiRequest.EventRequest();
        r.setStartTime(request.getStartTime());
        r.setEndTime(request.getEndTime());
        EsEventRequestParsedResult hostResult = this.getHostEsEventRequestParsedResult(sceneHandler, r);
        indexes.addAll(hostResult.getIndexes());
        mergeQuery.should(hostResult.getQuery());

        return getResult(request.getPage(), request.getPageSize(), sceneHandler, indexes, mergeQuery);
    }

    private ListObjServiceResult<TaiShiEventInfo> getResult(int page, int pageSize, BatchSceneHandler sceneHandler, Set<String> indexes, BoolQueryBuilder mergeQuery) {
        var searchBuilder = new SearchSourceBuilder();
        searchBuilder.query(mergeQuery);

        // 分页
        searchBuilder.from((page - 1) * pageSize);
        searchBuilder.size(pageSize);
        // 时间范围
        // 排序
        searchBuilder.sort("@timestamp", SortOrder.DESC);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexes.toArray(new String[]{}));
        searchRequest.source(searchBuilder);
        List<TaiShiEventInfo> result = new ArrayList<>();
        int total = 0;
        try {
            var response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            total = (int) response.getHits().getTotalHits().value;
            var mac2Elements = sceneHandler.getAllMac2ElementMapForScene();
            var eid2Elements = sceneHandler.getAllEid2ElementMap();
            for (var hit : response.getHits().getHits()) {
                if (hit.getSourceAsMap().containsKey("agent")) {
                    var eventData = JsonCommon.snakeMapToBean(hit.getSourceAsMap(), WazuhEventData.class);
                    var taishi = this.getEventInfoFrom(eventData, eid2Elements);
                    result.add(taishi);
                } else {
                    var eventData = JsonCommon.mapToBean(hit.getSourceAsMap(), FlowEventData.class);
                    eventData.setTimestamp(hit.getSourceAsMap().get("@timestamp").toString());
                    var taishi = this.getEventInfoFrom(eventData, mac2Elements);
                    result.add(taishi);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ListObjServiceResult<>(new ListObjResult<>(result, total));
    }

    private EsEventRequestParsedResult getHostEsEventRequestParsedResult(BatchSceneHandler sceneHandler, TaiShiRequest.EventRequest request) {
        LocalDateTime start = request.getStartTime() == null ? sceneHandler.getEarliestStartTime() : request.getStartTime();
        LocalDateTime end = request.getEndTime() == null ? start.plusSeconds(10) : request.getEndTime();

        // 取探针数据索引
        var indices = dataCollectionService.indexListBuilder(start, end);

        // 探针数据查询条件
        List<String> targetNodeEids = this.getEidsByNodeIds(sceneHandler, request.getTargetNodeIds());
        var agentQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("agent.name", targetNodeEids))
                .must(QueryBuilders.existsQuery("rule.mitre.tactic"));
        if (CollectionUtils.isNotEmpty(request.getDefenseTypes())) {
            List<String> types = request.getDefenseTypes().stream()
                    .map(d -> FlowAttackTypeConvert.getFormatType(d))
                    .collect(Collectors.toList());
            agentQuery.must(QueryBuilders.termsQuery("rule.mitre.tactic", types));
        }

        if (CollectionUtils.isNotEmpty(request.getEventName())) {
            // 加了 event name 条件,默认查不到数据
            agentQuery.must(QueryBuilders.termsQuery("rule.mitre.tactic", Arrays.asList(TextUtils.uuid())));
        }

        EsEventRequestParsedResult result = new EsEventRequestParsedResult();
        result.setIndexes(indices);
        result.setQuery(agentQuery);
        return result;
    }

    private EsEventRequestParsedResult getFlowEsEventRequestParsedResult(String sceneId, BatchSceneHandler sceneHandler, TaiShiRequest.EventRequest request) {
        if (request != null && CollectionUtils.isNotEmpty(request.getEventName())) {
            return null;
        }
        CollectionScenePluginHandler handler = sceneHandler.getCollectionScenePluginHandler(sceneId);
        String flowIndex = handler.getScene().getFlowCollector();
        if (StringUtils.isEmpty(flowIndex)) {
            return null;
        }
        flowIndex = flowIndex + "_session*";

        // 流量查询条件
        var flowQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.existsQuery("suricata"));
        // 流量数据既有源也有目的 可以使用ip来查下
        List<String> sourceIps = this.getIpsByNodeIds(sceneHandler, request.getSourceNodeIds());
        List<String> targetIps = this.getIpsByNodeIds(sceneHandler, request.getTargetNodeIds());
        flowQuery.must(QueryBuilders.termsQuery("source.ip", sourceIps));
        flowQuery.must(QueryBuilders.termsQuery("destination.ip", targetIps));
        if (CollectionUtils.isNotEmpty(request.getDefenseTypes())) {
            List<String> attackTypes = new ArrayList<>();
            for (var defenseType : request.getDefenseTypes()) {
                attackTypes.addAll(FlowAttackTypeConvert.getAttackTypes(FlowAttackTypeConvert.getFormatType(defenseType)));
            }
            flowQuery.must(QueryBuilders.termsQuery("suricata.category", attackTypes));
        }

        if (CollectionUtils.isNotEmpty(request.getEventName())) {
            // 加了 event name 条件,默认查不到数据
            flowQuery.must(QueryBuilders.termsQuery("rule.mitre.tactic", Arrays.asList(TextUtils.uuid())));
        }

        EsEventRequestParsedResult result = new EsEventRequestParsedResult();
        result.setIndexes(Arrays.asList(flowIndex));
        result.setQuery(flowQuery);
        return result;
    }

    public ServiceResult<List<FlowTrend>> getEventTiming(String sceneId) {
        var sceneResult = csceneInstanceService.get(sceneId);
        if (sceneResult == null || sceneResult.getSource() == null || sceneResult.getSource().getObj() == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }

        var scene = sceneResult.getSource().getObj();
        LocalDateTime startTime = scene.getCreateTime();
        LocalDateTime endTime = LocalDateTime.now();
        if (Boolean.TRUE.equals(scene.getDeleted())) {
            endTime = scene.getUpdateTime();
        }

        // todo 10秒来分可能会有很多数据
        List<LocalDateTime> timeList = SegmentCount2IntervalSecondsUtil.generateTimeList(startTime, endTime, 10);
        var flows = this.getFlowEventTrend(sceneId, startTime, endTime, timeList.size());
        var hosts = this.getHostEventTrend(sceneId, startTime, endTime, timeList.size());
        List<FlowTrend> trends = new ArrayList<>();
        trends.addAll(flows);
        for (int i = 0; i < hosts.size(); i++) {
            trends.get(i).setDataSize(trends.get(i).getDataSize() + hosts.get(i).getDataSize());
        }
        var results = trends.stream()
                .filter(f -> f.getDataSize() > 0)
                .collect(Collectors.toList());
        return new ServiceResult<>(results);
    }

    public ServiceResult<List<FlowTrend>> getEventTrend(String sceneId) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(10);// todo 暂时查最近10天内的 方便测试

        var flows = this.getFlowEventTrend(sceneId, startTime, endTime, 24);
        var hosts = this.getHostEventTrend(sceneId, startTime, endTime, 24);
        List<FlowTrend> trends = new ArrayList<>();
        trends.addAll(flows);
        for (int i = 0; i < hosts.size(); i++) {
            trends.get(i).setDataSize(trends.get(i).getDataSize() + hosts.get(i).getDataSize());
        }
        return new ServiceResult<>(trends);
    }

    private List<FlowTrend> getFlowEventTrend(String sceneId, LocalDateTime startTime, LocalDateTime endTime, int segmentCount) {
        FlowTrendRequest request = new FlowTrendRequest();
        request.setSegmentCount(segmentCount);
        request.setSceneId(sceneId);
        request.setType(NumberUtils.INTEGER_ONE);
        request.setEndTime(endTime);
        request.setStartTime(startTime);
        var flows = flowMonitorService.getSceneFlowTrend(request);
        return flows;
    }

    private List<FlowTrend> getHostEventTrend(String sceneId, LocalDateTime startTime, LocalDateTime endTime, int segmentCount) {
        Long intervalSeconds = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(startTime, endTime, segmentCount);
        List<LocalDateTime> timeList = SegmentCount2IntervalSecondsUtil.generateTimeList(startTime, endTime, intervalSeconds);
        Map<LocalDateTime, FlowTrend> flowTrendMap = new HashMap<>();
        for (LocalDateTime time : timeList) {
            FlowTrend flowTrend = new FlowTrend();
            // 初始化
            flowTrend.setTime(time);
            flowTrend.setDataSize(0L);
            flowTrendMap.put(time, flowTrend);
        }

        BaseSceneElementRequest elementRequest = new BaseSceneElementRequest();
        elementRequest.setSceneId(sceneId);
        var hosts = hostStateService.getSceneAttCkTypeCountTrend(elementRequest, startTime, endTime).getSource();
        for (IntervalSizeBO<LocalDateTime, Long> bo : hosts) {
            if (Objects.nonNull(bo.getStart())) {
                FlowTrend flowTrend = getClosest(bo.getStart(), flowTrendMap);
                if (Objects.nonNull(flowTrend)) {
                    flowTrend.setDataSize(bo.getSize() + flowTrend.getDataSize());
                }
            }
        }

        List<FlowTrend> hostTrends = new ArrayList<>();
        flowTrendMap.entrySet().forEach(entry -> {
            hostTrends.add(entry.getValue());
        });
        hostTrends.sort(Comparator.comparing(FlowTrend::getTime));
        return hostTrends;
    }

    public ServiceResult<TaiShiTotalCount> getAttCkEventCount(String sceneId) {
        TaiShiTotalCount taiShiTotalCount = new TaiShiTotalCount();
        taiShiTotalCount.setFlow(this.flowTotalCount(sceneId));
        taiShiTotalCount.setHost(this.hostTotalCount(sceneId));
        return new ServiceResult<>(taiShiTotalCount);
    }

    private long hostTotalCount(String sceneId) {
        BaseSceneElementRequest request = new BaseSceneElementRequest();
        request.setSceneId(sceneId);
        return hostStateService.getSceneAttCkTypeCountInfo(request).getSource().getTotalTimes();
    }

    private long flowTotalCount(String sceneId) {
        AttackTypeTopRequest request = new AttackTypeTopRequest();
        request.setSceneId(sceneId);
        List<FlowAttackTypeCount> attackTypeCounts = flowMonitorService.getAttackTypeCount(request);
        long total = 0;
        if (CollectionUtils.isNotEmpty(attackTypeCounts)) {
            for (FlowAttackTypeCount flowAttackTypeCount : attackTypeCounts) {
                total += flowAttackTypeCount.getTimes();
            }
        }
        return total;
    }

    private TaiShiEventInfo getEventInfoFrom(WazuhEventData eventData, Map<String, SceneElementBO> eid2Elements) {
        TaiShiEventInfo taiShi = new TaiShiEventInfo();
        taiShi.setActionType("attack");
        taiShi.setTriggerType("host");
        taiShi.setAttCks(eventData.getRule().getMitre().getTactic());
        taiShi.setAttCkNames(this.getAttCkTypeNames(taiShi.getAttCks()));
        taiShi.setHostEventType("");
        taiShi.setHostEventDesc(StringUtils.join(eventData.getRule().getMitre().getTechnique(), ","));
        String utcTime = eventData.getTimestamp();
        if (!eventData.getTimestamp().contains("Z")) {
            utcTime = StringUtils.substringBeforeLast(utcTime, ".").concat("Z");
        }
        taiShi.setTime(TimeUtils.utcStringToLocalDateTime(utcTime));
        if (eventData.getAgent() != null && eid2Elements.containsKey(eventData.getAgent().getName())) {
            var element = eid2Elements.get(eventData.getAgent().getName());
            taiShi.setTargetNodeName(element.getName());
            taiShi.setTargetNodeIp(eventData.getAgent().getIp());
            taiShi.setTargetNodeId(element.getId());
        }
        return taiShi;
    }

    private TaiShiEventInfo getEventInfoFrom(FlowEventData eventData, Map<String, SceneElementBO> mac2Elements) {
        TaiShiEventInfo taiShi = new TaiShiEventInfo();
        taiShi.setActionType("attack");
        taiShi.setTriggerType("flow");
        taiShi.setTime(TimeUtils.timestampToLocalDateTime(Long.parseLong(eventData.getTimestamp())));
        if (eventData.getSource() != null) {
            var element = this.getElementBy(eventData.getSource().getMac(), mac2Elements);
            if (element != null) {
                taiShi.setSourceNodeId(element.getId());
                taiShi.setSourceNodeIp(eventData.getSource().getIp());
                taiShi.setSourceNodeName(element.getName());
                taiShi.setSourceNodePort(eventData.getSource().getPort());
            }
        }
        if (eventData.getDestination() != null) {
            var element = this.getElementBy(eventData.getDestination().getMac(), mac2Elements);
            if (element != null) {
                taiShi.setTargetNodeId(element.getId());
                taiShi.setTargetNodeIp(eventData.getDestination().getIp());
                taiShi.setTargetNodeName(element.getName());
                taiShi.setTargetNodePort(eventData.getDestination().getPort());
            }
        }
        taiShi.setAttCks(this.getAttCksByFlowAttackTypes(eventData.getSuricata().getCategory()));
        taiShi.setAttCkNames(this.getAttCkTypeNames(taiShi.getAttCks()));
        return taiShi;
    }

    private SceneElementBO getElementBy(List<String> onePortMac, Map<String, SceneElementBO> mac2Elements) {
        if (CollectionUtils.isEmpty(onePortMac)) {
            return null;
        }
        for (String mac : onePortMac) {
            if (mac2Elements.containsKey(mac)) {
                return mac2Elements.get(mac);
            }
        }
        return null;
    }

    private List<String> getAttCkTypeNames(List<String> attCks) {
        if (CollectionUtils.isEmpty(attCks)) {
            return new ArrayList<>();
        }
        return attCks.stream()
                .map(a -> this.getAttCkTypeName(a))
                .collect(Collectors.toList());
    }

    private String getAttCkTypeName(String attCk) {
        String key = attCk.replaceAll(" ", "_");
        return MessageCommon.get("cscene.attck." + key.toLowerCase());
    }

    public List<String> getAttCksByFlowAttackTypes(List<String> flowAttackTypes) {
        List<String> result = new ArrayList<>();
        for (var a : flowAttackTypes) {
            var attCk = this.getAttCkByFlowAttackType(a);
            if (attCk != null) {
                result.add(attCk);
            }
        }
        return result;
    }

    private String getAttCkByFlowAttackType(String flowAttackType) {
        return FlowAttackTypeConvert.attackType2AttCks.get(flowAttackType);
    }

    private List<String> getIpsByNodeIds(BatchSceneHandler sceneHandler, List<String> nodeIds) {
        if (CollectionUtils.isEmpty(nodeIds)) {
            return new ArrayList<>(sceneHandler.getAllIps());
        } else {
            List<String> result = new ArrayList<>();
            if (CollectionUtils.isEmpty(nodeIds)) {
                result.addAll(sceneHandler.getAllIps());
            } else {
                for (var nodeId : nodeIds) {
                    var ips = sceneHandler.getIpsByNodeId(nodeId);
                    if (CollectionUtils.isNotEmpty(ips)) {
                        result.addAll(ips);
                    }
                }
            }
            return result;
        }
    }

    private List<String> getEidsByNodeIds(BatchSceneHandler sceneHandler, List<String> nodeIds) {
        List<String> targetNodeEids = new ArrayList<>();
        // 探针数据 只有目标节点 没有源节点
        if (CollectionUtils.isEmpty(nodeIds)) {
            // 参数未指定 则查询全部节点
            targetNodeEids.addAll(sceneHandler.getAllEid2ElementMap().keySet());
        } else {
            // 参数指定 则转换一下eid
            for (var nodeId : nodeIds) {
                var node = sceneHandler.getNodeByNodeId(nodeId);
                if (node != null) {
                    targetNodeEids.add(node.getEid());
                }
            }
        }
        return targetNodeEids;
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

    @Data
    private static class EsEventRequestParsedResult {
        private List<String> indexes;

        private BoolQueryBuilder query;
    }
}
