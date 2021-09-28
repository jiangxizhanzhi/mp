package cn.cyberpeace.mp.sceneplugincloud.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeSessionBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScenePluginCloudSceneService {

    public ServiceResult<List<NodeSessionBO>> getSceneRemoteSessions(String sceneId, String[] nodeIds, boolean monitor,
                                                                     boolean assistance) {
        if (!monitor && !assistance) {
            return new ServiceResult<>(new ArrayList<>());
        }

        var filterNodeIds = nodeIds == null ? null : Arrays.asList(nodeIds);
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        Map<String, CloudPluginSceneElementBO> connectionIdElementMap = new HashMap<>();
        Map<String, CloudAccessMode> connectionIdAccessModeMap = new HashMap<>();
        pluginHandler.getElementMap().forEach((nodeId, cloudElement) -> {
            if (filterNodeIds != null && !filterNodeIds.isEmpty() && !filterNodeIds.contains(nodeId)) {
                return;
            }

            var srcElement = cloudElement.getSrcElement();
            var srcPorts = srcElement.getPorts();
            if (srcPorts == null) {
                return;
            }
            srcPorts.forEach(srcPort -> {
                var cloudPort = pluginHandler.getPort(srcPort.getId());
                if (cloudPort == null) {
                    return;
                }
                var accessModes = cloudPort.getAccessModes();
                if (accessModes == null) {
                    return;
                }
                accessModes.forEach(accessMode -> {
                    var connectionId = accessMode.getConnectionId();
                    if (StringUtils.isEmpty(connectionId)) {
                        return;
                    }
                    connectionIdElementMap.put(connectionId, cloudElement);
                    connectionIdAccessModeMap.put(connectionId, accessMode);
                });
            });
        });

        String[] connectionIds = connectionIdElementMap.keySet().toArray(new String[0]);
        if (connectionIds.length == 0) {
            return new ServiceResult<>(new ArrayList<>());
        }

        var remoteClient = new Remote();
        Map<String, NodeSessionBO> connectionIdSessionMap = new HashMap<>();
        if (monitor) {
            var monitors = remoteClient.getMonitors(connectionIds);
            if (monitors != null) {
                monitors.forEach((connectionId, monitorData) ->{
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
                assistances.forEach((connectionId, assistanceData) ->{
                    var nodeSession = connectionIdSessionMap.getOrDefault(connectionId,
                            new NodeSessionBO());
                    nodeSession.setAssistanceUrl(assistanceData.getAssistanceUrl());
                    connectionIdSessionMap.put(connectionId, nodeSession);
                });
            }
        }

        connectionIdSessionMap.forEach((connectionId, nodeSession) -> {
            var cloudElement = connectionIdElementMap.get(connectionId);
            var srcElement = cloudElement.getSrcElement();
            nodeSession.setNodeId(srcElement.getId());
            nodeSession.setNodeName(srcElement.getName());
            var accessMode = connectionIdAccessModeMap.get(connectionId);
            nodeSession.setProtocol(accessMode.getProtocol());
            nodeSession.setUser(accessMode.getConnectionUser());
        });

        return new ServiceResult<>(new ArrayList<>(connectionIdSessionMap.values()));
    }

}
