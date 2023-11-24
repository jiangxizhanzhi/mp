package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.model.log.NetworkAssessmentLogPo;
import cn.cyberpeace.mp.collection.service.NetworkAssessmentLogService;
import cn.cyberpeace.mp.collectionapi.bo.NetworkAssessmentLogDto;
import cn.cyberpeace.mp.collectionapi.request.NetworkLogQuery;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
            log.info("network log: {}", JsonCommon.stringify(networkAssessmentLogDto));
        }

        NetworkAssessmentLogPo networkAssessmentLogPo = NetworkAssessmentLogDto.convertToPo(networkAssessmentLogDto);
        networkAssessmentLogService.save(networkAssessmentLogPo);
        return ServiceResult.success(true);
    }

    @PostMapping("/list")
    public ServiceResult<Page<NetworkAssessmentLogPo>> listByTime(@RequestBody @NonNull NetworkLogQuery networkLogQuery) {
        if (FlowController.debugLogFlag) {
            log.info("listByTime networkLogQuery = {}", networkLogQuery);
        }
        Integer page = Optional.of(networkLogQuery).map(NetworkLogQuery::getPage).orElse(0);
        Integer pageSize = Optional.of(networkLogQuery).map(NetworkLogQuery::getPageSize).orElse(10);

        Sort.TypedSort<NetworkAssessmentLogPo> typedSort = Sort.sort(NetworkAssessmentLogPo.class);
        Sort sort = typedSort.by(NetworkAssessmentLogPo::getReportTime).descending();
        PageRequest pageRequest = PageRequest.of(page, pageSize, sort);
        return ServiceResult.success(networkAssessmentLogService.page(pageRequest));
    }

}
