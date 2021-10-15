package cn.cyberpeace.mp.sceneplugincloud.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.FloatUtils;
import cn.cyberpeace.mp.base.util.IntUtils;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeRecordingBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeSessionBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GetGuacamoleRecordingData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GuacamoleRecordingData;
import cn.cyberpeace.mp.sceneplugincloud.service.param.RecordingQueryParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScenePluginCloudSceneService {

    @Data
    @AllArgsConstructor
    private class ConnectionLinkInfo {
        private String connectionId;
        private CloudAccessMode accessMode;
        private CloudPluginSceneElementPortBO cloudPort;
        private CloudPluginSceneElementBO cloudElement;
    }

    private Map<String, ConnectionLinkInfo> collectConnectionLinkInfo(String sceneId, String[] nodeIds) {
        Map<String, ConnectionLinkInfo> connectionLinkInfoMap = new HashMap<>();

        var filterNodeIds = nodeIds == null ? null : Arrays.asList(nodeIds);
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        pluginHandler.getElementMap().forEach((nodeId, cloudElement) -> {
            if (filterNodeIds != null && !filterNodeIds.isEmpty() && !filterNodeIds.contains(nodeId)) {
                return;
            }

            var srcElement = cloudElement.getSrcElement();
            var srcPorts = srcElement.getPorts();
            if (srcPorts == null) {
                return;
            }
            srcPorts.forEach(srcPort -> {
                var cloudPort = pluginHandler.getPort(srcPort.getId());
                if (cloudPort == null) {
                    return;
                }
                var accessModes = cloudPort.getAccessModes();
                if (accessModes == null) {
                    return;
                }
                accessModes.forEach(accessMode -> {
                    var connectionId = accessMode.getConnectionId();
                    if (StringUtils.isEmpty(connectionId)) {
                        return;
                    }

                    connectionLinkInfoMap.put(connectionId,
                            new ConnectionLinkInfo(connectionId, accessMode, cloudPort, cloudElement));
                });
            });
        });

        return connectionLinkInfoMap;
    }

    public ServiceResult<List<NodeSessionBO>> getSceneRemoteSessions(String sceneId, String[] nodeIds, boolean monitor,
                                                                     boolean assistance) {
        if (!monitor && !assistance) {
            return new ServiceResult<>(new ArrayList<>());
        }

        var connectionLinkInfoMap = collectConnectionLinkInfo(sceneId, nodeIds);
        String[] connectionIds = connectionLinkInfoMap.keySet().toArray(new String[0]);
        if (connectionIds.length == 0) {
            return new ServiceResult<>(new ArrayList<>());
        }

        var remoteClient = new Remote();
        Map<String, NodeSessionBO> connectionIdSessionMap = new HashMap<>();
        if (monitor) {
            var monitors = remoteClient.getMonitors(connectionIds);
            if (monitors != null) {
                monitors.forEach((connectionId, monitorData) ->{
                    var nodeSession = connectionIdSessionMap.getOrDefault(connectionId,
                            new NodeSessionBO());
                    nodeSession.setMonitorUrl(monitorData.getMonitorUrl());
                    connectionIdSessionMap.put(connectionId, nodeSession);
                });
            }
        }

        if (assistance) {
            var assistances = remoteClient.getAssistances(connectionIds);
            if (assistances != null) {
                assistances.forEach((connectionId, assistanceData) ->{
                    var nodeSession = connectionIdSessionMap.getOrDefault(connectionId,
                            new NodeSessionBO());
                    nodeSession.setAssistanceUrl(assistanceData.getAssistanceUrl());
                    connectionIdSessionMap.put(connectionId, nodeSession);
                });
            }
        }

        connectionIdSessionMap.forEach((connectionId, nodeSession) -> {
            var connectionLinkInfo = connectionLinkInfoMap.get(connectionId);
            var cloudElement = connectionLinkInfo.getCloudElement();
            var srcElement = cloudElement.getSrcElement();
            nodeSession.setNodeId(srcElement.getId());
            nodeSession.setNodeName(srcElement.getName());
            var accessMode = connectionLinkInfo.getAccessMode();
            nodeSession.setProtocol(accessMode.getProtocol());
            nodeSession.setUser(accessMode.getConnectionUser());
        });

        return new ServiceResult<>(new ArrayList<>(connectionIdSessionMap.values()));
    }

    public ListObjServiceResult<NodeRecordingBO> getSceneRemoteRecordings(String sceneId, String[] nodeIds,
                                                                          RecordingQueryParams queryParams) {
        if (StringUtils.isEmpty(sceneId)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        var connectionLinkInfoMap = collectConnectionLinkInfo(sceneId, nodeIds);
        String[] connectionIds = connectionLinkInfoMap.keySet().toArray(new String[0]);
        if (connectionIds.length == 0) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        var remoteClient = new Remote();
        var queryData = new GetGuacamoleRecordingData();
        queryData.setConnectionID(Arrays.asList(connectionIds));
        queryData.setPage(queryParams.getPage());
        queryData.setLimit(queryParams.getPageSize());
        queryData.setParams(queryParams.getSearch());
        queryData.setStartTime(queryParams.getStartTime());
        queryData.setEndTime(queryParams.getEndTime());
        ListObjResult<GuacamoleRecordingData.RecordingInfo> rawRecordings = remoteClient.getRecordings(queryData);
        List<NodeRecordingBO> recordings = rawRecordings.getObjs().stream().map(rawRecording -> {
            var connectionLinkInfo = connectionLinkInfoMap.get(rawRecording.getConnectionId());
            var cloudElement = connectionLinkInfo.getCloudElement();
            var srcElement = cloudElement.getSrcElement();
            var accessMode = connectionLinkInfo.getAccessMode();

            var recording = new NodeRecordingBO();
            recording.setNodeId(srcElement.getId());
            recording.setNodeName(srcElement.getName());
            recording.setUser(rawRecording.getUser());
            recording.setProtocol(accessMode.getProtocol());
            recording.setName(rawRecording.getRecordingName());
            recording.setFilename(rawRecording.getRecordingFile());
            var filesizeStr = rawRecording.getFileSize();
            if (!StringUtils.isEmpty(filesizeStr)) {
                recording.setFilesize(IntUtils.fromStr(filesizeStr));
            }
            var durationStr = rawRecording.getRecortime();
            if (!StringUtils.isEmpty(durationStr)) {
                recording.setDuration(FloatUtils.fromStr(durationStr));
            }
            var timeStr = rawRecording.getRecordate();
            if (!StringUtils.isEmpty(timeStr)) {
                // TODO 统一转换调用
                recording.setTime(LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ssZ")));
            }

            return recording;
        }).collect(Collectors.toList());

        return new ListObjServiceResult<>(new ListObjResult<>(recordings, rawRecordings.getTotal()));
    }

}
