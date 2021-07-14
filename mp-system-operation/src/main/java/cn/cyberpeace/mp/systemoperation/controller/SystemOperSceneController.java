package cn.cyberpeace.mp.systemoperation.controller;


import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.systemoperation.constant.ControllerConstant;
import cn.cyberpeace.mp.systemoperation.request.EventMonitorRequest;
import cn.cyberpeace.mp.systemoperation.request.FlowMonitorRequest;
import cn.cyberpeace.mp.systemoperation.request.HostMonitorRequest;
import cn.cyberpeace.mp.systemoperation.response.EventMonitorResponse;
import cn.cyberpeace.mp.systemoperation.response.FlowMonitorResponse;
import cn.cyberpeace.mp.systemoperation.response.HostMonitorResponse;
import cn.cyberpeace.mp.systemoperation.service.SystemOperSceneService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@Api(tags = "场景管理接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/scene")
public class SystemOperSceneController {

    @Autowired
    private SystemOperSceneService systemOperSceneService;

    /**
     * 场景节点监控分页查询
     * @param request
     * @return
     */
    @ApiOperation("场景节点监控分页查询")
    @GetMapping("/pageHostMonitor")
    public ServiceResult<HostMonitorResponse> pageHostMonitor(@Valid HostMonitorRequest request) {
        return systemOperSceneService.pageHostMonitor(request);
    }

    /**
     * 场景流量监控分页查询
     * @param request
     * @return
     */
    @ApiOperation("场景流量监控分页查询")
    @GetMapping("/pageFlowMonitor")
    public ServiceResult<FlowMonitorResponse> pageFlowMonitor(@Valid FlowMonitorRequest request) {
        return systemOperSceneService.pageFlowMonitor(request);
    }

    /**
     * 场景事件监控分页查询
     * @param request
     * @return
     */
    @ApiOperation("场景事件监控分页查询")
    @GetMapping("/pageEventMonitor")
    public ServiceResult<EventMonitorResponse> pageFlowMonitor(@Valid EventMonitorRequest request) {
        return systemOperSceneService.pageEventMonitor(request);
    }



}
