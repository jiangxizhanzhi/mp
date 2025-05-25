package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.controller.request.SceneAllElementsQueryRequest;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementPort;
import cn.cyberpeace.mp.cscene.dto.flow.*;
import cn.cyberpeace.mp.cscene.model.SceneSwitchInfoModel;
import cn.cyberpeace.mp.cscene.repository.SceneSwitchInfoMapper;
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
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.FlowMonitorService;
import cn.cyberpeace.mp.cscene.service.SceneMonitorService;
import cn.cyberpeace.mp.cscene.service.multirange.testrange.NodeInfo;
import cn.cyberpeace.mp.cscene.service.multirange.testrange.TestRangeApiProxy;
import cn.cyberpeace.mp.cscene.vo.FlowGroupByDestinationIpAndSeverityVO;
import cn.cyberpeace.mp.cscene.vo.FlowGroupPortWithProtocolVO;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.service.CloudCommonConfigService;
import cn.cyberpeace.mp.sceneplugincollection.bo.CategoryGroupBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.SceneAssetsBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.SceneConfigAssetBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.SceneConfigCategoryGroupBO;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
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

    @Autowired
    private FlowClassificationsService flowClassificationsService;
    @Autowired
    private TestRangeApiProxy testRangeApiProxy;

    @Autowired
    private SceneMonitorService sceneMonitorService;

    @Autowired
    private SceneElementMapper sceneElementMapper;

    @Autowired
    private SceneSwitchInfoMapper sceneSwitchInfoMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Resource
    private CloudCommonConfigService cloudCommonConfigService;

    @Autowired
    private CollectionAppConfig collectionAppConfig;

    @ApiOperation(value = "模拟流量")
    @GetMapping("/simulation")
    public ServiceResult simulation(FlowSceneSimulationRequest request) {
        flowMonitorService.simulation(request);

        return ResultCommon.success();
    }

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    /**
     * 默认查询场景所有节点
     * 忽略elementIds的值
     *
     * @return
     */
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

    /**
     * 默认查询场景所有节点
     * 忽略elementIds的值
     *
     * @return
     */
    @ApiOperation(value = "场景受攻击端口")
    @GetMapping("/scene_attacked_port")
    public ServiceResult<List<FlowGroupPortWithProtocolVO>> getSceneAttackPort(FlowCommonRequest request) {
        List<FlowGroupPortWithProtocolVO> list = flowMonitorService.groupByDestinationPortWithProtocol(request);

        return ResultCommon.success(list);
    }

    /**
     * 默认查询场景所有节点
     * 忽略elementIds的值
     *
     * @return
     */
    @ApiOperation(value = "场景主机状态")
    @GetMapping("/scene_target_status")
    public ServiceResult<List<FlowGroupByDestinationIpAndSeverityVO>> getSceneTargetStatus(FlowCommonRequest request) {
        List<FlowGroupByDestinationIpAndSeverityVO> list = flowMonitorService.groupByDestinationIpAndSeverity(request);

        return ResultCommon.success(list);
    }


    /**
     * elementIds若为多值，则只取第一个值
     *
     * @return
     */
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
    public ListObjServiceResult<?> pageFlow(@RequestParam String id, FlowQueryParams queryParams) {
        long startTime = System.currentTimeMillis();
        log.info("pageFlow ====>> id:{}, queryParams:{}", id, queryParams);
        if (Objects.isNull(queryParams.getPage())) {
            queryParams.setPage(1);
        }
        if (Objects.isNull(queryParams.getPerPage())) {
            queryParams.setPerPage(100);
        }
        queryParams.ensure();


        //查所有，然后按照ip过滤
        var orginTargetNodes = queryParams.getTargetNodeIds();
        int originPerPage = queryParams.getPerPage();
        int originPage = queryParams.getPage();

        queryParams.setPerPage(10000);
        queryParams.setPage(1);

        //        TODO WK2025 查询源在层节点中 目的不在层节点中
        List<String> allNodes = new ArrayList<>();
        allNodes.addAll(testRangeApiProxy.getAllNodeIdsWithAssignedArea(id));
        queryParams.setSourceNodeIds(allNodes);

        ArrayList<String> emptyAreaNodeIds = new ArrayList<>();
        emptyAreaNodeIds.addAll(testRangeApiProxy.getAllNodeIdsWithEmptyArea(id));
        queryParams.setTargetNodeIds(emptyAreaNodeIds);

        List<FlowAttackDto> flowAttackInCurrentRange = new ArrayList<>();

        log.info("--------------------------------1");
        log.info(String.valueOf(System.currentTimeMillis()-startTime));
        startTime = System.currentTimeMillis();

        try {
            flowAttackInCurrentRange = flowMonitorService.pageFlow(id, queryParams).getData().getList();
        } catch (MpRuntimeException e) {
            log.error("error", e);
            return new ListObjServiceResult<>(new ListObjResult<>(Collections.emptyList(), 0).asResult()).asResult();
        }

        log.info("--------------------------------2");
        log.info(String.valueOf(System.currentTimeMillis()-startTime));
        startTime = System.currentTimeMillis();

        FlowQueryParams otherRangeFlowPageQuery = new FlowQueryParams();
        BeanUtils.copyProperties(queryParams, otherRangeFlowPageQuery);
        otherRangeFlowPageQuery.setPerPage(10000);
        otherRangeFlowPageQuery.setPage(1);
        otherRangeFlowPageQuery.setTargetNodeIds(Collections.emptyList());

        List<FlowAttackDto> allFlowAttack = new ArrayList<>();
        allFlowAttack.addAll(flowAttackInCurrentRange);

        try {
            List<FlowAttackDto> attackInOtherRange = testRangeApiProxy.pageFlow(id, otherRangeFlowPageQuery, true);
            allFlowAttack.addAll(attackInOtherRange);
        } catch (MpRuntimeException e) {
            log.error("error", e);
        }

        log.info("--------------------------------3");
        log.info(String.valueOf(System.currentTimeMillis()-startTime));
        startTime = System.currentTimeMillis();


        boolean showAllFlows = CollectionUtils.isEmpty(orginTargetNodes) || (orginTargetNodes.size() == 1 && orginTargetNodes.get(0).equals("null"));
        Set<String> validIps;
        if (!showAllFlows) {
            validIps = getIpsByNodes(id, orginTargetNodes);
        } else {
            validIps = new HashSet<>();
        }


        List<FlowAttackDto> sortedFlowAttacks = allFlowAttack.stream().filter(flowAttack -> {
            if (showAllFlows) {
                return true;
            } else {
                return validIps.contains(flowAttack.getTargetIp());
            }
        }).sorted((o1, o2) -> (int) (o2.getTimestamp() - o1.getTimestamp())).collect(Collectors.toList());
        int startIncludeIndex = (originPage - 1) * originPerPage;
        int endExcludeIndex = originPage * originPerPage;
        if (endExcludeIndex > sortedFlowAttacks.size()) {
            endExcludeIndex = sortedFlowAttacks.size();
        }
        var subList = sortedFlowAttacks.subList(startIncludeIndex, endExcludeIndex);

        Map<String, NodeInfo> ipToNodeInfoMap = testRangeApiProxy.getIpToNodeMap(id);

        log.info("--------------------------------4");
        log.info(String.valueOf(System.currentTimeMillis()-startTime));
        startTime = System.currentTimeMillis();

        subList.forEach(attackInfo -> {
            NodeInfo sourceNodeInfo = ipToNodeInfoMap.get(attackInfo.getSourceIp());
            if (Objects.nonNull(sourceNodeInfo)) {
                attackInfo.setSourceNodeId(sourceNodeInfo.getNodeId());
                attackInfo.setSourceNodeName(sourceNodeInfo.getNodeName());
            }
            NodeInfo targetNodeInfo = ipToNodeInfoMap.get(attackInfo.getTargetIp());
            if (Objects.nonNull(targetNodeInfo)) {
                attackInfo.setTargetNodeId(targetNodeInfo.getNodeId());
                attackInfo.setTargetNodeName(targetNodeInfo.getNodeName());
            }
        });
        ServiceResult<Map<String, Object>> mapServiceResult = cloudCommonConfigService.getSystemTypeToAgentSupport().asResult();
        HumanCondition humanCondition = new HumanCondition();
        String sceneId = csceneInstanceService.getSceneId(id);
        humanCondition.filterEqual(SceneElementModel::getScene, sceneId);
        List<SceneElementModel> list = sceneElementMapper.list(humanCondition);
        Map<String, String> collect = list.stream().collect(Collectors.toMap(SceneElementModel::getNodeId, SceneElementModel::getSystemType));
        HumanCondition humanCondition1 = new HumanCondition();
        humanCondition1.filterEqual(SceneSwitchInfoModel::getSceneId, id);
        List<SceneSwitchInfoModel> list1 = sceneSwitchInfoMapper.list(humanCondition1);
        Map<String, List<SceneSwitchInfoModel>> collect2 = list1.stream().collect(Collectors.groupingBy(SceneSwitchInfoModel::getNodeId));

        List<UserModel> userModels = userService.listByUsers();

        log.info("--------------------------------5");
        log.info(String.valueOf(System.currentTimeMillis()-startTime));
        startTime = System.currentTimeMillis();

        Map<String, UserModel> userModelMap = userModels.stream().collect(Collectors.toMap(UserModel::getId, s -> s));
        subList.stream().forEach(item -> {
            String targetNodeId = item.getTargetNodeId();
            String systemType = collect.get(targetNodeId);
            if(mapServiceResult.getSource().containsKey(systemType)){
                String standardSystemType = SystemTypeUtils.getStandardSystemType(systemType);
                item.setTargetNodeType(standardSystemType);
            } else {
                item.setTargetNodeType("其他");
            }

            String sourceNodeId = item.getSourceNodeId();
            if (collect2.containsKey(sourceNodeId)) {
                List<SceneSwitchInfoModel> sceneSwitchInfoModels = collect2.get(sourceNodeId);
                Optional<SceneSwitchInfoModel> max = sceneSwitchInfoModels.stream().filter(ii -> ii.getSceneId().equals(id)).max(Comparator.comparing(SceneSwitchInfoModel::getCreateTime));
                var ref = new Object() {
                    String userId = null;
                };
                max.ifPresent(o-> ref.userId = o.getUserId());
                if(userModelMap.containsKey(ref.userId)){
                    UserModel userModel = userModelMap.get(ref.userId);
                    String name = userModel.getName();
                    String username = userModel.getUsername();
                    item.setUserName(username);
                    item.setName(name);
                }
            }
        });

        log.info("--------------------------------6");
        log.info(String.valueOf(System.currentTimeMillis()-startTime));
        startTime = System.currentTimeMillis();

        try {
            return new ListObjServiceResult<>(new ListObjResult<>(subList, sortedFlowAttacks.size()).asResult()).asResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ListObjServiceResult<>(new ListObjResult<>(Collections.emptyList(), sortedFlowAttacks.size()).asResult()).asResult();
        }
    }

    private Set<String> getIpsByNodes(String csceneId, List<String> nodes) {
        Map<String, Set<String>> nodeToIpsMap = getNodeIps(csceneId);
        Set<String> ips = new HashSet<>();
        for (String node : nodes) {
            Set<String> ipsByNode = nodeToIpsMap.get(node);
            if (CollectionUtils.isNotEmpty(ipsByNode)) {
                ips.addAll(ipsByNode);
            }
        }
        return ips;

    }

    Map<String, List<ElementMonitorBasicInfo>> basicInfoElements = new HashMap<>();

    private Map<String, Set<String>> getNodeIps(String csceneId) {
        var request = new SceneAllElementsQueryRequest();
        long t = System.currentTimeMillis();
        List<ElementMonitorBasicInfo> basicInfoList = basicInfoElements.get(csceneId);
        if (basicInfoList == null) {
            basicInfoList = sceneMonitorService.getAllSceneElements(csceneId, false, request);
            basicInfoElements.put(csceneId, basicInfoList);
        }
        System.out.println(System.currentTimeMillis() - t);
        if (Boolean.TRUE.equals(request.getIncludeDeleting())) {
            return Collections.emptyMap();
        }
        List<ElementMonitorBasicInfo> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(basicInfoList)) {
            for (var e : basicInfoList) {
                if (e.getEvent() != null) {
                    if (e.getEvent() != SceneElementModel.Event.DELETE) {
                        result.add(e);
                    } else if (e.getEvent() == SceneElementModel.Event.DELETE && e.getEventStatus() == EventStatus.OVER) {
                        result.add(e);
                    }
                } else {
                    result.add(e);
                }
            }
        }

        try {
            var testRangeResult = testRangeApiProxy.listAllElementBasicInfo(csceneId, request);
            result.addAll(testRangeResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        Map<String, Set<String>> nodesToIpsMap = new HashMap<>();
        for (ElementMonitorBasicInfo elementMonitorBasicInfo : result) {
            String nodeId = elementMonitorBasicInfo.getId();
            var ports = elementMonitorBasicInfo.getPorts();
            if (CollectionUtils.isEmpty(ports)) {
                nodesToIpsMap.put(nodeId, Collections.emptySet());
            } else {
                nodesToIpsMap.put(nodeId, ports.stream().map(ElementPort::getIp).collect(Collectors.toSet()));
            }
        }
        return nodesToIpsMap;

    }


    @ApiOperation(value = "资产统计", notes = "资产统计")
    @GetMapping("/assets_statistics")
    public ServiceResult<List<AssetsStatisticsResponse>> getAssetsStatistics(AssetsStatisticsRequest request) {
        log.info("assets_statistics params: {}", JsonCommon.stringify(request));
        // 转换场景id
        List<String> sceneIds = csceneCommonService.translateSceneIds(request.getSceneIds());
        if (CollectionUtils.isEmpty(sceneIds)) {
            log.error("translateSceneIds is null");
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        List<AssetsStatisticsResponse> responseList = new ArrayList<>();

//        TODO WK2025 手动修改资产统计
        if(request.getSceneIds().get(0).equals(collectionAppConfig.getWkSceneId())){
            sceneIds.forEach(id -> {
                AssetsStatisticsResponse response = AssetsStatisticsResponse.builder()
                        .sceneId(id)
                        .assetsTotal(collectionAppConfig.getWkAssetsTotal())
                        .ipTotal(collectionAppConfig.getWkIpTotal())
                        .operationSystemCount(collectionAppConfig.getWkOperationSystemCount())
                        .applicationSystemCount(collectionAppConfig.getWkApplicationSystemCount())
                        .networkCount(collectionAppConfig.getWkNetworkCount())
                        .safetySystemCount(collectionAppConfig.getWkSafetySystemCount())
                        .build();
                responseList.add(response);
            });
            return ResultCommon.success(responseList);
        }

        List<SceneAssetsBO> sceneAssetsList = scenePluginCollectionSceneService.listSceneAssets(sceneIds).getSource();

        // 统计资产数量
        sceneIds.forEach(id -> {
            SceneAssetsBO sceneAssetsBO = sceneAssetsList.stream().filter(list -> id.equals(list.getSceneId())).findFirst().get();
            List<CategoryGroupBO> categoryGroups = sceneAssetsBO.getCategoryGroups();
            // 操作系统
            List<CategoryGroupBO> osList = categoryGroups.stream().filter(group -> group.getCategory() == 1).collect(Collectors.toList());
            // 应用系统
            List<CategoryGroupBO> asList = categoryGroups.stream().filter(group -> group.getCategory() == 2).collect(Collectors.toList());
            // 网络设备
            List<CategoryGroupBO> networkList = categoryGroups.stream().filter(group -> group.getCategory() == 3).collect(Collectors.toList());
            // 安全设备
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
        // 统计资产数量
        List<SceneConfigCategoryGroupBO> categoryGroups = sceneConfigAsset.getCategoryGroups();
        // 操作系统
        List<SceneConfigCategoryGroupBO> osList = categoryGroups.stream().filter(group -> group.getCategory() == 1).collect(Collectors.toList());
        // 应用系统
        List<SceneConfigCategoryGroupBO> asList = categoryGroups.stream().filter(group -> group.getCategory() == 2).collect(Collectors.toList());
        // 网络设备
        List<SceneConfigCategoryGroupBO> networkList = categoryGroups.stream().filter(group -> group.getCategory() == 3).collect(Collectors.toList());
        // 安全设备
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
