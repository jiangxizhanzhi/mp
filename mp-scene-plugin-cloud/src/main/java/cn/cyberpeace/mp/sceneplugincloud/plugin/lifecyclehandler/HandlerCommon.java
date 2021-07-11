package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.NetworkUtils;
import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudmessageconvert.CloudMessageConvertUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class HandlerCommon extends PluginSceneEventHandlerCommon<CloudScenePluginHandler> {

    @Getter
    private HandlerUtils handlerUtils;

    public HandlerCommon(CloudScenePluginHandler pluginHandler, TaskPool taskPool, int event) {
        super(pluginHandler, taskPool, event);
        handlerUtils = pluginHandler.getHandlerUtils();
    }

    
    public void checkAccessModes(CloudPluginSceneElementBO cloudElement) {
        var cloudPorts = getPluginHandler().getPorts(cloudElement);
        
        Map<String, List<CloudAccessMode>> ipAccessModesMap = new HashMap<>();
        cloudPorts.forEach(cloudPort -> {
            if (Boolean.TRUE.equals(cloudPort.getSrcPort().getIsAuxiliary())) {
                return;
            }
            var accessModes = cloudPort.getAccessModes();
            if (accessModes == null) {
                return;
            }
            accessModes.forEach(accessMode -> {
                var protocol = accessMode.getProtocol();
                
                if (AccessMode.Protocol.CONSOLE.equals(protocol)) {
                    accessMode.setAvailable(true);
                    return;
                }
                if (AccessMode.Protocol.COM.equals(protocol)) {
                    accessMode.setAvailable(true);
                    return;
                }
                if (!Boolean.TRUE.equals(accessMode.getProxy())) {
                    accessMode.setAvailable(true);
                    return;
                }

                var ip = accessMode.getAccessIp();
                if (StringUtils.isEmpty(ip)) {
                    return;
                }

                var port = accessMode.getAccessPort();
                var portStr = StringUtils.EMPTY;
                if (port != null && port.compareTo(0) > 0) {
                    portStr = port.toString();
                }
                var ipPortKey = String.format("%s:%s", ip, portStr);
                var ipAccessModes = ipAccessModesMap.getOrDefault(ipPortKey, new ArrayList<>());
                ipAccessModes.add(accessMode);
                ipAccessModesMap.put(ipPortKey, ipAccessModes);
            });
        });

        
        var nodeId = cloudElement.getSrcElement().getId();
        ipAccessModesMap.forEach((ipPortKey, accessModes) -> {
            var ip = accessModes.get(0).getAccessIp();
            var port = accessModes.get(0).getAccessPort();
            var protocol = accessModes.get(0).getProtocol();
            Runnable checkSuccessCallback = () -> {
                accessModes.forEach(accessMode -> accessMode.setAvailable(true));
                getPluginHandler().asyncAckElementChanged(nodeId, true);
                if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
                    getPluginHandler().getSceneHandler().updateSceneElementWithPlugins(nodeId);
                }
            };

            if (port == null || port.compareTo(0) <= 0) {
                TaskCommon.executeTask(() -> NetworkUtils.probeHost(ip, checkSuccessCallback, checkSuccessCallback));
            } else {
                TaskCommon.executeTask(
                        () -> NetworkUtils.probePort(protocol, ip, port, checkSuccessCallback, checkSuccessCallback));
            }
        });
    }

    
    public void closeAccessModes(CloudPluginSceneElementBO cloudElement) {
        var cloudPorts = getPluginHandler().getPorts(cloudElement);
        var closeFlag = false;
        for (var cloudPort : cloudPorts) {
            var accessModes = cloudPort.getAccessModes();
            if (accessModes == null) {
                return;
            }
            for (var accessMode : accessModes) {
                accessMode.setAvailable(false);
                closeFlag = true;
            }
        }

        if (closeFlag) {
            var nodeId = cloudElement.getSrcElement().getId();
            try {
                getPluginHandler().asyncAckElementChanged(nodeId, true);
                if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
                    getPluginHandler().getSceneHandler().updateSceneElementWithPlugins(nodeId);
                }
            } catch (Exception e) {
                log.error("Close access mode ack element changed error: ", e);
            }
        }
    }

    @Override
    protected <E extends BasePluginSceneElementBO> MpRuntimeException handlerSpecialException(E element, Exception e) {
        if (element == null || e == null || StringUtils.isEmpty(e.getMessage())) {
            return null;
        }
        if (!(e instanceof MpRuntimeException)) {
            return null;
        }
        MpRuntimeException mpe = (MpRuntimeException) e;
        String message = mpe.getMessage().toLowerCase();
        log.error("element {} real error info {}", element.getSrcElement().getEid(), message);

        String nodeName = element.getSrcElement().getName();
        CloudPluginSceneElementBO cloudElement = (CloudPluginSceneElementBO) element;
        String ip = "";
        if (CollectionUtils.isNotEmpty(cloudElement.getSrcElement().getPorts())) {
            ip = cloudElement.getSrcElement().getPorts().get(0).getIp();
        }
        String[] params = new String[]{nodeName, cloudElement.getImage(), ip};
        String convertMessage = CloudMessageConvertUtil.getMessage(message, params);
        if (convertMessage != null) {
            MpRuntimeException resultException = new MpRuntimeException(convertMessage);
            return resultException;
        } else {
            return null;
        }

















    }
}
