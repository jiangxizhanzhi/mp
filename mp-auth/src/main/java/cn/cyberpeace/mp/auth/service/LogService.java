package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.AuthLogConstant;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.service.OperateLogServiceFactory;
import cn.cyberpeace.mp.devops.log.utils.SnUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class LogService {

    @Autowired
    private OperateLogServiceFactory operateLogServiceFactory;

    @Resource(name = "logRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    public void record(UserModel user) {
        if (Objects.isNull(user)) {
            return;
        }

        OperationLogModel operationLogModel = new OperationLogModel();
        //站点类型
        operationLogModel.setSiteType(SiteTypeEnums.MP);
        //操作模块
        operationLogModel.setOpModule(AuthLogConstant.MODULE);
        //操作类型
        operationLogModel.setOpType(AuthLogConstant.TYPE_LOGIN);
        //操作主题
        operationLogModel.setTitle("登录");
        //操作描述
        operationLogModel.setDescription("账号密码登录");
        //记录操作时间
        operationLogModel.setOpTime(LocalDateTime.now());

        // todo 异步处理的时候request外部传入
        HttpServletRequest request = ServletUtils.getRequest();

        //请求IP
        // 登录接口获取不到request
        operationLogModel.setOpIp(request == null ? "" : SnUtils.getRealRemoteIp(request));

        //操作用
        operationLogModel.setUserId(user.getId());
        operationLogModel.setUserName(user.getUsername());
        operationLogModel.setUserType(LogConstant.USER_TYPE_USER);

        operateLogServiceFactory.create(operationLogModel);
    }


    public void getUserLog(String userId, String username, String type) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

//        sourceBuilder.query(QueryBuilders.termQuery("user", "kimchy"));

        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

        boolBuilder.must(QueryBuilders.termQuery("opModule.keyword", AuthLogConstant.MODULE));

        if (StringUtils.isNotBlank(username)) {
            boolBuilder.must(QueryBuilders.termQuery("userName.keyword", username));
        }
        if (StringUtils.isNotBlank(type)) {
            boolBuilder.must(QueryBuilders.termQuery("opType.keyword", type));
        }

        sourceBuilder.query(boolBuilder);
        sourceBuilder.sort("opTime", SortOrder.DESC);
        SearchRequest searchRequest = new SearchRequest("mp_operate_log");
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits.getHits()) {
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField highlight = highlightFields.get("title");
                Text[] fragments = highlight.fragments();
                String fragmentString = fragments[0].string();
            }
        } catch (Exception e) {

        }


    }


}
