package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.situationaware.config.EsDateFormatConfig;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.model.SPIViewRequest;
import cn.cyberpeace.mp.situationaware.model.SessionModel;
import cn.cyberpeace.mp.situationaware.model.SessionRequest;
import cn.cyberpeace.mp.situationaware.model.SessionResult;
import cn.cyberpeace.mp.situationaware.model.SessionSourceModel;
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

/**
 * @author zhangjin
 */
@Service
@Slf4j
public class NetworkTrafficService extends AbstractBaseService{

    @Resource(name = "datasandtableRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    private static final String SESSION3 = "_sessions3";


    /**
     * 分组维度查询流量
     *
     * @param sessionRequest
     * @return List<SessionResult>
     */
    public List<SessionResult> querySessionListWithGroup(SessionRequest sessionRequest) {
        List<SessionResult> sessionResults = Lists.newArrayList();

        var searchRequest = new SearchRequest(this.indices(sessionRequest.getGroupList(),sessionRequest.getStartTime(),sessionRequest.getEndTime()));
        // 1.创建并设置SearchSourceBuilder对象
        var sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(sessionRequest.getPageSize());
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        var boolQueryBuilder = QueryBuilders.boolQuery();
        //时间查询
        var rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp")
            .from(sessionRequest.getStartTime().toString()).to(sessionRequest.getEndTime().toString());

        //构造动态条件查询
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

    /**
     * 支持分页查询
     * @param sessionRequest sessionRequest
     * @return ListObjResult<SessionResult>
     */
    public ListObjResult<SessionResult> querySessionWithPage(SessionRequest sessionRequest) {
        List<SessionResult> sessionResults = Lists.newArrayList();

        var indexArray = this.indices(sessionRequest.getGroupList(),sessionRequest.getStartTime(),sessionRequest.getEndTime());

        var searchRequest = new SearchRequest(indexArray);
        // 1.创建并设置SearchSourceBuilder对象
        var sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(sessionRequest.getPageSize());
        var boolQueryBuilder = QueryBuilders.boolQuery();
        //时间查询
        if (Objects.nonNull(sessionRequest.getStartTime()) && Objects.nonNull(sessionRequest.getEndTime())) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("@timestamp")
                    .from(sessionRequest.getStartTime().atZone(ZoneId.systemDefault()).format(EsDateFormatConfig.esDateTimeFormatter))
                    .to(sessionRequest.getEndTime().atZone(ZoneId.systemDefault()).format(EsDateFormatConfig.esDateTimeFormatter)));
        }

        //构造动态条件查询
        buildDynamicQuery(sessionRequest, boolQueryBuilder);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        //处理分页
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




    /**
     * 根据分组id下载流量包
     */
    public HttpResponse queryPcapFileByGroupId(String groupId, String sessionId) {
        HttpResponse httpResponse = null;
        String molochUrl;
        try {
            molochUrl = SituationalAwareConstant.groupIdSessionUrlCache.get(groupId);
        } catch (ExecutionException e) {
            log.error("查询缓存异常 groupId {}" + e, groupId);
            throw new MpRuntimeException("查询缓存异常 groupId: "+ groupId);
        }

        molochUrl = molochUrl + "/sessions.pcap?ids=" + sessionId;

        try {
            httpResponse =  HttpUtils.sendGetDownLoadWithDigestAuth(molochUrl,
                situationAwareConfig.getUserName(), situationAwareConfig.getPassword());
        } catch (IOException e) {
            log.error("查询缓存异常 groupId {}" + e, groupId);
            throw new MpRuntimeException("查询缓存异常 groupId: "+ groupId);
        }
        return httpResponse;
    }

    /**
     * 查询spiView数据
     * @param spiViewRequest spiViewRequest
     * @return String
     */
    public String querySPIView(SPIViewRequest spiViewRequest) {
        String molochUrl;
        try {
            molochUrl = SituationalAwareConstant.groupIdSessionUrlCache.get(spiViewRequest.getGroupId());
        } catch (ExecutionException e) {
            log.error("查询缓存异常 groupId {}" + e, spiViewRequest.getGroupId());
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
           return HttpUtils.sendGet(spiMolochUrlBuilder.toString(), situationAwareConfig.getUserName(), situationAwareConfig.getPassword());
        } catch (Exception e) {
            log.error("查询spi视图异常 groupId {}" + e, spiViewRequest.getGroupId());
            throw new MpRuntimeException("查询spi视图异常 groupId: " + spiViewRequest.getGroupId());
        }
    }



    /**
     * 构造动态条件查询
     * @param sessionRequest sessionRequest
     * @param boolQueryBuilder boolQueryBuilder
     */
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

    /**
     * 查询所有集群
     *
     * @param sessionRequest 这个查询方法 groupList毕传
     * @return
     */
    public List<SessionResult> querySessionAllList(SessionRequest sessionRequest) {
        if (CollectionUtils.isEmpty(sessionRequest.getGroupList())) {
            throw new MpRuntimeException("分组id列表必须传");
        }
        return querySessionListWithGroup(sessionRequest);
    }

    /**
     * es bean 到中台bean的转换
     *
     * @param model
     * @return
     */
    private SessionResult convertSession(SessionModel model) {
        return SessionResult.builder()
            //从索引字段获取groupId字段
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

//    /**
//     * 构造查询需要的索引
//     *
//     * @return List<IndexBoost>
//     */
//    private List<String> indexListBuilder(SessionRequest sessionRequest) {
//        List<String> groupList = sessionRequest.getGroupList();
//
//        List<String> dateStringList = LocalDateUtils.fetchFormatterTime(sessionRequest.getStartTime(), sessionRequest.getEndTime());
//        if (CollectionUtils.isEmpty(groupList)) {
//            return Lists.newArrayList();
//        }
//
//        return sessionRequest.getGroupList()
//            .stream().map(groupId -> dateStringList.stream().map(dateString -> sessionIndexFormatter(groupId, dateString))
//                .collect(Collectors.toList()))
//            .flatMap(Collection::stream)
//            .collect(Collectors.toList());
//    }

//    /**
//     * 获取比如2021-05-01 到2021-05-20.所有的日期，方便构建所有的时间字符串210501,210502,....
//     *
//     * @param startTime startTime
//     * @param endTime endTime
//     * @return List<String>
//     */
//    private List<String> fetchFormatterTime(LocalDateTime startTime, LocalDateTime endTime) {
//        var formatter = DateTimeFormatter.ofPattern("yyMMdd");
//        long betweenDay = ChronoUnit.DAYS.between(startTime, endTime);
//        long month = ChronoUnit.MONTHS.between(startTime, endTime);
//        int days = (int) betweenDay;
//        if (month > 1) {
//            return IntStream.range(0, days + 2).boxed().map(startTime::plusDays).map((time) -> time.format(DateTimeFormatter.ofPattern("yyMM*")))
//                .distinct().collect(Collectors.toList());
//        }
//        return IntStream.range(0, days + 1).boxed().map(startTime::plusDays).map((time) -> time.format(formatter))
//            .collect(Collectors.toList());
//    }
//
//    /**
//     * 构造流量索引
//     *LocaleResolver
//     * @param groupId groupId
//     * @param dateString dateString
//     * @return indexString
//     */
//    private String sessionIndexFormatter(String groupId, String dateString) {
//        StringBuilder sessionIndexBuilder = new StringBuilder();
//        sessionIndexBuilder.append(groupId).append(SESSION3).append("-")
//            .append(dateString);
//        return sessionIndexBuilder.toString();
//    }
}
