 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogFileReportController.java.
 // File size 1710.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.constant.ControllerConstant;
import cn.cyberpeace.mp.log.request.LogReportRequest;
import cn.cyberpeace.mp.log.service.LogReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName LogFileReportController
 * @Description 日志上报接口
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/report")
@Api(value = "日志模块", tags = "日志模块")
@RequiredArgsConstructor
@AuthTarget(TargetEnum.NO_AUTH)
public class LogFileReportController {

    private final LogReportService logReportService;

    /**
     * report
     *
     * @param request
     * @return
     */
    @PostMapping
    @ApiOperation(value = "上报日志", notes = "")
    @ApiImplicitParam(name = "request", value = "日志上报请求", dataTypeClass = LogReportRequest.class)
    public ServiceResult<Integer> report(@RequestBody LogReportRequest request) {
        return new ServiceResult(logReportService.report(request.getLogReportTypeEnum(),request.getList())).asResult();
    }
}
