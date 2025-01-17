 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SystemStatusInfoController.java.
 // File size 1505.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.controller;

import cn.cyberpeace.mp.base.service.ISystemStatusInfo;
import cn.cyberpeace.mp.base.service.code.ServiceCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.thread.taskpool.ThreadPoolInfo;
import cn.cyberpeace.mp.log.constant.ControllerConstant;
import cn.cyberpeace.mp.log.request.LogLevelBatchRequest;
import cn.cyberpeace.mp.log.response.LogLevelResponse;
import cn.cyberpeace.mp.log.service.ILogSettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

 /**
  * 系统状态信息控制器
  *
  * @author wangssh
  * @date 2023/6/29
  */
 @Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/system")
@Api(tags = "系统运行状态信息")
public class SystemStatusInfoController {

     /**
      *
      */
     @Autowired
    private ISystemStatusInfo systemStatusInfo;

     /**
      * 获取日志级别列表
      *
      * @return 日志级别信息
      */
     @GetMapping("/threadpool/info")
    @ApiOperation(value = "获取线程池状态信息")
    public ServiceResult getLogLevelList() {
        return new ServiceResult<>(systemStatusInfo.getThreadPoolInfos()).asResult();
    }

}
