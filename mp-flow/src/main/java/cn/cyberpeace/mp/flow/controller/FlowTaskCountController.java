//package cn.cyberpeace.mp.flow.controller;
//
//import cn.cyberpeace.mp.base.controller.common.ResultCommon;
//import cn.cyberpeace.mp.base.service.result.ServiceResult;
//import cn.cyberpeace.mp.flow.bo.FlowNodeTxBps;
//import cn.cyberpeace.mp.flow.bo.FlowStatisticsAll;
//import cn.cyberpeace.mp.flow.bo.FlowStatisticsInfo;
//import cn.cyberpeace.mp.flow.bo.FlowSummary;
//import cn.cyberpeace.mp.flow.service.impl.FlowTaskRecordService;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiOperation;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.List;
//
//@Api(value = "场景流量", tags = "场景流量")
//@Slf4j
//@RestController
//@RequestMapping("/cscene/cscene_flow")
//@Data
//public class FlowTaskCountController {
//
//    @Resource
//    private FlowTaskRecordService flowTaskRecordService;
//
//    /**
//     * 流量仿真概况
//     * @param id
//     * @return
//     */
//    @ApiOperation("流量仿真概况")
//    @ApiImplicitParam(name = "id", value = "执行器id", dataTypeClass = String.class)
//    @GetMapping("/summary")
//    public ServiceResult<FlowSummary> queryFlowSummary(@RequestParam String id) {
//        FlowSummary result = flowTaskRecordService.queryFlowSummary(id);
//        return new ServiceResult<>(result).asResult();
//    }
//
//    /**
//     * 流量仿真统计
//     * @param id
//     * @return
//     */
//    @ApiOperation("流量仿真统计")
//    @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
//    @GetMapping("/statistics")
//    public ServiceResult<FlowStatisticsInfo> flowStatistics(@RequestParam String id) {
//        FlowStatisticsInfo result = null;
//        return ResultCommon.success(result);
//    }
//
//    /**
//     * 流量仿真所有统计（包括概况、统计、环境总速率、top5）
//     * @param id
//     * @return
//     */
//    @ApiOperation("流量仿真所有统计")
//    @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
//    @GetMapping("/statistics_all")
//    public ServiceResult<FlowStatisticsAll> flowStatisticsAll(@RequestParam String id) {
//        FlowStatisticsAll flowStatisticsAll = new FlowStatisticsAll();
//        flowStatisticsAll.setFlowSummary(flowTaskRecordService.queryFlowSummary(id));
//        flowStatisticsAll.setFlowStatisticsInfo(flowTaskRecordService.flowStatistics(id));
//        flowStatisticsAll.setTop5NodeTxBpsList(flowTaskRecordService.queryNodeTxBps(id));
//        flowStatisticsAll.setTop5Protocols(flowTaskRecordService.top5Protocols(id));
//        flowStatisticsAll.setTotalRateList(new ArrayList<>());
//        return ResultCommon.success(flowStatisticsAll);
//    }
//
//    /**
//     * TOP5实时速率
//     * @param id
//     * @return
//     */
//    @ApiOperation("TOP5实时速率")
//    @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
//    @GetMapping("/top5_node_txbps")
//    public ServiceResult<List<FlowNodeTxBps>> top5NodeTxBps(@RequestParam String id) {
//        List<FlowNodeTxBps> result = flowTaskRecordService.queryNodeTxBps(id);
//        return ResultCommon.success(result);
//    }
//
//    /**
//     * TOP5流量协议
//     * @param id
//     * @return
//     */
//    @ApiOperation("TOP5流量协议")
//    @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
//    @GetMapping("/top5_protocols")
//    public ServiceResult<ArrayNode> top5Protocols(@RequestParam String id) {
//        ArrayNode result = flowTaskRecordService.top5Protocols(id);
//        return ResultCommon.success(result);
//    }
//}
