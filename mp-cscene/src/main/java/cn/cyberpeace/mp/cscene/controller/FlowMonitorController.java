package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackDto;
import cn.cyberpeace.mp.cscene.dto.flow.FlowRiskCount;
import cn.cyberpeace.mp.cscene.dto.flow.FlowTrend;
import cn.cyberpeace.mp.cscene.dto.flow.FlowTrendSceneElementRequest;
import cn.cyberpeace.mp.cscene.service.FlowMonitorService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    private FlowMonitorService flowMonitorService;

    /**
     * 默认查询场景所有节点
     * 忽略elementIds的值
     *
     * @return
     */
    @ApiOperation(value = "场景流量趋势图")
    @GetMapping("/scene_trend")
    public ServiceResult<List<FlowTrend>> getSceneFlowTrend(FlowTrendSceneElementRequest flowTrendRequest) {
        log.info("flowTrendRequest:{}", flowTrendRequest);
        List<FlowTrend> flowTrends = flowMonitorService.getSceneFlowTrend(flowTrendRequest);

        return ServiceResult.success(flowTrends);
    }


    /**
     * elementIds若为多值，则只取第一个值
     *
     * @return
     */
    @ApiOperation(value = "节点流量趋势图")
    @GetMapping("/element_trend")
    public ServiceResult<List<FlowTrend>> getElementFlowTrend(FlowTrendSceneElementRequest flowTrendRequest) {
        log.info("flowTrendRequest:{}", flowTrendRequest);
        List<FlowTrend> flowTrends = flowMonitorService.getElementFlowTrend(flowTrendRequest);

        return ServiceResult.success(flowTrends);
    }


    @ApiOperation(value = "流量风险各级别统计")
    @GetMapping("/risk_count")
    public ServiceResult<FlowRiskCount> getFlowRiskCount(BaseSceneElementRequest baseRequest) {
        log.info("{}", baseRequest);

        FlowRiskCount riskCount = flowMonitorService.getFlowRiskCount(baseRequest);
        return ServiceResult.success(riskCount);
    }


    @ApiOperation(value = "流量分页接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
    })
    @GetMapping("/page")
    public ListObjServiceResult<FlowAttackDto> pageFlow(@RequestParam String id, FlowQueryParams queryParams) {
        log.info("id:{}, queryParams:{}", id, queryParams);

        ListObjServiceResult<FlowAttackDto> result = flowMonitorService.pageFlow(id, queryParams);
        return result;
    }


}
