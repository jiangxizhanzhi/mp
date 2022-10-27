package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.collection.bo.CountBO;
import cn.cyberpeace.mp.collection.bo.ESIPFieldBo;
import cn.cyberpeace.mp.collection.bo.AttackFlowBO;
import cn.cyberpeace.mp.collection.bo.AttackTypeBO;
import cn.cyberpeace.mp.collection.bo.FlowBO;
import cn.cyberpeace.mp.collection.core.es.IRestHighLevelClient;
import cn.cyberpeace.mp.collection.model.FlowFileRoot;
import cn.cyberpeace.mp.collection.request.FlowQueryRequest;
import cn.cyberpeace.mp.collection.request.PageFlowRequest;
import cn.cyberpeace.mp.collection.util.EsResponseParseUtil;
import cn.cyberpeace.mp.collection.bo.FlowDetailInfo;
import cn.cyberpeace.mp.collection.bo.TeamAttackCountBO;
import cn.cyberpeace.mp.collection.bo.TeamFlowInfo;
import cn.hutool.core.collection.CollectionUtil;

import com.alibaba.fastjson.JSON;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
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
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.LongBounds;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
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
    
    private void buildSeverityQuery(BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.existsQuery("suricata.severity"));
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
	 * 攻击次数与流量大小（战队 or 设备）
	 * 
	 * @param ips
	 * @return
	 */
	public List<AttackFlowBO> queryAttackFlow(List<ESIPFieldBo> esipFieldBos) {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		buildRuleIpQuery(esipFieldBos, queryBuilder);
		
		String ipTerm = "target_ip", sumTerm = "flow_sum", ipField = esipFieldBos.get(0).getIPFieldName();
		AggregationBuilder aggrBuilder = AggregationBuilders.terms(ipTerm).field(ipField)
				.subAggregation(AggregationBuilders.sum(sumTerm).field("network.bytes"));
		SearchResponse response = esAggrQuery(queryBuilder, aggrBuilder);

		List<AttackFlowBO> bos = Lists.newArrayList();
		ParsedTerms parsedTermsIp = response.getAggregations().get(ipTerm);
		parsedTermsIp.getBuckets().forEach(bucketIp -> {
			ParsedSum sum = bucketIp.getAggregations().get(sumTerm);
			AttackFlowBO bo = new AttackFlowBO();
			bo.setIp(bucketIp.getKeyAsString());
			bo.setSize((long) sum.getValue());
			bo.setCount(bucketIp.getDocCount());
			bos.add(bo);
		});

		return bos;
	}
	
	/**
	 * 攻击类型占比top5（所有队伍）
	 * 
	 * @param ips
	 * @return
	 */
	public List<CountBO> queryAttackType(int topSize) {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		
		String typeTerm = "cate";
		AggregationBuilder aggrBuilder = AggregationBuilders.terms(typeTerm).field("suricata.category")
				.order(BucketOrder.count(false)).size(topSize);
		SearchResponse response = esAggrQuery(queryBuilder, aggrBuilder);

		List<CountBO> bos = Lists.newArrayList();
		// 第一个元素是总数
		CountBO total = new CountBO();
		total.setKey("total");
		total.setCount(response.getHits().getTotalHits().value);
		bos.add(total);
		
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
	 * 攻击类型占比top5（指定队伍）
	 * 
	 * @param ips
	 * @return
	 */
	public List<AttackFlowBO> queryAttackType(List<ESIPFieldBo> esipFieldBos) {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		buildRuleIpQuery(esipFieldBos, queryBuilder);
		
		String ipTerm = "target_ip", cateTerm = "cate";
		AggregationBuilder aggrBuilder = AggregationBuilders.terms(ipTerm).field(esipFieldBos.get(0).getIPFieldName())
				.subAggregation(AggregationBuilders.terms(cateTerm).field("suricata.category"));
		SearchResponse response = esAggrQuery(queryBuilder, aggrBuilder);

		List<AttackFlowBO> bos = Lists.newArrayList();
		ParsedTerms parsedTermsIp = response.getAggregations().get(ipTerm);
		parsedTermsIp.getBuckets().forEach(bucketIp -> {
			AttackFlowBO attackFlowBO = new AttackFlowBO();
			attackFlowBO.setIp(bucketIp.getKeyAsString());
			List<AttackTypeBO> attackTypes = Lists.newArrayList();
			attackFlowBO.setAttackTypes(attackTypes);
			bos.add(attackFlowBO);
			
			ParsedTerms parsedTermsCate = bucketIp.getAggregations().get(cateTerm);
			parsedTermsCate.getBuckets().forEach(bucketCate -> {
				AttackTypeBO bo = new AttackTypeBO();
				bo.setEnName(bucketCate.getKeyAsString());
				bo.setCount(bucketIp.getDocCount());
				attackTypes.add(bo);
			});
		});

		return bos;
	}
	
	/**
	 * 总体攻击趋势
	 * 
	 * @param ips
	 * @return
	 */
	public List<CountBO> queryAttackTrend() {
		long endTime = Calendar.getInstance().getTimeInMillis();
		long startTime = endTime - 70 * 60000L; // 分7段，每段间隔10分钟

		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		buildRuleStarTimeQuery(startTime, queryBuilder);
		buildRuleEndTimeQuery(endTime, queryBuilder);
		buildSeverityQuery(queryBuilder);

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
	public Map<String, List<CountBO>> queryAttackLevel() {
		long endTime = Calendar.getInstance().getTimeInMillis();
		long startTime = endTime - 70 * 60000L; // 分7段，每段间隔10分钟

		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		buildRuleStarTimeQuery(startTime, queryBuilder);
		buildRuleEndTimeQuery(endTime, queryBuilder);

		String severityTerm = "severity", histogramTerm = "histogram";
		AggregationBuilder aggrBuilder = AggregationBuilders.terms(severityTerm).field("suricata.severity")
				.subAggregation(AggregationBuilders.dateHistogram(histogramTerm).field("@timestamp")
						.fixedInterval(DateHistogramInterval.minutes(10)).format("HH:mm"));
		SearchResponse response = esAggrQuery(queryBuilder, aggrBuilder);

		Map<String, List<CountBO>> severityMap = Maps.newHashMap();
		ParsedTerms parsedTermsSeverity = response.getAggregations().get(severityTerm);
		parsedTermsSeverity.getBuckets().forEach(bucketSeverity -> {
			String severity = bucketSeverity.getKeyAsString();
			
			List<CountBO> bos = Lists.newArrayList();
			ParsedTerms parsedTermsHistogram = bucketSeverity.getAggregations().get(histogramTerm);
			parsedTermsHistogram.getBuckets().forEach(bucketHistogram -> {
				CountBO bo = new CountBO();
				bo.setKey(bucketHistogram.getKeyAsString());
				bo.setCount(bucketHistogram.getDocCount());
				bos.add(bo);
			});
			severityMap.put(severity, bos);
		});
		
		return severityMap;
	}
	
	/**
	 * 战队攻击趋势
	 * 
	 * @param ips
	 * @return
	 */
	public Map<String, List<CountBO>> queryTeamAttackTrend(List<ESIPFieldBo> esipFieldBos) {
		long endTime = Calendar.getInstance().getTimeInMillis();
		long startTime = endTime - 365 * 60000L; // 六小时, 五分钟间隔

		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		buildRuleStarTimeQuery(startTime, queryBuilder);
		buildRuleEndTimeQuery(endTime, queryBuilder);
		buildSeverityQuery(queryBuilder);

		String ipTerm = "target_ip", histogramTerm = "histogram", sumTerm = "flow_sum";
		AggregationBuilder aggrBuilder = AggregationBuilders.terms(ipTerm).field(esipFieldBos.get(0).getIPFieldName())
				.subAggregation(AggregationBuilders.dateHistogram(histogramTerm).field("@timestamp")
						.fixedInterval(DateHistogramInterval.minutes(5)).hardBounds(new LongBounds(startTime, endTime))
						.format("HH:mm").subAggregation(AggregationBuilders.sum(sumTerm).field("network.bytes")));
		SearchResponse response = esAggrQuery(queryBuilder, aggrBuilder);

		Map<String, List<CountBO>> ipMap = Maps.newHashMap();
		ParsedTerms parsedTermsIp = response.getAggregations().get(ipTerm);
		parsedTermsIp.getBuckets().forEach(bucketIp -> {
			String ip = bucketIp.getKeyAsString();
			
			List<CountBO> bos = Lists.newArrayList();
			ParsedTerms parsedTermsHistogram = bucketIp.getAggregations().get(histogramTerm);
			parsedTermsHistogram.getBuckets().forEach(bucketHistogram -> {
				ParsedSum sum = bucketHistogram.getAggregations().get(sumTerm);
				
				CountBO bo = new CountBO();
				bo.setKey(bucketHistogram.getKeyAsString());
				bo.setCount((long) sum.getValue());
				bos.add(bo);
			});
			ipMap.put(ip, bos);
		});

		return ipMap;
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
				.order(BucketOrder.count(false));
		SearchResponse response = esAggrQuery(queryBuilder, aggrBuilder);

		List<String> ignoreProtocals = Lists.newArrayList("tcp", "udp");
		List<CountBO> bos = Lists.newArrayList();
		ParsedTerms parsedTermsIp = response.getAggregations().get(typeTerm);
		for(Bucket bucket : parsedTermsIp.getBuckets()) {
			String protocol = bucket.getKeyAsString();
			if(ignoreProtocals.contains(protocol.toLowerCase())) {
				continue;
			}
			
			CountBO bo = new CountBO();
			bo.setKey(protocol);
			bo.setCount(bucket.getDocCount());
			bos.add(bo);
			
			if(bos.size() == topSize) {
				break;
			}
		}
		
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
	 * 攻击流量列表
	 * 
	 * @param ips
	 * @return
	 */
	public List<FlowBO> attackFlowList(Integer pageNum, Integer pageSize) {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		buildSeverityQuery(queryBuilder);
		SearchResponse response = esListQuery(queryBuilder, pageNum, pageSize);
		
		List<FlowBO> bos = Lists.newArrayList();
		for (SearchHit hit : response.getHits().getHits()) {
			FlowBO bo = JSON.parseObject(hit.getSourceAsString(), FlowBO.class);
			bos.add(bo);
	    }

		return bos;
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
		buildSeverityQuery(queryBuilder);

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
	 * es 列表查询
	 * 
	 * @param ips
	 * @param aggrTerm
	 * @param aggrField
	 * @return
	 */
	private SearchResponse esListQuery(BoolQueryBuilder queryBuilder, Integer pageNum, Integer pageSize){
		String[] indexArray = getFlowIndex();
		var searchRequest = new SearchRequest(indexArray);
		
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		if(pageNum != null) {
			sourceBuilder.size(pageSize);
			sourceBuilder.from((pageNum - 1) * pageSize);
		}
		sourceBuilder.trackTotalHits(true);

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
