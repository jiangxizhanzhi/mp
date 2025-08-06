package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.bo.IntervalSizeBO;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
import cn.cyberpeace.mp.base.util.mptool.NumberUtil;
import cn.cyberpeace.mp.base.util.mptool.URLUtil;
import cn.cyberpeace.mp.common.config.CommonAppConfig;
import cn.cyberpeace.mp.cscene.bo.CsceneCollectionFlowBO;
import cn.cyberpeace.mp.cscene.controller.request.SceneAllElementsQueryRequest;
import cn.cyberpeace.mp.cscene.dto.common.FlowApp;
import cn.cyberpeace.mp.cscene.dto.element.ConnectionState;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.flow.*;
import cn.cyberpeace.mp.cscene.request.common.AttackCountRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseTimeRangeRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.flow.AttackTypeTopRequest;
import cn.cyberpeace.mp.cscene.request.flow.FlowCommonRequest;
import cn.cyberpeace.mp.cscene.request.flow.FlowSceneSimulationRequest;
import cn.cyberpeace.mp.cscene.request.flow.FlowTrendRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.cscene.util.SegmentCount2IntervalSecondsUtil;
import cn.cyberpeace.mp.cscene.vo.FlowGroupByDestinationIpAndSeverityVO;
import cn.cyberpeace.mp.cscene.vo.FlowGroupPortWithProtocolVO;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincollection.bo.AttackInfo;
import cn.cyberpeace.mp.sceneplugincollection.bo.FlowAttackCountBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.FlowGroupByDestinationIpAndSeverityBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.FlowGroupPortWithProtocolBO;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowGroupByDestinationPortParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowGroupCountParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowSimulationParams;
import cn.cyberpeace.mp.sceneplugincollection.util.FlowPcapExportUtil;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneModel;
import cn.cyberpeace.mp.scenepluginflow.repository.FlowPluginSceneMapper;
import cn.cyberpeace.mp.situationaware.bo.AttackWayAggregationBO;
import cn.cyberpeace.mp.situationaware.bo.TeamAttackBO;
import cn.cyberpeace.mp.situationaware.model.enums.SeverityEnum;
import cn.cyberpeace.mp.situationaware.request.FlowAnalysisRequest;
import cn.cyberpeace.mp.situationaware.service.FlowAnalysisService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/30 13:41
 */
@Slf4j
@Service
public class FlowMonitorService {


    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private CsceneCommonService csceneCommonService;

    @Autowired
    private FlowAnalysisService flowAnalysisService;

    @Autowired
    private SceneMonitorService sceneMonitorService;

    @Autowired
    private SceneElementMonitorService elementMonitorService;

    @Autowired
    private CsceneFileService csceneFileService;

    @Autowired
    private JsonCommon jsonCommon;

    private static final String FLOW_RESOURCE_PATH = "/root/.mp/resources/flows";
    private static final String FLOW_APP_FILE = "flow_app.json";

    private static volatile Map<Integer, String> PORT_TO_PROTOCOL = new HashMap<>();
    private static volatile long FLOW_APP_LAST_MODIFY = 0L;

    private static final ScheduledThreadPoolExecutor FLOW_APP_INFO_EXECUTOR = new ScheduledThreadPoolExecutor(1);

    static {
        FLOW_APP_INFO_EXECUTOR.scheduleWithFixedDelay(() -> {
            if (!Files.exists(Path.of(FLOW_RESOURCE_PATH, FLOW_APP_FILE))) {
                ClassPathResource resource = new ClassPathResource("flow_app.json");
                try {
                    Files.copy(resource.getInputStream(), Path.of(FLOW_RESOURCE_PATH, FLOW_APP_FILE));
                } catch (Exception e) {
                    log.error("copy flow_app.json error", e);
                }
            }

            File flowAppFile = new File(FLOW_RESOURCE_PATH, FLOW_APP_FILE);
            if (FLOW_APP_LAST_MODIFY < flowAppFile.lastModified()) {
                FLOW_APP_LAST_MODIFY = flowAppFile.lastModified();
                try {
                    String content = Files.readString(Path.of(FLOW_RESOURCE_PATH, FLOW_APP_FILE));
                    List<FlowApp> flowApps = JsonCommon.parse(content, new TypeReference<>() {
                    });

                    PORT_TO_PROTOCOL = flowApps.stream().collect(Collectors.toMap(FlowApp::getPort, FlowApp::getName, (a, b) -> a));
                } catch (IOException e) {
                    log.error("read flow_app.json error", e);
                }
            }
        }, 5L, 10L, TimeUnit.SECONDS);
    }

    @Cacheable("trex_ip_by_scene_instance_id")
    public String getTrexIp(String sceneInstanceId) {
        FlowPluginSceneMapper flowPluginSceneMapper = BeanCommon.getBean(FlowPluginSceneMapper.class);
        var flowScene = flowPluginSceneMapper.first(new HumanCondition().filterEqual(FlowPluginSceneModel::getScene, sceneInstanceId));
        if (Objects.isNull(flowScene)) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("当前场景不存在流量执行器"));
        }
        var flowActuatorMapper = BeanCommon.getBean(FlowActuatorMapper.class);
        var flowActuator = flowActuatorMapper.first(new HumanCondition().filterEqual(FlowActuatorModel::getId, flowScene.getExecutor()));
        if (Objects.isNull(flowActuator)) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("无法获取当前场景的流量执行器"));
        }
        String[] ipAndPort = flowActuator.getIp().split(":");
        return ipAndPort[0];
    }


    /**
     * 查询单个节点机器流量趋势
     *
     * @param trendRequest
     * @return
     */
    public List<FlowTrend> getElementFlowTrend(FlowTrendRequest trendRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(trendRequest);
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());
        List<String> nodeIds = CollectionUtil.toList(trendRequest.getElementIds().get(0));

        return processFlowTrend(sceneIds, nodeIds, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount(), null, trendRequest.getType());
    }


    /**
     * 查询场景机器流量趋势
     *
     * @param trendRequest
     * @return
     */
    public List<FlowTrend> getSceneFlowTrend(FlowTrendRequest trendRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneId(trendRequest);
        // 逗号分割
        List<String> sceneIds = CollectionUtil.toList(StringUtils.split(trendRequest.getSceneId(), ","));

        return processFlowTrend(sceneIds, null, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount(), trendRequest.getSourceIps(), trendRequest.getType());
    }

    /**
     * 模拟流量
     *
     * @param request
     * @return
     */
    public ServiceResult simulation(FlowSceneSimulationRequest request) {
        // 逗号分割
        List<String> sceneIds = CollectionUtil.toList(StringUtils.split(request.getSceneId(), ","));
        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);

        FlowSimulationParams params = new FlowSimulationParams();
        params.setSceneIds(sceneIdList);
        params.setSourceIp(request.getSourceIp());
        params.setSourceMacs(request.getSourceMacs());
        params.setDestinationIp(request.getDestinationIp());
        params.setDestinationMacs(request.getDestinationMacs());
        params.setNodeIds(request.getNodeIds());
        params.setNetworkBytes(request.getNetworkBytes());
        params.setDateTime(request.getDateTime());
        params.setShuffleCategory(request.getShuffleCategory());
        scenePluginCollectionSceneService.simulation(params);

        return ResultCommon.success();
    }

    /**
     * 统计受攻击端口信息
     *
     * @param request
     * @return
     */
    public List<FlowGroupPortWithProtocolVO> groupByDestinationPortWithProtocol(FlowCommonRequest request) {
        BaseRequestUtil.checkBaseRequestWithSceneId(request);
        // 逗号分割
        List<String> sceneIds = CollectionUtil.toList(StringUtils.split(request.getSceneId(), ","));
        LocalDateTime endTime = request.getEndTime();
        LocalDateTime startTime = request.getStartTime();

        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);

        FlowGroupByDestinationPortParams params = new FlowGroupByDestinationPortParams();
        params.setStartTime(startTime);
        params.setEndTime(endTime);
        params.setSourceIps(request.getSourceIps());
        params.setSceneIds(sceneIdList);
        params.setTopSize(request.getTopSize());

        ServiceResult<List<FlowGroupPortWithProtocolBO>> serviceResult = scenePluginCollectionSceneService.groupByDestinationPortWithProtocol(params);

        List<FlowGroupPortWithProtocolBO> boList = serviceResult.getSource();

        if (CollectionUtils.isEmpty(boList)) {
            return Lists.newArrayList();
        }
        List<FlowGroupPortWithProtocolVO> list = new ArrayList<>();
        for (FlowGroupPortWithProtocolBO v : boList) {
            FlowGroupPortWithProtocolVO flowGroupPortWithProtocolVO = getFlowGroupPortWithProtocolVO(v);
            list.add(flowGroupPortWithProtocolVO);
        }
        return list;
    }

    private static FlowGroupPortWithProtocolVO getFlowGroupPortWithProtocolVO(FlowGroupPortWithProtocolBO v) {
        FlowGroupPortWithProtocolVO vo = new FlowGroupPortWithProtocolVO();
        vo.setProtocol(v.getProtocol());
        vo.setPort(v.getPort());
        vo.setAttackNumber(v.getAttackNumber());
        vo.setTrafficSize(v.getTrafficSize());
        return vo;
    }


    /**
     * 统计主机状态
     *
     * @param request
     * @return
     */
    public List<FlowGroupByDestinationIpAndSeverityVO> groupByDestinationIpAndSeverity(FlowCommonRequest request) {
        BaseRequestUtil.checkBaseRequestWithSceneId(request);
        // 逗号分割
        List<String> sceneIds = CollectionUtil.toList(StringUtils.split(request.getSceneId(), ","));

        LocalDateTime endTime = request.getEndTime();

        LocalDateTime startTime = request.getStartTime();

        // todo 转好
        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);

        FlowGroupByDestinationPortParams params = new FlowGroupByDestinationPortParams();
        params.setStartTime(startTime);
        params.setEndTime(endTime);
        params.setSourceIps(request.getSourceIps());
        params.setSceneIds(sceneIdList);
        params.setTopSize(request.getTopSize());

        ServiceResult<List<FlowGroupByDestinationIpAndSeverityBO>> serviceResult = scenePluginCollectionSceneService.groupByDestinationIpAndSeverity(params);

        List<FlowGroupByDestinationIpAndSeverityBO> boList = serviceResult.getSource();

        if (CollectionUtils.isEmpty(boList)) {
            boList = Lists.newArrayList();
        }

        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIdList, request.getElementIds());
        // 合并同一节点的多IP数据
        List<FlowGroupByDestinationIpAndSeverityVO> voList = this.convertSeverityList(boList, batchSceneHandler);

        int allNodeSize = batchSceneHandler.getAllEid2ElementMap().keySet().size();
        boolean seen = false;
        Integer acc = null;
        for (FlowGroupByDestinationIpAndSeverityVO groupByDestinationIpAndSeverityVO : voList) {
            Integer num = groupByDestinationIpAndSeverityVO.getNum();
            if (!seen) {
                seen = true;
                acc = num;
            } else {
                acc = acc + num;
            }
        }
        int severitySize = seen ? acc : 0;
        int normalSize = allNodeSize - severitySize;
        voList.add(new FlowGroupByDestinationIpAndSeverityVO(0, normalSize));
        List<FlowGroupByDestinationIpAndSeverityVO> list = new ArrayList<>();
        for (FlowGroupByDestinationIpAndSeverityVO flowGroupByDestinationIpAndSeverityVO : voList) {
            list.add(flowGroupByDestinationIpAndSeverityVO);
        }
        list.sort(Comparator.comparing(FlowGroupByDestinationIpAndSeverityVO::getSeverity).reversed());
        return list;
    }

    /**
     * 合并同一节点的多IP数据
     *
     * @param boList
     * @param batchSceneHandler
     * @return
     */
    private List<FlowGroupByDestinationIpAndSeverityVO> convertSeverityList(List<FlowGroupByDestinationIpAndSeverityBO> boList, BatchSceneHandler batchSceneHandler) {
        // 各节点风险等级map
        Map<String, Integer> severityMap = new HashMap<>();
        // 将ip转换成节点，整合风险等级，合并一个节点有多ip的数据
        for (FlowGroupByDestinationIpAndSeverityBO flowGroupByDestinationIpAndSeverityBO : boList) {
            List<String> destinationIpList = flowGroupByDestinationIpAndSeverityBO.getDestinationIpList();
            for (String ip : destinationIpList) {
                SceneElementBO nodeByIpAddr = batchSceneHandler.getNodeByIpAddr(ip);
                // map中是否包含当前节点的风险等级
                if (severityMap.containsKey(nodeByIpAddr.getId())) {
                    // 已包含，只记录较高的风险等级
                    if (severityMap.get(nodeByIpAddr.getId()) < flowGroupByDestinationIpAndSeverityBO.getSeverity()) {
                        severityMap.put(nodeByIpAddr.getId(), flowGroupByDestinationIpAndSeverityBO.getSeverity());
                    }
                } else {
                    severityMap.put(nodeByIpAddr.getId(), flowGroupByDestinationIpAndSeverityBO.getSeverity());
                }
            }
        }

        // 统计不同风险等级节点数量
        Map<Integer, Integer> severityNumMap = new HashMap<>();
        for (Map.Entry<String, Integer> e : severityMap.entrySet()) {
            String k = e.getKey();
            Integer v = e.getValue();
            if (severityNumMap.containsKey(v)) {
                severityNumMap.put(v, severityNumMap.get(v) + 1);
            } else {
                severityNumMap.put(v, 1);
            }
        }

        // 补充风险数量为0的数据
        if (boList.size() > severityNumMap.size()) {
            for (FlowGroupByDestinationIpAndSeverityBO bo : boList) {
                if (!severityNumMap.containsKey(bo.getSeverity())) {
                    severityNumMap.put(bo.getSeverity(), 0);
                }
            }
        }

        List<FlowGroupByDestinationIpAndSeverityVO> voList = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : severityNumMap.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            FlowGroupByDestinationIpAndSeverityVO severityVO = new FlowGroupByDestinationIpAndSeverityVO();
            severityVO.setSeverity(key);
            severityVO.setNum(value);
            voList.add(severityVO);
        }

        return voList;
    }


    public FlowRiskCount getFlowRiskCount(BaseSceneElementRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);

        BaseRequestUtil.checkBaseRequestWithSceneId(baseRequest);

        List<String> sceneIds = CollectionUtil.toList(baseRequest.getSceneId());

        List<String> nodeIds = null;
        if (!CollectionUtil.isEmpty(baseRequest.getElementIds())) {
            nodeIds = CollectionUtil.toList(baseRequest.getElementIds().get(0));
        }

        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);

        ServiceResult<Map<String, Integer>> mapResult = scenePluginCollectionSceneService.groupCountFlowByLevel(sceneIdList, nodeIds, null, null);
        Map<String, Integer> map = mapResult.getSource();

        // 风险等级 1,2,3,4 数字越低，级别越高
        FlowRiskCount riskCount = new FlowRiskCount();
        if (Objects.nonNull(map)) {
            riskCount.setHighCount(map.get("1") == null ? 0 : map.get("1"));
            riskCount.setMediumCount(map.get("2") == null ? 0 : map.get("2"));
            Integer low1 = map.get("3") == null ? 0 : map.get("3");
            Integer low2 = map.get("4") == null ? 0 : map.get("4");
            riskCount.setLowCount(low1 + low2);
        }
        return riskCount;
    }

    public List<FlowAttackTypeCount> getAttackTypeCount(AttackTypeTopRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);

        BaseRequestUtil.checkBaseRequestWithSceneId(baseRequest);

        List<String> sceneIds = Arrays.asList(StringUtils.split(baseRequest.getSceneId(), ","));

        List<String> nodeIds = null;
        if (!CollectionUtil.isEmpty(baseRequest.getElementIds())) {
            nodeIds = CollectionUtil.toList(baseRequest.getElementIds().toArray(new String[]{}));
        }

        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);

        ServiceResult<Map<String, Integer>> mapResult = scenePluginCollectionSceneService.groupCountFlowByType(sceneIdList, nodeIds, null, null, baseRequest.getSourceIps(), baseRequest.getTopSize(), baseRequest.isAttackTypeI18());
        Map<String, Integer> map = mapResult.getSource();
        List<FlowAttackTypeCount> result = new ArrayList<>();
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String type = entry.getKey();
                Integer num = entry.getValue();
                result.add(new FlowAttackTypeCount(type, num));
            }
            Collections.sort(result, (c1, c2) -> c2.getTimes() - c1.getTimes());
        }
        return result;
    }

    public List<FlowAttackTime> getAttackTimeCountNoName(AttackCountRequest baseRequest) {
        log.info("baseRequest:{}", JsonCommon.stringify(baseRequest));
        BaseRequestUtil.checkBaseRequestWithSceneId(baseRequest);

        List<String> sceneIds = Arrays.asList(StringUtils.split(baseRequest.getSceneId(), ","));
        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);
        ServiceResult<Map<String, Integer>> mapResult = scenePluginCollectionSceneService.groupCountFlowWithTopSize(sceneIdList, baseRequest.getIps(), null, null);
        Map<String, Integer> map = mapResult.getSource();
        List<FlowAttackTime> result = new ArrayList<>();
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String type = entry.getKey();
                Integer num = entry.getValue();
                result.add(FlowAttackTime.builder()
                        .ip(type)
                        .times(num)
                        .build());
            }
            Collections.sort(result, (c1, c2) -> c2.getTimes() - c1.getTimes());
        }
        return result;
    }

    public List<FlowAttackTime> getAttackIpCount(AttackCountRequest baseRequest) {
        log.info("baseRequest:{}", JsonCommon.stringify(baseRequest));
        BaseRequestUtil.checkBaseRequestWithSceneId(baseRequest);

        List<String> sceneIds = Arrays.asList(StringUtils.split(baseRequest.getSceneId(), ","));
        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);
        String groupField = baseRequest.getIpType() == 0 ?
                ScenePluginCollectionSceneService.GroupCountFlowField.SOURCE_IP :
                ScenePluginCollectionSceneService.GroupCountFlowField.DEST_IP;
        FlowGroupCountParams params = new FlowGroupCountParams(groupField);
        params.setQueryAddress(baseRequest.getIps());
        params.setStartTime(baseRequest.getStartTime());
        params.setEndTime(baseRequest.getEndTime());
        params.setTopSize(baseRequest.getTopSize());
        params.setSourceIps(baseRequest.getSourceIps());
        List<FlowAttackCountBO> result = scenePluginCollectionSceneService.groupCountFlowAndNodeInfo(sceneIdList, params).getSource();
        List<FlowAttackTime> list = new ArrayList<>();
        for (FlowAttackCountBO bo : result) {
            FlowAttackTime build = FlowAttackTime.builder()
                    .times(bo.getTimes())
                    .nodeName(bo.getNodeName())
                    .ip(bo.getIp())
                    .build();
            list.add(build);
        }
        return list;
    }

    public TotalFlowAttackRatio getAttackRatioCount(AttackCountRequest baseRequest) {
        log.info("getAttackRatioCount baseRequest:{}", JsonCommon.stringify(baseRequest));
        BaseRequestUtil.checkBaseRequestWithSceneId(baseRequest);

        List<String> sceneIds = Arrays.asList(StringUtils.split(baseRequest.getSceneId(), ","));
        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);
        TeamAttackBO countResult = scenePluginCollectionSceneService.groupCountFlow(sceneIdList, baseRequest.getIps(), baseRequest.getTopSize(), baseRequest.getStartTime(), baseRequest.getEndTime()).getSource();
        TotalFlowAttackRatio totalFlowAttackRatio = new TotalFlowAttackRatio();
        totalFlowAttackRatio.setTotalAttackNumber(countResult.getAttackNumber());
        totalFlowAttackRatio.setTotalTrafficSize(countResult.getTrafficSize());
        totalFlowAttackRatio.setAttackWayList(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(countResult.getAttackWayList())) {
            for (AttackWayAggregationBO v : countResult.getAttackWayList()) {
                totalFlowAttackRatio.getAttackWayList()
                        .add(FlowAttackRatio.builder()
                                .attackTrafficPercent(v.getAttackNumberPercent().doubleValue())
                                .type(v.getAggregationType())
                                .build());
            }
        }
        return totalFlowAttackRatio;
    }


    public ListObjServiceResult<FlowAttackData> pageFlow(FlowQueryParams queryParams) {
        ListObjServiceResult<FlowAttackDto> result = pageFlow(queryParams.getId(), queryParams);
        var data =  result.getData().getList().stream().map(FlowAttackData::new).collect(Collectors.toList());
        return new ListObjServiceResult(new ListObjResult(data, result.getData().getTotal()).asResult()).asResult();
    }



    public void addFlowAppInfo(FlowAttackDto flowAttackDto) {
        try {
            int targetPort = Integer.parseInt(flowAttackDto.getTargetPort());

            String app = PORT_TO_PROTOCOL.get(targetPort);
            if (StringUtils.isNotEmpty(app)) {
                flowAttackDto.setApp(app);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    public void addFLowGeoInfo(FlowAttackDto flowAttackDto) {
        SceneElementMapper sceneElementMapper = BeanCommon.getBean(SceneElementMapper.class);
        List<SceneElementModel> sceneElementModels = sceneElementMapper.list(new HumanCondition().filterIn(SceneElementModel::getNodeId,
                new String[]{flowAttackDto.getSourceNodeId(), flowAttackDto.getTargetNodeId()}));
        if (CollectionUtils.isEmpty(sceneElementModels)) {
            return;
        } else {
            Set<String> geos = sceneElementModels.stream().map(SceneElementModel::getGeo).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
            flowAttackDto.setGeo(String.join(",", geos));
        }
    }


    public boolean flowFilter(FlowAttackDto flowAttackDto, FlowQueryParams queryParams) {
        if (StringUtils.isNotEmpty(flowAttackDto.getApp()) && StringUtils.isNotEmpty(queryParams.getApp())) {
            if (!flowAttackDto.getApp().contains(queryParams.getApp())) {
                return false;
            }
        }

        if (StringUtils.isNotEmpty(flowAttackDto.getGeo()) && StringUtils.isNotEmpty(queryParams.getGeo())) {
            if (!flowAttackDto.getGeo().contains(queryParams.getGeo())) {
                return false;
            }
        }

        if (StringUtils.isNotEmpty(flowAttackDto.getVlan()) && StringUtils.isNotEmpty(queryParams.getVlan())) {
            if (!flowAttackDto.getVlan().contains(queryParams.getVlan())) {
                return false;
            }
        }

        if (CollectionUtils.isEmpty(flowAttackDto.getSourceMac())) {
            flowAttackDto.setSourceMac(new ArrayList<>());
        }

        if (CollectionUtils.isEmpty(flowAttackDto.getTargetMac())) {
            flowAttackDto.setTargetMac(new ArrayList<>());
        }

        if (StringUtils.isNotEmpty(queryParams.getMacAddr())) {
            if (!flowAttackDto.getSourceMac().contains(queryParams.getMacAddr()) && !flowAttackDto.getTargetMac().contains(queryParams.getMacAddr())) {
                return false;
            }
        }
        return true;

    }

    /**
     * 流量分页接口包装
     * 根据攻击类型拆分
     *
     * @param id
     * @param queryParams
     * @return
     */
    public ListObjServiceResult<FlowAttackDto> pageFlow(String id, FlowQueryParams queryParams) {
        if (Objects.isNull(queryParams.getPage())) {
            queryParams.setPage(1);
        }
        if (Objects.isNull(queryParams.getPerPage())) {
            queryParams.setPerPage(100);
        }
        queryParams.ensure();

        ListObjServiceResult<CsceneCollectionFlowBO> flowBOResult = csceneInstanceService.listFlows(id, queryParams);
        List<CsceneCollectionFlowBO> flowBOS = flowBOResult.getSource().getObjs();

        List<FlowAttackDto> flowAttackDtos = new ArrayList<>();

        for (CsceneCollectionFlowBO flowBO : flowBOS) {
            if (Objects.nonNull(flowBO.getAttackInfos())) {
                for (AttackInfo attackInfo : flowBO.getAttackInfos()) {
                    FlowAttackDto flowAttackDto = new FlowAttackDto();
                    BeanUtils.copyProperties(flowBO, flowAttackDto);
                    flowAttackDto.setAttackInfo(attackInfo);
                    flowAttackDtos.add(flowAttackDto);
                }
            } else {
                // 没有攻击类型标签，全部设置为默认的低风险
                FlowAttackDto flowAttackDto = new FlowAttackDto();
                BeanUtils.copyProperties(flowBO, flowAttackDto);
//                flowAttackDto.setAttackInfo(new AttackInfo(4, "Abnormal links"));
                flowAttackDtos.add(flowAttackDto);
            }
        }

        List<FlowAttackDto> finalList = flowAttackDtos;
        if (flowAttackDtos.size() > queryParams.getPageSize()) {
            finalList = flowAttackDtos.subList(0, queryParams.getPageSize());
        }

        return new ListObjServiceResult(new ListObjResult(finalList, flowBOResult.getSource().getTotal()).asResult()).asResult();
    }


    private List<FlowTrend> processFlowTrend(List<String> sceneIds, List<String> elementIds, LocalDateTime startTime, LocalDateTime endTime, Integer segmentCount, List<String> sourceIps, Integer type) {
        // 默认值
        if (Objects.isNull(endTime)) {
            endTime = LocalDateTime.now();
        }

        if (Objects.isNull(startTime)) {
            // todo 默认时间间隔8小时
            startTime = endTime.minusHours(4);
        }

        if (Objects.isNull(segmentCount)) {
            segmentCount = 40;
        }

        // todo 转好
        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);
        Long intervalSeconds = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(startTime, endTime, segmentCount);

        ServiceResult<List<IntervalSizeBO<LocalDateTime, Long>>> flowResult = scenePluginCollectionSceneService.timeIntervalSumFlowSize(sceneIdList, elementIds, startTime, endTime, intervalSeconds.intValue(), sourceIps, type);

        List<IntervalSizeBO<LocalDateTime, Long>> intervalFlowSizeBOs = flowResult.getSource();
        List<FlowTrend> flowTrends = new ArrayList<>();
        if (Objects.isNull(intervalFlowSizeBOs)) {
            return flowTrends;
        }

        List<LocalDateTime> timeList = SegmentCount2IntervalSecondsUtil.generateTimeList(startTime, endTime, intervalSeconds);
        Map<LocalDateTime, FlowTrend> flowTrendMap = new HashMap<>();
        for (LocalDateTime time : timeList) {
            FlowTrend flowTrend = new FlowTrend();
            // 初始化
            flowTrend.setTime(time);
            flowTrend.setDataSize(0L);
            flowTrendMap.put(time, flowTrend);
        }

        // flow
        for (IntervalSizeBO<LocalDateTime, Long> bo : intervalFlowSizeBOs) {
            if (Objects.nonNull(bo.getStart())) {
                FlowTrend flowTrend = getClosest(bo.getStart(), flowTrendMap);
                if (Objects.nonNull(flowTrend)) {
                    flowTrend.setDataSize(bo.getSize());
                }
            }
        }

        for (Map.Entry<LocalDateTime, FlowTrend> entry : flowTrendMap.entrySet()) {
            flowTrends.add(entry.getValue());
        }
        flowTrends.sort(Comparator.comparing(FlowTrend::getTime));

        return flowTrends;
    }

    /**
     * @param timeRangeRequest
     */
    public FlowAnalysisSummary getFlowAnalysisSummary(BaseTimeRangeRequest timeRangeRequest) {
        LocalDateTime now = LocalDateTime.now();
        if (Objects.isNull(timeRangeRequest.getEndTime())) {
            timeRangeRequest.setEndTime(now);
        }

        List<String> translatedSceneIds = csceneCommonService.translateSceneIds(CollectionUtil.toList(timeRangeRequest.getSceneId()));
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(translatedSceneIds, timeRangeRequest.getElementIds());
        if (timeRangeRequest.getStartTime() == null) {
            if (batchSceneHandler.getEarliestStartTime() != null) {
                timeRangeRequest.setStartTime(batchSceneHandler.getEarliestStartTime().minusHours(24));
            } else {
                timeRangeRequest.setStartTime(timeRangeRequest.getEndTime().minusDays(10));
            }
        }

        FlowAnalysisRequest flowAnalysisRequest = FlowAnalysisRequest.builder()
                .groupList(batchSceneHandler.getFlowCollectors())
                .startDateTime(timeRangeRequest.getStartTime())
                .endDateTime(timeRangeRequest.getEndTime()).build();
        Double totalFlowBytes = 0.0;
        Double totalAttackFlowBytes = 0.0;
        Long attackCount = 0L;
        if (flowAnalysisRequest.getGroupList() != null && flowAnalysisRequest.getGroupList().size() > 0) {
            // 总流量
            totalFlowBytes = flowAnalysisService.calcNetworkBytes(flowAnalysisRequest);

            // 攻击流量
            flowAnalysisRequest.setSeverityEnum(SeverityEnum.ALL);
            totalAttackFlowBytes = flowAnalysisService.calcNetworkBytes(flowAnalysisRequest);

            // 攻击事件
            FlowRiskCount riskCount = getFlowRiskCount(timeRangeRequest);
            attackCount = (long) (riskCount.getHighCount() + riskCount.getMediumCount() + riskCount.getLowCount());
        }

        // 网络连接
        Integer netConnSize = 0;
        List<ElementMonitorBasicInfo> eles = sceneMonitorService.getAllSceneElements(timeRangeRequest.getSceneId(), false, new SceneAllElementsQueryRequest());

        for (ElementMonitorBasicInfo ele : eles) {
            BaseSceneElementPageRequest request = new BaseSceneElementPageRequest();
            request.setSceneId(timeRangeRequest.getSceneId());
            request.setElementIds(CollectionUtil.toList(ele.getId()));
            ListObjServiceResult<ConnectionState> conns = elementMonitorService.pageNetworkConnections(request);
            int total = conns.getSource().getTotal();
            netConnSize = netConnSize + total;
        }

        // 带宽占用
        Double bandwith = 30.0;

        // Byte -> GB
        long deno = 1024 * 1024 * 1024;
        double totalFlowGB = 0.0;
        if (totalFlowBytes != 0) {
            totalFlowGB = NumberUtil.round(totalFlowBytes / deno, 3).doubleValue();
        }
        double totalAttackFlowGB = 0.0;
        if (totalAttackFlowBytes != 0) {
            totalAttackFlowGB = NumberUtil.round(totalAttackFlowBytes / deno, 3).doubleValue();
        }

        // 有数值的话，不足0.001GB的显示0.001GB
        if (totalFlowBytes > 0 && totalFlowGB == 0) {
            totalFlowGB = 0.001;
        }

        if (totalAttackFlowBytes > 0 && totalAttackFlowGB == 0) {
            totalAttackFlowGB = 0.001;
        }


        FlowAnalysisSummary summary =
                FlowAnalysisSummary.builder()
                        .totalFlowSize(totalFlowGB)
                        .totalAttackFlowSize(totalAttackFlowGB)
                        .attackCount(attackCount)
                        .netConnSize(netConnSize)
                        .bandwith(bandwith)
                        .build();

        return summary;

    }


    private FlowTrend getClosest(LocalDateTime time, Map<LocalDateTime, FlowTrend> flowTrendMap) {
        LocalDateTime closestTime = null;
        long durationSeconds = Long.MAX_VALUE;
        for (Map.Entry<LocalDateTime, FlowTrend> entry : flowTrendMap.entrySet()) {
            Duration duration = Duration.between(entry.getKey(), time);
            long duraSecs = Math.abs(duration.toSeconds());
            if (durationSeconds > duraSecs) {
                durationSeconds = duraSecs;
                closestTime = entry.getKey();
            }
        }
        return flowTrendMap.get(closestTime);
    }

    /**
     * 导出全部流量文件
     *
     */
    public String export(String taskId, String csceneId) {
        // csceneId -> sceneId
        String sceneId = csceneCommonService.translateSceneId(csceneId);
        if (StringUtils.isEmpty(sceneId)) {
            log.error("find no sceneId by csceneId {}", sceneId);
            return StringUtils.EMPTY;
        }
        String url = scenePluginCollectionSceneService.exportFlowPcap(sceneId, taskId).getSource();
        if (StringUtils.isNotEmpty(url)) {
            CloudAppConfig config = BeanCommon.getBean(CloudAppConfig.class);
            if (config.getPublicNetProxy() && config.getPublicNetProxyServer()) {
                if (StringUtils.isNotEmpty(config.getPublicNetProxyIp())) {
                    URL u = URLUtil.url(url);
                    // 如果和cpcloud同网段的才进行ip的替换
                    CommonAppConfig commonAppConfig = BeanCommon.getBean(CommonAppConfig.class);
                    String[] ip1 = commonAppConfig.getCpIp().split("\\.");
                    String[] ip2 = u.getHost().split("\\.");
                    // 比较前三个分段
                    boolean replace = true;
                    for (int i = 0; i < 3; i++) {
                        if (!ip1[i].equals(ip2[i])) {
                            replace = false;
                            break;
                        }
                    }
                    if (replace) {
                        url = StringUtils.replaceOnce(url, u.getHost(), config.getPublicNetProxyIp());
                    }
                }
            }
        }
        FlowPcapExportUtil.finalProcess(taskId, url);
        return url;
    }
}
