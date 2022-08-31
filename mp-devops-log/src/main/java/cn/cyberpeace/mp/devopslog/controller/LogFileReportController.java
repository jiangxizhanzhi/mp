package cn.cyberpeace.mp.devopslog.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopslog.constant.ControllerConstant;
import cn.cyberpeace.mp.devopslog.request.LogReportRequest;
import cn.cyberpeace.mp.devopslog.service.LogReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志上报接口
 **/
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/report")
@Api(value = "日志模块", tags = "日志模块")
@RequiredArgsConstructor
public class LogFileReportController {

    private final LogReportService logReportService;

    @PostMapping
    @ApiOperation(value = "上报日志", notes = "")
    @ApiImplicitParam(name = "request", value = "日志上报请求", dataTypeClass = LogReportRequest.class)
    public ServiceResult<Integer> report(@RequestBody LogReportRequest request) {
        return new ServiceResult(logReportService.report(request.getLogReportTypeEnum(),request.getList())).asResult();
    }
}
