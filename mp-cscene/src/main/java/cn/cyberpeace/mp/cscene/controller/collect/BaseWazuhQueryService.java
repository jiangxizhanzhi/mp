package cn.cyberpeace.mp.cscene.controller.collect;

import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseWazuhQueryService<T> {

    protected RestHighLevelClient restHighLevelClient;

    public BaseWazuhQueryService(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    protected abstract BoolQueryBuilder getBizQueryBuilder();

    protected abstract T parsedOneData(WazuhFileRoot wazuhFileRoot);

    public WazuhResult getEvents(String agentName, int from, int size) {
        var finalQuery = QueryBuilders.boolQuery();
        var mustAgentName = QueryBuilders.termQuery("agent.name", agentName);
        finalQuery.must(mustAgentName).must(this.getBizQueryBuilder());

        var sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(finalQuery);
        sourceBuilder.size(size);
        sourceBuilder.from(from);

        String[] indexArray = DataCollectionService.buildRequestIndex(LocalDateTime.now(), LocalDateTime.now());
        var searchRequest = new SearchRequest(indexArray);
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<WazuhFileRoot> wazuhFileRootList = new ArrayList<>();
            for (SearchHit hit : response.getHits().getHits()) {
                wazuhFileRootList.add(JSONObject.parseObject(hit.getSourceAsString(), WazuhFileRoot.class));
            }

            WazuhResult wazuhResult = new WazuhResult();
            wazuhResult.setList(wazuhFileRootList);
            wazuhResult.setTotal(response.getHits().getTotalHits().value);
            return wazuhResult;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public QueryResult<T> getParsedEvents(String agentName, int from, int size) {
        WazuhResult wazuhResult = this.getEvents(agentName, from, size);
        if (wazuhResult.total == 0) {
            QueryResult<T> result = new QueryResult<>();
            result.setData(new ArrayList<>());
            result.setTotal(0);
            return result;
        } else {
            QueryResult<T> result = new QueryResult<>();
            result.setTotal(wazuhResult.getTotal());
            List<T> data = new ArrayList<>();
            for (WazuhFileRoot r : wazuhResult.getList()) {
                data.add(this.parsedOneData(r));
            }
            result.setData(data);
            return result;
        }
    }

    @Getter
    @Setter
    protected static class WazuhResult {
        private List<WazuhFileRoot> list;
        private long total;
    }

    @Setter
    @Getter
    public static class QueryResult<T> {
        private List<T> data;
        private long total;
    }
}
