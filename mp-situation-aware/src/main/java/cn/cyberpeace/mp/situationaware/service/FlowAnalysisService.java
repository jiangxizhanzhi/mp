package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.situationaware.bo.AttackClassificationBO;
import cn.cyberpeace.mp.situationaware.bo.AttackDateHistogramNetworkBytesBO;
import cn.cyberpeace.mp.situationaware.bo.AttackWayAggregationBO;
import cn.cyberpeace.mp.situationaware.bo.FlowLogBO;
import cn.cyberpeace.mp.situationaware.model.FlowClassificationModel;
import cn.cyberpeace.mp.situationaware.model.enums.AggregationTypeEnum;
import cn.cyberpeace.mp.situationaware.model.enums.SeverityEnum;
import cn.cyberpeace.mp.situationaware.request.FlowAnalysisRequest;
import cn.cyberpeace.mp.situationaware.utils.FileUtils;
import cn.cyberpeace.mp.situationaware.utils.LocalDateUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流量分析Service
 * @author wanggang
 */
@Slf4j
@Service
public class FlowAnalysisService extends AbstractBaseService {

    @javax.annotation.Resource(name = "datasandtableRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;


    @Autowired
    private FlowClassificationsService flowClassificationsService;

    /**
     * 初始化攻击方式
     * @return
     * @throws IOException
     */
    public void initClassification() throws IOException {
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "suricata/classification.json");
        String json = FileUtils.readLine(resources);
        var attackClassificationBOS = JSONObject.parseObject(json, AttackClassificationBO.class);
        log.info("attackClassificationBOS：{}",attackClassificationBOS);
        flowClassificationsService.deleteAll();
        flowClassificationsService.batchSave(attackClassificationBOS);
    }

    /**
     * 统计时间窗口里的流量大小
     * @param request
     *        属性secondsInterval不可为空且必须大于0;
     *        属性groupList不可为空;
     *        属性startDateTime不可为空;
     *        属性endDateTime不可为空；
     * @return
     */
    public List<AttackDateHistogramNetworkBytesBO> sumNetworkBytesByDateHistogramTimestamp(FlowAnalysisRequest request){
        Integer secondsInterval = request.getSecondsInterval();
        if (Objects.isNull(secondsInterval) || secondsInterval <= 0
                || CollectionUtils.isEmpty(request.getGroupList())
                || Objects.isNull(request.getStartDateTime())
                || Objects.isNull(request.getEndDateTime())
                || request.getStartDateTime().isAfter(request.getEndDateTime()) ){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        AggregationBuilder aggregationBuilder = AggregationBuilders.dateHistogram("lastHourAttack").field("@timestamp")
                .fixedInterval(DateHistogramInterval.seconds(secondsInterval))
                .subAggregation(AggregationBuilders.sum("network_bytes").field("network.bytes"));

        var boolQueryBuilder = buildBoolQueryBuilder(request);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.query(boolQueryBuilder);

        var searchRequest = new SearchRequest(this.indices(request.getGroupList(),request.getStartDateTime(),request.getEndDateTime()));
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        searchRequest.source(searchSourceBuilder);
        List<AttackDateHistogramNetworkBytesBO> attackTimestampBOS = new ArrayList();
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            long totalHits = searchResponse.getHits().getTotalHits().value;
            if (totalHits == 0){
                return Lists.newArrayList();
            }
            Aggregations aggregations = searchResponse.getAggregations();
            ParsedDateHistogram parsedTerms = aggregations.get("lastHourAttack");
            List<? extends Histogram.Bucket>  buckets = parsedTerms.getBuckets();
            attackTimestampBOS = buckets.stream().map(bucket -> {
                AttackDateHistogramNetworkBytesBO bo = new AttackDateHistogramNetworkBytesBO();
                bo.setTime(bucket.getKeyAsString());
                Aggregations bucketAggregations = bucket.getAggregations();
                ParsedSum parsedSum = bucketAggregations.get("network_bytes");
                bo.setNetworkBytes(parsedSum.getValue());
                return bo;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("search attack way error",e);
        }
        return attackTimestampBOS;
    }

    /**
     * 攻击日志
     * @param request
     * @return
     */
    public List<FlowLogBO> flowLog(FlowAnalysisRequest request){

        var boolQueryBuilder = this.buildBoolQueryBuilder(request);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.sort("@timestamp", SortOrder.DESC);

        if(Objects.nonNull(request.getPageSize()) && Objects.nonNull(request.getPageNum())){
            int  offset = (request.getPageNum() -1) * (request.getPageSize());
            searchSourceBuilder.from(offset);
            searchSourceBuilder.size(request.getPageSize());
        }else {
            searchSourceBuilder.from(0).size(request.getTopSize());
        }

        var searchRequest = new SearchRequest(this.indices(request.getGroupList(),request.getStartDateTime(),request.getEndDateTime()));
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        searchRequest.source(searchSourceBuilder);
        List<FlowLogBO> flowLogBOList = new ArrayList<>();
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            long totalHits = hits.getTotalHits().value;
            if (totalHits == 0){
                return Lists.newArrayList();
            }
            SearchHit[] searchHits = hits.getHits();
            flowLogBOList = Arrays.stream(searchHits).map(hit -> {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                FlowLogBO flowLogBO = JSONObject.parseObject(hit.getSourceAsString(), FlowLogBO.class);
                flowLogBO.setTimestamp(Long.valueOf(sourceAsMap.get("@timestamp").toString()));
                return flowLogBO;
            } ).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("search attack way error",e);
        }
        return flowLogBOList;
    }

    /**
     * 流量日志，包含分类的中英文名称
     * @param request
     * @return
     */
    public List<FlowLogBO> flowLogWithCategoryZh(FlowAnalysisRequest request){

        List<FlowLogBO> flowLogBOS = this.flowLog(request);
        if (CollectionUtils.isEmpty(flowLogBOS)){
            return Lists.newArrayList();
        }
        Map<String, FlowClassificationModel> allCategoryDicMap = flowClassificationsService.findAllCategoryDicMap();
        flowLogBOS.stream().filter(v -> Objects.nonNull(v.getSuricata())).forEach(flowLogBO -> {
            if (MapUtils.isNotEmpty(allCategoryDicMap)){
                List<String> categoryZh = flowLogBO.getSuricata().getCategory().stream()
                        .map(c -> Objects.nonNull(allCategoryDicMap.get(c)) ?
                        allCategoryDicMap.get(c).getCategoryEn() : c).collect(Collectors.toList());
                flowLogBO.getSuricata().setCategoryZh(categoryZh);
            }
        });

        return flowLogBOS;
    }

    /**
     * 根据聚合类型聚合不同的TOP N数据，目标支持攻击分类、源IP、目标IP、攻击严重性,默认TOP 10
     * @param request
     * @return
     */
    public List<AttackWayAggregationBO> topByAggregationTypeEnum(FlowAnalysisRequest request){
        AggregationTypeEnum aggregationTypeEnum = request.getAggregationTypeEnum();
        if (Objects.isNull(aggregationTypeEnum)){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }

        var boolQueryBuilder = this.buildBoolQueryBuilder(request);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermsAggregationBuilder aggregationBuilder;
        if (aggregationTypeEnum == AggregationTypeEnum.SURICATA_CATEGORY) {
            aggregationBuilder = AggregationBuilders.terms("aggregationType")
                    .field("suricata.category").size(request.getTopSize());
            searchSourceBuilder.aggregation(aggregationBuilder);

        }else if (aggregationTypeEnum == AggregationTypeEnum.SOURCE_IP){
            aggregationBuilder = AggregationBuilders.terms("aggregationType")
                    .field("source.ip").size(request.getTopSize());
            searchSourceBuilder.aggregation(aggregationBuilder);

        }else if(aggregationTypeEnum == AggregationTypeEnum.DESTINATION_IP){
            aggregationBuilder = AggregationBuilders.terms("aggregationType")
                    .field("destination.ip").size(request.getTopSize());
            searchSourceBuilder.aggregation(aggregationBuilder);
        }else if (aggregationTypeEnum == AggregationTypeEnum.SURICATA_SEVERITY){
            aggregationBuilder = AggregationBuilders.terms("aggregationType")
                    .field("suricata.severity").size(request.getTopSize());
            searchSourceBuilder.aggregation(aggregationBuilder);
        }

        searchSourceBuilder.size(0);

        var searchRequest = new SearchRequest(this.indices(request.getGroupList(),request.getStartDateTime(),request.getEndDateTime()));
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        searchRequest.source(searchSourceBuilder.query(boolQueryBuilder));

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            long totalHits = response.getHits().getTotalHits().value;
            if (totalHits == 0){
                return Lists.newArrayList();
            }
            Aggregations aggregations = response.getAggregations();
            ParsedTerms parsedTerms = aggregations.get("aggregationType");
            return parsedTerms.getBuckets().stream().map(bucket -> {
                AttackWayAggregationBO bo = new AttackWayAggregationBO();
                bo.setAggregationType(bucket.getKeyAsString());
                bo.setAttackNumber(bucket.getDocCount());
                return bo;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("search attack way error",e);
        }
        return Lists.newArrayList();
    }



    /**
     * 计算流量大小
     * @param request
     * @return
     */
    public Double calcNetworkBytes(FlowAnalysisRequest request){
        SumAggregationBuilder aggregationBuilder = AggregationBuilders.sum("network_bytes").field("network.bytes");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.size(0);

        var searchRequest = new SearchRequest(this.indices(request.getGroupList(),request.getStartDateTime(),request.getEndDateTime()));
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        var boolQueryBuilder = this.buildBoolQueryBuilder(request);

        searchRequest.source(searchSourceBuilder.query(boolQueryBuilder));

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            long totalHits = response.getHits().getTotalHits().value;
            if (totalHits == 0){
                return Double.valueOf("0");
            }
            Aggregations aggregations = response.getAggregations();
            ParsedSum parsedSum = aggregations.get("network_bytes");
            return parsedSum.getValue();

        } catch (IOException e) {
            log.error("search attack way error",e);
        }
        return Double.valueOf("0");
    }


    /**
     * 统计总数
     * @param request
     * @return
     */
    public long count(FlowAnalysisRequest request){
        var boolQueryBuilder = this.buildBoolQueryBuilder(request);

        CountRequest countRequest = new CountRequest();
        countRequest.indices(this.indices(request.getGroupList(),request.getStartDateTime(),request.getEndDateTime()));
        countRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        countRequest.query(boolQueryBuilder);
        try {
            CountResponse countResponse = restHighLevelClient.count(countRequest,RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 构建BoolQueryBuilder对象
     * @param request
     * @return
     */
    protected BoolQueryBuilder buildBoolQueryBuilder(FlowAnalysisRequest request){
        var boolQueryBuilder = QueryBuilders.boolQuery();

        if (Objects.nonNull(request.getSeverityEnum())){
            List<Integer> allSeverityList = Arrays.stream(SeverityEnum.values()).map(Enum::ordinal).collect(Collectors.toList());
            List<Integer> severityList = request.getSeverityEnum() == SeverityEnum.ALL ? allSeverityList :
                    allSeverityList.stream().filter(v -> v <= request.getSeverityEnum().ordinal()).collect(Collectors.toList());
            boolQueryBuilder.filter(QueryBuilders.termsQuery("suricata.severity", severityList));
        }

        if (CollectionUtils.isNotEmpty(request.getSourceIps())) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("source.ip", request.getSourceIps()));
        }

        if (StringUtils.isNotBlank(request.getSourcePort())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("source.port", request.getSourcePort()));
        }

        if (CollectionUtils.isNotEmpty(request.getSourceMacs())) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("source.mac", request.getSourceMacs()));
        }

        if (CollectionUtils.isNotEmpty(request.getDestinationIps())) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("destination.ip", request.getDestinationIps()));
        }

        if (StringUtils.isNotBlank(request.getDestinationPort())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("destination.port", request.getDestinationPort()));
        }

        if (CollectionUtils.isNotEmpty(request.getProtocols())) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("protocol",request.getProtocols()));
        }

        if (Objects.nonNull(request.getStartDateTime()) && Objects.nonNull(request.getEndDateTime())
                && request.getStartDateTime().isBefore(request.getEndDateTime())){
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
            rangeQueryBuilder.gte(LocalDateUtils.toEpochMilli(request.getStartDateTime()));
            rangeQueryBuilder.lte(LocalDateUtils.toEpochMilli(request.getEndDateTime()));
            boolQueryBuilder.must(rangeQueryBuilder);
        }
        return boolQueryBuilder;
    }

}
