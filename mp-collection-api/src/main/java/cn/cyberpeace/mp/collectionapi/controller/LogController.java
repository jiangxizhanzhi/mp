package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.request.IndexQueryRequest;
import cn.cyberpeace.mp.collectionapi.bo.LogReportBO;
import cn.cyberpeace.mp.collectionapi.bo.RuntimeLogReportBO;
import cn.cyberpeace.mp.collectionapi.request.LogQueryPageRequest;
import cn.cyberpeace.mp.collectionapi.service.LogService;
import cn.cyberpeace.mp.collectionapi.vo.LogVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/log")
public class LogController {
    @Resource
    private LogService logQueryService;

    @GetMapping("/list")
    @ApiOperation(value = "查询日志列表")
    public ListObjServiceResult<LogVO> list(LogQueryPageRequest request, @RequestBody LogQueryPageRequest bodyParams) {
        request.setAttackIps(bodyParams.getAttackIps());
        request.setIps(bodyParams.getIps());
        return logQueryService.queryContestLog(request).asResult();
    }

    @PostMapping("/report")
    @ApiOperation(value = "日志上报")
    public ServiceResult<Boolean> report(@RequestBody LogReportBO logReportBO) {
        log.info("attack log: {}", JsonCommon.stringify(logReportBO));
        logQueryService.addContestLog(logReportBO);
        return new ServiceResult<>(true).asResult();
    }

    @PostMapping("/report_run")
    @ApiOperation(value = "内部日志上报")
    public ServiceResult<Boolean> report(@RequestBody RuntimeLogReportBO runtimeLogReportBO) {
        log.info("device log: {}", JsonCommon.stringify(runtimeLogReportBO));
        logQueryService.addRuntimeLog(runtimeLogReportBO);
        return new ServiceResult<>(true).asResult();
    }

    @GetMapping("/query_by_index")
    @ApiOperation(value = "分页查询指定索引")
    public ListObjServiceResult<Map> queryByIndex(IndexQueryRequest request) {
        return logQueryService.queryByIndex(request).asResult();
    }
}
