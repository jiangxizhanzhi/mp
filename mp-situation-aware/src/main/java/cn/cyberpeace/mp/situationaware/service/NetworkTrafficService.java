package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.model.*;
import cn.cyberpeace.mp.situationaware.model.enums.SeverityEnum;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.elasticsearch.ElasticsearchStatusException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Service
@Slf4j
public class NetworkTrafficService extends AbstractBaseService{

    @Resource(name = "datasandtableRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;


    @Autowired
    private SituationAwareConfig situationAwareConfig;

    private static final String SESSION3 = "_sessions3";


    
    public List<SessionResult> querySessionListWithGroup(SessionRequest sessionRequest) {
        List<SessionResult> sessionResults = Lists.newArrayList();

        var searchRequest = new SearchRequest(this.indices(sessionRequest.getGroupList(),sessionRequest.getStartTime(),sessionRequest.getEndTime()));
        
        var sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(sessionRequest.getPageSize());
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        var boolQueryBuilder = QueryBuilders.boolQuery();
        
        var rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp")
            .from(sessionRequest.getStartTime().toString()).to(sessionRequest.getEndTime().toString());

        
        buildDynamicQuery(sessionRequest, boolQueryBuilder);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder.must(rangeQueryBuilder)));

        log.info("Search JSON query: {}", searchRequest.source().toString());
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<SessionModel> sessionModelList = Arrays.stream(response.getHits().getHits()).map(hit -> {
                var sessionSourceModel = JSONObject.parseObject(hit.getSourceAsString(), SessionSourceModel.class);
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                sessionSourceModel.setTimestamp(Long.valueOf(sourceAsMap.get("@timestamp").toString()));
                sessionSourceModel.setIndex(hit.getIndex());
                var sessionModel = new SessionModel();
                sessionModel.setIndex(sessionSourceModel.getIndex());
                sessionModel.setSrcIp(sessionSourceModel.getSource().getIp());
                sessionModel.setSrcMac(sessionSourceModel.getSource().getMac());
                sessionModel.setSrcPort(sessionSourceModel.getSource().getPort());
                sessionModel.setDstIp(sessionSourceModel.getDestination().getIp());
                sessionModel.setDstMac(sessionSourceModel.getDestination().getMac());
                sessionModel.setDstPort(sessionSourceModel.getDestination().getPort());
                sessionModel.setProtocol(sessionSourceModel.getProtocol());
                sessionModel.setTotBytes(sessionSourceModel.getNetwork().getBytes());
                sessionModel.setTimestamp(sessionSourceModel.getTimestamp());
                return sessionModel;
            }).collect(Collectors.toList());
            sessionResults = sessionModelList.stream().map(this::convertSession).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sessionResults;
    }

    
    public ListObjResult<SessionResult> querySessionWithPage(SessionRequest sessionRequest) {
        List<SessionResult> sessionResults = Lists.newArrayList();

        var indexArray = this.indices(sessionRequest.getGroupList(),sessionRequest.getStartTime(),sessionRequest.getEndTime());

        var searchRequest = new SearchRequest(indexArray);
        
        var sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(sessionRequest.getPageSize());
        var boolQueryBuilder = QueryBuilders.boolQuery();
        
        if (Objects.nonNull(sessionRequest.getStartTime()) && Objects.nonNull(sessionRequest.getEndTime())) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("@timestamp")
                    .from(sessionRequest.getStartTime().atZone(ZoneId.systemDefault()).format(SituationAwareConfig.esDateTimeFormatter))
                    .to(sessionRequest.getEndTime().atZone(ZoneId.systemDefault()).format(SituationAwareConfig.esDateTimeFormatter)));
        }

        
        buildDynamicQuery(sessionRequest, boolQueryBuilder);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        
        int  offset = (sessionRequest.getPageNum() -1) * (sessionRequest.getPageSize());
        sourceBuilder.from(offset);
        sourceBuilder.size(sessionRequest.getPageSize());

        log.info("Search JSON query: {}", searchRequest.source().toString());
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<SessionModel> sessionModelList = Arrays.stream(response.getHits().getHits()).map(hit -> {
                var sessionSourceModel = JSONObject.parseObject(hit.getSourceAsString(), SessionSourceModel.class);
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                sessionSourceModel.setTimestamp(Long.valueOf(sourceAsMap.get("@timestamp").toString()));
                sessionSourceModel.setIndex(hit.getIndex());
                var sessionModel = new SessionModel();
                sessionModel.setIndex(sessionSourceModel.getIndex());
                sessionModel.setSrcIp(sessionSourceModel.getSource().getIp());
                sessionModel.setSrcMac(sessionSourceModel.getSource().getMac());
                sessionModel.setSrcPort(sessionSourceModel.getSource().getPort());
                sessionModel.setDstIp(sessionSourceModel.getDestination().getIp());
                sessionModel.setDstMac(sessionSourceModel.getDestination().getMac());
                sessionModel.setDstPort(sessionSourceModel.getDestination().getPort());
                sessionModel.setProtocol(sessionSourceModel.getProtocol());
                sessionModel.setTotBytes(sessionSourceModel.getNetwork().getBytes());
                sessionModel.setTimestamp(sessionSourceModel.getTimestamp());
                return sessionModel;
            }).collect(Collectors.toList());
            sessionResults = sessionModelList.stream().map(this::convertSession).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        var countRequest = new CountRequest(indexArray);
        countRequest.query(boolQueryBuilder);
        countRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        var total = 0;

        try {
            CountResponse totalSize = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            total = (int) totalSize.getCount();
        } catch (IOException | ElasticsearchStatusException exception) {
            log.error("query session error macIds{} error{}", sessionRequest.getMacIds(), ExceptionUtils.getStackTrace(exception));
            throw new MpRuntimeException(BaseErrorCode.DATA_NOT_EXIST, ExceptionUtils.getStackTrace(exception));
        }

        return new ListObjResult<>(sessionResults, total);
    }




    
    public HttpResponse queryPcapFileByGroupId(String groupId, String sessionId) {
        HttpResponse httpResponse = null;
        String molochUrl;
        try {
            molochUrl = SituationalAwareConstant.groupIdSessionUrlCache.get(groupId);
        } catch (ExecutionException e) {
            log.error("查询缓存异常 groupId {}, {}", groupId, ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException("查询缓存异常 groupId: "+ groupId);
        }

        molochUrl = molochUrl + "/sessions.pcap?ids=" + sessionId;

        try {
            httpResponse =  HttpUtils.sendGetDownLoadWithDigestAuth(molochUrl,
                situationAwareConfig.getArkimeUsername(), situationAwareConfig.getArkimePassword());
        } catch (IOException e) {
            log.error("查询缓存异常 groupId {}, {}", groupId , ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException("查询缓存异常 groupId: "+ groupId);
        }
        return httpResponse;
    }

    
    public String querySPIView(SPIViewRequest spiViewRequest) {
        String molochUrl;
        try {
            molochUrl = SituationalAwareConstant.groupIdSessionUrlCache.get(spiViewRequest.getGroupId());
        } catch (ExecutionException e) {
            log.error("查询缓存异常 groupId {}", spiViewRequest.getGroupId(), ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException("查询缓存异常 groupId: "+ spiViewRequest.getGroupId());
        }

        molochUrl = molochUrl + "/spiview.json";
        var spiMolochUrlBuilder = new StringBuilder();
        spiMolochUrlBuilder.append(molochUrl)
            .append("?facets=").append(1)
            .append("&spi=").append("srcIp,dstIp,ipProtocol")
            .append("&startTime=").append(spiViewRequest.getStartTime())
            .append("&stopTime=").append(spiViewRequest.getStopTime())
            .append("&interval").append("auto")
        ;
        try {
           return HttpUtils.sendGet(spiMolochUrlBuilder.toString(), situationAwareConfig.getArkimeUsername(), situationAwareConfig.getArkimePassword());
        } catch (Exception e) {
            log.error("查询spi视图异常 groupId {}, {}", spiViewRequest.getGroupId(), ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException("查询spi视图异常 groupId: " + spiViewRequest.getGroupId());
        }
    }



    
    private void buildDynamicQuery(SessionRequest sessionRequest,
        BoolQueryBuilder boolQueryBuilder) {
        if (!ObjectUtils.isEmpty(sessionRequest.getSourceIp())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("source.ip", sessionRequest.getSourceIp()));
        }
        if (!ObjectUtils.isEmpty(sessionRequest.getTargetIp())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("destination.ip", sessionRequest.getTargetIp()));
        }
        if (!ObjectUtils.isEmpty(sessionRequest.getProtocol())) {
            boolQueryBuilder.must(QueryBuilders.wildcardQuery("protocol","*" +sessionRequest.getProtocol() + "*"));
        }
        if (!ObjectUtils.isEmpty(sessionRequest.getMacIds())) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("source.mac", sessionRequest.getMacIds()));
        }
        if (!ObjectUtils.isEmpty(sessionRequest.getSeverityEnum())){
            List<Integer> allSeverityList = Arrays.stream(SeverityEnum.values()).map(Enum::ordinal).collect(Collectors.toList());
            List<Integer> severityList = sessionRequest.getSeverityEnum() == SeverityEnum.ALL ? allSeverityList :
                    allSeverityList.stream().filter(v -> v <= sessionRequest.getSeverityEnum().ordinal()).collect(Collectors.toList());
            boolQueryBuilder.filter(QueryBuilders.termsQuery("suricata.severity", severityList));
        }
    }

    
    public List<SessionResult> querySessionAllList(SessionRequest sessionRequest) {
        if (CollectionUtils.isEmpty(sessionRequest.getGroupList())) {
            throw new MpRuntimeException("分组id列表必须传");
        }
        return querySessionListWithGroup(sessionRequest);
    }

    
    private SessionResult convertSession(SessionModel model) {
        return SessionResult.builder()
            
            .groupId(model.getIndex().substring(0, model.getIndex().indexOf(SESSION3)))
            .sourceIp(model.getSrcIp())
            .targetIp(model.getDstIp())
            .sourceMac(model.getSrcMac())
            .targetMac(model.getDstMac())
            .protocolType(model.getProtocol())
            .srcPort(model.getSrcPort().toString())
            .dstPort(model.getDstPort().toString())
            .totalBytes(model.getTotBytes().intValue())
            .timestamp(model.getTimestamp())
            .build();
    }






































}
