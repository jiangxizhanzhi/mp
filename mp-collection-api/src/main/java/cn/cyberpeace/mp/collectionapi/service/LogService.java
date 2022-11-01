package cn.cyberpeace.mp.collectionapi.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.collection.model.contest.ContestLogModel;
import cn.cyberpeace.mp.collection.model.contest.RuntimeLogModel;
import cn.cyberpeace.mp.collection.request.LogQueryRequest;
import cn.cyberpeace.mp.collection.service.ContestLogService;
import cn.cyberpeace.mp.collectionapi.bo.LogReportBO;
import cn.cyberpeace.mp.collectionapi.bo.RuntimeLogReportBO;
import cn.cyberpeace.mp.collectionapi.request.LogQueryPageRequest;
import cn.cyberpeace.mp.collectionapi.vo.LogVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class LogService {
    @Resource
    private ContestLogService contestLogService;

    public void addContestLog(LogReportBO logReportBO) {
        ContestLogModel contestLogModel = new ContestLogModel();
        contestLogModel.setIp(logReportBO.getIp());
        if (StringUtils.isNotEmpty(logReportBO.getAttackIp()) && logReportBO.getAttackIp().contains(":")) {
            int index = logReportBO.getAttackIp().indexOf(":");
            contestLogModel.setAttackIp(logReportBO.getAttackIp().substring(0, index));
        } else {
            contestLogModel.setAttackIp(logReportBO.getAttackIp());
        }
        contestLogModel.setMessage(logReportBO.getMessage());
        contestLogModel.setSeverity(logReportBO.getSeverity());
        contestLogService.addContestLog(contestLogModel);
    }

    public ListObjServiceResult<LogVO> queryContestLog(LogQueryPageRequest request) {
        LogQueryRequest logQueryRequest = new LogQueryRequest();
        logQueryRequest.setPageNum(request.getPageNum());
        logQueryRequest.setPageSize(request.getPageSize());
        if (StringUtils.isNotEmpty(request.getStartTime())) {
            logQueryRequest.setStartTime(TimeUtils.stringToLocalDateTime(request.getStartTime()));

        }
        if (StringUtils.isNotEmpty(request.getEndTime())) {
            logQueryRequest.setEndTime(TimeUtils.stringToLocalDateTime(request.getEndTime()));
        }
        logQueryRequest.setSeverity(request.getSeverity());
        logQueryRequest.setIps(request.getIps());
        logQueryRequest.setAttackIps(request.getAttackIps());
        var result = contestLogService.queryContestLog(logQueryRequest);
        var res = result.getObjs().stream()
                .map(contestLogModel -> {
                    LogVO logVO = new LogVO();
                    logVO.setSeverity(contestLogModel.getSeverity());
                    logVO.setTime(contestLogModel.getTime());
                    logVO.setContent(contestLogModel.getMessage());
                    logVO.setIp(contestLogModel.getIp());
                    logVO.setAttackIp(contestLogModel.getAttackIp());
                    return logVO;
                }).collect(Collectors.toList());
        return new ListObjServiceResult<>(new ListObjResult<>(res, result.getTotal()));
    }

    public void addRuntimeLog(RuntimeLogReportBO runtimeLogReportBO) {
        RuntimeLogModel runtimeLogModel = new RuntimeLogModel();
        runtimeLogModel.setIp(runtimeLogReportBO.getIp());
        runtimeLogModel.setType(runtimeLogReportBO.getType());
        LocalDateTime time = runtimeLogReportBO.getTime() == null ? LocalDateTime.now() : TimeUtils.stringToLocalDateTime(runtimeLogReportBO.getTime());
        runtimeLogModel.setTime(time);
        runtimeLogModel.setMessage(runtimeLogReportBO.getMessage());
        runtimeLogModel.setLogType(runtimeLogReportBO.getLogType());
        contestLogService.addRuntimeLog(runtimeLogModel);
    }
}
