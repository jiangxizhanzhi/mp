package cn.cyberpeace.mp.situationaware.utils;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpHost;
import org.apache.http.util.Asserts;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.springframework.util.ObjectUtils;

@Slf4j
public class ElasticsearchUtils {
    public static RestHighLevelClient restHighLevelClient(String esAdress) {
        return new RestHighLevelClient(RestClient.builder(createHttpHost(esAdress)));
    }

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

    private static HttpHost[] createHttpHost(String esAdress) {
        Asserts.check(!ObjectUtils.isEmpty(esAdress), "ElasticSearch cluster ip address cannot empty");
        String[] ipArray = esAdress.split(",");
        var httpHosts = new HttpHost[ipArray.length];
        for (int i = 0, len = ipArray.length; i < len; i++) {
            var ipAddress = ipArray[i];
            String[] values = ipAddress.split(":");
            String ip = values[0];
            var port = Integer.parseInt(values[1]);
            httpHosts[i] = new HttpHost(ip, port, "http");
        }
        return httpHosts;
    }
}
