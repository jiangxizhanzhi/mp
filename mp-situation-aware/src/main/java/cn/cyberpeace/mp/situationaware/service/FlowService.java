package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.situationaware.BO.TeamFlowInfo;
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
import cn.cyberpeace.mp.situationaware.request.PageFlowRequest;
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
import org.elasticsearch.index.query.QueryBuilder;
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
import java.util.Date;
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
public class FlowService extends CollectionBaseService {

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private WazuhRepository wazuhRepository;

    private static final String WAZUH_ALERT_INDEX_PREFIX = "wazuh-alerts-4.x-";

    private LocalDateTime getUtcTime(LocalDateTime time) {
        return TimeUtils.utcToLocalDateTime(TimeUtils.localDateTimeToUtcString(time));
    }

    public static final String CACHE_KEY_FLOW_INFO = "flow_info";


    /**
     * 根据条件来筛选
     *
     * @param pageFlowRequest
     * @return ListObjServiceResult<List<TeamFlowInfo>>
     */
    @Cacheable(value = CACHE_KEY_FLOW_INFO)
    public ListObjServiceResult<List<TeamFlowInfo>> queryFlowPage(PageFlowRequest pageFlowRequest) {
        log.info("{}", pageFlowRequest);
        //根据日期构造索引
        String[] indexArray = buildRequestIndex(pageFlowRequest.getStartTime(), pageFlowRequest.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        //构造查询条件
        buildFileEventBoolQueryBuilder(pageFlowRequest, boolQueryBuilder, null);
        // 按时间降序
        sourceBuilder.sort("timestamp", SortOrder.DESC);
        sourceBuilder.trackTotalHits(true);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));

        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        //处理分页
        processPageable(pageFlowRequest, indexArray, boolQueryBuilder, sourceBuilder);

        log.info("elasticsearch JSON query index:{}, DSL:\n{}", indexArray, searchRequest.source().toString());
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<WazuhFileRoot> wazuhFileRootList = Arrays.stream(response.getHits().getHits()).map(hit -> JSONObject.parseObject(hit.getSourceAsString(), WazuhFileRoot.class)).collect(Collectors.toList());
            List<FileEventModel> fileEventModelList = wazuhFileRootList.stream().map((root -> this.getFileEventModel(root, null))).collect(Collectors.toList());

            List<TeamFlowInfo> teamFlowInfos = Arrays.stream(response.getHits().getHits()).map().collect(Collectors.toList());
            long total = 0L;
            if (Objects.nonNull(response.getHits()) && Objects.nonNull(response.getHits().getTotalHits())) {
                total = response.getHits().getTotalHits().value;
            }
            return new ListObjServiceResult<>(new ListObjResult<>(List.of(teamFlowInfos), (int)total)).asResult();
        } catch (IOException e) {
            log.error("es queryFileEvents error: {} ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    private void buildFileEventBoolQueryBuilder(PageFlowRequest pageFlowRequest,
                                                BoolQueryBuilder boolQueryBuilder, MultiValueMap<String, String> allAgentMacAddr) {
        //构造查询条件
        //源ip
        if ((pageFlowRequest.getSourceIp() != null) && (pageFlowRequest.getSourceIp() != "")) {
            buildRuleSourceIpQuery(pageFlowRequest.getSourceIp(), boolQueryBuilder);
        }

    }

    /**
     * 处理分页
     *
     * @param pageFlowRequest
     * @param indexArray
     * @param boolQueryBuilder
     * @param sourceBuilder
     * @return
     */
    private int processPageable(PageFlowRequest pageFlowRequest, String[] indexArray,
                                BoolQueryBuilder boolQueryBuilder, SearchSourceBuilder sourceBuilder) {

        if(Objects.isNull(pageFlowRequest.getPage())) {
            pageFlowRequest.setPage(1);
        }
        if(Objects.isNull(pageFlowRequest.getPageSize())) {
            pageFlowRequest.setPageSize(100);
        }

        int offset = (pageFlowRequest.getPage() - 1) * (pageFlowRequest.getPageSize());
        sourceBuilder.from(Math.min(offset, 10000));
        if ((offset + pageFlowRequest.getPageSize()) > 10000) {
            sourceBuilder.size(Math.max(10000 - offset, 0));
        } else {
            sourceBuilder.size(pageFlowRequest.getPageSize());
        }
        int total = queryTotalCount(indexArray, boolQueryBuilder);
        if (10000 < total) {
            total = 10000;
        }
        return total;
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
                    .level(wazuhFileRoot.getRule().getLevel())
                    .ruleId(wazuhFileRoot.getRule().getId());
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

    private void buildRuleSourceIpQuery(String sourceIp, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.should(QueryBuilders.matchQuery("source.ip", sourceIp));
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

    private Long string2Long(String val) {
        Double doubleVal = Double.valueOf(val);
        Long longVal = BigDecimal.valueOf(doubleVal).longValue();
        return longVal;
    }

}
