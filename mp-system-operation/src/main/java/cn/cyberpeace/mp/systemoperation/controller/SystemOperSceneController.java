package cn.cyberpeace.mp.systemoperation.controller;


import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.systemoperation.constant.ControllerConstant;
import cn.cyberpeace.mp.systemoperation.request.EventMonitorRequest;
import cn.cyberpeace.mp.systemoperation.request.FlowMonitorRequest;
import cn.cyberpeace.mp.systemoperation.request.HostMonitorRequest;
import cn.cyberpeace.mp.systemoperation.response.EventMonitorResponse;
import cn.cyberpeace.mp.systemoperation.response.FlowMonitorResponse;
import cn.cyberpeace.mp.systemoperation.response.HostMonitorResponse;
import cn.cyberpeace.mp.systemoperation.response.SceneBaseResponse;
import cn.cyberpeace.mp.systemoperation.service.SystemOperSceneService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

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
    public ServiceResult pageHostMonitor(HostMonitorRequest request) {
        SceneBaseResponse<HostMonitorResponse> response = systemOperSceneService.pageHostMonitor(request);
        if (Objects.nonNull(response.getData()) && Objects.nonNull(response.getLinks())) {
            return ServiceResult.success(response);
        } else {
            return ServiceResult.fail("场景节点监控信息查询异常");
        }
    }

    /**
     * 场景流量监控分页查询
     * @param request
     * @return
     */
    @ApiOperation("场景流量监控分页查询")
    @GetMapping("/pageFlowMonitor")
    public ServiceResult pageFlowMonitor(FlowMonitorRequest request) {
        SceneBaseResponse<FlowMonitorResponse> response = systemOperSceneService.pageFlowMonitor(request);
        if (Objects.nonNull(response.getData()) && Objects.nonNull(response.getLinks())) {
            return ServiceResult.success(response);
        } else {
            return ServiceResult.fail("场景流量监控信息查询异常");
        }
    }

    /**
     * 场景事件监控分页查询
     * @param request
     * @return
     */
    @ApiOperation("场景事件监控分页查询")
    @GetMapping("/pageEventMonitor")
    public ServiceResult pageFlowMonitor(EventMonitorRequest request) {
        SceneBaseResponse<EventMonitorResponse> response = systemOperSceneService.pageEventMonitor(request);
        if (Objects.nonNull(response.getData()) && Objects.nonNull(response.getLinks())) {
            return ServiceResult.success(response);
        } else {
            return ServiceResult.fail("事件流量监控信息查询异常");
        }
    }



}
