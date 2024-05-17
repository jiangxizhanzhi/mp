package cn.cyberpeace.mp.sceneplugincloud.service.common;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudElementAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConnectionModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GetConnectionUrlParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GetConnectionUrlResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GuacamoleCreateConnectionData;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConnectionMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class CloudSceneConnectionCommon {

    private static CloudPluginSceneConnectionMapper cloudPluginSceneConnectionMapper;

    @Autowired
    private CloudSceneConnectionCommon(CloudPluginSceneConnectionMapper cloudPluginSceneConnectionMapper) {
        CloudSceneConnectionCommon.cloudPluginSceneConnectionMapper = cloudPluginSceneConnectionMapper;
    }

    @Data
    public static class SdnConnectionParam {
        private String userId;
        private String sdnIp;
        private String protocol;
        private Integer port;
        private String username;
        private String password;
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

    public static GetConnectionUrlResult getConnectionUrl(GetConnectionUrlParam param, HttpServletResponse response) {
        var remoteClient = new Remote();
        if (CloudElementAccessMode.Protocol.VNC.equals(param.getConsoleType())) {
            param.setConsoleType(CloudElementAccessMode.Protocol.NO_VNC);
            return remoteClient.getNoVncConnectionUrl(param);
        } else if (CloudElementAccessMode.Protocol.CONSOLE.equals(param.getConsoleType())) {
            return remoteClient.getContainerConnectionUrl(param);
        } else if (CloudElementAccessMode.Protocol.SPICE.equals(param.getConsoleType())) {
            remoteClient.getConnectionFile(param, response);
        } else {
            return remoteClient.getNoVncConnectionUrl(param);
        }
        return new GetConnectionUrlResult();
    }

    public static CloudPluginSceneConnectionModel getOrCreateSceneConnection(SceneConnectionParam param) {
        var sceneId = param.getSceneId();
        var sceneElementId = param.getSceneElementId();
        var userId = param.getUserId();

        CloudPluginSceneConnectionModel sceneConnection = cloudPluginSceneConnectionMapper.first(new HumanCondition()
                .filterEqual(CloudPluginSceneConnectionModel::getScene, sceneId)
                .filterEqual(CloudPluginSceneConnectionModel::getSceneElement, sceneElementId)
                .filterEqual(CloudPluginSceneConnectionModel::getAccessUser, userId)
                .filterEqual(CloudPluginSceneConnectionModel::getDisable, false)
                .reverseOrderBy(CloudPluginSceneConnectionModel::getCreateTime));

        // com信息可能会变，获取最新的com信息
        var resourceId = param.getResourceId();
        var vmClient = new VirtualMachine();
        var comInfo = vmClient.getComInfo(resourceId);
        if (sceneConnection == null || !sceneConnection.getAccessIp().equals(comInfo.getAddress())) {
            sceneConnection = new CloudPluginSceneConnectionModel();
            sceneConnection.setScene(sceneId);
            sceneConnection.setSceneElement(sceneElementId);
            sceneConnection.setAccessUser(userId);
            sceneConnection.setAccessIp(comInfo.getAddress());
            sceneConnection.setAccessPort(Integer.parseInt(comInfo.getPort()));
        }
        var dataUpdateFlag = false;
        var remoteClient = new Remote();
        if (StringUtils.isEmpty(sceneConnection.getConnectionId())) {
            var guacamoleCreateConnectionData = new GuacamoleCreateConnectionData();
            guacamoleCreateConnectionData.setUsername(userId);
            guacamoleCreateConnectionData.setVmId(resourceId);
            GuacamoleCreateConnectionData.ConnectionParams params = new GuacamoleCreateConnectionData.ConnectionParams();
            params.setUsername(comInfo.getUser());
            params.setHostname(comInfo.getAddress());
            params.setPort(comInfo.getPort());
            params.setNeedPrivateKey(true);
            params.setCommand("connect --vm-id=" + resourceId);
            guacamoleCreateConnectionData.setConnectionParams(params);
            var result = remoteClient.createConnection(guacamoleCreateConnectionData);
            if (result == null || StringUtils.isEmpty(result.getConnectionId())) {
                log.error("Create scene connection[{}] with empty result: {}", guacamoleCreateConnectionData, result);
                return null;
            } else {
                sceneConnection.setConnectionId(result.getConnectionId());
                sceneConnection.setConnectionUrl(result.getUrl());
                sceneConnection.setConnectionRecordingName(result.getConnectionName());
                dataUpdateFlag = true;
            }
        }

        if (StringUtils.isEmpty(sceneConnection.getConnectionUrl())) {
            log.error("Get scene connection[{}] url with empty result: {}", sceneConnection.getConnectionId(), JsonCommon.stringify(sceneConnection));
        }

        if (dataUpdateFlag) {
            cloudPluginSceneConnectionMapper.save(sceneConnection);
        }

        return sceneConnection;
    }


    public static CloudPluginSceneConnectionModel getOrCreateSdnConnection(SdnConnectionParam param) {
        CloudPluginSceneConnectionModel sdnConnection = cloudPluginSceneConnectionMapper.first(new HumanCondition()
                .filterEqual(CloudPluginSceneConnectionModel::getAccessIp, param.getSdnIp())
                .filterEqual(CloudPluginSceneConnectionModel::getProtocol, param.getProtocol())
                .filterEqual(CloudPluginSceneConnectionModel::getPort, param.getPort())
                .filterEqual(CloudPluginSceneConnectionModel::getAccessUser, param.getUserId())
                .filterEqual(CloudPluginSceneConnectionModel::getUsername, param.getUsername())
                .filterEqual(CloudPluginSceneConnectionModel::getPassword, param.getPassword())
                .filterEqual(CloudPluginSceneConnectionModel::getDisable, false)
                .reverseOrderBy(CloudPluginSceneConnectionModel::getPk));
        if (sdnConnection != null) {
            return sdnConnection;
        }

        sdnConnection = new CloudPluginSceneConnectionModel();
        sdnConnection.setAccessIp(param.getSdnIp());
        sdnConnection.setProtocol(param.getProtocol());
        sdnConnection.setPort(param.getPort());
        sdnConnection.setAccessUser(param.getUserId());
        sdnConnection.setUsername(param.getUsername());
        sdnConnection.setPassword(param.getPassword());

        var remoteClient = new Remote();
        var guacamoleCreateConnectionData = new GuacamoleCreateConnectionData();
        guacamoleCreateConnectionData.setUsername(param.getUserId());
        GuacamoleCreateConnectionData.ConnectionParams params = new GuacamoleCreateConnectionData.ConnectionParams();
        params.setProtocol(param.getProtocol());
        params.setHostname(param.getSdnIp());
        params.setPort(param.getPort().toString());
        params.setUsername(param.getUsername());
        params.setPassword(param.getPassword());
        guacamoleCreateConnectionData.setConnectionParams(params);
        var result = remoteClient.createConnection(guacamoleCreateConnectionData);
        if (result == null || StringUtils.isEmpty(result.getConnectionId())) {
            log.error("Create sdn connection[{}] with empty result: {}", guacamoleCreateConnectionData, result);
            return null;
        } else {
            sdnConnection.setConnectionId(result.getConnectionId());
            sdnConnection.setConnectionUrl(result.getUrl());
            sdnConnection.setConnectionRecordingName(result.getConnectionName());
        }
        if (StringUtils.isEmpty(sdnConnection.getConnectionUrl())) {
            log.error("Get sdn connection[{}] url with empty result: {}", sdnConnection.getConnectionId(), JsonCommon.stringify(sdnConnection));
        }

        cloudPluginSceneConnectionMapper.save(sdnConnection);
        return sdnConnection;
    }

    public static boolean enableRecording(CloudPluginSceneConnectionModel sceneConnection,
                                          SceneConnectionEnableRecordingExtraParam extraParam) {
        String connectionId = sceneConnection.getConnectionId();
        if (StringUtils.isEmpty(connectionId)) {
            return false;
        }

        Remote remoteClient = new Remote();
        String result = remoteClient.enableRecording(connectionId, extraParam.getSceneName(), extraParam.getSceneId(),
                extraParam.getNodeName(), extraParam.getNodeId(), extraParam.getUsername(), extraParam.getDepartment());
        boolean flag = !StringUtils.isEmpty(result);
        if (flag) {
            sceneConnection.setConnectionEnableRecording(true);
            sceneConnection.setConnectionRecordingName(result);
            cloudPluginSceneConnectionMapper.save(sceneConnection);
        }

        return flag;
    }

    public static boolean disableRecording(CloudPluginSceneConnectionModel sceneConnection) {
        String connectionId = sceneConnection.getConnectionId();
        if (StringUtils.isEmpty(connectionId)) {
            return false;
        }

        Remote remoteClient = new Remote();
        String result = remoteClient.disableRecording(connectionId);
        boolean flag = !StringUtils.isEmpty(result);
        if (flag) {
            sceneConnection.setConnectionEnableRecording(false);
            sceneConnection.setConnectionRecordingName(result);
            cloudPluginSceneConnectionMapper.save(sceneConnection);
        }

        return flag;
    }

    public static boolean convertRecording(CloudPluginSceneConnectionModel sceneConnection) {
        String connectionId = sceneConnection.getConnectionId();
        if (StringUtils.isEmpty(connectionId)) {
            return false;
        }

        Remote remoteClient = new Remote();
        String result = remoteClient.convertRecording(connectionId);
        boolean flag = !StringUtils.isEmpty(result);
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

}
