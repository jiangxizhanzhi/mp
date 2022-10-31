package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.collection.core.es.IRestHighLevelClient;
import cn.cyberpeace.mp.collection.model.contest.ContestLogModel;
import cn.cyberpeace.mp.collection.request.LogQueryRequest;
import cn.cyberpeace.mp.collection.util.EsResponseParseUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ContestLogService {
    @Resource(name = "restHighLevelClientImpl")
    private IRestHighLevelClient restHighLevelClient;

    public static final String INDEX_MP_CONTEST_LOG = "mp_contest_log";

    public void addContestLog(ContestLogModel contestLogModel) {
        Map<String, Object> source = JSON.parseObject(JSON.toJSONString(contestLogModel), Map.class);
        IndexRequest indexRequest = new IndexRequest(INDEX_MP_CONTEST_LOG).source(source);
        restHighLevelClient.index(indexRequest);
    }

    public ListObjResult<ContestLogModel> queryContestLog(LogQueryRequest request) {
        var sourceBuilder = buildSearchSourceBuilder(request);
        var boolQueryBuilder = buildBoolQueryBuilder(request);
        SearchRequest searchRequest = buildSearchRequest(sourceBuilder, boolQueryBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest);
        List<ContestLogModel> contestLogModels = new ArrayList<>();
        if (Objects.isNull(response) || Objects.isNull(response.getHits()) || Objects.isNull(response.getHits().getHits())) {
            return new ListObjResult<>(contestLogModels, 0);
        }
        contestLogModels = EsResponseParseUtil.parseResponse(response, ContestLogModel.class);
        long total = 0L;
        if (Objects.nonNull(response.getHits()) && Objects.nonNull(response.getHits().getTotalHits())) {
            total = response.getHits().getTotalHits().value;
        }
        return new ListObjResult<>(contestLogModels, BigDecimal.valueOf(total).intValue());
    }

    private SearchSourceBuilder buildSearchSourceBuilder(LogQueryRequest request) {
        var sourceBuilder = new SearchSourceBuilder();
        // 按时间排序
        sourceBuilder.sort("time", SortOrder.DESC);
        //处理分页
        processPageable(request, sourceBuilder);
        return sourceBuilder;
    }

    private void processPageable(LogQueryRequest request, SearchSourceBuilder sourceBuilder) {
        int offset = (request.getPageNum() - 1) * (request.getPageSize());
        sourceBuilder.from(Math.min(offset, 10000));
        if ((offset + request.getPageSize()) > 10000) {
            sourceBuilder.size(Math.max(10000 - offset, 0));
        } else {
            sourceBuilder.size(request.getPageSize());
        }
    }

    private BoolQueryBuilder buildBoolQueryBuilder(LogQueryRequest request) {
        var boolQueryBuilder = QueryBuilders.boolQuery();
        //构造查询条件
        buildLogBoolQueryBuilder(request, boolQueryBuilder);
        return boolQueryBuilder;
    }

    private void buildLogBoolQueryBuilder(LogQueryRequest request, BoolQueryBuilder boolQueryBuilder) {
        if (CollectionUtils.isNotEmpty(request.getIps())) {
            buildIpQuery(request.getIps(), boolQueryBuilder);
        }

        if (CollectionUtils.isNotEmpty(request.getAttackIps())) {
            buildAttackIpQuery(request.getAttackIps(), boolQueryBuilder);
        }

        if (CollectionUtils.isNotEmpty(request.getSeverity())) {
            buildSeverityQuery(request.getSeverity(), boolQueryBuilder);
        }

        if (Objects.nonNull(request.getStartTime()) && Objects.nonNull(request.getEndTime())) {
            //时间查询
            boolQueryBuilder.must(QueryBuilders.rangeQuery("time")
                    .from(request.getStartTime())
                    .to(request.getEndTime()));
        }
    }

    private void buildIpQuery(List<String> ips, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("ip", ips));
    }

    private void buildAttackIpQuery(List<String> attackIps, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("attackIp", attackIps));
    }

    private void buildSeverityQuery(List<Integer> severity, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("severity", severity));
    }

    private SearchRequest buildSearchRequest(SearchSourceBuilder sourceBuilder, BoolQueryBuilder boolQueryBuilder) {
        var searchRequest = new SearchRequest(INDEX_MP_CONTEST_LOG);

        searchRequest.source(sourceBuilder.query(boolQueryBuilder));

        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        return searchRequest;
    }
}
