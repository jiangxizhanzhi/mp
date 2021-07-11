package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.log.model.OperationLogModel;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.service.BasEsLogService;
import cn.cyberpeace.mp.log.service.MpOperateLogService;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;


@Slf4j
@Service
public class MpOperateLogServiceImpl extends BasEsLogService implements MpOperateLogService {

    private static final String INDEX_MP_OPERATE_LOG = "mp_operate_log";

    private boolean indexExisted = false;

    @Override
    public IndexResponse create(OperationLogModel operationLogModel) {
        if (!isIndexExisted()) {
            return null;
        }
        Map<String,Object> source = JSON.parseObject(JSON.toJSONString(operationLogModel), Map.class);
        IndexRequest indexRequest = new IndexRequest(INDEX_MP_OPERATE_LOG).source(source);
        return super.create(indexRequest);
    }


    @Override
    public SearchResponse search(OperationLogPageRequest request) {
        if (!isIndexExisted()) {
            return null;
        }
        SearchRequest searchRequest = new SearchRequest(INDEX_MP_OPERATE_LOG);
        SearchSourceBuilder sourceBuilder = request.buildSearchSourceBuilder();
        sourceBuilder.trackTotalHits(true);
        searchRequest.source(sourceBuilder);
        log.info("query ES DSL:\n{}", searchRequest.source().toString());
        return super.search(searchRequest);
    }

    @Override
    public SiteTypeEnums siteType(){
        return SiteTypeEnums.MP;
    }

    private boolean isIndexExisted() {
        if (indexExisted) {
            return true;
        }
        try {
            GetIndexRequest getIndexRequest = new GetIndexRequest();
            getIndexRequest.indices(INDEX_MP_OPERATE_LOG);
            synchronized (this) {
                boolean exists = getRestHighLevelClient().indices().exists(getIndexRequest, RequestOptions.DEFAULT);
                if (!exists) {
                    CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX_MP_OPERATE_LOG);
                    createIndexRequest.settings(Settings.builder().put("max_result_window", Integer.MAX_VALUE));
                    getRestHighLevelClient().indices().create(createIndexRequest, RequestOptions.DEFAULT);
                }
            }
            indexExisted = true;
            return true;
        } catch (Exception e) {
            log.error("es getIndexRequest error! {}", ExceptionUtil.stacktraceToString(e));
            return false;
        }
    }


}
