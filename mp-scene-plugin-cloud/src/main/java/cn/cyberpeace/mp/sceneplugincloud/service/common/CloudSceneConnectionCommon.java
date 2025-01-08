package cn.cyberpeace.mp.sceneplugincloud.service.common;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.IntUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConnectionModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ComInfoGetResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ConsoleInfoGetResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GuacamoleCreateConnectionData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConnectionMapper;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@DependsOn("situationAwareConfig")
public class CloudSceneConnectionCommon {

    private static CloudPluginSceneConnectionMapper cloudPluginSceneConnectionMapper;
    private final static String CONTAINER_EXEC_COMMAND_TEMPLATE = "az login --service-principal -u %s -p %s --tenant %s > /dev/null 2>&1 && az container exec --resource-group %s --name %s --exec-command '%s'";

    private final static String LOAD_EXEC_COMMAND_TEMPLATE = "bash -c 'echo %s;%s;bash'";
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
        private String systemType;
        private Boolean entity;

        //        private String username;
        private String sceneName;
        private String department;
    }

    @Data
    @NoArgsConstructor
    public static class SceneConnectionEnableRecordingExtraParam {
        private String sceneId;
        private String sceneName;
        private String nodeId;
        private String nodeName;
        private String username;
        private String department;
    }

    public static CloudPluginSceneConnectionModel getOrCreateSceneConnection(SceneConnectionParam param, SituationAwareConfig situationAwareConfig, String interpreter) {
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

        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, null);
        Integer cloudType = sceneHandler.getScene().getCloudType();

        var isConsole = cloudAccessMode.getProtocol().equals(AccessMode.Protocol.CONSOLE);
        var isCom = cloudAccessMode.getProtocol().equals(AccessMode.Protocol.COM);
        if ((!isConsole && !isCom) && StringUtils.isEmpty(cloudAccessMode.getAccessIp())) {
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
                .filterEqual(CloudPluginSceneConnectionModel::getDisable, false)
                .reverseOrderBy(CloudPluginSceneConnectionModel::getCreateTime));
        if (sceneConnection != null && StringUtils.isNotEmpty(sceneConnection.getConnectionId())) {
            sceneConnection = null;
        }

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
                if (latestConsoleIp != null) {
                    sceneConnection = null;
                    accessIp = latestConsoleIp;
                    accessPort = latestConsolePort;
                }
            } else {
                log.error("Get instance({}, {}) console empty info", resourceHost, resourceId);
            }
        } else if (isCom && !StringUtils.isEmpty(resourceId) && !StringUtils.isEmpty(resourceHost)) {
            var vmClient = new VirtualMachine();
            ComInfoGetResult comInfoGetResult = null;
            try {
                comInfoGetResult = vmClient.getComInfo(resourceId, resourceHost);
            } catch (Exception e) {
                log.error("Get instance({}, {}) console info error:", resourceHost, resourceId, e);
            }
            // com信息不同，需要创建新的连接
            if (comInfoGetResult != null) {
                var latestConsoleIp = comInfoGetResult.getIpAddress();
                var latestConsolePort = IntUtils.fromStr(comInfoGetResult.getPort());
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
            guacamoleCreateConnectionData.setHost(param.getResourceHost());
            guacamoleCreateConnectionData.setFlag(false);
            var connectionParams = new GuacamoleCreateConnectionData.ConnectionParams();
            // 协议的特殊处理
            String protocol = null;
            if (Boolean.TRUE.equals(param.getEntity()) && sceneConnection.getProtocol().equals(AccessMode.Protocol.CONSOLE)) {
                // 实体节点的console协议使用telnet代替
                protocol = AccessMode.Protocol.TELNET;
            } else if (sceneConnection.getProtocol().equals(AccessMode.Protocol.CONSOLE)) {
                protocol = AccessMode.Protocol.VNC;
            } else if (sceneConnection.getProtocol().equals(AccessMode.Protocol.COM)) {
                protocol = AccessMode.Protocol.TELNET;
            } else {
                protocol = sceneConnection.getProtocol();
            }

            connectionParams.setProtocol(protocol);
            // windows系统的rdp连接,需要禁用字形缓存
            if (AccessMode.Protocol.RDP.equals(protocol) && SystemTypeCommandUtils.isWindows(SystemTypeUtils.getStandardSystemType(param.getSystemType()))) {
                connectionParams.setDisableGlyphCaching(true);
            }
            connectionParams.setPort(sceneConnection.getAccessPort().toString());
            if (Boolean.TRUE.equals(cloudAccessMode.getPrivateKeyEnable())) {
                connectionParams.setPrivateKeyPath(cloudAccessMode.getPrivateKeyPath());
                if (StringUtils.isNotEmpty(sceneConnection.getUsername())) {
                    connectionParams.setUsername(sceneConnection.getUsername());
                }
            } else {
                connectionParams.setUsername(sceneConnection.getUsername());
                connectionParams.setPassword(sceneConnection.getPassword());
            }
            if (BaseSceneModel.CloudType.DEFAULT != cloudType) {

                CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
                CloudPluginSceneElementBO element = pluginHandler.getElement(sceneElementId);
                String halfString = halfString(sceneId) + halfString(element.getSrcElement().getEid());
                if (BaseCloudPluginSceneElementModel.ImageType.LAAS_VM.equals(element.getImageType())) {
                    if (element.getIsPublicIp()) {
                        connectionParams.setHostname(element.getPublicIpAddress());
                    } else if (StringUtils.isNotBlank(element.getManagementIp())) {
                        userId = userId + "_" + halfString;
                        connectionParams.setHostname(sceneConnection.getAccessIp());
                        guacamoleCreateConnectionData.setHost(element.getManagementIp());
                    }
                } else if (BaseCloudPluginSceneElementModel.ImageType.CONTAINER_REGISTRY.equals(element.getImageType())) {
                    userId = userId + "_" + halfString;
                    guacamoleCreateConnectionData.setHost(element.getManagementIp());
                    connectionParams.setHostname(element.getManagementIp());
                    connectionParams.setProtocol("container");
                    connectionParams.setPort("22");
                    connectionParams.setUsername(situationAwareConfig.getManagementUsername());
                    connectionParams.setPassword(situationAwareConfig.getManagementPassword());
                    connectionParams.setCmd(getContainerCmd(pluginHandler, cloudType, element, interpreter));
                } else if (StringUtils.isNotBlank(interpreter) && AccessMode.Protocol.SSH.equals(protocol)) {
                    userId = userId + "_" + halfString;
                    connectionParams.setHostname(element.getSrcElement().getPorts().get(0).getIp());
                    connectionParams.setProtocol("container");
                    connectionParams.setPort(sceneConnection.getPort().toString());
                    connectionParams.setCmd(getLoadCmd(interpreter));
                } else {
                    connectionParams.setHostname(accessIp);
                }
            } else {
                connectionParams.setHostname(accessIp);
            }

            guacamoleCreateConnectionData.setUsername(userId);
            guacamoleCreateConnectionData.setConnectionParams(connectionParams);
            var result = remoteClient.createConnection(guacamoleCreateConnectionData);
            String s = JsonUtils.snakeStringify(guacamoleCreateConnectionData);
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


    public static String getOrCreateBorderConnection(String protocol, String hostname, String port, String username, String password, String userId) {
        var remoteClient = new Remote();
        var connectionParams = new GuacamoleCreateConnectionData.ConnectionParams();
        connectionParams.setProtocol(protocol);
        connectionParams.setHostname(hostname);
        connectionParams.setPort(port);
        connectionParams.setUsername(username);
        connectionParams.setPassword(password);
        var guacamoleCreateConnectionData = new GuacamoleCreateConnectionData();
        guacamoleCreateConnectionData.setUsername(userId);
        guacamoleCreateConnectionData.setConnectionParams(connectionParams);

        var result = remoteClient.createConnection(guacamoleCreateConnectionData);
        String s = JsonUtils.snakeStringify(guacamoleCreateConnectionData);
        if (result == null || StringUtils.isEmpty(result.getConnectionId())) {
            log.error("Create scene connection[{}] with empty result: {}", guacamoleCreateConnectionData, result);
            return null;
        }
        CloudPluginSceneConnectionModel sceneConnection = new CloudPluginSceneConnectionModel();
        var result1 = remoteClient.getConnectionUrl(result.getConnectionId(), userId);
        if (result == null || StringUtils.isEmpty(result.getUrl())) {
            log.error("Get scene connection[{}] url with empty result: {}", hostname, result);
            return null;
        } else {
            return result1.getUrl();
        }
    }

    public static boolean enableRecording(CloudPluginSceneConnectionModel sceneConnection,
                                          SceneConnectionEnableRecordingExtraParam extraParam) {
        var connectionId = sceneConnection.getConnectionId();
        if (StringUtils.isEmpty(connectionId)) {
            return false;
        }

        var remoteClient = new Remote();
        var result = remoteClient.enableRecording(connectionId, extraParam.getSceneName(), extraParam.getSceneId(),
                extraParam.getNodeName(), extraParam.getNodeId(), extraParam.getUsername(), extraParam.getDepartment());
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

    public static boolean convertRecording(CloudPluginSceneConnectionModel sceneConnection) {
        var connectionId = sceneConnection.getConnectionId();
        if (StringUtils.isEmpty(connectionId)) {
            return false;
        }

        var remoteClient = new Remote();
        var result = remoteClient.convertRecording(connectionId);
        var flag = !StringUtils.isEmpty(result);
        sceneConnection.setDisable(true);
        if (flag) {
            sceneConnection.setConnectionRecordingName(result);
        }
        cloudPluginSceneConnectionMapper.save(sceneConnection);
        return flag;
    }

    public static String getContainerConsoleUrl(String resourceId) {
        String url = new Remote().getContainerConsoleUrl(resourceId);
        if (StringUtils.isEmpty(url)) {
            log.error("failed to get container console url , containId is {}", resourceId);
        }
        return url;
    }

    /**
     * 根据插件处理器、云类型和云插件场景元素生成容器启动命令。
     * 此方法专门用于构建针对Azure容器实例的启动命令，命令中包含了Azure AD客户端的凭证信息，
     * 以及容器所属的资源组和容器名称。
     *
     * @param pluginHandler 云场景插件处理器，用于获取场景ID。
     * @param cloudType     云类型，用于获取特定云服务商的资源管理器实例。
     * @param cloudElement  云插件场景元素，用于生成容器名称。
     * @param interpreter
     * @return 返回格式化后的容器启动命令字符串。
     */
    private static String getContainerCmd(CloudScenePluginHandler pluginHandler, Integer cloudType, CloudPluginSceneElementBO cloudElement, String interpreter) {
        IAzureResourceManager azureResourceManager = IAzureResourceManager.getInstance(cloudType);
        String azureClientId = azureResourceManager.getUser().getAzureClientId();
        String azureClientSecret = azureResourceManager.getUser().getAzureClientSecret();
        String azureTenantId = azureResourceManager.getUser().getAzureTenantId();
        String groupName = pluginHandler.getSourceSceneId();
        String containerName = HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid());
        return String.format(CONTAINER_EXEC_COMMAND_TEMPLATE, azureClientId, azureClientSecret, azureTenantId, groupName, containerName, interpreter);
    }

    /**
     * 包装载荷命令
     * @param interpreter
     * @return
     */
    private static String getLoadCmd(String interpreter) {
        return String.format(LOAD_EXEC_COMMAND_TEMPLATE, interpreter, interpreter);
    }


    /**
     * 截取一半的字符
     */
    public static String halfString(String str) {
        int length = str.length();
        if (length % 2 == 0) {
            str = str.substring(0, length / 2);
        } else {
            str = str.substring(0, length / 2 - 1);
        }
        return str;
    }
}
