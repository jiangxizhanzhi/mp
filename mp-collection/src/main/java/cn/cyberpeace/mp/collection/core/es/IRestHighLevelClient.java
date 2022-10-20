package cn.cyberpeace.mp.collection.core.es;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;

public interface IRestHighLevelClient {
    SearchResponse search(SearchRequest searchRequest);

    SearchResponse scroll(SearchScrollRequest scrollRequest);

    CountResponse count(CountRequest countRequest);

    BulkByScrollResponse deleteByQuery(DeleteByQueryRequest deleteRequest);

//    void allDeleteByQuery(DeleteByQueryRequest deleteRequest);

    GetIndexResponse getIndices(GetIndexRequest getIndexRequest);
}
