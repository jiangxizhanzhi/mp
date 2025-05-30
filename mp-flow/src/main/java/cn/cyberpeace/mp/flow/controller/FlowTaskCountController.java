package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.bo.FlowNodeTxBps;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsAll;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsInfo;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsProtocolsTopN;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsRate;
import cn.cyberpeace.mp.flow.bo.FlowStatisticsTxTopN;
import cn.cyberpeace.mp.flow.bo.FlowSummary;
import cn.cyberpeace.mp.flow.enums.TaskTaskStatusEnum;
import cn.cyberpeace.mp.flow.model.FlowProtocolModel;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowProtocolMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskRecordService;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        FlowTaskTaskMapper flowTaskTaskMapper = BeanCommon.getBean(FlowTaskTaskMapper.class);
        // 运行中的任务
        var tasks = flowTaskTaskMapper.list(new HumanCondition().filterEqual(FlowTaskTaskModel::getActuatorId, id)
                                                    .filterEqual(FlowTaskTaskModel::getTaskStatus, TaskTaskStatusEnum.RUNNING.toValue()));
        if (CollectionUtils.isEmpty(tasks)) {
            FlowStatisticsProtocolsTopN topN = new FlowStatisticsProtocolsTopN();
            topN.setTopProtocols(JsonCommon.createArrayNode());
            return new ServiceResult<>(topN).asResult();
        } else {
            List<String> flowIds = new ArrayList<>();
            for (var f : tasks) {
                flowIds.add(f.getFlowId());
            }
            FlowProtocolMapper flowProtocolMapper = BeanCommon.getBean(FlowProtocolMapper.class);
            var protocols = flowProtocolMapper.list(new HumanCondition().filterIn(FlowProtocolModel::getFlowId, flowIds.toArray(new String[]{})));
            if (CollectionUtils.isEmpty(protocols)) {
                FlowStatisticsProtocolsTopN topN = new FlowStatisticsProtocolsTopN();
                topN.setTopProtocols(JsonCommon.createArrayNode());
                return new ServiceResult<>(topN).asResult();
            }
            int totalTasks = flowIds.size();
            Map<String, FlowStatisticsRateV2> protocolRate = new HashMap<>();
            for (var p : protocols) {
                if (!protocolRate.containsKey(p.getProtocolName())) {
                    var rate = new FlowStatisticsRateV2();
                    rate.setProtocol(p.getProtocolName());
                    rate.setRate(BigDecimal.valueOf(0));
                    protocolRate.put(p.getProtocolName(), rate);
                }
                var big = protocolRate.get(p.getProtocolName());
                big.setRate(big.getRate().add(p.getRate()));
            }
            List<FlowStatisticsRateV2> results = new ArrayList<>(protocolRate.values());
            for (var r : results) {
                r.setRate(r.getRate().divide(BigDecimal.valueOf(totalTasks), 2, RoundingMode.HALF_UP));
            }
            Collections.sort(results, (o1, o2) -> o2.getRate().compareTo(o1.getRate()));
            if (results.size() > 5) {
                results = results.subList(0, 5);
            }
            JsonNode rspObj = JsonCommon.parseTree(JsonCommon.stringify(results));
            var xx = JsonCommon.createArrayNode();
            xx.addAll((ArrayNode) rspObj);
            FlowStatisticsProtocolsTopN topN = new FlowStatisticsProtocolsTopN();
            topN.setTopProtocols(xx);
            return new ServiceResult<>(topN).asResult();
        }
    }

    @Getter
    @Setter
    private static class FlowStatisticsRateV2 {
        private String protocol;
        private BigDecimal rate;
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
