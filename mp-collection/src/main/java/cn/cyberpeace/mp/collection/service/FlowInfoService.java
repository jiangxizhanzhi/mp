package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.collection.bo.FlowAttackBO;
import cn.cyberpeace.mp.collection.core.es.IRestHighLevelClient;
import cn.cyberpeace.mp.collection.model.FlowFileRoot;
import cn.cyberpeace.mp.collection.request.FlowQueryRequest;
import cn.cyberpeace.mp.collection.request.PageFlowRequest;
import cn.cyberpeace.mp.collection.util.EsResponseParseUtil;
import cn.cyberpeace.mp.collection.bo.FlowDetailInfo;
import cn.cyberpeace.mp.collection.bo.TeamFlowInfo;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FlowInfoService {

    @Resource(name = "restHighLevelClientImpl")
    private IRestHighLevelClient restHighLevelClient;

    private static final String ARKIME_SESSIONS3_ = "arkime_sessions3-";

    /**
     * 根据条件筛选流量信息（不传条件默认查询全部）
     * @param pageFlowRequest
     * @return ListObjServiceResult<TeamFlowInfo>
     */
    public ListObjServiceResult<TeamFlowInfo> queryFlowPage(PageFlowRequest pageFlowRequest) {
        log.info("{}", pageFlowRequest);

        String[] indexArray = buildEsIndex(pageFlowRequest.getStartTime(), pageFlowRequest.getEndTime());
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

        SearchResponse response = restHighLevelClient.search(searchRequest);

        List<TeamFlowInfo> teamFlowInfos = new ArrayList<>();
        List<FlowFileRoot> flowFileRoots = EsResponseParseUtil.parseResponse(response, FlowFileRoot.class);
        if (CollectionUtil.isEmpty(flowFileRoots)) {
            return new ListObjServiceResult<>(new ListObjResult<>(teamFlowInfos, 0));
        }

        long total = 0L;
        if (Objects.nonNull(response.getHits()) && Objects.nonNull(response.getHits().getTotalHits())) {
            total = response.getHits().getTotalHits().value;
        }


        for (int i = 0; i < flowFileRoots.size(); i++) {
            flowFileRoots.get(i).setId(response.getHits().getAt(i).getId());
            teamFlowInfos.add(this.getTeamFlowModel(flowFileRoots.get(i), pageFlowRequest));
        }

        return new ListObjServiceResult<>(new ListObjResult<>(teamFlowInfos, (int) total)).asResult();
    }

    public ServiceResult<Map<String, Object>> queryFlowDetail(FlowQueryRequest flowQueryRequest) {
        if (StringUtils.isEmpty(flowQueryRequest.getDataId())) {
            return new ServiceResult<>(new HashMap<>());
        }
        log.info("{}", flowQueryRequest);
        String[] indexArray = buildEsIndex(flowQueryRequest.getStartTime(), flowQueryRequest.getEndTime());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        buildFlowQueryBuilder(flowQueryRequest, boolQueryBuilder);

        sourceBuilder.sort("@timestamp", SortOrder.DESC);
        sourceBuilder.trackTotalHits(true);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));

        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        log.info("elasticsearch JSON query index:{}, DSL:\n{}", indexArray, searchRequest.source().toString());

        SearchResponse response = restHighLevelClient.search(searchRequest);
        if (response.getHits().getTotalHits().value < 1L) {
            return new ServiceResult<>(new HashMap<>());
        }
        var flowDetail = JsonCommon.parseMap(response.getHits().getAt(0).getSourceAsString());
        return new ServiceResult<>(flowDetail);
    }

    public ListObjServiceResult<FlowAttackBO> queryFlowAttack() {
        var indexArray = getFlowIndex();
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();
        return null;
    }

    //    /**
//     * 删除从开始时间到结束时间内的文档
//     * @param
//     * @return
//     */
//    @Override
//    public void deleteDoc(LocalDateTime startTime, LocalDateTime endTime) {
//        DeleteByQueryRequest deleteByQueryRequest = buildDeleteRequest(startTime, endTime);
//        restHighLevelClient.allDeleteByQuery(deleteByQueryRequest);
//    }

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

        if (Objects.nonNull(pageFlowRequest.getStartTime())) {
            buildRuleStarTimeQuery(
                    pageFlowRequest.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                    boolQueryBuilder);
        }
        if (Objects.nonNull(pageFlowRequest.getEndTime())) {
            buildRuleEndTimeQuery(
                    pageFlowRequest.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                    boolQueryBuilder);
        }
    }

    private void buildFlowQueryBuilder(FlowQueryRequest flowQueryRequest, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("_id", flowQueryRequest.getDataId()));
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

    private void buildRuleStarTimeQuery(Long startTime, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("@timestamp").gte(startTime));
    }

    private void buildRuleEndTimeQuery(Long endTime, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("@timestamp").lte(endTime));
    }
    
    private void buildSeverityQuery(Long severity, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("suricata.severity").gt(severity));
    }

    private DeleteByQueryRequest buildDeleteRequest(LocalDateTime startTime, LocalDateTime endTime) {
        String[] indexArray = buildEsIndex(startTime, endTime);
        var deleteRequest = new DeleteByQueryRequest(indexArray);

        var boolQueryBuilder = QueryBuilders.boolQuery();

        //构造查询条件
        buildDeleteDocQuery(startTime, endTime, boolQueryBuilder);
        deleteRequest.setQuery(boolQueryBuilder);

        deleteRequest.setIndicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        return deleteRequest;
    }

    private void buildDeleteDocQuery(LocalDateTime startTime, LocalDateTime endTime, BoolQueryBuilder boolQueryBuilder) {
        if (Objects.nonNull(startTime) && Objects.nonNull(endTime)) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("@timestamp")
                    .from(startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .to(endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        } else {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }

    }

    /**
     * 转换为TeamFlowInfo类
     * @param flowFileRoot
     * @return TeamFlowInfo
     */
    private TeamFlowInfo getTeamFlowModel(FlowFileRoot flowFileRoot, PageFlowRequest pageFlowRequest) {
        TeamFlowInfo.TeamFlowInfoBuilder builder = TeamFlowInfo.builder();
        if (Objects.nonNull(flowFileRoot.getSource())) {
            builder.sourceIp(flowFileRoot.getSource().getIp());
            builder.sourcePort(flowFileRoot.getSource().getPort().toString());
            builder.size(flowFileRoot.getSource().getBytes());
        }

        if (Objects.nonNull(flowFileRoot.getDestination())) {
            builder.targetIp(flowFileRoot.getDestination().getIp());
        }

        if (!StringUtils.isEmpty(pageFlowRequest.getProtocol())) {
            builder.protocol(pageFlowRequest.getProtocol());
        } else {
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
    private FlowDetailInfo getFlowDetailModel(FlowFileRoot flowFileRoot, FlowQueryRequest flowQueryRequest) {
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

        if (!StringUtils.isEmpty(flowQueryRequest.getTeamId())) {
            builder.teamId(flowQueryRequest.getTeamId());
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
    private void processPageable(PageFlowRequest pageFlowRequest, String[] indexArray,
                                 BoolQueryBuilder boolQueryBuilder, SearchSourceBuilder sourceBuilder) {

        if (Objects.isNull(pageFlowRequest.getPage())) {
            pageFlowRequest.setPage(1);
        }
        if (Objects.isNull(pageFlowRequest.getPageSize())) {
            pageFlowRequest.setPageSize(10);
        }

        int offset = (pageFlowRequest.getPage() - 1) * (pageFlowRequest.getPageSize());
        sourceBuilder.from(Math.min(offset, 10000));
        if ((offset + pageFlowRequest.getPageSize()) > 10000) {
            sourceBuilder.size(Math.max(10000 - offset, 0));
        } else {
            sourceBuilder.size(pageFlowRequest.getPageSize());
        }

    }


    /**
     * 根据时间创建索引
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

    public String[] buildEsIndex(LocalDateTime start, LocalDateTime end) {
        if (Objects.nonNull(start) && Objects.nonNull(end)) {
            return buildRequestIndex(start, end);
        } else {
            return getFlowIndex();
        }
    }

    private String[] getFlowIndex() {
        IndicesStatsRequest indicesStatsRequest = new IndicesStatsRequest();
        indicesStatsRequest.indices(ARKIME_SESSIONS3_ + "*");
        GetIndexRequest getIndexRequest = new GetIndexRequest(ARKIME_SESSIONS3_ + "*");
        GetIndexResponse indices = restHighLevelClient.getIndices(getIndexRequest);
        return indices.getIndices();
    }

    /**
     * @return List<IndexBoost>
     */
    @VisibleForTesting
    public static List<String> indexListBuilder(LocalDateTime start, LocalDateTime end) {
        if (Objects.isNull(start) || Objects.isNull(end)) {
            return new ArrayList<>();
        }
        List<String> dateStringList = fetchFormatterTime(start, end);
        return dateStringList.stream().map(dateString -> ARKIME_SESSIONS3_ + dateString).collect(Collectors.toList());
    }

    /**
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
            dayList.add(startDate.format(DateTimeFormatter.ofPattern("yyMMdd")));
            startDate = startDate.plusDays(1);
        }
        log.info("dayList:{}", dayList);
        return dayList;
    }
    
	/**
	 * 特定ip设备被攻击总次数
	 * 
	 * @param ips
	 * @return
	 */
	public ServiceResult<Long> queryEquipmentAttackCount(List<String> ips) {
		log.info("{}", ips);

		String[] indexArray = getFlowIndex();
		var searchRequest = new SearchRequest(indexArray);
		var boolQueryBuilder = QueryBuilders.boolQuery();
		var sourceBuilder = new SearchSourceBuilder();

		if (CollectionUtil.isNotEmpty(ips)) {
			buildRuleTargetIpQuery(ips, boolQueryBuilder);
		}
		buildSeverityQuery(0L, boolQueryBuilder);
		sourceBuilder.size(0);
		sourceBuilder.trackTotalHits(true);
		searchRequest.source(sourceBuilder.query(boolQueryBuilder));
		searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
		log.info("elasticsearch JSON query index:{}, DSL:\n{}", indexArray, searchRequest.source().toString());
		SearchResponse response = restHighLevelClient.search(searchRequest);

		return new ServiceResult<>(response.getHits().getTotalHits().value);
	}

	/**
	 * 特定ip设备被攻击总次数
	 * 
	 * @param ips
	 * @return
	 */
	public Map<String, Long> queryEquipmentAttackCountInfo(List<String> ips) {
        // todo 返回service result 尽量不要返回map 返回对象
		log.info("{}", ips);

		String[] indexArray = getFlowIndex();
		var searchRequest = new SearchRequest(indexArray);
		var boolQueryBuilder = QueryBuilders.boolQuery();
		var sourceBuilder = new SearchSourceBuilder();

		if (CollectionUtil.isNotEmpty(ips)) {
			buildRuleTargetIpQuery(ips, boolQueryBuilder);
		}
		buildSeverityQuery(0L, boolQueryBuilder);

		String deIpCountTerm = "des_ip_count";
		sourceBuilder.size(0);
		sourceBuilder.trackTotalHits(true);
		sourceBuilder.aggregation(AggregationBuilders.terms(deIpCountTerm).field("destination.ip"));

		searchRequest.source(sourceBuilder.query(boolQueryBuilder));
		searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
		log.info("elasticsearch JSON query index:{}, DSL:\n{}", indexArray, searchRequest.source().toString());

		Map<String, Long> ipCountMap = Maps.newHashMap();
		SearchResponse response = restHighLevelClient.search(searchRequest);
		Aggregations aggregations = response.getAggregations();
		ParsedTerms parsedTerms = aggregations.get(deIpCountTerm);
		List<? extends Terms.Bucket> buckets = parsedTerms.getBuckets();
		buckets.forEach(bucket -> {
			ipCountMap.put(bucket.getKeyAsString(), bucket.getDocCount());
		});

		return ipCountMap;
	}

	/**
	 * 设备被攻击手法top5
	 * 
	 * @param ips
	 * @return
	 */
	public Map<String, Long> queryEquipmentAttackTypeTop5(List<String> ips) {
        // todo 返回service result 尽量不要返回map 返回对象
		log.info("{}", ips);

		String[] indexArray = getFlowIndex();
		var searchRequest = new SearchRequest(indexArray);
		var boolQueryBuilder = QueryBuilders.boolQuery();
		var sourceBuilder = new SearchSourceBuilder();

		if (CollectionUtil.isNotEmpty(ips)) {
			buildRuleTargetIpQuery(ips, boolQueryBuilder);
		}
		buildSeverityQuery(0L, boolQueryBuilder);

		String cateCountTerm = "cate_count";
		sourceBuilder.size(0);
		sourceBuilder.trackTotalHits(true);
		sourceBuilder.aggregation(AggregationBuilders.terms(cateCountTerm).field("suricata.category")
				.order(BucketOrder.count(false)).size(5));

		searchRequest.source(sourceBuilder.query(boolQueryBuilder));
		searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
		log.info("elasticsearch JSON query index:{}, DSL:\n{}", indexArray, searchRequest.source().toString());

		Map<String, Long> typeCountMap = Maps.newLinkedHashMap();
		SearchResponse response = restHighLevelClient.search(searchRequest);
		Aggregations aggregations = response.getAggregations();
		ParsedTerms parsedTerms = aggregations.get(cateCountTerm);
		List<? extends Terms.Bucket> buckets = parsedTerms.getBuckets();
		buckets.forEach(bucket -> {
			typeCountMap.put(bucket.getKeyAsString(), bucket.getDocCount());
		});

		return typeCountMap;
	}
    
}
