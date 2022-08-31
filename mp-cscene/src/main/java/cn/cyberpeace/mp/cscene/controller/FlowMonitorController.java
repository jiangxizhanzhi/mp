package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.request.common.AssetsStatisticsRequest;
import cn.cyberpeace.mp.cscene.request.common.AttackCountRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseTimeRangeRequest;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAnalysisSummary;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackDto;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackTime;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackTypeCount;
import cn.cyberpeace.mp.cscene.request.flow.FlowCommonRequest;
import cn.cyberpeace.mp.cscene.dto.flow.FlowRiskCount;
import cn.cyberpeace.mp.cscene.request.flow.FlowSceneSimulationRequest;
import cn.cyberpeace.mp.cscene.dto.flow.FlowTrend;
import cn.cyberpeace.mp.cscene.request.flow.FlowTrendRequest;
import cn.cyberpeace.mp.cscene.dto.flow.TotalFlowAttackRatio;
import cn.cyberpeace.mp.cscene.response.AssetsStatisticsResponse;
import cn.cyberpeace.mp.cscene.service.CsceneCommonService;
import cn.cyberpeace.mp.cscene.service.FlowMonitorService;
import cn.cyberpeace.mp.cscene.vo.FlowGroupByDestinationIpAndSeverityVO;
import cn.cyberpeace.mp.cscene.vo.FlowGroupPortWithProtocolVO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CategoryGroupBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.SceneAssetsBO;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private CsceneCommonService csceneCommonService;

    @ApiOperation(value = "模拟流量")
    @ApiImplicitParam(name = "request", value = "请求体")
    @GetMapping("/simulation")
    public ServiceResult simulation(FlowSceneSimulationRequest request) {
        flowMonitorService.simulation(request);

        return ServiceResult.successful();
    }

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    /**
     * 默认查询场景所有节点
     * 忽略elementIds的值
     * @return
     */
    @ApiOperation(value = "场景流量趋势图")
    @ApiImplicitParam(name = "flowTrendRequest", value = "查询数据请求体")
    @GetMapping("/scene_trend")
    public ServiceResult<List<FlowTrend>> getSceneFlowTrend(FlowTrendRequest flowTrendRequest) {
        List<FlowTrend> flowTrends = flowMonitorService.getSceneFlowTrend(flowTrendRequest);

        return ServiceResult.success(flowTrends);
    }

    /**
     * 默认查询场景所有节点
     * 忽略elementIds的值
     * @return
     */
    @ApiOperation(value = "场景受攻击端口")
    @ApiImplicitParam(name = "request", value = "查询数据请求体")
    @GetMapping("/scene_attacked_port")
    public ServiceResult<List<FlowGroupPortWithProtocolVO>> getSceneAttackPort(FlowCommonRequest request) {
        List<FlowGroupPortWithProtocolVO> list = flowMonitorService.groupByDestinationPortWithProtocol(request);

        return ServiceResult.success(list);
    }

    /**
     * 默认查询场景所有节点
     * 忽略elementIds的值
     * @return
     */
    @ApiOperation(value = "场景主机状态")
    @ApiImplicitParam(name = "request", value = "查询数据请求体")
    @GetMapping("/scene_target_status")
    public ServiceResult<List<FlowGroupByDestinationIpAndSeverityVO>> getSceneTargetStatus(FlowCommonRequest request) {
        List<FlowGroupByDestinationIpAndSeverityVO> list = flowMonitorService.groupByDestinationIpAndSeverity(request);

        return ServiceResult.success(list);
    }


    /**
     * elementIds若为多值，则只取第一个值
     * @return
     */
    @ApiOperation(value = "节点流量趋势图")
    @ApiImplicitParam(name = "flowTrendRequest", value = "查询数据请求体")
    @GetMapping("/element_trend")
    public ServiceResult<List<FlowTrend>> getElementFlowTrend(FlowTrendRequest flowTrendRequest) {
        List<FlowTrend> flowTrends = flowMonitorService.getElementFlowTrend(flowTrendRequest);

        return ServiceResult.success(flowTrends);
    }


    @ApiOperation(value = "流量风险各级别统计")
    @ApiImplicitParam(name = "baseRequest", value = "查询数据请求体")
    @GetMapping("/risk_count")
    public ServiceResult<FlowRiskCount> getFlowRiskCount(BaseSceneElementRequest baseRequest) {
        log.info("{}", baseRequest);

        FlowRiskCount riskCount = flowMonitorService.getFlowRiskCount(baseRequest);
        return ServiceResult.success(riskCount);
    }

    @ApiOperation(value = "攻击流量各类别统计")
    @ApiImplicitParam(name = "baseRequest", value = "查询数据请求体")
    @GetMapping("/attack_type_count")
    public ServiceResult<List<FlowAttackTypeCount>> getAttackTypeCount(BaseSceneElementRequest baseRequest) {
        log.info("attack_type_count params: {}", JsonCommon.stringify(baseRequest));

        List<FlowAttackTypeCount> attackTypeCounts = flowMonitorService.getAttackTypeCount(baseRequest);
        return ServiceResult.success(attackTypeCounts);
    }

    @ApiOperation(value = "攻击次数各按ip统计", notes = "单个ip发起攻击或受到攻击的次数")
    @ApiImplicitParam(name = "request", value = "查询数据请求体")
    @GetMapping("/attack_ip_count")
    public ServiceResult<List<FlowAttackTime>> getAttackTimes(AttackCountRequest request) {
        log.info("attack_times params: {}", JsonCommon.stringify(request));
        List<FlowAttackTime> attackTypeCounts = flowMonitorService.getAttackIpCount(request);
        return ServiceResult.success(attackTypeCounts);
    }

    @ApiOperation(value = "攻击占比统计")
    @ApiImplicitParam(name = "baseRequest", value = "查询数据请求体")
    @GetMapping("/attack_ratio_count")
    public ServiceResult<TotalFlowAttackRatio> getAttackRatioCount(AttackCountRequest baseRequest) {
        log.info("attack_times params: {}", JsonCommon.stringify(baseRequest));
        TotalFlowAttackRatio attackTypeCounts = flowMonitorService.getAttackRatioCount(baseRequest);
        return ServiceResult.success(attackTypeCounts);
    }

    @ApiOperation(value = "场景监控-流量分析: 统计场景流量，攻击流量，网络连接，带宽占用，攻击事件")
    @ApiImplicitParam(name = "timeRangeRequest", value = "查询数据请求体")
    @GetMapping("/summary")
    public ServiceResult<FlowAnalysisSummary> getFlowAnalysisSummary(BaseTimeRangeRequest timeRangeRequest) {
        log.info("{}", timeRangeRequest);
        FlowAnalysisSummary summary = flowMonitorService.getFlowAnalysisSummary(timeRangeRequest);
        return ServiceResult.success(summary);
    }


    @ApiOperation(value = "流量分页接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),
            @ApiImplicitParam(name = "queryParams", value = "查询数据请求体")
    })
    @GetMapping("/page")
    public ListObjServiceResult<FlowAttackDto> pageFlow(@RequestParam String id, FlowQueryParams queryParams) {
        log.info("pageFlow ====>> id:{}, queryParams:{}", id, queryParams);

        ListObjServiceResult<FlowAttackDto> result = flowMonitorService.pageFlow(id, queryParams);
        return result;
    }


    @ApiOperation(value = "资产统计", notes = "资产统计")
    @ApiImplicitParam(name = "request", value = "查询数据请求体")
    @GetMapping("/assets_statistics")
    public ServiceResult<List<AssetsStatisticsResponse>> getAssetsStatistics(AssetsStatisticsRequest request) {
        log.info("assets_statistics params: {}", JsonCommon.stringify(request));
        // 转换场景id
        List<String> sceneIds = csceneCommonService.translateSceneIds(request.getSceneIds());
        if (CollectionUtils.isEmpty(sceneIds)) {
            log.error("translateSceneIds is null");
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        List<SceneAssetsBO> sceneAssetsList = scenePluginCollectionSceneService.listSceneAssets(sceneIds).getSource();

        List<AssetsStatisticsResponse> responseList = new ArrayList<>();
        // 统计资产数量
        sceneIds.forEach(id -> {
            SceneAssetsBO sceneAssetsBO =
                    sceneAssetsList.stream().filter(list -> id.equals(list.getSceneId())).findFirst().get();
            List<CategoryGroupBO> categoryGroups = sceneAssetsBO.getCategoryGroups();
            // 操作系统
            List<CategoryGroupBO> osList = categoryGroups.stream().filter(group -> group.getCategory() == 1).collect(Collectors.toList());
            // 应用系统
            List<CategoryGroupBO> asList = categoryGroups.stream().filter(group -> group.getCategory() == 2).collect(Collectors.toList());
            // 网络设备
            List<CategoryGroupBO> networkList = categoryGroups.stream().filter(group -> group.getCategory() == 3).collect(Collectors.toList());
            // 安全设备
            List<CategoryGroupBO> ssList = categoryGroups.stream().filter(group -> group.getCategory() == 4).collect(Collectors.toList());
            AssetsStatisticsResponse response = AssetsStatisticsResponse.builder()
                    .sceneId(id)
                    .assetsTotal(sceneAssetsBO.getNodeCount())
                    .ipTotal(sceneAssetsBO.getIpCount())
                    .operationSystemCount(CollectionUtils.isNotEmpty(osList) ? osList.get(0).getElements().size() : 0)
                    .applicationSystemCount(CollectionUtils.isNotEmpty(asList) ? asList.get(0).getElements().size() : 0)
                    .networkCount(CollectionUtils.isNotEmpty(networkList) ? networkList.get(0).getElements().size() : 0)
                    .safetySystemCount(CollectionUtils.isNotEmpty(ssList) ? ssList.get(0).getElements().size() : 0)
                    .build();
            responseList.add(response);
        });

        return ServiceResult.success(responseList);
    }


}
