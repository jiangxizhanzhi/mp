package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.request.AddFlowTaskRequest;
import cn.cyberpeace.mp.flow.request.BatchActuatorTaskRequest;
import cn.cyberpeace.mp.flow.request.BatchAddFlowTaskRequest;
import cn.cyberpeace.mp.flow.request.BatchDeleteFlowTaskRequest;
import cn.cyberpeace.mp.flow.response.FlowRecordsResponse;
import cn.cyberpeace.mp.flow.service.FlowTaskService;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 流量任务控制器
 * @Author qiaokang
 * @Date：2021/8/31 17:17
 */
@Slf4j
@Api(tags = "流量任务管理接口")
//@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/task")
public class FlowTaskController extends BaseModelController<FlowTaskModel, QueryParams, FlowTaskService> {

    @Autowired
    private FlowTaskServiceImpl flowTaskService;

    @Autowired
    public FlowTaskController(FlowTaskService flowTaskService) {
        setModelService(flowTaskService);
    }

    /**
     * 查询任务详情
     * @param id
     * @param fields
     * @return
     */
    @ApiOperation("查询任务详情")
    @GetMapping("/detail/{id}")
    public ServiceResult<FlowTaskModel> getDetail(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        return flowTaskService.getDetail(id, fields);
    }


    /**
     * 分页查询
     *
     * @param queryParams
     * @return
     */
    @ApiOperation("分页查询任务")
    @GetMapping("/page")
    public ListObjResult<FlowTaskModel> pageFlowTask(@Validated QueryParams queryParams) {
        if (Objects.isNull(queryParams.getOrders()) && Objects.isNull(queryParams.getSort())) {
            // 默认按时间降序
            queryParams.setOrders(new String[]{"-createTime"});
        }
        handleQueryParams(BaseConstants.STR_LIST, queryParams);
        return flowTaskService.pageFlowTask(queryParams);
    }

    /**
     * 新增流量任务
     *
     * @param request
     * @return
     */
    @ApiOperation("新增流量任务")
    @PostMapping("/add")
    public ServiceResult<FlowTaskModel> addFlowTask(@RequestBody @Validated AddFlowTaskRequest request) {
        return flowTaskService.addFlowTask(request);
    }

    /**
     * 更新流量任务
     *
     * @param request
     * @return
     */
    @ApiOperation("更新流量任务")
    @PatchMapping("/update")
    public ServiceResult<FlowTaskModel> updateFlowTask(@RequestBody @Validated AddFlowTaskRequest request) {
        return flowTaskService.updateFlowTask(request);
    }

    /**
     * 删除流量任务
     *
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
     *
     * @param request
     * @return
     */
    @ApiOperation("批量删除流量任务")
    @DeleteMapping("/batch_delete")
    public ServiceResult<Integer> batchDeleteFlowTask(@Validated BatchDeleteFlowTaskRequest request) {
        return flowTaskService.batchDeleteFlowTask(request);
    }

    /**
     * 执行流量任务
     *
     * @param id
     * @param taskAction 1 start， 2 stop
     * @return
     */
    @ApiOperation("执行流量任务")
    @PostMapping("/actuator/{id}/{taskAction}")
    public ServiceResult<Boolean> actuator(@PathVariable String id, @PathVariable int taskAction) {
        return flowTaskService.actuator(id, taskAction, null, null);
    }

    /**
     * 查询任务流量记录信息
     * @param taskId
     * @return
     */
    @ApiOperation("查询任务流量记录信息")
    @GetMapping("/flow/records")
    public ServiceResult<FlowRecordsResponse> queryFlowRecords(@RequestParam String taskId) {
        return flowTaskService.queryFlowRecords(taskId);
    }

    /**
     * 批量新增流量任务
     * @param request
     * @return
     */
    @ApiOperation("批量新增流量任务")
    @PostMapping("/batch/add")
    public ServiceResult<List<FlowTaskModel>> batchAddFlowTask(@RequestBody @Validated BatchAddFlowTaskRequest request) {
        return ServiceResult.success(flowTaskService.batchSavePlatFlowTask(request.getAddTaskRequestList()));
    }

    /**
     * 批量执行流量任务
     * @param request
     * @return
     */
    @ApiOperation("批量执行流量任务")
    @PostMapping("/batch/actuator")
    public ServiceResult<Boolean> batchActuator(@RequestBody @Validated BatchActuatorTaskRequest request) {
        return flowTaskService.batchActuator(request);
    }

}
