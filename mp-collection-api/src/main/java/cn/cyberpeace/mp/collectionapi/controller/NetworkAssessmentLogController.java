package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.model.log.NetworkAssessmentLogPo;
import cn.cyberpeace.mp.collection.service.NetworkAssessmentLogService;
import cn.cyberpeace.mp.collectionapi.bo.NetworkAssessmentLogDto;
import cn.cyberpeace.mp.collectionapi.bo.NetworkAssessmentLogScreenDto;
import cn.cyberpeace.mp.collectionapi.request.NetworkLogQuery;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName NetworkAssessmentLogController
 * @Description
 * @Author zhanzhi
 * @Date 2023/11/16 18:00
 **/
@Slf4j
@RestController
@RequestMapping("/log/network")
@RequiredArgsConstructor
public class NetworkAssessmentLogController {

    private final NetworkAssessmentLogService networkAssessmentLogService;

    @PostMapping("")
    @ApiOperation(value = "上报单条设备日志")
    public ServiceResult<Boolean> reportNetworkAssessmentLog(@RequestBody @NonNull NetworkAssessmentLogDto networkAssessmentLogDto) {
        if (FlowController.debugLogFlag) {
            log.debug("network log: {}", JsonCommon.stringify(networkAssessmentLogDto));
        }

        NetworkAssessmentLogPo networkAssessmentLogPo = NetworkAssessmentLogDto.convertToPo(networkAssessmentLogDto);
        networkAssessmentLogService.save(networkAssessmentLogPo);
        return ServiceResult.success(true);
    }

    @PostMapping("/list")
    public ServiceResult<List<NetworkAssessmentLogScreenDto>> listByTime(@RequestBody @NonNull NetworkLogQuery networkLogQuery) {
        if (FlowController.debugLogFlag) {
            log.debug("listByTime networkLogQuery = {}", networkLogQuery);
        }
        LocalDateTime startTime = networkLogQuery.getStartTime();
        LocalDateTime endTime = networkLogQuery.getEndTime();
        List<NetworkAssessmentLogPo> networkAssessmentLogPoList = networkAssessmentLogService.listLogByTime(startTime, endTime);
        if (CollectionUtils.isEmpty(networkAssessmentLogPoList)) {
            return ServiceResult.success(new ArrayList<>());
        } else {
            return ServiceResult.success(networkAssessmentLogPoList.stream().map(NetworkAssessmentLogScreenDto::getFromPo)
                    .collect(Collectors.toList()));
        }
    }

}
