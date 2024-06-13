package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.bo.IntervalSizeBO;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackTypeCount;
import cn.cyberpeace.mp.cscene.dto.flow.FlowClassificationDto;
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
import cn.cyberpeace.mp.sceneplugincollection.bo.AttCkTypeCount;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.situationaware.model.FlowClassificationFromJsonModel;
import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
import cn.cyberpeace.mp.situationaware.service.ClassificationAnalyseService;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.utils.FileUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.*;
import java.util.*;

@Slf4j
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

    @Autowired
    private ClassificationAnalyseService classificationAnalyseService;

    public List<FlowClassificationDto> listAllFlowClassifications() {
        FlowClassificationFromJsonModel flowClassificationFromJsonModel = classificationAnalyseService.getFlowClassificationInfoFromJson();
        List<FlowClassificationDto> flowClassificationDtoList = FlowClassificationDto.getDtoListFromModel(flowClassificationFromJsonModel);
        List<FlowClassificationDto> attackTypeNotEmptyDtoList = new ArrayList<>();
        for (FlowClassificationDto dto : flowClassificationDtoList) {
            if (StringUtils.isNotBlank(dto.getAttackType())) {
                attackTypeNotEmptyDtoList.add(dto);
            }
        }
        return attackTypeNotEmptyDtoList;
    }

    public ListObjServiceResult<TaiShiEventInfo> getAttCkEventList(TaiShiRequest request) {
        List<String> sceneIds = new ArrayList<>();
        if (StringUtils.isNotEmpty(request.getSceneId()) && request.getSceneId().contains(",")) {
            String[] ids = request.getSceneId().split(",");
            List<String> sceneIdsTmp = csceneCommonService.translateSceneIds(Arrays.asList(ids));
            sceneIds.addAll(sceneIdsTmp);
        } else {
            List<String> sceneIdsTmp = csceneCommonService.translateSceneIds(Arrays.asList(request.getSceneId()));
            sceneIds.addAll(sceneIdsTmp);
        }
        if (CollectionUtils.isEmpty(sceneIds)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        BatchSceneHandler sceneHandler = new BatchSceneHandler(sceneIds, new ArrayList<>());
        CollectionScenePluginHandler handler = sceneHandler.getCollectionScenePluginHandler(sceneIds.get(0));
        if (handler == null) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        if (!Boolean.TRUE.equals(request.getHost()) && !Boolean.TRUE.equals(request.getFlow())) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        Set<String> indexes = new HashSet<>();
        BoolQueryBuilder mergeQuery = QueryBuilders.boolQuery();

        TaiShiRequest.EventRequest formatDefenseReq = request.getDefense() == null ? new TaiShiRequest.EventRequest() : request.getDefense();
        if (CollectionUtils.isNotEmpty(formatDefenseReq.getDefenseTypes())) {
            List<String> formatDefenseTypes = new ArrayList<>();
            for (String d : formatDefenseReq.getDefenseTypes()) {
                String engageKey = ClassificationConvert.getEngageKey(d);
                formatDefenseTypes.add(engageKey);
            }
            formatDefenseReq.setDefenseTypes(formatDefenseTypes);
        } else {
            formatDefenseReq.setDefenseTypes(ClassificationConvert.getAllEngageKeys());
        }

        TaiShiRequest.EventRequest formatAttCkReq = request.getAttack() == null ? new TaiShiRequest.EventRequest() : request.getAttack();
        if (CollectionUtils.isNotEmpty(formatAttCkReq.getDefenseTypes())) {
            List<String> formatDefenseTypes = new ArrayList<>();
            for (String d : formatAttCkReq.getDefenseTypes()) {
                String attCkKey = ClassificationConvert.getAttCkKey(d);
                formatDefenseTypes.add(attCkKey);
            }
            formatAttCkReq.setDefenseTypes(formatDefenseTypes);
        } else {
            formatAttCkReq.setDefenseTypes(ClassificationConvert.getAllAttCkKeys());
        }

        if (Boolean.TRUE.equals(request.getHost())) {
            EsEventRequestParsedResult hostDefenseResult = this.getHostEsEventRequestParsedResult(sceneHandler, formatDefenseReq);
            EsEventRequestParsedResult hostAttackResult = this.getHostEsEventRequestParsedResult(sceneHandler, formatAttCkReq);
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
            EsEventRequestParsedResult flowDefenseResult = this.getFlowEsEventRequestParsedResult(sceneIds.get(0), sceneHandler, formatDefenseReq);
            EsEventRequestParsedResult flowAttackResult = this.getFlowEsEventRequestParsedResult(sceneIds.get(0), sceneHandler, formatAttCkReq);
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
        List<String> sceneIds = csceneCommonService.translateSceneIds(Arrays.asList(request.getSceneId()));
        if (CollectionUtils.isEmpty(sceneIds)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        BatchSceneHandler sceneHandler = new BatchSceneHandler(sceneIds, new ArrayList<>());
        CollectionScenePluginHandler handler = sceneHandler.getCollectionScenePluginHandler(sceneIds.get(0));
        if (handler == null) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        Set<String> indexes = new HashSet<>();
        BoolQueryBuilder mergeQuery = QueryBuilders.boolQuery();

        TaiShiRequest.EventRequest r = new TaiShiRequest.EventRequest();
        r.setStartTime(request.getStartTime());
        r.setEndTime(request.getEndTime());
        EsEventRequestParsedResult hostResult = this.getHostEsEventRequestParsedResult(sceneHandler, r);
        indexes.addAll(hostResult.getIndexes());
        mergeQuery.should(hostResult.getQuery());

        return getResult(request.getPage(), request.getPageSize(), sceneHandler, indexes, mergeQuery);
    }

    private ServiceResult<List<FlowTrend>> getResultHistogram(BatchSceneHandler sceneHandler, Set<String> indexes, BoolQueryBuilder mergeQuery) {
        long interval = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(sceneHandler.getEarliestStartTime(), sceneHandler.getLatestSceneRunningTime(), 50);
        SearchSourceBuilder searchBuilder = new SearchSourceBuilder();
        searchBuilder.query(mergeQuery);
        AggregationBuilder aggregationBuilder = AggregationBuilders.dateHistogram("histogram").field("@timestamp")
                .fixedInterval(DateHistogramInterval.seconds((int) interval))
                .subAggregation(AggregationBuilders.count("count").field("@timestamp"));
        searchBuilder.aggregation(aggregationBuilder);
        searchBuilder.size(0);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexes.toArray(new String[]{}));
        searchRequest.source(searchBuilder);
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = searchResponse.getAggregations();
            ParsedDateHistogram parsedTerms = aggregations.get("histogram");
            List<? extends Histogram.Bucket>  buckets = parsedTerms.getBuckets();
            List<FlowTrend> flowTrends = new ArrayList<>();
            for (Histogram.Bucket bucket : buckets) {
                FlowTrend flowTrend = getFlowTrend(bucket.getKeyAsString(), bucket.getAggregations());
                flowTrends.add(flowTrend);
            }
            return new ServiceResult<>(flowTrends);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static FlowTrend getFlowTrend(String bucket, Aggregations bucket1) {
        FlowTrend bo = new FlowTrend();
        LocalDateTime time = TimeUtils.stringToLocalDateTime(bucket, TimeUtils.tzMillsDateTimeFormatter).atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        bo.setTime(time);
        Aggregations bucketAggregations = bucket1;
        ParsedValueCount parsedValueCount = bucketAggregations.get("count");
        bo.setDataSize(parsedValueCount.getValue());
        return bo;
    }

    private ServiceResult<List<FlowTrend>> getResultHistogram(Set<String> indexes, BoolQueryBuilder mergeQuery) {
        SearchSourceBuilder searchBuilder = new SearchSourceBuilder();
        searchBuilder.query(mergeQuery);
        AggregationBuilder aggregationBuilder = AggregationBuilders.dateHistogram("histogram").field("@timestamp")
                .fixedInterval(DateHistogramInterval.seconds(3600))
                .minDocCount(0)
                .subAggregation(AggregationBuilders.count("count").field("@timestamp"));
        searchBuilder.aggregation(aggregationBuilder);
        searchBuilder.size(0);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexes.toArray(new String[]{}));
        searchRequest.source(searchBuilder);
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = searchResponse.getAggregations();
            ParsedDateHistogram parsedTerms = aggregations.get("histogram");
            List<? extends Histogram.Bucket> buckets = parsedTerms.getBuckets();
            List<FlowTrend> flowTrends = new ArrayList<>();
            for (Histogram.Bucket bucket : buckets) {
                FlowTrend flowTrend = getFlowTrend(bucket.getKeyAsString(), bucket.getAggregations());
                flowTrends.add(flowTrend);
            }
            return new ServiceResult<>(flowTrends);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ListObjServiceResult<TaiShiEventInfo> getResult(int page, int pageSize, BatchSceneHandler sceneHandler, Set<String> indexes, BoolQueryBuilder mergeQuery) {
        SearchSourceBuilder searchBuilder = new SearchSourceBuilder();
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
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        List<TaiShiEventInfo> result = new ArrayList<>();
        int total = 0;
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            total = (int) response.getHits().getTotalHits().value;
            Map<String, SceneElementBO> mac2Elements = sceneHandler.getAllMac2ElementMapForScene();
            Map<String, SceneElementBO> eid2Elements = sceneHandler.getAllEid2ElementMap();
            for (SearchHit hit : response.getHits().getHits()) {
                if (hit.getSourceAsMap().containsKey("agent")) {
                    WazuhEventData eventData = JsonCommon.snakeMapToBean(hit.getSourceAsMap(), WazuhEventData.class);
                    TaiShiEventInfo taishi = this.getEventInfoFrom(eventData, eid2Elements);
                    result.add(taishi);
                } else {
                    FlowEventData eventData = JsonCommon.mapToBean(hit.getSourceAsMap(), FlowEventData.class);
                    eventData.setTimestamp(hit.getSourceAsMap().get("@timestamp").toString());
                    TaiShiEventInfo taishi = this.getEventInfoFrom(eventData, mac2Elements);
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
        LocalDateTime end = request.getEndTime() == null ? sceneHandler.getLatestSceneRunningTime() : request.getEndTime();

        // 取探针数据索引
        List<String> indices = dataCollectionService.indexListBuilder(start, end);

        // 探针数据查询条件
        List<String> targetNodeEids = this.getEidsByNodeIds(sceneHandler, request.getTargetNodeIds());
        BoolQueryBuilder agentQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("agent.name", targetNodeEids))
                .must(QueryBuilders.existsQuery("rule.mitre.tactic"));

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(start.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        rangeQueryBuilder.lte(end.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        agentQuery.must(rangeQueryBuilder);

        if (CollectionUtils.isNotEmpty(request.getDefenseTypes())) {
            List<String> types = new ArrayList<>();
            for (String d : request.getDefenseTypes()) {
                String formatType = FlowAttackTypeConvert.getFormatType(d);
                types.add(formatType);
            }
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
        if (handler == null) {
            return null;
        }
        String flowIndex = handler.getScene().getFlowCollector();
        if (StringUtils.isEmpty(flowIndex)) {
            return null;
        }
        flowIndex = flowIndex + "_session*";

        // 流量查询条件
        BoolQueryBuilder flowQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.existsQuery("suricata"));
        // 流量数据既有源也有目的 可以使用ip来查下
        List<String> sourceIps = this.getIpsByNodeIds(sceneHandler, request.getSourceNodeIds());
        List<String> targetIps = this.getIpsByNodeIds(sceneHandler, request.getTargetNodeIds());
        flowQuery.must(QueryBuilders.termsQuery("source.ip", sourceIps));
        flowQuery.must(QueryBuilders.termsQuery("destination.ip", targetIps));
        if (CollectionUtils.isNotEmpty(request.getDefenseTypes())) {
            List<String> attackTypes = new ArrayList<>();
            for (String defenseType : request.getDefenseTypes()) {
                attackTypes.addAll(FlowAttackTypeConvert.getAttackTypes(FlowAttackTypeConvert.getFormatType(defenseType)));
            }
            flowQuery.must(QueryBuilders.termsQuery("suricata.category", attackTypes));
        }

        if (CollectionUtils.isNotEmpty(request.getEventName())) {
            // 加了 event name 条件,默认查不到数据
            flowQuery.must(QueryBuilders.termsQuery("rule.mitre.tactic", Arrays.asList(TextUtils.uuid())));
        }
        LocalDateTime start = request.getStartTime() == null ? sceneHandler.getEarliestStartTime() : request.getStartTime();
        LocalDateTime end = request.getEndTime() == null ? sceneHandler.getLatestSceneRunningTime() : request.getEndTime();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(start.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        rangeQueryBuilder.lte(end.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        flowQuery.must(rangeQueryBuilder);

        EsEventRequestParsedResult result = new EsEventRequestParsedResult();
        result.setIndexes(Arrays.asList(flowIndex));
        result.setQuery(flowQuery);
        return result;
    }

    public ServiceResult<List<FlowTrend>> getEventTiming(TaiShiRequest request) {
        List<String> sceneIds = new ArrayList<>();
        if (StringUtils.isNotEmpty(request.getSceneId()) && request.getSceneId().contains(",")) {
            String[] ids = request.getSceneId().split(",");
            List<String> sceneIdsTmp = csceneCommonService.translateSceneIds(Arrays.asList(ids));
            sceneIds.addAll(sceneIdsTmp);
        } else {
            List<String> sceneIdsTmp = csceneCommonService.translateSceneIds(Arrays.asList(request.getSceneId()));
            sceneIds.addAll(sceneIdsTmp);
        }
        if (CollectionUtils.isEmpty(sceneIds)) {
            return new ServiceResult<>(new ArrayList<>());
        }
        BatchSceneHandler sceneHandler = new BatchSceneHandler(sceneIds, new ArrayList<>());
        CollectionScenePluginHandler handler = sceneHandler.getCollectionScenePluginHandler(sceneIds.get(0));
        if (handler == null) {
            return new ServiceResult<>(new ArrayList<>());
        }

        if (!Boolean.TRUE.equals(request.getHost()) && !Boolean.TRUE.equals(request.getFlow())) {
            return new ServiceResult<>(new ArrayList<>());
        }

        Set<String> indexes = new HashSet<>();
        BoolQueryBuilder mergeQuery = QueryBuilders.boolQuery();

        TaiShiRequest.EventRequest formatDefenseReq = request.getDefense() == null ? new TaiShiRequest.EventRequest() : request.getDefense();
        if (CollectionUtils.isNotEmpty(formatDefenseReq.getDefenseTypes())) {
            List<String> formatDefenseTypes = new ArrayList<>();
            for (String d : formatDefenseReq.getDefenseTypes()) {
                String engageKey = ClassificationConvert.getEngageKey(d);
                formatDefenseTypes.add(engageKey);
            }
            formatDefenseReq.setDefenseTypes(formatDefenseTypes);
        } else {
            formatDefenseReq.setDefenseTypes(ClassificationConvert.getAllEngageKeys());
        }

        TaiShiRequest.EventRequest formatAttCkReq = request.getAttack() == null ? new TaiShiRequest.EventRequest() : request.getAttack();
        if (CollectionUtils.isNotEmpty(formatAttCkReq.getDefenseTypes())) {
            List<String> formatDefenseTypes = new ArrayList<>();
            for (String d : formatAttCkReq.getDefenseTypes()) {
                String attCkKey = ClassificationConvert.getAttCkKey(d);
                formatDefenseTypes.add(attCkKey);
            }
            formatAttCkReq.setDefenseTypes(formatDefenseTypes);
        } else {
            formatAttCkReq.setDefenseTypes(ClassificationConvert.getAllAttCkKeys());
        }

        if (Boolean.TRUE.equals(request.getHost())) {
            EsEventRequestParsedResult hostDefenseResult = this.getHostEsEventRequestParsedResult(sceneHandler, formatDefenseReq);
            EsEventRequestParsedResult hostAttackResult = this.getHostEsEventRequestParsedResult(sceneHandler, formatAttCkReq);
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
            EsEventRequestParsedResult flowDefenseResult = this.getFlowEsEventRequestParsedResult(sceneIds.get(0), sceneHandler, formatDefenseReq);
            EsEventRequestParsedResult flowAttackResult = this.getFlowEsEventRequestParsedResult(sceneIds.get(0), sceneHandler, formatAttCkReq);
            if (flowDefenseResult != null) {
                indexes.addAll(flowDefenseResult.getIndexes());
                mergeQuery.should(flowDefenseResult.getQuery());
            }
            if (flowAttackResult != null) {
                indexes.addAll(flowAttackResult.getIndexes());
                mergeQuery.should(flowAttackResult.getQuery());
            }
        }
        return getResultHistogram(sceneHandler, indexes, mergeQuery);
    }

    public ServiceResult<List<FlowTrend>> getEventTrend(String sceneId) {
        List<String> sceneIdList = csceneCommonService.translateSceneIds(Arrays.asList(sceneId));
        BatchSceneHandler sceneHandler = new BatchSceneHandler(sceneIdList, new ArrayList<>());
        LocalDateTime sceneEndTime = sceneHandler.getLatestSceneRunningTime().plusHours(1);
        int hour = sceneEndTime.toLocalTime().getHour();
        LocalDateTime endTime = LocalDateTime.of(sceneEndTime.toLocalDate(), LocalTime.of(hour, 0, 0));
        LocalDateTime startTime = endTime.minusDays(1);

        TaiShiRequest.EventRequest formatDefenseReq = new TaiShiRequest.EventRequest();
        formatDefenseReq.setDefenseTypes(ClassificationConvert.getAllEngageKeys());
        formatDefenseReq.setStartTime(startTime);
        formatDefenseReq.setEndTime(endTime);
        TaiShiRequest.EventRequest formatAttCkReq = new TaiShiRequest.EventRequest();
        formatAttCkReq.setDefenseTypes(ClassificationConvert.getAllAttCkKeys());
        formatAttCkReq.setStartTime(startTime);
        formatAttCkReq.setEndTime(endTime);

        Set<String> indexes = new HashSet<>();
        BoolQueryBuilder mergeQuery = QueryBuilders.boolQuery();

        EsEventRequestParsedResult hostDefenseResult = this.getHostEsEventRequestParsedResult(sceneHandler, formatDefenseReq);
        EsEventRequestParsedResult hostAttackResult = this.getHostEsEventRequestParsedResult(sceneHandler, formatAttCkReq);
        if (hostDefenseResult != null) {
            indexes.addAll(hostDefenseResult.getIndexes());
            mergeQuery.should(hostDefenseResult.getQuery());
        }
        if (hostAttackResult != null) {
            indexes.addAll(hostAttackResult.getIndexes());
            mergeQuery.should(hostAttackResult.getQuery());
        }

        EsEventRequestParsedResult flowDefenseResult = this.getFlowEsEventRequestParsedResult(sceneIdList.get(0), sceneHandler, formatDefenseReq);
        EsEventRequestParsedResult flowAttackResult = this.getFlowEsEventRequestParsedResult(sceneIdList.get(0), sceneHandler, formatAttCkReq);
        if (flowDefenseResult != null) {
            indexes.addAll(flowDefenseResult.getIndexes());
            mergeQuery.should(flowDefenseResult.getQuery());
        }
        if (flowAttackResult != null) {
            indexes.addAll(flowAttackResult.getIndexes());
            mergeQuery.should(flowAttackResult.getQuery());
        }

        List<FlowTrend> result = this.getResultHistogram(indexes, mergeQuery).getSource();
        Map<String, FlowTrend> resultMap = new HashMap<>();
        for (FlowTrend r : result) {
            resultMap.put(TimeUtils.localDateTimeToUtcString(r.getTime()), r);
        }

        List<FlowTrend> trends = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            LocalDateTime time = startTime.plusHours(i);
            FlowTrend flowTrend = resultMap.get(TimeUtils.localDateTimeToUtcString(time));
            if (flowTrend == null) {
                flowTrend = new FlowTrend();
                flowTrend.setTime(time);
                flowTrend.setDataSize(0L);
            }
            trends.add(flowTrend);
        }
        return new ServiceResult<>(trends);
    }

    private List<FlowTrend> getFlowEventTrend(String[] sceneIds, LocalDateTime startTime, LocalDateTime endTime, int segmentCount) {
        FlowTrendRequest request = new FlowTrendRequest();
        request.setSegmentCount(segmentCount);
        request.setSceneId(StringUtils.join(sceneIds, ","));
        request.setType(NumberUtils.INTEGER_ONE);
        request.setEndTime(endTime);
        request.setStartTime(startTime);
        List<FlowTrend> flows = flowMonitorService.getSceneFlowTrend(request);
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
        List<IntervalSizeBO<LocalDateTime, Long>> hosts = hostStateService.getSceneAttCkTypeCountTrend(elementRequest, startTime, endTime).getSource();
        for (IntervalSizeBO<LocalDateTime, Long> bo : hosts) {
            if (Objects.nonNull(bo.getStart())) {
                FlowTrend flowTrend = getClosest(bo.getStart(), flowTrendMap);
                if (Objects.nonNull(flowTrend)) {
                    flowTrend.setDataSize(bo.getSize() + flowTrend.getDataSize());
                }
            }
        }

        List<FlowTrend> hostTrends = new ArrayList<>();
        for (Map.Entry<LocalDateTime, FlowTrend> entry : flowTrendMap.entrySet()) {
            hostTrends.add(entry.getValue());
        }
        hostTrends.sort(Comparator.comparing(FlowTrend::getTime));
        return hostTrends;
    }

    public ServiceResult<TaiShiTotalCount> getAttCkEventCount(String sceneId) {
        TaiShiTotalCount taiShiTotalCount = new TaiShiTotalCount();
        taiShiTotalCount.setFlow(this.flowTotalCount(sceneId));
        taiShiTotalCount.setHost(this.hostTotalCount(sceneId));
        return new ServiceResult<>(taiShiTotalCount);
    }

    public ServiceResult<List<FlowAttackTypeCount>> getAttCkEventGroupCount(String sceneId) {
        Map<String, FlowAttackTypeCount> result = new HashMap<>();

        AttackTypeTopRequest attackTypeTopRequest = new AttackTypeTopRequest();
        attackTypeTopRequest.setSceneId(sceneId);
        attackTypeTopRequest.setAttackTypeI18(false);
        List<FlowAttackTypeCount> attackTypeCounts = flowMonitorService.getAttackTypeCount(attackTypeTopRequest);
        if (CollectionUtils.isNotEmpty(attackTypeCounts)) {
            for (FlowAttackTypeCount f : attackTypeCounts) {
                String newType = FlowAttackTypeConvert.getAttCk(f.getAttackType());
                String key = newType.toLowerCase();
                FlowAttackTypeCount count = new FlowAttackTypeCount();
                count.setTimes(f.getTimes());
                count.setAttackType(key);
                result.put(count.getAttackType(), count);
            }
        }

        BaseSceneElementRequest elementRequest = new BaseSceneElementRequest();
        elementRequest.setSceneId(sceneId);
        List<AttCkTypeCount> list = hostStateService.getSceneAttCkTypeCountInfo(elementRequest).getSource().getList();
        if (CollectionUtils.isNotEmpty(list)) {
            for (AttCkTypeCount a : list) {
                String key = a.getType().toLowerCase();
                if (!result.containsKey(key)) {
                    FlowAttackTypeCount count = new FlowAttackTypeCount();
                    count.setTimes(a.getTimes().intValue());
                    count.setAttackType(key);
                    result.put(count.getAttackType(), count);
                } else {
                    FlowAttackTypeCount count = result.get(key);
                    count.setTimes(count.getTimes() + a.getTimes().intValue());
                }
            }
        }
        List<FlowAttackTypeCount> sortedResult = new ArrayList<>(result.values());
        Collections.sort(sortedResult, (a1, a2) -> a2.getTimes() - a1.getTimes());
        for (FlowAttackTypeCount s : sortedResult) {
            String key2 = s.getAttackType().replaceAll(" ", "_");
            String attackTypeName = MessageCommon.get("cscene.attck." + key2);
            s.setAttackType(attackTypeName);
        }
        return new ServiceResult<>(sortedResult);
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
        if (CollectionUtils.isNotEmpty(eventData.getRule().getGroups()) && eventData.getRule().getGroups().contains("syscheck_registry")) {
            taiShi.setHostEventType(FileEventType.REGISTRY.toString());
        } else {
            taiShi.setHostEventType(FileEventType.FILE.toString());
        }
        taiShi.setHostEventDesc(StringUtils.join(eventData.getRule().getMitre().getTechnique(), ","));
        taiShi.setTime(TimeUtils.dateToLocalDateTime(eventData.getTimestamp()));
        if (eventData.getAgent() != null && eid2Elements.containsKey(eventData.getAgent().getName())) {
            SceneElementBO element = eid2Elements.get(eventData.getAgent().getName());
            taiShi.setTargetNodeName(element.getName());
            taiShi.setTargetNodeIp(eventData.getAgent().getIp());
            taiShi.setTargetNodeId(element.getId());
        }
        return taiShi;
    }

    private TaiShiEventInfo getEventInfoFrom(FlowEventData eventData, Map<String, SceneElementBO> mac2ElementMap) {
        TaiShiEventInfo taiShi = new TaiShiEventInfo();
        taiShi.setActionType("attack");
        taiShi.setTriggerType("flow");
        taiShi.setTime(TimeUtils.timestampToLocalDateTime(Long.parseLong(eventData.getTimestamp())));
        if (eventData.getSource() != null) {
            String mac = StringUtils.EMPTY;
            if (CollectionUtils.isNotEmpty(eventData.getSource().getMac())) {
                mac = eventData.getSource().getMac().get(0);
            }
            SceneElementBO element = this.getElementBy(mac, mac2ElementMap);
            if (element != null) {
                taiShi.setSourceNodeId(element.getId());
                taiShi.setSourceNodeIp(eventData.getSource().getIp());
                taiShi.setSourceNodeName(element.getName());
                taiShi.setSourceNodePort(eventData.getSource().getPort());
            }
        }
        if (eventData.getDestination() != null) {
            String mac = StringUtils.EMPTY;
            if (CollectionUtils.isNotEmpty(eventData.getSource().getMac())) {
                mac = eventData.getSource().getMac().get(0);
            }
            SceneElementBO element = this.getElementBy(mac, mac2ElementMap);
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

    private SceneElementBO getElementBy(String ip, Map<String, SceneElementBO> ip2ElementMap) {
        if (StringUtils.isBlank(ip)) {
            return null;
        }
        return ip2ElementMap.get(ip);
    }

    private List<String> getAttCkTypeNames(List<String> attCks) {
        if (CollectionUtils.isEmpty(attCks)) {
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>();
        for (String a : attCks) {
            String attCkTypeName = this.getAttCkTypeName(a);
            list.add(attCkTypeName);
        }
        return list;
    }

    private String getAttCkTypeName(String attCk) {
        String key = attCk.replaceAll(" ", "_");
        return MessageCommon.get("cscene.attck." + key.toLowerCase());
    }

    public List<String> getAttCksByFlowAttackTypes(List<String> flowAttackTypes) {
        List<String> result = new ArrayList<>();
        for (String a : flowAttackTypes) {
            String attCk = this.getAttCkByFlowAttackType(a);
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
                for (String nodeId : nodeIds) {
                    List<String> ips = sceneHandler.getIpsByNodeId(nodeId);
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
            for (String nodeId : nodeIds) {
                SceneElementBO node = sceneHandler.getNodeByNodeId(nodeId);
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

    private static class ClassificationConvert {
        private static ClassificationInfoContainer CONVERT_INFO;

        private static Map<String, ClassificationInfo> KEY_2_ATT_CK_INFO = new HashMap<>();

        private static Map<String, ClassificationInfo> KEY_2_ENGAGE_INFO = new HashMap<>();

        private static Map<String, String> ALIAS_2_ATT_CK_KEY = new HashMap<>();

        private static Map<String, String> ALIAS_2_ENGAGE_KEY = new HashMap<>();

        private synchronized static void init() {
            if (CONVERT_INFO == null) {
                try {
                    org.springframework.core.io.Resource[] resources = new PathMatchingResourcePatternResolver()
                            .getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "config/classification.json");
                    String json = FileUtils.readLine(resources);
                    CONVERT_INFO = JsonCommon.snakeParse(json, ClassificationInfoContainer.class);
                } catch (Exception error) {
                    log.error("failed to parse classification info", error);
                }
                if (CollectionUtils.isNotEmpty(CONVERT_INFO.attCk)) {
                    for (ClassificationInfo a : CONVERT_INFO.attCk) {
                        KEY_2_ATT_CK_INFO.put(a.key, a);
                        if (a.alias != null) {
                            for (String alia : a.alias) {
                                ALIAS_2_ATT_CK_KEY.put(alia, a.key);
                            }
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(CONVERT_INFO.engage)) {
                    for (ClassificationInfo a : CONVERT_INFO.engage) {
                        KEY_2_ENGAGE_INFO.put(a.key, a);
                        if (a.alias != null) {
                            for (String alia : a.alias) {
                                ALIAS_2_ENGAGE_KEY.put(alia, a.key);
                            }
                        }
                    }
                }
            }
        }

        public static String getAttCkKey(String alia) {
            if (ALIAS_2_ATT_CK_KEY.containsKey(alia)) {
                return ALIAS_2_ATT_CK_KEY.get(alia);
            }
            return ALIAS_2_ATT_CK_KEY.getOrDefault(alia.toLowerCase(), alia);
        }

        public static String getEngageKey(String alia) {
            if (ALIAS_2_ENGAGE_KEY.containsKey(alia)) {
                return ALIAS_2_ENGAGE_KEY.get(alia);
            }
            return ALIAS_2_ENGAGE_KEY.getOrDefault(alia.toLowerCase(), alia);
        }

        public static List<String> getAllAttCkKeys() {
            if (CONVERT_INFO == null) {
                init();
            }
            return new ArrayList<>(KEY_2_ATT_CK_INFO.keySet());
        }

        public static List<String> getAllEngageKeys() {
            if (CONVERT_INFO == null) {
                init();
            }
            return new ArrayList<>(KEY_2_ENGAGE_INFO.keySet());
        }

    }

    @Getter
    @Setter
    private static class ClassificationInfoContainer {
        private List<ClassificationInfo> attCk;
        private List<ClassificationInfo> engage;
    }

    @Getter
    @Setter
    private static class ClassificationInfo {
        private String typeCn;
        private String typeEn;
        private String key;
        private List<String> alias;
    }

}
