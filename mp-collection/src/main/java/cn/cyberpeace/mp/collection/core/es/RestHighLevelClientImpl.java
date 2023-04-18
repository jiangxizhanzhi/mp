package cn.cyberpeace.mp.collection.core.es;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import cn.cyberpeace.mp.collection.config.ClusterConfig;
import cn.cyberpeace.mp.collection.context.ContextHolder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RestHighLevelClientImpl implements IRestHighLevelClient{
    private static volatile boolean init = false;

    private static Map<String, RestHighLevelClient> restHighLevelClientMap;

    private static Map<String, HttpClient> httpClientMap;

    private static Map<String, GroupSet> groupSetMap = new ConcurrentHashMap<>();

    public static void updateClientMap() {
        init = false;
        initClientMap();
    }

    public static void initClientMap() {
        if (!init && CollectionUtils.isNotEmpty(ClusterConfig.nodes)) {
            restHighLevelClientMap = new ConcurrentHashMap<>();
            httpClientMap = new ConcurrentHashMap<>();
            ClusterConfig.nodes.forEach(node -> {
                restHighLevelClientMap.put(node.getNodeId(), restHighLevelClient(node.getEsAddress()));
                httpClientMap.put(node.getNodeId(), HttpClient.builder().configServer("http://" + node.getEsAddress()).build());
            });
            init = true;
        }
    }

    public static RestHighLevelClient restHighLevelClient(String esAddress) {
        return new RestHighLevelClient(RestClient.builder(createHttpHost(esAddress)));
    }

    @Override
    public SearchResponse search(SearchRequest searchRequest) {
        try {
            return restHighLevelClientMap.get(ContextHolder.getContext().getGroupId()).search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es query error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public SearchResponse scroll(SearchScrollRequest scrollRequest) {
        try {
            return restHighLevelClientMap.get(ContextHolder.getContext().getGroupId()).scroll(scrollRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es scroll error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public CountResponse count(CountRequest countRequest) {
        try {
            return restHighLevelClientMap.get(ContextHolder.getContext().getGroupId()).count(countRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es count error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public BulkByScrollResponse deleteByQuery(DeleteByQueryRequest deleteRequest) {
        try {
            return restHighLevelClientMap.get(ContextHolder.getContext().getGroupId()).deleteByQuery(deleteRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es delete by query error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void allDeleteByQuery(DeleteByQueryRequest deleteRequest) {
        restHighLevelClientMap.values().forEach(restHighLevelClient -> {
            try {
                restHighLevelClient.deleteByQuery(deleteRequest, RequestOptions.DEFAULT);
            } catch (Exception e) {
                log.error("es delete by query error: {}", ExceptionUtils.getStackTrace(e));
            }
        });
    }

    @Override
    public GetIndexResponse getIndices(GetIndexRequest getIndexRequest) {
        try {
            return restHighLevelClientMap.get(ContextHolder.getContext().getGroupId()).indices().get(getIndexRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es get indices error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void updateDataMaxQueryNum(String index, long total) {
        GroupSet groupSet = groupSetMap.getOrDefault(ContextHolder.getContext().getGroupId(), new GroupSet());
        groupSetMap.put(ContextHolder.getContext().getGroupId(), groupSet);
        LocalDate date = TimeUtils.utcToLocalDateTime(TimeUtils.localDateTimeToUtcString(LocalDateTime.now())).toLocalDate();
        if (groupSet.getIndex().contains(index) && Objects.equals(groupSet.getDate(), date) && groupSet.getMaxTotal() > total) {
            return;
        }
        groupSet.setDate(date);
        groupSet.getIndex().add(index);
        groupSet.setMaxTotal(getMaxTotal(total, groupSet.getMaxTotal()));

        IndexParams indexParams = new IndexParams();
        indexParams.setMaxResultWindow(groupSet.getMaxTotal());
        Data data = new Data();
        data.setIndex(indexParams);
        httpClientMap.get(ContextHolder.getContext().getGroupId()).put("/" + index + "/_settings", data, null, new HashMap<>(), null);
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

    private long getMaxTotal(long total, long maxTotal) {
        long initialMaxTotal = 1000000l;
        for (int i = 1; maxTotal < total; i++) {
            maxTotal = maxTotal + initialMaxTotal * i;
        }
        return maxTotal;
    }

    @Getter
    @Setter
    private static class Data implements SnakeHttpData {
        private IndexParams index;
    }

    @Getter
    @Setter
    private static class IndexParams {
        private long maxResultWindow;
    }

    @Getter
    @Setter
    private static class GroupSet {
        private LocalDate date;
        private long maxTotal;
        private Set<String> index = new HashSet<>();
    }
}
