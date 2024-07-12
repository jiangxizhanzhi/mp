 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudSceneConnectionCommon.java.
 // File size 13813.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
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

/**
 * @ClassName CloudSceneConnectionCommon
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Component
public class CloudSceneConnectionCommon {

    private static CloudPluginSceneConnectionMapper cloudPluginSceneConnectionMapper;

    /**
     * CloudSceneConnectionCommon
     *
     * @param cloudPluginSceneConnectionMapper
     */
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

    /**
     * 文件名：Example.java
     * 版本信息：v1.0
     * 日期：2023-04-01
     * 描述：
     * 这是一个示例Java文件，展示了开头注释的标准格式。
     * <p>
     * 版权信息：
     * © 2023 你的名字或公司
     * <p>
     * 修改历史：
     * 2023-04-01 初始版本
     */
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

    /**
     * getConnectionUrl
     *
     * @param param
     * @param response
     * @return
     */
    public static GetConnectionUrlResult getConnectionUrl(GetConnectionUrlParam param, HttpServletResponse response) {
        var remoteClient = new Remote();
        if (CloudElementAccessMode.Protocol.VNC.equals(param.getConsoleType())) {
            param.setConsoleType(CloudElementAccessMode.Protocol.NO_VNC);
            return remoteClient.getNoVncConnectionUrl(param);
        } else if (CloudElementAccessMode.Protocol.CONSOLE.equals(param.getConsoleType())) {
            return remoteClient.getContainerConnectionUrl(param);
        } else if (CloudElementAccessMode.Protocol.SPICE.equals(param.getConsoleType())) {
            return remoteClient.getConnectionFile(param, response);
        } else {
            return remoteClient.getNoVncConnectionUrl(param);
        }
    }

    /**
     * getOrCreateSceneConnection
     *
     * @param param
     * @return
     */
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


    /**
     * getOrCreateSdnConnection
     *
     * @param param
     * @return
     */
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

    /**
     * enableRecording
     *
     * @param sceneConnection
     * @param extraParam
     * @return boolean
     */
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

    /**
     * disableRecording
     *
     * @param sceneConnection
     * @return boolean
     */
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

    /**
     * convertRecording
     *
     * @param sceneConnection
     * @return boolean
     */
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

    /**
     * getContainerConsoleUrl
     *
     * @param resourceId
     * @return
     */
    public static String getContainerConsoleUrl(String resourceId) {
        String url = new Remote().getContainerConsoleUrl(resourceId);
        if (StringUtils.isEmpty(url)) {
            log.error("failed to get container console url , containId is {}", resourceId);
        }
        return url;
    }

}
