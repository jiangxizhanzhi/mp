package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.rest.RestStatus;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;

/**
 * 基础ES操作接口
 * @author wanggang
 */
@Slf4j
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
        } catch (Exception e) {
            throw new MpRuntimeException(e.getMessage());
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
        } catch (ElasticsearchStatusException esEx) {
            log.error("catch ElasticsearchStatusException ex:{}", ExceptionUtil.stacktraceToString(esEx));
            if (Objects.equals(RestStatus.NOT_FOUND, esEx.status())) {
                checkOrCreateIndex(searchRequest.indices());
                try {
                    log.info("retry search...");
                    return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new MpRuntimeException(esEx.getMessage());
                }
            } else {
                throw new MpRuntimeException(esEx.getMessage());
            }
        } catch (IOException e) {
            throw new MpRuntimeException(e.getMessage());
        }
    }

    private void checkOrCreateIndex(String... indices) {
        log.info("checkOrCreateIndex...");
        GetIndexRequest getIndexRequest = new GetIndexRequest(indices);
        try {
            boolean indexExisted = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
            log.info("indices:{}, indexExisted:{}", indices, indexExisted);
            if (!indexExisted) {
                for (String index : indices) {
                    log.info("create index:{}", index);
                    CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
                    restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
                }
            }
        } catch (IOException e) {
            throw new MpRuntimeException(e.getMessage());
        }
    }

}
