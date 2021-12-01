package cn.cyberpeace.mp.sceneplugincollection.service;

import cn.cyberpeace.mp.base.bo.IntervalSizeBO;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionFlowBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostStateBO;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostStateQueryParams;
import cn.cyberpeace.mp.situationaware.bo.FlowLogBO;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.situationaware.request.FlowAnalysisRequest;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.FlowAnalysisService;
import cn.cyberpeace.mp.situationaware.service.NetworkTrafficService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScenePluginCollectionSceneService {

    public static class GroupCountFlowField {
        public static final String LEVEL = "level";
    }

    public static class GroupCountHostEventField {
        public static final String EVENT_TYPE = "eventType";
        public static final String NODE_NAME = "nodeName";
    }

    @Autowired
    private NetworkTrafficService networkTrafficService;

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private FlowAnalysisService flowAnalysisService;

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
        return new ServiceResult<>(new HashMap<>());
    }

    public ListObjServiceResult<CollectionFlowBO> listFlows(String sceneId, FlowQueryParams queryParams) {
        if (StringUtils.isBlank(queryParams.getStartTime()) || StringUtils.isBlank(queryParams.getEndTime())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
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
        request.setStartDateTime(TimeUtils.stringToLocalDateTime(queryParams.getStartTime()));
        request.setEndDateTime(TimeUtils.stringToLocalDateTime(queryParams.getEndTime()));
        List<FlowLogBO> flowLogBOS = flowAnalysisService.flowLog(request);
        if (CollectionUtils.isEmpty(flowLogBOS)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        List<CollectionFlowBO> list = flowLogBOS.stream().map(flowLogBO -> this.buildCollectionFlowBOFrom(sceneId, flowLogBO, macElementMap)).collect(Collectors.toList());
        return new ListObjServiceResult<>(new ListObjResult<>(list, list.size()));
    }

    public ListObjServiceResult<CollectionHostStateBO> listHostStates(String sceneId, HostStateQueryParams queryParams) {
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        List<SceneElementBO> srcElements = getSrcElements(sceneHandler, queryParams.getNodeIds());

        CollectionScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        Map<String, SceneElementBO> agentNameElementMap = new HashMap<>();
        srcElements.forEach(srcElement -> {
            var collectionElement = pluginHandler.getElement(srcElement.getId());
            if (collectionElement == null || CollectionUtils.isEmpty(collectionElement.getProbes())) {
                return;
            }
            agentNameElementMap.put(srcElement.getId(), srcElement);
        });

        if (agentNameElementMap.isEmpty()) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        var request = new WazuhMonitorEventsRequest();
        request.setPageNum(queryParams.getPage());
        request.setPageSize(queryParams.getPageSize());
        request.setAgentNames(new ArrayList<>(agentNameElementMap.keySet()));
        var result = dataCollectionService.queryMonitorEvents(request);

        List<CollectionHostStateBO> hostStates = new ArrayList<>();
        result.getObjs().forEach(srcHostState -> {
            var hostState = new CollectionHostStateBO();
            hostState.setSceneId(sceneId);
            hostState.setCpuCount(srcHostState.getCpuCount());
            hostState.setCpuPercent(srcHostState.getCpuTotalPercent());
            hostState.setMemoryTotal(srcHostState.getMemoryTotal());
            hostState.setMemoryFree(srcHostState.getMemoryFree());
            hostState.setMemoryPercent(srcHostState.getMemoryPercent());
            hostState.setDiskSpace(srcHostState.getDiskSpace());
            hostState.setTime(srcHostState.getTime());

            var srcElement = agentNameElementMap.getOrDefault(srcHostState.getAgentName(), null);
            if (srcElement != null) {
                hostState.setNodeId(srcElement.getId());
                hostState.setNodeName(srcElement.getName());
            }
        });

        return new ListObjServiceResult<>(new ListObjResult<>(hostStates, result.getTotal()));
    }

    public ListObjServiceResult<CollectionHostEventBO> listHostEvents(String sceneId, HostEventQueryParams queryParams) {
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        List<SceneElementBO> srcElements = getSrcElements(sceneHandler, queryParams.getNodeIds());
        CollectionScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        Map<String, SceneElementBO> agentNameElementMap = new HashMap<>();
        srcElements.forEach(srcElement -> {
            var collectionElement = pluginHandler.getElement(srcElement.getId());
            if (collectionElement == null || CollectionUtils.isEmpty(collectionElement.getProbes())) {
                return;
            }
            agentNameElementMap.put(srcElement.getId(), srcElement);
        });

        if (agentNameElementMap.isEmpty()) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        var request = new WazuhEventRequest();
        request.setPageNum(queryParams.getPage());
        request.setPageSize(queryParams.getPageSize());
        request.setAgentNames(new ArrayList<>(agentNameElementMap.keySet()));
        var startTimeStr = queryParams.getStartTime();
        if (!StringUtils.isEmpty(startTimeStr)) {
            request.setStartTime(TimeUtils.stringToLocalDateTime(startTimeStr));
        }
        var endTimeStr = queryParams.getEndTime();
        if (!StringUtils.isEmpty(endTimeStr)) {
            request.setEndTime(TimeUtils.stringToLocalDateTime(endTimeStr));
        }
        var result = dataCollectionService.queryFileEvents(request);

        List<CollectionHostEventBO> hostEvents = new ArrayList<>();
        result.getObjs().forEach(srcHostEvent -> {
            var hostEvent = new CollectionHostEventBO();
            hostEvent.setSceneId(sceneId);
            hostEvent.setEventType(srcHostEvent.getFileEventType().name());
            hostEvent.setEventDesc(srcHostEvent.getAction());
            hostEvent.setTime(srcHostEvent.getUpdateTime());

            var srcElement = agentNameElementMap.getOrDefault(srcHostEvent.getAgentName(), null);
            if (srcElement != null) {
                hostEvent.setNodeId(srcElement.getId());
                hostEvent.setNodeName(srcElement.getName());
            }
        });

        return new ListObjServiceResult<>(new ListObjResult<>(hostEvents, result.getTotal()));
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
                List<CollectionFlowBO.AttackInfo> attackInfos = new ArrayList<>();
                flowBO.setAttackInfos(attackInfos);
                for (int i = 0; i < attackInfoNum; i++) {
                    attackInfos.add(new CollectionFlowBO.AttackInfo(severity.get(i), category.get(i)));
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
    public ServiceResult<List<IntervalSizeBO<LocalDateTime>>> timeIntervalCountFlow(
            List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime) {
        return new ServiceResult<>(new ArrayList<>());
    }

    /**
     * 时间区间统计流量大小
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param intervalNumber 间隔数量
     * @return 聚合统计结果
     */
    public ServiceResult<List<IntervalSizeBO<LocalDateTime>>> timeIntervalSumFlowSize(
            List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime,
            int intervalNumber) {
        return new ServiceResult<>(new ArrayList<>());
    }

    /**
     * 时间区间统计主机cpu占用平均值
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param intervalNumber 间隔数量
     * @return 聚合统计结果
     */
    public ServiceResult<List<IntervalSizeBO<LocalDateTime>>> timeIntervalAvgHostCpuPercent(
            List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime,
            int intervalNumber) {
        return new ServiceResult<>(new ArrayList<>());
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
    public ServiceResult<List<IntervalSizeBO<LocalDateTime>>> timeIntervalAvgHostMemoryPercent(
            List<String> sceneIds, List<String> nodeIds, LocalDateTime startTime, LocalDateTime endTime,
            int intervalNumber) {
        return new ServiceResult<>(new ArrayList<>());
    }

}
