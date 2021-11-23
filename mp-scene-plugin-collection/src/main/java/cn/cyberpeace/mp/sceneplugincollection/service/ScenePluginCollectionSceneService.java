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
import cn.cyberpeace.mp.situationaware.model.SessionRequest;
import cn.cyberpeace.mp.situationaware.model.SessionResult;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.NetworkTrafficService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScenePluginCollectionSceneService {

    @Autowired
    private NetworkTrafficService networkTrafficService;

    @Autowired
    private DataCollectionService dataCollectionService;

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
        List<String> flowCollectMacAddrs = new ArrayList<>();
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
                macElementMap.put(macAddr, srcElement);

                var collectionPort = pluginHandler.getPort(srcPort.getId());
                if (collectionPort == null || !Boolean.TRUE.equals(collectionPort.getEnableFlowCollect())) {
                    return;
                }

                flowCollectMacAddrs.add(macAddr);
            });
        });

        if (flowCollectMacAddrs.isEmpty()) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        var sessionRequest = new SessionRequest();
        sessionRequest.setGroupList(List.of(sceneId));
        sessionRequest.setPageNum(queryParams.getPage());
        sessionRequest.setPageSize(queryParams.getPageSize());
        sessionRequest.setProtocol(queryParams.getProtocol());
        var startTimeStr = queryParams.getStartTime();
        if (!StringUtils.isEmpty(startTimeStr)) {
            sessionRequest.setStartTime(TimeUtils.stringToLocalDateTime(startTimeStr));
        }
        var endTimeStr = queryParams.getEndTime();
        if (!StringUtils.isEmpty(endTimeStr)) {
            sessionRequest.setEndTime(TimeUtils.stringToLocalDateTime(endTimeStr));
        }
        sessionRequest.setMacIds(flowCollectMacAddrs);
        ListObjResult<SessionResult> result = networkTrafficService.querySessionWithPage(sessionRequest);

        List<CollectionFlowBO> flows = new ArrayList<>();
        result.getObjs().forEach(srcFlow -> {
            var flow = new CollectionFlowBO();
            flow.setSceneId(sceneId);
            flow.setSourceIp(srcFlow.getSourceIp());
            flow.setTargetIp(srcFlow.getTargetIp());
            flow.setSourceMac(srcFlow.getSourceMac());
            flow.setTargetMac(srcFlow.getTargetMac());
            flow.setSourcePort(srcFlow.getSrcPort());
            flow.setTargetPort(srcFlow.getDstPort());
            flow.setProtocol(srcFlow.getProtocolType());
            flow.setSize(srcFlow.getTotalBytes());

            flow.setTime(TimeUtils.timestampToLocalDateTime(srcFlow.getTimestamp()));

            var sourceMacs = flow.getSourceMac();
            SceneElementBO sourceElement;
            for (var sourceMac : sourceMacs) {
                sourceElement = macElementMap.getOrDefault(sourceMac, null);
                if (sourceElement != null) {
                    flow.setSourceNodeId(sourceElement.getId());
                    flow.setSourceNodeName(sourceElement.getName());
                    break;
                }
            }

            var targetMacs = flow.getTargetMac();
            SceneElementBO targetElement;
            for (var targetMac : targetMacs) {
                targetElement = macElementMap.getOrDefault(targetMac, null);
                if (targetElement != null) {
                    flow.setTargetNodeId(targetElement.getId());
                    flow.setTargetNodeName(targetElement.getName());
                }
            }
        });

        return new ListObjServiceResult<>(new ListObjResult<>(flows, result.getTotal()));
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
}
