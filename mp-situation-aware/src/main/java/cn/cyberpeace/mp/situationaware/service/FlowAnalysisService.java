package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.situationaware.bo.AttackDestinationIpAggregationBO;
import cn.cyberpeace.mp.situationaware.bo.AttackWayAggregationBO;
import cn.cyberpeace.mp.situationaware.bo.FlowLogBO;
import cn.cyberpeace.mp.situationaware.bo.FlowSeverityBO;
import cn.cyberpeace.mp.situationaware.request.FlowAnalysisRequest;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流量分析Service
 * @author wanggang
 */
@Slf4j
@Service
public class FlowAnalysisService extends AbstractBaseService {

    private RestHighLevelClient restHighLevelClient;

    @Autowired
    public FlowAnalysisService(RestHighLevelClient datasandtableRestHighLevelClient) {
        this.restHighLevelClient = datasandtableRestHighLevelClient;
    }

    /**
     * 攻击等级TOP N(默认TOP 10)
     * @param request
     * @return
     */
    public List<FlowSeverityBO> topBySeverity(FlowAnalysisRequest request){
        var boolQueryBuilder = this.buildBoolQueryBuilder(request);

        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("suricata_severity")
                .field("suricata.severity").size(request.getTopSize());

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.size(0);

        var searchRequest = new SearchRequest(this.indices(request.getGroupList(),request.getStartDate(),request.getEndDate()));
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        searchRequest.source(searchSourceBuilder.query(boolQueryBuilder));

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            ParsedTerms parsedTerms = aggregations.get("suricata_severity");
            return parsedTerms.getBuckets().stream().map(bucket -> {
                FlowSeverityBO bo = FlowSeverityBO.builder()
                        .severityId(bucket.getKey().toString()).build();
                bo.setAttackNumber(bucket.getDocCount());

                return bo;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("search attack way error",e);
        }
        return Lists.newArrayList();
    }

    /**
     * 攻击方式TOP N(默认TOP 10)
     * @param request
     * @return
     */
    public List<AttackWayAggregationBO> topBySuricataCategory(FlowAnalysisRequest request){
        var boolQueryBuilder = this.buildBoolQueryBuilder(request);

        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("suricata_category")
                .field("suricata.category").size(request.getTopSize());

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.size(0);

        var searchRequest = new SearchRequest(this.indices(request.getGroupList(),request.getStartDate(),request.getEndDate()));
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        searchRequest.source(searchSourceBuilder.query(boolQueryBuilder));

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            ParsedTerms parsedTerms = aggregations.get("suricata_category");
            return parsedTerms.getBuckets().stream().map(bucket -> {
                AttackWayAggregationBO bo = AttackWayAggregationBO.builder()
                        .attackWay(bucket.getKey().toString()).build();
                bo.setAttackNumber(bucket.getDocCount());

                return bo;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("search attack way error",e);
        }
        return Lists.newArrayList();
    }

    /**
     * 攻击节点TOP N (默认TOP 10)
     * @param request
     * @return
     */
    public List<AttackDestinationIpAggregationBO> topBySourceIp(FlowAnalysisRequest request){

        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("source_ip")
                .field("source.ip").size(request.getTopSize());

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.size(0);

        var searchRequest = new SearchRequest(this.indices(request.getGroupList(),request.getStartDate(),request.getEndDate()));
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        var boolQueryBuilder = this.buildBoolQueryBuilder(request);

        searchRequest.source(searchSourceBuilder.query(boolQueryBuilder));

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            ParsedTerms parsedTerms = aggregations.get("source_ip");
            return parsedTerms.getBuckets().stream().map(bucket -> {
                AttackDestinationIpAggregationBO bo = AttackDestinationIpAggregationBO.builder()
                        .attackDestinationIp(bucket.getKey().toString()).num(bucket.getDocCount()).build();
                return bo;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("search attack way error",e);
        }
        return Lists.newArrayList();
    }

    /**
     * 被攻击节点TOP N (默认TOP 10)
     * @param request
     * @return
     */
    public List<AttackDestinationIpAggregationBO> topByDestinationIp(FlowAnalysisRequest request){

        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("destination_ip")
                .field("destination.ip").size(request.getTopSize());

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.size(0);

        var searchRequest = new SearchRequest(this.indices(request.getGroupList(),request.getStartDate(),request.getEndDate()));
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        var boolQueryBuilder = this.buildBoolQueryBuilder(request);

        searchRequest.source(searchSourceBuilder.query(boolQueryBuilder));

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            ParsedTerms parsedTerms = aggregations.get("destination_ip");
            return parsedTerms.getBuckets().stream().map(bucket -> {
                AttackDestinationIpAggregationBO bo = AttackDestinationIpAggregationBO.builder()
                        .attackDestinationIp(bucket.getKey().toString()).num(bucket.getDocCount()).build();
                return bo;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("search attack way error",e);
        }
        return Lists.newArrayList();
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
        searchSourceBuilder.from(0).size(request.getTopSize());

        var searchRequest = new SearchRequest(this.indices(request.getGroupList(),request.getStartDate(),request.getEndDate()));
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        searchRequest.source(searchSourceBuilder);
        List<FlowLogBO> flowLogBOList = new ArrayList<>();
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
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
     * 计算流量大小
     * @param request
     * @return
     */
    public Double calcNetworkBytes(FlowAnalysisRequest request){
        SumAggregationBuilder aggregationBuilder = AggregationBuilders.sum("network_bytes").field("network.bytes");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.size(0);

        var searchRequest = new SearchRequest(this.indices(request.getGroupList(),request.getStartDate(),request.getEndDate()));
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        var boolQueryBuilder = this.buildBoolQueryBuilder(request);

        searchRequest.source(searchSourceBuilder.query(boolQueryBuilder));

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            ParsedSum parsedSum = aggregations.get("network_bytes");
            return parsedSum.getValue();

        } catch (IOException e) {
            log.error("search attack way error",e);
        }
        return Double.valueOf("0");
    }

    /**
     * 构建BoolQueryBuilder对象
     * @param request
     * @return
     */
    protected BoolQueryBuilder buildBoolQueryBuilder(FlowAnalysisRequest request){
        var boolQueryBuilder = QueryBuilders.boolQuery();
        return boolQueryBuilder;
    }
}
