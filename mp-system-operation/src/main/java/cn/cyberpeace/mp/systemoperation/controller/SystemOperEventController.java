package cn.cyberpeace.mp.systemoperation.controller;


import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.datasandtable.model.*;
import cn.cyberpeace.mp.datasandtable.service.WazuhService;
import cn.cyberpeace.mp.systemoperation.constant.ControllerConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@Api(tags = "事件监控接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/event")
public class SystemOperEventController {

    @Autowired
    private WazuhService wazuhService;

    /**
     * 分页查询文件事件
     * @param wazuhFileEventRequest
     * @return
     */
    @ApiOperation("文件事件分页查询")
    @PostMapping("pageFileEvent")
    public ServiceResult<WazuhFileEventResponse> queryFileEvents(@RequestBody @Valid WazuhEventRequest wazuhFileEventRequest) {
        ListObjResult<FileEventModel> listObjResult = wazuhService.queryFileEvents(wazuhFileEventRequest);
        WazuhFileEventResponse response = WazuhFileEventResponse.builder()
                .fileEventModelList(listObjResult.getObjs())
                .total(listObjResult.getTotal())
                .build();

        return ServiceResult.success(response);
    }

    /**
     * 分页查询监控指标
     * @param wazuhMonitorEventRequest
     * @return
     */
    @ApiOperation("监控指标分页查询")
    @PostMapping("pageMonitorEvent")
    public ServiceResult<WazuhMonitorEventResponse> queryMonitorEvents(@RequestBody @Valid WazuhEventRequest wazuhMonitorEventRequest) {
        ListObjResult<WazuhModel> listObjResult = wazuhService.queryMonitorEvents(wazuhMonitorEventRequest);
        WazuhMonitorEventResponse response = WazuhMonitorEventResponse.builder()
                .monitorEventModelList(listObjResult.getObjs())
                .total(listObjResult.getTotal())
                .build();

        return ServiceResult.success(response);
    }


}
