package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.request.BatchDeleteFlowTaskRequest;
import cn.cyberpeace.mp.flow.service.FlowTaskService;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "流量任务管理接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/task")
public class FlowTaskController extends BaseModelController<FlowTaskModel, QueryParams, FlowTaskService> {

    @Autowired
    public FlowTaskController(FlowTaskService flowTaskService) {
        setModelService(flowTaskService);
    }

    @Autowired
    private FlowTaskServiceImpl flowTaskService;


    /**
     * 新增流量任务
     *
     * @param model
     * @return
     */
    @ApiOperation("新增流量任务")
    @PostMapping("/add")
    public ServiceResult<FlowTaskModel> addFlowTask(@RequestBody @Validated FlowTaskModel model) {
        return flowTaskService.addFlowTask(model);
    }

    /**
     * 更新流量任务
     *
     * @param flowTaskModel
     * @return
     */
    @ApiOperation("更新流量任务")
    @PatchMapping("/update")
    public ServiceResult<FlowTaskModel> updateFlowTask(@RequestBody @Validated FlowTaskModel flowTaskModel) {
        return flowTaskService.updateFlowTask(flowTaskModel);
    }

    /**
     * 删除流量任务
     * @param id
     * @return
     */
    @ApiOperation("删除流量任务")
    @DeleteMapping("/delete/{id}")
    public ServiceResult<Boolean> deleteFlowTask(@PathVariable String id) {
        return flowTaskService.deleteFlowTask(id);
    }

    /**
     * 批量删除流量任务
     * @param request
     * @return
     */
    @ApiOperation("批量删除流量任务")
    @DeleteMapping("/batch_delete")
    public ServiceResult<Integer> batchDeleteFlowTask(@Validated BatchDeleteFlowTaskRequest request) {
        return flowTaskService.batchDeleteFlowTask(request);
    }

    @ApiOperation("执行流量任务")
    @PostMapping("/actuator/{id}")
    public ServiceResult actuator(@PathVariable String id) {
        return flowTaskService.actuator(id);
    }

}
