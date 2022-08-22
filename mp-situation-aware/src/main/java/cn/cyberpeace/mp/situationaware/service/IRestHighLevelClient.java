package cn.cyberpeace.mp.situationaware.service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;

public interface IRestHighLevelClient {
    SearchResponse elasticsearchQuery(SearchRequest searchRequest);

    SearchResponse elasticsearchScroll(SearchScrollRequest scrollRequest);

    CountResponse elasticsearchCount(CountRequest countRequest);

    BulkByScrollResponse elasticsearchDelete(DeleteByQueryRequest deleteRequest);
}
