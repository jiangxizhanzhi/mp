package cn.cyberpeace.mp.devops.log.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.log.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devops.log.request.OperationLogRequest;
import cn.cyberpeace.mp.devops.log.service.OperateLogServiceFactory;
import cn.cyberpeace.mp.devops.log.utils.SnUtils;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Object add(@RequestBody OperationLogRequest request, HttpServletRequest httpServletRequest){
        OperationLogModel model = new OperationLogModel();
        model.setFrom(request.getFrom());
        model.setLogType(request.getLogType());
        model.setOpModule(OpModuleEnums.fromValue(request.getOpModule()).getDesc());
        model.setOpType(OpModuleEnums.fromValue(request.getOpTypeEnums()).getDesc());
        model.setOpTarget(request.getOpTarget());
        model.setTitle(request.getTitle());
        model.setUserId(request.getUserId());
        model.setUserName(request.getUserName());
        model.setUserType(request.getUserType());
        model.setDescription(request.getDescription());
        model.setOpIp(SnUtils.getRealRemoteIp(httpServletRequest));
        return operateLogServiceFactory.create(model);
    }


    @ApiOperation(value = "查询操作日志")
    @GetMapping("/page")
    public ListObjServiceResult<OperationLogModel> page(OperationLogPageRequest request) {
        SearchResponse searchResponse = operateLogServiceFactory.search(request);
        SearchHits searchHits = searchResponse.getHits();
        List<OperationLogModel> list = new ArrayList<>(searchHits.getHits().length);
        for (SearchHit hit : searchHits.getHits()) {
            OperationLogModel operationLog = JSON.parseObject(hit.getSourceAsString(), OperationLogModel.class);
            list.add(operationLog);
        }
        return new ListObjServiceResult(list,Integer.valueOf(String.valueOf(searchHits.getTotalHits().value)));
    }


}
