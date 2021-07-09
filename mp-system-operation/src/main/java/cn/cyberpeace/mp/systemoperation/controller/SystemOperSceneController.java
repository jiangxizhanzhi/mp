package cn.cyberpeace.mp.systemoperation.controller;


import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.systemoperation.constant.ControllerConstant;
import cn.cyberpeace.mp.systemoperation.request.HostMonitorRequest;
import cn.cyberpeace.mp.systemoperation.response.HostMonitorResponse;
import cn.cyberpeace.mp.systemoperation.response.SceneBaseResponse;
import cn.cyberpeace.mp.systemoperation.service.SystemOperSceneService;
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
    @PostMapping("/pageHostMonitor")
    public ServiceResult pageHostMonitor(@RequestBody @Valid HostMonitorRequest request) {
        SceneBaseResponse<HostMonitorResponse> response = systemOperSceneService.pageHostMonitor(request);

        return ServiceResult.success(response);
    }



}
