package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.collection.BO.FlowDetailInfo;
import cn.cyberpeace.mp.collection.BO.TeamFlowInfo;
import cn.cyberpeace.mp.collection.config.SituationCacheConfig;
import cn.cyberpeace.mp.collection.model.*;
import cn.cyberpeace.mp.collection.request.FlowQueryRequest;
import cn.cyberpeace.mp.collection.request.PageFlowRequest;
import cn.cyberpeace.mp.collection.core.es.CollectionBaseService;
import com.alibaba.fastjson.JSONObject;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 *
 */
@Service
@Slf4j
@CacheConfig(cacheManager = SituationCacheConfig.CACHE_MANAGER)
public class FlowService extends CollectionBaseService {

    @Resource(name = "flowRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    private static final String ARKIME_SESSIONS3_ = "arkime_sessions3_";

    public static final String CACHE_KEY_FLOW_INFO = "flow_info";

    /**
     *根据条件筛选流量信息（不传条件默认查询全部）
     *
     * @param pageFlowRequest
     * @return ListObjServiceResult<TeamFlowInfo>
     */
    @Cacheable(value = CACHE_KEY_FLOW_INFO)
    public ListObjServiceResult<TeamFlowInfo> queryFlowPage(PageFlowRequest pageFlowRequest) {
        log.info("{}", pageFlowRequest);
        String[] indexArray = {"mp_1659956876654_sessions3-220808"};//buildRequestIndex(pageFlowRequest.getStartTime(), pageFlowRequest.getEndTime());//
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        buildFlowPageQueryBuilder(pageFlowRequest, boolQueryBuilder);
        sourceBuilder.sort("@timestamp", SortOrder.DESC);
        sourceBuilder.trackTotalHits(true);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));

        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        processPageable(pageFlowRequest, indexArray, boolQueryBuilder, sourceBuilder);

        log.info("elasticsearch JSON query index:{}, DSL:\n{}", indexArray, searchRequest.source().toString());
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            List<FlowFileRoot> flowFileRoots = Arrays.stream(response.getHits().getHits()).map(hit -> JSONObject.parseObject(hit.getSourceAsString(), FlowFileRoot.class)).collect(Collectors.toList());

            for (int i = 0; i < flowFileRoots.size(); i ++) {
                flowFileRoots.get(i).setId(response.getHits().getAt(i).getId());
            }

            List<TeamFlowInfo> teamFlowInfos = flowFileRoots.stream().map(flowFile -> this.getTeamFlowModel(flowFile)).collect(Collectors.toList());
            long total = 0L;
            if (Objects.nonNull(response.getHits()) && Objects.nonNull(response.getHits().getTotalHits())) {
                total = response.getHits().getTotalHits().value;
            }

            //TODO:用这样做吗？返回页数和total中最小的那个值
            total = total > pageFlowRequest.getPageSize() ? (pageFlowRequest.getPageSize()) : total;
            return new ListObjServiceResult<>(new ListObjResult<>(teamFlowInfos, (int)total)).asResult();
        } catch (IOException e) {
            log.error("es queryFileEvents error: {} ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    public ServiceResult<FlowDetailInfo> queryFlowDetail(FlowQueryRequest flowQueryRequest) {
        log.info("{}", flowQueryRequest);
        String[] indexArray = {"mp_1659956876654_sessions3-220808"};//buildRequestIndex(flowQueryRequest.getTime(), flowQueryRequest.getTime());//
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        buildFlowQueryBuilder(flowQueryRequest, boolQueryBuilder);
        sourceBuilder.sort("@timestamp", SortOrder.DESC);
        sourceBuilder.trackTotalHits(true);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));

        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        log.info("elasticsearch JSON query index:{}, DSL:\n{}", indexArray, searchRequest.source().toString());
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<FlowFileRoot> flowFileRoots = Arrays.stream(response.getHits().getHits()).map(hit -> JSONObject.parseObject(hit.getSourceAsString(), FlowFileRoot.class)).collect(Collectors.toList());

            if (Objects.isNull(flowFileRoots)) {
                return new ServiceResult<>(new FlowDetailInfo()).asResult();
            }
            FlowDetailInfo flowDetailInfo = this.getFlowDetailModel(flowFileRoots.get(0));
            return new ServiceResult<>(flowDetailInfo).asResult();
        } catch (IOException e) {
            log.error("es queryFileEvents error: {} ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    private void buildFlowPageQueryBuilder(PageFlowRequest pageFlowRequest, BoolQueryBuilder boolQueryBuilder) {
        if (!CollectionUtils.isEmpty(pageFlowRequest.getSourceIps())) {
            buildRuleSourceIpQuery(pageFlowRequest.getSourceIps(), boolQueryBuilder);
        }

        if (!CollectionUtils.isEmpty(pageFlowRequest.getTargetIps())) {
            buildRuleTargetIpQuery(pageFlowRequest.getTargetIps(), boolQueryBuilder);
        }

        if (!StringUtils.isEmpty(pageFlowRequest.getPort())) {
            buildRulePortQuery(pageFlowRequest.getPort(), boolQueryBuilder);
        }

        if (!StringUtils.isEmpty(pageFlowRequest.getProtocol())) {
            buildRuleProtocolQuery(pageFlowRequest.getProtocol(), boolQueryBuilder);
        }

        Long starTimestamp = Objects.nonNull(pageFlowRequest.getStartTime()) ?
                pageFlowRequest.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0L;
        Long endTimestamp = Objects.nonNull(pageFlowRequest.getEndTime()) ?
                pageFlowRequest.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0L;
        if (starTimestamp <= endTimestamp) {
            if ((starTimestamp > 0L) && (endTimestamp > 0L)) {
                buildRuleStarEndTimeQuery(starTimestamp, endTimestamp, boolQueryBuilder);
            }

            if (starTimestamp > 0L) {
                buildRuleStarTimeQuery(starTimestamp, boolQueryBuilder);
            }

            if (endTimestamp > 0L) {
                buildRuleEndTimeQuery(endTimestamp, boolQueryBuilder);
            }
        }
    }

    private void buildFlowQueryBuilder(FlowQueryRequest flowQueryRequest, BoolQueryBuilder boolQueryBuilder) {
        if (!StringUtils.isEmpty(flowQueryRequest.getDataId())) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("_id", flowQueryRequest.getDataId()));
        }
    }
    private void buildRuleSourceIpQuery(List<String> sourceIps, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("source.ip", sourceIps));
    }

    private void buildRuleTargetIpQuery(List<String> targetIps, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("destination.ip", targetIps));
    }

    private void buildRulePortQuery(String port, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("source.port", port));
    }

    private void buildRuleProtocolQuery(String protocol, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("protocol", protocol));
    }

    private void buildRuleStarEndTimeQuery(Long startTime, Long endTime, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("@timestamp").gte(startTime).lte(endTime));
    }

    private void buildRuleStarTimeQuery(Long startTime, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("@timestamp").gte(startTime));
    }

    private void buildRuleEndTimeQuery(Long endTime, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("@timestamp").lte(endTime));
    }
    /**
     * 转换为TeamFlowInfo类
     * @param flowFileRoot
     * @return TeamFlowInfo
     */
    private TeamFlowInfo getTeamFlowModel(FlowFileRoot flowFileRoot) {
        TeamFlowInfo.TeamFlowInfoBuilder builder = TeamFlowInfo.builder();
        if (Objects.nonNull(flowFileRoot.getSource())) {
            builder.sourceIp(flowFileRoot.getSource().getIp());
            builder.sourcePort(flowFileRoot.getSource().getPort().toString());
            builder.size(flowFileRoot.getSource().getBytes());
        }

        if (Objects.nonNull(flowFileRoot.getDestination())) {
            builder.targetIp(flowFileRoot.getDestination().getIp());
        }

        if (Objects.nonNull(flowFileRoot.getProtocol())) {
            builder.protocol(flowFileRoot.getProtocol().get(0));
        }

        if (!StringUtils.isEmpty(flowFileRoot.getId())) {
            builder.dataId(flowFileRoot.getId());
        }
        return builder.time(TimeUtils.timestampToLocalDateTime(flowFileRoot.getTimestamp())).build();
    }

    /**
     * 转换为FlowDetailInfo类
     * @param flowFileRoot
     * @return TeamFlowInfo
     */
    private FlowDetailInfo getFlowDetailModel(FlowFileRoot flowFileRoot) {
        FlowDetailInfo.FlowDetailInfoBuilder builder = FlowDetailInfo.builder();
        if (Objects.nonNull(flowFileRoot.getSource())) {
            builder.sourceIp(flowFileRoot.getSource().getIp());
            builder.sourcePort(flowFileRoot.getSource().getPort().toString());
            builder.sourceMac(flowFileRoot.getSource().getMac().get(0));
            builder.size(flowFileRoot.getSource().getBytes());
        }

        if (Objects.nonNull(flowFileRoot.getDestination())) {
            builder.targetIp(flowFileRoot.getDestination().getIp());
            builder.targetPort(flowFileRoot.getDestination().getPort().toString());
            builder.targetMac(flowFileRoot.getDestination().getMac().get(0));
        }

        if (Objects.nonNull(flowFileRoot.getProtocol())) {
            builder.protocol(flowFileRoot.getProtocol().get(0));
        }
        return builder.time(TimeUtils.timestampToLocalDateTime(flowFileRoot.getTimestamp())).build();
    }

    /**
     * 分页
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
            pageFlowRequest.setPageSize(10);
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
     *查询结果总数
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
     * 根据时间创建索引
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

    /**
     *
     *
     * @return List<IndexBoost>
     */
    @VisibleForTesting
    public static List<String> indexListBuilder(LocalDateTime start, LocalDateTime end) {
        List<String> dateStringList = fetchFormatterTime(start, end);
        return dateStringList.stream().map(dateString -> ARKIME_SESSIONS3_ + dateString).collect(Collectors.toList());
    }

    /**
     *
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
            dayList.add(startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            startDate = startDate.plusDays(1);
        }
        log.info("dayList:{}", dayList);
        return dayList;
    }

}
