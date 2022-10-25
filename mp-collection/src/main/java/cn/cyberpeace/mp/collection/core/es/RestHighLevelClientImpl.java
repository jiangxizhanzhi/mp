package cn.cyberpeace.mp.collection.core.es;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpHost;
import org.apache.http.util.Asserts;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.IOException;

@Component
@Slf4j
public class RestHighLevelClientImpl implements IRestHighLevelClient{
//    private static volatile boolean init = false;

    @Resource(name = "restHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

//    private static Map<String, RestHighLevelClient> restHighLevelClientMap;
//
//    public static void updateClientMap() {
//        init = false;
//        initClientMap();
//    }
//
//    public static void initClientMap() {
//        if (!init && CollectionUtils.isNotEmpty(ClusterConfig.nodes)) {
//            restHighLevelClientMap = new ConcurrentHashMap<>();
//            ClusterConfig.nodes.forEach(node ->
//                    restHighLevelClientMap.put(node.getNodeId(), restHighLevelClient(node.getEsAddress())));
//            init = true;
//        }
//    }
//
//    public static RestHighLevelClient restHighLevelClient(String esAddress) {
//        return new RestHighLevelClient(RestClient.builder(createHttpHost(esAddress)));
//    }

    @Override
    public SearchResponse search(SearchRequest searchRequest) {
        try {
            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es query error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public SearchResponse scroll(SearchScrollRequest scrollRequest) {
        try {
            return restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es scroll error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public CountResponse count(CountRequest countRequest) {
        try {
            return restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es count error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public BulkByScrollResponse deleteByQuery(DeleteByQueryRequest deleteRequest) {
        try {
            return restHighLevelClient.deleteByQuery(deleteRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es delete by query error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

//    @Override
//    public void allDeleteByQuery(DeleteByQueryRequest deleteRequest) {
//        restHighLevelClientMap.values().forEach(restHighLevelClient -> {
//            try {
//                restHighLevelClient.deleteByQuery(deleteRequest, RequestOptions.DEFAULT);
//            } catch (Exception e) {
//                log.error("es delete by query error: {}", ExceptionUtils.getStackTrace(e));
//            }
//        });
//    }

    @Override
    public GetIndexResponse getIndices(GetIndexRequest getIndexRequest) {
        try {
            return restHighLevelClient.indices().get(getIndexRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es get indices error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public IndexResponse index(IndexRequest indexRequest) {
        try {
            return restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es index error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    private static HttpHost[] createHttpHost(String esAddress) {
        Asserts.check(!ObjectUtils.isEmpty(esAddress), "ElasticSearch cluster ip address cannot empty");
        String[] ipArray = esAddress.split(",");
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
