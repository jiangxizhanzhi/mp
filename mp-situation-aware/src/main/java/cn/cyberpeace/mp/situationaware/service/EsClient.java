package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.situationaware.config.EsDateFormatConfig;
import cn.cyberpeace.mp.situationaware.model.*;
import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
import cn.cyberpeace.mp.situationaware.model.sic.BaseInput;
import cn.cyberpeace.mp.situationaware.model.sic.Launch;
import cn.cyberpeace.mp.situationaware.utils.ElasticsearchUtils;
import cn.cyberpeace.mp.situationaware.utils.EsResponseParseUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class EsClient extends CollectionBaseService {
    public EsClient(String esAdress) {
        this.restHighLevelClient = ElasticsearchUtils.restHighLevelClient(esAdress);
    }

    private RestHighLevelClient restHighLevelClient;

    private static final String WAZUH_ALERT_INDEX_PREFIX = "wazuh-alerts-4.x-";

    private static final String CUSTOM_PROBE_ID_PATH = "data.sic_launch_id";

    public static final String CACHE_KEY_FILE_EVENTS = "file_events";

    public static final String CACHE_KEY_CUSTOM_PROBES = "custom_probes";

    public static final String CACHE_KEY_MONITOR_EVENTS = "monitor_events";

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
     * 查询文件事件分页
     *
     * @param request
     * @return ListObjResult<FileEventModel>
     */
    @Cacheable(value = CACHE_KEY_FILE_EVENTS)
    public ListObjServiceResult<FileEventModel> queryFileEvents(WazuhEventRequest request) {
        if (isAgentNamesEmpty(request.getAgentNames())) {
            return new ListObjServiceResult<>(new ListObjResult<>(Collections.emptyList(), 0));
        }
        SearchRequest searchRequest = buildSearchRequest(request, true, SortOrder.DESC);

        SearchResponse response = ElasticsearchUtils.elasticsearchQuery(restHighLevelClient, searchRequest);
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
        return new ListObjServiceResult<>(new ListObjResult<>(fileEventModelList, BigDecimal.valueOf(total).intValue()));
    }

    /**
     * 滚动分页查询文件事件
     *
     * @return ListObjResult<FileEventModel>
     */
    public ListObjServiceResult<FileEventModel> scrollFileEvents(String scrollId) {
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(11L));
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(scroll);
        SearchResponse response = ElasticsearchUtils.elasticsearchScroll(restHighLevelClient, scrollRequest);
        var wazuhFileRootList = EsResponseParseUtils.parseResponse(response, WazuhFileRoot.class);
        List<FileEventModel> fileEventModelList = wazuhFileRootList.stream().map((this::getFileEventModel)).collect(Collectors.toList());

        String responseScrollId = response.getScrollId();
        fileEventModelList.forEach(model -> model.setScrollId(responseScrollId));
        return new ListObjServiceResult<>(new ListObjResult<>(fileEventModelList, (int) response.getHits().getTotalHits().value));
    }

    /**
     * 查询自定义探针采集数据分页
     *
     * @param request
     * @return ListObjResult<WazuhProbeCommonModel>
     */
    @Cacheable(value = CACHE_KEY_CUSTOM_PROBES)
    public ListObjServiceResult<WazuhProbeCommonModel> queryCustomProbeData(WazuhEventRequest request) {
        if (isAgentNamesEmpty(request.getAgentNames())) {
            return new ListObjServiceResult<>(new ListObjResult<>(Collections.emptyList(), 0));
        }
        SearchRequest searchRequest = buildSearchRequest(request, false, SortOrder.DESC);

        SearchResponse response = ElasticsearchUtils.elasticsearchQuery(restHighLevelClient, searchRequest);
        List<WazuhProbeCommonModel> wazuhModelList = new ArrayList<>();
        if (Objects.isNull(response) || Objects.isNull(response.getHits()) || Objects.isNull(response.getHits().getHits())) {
            return new ListObjServiceResult<>(new ListObjResult<>(wazuhModelList, 0));
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
        return new ListObjServiceResult<>(new ListObjResult(wazuhModelList, BigDecimal.valueOf(total).intValue()));
    }

    /**
     * 所有节点的采集事件总数
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_COUNT_BY_AGENTNAME)
    public ServiceResult<Long> countByAgentNames(WazuhEventRequest request) {
        if (isAgentNamesEmpty(request.getAgentNames())) {
            return new ServiceResult<>(0L);
        }
        CountRequest countRequest = buildCountRequest(request);
        CountResponse response = ElasticsearchUtils.elasticsearchCount(restHighLevelClient, countRequest);
        return new ServiceResult<>(response.getCount());
    }

    /**
     * 场景删除之后，删除对应es文档数据
     * @param request
     */
    public ServiceResult<Long> deleteDocByAgentNames(WazuhEventRequest request) {
        if (isAgentNamesEmpty(request.getAgentNames())) {
            return new ServiceResult<>(0L);
        }
        DeleteByQueryRequest deleteRequest = buildDeleteRequest(request);
        BulkByScrollResponse response = ElasticsearchUtils.elasticsearchDelete(restHighLevelClient, deleteRequest);
        return new ServiceResult<>(response.getUpdated());
    }

    private LocalDateTime getUtcTime(LocalDateTime time) {
        return TimeUtils.utcToLocalDateTime(TimeUtils.localDateTimeToUtcString(time));
    }

    /**
     * 构造请求的索引
     *
     * @param start
     * @param end
     * @return
     */
    private String[] buildRequestIndex(LocalDateTime start, LocalDateTime end) {
        List<String> indexList = indexListBuilder(getUtcTime(start), getUtcTime(end));
        var indexArray = new String[indexList.size()];
        indexList.toArray(indexArray);
        return indexArray;
    }

    /**
     * 构造查询需要的索引
     *
     * @return List<IndexBoost>
     */
    private List<String> indexListBuilder(LocalDateTime start, LocalDateTime end) {
        List<String> dateStringList = fetchFormatterTime(start, end);
        return dateStringList.stream().map(dateString -> WAZUH_ALERT_INDEX_PREFIX + dateString).collect(Collectors.toList());
    }

    /**
     * 获取比如2021-05-01 到2021-05-20.所有的日期，方便构建所有的时间字符串2021.05.01,2021.05.02,....
     *
     * @param startTime startTime
     * @param endTime   endTime
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

    private SearchRequest buildSearchRequest(WazuhEventRequest request, boolean trackTotalHits, SortOrder sortOrder) {
        String[] indexArray = buildRequestIndex(request.getStartTime(), request.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        //构造查询条件
        buildFileEventBoolQueryBuilder(request, boolQueryBuilder);
        // 按时间降序
        sourceBuilder.sort("timestamp", sortOrder);
        sourceBuilder.trackTotalHits(trackTotalHits);
        //处理分页
        processPageable(request, sourceBuilder);

        searchRequest.source(sourceBuilder.query(boolQueryBuilder));

        if (request.isScroll()) {
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(35L));
            searchRequest.scroll(scroll);
        }
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        return searchRequest;
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
     *
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

    /**
     * 采集事件转换
     *
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
        builder.agentName(wazuhFileRoot.getAgent().getName()).agentId(wazuhFileRoot.getAgent().getId());

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
}
