package cn.cyberpeace.mp.devops.log.controller;

import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.service.OperateLogServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户操作日志记录
 * @author wanggang
 */
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/operate")
public class OperateLogController {

    private OperateLogServiceFactory operateLogServiceFactory;

    @Autowired
    public OperateLogController(OperateLogServiceFactory operateLogServiceFactory) {
        this.operateLogServiceFactory = operateLogServiceFactory;
    }

    @OperateLog
    @PostMapping("/add")
    public Object create(@RequestBody OperationLogModel operationLogModel){
       return operateLogServiceFactory.create(operationLogModel);
    }


}
