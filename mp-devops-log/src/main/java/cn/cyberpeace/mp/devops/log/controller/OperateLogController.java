package cn.cyberpeace.mp.devops.log.controller;

import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.log.service.OperateLogServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wanggang
 */
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/operate")
public class OperateLogController {

    @Autowired
    private OperateLogServiceFactory operateLogServiceFactory;

    @OperateLog
    @PostMapping("/add")
    public void create(){
//        operateLogServiceFactory.create();
    }
}
