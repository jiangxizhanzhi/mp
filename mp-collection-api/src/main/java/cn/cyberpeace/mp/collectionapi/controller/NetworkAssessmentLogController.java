package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.model.log.NetworkAssessmentLogPo;
import cn.cyberpeace.mp.collection.service.NetworkAssessmentLogService;
import cn.cyberpeace.mp.collectionapi.bo.NetworkAssessmentLogDto;
import cn.cyberpeace.mp.collectionapi.request.NetworkLogQuery;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
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
public class NetworkAssessmentLogController {

    private NetworkAssessmentLogService networkAssessmentLogService;

    @PostMapping("")
    @ApiOperation(value = "上报单条设备日志")
    public ServiceResult<Boolean> reportNetworkAssessmentLog(@RequestBody @NonNull NetworkAssessmentLogDto networkAssessmentLogDto) {

        log.info("network log: {}", JsonCommon.stringify(networkAssessmentLogDto));

        NetworkAssessmentLogPo networkAssessmentLogPo = NetworkAssessmentLogDto.convertToPo(networkAssessmentLogDto);
        networkAssessmentLogService.save(networkAssessmentLogPo);
        return ServiceResult.success(true).asResult();
    }

    @PostMapping("/list")
    public ServiceResult<List<NetworkAssessmentLogDto>> listByTime(@RequestBody @NonNull NetworkLogQuery networkLogQuery) {
        LocalDateTime startTime = networkLogQuery.getStartTime();
        LocalDateTime endTime = networkLogQuery.getEndTime();
        List<NetworkAssessmentLogPo> networkAssessmentLogPoList = networkAssessmentLogService.listLogByTime(startTime, endTime);
        if (CollectionUtils.isEmpty(networkAssessmentLogPoList)) {
            return ServiceResult.success(Collections.emptyList()).asResult();
        } else {
            return ServiceResult.success(networkAssessmentLogPoList.stream().map(NetworkAssessmentLogDto::getFromPo)
                    .collect(Collectors.toList())).asResult();
        }
    }

}
