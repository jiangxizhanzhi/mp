package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.request.CsceneInstanceAddFlowTaskRequest;
import cn.cyberpeace.mp.flow.request.CsceneInstanceFlowTaskRequest;
import cn.cyberpeace.mp.flow.response.CsceneInstanceFlowTaskResponse;
import cn.cyberpeace.mp.flow.service.FlowCsceneInstanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@Api(tags = "场景流量")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscene_instance")
public class FlowCsceneInstanceController {

    @Resource
    private FlowCsceneInstanceService flowCsceneInstanceService;

    @ApiOperation("分页查询场景实例中的已配置的流量任务")
    @GetMapping("/task/list")
    public ListObjServiceResult<CsceneInstanceFlowTaskResponse> list(CsceneInstanceFlowTaskRequest request) {
        return flowCsceneInstanceService.list(request).asResult();
    }

    @ApiOperation("新增场景实例中的流量任务")
    @PostMapping("/task/add")
    public ListObjServiceResult<CsceneInstanceFlowTaskResponse> add(@RequestBody CsceneInstanceAddFlowTaskRequest request) {
        return flowCsceneInstanceService.add(request).asResult();
    }
}
