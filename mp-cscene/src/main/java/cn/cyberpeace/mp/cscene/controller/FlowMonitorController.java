package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.controller.request.FlowSimpleReplayRequest;
import cn.cyberpeace.mp.cscene.dto.flow.*;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
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
import cn.cyberpeace.mp.cscene.service.FlowAttackTypeConvert;
import cn.cyberpeace.mp.cscene.service.FlowMonitorService;
import cn.cyberpeace.mp.cscene.service.exportfile.CsvExportFileService;
import cn.cyberpeace.mp.cscene.service.exportfile.PageIterator;
import cn.cyberpeace.mp.cscene.service.exportfile.XlsExportFileService;
import cn.cyberpeace.mp.cscene.service.exportfile.XlsxExportFileService;
import cn.cyberpeace.mp.cscene.vo.FlowGroupByDestinationIpAndSeverityVO;
import cn.cyberpeace.mp.cscene.vo.FlowGroupPortWithProtocolVO;
import cn.cyberpeace.mp.flow.util.SshUtil;
import cn.cyberpeace.mp.frontadmin.service.sys.SysConfigService;
import cn.cyberpeace.mp.frontadmin.util.CompressFile;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincollection.bo.CategoryGroupBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.SceneAssetsBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.SceneConfigAssetBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.SceneConfigCategoryGroupBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneModel;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneMapper;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.util.FlowPcapExportUtil;
import cn.cyberpeace.mp.situationaware.model.FlowClassificationModel;
import cn.cyberpeace.mp.situationaware.service.FlowClassificationsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
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
@AuthTarget(TargetEnum.CSCENE_MONITOR_GROUP)
public class FlowMonitorController {

    public static final Map<String, Queue<Path>> SCENE_TO_FILE_QUEUE_MAP = new ConcurrentHashMap<>();

    public static final DateTimeFormatter BASIC_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());


    private static final Map<String, ReentrantLock> PCAP_REPLAY_LOCK = new ConcurrentHashMap<>();

    private static final String STR_PROTOCOLS = "[\"arp\", \"dns\", \"ftp\", \"ftp-data\", \"http\", \"icmp\", \"ikev2\", \"ip\", \"ipv6\", \"krb5\", \"ldap\", \"nfs\", \"ntp\", \"pkthdr\", \"pop3\", \"rdp\", \"rmi\", \"sftp\", \"smb\", \"smtp\", \"snmp\", \"ssh\", \"syn\", \"tcp\", \"tcp-pkt\", \"tcp-stream\", \"tcpmux\", \"telnet\", \"tls\", \"udp\" , \"dhcpv6\"]";

    @Autowired
    private FlowMonitorService flowMonitorService;

    @Autowired
    private CsceneCommonService csceneCommonService;

    @Autowired
    private FlowClassificationsService flowClassificationsService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private CsceneInstanceService csceneInstanceService;

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

    @ApiOperation(value = "场景实时攻击")
    @GetMapping("/scene_real_time_attack")
    public ServiceResult<Long> getSceneRealTimeAttack(FlowTrendRequest flowTrendRequest) {
        Long count = 0L;
        if (StringUtils.isEmpty(flowTrendRequest.getSceneId())) {
            return ResultCommon.success(count);
        }

        List<FlowTrend> flowTrends;
        if (CollectionUtils.isNotEmpty(flowTrendRequest.getElementIds())) {
            flowTrends = flowMonitorService.getElementFlowTrend(flowTrendRequest);
        } else {
            flowTrends = flowMonitorService.getSceneFlowTrend(flowTrendRequest);
        }
        if (CollectionUtils.isNotEmpty(flowTrends)) {
            long sum = 0L;
            for (FlowTrend flowTrend : flowTrends) {
                long dataSize = flowTrend.getDataSize();
                sum += dataSize;
            }
            count = sum;
        }

        return ResultCommon.success(count);
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

    @AuthTarget(TargetEnum.NO_AUTH)
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
                FlowAttackTypeCount otherCount = new FlowAttackTypeCount();
                otherCount.setAttackType(MessageCommon.get("cscene.flowtype.other"));
                otherCount.setTimes(other);
                result.add(otherCount);
                return ResultCommon.success(result);
            }
        }
        return ResultCommon.success(attackTypeCounts);
    }

    @ApiOperation(value = "攻击流量各类别统计(归14大类)")
    @GetMapping("/attack_type_count_group")
    public ServiceResult<List<FlowAttackTypeCount>> getAttackTypeCountGroup(AttackTypeTopRequest baseRequest) {
        log.debug("attack_type_count_group params: {}", JsonCommon.stringify(baseRequest));
        if (StringUtils.isEmpty(baseRequest.getSceneId())) {
            return ResultCommon.success(new ArrayList<>());
        }

        baseRequest.setAttackTypeI18(false);
        List<FlowAttackTypeCount> attackTypeCounts = flowMonitorService.getAttackTypeCount(baseRequest);
        List<FlowAttackTypeCount> resultCounts = new ArrayList<>();
        Map<String, Integer> resultMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(attackTypeCounts)) {
            for (FlowAttackTypeCount flowAttackTypeCount : attackTypeCounts) {
                String newType = FlowAttackTypeConvert.getAttCk(flowAttackTypeCount.getAttackType());
                if (resultMap.get(newType) == null) {
                    resultMap.put(newType, flowAttackTypeCount.getTimes());
                } else {
                    resultMap.put(newType, resultMap.get(newType) + flowAttackTypeCount.getTimes());
                }
            }
            for (String key:resultMap.keySet()){
                FlowAttackTypeCount flowAttackTypeCount = new FlowAttackTypeCount();
                String key2 = key.replaceAll(" ", "_");
                String attackTypeName = MessageCommon.get("cscene.attck." + key2.toLowerCase());
                flowAttackTypeCount.setAttackType(attackTypeName);
                flowAttackTypeCount.setTimes(resultMap.get(key));
                resultCounts.add(flowAttackTypeCount);
            }
        }
        if (baseRequest.getTopNWithOther() != null) {
            if (resultCounts != null && resultCounts.size() > baseRequest.getTopNWithOther()) {
                Collections.sort(resultCounts, (a1, a2) -> a2.getTimes() - a1.getTimes());
                int other = 0;
                List<FlowAttackTypeCount> result = new ArrayList<>();
                for (int i = 0; i < resultCounts.size(); i++) {
                    if (i < baseRequest.getTopNWithOther()) {
                        result.add(resultCounts.get(i));
                    } else {
                        other += resultCounts.get(i).getTimes();
                    }
                }
                FlowAttackTypeCount otherCount = new FlowAttackTypeCount();
                otherCount.setAttackType(MessageCommon.get("cscene.flowtype.other"));
                otherCount.setTimes(other);
                result.add(otherCount);
                return ResultCommon.success(result);
            }
        }
        return ResultCommon.success(resultCounts);
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
        List<FlowClassificationModel> result = flowClassificationsService.findAll();
        List<FlowAttackConfigInfo.FlowAttackType> list = new ArrayList<>();
        for (FlowClassificationModel r : result) {
            FlowAttackConfigInfo.FlowAttackType flowAttackType = getFlowAttackType(r);
            list.add(flowAttackType);
        }
        FlowAttackConfigInfo info = new FlowAttackConfigInfo();
        info.setTypes(list);
        return new ServiceResult<>(info).asResult();
    }

    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation(value = "查询流量攻击协议列表")
    @GetMapping("/flow_protocols")
    public ServiceResult<FlowProtocolsInfo> getFlowProtocolsInfo() {
        String strProtocols = STR_PROTOCOLS;
        var model = sysConfigService.getByKeyName("cscene_flow_allprotocols");
        if (model != null) {
            strProtocols = model.getValue();
        }
        var protocols = JsonCommon.parseList(strProtocols, String.class);
        List<FlowProtocolsInfo.FlowProtocol> list = new ArrayList<>();
        FlowProtocolsInfo.FlowProtocol v = new FlowProtocolsInfo.FlowProtocol();
        v.setValue(null);
        v.setLabel(MessageCommon.get("cscene.flow.allprotocols"));
        list.add(v);
        for (var p : protocols) {
            v = new FlowProtocolsInfo.FlowProtocol();
            v.setLabel(p);
            v.setValue(p);
            list.add(v);
        }
        FlowProtocolsInfo flowProtocolsInfo = new FlowProtocolsInfo();
        flowProtocolsInfo.setFlowOptions(list);
        return new ServiceResult<>(flowProtocolsInfo).asResult();
    }

    private static FlowAttackConfigInfo.FlowAttackType getFlowAttackType(FlowClassificationModel r) {
        FlowAttackConfigInfo.FlowAttackType type = new FlowAttackConfigInfo.FlowAttackType();
        type.setValue(r.getCategoryEn());
        type.setSeverity(r.getSeverity());
        type.setName(r.getCategoryZh());
        return type;
    }

    @ApiOperation(value = "流量分页接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "场景实例id", dataTypeClass = String.class),})
    @GetMapping("/page")
    public ListObjServiceResult<FlowAttackDto> pageFlow(@RequestParam String id, FlowQueryParams queryParams) {
        log.info("pageFlow ====>> id:{}, queryParams:{}", id, queryParams);
        Integer originPerPage = queryParams.getPerPage();
        if (Objects.isNull(originPerPage)) {
            originPerPage = 10;
        }
        Integer originPage = queryParams.getPage();
        if (Objects.isNull(originPage)) {
            originPage = 1;
        }

        // 在内存按照应用地理位置、应用、vlan过滤
        queryParams.setPerPage(10000);
        queryParams.setPage(1);
        ListObjServiceResult<FlowAttackDto> result = flowMonitorService.pageFlow(id, queryParams);

        if (CollectionUtils.isNotEmpty(result.getData().getList())) {
            List<FlowAttackDto> list = result.getData().getList().stream().peek(flow -> flowMonitorService.addFlowAppInfo(flow))
                    .peek(flow -> flowMonitorService.addFLowGeoInfo(flow))
                    .peek(flow -> flowMonitorService.addFlowMacInfo(flow))
                    .filter(flow -> flowMonitorService.
                    flowFilter(flow, queryParams)).collect(Collectors.toList());

            try {

                int startIndex = (originPage - 1) * originPerPage;
                int endIndex = originPage * originPerPage;
                if (startIndex < list.size()) {
                    endIndex = Math.min(endIndex, list.size());
                }
                return new ListObjServiceResult(new ListObjResult(list.subList(startIndex, endIndex)
                        , list.size()).asResult()).asResult();
            } catch (IndexOutOfBoundsException e) {
                log.error(e.getMessage(), e);
                return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<FlowAttackDto>(), 0).asResult()).asResult();
            }
        } else {
            return result;
        }

    }

    @ApiOperation(value = "流量分页查询的数据的导出")
    @GetMapping("/export_file")
    public void exportPageFlowFile(@RequestParam String id, FlowQueryParams queryParams, @RequestParam("file_type") String fileType, HttpServletResponse response) {
        log.info("download pageFlow ====>> id:{}, queryParams:{}, fileType: {}", id, queryParams, fileType);
        queryParams.setId(id);
        if (StringUtils.isEmpty(fileType)) {
            fileType = "xlsx";
        }
        String filename = "流量分析文件";
        PageIterator<FlowAttackData, FlowQueryParams> iterator =
                new PageIterator<>(queryParams, flowMonitorService::pageFlow) {};

        switch (fileType) {
            case "xlsx":
                XlsxExportFileService service = new XlsxExportFileService();

                service.exportFile(response, iterator, filename);
                break;
            case "xls":
                XlsExportFileService xlsExportFileService = new XlsExportFileService();
                xlsExportFileService.exportFile(response, iterator, filename);
                break;
            case "csv":
                CsvExportFileService csvExportFileService = new CsvExportFileService();
                csvExportFileService.exportFile(response, iterator, filename);
            default:
                XlsxExportFileService service1 = new XlsxExportFileService();
                service1.exportFile(response, iterator, filename);
                break;
        }

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
        List<SceneAssetsBO> sceneAssetsList = scenePluginCollectionSceneService.listSceneAssets(sceneIds).getSource();

        List<AssetsStatisticsResponse> responseList = new ArrayList<>();
        // 统计资产数量
        for (String id : sceneIds) {
            Optional<SceneAssetsBO> found = Optional.empty();
            for (SceneAssetsBO list : sceneAssetsList) {
                if (id.equals(list.getSceneId())) {
                    found = Optional.of(list);
                    break;
                }
            }
            SceneAssetsBO sceneAssetsBO = found.get();
            List<CategoryGroupBO> categoryGroups = sceneAssetsBO.getCategoryGroups();
            // 操作系统
            List<CategoryGroupBO> osList = new ArrayList<>();
            for (CategoryGroupBO groupBO : categoryGroups) {
                if (groupBO.getCategory() == 1) {
                    osList.add(groupBO);
                }
            }
            // 应用系统
            List<CategoryGroupBO> asList = new ArrayList<>();
            for (CategoryGroupBO categoryGroupBO : categoryGroups) {
                if (categoryGroupBO.getCategory() == 2) {
                    asList.add(categoryGroupBO);
                }
            }
            // 网络设备
            List<CategoryGroupBO> networkList = new ArrayList<>();
            for (CategoryGroupBO categoryGroup : categoryGroups) {
                if (categoryGroup.getCategory() == 3) {
                    networkList.add(categoryGroup);
                }
            }
            // 安全设备
            List<CategoryGroupBO> ssList = new ArrayList<>();
            for (CategoryGroupBO group : categoryGroups) {
                if (group.getCategory() == 4) {
                    ssList.add(group);
                }
            }
            AssetsStatisticsResponse response = AssetsStatisticsResponse.builder().sceneId(id).assetsTotal(sceneAssetsBO.getNodeCount()).ipTotal(sceneAssetsBO.getIpCount()).operationSystemCount(CollectionUtils.isNotEmpty(osList) ? osList.get(0).getElements().size() : 0).applicationSystemCount(CollectionUtils.isNotEmpty(asList) ? asList.get(0).getElements().size() : 0).networkCount(CollectionUtils.isNotEmpty(networkList) ? networkList.get(0).getElements().size() : 0).safetySystemCount(CollectionUtils.isNotEmpty(ssList) ? ssList.get(0).getElements().size() : 0).build();
            responseList.add(response);
        }

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
        List<SceneConfigCategoryGroupBO> osList = new ArrayList<>();
        for (SceneConfigCategoryGroupBO configCategoryGroupBO : categoryGroups) {
            if (configCategoryGroupBO.getCategory() == 1) {
                osList.add(configCategoryGroupBO);
            }
        }
        // 应用系统
        List<SceneConfigCategoryGroupBO> asList = new ArrayList<>();
        for (SceneConfigCategoryGroupBO sceneConfigCategoryGroupBO : categoryGroups) {
            if (sceneConfigCategoryGroupBO.getCategory() == 2) {
                asList.add(sceneConfigCategoryGroupBO);
            }
        }
        // 网络设备
        List<SceneConfigCategoryGroupBO> networkList = new ArrayList<>();
        for (SceneConfigCategoryGroupBO categoryGroup : categoryGroups) {
            if (categoryGroup.getCategory() == 3) {
                networkList.add(categoryGroup);
            }
        }
        // 安全设备
        List<SceneConfigCategoryGroupBO> ssList = new ArrayList<>();
        for (SceneConfigCategoryGroupBO group : categoryGroups) {
            if (group.getCategory() == 4) {
                ssList.add(group);
            }
        }
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

    @ApiOperation(value = "指定pcap重放")
    @PostMapping("/pcap/replay")
    public ServiceResult<String> replayPcap(@Validated @RequestBody FlowSimpleReplayRequest flowSimpleReplayRequest) {
        var lock = PCAP_REPLAY_LOCK.putIfAbsent(flowSimpleReplayRequest.getPcapId(), new ReentrantLock());
        if (Objects.isNull(lock)) {
            lock = PCAP_REPLAY_LOCK.get(flowSimpleReplayRequest.getPcapId());
        }
        synchronized (lock) {
            if (lock.isLocked()) {
                throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("当前流量正在执行重放中，请等待流量重放完毕再重试"));
            }
            lock.lock();
        }

        try {
            String sceneId = csceneInstanceService.getSceneId(flowSimpleReplayRequest.getCsceneId());
            CollectionPluginSceneMapper sceneMapper = BeanCommon.getBean(CollectionPluginSceneMapper.class);
            var scene = sceneMapper.first(new HumanCondition().filterEqual(CollectionPluginSceneModel::getScene, sceneId));
            if (scene == null) {
                throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
            }
            String arkimeName = scene.getFlowCollector();
            Container container = new Container();
            var c = container.getContainerByName(arkimeName);
            String ip = new ArrayList<>(c.getAddresses().values()).get(0).get(0).getAddr();
            String hostname = c.getHostname();
            String downloadUrl = "http://" + ip + ":8005/" + hostname + "/pcap/" + flowSimpleReplayRequest.getPcapId() + ".pcap";
            String trexIp = flowMonitorService.getTrexIp(sceneId);

            String result = SshUtil.executeCommandWithRetrunAndRetry(new SshUtil.ServerInfo(trexIp, "root", "111111"), "bash /root/tcpreplay/tcpreplay_start.sh " + downloadUrl + " " + flowSimpleReplayRequest.getTargetIp() + " " + SysContextHolder.getCallChainId());
            return new ServiceResult<>(result).asResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("重放流量发送异常: " + e.getMessage()));
        } finally {
            lock.unlock();
        }
    }



    @ApiOperation(value = "指定pcap下载")
    @GetMapping("/download_pcap/by_id")
    public ResponseEntity<Resource> exportPcap(HttpServletResponse httpServletResponse, @RequestParam("cscene_id") String csceneId,
                                               @RequestParam("pcap_id") String pcapId) {
        if (Objects.isNull(pcapId)) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("传入的pcapId为空"));
        }
        String sceneId = csceneInstanceService.getSceneId(csceneId);
        CollectionPluginSceneMapper sceneMapper = BeanCommon.getBean(CollectionPluginSceneMapper.class);
        var scene = sceneMapper.first(new HumanCondition().filterEqual(CollectionPluginSceneModel::getScene, sceneId));
        if (scene == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        String arkimeName = scene.getFlowCollector();
        Container container = new Container();
        var c = container.getContainerByName(arkimeName);
        String ip = new ArrayList<>(c.getAddresses().values()).get(0).get(0).getAddr();
        String hostname = c.getHostname();
        String downloadUrl = "http://${ip}:8005/${hostname}/pcap/${pcapId}.pcap";
        downloadUrl = downloadUrl.replace("${ip}", ip);
        downloadUrl = downloadUrl.replace("${hostname}", hostname);
        downloadUrl = downloadUrl.replace("${pcapId}", pcapId);
        File tmpFile = new File(TextUtils.uuid());
        File tempCompressFile = null;
        try {
            var httpResponse = HttpUtils.sendGetDownLoadWithDigestAuth(downloadUrl, "admin", "admin");
            HttpHeaders httpHeaders = new HttpHeaders();
            var output = new FileOutputStream(tmpFile);
            var input = httpResponse.getEntity().getContent();
            IOUtils.copy(input, output);

            tempCompressFile = CompressFile.compressFile(tmpFile);

            var resource = new InputStreamResource(new FileInputStream(tempCompressFile));
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + tempCompressFile.getName());
            return ResponseEntity.ok().headers(httpHeaders)
                    .contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
            if (tempCompressFile != null && tempCompressFile.exists()) {
                tempCompressFile.delete();
            }
        }
    }

    @ApiOperation(value = "流量归档页面分页查询接口")
    @GetMapping("/persistence/page")
    public ListObjServiceResult<FlowPersistenceFileDto> pagePersistenceFlowFile(@RequestParam String id, FlowQueryParams queryParams) {
        queryParams.ensure();
        CsceneInstanceMapper csceneInstanceMapper = BeanCommon.getBean(CsceneInstanceMapper.class);
        List<CsceneInstanceModel> csceneInstances = csceneInstanceMapper.list(new HumanCondition().filterEqual(CsceneInstanceModel::getId, id));
        if (CollectionUtils.isEmpty(csceneInstances)) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("当前场景实例不存在"));
        }
        String scene = csceneInstances.get(0).getScene();

        var queue = SCENE_TO_FILE_QUEUE_MAP.get(scene);
        if (CollectionUtils.isEmpty(queue)) {
            return new ListObjServiceResult(new ListObjResult(Collections.emptyList(), 0).asResult()).asResult();
        } else {
            ArrayList<Path> paths = new ArrayList<>(queue);
            int originPage = queryParams.getPage();
            int originPerPage = queryParams.getPerPage();
            List<Path> pagePaths = new ArrayList<>();
            try {
                int startIndex = (originPage - 1) * originPerPage;
                int endIndex = originPage * originPerPage;
                if (startIndex < paths.size()) {
                    endIndex = Math.min(endIndex, paths.size());
                }
                pagePaths = paths.subList(startIndex, endIndex);
            } catch (IndexOutOfBoundsException e) {
                pagePaths = new ArrayList<>();
            }

            List<FlowPersistenceFileDto> flowPersistenceFiles = new ArrayList<>();
            for (Path pagePath : pagePaths) {
                String fileName = pagePath.getFileName().toString();
                FlowPersistenceFileDto flowPersistenceFileDto = new FlowPersistenceFileDto();
                flowPersistenceFileDto.setFileName(fileName);
                try {
                    BasicFileAttributes basicAttributes = Files.readAttributes(pagePath, BasicFileAttributes.class);
                    String fileSize = basicAttributes.size() / 1024 + "KB";
                    String datetime = BASIC_DATETIME_FORMATTER.format(basicAttributes.lastModifiedTime().toInstant());

                    flowPersistenceFileDto.setFileSize(fileSize);
                    flowPersistenceFileDto.setModifyTime(datetime);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                flowPersistenceFiles.add(flowPersistenceFileDto);
            }

            return new ListObjServiceResult(new ListObjResult(flowPersistenceFiles, paths.size()).asResult()).asResult();
        }

    }


    @ApiOperation(value = "流量归档页面分页查询接口")
    @GetMapping("/persistence/download")
    public ResponseEntity<Resource> exportPersistenceFile(HttpServletResponse httpServletResponse, @RequestParam("cscene_id") String csceneId,
                                               @RequestParam("file_name") String fileName) {
        CsceneInstanceMapper csceneInstanceMapper = BeanCommon.getBean(CsceneInstanceMapper.class);
        List<CsceneInstanceModel> csceneInstances = csceneInstanceMapper.list(new HumanCondition().filterEqual(CsceneInstanceModel::getId, csceneId));
        if (CollectionUtils.isEmpty(csceneInstances)) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("当前场景实例不存在"));
        }
        String scene = csceneInstances.get(0).getScene();

        Path filepath = Path.of("/root/.mp/files", scene, fileName);

        File compressFile = CompressFile.compressFile(filepath.toFile());
        try {
            var resource = new InputStreamResource(new FileInputStream(compressFile));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + compressFile.getName());
            return ResponseEntity.ok().headers(httpHeaders)
                    .contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("文件下载失败"));
        }
    }
}
