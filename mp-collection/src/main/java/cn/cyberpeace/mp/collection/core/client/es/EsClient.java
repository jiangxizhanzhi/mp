package cn.cyberpeace.mp.collection.core.client.es;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.collection.config.CollectionCacheConfig;
import cn.cyberpeace.mp.collection.config.EsDateFormatConfig;
import cn.cyberpeace.mp.collection.constant.SicLaunchConstant;
import cn.cyberpeace.mp.collection.util.ConvertModelUtil;
import cn.cyberpeace.mp.collection.core.model.*;
import cn.cyberpeace.mp.collection.core.model.sic.BaseChangeCountOutput;
import cn.cyberpeace.mp.collection.core.model.sic.BaseInput;
import cn.cyberpeace.mp.collection.core.model.sic.HostStateOutput;
import cn.cyberpeace.mp.collection.core.model.sic.Launch;
import cn.cyberpeace.mp.collection.core.request.WazuhEventRequest;
import cn.cyberpeace.mp.collection.core.request.WazuhHostMonitorPageRequest;
import cn.cyberpeace.mp.collection.core.response.WazuhSystemUserResponse;
import cn.cyberpeace.mp.collection.util.AgentNameUtils;
import cn.cyberpeace.mp.collection.util.EsResponseParseUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.cyberpeace.mp.collection.constant.CacheKeyConstant.*;

@CacheConfig(cacheManager = CollectionCacheConfig.CACHE_MANAGER)
public class EsClient {
    private RestHighLevelClientImpl restHighLevelClient;

    public static class EsClientBuilder {
        private RestHighLevelClientImpl.RestHighLevelClientBuilder restHighLevelClientBuilder;

        private EsClientBuilder() {
            this.restHighLevelClientBuilder = RestHighLevelClientImpl.builder();
        }

        public void configEs(String esServer) {
            restHighLevelClientBuilder.configEs(esServer);
        }

        public EsClient build() {
            return new EsClient(restHighLevelClientBuilder.build());
        }
    }

    private EsClient(RestHighLevelClientImpl restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    public static EsClientBuilder builder() {
        return new EsClientBuilder();
    }

    private static final String WAZUH_ALERT_INDEX_PREFIX = "wazuh-alerts-4.x-";

    private static final String CUSTOM_PROBE_ID_PATH = "data.sic_launch_id";

    /**
     * 查询采集事件分页
     * @param request
     * @return ListObjResult<FileEventModel>
     */
    @Cacheable(value = CACHE_KEY_FILE_EVENTS)
    public ListObjResult<FileEvent> queryFileEvents(WazuhEventRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return new ListObjResult<>(Collections.emptyList(), 0);
        }
        var sourceBuilder = buildSearchSourceBuilder(request);
        var boolQueryBuilder = buildBoolQueryBuilder(request);
        SearchRequest searchRequest = buildSearchRequest(request, sourceBuilder, boolQueryBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest);
        var wazuhFileRootList = EsResponseParseUtils.parseResponse(response, WazuhFileRoot.class);
        List<FileEvent> fileEventModelList = wazuhFileRootList.stream().map((ConvertModelUtil::getFileEvent)).collect(Collectors.toList());

        String responseScrollId = response.getScrollId();
        if (request.isScroll()) {
            fileEventModelList.forEach(model -> model.setScrollId(responseScrollId));
        }

        long total = 0L;
        if (Objects.nonNull(response.getHits()) && Objects.nonNull(response.getHits().getTotalHits())) {
            total = response.getHits().getTotalHits().value;
        }
        return new ListObjResult<>(fileEventModelList, BigDecimal.valueOf(total).intValue());
    }

    /**
     * 滚动分页查询采集事件
     * @return ListObjResult<FileEventModel>
     */
    public ListObjResult<FileEvent> scrollFileEvents(String scrollId) {
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(11L));
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(scroll);
        SearchResponse response = restHighLevelClient.scroll(scrollRequest);
        var wazuhFileRootList = EsResponseParseUtils.parseResponse(response, WazuhFileRoot.class);
        List<FileEvent> fileEventModelList = wazuhFileRootList.stream().map((ConvertModelUtil::getFileEvent)).collect(Collectors.toList());

        String responseScrollId = response.getScrollId();
        fileEventModelList.forEach(model -> model.setScrollId(responseScrollId));
        return new ListObjResult<>(fileEventModelList, (int) response.getHits().getTotalHits().value);
    }

    /**
     * 查询自定义探针采集数据分页
     * @param request
     * @return ListObjResult<WazuhProbeCommonModel>
     */
    @Cacheable(value = CACHE_KEY_CUSTOM_PROBES)
    public ListObjResult<WazuhProbeCommon> queryCustomProbeData(WazuhEventRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return new ListObjResult<>(Collections.emptyList(), 0);
        }
        var sourceBuilder = buildSearchSourceBuilder(request);
        var boolQueryBuilder = buildBoolQueryBuilder(request);
        SearchRequest searchRequest = buildSearchRequest(request, sourceBuilder, boolQueryBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest);
        List<WazuhProbeCommon> wazuhModelList = new ArrayList<>();
        if (Objects.isNull(response) || Objects.isNull(response.getHits()) || Objects.isNull(response.getHits().getHits())) {
            return new ListObjResult<>(wazuhModelList, 0);
        }
        for (var hit : response.getHits().getHits()) {
            WazuhMonitorRoot<Launch<Object, BaseInput>> wazuhMonitorRoot = JSON.parseObject(hit.getSourceAsString(),
                    new TypeReference<>() {
                    });
            if (Objects.nonNull(wazuhMonitorRoot.getData()) && Objects.nonNull(wazuhMonitorRoot.getData().getOutput())) {
                String output = JsonCommon.stringify(wazuhMonitorRoot.getData().getOutput());
                WazuhProbeCommon wazuhProbeCommon = new WazuhProbeCommon();
                wazuhProbeCommon.setRawOutPut(output);
                wazuhProbeCommon.setAgentId(wazuhMonitorRoot.getAgent().getId());
                wazuhProbeCommon.setAgentName(wazuhMonitorRoot.getAgent().getName());
                wazuhProbeCommon.setTime(TimeUtils.dateToLocalDateTime(wazuhMonitorRoot.getTimestamp()));
                wazuhProbeCommon.setProbeId(wazuhMonitorRoot.getData().getId());
                wazuhModelList.add(wazuhProbeCommon);
            }
        }

        long total = 0L;
        if (Objects.nonNull(response.getHits()) && Objects.nonNull(response.getHits().getTotalHits())) {
            total = response.getHits().getTotalHits().value;
        }
        return new ListObjResult(wazuhModelList, BigDecimal.valueOf(total).intValue());
    }

    /**
     * 查询漏洞信息分页
     * @param request
     * @return ListObjResult<WazuhVulnerabilityInfoModel>
     */
    @Cacheable(value = CACHE_KEY_VUL_INFOS)
    public ListObjResult<WazuhVulnerabilityInfo> queryVulnerabilityInfo(WazuhEventRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return new ListObjResult<>(Collections.emptyList(), 0);
        }
        var sourceBuilder = buildSearchSourceBuilder(request);
        var boolQueryBuilder = buildBoolQueryBuilder(request);
        SearchRequest searchRequest = buildSearchRequest(request, sourceBuilder, boolQueryBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest);
        List<WazuhVulnerabilityInfo> wazuhModelList = new ArrayList<>();
        for (var hit : response.getHits().getHits()) {
            WazuhMonitorRoot<WazuhVulnerabilityInfo> wazuhMonitorRoot = JSON.parseObject(hit.getSourceAsString(),
                    new TypeReference<>() {
                    });
            if (Objects.nonNull(wazuhMonitorRoot.getData())) {
                var vul = wazuhMonitorRoot.getData().getVulnerability();
                if (Objects.nonNull(vul)) {
                    WazuhVulnerabilityInfo vulnerability = new WazuhVulnerabilityInfo();
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
    }

    /**
     * 查询漏洞信息数量
     * @param request
     * @param fieldPath 字段所在路径
     * @param fieldName 字段名称
     * @return List<WazuhVulnerabilityInfoCountModel>
     */
    @Cacheable(value = CACHE_KEY_VUL_COUNTS)
    public List<WazuhVulnerabilityInfoCount> queryVulnerabilityCountInfo(WazuhEventRequest request,
                                                                         String fieldPath, String fieldName) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return new ArrayList<>();
        }
        var sourceBuilder = buildSearchSourceBuilder(request);
        sourceBuilder.aggregation(AggregationBuilders.terms(fieldName).field(fieldPath));
        var boolQueryBuilder = buildBoolQueryBuilder(request);
        SearchRequest searchRequest = buildSearchRequest(request, sourceBuilder, boolQueryBuilder);
        List<WazuhVulnerabilityInfoCount> vulnerabilityInfoCounts;
        SearchResponse response = restHighLevelClient.search(searchRequest);
        long totalHits = response.getHits().getTotalHits().value;
        if (totalHits == 0) {
            return new ArrayList<>();
        }
        Aggregations aggregations = response.getAggregations();
        ParsedTerms parsedTerms = aggregations.get(fieldName);
        List<? extends Terms.Bucket> buckets = parsedTerms.getBuckets();
        vulnerabilityInfoCounts = buckets.stream().map(bucket -> {
            WazuhVulnerabilityInfoCount infoCount = new WazuhVulnerabilityInfoCount();
            infoCount.setFieldName(bucket.getKeyAsString());
            infoCount.setCount(bucket.getDocCount());
            return infoCount;
        }).collect(Collectors.toList());
        return vulnerabilityInfoCounts;
    }

    /**
     * 查询主机状态信息
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_HOST_STATE)
    public ListObjResult<WazuhHostState> queryHostStateEventsWithTimeRange(WazuhHostMonitorPageRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return new ListObjResult<>(new ArrayList<>(), 0);
        }
        SearchResponse response = querySICLaunchEventsWithTimeRange(request, SicLaunchConstant.LAUNCH_ID_HOST_STATE);
        List<WazuhMonitorRoot<Launch<HostStateOutput, BaseInput>>> wazuhFileRootList = Arrays.stream(response.getHits()
                .getHits()).map(hit ->
                JSON.parseObject(hit.getSourceAsString(), new TypeReference<WazuhMonitorRoot<Launch<HostStateOutput, BaseInput>>>() {
                })).collect(Collectors.toList());

        var hostStateModels = wazuhFileRootList.stream().map(model -> {
            if (Objects.nonNull(model.getData()) && Objects.nonNull(model.getData().getOutput())) {
                HostStateOutput output = model.getData().getOutput();
                WazuhHostState wazuhHostState = ConvertModelUtil.convertHostStateOutput2WazuhHost(output);
                wazuhHostState.setAgentId(model.getAgent().getId());
                wazuhHostState.setAgentName(model.getAgent().getName());
                wazuhHostState.setAgentIp(model.getAgent().getIp());
                wazuhHostState.setTime(TimeUtils.dateToLocalDateTime(model.getTimestamp()));
                return wazuhHostState;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        long total = 0L;
        if (Objects.nonNull(response.getHits()) && Objects.nonNull(response.getHits().getTotalHits())) {
            total = response.getHits().getTotalHits().value;
        }
        return new ListObjResult<>(hostStateModels, BigDecimal.valueOf(total).intValue());
    }

    /**
     * 查询主机网络连接信息
     * @param
     * @return
     */
    @Cacheable(value = CACHE_KEY_NET_CONN)
    public List<WazuhNetworkConnection> queryNetConnEventsWithTimeRange(WazuhHostMonitorPageRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return Collections.emptyList();
        }
        // 每个agent取1条数据，做假分页处理
        request.setPageNum(1);
        request.setPageSize(request.getAgentNames().size());
        SearchResponse response = querySICLaunchEventsWithTimeRange(request, SicLaunchConstant.LAUNCH_ID_NET_CONNS);
        List<JSONObject> jsonObjects = processBaseChangeOutput(response);
        if (CollectionUtils.isEmpty(jsonObjects)) {
            return Collections.emptyList();
        }
        List<WazuhNetworkConnection> list = new ArrayList<>();
        for (JSONObject jsonObject : jsonObjects) {
            JSONArray arr = jsonObject.getJSONArray("values");
            if (Objects.isNull(arr)) {
                continue;
            }
            for (int i = 0; i < arr.size(); i++) {
                JSONObject data = arr.getJSONObject(i);
                WazuhNetworkConnection netConnModel = JSON.toJavaObject(data, WazuhNetworkConnection.class);
                netConnModel.setTime((LocalDateTime) jsonObject.get("time"));
                netConnModel.setDescription("CMD LAUNCH LOG");
                list.add(netConnModel);
            }
        }
        return list;
    }

    /**
     * 查询主机进程信息
     * @param
     * @return
     */
    @Cacheable(value = CACHE_KEY_PROCESS)
    public List<WazuhProcess> queryProcessesWithTimeRange(WazuhHostMonitorPageRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return Collections.emptyList();
        }
        // 每个agent取1条数据，做假分页处理
        request.setPageNum(1);
        request.setPageSize(request.getAgentNames().size());
        SearchResponse response = querySICLaunchEventsWithTimeRange(request, SicLaunchConstant.LAUNCH_ID_PROCESSES);
        List<JSONObject> jsonObjects = processBaseChangeOutput(response);
        if (CollectionUtils.isEmpty(jsonObjects)) {
            return Collections.emptyList();
        }
        List<WazuhProcess> list = new ArrayList<>();
        for (JSONObject jsonObject : jsonObjects) {
            JSONArray arr = jsonObject.getJSONArray("values");
            if (Objects.isNull(arr)) {
                continue;
            }
            for (int i = 0; i < arr.size(); i++) {
                JSONObject data = arr.getJSONObject(i);
                WazuhProcess processModel = JSON.toJavaObject(data, WazuhProcess.class);
                processModel.setTime((LocalDateTime) jsonObject.get("time"));
                processModel.setAgentIp((String) jsonObject.get("agent_ip"));
                processModel.setAgentName((String) jsonObject.get("agent_name"));
                processModel.setDescription("PROCESS");
                list.add(processModel);
            }
        }
        return list;
    }

    /**
     * 查询主机系统用户信息
     * @param
     * @return
     */
    @Cacheable(value = CACHE_KEY_USERS)
    public WazuhSystemUserResponse querySystemUserWithTimeRange(WazuhHostMonitorPageRequest wazuhMonitorEventsRequest) {
        if (AgentNameUtils.isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return null;
        }
        // 取agent个数条数据，做假分页处理
        wazuhMonitorEventsRequest.setPageNum(1);
        wazuhMonitorEventsRequest.setPageSize(wazuhMonitorEventsRequest.getAgentNames().size());
        SearchResponse response = querySICLaunchEventsWithTimeRange(wazuhMonitorEventsRequest, SicLaunchConstant.LAUNCH_ID_SYSTEM_USERS);
        List<JSONObject> jsonObjects = processBaseChangeOutput(response);
        if (CollectionUtils.isEmpty(jsonObjects)) {
            return null;
        }
        WazuhSystemUserResponse systemUserResponse = new WazuhSystemUserResponse();
        for (JSONObject jsonObject : jsonObjects) {
            JSONArray values = jsonObject.getJSONArray("values");
            JSONArray modified = jsonObject.getJSONArray("modified");
            JSONArray deleted = jsonObject.getJSONArray("deleted_prev");
            JSONArray added = jsonObject.getJSONArray("added");
            systemUserResponse.setValues(ConvertModelUtil.processJSONArray(values, jsonObject));
            systemUserResponse.setModified(ConvertModelUtil.processJSONArray(modified, jsonObject));
            systemUserResponse.setDeletedPrev(ConvertModelUtil.processJSONArray(deleted, jsonObject));
            systemUserResponse.setAdded(ConvertModelUtil.processJSONArray(added, jsonObject));
        }
        return systemUserResponse;
    }

    /**
     * 一段时间内的环境变量数变动
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_ENV_VAR_CHANGED_COUNT)
    public int sumEnvVarChangedCountByTimeRange(WazuhHostMonitorPageRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return 0;
        }
        return aggsChangedCountWithTimeRange(request, SicLaunchConstant.LAUNCH_ID_ENV_VAR);
    }

    /**
     * 一段时间内的用户数量变动
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_SYSTEM_USERS_CHANGED_COUNT)
    public int sumSysUsersChangedCountByTimeRange(WazuhHostMonitorPageRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return 0;
        }
        return aggsChangedCountWithTimeRange(request, SicLaunchConstant.LAUNCH_ID_SYSTEM_USERS);
    }

    /**
     * 一段时间内的进程数量变动
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_PROCESS_CHANGED_COUNT)
    public int sumProcessesChangedCountByTimeRange(WazuhHostMonitorPageRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return 0;
        }
        return aggsChangedCountWithTimeRange(request, SicLaunchConstant.LAUNCH_ID_PROCESSES);
    }

    /**
     * 一段时间内的网络连接数量变动
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_NET_CONNS_CHANGED_COUNT)
    public int sumNetConnsChangedCountByTimeRange(WazuhHostMonitorPageRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return 0;
        }
        return aggsChangedCountWithTimeRange(request, SicLaunchConstant.LAUNCH_ID_NET_CONNS);
    }

    /**
     * 统计每个节点的采集事件总数，并排序
     * 如：node5:100  node4:85  node3:60  node2:50  node1: 10
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_AGGS_BY_AGENTNAME)
    public Map<String, Long> countByAgentNamesBucket(WazuhEventRequest request) {
        Map<String, Long> map = new LinkedHashMap<>();
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return map;
        }
        String aggregationName = "aggs_count_by_agent_name";
        request.getAgentNames().forEach(a -> map.put(a, 0L));
        var boolQueryBuilder = buildBoolQueryBuilder(request);
        var sourceBuilder = buildSearchSourceBuilder(request);
        sourceBuilder.size(0);
        // build the aggregation
        TermsAggregationBuilder aggregation = AggregationBuilders.terms(aggregationName)
                .field("agent.name");
        sourceBuilder.aggregation(aggregation);
        var searchRequest = buildSearchRequest(request, sourceBuilder, boolQueryBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest);
        if (Objects.nonNull(response.getAggregations())) {
            Terms terms = response.getAggregations().get(aggregationName);
            terms.getBuckets().forEach(b -> map.put(b.getKeyAsString(), b.getDocCount()));
        }
        return map;
    }

    /**
     * 查询主机匹配到的所有rule id，最大数量100
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_AGGS_RULEIDS_BY_AGENTNAME)
    public Map<String, Long> aggsRuleIdsByAgentName(WazuhEventRequest request) {
        Map<String, Long> map = new LinkedHashMap<>();
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return map;
        }
        String aggregationName = "aggs_ruleids_by_agent_name";
        var boolQueryBuilder = buildBoolQueryBuilder(request);
        var sourceBuilder = buildSearchSourceBuilder(request);
        sourceBuilder.size(0);
        // build the aggregation
        TermsAggregationBuilder aggregation = AggregationBuilders.terms(aggregationName)
                .field("rule.id").size(100);
        sourceBuilder.aggregation(aggregation);
        var searchRequest = buildSearchRequest(request, sourceBuilder, boolQueryBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest);
        if (Objects.nonNull(response.getAggregations())) {
            Terms terms = response.getAggregations().get(aggregationName);
            terms.getBuckets().forEach(b -> map.put(b.getKeyAsString(), b.getDocCount()));
        }
        return map;
    }

    /**
     * 所有节点的采集事件总数
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_COUNT_BY_AGENTNAME)
    public Long countByAgentNames(WazuhEventRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return 0L;
        }
        CountRequest countRequest = buildCountRequest(request);
        CountResponse response = restHighLevelClient.count(countRequest);
        return response.getCount();
    }

    /**
     * 场景删除之后，删除对应es文档数据
     * @param request
     */
    public Long deleteDocByAgentNames(WazuhEventRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return 0L;
        }
        DeleteByQueryRequest deleteRequest = buildDeleteRequest(request);
        BulkByScrollResponse response = restHighLevelClient.deleteByQuery(deleteRequest);
        return response.getUpdated();
    }

    /**
     * 查询数据变动数通用方法
     * @param request
     * @param launchId
     * @return
     */
    private int aggsChangedCountWithTimeRange(WazuhHostMonitorPageRequest request, String launchId) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return 0;
        }

        var boolQueryBuilder = buildBoolQueryBuilder(request);
        if (StringUtils.isNotEmpty(launchId)) {
            boolQueryBuilder.must(QueryBuilders.termQuery(CUSTOM_PROBE_ID_PATH, launchId));
        }

        BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("data.sic_launch_output.changed_count").gt(0L));
        boolQueryBuilder.must(QueryBuilders.nestedQuery("data.sic_launch_output", nestedBoolQueryBuilder, ScoreMode.None));

        String aggregationNested = "aggs_nested";
        String aggregationSum = "aggs_changed_count_sum";
        // build the aggregation
        Script sumScript = new Script("Integer.parseInt(doc['data.sic_launch_output.changed_count'].value)");
        NestedAggregationBuilder sumAggregationBuilder = AggregationBuilders.nested(aggregationNested, "data.sic_launch_output").subAggregation(AggregationBuilders.sum(aggregationSum).script(sumScript));

        var sourceBuilder = buildSearchSourceBuilder(request);
        sourceBuilder.aggregation(sumAggregationBuilder);
        sourceBuilder.size(0);
        var searchRequest = buildSearchRequest(request, sourceBuilder, boolQueryBuilder);
        int changedCount = 0;
        SearchResponse response = restHighLevelClient.search(searchRequest);
        if (Objects.nonNull(response.getAggregations())) {
            ParsedNested nested = response.getAggregations().get(aggregationNested);
            ParsedSum sum = nested.getAggregations().get(aggregationSum);
            changedCount = (int) sum.getValue();
        }

        return changedCount;
    }

    /**
     * 处理BaseChangeOutput通用方法
     * @param response
     * @return
     */
    private List<JSONObject> processBaseChangeOutput(SearchResponse response) {
        List<WazuhMonitorRoot<Launch<BaseChangeCountOutput, BaseInput>>> wazuhFileRootList = Arrays.stream(response.getHits()
                .getHits()).map(hit ->
                JSON.parseObject(hit.getSourceAsString(), new TypeReference<WazuhMonitorRoot<Launch<BaseChangeCountOutput, BaseInput>>>() {
                })).collect(Collectors.toList());

        List<JSONObject> jsonObjects = new ArrayList<>();
        if (CollectionUtils.isEmpty(wazuhFileRootList)) {
            return jsonObjects;
        }

        wazuhFileRootList.forEach(launchModel -> {
            if (Objects.nonNull(launchModel.getData()) && Objects.nonNull(launchModel.getData().getOutput())) {
                BaseChangeCountOutput changedCntOutput = launchModel.getData().getOutput();
                if (StringUtils.isNotBlank(changedCntOutput.getData())) {
                    byte[] raw = TextUtils.rawDecodeBase64(StringUtils.strip(changedCntOutput.getData().trim(), "\""));
                    String output = TextUtils.decompress(raw);
                    JSONObject jsonObject = JSON.parseObject(output);

                    Date date = launchModel.getTimestamp();
                    if (Objects.nonNull(date)) {
                        jsonObject.put("time", date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    }
                    jsonObject.put("agent_ip", launchModel.getAgent().getIp());
                    jsonObject.put("agent_name", launchModel.getAgent().getName());
                    jsonObjects.add(jsonObject);
                }
            }
        });
        return jsonObjects;
    }

    /**
     * 查询LAUNCH_ID通用方法，返回SearchResponse
     * @param request
     * @param launchId
     * @return
     */
    private SearchResponse querySICLaunchEventsWithTimeRange(WazuhHostMonitorPageRequest request, String launchId) {
        var sourceBuilder = buildSearchSourceBuilder(request);
        var boolQueryBuilder = buildBoolQueryBuilder(request);
        if (StringUtils.isNotEmpty(launchId)) {
            boolQueryBuilder.must(QueryBuilders.termQuery(CUSTOM_PROBE_ID_PATH, launchId));
        }
        var searchRequest = buildSearchRequest(request, sourceBuilder, boolQueryBuilder);
        return restHighLevelClient.search(searchRequest);
    }

    private LocalDateTime getUtcTime(LocalDateTime time) {
        return TimeUtils.utcToLocalDateTime(TimeUtils.localDateTimeToUtcString(time));
    }

    /**
     * 构造请求的索引
     * @param start
     * @param end
     * @return
     */
    private String[] buildRequestIndex(LocalDateTime start, LocalDateTime end) {
        // 时间为空时返回所有相关索引
        if (Objects.isNull(start) || Objects.isNull(end)) {
            GetIndexRequest getIndexRequest = new GetIndexRequest(WAZUH_ALERT_INDEX_PREFIX + "*");
            var result = restHighLevelClient.getIndices(getIndexRequest);
            return result.getIndices();
        }
        List<String> indexList = indexListBuilder(getUtcTime(start), getUtcTime(end));
        var indexArray = new String[indexList.size()];
        indexList.toArray(indexArray);
        return indexArray;
    }

    /**
     * 构造查询需要的索引
     * @return List<IndexBoost>
     */
    private List<String> indexListBuilder(LocalDateTime start, LocalDateTime end) {
        List<String> dateStringList = fetchFormatterTime(start, end);
        return dateStringList.stream().map(dateString -> WAZUH_ALERT_INDEX_PREFIX + dateString).collect(Collectors.toList());
    }

    /**
     * 获取比如2021-05-01 到2021-05-20.所有的日期，方便构建所有的时间字符串2021.05.01,2021.05.02,....
     * @param startTime startTime
     * @param endTime endTime
     * @return List<String>
     */
    private List<String> fetchFormatterTime(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDate startDate = startTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();
        List<String> dayList = new ArrayList<>();
        while (!startDate.isAfter(endDate)) {
            dayList.add(startDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
            startDate = startDate.plusDays(1);
        }
        return dayList;
    }

    private void buildHostMonitorBoolQueryBuilder(WazuhHostMonitorPageRequest hostMonitorRequest, BoolQueryBuilder boolQueryBuilder) {
        // agentNames
        if (CollectionUtils.isNotEmpty(hostMonitorRequest.getAgentNames())) {
            buildAgentNameQuery(hostMonitorRequest.getAgentNames(), boolQueryBuilder);
        }

        if (Objects.nonNull(hostMonitorRequest.getStartTime()) && Objects.nonNull(hostMonitorRequest.getEndTime())) {
            //构造时间条件
            boolQueryBuilder.must(QueryBuilders.rangeQuery("@timestamp")
                    .from(hostMonitorRequest.getStartTime().atZone(ZoneId.systemDefault()).format(EsDateFormatConfig.esDateTimeFormatter))
                    .to(hostMonitorRequest.getEndTime().atZone(ZoneId.systemDefault()).format(EsDateFormatConfig.esDateTimeFormatter)));
        }
    }

    private void buildFileEventBoolQueryBuilder(WazuhEventRequest eventRequest, BoolQueryBuilder boolQueryBuilder) {
        // ruleGroups
        if (!CollectionUtils.isEmpty(eventRequest.getRuleGroups())) {
            buildRuleGroupQuery(eventRequest.getRuleGroups(), boolQueryBuilder);
        }

        // ruleIds
        if (!CollectionUtils.isEmpty(eventRequest.getRuleIds())) {
            buildRuleIdsQuery(eventRequest.getRuleIds(), boolQueryBuilder);
        }

        if (!CollectionUtils.isEmpty(eventRequest.getAgentNames())) {
            buildAgentNameQuery(eventRequest.getAgentNames(), boolQueryBuilder);
        }

        // keywords
        if (!CollectionUtils.isEmpty(eventRequest.getKeyword())) {
            buildKeywordsQuery(eventRequest.getKeyword(), boolQueryBuilder);
        }

        // levels
        if (!CollectionUtils.isEmpty(eventRequest.getLevels())) {
            buildLevelQuery(eventRequest.getLevels(), boolQueryBuilder);
        }

        // path
        if (StringUtils.isNotEmpty(eventRequest.getPath())) {
            buildPathQuery(eventRequest.getPath(), boolQueryBuilder);
        }

        // probeIds
        if (CollectionUtils.isNotEmpty(eventRequest.getProbeIds())) {
            buildProbeIdQuery(eventRequest.getProbeIds(), boolQueryBuilder);
        }

        if (Objects.nonNull(eventRequest.getStartTime()) && Objects.nonNull(eventRequest.getEndTime())) {
            //时间查询
            boolQueryBuilder.must(QueryBuilders.rangeQuery("timestamp")
                    .from(eventRequest.getStartTime().atZone(ZoneId.systemDefault()).format(EsDateFormatConfig.esDateTimeFormatter))
                    .to(eventRequest.getEndTime().atZone(ZoneId.systemDefault()).format(EsDateFormatConfig.esDateTimeFormatter)));
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

    private void buildProbeIdQuery(List<String> probeIds, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery(CUSTOM_PROBE_ID_PATH, probeIds));
    }

    private void buildLevelQuery(List<Integer> levels, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("rule.level", levels));
    }

    private SearchRequest buildSearchRequest(WazuhEventRequest request, SearchSourceBuilder sourceBuilder, BoolQueryBuilder boolQueryBuilder) {
        String[] indexArray = buildRequestIndex(request.getStartTime(), request.getEndTime());
        var searchRequest = new SearchRequest(indexArray);

        searchRequest.source(sourceBuilder.query(boolQueryBuilder));

        if (request.isScroll()) {
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(35L));
            searchRequest.scroll(scroll);
        }
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        return searchRequest;
    }

    private SearchRequest buildSearchRequest(WazuhHostMonitorPageRequest request, SearchSourceBuilder sourceBuilder, BoolQueryBuilder boolQueryBuilder) {
        String[] indexArray = buildRequestIndex(request.getStartTime(), request.getEndTime());
        var searchRequest = new SearchRequest(indexArray);

        searchRequest.source(sourceBuilder.query(boolQueryBuilder));

        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        return searchRequest;
    }

    private SearchSourceBuilder buildSearchSourceBuilder(WazuhEventRequest request) {
        var sourceBuilder = new SearchSourceBuilder();
        // 按时间排序
        if (request.isDesc()) {
            sourceBuilder.sort("timestamp", SortOrder.DESC);
        } else {
            sourceBuilder.sort("timestamp", SortOrder.ASC);
        }
        if (request.isTrackTotalHits()) {
            sourceBuilder.trackTotalHits(true);
        }
        // 获取前TopSize条数据
        if (Objects.nonNull(request.getTopSize())) {
            sourceBuilder.from(0).size(request.getTopSize());
        }
        //处理分页
        processPageable(request, sourceBuilder);
        return sourceBuilder;
    }

    private SearchSourceBuilder buildSearchSourceBuilder(WazuhHostMonitorPageRequest request) {
        var sourceBuilder = new SearchSourceBuilder();
        // 按时间排序
        sourceBuilder.sort("timestamp", SortOrder.DESC);
        //处理分页
        processPageable(request, sourceBuilder);
        return sourceBuilder;
    }

    private BoolQueryBuilder buildBoolQueryBuilder(WazuhEventRequest request) {
        var boolQueryBuilder = QueryBuilders.boolQuery();
        //构造查询条件
        buildFileEventBoolQueryBuilder(request, boolQueryBuilder);
        return boolQueryBuilder;
    }

    private BoolQueryBuilder buildBoolQueryBuilder(WazuhHostMonitorPageRequest request) {
        var boolQueryBuilder = QueryBuilders.boolQuery();
        //构造查询条件
        buildHostMonitorBoolQueryBuilder(request, boolQueryBuilder);
        return boolQueryBuilder;
    }

    private CountRequest buildCountRequest(WazuhEventRequest request) {
        String[] indexArray = buildRequestIndex(request.getStartTime(), request.getEndTime());
        var countRequest = new CountRequest(indexArray);

        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        //构造查询条件
        buildFileEventBoolQueryBuilder(request, boolQueryBuilder);
        countRequest.source(sourceBuilder.query(boolQueryBuilder));

        //配置索引为空
        countRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        return countRequest;
    }

    private DeleteByQueryRequest buildDeleteRequest(WazuhEventRequest request) {
        String[] indexArray = buildRequestIndex(request.getStartTime(), request.getEndTime());
        var deleteRequest = new DeleteByQueryRequest(indexArray);

        var boolQueryBuilder = QueryBuilders.boolQuery();

        //构造查询条件
        buildFileEventBoolQueryBuilder(request, boolQueryBuilder);
        deleteRequest.setQuery(boolQueryBuilder);

        deleteRequest.setIndicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        return deleteRequest;
    }

    /**
     * 处理分页
     * @param eventRequest
     * @param sourceBuilder
     * @return
     */
    private void processPageable(WazuhEventRequest eventRequest, SearchSourceBuilder sourceBuilder) {
        if (Objects.isNull(eventRequest.getPageNum())) {
            eventRequest.setPageNum(1);
        }
        if (Objects.isNull(eventRequest.getPageSize())) {
            eventRequest.setPageSize(100);
        }

        int offset = (eventRequest.getPageNum() - 1) * (eventRequest.getPageSize());
        sourceBuilder.from(Math.min(offset, 10000));
        if ((offset + eventRequest.getPageSize()) > 10000) {
            sourceBuilder.size(Math.max(10000 - offset, 0));
        } else {
            sourceBuilder.size(eventRequest.getPageSize());
        }
    }

    private void processPageable(WazuhHostMonitorPageRequest hostMonitorRequest, SearchSourceBuilder sourceBuilder) {
        int offset = (hostMonitorRequest.getPageNum() - 1) * (hostMonitorRequest.getPageSize());
        sourceBuilder.from(Math.min(offset, 10000));
        if ((offset + hostMonitorRequest.getPageSize()) > 10000) {
            sourceBuilder.size(Math.max(10000 - offset, 0));
        } else {
            sourceBuilder.size(hostMonitorRequest.getPageSize());
        }
    }
}
