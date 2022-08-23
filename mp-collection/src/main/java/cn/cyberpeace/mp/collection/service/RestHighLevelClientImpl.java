package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.collection.config.GroupConfig;
import cn.cyberpeace.mp.collection.context.ContextHolder;
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
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RestHighLevelClientImpl implements IRestHighLevelClient{
    private static Map<String,RestHighLevelClient> restHighLevelClientMap = new ConcurrentHashMap<>();

    @Resource
    private GroupConfig groupConfig;

    @Autowired
    public RestHighLevelClientImpl(GroupConfig groupConfig) {
        this.groupConfig = groupConfig;
        if (this.groupConfig != null && CollectionUtils.isNotEmpty(this.groupConfig.getGroups())) {
            for (var group : this.groupConfig.getGroups()) {
                restHighLevelClientMap.put(group.getGroupId(), this.restHighLevelClient(group.getEsNode()));
            }
        }
    }

    public RestHighLevelClient restHighLevelClient(String esAdress) {
        return new RestHighLevelClient(RestClient.builder(createHttpHost(esAdress)));
    }

    @Override
    public SearchResponse elasticsearchQuery(SearchRequest searchRequest) {
        try {
            return restHighLevelClientMap.get(ContextHolder.getGroupId()).search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es query error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public SearchResponse elasticsearchScroll(SearchScrollRequest scrollRequest) {
        try {
            return restHighLevelClientMap.get(ContextHolder.getGroupId()).scroll(scrollRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es scroll error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public CountResponse elasticsearchCount(CountRequest countRequest) {
        try {
            return restHighLevelClientMap.get(ContextHolder.getGroupId()).count(countRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es count error: ", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public BulkByScrollResponse elasticsearchDelete(DeleteByQueryRequest deleteRequest) {
        try {
            return restHighLevelClientMap.get(ContextHolder.getGroupId()).deleteByQuery(deleteRequest, RequestOptions.DEFAULT);
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
