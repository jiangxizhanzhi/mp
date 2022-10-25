package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.collectionapi.bo.LogReportBO;
import cn.cyberpeace.mp.collectionapi.request.LogQueryPageRequest;
import cn.cyberpeace.mp.collectionapi.vo.LogVO;
import cn.cyberpeace.mp.collectionapi.service.LogService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/log")
public class LogController {
    @Resource
    private LogService logQueryService;

    @GetMapping("/list")
    @ApiOperation(value = "查询日志列表")
    public ListObjServiceResult<LogVO> list(LogQueryPageRequest request) {
        return logQueryService.queryContestLog(request).asResult();
    }

    @PostMapping("/report")
    @ApiOperation(value = "日志上报")
    public void report(@RequestBody LogReportBO logReportBO) {
        logQueryService.addContestLog(logReportBO);
    }
}
