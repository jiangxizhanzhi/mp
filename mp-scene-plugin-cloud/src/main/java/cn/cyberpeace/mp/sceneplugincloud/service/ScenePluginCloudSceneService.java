package cn.cyberpeace.mp.sceneplugincloud.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.FloatUtils;
import cn.cyberpeace.mp.base.util.basic.IntUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeConnectionBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeRecordingBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeSessionBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConnectionModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementPortModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginContext;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GetGuacamoleRecordingData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GuacamoleRecordingData;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConnectionMapper;
import cn.cyberpeace.mp.sceneplugincloud.service.common.CloudSceneConnectionCommon;
import cn.cyberpeace.mp.sceneplugincloud.service.common.CloudSceneStatCommon;
import cn.cyberpeace.mp.sceneplugincloud.service.param.RecordingQueryParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScenePluginCloudSceneService {

    public static final List<String> COUNT_ELEMENT_FIELDS = Arrays.asList(
            BeanUtils.getBeanGetterFieldNames(
                    CloudPluginSceneElementModel::getHost,
                    CloudPluginSceneElementModel::getVirtualType
            )
    );

    public static final List<String> CALC_COUNT_ELEMENT_FIELDS = Arrays.asList(
            BeanUtils.getBeanGetterFieldNames(
                    CloudPluginSceneElementModel::getFirewallRules
            )
    );

    public static final List<String> COUNT_PORT_FIELDS = Arrays.asList(
            BeanUtils.getBeanGetterFieldNames(
                    CloudPluginSceneElementPortModel::getFloatingIp,
                    CloudPluginSceneElementPortModel::getAuxiliaryNetworkId
            )
    );

    public static final List<String> CALC_COUNT_PORT_FIELDS = Arrays.asList(
            BeanUtils.getBeanGetterFieldNames(
                    CloudPluginSceneElementPortModel::getFirewallRules
            )
    );

    private CloudSceneStatCommon cloudSceneStatCommon;

    private CloudPluginSceneConnectionMapper cloudPluginSceneConnectionMapper;

    @Data
    @AllArgsConstructor
    private static class AccessModeLinkInfo {
        private String accessModeId;
        private CloudAccessMode accessMode;
        private CloudPluginSceneElementPortBO cloudPort;
        private CloudPluginSceneElementBO cloudElement;
        private SceneBO srcScene;
        private CloudPluginSceneBO cloudScene;

        public NodeConnectionBO toNewNodeConnection() {
            var nodeConnection = new NodeConnectionBO();
            nodeConnection.setSceneId(srcScene.getEid());
            nodeConnection.setSceneName(srcScene.getName());
            nodeConnection.setNodeId(cloudElement.getSrcElement().getId());
            nodeConnection.setNodeName(cloudElement.getSrcElement().getName());
            nodeConnection.setProtocol(accessMode.getProtocol());
            return nodeConnection;
        }
    }

    private Map<String, AccessModeLinkInfo> collectAccessModeLinkInfo(String sceneId, List<String> nodeIds) {
        Map<String, AccessModeLinkInfo> accessModeLinkInfoMap = new HashMap<>();

        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        var scene = sceneHandler.getScene();
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        var cloudScene = pluginHandler.getScene();
        pluginHandler.getElementMap().forEach((nodeId, cloudElement) -> {
            if (nodeIds != null && !nodeIds.isEmpty() && !nodeIds.contains(nodeId)) {
                return;
            }

            var cloudPorts = pluginHandler.getPorts(cloudElement);
            cloudPorts.forEach(cloudPort -> {
                var accessModes = cloudPort.getAccessModes();
                if (accessModes == null) {
                    return;
                }
                accessModes.forEach(accessMode -> {
                    var accessModeId = accessMode.getId();
                    if (StringUtils.isEmpty(accessModeId)) {
                        return;
                    }

                    accessModeLinkInfoMap.put(accessModeId,
                            new AccessModeLinkInfo(accessModeId, accessMode, cloudPort, cloudElement, scene, cloudScene));
                });
            });
        });

        return accessModeLinkInfoMap;
    }

    private Map<String, AccessModeLinkInfo> collectAccessModeLinkInfo(List<String> sceneIds, List<String> nodeIds) {
        Map<String, AccessModeLinkInfo> accessModeLinkInfoMap = new HashMap<>();
        for (var sceneId : sceneIds) {
            accessModeLinkInfoMap.putAll(collectAccessModeLinkInfo(sceneId, nodeIds));
        }
        return accessModeLinkInfoMap;
    }

    @Data
    @AllArgsConstructor
    private static class ConnectionLinkInfo {
        private String connectionId;
        private CloudPluginSceneConnectionModel sceneConnection;
        private AccessModeLinkInfo accessModeLinkInfo;

        public NodeConnectionBO toNewNodeConnection() {
            var nodeConnection = accessModeLinkInfo.toNewNodeConnection();
            nodeConnection.setUser(sceneConnection.getAccessUser());
            nodeConnection.setConnectionId(sceneConnection.getConnectionId());
            nodeConnection.setConnectionEnableRecording(sceneConnection.getConnectionEnableRecording());
            nodeConnection.setConnectionRecordingName(sceneConnection.getConnectionRecordingName());
            nodeConnection.setConnectionUrl(sceneConnection.getConnectionUrl());
            return nodeConnection;
        }
    }

    private Map<String, ConnectionLinkInfo> collectConnectionLinkInfo(String sceneId, List<String> nodeIds,
                                                                      List<String> userIds) {
        Map<String, AccessModeLinkInfo> accessModeLinkInfoMap = collectAccessModeLinkInfo(sceneId, nodeIds);
        Map<String, ConnectionLinkInfo> connectionLinkInfoMap = new HashMap<>();
        if (accessModeLinkInfoMap.isEmpty()) {
            return connectionLinkInfoMap;
        }

        var condition = new HumanCondition()
                .filterEqual(CloudPluginSceneConnectionModel::getScene, sceneId)
                .filterNotEqual(CloudPluginSceneConnectionModel::getConnectionId, StringUtils.EMPTY)
                .filterIn(CloudPluginSceneConnectionModel::getAccessModeId,
                        accessModeLinkInfoMap.keySet().toArray(new String[0]));
        if (!CollectionUtils.isEmpty(userIds)) {
            condition.filterIn(CloudPluginSceneConnectionModel::getAccessUser, userIds.toArray());
        }
        var sceneConnections = cloudPluginSceneConnectionMapper.list(condition);
        sceneConnections.forEach(sceneConnection -> {
            var connectionId = sceneConnection.getConnectionId();
            var accessModeLinkInfo = accessModeLinkInfoMap.get(sceneConnection.getAccessModeId());
            var connectionLinkInfo = new ConnectionLinkInfo(connectionId, sceneConnection, accessModeLinkInfo);
            connectionLinkInfoMap.put(connectionId, connectionLinkInfo);
        });

        return connectionLinkInfoMap;
    }

    private Map<String, ConnectionLinkInfo> collectConnectionLinkInfo(List<String> sceneIds, List<String> nodeIds,
                                                                      List<String> userIds) {
        Map<String, ConnectionLinkInfo> connectionLinkInfoMap = new HashMap<>();
        for (var sceneId : sceneIds) {
            connectionLinkInfoMap.putAll(collectConnectionLinkInfo(sceneId, nodeIds, userIds));
        }
        return connectionLinkInfoMap;
    }

    private List<String> collectRecordingName(String sceneId) {
        List<String> connectionIds = new ArrayList<>();
        HumanCondition condition = new HumanCondition().filterEqual(CloudPluginSceneConnectionModel::getScene, sceneId);
        var sceneConnections = cloudPluginSceneConnectionMapper.list(condition);
        if (CollectionUtils.isEmpty(sceneConnections)) {
            return new ArrayList<>();
        }
        sceneConnections.forEach(sceneConnection -> {
            if (sceneConnection.getConnectionId() != null) {
                connectionIds.add(sceneConnection.getConnectionId());
            }
        });
        if (CollectionUtils.isNotEmpty(connectionIds)) {
            var remoteClient = new Remote();
            return remoteClient.getRecords(connectionIds);
        }
        return new ArrayList<>();
    }

    public void deleteSceneRecords(String sceneId, boolean logic) {
        if (logic) {
            return;
        }
        List<String> recordingNames = this.collectRecordingName(sceneId);
        if (CollectionUtils.isNotEmpty(recordingNames)) {
            var remoteClient = new Remote();
            remoteClient.deleteRecords(recordingNames);
        }
    }

    @Autowired
    public ScenePluginCloudSceneService(CloudSceneStatCommon cloudSceneStatCommon,
                                        CloudPluginSceneConnectionMapper cloudPluginSceneConnectionMapper) {
        this.cloudSceneStatCommon = cloudSceneStatCommon;
        this.cloudPluginSceneConnectionMapper = cloudPluginSceneConnectionMapper;
    }

    public ServiceResult<List<NodeSessionBO>> getSceneRemoteSessions(List<String> sceneIds, List<String> nodeIds,
                                                                     List<String> userIds, boolean monitor, boolean assistance) {
        if (!monitor && !assistance) {
            return new ServiceResult<>(new ArrayList<>());
        }

        var connectionLinkInfoMap = collectConnectionLinkInfo(sceneIds, nodeIds, userIds);
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
                    nodeSession.setUser(monitorData.getUsername());
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
                    nodeSession.setUser(assistanceData.getUsername());
                    connectionIdSessionMap.put(connectionId, nodeSession);
                });
            }
        }

        connectionIdSessionMap.forEach((connectionId, nodeSession) -> {
            var connectionLinkInfo = connectionLinkInfoMap.get(connectionId);
            var accessModeLinkInfo = connectionLinkInfo.getAccessModeLinkInfo();
            var srcScene = accessModeLinkInfo.getSrcScene();
            var cloudElement = accessModeLinkInfo.getCloudElement();
            var srcElement = cloudElement.getSrcElement();
            nodeSession.setSceneId(srcScene.getEid());
            nodeSession.setSceneName(srcScene.getName());
            nodeSession.setNodeId(srcElement.getId());
            nodeSession.setNodeName(srcElement.getName());
            var accessMode = accessModeLinkInfo.getAccessMode();
            nodeSession.setProtocol(accessMode.getProtocol());
        });

        return new ServiceResult<>(new ArrayList<>(connectionIdSessionMap.values()));
    }

    public ListObjServiceResult<NodeRecordingBO> getSceneRemoteRecordings(List<String> sceneIds, List<String> nodeIds,
                                                                          List<String> userIds,
                                                                          RecordingQueryParams queryParams) {
        var connectionLinkInfoMap = collectConnectionLinkInfo(sceneIds, nodeIds, userIds);
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
            var accessModeLinkInfo = connectionLinkInfo.getAccessModeLinkInfo();
            var srcScene = accessModeLinkInfo.getSrcScene();
            var cloudElement = accessModeLinkInfo.getCloudElement();
            var srcElement = cloudElement.getSrcElement();
            var accessMode = accessModeLinkInfo.getAccessMode();

            var recording = new NodeRecordingBO();
            recording.setSceneId(srcScene.getEid());
            recording.setSceneName(srcScene.getName());
            recording.setNodeId(srcElement.getId());
            recording.setNodeName(srcElement.getName());
            recording.setUser(rawRecording.getUsername());
            recording.setProtocol(accessMode.getProtocol());
            recording.setName(rawRecording.getRecordingFile());
            recording.setFilename(rawRecording.getRecordingFile());
            recording.setOrgName(rawRecording.getDepartment());
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

    public ServiceResult<NodeConnectionBO> enableRemoteRecording(String sceneId, String nodeId, String userId, String connectionId,
                                                                 CloudScenePluginContext context) {
        var connectionLinkInfoMap = collectConnectionLinkInfo(sceneId, List.of(nodeId), List.of(userId));
        if (!connectionLinkInfoMap.containsKey(connectionId)) {
            return new ServiceResult<>(null, CloudErrorCode.CONNECTION_NOT_EXIST);
        }
        var connectionLinkInfo = connectionLinkInfoMap.get(connectionId);
        var accessModeInfo = connectionLinkInfo.getAccessModeLinkInfo();
        var extraParam = new CloudSceneConnectionCommon.SceneConnectionEnableRecordingExtraParam();
        extraParam.setSceneName(accessModeInfo.getSrcScene().getName());
        extraParam.setNodeName(accessModeInfo.getCloudElement().getSrcElement().getName());
        extraParam.setUsername(userId);
        extraParam.setDepartment(context == null ? StringUtils.EMPTY : context.getRequestUserOrg());
        CloudSceneConnectionCommon.enableRecording(connectionLinkInfo.getSceneConnection(), extraParam);
        return new ServiceResult<>(connectionLinkInfo.toNewNodeConnection());
    }

    public ServiceResult<NodeConnectionBO> disableRemoteRecording(String sceneId, String nodeId, String userId, String connectionId) {
        var connectionLinkInfoMap = collectConnectionLinkInfo(sceneId, List.of(nodeId), List.of(userId));
        if (!connectionLinkInfoMap.containsKey(connectionId)) {
            return new ServiceResult<>(null, CloudErrorCode.CONNECTION_NOT_EXIST);
        }
        var connectionLinkInfo = connectionLinkInfoMap.get(connectionId);
        CloudSceneConnectionCommon.disableRecording(connectionLinkInfo.getSceneConnection());
        return new ServiceResult<>(connectionLinkInfo.toNewNodeConnection());
    }

    public ServiceResult<NodeConnectionBO> getSceneRemoteConnection(String sceneId, String nodeId,
                                                                    String accessModeId, String userId,
                                                                    CloudScenePluginContext context) {
        var accessModeInfoMap = collectAccessModeLinkInfo(sceneId, List.of(nodeId));
        var accessModeInfo = accessModeInfoMap.getOrDefault(accessModeId, null);
        if (accessModeInfo == null) {
            return new ServiceResult<>(null);
        }

        var nodeConnection = accessModeInfo.toNewNodeConnection();
        nodeConnection.setUser(userId);

        var accessMode = accessModeInfo.getAccessMode();
        if (accessMode.getProtocol().equals(AccessMode.Protocol.CONSOLE)) {
            var virtualType = accessModeInfo.getCloudElement().getVirtualType();
            if (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                nodeConnection.setConnectionUrl(CloudSceneConnectionCommon.getContainerConsoleUrl(accessModeInfo.getCloudElement().getResourceId()));
                return new ServiceResult<>(nodeConnection);
            }
        }

        var srcScene = accessModeInfo.getSrcScene();
        var cloudScene = accessModeInfo.getCloudScene();
        var cloudElement = accessModeInfo.getCloudElement();
        var portId = accessModeInfo.getCloudPort().getSrcPort().getId();
        var sceneConnectionParam = new CloudSceneConnectionCommon.SceneConnectionParam();
        sceneConnectionParam.setSceneId(sceneId);
        sceneConnectionParam.setSceneElementId(nodeId);
        sceneConnectionParam.setResourceId(cloudElement.getResourceId());
        sceneConnectionParam.setResourceHost(cloudElement.getHost());
        sceneConnectionParam.setSceneElementPortId(portId);
        sceneConnectionParam.setCloudAccessMode(accessModeInfo.getAccessMode());
        sceneConnectionParam.setUserId(userId);
        sceneConnectionParam.setSceneName(srcScene.getName());
        sceneConnectionParam.setSystemType(cloudElement.getSrcElement() == null ? StringUtils.EMPTY : cloudElement.getSrcElement().getSystemType());
        sceneConnectionParam.setDepartment(context == null ? StringUtils.EMPTY : context.getRequestUserOrg());
        var sceneConnection = CloudSceneConnectionCommon.getOrCreateSceneConnection(sceneConnectionParam);
        if (sceneConnection == null) {
            return new ServiceResult<>(null);
        }

        nodeConnection.setConnectionId(sceneConnection.getConnectionId());
        nodeConnection.setConnectionUrl(sceneConnection.getConnectionUrl());

        if (cloudScene != null && Boolean.TRUE.equals(cloudScene.getEnableRecording())
                && !Boolean.TRUE.equals(sceneConnection.getConnectionEnableRecording())) {
            var extraParam = new CloudSceneConnectionCommon.SceneConnectionEnableRecordingExtraParam();
            extraParam.setSceneName(accessModeInfo.getSrcScene().getName());
            extraParam.setNodeName(accessModeInfo.getCloudElement().getSrcElement().getName());
            extraParam.setUsername(userId);
            extraParam.setDepartment(context == null ? StringUtils.EMPTY : context.getRequestUserOrg());
            var enableRecordingFlag = CloudSceneConnectionCommon.enableRecording(sceneConnection, extraParam);
            if (enableRecordingFlag) {
                nodeConnection.setConnectionEnableRecording(sceneConnection.getConnectionEnableRecording());
                nodeConnection.setConnectionRecordingName(sceneConnection.getConnectionRecordingName());
            }
        }

        return new ServiceResult<>(nodeConnection);
    }

    /**
     * 查询网元指定字段的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param fieldNames 聚合统计字段  有效字段 COUNT_ELEMENT_FIELDS
     * @return 字段的聚合统计数量 Map<字段名称, Map<字段值, 该字段统计数量>>
     */
    public ServiceResult<Map<String, Map<Object, Long>>> groupCountElement(
            List<String> sceneIds, List<String> nodeIds, List<String> fieldNames) {
        List<String> countFieldNames = new ArrayList<>();
        fieldNames.forEach(fieldName -> {
            if (COUNT_ELEMENT_FIELDS.contains(fieldName)) {
                countFieldNames.add(fieldName);
            }
        });

        Map<String, Map<Object, Long>> result = new HashMap<>();
        if (!countFieldNames.isEmpty()) {
            countFieldNames.forEach(fieldName -> {
                result.put(fieldName, cloudSceneStatCommon.groupCountElement(sceneIds, nodeIds, fieldName, fieldName, false));
            });
        }

        return new ServiceResult<>(result);
    }

    /**
     * 查询网元指定字段的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param fieldNames 聚合统计字段  有效字段 COUNT_ELEMENT_FIELDS + CALC_COUNT_ELEMENT_FIELDS
     * @return 字段的聚合统计数量 Map<字段名称, 该字段统计数量>
     */
    public ServiceResult<Map<String, Long>> countElement(
            List<String> sceneIds, List<String> nodeIds, List<String> fieldNames) {
        List<String> countFieldNames = new ArrayList<>();
        List<String> calcCountFieldNames = new ArrayList<>();
        fieldNames.forEach(fieldName -> {
            if (COUNT_ELEMENT_FIELDS.contains(fieldName)) {
                countFieldNames.add(fieldName);
            } else if (CALC_COUNT_ELEMENT_FIELDS.contains(fieldName)) {
                calcCountFieldNames.add(fieldName);
            }
        });

        Map<String, Long> result = new HashMap<>();
        if (!countFieldNames.isEmpty()) {
            result.putAll(cloudSceneStatCommon.countElement(sceneIds, nodeIds, countFieldNames));
        }
        if (!calcCountFieldNames.isEmpty()) {
            result.putAll(cloudSceneStatCommon.calcCountElement(sceneIds, nodeIds, calcCountFieldNames));
        }

        return new ServiceResult<>(result);
    }

    /**
     * 查询端口指定字段的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param fieldNames 聚合统计字段  有效字段 COUNT_PORT_FIELDS + CALC_COUNT_PORT_FIELDS
     * @return 字段的聚合统计数量 Map<字段名称, 该字段统计数量>
     */
    public ServiceResult<Map<String, Long>> countPort(
            List<String> sceneIds, List<String> nodeIds, List<String> fieldNames) {
        List<String> countFieldNames = new ArrayList<>();
        List<String> calcCountFieldNames = new ArrayList<>();
        fieldNames.forEach(fieldName -> {
            if (COUNT_PORT_FIELDS.contains(fieldName)) {
                countFieldNames.add(fieldName);
            } else if (CALC_COUNT_PORT_FIELDS.contains(fieldName)) {
                calcCountFieldNames.add(fieldName);
            }
        });

        Map<String, Long> result = new HashMap<>();
        if (!countFieldNames.isEmpty()) {
            result.putAll(cloudSceneStatCommon.countPort(sceneIds, nodeIds, countFieldNames));
        }
        if (!calcCountFieldNames.isEmpty()) {
            result.putAll(cloudSceneStatCommon.calcCountPort(sceneIds, nodeIds, calcCountFieldNames));
        }

        return new ServiceResult<>(result);
    }

    public List<String> getRecordingUsers(String sceneId, List<String> nodeIds) {
        HumanCondition humanCondition = new HumanCondition().filterEqual(CloudPluginSceneConnectionModel::getScene, sceneId);
        if (CollectionUtils.isNotEmpty(nodeIds)) {
            humanCondition.filterIn(CloudPluginSceneConnectionModel::getSceneElement, nodeIds.toArray(new String[]{}));
        }
        var list = cloudPluginSceneConnectionMapper.list(humanCondition);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        Set<String> users = new HashSet<>();
        list.forEach(model -> {
            if (StringUtils.isNotEmpty(model.getAccessUser())) {
                users.add(model.getAccessUser());
            }
        });
        return new ArrayList<>(users);
    }
}
