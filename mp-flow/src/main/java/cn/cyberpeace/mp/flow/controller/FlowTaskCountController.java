package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.bo.FlowNodeTxBps;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsAll;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsInfo;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsProtocolsTopN;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsRate;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsTxTopN;
import cn.cyberpeace.mp.flow.bo.FlowSummary;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskRecordService;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Api(value = "场景流量", tags = "场景流量")
@Slf4j
@RestController
@RequestMapping("/cscene/cscene_flow")
@Data
public class FlowTaskCountController {

    @Resource
    private FlowTaskRecordService flowTaskRecordService;

    /**
     * 流量仿真概况
     * @param id
     * @return
     */
    @ApiOperation("流量仿真概况")
    @ApiImplicitParam(name = "id", value = "执行器id", dataTypeClass = String.class)
    @GetMapping("/summary")
    public ServiceResult<FlowSummary> queryFlowSummary(@RequestParam String id) {
        FlowSummary result = flowTaskRecordService.queryFlowSummary(id);
        return new ServiceResult<>(result).asResult();
    }

    @ApiOperation("环境总速率")
    @ApiImplicitParam(name = "id", value = "执行器id", dataTypeClass = String.class)
    @GetMapping("/total_rate_list")
    public ServiceResult<FlowSummary> totalRateList(@RequestParam String id) {
        FlowSummary result = flowTaskRecordService.queryFlowSummary(id);
        return new ServiceResult<>(result).asResult();
    }

    @ApiOperation("总速率")
    @ApiImplicitParam(name = "id", value = "执行器id", dataTypeClass = String.class)
    @GetMapping("/total_tx_bps_list")
    public ServiceResult<FlowStatisticsRate> totalTxBpsList(@RequestParam String id) {
        FlowStatisticsRate flowStatisticsRate = new FlowStatisticsRate();
        var result = flowTaskRecordService.flowStatistics(id);
        flowStatisticsRate.setTxBpsList(result.getTxBpsList());
        return new ServiceResult<>(flowStatisticsRate).asResult();
    }

    @ApiOperation("top5实时速率")
    @ApiImplicitParam(name = "id", value = "执行器id", dataTypeClass = String.class)
    @GetMapping("/top_node_tx_bps_list")
    public ServiceResult<FlowStatisticsTxTopN> topNodeTxBpsList(@RequestParam String id) {
        var result = flowTaskRecordService.queryNodeTxBps(id);
        FlowStatisticsTxTopN txTopN = new FlowStatisticsTxTopN();
        txTopN.setTop5NodeTxBpsList(result);
        return new ServiceResult<>(txTopN).asResult();
    }

    @ApiOperation("top5流量协议")
    @ApiImplicitParam(name = "id", value = "执行器id", dataTypeClass = String.class)
    @GetMapping("/top_protocols")
    public ServiceResult<FlowStatisticsProtocolsTopN> topProtocols(@RequestParam String id) {
        var result = flowTaskRecordService.top5Protocols(id);
        FlowStatisticsProtocolsTopN topN = new FlowStatisticsProtocolsTopN();
        topN.setTopProtocols(result);
        return new ServiceResult<>(topN).asResult();
    }

    /**
     * 流量仿真统计
     * @param id
     * @return
     */
    @ApiOperation("流量仿真统计")
    @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class)
    @GetMapping("/statistics")
    public ServiceResult<FlowStatisticsInfo> flowStatistics(@RequestParam String id) {
        var res = flowTaskRecordService.flowStatistics(id);
        return ResultCommon.success(res);
    }
}
