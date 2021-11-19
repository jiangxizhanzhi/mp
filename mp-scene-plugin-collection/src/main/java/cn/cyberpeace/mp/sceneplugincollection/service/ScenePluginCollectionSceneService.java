package cn.cyberpeace.mp.sceneplugincollection.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionFlowBO;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
import cn.cyberpeace.mp.situationaware.model.SessionRequest;
import cn.cyberpeace.mp.situationaware.model.SessionResult;
import cn.cyberpeace.mp.situationaware.service.NetworkTrafficService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ScenePluginCollectionSceneService {

    @Autowired
    private NetworkTrafficService networkTrafficService;

    public ListObjServiceResult<CollectionFlowBO> listFlows(String sceneId, FlowQueryParams queryParams) {
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        List<SceneElementBO> srcElements;
        List<String> nodeIds = queryParams.getNodeIds() == null ? null : Arrays.asList(queryParams.getNodeIds());
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

        if (!flowCollectMacAddrs.isEmpty()) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        var sessionRequest = new SessionRequest();
        sessionRequest.setGroupList(List.of(sceneId));
        sessionRequest.setPageNum(queryParams.getPage());
        sessionRequest.setPageSize(queryParams.getPageSize());
        sessionRequest.setProtocol(queryParams.getProtocol());
        var startTimeStr = queryParams.getStartTime();
        if (!StringUtils.isEmpty(startTimeStr)) {
            // TODO 统一转换调用
            sessionRequest.setStartTime(LocalDateTime.parse(startTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        var endTimeStr = queryParams.getEndTime();
        if (!StringUtils.isEmpty(endTimeStr)) {
            // TODO 统一转换调用
            sessionRequest.setEndTime(LocalDateTime.parse(endTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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

            var timestamp = srcFlow.getTimestamp();
            flow.setTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));

            var sourceMac = flow.getSourceMac();
            //todo 多个mac使用map获取存在问题
            var sourceElement = macElementMap.getOrDefault(sourceMac, null);
            if (sourceElement != null) {
                flow.setSourceNodeId(sourceElement.getId());
                flow.setSourceNodeName(sourceElement.getName());
            }
            var targetMac = flow.getTargetMac();
            //todo 多个mac使用map获取存在问题
            var targetElement = macElementMap.getOrDefault(targetMac, null);
            if (targetElement != null) {
                flow.setTargetNodeId(targetElement.getId());
                flow.setTargetNodeName(targetElement.getName());
            }
        });

        return new ListObjServiceResult<>(new ListObjResult<>(flows, result.getTotal()));
    }

}
