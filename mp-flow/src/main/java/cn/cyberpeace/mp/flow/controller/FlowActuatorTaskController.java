package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskParams;
import cn.cyberpeace.mp.flow.bo.EditFlowTaskConfRequest;
import cn.cyberpeace.mp.flow.bo.FlowTaskBatchStartRequest;
import cn.cyberpeace.mp.flow.bo.QueryFlowTaskConfResponse;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.request.queryparam.FlowTaskPageQueryParams;
import cn.cyberpeace.mp.flow.response.FlowTaskPageResponse;
import cn.cyberpeace.mp.flow.service.FlowActuatorTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Api(tags = "流量任务管理")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/actuator")
@Data
public class FlowActuatorTaskController {
    
    @Resource
    private FlowActuatorTaskService flowActuatorTaskService;

    @ApiOperation("流量任务列表查询")
    @GetMapping("/task/list")
    public ListObjServiceResult<FlowTaskPageResponse> taskList(FlowTaskPageQueryParams request) {
        return flowActuatorTaskService.taskList(request).asResult();
    }

    @ApiOperation("新增流量任务")
    @PostMapping("/task/add")
    public ServiceResult addTasks(@RequestBody AddFlowTaskParams params) {
        flowActuatorTaskService.addTasksByLocalData(params.getTasks(), params.getActuatorId(), params.getNetworkId(), params.getTargetIp());
        return new ServiceResult(true).asResult();
    }

    @ApiOperation("删除流量任务")
    @DeleteMapping("/task/delete")
    public ServiceResult deleteTasks(List<String> taskIds) {
        flowActuatorTaskService.deleteTasks(taskIds);
        return new ServiceResult(true).asResult();
    }

    @ApiOperation("启动单个流量任务")
    @PostMapping("/task/start")
    public ServiceResult<Boolean> startTask(String taskId) {
        flowActuatorTaskService.startTask(taskId);
        return new ServiceResult<>(true).asResult();
    }

    @ApiOperation("关闭单个流量任务")
    @PostMapping("/task/stop")
    public ServiceResult<Boolean> stopTask(String taskId) {
        flowActuatorTaskService.stopTask(taskId);
        return new ServiceResult<>(true).asResult();
    }

    @ApiOperation("启动多个流量任务")
    @PostMapping("/task/batch_start")
    public ServiceResult<Boolean> startTasks(@RequestBody FlowTaskBatchStartRequest request) {
        flowActuatorTaskService.startTasks(request.getTaskIds());
        return new ServiceResult<>(true).asResult();
    }

    @ApiOperation("关闭多个流量任务")
    @GetMapping("/stop_tasks")
    public String stopTasks(@RequestBody FlowTaskBatchStartRequest request) {
        flowActuatorTaskService.stopTasks(request.getTaskIds());
        return null;
    }

    /**
     * 编辑流量任务配置
     * @param request
     * @return
     */
    @ApiOperation("编辑流量任务配置")
    @PostMapping("/task/conf/edit")
    public ServiceResult editTaskConf(@RequestBody EditFlowTaskConfRequest request) {
        flowActuatorTaskService.editTaskConf(request);
        return ResultCommon.success();
    }

    /**
     * 流量任务配置查询
     * @param taskId
     * @return
     */
    @ApiOperation("流量任务配置查询")
    @ApiImplicitParam(name = "taskId", value = "流量任务ID", dataTypeClass = String.class)
    @GetMapping("/task/conf/detail")
    public ServiceResult<QueryFlowTaskConfResponse> queryConfByTaskId(@RequestParam String taskId) {
        var result = flowActuatorTaskService.queryConfByTaskId(taskId);
        return new ServiceResult<>(result).asResult();
    }

}