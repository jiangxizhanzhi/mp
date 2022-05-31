package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.NetworkUtils;
import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 检查接入方式可用性
     */
    public void checkAccessModes(CloudPluginSceneElementBO cloudElement) {
        var cloudPorts = getPluginHandler().getPorts(cloudElement);
        // 同样ip、端口可能对应多个接入方式
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
            var protocol = accessModes.get(0).getProtocol();
            Runnable checkSuccessCallback = () -> {
                accessModes.forEach(accessMode -> accessMode.setAvailable(true));
                getPluginHandler().asyncAckElementChanged(nodeId, true);
            };

            if (port == null || port.compareTo(0) <= 0) {
                TaskCommon.executeTask(() -> NetworkUtils.probeHost(ip, checkSuccessCallback, checkSuccessCallback));
            } else {
                TaskCommon.executeTask(
                        () -> NetworkUtils.probePort(protocol, ip, port, checkSuccessCallback, checkSuccessCallback));
            }
        });
    }

    /**
     * 关闭接入方式可用性
     */
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
        String message = mpe.getMessage();
        String nodeName = element.getSrcElement().getName();
        CloudPluginSceneElementBO cloudElement = (CloudPluginSceneElementBO) element;
        if (message.contains("Instance") && message.contains("could not be found")) {
            // 实例不存在
            log.error("element {} real error info {}", element.getSrcElement().getEid(), message);
            MpRuntimeException resultException = new MpRuntimeException(MessageCommon.get("sceneplugincloud.error.instance_not_found", nodeName));
            return resultException;
        } else if (message.contains("object vm") && message.contains("status error") && message.contains("build of instance")) {
            log.error("element {} real error info {}", element.getSrcElement().getEid(), message);
            MpRuntimeException resultException = new MpRuntimeException(MessageCommon.get("sceneplugincloud.error.vm_create_error", nodeName));
            return resultException;
        } else if (message.equals("Parameters image or volumes must be supplied.")) {
            log.error("element {} real error info {}", element.getSrcElement().getEid(), message);
            MpRuntimeException resultException = new MpRuntimeException(MessageCommon.get("sceneplugincloud.error.image_not_found", nodeName, cloudElement.getImage()));
            return resultException;
        } else {
            return null;
        }
    }
}
