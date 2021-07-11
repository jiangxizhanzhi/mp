package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.controller.BaseModelListControllerBase;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.request.flow.AddFlowTaskTaskRequest;
import cn.cyberpeace.mp.cscene.request.flow.EditFlowTaskConfRequest;
import cn.cyberpeace.mp.cscene.request.flow.FlowTaskBatchStartRequest;
import cn.cyberpeace.mp.cscene.request.flow.FlowTaskBatchStopRequest;
import cn.cyberpeace.mp.cscene.response.QueryFlowTaskConfResponse;
import cn.cyberpeace.mp.cscene.service.CsceneFlowService;
import cn.cyberpeace.mp.flow.bo.*;
import cn.cyberpeace.mp.flow.request.queryparam.FlowTaskPageQueryParams;
import cn.cyberpeace.mp.flow.response.FlowTaskPageResponse;
import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value = "场景流量", tags = "场景流量")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscene_flow")
@Data
public class CsceneFlowController implements BaseModelListControllerBase {

    @Autowired
    private CsceneFlowService csceneFlowService;

    
    @ApiOperation("新增流量任务")
    @PostMapping("/task/add")
    public ServiceResult addTask(@RequestBody AddFlowTaskTaskRequest request) {
        log.info("CsceneFlowController.addTask begin. request:{}", request);
        try {
            csceneFlowService.addTask(request);
        } catch (MpRuntimeException e) {
            return ResultCommon.fail(e.getServiceCode());
        }
        log.info("CsceneFlowController.addTask end.");
        return ResultCommon.success();
    }

    
    @ApiOperation("删除流量任务")
    @ApiImplicitParam(name = "id", value = "流量任务ID", dataTypeClass = String.class)
    @DeleteMapping("/task/delete")
    public ServiceResult deleteTask(@RequestParam String id){
        log.info("FlowInfoController.deleteTask begin. id:{}", id);
        csceneFlowService.deleteTask(id);
        log.info("FlowInfoController.deleteTask end");
        return ResultCommon.success();
    }

    
    @ApiOperation("流量任务列表查询")
    @GetMapping("/task/list")
    public ListObjServiceResult<FlowTaskPageResponse> taskList(FlowTaskPageQueryParams request) {
        log.info("FlowInfoController.taskList begin. request:{}", request);
        request.ensure();
        ListObjServiceResult<FlowTaskPageResponse> result = csceneFlowService.taskList(request);
        log.info("FlowInfoController.taskList end. result:{}", result);
        return result;
    }

    
    @ApiOperation("条件查询任务id集合")
    @GetMapping("/task_ids/list")
    public ServiceResult<List<String>> taskIdsList(FlowTaskPageQueryParams request) {
        request.ensure();
        return csceneFlowService.getTaskIds(request);
    }

    
    @ApiOperation("编辑流量任务配置")
    @PostMapping("/task/conf/edit")
    public ServiceResult editTaskConf(@RequestBody EditFlowTaskConfRequest request) {
        log.info("CsceneFlowController.editTaskConf begin. request:{}", request);
        csceneFlowService.editTaskConf(request);
        log.info("CsceneFlowController.editTaskConf end.");
        return ResultCommon.success();
    }

    
    @ApiOperation("流量任务配置查询")
    @ApiImplicitParam(name = "taskId", value = "流量任务ID", dataTypeClass = String.class)
    @GetMapping("/task/conf/detail")
    public ServiceResult<QueryFlowTaskConfResponse> queryConfByTaskId(@RequestParam String taskId) {
        log.info("FlowInfoController.queryConfByTaskId begin. taskId:{}", taskId);
        QueryFlowTaskConfResponse result = csceneFlowService.queryConfByTaskId(taskId);
        log.info("FlowInfoController.queryConfByTaskId end. result:{}", result);
        return ResultCommon.success(result);
    }

    
    @ApiOperation("流量仿真概况")
    @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    @GetMapping("/summary")
    public ServiceResult<FlowSummary> queryFlowSummary(@RequestParam String id) {
        log.info("FlowInfoController.queryFlowSummary begin. id:{}", id);
        FlowSummary result = csceneFlowService.queryFlowSummary(id);
        log.info("FlowInfoController.queryFlowSummary end. result:{}", result);
        return ResultCommon.success(result);
    }

    
    @ApiOperation("流量仿真统计")
    @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    @GetMapping("/statistics")
    public ServiceResult<FlowStatisticsInfo> flowStatistics(@RequestParam String id) {
        log.info("FlowInfoController.flowStatistics begin. id:{}", id);
        FlowStatisticsInfo result = csceneFlowService.flowStatistics(id);
        log.info("FlowInfoController.flowStatistics end. result:{}", result);
        return ResultCommon.success(result);
    }

    
    @ApiOperation("流量仿真所有统计")
    @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    @GetMapping("/statistics_all")
    public ServiceResult<FlowStatisticsAll> flowStatisticsAll(@RequestParam String id) {
        log.info("FlowInfoController.flowStatisticsAll begin. id:{}", id);
        FlowStatisticsAll result = csceneFlowService.flowStatisticsAll(id);
        log.info("FlowInfoController.flowStatisticsAll end. result:{}", result);
        return ResultCommon.success(result);
    }

    
    @ApiOperation("TOP5实时速率")
    @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    @GetMapping("/top5_node_txbps")
    public ServiceResult<List<FlowNodeTxBps>> top5NodeTxBps(@RequestParam String id) {
        log.info("FlowInfoController.flowStatistics begin. id:{}", id);
        List<FlowNodeTxBps> result = csceneFlowService.top5NodeTxBps(id);
        log.info("FlowInfoController.flowStatistics end. result:{}", result);
        return ResultCommon.success(result);
    }

    
    @ApiOperation("TOP5流量协议")
    @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    @GetMapping("/top5_protocols")
    public ServiceResult<JSONArray> top5Protocols(@RequestParam String id) {
        log.info("FlowInfoController.top5Protocols begin. id:{}", id);
        JSONArray result = csceneFlowService.top5Protocols(id);
        log.info("FlowInfoController.top5Protocols end. result:{}", result);
        return ResultCommon.success(result);
    }

    
    @ApiOperation("流量任务启动")
    @ApiImplicitParam(name = "taskId", value = "流量任务ID", dataTypeClass = String.class)
    @PostMapping("/task/start")
    public ServiceResult startTask(@RequestParam String taskId) {
        log.info("FlowInfoController.startTask begin. taskId:{}", taskId);
        csceneFlowService.startTask(taskId);
        log.info("FlowInfoController.startTask end.");
        return ResultCommon.success();
    }

    
    @ApiOperation("流量任务批量启动")
    @PostMapping("/task/batch_start")
    public ServiceResult batchStartTasks(@RequestBody FlowTaskBatchStartRequest request) {
        log.info("FlowInfoController.batchStartTasks begin. request:{}", request);
        csceneFlowService.batchStartTasks(request.getTaskIds());
        log.info("FlowInfoController.batchStartTasks end.");
        return ResultCommon.success();
    }

    
    @ApiOperation("流量任务停止")
    @ApiImplicitParam(name = "taskId", value = "流量任务ID", dataTypeClass = String.class)
    @PostMapping("/task/stop")
    public ServiceResult stopTask(@RequestParam String taskId) {
        log.info("FlowInfoController.stopTask begin. taskId:{}", taskId);
        csceneFlowService.stopTask(taskId);
        log.info("FlowInfoController.stopTask end.");
        return ResultCommon.success();
    }

    
    @ApiOperation("流量任务批量停止")
    @PostMapping("/task/batch_stop")
    public ServiceResult batchStopTasks(@RequestBody FlowTaskBatchStopRequest request) {
        log.info("FlowInfoController.batchStopTasks begin. request:{}", request);
        csceneFlowService.batchStopTasks(request.getTaskIds());
        log.info("FlowInfoController.batchStopTasks end.");
        return ResultCommon.success();
    }

    
    @ApiOperation("目标设备下拉选项")
    @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    @GetMapping("/node_option")
    public ServiceResult<List<FlowTaskNode>> nodeOption(@RequestParam String id) {
        log.info("FlowInfoController.nodeOption begin. id:{}", id);
        List<FlowTaskNode> result = csceneFlowService.nodeOption(id);
        log.info("FlowInfoController.nodeOption end. result:{}", result);
        return ResultCommon.success(result);
    }

    
    @ApiOperation("环境总速率")
    @ApiImplicitParam(name = "id", value = "环境总速率", dataTypeClass = String.class)
    @GetMapping("/total_rate")
    public ServiceResult<List<FlowStatisticsDetail>> totalRate(@RequestParam String id) {
        log.info("FlowInfoController.totalRate begin. id:{}", id);
        List<FlowStatisticsDetail> result = csceneFlowService.totalRate(id);
        log.info("FlowInfoController.totalRate end. result:{}", result);
        return ResultCommon.success(result);
    }

}
