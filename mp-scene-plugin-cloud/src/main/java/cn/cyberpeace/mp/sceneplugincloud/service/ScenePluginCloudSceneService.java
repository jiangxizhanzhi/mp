package cn.cyberpeace.mp.sceneplugincloud.service;

import cn.cyberpeace.mp.base.common.StatCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.*;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneElementPortModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.*;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementPortModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GetGuacamoleRecordingData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GuacamoleRecordingData;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementPortMapper;
import cn.cyberpeace.mp.sceneplugincloud.service.param.GroupCountFilter;
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

    public static final List<String> COUNT_PORT_FIELDS = Arrays.asList(
            BeanUtils.getBeanGetterFieldNames(CloudPluginSceneElementPortModel::getFloatingIp, CloudPluginSceneElementPortModel::getAuxiliaryNetworkId)
    );

    private CloudPluginSceneElementMapper cloudPluginSceneElementMapper;

    private CloudPluginSceneElementPortMapper cloudPluginSceneElementPortMapper;

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

    /**
     * 分组查询指定字段的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param filter 过滤条件
     * @param groupFieldName 分组聚合字段
     * @param <T> 分组聚合字段的值类型
     * @return 字段的聚合统计数量 Map<字段值, 该字段值统计数量>
     */
    private <T> ServiceResult<Map<T, Long>> groupCountElement(List<String> sceneIds, List<String> nodeIds,
                                                              GroupCountFilter filter, String groupFieldName) {
        var condition = new HumanCondition()
                .link(SceneModel.class, CloudPluginSceneElementModel.class)
                .filterLink(SceneModel.class, SceneModel::getId,
                        CloudPluginSceneElementModel.class, CloudPluginSceneElementModel::getScene)
                .currentLink(SceneModel.class)
                .filterEqual(SceneModel::getDeleted, false);
        if (!CollectionUtils.isEmpty(sceneIds)) {
            condition.filterIn(SceneModel::getId, sceneIds.toArray(new String[0]));
        }

        condition.currentLink(CloudPluginSceneElementModel.class);
        if (!CollectionUtils.isEmpty(nodeIds)) {
            condition.filterIn(CloudPluginSceneElementModel::getSceneElement, nodeIds.toArray(new String[0]));
        }
        if (filter != null) {
            if (!CollectionUtils.isEmpty(filter.getHosts())) {
                condition.filterIn(CloudPluginSceneElementModel::getHost, filter.getHosts().toArray());
            }
            if (!CollectionUtils.isEmpty(filter.getVirtualTypes())) {
                condition.filterIn(CloudPluginSceneElementModel::getVirtualType, filter.getVirtualTypes().toArray());
            }
        }

        Map<T, Long> result;
        try {
            result = StatCommon.groupCount(cloudPluginSceneElementMapper, condition, groupFieldName);
        } catch (Exception e) {
            log.error("Group count cloud element on field {} error: {}", groupFieldName, e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, e);
        }

        return new ServiceResult<>(result);
    }

    @Autowired
    public ScenePluginCloudSceneService(CloudPluginSceneElementMapper cloudPluginSceneElementMapper,
                                        CloudPluginSceneElementPortMapper cloudPluginSceneElementPortMapper) {
        this.cloudPluginSceneElementMapper = cloudPluginSceneElementMapper;
        this.cloudPluginSceneElementPortMapper = cloudPluginSceneElementPortMapper;
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
     * 分组查询host的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param filter 过滤条件
     * @return 字段的聚合统计数量 Map<host, 该字段值统计数量>
     */
    public ServiceResult<Map<String, Long>> groupCountElementByHost(
            List<String> sceneIds, List<String> nodeIds, GroupCountFilter filter) {
        return groupCountElement(sceneIds, nodeIds, filter,
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getHost));
    }

    /**
     * 分组查询virtualType的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param filter 过滤条件
     * @return 字段的聚合统计数量 Map<virtualType, 该字段值统计数量>
     */
    public ServiceResult<Map<Integer, Long>> groupCountElementByVirtualType(
            List<String> sceneIds, List<String> nodeIds, GroupCountFilter filter) {
        return groupCountElement(sceneIds, nodeIds, filter,
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getVirtualType));
    }

    /**
     * 查询端口指定字段的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param fieldNames 聚合统计字段
     * @return 字段的聚合统计数量 Map<字段名称, 该字段统计数量>
     */
    public ServiceResult<Map<String, Long>> countPort(
            List<String> sceneIds, List<String> nodeIds, List<String> fieldNames) {
        fieldNames = fieldNames.stream().filter(fieldName -> COUNT_PORT_FIELDS.contains(fieldName))
                .collect(Collectors.toList());
        if (fieldNames.isEmpty()) {
            return new ServiceResult<>(new HashMap<>());
        }

        var condition = new HumanCondition()
                .link(SceneModel.class, SceneElementModel.class, SceneElementPortModel.class, CloudPluginSceneElementPortModel.class)
                .filterLink(SceneModel.class, SceneModel::getId,
                        SceneElementModel.class, SceneElementModel::getScene)
                .filterLink(SceneModel.class, SceneModel::getId,
                        SceneElementPortModel.class, SceneElementPortModel::getScene)
                .filterLink(SceneModel.class, SceneModel::getId,
                        CloudPluginSceneElementPortModel.class, CloudPluginSceneElementPortModel::getScene)
                .filterLink(SceneElementModel.class, SceneElementModel::getId,
                        SceneElementPortModel.class, SceneElementPortModel::getSceneElement)
                .filterLink(SceneElementPortModel.class, SceneElementPortModel::getPortId,
                        CloudPluginSceneElementPortModel.class, CloudPluginSceneElementPortModel::getSceneElementPort)
                .currentLink(SceneModel.class)
                .filterEqual(SceneModel::getDeleted, false);
        if (!CollectionUtils.isEmpty(sceneIds)) {
            condition.filterIn(SceneModel::getId, sceneIds.toArray(new String[0]));
        }

        condition.currentLink(SceneElementModel.class);
        if (!CollectionUtils.isEmpty(nodeIds)) {
            condition.filterIn(SceneElementModel::getNodeId, nodeIds.toArray(new String[0]));
        }

        condition.currentLink(CloudPluginSceneElementPortModel.class);
        Map<String, Long> result;
        try {
            result = StatCommon.count(cloudPluginSceneElementPortMapper, condition, fieldNames);
        } catch (Exception e) {
            log.error("Count cloud element port on fields {} error: {}", fieldNames, e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, e);
        }

        return new ServiceResult<>(result);
    }

}
