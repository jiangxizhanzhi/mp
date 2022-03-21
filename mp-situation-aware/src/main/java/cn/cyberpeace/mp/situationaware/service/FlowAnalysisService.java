package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.situationaware.bo.*;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.model.FlowClassificationModel;
import cn.cyberpeace.mp.situationaware.model.enums.AggregationTypeEnum;
import cn.cyberpeace.mp.situationaware.model.enums.SeverityEnum;
import cn.cyberpeace.mp.situationaware.request.FlowAnalysisRequest;
import cn.cyberpeace.mp.situationaware.request.FlowSimulationRequest;
import cn.cyberpeace.mp.situationaware.utils.FileUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private FlowPortProtocolService flowPortProtocolService;

    /**
     * 初始化攻击方式
     * @return
     * @throws IOException
     */
    @Transactional
    public void initClassification() throws IOException {
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "suricata/classification.json");
        String json = FileUtils.readLine(resources);
        var attackClassificationBOS = JSONObject.parseObject(json, AttackClassificationBO.class);
        log.info("initClassification >>> attackClassificationBOS：{}",attackClassificationBOS);
        flowClassificationsService.deleteAll();
        flowClassificationsService.batchSave(attackClassificationBOS);
    }

    /**
     * 模拟生成流量
     * @return
     * @throws IOException
     */
    public Boolean addSimulationFlow(FlowSimulationRequest request) throws IOException {

        String group = request.getGroup();

        if (StringUtils.isBlank(group) || !group.startsWith("mp_")){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }

        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "suricata/flow.json");
        String json = FileUtils.readLine(resources);

        var flow = JSONObject.parseObject(json,FlowTemplateBO.class);

        String destinationIp = request.getDestinationIp();

        if (StringUtils.isNotBlank(destinationIp)){
            FlowTemplateBO.DestinationDTO destination = flow.getDestination();
            destination.setIp(destinationIp);
            flow.setDestination(destination);
        }

        List<String> destinationMacs = request.getDestinationMacs();

        if (CollectionUtils.isNotEmpty(destinationMacs)){
            FlowTemplateBO.DestinationDTO destination = flow.getDestination();
            destination.setMac(destinationMacs);
            destination.setMaccnt(destinationMacs.size());
            flow.setDestination(destination);
        }

        String sourceIp = request.getSourceIp();

        if (StringUtils.isNotBlank(sourceIp)){
            FlowTemplateBO.SourceDTO source = flow.getSource();
            source.setIp(sourceIp);
            flow.setSource(source);
        }

        List<String> sourceMacs = request.getSourceMacs();

        if (CollectionUtils.isNotEmpty(sourceMacs)){
            FlowTemplateBO.SourceDTO source = flow.getSource();
            source.setMac(sourceMacs);
            source.setMaccnt(sourceMacs.size());
            flow.setSource(source);
        }

        Integer networkBytes = request.getNetworkBytes();

        if (Objects.nonNull(networkBytes)){
            FlowTemplateBO.NetworkDTO network = flow.getNetwork();
            network.setBytes(networkBytes);
            flow.setNetwork(network);
        }

        if (Objects.nonNull(request.getShuffleCategory()) && request.getShuffleCategory()) {
            List<FlowClassificationModel> classificationModels = flowClassificationsService.findAll();
            if (CollectionUtils.isNotEmpty(classificationModels)) {
                Collections.shuffle(classificationModels);
                FlowTemplateBO.SuricataDTO suricata = flow.getSuricata();
                FlowClassificationModel classificationModel = classificationModels.get(0);
                suricata.setCategory(Arrays.asList(classificationModel.getCategoryEn()));
                suricata.setSeverity(Arrays.asList(classificationModel.getSeverity()));
                flow.setSuricata(suricata);
            }
        }


        Map<String,Object> source = JSON.parseObject(JSON.toJSONString(flow), Map.class);

        LocalDateTime dateTime = request.getDateTime();

        if (Objects.isNull(dateTime)){
            dateTime = LocalDateTime.now();
        }

        source.put("@timestamp",dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        var formatter = DateTimeFormatter.ofPattern("yyMMdd");

        IndexRequest indexRequest = new IndexRequest(group.concat("_").concat(SESSION3).concat("-").concat(dateTime.format(formatter))).source(source);

        restHighLevelClient.index(indexRequest,RequestOptions.DEFAULT);
        return Boolean.TRUE;
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
        searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.query(boolQueryBuilder);

        var searchRequest = this.buildSearchRequest(request,searchSourceBuilder);

        log.info("sumNetworkBytesByDateHistogramTimestamp >>> Search JSON query: {}", searchRequest.source().toString());

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
                bo.setTrafficSize(parsedSum.getValue());
                return bo;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("search attack way error",e);
        }
        return attackTimestampBOS;
    }

    /**
     * 统计时间窗口里的攻击次数
     * @param request
     *        属性secondsInterval不可为空且必须大于0;
     *        属性groupList不可为空;
     *        属性startDateTime不可为空;
     *        属性endDateTime不可为空；
     * @return
     */
    public List<AttackDateHistogramNetworkBytesBO> countByDateHistogramTimestamp(FlowAnalysisRequest request){
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
                .subAggregation(AggregationBuilders.count("count").field("destination.ip"));

        var boolQueryBuilder = buildBoolQueryBuilder(request);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.query(boolQueryBuilder);

        var searchRequest = this.buildSearchRequest(request,searchSourceBuilder);

        log.info("sumNetworkBytesByDateHistogramTimestamp >>> Search JSON query: {}", searchRequest.source().toString());

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
                ParsedValueCount parsedValueCount = bucketAggregations.get("count");
                bo.setAttackNumber(parsedValueCount.getValue());
                return bo;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("search attack way error",e);
        }
        return attackTimestampBOS;
    }

    /**
     * 统计受攻击端口-攻击次数&流量大小(端口映射协议)
     * @param request
     * @return
     */
    public List<AttackPortAggregationBO> groupByDestinationPortWithProtocol(FlowAnalysisRequest request){

        List<AttackPortAggregationBO> boList = this.groupByDestinationPort(request);

        Map<String, String> convertToMap = flowPortProtocolService.convertToMap();

        boList.stream().forEach(v ->
            v.setProtocol(convertToMap.get(v.getPort()))
        );

        return boList;
    }

    /**
     * 统计受攻击端口-攻击次数&流量大小
     * @param request
     *        属性secondsInterval不可为空且必须大于0;
     *        属性groupList不可为空;
     *        属性startDateTime不可为空;
     *        属性endDateTime不可为空；
     * @return
     */
    public List<AttackPortAggregationBO> groupByDestinationPort(FlowAnalysisRequest request){
        if (CollectionUtils.isEmpty(request.getGroupList())
                || Objects.isNull(request.getStartDateTime())
                || Objects.isNull(request.getEndDateTime())
                || request.getStartDateTime().isAfter(request.getEndDateTime()) ){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("destination_port").field("destination.port")
                .subAggregation(AggregationBuilders.sum("network_bytes").field("network.bytes"))
                .subAggregation(AggregationBuilders.count("count").field("destination.ip"));

        var boolQueryBuilder = buildBoolQueryBuilder(request);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(0).size(request.getTopSize());

        var searchRequest = this.buildSearchRequest(request,searchSourceBuilder);

        log.info("groupByDestinationPort  >>> Search JSON query: {}", searchRequest.source().toString());

        List<AttackPortAggregationBO> attackTimestampBOS = new ArrayList();
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            long totalHits = searchResponse.getHits().getTotalHits().value;
            if (totalHits == 0){
                return Lists.newArrayList();
            }
            Aggregations aggregations = searchResponse.getAggregations();
            ParsedTerms parsedTerms = aggregations.get("destination_port");
            List<? extends Terms.Bucket> buckets = parsedTerms.getBuckets();
            attackTimestampBOS = buckets.stream().map(bucket -> {
                AttackPortAggregationBO bo = new AttackPortAggregationBO();
                bo.setPort(bucket.getKeyAsString());
                Aggregations bucketAggregations = bucket.getAggregations();
                ParsedSum parsedSum = bucketAggregations.get("network_bytes");
                bo.setTrafficSize(parsedSum.getValue());

                ParsedValueCount valueCount = bucketAggregations.get("count");
                bo.setAttackNumber(valueCount.getValue());
                return bo;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("search attack way error",e);
        }
        return attackTimestampBOS;
    }

    /**
     *
     * @param request
     * @return
     */
    public List<AttackSeverityAndDestinationIpAggregationBO> groupByDestinationIpAndSeverity(FlowAnalysisRequest request){
        if (CollectionUtils.isEmpty(request.getGroupList())
                || Objects.isNull(request.getStartDateTime())
                || Objects.isNull(request.getEndDateTime())
                || request.getStartDateTime().isAfter(request.getEndDateTime()) ){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("suricata_severity").field("suricata.severity")
                .subAggregation(AggregationBuilders.terms("destination_ip").field("destination.ip"));

        var boolQueryBuilder = buildBoolQueryBuilder(request);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(0);

        var searchRequest = this.buildSearchRequest(request,searchSourceBuilder);

        log.info("groupByDestinationIpAndSeverity >>> Search JSON query: {}", searchRequest.source().toString());

        List<AttackSeverityAndDestinationIpAggregationBO> attackTimestampBOS = new ArrayList();
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            long totalHits = searchResponse.getHits().getTotalHits().value;
            if (totalHits == 0){
                return Lists.newArrayList();
            }
            Aggregations aggregations = searchResponse.getAggregations();
            ParsedTerms parsedTerms = aggregations.get("suricata_severity");
            List<? extends Terms.Bucket> buckets = parsedTerms.getBuckets();
            attackTimestampBOS = buckets.stream().map(bucket -> {
                AttackSeverityAndDestinationIpAggregationBO bo = new AttackSeverityAndDestinationIpAggregationBO();
                bo.setSeverity(Integer.parseInt(bucket.getKeyAsString()));
                Aggregations bucketAggregations = bucket.getAggregations();
                ParsedTerms terms = bucketAggregations.get("destination_ip");
                List<? extends Terms.Bucket> aggregationBuckets = terms.getBuckets();
                List<String> destinationIpList = aggregationBuckets.stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
                bo.setDestinationIpList(destinationIpList);
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
        searchSourceBuilder.trackTotalHits(true);

        if(Objects.nonNull(request.getPageSize()) && Objects.nonNull(request.getPageNum())){
            int  offset = (request.getPageNum() -1) * (request.getPageSize());
            searchSourceBuilder.from(offset);
            searchSourceBuilder.size(request.getPageSize());
        }else {
            searchSourceBuilder.from(0).size(request.getTopSize());
        }

        var searchRequest = this.buildSearchRequest(request,searchSourceBuilder);

        log.info("flowLog >>>>> Search JSON query: {}", searchRequest.source().toString());

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
     * 流量日志，国际化处理
     * @param request
     * @return
     */
    public List<FlowLogBO> globalizedWithFlowLog(FlowAnalysisRequest request){
        List<FlowLogBO> flowLogBOS = this.flowLog(request);
        if (CollectionUtils.isEmpty(flowLogBOS)){
            return Lists.newArrayList();
        }
        Map<String, FlowClassificationModel> allCategoryDicMap = flowClassificationsService.findAllCategoryDicMap();
        flowLogBOS.stream().filter(v -> Objects.nonNull(v.getSuricata())).forEach(flowLogBO -> {
            if (MapUtils.isNotEmpty(allCategoryDicMap)){
                List<String> categoryList = flowLogBO.getSuricata().getCategory().stream()
                        .map(c -> this.globalizedWithAttackWay(c,allCategoryDicMap)).collect(Collectors.toList());
                flowLogBO.getSuricata().setCategory(categoryList);
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
        List<AttackWayAggregationBO> aggregationBOS = queryTopByAggregationTypeEnum(request);
        if (CollectionUtils.isEmpty(aggregationBOS)){
            return aggregationBOS;
        }
        if (aggregationTypeEnum == AggregationTypeEnum.SURICATA_CATEGORY) {
            globalizedWithAttackWay(aggregationBOS);
        }
        return aggregationBOS;
    }

    /**
     *
     * @param request
     * @return
     */
    public List<AttackWayAggregationBO>  queryTopByAggregationTypeEnum(FlowAnalysisRequest request){
        AggregationTypeEnum aggregationTypeEnum = request.getAggregationTypeEnum();
        if (Objects.isNull(aggregationTypeEnum)){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }

        var boolQueryBuilder = this.buildBoolQueryBuilder(request);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.trackTotalHits(true);
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

        var searchRequest = this.buildSearchRequest(request,searchSourceBuilder);

        log.info("topByAggregationTypeEnum >>>> Search JSON query: {}", searchRequest.source().toString());
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

        log.info("calcNetworkBytes >>>>  Search JSON query: {}", searchRequest.source().toString());

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

        log.info(" count >>>> Search JSON query: {}", countRequest);

        try {
            CountResponse countResponse = restHighLevelClient.count(countRequest,RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 查询一个战队的数据聚合
     *
     * @return
     */
    public TeamAttackBO queryOneTeamAttackSumList(FlowAnalysisRequest request) {
        TeamAttackBO teamAttackResultVO = new TeamAttackBO();

        // 1.创建并设置SearchSourceBuilder对象
        var searchSourceBuilder = new SearchSourceBuilder();

        var boolQueryBuilder = this.buildBoolQueryBuilder(request);

        searchSourceBuilder.aggregation(AggregationBuilders.sum("packageSize").field("network.bytes"));
        searchSourceBuilder.aggregation(AggregationBuilders.terms("attackWay").field("suricata.category").size(request.getTopSize()));
        //构造动态条件查询
        searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.query(boolQueryBuilder);

        var searchRequest = this.buildSearchRequest(request,searchSourceBuilder);

        log.info("queryOneTeamAttackSumList Search JSON query: {}", searchRequest.source().toString());

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //设置战队攻击流量攻击次数
            teamAttackResultVO.setTrafficSize(((ParsedSum) response.getAggregations().get("packageSize")).getValue());
            teamAttackResultVO.setAttackNumber(response.getHits().getTotalHits().value);
            //设置攻击手法
            List<AttackWayAggregationBO> attackWayList = ((ParsedStringTerms) response.getAggregations().get("attackWay")).getBuckets().stream()
                    .map(bucket -> {
                        var parsedBucket = (ParsedStringTerms.ParsedBucket) bucket;
                        var attackResult = new AttackWayAggregationBO();
                        attackResult.setAggregationType(parsedBucket.getKeyAsString());
                        attackResult.setAttackNumber(parsedBucket.getDocCount());
                        return attackResult;
                    })
                    .collect(Collectors.toList());
            globalizedWithAttackWay(attackWayList);
            teamAttackResultVO.setAttackWayList(buildTeamAttackWayPercent(attackWayList));
        } catch (IOException e) {
            log.error("query one team error : {}", ExceptionUtils.getMessage(e));
            e.printStackTrace();
        }

        return teamAttackResultVO;
    }

    /**
     * 删除对应场景流量数据
     * @param groupList
     * @return
     */
    public Boolean delete(List<String> groupList){
        if (CollectionUtils.isEmpty(groupList)){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        String[] indices = groupList.stream().filter(v -> v.startsWith("mp_")).map(v -> v.concat("_*")).toArray(String[]::new);
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest();
        deleteIndexRequest.indices(indices);
        deleteIndexRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        try {
            restHighLevelClient.indices().delete(deleteIndexRequest,RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("delete error : {}",e.getMessage(),e);
        }
        return Boolean.TRUE;
    }

    /**
     * 攻击手法-攻击次数百分比
     */
    private List<AttackWayAggregationBO> buildTeamAttackWayPercent(List<AttackWayAggregationBO> attackResultVOS) {
        Long totalNumber = attackResultVOS.stream().mapToLong(AttackWayAggregationBO::getAttackNumber).sum();
        if (Long.valueOf(0).equals(totalNumber)) {
            return attackResultVOS;
        }
        for (int i = 0; i < attackResultVOS.size(); i++) {
            var attack = attackResultVOS.get(i);
            if (i > 1 && i == attackResultVOS.size() - 1) {
                BigDecimal beforeSum = attackResultVOS.subList(0, i).stream().map(AttackWayAggregationBO::getAttackNumberPercent).reduce((a, b) -> a.add(b)).get();
                attack.setAttackNumberPercent(BigDecimal.valueOf(100).compareTo(beforeSum) == -1 ? BigDecimal.ZERO : BigDecimal.valueOf(100).subtract(beforeSum));
            } else {
                attack.setAttackNumberPercent(BigDecimal.valueOf(attack.getAttackNumber()).divide(BigDecimal.valueOf(totalNumber), 4, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(100)));
            }
        }
        attackResultVOS = attackResultVOS.stream().sorted(Comparator.comparing(AttackWayAggregationBO::getAttackNumberPercent).reversed()).collect(Collectors.toList());
        return attackResultVOS;
    }

    /**
     * 构建SearchRequest对象
     * @param request
     * @return
     */
    protected SearchRequest buildSearchRequest(FlowAnalysisRequest request,SearchSourceBuilder searchSourceBuilder){
        var searchRequest = new SearchRequest();
        searchRequest.indices(ArrayUtils.isNotEmpty(request.getIndices()) ? request.getIndices() : this.indices(request.getGroupList(),request.getStartDateTime(),request.getEndDateTime()));
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
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
        if (CollectionUtils.isNotEmpty(request.getFilterDestinationIps())) {
            boolQueryBuilder.mustNot(QueryBuilders.termsQuery("destination.ip", request.getFilterDestinationIps()));
        }
        if (Objects.nonNull(request.getStartDateTime()) && Objects.nonNull(request.getEndDateTime())
                && request.getStartDateTime().isBefore(request.getEndDateTime())){
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
            rangeQueryBuilder.gte(request.getStartDateTime().atZone(ZoneId.systemDefault()));
            rangeQueryBuilder.lte(request.getEndDateTime().atZone(ZoneId.systemDefault()));
            boolQueryBuilder.must(rangeQueryBuilder);
        }

        return boolQueryBuilder;
    }

    /**
     * 国际化攻击手法
     * @param aggregationBOS
     */
    protected void globalizedWithAttackWay(List<AttackWayAggregationBO> aggregationBOS){
        if (CollectionUtils.isEmpty(aggregationBOS)){
            return;
        }
        Map<String, FlowClassificationModel> allCategoryDicMap = flowClassificationsService.findAllCategoryDicMap();
        aggregationBOS.stream().forEach(bo ->{
            String aggregationType = this.globalizedWithAttackWay(bo.getAggregationType(),allCategoryDicMap);
            bo.setAggregationType(aggregationType);
        });
    }

    /**
     * 国际化攻击手法
     * @param attackWay
     * @param allCategoryDicMap
     * @return
     */
    protected String globalizedWithAttackWay(String attackWay,Map<String, FlowClassificationModel> allCategoryDicMap){
        if (MapUtils.isEmpty(allCategoryDicMap)){
            return attackWay;
        }
        FlowClassificationModel model = allCategoryDicMap.get(attackWay);
        String globalized = attackWay;
        if (Objects.nonNull(model)) {
            Locale locale = LocaleContextHolder.getLocale();
            globalized = this.getLocalMessage(locale, SituationalAwareConstant.ERROR_CODE_PREFIX + model.getName());
        }
        return globalized;
    }

    /**
     * 获取所有索引,支持模糊匹配索引
     * @return
     */
    public String[] getAllIndices(String... indices){
        GetIndexRequest getIndexRequest = new GetIndexRequest(indices);
        GetIndexResponse getIndexResponse = null;
        try {
            getIndexResponse = restHighLevelClient.indices().get(getIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("get all indices error : {}",e.getMessage(),e);
            return new String[]{};
        }
        return getIndexResponse.getIndices();
    }

}
