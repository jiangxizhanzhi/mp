package cn.cyberpeace.mp.devops.log.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devops.log.request.OperationLogRequest;
import cn.cyberpeace.mp.devops.log.response.OperationLogPageResponse;
import cn.cyberpeace.mp.devops.log.service.OperateLogServiceFactory;
import cn.cyberpeace.mp.devops.log.utils.SnUtils;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户操作日志记录
 * @author wanggang
 */
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/operate")
@Api(tags = "日志模块")
public class OperateLogController {

    private OperateLogServiceFactory operateLogServiceFactory;

    @Autowired
    public OperateLogController(OperateLogServiceFactory operateLogServiceFactory) {
        this.operateLogServiceFactory = operateLogServiceFactory;
    }

    @ApiOperation(value = "新增操作日志")
    @PostMapping("/add")
    public ServiceResult add(@RequestBody OperationLogRequest request, HttpServletRequest httpServletRequest){
        request.setOpIp(SnUtils.getRealRemoteIp(httpServletRequest));
        operateLogServiceFactory.create(request);
        return ServiceResult.successful();
    }


    @ApiOperation(value = "查询操作日志")
    @GetMapping("/page")
    public ListObjServiceResult<OperationLogPageResponse> page(OperationLogPageRequest request) {
        SearchResponse searchResponse = operateLogServiceFactory.search(request);
        SearchHits searchHits = searchResponse.getHits();
        List<OperationLogPageResponse> list = new ArrayList<>(searchHits.getHits().length);
        for (SearchHit hit : searchHits.getHits()) {
            OperationLogPageResponse operationLog = JSON.parseObject(hit.getSourceAsString(), OperationLogPageResponse.class);
            list.add(operationLog);
        }
        return new ListObjServiceResult(list,Integer.valueOf(String.valueOf(searchHits.getTotalHits().value)));
    }


}
