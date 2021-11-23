package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.flow.FlowDataStatistic;
import cn.cyberpeace.mp.cscene.dto.flow.FlowPageRequest;
import cn.cyberpeace.mp.cscene.dto.flow.FlowRiskAggrResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 11:22
 */
@Api(value = "流量监控", tags = "流量监控")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor/flow")
public class FlowMonitorController {

    @ApiOperation(value = "流量统计")
    @GetMapping("/statistic")
    public ServiceResult<FlowDataStatistic> getFlowStatistic() {

        return null;
    }

    @ApiOperation(value = "流量风险统计")
    @GetMapping("/risk_aggr")
    public ServiceResult<FlowRiskAggrResponse> getFlowRiskAggr() {

        return null;
    }

    @ApiOperation(value = "分页查询流量")
    @GetMapping("/page")
    public ServiceResult getEventPage(FlowPageRequest flowPageRequest) {
        log.info("flowPageRequest:{}", flowPageRequest);

        return null;
    }

}
