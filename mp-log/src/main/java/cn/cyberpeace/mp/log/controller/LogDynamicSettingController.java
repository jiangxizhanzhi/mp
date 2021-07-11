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


@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/settings")
@Api(tags = "日志级别动态设置")
public class LogDynamicSettingController {

    @Autowired
    private ILogSettingService logSettingService;

    @GetMapping("/logLevel/{logName}/{logLevel}")
    @ApiOperation(value = "设置日志级别")
    public ServiceResult<LogLevelResponse> setLogLevel(@PathVariable("logName") String logName, @PathVariable("logLevel") String logLevel) {
        return logSettingService.setLogLevel(logName, logLevel);
    }

    @PostMapping("/logLevel/batch")
    @ApiOperation(value = "批量设置日志级别")
    public ServiceResult<List<LogLevelResponse>> setBatchLogLevel(@RequestBody LogLevelBatchRequest logLevelBatch) {

        return logSettingService.setBatchLogLevel(logLevelBatch.getLogLevelList());
    }

    @GetMapping("/logLevel/reset")
    @ApiOperation(value = "重置日志级别")
    public ServiceResult<List<LogLevelResponse>> resetDefaultLogLevel() {
        return logSettingService.resetDefualtLogLevel();
    }

    
    @GetMapping("/logLevel/{logName}")
    @ApiOperation(value = "获取日志级别")
    public ServiceResult<List<LogLevelResponse>> getLogLevelList(@PathVariable String logName) {
        return logSettingService.getLogLevel(logName);
    }

    
    @GetMapping("/logLevel")
    @ApiOperation(value = "获取日志级别")
    public ServiceResult<List<LogLevelResponse>> getLogLevelList() {
        return logSettingService.getLogLevel("");
    }

}
