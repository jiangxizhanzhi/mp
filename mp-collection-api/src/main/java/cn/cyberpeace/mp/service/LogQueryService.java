package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.collection.core.es.CollectionClient;
import cn.cyberpeace.mp.collection.model.ContestLogModel;
import cn.cyberpeace.mp.collection.request.LogQueryRequest;
import cn.cyberpeace.mp.collectionapi.config.ContestConfig;
import cn.cyberpeace.mp.collectionapi.request.LogQueryPageRequest;
import cn.cyberpeace.mp.collectionapi.util.ContestConfigUtil;
import cn.cyberpeace.mp.collectionapi.vo.LogVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LogQueryService {
    @Resource
    private CollectionClient collectionClient;

    public ListObjServiceResult<LogVO> queryContestLog(LogQueryPageRequest request) {
        LogQueryRequest logQueryRequest = new LogQueryRequest();
        logQueryRequest.setPageNum(request.getPageNum());
        logQueryRequest.setPageSize(request.getPageSize());
        if (StringUtils.isNotEmpty(request.getStartTime()) && StringUtils.isNotEmpty(request.getEndTime())) {
            logQueryRequest.setStartTime(TimeUtils.stringToLocalDateTime(request.getStartTime()));
            logQueryRequest.setEndTime(TimeUtils.stringToLocalDateTime(request.getEndTime()));
        }
        var equipmentStream = ContestConfig.equipments.stream();
        if (request.getType() == 1) {
            logQueryRequest.setSeverity(ContestLogModel.Severity.ABNORMAL);
            equipmentStream = equipmentStream.filter(equipment -> ContestConfig.BoxType.WHITE.equals(equipment.getBoxType()));
        } else if (request.getType() == 2) {
            logQueryRequest.setSeverity(ContestLogModel.Severity.ABNORMAL);
            equipmentStream = equipmentStream.filter(equipment -> ContestConfig.BoxType.BLACK.equals(equipment.getBoxType()));
        }
        logQueryRequest.setIps(getEquipmentIps(equipmentStream, request));
        if (StringUtils.isNotEmpty(request.getTeam())) {
            var teamIps = ContestConfig.teams.stream()
                    .filter(t -> t.getName().equals(request.getTeam()))
                    .map(t -> t.getIp())
                    .collect(Collectors.toList());
            logQueryRequest.setAttackIps(teamIps);
        }
        var result = collectionClient.queryContestLog(logQueryRequest);
        var ip2Equipment = ContestConfigUtil.getIp2Equipment();
        var ip2Team = ContestConfigUtil.getIp2Team();
        var res = result.getObjs().stream()
                .map(contestLogModel -> {
                    var equipment = ip2Equipment.get(contestLogModel.getIp());
                    var team = ip2Team.get(contestLogModel.getAttackIp());
                    LogVO logVO = new LogVO();
                    logVO.setEquipment(equipment.getName());
                    logVO.setEquipmentType(equipment.getType());
                    logVO.setTeam(team.getName());
                    logVO.setType(contestLogModel.getSeverity());
                    logVO.setTime(contestLogModel.getTime());
                    logVO.setContent(contestLogModel.getMessage());
                    return logVO;
                }).collect(Collectors.toList());
        return new ListObjServiceResult<>(new ListObjResult<>(res, result.getTotal()));
    }

    private List<String> getEquipmentIps(Stream<ContestConfig.Equipment> equipmentStream, LogQueryPageRequest request) {
        List<String> ips = new ArrayList<>();
        if (StringUtils.isNotEmpty(request.getEquipment())) {
            equipmentStream = equipmentStream.filter(equipment -> equipment.getName().equals(request.getEquipment()));
        }
        if (StringUtils.isNotEmpty(request.getEquipmentType())) {
            equipmentStream = equipmentStream.filter(equipment -> equipment.getType().equals(request.getEquipmentType()));
        }
        equipmentStream.forEach(equipment -> ips.addAll(equipment.getIps()));
        return ips;
    }
}
