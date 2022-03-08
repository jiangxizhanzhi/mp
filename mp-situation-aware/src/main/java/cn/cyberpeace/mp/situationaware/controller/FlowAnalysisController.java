package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.bo.*;
import cn.cyberpeace.mp.situationaware.request.FlowAnalysisRequest;
import cn.cyberpeace.mp.situationaware.service.FlowAnalysisService;
import cn.cyberpeace.mp.situationaware.service.FlowPortProtocolService;
import cn.cyberpeace.mp.situationaware.vo.AttackDateHistogramNetworkBytesVO;
import cn.cyberpeace.mp.situationaware.vo.AttackWayAggregationVO;
import cn.cyberpeace.mp.situationaware.vo.FlowLogVO;
import cn.cyberpeace.mp.situationaware.vo.TeamAttackVO;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
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

    @ApiOperation(value = "流量日志")
    @GetMapping("/log")
    @Deprecated
    public ServiceResult<List<FlowLogVO>> flowLog(FlowAnalysisRequest request){
        request.setIndices(flowAnalysisService.getAllIndices(INDICES));
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
            vo.setTimestamp(bo.getTimestamp());
            return vo;
        }).collect(Collectors.toList());
        return new ServiceResult(flowLogVOList).asResult();
    }

    @ApiOperation(value = "根据聚合类型聚合不同的TOP N数据",hidden = true)
    @GetMapping("/top-By-aggregation-type")
    @Deprecated
    public ServiceResult<List<AttackWayAggregationVO>> topByAggregationTypeEnum(FlowAnalysisRequest request){
        request.setIndices(flowAnalysisService.getAllIndices(INDICES));
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
        request.setIndices(flowAnalysisService.getAllIndices(INDICES));
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

    @ApiOperation(value = "查询所有战队流量聚合数据, 战队名次按照攻击次数排名",hidden = true)
    @GetMapping("/team_traffic_sum")
    @Deprecated
    public ServiceResult<List<TeamAttackVO>> queryAllTeamAttackList(FlowAnalysisRequest request){
        List<TeamBO> teamBOList = request.getTeamBOS();
        FlowAnalysisRequest flowAnalysisRequest = this.buildFlowAnalysisRequest(request);

        List<TeamAttackVO> teamAttackResultVOS = teamBOList.stream().map(teamBo -> {
            flowAnalysisRequest.setSourceIps(teamBo.getIps());
            TeamAttackBO bo = flowAnalysisService.queryOneTeamAttackSumList(flowAnalysisRequest);

            TeamAttackVO teamAttackVO = new TeamAttackVO();
            teamAttackVO.setAttackTrafficPercent(bo.getAttackTrafficPercent());
            teamAttackVO.setAttackNumberPercent(bo.getAttackNumberPercent());
            teamAttackVO.setTeamName(teamBo.getName());
            teamAttackVO.setAttackWayList(bo.getAttackWayList());
            teamAttackVO.setAttackNumber(bo.getAttackNumber());
            teamAttackVO.setTrafficSize(bo.getTrafficSize());

            return teamAttackVO;
        }).collect(Collectors.toList());

        //计算百分比
        teamAttackResultVOS = buildPercent(teamAttackResultVOS);
        teamAttackResultVOS = teamAttackResultVOS.stream().sorted(Comparator.comparingLong(TeamAttackVO::getAttackNumber).reversed()).collect(Collectors.toList());
        return new ServiceResult<>(teamAttackResultVOS).asResult();
    }

    @ApiOperation(value = "测试")
    @GetMapping("/test")
    public void groupByDestinationPort(FlowAnalysisRequest request){
        flowPortProtocolService.findAll();
    }

    /**
     * 攻击手法百分比
     */
    private List<TeamAttackVO> buildPercent(List<TeamAttackVO> attackResultVOList) {
        Long totalNumber = attackResultVOList.stream().mapToLong(TeamAttackVO::getAttackNumber).sum();
        Double totalTraffic = attackResultVOList.stream().mapToDouble(TeamAttackVO::getTrafficSize).sum();
        if(Long.valueOf(0).equals(totalNumber) || Double.valueOf(0).equals(totalTraffic)){
            return attackResultVOList;
        }

        for (int i = 0; i < attackResultVOList.size(); i++) {
            var attack = attackResultVOList.get(i);
            if (i> 1 &&  i == attackResultVOList.size() - 1) {
                BigDecimal beforeSumNumber = attackResultVOList.subList(0, i).stream().map(TeamAttackVO::getAttackNumberPercent).reduce((a, b) -> a.add(b)).get();
                BigDecimal beforeSumTraffic = attackResultVOList.subList(0, i).stream().map(TeamAttackVO::getAttackTrafficPercent).reduce((a, b) -> a.add(b)).get();
                attack.setAttackNumberPercent(BigDecimal.valueOf(100).subtract(beforeSumNumber));
                attack.setAttackTrafficPercent(BigDecimal.valueOf(100).subtract(beforeSumTraffic));
            }else{
                attack.setAttackNumberPercent((BigDecimal.valueOf(attack.getAttackNumber()).divide(BigDecimal.valueOf(totalNumber), 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100)));
                attack.setAttackTrafficPercent((BigDecimal.valueOf(attack.getTrafficSize()).divide(BigDecimal.valueOf(totalTraffic), 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100)));
            }
        }
        return attackResultVOList;
    }

    /**
     *
     * @param request
     * @return
     */
    private FlowAnalysisRequest buildFlowAnalysisRequest(FlowAnalysisRequest request){
        FlowAnalysisRequest flowAnalysisRequest = new FlowAnalysisRequest();
        flowAnalysisRequest.setPageNum(request.getPageNum());
        flowAnalysisRequest.setPageSize(request.getPageSize());
        flowAnalysisRequest.setTopSize(request.getTopSize());
        flowAnalysisRequest.setStartDateTime(request.getStartDateTime());
        flowAnalysisRequest.setEndDateTime(request.getEndDateTime());
        flowAnalysisRequest.setGroupList(request.getGroupList());
        flowAnalysisRequest.setSourcePort(request.getSourcePort());
        flowAnalysisRequest.setSourceMacs(request.getSourceMacs());
        flowAnalysisRequest.setDestinationIps(request.getDestinationIps());
        flowAnalysisRequest.setProtocols(request.getProtocols());
        flowAnalysisRequest.setDestinationPort(request.getDestinationPort());
        flowAnalysisRequest.setSecondsInterval(request.getSecondsInterval());
        flowAnalysisRequest.setSeverityEnum(request.getSeverityEnum());
        flowAnalysisRequest.setAggregationTypeEnum(request.getAggregationTypeEnum());
        return flowAnalysisRequest;
    }
}
