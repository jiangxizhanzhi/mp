package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.base.common.BaseCommon;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.devops.log.config.ElasticsearchConfig;
import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devops.log.service.BasEsLogService;
import cn.cyberpeace.mp.devops.log.service.OperateLogService;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * MP中台的操作日志
 * @author wanggang
 */
@Slf4j
@Service
public class MpOperateLogServiceImpl extends BasEsLogService implements OperateLogService {

    private static final String INDEX_MP_OPERATE_LOG = "mp_operate_log";

    @Override
    public IndexResponse create(OperationLogModel operationLogModel) {
        Map<String,Object> source = JSON.parseObject(JSON.toJSONString(operationLogModel), Map.class);
        IndexRequest indexRequest = new IndexRequest(INDEX_MP_OPERATE_LOG).source(source);
        return super.create(indexRequest);
    }


    @Override
    public SearchResponse search(OperationLogPageRequest request) {
        if (request.getLimit() * request.getOffset() > 100000) {
            throw new MpRuntimeException("不支持深度分页查询！");
        }
        ElasticsearchConfig config = BeanCommon.getBean(ElasticsearchConfig.class);


        try {
            String content = "{\"index.max_result_window\": \"100000\"}";
            HttpUtils.sendPut("http://" + config.getElasticsearchNodes() + "/mp_operate_log/_settings", content, null);
        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
        }

        SearchRequest searchRequest = new SearchRequest(INDEX_MP_OPERATE_LOG);
        var builder = request.buildSearchSourceBuilder();
        builder.trackTotalHits(true);
        searchRequest.source(builder);
        return super.search(searchRequest);
    }

    @Override
    public SiteTypeEnums siteType(){
        return SiteTypeEnums.MP;
    }


}
