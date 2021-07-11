package cn.cyberpeace.mp.sceneplugincloud.service;

import cn.cyberpeace.mp.base.common.BaseCommon;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.FloatUtils;
import cn.cyberpeace.mp.base.util.basic.IntUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.service.SceneConfigService;
import cn.cyberpeace.mp.scene.service.SceneSnapshotService;
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.ElementCommandExecBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeConnectionBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeLinkStatusBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeRecordingBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeSessionBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.UserConnectionNumBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
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
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GetGuacamoleRecordingData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GuacamoleRecordingData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NetworkFlowRateResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.OriginIpsResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ResetCplcoudAllIpsData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.SnapshotDeleteParams;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineExecCommandData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachinePrivateOperateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineUploadFileData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConnectionMapper;
import cn.cyberpeace.mp.sceneplugincloud.service.common.CloudSceneConnectionCommon;
import cn.cyberpeace.mp.sceneplugincloud.service.common.CloudSceneStatCommon;
import cn.cyberpeace.mp.sceneplugincloud.service.param.RecordingQueryParams;
import cn.cyberpeace.mp.sceneplugincloud.util.SceneInstanceLinkCacheUtil;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
                    CloudPluginSceneElementModel::getVirtualType,
                    CloudPluginSceneElementModel::getRole
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

    @Resource
    private SceneConfigService sceneConfigService;

    @Resource
    private CloudSceneStatCommon cloudSceneStatCommon;

    @Resource
    private CloudPluginSceneConnectionMapper cloudPluginSceneConnectionMapper;

    @Resource
    private SceneSnapshotService sceneSnapshotService;

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

    public OriginIpsResult resetCpcloudAllIps(ResetCplcoudAllIpsData request) {
        return new Remote().resetCpcloudAllIps(request);
    }

    public OriginIpsResult getCpcloudOriginIps() {
        return new Remote().getCpcloudOriginIps();
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
                    String formatUrl = monitorData.getMonitorUrl();
                    CloudAppConfig config = BeanCommon.getBean(CloudAppConfig.class);
                    if (config.getPublicNetProxy() && config.getPublicNetProxyServer()) {
                        if (StringUtils.isNotEmpty(config.getPublicNetProxyIp())) {
                            var u = URLUtil.url(formatUrl);
                            formatUrl = StringUtils.replaceOnce(formatUrl, u.getHost(), config.getPublicNetProxyIp());
                        }
                    }
                    nodeSession.setMonitorUrl(formatUrl);
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
                    String formatUrl = assistanceData.getAssistanceUrl();
                    CloudAppConfig config = BeanCommon.getBean(CloudAppConfig.class);
                    if (config.getPublicNetProxy() && config.getPublicNetProxyServer()) {
                        if (StringUtils.isNotEmpty(config.getPublicNetProxyIp())) {
                            var u = URLUtil.url(formatUrl);
                            formatUrl = StringUtils.replaceOnce(formatUrl, u.getHost(), config.getPublicNetProxyIp());
                        }
                    }
                    nodeSession.setAssistanceUrl(formatUrl);
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
            recording.setPlayUrl(StringUtils.join(CloudSingleton.cloudAppConfig.getCpcloudServer(),
                    String.format(CloudUrlConstant.RECORDING_PLAY_URL, rawRecording.getRecordingFile())));

            CloudAppConfig config = BeanCommon.getBean(CloudAppConfig.class);
            if (config.getPublicNetProxy() && config.getPublicNetProxyServer()) {
                if (StringUtils.isNotEmpty(config.getPublicNetProxyIp())) {
                    if (StringUtils.isNotEmpty(recording.getFileUrl())) {
                        String formatUrl = recording.getFileUrl();
                        var u = URLUtil.url(formatUrl);
                        formatUrl = StringUtils.replaceOnce(formatUrl, u.getHost(), config.getPublicNetProxyIp());
                        recording.setFileUrl(formatUrl);
                    }
                    if (StringUtils.isNotEmpty(recording.getPlayUrl())) {
                        String formatUrl = recording.getPlayUrl();
                        var u = URLUtil.url(formatUrl);
                        formatUrl = StringUtils.replaceOnce(formatUrl, u.getHost(), config.getPublicNetProxyIp());
                        recording.setPlayUrl(formatUrl);
                    }
                }
            }

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
        extraParam.setSceneId(sceneId);
        extraParam.setNodeId(nodeId);
        CloudSceneConnectionCommon.enableRecording(connectionLinkInfo.getSceneConnection(), extraParam);
        return new ServiceResult<>(connectionLinkInfo.toNewNodeConnection());
    }

    public ServiceResult<NodeConnectionBO> disableRemoteRecording(String sceneId, String nodeId, String userId, String connectionId) {
        var connectionLinkInfoMap = collectConnectionLinkInfo(sceneId, List.of(nodeId), List.of(userId));
        if (!connectionLinkInfoMap.containsKey(connectionId)) {
            
            return new ServiceResult<>(new NodeConnectionBO());
        }
        var connectionLinkInfo = connectionLinkInfoMap.get(connectionId);

        CloudSceneConnectionCommon.convertRecording(connectionLinkInfo.getSceneConnection());
        return new ServiceResult<>(connectionLinkInfo.toNewNodeConnection());
    }

    public ServiceResult<NodeConnectionBO> getRemoteTtyConnection(String sceneId, String nodeId) {
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, null);
        CloudScenePluginHandler cloudPlugin = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (cloudPlugin != null) {
            var cloudElement = cloudPlugin.getElement(nodeId);
            if (cloudElement != null) {
                String resourceId = cloudElement.getResourceId();
                String url = CloudSceneConnectionCommon.getContainerConsoleUrl(resourceId);
                NodeConnectionBO nodeConnectionBO = new NodeConnectionBO();
                nodeConnectionBO.setNodeId(nodeId);
                nodeConnectionBO.setConnectionUrl(url);
                nodeConnectionBO.setSceneName(sceneHandler.getScene().getName());
                nodeConnectionBO.setNodeName(sceneHandler.getElement(nodeId).getName());
                return new ServiceResult<>(nodeConnectionBO);
            }
        }
        throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
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
                String url = CloudSceneConnectionCommon.getContainerConsoleUrl(accessModeInfo.getCloudElement().getResourceId());
                String formatUrl = formatUrl(url, accessModeInfo);
                nodeConnection.setConnectionUrl(formatUrl);
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
        sceneConnectionParam.setEntity(cloudElement.getVirtualType() != null && cloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE);
        var sceneConnection = CloudSceneConnectionCommon.getOrCreateSceneConnection(sceneConnectionParam);
        if (sceneConnection == null) {
            return new ServiceResult<>(null);
        }

        nodeConnection.setConnectionId(sceneConnection.getConnectionId());
        String url = sceneConnection.getConnectionUrl();
        String formatUrl = formatUrl(url, accessModeInfo);
        nodeConnection.setConnectionUrl(formatUrl);

        if (cloudScene != null && Boolean.TRUE.equals(cloudScene.getEnableRecording())
                && !Boolean.TRUE.equals(sceneConnection.getConnectionEnableRecording())) {
            var extraParam = new CloudSceneConnectionCommon.SceneConnectionEnableRecordingExtraParam();
            extraParam.setSceneName(accessModeInfo.getSrcScene().getName());
            extraParam.setNodeName(accessModeInfo.getCloudElement().getSrcElement().getName());
            extraParam.setUsername(userId);
            extraParam.setDepartment(context == null ? StringUtils.EMPTY : context.getRequestUserOrg());
            extraParam.setSceneId(sceneId);
            extraParam.setNodeId(nodeId);
            var enableRecordingFlag = CloudSceneConnectionCommon.enableRecording(sceneConnection, extraParam);
            if (enableRecordingFlag) {
                nodeConnection.setConnectionEnableRecording(sceneConnection.getConnectionEnableRecording());
                nodeConnection.setConnectionRecordingName(sceneConnection.getConnectionRecordingName());
            }
        }

        return new ServiceResult<>(nodeConnection);
    }

    private String formatUrl(String inputUrl, AccessModeLinkInfo modeLinkInfo) {
        String formatUrl = inputUrl;
        if (CloudSingleton.cloudAppConfig.getPublicNetProxyServer()) {
            CloudAccessMode accessMode = modeLinkInfo.getAccessMode();
            var u = URLUtil.url(inputUrl);
            if (accessMode.getPublicNetProxy() != null) {
                if (Boolean.TRUE.equals(accessMode.getPublicNetProxy())) {
                    String ip = StringUtils.isNotEmpty(accessMode.getPublicNetProxyIp()) ? accessMode.getPublicNetProxyIp() : CloudSingleton.cloudAppConfig.getPublicNetProxyIp();
                    formatUrl = StringUtils.replaceOnce(formatUrl, u.getHost(), ip);
                }
            } else if (CloudSingleton.cloudAppConfig.getPublicNetProxy()) {
                formatUrl = StringUtils.replaceOnce(formatUrl, u.getHost(), CloudSingleton.cloudAppConfig.getPublicNetProxyIp());
            }
        }
        return formatUrl;
    }

    
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
                var countResult = cloudSceneStatCommon.groupCountElement(sceneIds, nodeIds, fieldName, List.of(fieldName), false);
                result.put(fieldName, countResult.get(fieldName));
            });
        }

        return new ServiceResult<>(result);
    }

    
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
            result.putAll(cloudSceneStatCommon.countElement(sceneIds, nodeIds, countFieldNames, true));
        }
        if (!calcCountFieldNames.isEmpty()) {
            result.putAll(cloudSceneStatCommon.calcCountElement(sceneIds, nodeIds, calcCountFieldNames));
        }

        return new ServiceResult<>(result);
    }

    
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
            result.putAll(cloudSceneStatCommon.countPort(sceneIds, nodeIds, countFieldNames, true));
        }
        if (!calcCountFieldNames.isEmpty()) {
            result.putAll(cloudSceneStatCommon.calcCountPort(sceneIds, nodeIds, calcCountFieldNames));
        }

        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> expireAllRemoteConnect(String userId) {
        Remote remote = new Remote();
        for (int i = 0; i < 2; i++) {
            try {
                remote.expireAllConnect(userId);
                break;
            } catch (Exception e) {
                log.error("failed to expire remote connect , userId is {}", userId, e);
                continue;
            }
        }
        return new ServiceResult<>(true);
    }

    public ServiceResult<List<String>> getRecordingUsers(String sceneId, List<String> nodeIds) {
        HumanCondition humanCondition = new HumanCondition().filterEqual(CloudPluginSceneConnectionModel::getScene, sceneId);
        if (CollectionUtils.isNotEmpty(nodeIds)) {
            humanCondition.filterIn(CloudPluginSceneConnectionModel::getSceneElement, nodeIds.toArray(new String[]{}));
        }
        var list = cloudPluginSceneConnectionMapper.list(humanCondition);
        if (CollectionUtils.isEmpty(list)) {
            return new ServiceResult<>(new ArrayList<>());
        }
        Set<String> users = new HashSet<>();
        list.forEach(model -> {
            if (StringUtils.isNotEmpty(model.getAccessUser())) {
                users.add(model.getAccessUser());
            }
        });
        return new ServiceResult<>(new ArrayList<>(users));
    }

    public void changeVlanRange(String vlanRange) {
        TaskCommon.executeTask(() -> {
            if (StringUtils.isEmpty(vlanRange)) {
                return;
            }
            
            String formatVlanRange = vlanRange.replace("-", ":");
            boolean result = new Remote().changeVlanRange(formatVlanRange);
            if (!result) {
                log.error("change vlan range {} error", formatVlanRange);
            }
        });
    }

    public ServiceResult<List<NodeLinkStatusBO>> linkStatus(String sceneId) {
        try {
            var list = SceneInstanceLinkCacheUtil.getCached(sceneId);
            SceneInstanceLinkCacheUtil.tryStartCronRefresh();
            return new ServiceResult<>(list);
        } catch (Exception e) {
            log.error("failed get link status for scene {} , message is {}", sceneId, ExceptionUtils.getStackTrace(e));
            return new ServiceResult<>(new ArrayList<>());
        }
    }

    public ServiceResult<ElementCommandExecBO> execElementCommand(String sceneId, String nodeId, String command, String user) {
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        var node = pluginHandler.getElement(nodeId);
        if (node != null && node.getVirtualType() != null) {
            if (BaseCloudPluginSceneElementModel.VirtualType.CONTAINER == node.getVirtualType()) {
                Container container = new Container();
                var result = container.executeCommand(node.getResourceId(), command,user);
                if (result.getExitCode() != null && result.getExitCode() == 0) {
                    ElementCommandExecBO execRes = new ElementCommandExecBO();
                    execRes.setOutput(result.getOutput());
                    execRes.setStatus(true);
                    return new ServiceResult<>(execRes);
                } else {
                    log.error("failed to exec command {} , nodeId is {}", command, nodeId);
                    ElementCommandExecBO execRes = new ElementCommandExecBO();
                    execRes.setStatus(false);
                    execRes.setOutput(result.getOutput());
                    return new ServiceResult<>(execRes);
                }
            } else if (BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE == node.getVirtualType()) {
                VirtualMachine virtualMachine = new VirtualMachine();
                var param = this.getVirtualMachineUploadFileInfo(pluginHandler, node);
                if (param != null) {
                    var execCommandData = new VirtualMachineExecCommandData();
                    BeanUtils.copyProperties(param, execCommandData);
                    execCommandData.setCmd(command);
                    var result = virtualMachine.execCommand(execCommandData);
                    if (result != null && Boolean.TRUE.equals(result.getSuccess())) {
                        ElementCommandExecBO execRes = new ElementCommandExecBO();
                        execRes.setStatus(true);
                        execRes.setOutput(result.getStdout());
                        return new ServiceResult<>(execRes);
                    } else {
                        ElementCommandExecBO execRes = new ElementCommandExecBO();
                        execRes.setStatus(false);
                        if (result != null) {
                            execRes.setOutput(result.getStdout());
                        }
                        return new ServiceResult<>(execRes);
                    }
                }
            }

        }
        ElementCommandExecBO execRes = new ElementCommandExecBO();
        execRes.setStatus(false);
        return new ServiceResult<>(execRes);
    }

    public ServiceResult<Boolean> uploadElementFile(String sceneId, String nodeId, String filePath, String filename, String destPath) {
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        var node = pluginHandler.getElement(nodeId);
        String finalPath = filePath;
        if (StringUtils.startsWith(finalPath, BaseCommon.baseAppConfig.getFdfsOuterHttpServer())) {
            finalPath = StringUtils.replaceOnce(finalPath, BaseCommon.baseAppConfig.getFdfsOuterHttpServer(), "");
        }
        if (StringUtils.startsWith(finalPath, "/")) {
            finalPath = StringUtils.replaceOnce(finalPath, "/", "");
        }
        if (node != null && node.getVirtualType() != null) {
            if (BaseCloudPluginSceneElementModel.VirtualType.CONTAINER == node.getVirtualType()) {
                Container container = new Container();

                String dir = "/tmp/mp/" + sceneId + "_" + nodeId;
                String tmpZipFilepath = dir + "/" + filename;
                File file = new File(dir);
                if (!file.exists()) {
                    file.mkdir();
                }
                try {
                    FsCommon.download(URLUtil.getPath(finalPath), tmpZipFilepath);
                    var result = container.copyFile(node.getResourceId(), tmpZipFilepath, destPath);
                    return new ServiceResult<>(result);
                } finally {
                    try {
                        FileUtils.deleteDirectory(file);
                    } catch (Exception e) {
                        log.error("failed to delete dir , {} ", tmpZipFilepath, e);
                    }
                }
            } else if (BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE == node.getVirtualType()) {
                var param = this.getVirtualMachineUploadFileInfo(pluginHandler, node);
                if (param != null) {
                    var standardSystemType = SystemTypeUtils.getStandardSystemType(node.getSrcElement().getSystemType());
                    var uploadInfoData = new VirtualMachineUploadFileData();
                    BeanUtils.copyProperties(param, uploadInfoData);
                    uploadInfoData.setFile(finalPath);
                    uploadInfoData.setDestPath(destPath + SystemTypeCommandUtils.getPathDelimiter(standardSystemType) + filename);
                    VirtualMachine virtualMachine = new VirtualMachine();
                    virtualMachine.uploadFile(uploadInfoData);
                    return new ServiceResult<>(true);
                }
            }
        }
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> uploadElementFile(String sceneId, String nodeId, InputStream inputStream, String filename, String destPath) {
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        var node = pluginHandler.getElement(nodeId);
        if (node != null && node.getVirtualType() != null) {
            if (BaseCloudPluginSceneElementModel.VirtualType.CONTAINER == node.getVirtualType()) {
                Container container = new Container();
                var tmpFileDir = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, FileUtil.getTmpDirPath(), sceneId, TextUtils.uuid());
                new File(tmpFileDir).mkdirs();
                String tmpZipFilepath = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, tmpFileDir, filename);
                File file = new File(tmpZipFilepath);
                try {
                    FileUtils.copyInputStreamToFile(inputStream, file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Boolean result = null;
                for (int i = 0; i < 3; i++) {
                    try {
                        result = container.copyFile(node.getResourceId(), tmpZipFilepath, destPath);
                        break;
                    } catch (Exception e) {
                        log.error("failed to copy file at {} time, sceneId is {},nodeId is {},exception is ", i, sceneId, nodeId, ExceptionUtils.getStackTrace(e));
                        if (i == 2) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                return new ServiceResult<>(result);
            } else if (BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE == node.getVirtualType()) {
                var param = this.getVirtualMachineUploadFileInfo(pluginHandler, node);
                String finalPath = FsCommon.upload(inputStream);
                if (param != null) {
                    var standardSystemType = SystemTypeUtils.getStandardSystemType(node.getSrcElement().getSystemType());
                    var uploadInfoData = new VirtualMachineUploadFileData();
                    BeanUtils.copyProperties(param, uploadInfoData);
                    uploadInfoData.setFile(finalPath);
                    uploadInfoData.setDestPath(destPath + SystemTypeCommandUtils.getPathDelimiter(standardSystemType) + filename);
                    VirtualMachine virtualMachine = new VirtualMachine();
                    virtualMachine.uploadFile(uploadInfoData);
                    
                    return new ServiceResult<>(true);
                }
            }
        }
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> deleteElementSnapshot(String sceneId, String nodeId, String snapshotId, String snapshotUuid, String snapshotName) {
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        var node = pluginHandler.getElement(nodeId);
        if (node != null) {
            SnapshotDeleteParams params = new SnapshotDeleteParams();
            params.setSnapshotName(snapshotName);
            params.setSnapshotUuid(snapshotUuid);
            params.setImageType(node.getImageType());
            params.setDeviceUuid(node.getImageUuid());
            try {
                new Remote().cloudDelete(CloudUrlConstant.VIRTUAL_MACHINE_DEL_SNAPSHOT_URL, params, null, new TypeReference<CloudResponse<Object>>() {
                }, true);
                sceneSnapshotService.deleteElementSnapshot(snapshotId);
            } catch (Exception e) {
                if (!"snapshot not exists".equals(e.getMessage())) {
                    throw e;
                }
            }
        }
        return new ServiceResult<>(true);
    }

    private VirtualMachinePrivateOperateData getVirtualMachineUploadFileInfo(CloudScenePluginHandler pluginHandler, CloudPluginSceneElementBO node) {
        var ports = node.getSrcElement().getPorts();
        if (CollectionUtils.isNotEmpty(ports)) {
            for (var port : ports) {
                if (CollectionUtils.isNotEmpty(port.getAccessModes())) {
                    for (var mode : port.getAccessModes()) {
                        if (Boolean.TRUE.equals(mode.getManageMode()) && AccessMode.Protocol.SSH.equals(mode.getProtocol())) {
                            String networkId = pluginHandler.getPort(port.getId()).getAuxiliaryNetworkId();
                            if (StringUtils.isNotEmpty(networkId)) {
                                VirtualMachinePrivateOperateData result = new VirtualMachineUploadFileData();
                                result.setNetworkId(networkId);
                                result.setUsername(mode.getUsername());
                                if (Boolean.TRUE.equals(mode.getPrivateKeyEnable())) {
                                    result.setPrivateKeyPath(mode.getPrivateKeyPath());
                                } else {
                                    result.setPassword(mode.getPassword());
                                }
                                result.setPort(mode.getPort() != null ? mode.getPort() : AccessMode.Protocol.DEFAULT_PORT_MAP.get(AccessMode.Protocol.SSH));
                                result.setInstanceIp(port.getIp());
                                return result;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public ServiceResult<List<UserConnectionNumBO>> countUserConnections(List<String> users) {
        List<UserConnectionNumBO> connectionNumBOS = new ArrayList<>();
        for (String user : users) {
            UserConnectionNumBO connectionNumBO = new UserConnectionNumBO();
            connectionNumBO.setUser(user);
            connectionNumBO.setConnectionNum(0);
            connectionNumBOS.add(connectionNumBO);
        }
        HumanCondition condition = new HumanCondition()
                .filterEqual(CloudPluginSceneConnectionModel::getDisable, false)
                .filterIn(CloudPluginSceneConnectionModel::getAccessUser, users.toArray());
        var list = cloudPluginSceneConnectionMapper.list(condition);
        if (CollectionUtils.isEmpty(list)) {
            return new ServiceResult<>(connectionNumBOS);
        }
        Set<String> connectionIds = new HashSet<>();
        list.forEach(c -> {
            if (StringUtils.isNotEmpty(c.getConnectionId())) {
                connectionIds.add(c.getConnectionId());
            }
        });
        if (CollectionUtils.isEmpty(connectionIds)) {
            return new ServiceResult<>(connectionNumBOS);
        }
        var monitors = new Remote().getMonitors(connectionIds.toArray(new String[]{}));
        if (monitors == null || monitors.isEmpty()) {
            return new ServiceResult<>(connectionNumBOS);
        }
        Map<String, Set<String>> user2ConnectionIds = new HashMap<>();
        for (var model : list) {
            if (!user2ConnectionIds.containsKey(model.getAccessUser())) {
                user2ConnectionIds.put(model.getAccessUser(), new HashSet<>());
            }
            if (StringUtils.isNotEmpty(model.getConnectionId())) {
                user2ConnectionIds.get(model.getAccessUser()).add(model.getConnectionId());
            }
        }
        for (var node : connectionNumBOS) {
            var userConnectIds = user2ConnectionIds.get(node.getUser());
            if (userConnectIds != null) {
                for (String cId : userConnectIds) {
                    if (monitors.keySet().contains(cId)) {
                        node.setConnectionNum(node.getConnectionNum() + 1);
                    }
                }
            }
        }
        return new ServiceResult<>(connectionNumBOS);
    }

    public ServiceResult<List<NodeSessionBO>> getUserNodeSessions(String user, boolean monitor, boolean assistance) {
        HumanCondition condition = new HumanCondition()
                .filterEqual(CloudPluginSceneConnectionModel::getDisable, false)
                .filterEqual(CloudPluginSceneConnectionModel::getAccessUser, user);
        var connectionModels = cloudPluginSceneConnectionMapper.list(condition);
        if (CollectionUtils.isEmpty(connectionModels)) {
            return new ServiceResult<>(new ArrayList<>());
        }
        Map<String, CloudPluginSceneConnectionModel> connectionId2Model = new HashMap<>();
        connectionModels.forEach(m -> {
            if (StringUtils.isNotEmpty(m.getConnectionId())) {
                connectionId2Model.put(m.getConnectionId(), m);
            }
        });
        if (connectionId2Model.isEmpty()) {
            return new ServiceResult<>(new ArrayList<>());
        }
        Map<String, NodeSessionBO> connectionIdSessionMap = this.getNodeSessions(connectionId2Model.keySet().toArray(new String[]{}), monitor, assistance);
        if (connectionIdSessionMap.isEmpty()) {
            return new ServiceResult<>(new ArrayList<>());
        }
        connectionIdSessionMap.forEach((connectionId, session) -> {
            var model = connectionId2Model.get(connectionId);
            if (model != null) {
                session.setSceneId(model.getScene());
                session.setNodeId(model.getSceneElement());
            }
        });
        return new ServiceResult<>(new ArrayList<>(connectionIdSessionMap.values()));
    }

    private Map<String, NodeSessionBO> getNodeSessions(String[] connectionIds, boolean monitor, boolean assistance) {
        var remoteClient = new Remote();
        Map<String, NodeSessionBO> connectionIdSessionMap = new HashMap<>();
        if (monitor) {
            var monitors = remoteClient.getMonitors(connectionIds);
            if (monitors != null) {
                monitors.forEach((connectionId, monitorData) -> {
                    var nodeSession = connectionIdSessionMap.getOrDefault(connectionId,
                            new NodeSessionBO());
                    String formatUrl = monitorData.getMonitorUrl();
                    CloudAppConfig config = BeanCommon.getBean(CloudAppConfig.class);
                    if (config.getPublicNetProxy() && config.getPublicNetProxyServer()) {
                        if (StringUtils.isNotEmpty(config.getPublicNetProxyIp())) {
                            var u = URLUtil.url(formatUrl);
                            formatUrl = StringUtils.replaceOnce(formatUrl, u.getHost(), config.getPublicNetProxyIp());
                        }
                    }
                    nodeSession.setMonitorUrl(formatUrl);
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
                    String formatUrl = assistanceData.getAssistanceUrl();
                    CloudAppConfig config = BeanCommon.getBean(CloudAppConfig.class);
                    if (config.getPublicNetProxy() && config.getPublicNetProxyServer()) {
                        if (StringUtils.isNotEmpty(config.getPublicNetProxyIp())) {
                            var u = URLUtil.url(formatUrl);
                            formatUrl = StringUtils.replaceOnce(formatUrl, u.getHost(), config.getPublicNetProxyIp());
                        }
                    }
                    nodeSession.setAssistanceUrl(formatUrl);
                    nodeSession.setUser(assistanceData.getUsername());
                    connectionIdSessionMap.put(connectionId, nodeSession);
                });
            }
        }
        return connectionIdSessionMap;
    }

    
    public NetworkFlowRateResult networkFlowRate(List<String> portIds, Long totalSecond) {
        log.info("ScenePluginCloudSceneService.networkFlowRate begin. portIds:{}", portIds);
        NetworkFlowRateResult result = new Remote().networkFlowRate(portIds, totalSecond);
        log.info("ScenePluginCloudSceneService.networkFlowRate end. result:{}", result);
        return result;
    }
}
