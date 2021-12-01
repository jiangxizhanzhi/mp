package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.flow.FlowRiskCount;
import cn.cyberpeace.mp.cscene.dto.flow.FlowTrendSceneElementRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    /**
     * 默认查询场景所有节点
     * 忽略elementIds的值
     *
     * @return
     */
    @ApiOperation(value = "场景流量趋势图")
    @GetMapping("/scene_trend")
    public ServiceResult<List<HostState>> getSceneFlowTrend(FlowTrendSceneElementRequest flowTrendRequest) {
        log.info("flowTrendRequest:{}", flowTrendRequest);

        return null;
    }


    /**
     * elementIds若为多值，则只取第一个值
     *
     * @return
     */
    @ApiOperation(value = "节点流量趋势图")
    @GetMapping("/element_trend")
    public ServiceResult<List<HostState>> getElementHostStateTrend(FlowTrendSceneElementRequest flowTrendRequest) {
        log.info("flowTrendRequest:{}", flowTrendRequest);

        return null;
    }


    @ApiOperation(value = "流量风险各级别统计")
    @GetMapping("/risk_count")
    public ServiceResult<FlowRiskCount> getFlowRiskCount(BaseSceneElementRequest baseRequest) {
        log.info("{}", baseRequest);

        return null;
    }


}
