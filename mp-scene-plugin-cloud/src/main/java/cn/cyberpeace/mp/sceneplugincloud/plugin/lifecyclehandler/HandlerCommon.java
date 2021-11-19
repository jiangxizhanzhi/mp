package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.util.NetworkUtils;
import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
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
    private HandlerUtils<CloudScenePluginHandler> handlerUtils;

    public HandlerCommon(CloudScenePluginHandler pluginHandler, TaskPool taskPool, int event) {
        super(pluginHandler, taskPool, event);
        handlerUtils = pluginHandler.getHandlerUtils();
    }

    public String genResourceName(String name) {
        return StringUtils.joinWith("_", CloudSingleton.cloudAppConfig.getCpcloudResourceGroup(), name);
    }

    public String getRequestUser() {
        var context = getPluginHandler().getContext();
        if (context == null) {
            return null;
        }

        return context.getRequestUser();
    }

    /**
     * 检查接入方式可用性
     */
    public void checkAccessModes(CloudPluginSceneElementBO cloudElement) {
        var srcPorts = cloudElement.getSrcElement().getPorts();
        if (srcPorts == null) {
            return;
        }

        // 同样ip、端口可能对应多个接入方式
        Map<String, List<CloudAccessMode>> ipAccessModesMap = new HashMap<>();
        srcPorts.forEach(srcPort -> {
            var cloudPort = getPluginHandler().getPort(srcPort.getId());
            var accessModes = cloudPort.getAccessModes();
            if (accessModes == null) {
                return;
            }
            accessModes.forEach(accessMode -> {
                var protocol = accessMode.getProtocol();
                // console特殊模式可直接接入，不用检查
                if (AccessMode.Protocol.CONSOLE.equals(protocol)) {
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

        // 检查接入连通性
        var nodeId = cloudElement.getSrcElement().getId();
        ipAccessModesMap.forEach((ipPortKey, accessModes) -> {
            var ip = accessModes.get(0).getAccessIp();
            var port = accessModes.get(0).getAccessPort();
            Runnable checkSuccessCallback = () -> {
                accessModes.forEach(accessMode -> accessMode.setAvailable(true));
                getPluginHandler().ackElementChanged(nodeId, true);
            };

            if (port == null || port.compareTo(0) <= 0) {
                TaskCommon.executeTask(() -> NetworkUtils.probeHost(ip, checkSuccessCallback, checkSuccessCallback));
            } else {
                TaskCommon.executeTask(
                        () -> NetworkUtils.probePort(ip, port, checkSuccessCallback, checkSuccessCallback));
            }
        });
    }

    /**
     * 关闭接入方式可用性
     */
    public void closeAccessModes(CloudPluginSceneElementBO cloudElement) {
        var srcPorts = cloudElement.getSrcElement().getPorts();
        if (srcPorts == null) {
            return;
        }

        var closeFlag = false;
        for (var srcPort : srcPorts) {
            var cloudPort = getPluginHandler().getPort(srcPort.getId());
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
                getPluginHandler().ackElementChanged(nodeId, true);
            } catch (Exception e) {
                log.error("Close access mode ack element changed error: ", e);
            }
        }
    }

}
