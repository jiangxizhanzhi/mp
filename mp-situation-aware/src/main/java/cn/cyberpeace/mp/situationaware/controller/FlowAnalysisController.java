package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.situationaware.bo.AttackDateHistogramNetworkBytesBO;
import cn.cyberpeace.mp.situationaware.bo.AttackWayAggregationBO;
import cn.cyberpeace.mp.situationaware.bo.FlowLogBO;
import cn.cyberpeace.mp.situationaware.request.FlowAnalysisRequest;
import cn.cyberpeace.mp.situationaware.request.FlowSimulationRequest;
import cn.cyberpeace.mp.situationaware.service.FlowAnalysisService;
import cn.cyberpeace.mp.situationaware.service.FlowPortProtocolService;
import cn.cyberpeace.mp.situationaware.vo.AttackDateHistogramNetworkBytesVO;
import cn.cyberpeace.mp.situationaware.vo.AttackWayAggregationVO;
import cn.cyberpeace.mp.situationaware.vo.FlowLogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流量分析Controller
 * @author wanggang
 */
@Slf4j
@RestController
@RequestMapping("/flow/analysis")
@Validated
@Api(tags = "流量分析")
public class FlowAnalysisController {

    private static final String INDICES = "mp*sessions3*";

    private FlowAnalysisService flowAnalysisService;

    @Autowired
    private FlowPortProtocolService flowPortProtocolService;


    @Autowired
    public FlowAnalysisController(FlowAnalysisService flowAnalysisService) {
        this.flowAnalysisService = flowAnalysisService;
    }

    @ApiOperation("初始化攻击分类")
    @GetMapping("/init-classification")
    public void initClassification() throws IOException {
        //初始化分类数据
        flowAnalysisService.initClassification();
    }

    @ApiOperation("新增模拟流量")
    @GetMapping("/add-simulation")
    public void addSimulation(FlowSimulationRequest request) throws IOException {
        flowAnalysisService.addSimulationFlow(request);
    }

    @ApiOperation(value = "流量日志")
    @GetMapping("/log")
    @Deprecated
    public ServiceResult<List<FlowLogVO>> flowLog(FlowAnalysisRequest request){
        List<FlowLogBO> flowLogBOList = flowAnalysisService.flowLog(request);
        if (CollectionUtils.isEmpty(flowLogBOList)){
            return new ServiceResult().asResult();
        }
        List<FlowLogVO> flowLogVOList = flowLogBOList.stream().map(bo -> {
            FlowLogVO vo = new FlowLogVO();
            vo.setDestination(bo.getDestination());
            vo.setNetwork(bo.getNetwork());
            vo.setSource(bo.getSource());
            vo.setSuricata(bo.getSuricata());
            vo.setTimestamp(TimeUtils.timestampToLocalDateTime(bo.getTimestamp()));
            return vo;
        }).collect(Collectors.toList());
        return new ServiceResult(flowLogVOList).asResult();
    }

    @ApiOperation(value = "根据聚合类型聚合不同的TOP N数据")
    @GetMapping("/top-By-aggregation-type")
    @Deprecated
    public ServiceResult<List<AttackWayAggregationVO>> topByAggregationTypeEnum(FlowAnalysisRequest request){
        List<AttackWayAggregationBO> attackWayAggregationBOS = flowAnalysisService.topByAggregationTypeEnum(request);
        if (CollectionUtils.isEmpty(attackWayAggregationBOS)){
            return new ServiceResult().asResult();
        }
        List<AttackWayAggregationVO> voList = attackWayAggregationBOS.stream().map(bo -> {
            AttackWayAggregationVO vo = new AttackWayAggregationVO();
            vo.setAggregationType(bo.getAggregationType());
            vo.setAttackNumber(bo.getAttackNumber());
            vo.setTrafficSize(bo.getTrafficSize());
            return vo;
        }).collect(Collectors.toList());
        return new ServiceResult(voList).asResult();
    }

    @ApiOperation(value = "统计时间窗口里的流量大小",hidden = true)
    @GetMapping("/sum-network-bytes")
    @Deprecated
    public ServiceResult<List<AttackDateHistogramNetworkBytesVO>> sumNetworkBytesByDateHistogramTimestamp(FlowAnalysisRequest request){
        List<AttackDateHistogramNetworkBytesBO> bos = flowAnalysisService.sumNetworkBytesByDateHistogramTimestamp(request);
        if (CollectionUtils.isEmpty(bos)){
            return new ServiceResult().asResult();
        }
        List<AttackDateHistogramNetworkBytesVO> vos = bos.stream().map(bo -> {
            AttackDateHistogramNetworkBytesVO vo = new AttackDateHistogramNetworkBytesVO();
            vo.setNetworkBytes(bo.getTrafficSize());
            vo.setTime(bo.getTime());
            return vo;
        }).collect(Collectors.toList());
        return new ServiceResult(vos).asResult();
    }
}
