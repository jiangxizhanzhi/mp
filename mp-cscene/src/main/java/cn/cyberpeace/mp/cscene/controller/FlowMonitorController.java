package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAnalysisSummary;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackConfigInfo;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackDto;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackTime;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackTypeCount;
import cn.cyberpeace.mp.cscene.dto.flow.FlowPcapFileInfo;
import cn.cyberpeace.mp.cscene.dto.flow.FlowRiskCount;
import cn.cyberpeace.mp.cscene.dto.flow.FlowTrend;
import cn.cyberpeace.mp.cscene.dto.flow.TotalFlowAttackRatio;
import cn.cyberpeace.mp.cscene.request.common.AssetsStatisticsRequest;
import cn.cyberpeace.mp.cscene.request.common.AttackCountRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseTimeRangeRequest;
import cn.cyberpeace.mp.cscene.request.flow.AttackTypeTopRequest;
import cn.cyberpeace.mp.cscene.request.flow.FlowCommonRequest;
import cn.cyberpeace.mp.cscene.request.flow.FlowSceneSimulationRequest;
import cn.cyberpeace.mp.cscene.request.flow.FlowTrendRequest;
import cn.cyberpeace.mp.cscene.response.AssetsStatisticsResponse;
import cn.cyberpeace.mp.cscene.service.CsceneCommonService;
import cn.cyberpeace.mp.cscene.service.FlowMonitorService;
import cn.cyberpeace.mp.cscene.vo.FlowGroupByDestinationIpAndSeverityVO;
import cn.cyberpeace.mp.cscene.vo.FlowGroupPortWithProtocolVO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CategoryGroupBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.SceneAssetsBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.SceneConfigAssetBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.SceneConfigCategoryGroupBO;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.util.FlowPcapExportUtil;
import cn.cyberpeace.mp.situationaware.service.FlowClassificationsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;


@Api(value = "流量监控", tags = "流量监控")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor/flow")
public class FlowMonitorController {

    @Autowired
    private FlowMonitorService flowMonitorService;

    @Autowired
    private CsceneCommonService csceneCommonService;

    @Autowired
    private FlowClassificationsService flowClassificationsService;

    @ApiOperation(value = "模拟流量")
    @GetMapping("/simulation")
    public ServiceResult simulation(FlowSceneSimulationRequest request) {
        flowMonitorService.simulation(request);

        return ResultCommon.success();
    }

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    
    @ApiOperation(value = "场景流量趋势图")
    @GetMapping("/scene_trend")
    public ServiceResult<List<FlowTrend>> getSceneFlowTrend(FlowTrendRequest flowTrendRequest) {
        if (StringUtils.isEmpty(flowTrendRequest.getSceneId())) {
            return ResultCommon.success(new ArrayList<>());
        }
        if (CollectionUtils.isNotEmpty(flowTrendRequest.getElementIds())) {
            List<FlowTrend> flowTrends = flowMonitorService.getElementFlowTrend(flowTrendRequest);
            return ResultCommon.success(flowTrends);
        } else {
            List<FlowTrend> flowTrends = flowMonitorService.getSceneFlowTrend(flowTrendRequest);
            return ResultCommon.success(flowTrends);
        }
    }

    
    @ApiOperation(value = "场景受攻击端口")
    @GetMapping("/scene_attacked_port")
    public ServiceResult<List<FlowGroupPortWithProtocolVO>> getSceneAttackPort(FlowCommonRequest request) {
        List<FlowGroupPortWithProtocolVO> list = flowMonitorService.groupByDestinationPortWithProtocol(request);

        return ResultCommon.success(list);
    }

    
    @ApiOperation(value = "场景主机状态")
    @GetMapping("/scene_target_status")
    public ServiceResult<List<FlowGroupByDestinationIpAndSeverityVO>> getSceneTargetStatus(FlowCommonRequest request) {
        List<FlowGroupByDestinationIpAndSeverityVO> list = flowMonitorService.groupByDestinationIpAndSeverity(request);

        return ResultCommon.success(list);
    }


    
    @ApiOperation(value = "节点流量趋势图")
    @GetMapping("/element_trend")
    public ServiceResult<List<FlowTrend>> getElementFlowTrend(FlowTrendRequest flowTrendRequest) {
        List<FlowTrend> flowTrends = flowMonitorService.getElementFlowTrend(flowTrendRequest);

        return ResultCommon.success(flowTrends);
    }


    @ApiOperation(value = "流量风险各级别统计")
    @GetMapping("/risk_count")
    public ServiceResult<FlowRiskCount> getFlowRiskCount(BaseSceneElementRequest baseRequest) {
        log.info("{}", baseRequest);

        FlowRiskCount riskCount = flowMonitorService.getFlowRiskCount(baseRequest);
        return ResultCommon.success(riskCount);
    }

    @ApiOperation(value = "攻击流量各类别统计")
    @GetMapping("/attack_type_count")
    public ServiceResult<List<FlowAttackTypeCount>> getAttackTypeCount(AttackTypeTopRequest baseRequest) {
        log.info("attack_type_count params: {}", JsonCommon.stringify(baseRequest));
        if (StringUtils.isEmpty(baseRequest.getSceneId())) {
            return ResultCommon.success(new ArrayList<>());
        }

        List<FlowAttackTypeCount> attackTypeCounts = flowMonitorService.getAttackTypeCount(baseRequest);
        if (baseRequest.getTopNWithOther() != null) {
            if (attackTypeCounts != null && attackTypeCounts.size() > baseRequest.getTopNWithOther()) {
                Collections.sort(attackTypeCounts, (a1, a2) -> a2.getTimes() - a1.getTimes());
                int other = 0;
                List<FlowAttackTypeCount> result = new ArrayList<>();
                for (int i = 0; i < attackTypeCounts.size(); i++) {
                    if (i < baseRequest.getTopNWithOther()) {
                        result.add(attackTypeCounts.get(i));
                    } else {
                        other += attackTypeCounts.get(i).getTimes();
                    }
                }
                var otherCount = new FlowAttackTypeCount();
                otherCount.setAttackType(MessageCommon.get("cscene.flowtype.other"));
                otherCount.setTimes(other);
                result.add(otherCount);
                return ResultCommon.success(result);
            }
        }
        return ResultCommon.success(attackTypeCounts);
    }

    @ApiOperation(value = "攻击次数各按ip统计", notes = "单个ip发起攻击或受到攻击的次数")
    @GetMapping("/attack_ip_count")
    public ServiceResult<List<FlowAttackTime>> getAttackTimes(AttackCountRequest request) {
        log.info("attack_times params: {}", JsonCommon.stringify(request));
        List<FlowAttackTime> attackTypeCounts = flowMonitorService.getAttackIpCount(request);
        return ResultCommon.success(attackTypeCounts);
    }

    @ApiOperation(value = "攻击占比统计")
    @GetMapping("/attack_ratio_count")
    public ServiceResult<TotalFlowAttackRatio> getAttackRatioCount(AttackCountRequest baseRequest) {
        log.info("attack_times params: {}", JsonCommon.stringify(baseRequest));
        TotalFlowAttackRatio attackTypeCounts = flowMonitorService.getAttackRatioCount(baseRequest);
        return ResultCommon.success(attackTypeCounts);
    }

    @ApiOperation(value = "场景监控-流量分析: 统计场景流量，攻击流量，网络连接，带宽占用，攻击事件")
    @GetMapping("/summary")
    public ServiceResult<FlowAnalysisSummary> getFlowAnalysisSummary(BaseTimeRangeRequest timeRangeRequest) {
        log.info("{}", timeRangeRequest);
        FlowAnalysisSummary summary = flowMonitorService.getFlowAnalysisSummary(timeRangeRequest);
        return ResultCommon.success(summary);
    }

    @ApiOperation(value = "查询流量攻击类型列表")
    @GetMapping("/attack_types")
    public ServiceResult<FlowAttackConfigInfo> getFlowAttackType() {
        var result = flowClassificationsService.findAll();
        var list = result.stream().map(r -> {
            FlowAttackConfigInfo.FlowAttackType type = new FlowAttackConfigInfo.FlowAttackType();
            type.setValue(r.getCategoryEn());
            type.setSeverity(r.getSeverity());
            Locale locale = LocaleContextHolder.getLocale();
            if (Objects.equals(locale, Locale.US)) {
                type.setName(r.getCategoryEn());
            } else {
                type.setName(r.getCategoryZh());
            }
            return type;
        }).collect(Collectors.toList());
        FlowAttackConfigInfo info = new FlowAttackConfigInfo();
        info.setTypes(list);
        return new ServiceResult<>(info).asResult();
    }

    @ApiOperation(value = "流量分页接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),})
    @GetMapping("/page")
    public ListObjServiceResult<FlowAttackDto> pageFlow(@RequestParam String id, FlowQueryParams queryParams) {
        log.info("pageFlow ====>> id:{}, queryParams:{}", id, queryParams);

        ListObjServiceResult<FlowAttackDto> result = flowMonitorService.pageFlow(id, queryParams);
        return result;
    }


    @ApiOperation(value = "资产统计", notes = "资产统计")
    @GetMapping("/assets_statistics")
    public ServiceResult<List<AssetsStatisticsResponse>> getAssetsStatistics(AssetsStatisticsRequest request) {
        log.info("assets_statistics params: {}", JsonCommon.stringify(request));
        
        List<String> sceneIds = csceneCommonService.translateSceneIds(request.getSceneIds());
        if (CollectionUtils.isEmpty(sceneIds)) {
            log.error("translateSceneIds is null");
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        List<SceneAssetsBO> sceneAssetsList = scenePluginCollectionSceneService.listSceneAssets(sceneIds).getSource();

        List<AssetsStatisticsResponse> responseList = new ArrayList<>();
        
        sceneIds.forEach(id -> {
            SceneAssetsBO sceneAssetsBO = sceneAssetsList.stream().filter(list -> id.equals(list.getSceneId())).findFirst().get();
            List<CategoryGroupBO> categoryGroups = sceneAssetsBO.getCategoryGroups();
            
            List<CategoryGroupBO> osList = categoryGroups.stream().filter(group -> group.getCategory() == 1).collect(Collectors.toList());
            
            List<CategoryGroupBO> asList = categoryGroups.stream().filter(group -> group.getCategory() == 2).collect(Collectors.toList());
            
            List<CategoryGroupBO> networkList = categoryGroups.stream().filter(group -> group.getCategory() == 3).collect(Collectors.toList());
            
            List<CategoryGroupBO> ssList = categoryGroups.stream().filter(group -> group.getCategory() == 4).collect(Collectors.toList());
            AssetsStatisticsResponse response = AssetsStatisticsResponse.builder().sceneId(id).assetsTotal(sceneAssetsBO.getNodeCount()).ipTotal(sceneAssetsBO.getIpCount()).operationSystemCount(CollectionUtils.isNotEmpty(osList) ? osList.get(0).getElements().size() : 0).applicationSystemCount(CollectionUtils.isNotEmpty(asList) ? asList.get(0).getElements().size() : 0).networkCount(CollectionUtils.isNotEmpty(networkList) ? networkList.get(0).getElements().size() : 0).safetySystemCount(CollectionUtils.isNotEmpty(ssList) ? ssList.get(0).getElements().size() : 0).build();
            responseList.add(response);
        });

        return ResultCommon.success(responseList);
    }

    @ApiOperation(value = "场景模板资产统计", notes = "场景模板资产统计")
    @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class)
    @GetMapping("/scene_config/assets_statistics")
    public ServiceResult<List<AssetsStatisticsResponse>> getSceneConfigAssetsStatistics(@RequestParam String id) {
        log.info("assets_statistics params: {}", id);
        String sceneConfigId = csceneCommonService.translateSceneConfigId(id);
        if (sceneConfigId == null) {
            log.error("translateSceneConfigId is null");
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        SceneConfigAssetBO sceneConfigAsset = scenePluginCollectionSceneService.listSceneConfigAssets(sceneConfigId).getSource();
        List<AssetsStatisticsResponse> responseList = new ArrayList<>();
        
        List<SceneConfigCategoryGroupBO> categoryGroups = sceneConfigAsset.getCategoryGroups();
        
        List<SceneConfigCategoryGroupBO> osList = categoryGroups.stream().filter(group -> group.getCategory() == 1).collect(Collectors.toList());
        
        List<SceneConfigCategoryGroupBO> asList = categoryGroups.stream().filter(group -> group.getCategory() == 2).collect(Collectors.toList());
        
        List<SceneConfigCategoryGroupBO> networkList = categoryGroups.stream().filter(group -> group.getCategory() == 3).collect(Collectors.toList());
        
        List<SceneConfigCategoryGroupBO> ssList = categoryGroups.stream().filter(group -> group.getCategory() == 4).collect(Collectors.toList());
        AssetsStatisticsResponse response = AssetsStatisticsResponse.builder().sceneId(id).assetsTotal(sceneConfigAsset.getNodeCount()).operationSystemCount(CollectionUtils.isNotEmpty(osList) ? osList.get(0).getElements().size() : 0).applicationSystemCount(CollectionUtils.isNotEmpty(asList) ? asList.get(0).getElements().size() : 0).networkCount(CollectionUtils.isNotEmpty(networkList) ? networkList.get(0).getElements().size() : 0).safetySystemCount(CollectionUtils.isNotEmpty(ssList) ? ssList.get(0).getElements().size() : 0).build();
        responseList.add(response);

        return ResultCommon.success(responseList);
    }

    @ApiOperation(value = "导出全部流量文件")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),})
    @GetMapping("/export")
    public ServiceResult<FlowPcapFileInfo> export(String id) {
        FlowPcapFileInfo pcapFileInfo = new FlowPcapFileInfo();
        String taskId = id;
        if (FlowPcapExportUtil.initProcess(taskId)) {
            TaskCommon.executeTask(new Runnable() {
                @Override
                public void run() {
                    flowMonitorService.export(taskId, id);
                }
            });
        }
        pcapFileInfo.setTaskId(taskId);
        return new ServiceResult<>(pcapFileInfo).asResult();
    }


    @ApiOperation(value = "流量导出进度")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "任务id", dataTypeClass = String.class),})
    @GetMapping("/export/process")
    public ServiceResult<FlowPcapExportUtil.FlowPcapExportProcess> process(String id) {
        return new ServiceResult<>(FlowPcapExportUtil.getProcess(id)).asResult();
    }

}
