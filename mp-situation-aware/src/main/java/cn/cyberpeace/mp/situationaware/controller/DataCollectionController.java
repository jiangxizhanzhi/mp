package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.model.FileEventModel;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhHostStateModel;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Objects;

@RestController
@RequestMapping("/wazuh")
@Api(value = "态势感知-场景监控事件查询接口", tags = "态势感知-场景监控事件查询接口")
public class DataCollectionController {

    @Autowired
    private DataCollectionService dataCollectionService;

    @PostMapping("/file_events")
    @ApiOperation(value = "查询文件注册表变更信息", notes = "")
    public ListObjServiceResult<FileEventModel> queryFileEvents(@RequestBody @Valid WazuhEventRequest wazuhEventRequest) {
        if (Objects.isNull(wazuhEventRequest.getStartTime()) || Objects.isNull(wazuhEventRequest.getEndTime())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        if (CollectionUtils.isEmpty(wazuhEventRequest.getMacIds()) && CollectionUtils.isEmpty(wazuhEventRequest.getAgentNames())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        wazuhEventRequest.setRuleGroups(Arrays.asList("syscheck"));
        return new ListObjServiceResult<>(dataCollectionService.queryFileEvents(wazuhEventRequest)).asResult();
    }

    @PostMapping("/monitor_events")
    @ApiOperation(value = "查询场景系统指标信息", notes = "")
    public ListObjServiceResult<WazuhHostStateModel> queryMonitorEvents(@RequestBody @Valid WazuhMonitorEventsRequest wazuhMonitorEventsRequest) {
        if (CollectionUtils.isEmpty(wazuhMonitorEventsRequest.getMacIds()) && CollectionUtils.isEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        return new ListObjServiceResult<>(dataCollectionService.queryMonitorEvents(wazuhMonitorEventsRequest)).asResult();
    }

    @PostMapping("/monitor_agent_mac")
    @ApiOperation(value = "查询场景agent和mac信息", notes = "")
    public ServiceResult<MultiValueMap<String, String>> queryAllAgentMacAddr() {
        return ServiceResult.success((dataCollectionService.queryAllAgentMacAddr()));
    }
}
