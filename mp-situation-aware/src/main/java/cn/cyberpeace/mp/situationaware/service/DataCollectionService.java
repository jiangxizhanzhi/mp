package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.es.EsConstants;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.model.DataCollectionEventModel;
import cn.cyberpeace.mp.situationaware.model.FileEventModel;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.model.WazuhModel;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorRoot;
import cn.cyberpeace.mp.situationaware.model.enums.DataCollectionType;
import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
import cn.cyberpeace.mp.situationaware.repository.WazuhRepository;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant.ES_DOC_MAXCOUNT;

/**
 * wazuh对接
 */
@Service
@Slf4j
public class DataCollectionService {

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private WazuhRepository wazuhRepository;

    private static final String WAZUH_ALERT_INDEX_PREFIX = "wazuh-alerts-4.x-";

    /**
     * 查询文件事件分页
     *
     * @param wazuhEventRequest
     * @return ListObjResult<FileEventModel>
     */
    public ListObjResult<FileEventModel> queryFileEvents(WazuhEventRequest wazuhEventRequest) {
        log.info("{}", wazuhEventRequest);
        //根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhEventRequest.getStartTime(), wazuhEventRequest.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        MultiValueMap<String, String> multiValueMap = this.queryAllAgentMacAddr();
        //构造查询条件
        buildFileEventBoolQueryBuilder(wazuhEventRequest, boolQueryBuilder, multiValueMap);
        // 按时间降序
        sourceBuilder.sort("timestamp", SortOrder.DESC);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        //处理分页
        int total = processPageable(wazuhEventRequest, indexArray, boolQueryBuilder, sourceBuilder);

        log.info("elasticsearch JSON query:\n{}", searchRequest.source().toString());
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<WazuhFileRoot> wazuhFileRootList = Arrays.stream(response.getHits().getHits()).map(hit -> JSONObject.parseObject(hit.getSourceAsString(), WazuhFileRoot.class)).collect(Collectors.toList());
            List<FileEventModel> fileEventModelList = wazuhFileRootList.stream().map((root -> this.getFileEventModel(root, multiValueMap))).collect(Collectors.toList());
            return new ListObjResult<>(fileEventModelList, total);
        } catch (IOException e) {
            log.error("es queryFileEvents error: {} ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 查询监控数据 查询最近五分钟数据，监控指标频率为 1min, 对五分钟数据按照agentId进行分组，再去最近的时间的数据
     *
     * @param wazuhMonitorEventsRequest wazuhMonitorEventsRequest
     */
    public ListObjResult<WazuhModel> queryMonitorEvents(WazuhMonitorEventsRequest wazuhMonitorEventsRequest) {
        //根据日期构造索引
        String[] indexArray = buildRequestIndex(LocalDateTime.now(), LocalDateTime.now());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        MultiValueMap<String, String> multiValueMap = this.queryAllAgentMacAddr();
        int agentSize = buildMonitorEventsBuilder(wazuhMonitorEventsRequest, boolQueryBuilder, multiValueMap);
        // 按时间降序
        sourceBuilder.sort("timestamp", SortOrder.DESC);
        // 监控数据每个agent一条，所以这里分页大小设为agent数量的2倍
        sourceBuilder.from(0);
        sourceBuilder.size(NumberUtil.equals(agentSize, 0) ? 10 : agentSize * 2);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        log.info("monitor JSON query: {}", searchRequest.source().toString());

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
                    var result = JSONObject.parseObject(monitorMessage, WazuhModel.class);
                    result.setAgentId(model.getAgent().getId());
                    result.setAgentName(model.getAgent().getName());
                    result.setMacIds(multiValueMap.get(model.getAgent().getId()));
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
    public List<DataCollectionEventModel> queryMonitorEventsAgg(WazuhMonitorEventsRequest wazuhMonitorEventsRequest){
        //根据日期构造索引
        String[] indexArray = buildRequestIndex(LocalDateTime.now(), LocalDateTime.now());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        MultiValueMap<String, String> multiValueMap = this.queryAllAgentMacAddr();
        buildMonitorEventsAggBuilder(wazuhMonitorEventsRequest, boolQueryBuilder, multiValueMap);
        // 按时间降序
        sourceBuilder.sort("timestamp", SortOrder.DESC);
        // 监控数据每个agent一条，所以这里分页大小设为agent数量的2倍
        sourceBuilder.from(0);
        sourceBuilder.size(ES_DOC_MAXCOUNT);

        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        log.info("queryMonitorEventsAgg JSON query: {}", searchRequest.source().toString());

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
    public List<WazuhModel> queryMonitorEventsWithTimeRange(WazuhMonitorEventsRequest wazuhMonitorEventsRequest) {
        //根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhMonitorEventsRequest.getStartTime(), wazuhMonitorEventsRequest.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        MultiValueMap<String, String> multiValueMap = this.queryAllAgentMacAddr();
        buildMonitorEventsBuilderTimeRange(wazuhMonitorEventsRequest, boolQueryBuilder, multiValueMap);
        // 按时间降序
        sourceBuilder.sort("timestamp", SortOrder.DESC);
        if (Objects.nonNull(wazuhMonitorEventsRequest.getPageNum()) && Objects.nonNull(wazuhMonitorEventsRequest.getPageSize())) {
            if (wazuhMonitorEventsRequest.getPageNum() * wazuhMonitorEventsRequest.getPageSize() > 10000) {
                throw new MpRuntimeException("不支持深度分页查询！");
            }
            sourceBuilder.from(wazuhMonitorEventsRequest.getPageNum() - 1 > 0 ? wazuhMonitorEventsRequest.getPageNum() - 1 : 0);
            sourceBuilder.size(wazuhMonitorEventsRequest.getPageSize());
        } else {
            sourceBuilder.size(ES_DOC_MAXCOUNT);
        }
        // 监控数据每个agent一条，所以这里分页大小设为agent数量的2倍
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        log.info("monitor JSON query: {}", searchRequest.source().toString());

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            List<WazuhMonitorRoot> wazuhFileRootList = Arrays.stream(response.getHits().getHits()).map(hit -> JSONObject.parseObject(hit.getSourceAsString(), WazuhMonitorRoot.class)).collect(Collectors.toList());
            var wazuhModelList = wazuhFileRootList.stream()
                .filter(model -> model.getFullLog().contains("MonitorMessage:")).map(model -> {
                    var monitorMessage = model.getFullLog().substring("MonitorMessage:".length());
                    var result = JSONObject.parseObject(monitorMessage, WazuhModel.class);
                    result.setAgentId(model.getAgent().getId());
                    result.setAgentName(model.getAgent().getName());
                    result.setMacIds(multiValueMap.get(model.getAgent().getId()));
                    result.setTime(TimeUtils.dateToLocalDateTime(model.getTimestamp()));
                    return result;
                }).collect(Collectors.toList());

            //process page
            return wazuhModelList;
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
        boolQueryBuilder.must(QueryBuilders.termQuery("rule.id", "900002"));
        int agentSize = 0;

        if (!CollectionUtils.isEmpty(wazuhEventRequest.getMacIds())) {
            agentSize = buildAgentIdQuery(wazuhEventRequest.getMacIds(), boolQueryBuilder, allAgentMacAddr);
        }

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
        boolQueryBuilder.must(QueryBuilders.termQuery("rule.id", "900002"));

        if (!CollectionUtils.isEmpty(wazuhEventRequest.getMacIds())) {
            buildAgentIdQuery(wazuhEventRequest.getMacIds(), boolQueryBuilder, allAgentMacAddr);
        }

        if (!CollectionUtils.isEmpty(wazuhEventRequest.getAgentNames())) {
            buildAgentNameQuery(wazuhEventRequest.getAgentNames(), boolQueryBuilder);
        }

        //构造事件条件
        boolQueryBuilder.must(QueryBuilders.rangeQuery("@timestamp")
            .from(TimeUtils.localDateTimeToString(wazuhEventRequest.getStartTime(), EsConstants.esDateTimeFormatter))
            .to(TimeUtils.localDateTimeToString(wazuhEventRequest.getEndTime(), EsConstants.esDateTimeFormatter)));
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
                    .action(wazuhFileRoot.getSyscheck().getEvent());
        }

        if (Objects.nonNull(wazuhFileRoot.getAgent())) {
            builder.macIds(multiValueMap.get(wazuhFileRoot.getAgent().getId()))
                    .agentName(wazuhFileRoot.getAgent().getName())
                    .agentId(wazuhFileRoot.getAgent().getId());
        }

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
        int offset = (wazuhEventRequest.getPageNum() - 1) * (wazuhEventRequest.getPageSize());
        sourceBuilder.from(offset);
        sourceBuilder.size(wazuhEventRequest.getPageSize());
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

        if (!CollectionUtils.isEmpty(wazuhEventRequest.getMacIds())) {
            //构造agentId
            buildAgentIdQuery(wazuhEventRequest.getMacIds(), boolQueryBuilder, allAgentMacAddr);
        }

        if (!CollectionUtils.isEmpty(wazuhEventRequest.getAgentNames())) {
            buildAgentNameQuery(wazuhEventRequest.getAgentNames(), boolQueryBuilder);
        }

        // catagories
        if(!CollectionUtils.isEmpty(wazuhEventRequest.getCatagories())) {
            buildCatagoryQuery(wazuhEventRequest.getCatagories(), boolQueryBuilder);
        }

        // keywords
        if(!CollectionUtils.isEmpty(wazuhEventRequest.getKeywords())) {
            buildKeywordsQuery(wazuhEventRequest.getKeywords(), boolQueryBuilder);
        }

        // levels
        if(!CollectionUtils.isEmpty(wazuhEventRequest.getLevels())) {
            buildLevelQuery(wazuhEventRequest.getLevels(), boolQueryBuilder);
        }


        //时间查询
        boolQueryBuilder.must(QueryBuilders.rangeQuery("timestamp")
                .from(TimeUtils.localDateTimeToString(wazuhEventRequest.getStartTime(), EsConstants.esDateTimeFormatter))
                .to(TimeUtils.localDateTimeToString(wazuhEventRequest.getEndTime(), EsConstants.esDateTimeFormatter)));
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
        long betweenDay = ChronoUnit.DAYS.between(startTime, endTime);
        long month = ChronoUnit.MONTHS.between(startTime, endTime);
        int days = (int) betweenDay;
        if (month > 1) {
            return IntStream.range(0, days + 2).boxed().map(startTime::plusDays).map((time) -> time.format(DateTimeFormatter.ofPattern("yyyy.MM.*")))
                .distinct().collect(Collectors.toList());
        }
        return IntStream.range(0, days + 1).boxed().map(startTime::plusDays).map((time) -> time.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
            .distinct().collect(Collectors.toList());
    }
}
