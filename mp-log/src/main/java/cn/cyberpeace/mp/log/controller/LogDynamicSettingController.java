 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogDynamicSettingController.java.
 // File size 2498.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
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
  * 测井动态整定控制器
  *
  * @author wangssh
  * @date 2023/5/30
  */
 @Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/settings")
@Api(tags = "日志级别动态设置")
public class LogDynamicSettingController {

     /**
      * 日志设置服务
      */
     @Autowired
    private ILogSettingService logSettingService;

     /**
      * 设置日志级别
      *
      * @param logName  日志名字
      * @param logLevel 日志级别
      * @return {@link ServiceResult }<{@link LogLevelResponse }>
      */
     @GetMapping("/logLevel/{logName}/{logLevel}")
    @ApiOperation(value = "设置日志级别")
    public ServiceResult<LogLevelResponse> setLogLevel(@PathVariable("logName") String logName, @PathVariable("logLevel") String logLevel) {
        return logSettingService.setLogLevel(logName, logLevel);
    }

     /**
      * 设置批处理日志级别
      *
      * @param logLevelBatch
      * @return {@link ServiceResult }<{@link List }<{@link LogLevelResponse }>>
      */
     @PostMapping("/logLevel/batch")
    @ApiOperation(value = "批量设置日志级别")
    public ServiceResult<List<LogLevelResponse>> setBatchLogLevel(@RequestBody LogLevelBatchRequest logLevelBatch) {

        return logSettingService.setBatchLogLevel(logLevelBatch.getLogLevelList());
    }

     /**
      * 重置默认日志级别
      *
      * @return {@link ServiceResult }<{@link List }<{@link LogLevelResponse }>>
      */
     @GetMapping("/logLevel/reset")
    @ApiOperation(value = "重置日志级别")
    public ServiceResult<List<LogLevelResponse>> resetDefaultLogLevel() {
        return logSettingService.resetDefualtLogLevel();
    }

     /**
      * 获取日志级别列表
      *
      * @param logName 日志名字
      * @return 日志级别信息
      */
     @GetMapping("/logLevel/{logName}")
    @ApiOperation(value = "获取日志级别")
    public ServiceResult<List<LogLevelResponse>> getLogLevelList(@PathVariable String logName) {
        return logSettingService.getLogLevel(logName);
    }

     /**
      * 获取日志级别列表
      *
      * @return 日志级别信息
      */
     @GetMapping("/logLevel")
    @ApiOperation(value = "获取日志级别")
    public ServiceResult<List<LogLevelResponse>> getLogLevelList() {
        return logSettingService.getLogLevel("");
    }

}
