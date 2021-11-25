package cn.cyberpeace.mp.sceneplugincollection.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScenePluginCollectionSceneService {

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

    public ListObjServiceResult<CollectionFlowBO> listFlows(String sceneId, FlowQueryParams queryParams) {
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        List<SceneElementBO> srcElements = getSrcElements(sceneHandler, queryParams.getNodeIds());
        CollectionScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        Map<String, SceneElementBO> macElementMap = new HashMap<>();
        List<String> flowCollectMacs = this.loadMac2ElementBOMapAndGetEnableMac(macElementMap, srcElements, pluginHandler);
        if (flowCollectMacs.isEmpty()) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        FlowAnalysisRequest request = new FlowAnalysisRequest();
        request.setGroupList(Arrays.asList(sceneId));
        request.setSecondsInterval(queryParams.getInterval());
        request.setStartDateTime(TimeUtils.stringToLocalDateTime(queryParams.getStartTime()));
        request.setEndDateTime(TimeUtils.stringToLocalDateTime(queryParams.getEndTime()));
        request.setTopSize(10);
        List<FlowLogBO> flowLogBOS = flowAnalysisService.flowLog(request);
        if (CollectionUtils.isEmpty(flowLogBOS)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        List<CollectionFlowBO> list = flowLogBOS.stream().map(flowLogBO -> {
            return this.buildCollectionFlowBOFrom(sceneId, flowLogBO);
        }).collect(Collectors.toList());
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

    private CollectionFlowBO buildCollectionFlowBOFrom(String sceneId, FlowLogBO flowLogBO) {
        CollectionFlowBO flowBO = new CollectionFlowBO();
        flowBO.setSceneId(sceneId);
        if (flowLogBO.getSource() != null) {
            flowBO.setSourceIp(flowLogBO.getSource().getIp());
        }
        if (flowLogBO.getDestination() != null) {
            flowBO.setTargetIp(flowLogBO.getDestination().getIp());
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
}
