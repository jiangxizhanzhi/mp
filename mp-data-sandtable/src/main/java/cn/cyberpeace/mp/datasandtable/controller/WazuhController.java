package cn.cyberpeace.mp.datasandtable.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.datasandtable.model.FileEventModel;
import cn.cyberpeace.mp.datasandtable.model.WazuhEventRequest;
import cn.cyberpeace.mp.datasandtable.model.WazuhModel;
import cn.cyberpeace.mp.datasandtable.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.datasandtable.service.WazuhService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/wazuh")
@Api(value = "数据沙盘-场景监控事件查询接口", tags = "数据沙盘-场景监控事件查询接口")
public class WazuhController {

    @Autowired
    private WazuhService wazuhService;

    @PostMapping("/file_events")
    @ApiOperation(value = "查询文件注册表变更信息", notes = "")
    public ListObjServiceResult<FileEventModel> queryFileEvents(@RequestBody @Valid WazuhEventRequest wazuhEventRequest) {
        return new ListObjServiceResult<>(wazuhService.queryFileEvents(wazuhEventRequest)).asResult();
    }

    @PostMapping("/monitor_events")
    @ApiOperation(value = "查询场景系统指标信息", notes = "")
    public ListObjServiceResult<WazuhModel> queryMonitorEvents(@RequestBody @Valid WazuhMonitorEventsRequest wazuhMonitorEventsRequest) {
        return new ListObjServiceResult<>(wazuhService.queryMonitorEvents(wazuhMonitorEventsRequest)).asResult();
    }

//    @PostMapping("/monitor_agent_mac")
//    @ApiOperation(value = "查询场景agent和mac信息", notes = "")
//    public ServiceResult<MultiValueMap<String, String>> queryAllAgentMacAddr() {
//        return ServiceResult.success((wazuhService.queryAllAgentMacAddr()));
//    }
}
