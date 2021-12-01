package cn.cyberpeace.mp.sceneplugincloud.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.FloatUtils;
import cn.cyberpeace.mp.base.util.basic.IntUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.*;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementPortModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GetGuacamoleRecordingData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GuacamoleRecordingData;
import cn.cyberpeace.mp.sceneplugincloud.service.common.CloudSceneStatCommon;
import cn.cyberpeace.mp.sceneplugincloud.service.param.RecordingQueryParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Data
    @AllArgsConstructor
    private static class ConnectionLinkInfo {
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

            var cloudPorts = pluginHandler.getPorts(cloudElement);
            cloudPorts.forEach(cloudPort -> {
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
    public ScenePluginCloudSceneService(CloudSceneStatCommon cloudSceneStatCommon) {
        this.cloudSceneStatCommon = cloudSceneStatCommon;
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

}
