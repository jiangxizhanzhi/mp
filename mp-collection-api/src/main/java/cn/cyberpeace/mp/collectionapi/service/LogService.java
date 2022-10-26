package cn.cyberpeace.mp.collectionapi.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.collection.model.contest.ContestLogModel;
import cn.cyberpeace.mp.collection.request.LogQueryRequest;
import cn.cyberpeace.mp.collection.service.ContestLogService;
import cn.cyberpeace.mp.collectionapi.bo.LogReportBO;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowBaseRequest;
import cn.cyberpeace.mp.collectionapi.request.LogQueryPageRequest;
import cn.cyberpeace.mp.collectionapi.vo.CountVO;
import cn.cyberpeace.mp.collectionapi.vo.LogVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        if (StringUtils.isNotEmpty(request.getStartTime()) && StringUtils.isNotEmpty(request.getEndTime())) {
            logQueryRequest.setStartTime(TimeUtils.stringToLocalDateTime(request.getStartTime()));
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

    public ServiceResult<List<CountVO>> countContestLogBySeverity(List<AttackFlowBaseRequest.IPField> attacks) {
        Map<String, CountVO> name2Count = new HashMap<>();
        attacks.forEach(ipField -> {
            CountVO countVO = new CountVO();
            countVO.setName(ipField.getName());
            countVO.setCount(0);
            name2Count.put(ipField.getName(), countVO);
        });
        Map<String, String> ip2name = new HashMap<>();
        Map<String, String> cidr2Name = new HashMap<>();
        attacks.forEach(ipField -> {
            if (CollectionUtils.isNotEmpty(ipField.getIps())) {
                ipField.getIps().forEach(ip -> ip2name.put(ip, ipField.getName()));
            } else if (StringUtils.isNotEmpty(ipField.getCidr())) {
                cidr2Name.put(ipField.getCidr().substring(0, ipField.getCidr().lastIndexOf(".") + 1), ipField.getName());
            }
        });
        contestLogService.countContestLogBySeverity().forEach(countBO -> {
            if (!ip2name.isEmpty()) {
                var countVO = name2Count.get(ip2name.get(countBO.getKey()));
                countVO.setCount(countVO.getCount() + countBO.getCount());
            } else if (!cidr2Name.isEmpty()) {
                String ip = countBO.getKey().substring(0, countBO.getKey().lastIndexOf(".") + 1);
                var countVO = name2Count.get(cidr2Name.get(ip));
                countVO.setCount(countVO.getCount() + countBO.getCount());
            }
        });
        return new ServiceResult<>(new ArrayList<>(name2Count.values()));
    }
}
