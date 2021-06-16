package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * 基础ES操作接口
 * @author wanggang
 */
public abstract class BasEsLogService {

    @Resource(name = "logRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引并新增数据
     * @param index
     * @param source
     * @return
     */
    protected Boolean create(String index, Map<String,Object> source) {
        IndexRequest indexRequest = new IndexRequest(index).source(source);
        try {
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            if (response.status() == RestStatus.CREATED) {
                return Boolean.TRUE;
            }
        } catch (IOException e) {
            throw new MpRuntimeException("restHighLevelClient.create.error");
        }
        return Boolean.FALSE;
    }


}
