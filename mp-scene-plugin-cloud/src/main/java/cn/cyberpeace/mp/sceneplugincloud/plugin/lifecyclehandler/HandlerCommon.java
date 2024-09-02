 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HandlerCommon.java.
 // File size 12953.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.NetworkUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementSnapshotBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneElementSnapshotModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.scene.repository.SceneElementPluginMapper;
import cn.cyberpeace.mp.scene.repository.SceneElementSnapshotMapper;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudElementAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.DeviceSnapshotParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.DeviceSnapshotResultData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GetConnectionUrlParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.SnapshotResultData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.SnapshotAddressUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudmessageconvert.CloudMessageConvertUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


/**
 * @ClassName HandlerCommon
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see PluginSceneEventHandlerCommon
 */
@Slf4j
public class HandlerCommon extends PluginSceneEventHandlerCommon<CloudScenePluginHandler> {

    @Getter
    private HandlerUtils handlerUtils;

    @Getter
    private SceneElementMapper sceneElementMapper;

    private final Remote remote = new Remote();
    private final VirtualMachine virtualMachine = new VirtualMachine();


    /**
     * HandlerCommon
     *
     * @param pluginHandler
     * @param taskPool
     * @param event
     */
    public HandlerCommon(CloudScenePluginHandler pluginHandler, TaskPool taskPool, int event) {
        super(pluginHandler, taskPool, event);
        handlerUtils = pluginHandler.getHandlerUtils();
        sceneElementMapper = BeanCommon.getBean(SceneElementMapper.class);
    }

    /**
     * checkAccessModes
     *
     * @param cloudElement
     */
    public void checkAccessModes(CloudPluginSceneElementBO cloudElement) {
        List<CloudPluginSceneElementPortBO> cloudPorts = getPluginHandler().getPorts(cloudElement);
        if (cloudPorts == null || cloudPorts.size() == 0) {
            return;
        }
        Set<String> supportProtocols = new HashSet<>();
        supportProtocols.add(CloudElementAccessMode.Protocol.VNC);
        supportProtocols.add(CloudElementAccessMode.Protocol.SPICE);
        supportProtocols.add(CloudElementAccessMode.Protocol.CONSOLE);

        boolean existsSupportProtocols = false;
        for (CloudPluginSceneElementPortBO port : cloudPorts) {
            if (port.getAccessModes() != null) {
                for (CloudAccessMode accessMode : port.getAccessModes()) {
                    String protocol = accessMode.getProtocol();
                    if (supportProtocols.contains(protocol)) {
                        existsSupportProtocols = true;
                    }
                }
            }
        }
        // 不存在 VNC/SPICE ，则给虚拟机自动加上 VNC和SPICE  ， 给容器自动加上console
        if (!existsSupportProtocols) {
            CloudPluginSceneElementPortBO cloudPort = cloudPorts.get(0);
            if (cloudPort.getAccessModes() == null) {
                cloudPort.setAccessModes(new ArrayList<>());
            }
            List<CloudAccessMode> cloudAccessModes = cloudPort.getAccessModes();
            String ip = cloudPort.getSrcPort().getIp();
            if (BaseCloudPluginSceneElementModel.ImageType.CONTAINER.equals(cloudElement.getImageType())) {
                CloudAccessMode cloudAccessMode = new CloudAccessMode();
                cloudAccessMode.setAccessIp(ip);
                cloudAccessMode.setAvailable(true);
                cloudAccessMode.setProtocol(CloudElementAccessMode.Protocol.CONSOLE);
                cloudAccessMode.setId(TextUtils.uuid());
                cloudAccessModes.add(cloudAccessMode);
            } else if (BaseCloudPluginSceneElementModel.ImageType.VM.equals(cloudElement.getImageType())) {
                CloudAccessMode spiceAccessMode = new CloudAccessMode();
                spiceAccessMode.setAccessIp(ip);
                spiceAccessMode.setConnectionUrl("");
                spiceAccessMode.setProtocol(CloudElementAccessMode.Protocol.SPICE);
                spiceAccessMode.setId(TextUtils.uuid());
                spiceAccessMode.setAvailable(true);
                cloudAccessModes.add(spiceAccessMode);

                CloudAccessMode vncAccessMode = new CloudAccessMode();
                vncAccessMode.setAccessIp(ip);
                vncAccessMode.setProtocol(CloudElementAccessMode.Protocol.VNC);
                vncAccessMode.setAvailable(true);
                vncAccessMode.setId(TextUtils.uuid());
                cloudAccessModes.add(vncAccessMode);
            }
        }

        // 给所有接入协议先设置联通 使页面显示正常
        for (CloudPluginSceneElementPortBO port : cloudPorts) {
            if (port.getAccessModes() != null) {
                for (CloudAccessMode accessMode : port.getAccessModes()) {
                    accessMode.setAvailable(true);
                }
            }
        }
    }

    /**
     * 关闭接入方式可用性
     *
     * @param cloudElement
     */
    public void closeAccessModes(CloudPluginSceneElementBO cloudElement) {
        List<CloudPluginSceneElementPortBO> cloudPorts = getPluginHandler().getPorts(cloudElement);
        boolean closeFlag = false;
        for (CloudPluginSceneElementPortBO cloudPort : cloudPorts) {
            List<CloudAccessMode> accessModes = cloudPort.getAccessModes();
            if (accessModes == null) {
                return;
            }
            for (CloudAccessMode accessMode : accessModes) {
                accessMode.setAvailable(false);
                closeFlag = true;
            }
        }

        if (closeFlag) {
            String nodeId = cloudElement.getSrcElement().getId();
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

    /**
     * handlerSpecialException
     *
     * @param element
     * @param e
     * @return 文件名：Example.java
     */
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

//        if (message.contains("instance") && message.contains("could not be found")) {
//            // 实例不存在
//            log.error("element {} real error info {}", element.getSrcElement().getEid(), message);
//            MpRuntimeException resultException = new MpRuntimeException(MessageCommon.get("sceneplugincloud.error.instance_not_found", nodeName));
//            return resultException;
//        } else if (message.contains("object vm") && message.contains("status error") && message.contains("build of instance")) {
//            log.error("element {} real error info {}", element.getSrcElement().getEid(), message);
//            MpRuntimeException resultException = new MpRuntimeException(MessageCommon.get("sceneplugincloud.error.vm_create_error", nodeName));
//            return resultException;
//        } else if (message.equals("parameters image or volumes must be supplied.")) {
//            log.error("element {} real error info {}", element.getSrcElement().getEid(), message);
//            MpRuntimeException resultException = new MpRuntimeException(MessageCommon.get("sceneplugincloud.error.image_not_found", nodeName, cloudElement.getImage()));
//            return resultException;
//        } else {
//            return null;
//        }
    }

    /**
     * checkSuccessCallback
     *
     * @param ipAccessModesMap
     * @param nodeId
     */
    private void checkSuccessCallback(Map<String, List<CloudAccessMode>> ipAccessModesMap, String nodeId) {
        ipAccessModesMap.forEach((ipPortKey, accessModes) -> {
            accessModes.forEach(accessMode -> accessMode.setAvailable(true));
            getPluginHandler().asyncAckElementChanged(nodeId, true);
            if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
                getPluginHandler().getSceneHandler().updateSceneElementWithPlugins(nodeId);
            }
        });
    }

    /**
     * checkTimeoutCallback
     *
     * @param ipAccessModesMap
     * @param nodeId
     */
    private void checkTimeoutCallback(Map<String, List<CloudAccessMode>> ipAccessModesMap, String nodeId) {
        ipAccessModesMap.forEach((ipPortKey, accessModes) -> {
            accessModes.forEach(accessMode -> accessMode.setAvailable(true));
            String sceneId = getPluginHandler().getSceneHandler().getSceneId();
            SceneHandler sceneHandler = new SceneHandler(sceneId, null, null);
            SceneElementBO dbElement = sceneHandler.getElement(nodeId);
            if (dbElement != null && dbElement.getEvent().equals(getPluginHandler().getSrcElement(nodeId).getEvent())) {
                log.info("check access mode timeout , update access info , sceneId {},nodeId {}", sceneId, nodeId);
                getPluginHandler().asyncAckElementChanged(nodeId, true);
                if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
                    getPluginHandler().getSceneHandler().updateSceneElementWithPlugins(nodeId);
                }
            } else {
                log.info("check access mode timeout , element event has been changed , do not update, sceneId {},nodeId {}", sceneId, nodeId);
            }
        });
    }

    /**
     * setSnapshotNum
     *
     * @param element
     */
    public void setSnapshotNum(CloudPluginSceneElementBO element) {
        if (StringUtils.isNotEmpty(element.getImageUuid())) {
            try {
                DeviceSnapshotParam param = new DeviceSnapshotParam();
                param.setOffset(1);
                param.setLimit(1);
                var result = remote.getHttpClient().get(CloudUrlConstant.VIRTUAL_MACHINE_SNAPSHOT_LIST
                        .replace("{vm_id}", element.getResourceId()), param, DeviceSnapshotResultData.class);
                if (result.getData() != null) {
                    element.setImageSnapshotNum(result.getData().getTotal());
                }
            } catch (Exception e) {
                log.error("failed to get image snapshot num , vm id is {}", element.getResourceId(), e);
            }
        }
    }

}
