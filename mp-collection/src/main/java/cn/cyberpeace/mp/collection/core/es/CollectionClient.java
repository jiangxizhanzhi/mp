package cn.cyberpeace.mp.collection.core.es;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.collection.config.EsDateFormatConfig;
import cn.cyberpeace.mp.collection.config.CollectionCacheConfig;
import cn.cyberpeace.mp.collection.constant.SicLaunchConfig;
import cn.cyberpeace.mp.collection.model.*;
import cn.cyberpeace.mp.collection.model.enums.FileEventType;
import cn.cyberpeace.mp.collection.model.sic.BaseChangeCountOutput;
import cn.cyberpeace.mp.collection.model.sic.BaseInput;
import cn.cyberpeace.mp.collection.model.sic.HostStateOutput;
import cn.cyberpeace.mp.collection.model.sic.Launch;
import cn.cyberpeace.mp.collection.utils.AgentNameUtils;
import cn.cyberpeace.mp.collection.utils.EsResponseParseUtils;
import cn.hutool.core.util.NumberUtil;
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
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@CacheConfig(cacheManager = CollectionCacheConfig.CACHE_MANAGER)
public class CollectionClient {
    @Resource(name = "restHighLevelClientImpl")
    private IRestHighLevelClient restHighLevelClient;

    private static final String WAZUH_ALERT_INDEX_PREFIX = "wazuh-alerts-4.x-";

    private static final String CUSTOM_PROBE_ID_PATH = "data.sic_launch_id";

    public static final String CACHE_KEY_FILE_EVENTS = "file_events";

    public static final String CACHE_KEY_CUSTOM_PROBES = "custom_probes";

    public static final String CACHE_KEY_VUL_INFOS = "vul_infos";

    public static final String CACHE_KEY_VUL_COUNTS = "vul_counts";

    public static final String CACHE_KEY_HOST_STATE = "host_states";

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
     * 查询采集事件分页
     * @param request
     * @return ListObjResult<FileEventModel>
     */
    @Cacheable(value = CACHE_KEY_FILE_EVENTS)
    public ListObjResult<FileEventModel> queryFileEvents(WazuhEventRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return new ListObjResult<>(Collections.emptyList(), 0);
        }
        var sourceBuilder = buildSearchSourceBuilder(request);
        var boolQueryBuilder = buildBoolQueryBuilder(request);
        SearchRequest searchRequest = buildSearchRequest(request, sourceBuilder, boolQueryBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest);
        var wazuhFileRootList = EsResponseParseUtils.parseResponse(response, WazuhFileRoot.class);
        List<FileEventModel> fileEventModelList = wazuhFileRootList.stream().map((this::getFileEventModel)).collect(Collectors.toList());

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
    public ListObjResult<FileEventModel> scrollFileEvents(String scrollId) {
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(11L));
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(scroll);
        SearchResponse response = restHighLevelClient.scroll(scrollRequest);
        var wazuhFileRootList = EsResponseParseUtils.parseResponse(response, WazuhFileRoot.class);
        List<FileEventModel> fileEventModelList = wazuhFileRootList.stream().map((this::getFileEventModel)).collect(Collectors.toList());

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
    public ListObjResult<WazuhProbeCommonModel> queryCustomProbeData(WazuhEventRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return new ListObjResult<>(Collections.emptyList(), 0);
        }
        var sourceBuilder = buildSearchSourceBuilder(request);
        var boolQueryBuilder = buildBoolQueryBuilder(request);
        SearchRequest searchRequest = buildSearchRequest(request, sourceBuilder, boolQueryBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest);
        List<WazuhProbeCommonModel> wazuhModelList = new ArrayList<>();
        if (Objects.isNull(response) || Objects.isNull(response.getHits()) || Objects.isNull(response.getHits().getHits())) {
            return new ListObjResult<>(wazuhModelList, 0);
        }
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
    }

    /**
     * 查询漏洞信息分页
     * @param request
     * @return ListObjResult<WazuhVulnerabilityInfoModel>
     */
    @Cacheable(value = CACHE_KEY_VUL_INFOS)
    public ListObjResult<WazuhVulnerabilityInfoModel> queryVulnerabilityInfo(WazuhEventRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return new ListObjResult<>(Collections.emptyList(), 0);
        }
        var sourceBuilder = buildSearchSourceBuilder(request);
        var boolQueryBuilder = buildBoolQueryBuilder(request);
        SearchRequest searchRequest = buildSearchRequest(request, sourceBuilder, boolQueryBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest);
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
    }

    /**
     * 查询漏洞信息数量
     * @param request
     * @param fieldPath 字段所在路径
     * @param fieldName 字段名称
     * @return List<WazuhVulnerabilityInfoCountModel>
     */
    @Cacheable(value = CACHE_KEY_VUL_COUNTS)
    public List<WazuhVulnerabilityInfoCountModel> queryVulnerabilityCountInfo(WazuhEventRequest request,
                                                                              String fieldPath, String fieldName) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return new ArrayList<>();
        }
        var sourceBuilder = buildSearchSourceBuilder(request);
        sourceBuilder.aggregation(AggregationBuilders.terms(fieldName).field(fieldPath));
        var boolQueryBuilder = buildBoolQueryBuilder(request);
        SearchRequest searchRequest = buildSearchRequest(request, sourceBuilder, boolQueryBuilder);
        List<WazuhVulnerabilityInfoCountModel> vulnerabilityInfoCounts;
        SearchResponse response = restHighLevelClient.search(searchRequest);
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
    }

    /**
     * 查询主机状态信息
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_HOST_STATE)
    public ListObjResult<WazuhHostStateModel> queryHostStateEventsWithTimeRange(WazuhHostMonitorPageRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return new ListObjResult<>(new ArrayList<>(), 0);
        }
        SearchResponse response = querySICLaunchEventsWithTimeRange(request, SicLaunchConfig.LAUNCH_ID_HOST_STATE);
        List<WazuhMonitorRoot<Launch<HostStateOutput, BaseInput>>> wazuhFileRootList = Arrays.stream(response.getHits()
                .getHits()).map(hit ->
                JSON.parseObject(hit.getSourceAsString(), new TypeReference<WazuhMonitorRoot<Launch<HostStateOutput, BaseInput>>>() {
                })).collect(Collectors.toList());

        var hostStateModels = wazuhFileRootList.stream().map(model -> {
            if (Objects.nonNull(model.getData()) && Objects.nonNull(model.getData().getOutput())) {
                HostStateOutput output = model.getData().getOutput();
                WazuhHostStateModel wazuhHostStateModel = convertHostStateOutput2WazuhModel(output);
                wazuhHostStateModel.setAgentId(model.getAgent().getId());
                wazuhHostStateModel.setAgentName(model.getAgent().getName());
                wazuhHostStateModel.setAgentIp(model.getAgent().getIp());
                wazuhHostStateModel.setTime(TimeUtils.dateToLocalDateTime(model.getTimestamp()));
                return wazuhHostStateModel;
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
    public List<WazuhNetworkConnectionModel> queryNetConnEventsWithTimeRange(WazuhHostMonitorPageRequest wazuhMonitorEventsRequest) {
        if (AgentNameUtils.isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return Collections.emptyList();
        }
        // 每个agent取1条数据，做假分页处理
        wazuhMonitorEventsRequest.setPageNum(1);
        wazuhMonitorEventsRequest.setPageSize(wazuhMonitorEventsRequest.getAgentNames().size());
        SearchResponse response = querySICLaunchEventsWithTimeRange(wazuhMonitorEventsRequest, SicLaunchConfig.LAUNCH_ID_NET_CONNS);
        List<JSONObject> jsonObjects = processBaseChangeOutput(response);
        if (CollectionUtils.isEmpty(jsonObjects)) {
            return Collections.emptyList();
        }
        List<WazuhNetworkConnectionModel> list = new ArrayList<>();
        for (JSONObject jsonObject : jsonObjects) {
            JSONArray arr = jsonObject.getJSONArray("values");
            if (Objects.isNull(arr)) {
                continue;
            }
            for (int i = 0; i < arr.size(); i++) {
                JSONObject data = arr.getJSONObject(i);
                WazuhNetworkConnectionModel netConnModel = JSON.toJavaObject(data, WazuhNetworkConnectionModel.class);
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
    public List<WazuhProcessModel> queryProcessesWithTimeRange(WazuhHostMonitorPageRequest wazuhMonitorEventsRequest) {
        if (AgentNameUtils.isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return Collections.emptyList();
        }
        // 每个agent取1条数据，做假分页处理
        wazuhMonitorEventsRequest.setPageNum(1);
        wazuhMonitorEventsRequest.setPageSize(wazuhMonitorEventsRequest.getAgentNames().size());
        SearchResponse response = querySICLaunchEventsWithTimeRange(wazuhMonitorEventsRequest, SicLaunchConfig.LAUNCH_ID_PROCESSES);
        List<JSONObject> jsonObjects = processBaseChangeOutput(response);
        if (CollectionUtils.isEmpty(jsonObjects)) {
            return Collections.emptyList();
        }
        List<WazuhProcessModel> list = new ArrayList<>();
        for (JSONObject jsonObject : jsonObjects) {
            JSONArray arr = jsonObject.getJSONArray("values");
            if (Objects.isNull(arr)) {
                continue;
            }
            for (int i = 0; i < arr.size(); i++) {
                JSONObject data = arr.getJSONObject(i);
                WazuhProcessModel processModel = JSON.toJavaObject(data, WazuhProcessModel.class);
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
    public List<WazuhSystemUserModel> querySystemUserWithTimeRange(WazuhHostMonitorPageRequest wazuhMonitorEventsRequest) {
        if (AgentNameUtils.isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return Collections.emptyList();
        }
        // 取agent个数条数据，做假分页处理
        wazuhMonitorEventsRequest.setPageNum(1);
        wazuhMonitorEventsRequest.setPageSize(wazuhMonitorEventsRequest.getAgentNames().size());
        SearchResponse response = querySICLaunchEventsWithTimeRange(wazuhMonitorEventsRequest, SicLaunchConfig.LAUNCH_ID_SYSTEM_USERS);
        List<JSONObject> jsonObjects = processBaseChangeOutput(response);
        if (CollectionUtils.isEmpty(jsonObjects)) {
            return Collections.emptyList();
        }
        List<WazuhSystemUserModel> list = new ArrayList<>();
        for (JSONObject jsonObject : jsonObjects) {
            JSONArray values = jsonObject.getJSONArray("values");
            JSONArray modified = jsonObject.getJSONArray("modified");
            JSONArray deleted = jsonObject.getJSONArray("deleted_prev");
            JSONArray added = jsonObject.getJSONArray("added");
            list.addAll(processJSONArray(values, jsonObject, StringUtils.EMPTY));
            list.addAll(processJSONArray(modified, jsonObject, "modify"));
            list.addAll(processJSONArray(deleted, jsonObject, "delete"));
            list.addAll(processJSONArray(added, jsonObject, "add"));
        }
        return list;
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
        return aggsChangedCountWithTimeRange(request, SicLaunchConfig.LAUNCH_ID_ENV_VAR);
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
        return aggsChangedCountWithTimeRange(request, SicLaunchConfig.LAUNCH_ID_SYSTEM_USERS);
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
        return aggsChangedCountWithTimeRange(request, SicLaunchConfig.LAUNCH_ID_PROCESSES);
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
        return aggsChangedCountWithTimeRange(request, SicLaunchConfig.LAUNCH_ID_NET_CONNS);
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

    private List<WazuhSystemUserModel> processJSONArray(JSONArray jsonArray, JSONObject jsonObject, String acton) {
        List<WazuhSystemUserModel> list = new ArrayList<>();
        if (Objects.isNull(jsonArray)) {
            return list;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject data = jsonArray.getJSONObject(i);
            WazuhSystemUserModel userModel = JSON.toJavaObject(data, WazuhSystemUserModel.class);
            if (Objects.nonNull(jsonObject.get("time"))) {
                userModel.setTime((LocalDateTime) jsonObject.get("time"));
            }
            if (Objects.nonNull(jsonObject.get("agent_ip"))) {
                userModel.setAgentIp((String) jsonObject.get("agent_ip"));
                userModel.setAgentName((String) jsonObject.get("agent_name"));
            }
            userModel.setDescription("USER MODIFIED");
            userModel.setAction(acton);
            list.add(userModel);
        }
        return list;
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
        if (Objects.isNull(start) || Objects.isNull(end)) {
            return new String[0];
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

    private void buildFileEventBoolQueryBuilder(WazuhEventRequest wazuhEventRequest, BoolQueryBuilder boolQueryBuilder) {
        // ruleGroups
        if (!CollectionUtils.isEmpty(wazuhEventRequest.getRuleGroups())) {
            buildRuleGroupQuery(wazuhEventRequest.getRuleGroups(), boolQueryBuilder);
        }

        // ruleIds
        if (!CollectionUtils.isEmpty(wazuhEventRequest.getRuleIds())) {
            buildRuleIdsQuery(wazuhEventRequest.getRuleIds(), boolQueryBuilder);
        }

        if (!CollectionUtils.isEmpty(wazuhEventRequest.getAgentNames())) {
            buildAgentNameQuery(wazuhEventRequest.getAgentNames(), boolQueryBuilder);
        }

        // keywords
        if (!CollectionUtils.isEmpty(wazuhEventRequest.getKeyword())) {
            buildKeywordsQuery(wazuhEventRequest.getKeyword(), boolQueryBuilder);
        }

        // levels
        if (!CollectionUtils.isEmpty(wazuhEventRequest.getLevels())) {
            buildLevelQuery(wazuhEventRequest.getLevels(), boolQueryBuilder);
        }

        // path
        if (StringUtils.isNotEmpty(wazuhEventRequest.getPath())) {
            buildPathQuery(wazuhEventRequest.getPath(), boolQueryBuilder);
        }

        // probeIds
        if (CollectionUtils.isNotEmpty(wazuhEventRequest.getProbeIds())) {
            buildProbeIdQuery(wazuhEventRequest.getProbeIds(), boolQueryBuilder);
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
        SearchRequest searchRequest;
        if (indexArray.length == 0) {
            searchRequest = new SearchRequest();
        } else {
            searchRequest = new SearchRequest(indexArray);
        }
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
        SearchRequest searchRequest;
        if (indexArray.length == 0) {
            searchRequest = new SearchRequest();
        } else {
            searchRequest = new SearchRequest(indexArray);
        }
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
        CountRequest countRequest;
        if (indexArray.length == 0) {
            countRequest = new CountRequest();
        } else {
            countRequest = new CountRequest(indexArray);
        }
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
        DeleteByQueryRequest deleteRequest;
        if (indexArray.length == 0) {
            deleteRequest = new DeleteByQueryRequest();
        } else {
            deleteRequest = new DeleteByQueryRequest(indexArray);
        }
        var boolQueryBuilder = QueryBuilders.boolQuery();

        //构造查询条件
        buildFileEventBoolQueryBuilder(request, boolQueryBuilder);
        deleteRequest.setQuery(boolQueryBuilder);

        deleteRequest.setIndicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        return deleteRequest;
    }

    /**
     * 处理分页
     * @param wazuhEventRequest
     * @param sourceBuilder
     * @return
     */
    private void processPageable(WazuhEventRequest wazuhEventRequest, SearchSourceBuilder sourceBuilder) {
        if (Objects.isNull(wazuhEventRequest.getPageNum())) {
            wazuhEventRequest.setPageNum(1);
        }
        if (Objects.isNull(wazuhEventRequest.getPageSize())) {
            wazuhEventRequest.setPageSize(100);
        }

        int offset = (wazuhEventRequest.getPageNum() - 1) * (wazuhEventRequest.getPageSize());
        sourceBuilder.from(Math.min(offset, 10000));
        if ((offset + wazuhEventRequest.getPageSize()) > 10000) {
            sourceBuilder.size(Math.max(10000 - offset, 0));
        } else {
            sourceBuilder.size(wazuhEventRequest.getPageSize());
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

    /**
     * 采集事件转换
     * @param wazuhFileRoot
     * @return
     */
    private FileEventModel getFileEventModel(WazuhFileRoot wazuhFileRoot) {

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
        builder.agentName(wazuhFileRoot.getAgent().getName())
                .agentId(wazuhFileRoot.getAgent().getId())
                .agentIp(wazuhFileRoot.getAgent().getIp());

        if (Objects.nonNull(wazuhFileRoot.getRule())) {
            //区分文件还是注册表
            builder.description(wazuhFileRoot.getRule().getDescription())
                    .level(wazuhFileRoot.getRule().getLevel())
                    .ruleId(wazuhFileRoot.getRule().getId());
            if (CollectionUtils.isNotEmpty(wazuhFileRoot.getRule().getGroups())) {
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

        if (StringUtils.isBlank(output.getUploadSpeed())) {
            wazuhHostStateModel.setUploadSpeed("0.00KB/s");
        } else {
            wazuhHostStateModel.setUploadSpeed(output.getUploadSpeed());
        }

        if (StringUtils.isBlank(output.getDiskReadBytes())) {
            wazuhHostStateModel.setDiskReadBytes(0);
        } else {
            wazuhHostStateModel.setDiskReadBytes(Long.parseLong(output.getDiskReadBytes()));
        }

        if (StringUtils.isBlank(output.getDiskWriteBytes())) {
            wazuhHostStateModel.setDiskWriteBytes(0);
        } else {
            wazuhHostStateModel.setDiskWriteBytes(Long.parseLong(output.getDiskWriteBytes()));
        }

        return wazuhHostStateModel;
    }

    private Long string2Long(String val) {
        Double doubleVal = Double.valueOf(val);
        return BigDecimal.valueOf(doubleVal).longValue();
    }
}
