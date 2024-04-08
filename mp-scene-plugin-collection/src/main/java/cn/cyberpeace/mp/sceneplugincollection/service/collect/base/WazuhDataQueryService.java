package cn.cyberpeace.mp.sceneplugincollection.service.collect.base;

import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WazuhDataQueryService {

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    public WazuhResult<WazuhFileRoot> queryWazuhData(String[] indexArray, int from, int size, List<BoolQueryBuilder> boolQueryBuilders, List<SortBuilder<?>> sorts) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder finalBuilder = QueryBuilders.boolQuery();
        for (BoolQueryBuilder queryBuilder : boolQueryBuilders) {
            finalBuilder.should(queryBuilder);
        }

        sourceBuilder.query(finalBuilder);
        sourceBuilder.size(size);
        sourceBuilder.from(from);
        sourceBuilder.sort(sorts);

        SearchRequest searchRequest = new SearchRequest(indexArray);
        searchRequest.source(sourceBuilder);
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
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

}
