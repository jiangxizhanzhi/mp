package cn.cyberpeace.mp.sceneplugincloud.service.common;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConnectionModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GuacamoleCreateConnectionData;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConnectionMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        private String sceneElementPortId;
        private CloudAccessMode cloudAccessMode;
        private String userId;

//        private String username;
        private String sceneName;
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
            return null;
        }
        if (StringUtils.isEmpty(cloudAccessMode.getAccessIp())) {
            return null;
        }

        CloudPluginSceneConnectionModel sceneConnection = cloudPluginSceneConnectionMapper.first(new HumanCondition()
                .filterEqual(CloudPluginSceneConnectionModel::getScene, sceneId)
                .filterEqual(CloudPluginSceneConnectionModel::getSceneElement, sceneElementId)
                .filterEqual(CloudPluginSceneConnectionModel::getSceneElementPort, sceneElementPortId)
                .filterEqual(CloudPluginSceneConnectionModel::getAccessModeId, accessModeId)
                .filterEqual(CloudPluginSceneConnectionModel::getAccessUser, userId));
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
            sceneConnection.setAccessIp(cloudAccessMode.getAccessIp());
            sceneConnection.setAccessPort(cloudAccessMode.getAccessPort());
        }

        var dataUpdateFlag = false;
        var remoteClient = new Remote();
        if (StringUtils.isEmpty(sceneConnection.getConnectionId())) {
            var guacamoleCreateConnectionData = new GuacamoleCreateConnectionData();
            guacamoleCreateConnectionData.setScene(param.getSceneName());
            guacamoleCreateConnectionData.setUsername(userId);
            guacamoleCreateConnectionData.setDepartment(param.getDepartment());
            guacamoleCreateConnectionData.setFlag(true);
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
        if (StringUtils.isEmpty(sceneConnection.getConnectionUrl())) {
            var result = remoteClient.getConnectionUrl(sceneConnection.getConnectionId(), userId);
            if (result == null || StringUtils.isEmpty(result.getUrl())) {
                log.error("Get scene connection[{}] url with empty result: {}", sceneConnection.getConnectionId(), result);
            } else {
                sceneConnection.setConnectionUrl(result.getUrl());
                dataUpdateFlag = true;
            }
        }

        if (dataUpdateFlag) {
            cloudPluginSceneConnectionMapper.save(sceneConnection);
        }

        return sceneConnection;
    }
}
