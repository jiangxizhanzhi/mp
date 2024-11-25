package cn.cyberpeace.mp.cscene.controller.collect.base;

import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WazuhQueryService {

    private RestHighLevelClient restHighLevelClient;

    public WazuhQueryService(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    public <T> WazuhParsedResult<T> queryParsedWazuhData(String[] indexArray, int from, int size, WazuhDataQueryParamsBuilder paramsBuilder, WazuhDataParser<T> dataParser) {
        WazuhResult wazuhResult = this.queryWazuhData(indexArray, from, size, paramsBuilder);
        return this.parseWazuhData(wazuhResult, dataParser);
    }

    public WazuhResult queryWazuhData(String[] indexArray, int from, int size, WazuhDataQueryParamsBuilder paramsBuilder) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(paramsBuilder.build());
        sourceBuilder.size(size);
        sourceBuilder.from(from);
        sourceBuilder.sort(paramsBuilder.sorts());

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

    public <T> WazuhParsedResult<T> parseWazuhData(WazuhResult wazuhResult, WazuhDataParser<T> dataParser) {
        WazuhParsedResult<T> result = new WazuhParsedResult<>();
        result.setTotal(wazuhResult.getTotal());
        List<T> data = new ArrayList<>();
        if (wazuhResult.getList() != null) {
            for (WazuhFileRoot r : wazuhResult.getList()) {
                data.add(dataParser.parsedOneData(r));
            }
        }
        result.setData(data);
        return result;
    }

    @Getter
    @Setter
    public static class WazuhResult {
        private List<WazuhFileRoot> list;
        private long total;
    }

}
