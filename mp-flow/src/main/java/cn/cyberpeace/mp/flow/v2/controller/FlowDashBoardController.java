package cn.cyberpeace.mp.flow.v2.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.v2.bo.FlowBandwidthCountBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowDashBoardCountBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBandwidthCountBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskBandwidthCountBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.service.FlowDashBoardService;
import cn.cyberpeace.mp.flow.v2.service.FlowTaskCountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    private FlowTaskCountService flowTaskCountService;

    @Resource
    private FlowDashBoardService flowDashBoardService;

    @ApiOperation("实时仿真数量统计")
    @GetMapping("/task_count")
    public ServiceResult<FlowDashBoardCountBO> countTask() {
        var result = new FlowDashBoardCountBO();
        result.setCount(10L);
        return new ServiceResult<>(result);
    }

    @ApiOperation("实时总带宽统计")
    @GetMapping("/bandwidth_count")
    public ServiceResult<FlowDashBoardCountBO> countBandwidth() {
        // 单位字节？
        var result = new FlowDashBoardCountBO();
        result.setCount(10L);
        return new ServiceResult<>(result);
    }

    @ApiOperation("流量执行器带宽统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flow_generator_id", value = "流量执行器id", dataTypeClass = String.class),
    })
    @GetMapping("/generator/bandwidth_count")
    public ServiceResult<FlowGeneratorBandwidthCountBO> countGeneratorBandwidth(
            @RequestParam(name = "flow_generator_id") String flowGeneratorId
    ) {
        // 单位字节？
        var result = new FlowGeneratorBandwidthCountBO();
        result.setTotal(10L);
        return new ServiceResult<>(result);
    }

    @ApiOperation("仿真任务带宽统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flow_generator_id", value = "流量执行器id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "record_id", value = "流量任务执行id", dataTypeClass = String.class),
    })
    @GetMapping("/generator/task/bandwidth_count")
    public ServiceResult<FlowTaskBandwidthCountBO> countTaskBandwidth(
            @RequestParam(name = "flow_generator_id") String flowGeneratorId,
            @RequestParam(name = "record_id") String recordId
    ) throws Exception {
        // 单位字节？
        List<Date> dates = new ArrayList<>();
        dates.add(DateUtils.parseDate("2022-11-01 11:41:10", "YYYY-MM-dd HH:mm:ss"));
        dates.add(DateUtils.parseDate("2022-11-01 11:41:20", "YYYY-MM-dd HH:mm:ss"));
        dates.add(DateUtils.parseDate("2022-11-01 11:41:30", "YYYY-MM-dd HH:mm:ss"));
        dates.add(DateUtils.parseDate("2022-11-01 11:41:40", "YYYY-MM-dd HH:mm:ss"));
        flowDashBoardService.count(flowGeneratorId, recordId, dates);
        var result = new FlowTaskBandwidthCountBO();
        result.setTotal(10L);
        return new ServiceResult<>(result);
    }

    @ApiOperation("仿真任务单个流带宽统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flow_generator_id", value = "流量执行器id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "record_id", value = "流量任务执行记录id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "flow_id", value = "流量id", dataTypeClass = String.class),
    })
    @GetMapping("/generator/task/flow/bandwidth_count")
    public ServiceResult<FlowBandwidthCountBO> countFlowBandwidth(
            @RequestParam(name = "flow_generator_id") String flowGeneratorId,
            @RequestParam(name = "record_id") String recordId,
            @RequestParam(name = "flow_id") String flowId
    ) {
        // 单位字节？
        var result = new FlowBandwidthCountBO();
        return new ServiceResult<>(result);
    }

    @ApiOperation("流量执行器仿真任务查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flow_generator_id", value = "流量执行器id", dataTypeClass = String.class),
    })
    @GetMapping("/generator/task_list")
    public ListObjServiceResult<ListObjResult<FlowTaskRecordBO>> countGeneratorTaskList(@RequestParam(name = "flow_generator_id") String flowGeneratorId) {
        // 单位字节？
        ListObjResult<FlowTaskRecordBO> result = new ListObjResult<>();
        return new ListObjServiceResult(result);
    }

}