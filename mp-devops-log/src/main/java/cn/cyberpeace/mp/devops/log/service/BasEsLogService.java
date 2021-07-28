package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 基础ES操作接口
 * @author wanggang
 */
public abstract class BasEsLogService {

    @Resource(name = "logRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引并新增数据
     * @param indexRequest
     * @return
     */
    protected IndexResponse create(IndexRequest indexRequest) {
        try {
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            if (response.status() == RestStatus.CREATED) {
                return response;
            }
        } catch (IOException e) {
            throw new MpRuntimeException("restHighLevelClient.create.error");
        }
        return null;
    }

    /**
     * 查询
     * @param searchRequest
     * @return
     */
    protected SearchResponse search(SearchRequest searchRequest){
        try {
            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new MpRuntimeException("restHighLevelClient.search.error");
        }
    }

}
