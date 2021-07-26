package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.AuthLogConstant;
import cn.cyberpeace.mp.auth.dto.user.UserLogPageRequest;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.service.OperateLogServiceFactory;
import cn.cyberpeace.mp.devops.log.utils.SnUtils;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserLogService {

    @Autowired
    private OperateLogServiceFactory operateLogServiceFactory;

    @Resource(name = "logRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    public void record(OperationLogModel logModel) {

        //站点类型
        logModel.setSiteType(SiteTypeEnums.MP);
        //操作模块
        logModel.setOpModule(AuthLogConstant.MODULE);
        //记录操作时间
        logModel.setOpTime(LocalDateTime.now());

        if (StringUtils.isBlank(logModel.getOpIp())) {
            // todo 异步处理的时候request外部传入
            HttpServletRequest request = ServletUtils.getRequest();

            //请求IP
            // 登录接口获取不到request
            logModel.setOpIp(request == null ? "" : SnUtils.getRealRemoteIp(request));
        }


        try {
            operateLogServiceFactory.create(logModel);
        } catch (Exception e) {
            log.error("{}", ExceptionUtil.stacktraceToString(e));

        }
    }


    public PageInfo getUserLog(UserLogPageRequest logPageRequest) {
        logPageRequest.ensure();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        if (logPageRequest.getPage() * logPageRequest.getPageSize() > 10000) {
            throw new MpRuntimeException("不支持深度分页查询！");
        }

        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(logPageRequest.getModule())) {
            boolBuilder.must(QueryBuilders.termQuery("opModule.keyword", logPageRequest.getModule()));
        }
        if (StringUtils.isNotBlank(logPageRequest.getUserid())) {
            boolBuilder.must(QueryBuilders.termQuery("userId.keyword", logPageRequest.getUserid()));
        }
        if (StringUtils.isNotBlank(logPageRequest.getUsername())) {
            boolBuilder.must(QueryBuilders.termQuery("userName.keyword", logPageRequest.getUsername()));
        }
        if (StringUtils.isNotBlank(logPageRequest.getType())) {
            boolBuilder.must(QueryBuilders.termQuery("opType.keyword", logPageRequest.getType()));
        }

        sourceBuilder.query(boolBuilder).sort("opTime", SortOrder.DESC).from(logPageRequest.getPage() * logPageRequest.getPageSize()).size(logPageRequest.getPageSize());

        SearchRequest searchRequest = new SearchRequest("mp_operate_log");
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            List<OperationLogModel> logList = new ArrayList<>(hits.getHits().length);
            for (SearchHit hit : hits.getHits()) {
                OperationLogModel operationLog = JSON.parseObject(hit.getSourceAsString(), OperationLogModel.class);
                logList.add(operationLog);
            }

            PageInfo page = new PageInfo();
            page.setList(logList);
            page.setTotal(hits.getTotalHits().value);
            return page;
        } catch (Exception e) {
            log.error("{}", ExceptionUtil.stacktraceToString(e));
            throw new MpRuntimeException(e.getMessage());
        }

    }


}
