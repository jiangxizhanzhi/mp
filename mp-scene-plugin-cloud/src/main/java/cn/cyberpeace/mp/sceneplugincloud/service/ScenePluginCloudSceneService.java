 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ScenePluginCloudSceneService.java.
 // File size 55126.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
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
import cn.cyberpeace.mp.base.util.basic.*;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.base.util.mptool.URLUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.service.SceneConfigService;
import cn.cyberpeace.mp.scene.service.SceneSnapshotService;
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.*;
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
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConnectionMapper;
import cn.cyberpeace.mp.sceneplugincloud.service.common.CloudSceneConnectionCommon;
import cn.cyberpeace.mp.sceneplugincloud.service.common.CloudSceneStatCommon;
import cn.cyberpeace.mp.sceneplugincloud.service.param.RecordingQueryParams;
import cn.cyberpeace.mp.sceneplugincloud.util.SceneInstanceLinkCacheUtil;
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
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ScenePluginCloudSceneService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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

        /**
         * toNewNodeConnection
         *
         * @return
         */
        public NodeConnectionBO toNewNodeConnection() {
            NodeConnectionBO nodeConnection = new NodeConnectionBO();
            nodeConnection.setSceneId(srcScene.getEid());
            nodeConnection.setSceneName(srcScene.getName());
            nodeConnection.setNodeId(cloudElement.getSrcElement().getId());
            nodeConnection.setNodeName(cloudElement.getSrcElement().getName());
            nodeConnection.setProtocol(accessMode.getProtocol());
            return nodeConnection;
        }
    }

    /**
     * collectAccessModeLinkInfo
     *
     * @param sceneId
     * @param nodeIds
     * @return
     */
    private Map<String, AccessModeLinkInfo> collectAccessModeLinkInfo(String sceneId, List<String> nodeIds) {
        Map<String, AccessModeLinkInfo> accessModeLinkInfoMap = new HashMap<>();

        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, null);
        SceneBO scene = sceneHandler.getScene();
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        CloudPluginSceneBO cloudScene = pluginHandler.getScene();
        pluginHandler.getElementMap().forEach((nodeId, cloudElement) -> {
            if (nodeIds != null && !nodeIds.isEmpty() && !nodeIds.contains(nodeId)) {
                return;
            }
            List<CloudPluginSceneElementPortBO> cloudPorts = pluginHandler.getPorts(cloudElement);
            cloudPorts.forEach(cloudPort -> {
                List<CloudAccessMode> accessModes = cloudPort.getAccessModes();
                if (accessModes == null) {
                    return;
                }
                accessModes.forEach(accessMode -> {
                    String accessModeId = accessMode.getId();
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

    @Data
    @AllArgsConstructor
    private static class ConnectionLinkInfo {
        private String connectionId;
        private CloudPluginSceneConnectionModel sceneConnection;
        private AccessModeLinkInfo accessModeLinkInfo;

        public NodeConnectionBO toNewNodeConnection() {
            NodeConnectionBO nodeConnection = accessModeLinkInfo.toNewNodeConnection();
            nodeConnection.setUser(sceneConnection.getAccessUser());
            nodeConnection.setConnectionId(sceneConnection.getConnectionId());
            nodeConnection.setConnectionEnableRecording(sceneConnection.getConnectionEnableRecording());
            nodeConnection.setConnectionRecordingName(sceneConnection.getConnectionRecordingName());
            nodeConnection.setConnectionUrl(sceneConnection.getConnectionUrl());
            return nodeConnection;
        }
    }

    /**
     * collectConnectionLinkInfo
     *
     * @param sceneId
     * @param nodeIds
     * @param userIds
     * @return
     */
    private Map<String, ConnectionLinkInfo> collectConnectionLinkInfo(String sceneId, List<String> nodeIds,
                                                                      List<String> userIds) {
         Map<String, AccessModeLinkInfo> accessModeLinkInfoMap = collectAccessModeLinkInfo(sceneId, nodeIds);
        Map<String, ConnectionLinkInfo> connectionLinkInfoMap = new HashMap<>();
        if (accessModeLinkInfoMap.isEmpty()) {
            return connectionLinkInfoMap;
        }

        HumanCondition condition = new HumanCondition()
                .filterEqual(CloudPluginSceneConnectionModel::getScene, sceneId)
                .filterNotEqual(CloudPluginSceneConnectionModel::getConnectionId, StringUtils.EMPTY)
                .filterIn(CloudPluginSceneConnectionModel::getAccessModeId,
                        accessModeLinkInfoMap.keySet().toArray(new String[0]));
        if (!CollectionUtils.isEmpty(userIds)) {
            condition.filterIn(CloudPluginSceneConnectionModel::getAccessUser, userIds.toArray());
        }
        List<CloudPluginSceneConnectionModel> sceneConnections = cloudPluginSceneConnectionMapper.list(condition);
        sceneConnections.forEach(sceneConnection -> {
            String connectionId = sceneConnection.getConnectionId();
            AccessModeLinkInfo accessModeLinkInfo = accessModeLinkInfoMap.get(sceneConnection.getAccessModeId());
            ConnectionLinkInfo connectionLinkInfo = new ConnectionLinkInfo(connectionId, sceneConnection, accessModeLinkInfo);
            connectionLinkInfoMap.put(connectionId, connectionLinkInfo);
        });

        return connectionLinkInfoMap;
    }

    /**
     * collectConnectionLinkInfo
     *
     * @param sceneIds
     * @param nodeIds
     * @param userIds
     * @return
     */
    private Map<String, ConnectionLinkInfo> collectConnectionLinkInfo(List<String> sceneIds, List<String> nodeIds,
                                                                      List<String> userIds) {
        Map<String, ConnectionLinkInfo> connectionLinkInfoMap = new HashMap<>();
        for (String sceneId : sceneIds) {
            connectionLinkInfoMap.putAll(collectConnectionLinkInfo(sceneId, nodeIds, userIds));
        }
        return connectionLinkInfoMap;
    }

    /**
     * collectRecordingName
     *
     * @param sceneId
     * @return
     */
    private List<String> collectRecordingName(String sceneId) {
        List<String> connectionIds = new ArrayList<>();
        HumanCondition condition = new HumanCondition().filterEqual(CloudPluginSceneConnectionModel::getScene, sceneId);
        List<CloudPluginSceneConnectionModel> sceneConnections = cloudPluginSceneConnectionMapper.list(condition);
        if (CollectionUtils.isEmpty(sceneConnections)) {
            return new ArrayList<>();
        }
        sceneConnections.forEach(sceneConnection -> {
            if (sceneConnection.getConnectionId() != null) {
                connectionIds.add(sceneConnection.getConnectionId());
            }
        });
        if (CollectionUtils.isNotEmpty(connectionIds)) {
            Remote remoteClient = new Remote();
            return remoteClient.getRecords(connectionIds);
        }
        return new ArrayList<>();
    }

    /**
     * deleteSceneRecords
     *
     * @param sceneId
     * @param logic
     */
    public void deleteSceneRecords(String sceneId, boolean logic) {
        if (logic) {
            return;
        }
        List<String> recordingNames = this.collectRecordingName(sceneId);
        if (CollectionUtils.isNotEmpty(recordingNames)) {
            Remote remoteClient = new Remote();
            remoteClient.deleteRecords(recordingNames);
        }
    }

    /**
     * resetCpcloudAllIps
     *
     * @param request
     * @return
     */
    public OriginIpsResult resetCpcloudAllIps(ResetCplcoudAllIpsData request) {
        return new Remote().resetCpcloudAllIps(request);
    }

    /**
     * getCpcloudOriginIps
     *
     * @return
     */
    public OriginIpsResult getCpcloudOriginIps() {
        return new Remote().getCpcloudOriginIps();
    }

    /**
     * getSceneRemoteSessions
     *
     * @param sceneIds
     * @param nodeIds
     * @param userIds
     * @param monitor
     * @param assistance
     * @return
     */
    public ServiceResult<List<NodeSessionBO>> getSceneRemoteSessions(List<String> sceneIds, List<String> nodeIds,
                                                                     List<String> userIds, boolean monitor, boolean assistance) {
        if (!monitor && !assistance) {
            return new ServiceResult<>(new ArrayList<>());
        }

        Map<String, ConnectionLinkInfo> connectionLinkInfoMap = collectConnectionLinkInfo(sceneIds, nodeIds, userIds);
        String[] connectionIds = connectionLinkInfoMap.keySet().toArray(new String[0]);
        if (connectionIds.length == 0) {
            return new ServiceResult<>(new ArrayList<>());
        }

        Remote remoteClient = new Remote();
        Map<String, NodeSessionBO> connectionIdSessionMap = new HashMap<>();
        if (monitor) {
            Map<String, GuacamoleMonitorData.MonitorInfo> monitors = remoteClient.getMonitors(connectionIds);
            if (monitors != null) {
                monitors.forEach((connectionId, monitorData) -> {
                    NodeSessionBO nodeSession = connectionIdSessionMap.getOrDefault(connectionId,
                            new NodeSessionBO());
                    String formatUrl = monitorData.getMonitorUrl();
                    CloudAppConfig config = BeanCommon.getBean(CloudAppConfig.class);
                    if (config.getPublicNetProxy() && config.getPublicNetProxyServer()) {
                        if (StringUtils.isNotEmpty(config.getPublicNetProxyIp())) {
                            URL u = URLUtil.url(formatUrl);
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
            Map<String, GuacamoleAssistanceData.AssistanceInfo> assistances = remoteClient.getAssistances(connectionIds);
            if (assistances != null) {
                assistances.forEach((connectionId, assistanceData) -> {
                    NodeSessionBO nodeSession = connectionIdSessionMap.getOrDefault(connectionId,
                            new NodeSessionBO());
                    String formatUrl = assistanceData.getAssistanceUrl();
                    CloudAppConfig config = BeanCommon.getBean(CloudAppConfig.class);
                    if (config.getPublicNetProxy() && config.getPublicNetProxyServer()) {
                        if (StringUtils.isNotEmpty(config.getPublicNetProxyIp())) {
                            URL u = URLUtil.url(formatUrl);
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
            ConnectionLinkInfo connectionLinkInfo = connectionLinkInfoMap.get(connectionId);
            AccessModeLinkInfo accessModeLinkInfo = connectionLinkInfo.getAccessModeLinkInfo();
            SceneBO srcScene = accessModeLinkInfo.getSrcScene();
            CloudPluginSceneElementBO cloudElement = accessModeLinkInfo.getCloudElement();
            SceneElementBO srcElement = cloudElement.getSrcElement();
            nodeSession.setSceneId(srcScene.getEid());
            nodeSession.setSceneName(srcScene.getName());
            nodeSession.setNodeId(srcElement.getId());
            nodeSession.setNodeName(srcElement.getName());
            CloudAccessMode accessMode = accessModeLinkInfo.getAccessMode();
            nodeSession.setProtocol(accessMode.getProtocol());
        });

        return new ServiceResult<>(new ArrayList<>(connectionIdSessionMap.values()));
    }

    /**
     * getSceneRemoteRecordings
     *
     * @param sceneIds
     * @param nodeIds
     * @param userIds
     * @param queryParams
     * @return
     */
    public ListObjServiceResult<NodeRecordingBO> getSceneRemoteRecordings(List<String> sceneIds, List<String> nodeIds,
                                                                          List<String> userIds,
                                                                          RecordingQueryParams queryParams) {
        Map<String, ConnectionLinkInfo> connectionLinkInfoMap = collectConnectionLinkInfo(sceneIds, nodeIds, userIds);
        String[] connectionIds = connectionLinkInfoMap.keySet().toArray(new String[0]);
        if (connectionIds.length == 0) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }

        Remote remoteClient = new Remote();
        GetGuacamoleRecordingData queryData = new GetGuacamoleRecordingData();
        queryData.setConnectionID(Arrays.asList(connectionIds));
        queryData.setPage(queryParams.getPage());
        queryData.setLimit(queryParams.getPageSize());
        queryData.setParams(queryParams.getSearch());
        queryData.setStartTime(queryParams.getStartTime());
        queryData.setEndTime(queryParams.getEndTime());
        ListObjResult<GuacamoleRecordingData.RecordingInfo> rawRecordings = remoteClient.getRecordings(queryData);
        List<NodeRecordingBO> recordings = rawRecordings.getObjs().stream().map(rawRecording -> {
            ConnectionLinkInfo connectionLinkInfo = connectionLinkInfoMap.get(rawRecording.getConnectionId());
            AccessModeLinkInfo accessModeLinkInfo = connectionLinkInfo.getAccessModeLinkInfo();
            SceneBO srcScene = accessModeLinkInfo.getSrcScene();
            CloudPluginSceneElementBO cloudElement = accessModeLinkInfo.getCloudElement();
            SceneElementBO srcElement = cloudElement.getSrcElement();
            CloudAccessMode accessMode = accessModeLinkInfo.getAccessMode();

            NodeRecordingBO recording = new NodeRecordingBO();
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
                        URL u = URLUtil.url(formatUrl);
                        formatUrl = StringUtils.replaceOnce(formatUrl, u.getHost(), config.getPublicNetProxyIp());
                        recording.setFileUrl(formatUrl);
                    }
                    if (StringUtils.isNotEmpty(recording.getPlayUrl())) {
                        String formatUrl = recording.getPlayUrl();
                        URL u = URLUtil.url(formatUrl);
                        formatUrl = StringUtils.replaceOnce(formatUrl, u.getHost(), config.getPublicNetProxyIp());
                        recording.setPlayUrl(formatUrl);
                    }
                }
            }

            String filesizeStr = rawRecording.getFileSize();
            if (!StringUtils.isEmpty(filesizeStr)) {
                recording.setFilesize(IntUtils.fromStr(filesizeStr));
            }
            String durationStr = rawRecording.getRecortime();
            if (!StringUtils.isEmpty(durationStr)) {
                recording.setDuration(FloatUtils.fromStr(durationStr));
            }
            String timeStr = rawRecording.getRecordate();
            if (!StringUtils.isEmpty(timeStr)) {
                recording.setTime(TimeUtils.utcStringToLocalDateTime(timeStr));
            }

            return recording;
        }).collect(Collectors.toList());

        return new ListObjServiceResult<>(new ListObjResult<>(recordings, rawRecordings.getTotal()));
    }

    /**
     * enableRemoteRecording
     *
     * @param sceneId
     * @param nodeId
     * @param userId
     * @param connectionId
     * @param context
     * @return
     */
    public ServiceResult<NodeConnectionBO> enableRemoteRecording(String sceneId, String nodeId, String userId, String connectionId,
                                                                 CloudScenePluginContext context) {
        Map<String, ConnectionLinkInfo> connectionLinkInfoMap = collectConnectionLinkInfo(sceneId, List.of(nodeId), List.of(userId));
        if (!connectionLinkInfoMap.containsKey(connectionId)) {
            return new ServiceResult<>(null, CloudErrorCode.CONNECTION_NOT_EXIST);
        }
        ConnectionLinkInfo connectionLinkInfo = connectionLinkInfoMap.get(connectionId);
        AccessModeLinkInfo accessModeInfo = connectionLinkInfo.getAccessModeLinkInfo();
        CloudSceneConnectionCommon.SceneConnectionEnableRecordingExtraParam extraParam = new CloudSceneConnectionCommon.SceneConnectionEnableRecordingExtraParam();
        extraParam.setSceneName(accessModeInfo.getSrcScene().getName());
        extraParam.setNodeName(accessModeInfo.getCloudElement().getSrcElement().getName());
        extraParam.setUsername(userId);
        extraParam.setDepartment(context == null ? StringUtils.EMPTY : context.getRequestUserOrg());
        extraParam.setSceneId(sceneId);
        extraParam.setNodeId(nodeId);
        CloudSceneConnectionCommon.enableRecording(connectionLinkInfo.getSceneConnection(), extraParam);
        return new ServiceResult<>(connectionLinkInfo.toNewNodeConnection());
    }

    /**
     * disableRemoteRecording
     *
     * @param sceneId
     * @param nodeId
     * @param userId
     * @param connectionId
     * @return
     */
    public ServiceResult<NodeConnectionBO> disableRemoteRecording(String sceneId, String nodeId, String userId, String connectionId) {
        Map<String, ConnectionLinkInfo> connectionLinkInfoMap = collectConnectionLinkInfo(sceneId, List.of(nodeId), List.of(userId));
        if (!connectionLinkInfoMap.containsKey(connectionId)) {
            // 不再抛出异常
            return new ServiceResult<>(new NodeConnectionBO());
        }
        ConnectionLinkInfo connectionLinkInfo = connectionLinkInfoMap.get(connectionId);
//        CloudSceneConnectionCommon.disableRecording(connectionLinkInfo.getSceneConnection());
        CloudSceneConnectionCommon.convertRecording(connectionLinkInfo.getSceneConnection());
        return new ServiceResult<>(connectionLinkInfo.toNewNodeConnection());
    }

    /**
     * getRemoteTtyConnection
     *
     * @param sceneId
     * @param nodeId
     * @return
     */
    public ServiceResult<NodeConnectionBO> getRemoteTtyConnection(String sceneId, String nodeId) {
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, null);
        CloudScenePluginHandler cloudPlugin = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (cloudPlugin != null) {
            CloudPluginSceneElementBO cloudElement = cloudPlugin.getElement(nodeId);
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

    /**
     * getSceneRemoteConnection
     *
     * @param sceneId
     * @param nodeId
     * @param accessModeId
     * @param userId
     * @param context
     * @return
     */
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
        var cloudElement = accessModeInfo.getCloudElement();
        var sceneConnectionParam = new CloudSceneConnectionCommon.SceneConnectionParam();
        sceneConnectionParam.setSceneId(sceneId);
        sceneConnectionParam.setSceneElementId(nodeId);
        sceneConnectionParam.setResourceId(cloudElement.getResourceId());
        sceneConnectionParam.setUserId(userId);
        sceneConnectionParam.setCloudAccessMode(accessModeInfo.accessMode);
        sceneConnectionParam.setConfigIp(cloudElement.getConfigIp());
        var sceneConnection = CloudSceneConnectionCommon.getOrCreateSceneConnection(sceneConnectionParam);

        var cloudScene = accessModeInfo.getCloudScene();
        if (cloudScene != null && Boolean.TRUE.equals(cloudScene.getEnableRecording())) {
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

        if (sceneConnection == null) {
            return new ServiceResult<>(null);
        }

        nodeConnection.setConnectionId(sceneConnection.getConnectionId());
        nodeConnection.setConnectionUrl(sceneConnection.getConnectionUrl());
        return new ServiceResult<>(nodeConnection);
    }

    private String formatUrl(String inputUrl, AccessModeLinkInfo modeLinkInfo) {
        String formatUrl = inputUrl;
        if (CloudSingleton.cloudAppConfig.getPublicNetProxyServer()) {
            CloudAccessMode accessMode = modeLinkInfo.getAccessMode();
            URL u = URLUtil.url(inputUrl);
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

    /**
     * 查询网元指定字段的聚合统计数量
     *
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
                Map<String, Map<Object, Long>> countResult = cloudSceneStatCommon.groupCountElement(sceneIds, nodeIds, fieldName, List.of(fieldName), false);
                result.put(fieldName, countResult.get(fieldName));
            });
        }

        return new ServiceResult<>(result);
    }

    /**
     * 查询网元指定字段的聚合统计数量
     *
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
            result.putAll(cloudSceneStatCommon.countElement(sceneIds, nodeIds, countFieldNames, true));
        }
        if (!calcCountFieldNames.isEmpty()) {
            result.putAll(cloudSceneStatCommon.calcCountElement(sceneIds, nodeIds, calcCountFieldNames));
        }

        return new ServiceResult<>(result);
    }

    /**
     * 查询端口指定字段的聚合统计数量
     *
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
            result.putAll(cloudSceneStatCommon.countPort(sceneIds, nodeIds, countFieldNames, true));
        }
        if (!calcCountFieldNames.isEmpty()) {
            result.putAll(cloudSceneStatCommon.calcCountPort(sceneIds, nodeIds, calcCountFieldNames));
        }

        return new ServiceResult<>(result);
    }

    /**
     * expireAllRemoteConnect
     *
     * @param userId
     * @return
     */
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

    /**
     * getRecordingUsers
     *
     * @param sceneId
     * @param nodeIds
     * @return
     */
    public ServiceResult<List<String>> getRecordingUsers(String sceneId, List<String> nodeIds) {
        HumanCondition humanCondition = new HumanCondition().filterEqual(CloudPluginSceneConnectionModel::getScene, sceneId);
        if (CollectionUtils.isNotEmpty(nodeIds)) {
            humanCondition.filterIn(CloudPluginSceneConnectionModel::getSceneElement, nodeIds.toArray(new String[]{}));
        }
        List<CloudPluginSceneConnectionModel> list = cloudPluginSceneConnectionMapper.list(humanCondition);
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

    /**
     * changeVlanRange
     *
     * @param vlanRange
     */
    public void changeVlanRange(String vlanRange) {
        TaskCommon.executeTask(() -> {
            if (StringUtils.isEmpty(vlanRange)) {
                return;
            }
            // 转换格式
            String formatVlanRange = vlanRange.replace("-", ":");
            boolean result = new Remote().changeVlanRange(formatVlanRange);
            if (!result) {
                log.error("change vlan range {} error", formatVlanRange);
            }
        });
    }

    /**
     * linkStatus
     *
     * @param sceneId
     * @return
     */
    public ServiceResult<List<NodeLinkStatusBO>> linkStatus(String sceneId) {
        try {
//            List<NodeLinkStatusBO> list = SceneInstanceLinkCacheUtil.getCached(sceneId);
//            SceneInstanceLinkCacheUtil.tryStartCronRefresh();
            List<NodeLinkStatusBO> list = new ArrayList<>();
            //log.warn("not run link status function");
            return new ServiceResult<>(list);
        } catch (Exception e) {
            log.error("failed get link status for scene {} , message is {}", sceneId, ExceptionUtils.getStackTrace(e));
            return new ServiceResult<>(new ArrayList<>());
        }
    }

    /**
     * execElementCommand
     *
     * @param sceneId
     * @param nodeId
     * @param command
     * @param user
     * @return
     */
    public ServiceResult<ElementCommandExecBO> execElementCommand(String sceneId, String nodeId, String command, String user) {
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, null);
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        CloudPluginSceneElementBO node = pluginHandler.getElement(nodeId);
        if (node != null && node.getVirtualType() != null) {
            if (BaseCloudPluginSceneElementModel.VirtualType.CONTAINER == node.getVirtualType()) {
                Container container = new Container();
                ContainerExecuteResult result = container.executeCommand(node.getResourceId(), command,user);
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
                VirtualMachinePrivateOperateData param = this.getVirtualMachineUploadFileInfo(pluginHandler, node);
                if (param != null) {
                    VirtualMachineExecCommandData execCommandData = new VirtualMachineExecCommandData();
                    BeanUtils.copyProperties(param, execCommandData);
                    execCommandData.setCmd(command);
                    VirtualMachineExecuteResult result = virtualMachine.execCommand(execCommandData);
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

    /**
     * uploadElementFile
     *
     * @param sceneId
     * @param nodeId
     * @param filePath
     * @param filename
     * @param destPath
     * @return
     */
    public ServiceResult<Boolean> uploadElementFile(String sceneId, String nodeId, String filePath, String filename, String destPath) {
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, null);
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        CloudPluginSceneElementBO node = pluginHandler.getElement(nodeId);
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
                    boolean result = container.copyFile(node.getResourceId(), tmpZipFilepath, destPath);
                    return new ServiceResult<>(result);
                } finally {
                    try {
                        FileUtils.deleteDirectory(file);
                    } catch (Exception e) {
                        log.error("failed to delete dir , {} ", tmpZipFilepath, e);
                    }
                }
            } else if (BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE == node.getVirtualType()) {
                VirtualMachinePrivateOperateData param = this.getVirtualMachineUploadFileInfo(pluginHandler, node);
                if (param != null) {
                    String standardSystemType = SystemTypeUtils.getStandardSystemType(node.getSrcElement().getSystemType());
                    VirtualMachineUploadFileData uploadInfoData = new VirtualMachineUploadFileData();
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

    /**
     * uploadElementFile
     *
     * @param sceneId
     * @param nodeId
     * @param inputStream
     * @param filename
     * @param destPath
     * @return
     */
    public ServiceResult<Boolean> uploadElementFile(String sceneId, String nodeId, InputStream inputStream, String filename, String destPath) {
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, null);
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        CloudPluginSceneElementBO node = pluginHandler.getElement(nodeId);
        if (node != null && node.getVirtualType() != null) {
            if (BaseCloudPluginSceneElementModel.VirtualType.CONTAINER == node.getVirtualType()) {
                Container container = new Container();
                String tmpFileDir = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, FileUtil.getTmpDirPath(), sceneId, TextUtils.uuid());
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
                VirtualMachinePrivateOperateData param = this.getVirtualMachineUploadFileInfo(pluginHandler, node);
                String finalPath = FsCommon.upload(inputStream);
                if (param != null) {
                    String standardSystemType = SystemTypeUtils.getStandardSystemType(node.getSrcElement().getSystemType());
                    VirtualMachineUploadFileData uploadInfoData = new VirtualMachineUploadFileData();
                    BeanUtils.copyProperties(param, uploadInfoData);
                    uploadInfoData.setFile(finalPath);
                    uploadInfoData.setDestPath(destPath + SystemTypeCommandUtils.getPathDelimiter(standardSystemType) + filename);
                    VirtualMachine virtualMachine = new VirtualMachine();
                    virtualMachine.uploadFile(uploadInfoData);
                    // todo 报错信息处理下
                    return new ServiceResult<>(true);
                }
            }
        }
        return new ServiceResult<>(true);
    }

    /**
     * getVirtualMachineUploadFileInfo
     *
     * @param pluginHandler
     * @param node
     * @return
     */
    private VirtualMachinePrivateOperateData getVirtualMachineUploadFileInfo(CloudScenePluginHandler pluginHandler, CloudPluginSceneElementBO node) {
        List<SceneElementPortBO> ports = node.getSrcElement().getPorts();
        if (CollectionUtils.isNotEmpty(ports)) {
            for (SceneElementPortBO port : ports) {
                if (CollectionUtils.isNotEmpty(port.getAccessModes())) {
                    for (AccessMode mode : port.getAccessModes()) {
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

    /**
     * countUserConnections
     *
     * @param users
     * @return
     */
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
        List<CloudPluginSceneConnectionModel> list = cloudPluginSceneConnectionMapper.list(condition);
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
        Map<String, GuacamoleMonitorData.MonitorInfo> monitors = new Remote().getMonitors(connectionIds.toArray(new String[]{}));
        if (monitors == null || monitors.isEmpty()) {
            return new ServiceResult<>(connectionNumBOS);
        }
        Map<String, Set<String>> user2ConnectionIds = new HashMap<>();
        for (CloudPluginSceneConnectionModel model : list) {
            if (!user2ConnectionIds.containsKey(model.getAccessUser())) {
                user2ConnectionIds.put(model.getAccessUser(), new HashSet<>());
            }
            if (StringUtils.isNotEmpty(model.getConnectionId())) {
                user2ConnectionIds.get(model.getAccessUser()).add(model.getConnectionId());
            }
        }
        for (UserConnectionNumBO node : connectionNumBOS) {
            Set<String> userConnectIds = user2ConnectionIds.get(node.getUser());
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

    /**
     * getUserNodeSessions
     *
     * @param user
     * @param monitor
     * @param assistance
     * @return
     */
    public ServiceResult<List<NodeSessionBO>> getUserNodeSessions(String user, boolean monitor, boolean assistance) {
        HumanCondition condition = new HumanCondition()
                .filterEqual(CloudPluginSceneConnectionModel::getDisable, false)
                .filterEqual(CloudPluginSceneConnectionModel::getAccessUser, user);
        List<CloudPluginSceneConnectionModel> connectionModels = cloudPluginSceneConnectionMapper.list(condition);
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
            CloudPluginSceneConnectionModel model = connectionId2Model.get(connectionId);
            if (model != null) {
                session.setSceneId(model.getScene());
                session.setNodeId(model.getSceneElement());
            }
        });
        return new ServiceResult<>(new ArrayList<>(connectionIdSessionMap.values()));
    }

    /**
     * getNodeSessions
     *
     * @param connectionIds
     * @param monitor
     * @param assistance
     * @return
     */
    private Map<String, NodeSessionBO> getNodeSessions(String[] connectionIds, boolean monitor, boolean assistance) {
        Remote remoteClient = new Remote();
        Map<String, NodeSessionBO> connectionIdSessionMap = new HashMap<>();
        if (monitor) {
            Map<String, GuacamoleMonitorData.MonitorInfo> monitors = remoteClient.getMonitors(connectionIds);
            if (monitors != null) {
                monitors.forEach((connectionId, monitorData) -> {
                    NodeSessionBO nodeSession = connectionIdSessionMap.getOrDefault(connectionId,
                            new NodeSessionBO());
                    String formatUrl = monitorData.getMonitorUrl();
                    CloudAppConfig config = BeanCommon.getBean(CloudAppConfig.class);
                    if (config.getPublicNetProxy() && config.getPublicNetProxyServer()) {
                        if (StringUtils.isNotEmpty(config.getPublicNetProxyIp())) {
                            URL u = URLUtil.url(formatUrl);
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
            Map<String, GuacamoleAssistanceData.AssistanceInfo> assistances = remoteClient.getAssistances(connectionIds);
            if (assistances != null) {
                assistances.forEach((connectionId, assistanceData) -> {
                    NodeSessionBO nodeSession = connectionIdSessionMap.getOrDefault(connectionId,
                            new NodeSessionBO());
                    String formatUrl = assistanceData.getAssistanceUrl();
                    CloudAppConfig config = BeanCommon.getBean(CloudAppConfig.class);
                    if (config.getPublicNetProxy() && config.getPublicNetProxyServer()) {
                        if (StringUtils.isNotEmpty(config.getPublicNetProxyIp())) {
                            URL u = URLUtil.url(formatUrl);
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

    /**
     * 获取统计环境总速率信息
     *
     * @param portIds
     * @param totalSecond
     * @return
     */
    public NetworkFlowRateResult networkFlowRate(List<String> portIds, Long totalSecond) {
        log.debug("ScenePluginCloudSceneService.networkFlowRate begin. portIds:{}", portIds);
        NetworkFlowRateResult result = new Remote().networkFlowRate(portIds, totalSecond);
        log.debug("ScenePluginCloudSceneService.networkFlowRate end. result:{}", result);
        return result;
    }
}
