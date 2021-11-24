package cn.cyberpeace.mp.sceneplugincloud.service;

import cn.cyberpeace.mp.base.bo.GroupCountBO;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Count;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.FloatUtils;
import cn.cyberpeace.mp.base.util.basic.IntUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.*;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GetGuacamoleRecordingData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GuacamoleRecordingData;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.service.param.RecordingQueryParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScenePluginCloudSceneService {

    public static final List<String> AVAILABLE_CLOUD_ELEMENT_GROUP_COUNT_FIELDS = Arrays.asList(
            BeanUtils.getBeanGetterFieldNames(
                    BaseCloudPluginSceneElementModel::getVirtualType
            )
    );

    private CloudPluginSceneElementMapper cloudPluginSceneElementMapper;

    @Data
    @AllArgsConstructor
    private class ConnectionLinkInfo {
        private String connectionId;
        private CloudAccessMode accessMode;
        private CloudPluginSceneElementPortBO cloudPort;
        private CloudPluginSceneElementBO cloudElement;
        private SceneBO srcScene;
    }

    private Map<String, ConnectionLinkInfo> collectConnectionLinkInfo(String sceneId, List<String> nodeIds) {
        Map<String, ConnectionLinkInfo> connectionLinkInfoMap = new HashMap<>();

        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        var scene = sceneHandler.getScene();
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        pluginHandler.getElementMap().forEach((nodeId, cloudElement) -> {
            if (nodeIds != null && !nodeIds.isEmpty() && !nodeIds.contains(nodeId)) {
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
                            new ConnectionLinkInfo(connectionId, accessMode, cloudPort, cloudElement, scene));
                });
            });
        });

        return connectionLinkInfoMap;
    }

    private Map<String, ConnectionLinkInfo> collectConnectionLinkInfo(List<String> sceneIds, List<String> nodeIds) {
        Map<String, ConnectionLinkInfo> connectionLinkInfoMap = new HashMap<>();
        for (var sceneId : sceneIds) {
            connectionLinkInfoMap.putAll(collectConnectionLinkInfo(sceneId, nodeIds));
        }
        return connectionLinkInfoMap;
    }

    @Autowired
    public ScenePluginCloudSceneService(CloudPluginSceneElementMapper cloudPluginSceneElementMapper) {
        this.cloudPluginSceneElementMapper = cloudPluginSceneElementMapper;
    }

    public ServiceResult<List<NodeSessionBO>> getSceneRemoteSessions(List<String> sceneIds, List<String> nodeIds,
                                                                     boolean monitor, boolean assistance) {
        if (!monitor && !assistance) {
            return new ServiceResult<>(new ArrayList<>());
        }

        var connectionLinkInfoMap = collectConnectionLinkInfo(sceneIds, nodeIds);
        String[] connectionIds = connectionLinkInfoMap.keySet().toArray(new String[0]);
        if (connectionIds.length == 0) {
            return new ServiceResult<>(new ArrayList<>());
        }

        var remoteClient = new Remote();
        Map<String, NodeSessionBO> connectionIdSessionMap = new HashMap<>();
        if (monitor) {
            var monitors = remoteClient.getMonitors(connectionIds);
            if (monitors != null) {
                monitors.forEach((connectionId, monitorData) -> {
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
                assistances.forEach((connectionId, assistanceData) -> {
                    var nodeSession = connectionIdSessionMap.getOrDefault(connectionId,
                            new NodeSessionBO());
                    nodeSession.setAssistanceUrl(assistanceData.getAssistanceUrl());
                    connectionIdSessionMap.put(connectionId, nodeSession);
                });
            }
        }

        connectionIdSessionMap.forEach((connectionId, nodeSession) -> {
            var connectionLinkInfo = connectionLinkInfoMap.get(connectionId);
            var srcScene = connectionLinkInfo.getSrcScene();
            var cloudElement = connectionLinkInfo.getCloudElement();
            var srcElement = cloudElement.getSrcElement();
            nodeSession.setSceneId(srcScene.getEid());
            nodeSession.setSceneName(srcScene.getName());
            nodeSession.setNodeId(srcElement.getId());
            nodeSession.setNodeName(srcElement.getName());
            var accessMode = connectionLinkInfo.getAccessMode();
            nodeSession.setProtocol(accessMode.getProtocol());
            nodeSession.setUser(accessMode.getConnectionUser());
        });

        return new ServiceResult<>(new ArrayList<>(connectionIdSessionMap.values()));
    }

    public ListObjServiceResult<NodeRecordingBO> getSceneRemoteRecordings(List<String> sceneIds, List<String> nodeIds,
                                                                          RecordingQueryParams queryParams) {
        var connectionLinkInfoMap = collectConnectionLinkInfo(sceneIds, nodeIds);
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
            var srcScene = connectionLinkInfo.getSrcScene();
            var cloudElement = connectionLinkInfo.getCloudElement();
            var srcElement = cloudElement.getSrcElement();
            var accessMode = connectionLinkInfo.getAccessMode();

            var recording = new NodeRecordingBO();
            recording.setSceneId(srcScene.getEid());
            recording.setSceneName(srcScene.getName());
            recording.setNodeId(srcElement.getId());
            recording.setNodeName(srcElement.getName());
            recording.setUser(rawRecording.getUser());
            recording.setProtocol(accessMode.getProtocol());
            recording.setName(rawRecording.getRecordingName());
            recording.setFilename(rawRecording.getRecordingFile());
            recording.setFileUrl(StringUtils.join(CloudSingleton.cloudAppConfig.getCpcloudServer(),
                    String.format(CloudUrlConstant.RECORDING_DOWNLOAD_URL, rawRecording.getRecordingFile())));
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
                recording.setTime(TimeUtils.utcStringToLocalDateTime(timeStr));
            }

            return recording;
        }).collect(Collectors.toList());

        return new ListObjServiceResult<>(new ListObjResult<>(recordings, rawRecordings.getTotal()));
    }

    public ServiceResult<NodeActionRecordingBO> enableRemoteRecording(String sceneId, String nodeId, String connectionId) {
        var connectionLinkInfoMap = collectConnectionLinkInfo(sceneId, List.of(nodeId));
        if (!connectionLinkInfoMap.containsKey(connectionId)) {
            return new ServiceResult<>(null, CloudErrorCode.CONNECTION_NOT_EXIST);
        }

        var remoteClient = new Remote();
        var result = remoteClient.enableRecording(connectionId);
        var actionRecordingBO = new NodeActionRecordingBO();
        actionRecordingBO.setRecordingName(result);
        return new ServiceResult<>(actionRecordingBO);
    }

    public ServiceResult<NodeActionRecordingBO> disableRemoteRecording(String sceneId, String nodeId, String connectionId) {
        var connectionLinkInfoMap = collectConnectionLinkInfo(sceneId, List.of(nodeId));
        if (!connectionLinkInfoMap.containsKey(connectionId)) {
            return new ServiceResult<>(null, CloudErrorCode.CONNECTION_NOT_EXIST);
        }

        var remoteClient = new Remote();
        var result = remoteClient.disableRecording(connectionId);
        var actionRecordingBO = new NodeActionRecordingBO();
        actionRecordingBO.setRecordingName(result);
        return new ServiceResult<>(actionRecordingBO);
    }

    public <T> ServiceResult<Map<T, Integer>> groupCountElement(List<String> sceneIds, List<String> nodeIds,
                                                                String groupFieldName) {
        if (!AVAILABLE_CLOUD_ELEMENT_GROUP_COUNT_FIELDS.contains(groupFieldName)) {
            throw new MpRuntimeException(CloudErrorCode.INVALID_GROUP_COUNT_FIELD);
        }

        var condition = new HumanCondition()
                .link(SceneModel.class, CloudPluginSceneElementModel.class)
                .filterLink(SceneModel.class, SceneModel::getId,
                        CloudPluginSceneElementModel.class, CloudPluginSceneElementModel::getScene)
                .currentLink(SceneModel.class)
                .filterEqual(SceneModel::getDeleted, false)
                .currentLink(CloudPluginSceneElementModel.class)
                .selectAs(groupFieldName, GroupCountBO<T>::getTarget)
                .selectAs(Count.f(groupFieldName), GroupCountBO<T>::getCount)
                .groupBy(groupFieldName);
        if (!CollectionUtils.isEmpty(sceneIds)) {
            condition.filterIn(BaseCloudPluginSceneElementModel::getScene, sceneIds.toArray(new String[0]));
        }
        if (!CollectionUtils.isEmpty(nodeIds)) {
            condition.filterIn(BaseCloudPluginSceneElementModel::getSceneElement, nodeIds.toArray(new String[0]));
        }

        List<? extends GroupCountBO> groupCountBOs;
        try {
            groupCountBOs = cloudPluginSceneElementMapper.list(condition, new GroupCountBO<T>().getClass());
        } catch (Exception e) {
            log.error("Group count cloud element on field {} error: {}", groupFieldName, e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, e);
        }

        Map<T, Integer> result = groupCountBOs.stream()
                .collect(Collectors.toMap(groupCountBO -> (T) groupCountBO.getTarget(),
                        groupCountBO -> groupCountBO.getCount()));
        return new ServiceResult<>(result);
    }

}
