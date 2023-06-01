package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.config.SituationCacheConfig;
import cn.cyberpeace.mp.situationaware.constant.SicLaunchConfig;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.enums.LaunchStatusEnum;
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
import cn.cyberpeace.mp.situationaware.model.WazuhProcessModel;
import cn.cyberpeace.mp.situationaware.model.WazuhSystemUserModel;
import cn.cyberpeace.mp.situationaware.model.WazuhVulnerabilityInfoCountModel;
import cn.cyberpeace.mp.situationaware.model.WazuhVulnerabilityInfoModel;
import cn.cyberpeace.mp.situationaware.model.enums.DataCollectionType;
import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
import cn.cyberpeace.mp.situationaware.model.sic.BaseChangeCountOutput;
import cn.cyberpeace.mp.situationaware.model.sic.BaseInput;
import cn.cyberpeace.mp.situationaware.model.sic.BaseRawOutput;
import cn.cyberpeace.mp.situationaware.model.sic.HostStateOutput;
import cn.cyberpeace.mp.situationaware.model.sic.Launch;
import cn.cyberpeace.mp.situationaware.repository.WazuhRepository;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
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
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
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
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.LongBounds;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
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
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * wazuh对接
 */
@Service
@Slf4j
@CacheConfig(cacheManager = SituationCacheConfig.CACHE_MANAGER)
public class DataCollectionService extends CollectionBaseService {

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private WazuhRepository wazuhRepository;

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    private static final String WAZUH_ALERT_INDEX_PREFIX = "wazuh-alerts-4.x-";

    private static final String CUSTOM_PROBE_ID_PATH = "data.sic_launch_id";

    private LocalDateTime getUtcTime(LocalDateTime time) {
        return TimeUtils.utcToLocalDateTime(TimeUtils.localDateTimeToUtcString(time));
    }

    public static final String CACHE_KEY_FILE_EVENTS = "file_events";

    public static final String CACHE_KEY_MONITOR_EVENTS = "monitor_events";

    public static final String CACHE_KEY_HOST_STATE = "host_states";

    public static final String CACHE_KEY_MAP_HOST_STATE = "map_host_states";

    public static final String CACHE_KEY_NET_CONN = "net_conn";

    public static final String CACHE_KEY_PROCESS = "process";

    public static final String CACHE_KEY_USERS = "users";

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
        if (isAgentNamesEmpty(wazuhEventRequest.getAgentNames())) {
            return new ListObjResult<>(Collections.emptyList(), 0);
        }
        //根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhEventRequest.getStartTime(), wazuhEventRequest.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        //构造查询条件
        buildFileEventBoolQueryBuilder(wazuhEventRequest, boolQueryBuilder, null);
        // 按时间降序
        sourceBuilder.sort(SituationalAwareConstant.ES_TIMESTAMP_FIELD, SortOrder.DESC);
        sourceBuilder.trackTotalHits(true);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));

        if (wazuhEventRequest.isScroll()) {
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(35L));
            searchRequest.scroll(scroll);
        }
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        //处理分页
        processPageable(wazuhEventRequest, indexArray, boolQueryBuilder, sourceBuilder);

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

            long total = 0L;
            if (Objects.nonNull(response.getHits()) && Objects.nonNull(response.getHits().getTotalHits())) {
                total = response.getHits().getTotalHits().value;
            }
            return new ListObjResult(fileEventModelList, BigDecimal.valueOf(total).intValue());
        } catch (IOException e) {
            log.error("es queryFileEvents error: {} ", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 文件 or 注册表变动事件
     *
     * @param wazuhEventRequest
     * @return
     */
    public ListObjResult<FileEventModel> queryFileOrRegisterEvents(WazuhEventRequest wazuhEventRequest) {
        if (isAgentNamesEmpty(wazuhEventRequest.getAgentNames())) {
            return new ListObjResult<>(Collections.emptyList(), 0);
        }

        //根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhEventRequest.getStartTime(), wazuhEventRequest.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        //构造查询条件
        buildFileEventBoolQueryBuilder(wazuhEventRequest, boolQueryBuilder, null);
        sourceBuilder.trackTotalHits(true);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));

        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        log.info("elasticsearch JSON query index:{}, DSL:\n{}", indexArray, searchRequest.source().toString());
        try {
            String agentNameTerm = "agent_name", sicLaunchIdTerm = "sic_launch_id";
            AggregationBuilder aggBuilder = AggregationBuilders.terms(agentNameTerm).field("agent.name")
                    .subAggregation(AggregationBuilders.terms(sicLaunchIdTerm).field("data.sic_launch_time").order(BucketOrder.key(true)).size(1));
            sourceBuilder.aggregation(aggBuilder);

            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<FileEventModel> fileEventModels = Lists.newArrayList();
            for(SearchHit hit : response.getHits().getHits()) {
                JSONObject json = JSONObject.parseObject(hit.getSourceAsString());
                String rawOutput = Optional.of(json.getJSONObject("data")).map(j -> j.getJSONObject("sic_launch_output")).map(j -> j.getString("raw_output")).orElse(null);
                if(rawOutput == null) {
                    continue;
                }

                String agentName = json.getJSONObject("agent").getString("name");
                String utcTime = json.getString("timestamp");
                utcTime = StringUtils.substringBeforeLast(utcTime, ".").concat("Z");
                LocalDateTime time = TimeUtils.utcStringToLocalDateTime(utcTime);
                JSONArray files = JSONArray.parseArray(rawOutput);
                for(Object f : files.toArray()) {
                    FileEventModel m = new FileEventModel();
                    m.setFilePath((String) f);
                    m.setAgentId(agentName);
                    m.setUpdateTime(time);
                    fileEventModels.add(m);
                }
            }

            return new ListObjResult<>(fileEventModels, fileEventModels.size());
        } catch (IOException e) {
            log.error("es queryFileEvents error: {} ", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }


    /**
     * 滚动分页查询文件事件
     *
     * @return ListObjResult<FileEventModel>
     */
    public ListObjResult<FileEventModel> scrollFileEvents(String scrollId) {
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(11L));
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(scroll);
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
        if (CollUtil.isNotEmpty(probeIds)) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery(CUSTOM_PROBE_ID_PATH, probeIds));
            addLaunchOutputSuccessCondition(boolQueryBuilder);
        }
        // 按时间降序
        sourceBuilder.sort(SituationalAwareConstant.ES_TIMESTAMP_FIELD, SortOrder.DESC);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        // 配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        // 处理分页
        processPageable(wazuhEventRequest, indexArray, boolQueryBuilder, sourceBuilder);

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

            long total = 0L;
            if (Objects.nonNull(response.getHits()) && Objects.nonNull(response.getHits().getTotalHits())) {
                total = response.getHits().getTotalHits().value;
            }
            return new ListObjResult(wazuhModelList, BigDecimal.valueOf(total).intValue());
        } catch (IOException e) {
            log.error("es queryCustomProbeData error, {}", ExceptionUtils.getStackTrace(e));
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
        if (isAgentNamesEmpty(wazuhEventRequest.getAgentNames())) {
            return new ListObjResult<>(Collections.emptyList(), 0);
        }
        // 根据日期构造索引,将本地时间转化为UTC时间后构建
        String[] indexArray = buildRequestIndex(getUtcTime(wazuhEventRequest.getStartTime()), getUtcTime(wazuhEventRequest.getEndTime()));
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        // 构造查询条件
        buildFileEventBoolQueryBuilder(wazuhEventRequest, boolQueryBuilder, null);
        if (desc) {
            // 按时间降序
            sourceBuilder.sort(SituationalAwareConstant.ES_TIMESTAMP_FIELD, SortOrder.DESC);
        } else {
            // 按时间顺序
            sourceBuilder.sort(SituationalAwareConstant.ES_TIMESTAMP_FIELD, SortOrder.ASC);
        }
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        // 配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        // 处理分页
        processPageable(wazuhEventRequest, indexArray, boolQueryBuilder, sourceBuilder);

        log.info("elasticsearch JSON query index:{}, DSL:\n{}", indexArray, searchRequest.source().toString());
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<WazuhVulnerabilityInfoModel> wazuhModelList = new ArrayList<>();
            for (var hit : response.getHits().getHits()) {
                WazuhMonitorRoot<WazuhVulnerabilityInfoModel> wazuhMonitorRoot = JSON.parseObject(hit.getSourceAsString(),
                        new TypeReference<>() {
                        });
                if (Objects.nonNull(wazuhMonitorRoot.getData())) {
                    var vul = wazuhMonitorRoot.getData().getVulnerability();
                    if (Objects.nonNull(vul)) {
                        WazuhVulnerabilityInfoModel vulnerability = new WazuhVulnerabilityInfoModel();
                        vulnerability.setVulnerability(vul);
                        vulnerability.setAgentId(wazuhMonitorRoot.getAgent().getId());
                        vulnerability.setAgentName(wazuhMonitorRoot.getAgent().getName());
                        vulnerability.setTime(TimeUtils.dateToLocalDateTime(wazuhMonitorRoot.getTimestamp()));
                        wazuhModelList.add(vulnerability);
                    }
                }
            }

            long total = 0L;
            if (Objects.nonNull(response.getHits()) && Objects.nonNull(response.getHits().getTotalHits())) {
                total = response.getHits().getTotalHits().value;
            }
            return new ListObjResult<>(wazuhModelList, BigDecimal.valueOf(total).intValue());
        } catch (IOException e) {
            log.error("es queryVulnerabilityInfo error:{}", ExceptionUtils.getStackTrace(e));
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
        if (isAgentNamesEmpty(wazuhEventRequest.getAgentNames())) {
            return new ArrayList<>();
        }
        // 根据日期构造索引,将本地时间转化为UTC时间后构建
        String[] indexArray = buildRequestIndex(getUtcTime(wazuhEventRequest.getStartTime()), getUtcTime(wazuhEventRequest.getEndTime()));
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
            log.error("es queryVulnerabilityCountInfo error:{}", ExceptionUtils.getStackTrace(e));
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
        sourceBuilder.sort(SituationalAwareConstant.ES_TIMESTAMP_FIELD, SortOrder.DESC);
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
            log.error("es 查询监控事件分页失败 {}", ExceptionUtils.getStackTrace(e));
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

        buildMonitorEventsAggBuilder(wazuhMonitorEventsRequest, boolQueryBuilder, null);
        // 按时间降序
        sourceBuilder.sort(SituationalAwareConstant.ES_TIMESTAMP_FIELD, SortOrder.DESC);
        // 监控数据每个agent一条，所以这里分页大小设为agent数量的2倍
        sourceBuilder.from(0);
        sourceBuilder.size(situationAwareConfig.getWazuhEsMaxPageSize());

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
            log.error(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_EVENT_ERROR.getMessage() + "{}", ExceptionUtils.getStackTrace(e));
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
                wazuhHostStateModel.setTime(TimeUtils.dateToLocalDateTime(model.getTimestamp()));
                return wazuhHostStateModel;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return wazuhModelList;
    }



    /**
     * 根据agentName分组查询监控数据
     * @param wazuhMonitorEventsRequest wazuhMonitorEventsRequest
     */
    @Cacheable(value = CACHE_KEY_MAP_HOST_STATE)
    public Map<String, List<WazuhHostStateModel>> mapHostStatesByAgentNamesWithTimeRange(WazuhMonitorEventsRequest wazuhMonitorEventsRequest, int topSize) {
        if (isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return Collections.emptyMap();
        }

        String aggName = "aggs_group_by_agent_name";
        String subAggName = "top_docs";
        SearchResponse response = aggsSICLaunchEventsByAgentNameWithTimeRange(wazuhMonitorEventsRequest, SicLaunchConfig.LAUNCH_ID_HOST_STATE, topSize, aggName, subAggName);
        Map<String, List<WazuhHostStateModel>> map = new HashMap<>();
        if (Objects.nonNull(response.getAggregations())) {

            Terms terms = response.getAggregations().get(aggName);
            terms.getBuckets().forEach(b -> {
                if (Objects.nonNull(b.getAggregations())) {
                    String key = b.getKeyAsString();
                    TopHits topHits = b.getAggregations().get(subAggName);
                        List<WazuhMonitorRoot<Launch<HostStateOutput, BaseInput>>> wazuhFileRootList = Arrays.stream(topHits
                                .getHits().getHits()).map(hit ->
                                JSONObject.parseObject(hit.getSourceAsString(), new TypeReference<WazuhMonitorRoot<Launch<HostStateOutput, BaseInput>>>() {
                                })).collect(Collectors.toList());

                        var wazuhModelList = wazuhFileRootList.stream().map(model -> {
                            if (Objects.nonNull(model.getData()) && Objects.nonNull(model.getData().getOutput())) {
                                HostStateOutput output = model.getData().getOutput();
                                WazuhHostStateModel wazuhHostStateModel = convertHostStateOutput2WazuhModel(output);
                                wazuhHostStateModel.setAgentId(model.getAgent().getId());
                                wazuhHostStateModel.setAgentName(model.getAgent().getName());
                                wazuhHostStateModel.setTime(TimeUtils.dateToLocalDateTime(model.getTimestamp()));
                                return wazuhHostStateModel;
                            }
                            return null;
                        }).filter(Objects::nonNull).collect(Collectors.toList());

                    map.put(key, wazuhModelList);
                }
            });
        }

        return map;
    }

    /**
     * 查询网络连接
     * @param
     * @return
     */
    @Cacheable(value = CACHE_KEY_NET_CONN)
    public List<WazuhNetworkConnectionModel> queryNetConnEventsWithTimeRange(WazuhMonitorEventsRequest wazuhMonitorEventsRequest) {
        if (isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return Collections.emptyList();
        }
        // 只取一条数据，做假分页处理
        wazuhMonitorEventsRequest.setPageSize(1);
        SearchResponse response = querySICLaunchEventsWithTimeRange(wazuhMonitorEventsRequest, SicLaunchConfig.LAUNCH_ID_NET_CONNS);
        JSONObject jsonObject = processBaseChangeOutput(response);
        if (Objects.isNull(jsonObject)) {
            return Collections.emptyList();
        }
        JSONArray arr = jsonObject.getJSONArray("values");
        if (Objects.isNull(arr)) {
            return Collections.emptyList();
        }
        List<WazuhNetworkConnectionModel> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            JSONObject data = arr.getJSONObject(i);
            WazuhNetworkConnectionModel netConnModel = JSONObject.toJavaObject(data, WazuhNetworkConnectionModel.class);
            if (Objects.nonNull(jsonObject.get("time"))) {
                netConnModel.setTime((LocalDateTime) jsonObject.get("time"));
            }
            netConnModel.setDescription("CMD LAUNCH LOG");
            list.add(netConnModel);
        }

        return list;
    }


    /**
     * 查询进程
     * @param
     * @return
     */
    @Cacheable(value = CACHE_KEY_PROCESS)
    public List<WazuhProcessModel> queryProcessesWithTimeRange(WazuhMonitorEventsRequest wazuhMonitorEventsRequest) {
        if (isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return Collections.emptyList();
        }
        // 只取一条数据，做假分页处理
        wazuhMonitorEventsRequest.setPageSize(1);
        SearchResponse response = querySICLaunchEventsWithTimeRange(wazuhMonitorEventsRequest, SicLaunchConfig.LAUNCH_ID_PROCESSES);
        JSONObject jsonObject = processBaseChangeOutput(response);
        if (Objects.isNull(jsonObject)) {
            return Collections.emptyList();
        }
        JSONArray arr = jsonObject.getJSONArray("values");
        if (Objects.isNull(arr)) {
            return Collections.emptyList();
        }
        List<WazuhProcessModel> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            JSONObject data = arr.getJSONObject(i);
            WazuhProcessModel processModel = JSONObject.toJavaObject(data, WazuhProcessModel.class);
            if (Objects.nonNull(jsonObject.get("time"))) {
                processModel.setTime((LocalDateTime) jsonObject.get("time"));
            }
            processModel.setDescription("PROCESS");
            list.add(processModel);
        }

        return list;
    }

    /**
     * 查询用户变更事件
     * @param
     * @return
     */
    @Cacheable(value = CACHE_KEY_USERS)
    public List<WazuhSystemUserModel> queryUserChangeEventWithTimeRange(WazuhMonitorEventsRequest wazuhMonitorEventsRequest) {
        if (isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return Collections.emptyList();
        }
        // 只取一条数据，做假分页处理
        wazuhMonitorEventsRequest.setPageSize(1);
        SearchResponse response = querySICLaunchEventsWithTimeRange(wazuhMonitorEventsRequest, SicLaunchConfig.LAUNCH_ID_SYSTEM_USERS);
        JSONObject jsonObject = processBaseChangeOutput(response);
        if (Objects.isNull(jsonObject)) {
            return Collections.emptyList();
        }
        JSONArray arr = jsonObject.getJSONArray("modified");
        if (Objects.isNull(arr)) {
            return Collections.emptyList();
        }
        List<WazuhSystemUserModel> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            JSONObject data = arr.getJSONObject(i);
            WazuhSystemUserModel userModel = JSONObject.toJavaObject(data, WazuhSystemUserModel.class);
            if (Objects.nonNull(jsonObject.get("time"))) {
                userModel.setTime((LocalDateTime) jsonObject.get("time"));
            }
            userModel.setDescription("USER MODIFIED");
            list.add(userModel);
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
        return sum;
    }


    @Cacheable(value = CACHE_KEY_PROCESS_CHANGED_COUNT)
    public int sumProcessesChangedCountByTimeRange(WazuhMonitorEventsRequest wazuhMonitorEventsRequest) {
        if (isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return 0;
        }

        int sum = aggsChangedCountWithTimeRange(wazuhMonitorEventsRequest, SicLaunchConfig.LAUNCH_ID_PROCESSES);
        return sum;
    }

    @Cacheable(value = CACHE_KEY_NET_CONNS_CHANGED_COUNT)
    public int sumNetConnsChangedCountByTimeRange(WazuhMonitorEventsRequest wazuhMonitorEventsRequest) {
        if (isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return 0;
        }

        int sum = aggsChangedCountWithTimeRange(wazuhMonitorEventsRequest, SicLaunchConfig.LAUNCH_ID_NET_CONNS);
        return sum;
    }


    /**
     * 查询LAUNCH_ID通用方法，返回SearchResponse
     * @param wazuhMonitorEventsRequest
     * @param launchId
     * @return
     */
    public SearchResponse querySICLaunchEventsWithTimeRange(WazuhMonitorEventsRequest wazuhMonitorEventsRequest, String launchId) {
        //根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhMonitorEventsRequest.getStartTime(), wazuhMonitorEventsRequest.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        buildMonitorEventsBuilderTimeRange(wazuhMonitorEventsRequest, boolQueryBuilder, null);

        if (StringUtils.isNotBlank(launchId)) {
            boolQueryBuilder.must(QueryBuilders.termQuery(CUSTOM_PROBE_ID_PATH, launchId));
            addLaunchOutputSuccessCondition(boolQueryBuilder);
        }
        // 按时间降序
        sourceBuilder.sort(SituationalAwareConstant.ES_TIMESTAMP_FIELD, SortOrder.DESC);
        if (Objects.nonNull(wazuhMonitorEventsRequest.getPageNum()) && Objects.nonNull(wazuhMonitorEventsRequest.getPageSize())) {
            if (wazuhMonitorEventsRequest.getPageNum() * wazuhMonitorEventsRequest.getPageSize() > situationAwareConfig.getWazuhEsMaxPageSize()) {
                throw new MpRuntimeException("不支持深度分页查询！");
            }
            sourceBuilder.from(wazuhMonitorEventsRequest.getPageNum() - 1 > 0 ?
                    (wazuhMonitorEventsRequest.getPageNum() - 1) * wazuhMonitorEventsRequest.getPageSize() : 0);
            sourceBuilder.size(wazuhMonitorEventsRequest.getPageSize());
        } else {
            sourceBuilder.size(situationAwareConfig.getWazuhEsMaxPageSize());
        }
        // 监控数据每个agent一条，所以这里分页大小设为agent数量的2倍
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        log.info("monitor JSON query index:{}, \n DSL:{}", indexArray, searchRequest.source().toString());

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }


    /**
     * 查询LAUNCH_ID通用方法，返回SearchResponse
     * @param wazuhMonitorEventsRequest
     * @param launchId
     * @return
     */
    public SearchResponse aggsSICLaunchEventsByAgentNameWithTimeRange(WazuhMonitorEventsRequest wazuhMonitorEventsRequest, String launchId, int topSize,
                                                                      String aggName, String subAggName) {
        //根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhMonitorEventsRequest.getStartTime(), wazuhMonitorEventsRequest.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        buildMonitorEventsBuilderTimeRange(wazuhMonitorEventsRequest, boolQueryBuilder, null);

        if (StringUtils.isNotBlank(launchId)) {
            boolQueryBuilder.must(QueryBuilders.termQuery(CUSTOM_PROBE_ID_PATH, launchId));
            addLaunchOutputSuccessCondition(boolQueryBuilder);
        }
        sourceBuilder.size(0);
        // build the aggregation
        TermsAggregationBuilder aggr = AggregationBuilders.terms(aggName)
                .field("agent.name")
                .size(wazuhMonitorEventsRequest.getAgentNames().size())
                .minDocCount(1);

        TopHitsAggregationBuilder topHitsAggr = AggregationBuilders.topHits(subAggName)
                .size(topSize)
                .sort(SituationalAwareConstant.ES_TIMESTAMP_FIELD, SortOrder.DESC);

        aggr.subAggregation(topHitsAggr);

        sourceBuilder.aggregation(aggr);

        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        log.info("monitor JSON query index:{}, \n DSL:{}", indexArray, searchRequest.source().toString());

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }


    /**
     * 处理BaseChangeOutput通用方法
     * @param response
     * @return
     */
    public JSONObject processBaseChangeOutput(SearchResponse response) {
        List<WazuhMonitorRoot<Launch<BaseChangeCountOutput, BaseInput>>> wazuhFileRootList = Arrays.stream(response.getHits()
                .getHits()).map(hit ->
                JSONObject.parseObject(hit.getSourceAsString(), new TypeReference<WazuhMonitorRoot<Launch<BaseChangeCountOutput, BaseInput>>>() {
                })).collect(Collectors.toList());

        if (CollectionUtil.isEmpty(wazuhFileRootList)) {
            return null;
        }

        // 只取最近一条
        WazuhMonitorRoot<Launch<BaseChangeCountOutput, BaseInput>> launchModel = wazuhFileRootList.get(0);
        if (Objects.nonNull(launchModel.getData()) && Objects.nonNull(launchModel.getData().getOutput())) {
            BaseChangeCountOutput changedCntOutput = launchModel.getData().getOutput();
            if (StringUtils.isNotBlank(changedCntOutput.getData())) {
                byte[] raw = TextUtils.rawDecodeBase64(StringUtils.strip(changedCntOutput.getData().trim(), "\""));
                String output = TextUtils.decompress(raw);
                JSONObject jsonObject = JSONObject.parseObject(output);

                Date date = launchModel.getTimestamp();
                if (Objects.nonNull(date)) {
                    jsonObject.put("time", date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                }
                return jsonObject;
            }
        }

        return null;
    }


    /**
     * 处理BaseRawOutput通用方法
     * @param response
     * @return
     */
    public JSONObject processBaseRawOutput(SearchResponse response) {
        List<WazuhMonitorRoot<Launch<BaseRawOutput, BaseInput>>> wazuhFileRootList = Arrays.stream(response.getHits()
                .getHits()).map(hit ->
                JSONObject.parseObject(hit.getSourceAsString(), new TypeReference<WazuhMonitorRoot<Launch<BaseRawOutput, BaseInput>>>() {
                })).collect(Collectors.toList());

        if (CollectionUtil.isEmpty(wazuhFileRootList)) {
            return null;
        }

        // 只取最近一条
        WazuhMonitorRoot<Launch<BaseRawOutput, BaseInput>> launchModel = wazuhFileRootList.get(0);
        if (Objects.nonNull(launchModel.getData()) && Objects.nonNull(launchModel.getData().getOutput())) {
            BaseRawOutput rawOutput = launchModel.getData().getOutput();
            if (StringUtils.isNotBlank(rawOutput.getRawOutput())) {
                byte[] raw = TextUtils.rawDecodeBase64(StringUtils.strip(rawOutput.getRawOutput().trim(), "\""));
                String output = TextUtils.decompress(raw);
                JSONObject jsonObject = JSONObject.parseObject(output);

                Date date = launchModel.getTimestamp();
                if (Objects.nonNull(date)) {
                    jsonObject.put("time", date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                }

                return jsonObject;
            }
        }

        return null;
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
            addLaunchOutputSuccessCondition(boolQueryBuilder);
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
        sourceBuilder.sort(SituationalAwareConstant.ES_TIMESTAMP_FIELD, SortOrder.DESC);
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
            log.error("es queryMonitorEventsWithTimeRange error: {}", ExceptionUtils.getStackTrace(e));
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
        boolQueryBuilder.must(QueryBuilders.termQuery(CUSTOM_PROBE_ID_PATH, SicLaunchConfig.LAUNCH_ID_HOST_STATE));
        addLaunchOutputSuccessCondition(boolQueryBuilder);
        int agentSize = 0;

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
        if (!CollectionUtils.isEmpty(wazuhEventRequest.getAgentNames())) {
            buildAgentNameQuery(wazuhEventRequest.getAgentNames(), boolQueryBuilder);
        }

        if (Objects.nonNull(wazuhEventRequest.getStartTime()) && Objects.nonNull(wazuhEventRequest.getEndTime())) {
            //构造事件条件
            boolQueryBuilder.must(QueryBuilders.rangeQuery(SituationalAwareConstant.ES_TIMESTAMP_FIELD)
                    .from(wazuhEventRequest.getStartTime().atZone(ZoneId.systemDefault()).format(SituationAwareConfig.esDateTimeFormatter))
                    .to(wazuhEventRequest.getEndTime().atZone(ZoneId.systemDefault()).format(SituationAwareConfig.esDateTimeFormatter)));
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
            log.error("es query error: {} ", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }


    public Map<String, Long> aggsDateHistogram(WazuhEventRequest wazuhEventRequest, String interval, String minBounds, String maxBounds) {
        String aggregationName = "aggs_date_histogram";
        Map<String, Long> resultMap = new LinkedHashMap<>();

        //根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhEventRequest.getStartTime(), wazuhEventRequest.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(0);

        // build the aggregation
        DateHistogramAggregationBuilder aggrBuilder = AggregationBuilders.dateHistogram(aggregationName)
                .field(SituationalAwareConstant.ES_TIMESTAMP_FIELD)
                .fixedInterval(new DateHistogramInterval(interval))
                .order(BucketOrder.key(false))
                .minDocCount(0)
                .extendedBounds(new LongBounds(minBounds, maxBounds))
                .format(DatePattern.NORM_DATETIME_MINUTE_PATTERN)
                .timeZone(ZoneId.systemDefault());
        sourceBuilder.aggregation(aggrBuilder);

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
                Histogram histogram = response.getAggregations().get(aggregationName);
                histogram.getBuckets().forEach(b -> {
                    resultMap.put(b.getKeyAsString(), Long.valueOf(b.getDocCount()));
                });
            }

            return resultMap;

        } catch (IOException e) {
            log.error("es query error: {} ", ExceptionUtils.getStackTrace(e));
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
            log.error("es query error: {} ", ExceptionUtils.getStackTrace(e));
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
            log.error("es query error: {} ", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 场景删除之后，删除对应es文档数据
     * @param wazuhEventRequest
     */
    public Long deleteDocByAgentNames(WazuhEventRequest wazuhEventRequest) {
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
            log.error("es delete by query error: {} ", ExceptionUtils.getStackTrace(e));
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

        if (Objects.nonNull(wazuhFileRoot.getData()) && Objects.nonNull(wazuhFileRoot.getData().getWin()) &&
                Objects.nonNull(wazuhFileRoot.getData().getWin().getSystem())) {
            builder.message(wazuhFileRoot.getData().getWin().getSystem().getMessage());
        }

        if (Objects.nonNull(wazuhFileRoot.getData()) && Objects.nonNull(wazuhFileRoot.getData().getSca())) {
            builder.message(wazuhFileRoot.getData().getSca().getDescription());
            if (Objects.nonNull(wazuhFileRoot.getData().getSca().getCheck())) {
                builder.message(wazuhFileRoot.getData().getSca().getCheck().getDescription());
            }
        }

        builder.agentName(wazuhFileRoot.getAgent().getName()).agentId(wazuhFileRoot.getAgent().getId());

        FileEventType fileEventType = null;
        if (Objects.nonNull(wazuhFileRoot.getData())) {
            String sicLaunchId = wazuhFileRoot.getData().getSicLaunchId();
            if (SicLaunchConfig.LAUNCH_ID_PROCESSES.equals(sicLaunchId)) {
                fileEventType = FileEventType.PROCESS;
            } else if (SicLaunchConfig.LAUNCH_ID_NET_CONNS.equals(sicLaunchId)) {
                fileEventType = FileEventType.PORT;
            } else if (SicLaunchConfig.LAUNCH_ID_SYSTEM_USERS.equals(sicLaunchId)) {
                fileEventType = FileEventType.USERS;
            }
        }
        if (Objects.nonNull(wazuhFileRoot.getRule())) {
            //区分文件还是注册表
            builder.description(wazuhFileRoot.getRule().getDescription())
                    //  .catagory()
                    .level(wazuhFileRoot.getRule().getLevel()).ruleId(wazuhFileRoot.getRule().getId());
            if (CollectionUtil.isNotEmpty(wazuhFileRoot.getRule().getGroups()) && fileEventType == null) {
                fileEventType = wazuhFileRoot.getRule().getGroups().stream().anyMatch("syscheck_registry"::equals) ? FileEventType.REGISTRY : FileEventType.FILE;
            }
        }
        builder.fileEventType(fileEventType);
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

        if (wazuhEventRequest.getPageSize() > 10000) {
            log.error("es page size too large! {}", wazuhEventRequest.getPageSize());
            throw new MpRuntimeException("es page size too large! " + wazuhEventRequest.getPageSize());
        }

        int offset = (wazuhEventRequest.getPageNum() - 1) * (wazuhEventRequest.getPageSize());
        sourceBuilder.from(Math.min(offset, situationAwareConfig.getWazuhEsMaxPageSize()));
        if ((offset + wazuhEventRequest.getPageSize()) > situationAwareConfig.getWazuhEsMaxPageSize()) {
            sourceBuilder.size(Math.max(situationAwareConfig.getWazuhEsMaxPageSize() - offset, 0));
        } else {
            sourceBuilder.size(wazuhEventRequest.getPageSize());
        }
        int total = queryTotalCount(indexArray, boolQueryBuilder);
        return Math.min(situationAwareConfig.getWazuhEsMaxPageSize(), total);
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
            buildRuleGroupQuery(wazuhEventRequest.getRuleGroups(), boolQueryBuilder);
        }

        // ruleIds
        if (!CollectionUtil.isEmpty(wazuhEventRequest.getRuleIds())) {
            buildRuleIdsQuery(wazuhEventRequest.getRuleIds(), boolQueryBuilder);
        }

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
        if (StringUtils.isNotEmpty(wazuhEventRequest.getPath())) {
            buildPathQuery(wazuhEventRequest.getPath(), boolQueryBuilder);
        }

        // sicLaunchId
        if (StringUtils.isNotEmpty(wazuhEventRequest.getSicLaunchId())) {
            buildSicLaunchIdQuery(wazuhEventRequest.getSicLaunchId(), boolQueryBuilder);
        }


        if (Objects.nonNull(wazuhEventRequest.getStartTime()) && Objects.nonNull(wazuhEventRequest.getEndTime())) {
            //时间查询
            boolQueryBuilder.must(QueryBuilders.rangeQuery(SituationalAwareConstant.ES_TIMESTAMP_FIELD)
                    .from(wazuhEventRequest.getStartTime().atZone(ZoneId.systemDefault()).format(SituationAwareConfig.esDateTimeFormatter))
                    .to(wazuhEventRequest.getEndTime().atZone(ZoneId.systemDefault()).format(SituationAwareConfig.esDateTimeFormatter)));
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
                boolQueryBuilder.must(QueryBuilders.wildcardQuery("rule.description", "*" + QueryParserUtil.escape(keyword) + "*"));
            }
        }
    }

    private void buildPathQuery(String path, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.must(QueryBuilders.wildcardQuery("syscheck.path", "*" + QueryParserUtil.escape(path) + "*"));
    }

    private void buildSicLaunchIdQuery(String sicLaunchId, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termQuery(CUSTOM_PROBE_ID_PATH, sicLaunchId));
        addLaunchOutputSuccessCondition(boolQueryBuilder);
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
        LocalDate startDate = startTime.toLocalDate().minusDays(1);
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

        if (StringUtils.isNotBlank(output.getDiskTotal()) && StringUtils.isNotBlank(output.getDiskUsed())) {
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

        if (StringUtils.isBlank(output.getDownloadSpeed())) {
            wazuhHostStateModel.setDownloadSpeed("0.00KB/s");
        } else {
            wazuhHostStateModel.setDownloadSpeed(output.getDownloadSpeed());
        }

        if (StringUtils.isBlank(output.getMaxDownloadSpeed())) {
            wazuhHostStateModel.setMaxDownloadSpeed("0.00KB/s");
        } else {
            wazuhHostStateModel.setMaxDownloadSpeed(output.getMaxDownloadSpeed());
        }

        if (StringUtils.isBlank(output.getUploadSpeed())) {
            wazuhHostStateModel.setUploadSpeed("0.00KB/s");
        } else {
            wazuhHostStateModel.setUploadSpeed(output.getUploadSpeed());
        }

        if (StringUtils.isBlank(output.getMaxUploadSpeed())) {
            wazuhHostStateModel.setMaxUploadSpeed("0.00KB/s");
        } else {
            wazuhHostStateModel.setMaxUploadSpeed(output.getMaxUploadSpeed());
        }

        // 网卡最大速率
        if (StringUtils.isNotBlank(output.getMaxNetSpeed())) {
            // 默认1000
            wazuhHostStateModel.setMaxNetSpeed(1000);
            String maxSpeedStr = output.getMaxNetSpeed();
            if (maxSpeedStr.contains("Mb/s")) {
                maxSpeedStr = maxSpeedStr.replaceAll("Mb/s", "");
                long maxSpeed = Long.valueOf(maxSpeedStr);
                wazuhHostStateModel.setMaxNetSpeed(maxSpeed);
            }
        }

        if (StringUtils.isBlank(output.getDiskReadBytes())) {
            wazuhHostStateModel.setDiskReadBytes(0);
        } else {
            wazuhHostStateModel.setDiskReadBytes(Long.parseLong(output.getDiskReadBytes()));
        }

        if (StringUtils.isBlank(output.getDiskIoPercent())) {
            wazuhHostStateModel.setDiskIoPercent(0);
        } else {
            wazuhHostStateModel.setDiskIoPercent(Double.parseDouble(output.getDiskIoPercent()));
        }

        if (StringUtils.isBlank(output.getDiskWriteBytes())) {
            wazuhHostStateModel.setDiskWriteBytes(0);
        } else {
            wazuhHostStateModel.setDiskWriteBytes(Long.parseLong(output.getDiskWriteBytes()));
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
     * 添加output字段存在，且status为success条件
     * @param boolQueryBuilder
     */
    private void addLaunchOutputSuccessCondition(BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termQuery(SicLaunchConfig.FIELD_SIC_LAUNCH_STATUS, LaunchStatusEnum.SUCCESS.getValue()));
        ExistsQueryBuilder existsQuery = QueryBuilders.existsQuery(SicLaunchConfig.FIELD_SIC_LAUNCH_OUTPUT);
        boolQueryBuilder.filter(QueryBuilders.nestedQuery(SicLaunchConfig.FIELD_SIC_LAUNCH_OUTPUT, existsQuery, ScoreMode.None));
    }

}
