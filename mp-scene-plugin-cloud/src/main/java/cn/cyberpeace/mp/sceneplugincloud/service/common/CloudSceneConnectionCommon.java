package cn.cyberpeace.mp.sceneplugincloud.service.common;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.IntUtils;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConnectionModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ConsoleInfoGetResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GuacamoleCreateConnectionData;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConnectionMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class CloudSceneConnectionCommon {

    private static CloudPluginSceneConnectionMapper cloudPluginSceneConnectionMapper;

    @Autowired
    private CloudSceneConnectionCommon(CloudPluginSceneConnectionMapper cloudPluginSceneConnectionMapper) {
        CloudSceneConnectionCommon.cloudPluginSceneConnectionMapper = cloudPluginSceneConnectionMapper;
    }

    @Data
    @NoArgsConstructor
    public static class SceneConnectionParam {
        private String sceneId;
        private String sceneElementId;
        private String resourceId;
        private String resourceHost;
        private String sceneElementPortId;
        private CloudAccessMode cloudAccessMode;
        private String userId;

//        private String username;
        private String sceneName;
        private String department;
    }

    @Data
    @NoArgsConstructor
    public static class SceneConnectionEnableRecordingExtraParam {
        private String sceneName;
        private String nodeName;
        private String username;
        private String department;
    }

    public static CloudPluginSceneConnectionModel getOrCreateSceneConnection(SceneConnectionParam param) {
        var sceneId = param.getSceneId();
        var sceneElementId = param.getSceneElementId();
        var sceneElementPortId = param.getSceneElementPortId();
        var cloudAccessMode = param.getCloudAccessMode();
        var userId = param.getUserId();
        var accessModeId = cloudAccessMode.getId();
        if (StringUtils.isEmpty(accessModeId)) {
            log.error("Get or create scene({}) element({}) port({}) {} connection error: empty access mode id",
                    sceneId, sceneElementId, sceneElementPortId, cloudAccessMode.getProtocol());
            return null;
        }

        var isConsole = cloudAccessMode.getProtocol().equals(AccessMode.Protocol.CONSOLE);
        if (!isConsole && StringUtils.isEmpty(cloudAccessMode.getAccessIp())) {
            log.error("Get or create scene({}) element({}) port({}) {} connection error: empty access ip",
                    sceneId, sceneElementId, sceneElementPortId, cloudAccessMode.getProtocol());
            return null;
        }

        CloudPluginSceneConnectionModel sceneConnection = cloudPluginSceneConnectionMapper.first(new HumanCondition()
                .filterEqual(CloudPluginSceneConnectionModel::getScene, sceneId)
                .filterEqual(CloudPluginSceneConnectionModel::getSceneElement, sceneElementId)
                .filterEqual(CloudPluginSceneConnectionModel::getSceneElementPort, sceneElementPortId)
                .filterEqual(CloudPluginSceneConnectionModel::getAccessModeId, accessModeId)
                .filterEqual(CloudPluginSceneConnectionModel::getAccessUser, userId)
                .reverseOrderBy(CloudPluginSceneConnectionModel::getCreateTime));

        // console信息可能会变，获取最新的console信息
        var resourceId = param.getResourceId();
        var resourceHost = param.getResourceHost();
        var accessIp = sceneConnection == null ? cloudAccessMode.getAccessIp() : sceneConnection.getAccessIp();
        var accessPort = sceneConnection == null ? cloudAccessMode.getAccessPort() : sceneConnection.getAccessPort();
        if (isConsole && !StringUtils.isEmpty(resourceId) && !StringUtils.isEmpty(resourceHost)) {
            var vmClient = new VirtualMachine();
            ConsoleInfoGetResult consoleInfo = null;
            try {
                consoleInfo = vmClient.getConsoleInfo(resourceId, resourceHost);
            } catch (Exception e) {
                log.error("Get instance({}, {}) console info error:", resourceHost, resourceId, e);
            }
            // console信息不同，需要创建新的连接
            if (consoleInfo != null) {
                var latestConsoleIp = consoleInfo.getIpAddress();
                var latestConsolePort = IntUtils.fromStr(consoleInfo.getPort());
                if (latestConsoleIp != null
                        && (!latestConsoleIp.equals(accessIp) || !latestConsolePort.equals(accessPort))) {
                    sceneConnection = null;
                    accessIp = latestConsoleIp;
                    accessPort = latestConsolePort;
                }
            } else {
                log.error("Get instance({}, {}) console empty info", resourceHost, resourceId);
            }
        }
        if (StringUtils.isEmpty(accessIp) || accessPort == null) {
            log.error("Get or create scene({}) element({}) port({}) {} connection error: empty access ip({}) or port({})",
                    sceneId, sceneElementId, sceneElementPortId, cloudAccessMode.getProtocol(), accessIp, accessPort);
            return null;
        }

        if (sceneConnection == null) {
            sceneConnection = new CloudPluginSceneConnectionModel();
            sceneConnection.setScene(sceneId);
            sceneConnection.setSceneElement(sceneElementId);
            sceneConnection.setSceneElementPort(sceneElementPortId);
            sceneConnection.setAccessModeId(accessModeId);
            sceneConnection.setAccessUser(userId);
            sceneConnection.setProtocol(cloudAccessMode.getProtocol());
            sceneConnection.setPort(cloudAccessMode.getPort());
            sceneConnection.setUsername(cloudAccessMode.getUsername());
            sceneConnection.setPassword(cloudAccessMode.getPassword());
            sceneConnection.setAccessIp(accessIp);
            sceneConnection.setAccessPort(accessPort);
        }

        var dataUpdateFlag = false;
        var remoteClient = new Remote();
        if (StringUtils.isEmpty(sceneConnection.getConnectionId())) {
            var guacamoleCreateConnectionData = new GuacamoleCreateConnectionData();
            guacamoleCreateConnectionData.setScene(param.getSceneName());
            guacamoleCreateConnectionData.setUsername(userId);
            guacamoleCreateConnectionData.setDepartment(param.getDepartment());
            guacamoleCreateConnectionData.setFlag(false);
            var connectionParams = new GuacamoleCreateConnectionData.ConnectionParams();
            var protocol = sceneConnection.getProtocol().equals(AccessMode.Protocol.CONSOLE) ?
                    AccessMode.Protocol.VNC : sceneConnection.getProtocol();
            connectionParams.setProtocol(protocol);
            connectionParams.setPort(sceneConnection.getAccessPort().toString());
            connectionParams.setUsername(sceneConnection.getUsername());
            connectionParams.setPassword(sceneConnection.getPassword());
            connectionParams.setHostname(sceneConnection.getAccessIp());
            guacamoleCreateConnectionData.setConnectionParams(connectionParams);
            var result = remoteClient.createConnection(guacamoleCreateConnectionData);
            if (result == null || StringUtils.isEmpty(result.getConnectionId())) {
                log.error("Create scene connection[{}] with empty result: {}", guacamoleCreateConnectionData, result);
                return null;
            } else {
                sceneConnection.setConnectionId(result.getConnectionId());
                dataUpdateFlag = true;
            }
        }

        var result = remoteClient.getConnectionUrl(sceneConnection.getConnectionId(), userId);
        if (result == null || StringUtils.isEmpty(result.getUrl())) {
            log.error("Get scene connection[{}] url with empty result: {}", sceneConnection.getConnectionId(), result);
        } else {
            if (!result.getUrl().equals(sceneConnection.getConnectionUrl())) {
                sceneConnection.setConnectionUrl(result.getUrl());
                sceneConnection.setAccessTokenExpireTime(LocalDateTime.now()
                        .plusSeconds(CloudSingleton.cloudAppConfig.getRemoteConnectionTokenExpireSeconds()));
                dataUpdateFlag = true;
            }
        }

        if (dataUpdateFlag) {
            cloudPluginSceneConnectionMapper.save(sceneConnection);
        }

        return sceneConnection;
    }

    public static boolean enableRecording(CloudPluginSceneConnectionModel sceneConnection,
                                          SceneConnectionEnableRecordingExtraParam extraParam) {
        var connectionId = sceneConnection.getConnectionId();
        if (StringUtils.isEmpty(connectionId)) {
            return false;
        }

        var remoteClient = new Remote();
        var result = remoteClient.enableRecording(connectionId, extraParam.getSceneName(),
                extraParam.getNodeName(), extraParam.getUsername(), extraParam.getDepartment());
        var flag = !StringUtils.isEmpty(result);
        if (flag) {
            sceneConnection.setConnectionEnableRecording(true);
            sceneConnection.setConnectionRecordingName(result);
            cloudPluginSceneConnectionMapper.save(sceneConnection);
        }

        return flag;
    }

    public static boolean disableRecording(CloudPluginSceneConnectionModel sceneConnection) {
        var connectionId = sceneConnection.getConnectionId();
        if (StringUtils.isEmpty(connectionId)) {
            return false;
        }

        var remoteClient = new Remote();
        var result = remoteClient.disableRecording(connectionId);
        var flag = !StringUtils.isEmpty(result);
        if (flag) {
            sceneConnection.setConnectionEnableRecording(false);
            sceneConnection.setConnectionRecordingName(result);
            cloudPluginSceneConnectionMapper.save(sceneConnection);
        }

        return flag;
    }
}
