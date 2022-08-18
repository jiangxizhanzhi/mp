package cn.cyberpeace.mp.situationaware.utils;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;

@Slf4j
public class ElasticsearchUtils {
    public static SearchResponse elasticsearchQuery(RestHighLevelClient restHighLevelClient, SearchRequest searchRequest) {
        try {
            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es query error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    public static SearchResponse elasticsearchScroll(RestHighLevelClient restHighLevelClient, SearchScrollRequest scrollRequest) {
        try {
            return restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es scroll error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    public static CountResponse elasticsearchCount(RestHighLevelClient restHighLevelClient, CountRequest countRequest) {
        try {
            return restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es count error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    public static BulkByScrollResponse elasticsearchDelete(RestHighLevelClient restHighLevelClient, DeleteByQueryRequest deleteRequest) {
        try {
            return restHighLevelClient.deleteByQuery(deleteRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es delete by query error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }
}
