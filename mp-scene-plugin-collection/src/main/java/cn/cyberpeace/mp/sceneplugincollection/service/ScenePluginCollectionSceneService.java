package cn.cyberpeace.mp.sceneplugincollection.service;

import cn.cyberpeace.mp.base.bo.IntervalSizeBO;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.bo.AttackInfo;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionFlowBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostStateBO;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostStateQueryParams;
import cn.cyberpeace.mp.situationaware.bo.AttackDateHistogramNetworkBytesBO;
import cn.cyberpeace.mp.situationaware.bo.AttackWayAggregationBO;
import cn.cyberpeace.mp.situationaware.bo.FlowLogBO;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhModel;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.situationaware.model.enums.AggregationTypeEnum;
import cn.cyberpeace.mp.situationaware.request.FlowAnalysisRequest;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.FlowAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScenePluginCollectionSceneService {
    private static String LOG_PREFIX = "collection service -> ";

    public static class GroupCountFlowField {
        public static final String LEVEL = "level";
    }

    public static class GroupCountHostEventField {
        public static final String EVENT_TYPE = "eventType";
        public static final String NODE_NAME = "nodeName";
    }

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private FlowAnalysisService flowAnalysisService;

    public ListObjServiceResult<CollectionFlowBO> listFlows(String sceneId, FlowQueryParams queryParams) {
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        List<SceneElementBO> srcElements = getSrcElements(sceneHandler, queryParams.getNodeIds());
        CollectionScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (pluginHandler == null || pluginHandler.getScene() == null || pluginHandler.getScene().getFlowCollector() == null) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        Map<String, SceneElementBO> macElementMap = new HashMap<>();
        List<String> flowCollectMacs = this.loadMac2ElementBOMapAndGetEnableMac(macElementMap, srcElements, pluginHandler);
        if (flowCollectMacs.isEmpty()) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        FlowAnalysisRequest request = new FlowAnalysisRequest();
        request.setGroupList(Arrays.asList(pluginHandler.getScene().getFlowCollector()));
        request.setSecondsInterval(queryParams.getInterval());
        request.setSourceMacs(flowCollectMacs);

        // 查询时间段 默认使用场景创建到当前时间
        if (StringUtils.isBlank(queryParams.getStartTime())) {
            request.setStartDateTime(sceneHandler.getScene().getCreateTime());
        } else {
            request.setStartDateTime(TimeUtils.stringToLocalDateTime(queryParams.getStartTime()));
        }
        if (StringUtils.isBlank(queryParams.getEndTime())) {
            request.setEndDateTime(LocalDateTime.now());
        } else {
            request.setEndDateTime(TimeUtils.stringToLocalDateTime(queryParams.getEndTime()));
        }

        List<FlowLogBO> flowLogBOS = flowAnalysisService.flowLog(request);
        if (CollectionUtils.isEmpty(flowLogBOS)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        List<CollectionFlowBO> list = flowLogBOS.stream().map(flowLogBO -> this.buildCollectionFlowBOFrom(sceneId, flowLogBO, macElementMap)).collect(Collectors.toList());
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

        var result = dataCollectionService.queryFileEvents(request);

        List<CollectionHostEventBO> hostEvents = new ArrayList<>();
        result.getObjs().forEach(srcHostEvent -> {
            var hostEvent = new CollectionHostEventBO();
            hostEvent.setSceneId(sceneId);
            hostEvent.setEventType(srcHostEvent.getFileEventType().name());
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
        return groupCountFlow(sceneIds, nodeIds, GroupCountFlowField.LEVEL, startTime, endTime);
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
            int intervalSeconds) {
        // todo 暂时先实现一个
        var sceneHandler = new SceneHandler(sceneIds.get(0), null, false, null);
        CollectionScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (pluginHandler == null || pluginHandler.getScene() == null || pluginHandler.getScene().getFlowCollector() == null) {
            log.info("{} query time interval sum flow size,sceneId:{},no collection plugin or no flowCollector", LOG_PREFIX, JsonCommon.stringify(sceneIds));
            return new ServiceResult<>(new ArrayList<>());
        }
        List<SceneElementBO> elements = new ArrayList<>(sceneHandler.getElementMap().values());
        // 根据参数筛选一下节点
        if (CollectionUtils.isNotEmpty(nodeIds)) {
            Set<String> effectNodeIds = new HashSet<>(nodeIds);
            elements = elements.stream().filter(e -> effectNodeIds.contains(e.getId())).collect(Collectors.toList());
        }
        // 获取mac地址
        List<String> macAddrs = this.loadMac2ElementBOMapAndGetEnableMac(new HashMap<>(), elements, pluginHandler);
        if (CollectionUtils.isEmpty(macAddrs)) {
            log.info("{} query time interval sum flow size,sceneIds:{},nodeIds:{},no enabled macAddr", LOG_PREFIX, JsonCommon.stringify(sceneIds), JsonCommon.stringify(nodeIds));
            return new ServiceResult<>(new ArrayList<>());
        }
        // 构造参数
        FlowAnalysisRequest request = new FlowAnalysisRequest();
        request.setSourceMacs(macAddrs);
        request.setGroupList(Arrays.asList(pluginHandler.getScene().getFlowCollector()));
        request.setStartDateTime(Optional.ofNullable(startTime)
                .orElse(sceneHandler.getScene().getCreateTime())
        );
        request.setEndDateTime(Optional.ofNullable(endTime)
                .orElse(LocalDateTime.now())
        );
        request.setSecondsInterval(intervalSeconds);
        // 执行查询
        List<AttackDateHistogramNetworkBytesBO> bos = flowAnalysisService.sumNetworkBytesByDateHistogramTimestamp(request);
        if (CollectionUtils.isEmpty(bos)) {
            return new ServiceResult<>(new ArrayList<>());
        }
        List<IntervalSizeBO<LocalDateTime, Long>> result = bos.stream()
                .map(bo -> {
                    LocalDateTime time = TimeUtils.stringToLocalDateTime(bo.getTime(), TimeUtils.tzMillsDateTimeFormatter)
                            .atZone(ZoneOffset.UTC)
                            .withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalDateTime();
                    return new IntervalSizeBO<LocalDateTime, Long>(time, time, bo.getNetworkBytes().longValue());
                }).collect(Collectors.toList());
        Collections.sort(result, Comparator.comparing(IntervalSizeBO::getStart));
        return new ServiceResult<>(result);
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

    private CollectionFlowBO buildCollectionFlowBOFrom(String sceneId, FlowLogBO flowLogBO, Map<String, SceneElementBO> mac2ElementMap) {
        CollectionFlowBO flowBO = new CollectionFlowBO();
        flowBO.setSceneId(sceneId);
        if (flowLogBO.getTimestamp() != null) {
            flowBO.setTime(TimeUtils.timestampToLocalDateTime(flowLogBO.getTimestamp()));
        }
        if (flowLogBO.getSource() != null) {
            flowBO.setSourceIp(flowLogBO.getSource().getIp());
            if (CollectionUtils.isNotEmpty(flowLogBO.getSource().getMac())) {
                for (int i = 0; i < flowLogBO.getSource().getMac().size(); i++) {
                    String mac = flowLogBO.getSource().getMac().get(i);
                    if (mac2ElementMap.containsKey(mac)) {
                        flowBO.setSourceNodeId(mac2ElementMap.get(mac).getId());
                        flowBO.setSourceNodeName(mac2ElementMap.get(mac).getName());
                        break;
                    }
                }
            }
        }
        if (flowLogBO.getDestination() != null) {
            flowBO.setTargetIp(flowLogBO.getDestination().getIp());
            if (CollectionUtils.isNotEmpty(flowLogBO.getSource().getMac())) {
                for (int i = 0; i < flowLogBO.getDestination().getMac().size(); i++) {
                    String mac = flowLogBO.getDestination().getMac().get(i);
                    if (mac2ElementMap.containsKey(mac)) {
                        flowBO.setTargetNodeId(mac2ElementMap.get(mac).getId());
                        flowBO.setTargetNodeName(mac2ElementMap.get(mac).getName());
                        break;
                    }
                }
            }
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
            List<String> sceneIds, List<String> nodeIds, String groupField, LocalDateTime startTime, LocalDateTime endTime) {
        FlowAnalysisRequest request = new FlowAnalysisRequest();
        if (GroupCountFlowField.LEVEL.equals(groupField)) {
            request.setAggregationTypeEnum(AggregationTypeEnum.SURICATA_SEVERITY);
        }
        // todo 暂时先实现一个
        var sceneHandler = new SceneHandler(sceneIds.get(0), null, false, null);
        CollectionScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (pluginHandler == null) {
            return new ServiceResult<>();
        }
        if (pluginHandler.getScene() == null || StringUtils.isEmpty(pluginHandler.getScene().getFlowCollector())) {
            return new ServiceResult<>();
        }
        request.setGroupList(Arrays.asList(pluginHandler.getScene().getFlowCollector()));

        if (startTime == null) {
            request.setStartDateTime(sceneHandler.getScene().getCreateTime());
        } else {
            request.setStartDateTime(startTime);
        }
        if (endTime == null) {
            request.setEndDateTime(LocalDateTime.now());
        } else {
            request.setEndDateTime(endTime);
        }
        List<AttackWayAggregationBO> result = flowAnalysisService.topByAggregationTypeEnum(request);
        if (CollectionUtils.isEmpty(result)) {
            return new ServiceResult<>(new HashMap<>());
        }
        Map<T, Integer> finalResult = new HashMap<>();
        result.forEach(bo -> finalResult.put((T) bo.getAggregationType(), bo.getAttackNumber().intValue()));
        return new ServiceResult<>(finalResult);
    }

    private long byte2MB(long num) {
        return num >> 20;
    }

    private long byte2GB(long num) {
        return num >> 30;
    }
}
