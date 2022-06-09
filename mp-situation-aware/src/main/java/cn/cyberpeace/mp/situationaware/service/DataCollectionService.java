package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.situationaware.config.EsDateFormatConfig;
import cn.cyberpeace.mp.situationaware.config.SituationCacheConfig;
import cn.cyberpeace.mp.situationaware.constant.SicLaunchConfig;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.model.DataCollectionEventModel;
import cn.cyberpeace.mp.situationaware.model.FileEventModel;
import cn.cyberpeace.mp.situationaware.model.WazuhEnvVarEventModel;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.model.WazuhHostStateModel;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorRoot;
import cn.cyberpeace.mp.situationaware.model.WazuhNetworkConnectionModel;
import cn.cyberpeace.mp.situationaware.model.WazuhProbeCommonModel;
import cn.cyberpeace.mp.situationaware.model.WazuhVulnerabilityInfoCountModel;
import cn.cyberpeace.mp.situationaware.model.WazuhVulnerabilityInfoModel;
import cn.cyberpeace.mp.situationaware.model.enums.DataCollectionType;
import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
import cn.cyberpeace.mp.situationaware.model.sic.BaseChangeCountOutput;
import cn.cyberpeace.mp.situationaware.model.sic.BaseInput;
import cn.cyberpeace.mp.situationaware.model.sic.HostStateOutput;
import cn.cyberpeace.mp.situationaware.model.sic.Launch;
import cn.cyberpeace.mp.situationaware.repository.WazuhRepository;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant.ES_DOC_MAXCOUNT;

/**
 * wazuh对接
 */
@Service
@Slf4j
@CacheConfig(cacheManager = SituationCacheConfig.CACHE_MANAGER)
public class DataCollectionService {

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private WazuhRepository wazuhRepository;

    private static final String WAZUH_ALERT_INDEX_PREFIX = "wazuh-alerts-4.x-";

    private static final String CUSTOM_PROBE_ID_PATH = "data.sic_launch_id";


    public static final String CACHE_KEY_FILE_EVENTS = "file_events";

    public static final String CACHE_KEY_MONITOR_EVENTS = "monitor_events";

    public static final String CACHE_KEY_HOST_STATE = "host_states";

    public static final String CACHE_KEY_NET_CONN = "net_conn";

    public static final String CACHE_KEY_ENV_VAR_CHANGED_COUNT = "env_variable_changed_count";

    public static final String CACHE_KEY_SYSTEM_USERS_CHANGED_COUNT = "sys_users_changed_count";

    public static final String CACHE_KEY_PROCESS_CHANGED_COUNT = "processes_changed_count";

    public static final String CACHE_KEY_NET_CONNS_CHANGED_COUNT = "net_conns_changed_count";

    public static final String CACHE_KEY_AGGS_BY_AGENTNAME = "aggs_by_agentname";

    public static final String CACHE_KEY_AGGS_RULEIDS_BY_AGENTNAME = "aggs_ruleids_by_agentname";

    public static final String CACHE_KEY_COUNT_BY_AGENTNAME = "count_by_agentname";


    /**
     * 查询文件事件分页
     *
     * @param wazuhEventRequest
     * @return ListObjResult<FileEventModel>
     */
    @Cacheable(value = CACHE_KEY_FILE_EVENTS)
    public ListObjResult<FileEventModel> queryFileEvents(WazuhEventRequest wazuhEventRequest) {
        log.info("{}", wazuhEventRequest);
        if (isAgentNamesEmpty(wazuhEventRequest.getAgentNames())) {
            return new ListObjResult<>(Collections.emptyList(), 0);
        }
        //根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhEventRequest.getStartTime(), wazuhEventRequest.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

//        MultiValueMap<String, String> multiValueMap = this.queryAllAgentMacAddr();
        //构造查询条件
        buildFileEventBoolQueryBuilder(wazuhEventRequest, boolQueryBuilder, null);
        // 按时间降序
        sourceBuilder.sort("timestamp", SortOrder.DESC);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));

        if (wazuhEventRequest.isScroll()) {
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(35L));
            searchRequest.scroll(scroll);
        }
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        //处理分页
        int total = processPageable(wazuhEventRequest, indexArray, boolQueryBuilder, sourceBuilder);

        log.info("elasticsearch JSON query index:{}, DSL:\n{}", indexArray, searchRequest.source().toString());
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<WazuhFileRoot> wazuhFileRootList = Arrays.stream(response.getHits().getHits()).map(hit -> JSONObject.parseObject(hit.getSourceAsString(), WazuhFileRoot.class)).collect(Collectors.toList());
            List<FileEventModel> fileEventModelList = wazuhFileRootList.stream().map((root -> this.getFileEventModel(root, null))).collect(Collectors.toList());

            String responseScrollId = response.getScrollId();
            if (wazuhEventRequest.isScroll()) {
                log.info("response scrollId:{}", responseScrollId);
                fileEventModelList.forEach(model -> model.setScrollId(responseScrollId));
            }

            return new ListObjResult<>(fileEventModelList, total);
        } catch (IOException e) {
            log.error("es queryFileEvents error: {} ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }


    /**
     * 滚动分页查询文件事件
     *
     * @return ListObjResult<FileEventModel>
     */
    public ListObjResult<FileEventModel> scrollFileEvents(String scrollId) {
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(35L));
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(scroll);
        log.info("elasticsearch scroll query, scrollId:\n{}", scrollId);
        try {
            SearchResponse response = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
            List<WazuhFileRoot> wazuhFileRootList = Arrays.stream(response.getHits().getHits()).map(hit -> JSONObject.parseObject(hit.getSourceAsString(), WazuhFileRoot.class)).collect(Collectors.toList());
            List<FileEventModel> fileEventModelList = wazuhFileRootList.stream().map((root -> this.getFileEventModel(root, null))).collect(Collectors.toList());

            String responseScrollId = response.getScrollId();
            fileEventModelList.forEach(model -> model.setScrollId(responseScrollId));
            return new ListObjResult(fileEventModelList, Long.valueOf(response.getHits().getTotalHits().value).intValue());
        } catch (IOException e) {
            log.error("es scrollFileEvents error: {} ", ExceptionUtil.stacktraceToString(e));
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }

    }

    /**
     * 查询自定义探针采集数据分页
     *
     * @param wazuhEventRequest
     * @param probeIds 自定义探针id集合
     * @return ListObjResult<WazuhProbeCommonModel>
     */
    public ListObjResult<WazuhProbeCommonModel> queryCustomProbeData(WazuhEventRequest wazuhEventRequest, List<String> probeIds) {
        log.info("{}", wazuhEventRequest);
        if (isAgentNamesEmpty(wazuhEventRequest.getAgentNames())) {
            return new ListObjResult<>(Collections.emptyList(), 0);
        }
        // 根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhEventRequest.getStartTime(), wazuhEventRequest.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        // 构造查询条件
        buildFileEventBoolQueryBuilder(wazuhEventRequest, boolQueryBuilder, null);
        if (!CollUtil.isEmpty(probeIds)) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery(CUSTOM_PROBE_ID_PATH, probeIds));
        }
        // 按时间降序
        sourceBuilder.sort("timestamp", SortOrder.DESC);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        // 配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        // 处理分页
        int total = processPageable(wazuhEventRequest, indexArray, boolQueryBuilder, sourceBuilder);

        log.info("elasticsearch JSON query index:{}, DSL:\n{}", indexArray, searchRequest.source().toString());
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<WazuhProbeCommonModel> wazuhModelList = new ArrayList<>();
            for (var hit : response.getHits().getHits()) {
                WazuhMonitorRoot<Launch<Object, BaseInput>> wazuhMonitorRoot = JSON.parseObject(hit.getSourceAsString(),
                        new TypeReference<>() {
                        });
                if (Objects.nonNull(wazuhMonitorRoot.getData()) && Objects.nonNull(wazuhMonitorRoot.getData().getOutput())) {
                    String output = JsonCommon.stringify(wazuhMonitorRoot.getData().getOutput());
                    WazuhProbeCommonModel wazuhProbeCommonModel = new WazuhProbeCommonModel();
                    wazuhProbeCommonModel.setRawOutPut(output);
                    wazuhProbeCommonModel.setAgentId(wazuhMonitorRoot.getAgent().getId());
                    wazuhProbeCommonModel.setAgentName(wazuhMonitorRoot.getAgent().getName());
                    wazuhProbeCommonModel.setTime(TimeUtils.dateToLocalDateTime(wazuhMonitorRoot.getTimestamp()));
                    wazuhProbeCommonModel.setProbeId(wazuhMonitorRoot.getData().getId());
                    wazuhModelList.add(wazuhProbeCommonModel);
                }
            }
            return new ListObjResult<>(wazuhModelList, total);
        } catch (IOException e) {
            log.error("es queryCustomProbeData error", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 查询漏洞信息分页
     *
     * @param wazuhEventRequest
     * @param desc 是否按时间降序
     * @return ListObjResult<WazuhVulnerabilityInfoModel>
     */
    public ListObjResult<WazuhVulnerabilityInfoModel> queryVulnerabilityInfo(WazuhEventRequest wazuhEventRequest, boolean desc) {
        log.info("{}", wazuhEventRequest);
        if (isAgentNamesEmpty(wazuhEventRequest.getAgentNames())) {
            return new ListObjResult<>(Collections.emptyList(), 0);
        }
        // 根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhEventRequest.getStartTime(), wazuhEventRequest.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        // 构造查询条件
        buildFileEventBoolQueryBuilder(wazuhEventRequest, boolQueryBuilder, null);
        if (desc) {
            // 按时间降序
            sourceBuilder.sort("timestamp", SortOrder.DESC);
        } else {
            // 按时间顺序
            sourceBuilder.sort("timestamp", SortOrder.ASC);
        }
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        // 配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        // 处理分页
        int total = processPageable(wazuhEventRequest, indexArray, boolQueryBuilder, sourceBuilder);

        log.info("elasticsearch JSON query index:{}, DSL:\n{}", indexArray, searchRequest.source().toString());
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<WazuhVulnerabilityInfoModel> wazuhModelList = new ArrayList<>();
            for (var hit : response.getHits().getHits()) {
                WazuhMonitorRoot<WazuhVulnerabilityInfoModel> wazuhMonitorRoot = JSON.parseObject(hit.getSourceAsString(),
                        new TypeReference<>() {
                        });
                if (Objects.nonNull(wazuhMonitorRoot.getData())) {
                    WazuhVulnerabilityInfoModel vulnerability = new WazuhVulnerabilityInfoModel();
                    vulnerability.setVulnerability(wazuhMonitorRoot.getData().getVulnerability());
                    vulnerability.setAgentId(wazuhMonitorRoot.getAgent().getId());
                    vulnerability.setAgentName(wazuhMonitorRoot.getAgent().getName());
                    vulnerability.setTime(TimeUtils.dateToLocalDateTime(wazuhMonitorRoot.getTimestamp()));
                    wazuhModelList.add(vulnerability);
                }
            }
            return new ListObjResult<>(wazuhModelList, total);
        } catch (IOException e) {
            log.error("es queryVulnerabilityInfo error:", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }


    /**
     * 查询漏洞信息数量
     *
     * @param wazuhEventRequest
     * @param isTop 是否获取前十条数据
     * @param fieldPath 字段所在路径
     * @param fieldName 字段名称
     * @return List<WazuhVulnerabilityInfoCountModel>
     */
    public List<WazuhVulnerabilityInfoCountModel> queryVulnerabilityCountInfo(WazuhEventRequest wazuhEventRequest,
                                                                              boolean isTop, String fieldPath, String fieldName) {
        log.info("{}", wazuhEventRequest);
        if (isAgentNamesEmpty(wazuhEventRequest.getAgentNames())) {
            return new ArrayList<>();
        }
        // 根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhEventRequest.getStartTime(), wazuhEventRequest.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        // 构造查询条件
        buildFileEventBoolQueryBuilder(wazuhEventRequest, boolQueryBuilder, null);
        sourceBuilder.aggregation(AggregationBuilders.terms(fieldName).field(fieldPath));
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        if (isTop) {
            // 获取前十条的数据
            sourceBuilder.from(0).size(10);
        }
        // 配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        log.info("elasticsearch JSON query index:{}, DSL:\n{}", indexArray, searchRequest.source().toString());
        List<WazuhVulnerabilityInfoCountModel> vulnerabilityInfoCounts;
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            long totalHits = response.getHits().getTotalHits().value;
            if (totalHits == 0) {
                return new ArrayList<>();
            }
            Aggregations aggregations = response.getAggregations();
            ParsedTerms parsedTerms = aggregations.get(fieldName);
            List<? extends Terms.Bucket> buckets = parsedTerms.getBuckets();
            vulnerabilityInfoCounts = buckets.stream().map(bucket -> {
                WazuhVulnerabilityInfoCountModel countModel = new WazuhVulnerabilityInfoCountModel();
                countModel.setFieldName(bucket.getKeyAsString());
                countModel.setCount(bucket.getDocCount());
                return countModel;
            }).collect(Collectors.toList());
            return vulnerabilityInfoCounts;
        } catch (IOException e) {
            log.error("es queryVulnerabilityCountInfo error:", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 查询监控数据 查询最近五分钟数据，监控指标频率为 1min, 对五分钟数据按照agentId进行分组，再去最近的时间的数据
     *
     * @param wazuhMonitorEventsRequest wazuhMonitorEventsRequest
     */
    @Cacheable(value = CACHE_KEY_MONITOR_EVENTS)
    public ListObjResult<WazuhHostStateModel> queryMonitorEvents(WazuhMonitorEventsRequest wazuhMonitorEventsRequest) {
        if (isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return new ListObjResult<>(Collections.emptyList(), 0);
        }
        //根据日期构造索引
        String[] indexArray = buildRequestIndex(LocalDateTime.now(), LocalDateTime.now());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

//        MultiValueMap<String, String> multiValueMap = this.queryAllAgentMacAddr();
        int agentSize = buildMonitorEventsBuilder(wazuhMonitorEventsRequest, boolQueryBuilder, null);
        // 按时间降序
        sourceBuilder.sort("timestamp", SortOrder.DESC);
        // 监控数据每个agent一条，所以这里分页大小设为agent数量的2倍
        sourceBuilder.from(0);
        sourceBuilder.size(NumberUtil.equals(agentSize, 0) ? 10 : agentSize * 2);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        log.info("monitor JSON query index:{}, DSL: {}", indexArray, searchRequest.source().toString());

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            List<WazuhMonitorRoot> wazuhFileRootList = Arrays.stream(response.getHits().getHits()).map(hit -> JSONObject.parseObject(hit.getSourceAsString(), WazuhMonitorRoot.class)).collect(Collectors.toList());
            Map<String, Optional<WazuhMonitorRoot>> wazuhMonitorMap =
                wazuhFileRootList.stream()
                    .filter(wazuhMonitorRoot -> wazuhMonitorRoot.getFullLog().contains("MonitorMessage:"))
                    .sorted(Comparator.comparing(WazuhMonitorRoot::getTimestamp))
                    .collect(Collectors.groupingBy(root -> root.getAgent().getId(),
                        Collectors.maxBy(Comparator.comparing(WazuhMonitorRoot::getTimestamp))));

            wazuhFileRootList = wazuhMonitorMap.values().stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
            var wazuhModelList = wazuhFileRootList.stream()
                .filter(model -> model.getFullLog().contains("MonitorMessage:")).map(model -> {
                    var monitorMessage = model.getFullLog().substring("MonitorMessage:".length());
                    var result = JSONObject.parseObject(monitorMessage, WazuhHostStateModel.class);
                    result.setAgentId(model.getAgent().getId());
                    result.setAgentName(model.getAgent().getName());
//                    result.setMacIds(multiValueMap.get(model.getAgent().getId()));
                    result.setTime(TimeUtils.dateToLocalDateTime(model.getTimestamp()));
                    return result;
                }).collect(Collectors.toList());

            //process page
            int offset = (wazuhMonitorEventsRequest.getPageNum() - 1) * wazuhMonitorEventsRequest.getPageSize();
            if (offset < wazuhModelList.size()) {
                int toIndex = Math.min(offset + wazuhMonitorEventsRequest.getPageSize(), wazuhModelList.size());
                return new ListObjResult<>(wazuhModelList.subList(offset, toIndex), wazuhModelList.size());
            } else {
                return new ListObjResult<>(Lists.newArrayList(), wazuhModelList.size());
            }
        } catch (IOException e) {
            log.error("es 查询监控事件分页失败 {}", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 查询聚合分析监控事件
     * @param wazuhMonitorEventsRequest wazuhMonitorEventsRequest
     */
    @Deprecated
    public List<DataCollectionEventModel> queryMonitorEventsAgg(WazuhMonitorEventsRequest wazuhMonitorEventsRequest){
        if (isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return Collections.emptyList();
        }
        //根据日期构造索引
        String[] indexArray = buildRequestIndex(LocalDateTime.now(), LocalDateTime.now());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

//        MultiValueMap<String, String> multiValueMap = this.queryAllAgentMacAddr();
        buildMonitorEventsAggBuilder(wazuhMonitorEventsRequest, boolQueryBuilder, null);
        // 按时间降序
        sourceBuilder.sort("timestamp", SortOrder.DESC);
        // 监控数据每个agent一条，所以这里分页大小设为agent数量的2倍
        sourceBuilder.from(0);
        sourceBuilder.size(ES_DOC_MAXCOUNT);

        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        log.info("queryMonitorEventsAgg JSON query index:{}, DSL:{}", indexArray, searchRequest.source().toString());

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<WazuhMonitorRoot> wazuhMonitorRootList = Arrays.stream(response.getHits().getHits()).map(hit -> JSONObject.parseObject(hit.getSourceAsString(), WazuhMonitorRoot.class)).collect(Collectors.toList());
            return wazuhMonitorRootList.stream().map(root -> {
                DataCollectionEventModel dataCollectionEventModel = new DataCollectionEventModel();
                dataCollectionEventModel.setDataCollectionType(DataCollectionType.getTypeById(root.getRule().getId()));
                dataCollectionEventModel.setEventDes(dataCollectionEventModel.getDataCollectionType().getEventDes());
                dataCollectionEventModel.setAgentName(root.getAgent().getName());
                dataCollectionEventModel.setTime(root.getTimestamp());
                return dataCollectionEventModel;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_EVENT_ERROR.getMessage() + "{}", ExceptionUtils.getMessage(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_EVENT_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }


    /**
     * 查询监控数据 查询最近五分钟数据，监控指标频率为 1min, 对五分钟数据按照agentId进行分组，再去最近的时间的数据
     *
     * @param wazuhMonitorEventsRequest wazuhMonitorEventsRequest
     */
    @Cacheable(value = CACHE_KEY_HOST_STATE)
    public List<WazuhHostStateModel> queryHostStateEventsWithTimeRange(WazuhMonitorEventsRequest wazuhMonitorEventsRequest) {
        if (isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return Collections.emptyList();
        }
        SearchResponse response = querySICLaunchEventsWithTimeRange(wazuhMonitorEventsRequest, SicLaunchConfig.LAUNCH_ID_HOST_STATE);
        List<WazuhMonitorRoot<Launch<HostStateOutput, BaseInput>>> wazuhFileRootList = Arrays.stream(response.getHits()
                .getHits()).map(hit ->
                JSONObject.parseObject(hit.getSourceAsString(), new TypeReference<WazuhMonitorRoot<Launch<HostStateOutput, BaseInput>>>() {
                })).collect(Collectors.toList());

        var wazuhModelList = wazuhFileRootList.stream().map(model -> {
            if (Objects.nonNull(model.getData()) && Objects.nonNull(model.getData().getOutput())) {
                HostStateOutput output = model.getData().getOutput();
                WazuhHostStateModel wazuhHostStateModel = convertHostStateOutput2WazuhModel(output);
                wazuhHostStateModel.setAgentId(model.getAgent().getId());
                wazuhHostStateModel.setAgentName(model.getAgent().getName());
//                    result.setMacIds(multiValueMap.get(model.getAgent().getId()));
                wazuhHostStateModel.setTime(TimeUtils.dateToLocalDateTime(model.getTimestamp()));
                return wazuhHostStateModel;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return wazuhModelList;
    }

    @Cacheable(value = CACHE_KEY_NET_CONN)
    public List<WazuhNetworkConnectionModel> queryNetConnEventsWithTimeRange(WazuhMonitorEventsRequest wazuhMonitorEventsRequest) {
        if (isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return Collections.emptyList();
        }
        // 只取一条数据，做假分页处理
        wazuhMonitorEventsRequest.setPageSize(1);

        SearchResponse response = querySICLaunchEventsWithTimeRange(wazuhMonitorEventsRequest, SicLaunchConfig.LAUNCH_ID_NET_CONNS);
        List<WazuhMonitorRoot<Launch<BaseChangeCountOutput, BaseInput>>> wazuhFileRootList = Arrays.stream(response.getHits()
                .getHits()).map(hit ->
                JSONObject.parseObject(hit.getSourceAsString(), new TypeReference<WazuhMonitorRoot<Launch<BaseChangeCountOutput, BaseInput>>>() {
                })).collect(Collectors.toList());

        if (CollectionUtil.isEmpty(wazuhFileRootList)) {
            return Collections.emptyList();
        }

        // 只取最近一条
        WazuhMonitorRoot<Launch<BaseChangeCountOutput, BaseInput>> launchModel = wazuhFileRootList.get(0);
        List<WazuhNetworkConnectionModel> list = new ArrayList<>();
        if (Objects.nonNull(launchModel.getData()) && Objects.nonNull(launchModel.getData().getOutput())) {
            BaseChangeCountOutput changedCntOutput = launchModel.getData().getOutput();
            if (StringUtils.isNotBlank(changedCntOutput.getData())) {
                byte[] raw = TextUtils.rawDecodeBase64(StringUtils.strip(changedCntOutput.getData().trim(), "\""));
                String output = TextUtils.decompress(raw);
                JSONObject jsonObject = JSONObject.parseObject(output);

                JSONArray arr = jsonObject.getJSONArray("values");
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject data = arr.getJSONObject(i);
                    WazuhNetworkConnectionModel netConnModel = JSONObject.toJavaObject(data, WazuhNetworkConnectionModel.class);
                    list.add(netConnModel);
                }
            }
        }

        return list;
    }

    /**
     * 一段时间内的环境变量数变动
     * @param wazuhMonitorEventsRequest
     * @return
     */
    @Cacheable(value = CACHE_KEY_ENV_VAR_CHANGED_COUNT)
    public int sumEnvVarChangedCountByTimeRange(WazuhMonitorEventsRequest wazuhMonitorEventsRequest) {
        if (isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return 0;
        }

        int sum = aggsChangedCountWithTimeRange(wazuhMonitorEventsRequest, SicLaunchConfig.LAUNCH_ID_ENV_VAR);

        log.info("env var changed count: {}", sum);
        return sum;
    }


    /**
     * 一段时间内的用户数量变动
     * @param wazuhMonitorEventsRequest
     * @return
     */
    @Cacheable(value = CACHE_KEY_SYSTEM_USERS_CHANGED_COUNT)
    public int sumSysUsersChangedCountByTimeRange(WazuhMonitorEventsRequest wazuhMonitorEventsRequest) {
        if (isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return 0;
        }

        int sum = aggsChangedCountWithTimeRange(wazuhMonitorEventsRequest, SicLaunchConfig.LAUNCH_ID_SYSTEM_USERS);

        log.info("system users changed count: {}", sum);
        return sum;
    }


    @Cacheable(value = CACHE_KEY_PROCESS_CHANGED_COUNT)
    public int sumProcessesChangedCountByTimeRange(WazuhMonitorEventsRequest wazuhMonitorEventsRequest) {
        if (isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return 0;
        }

        int sum = aggsChangedCountWithTimeRange(wazuhMonitorEventsRequest, SicLaunchConfig.LAUNCH_ID_PROCESSES);
        log.info("processes changed count: {}", sum);
        return sum;
    }

    @Cacheable(value = CACHE_KEY_NET_CONNS_CHANGED_COUNT)
    public int sumNetConnsChangedCountByTimeRange(WazuhMonitorEventsRequest wazuhMonitorEventsRequest) {
        if (isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return 0;
        }

        int sum = aggsChangedCountWithTimeRange(wazuhMonitorEventsRequest, SicLaunchConfig.LAUNCH_ID_NET_CONNS);
        log.info("processes changed count: {}", sum);
        return sum;
    }

    public SearchResponse querySICLaunchEventsWithTimeRange(WazuhMonitorEventsRequest wazuhMonitorEventsRequest, String launchId) {
        //根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhMonitorEventsRequest.getStartTime(), wazuhMonitorEventsRequest.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        buildMonitorEventsBuilderTimeRange(wazuhMonitorEventsRequest, boolQueryBuilder, null);

        if (StringUtils.isNotBlank(launchId)) {
            boolQueryBuilder.must(QueryBuilders.termQuery(CUSTOM_PROBE_ID_PATH, launchId));
        }
        // 按时间降序
        sourceBuilder.sort("timestamp", SortOrder.DESC);
        if (Objects.nonNull(wazuhMonitorEventsRequest.getPageNum()) && Objects.nonNull(wazuhMonitorEventsRequest.getPageSize())) {
            if (wazuhMonitorEventsRequest.getPageNum() * wazuhMonitorEventsRequest.getPageSize() > 10000) {
                throw new MpRuntimeException("不支持深度分页查询！");
            }
            sourceBuilder.from(wazuhMonitorEventsRequest.getPageNum() - 1 > 0 ?
                    (wazuhMonitorEventsRequest.getPageNum() - 1) * wazuhMonitorEventsRequest.getPageSize() : 0);
            sourceBuilder.size(wazuhMonitorEventsRequest.getPageSize());
        } else {
            sourceBuilder.size(ES_DOC_MAXCOUNT);
        }
        // 监控数据每个agent一条，所以这里分页大小设为agent数量的2倍
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        log.info("monitor JSON query index:{}, \n DSL:{}", indexArray, searchRequest.source().toString());

        try {
            log.info("es search start");
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            log.info("es search end");

            return response;
        } catch (IOException e) {
            log.error("es queryMonitorEventsWithTimeRange error: {}", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }


    public int aggsChangedCountWithTimeRange(WazuhMonitorEventsRequest wazuhMonitorEventsRequest, String launchId) {
        if (isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return 0;
        }
        //根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhMonitorEventsRequest.getStartTime(), wazuhMonitorEventsRequest.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        buildMonitorEventsBuilderTimeRange(wazuhMonitorEventsRequest, boolQueryBuilder, null);

        if (StringUtils.isNotBlank(launchId)) {
            boolQueryBuilder.must(QueryBuilders.termQuery(CUSTOM_PROBE_ID_PATH, launchId));
        }


        BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("data.sic_launch_output.changed_count").gt(0L));
        boolQueryBuilder.must(QueryBuilders.nestedQuery("data.sic_launch_output", nestedBoolQueryBuilder, ScoreMode.None));

        String aggregationNested = "aggs_nested";
        String aggregationSum = "aggs_changed_count_sum";
        // build the aggregation
        Script sumScript = new Script("Integer.parseInt(doc['data.sic_launch_output.changed_count'].value)");
        NestedAggregationBuilder sumAggregationBuilder = AggregationBuilders.nested(aggregationNested, "data.sic_launch_output").subAggregation(AggregationBuilders.sum(aggregationSum).script(sumScript));

        sourceBuilder.aggregation(sumAggregationBuilder);

        // 按时间降序
        sourceBuilder.sort("timestamp", SortOrder.DESC);
        sourceBuilder.size(0);
        // 监控数据每个agent一条，所以这里分页大小设为agent数量的2倍
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        log.info("monitor JSON query index:{}, \n DSL:{}", indexArray, searchRequest.source().toString());

        int changedCount = 0;
        try {
            log.info("es search start");
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            if (Objects.nonNull(response.getAggregations())) {
                ParsedNested nested = response.getAggregations().get(aggregationNested);
                ParsedSum sum = nested.getAggregations().get(aggregationSum);
                changedCount = Double.valueOf(sum.getValue()).intValue();
            }

            return changedCount;
        } catch (IOException e) {
            log.error("es queryMonitorEventsWithTimeRange error: {}", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 根据agent查询
     *
     * @param agentId
     * @param macIds
     */
    private String queryMacIdByAgentId(String agentId, List<String> macIds,
        MultiValueMap<String, String> multiValueMap) {
        List<String> ids = multiValueMap.get(agentId);
        // macId集合都转换成小写
        macIds = macIds.stream().map(String::toLowerCase).collect(Collectors.toList());
        var currentMacId = Sets.intersection(Sets.newHashSet(macIds), Sets.newHashSet(ids));
        return currentMacId.iterator().next();
    }

    /**
     * 文件监控时间过滤
     *
     * @param wazuhEventRequest wazuhEventRequest
     * @param boolQueryBuilder boolQueryBuilder
     */
    private int buildMonitorEventsBuilder(WazuhMonitorEventsRequest wazuhEventRequest,
        BoolQueryBuilder boolQueryBuilder, MultiValueMap<String, String> allAgentMacAddr) {
        //构造查询条件
//        boolQueryBuilder.must(QueryBuilders.termQuery("rule.id", "900002"));
        boolQueryBuilder.must(QueryBuilders.termQuery(CUSTOM_PROBE_ID_PATH, SicLaunchConfig.LAUNCH_ID_HOST_STATE));
        int agentSize = 0;

//        if (!CollectionUtils.isEmpty(wazuhEventRequest.getMacIds())) {
//            agentSize = buildAgentIdQuery(wazuhEventRequest.getMacIds(), boolQueryBuilder, allAgentMacAddr);
//        }

        if (!CollectionUtils.isEmpty(wazuhEventRequest.getAgentNames())) {
            buildAgentNameQuery(wazuhEventRequest.getAgentNames(), boolQueryBuilder);
            agentSize = wazuhEventRequest.getAgentNames().size();
        }

        // 返回agent数量
        return agentSize;
    }



    /**
     * 文件监控时间过滤
     *
     * @param wazuhEventRequest wazuhEventRequest
     * @param boolQueryBuilder boolQueryBuilder
     */
    private void buildMonitorEventsBuilderTimeRange(WazuhMonitorEventsRequest wazuhEventRequest,
        BoolQueryBuilder boolQueryBuilder, MultiValueMap<String, String> allAgentMacAddr) {
        //构造查询条件
//        boolQueryBuilder.must(QueryBuilders.termQuery("rule.id", "900002"));

//        if (!CollectionUtils.isEmpty(wazuhEventRequest.getMacIds())) {
//            buildAgentIdQuery(wazuhEventRequest.getMacIds(), boolQueryBuilder, allAgentMacAddr);
//        }

        if (!CollectionUtils.isEmpty(wazuhEventRequest.getAgentNames())) {
            buildAgentNameQuery(wazuhEventRequest.getAgentNames(), boolQueryBuilder);
        }

        if (Objects.nonNull(wazuhEventRequest.getStartTime()) && Objects.nonNull(wazuhEventRequest.getEndTime())) {
            //构造事件条件
            boolQueryBuilder.must(QueryBuilders.rangeQuery("@timestamp")
                    .from(wazuhEventRequest.getStartTime().atZone(ZoneId.systemDefault()).format(EsDateFormatConfig.esDateTimeFormatter))
                    .to(wazuhEventRequest.getEndTime().atZone(ZoneId.systemDefault()).format(EsDateFormatConfig.esDateTimeFormatter)));
        }
    }


    /**
     * 文件监控时间过滤
     *
     * @param wazuhEventRequest wazuhEventRequest
     * @param boolQueryBuilder boolQueryBuilder
     */
    private void buildMonitorEventsAggBuilder(WazuhMonitorEventsRequest wazuhEventRequest,
        BoolQueryBuilder boolQueryBuilder, MultiValueMap<String, String> allAgentMacAddr) {
        //构造查询条件
        List<String> ruleIds = EnumSet.allOf(DataCollectionType.class).stream().map(DataCollectionType::getRuleId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(ruleIds)) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("rule.id", ruleIds));
        }

        if (!CollectionUtils.isEmpty(wazuhEventRequest.getAgentNames())) {
            buildAgentNameQuery(wazuhEventRequest.getAgentNames(), boolQueryBuilder);
        }
    }

    /**
     * 查询所有mac地址对应的
     *
     * @return
     */
    public MultiValueMap<String, String> queryAllAgentMacAddr() {
        MultiValueMap<String, String> responseMultiMap = new LinkedMultiValueMap<>();
        var wazuhNetifaceRoot = wazuhRepository.queryAllAgentMac();
        if (Objects.isNull(wazuhNetifaceRoot.getData())) {
            return responseMultiMap;
        }
        wazuhNetifaceRoot.getData().getAffectedItems().forEach(item -> {
            // 所有的macId都以小写保存
            responseMultiMap.add(item.getAgentId(), Objects.isNull(item.getMac()) ? "" : item.getMac().toLowerCase());
        });

        return responseMultiMap;
    }

    /**
     * 根据mac地质查询AgentIds
     *
     * @param macs
     * @return agentSet
     */
    public Set<String> queryAgentIdsByMac(List<String> macs, MultiValueMap<String, String> allAgentMacAddr) {
        // 转小写macId集合
        List<String> lowerMacs = macs.stream().map(String::toLowerCase).collect(Collectors.toList());
        Set<String> agentSet = Sets.newHashSet();
        allAgentMacAddr.forEach((key, macList) -> {
            if (macList.stream().anyMatch(lowerMacs::contains)) {
                agentSet.add(key);
            }
        });

        return agentSet;
    }

    /**
     * 统计每个节点的采集事件总数，并排序
     * 如：node5:100  node4:85  node3:60  node2:50  node1: 10
     * @param wazuhEventRequest
     * @return
     */
    @Cacheable(value = CACHE_KEY_AGGS_BY_AGENTNAME)
    public Map<String, Long> aggsCountByAgentNamesBucket(WazuhEventRequest wazuhEventRequest) {
        log.info("{}", wazuhEventRequest);
        Map<String, Long> map = new LinkedHashMap<>();
        if (isAgentNamesEmpty(wazuhEventRequest.getAgentNames())) {
            return map;
        }
        String aggregationName = "aggs_count_by_agent_name";
        wazuhEventRequest.getAgentNames().forEach(a -> map.put(a, 0L));

        //根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhEventRequest.getStartTime(), wazuhEventRequest.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(0);

        // build the aggregation
        TermsAggregationBuilder aggregation = AggregationBuilders.terms(aggregationName)
                .field("agent.name");

        sourceBuilder.aggregation(aggregation);

        //构造查询条件
        buildFileEventBoolQueryBuilder(wazuhEventRequest, boolQueryBuilder, null);
        // 按时间降序
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        log.info("elasticsearch aggs request query... index:{}, JSON query:\n{}", indexArray, searchRequest.source().toString());

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            if (Objects.nonNull(response.getAggregations())) {
                Terms terms = response.getAggregations().get(aggregationName);
                terms.getBuckets().forEach(b -> {
                    map.put(b.getKeyAsString(), Long.valueOf(b.getDocCount()));
                });
            }

            return map;

        } catch (IOException e) {
            log.error("es query error: {} ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }


    /**
     * 查询一个节点所有的rule id
     * @param wazuhEventRequest
     * @return
     */
    @Cacheable(value = CACHE_KEY_AGGS_RULEIDS_BY_AGENTNAME)
    public Map<String, Long> aggsRuleIdsByAgentName(WazuhEventRequest wazuhEventRequest) {
        log.info("{}", wazuhEventRequest);
        Map<String, Long> map = new LinkedHashMap<>();
        if (isAgentNamesEmpty(wazuhEventRequest.getAgentNames())) {
            return map;
        }
        String aggregationName = "aggs_ruleids_by_agent_name";
        //wazuhEventRequest.getAgentNames().forEach(a -> map.put(a, 0L));

        //根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhEventRequest.getStartTime(), wazuhEventRequest.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(0);

        // build the aggregation
        TermsAggregationBuilder aggregation = AggregationBuilders.terms(aggregationName)
                .field("rule.id").size(100);

        sourceBuilder.aggregation(aggregation);

        //构造查询条件
        buildFileEventBoolQueryBuilder(wazuhEventRequest, boolQueryBuilder, null);
        // 按时间降序
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        log.info("elasticsearch aggs request query... index:{}, JSON query:\n{}", indexArray, searchRequest.source().toString());

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            if (Objects.nonNull(response.getAggregations())) {
                Terms terms = response.getAggregations().get(aggregationName);
                terms.getBuckets().forEach(b -> {
                    map.put(b.getKeyAsString(), Long.valueOf(b.getDocCount()));
                });
            }
            return map;

        } catch (IOException e) {
            log.error("es query error: {} ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }


    /**
     * 所有节点的采集事件总数
     * @param wazuhEventRequest
     * @return
     */
    @Cacheable(value = CACHE_KEY_COUNT_BY_AGENTNAME)
    public long countByAgentNames(WazuhEventRequest wazuhEventRequest) {
        log.info("{}", wazuhEventRequest);
        if (isAgentNamesEmpty(wazuhEventRequest.getAgentNames())) {
            return 0;
        }

        //根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhEventRequest.getStartTime(), wazuhEventRequest.getEndTime());
        var countRequest = new CountRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        //构造查询条件
        buildFileEventBoolQueryBuilder(wazuhEventRequest, boolQueryBuilder, null);
        // 按时间降序
        countRequest.source(sourceBuilder.query(boolQueryBuilder));
        //配置索引为空
        countRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        log.info("elasticsearch count quest query... index:{}, JSON query:\n{}", indexArray, countRequest.source().toString());

        try {
            CountResponse response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            long count = response.getCount();
            return count;
        } catch (IOException e) {
            log.error("es query error: {} ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 场景删除之后，删除对应es文档数据
     * @param wazuhEventRequest
     */
    public Long deleteDocByAgentNames(WazuhEventRequest wazuhEventRequest) {
        log.info("{}", wazuhEventRequest);
        if (isAgentNamesEmpty(wazuhEventRequest.getAgentNames())) {
            return 0L;
        }
        //根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhEventRequest.getStartTime(), wazuhEventRequest.getEndTime());
        var deleteByQueryRequest = new DeleteByQueryRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();

        //构造查询条件
        buildFileEventBoolQueryBuilder(wazuhEventRequest, boolQueryBuilder, null);
        // 按时间降序
        deleteByQueryRequest.setQuery(boolQueryBuilder);

        //配置索引为空
        deleteByQueryRequest.setIndicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        log.info("elasticsearch delete by query quest... index:{}, JSON query:\n{}", indexArray, deleteByQueryRequest.getSearchRequest().source().toString());

        try {
            BulkByScrollResponse response = restHighLevelClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
            return response.getUpdated();
        } catch (IOException e) {
            log.error("es delete by query error: {} ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 文件转换
     *
     * @param wazuhFileRoot
     * @return
     */
    private FileEventModel getFileEventModel(WazuhFileRoot wazuhFileRoot, MultiValueMap<String, String> multiValueMap) {

        FileEventModel.FileEventModelBuilder builder = FileEventModel.builder();
        if (Objects.nonNull(wazuhFileRoot.getSyscheck())) {
            builder.filePath(wazuhFileRoot.getSyscheck().getPath())
                    .size(wazuhFileRoot.getSyscheck().getSizeAfter())
                    .user(wazuhFileRoot.getSyscheck().getUnameAfter())
                    .action(wazuhFileRoot.getSyscheck().getEvent())
                    .md5Before(wazuhFileRoot.getSyscheck().getMd5Before())
                    .md5After(wazuhFileRoot.getSyscheck().getMd5After());
        }

//        if (Objects.nonNull(wazuhFileRoot.getAgent())) {
//            builder.macIds(multiValueMap.get(wazuhFileRoot.getAgent().getId()))
//                    .agentName(wazuhFileRoot.getAgent().getName())
//                    .agentId(wazuhFileRoot.getAgent().getId());
//        }
        builder.agentName(wazuhFileRoot.getAgent().getName()).agentId(wazuhFileRoot.getAgent().getId());

        if (Objects.nonNull(wazuhFileRoot.getRule())) {
            //区分文件还是注册表
                builder.description(wazuhFileRoot.getRule().getDescription())
                    //  .catagory()
                    .level(wazuhFileRoot.getRule().getLevel());
            if (CollectionUtil.isNotEmpty(wazuhFileRoot.getRule().getGroups())) {
                builder.fileEventType(wazuhFileRoot.getRule().getGroups().stream().anyMatch("syscheck_registry"::equals) ? FileEventType.REGISTRY : FileEventType.FILE);
            }
        }

        builder.content(processContent(wazuhFileRoot));

        return builder.updateTime(TimeUtils.dateToLocalDateTime(wazuhFileRoot.getTimestamp())).build();
    }

    private JSONObject processContent(WazuhFileRoot wazuhFileRoot) {

        JSONObject jsonObject = new JSONObject();
        WazuhFileRoot.Rule rule = wazuhFileRoot.getRule();
        if (Objects.nonNull(rule)) {
            JSONObject ruleObj = new JSONObject();
            ruleObj.put("firedtimes", rule.getFiredtimes());
            ruleObj.put("description", rule.getDescription());
            ruleObj.put("groups", rule.getGroups());
            ruleObj.put("id", rule.getId());
            jsonObject.put("rule", ruleObj);
        }
        jsonObject.put("full_log", wazuhFileRoot.getFullLog());
        return jsonObject;
    }

    /**
     * 处理分页
     *
     * @param wazuhEventRequest
     * @param indexArray
     * @param boolQueryBuilder
     * @param sourceBuilder
     * @return
     */
    private int processPageable(WazuhEventRequest wazuhEventRequest, String[] indexArray,
        BoolQueryBuilder boolQueryBuilder, SearchSourceBuilder sourceBuilder) {

        if(Objects.isNull(wazuhEventRequest.getPageNum())) {
            wazuhEventRequest.setPageNum(1);
        }
        if(Objects.isNull(wazuhEventRequest.getPageSize())) {
            wazuhEventRequest.setPageSize(100);
        }

        int offset = (wazuhEventRequest.getPageNum() - 1) * (wazuhEventRequest.getPageSize());
        sourceBuilder.from(Math.min(offset, 10000));
        if ((offset + wazuhEventRequest.getPageSize()) > 10000) {
            sourceBuilder.size(Math.max(10000 - offset, 0));
        } else {
            sourceBuilder.size(wazuhEventRequest.getPageSize());
        }
        int total = queryTotalCount(indexArray, boolQueryBuilder);
        if (10000 < total) {
            total = 10000;
        }
        return total;
    }

    /**
     * 构造请求的索引
     *
     * @param start
     * @param end
     * @return
     */
    public static String[] buildRequestIndex(LocalDateTime start, LocalDateTime end) {
        List<String> indexList = indexListBuilder(start, end);
        var indexArray = new String[indexList.size()];
        indexList.toArray(indexArray);
        return indexArray;
    }

    private void buildFileEventBoolQueryBuilder(WazuhEventRequest wazuhEventRequest,
                                                BoolQueryBuilder boolQueryBuilder, MultiValueMap<String, String> allAgentMacAddr) {
        //构造查询条件
        if (!CollectionUtils.isEmpty(wazuhEventRequest.getRuleGroups())) {
//            boolQueryBuilder.must(QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("rule.groups", wazuhEventRequest.getRuleGroups().toArray())));
//            boolQueryBuilder.must(QueryBuilders.termsQuery("rule.groups", wazuhEventRequest.getRuleGroups().toArray()));
            buildRuleGroupQuery(wazuhEventRequest.getRuleGroups(), boolQueryBuilder);
        }

        // ruleIds
        if (!CollectionUtil.isEmpty(wazuhEventRequest.getRuleIds())) {
            buildRuleIdsQuery(wazuhEventRequest.getRuleIds(), boolQueryBuilder);
        }

//        if (!CollectionUtils.isEmpty(wazuhEventRequest.getMacIds())) {
//            //构造agentId
//            buildAgentIdQuery(wazuhEventRequest.getMacIds(), boolQueryBuilder, allAgentMacAddr);
//        }

        if (!CollectionUtils.isEmpty(wazuhEventRequest.getAgentNames())) {
            buildAgentNameQuery(wazuhEventRequest.getAgentNames(), boolQueryBuilder);
        }

        // catagories
        if(!CollectionUtils.isEmpty(wazuhEventRequest.getCatagories())) {
            buildCatagoryQuery(wazuhEventRequest.getCatagories(), boolQueryBuilder);
        }

        // keywords
        if(!CollectionUtils.isEmpty(wazuhEventRequest.getKeyword())) {
            buildKeywordsQuery(wazuhEventRequest.getKeyword(), boolQueryBuilder);
        }

        // levels
        if(!CollectionUtils.isEmpty(wazuhEventRequest.getLevels())) {
            buildLevelQuery(wazuhEventRequest.getLevels(), boolQueryBuilder);
        }

        // path
        if (wazuhEventRequest.getPath() != null) {
            buildPathQuery(wazuhEventRequest.getPath(), boolQueryBuilder);
        }


        if (Objects.nonNull(wazuhEventRequest.getStartTime()) && Objects.nonNull(wazuhEventRequest.getEndTime())) {
            //时间查询
            boolQueryBuilder.must(QueryBuilders.rangeQuery("timestamp")
                    .from(wazuhEventRequest.getStartTime().atZone(ZoneId.systemDefault()).format(EsDateFormatConfig.esDateTimeFormatter))
                    .to(wazuhEventRequest.getEndTime().atZone(ZoneId.systemDefault()).format(EsDateFormatConfig.esDateTimeFormatter)));
        }

    }

    private void buildRuleIdsQuery(List<String> ruleIds, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("rule.id", ruleIds));
    }

    private void buildRuleGroupQuery(List<String> groups, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("rule.groups", groups));
    }

    private void buildAgentNameQuery(List<String> names, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("agent.name", names));
    }

    private void buildCatagoryQuery(List<String> catagories, BoolQueryBuilder boolQueryBuilder) {
        // todo 分类 -> *.rules.xml -> ruleId 解析
    }

    private void buildKeywordsQuery(List<String> keywords, BoolQueryBuilder boolQueryBuilder) {
        if (!CollectionUtils.isEmpty(keywords)) {
            for (String keyword : keywords) {
                boolQueryBuilder.must(QueryBuilders.wildcardQuery("rule.description", "*" + keyword + "*"));
            }
        }
    }

    private void buildPathQuery(String path, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.must(QueryBuilders.wildcardQuery("syscheck.path", "*" + path + "*"));
    }

    private void buildLevelQuery(List<Integer> levels, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("rule.level", levels));
    }

    /**
     * 构造agentIdQuery
     *
     * @param macIds
     * @param boolQueryBuilder
     * @param allAgentMacAddr
     */
    private int buildAgentIdQuery(List<String> macIds, BoolQueryBuilder boolQueryBuilder,
        MultiValueMap<String, String> allAgentMacAddr) {
        Set<String> agentSet = this.queryAgentIdsByMac(macIds, allAgentMacAddr);
        boolQueryBuilder.filter(QueryBuilders.termsQuery("agent.id", agentSet));
        return agentSet.size();
    }

    /**
     * 查询count
     *
     * @param indexArray
     * @param boolQueryBuilder
     * @return count
     */
    private int queryTotalCount(String[] indexArray, BoolQueryBuilder boolQueryBuilder) {
        var countRequest = new CountRequest(indexArray);
        countRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        countRequest.query(boolQueryBuilder);
        var total = 0;
        try {
            CountResponse totalSize = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            total = (int) totalSize.getCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total;
    }

    /**
     * 构造查询需要的索引
     *
     * @return List<IndexBoost>
     */
    @VisibleForTesting
    public static List<String> indexListBuilder(LocalDateTime start, LocalDateTime end) {
        List<String> dateStringList = fetchFormatterTime(start, end);
        return dateStringList.stream().map(dateString -> WAZUH_ALERT_INDEX_PREFIX + dateString).collect(Collectors.toList());
    }

    /**
     * 获取比如2021-05-01 到2021-05-20.所有的日期，方便构建所有的时间字符串2021.05.01,2021.05.02,....
     *
     * @param startTime startTime
     * @param endTime endTime
     * @return List<String>
     */
    public static List<String> fetchFormatterTime(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("startTime:{}, endTime:{}", startTime, endTime);
        LocalDate startDate = startTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();
        List<String> dayList = new ArrayList<>();
        while (true) {
            if (startDate.isAfter(endDate)) {
                break;
            }
            dayList.add(startDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
            startDate = startDate.plusDays(1);
        }
        log.info("dayList:{}", dayList);
        return dayList;
    }


    private WazuhHostStateModel convertHostStateOutput2WazuhModel(HostStateOutput output) {
        WazuhHostStateModel wazuhHostStateModel = new WazuhHostStateModel();
        if (StringUtils.isNotBlank(output.getDiskTotal())) {
            wazuhHostStateModel.setDiskTotal(string2Long(output.getDiskTotal()));
        }

        if (StringUtils.isNotBlank(output.getDiskUsed())) {
            wazuhHostStateModel.setDiskUsed(string2Long(output.getDiskUsed()));
        }

        if (StringUtils.isNotBlank(output.getDiskUsed()) && StringUtils.isNotBlank(output.getDiskUsed())) {
            wazuhHostStateModel.setDiskUnused(wazuhHostStateModel.getDiskTotal() - wazuhHostStateModel.getDiskUsed());
        }

        if (wazuhHostStateModel.getDiskTotal() > 0 && wazuhHostStateModel.getDiskUsed() > 0) {
            double used = BigDecimal.valueOf(wazuhHostStateModel.getDiskUsed()).doubleValue();
            double total = BigDecimal.valueOf(wazuhHostStateModel.getDiskTotal()).doubleValue();
            // 换算百分比 要 * 100
            wazuhHostStateModel.setDiskPercent(NumberUtil.round(used * 100 / total, 2).doubleValue());
        }

        if (StringUtils.isNotBlank(output.getCPUCount())) {
            wazuhHostStateModel.setCpuCount(Integer.parseInt(output.getCPUCount()));
        }

        if (StringUtils.isNotBlank(output.getCPUFreq())) {
            wazuhHostStateModel.setCpuFreq(Double.parseDouble(output.getCPUFreq()));
        }

        if (StringUtils.isNotBlank(output.getCPUPercent())) {
            wazuhHostStateModel.setCpuTotalPercent(Double.parseDouble(output.getCPUPercent()));
        }

        if (StringUtils.isNotBlank(output.getMemoryFree())) {
            wazuhHostStateModel.setMemoryFree(string2Long(output.getMemoryFree()));
        }

        if (StringUtils.isNotBlank(output.getMemoryTotal())) {
            wazuhHostStateModel.setMemoryTotal(string2Long(output.getMemoryTotal()));
        }

        if (StringUtils.isNotBlank(output.getMemoryUsedPercent())) {
            wazuhHostStateModel.setMemoryPercent(Double.parseDouble(output.getMemoryUsedPercent()));
        }

        return wazuhHostStateModel;
    }

    private WazuhEnvVarEventModel convertEnvVarOutput2EnvVarModel(BaseChangeCountOutput output) {
        WazuhEnvVarEventModel model = new WazuhEnvVarEventModel();
        BeanUtils.copyProperties(output, model);
        return model;
    }

    private Long string2Long(String val) {
        Double doubleVal = Double.valueOf(val);
        Long longVal = BigDecimal.valueOf(doubleVal).longValue();
        return longVal;
    }

    /**
     * 剔除null
     * @param agentNames
     * @return
     */
    private List<String> agentNamesExcludeNullValue(List<String> agentNames) {
        List<String> nullExcludedAgentNames = agentNames.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return nullExcludedAgentNames;
    }

    private boolean isAgentNamesEmpty(List<String> agentNames) {
        List processedAgentNames = agentNamesExcludeNullValue(agentNames);
        boolean isEmpty = CollectionUtil.isEmpty(processedAgentNames);
        if(isEmpty) {
            log.warn("agentNames is empty:{}", agentNames);
        }
        return isEmpty;
    }

}
