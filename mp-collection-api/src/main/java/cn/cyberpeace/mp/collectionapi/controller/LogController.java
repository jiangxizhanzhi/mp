package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collectionapi.bo.LogReportBO;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowBaseRequest;
import cn.cyberpeace.mp.collectionapi.request.LogQueryPageRequest;
import cn.cyberpeace.mp.collectionapi.vo.CountVO;
import cn.cyberpeace.mp.collectionapi.vo.LogVO;
import cn.cyberpeace.mp.collectionapi.service.LogService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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

    @GetMapping("/query_disturbance_count")
    @ApiOperation(value = "查询扰动次数")
    @ApiImplicitParam(name = "attacks", value = "请求体", dataTypeClass = AttackFlowBaseRequest.IPField.class)
    public ServiceResult<List<CountVO>> countByIp(@RequestBody List<AttackFlowBaseRequest.IPField> attacks) {
        return logQueryService.countContestLogBySeverity(attacks).asResult();
    }

    @PostMapping("/report")
    @ApiOperation(value = "日志上报")
    @ApiImplicitParam(name = "logReportBO", value = "请求体", dataTypeClass = LogReportBO.class)
    public void report(@RequestBody LogReportBO logReportBO) {
        logQueryService.addContestLog(logReportBO);
    }
}
