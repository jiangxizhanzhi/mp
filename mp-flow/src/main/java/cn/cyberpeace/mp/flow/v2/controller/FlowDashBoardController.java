package cn.cyberpeace.mp.flow.v2.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.v2.bo.FlowBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowDashBoardCountBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBandwidthCountBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskBandwidthCountBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.service.FlowDashBoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/26
 */
@Api(value = "仪表管理", tags = "仪表管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/dashboard")
public class FlowDashBoardController {

    @Resource
    private FlowDashBoardService flowDashBoardService;

    @ApiOperation("实时仿真数量统计")
    @GetMapping("/task_count")
    public ServiceResult<List<FlowDashBoardCountBO>> countTask() {
        return flowDashBoardService.countStartTask().asResult();
    }

    @ApiOperation("实时总带宽统计")
    @GetMapping("/bandwidth_count")
    public ServiceResult<List<FlowDashBoardCountBO>> countBandwidth() {
        return flowDashBoardService.sumBandwidth().asResult();
    }

    @ApiOperation("流量执行器带宽统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flow_generator_id", value = "流量执行器id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "start", value = "开始时间", dataTypeClass = String.class),
            @ApiImplicitParam(name = "end", value = "结束时间", dataTypeClass = String.class)
    })
    @GetMapping("/generator/bandwidth_count")
    public ServiceResult<FlowGeneratorBandwidthCountBO> countGeneratorBandwidth(
            @RequestParam(name = "flow_generator_id") String flowGeneratorId,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end
    ) {
        return flowDashBoardService.countFlowGenerator(flowGeneratorId, start, end).asResult();
    }

    @ApiOperation("仿真任务带宽统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flow_generator_id", value = "流量执行器id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "record_id", value = "流量任务执行id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "start", value = "开始时间", dataTypeClass = String.class),
            @ApiImplicitParam(name = "end", value = "结束时间", dataTypeClass = String.class)
    })
    @GetMapping("/generator/task/bandwidth_count")
    public ServiceResult<FlowTaskBandwidthCountBO> countTaskBandwidth(
            @RequestParam(name = "flow_generator_id") String flowGeneratorId,
            @RequestParam(name = "record_id") String recordId,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end
    ) {
        return flowDashBoardService.countFlowRecord(flowGeneratorId, recordId, start, end).asResult();
    }

    @ApiOperation("流量执行器仿真任务查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flow_generator_id", value = "流量执行器id", dataTypeClass = String.class),
    })
    @GetMapping("/generator/task_list")
    public ListObjServiceResult<FlowTaskRecordBO> generatorTaskList(
            @RequestParam(name = "flow_generator_id", required = false) String flowGeneratorId,
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "page_size", required = false, defaultValue = "10") Integer pageSize) {
        return flowDashBoardService.generatorTaskList(flowGeneratorId, page, pageSize).asResult();
    }

    @ApiOperation("仿真任务流查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "record_id", value = "流量任务执行id", dataTypeClass = String.class),
    })
    @GetMapping("/task/flow_list")
    public ServiceResult<List<FlowBO>> taskFlowList(
            @RequestParam(name = "record_id") String recordId) {
        return flowDashBoardService.taskFlowList(recordId).asResult();
    }

}