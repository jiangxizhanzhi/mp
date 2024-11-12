package cn.cyberpeace.mp.scenepluginflow.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.request.queryparam.FlowTaskPageQueryParams;
import cn.cyberpeace.mp.flow.response.FlowTaskPageResponse;
import cn.cyberpeace.mp.scenepluginflow.bo.EditFlowTaskConfRequest;
import cn.cyberpeace.mp.scenepluginflow.bo.QueryFlowTaskConfResponse;
import cn.cyberpeace.mp.scenepluginflow.service.FlowActuatorTaskService;
import cn.cyberpeace.mp.scenepluginflow.service.TopoService;
import cn.cyberpeace.mp.scenepluginflow.service.params.AddFlowTaskParams;
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
    private TopoService topoService;

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
        topoService.addTasks(params.getTasks(), params.getActuatorId(), params.getNetworkId(), params.getTargetIp());
        return new ServiceResult(true).asResult();
    }

    @ApiOperation("删除流量任务")
    @DeleteMapping("/task/delete")
    public ServiceResult deleteTasks(List<String> taskIds) {
        topoService.deleteTasks(taskIds);
        return new ServiceResult(true).asResult();
    }

    @ApiOperation("批量启动流量任务")
    @GetMapping("/start_tasks")
    public String startTasks(String[] taskIds) {
        topoService.deleteTasks(List.of(taskIds));
        return null;
    }

    @ApiOperation("批量停止流量任务")
    @GetMapping("/stop_tasks")
    public String stopTasks(List<String> taskIds) {
        topoService.deleteTasks(taskIds);
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
        return new ServiceResult<>(new QueryFlowTaskConfResponse());
    }

}