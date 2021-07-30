package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.user.UserLogPageRequest;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.devops.log.controller.OperateLogController;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devops.log.request.OperationLogRequest;
import cn.cyberpeace.mp.devops.log.response.OperationLogPageResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserLogService {

    @Autowired
    private OperateLogServiceFactory operateLogServiceFactory;

    @Autowired
    private OperateLogController operateLogController;

    @Resource(name = "logRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    public void record(OperationLogRequest logModel) {

        //操作模块
        logModel.setOpModule(OpModuleEnums.ACCOUNT.ordinal());

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

    public List<OperationLogPageResponse> listOpLog(OperationLogPageRequest request) {
        ListObjServiceResult<OperationLogPageResponse> response = operateLogController.page(request);
        if (Objects.nonNull(response.getData().getList())) {
            List<OperationLogPageResponse> list = response.getData().getList();
            return list;
        }
        return new ArrayList<>();
    }


}
