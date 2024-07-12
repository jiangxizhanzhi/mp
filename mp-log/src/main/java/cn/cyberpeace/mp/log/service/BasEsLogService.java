 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BasEsLogService.java.
 // File size 1946.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
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
 * @ClassName BasEsLogService
 * @Description 基础ES操作接口
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public abstract class BasEsLogService {

    @Resource(name = "logRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引并新增数据
     *
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
     *
     * @param searchRequest
     * @return
     */
    protected SearchResponse search(SearchRequest searchRequest){
        try {
            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new MpRuntimeException(e.getMessage());
        }
    }

    /**
     * getRestHighLevelClient
     *
     * @return
     */
    protected RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

}
