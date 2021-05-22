package cn.cyberpeace.mp.data.sandtable.service;

import cn.cyberpeace.mp.base.exception.MqRuntimeException;
import cn.cyberpeace.mp.data.sandtable.model.SessionModel;
import cn.cyberpeace.mp.data.sandtable.model.SessionRequest;
import cn.cyberpeace.mp.data.sandtable.model.SessionResult;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
public class SessionService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 分组维度查询流量
     *
     * @param sessionRequest
     * @return List<SessionResult>
     */
    public List<SessionResult> querySessionListWithGroup(SessionRequest sessionRequest) {
        List<SessionResult> sessionResults = Lists.newArrayList();

        List<String> indexList = indexListBuilder(sessionRequest);
        var indexArray = new String[indexList.size()];
        indexList.toArray(indexArray);

        var searchRequest = new SearchRequest(indexArray);
        // 1.创建并设置SearchSourceBuilder对象
        var sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(sessionRequest.getLimitSize());
        var boolQueryBuilder = QueryBuilders.boolQuery();
        //时间查询
        var rangeQueryBuilder = QueryBuilders.rangeQuery("timestamp")
            .from(sessionRequest.getStartTime().toString()).to(sessionRequest.getEndTime().toString());

        //构造动态条件查询
        buildDynamicQuery(sessionRequest, boolQueryBuilder);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder.must(rangeQueryBuilder)));
        log.info("Search JSON query: {}", searchRequest.source().toString());
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<SessionModel> sessionModelList = Arrays.stream(response.getHits().getHits()).map(hit -> {
                var sessionModel = JSONObject.parseObject(hit.getSourceAsString(), SessionModel.class);
                sessionModel.setIndex(hit.getIndex());
                return sessionModel;
            }).collect(Collectors.toList());
            sessionResults = sessionModelList.stream().map(this::convertSession).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sessionResults;
    }


    /**
     * 构造动态条件查询
     * @param sessionRequest sessionRequest
     * @param boolQueryBuilder boolQueryBuilder
     */
    private void buildDynamicQuery(SessionRequest sessionRequest,
        BoolQueryBuilder boolQueryBuilder) {
        if (!ObjectUtils.isEmpty(sessionRequest.getSourceIp())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("srcIp", sessionRequest.getSourceIp()));
        }
        if (!ObjectUtils.isEmpty(sessionRequest.getTargetIp())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("dstIp", sessionRequest.getTargetIp()));
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
            throw new MqRuntimeException("分组id列表必须传");
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
            .groupId(model.getIndex().substring(0, model.getIndex().indexOf("_sessions2")))
            .sourceIp(model.getSrcIp())
            .targetIp(model.getDstIp())
            .sourceMac(model.getSrcMac())
            .targetMac(model.getDstMac())
            .protocalType(model.getProtocol())
            .build();
    }

    /**
     * 构造查询需要的索引
     *
     * @return List<IndexBoost>
     */
    private List<String> indexListBuilder(SessionRequest sessionRequest) {
        List<String> groupList = sessionRequest.getGroupList();

        List<String> dateStringList = fetchFormatterTime(sessionRequest.getStartTime(), sessionRequest.getEndTime());
        if (CollectionUtils.isEmpty(groupList)) {
            return Lists.newArrayList();
        }

        return sessionRequest.getGroupList()
            .stream().map(groupId -> dateStringList.stream().map(dateString -> sessionIndexFormatter(groupId, dateString))
                .collect(Collectors.toList()))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    /**
     * 获取比如2021-05-01 到2021-05-20.所有的日期，方便构建所有的时间字符串210501,210502,....
     *
     * @param startTime startTime
     * @param endTime endTime
     * @return List<String>
     */
    private List<String> fetchFormatterTime(LocalDateTime startTime, LocalDateTime endTime) {
        var formatter = DateTimeFormatter.ofPattern("yyMMdd");
        long betweenDay = ChronoUnit.DAYS.between(startTime, endTime);
        int days = (int) betweenDay;
        return IntStream.range(0, days + 1).boxed().map(startTime::plusDays).map((time) -> time.format(formatter))
            .collect(Collectors.toList());
    }

    /**
     * 构造流量索引
     *
     * @param groupId groupId
     * @param dateString dateString
     * @return indexString
     */
    private String sessionIndexFormatter(String groupId, String dateString) {
        StringBuilder sessionIndexBuilder = new StringBuilder();
        sessionIndexBuilder.append(groupId).append("_sessions2-")
            .append(dateString);
        return sessionIndexBuilder.toString();
    }
}
