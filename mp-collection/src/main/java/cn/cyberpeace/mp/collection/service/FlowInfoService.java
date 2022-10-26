package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.collection.bo.AttackTypeBO;
import cn.cyberpeace.mp.collection.bo.CountBO;
import cn.cyberpeace.mp.collection.bo.ESIPFieldBo;
import cn.cyberpeace.mp.collection.bo.FlowAttackBO;
import cn.cyberpeace.mp.collection.core.es.IRestHighLevelClient;
import cn.cyberpeace.mp.collection.model.FlowFileRoot;
import cn.cyberpeace.mp.collection.request.FlowQueryRequest;
import cn.cyberpeace.mp.collection.request.PageFlowRequest;
import cn.cyberpeace.mp.collection.util.EsResponseParseUtil;
import cn.cyberpeace.mp.collection.bo.FlowDetailInfo;
import cn.cyberpeace.mp.collection.bo.TeamAttackCountBO;
import cn.cyberpeace.mp.collection.bo.TeamFlowInfo;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

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
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.LongBounds;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
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
import java.util.Calendar;
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
    
    private void buildSeverityQuery(List<Long> severities, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("suricata.severity", severities));
    }
    
    private void buildRuleIpQuery(List<String> ips, String ipField, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery(ipField, ips));
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
	public Long queryEquipmentAttackCount(List<String> ips) {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		buildRuleIpQuery(ips, "destination.ip", queryBuilder);
		
		SearchResponse response = esAggrQuery(queryBuilder, null);

		return response.getHits().getTotalHits().value;
	}

	/**
	 * 特定ip设备被攻击总次数
	 * 
	 * @param ips
	 * @return
	 */
	public List<CountBO> queryEquipmentAttackCountInfo(List<String> ips) {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		buildRuleIpQuery(ips, "destination.ip", queryBuilder);
		
		String aggrTerm = "des_ip_count";
		AggregationBuilder aggrBuilder = AggregationBuilders.terms(aggrTerm).field("destination.ip");
		SearchResponse response = esAggrQuery(queryBuilder, aggrBuilder);
		
		ParsedTerms parsedTerms = response.getAggregations().get(aggrTerm);
		List<? extends Terms.Bucket> buckets = parsedTerms.getBuckets();
		return buckets.stream().map(bucket -> {
			return new CountBO(bucket.getKeyAsString(), bucket.getDocCount());
		}).collect(Collectors.toList());
	}

	/**
	 * 设备被攻击手法top5
	 * 
	 * @param ips
	 * @return
	 */
	public List<CountBO> queryEquipmentAttackTypeTop5(List<String> ips) {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		buildRuleIpQuery(ips, "destination.ip", queryBuilder);
		
		String aggrTerm = "cate_count";
		AggregationBuilder aggrBuilder = AggregationBuilders.terms(aggrTerm).field("suricata.category")
				.order(BucketOrder.count(false)).size(5);
		SearchResponse response = esAggrQuery(queryBuilder, aggrBuilder);
		
		ParsedTerms parsedTerms = response.getAggregations().get(aggrTerm);
		List<? extends Terms.Bucket> buckets = parsedTerms.getBuckets();
		return buckets.stream().map(bucket -> {
			return new CountBO(bucket.getKeyAsString(), bucket.getDocCount());
		}).collect(Collectors.toList());
	}
	
	/**
	 * 设备被攻击次数、流量大小、攻击手法占比
	 * 
	 * @param ips
	 * @return
	 */
	public List<FlowAttackBO> queryFlowAttackInfo(List<String> ips) {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		buildRuleIpQuery(ips, "destination.ip", queryBuilder);
		
		String ipTerm = "target_ip", typeTerm = "attack_type", sumTerm = "flow_sum";
		AggregationBuilder aggrBuilder = AggregationBuilders.terms(ipTerm).field("destination.ip")
				.subAggregation(AggregationBuilders.terms(typeTerm).field("suricata.category"))
				.subAggregation(AggregationBuilders.sum(sumTerm).field("network.bytes"));
		SearchResponse response = esAggrQuery(queryBuilder, aggrBuilder);

		List<FlowAttackBO> bos = Lists.newArrayList();
		ParsedTerms parsedTermsIp = response.getAggregations().get(ipTerm);
		parsedTermsIp.getBuckets().forEach(bucketIp -> {
			ParsedSum sum = bucketIp.getAggregations().get(sumTerm);
			FlowAttackBO bo = new FlowAttackBO();
			bo.setIp(bucketIp.getKeyAsString());
			bo.setSize((long) sum.getValue());
			bo.setCount(bucketIp.getDocCount());
			bos.add(bo);

			// 攻击类型列表
			List<AttackTypeBO> attackTypes = Lists.newArrayList();
			bo.setAttackTypes(attackTypes);
			ParsedTerms parsedTermsType = bucketIp.getAggregations().get(typeTerm);
			parsedTermsType.getBuckets().forEach(bucketType -> {
				AttackTypeBO typeBO = new AttackTypeBO();
				typeBO.setEnName(bucketType.getKeyAsString());
				typeBO.setCount(bucketType.getDocCount());
				attackTypes.add(typeBO);
			});
		});

		return bos;
	}
	
	/**
	 * 每个队伍的攻击次数(累计) top10
	 * 
	 * @param ips
	 * @return
	 */
	public List<CountBO> queryTeamAttackCount(List<String> ips) {
		String ipTerm = "source_ip", ipField = "source.ip";
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		buildRuleIpQuery(ips, ipField, queryBuilder);
		
		AggregationBuilder aggrBuilder = AggregationBuilders.terms(ipTerm).field(ipField)
				.order(BucketOrder.count(false)).size(10);
		SearchResponse response = esAggrQuery(queryBuilder, aggrBuilder);

		List<CountBO> bos = Lists.newArrayList();
		ParsedTerms parsedTermsIp = response.getAggregations().get(ipTerm);
		parsedTermsIp.getBuckets().forEach(bucketIp -> {
			CountBO bo = new CountBO();
			bo.setKey(bucketIp.getKeyAsString());
			bo.setCount(bucketIp.getDocCount());
			bos.add(bo);
		});

		return bos;
	}
	
	/**
	 * 所有队伍的攻击类型占比top5
	 * 
	 * @param ips
	 * @return
	 */
	public List<CountBO> queryAttackType(List<ESIPFieldBo> esipFieldBos, int topSize) {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		buildRuleIpQuery(esipFieldBos, queryBuilder);
		
		String typeTerm = "cate";
		AggregationBuilder aggrBuilder = AggregationBuilders.terms(typeTerm).field("suricata.category")
				.order(BucketOrder.count(false)).size(topSize);
		SearchResponse response = esAggrQuery(queryBuilder, aggrBuilder);

		List<CountBO> bos = Lists.newArrayList();
		ParsedTerms parsedTermsIp = response.getAggregations().get(typeTerm);
		parsedTermsIp.getBuckets().forEach(bucketIp -> {
			CountBO bo = new CountBO();
			bo.setKey(bucketIp.getKeyAsString());
			bo.setCount(bucketIp.getDocCount());
			bos.add(bo);
		});

		return bos;
	}
	
	/**
	 * 总体攻击趋势
	 * 
	 * @param ips
	 * @return
	 */
	public List<CountBO> queryAttackTrend(List<ESIPFieldBo> esipFieldBos) {
		long endTime = Calendar.getInstance().getTimeInMillis();
		long startTime = endTime - 6000000L; // 分10段，每段间隔10分钟

		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		buildRuleIpQuery(esipFieldBos, queryBuilder);
		buildRuleStarTimeQuery(startTime, queryBuilder);
		buildRuleEndTimeQuery(endTime, queryBuilder);

		String typeTerm = "date_dis";
		AggregationBuilder aggrBuilder = AggregationBuilders.dateHistogram(typeTerm).field("@timestamp")
				.fixedInterval(DateHistogramInterval.minutes(10)).format("HH:mm");
		SearchResponse response = esAggrQuery(queryBuilder, aggrBuilder);

		List<CountBO> bos = Lists.newArrayList();
		ParsedTerms parsedTermsDate = response.getAggregations().get(typeTerm);
		parsedTermsDate.getBuckets().forEach(bucketDate -> {
			CountBO bo = new CountBO();
			bo.setKey(bucketDate.getKeyAsString());
			bo.setCount(bucketDate.getDocCount());
			bos.add(bo);
		});

		return bos;
	}
	
	/**
	 * 攻击告警级别趋势
	 * 
	 * @param ips
	 * @return
	 */
	public List<CountBO> queryAttackLevel(List<ESIPFieldBo> esipFieldBos, List<Long> severities) {
		long endTime = Calendar.getInstance().getTimeInMillis();
		long startTime = endTime - 6000000L; // 分10段，每段间隔10分钟

		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		buildRuleIpQuery(esipFieldBos, queryBuilder);
		buildRuleStarTimeQuery(startTime, queryBuilder);
		buildRuleEndTimeQuery(endTime, queryBuilder);
		buildSeverityQuery(severities, queryBuilder);

		String typeTerm = "date";
		AggregationBuilder aggrBuilder = AggregationBuilders.dateHistogram(typeTerm).field("@timestamp").fixedInterval(
				DateHistogramInterval.minutes(10)).hardBounds(new LongBounds(startTime, endTime)).format("HH:mm");
		SearchResponse response = esAggrQuery(queryBuilder, aggrBuilder);

		List<CountBO> bos = Lists.newArrayList();
		ParsedTerms parsedTermsDate = response.getAggregations().get(typeTerm);
		parsedTermsDate.getBuckets().forEach(bucketDate -> {
			CountBO bo = new CountBO();
			bo.setKey(bucketDate.getKeyAsString());
			bo.setCount(bucketDate.getDocCount());
			bos.add(bo);
		});

		return bos;
	}
	
	/**
	 * 攻击告警级别趋势
	 * 
	 * @param ips
	 * @return
	 */
	public List<CountBO> queryLevelTrend(List<String> ips) {
		List<CountBO> bos = Lists.newArrayList();
		long endTime = Calendar.getInstance().getTimeInMillis();
		for(int i = 0; i< 10; i++) {
			endTime = endTime - i * 600000L; // 间隔10分钟
			
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
			buildRuleIpQuery(ips, "source.ip", queryBuilder);
			buildRuleEndTimeQuery(endTime, queryBuilder);
			SearchResponse response = esAggrQuery(queryBuilder, null);

			CountBO bo = new CountBO();
			bo.setKey(endTime + "");
			bo.setCount(response.getHits().getTotalHits().value);
			bos.add(bo);
		}

		return bos;
	}
	
	/**
	 * 设备攻击协议TOP5
	 * 
	 * @param ips
	 * @return
	 */
	public List<CountBO> queryProtocolTop5(List<ESIPFieldBo> esipFieldBos, int topSize) {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		buildRuleIpQuery(esipFieldBos, queryBuilder);
		
		String typeTerm = "protocol";
		AggregationBuilder aggrBuilder = AggregationBuilders.terms(typeTerm).field("protocol")
				.order(BucketOrder.count(false)).size(topSize);
		SearchResponse response = esAggrQuery(queryBuilder, aggrBuilder);

		List<CountBO> bos = Lists.newArrayList();
		ParsedTerms parsedTermsIp = response.getAggregations().get(typeTerm);
		parsedTermsIp.getBuckets().forEach(bucketIp -> {
			CountBO bo = new CountBO();
			bo.setKey(bucketIp.getKeyAsString());
			bo.setCount(bucketIp.getDocCount());
			bos.add(bo);
		});

		return bos;
	}
	
	/**
	 * 设备ip被攻击次数
	 * 
	 * @param ips
	 * @return
	 */
	public List<CountBO> queryEquipmentIpAttackCount(List<ESIPFieldBo> esipFieldBos) {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		buildRuleIpQuery(esipFieldBos, queryBuilder);
		
		String ipTerm = "target_ip";
		AggregationBuilder aggrBuilder = AggregationBuilders.terms(ipTerm).field("destination.ip");
		SearchResponse response = esAggrQuery(queryBuilder, aggrBuilder);

		List<CountBO> bos = Lists.newArrayList();
		ParsedTerms parsedTermsIp = response.getAggregations().get(ipTerm);
		parsedTermsIp.getBuckets().forEach(bucketIp -> {
			CountBO bo = new CountBO();
			bo.setKey(bucketIp.getKeyAsString());
			bo.setCount(bucketIp.getDocCount());
			bos.add(bo);
		});

		return bos;
	}
	
	/**
	 * 总攻击次数
	 * @return
	 */
	public long attackTotalCount() {
		SearchResponse response = esAggrQuery(QueryBuilders.boolQuery(), null);
		return response.getHits().getTotalHits().value;
	}
	
	/**
	 * 攻击聚合（source + destination + category）
	 * 
	 * @param ips
	 * @return
	 */
	public List<TeamAttackCountBO> queryTeamAttackAgg(List<ESIPFieldBo> esipFieldBos) {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		buildRuleIpQuery(esipFieldBos, queryBuilder);

		String srcIpTerm = "src_ip";
        String cateTerm = "cate";
        String desIpTerm = "des_ip";
		AggregationBuilder aggrBuilder = AggregationBuilders.terms(srcIpTerm)
				.field(esipFieldBos.get(0).getIPFieldName())
				.subAggregation(AggregationBuilders.sum(desIpTerm).field("destination.ip"))
				.subAggregation(AggregationBuilders.terms(cateTerm).field("suricata.category"));
		SearchResponse response = esAggrQuery(queryBuilder, aggrBuilder);

		ParsedTerms parsedTermsIp = response.getAggregations().get(srcIpTerm);

        return parsedTermsIp.getBuckets().stream().map(bucketSrcIp -> {
            ParsedTerms parsedTermsCate = bucketSrcIp.getAggregations().get(cateTerm);
            List<CountBO> attackTargetIps = parsedTermsCate.getBuckets().stream().map(bucketCate -> {
                CountBO bo = new CountBO();
                bo.setKey(bucketCate.getKeyAsString());
                bo.setCount(bucketCate.getDocCount());
                return bo;
            }).collect(Collectors.toList());

            ParsedTerms parsedTermsDesIp = bucketSrcIp.getAggregations().get(desIpTerm);
            List<CountBO> attackTypes = parsedTermsDesIp.getBuckets().stream().map(bucketDesIp -> {
                CountBO bo = new CountBO();
                bo.setKey(bucketDesIp.getKeyAsString());
                bo.setCount(bucketDesIp.getDocCount());
                return bo;
            }).collect(Collectors.toList());

            TeamAttackCountBO teamCountBo = new TeamAttackCountBO();
            teamCountBo.setIp(bucketSrcIp.getKeyAsString());
            teamCountBo.setCount(bucketSrcIp.getDocCount());
            teamCountBo.setAttackTargetIps(attackTargetIps);
            teamCountBo.setAttackTypes(attackTypes);
            return teamCountBo;
        }).collect(Collectors.toList());
	}
	
	/**
	 * es 聚合查询
	 * 
	 * @param ips
	 * @param aggrTerm
	 * @param aggrField
	 * @return
	 */
	private SearchResponse esAggrQuery(BoolQueryBuilder queryBuilder, AggregationBuilder aggrBuilder){
		String[] indexArray = getFlowIndex();
		var searchRequest = new SearchRequest(indexArray);
		var sourceBuilder = new SearchSourceBuilder();
		buildSeverityQuery(Lists.newArrayList(1L, 2L, 3L, 4L), queryBuilder);

		sourceBuilder.size(0);
		sourceBuilder.trackTotalHits(true);
		if(aggrBuilder != null) {
			sourceBuilder.aggregation(aggrBuilder);
		}

		searchRequest.source(sourceBuilder.query(queryBuilder));
		searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
		log.info("elasticsearch JSON query index:{}, DSL:\n{}", indexArray, searchRequest.source().toString());

		return restHighLevelClient.search(searchRequest);
	}
	
	/**
	 * 构建ip字段查询
	 * 
	 * @param esipFieldBos
	 * @param queryBuilder
	 */
	private void buildRuleIpQuery(List<ESIPFieldBo> esipFieldBos, BoolQueryBuilder queryBuilder) {
		BoolQueryBuilder ipQueryBuilder = QueryBuilders.boolQuery();
		for(ESIPFieldBo bo : esipFieldBos) {
			String ipField = bo.getIPFieldName();
			if(bo.getFilterType() == ESIPFieldBo.FilterType.LIST) {
				ipQueryBuilder.should(QueryBuilders.termsQuery(ipField, bo.getIps()));
			} else {
				ipQueryBuilder.should(QueryBuilders.prefixQuery(ipField, bo.getIps().get(0)));
			} 
		}
		
		queryBuilder.filter(ipQueryBuilder);
	}
    
}
