package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.bo.DeleteActuatorParams;
import cn.cyberpeace.mp.flow.bo.FlowActuatorQueryParams;
import cn.cyberpeace.mp.flow.bo.StartFlowActuatorParams;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.response.ActuatorSceneName;
import cn.cyberpeace.mp.flow.response.FlowActuatorResponse;
import cn.cyberpeace.mp.flow.service.FlowActuatorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@Api(tags = "流量发生器管理")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/actuator")
@Slf4j
@Data
public class FlowActuatorController {

    @Resource
    private FlowActuatorMapper flowActuatorMapper;

    @Resource
    private FlowActuatorService flowActuatorService;

    @ApiOperation("实时总带宽")
    @GetMapping("/total_rate")
    public ServiceResult<Integer> rateCount() {
        var result = flowActuatorService.totalRate();
        return new ServiceResult<>(result).asResult();
    }

    @ApiOperation("统计流量发生器已配置的任务数量")
    @GetMapping("/task_count")
    public ServiceResult<Integer> taskCount() {
        var names = flowActuatorService.taskCount();
        return new ServiceResult<>(names.get(0).getCount()).asResult();
    }

    @ApiOperation("统计流量发生器数量")
    @GetMapping("/statistics")
    public ServiceResult<Integer> statistics() {
        var names = flowActuatorService.getFlowActuatorStatistics();
        return new ServiceResult<>(names.get(0).getCount()).asResult();
    }

    @ApiOperation("查询流量发生器中的场景名称列表")
    @GetMapping("/name_list")
    public ServiceResult<List<ActuatorSceneName>> nameList() {
        var names = flowActuatorService.getFlowActuatorSceneNames();
        return new ServiceResult<>(names).asResult();
    }

    @ApiOperation("查询流量发生器列表")
    @GetMapping("/page")
    public ListObjServiceResult<FlowActuatorResponse> queryFlowActuator(FlowActuatorQueryParams params) {
        return flowActuatorService.getFlowActuatorList(params).asResult();
    }

    @ApiOperation("查询流量发生器详情")
    @GetMapping("/query_actuator")
    public ServiceResult<FlowActuatorResponse> queryFlowActuator(@RequestParam(value = "actuator_id") String actuatorId) {
        FlowActuatorResponse response = flowActuatorService.getFlowActuator(actuatorId);
        return new ServiceResult<>(response).asResult();
    }

    @ApiOperation("启动流量发生器")
    @PostMapping("/create_actuator")
    public ServiceResult<FlowActuatorResponse> createFlowActuator(@RequestBody StartFlowActuatorParams params) {
        var result = flowActuatorService.createFlowActuator(params);
        return new ServiceResult<>(result).asResult();
    }

    @ApiOperation("删除流量发生器")
    @PostMapping("/delete_actuator")
    public ServiceResult<Boolean> deleteFlowActuator(@RequestBody DeleteActuatorParams params) {
        flowActuatorService.deleteFlowActuator(params.getActuatorId());
        return new ServiceResult<>(true).asResult();
    }


    @Getter
    @Setter
    public static class TopoResponse<T> {
        private T data;
    }
}
