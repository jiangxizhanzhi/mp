package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devops.log.service.BasEsLogService;
import cn.cyberpeace.mp.devops.log.service.OperateLogService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * MP中台的操作日志
 * @author wanggang
 */
@Slf4j
@Service
public class MpOperateLogServiceImpl extends BasEsLogService implements OperateLogService {

    @Override
    public String create(OperationLogModel operationLogModel) {
        Map<String,Object> source = JSON.parseObject(JSON.toJSONString(operationLogModel), Map.class);
        return super.create("mp_operate_log",source);
    }


    @Override
    public void search(OperationLogPageRequest request) {
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//
//        if (request.getLimit() * request.getOffset() > 10000) {
//            throw new MpRuntimeException("不支持深度分页查询！");
//        }
//
//        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
//        String module = request.getModule();
//        String userName = request.getUserName();
//
//        if (StringUtils.isNotBlank(module)) {
//            boolBuilder.must(QueryBuilders.termQuery("opModule.keyword", module));
//        }
//
//        if (StringUtils.isNotBlank(request.getUserId())) {
//            boolBuilder.must(QueryBuilders.termQuery("userId.keyword", request.getUserid()));
//        }
//
//        if (StringUtils.isNotBlank(userName)) {
//            boolBuilder.must(QueryBuilders.termQuery("userName.keyword", userName));
//        }
//        if (StringUtils.isNotBlank(request.getOpType())) {
//            boolBuilder.must(QueryBuilders.termQuery("opType.keyword", logPageRequest.getType()));
//        }
//
//        sourceBuilder.query(boolBuilder).sort("opTime", SortOrder.DESC).from(logPageRequest.getPage() * logPageRequest.getPageSize()).size(logPageRequest.getPageSize());
//
//        SearchRequest searchRequest = new SearchRequest("mp_operate_log");
//        searchRequest.source(sourceBuilder);
//
//        try {
//            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//            SearchHits hits = searchResponse.getHits();
//            List<OperationLogModel> logList = new ArrayList<>(hits.getHits().length);
//            for (SearchHit hit : hits.getHits()) {
//                OperationLogModel operationLog = JSON.parseObject(hit.getSourceAsString(), OperationLogModel.class);
//                logList.add(operationLog);
//            }
//
//            PageInfo page = new PageInfo();
//            page.setList(logList);
//            page.setTotal(hits.getTotalHits().value);
//            return page;
//        } catch (Exception e) {
//            log.error("{}", ExceptionUtil.stacktraceToString(e));
//            throw new MpRuntimeException(e.getMessage());
//        }
    }

    @Override
    public SiteTypeEnums siteType(){
        return SiteTypeEnums.MP;
    }
}
