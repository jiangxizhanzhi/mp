package cn.cyberpeace.mp.data.sandtable.service;

import cn.cyberpeace.mp.data.sandtable.model.SessionModel;
import cn.cyberpeace.mp.data.sandtable.model.SessionQuery;
import cn.cyberpeace.mp.data.sandtable.model.SessionResult;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.IndexBoost;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class SessionService {


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 分组维度查询流量
     * @param sessionQuery
     * @return List<SessionResult>
     */
    public List<SessionResult> querySessionListWithGroup(SessionQuery sessionQuery) {
        NativeSearchQueryBuilder builder=new NativeSearchQueryBuilder();
        builder.withIndicesBoost(indexBoostsBuilder(sessionQuery))
        .withQuery(QueryBuilders.rangeQuery("@timestamp").from(sessionQuery.getStartTime().toString())
            .to(sessionQuery.getEndTime().toString()))
        ;
//        builder.withQuery(QueryBuilder)
        return null;
    }

    /**
     * 查询所有集群
     * @param sessionQuery
     * @return
     */
    public List<SessionResult> querySessionAllList(SessionQuery sessionQuery) {
        NativeSearchQueryBuilder builder=new NativeSearchQueryBuilder();
        builder.withIndicesBoost(indexBoostsBuilder(sessionQuery));
        return null;
    }

    /**
     * 构造查询需要的索引
     * @return List<IndexBoost>
     */
    private List<IndexBoost> indexBoostsBuilder(SessionQuery sessionQuery) {
        List<String> groupList = sessionQuery.getGroupList();
        if (!CollectionUtils.isEmpty(groupList)) {
//            localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//            return sessionQuery.getGroupList().stream().map(groupId-> new IndexBoost(groupId+"default_sessions2", groupList.indexOf(groupId))).collect(Collectors.toList());

            return null;
        }else {
            return Lists.newArrayList(new IndexBoost(sessionQuery.getGroupId()+"default_sessions2*", 1));
        }
    }

    /**
     * 获取比如2021-05-01 到2021-05-20.所有的日期，方便构建所有
     * @param startTime
     * @param endTime
     * @return List<String>
     */
    private List<String> fetchFormatterTime(LocalDateTime startTime, LocalDateTime endTime) {
        var formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        long betweenDay = ChronoUnit.DAYS.between(startTime, endTime);
        int days = (int) betweenDay;
        return IntStream.range(0, days + 1).boxed().map(startTime::plusDays).map((time) ->time.format(formatter))
            .collect(Collectors.toList());
    }
}
