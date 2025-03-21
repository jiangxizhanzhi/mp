 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SnapshotHandler.java.
 // File size 13180.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneElementSnapshotBO;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.core.event.base.ProcessStatus;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneElementSnapshotModel;
import cn.cyberpeace.mp.scene.repository.SceneElementSnapshotMapper;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerSnapshotData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.SnapshotResultData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.ResumeTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.util.SnapshotAddressUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName SnapshotHandler
 * @Description
 * @Author maojun
 * @Date 2021/10/28
 * @Version 1.0.0
 * @see HandlerCommon
 */
@Slf4j
public class SnapshotHandler extends HandlerCommon {
    private static final String NO_VIRTUAL_TYPE_LOG = "Element({}) {} has no virtual type";

    private final VirtualMachine virtualMachine = new VirtualMachine();
    private final Container container = new Container();

    private final SceneElementSnapshotMapper snapshotMapper = BeanCommon.getBean(SceneElementSnapshotMapper.class);

    public static class SceneProcess {
        public static final EventProcess SNAPSHOT_TERMINALS = new EventProcess("snapshot_terminals", "sceneplugincloud.event.snapshot_terminals");
    }

    public static class ElementProcess {
        public static final EventProcess SNAPSHOT_VIRTUAL_MACHINE = new EventProcess("snapshot_virtual_machine", "sceneplugincloud.event.snapshot_virtual_machine");
        public static final EventProcess SNAPSHOT_CONTAINER = new EventProcess("snapshot_container", "sceneplugincloud.event.snapshot_container");
    }

    /**
     * SnapshotHandler
     *
     * @param pluginHandler
     * @param event
     */
    public SnapshotHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, ResumeTaskPool.getInstance(), event);
    }

    public void snapshot() {
        for (CloudPluginSceneElementBO element : getPluginHandler().getElementMap().values()) {
            String nodeId = element.getSrcElement().getId();
            if (element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER) {
                // 容器快照
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.SNAPSHOT, Arrays.asList(ElementProcess.SNAPSHOT_CONTAINER));
                runElementProgress(nodeId, SceneElementModel.Event.SNAPSHOT, ElementProcess.SNAPSHOT_CONTAINER, () -> {
                    var containerSnapshotData = new ContainerSnapshotData();
                    containerSnapshotData.setName("");
                    var res = new Container().snapshot(element.getResourceId(), containerSnapshotData);
                    element.setSnapshotResourceId(res);
                });
            } else if (element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                // 虚拟机快照
                getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.SNAPSHOT, Arrays.asList(ElementProcess.SNAPSHOT_VIRTUAL_MACHINE));
                runElementProgress(nodeId, SceneElementModel.Event.SNAPSHOT, ElementProcess.SNAPSHOT_VIRTUAL_MACHINE, () -> {
                    var res = new VirtualMachine().snapshot(element.getResourceId(), "", "", "", "", new HashMap<>());
                    element.setSnapshotResourceId(res.getId());
                });
            }
        }
    }

    /**
     * snapshotElement
     *
     * @param element
     * @param snapshotId
     * @param snapshotName
     * @param description
     */
    public void snapshotElement(CloudPluginSceneElementBO element, String snapshotId, String snapshotName, String description) {
        String nodeId = element.getSrcElement().getId();
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn(NO_VIRTUAL_TYPE_LOG, element.getSrcElement().getEid(),
                    element.getSrcElement().getId());
            return;
        }
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.SNAPSHOT, Arrays.asList(ElementProcess.SNAPSHOT_VIRTUAL_MACHINE));
            runElementProgress(nodeId, SceneElementModel.Event.SNAPSHOT, ElementProcess.SNAPSHOT_VIRTUAL_MACHINE, () -> {
                this.virtualMachineSnapshot(element, snapshotId, snapshotName, description);
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.SNAPSHOT, Arrays.asList(ElementProcess.SNAPSHOT_CONTAINER));
            runElementProgress(nodeId, SceneElementModel.Event.SNAPSHOT, ElementProcess.SNAPSHOT_CONTAINER, () -> {
                this.containerSnapshot(element, snapshotName, description);
            });
        }
    }

    /**
     * snapshotElementV2
     *
     * @param element
     * @param snapshotId
     * @param snapshotName
     * @param description
     */
    public void snapshotElementV2(CloudPluginSceneElementBO element, String snapshotId, String snapshotName, String description) {
        String nodeId = element.getSrcElement().getId();
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(), element.getSrcElement()
                    .getId());
            return;
        }
        try {
            element.getSrcElement().setProcess(SceneProcess.SNAPSHOT_TERMINALS.getProcess());
            if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                this.snapshot(element, snapshotId, snapshotName, description);
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                this.snapshot(element, snapshotId, snapshotName, description);
            } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS)) {
                this.snapshot(element, snapshotId, snapshotName, description);
            }
            element.getSrcElement().setProcessStatus(ProcessStatus.OVER);
        } catch (Exception e) {
            element.getSrcElement().setProcessStatus(ProcessStatus.ABNORMAL);
            element.getSrcElement().setProcessMessage(e.getMessage());
            throw e;
        }
    }

    /**
     * deleteElementSnapshot
     *
     * @param element
     * @param snapshotId
     */
    public void deleteElementSnapshot(CloudPluginSceneElementBO element, String snapshotId) {
        String nodeId = element.getSrcElement().getId();
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn(NO_VIRTUAL_TYPE_LOG, element.getSrcElement().getEid(),
                    element.getSrcElement().getId());
            return;
        }
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.SNAPSHOT, Arrays.asList(ElementProcess.SNAPSHOT_VIRTUAL_MACHINE));
            runElementProgress(nodeId, SceneElementModel.Event.SNAPSHOT, ElementProcess.SNAPSHOT_VIRTUAL_MACHINE, () -> {
                virtualMachine.deleteSnapshot(element.getResourceId(), snapshotId);
                snapshotMapper.delete(snapshotId);
                super.setSnapshotNum(element);
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.SNAPSHOT, Arrays.asList(ElementProcess.SNAPSHOT_CONTAINER));
            runElementProgress(nodeId, SceneElementModel.Event.SNAPSHOT, ElementProcess.SNAPSHOT_CONTAINER, () -> {
                container.deleteSnapshot(element.getResourceId(), snapshotId);
                snapshotMapper.delete(snapshotId);
                super.setSnapshotNum(element);
            });
        }
    }

    /**
     * elementSnapshotAction
     *
     * @param element
     * @param snapshotId
     * @param action
     */
    public void elementSnapshotAction(CloudPluginSceneElementBO element, String snapshotId, String action) {
        String nodeId = element.getSrcElement().getId();
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn(NO_VIRTUAL_TYPE_LOG, element.getSrcElement().getEid(),
                    element.getSrcElement().getId());
            return;
        }
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.SNAPSHOT, Arrays.asList(ElementProcess.SNAPSHOT_VIRTUAL_MACHINE));
            runElementProgress(nodeId, SceneElementModel.Event.SNAPSHOT, ElementProcess.SNAPSHOT_VIRTUAL_MACHINE, () -> {
                virtualMachine.snapshotAction(element.getResourceId(), snapshotId, action);
                super.setSnapshotNum(element);
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.SNAPSHOT, Arrays.asList(ElementProcess.SNAPSHOT_CONTAINER));
            runElementProgress(nodeId, SceneElementModel.Event.SNAPSHOT, ElementProcess.SNAPSHOT_CONTAINER, () -> {
                var result = container.snapshotAction(element.getResourceId(), snapshotId, action);
                element.setResourceId(result.getContainerId());
                getPluginHandler().updateElements(element.getSrcElement().getId());
                super.setSnapshotNum(element);
            });
        }
    }

    /**
     * virtualMachineSnapshot
     *
     * @param element
     * @param snapshotId
     * @param snapshotName
     * @param description
     */
    private void virtualMachineSnapshot(CloudPluginSceneElementBO element, String snapshotId, String snapshotName, String description) {
        // 调用云平台接口 snapshot
        Map<String, Object> extInfo = Collections.singletonMap("sceneId", getPluginHandler().getSourceSceneId());
        var result = virtualMachine.snapshot(element.getResourceId(), element.getImage(),
                element.getImageUuid(), snapshotName, description, extInfo);
        SceneElementSnapshotBO snapshotBO = new SceneElementSnapshotBO();
        snapshotBO.setId(result.getId());
        snapshotBO.setName(snapshotName);
        snapshotBO.setSourceName(element.getImage());
//        snapshotBO.setAddress(SnapshotAddressUtils.buildAddress(result.getImageName(), result.getUuid()));
        snapshotBO.setDescription(description);
//        element.setImage(result.getImageName());
//        getPluginHandler().updateElements(element.getSrcElement().getId());
        var model = snapshotBO.toModel();
        model.setScene(getPluginHandler().getSourceSceneId());
        model.setSceneElement(element.getSrcElement().getId());
        snapshotMapper.create(model);
        this.setSnapshotNum(element);
    }

    /**
     * containerSnapshot
     *
     * @param element
     * @param snapshotName
     * @param description
     */
    private void containerSnapshot(CloudPluginSceneElementBO element, String snapshotName, String description) {
        ContainerSnapshotData data = new ContainerSnapshotData();
        data.setName(snapshotName);
        var result = container.snapshot(element.getResourceId(), data);
        SceneElementSnapshotBO snapshotBO = new SceneElementSnapshotBO();
        snapshotBO.setId(result);
        snapshotBO.setName(snapshotName);
        snapshotBO.setSourceName(element.getImage());
        snapshotBO.setDescription(description);
        var model = snapshotBO.toModel();
        model.setScene(getPluginHandler().getSourceSceneId());
        model.setSceneElement(element.getSrcElement().getId());
        snapshotMapper.create(model);
        this.setSnapshotNum(element);
    }


    /**
     * snapshot
     *
     * @param element
     * @param snapshotId
     * @param snapshotName
     * @param description
     */
    private void snapshot(CloudPluginSceneElementBO element, String snapshotId, String snapshotName, String description) {
        // 调用云平台接口 snapshot
        Map<String, Object> extInfo = Collections.singletonMap("sceneId", getPluginHandler().getSourceSceneId());
        SnapshotResultData result = virtualMachine.snapshot(element.getResourceId(), element.getImage(),
                element.getImageUuid(), snapshotName, description, extInfo);
        SceneElementSnapshotBO snapshotBO = new SceneElementSnapshotBO();
        snapshotBO.setId(snapshotId);
        snapshotBO.setName(snapshotName);
        snapshotBO.setSourceName(SnapshotAddressUtils.parseImageName(element.getImage(), element.getImageUuid()));
        snapshotBO.setAddress(SnapshotAddressUtils.buildAddress(result.getImageName(), result.getUuid()));
        snapshotBO.setDescription(description);
        element.setImage(result.getImageName());
        getPluginHandler().updateElements(element.getSrcElement().getId());
        SceneElementSnapshotModel model = snapshotBO.toModel();
        model.setScene(getPluginHandler().getSourceSceneId());
        model.setSceneElement(element.getSrcElement().getId());
        SceneElementSnapshotMapper snapshotMapper = BeanCommon.getBean(SceneElementSnapshotMapper.class);
        snapshotMapper.create(model);
        this.setSnapshotNum(element);
    }
}

