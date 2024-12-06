package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.request.CsceneConfigFlowTaskRequest;
import cn.cyberpeace.mp.flow.request.CsceneFlowTaskRequest;
import cn.cyberpeace.mp.flow.response.CsceneFlowTaskResponse;
import cn.cyberpeace.mp.flow.service.FlowCsceneService;
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
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscene")
public class FlowCsceneController {

    @Resource
    private FlowCsceneService flowCsceneService;

    @ApiOperation("分页查询场景模板中的已配置的流量任务")
    @GetMapping("/task/list")
    public ListObjServiceResult<CsceneFlowTaskResponse> list(CsceneConfigFlowTaskRequest request) {
        return flowCsceneService.list(request).asResult();
    }

    @ApiOperation("新增场景模板中的流量任务")
    @PostMapping("/task/add")
    public ListObjServiceResult<CsceneFlowTaskResponse> add(@RequestBody CsceneFlowTaskRequest request) {
        return flowCsceneService.add(request).asResult();
    }
}
