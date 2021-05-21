package cn.cyberpeace.mp.data.sandtable.service;

import cn.cyberpeace.mp.data.sandtable.model.SessionModel;
import cn.cyberpeace.mp.data.sandtable.model.SessionQuery;
import cn.cyberpeace.mp.data.sandtable.model.SessionResult;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class SessionService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 分组维度查询流量
     * @param sessionQuery
     * @return List<SessionResult>
     */
    public List<SessionResult> querySessionListWithGroup(SessionQuery sessionQuery) {
        List<String> indexList = indexListBuilder(sessionQuery);
        String[] indexArray = new String[indexList.size()];
        indexList.toArray(indexArray);
        SearchRequest searchRequest = new SearchRequest(indexArray);
        // 1.创建并设置SearchSourceBuilder对象
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("timestamp").gte(sessionQuery.getStartTime().toString()).lte(sessionQuery.getEndTime().toString());
        boolQueryBuilder.must(rangeQueryBuilder);
        sourceBuilder.query(boolQueryBuilder);

        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            response.getHits();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询所有集群
     * @param sessionQuery
     * @return
     */
    public List<SessionResult> querySessionAllList(SessionQuery sessionQuery) {
        return null;
    }


    /**
     * 构造查询需要的索引
     * @return List<IndexBoost>
     */
    private List<String> indexListBuilder(SessionQuery sessionQuery) {
        List<String> groupList = sessionQuery.getGroupList();
        List<String> dateStringList = fetchFormatterTime(sessionQuery.getStartTime(), sessionQuery.getEndTime());
        if (!CollectionUtils.isEmpty(groupList)) {
            return sessionQuery.getGroupList()
                .stream().map(groupId-> dateStringList.stream().map(dateString -> sessionIndexFormatter(groupId, dateString))
                .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        }else {
            return dateStringList.stream().map(dateString ->
                sessionIndexFormatter(sessionQuery.getGroupId(), dateString)).collect(Collectors.toList());
        }
    }


    /**
     * 获取比如2021-05-01 到2021-05-20.所有的日期，方便构建所有的时间字符串210501,210502,....
     * @param startTime  startTime
     * @param endTime endTime
     * @return List<String>
     */
    private List<String> fetchFormatterTime(LocalDateTime startTime, LocalDateTime endTime) {
        var formatter = DateTimeFormatter.ofPattern("yyMMdd");
        long betweenDay = ChronoUnit.DAYS.between(startTime, endTime);
        int days = (int) betweenDay;
        return IntStream.range(0, days + 1).boxed().map(startTime::plusDays).map((time) ->time.format(formatter))
            .collect(Collectors.toList());
    }


    /**
     * 构造流量索引
     * @param groupId  groupId
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
