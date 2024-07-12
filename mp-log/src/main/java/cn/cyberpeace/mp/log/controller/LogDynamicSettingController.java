 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogDynamicSettingController.java.
 // File size 2906.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
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
 * @ClassName LogDynamicSettingController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/settings")
@Api(tags = "日志级别动态设置")
@AuthTarget(TargetEnum.NO_AUTH)
public class LogDynamicSettingController {

    @Autowired
    private ILogSettingService logSettingService;

    /**
     * setLogLevel
     *
     * @param logName
     * @param logLevel
     * @return
     */
    @GetMapping("/logLevel/{logName}/{logLevel}")
    @ApiOperation(value = "设置日志级别")
    public ServiceResult<LogLevelResponse> setLogLevel(@PathVariable("logName") String logName, @PathVariable("logLevel") String logLevel) {
        return logSettingService.setLogLevel(logName, logLevel);
    }

    /**
     * setBatchLogLevel
     *
     * @param logLevelBatch
     * @return
     */
    @PostMapping("/logLevel/batch")
    @ApiOperation(value = "批量设置日志级别")
    public ServiceResult<List<LogLevelResponse>> setBatchLogLevel(@RequestBody LogLevelBatchRequest logLevelBatch) {

        return logSettingService.setBatchLogLevel(logLevelBatch.getLogLevelList());
    }

    /**
     * resetDefaultLogLevel
     *
     * @return
     */
    @GetMapping("/logLevel/reset")
    @ApiOperation(value = "重置日志级别")
    public ServiceResult<List<LogLevelResponse>> resetDefaultLogLevel() {
        return logSettingService.resetDefualtLogLevel();
    }

    /**
     * 获取日志级别列表
     *
     * @param logName
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
