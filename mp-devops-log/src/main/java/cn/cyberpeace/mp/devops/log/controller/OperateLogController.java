package cn.cyberpeace.mp.devops.log.controller;

import cn.cyberpeace.mp.devops.log.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devops.log.request.OperationLogRequest;
import cn.cyberpeace.mp.devops.log.service.OperateLogServiceFactory;
import cn.cyberpeace.mp.devops.log.utils.SnUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
        model.setOpModule(request.getOpModule());
        model.setOpType(request.getOpType());
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
    @GetMapping("/search")
    public void userlog(OperationLogPageRequest request) {
         operateLogServiceFactory.search(request);
    }


}
