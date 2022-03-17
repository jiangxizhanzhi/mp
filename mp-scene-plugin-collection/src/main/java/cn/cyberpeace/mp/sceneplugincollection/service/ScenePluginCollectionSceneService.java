package cn.cyberpeace.mp.sceneplugincollection.service;

import cn.cyberpeace.mp.base.bo.IntervalSizeBO;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincollection.bo.*;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.service.param.*;
import cn.cyberpeace.mp.situationaware.bo.*;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhModel;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.situationaware.model.enums.AggregationTypeEnum;
import cn.cyberpeace.mp.situationaware.model.enums.SeverityEnum;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import cn.cyberpeace.mp.situationaware.request.FlowAnalysisRequest;
import cn.cyberpeace.mp.situationaware.request.FlowSimulationRequest;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.FlowAnalysisService;
import cn.cyberpeace.mp.situationaware.service.wazuh.SysCollectorService;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScenePluginCollectionSceneService {
    private static String LOG_PREFIX = "collection service -> ";

    public static class GroupCountFlowField {
        public static final String LEVEL = "level";
        public static final String TYPE = "type";
        public static final String SOURCE_IP = "source_ip";
        public static final String DEST_IP = "dest_ip";
    }

    public static class GroupCountHostEventField {
        public static final String EVENT_TYPE = "eventType";
        public static final String NODE_NAME = "nodeName";
    }

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private FlowAnalysisService flowAnalysisService;

    @Autowired
    private SysCollectorService sysCollectorService;

    public ListObjServiceResult<CollectionFlowBO> listFlows(List<String> sceneIds, FlowQueryParams queryParams) {
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, null);
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectors())) {
            log.info("{} query time interval sum flow size,sceneIds:{},no collection plugin or no flowCollector", LOG_PREFIX, JsonCommon.stringify(sceneIds));
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectedMacAddrs())) {
            log.info("{} query time interval sum flow size,sceneIds:{},no collection plugin or no flowCollector", LOG_PREFIX, JsonCommon.stringify(sceneIds));
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        FlowAnalysisRequest request = new FlowAnalysisRequest();
        request.setGroupList(batchSceneHandler.getFlowCollectors());
        if (CollectionUtils.isNotEmpty(queryParams.getSourceIps())){
            request.setSourceIps(queryParams.getSourceIps());
        }else {
            request.setSourceMacs(batchSceneHandler.getFlowCollectedMacAddrs());
        }

        if (CollectionUtils.isNotEmpty(queryParams.getTargetIps())){
            request.setDestinationIps(queryParams.getTargetIps());
        } else {
            request.setDestinationIps(batchSceneHandler.getAllIpAddrs());
        }

        request.setSecondsInterval(queryParams.getInterval());
        // 查询时间段 默认使用场景创建到当前时间
        if (StringUtils.isBlank(queryParams.getStartTime())) {
            request.setStartDateTime(batchSceneHandler.getEarliestStartTime());
        } else {
            request.setStartDateTime(TimeUtils.stringToLocalDateTime(queryParams.getStartTime()));
        }
        if (StringUtils.isBlank(queryParams.getEndTime())) {
            request.setEndDateTime(LocalDateTime.now());
        } else {
            request.setEndDateTime(TimeUtils.stringToLocalDateTime(queryParams.getEndTime()));
        }
        if (Objects.nonNull(queryParams.getPerPage())) {
            request.setTopSize(queryParams.getPerPage());
        }

        if (StringUtils.isNotBlank(queryParams.getSourcePort())) {
            request.setSourcePort(queryParams.getSourcePort());
        }

        if (StringUtils.isNotBlank(queryParams.getTargetPort())) {
            request.setDestinationPort(queryParams.getTargetPort());
        }

        if (StringUtils.isNotBlank(queryParams.getProtocol())) {
            request.setProtocols(CollectionUtil.toList(queryParams.getProtocol()));
        }


        request.setSeverityEnum(SeverityEnum.ALL);
        request.setFilterDestinationIps(queryParams.getFilterDestinationIps());
        List<FlowLogBO> flowLogBOS = flowAnalysisService.flowLog(request);
        if (CollectionUtils.isEmpty(flowLogBOS)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        List<CollectionFlowBO> list = flowLogBOS.stream().map(flowLogBO -> this.buildCollectionFlowBOFrom(flowLogBO, batchSceneHandler.getAllMac2ElementMap(),batchSceneHandler.getAllIp2ElementMap(), batchSceneHandler.getAllMac2SceneIdMap())).collect(Collectors.toList());
        return new ListObjServiceResult<>(new ListObjResult<>(list, list.size()));
    }

    public ListObjServiceResult<CollectionHostStateBO> listHostStates(String sceneId, HostStateQueryParams queryParams) {
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        // 没有采集插件
        CollectionScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (pluginHandler == null) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        // 构建探针名和节点的映射
        List<SceneElementBO> srcElements = getSrcElements(sceneHandler, queryParams.getNodeIds());
        Map<String, SceneElementBO> agentName2Element = new HashMap<>();
        srcElements.forEach(srcElement -> {
            var collectionElement = pluginHandler.getElement(srcElement.getId());
            if (collectionElement == null || CollectionUtils.isEmpty(collectionElement.getProbes())) {
                return;
            }
            if (collectionElement.getEnableProbeCollect() == null || !collectionElement.getEnableProbeCollect()) {
                return;
            }
            agentName2Element.put(srcElement.getEid(), srcElement);
        });
        // 没有探针
        if (agentName2Element.isEmpty()) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        // 构建查询参数
        var request = new WazuhMonitorEventsRequest();
        request.setPageNum(queryParams.getPage());
        // todo 暂时最多取 10000 条
        request.setPageSize(Optional.ofNullable(queryParams.getPageSize()).orElse(10000));
        request.setAgentNames(new ArrayList<>(agentName2Element.keySet()));
        // 开始时间,默认为场景创建时间
        request.setStartTime(Optional.ofNullable(queryParams.getStartTime())
                .orElse(sceneHandler.getScene().getCreateTime())
        );
        // 结束时间,默认为当前时间
        request.setEndTime(Optional.ofNullable(queryParams.getEndTime())
                .orElse(LocalDateTime.now())
        );
        // 执行查询
        List<WazuhModel> result = dataCollectionService.queryMonitorEventsWithTimeRange(request);
        List<CollectionHostStateBO> hostStates = result.stream().map(srcHostState -> {
            CollectionHostStateBO hostState = new CollectionHostStateBO();
            hostState.setSceneId(sceneId);
            hostState.setCpuCount(srcHostState.getCpuCount());
            hostState.setCpuPercent(srcHostState.getCpuTotalPercent());
            // 内存统一使用单位M
            hostState.setMemoryTotal(this.byte2MB(srcHostState.getMemoryTotal()));
            hostState.setMemoryFree(this.byte2MB(srcHostState.getMemoryFree()));
            // 磁盘统一使用单位G
            hostState.setDiskSpace(this.byte2GB(srcHostState.getDiskSpace()));
            hostState.setMemoryPercent(srcHostState.getMemoryPercent());
            hostState.setTime(srcHostState.getTime());
            // todo 进程，端口，网络连接，磁盘使用率
            var srcElement = agentName2Element.getOrDefault(srcHostState.getAgentName(), null);
            if (srcElement != null) {
                hostState.setNodeId(srcElement.getId());
                hostState.setNodeName(srcElement.getName());
            }
            return hostState;
        }).collect(Collectors.toList());
        return new ListObjServiceResult<>(new ListObjResult<>(hostStates, result.size()));
    }

    public ListObjServiceResult<CollectionHostEventBO> listHostEvents(String sceneId, HostEventQueryParams queryParams) {
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        List<SceneElementBO> srcElements = getSrcElements(sceneHandler, queryParams.getNodeIds());
        CollectionScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (pluginHandler == null) {
            return new ListObjServiceResult<>();
        }
        Map<String, SceneElementBO> agentNameElementMap = new HashMap<>();
        srcElements.forEach(srcElement -> {
            var collectionElement = pluginHandler.getElement(srcElement.getId());
            if (collectionElement == null || CollectionUtils.isEmpty(collectionElement.getProbes())) {
                return;
            }
            if (collectionElement.getEnableProbeCollect() == null || !collectionElement.getEnableProbeCollect()) {
                return;
            }
            agentNameElementMap.put(srcElement.getEid(), srcElement);
        });

        if (agentNameElementMap.isEmpty()) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        var request = new WazuhEventRequest();
        request.setPageNum(queryParams.getPage());
        request.setPageSize(queryParams.getPageSize());
        request.setAgentNames(new ArrayList<>(agentNameElementMap.keySet()));

        // 查询时间段 默认使用场景创建到当前时间
        if (StringUtils.isBlank(queryParams.getStartTime())) {
            request.setStartTime(sceneHandler.getScene().getCreateTime());
        } else {
            request.setStartTime(TimeUtils.stringToLocalDateTime(queryParams.getStartTime()));
        }
        if (StringUtils.isBlank(queryParams.getEndTime())) {
            request.setEndTime(LocalDateTime.now());
        } else {
            request.setEndTime(TimeUtils.stringToLocalDateTime(queryParams.getEndTime()));
        }

        if (CollectionUtils.isNotEmpty(queryParams.getCatagories())) {
            request.setCatagories(queryParams.getCatagories());
        }

        if (CollectionUtils.isNotEmpty(queryParams.getLevels())) {
            request.setLevels(queryParams.getLevels());
        }

        if (CollectionUtils.isNotEmpty(queryParams.getKeyword())) {
            request.setKeyword(queryParams.getKeyword());
        }

        // todo 查询事件类型

        var result = dataCollectionService.queryFileEvents(request);

        List<CollectionHostEventBO> hostEvents = new ArrayList<>();
        result.getObjs().forEach(srcHostEvent -> {
            var hostEvent = new CollectionHostEventBO();
            hostEvent.setSceneId(sceneId);
            hostEvent.setType(srcHostEvent.getFileEventType().name());
            hostEvent.setEventDesc(srcHostEvent.getAction());
            hostEvent.setTime(srcHostEvent.getUpdateTime());
            hostEvent.setDescription(srcHostEvent.getDescription());
            hostEvent.setLevel(srcHostEvent.getLevel());
            hostEvent.setCatagory(srcHostEvent.getCatagory());
            hostEvent.setContent(srcHostEvent.getContent());
            var srcElement = agentNameElementMap.getOrDefault(srcHostEvent.getAgentName(), null);
            if (srcElement != null) {
                hostEvent.setNodeId(srcElement.getId());
                hostEvent.setNodeName(srcElement.getName());
                hostEvent.setSystemType(srcElement.getSystemType());
                hostEvent.setSystemTypeName(SystemTypeUtils.getStandardSystemType(srcElement.getSystemType()));
            }
            hostEvents.add(hostEvent);
        });

        return new ListObjServiceResult<>(new ListObjResult<>(hostEvents, result.getTotal()));
    }

    /**
     * 安全等级分组查询采集流量的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 字段的聚合统计数量 Map<安全等级, 该字段值统计数量>
     */
    public ServiceResult<Map<String, Integer>> groupCountFlowByLevel(
            List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime) {
        return groupCountFlow(sceneIds, nodeIds, GroupCountFlowField.LEVEL, startTime, endTime,null);
    }

    /**
     * 安全类型分组查询采集流量的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 字段的聚合统计数量 Map<安全等级, 该字段值统计数量>
     */
    public ServiceResult<Map<String, Integer>> groupCountFlowByType(
            List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime,List<String> sourceIps) {
        return groupCountFlow(sceneIds, nodeIds, GroupCountFlowField.TYPE, startTime, endTime,sourceIps);
    }


    /**
     * 事件类型分组查询主机事件的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 字段的聚合统计数量 Map<事件类型, 该字段值统计数量>
     */
    public ServiceResult<Map<String, Integer>> groupCountHostEventByEventType(
            List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime) {
        return groupCountHostEvent(sceneIds, nodeIds, GroupCountHostEventField.EVENT_TYPE, startTime, endTime);
    }

    /**
     * 节点名称分组查询主机事件的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 字段的聚合统计数量 Map<节点名称, 该字段值统计数量>
     */
    public ServiceResult<Map<String, Integer>> groupCountHostEventByNodeName(
            List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime) {
        return groupCountHostEvent(sceneIds, nodeIds, GroupCountHostEventField.NODE_NAME, startTime, endTime);
    }

    /**
     * 时间区间统计流量次数
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 聚合统计结果
     */
    public ServiceResult<List<IntervalSizeBO<LocalDateTime, Long>>> timeIntervalCountFlow(
            List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime) {
        return new ServiceResult<>(new ArrayList<>());
    }

    /**
     * 时间区间统计流量大小
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param intervalSeconds 每{intervalSeconds}秒统计一条数据
     * @return 聚合统计结果
     */
    public ServiceResult<List<IntervalSizeBO<LocalDateTime, Long>>> timeIntervalSumFlowSize(
            List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime,
            int intervalSeconds,List<String> sourceIps,Integer type) {
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, nodeIds);
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectors())) {
            log.info("{} query time interval sum flow size,sceneId:{},no collection plugin or no flowCollector", LOG_PREFIX, JsonCommon.stringify(sceneIds));
            return new ServiceResult<>(new ArrayList<>());
        }
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectedMacAddrs())) {
            log.info("{} query time interval sum flow size,sceneIds:{},nodeIds:{},no enabled macAddr", LOG_PREFIX, JsonCommon.stringify(sceneIds), JsonCommon.stringify(nodeIds));
            return new ServiceResult<>(new ArrayList<>());
        }
        // 构造参数
        FlowAnalysisRequest request = new FlowAnalysisRequest();
        request.setGroupList(batchSceneHandler.getFlowCollectors());
        if (CollectionUtils.isEmpty(sourceIps)) {
            request.setSourceMacs(batchSceneHandler.getFlowCollectedMacAddrs());
        }else{
            request.setSourceIps(sourceIps);
        }
        request.setDestinationIps(batchSceneHandler.getAllIpAddrs());
        request.setStartDateTime(Optional.ofNullable(startTime).orElse(batchSceneHandler.getEarliestStartTime()));
        request.setEndDateTime(Optional.ofNullable(endTime).orElse(LocalDateTime.now()));
        request.setSecondsInterval(intervalSeconds);
        request.setSeverityEnum(SeverityEnum.ALL);
        // 执行查询
        List<AttackDateHistogramNetworkBytesBO> bos;
        if (Objects.isNull(type) || NumberUtils.INTEGER_ZERO.equals(type)) {
            bos = flowAnalysisService.sumNetworkBytesByDateHistogramTimestamp(request);
        }else{
            bos = flowAnalysisService.countByDateHistogramTimestamp(request);
        }

        if (CollectionUtils.isEmpty(bos)) {
            return new ServiceResult<>(new ArrayList<>());
        }
        List<IntervalSizeBO<LocalDateTime, Long>> result = bos.stream()
                .map(bo -> {
                    LocalDateTime time = TimeUtils.stringToLocalDateTime(bo.getTime(), TimeUtils.tzMillsDateTimeFormatter)
                            .atZone(ZoneOffset.UTC)
                            .withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalDateTime();
                    return Objects.isNull(type) || NumberUtils.INTEGER_ZERO.equals(type) ?
                            new IntervalSizeBO<>(time, time, bo.getTrafficSize().longValue()) :
                            new IntervalSizeBO<>(time, time, bo.getAttackNumber());
                }).collect(Collectors.toList());
        Collections.sort(result, Comparator.comparing(IntervalSizeBO::getStart));
        return new ServiceResult<>(result);
    }

    /**
     * 模拟流量
     * @param params
     * @return
     */
    public ServiceResult simulation(FlowSimulationParams params){
        List<String> sceneIds = params.getSceneIds();

        List<String> nodeIds = params.getNodeIds();

        String sourceIp = params.getSourceIp();

        List<String> sourceMacs = params.getSourceMacs();

        String destinationIp = params.getDestinationIp();

        List<String> destinationMacs = params.getDestinationMacs();

        LocalDateTime dateTime = params.getDateTime();

        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, nodeIds);
        List<String> flowCollectors = batchSceneHandler.getFlowCollectors();
        if (CollectionUtils.isEmpty(flowCollectors)) {
            log.info("{} query time interval sum flow size,sceneId:{},no collection plugin or no flowCollector", LOG_PREFIX, JsonCommon.stringify(sceneIds));
            return new ServiceResult<>(new ArrayList<>());
        }

        // 构造参数
        FlowSimulationRequest request = new FlowSimulationRequest();
        request.setGroup(flowCollectors.get(0));
        request.setSourceIp(sourceIp);
        if (CollectionUtils.isEmpty(sourceMacs) && StringUtils.isNotBlank(sourceIp)){
            request.setSourceMacs(Arrays.asList(batchSceneHandler.getMacByIp(sourceIp)));
        }else{
            request.setSourceMacs(sourceMacs);
        }
        request.setDestinationIp(destinationIp);
        if (CollectionUtils.isEmpty(destinationMacs) && StringUtils.isNotBlank(destinationIp)){
            request.setDestinationMacs(Arrays.asList(batchSceneHandler.getMacByIp(destinationIp)));
        }else{
            request.setDestinationMacs(destinationMacs);
        }
        request.setNetworkBytes(params.getNetworkBytes());
        request.setDateTime(dateTime);

        try {
            flowAnalysisService.addSimulationFlow(request);
        } catch (IOException e) {
            log.error("simulation flow error : {}",e.getMessage(),e);
        }

        return new ServiceResult<>();
    }

    /**
     * 统计受攻击端口信息
     * @param params
     * @return
     */
    public ServiceResult<List<FlowGroupPortWithProtocolBO>> groupByDestinationPortWithProtocol(FlowGroupByDestinationPortParams params){
        List<String> sceneIds = params.getSceneIds();

        List<String> nodeIds = params.getNodeIds();

        List<String> sourceIps = params.getSourceIps();

        LocalDateTime startTime = params.getStartTime();

        LocalDateTime endTime = params.getEndTime();

        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, nodeIds);
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectors())) {
            log.info("{} query time interval sum flow size,sceneId:{},no collection plugin or no flowCollector", LOG_PREFIX, JsonCommon.stringify(sceneIds));
            return new ServiceResult<>(new ArrayList<>());
        }
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectedMacAddrs())) {
            log.info("{} query time interval sum flow size,sceneIds:{},nodeIds:{},no enabled macAddr", LOG_PREFIX, JsonCommon.stringify(sceneIds), JsonCommon.stringify(nodeIds));
            return new ServiceResult<>(new ArrayList<>());
        }
        // 构造参数
        FlowAnalysisRequest request = new FlowAnalysisRequest();
        request.setGroupList(batchSceneHandler.getFlowCollectors());
        if (CollectionUtils.isEmpty(sourceIps)) {
            request.setSourceMacs(batchSceneHandler.getFlowCollectedMacAddrs());
        }else{
            request.setSourceIps(sourceIps);
        }
        request.setDestinationIps(batchSceneHandler.getAllIpAddrs());
        request.setStartDateTime(Optional.ofNullable(startTime).orElse(batchSceneHandler.getEarliestStartTime()));
        request.setEndDateTime(Optional.ofNullable(endTime).orElse(LocalDateTime.now()));
        request.setSeverityEnum(SeverityEnum.ALL);
        request.setTopSize(params.getTopSize());
        // 执行查询
        List<AttackPortAggregationBO> boList = flowAnalysisService.groupByDestinationPortWithProtocol(request);

        List<FlowGroupPortWithProtocolBO> result = boList.stream().map(v -> {
            FlowGroupPortWithProtocolBO bo = new FlowGroupPortWithProtocolBO();
            bo.setProtocol(v.getProtocol());
            bo.setPort(v.getPort());
            bo.setAttackNumber(v.getAttackNumber());
            bo.setTrafficSize(v.getTrafficSize());
            return bo;
        }).collect(Collectors.toList());
        return new ServiceResult<>(result);
    }

    /**
     * 统计主机状态
     * @param params
     * @return
     */
    public ServiceResult<List<FlowGroupByDestinationIpAndSeverityBO>> groupByDestinationIpAndSeverity(FlowGroupByDestinationPortParams params){
        List<String> sceneIds = params.getSceneIds();

        List<String> nodeIds = params.getNodeIds();

        List<String> sourceIps = params.getSourceIps();

        LocalDateTime startTime = params.getStartTime();

        LocalDateTime endTime = params.getEndTime();

        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, nodeIds);
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectors())) {
            log.info("{} query time interval sum flow size,sceneId:{},no collection plugin or no flowCollector", LOG_PREFIX, JsonCommon.stringify(sceneIds));
            return new ServiceResult<>(new ArrayList<>());
        }
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectedMacAddrs())) {
            log.info("{} query time interval sum flow size,sceneIds:{},nodeIds:{},no enabled macAddr", LOG_PREFIX, JsonCommon.stringify(sceneIds), JsonCommon.stringify(nodeIds));
            return new ServiceResult<>(new ArrayList<>());
        }
        // 构造参数
        FlowAnalysisRequest request = new FlowAnalysisRequest();
        request.setGroupList(batchSceneHandler.getFlowCollectors());
        if (CollectionUtils.isNotEmpty(sourceIps)) {
            request.setSourceIps(sourceIps);
        }
        request.setDestinationIps(batchSceneHandler.getAllIpAddrs());
        request.setStartDateTime(Optional.ofNullable(startTime).orElse(batchSceneHandler.getEarliestStartTime()));
        request.setEndDateTime(Optional.ofNullable(endTime).orElse(LocalDateTime.now()));
        request.setSeverityEnum(SeverityEnum.ALL);
        // 执行查询
        List<AttackSeverityAndDestinationIpAggregationBO> boList = flowAnalysisService.groupByDestinationIpAndSeverity(request);

        List<AttackSeverityAndDestinationIpAggregationBO> sortedList = boList.stream().sorted(Comparator.comparing(AttackSeverityAndDestinationIpAggregationBO::getSeverity)).collect(Collectors.toList());

        List<String> destinationIpList = new ArrayList<>();

        List<FlowGroupByDestinationIpAndSeverityBO> resultList = new ArrayList<>();
        for (int i = 0; i < sortedList.size(); i++){
            FlowGroupByDestinationIpAndSeverityBO result = new FlowGroupByDestinationIpAndSeverityBO();
            AttackSeverityAndDestinationIpAggregationBO bo = boList.get(i);
            result.setSeverity(bo.getSeverity());
            List<String> ipList = bo.getDestinationIpList();
            if (CollectionUtils.isNotEmpty(ipList)) {
                List<String> ips = ipList.stream().filter(v -> !destinationIpList.contains(v)).collect(Collectors.toList());
                result.setDestinationIpList(ips);
                result.setNum(ips.size());
                destinationIpList.addAll(ips);
            }
            resultList.add(result);
        }

        int allIpSize = batchSceneHandler.getAllIpAddrs().size();
        int severitySize = resultList.stream().map(FlowGroupByDestinationIpAndSeverityBO::getNum).reduce((a,b) -> a + b).orElse(0);
        int normalSize = allIpSize - severitySize;
        resultList.add(new FlowGroupByDestinationIpAndSeverityBO(0,normalSize));
        return new ServiceResult<>(resultList);
    }

    /**
     * 时间区间统计主机cpu占用平均值
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param intervalSeconds 每{intervalSeconds}秒统计一条数据
     * @return 聚合统计结果
     */
    public ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> timeIntervalAvgHostCpuPercent(
            List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime,
            int intervalSeconds) {
        List<IntervalSizeBO<LocalDateTime, Double>> result = this.timeIntervalAvgBy(sceneIds, nodeIds, startTime, endTime, intervalSeconds, (state) -> state.getCpuPercent());
        return new ServiceResult<>(result);
    }

    /**
     * 时间区间统计主机内存占用平均值
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param intervalNumber 间隔数量
     * @return 聚合统计结果
     */
    public ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> timeIntervalAvgHostMemoryPercent(
            List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime,
            int intervalNumber) {
        List<IntervalSizeBO<LocalDateTime, Double>> result = this.timeIntervalAvgBy(sceneIds, nodeIds, startTime, endTime, intervalNumber, (state) -> state.getMemoryPercent().doubleValue());
        return new ServiceResult<>(result);
    }

    /**
     * 时间区间统计主机磁盘占用平均值
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param intervalNumber 间隔数量
     * @return 聚合统计结果
     */
    public ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> timeIntervalAvgHostDiskPercent(
            List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime,
            int intervalNumber) {
        // todo 主机磁盘使用率
        return new ServiceResult<>(null);
    }

    /**
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param intervalSeconds 间隔数量
     * @param execCal 查询使用的数据
     * @return
     */
    private List<IntervalSizeBO<LocalDateTime, Double>> timeIntervalAvgBy(
            List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime,
            int intervalSeconds, Function<CollectionHostStateBO, Double> execCal) {
        //todo 暂时先支持一个场景的查询
        HostStateQueryParams params = new HostStateQueryParams();
        params.setNodeIds(nodeIds);
        // 为计算平均值,往前多取一个周期的数据
        params.setStartTime(startTime.minus(intervalSeconds, ChronoUnit.SECONDS));
        params.setEndTime(endTime);
        var queryResult = this.listHostStates(sceneIds.get(0), params).getSource();
        if (queryResult == null || CollectionUtils.isEmpty(queryResult.getObjs())) {
            return new ArrayList<>();
        }
        List<CollectionHostStateBO> list = queryResult.getObjs();
        // 按时间升序排列
        Collections.sort(list, Comparator.comparing(CollectionHostStateBO::getTime));
        LocalDateTime tmpMaxTime = startTime;
        BigDecimal tmpValue = null;
        BigDecimal tmpSize = null;
        List<IntervalSizeBO<LocalDateTime, Double>> result = new ArrayList<>();
        int index = 0;
        while (index < list.size()) {
            CollectionHostStateBO state = list.get(index);
            if (state.getTime().isBefore(tmpMaxTime)) {
                // 数据属于当前周期,计算结果并指向下一个数据
                index++;
                double value = execCal.apply(state);
                tmpValue = Optional.ofNullable(tmpValue)
                        .orElse(BigDecimal.ZERO)
                        .add(BigDecimal.valueOf(value));
                tmpSize = Optional.ofNullable(tmpSize)
                        .orElse(BigDecimal.ZERO)
                        .add(BigDecimal.ONE);
            } else {
                // 数据不属于当前周期,记录当前周期结果,并指向下一个周期
                // 当前周期有数据则记录
                if (tmpValue != null) {
                    result.add(new IntervalSizeBO<LocalDateTime, Double>(tmpMaxTime, tmpMaxTime, tmpValue.divide(tmpSize, 10, RoundingMode.HALF_DOWN).doubleValue()));
                }
                // 进入下一个周期
                tmpMaxTime = tmpMaxTime.plus(intervalSeconds, ChronoUnit.SECONDS);
                tmpSize = null;
                tmpValue = null;
            }
        }
        // 记录最后一个数据
        if (tmpValue != null) {
            result.add(new IntervalSizeBO<LocalDateTime, Double>(tmpMaxTime, tmpMaxTime, tmpValue.divide(tmpSize, 10, RoundingMode.HALF_DOWN).doubleValue()));
        }
        return result;
    }

    private List<SceneElementBO> getSrcElements(SceneHandler sceneHandler, List<String> nodeIds) {
        List<SceneElementBO> srcElements;
        if (nodeIds == null || nodeIds.isEmpty()) {
            srcElements = new ArrayList<>(sceneHandler.getElementMap().values());
        } else {
            srcElements = new ArrayList<>();
            nodeIds.forEach(nodeId -> {
                var srcElement = sceneHandler.getElement(nodeId);
                if (srcElement != null) {
                    srcElements.add(srcElement);
                }
            });
        }
        return srcElements;
    }

    private CollectionFlowBO buildCollectionFlowBOFrom(FlowLogBO flowLogBO, Map<String, SceneElementBO> mac2ElementMap, Map<String, SceneElementBO>  ipElementMap,Map<String, String> mac2SceneId) {
        CollectionFlowBO flowBO = new CollectionFlowBO();
        if (flowLogBO.getTimestamp() != null) {
            flowBO.setTime(TimeUtils.timestampToLocalDateTime(flowLogBO.getTimestamp()));
            flowBO.setTimestamp(flowLogBO.getTimestamp());
        }
        if (flowLogBO.getSource() != null) {
            flowBO.setSourceIp(flowLogBO.getSource().getIp());
            if (CollectionUtils.isNotEmpty(flowLogBO.getSource().getMac())) {
                for (int i = 0; i < flowLogBO.getSource().getMac().size(); i++) {
                    String mac = flowLogBO.getSource().getMac().get(i);
                    if (mac2ElementMap.containsKey(mac)) {
                        flowBO.setSourceNodeId(mac2ElementMap.get(mac).getId());
                        flowBO.setSourceNodeName(mac2ElementMap.get(mac).getName());
                        flowBO.setSceneId(mac2SceneId.get(mac));
                        break;
                    }
                }
            }
        }
        if (flowLogBO.getDestination() != null) {
            String ip = flowLogBO.getDestination().getIp();
            flowBO.setTargetIp(ip);
            if (ipElementMap.containsKey(ip)){
                flowBO.setTargetNodeId(ipElementMap.get(ip).getId());
                flowBO.setTargetNodeName(ipElementMap.get(ip).getName());
                flowBO.setSceneId(mac2SceneId.get(ip));
            }
//            if (CollectionUtils.isNotEmpty(flowLogBO.getDestination().getMac())) {
//                for (int i = 0; i < flowLogBO.getDestination().getMac().size(); i++) {
//                    String mac = flowLogBO.getDestination().getMac().get(i);
//                    if (mac2ElementMap.containsKey(mac)) {
//                        flowBO.setTargetNodeId(mac2ElementMap.get(mac).getId());
//                        flowBO.setTargetNodeName(mac2ElementMap.get(mac).getName());
//                        flowBO.setSceneId(mac2SceneId.get(mac));
//                        break;
//                    }
//                }
//            }
        }
        if (flowLogBO.getSuricata() != null) {
            List<String> category = flowLogBO.getSuricata().getCategory();
            List<Integer> severity = flowLogBO.getSuricata().getSeverity();
            if (CollectionUtils.isNotEmpty(category) && CollectionUtils.isNotEmpty(severity)) {
                int attackInfoNum = category.size() < severity.size() ? category.size() : severity.size();
                List<AttackInfo> attackInfos = new ArrayList<>();
                flowBO.setAttackInfos(attackInfos);
                for (int i = 0; i < attackInfoNum; i++) {
                    attackInfos.add(new AttackInfo(severity.get(i), category.get(i)));
                }
            }
        }
        if (flowLogBO.getNetwork() != null) {
            flowBO.setSize(flowLogBO.getNetwork().getBytes());
        }
        return flowBO;
    }

    private List<String> loadMac2ElementBOMapAndGetEnableMac(Map<String, SceneElementBO> mac2ElementMap, List<SceneElementBO> srcElements, CollectionScenePluginHandler pluginHandler) {
        List<String> enableMac = new ArrayList<>();
        srcElements.forEach(srcElement -> {
            var srcPorts = srcElement.getPorts();
            if (srcPorts == null) {
                return;
            }
            srcPorts.forEach(srcPort -> {
                var macAddr = srcPort.getMacAddr();
                if (StringUtils.isEmpty(macAddr)) {
                    return;
                }
                mac2ElementMap.put(macAddr, srcElement);
                var collectionPort = pluginHandler.getPort(srcPort.getId());
                if (collectionPort == null || !Boolean.TRUE.equals(collectionPort.getEnableFlowCollect())) {
                    return;
                }
                enableMac.add(macAddr);
            });
        });
        return enableMac;
    }

    /**
     * TODO 分组查询主机事件的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param groupField 分组聚合字段类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param <T> 分组聚合字段的值类型
     * @return 字段的聚合统计数量 Map<字段值, 该字段值统计数量>
     */
    private <T> ServiceResult<Map<T, Integer>> groupCountHostEvent(
            List<String> sceneIds, List<String> nodeIds, String groupField, LocalDateTime startTime, LocalDateTime endTime) {
        return new ServiceResult<>(new HashMap<>());
    }

    /**
     * TODO 分组查询采集流量的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 字段的聚合统计数量 Map<字段值, 该字段值统计数量>
     */
    private <T> ServiceResult<Map<T, Integer>> groupCountFlow(
            List<String> sceneIds, List<String> nodeIds, String groupField, LocalDateTime startTime, LocalDateTime endTime,List<String> sourceIps) {
        FlowAnalysisRequest request = new FlowAnalysisRequest();
        if (GroupCountFlowField.LEVEL.equals(groupField)) {
            request.setAggregationTypeEnum(AggregationTypeEnum.SURICATA_SEVERITY);
        } else if (GroupCountFlowField.TYPE.equals(groupField)) {
            request.setAggregationTypeEnum(AggregationTypeEnum.SURICATA_CATEGORY);
        }

        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, nodeIds);
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectors())) {
            return new ServiceResult<>(new HashMap<>());
        }
        request.setGroupList(batchSceneHandler.getFlowCollectors());
        request.setStartDateTime(Optional.ofNullable(startTime).orElse(batchSceneHandler.getEarliestStartTime()));
        request.setEndDateTime(Optional.ofNullable(endTime).orElse(LocalDateTime.now()));
        if (CollectionUtils.isNotEmpty(sourceIps)){
            request.setSourceIps(sourceIps);
        }else {
            request.setSourceMacs(batchSceneHandler.getFlowCollectedMacAddrs());
        }
        request.setDestinationIps(batchSceneHandler.getAllIpAddrs());
        List<AttackWayAggregationBO> result = flowAnalysisService.topByAggregationTypeEnum(request);
        if (CollectionUtils.isEmpty(result)) {
            return new ServiceResult<>(new HashMap<>());
        }
        Map<T, Integer> finalResult = new HashMap<>();
        result.forEach(bo -> finalResult.put((T) bo.getAggregationType(), bo.getAttackNumber().intValue()));
        return new ServiceResult<>(finalResult);
    }

    public ServiceResult<Map<String, Integer>> groupCountFlowWithTopSize(List<String> sceneIds, List<String> ips, LocalDateTime startTime, LocalDateTime endTime) {
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, null);
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectors())) {
            return new ServiceResult<>(new HashMap<>());
        }
        LocalDateTime fStartTime = Optional.ofNullable(startTime).orElse(batchSceneHandler.getEarliestStartTime());
        LocalDateTime fEndTime = Optional.ofNullable(endTime).orElse(LocalDateTime.now());
        Map<String, Integer> result = this.doGroupCountFlowByIp(batchSceneHandler.getFlowCollectors(), ips, ips.size(), fStartTime, fEndTime);
        return new ServiceResult<>(result);
    }

    public ServiceResult<List<FlowAttackCountBO>> groupCountFlowAndNodeInfo(List<String> sceneIds, FlowGroupCountParams params) {
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, null);
        // 场景均未开启流量采集
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectors())) {
            return new ServiceResult<>(new ArrayList<>());
        }
        // 拼接参数
        FlowAnalysisRequest request = new FlowAnalysisRequest();
        request.setGroupList(batchSceneHandler.getFlowCollectors());
        // 起止时间参数
        LocalDateTime fStartTime = Optional.ofNullable(params.getStartTime()).orElse(batchSceneHandler.getEarliestStartTime());
        LocalDateTime fEndTime = Optional.ofNullable(params.getEndTime()).orElse(LocalDateTime.now());
        request.setStartDateTime(fStartTime);
        request.setEndDateTime(fEndTime);
        // 数据过滤地址参数
        List<String> paramsAddress = params.getQueryAddress();
        Integer queryTopSize = null;
        if (GroupCountFlowField.SOURCE_IP.equals(params.getQueryType())) {
            request.setSourceIps(CollectionUtils.isNotEmpty(paramsAddress) ? paramsAddress : batchSceneHandler.getFlowCollectedIpAddrs());
            queryTopSize = request.getSourceIps().size();
            request.setDestinationIps(batchSceneHandler.getAllIpAddrs());
            request.setAggregationTypeEnum(AggregationTypeEnum.SOURCE_IP);
            request.setSeverityEnum(SeverityEnum.ALL);
        } else if (GroupCountFlowField.DEST_IP.equals(params.getQueryType())) {
            request.setDestinationIps(CollectionUtils.isNotEmpty(paramsAddress) ? paramsAddress : batchSceneHandler.getAllIpAddrs());
            queryTopSize = request.getDestinationIps().size();
            request.setSourceIps(params.getSourceIps());
            request.setAggregationTypeEnum(AggregationTypeEnum.DESTINATION_IP);
            request.setSeverityEnum(SeverityEnum.ALL);
        } else {
            log.error("wrong params for groupCountFlowAndNodeInfo , params is {}", JsonCommon.stringify(params));
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        // topN 参数,上一层指定的优先
        queryTopSize = Optional.ofNullable(params.getTopSize()).orElse(queryTopSize);
        request.setTopSize(queryTopSize);
        List<AttackWayAggregationBO> aggregationBOS = flowAnalysisService.topByAggregationTypeEnum(request);
        if (CollectionUtils.isEmpty(aggregationBOS)) {
            return new ServiceResult<>(new ArrayList<>());
        }
        List<FlowAttackCountBO> result = aggregationBOS.stream().map(v -> {
            String ip = v.getAggregationType();
            int count = v.getAttackNumber().intValue();
            SceneElementBO node = batchSceneHandler.getNodeByIpAddr(ip);
            return FlowAttackCountBO.builder()
                    .sceneId(batchSceneHandler.getSceneIdByIpAddr(ip))
                    .nodeId(node == null ? StringUtils.EMPTY : node.getId())
                    .nodeName(node == null ? StringUtils.EMPTY : node.getName())
                    .ip(ip)
                    .times(count)
                    .build();
        }).collect(Collectors.toList());
        return new ServiceResult<>(result);
    }

    public ServiceResult<TeamAttackBO> groupCountFlow(List<String> sceneIds, List<String> ips, Integer topSize, LocalDateTime startTime, LocalDateTime endTime) {
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, null);
        if (CollectionUtils.isEmpty(batchSceneHandler.getFlowCollectors())) {
            TeamAttackBO teamAttackBO = new TeamAttackBO();
            teamAttackBO.setAttackNumber(0L);
            teamAttackBO.setAttackWayList(new ArrayList<>());
            teamAttackBO.setTrafficSize((double) 0);
            return new ServiceResult<>(teamAttackBO);
        }
        LocalDateTime fStartTime = Optional.ofNullable(startTime).orElse(batchSceneHandler.getEarliestStartTime());
        LocalDateTime fEndTime = Optional.ofNullable(endTime).orElse(LocalDateTime.now());
        FlowAnalysisRequest flowAnalysisRequest = FlowAnalysisRequest.builder()
                .groupList(batchSceneHandler.getFlowCollectors())
                .sourceIps(ips)
                .startDateTime(fStartTime)
                .endDateTime(fEndTime)
                .topSize(topSize)
                .aggregationTypeEnum(AggregationTypeEnum.SURICATA_CATEGORY)
                .destinationIps(batchSceneHandler.getAllIpAddrs())
                .severityEnum(SeverityEnum.ALL)
                .build();
        flowAnalysisRequest.setGroupList(batchSceneHandler.getFlowCollectors());
        TeamAttackBO teamAttackBO = flowAnalysisService.queryOneTeamAttackSumList(flowAnalysisRequest);
        return new ServiceResult<>(teamAttackBO);
    }

    private <T> Map<T, Integer> doGroupCountFlowByIp(List<String> flowCollectors, List<String> ips, Integer topSize, LocalDateTime startTime, LocalDateTime endTime) {
        FlowAnalysisRequest request = new FlowAnalysisRequest();
        request.setAggregationTypeEnum(AggregationTypeEnum.SOURCE_IP);
        request.setGroupList(flowCollectors);
        request.setStartDateTime(startTime);
        request.setEndDateTime(endTime);
        request.setSourceIps(ips);
        request.setTopSize(topSize);
        List<AttackWayAggregationBO> result = flowAnalysisService.topByAggregationTypeEnum(request);
        if (CollectionUtils.isEmpty(result)) {
            new HashMap<>();
        }
        Map<T, Integer> finalResult = new HashMap<>();
        result.forEach(bo -> finalResult.put((T) bo.getAggregationType(), bo.getAttackNumber().intValue()));
        return finalResult;
    }

    private long byte2MB(long num) {
        return num >> 20;
    }

    private long byte2GB(long num) {
        return num >> 30;
    }

    public ListObjServiceResult<Port> listPagePorts(ListPageInfoQueryParams listPageInfoQueryParams) {
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(listPageInfoQueryParams.getSceneIds(), null);
        Map<String, String> nodeId2Eidmap = batchSceneHandler.getProbeEnabledNodeId2EidMap();
        CollectorBaseRequest collectorBaseRequest = new CollectorBaseRequest();
        PageInfo<Port> pageInfo = new PageInfo();
        collectorBaseRequest.setAgentName(nodeId2Eidmap.get(listPageInfoQueryParams.getNodeIds().get(0)));
        collectorBaseRequest.setPage(listPageInfoQueryParams.getPage() == null ? 1 : listPageInfoQueryParams.getPage());
        collectorBaseRequest.setPageSize(listPageInfoQueryParams.getPageSize() == null ? 10 : listPageInfoQueryParams.getPageSize());
        collectorBaseRequest.setPerPage(listPageInfoQueryParams.getPerPage());
        pageInfo = sysCollectorService.pagePorts(collectorBaseRequest);
        return new ListObjServiceResult<>(new ListObjResult<>(pageInfo.getList(), (int) pageInfo.getTotal()).asResult()).asResult();
    }

    public ListObjServiceResult<Process> listPageProcess(ListPageInfoQueryParams listPageInfoQueryParams) {
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(listPageInfoQueryParams.getSceneIds(), null);
        Map<String, String> nodeId2Eidmap = batchSceneHandler.getProbeEnabledNodeId2EidMap();
        CollectorBaseRequest collectorBaseRequest = new CollectorBaseRequest();
        PageInfo<Process> pageInfo = new PageInfo<>();
        collectorBaseRequest.setAgentName(nodeId2Eidmap.get(listPageInfoQueryParams.getNodeIds().get(0)));
        collectorBaseRequest.setPage(listPageInfoQueryParams.getPage() == null ? 1 : listPageInfoQueryParams.getPage());
        collectorBaseRequest.setPageSize(listPageInfoQueryParams.getPageSize() == null ? 10 : listPageInfoQueryParams.getPageSize());
        collectorBaseRequest.setPerPage(listPageInfoQueryParams.getPerPage());
        pageInfo = sysCollectorService.pageProcesses(collectorBaseRequest);
        return new ListObjServiceResult<>(new ListObjResult<>(pageInfo.getList(), (int) pageInfo.getTotal()).asResult()).asResult();
    }

    /**
     * 查询场景中所有启动探针的节点列表
     * @param sceneId
     * @return
     */
    public List<SceneElementBO> listProbeEnabledElementBOs(String sceneId) {
        log.info("sceneId:{}", sceneId);
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(CollectionUtil.toList(sceneId), null);
        // 查询场景中所有开启探针的agentNames集合
        Map<String, String> nodeId2Eidmap = batchSceneHandler.getProbeEnabledNodeId2EidMap();

        // 根据每一个有效的eid(agentName)查询ElementBO
        List<SceneElementBO> elementBOS = nodeId2Eidmap.values().stream().map(eid -> batchSceneHandler.getNodeByEid(eid)).collect(Collectors.toList());
        return elementBOS;
    }


    public List<String> listIPs(String sceneId, List<String> nodeIds) {
        log.info("sceneId:{}, nodeIds:{}", sceneId, nodeIds);
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(CollectionUtil.toList(sceneId), nodeIds);
        return batchSceneHandler.getAllIps();
    }

    public List<SceneAssetsBO> listSceneAssets(List<String> sceneIds) {
        List<SceneAssetsBO> sceneAssets = new ArrayList<>();
        sceneIds.forEach(s -> {
            BatchSceneHandler batchSceneHandler = new BatchSceneHandler(CollectionUtil.toList(s), null);
            SceneAssetsBO asset = new SceneAssetsBO();
            asset.setSceneId(s);
            asset.setIpCount(batchSceneHandler.getAllIps().size());
            asset.setNodeCount(batchSceneHandler.getAllEid2ElementMap().keySet().size());
            asset.setCategoryGroups(new ArrayList<>(batchSceneHandler.getCategory2CountInfo().values()));
            sceneAssets.add(asset);
        });
        return sceneAssets;
    }

}
