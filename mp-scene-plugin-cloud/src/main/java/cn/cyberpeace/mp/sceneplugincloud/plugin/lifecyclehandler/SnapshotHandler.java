package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneElementSnapshotBO;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.repository.SceneElementSnapshotMapper;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.DeviceSnapshotParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.DeviceSnapshotResultData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.ResumeTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.util.SnapshotAddressUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;


@Slf4j
public class SnapshotHandler extends HandlerCommon {

    private final VirtualMachine virtualMachine = new VirtualMachine();

    private final Remote remote = new Remote();

    public static class SceneProcess {
        public static final EventProcess SNAPSHOT_TERMINALS = new EventProcess("snapshot_terminals", "sceneplugincloud.event.snapshot_terminals");
    }

    public static class ElementProcess {
        public static final EventProcess SNAPSHOT_VIRTUAL_MACHINE = new EventProcess("snapshot_virtual_machine", "sceneplugincloud.event.snapshot_virtual_machine");
        public static final EventProcess SNAPSHOT_CONTAINER = new EventProcess("snapshot_container", "sceneplugincloud.event.snapshot_container");
        public static final EventProcess SNAPSHOT_DYNAMIPS = new EventProcess("snapshot_dynamips", "sceneplugincloud.event.snapshot_dynamips");
    }

    public SnapshotHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, ResumeTaskPool.getInstance(), event);
    }

    public void snapshotElement(CloudPluginSceneElementBO element, String snapshotId, String snapshotName, String description) {
        String nodeId = element.getSrcElement().getId();
        Integer virtualType = element.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", element.getSrcElement().getEid(),
                    element.getSrcElement().getId());
            return;
        }
        if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.SNAPSHOT, Arrays.asList(ElementProcess.SNAPSHOT_VIRTUAL_MACHINE));
            runElementProgress(nodeId, SceneElementModel.Event.SNAPSHOT, ElementProcess.SNAPSHOT_VIRTUAL_MACHINE, () -> {
                this.snapshot(element, snapshotId, snapshotName, description);
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.SNAPSHOT, Arrays.asList(ElementProcess.SNAPSHOT_CONTAINER));
            runElementProgress(nodeId, SceneElementModel.Event.SNAPSHOT, ElementProcess.SNAPSHOT_CONTAINER, () -> {
                this.snapshot(element, snapshotId, snapshotName, description);
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.SNAPSHOT, Arrays.asList(ElementProcess.SNAPSHOT_DYNAMIPS));
            runElementProgress(nodeId, SceneElementModel.Event.SNAPSHOT, ElementProcess.SNAPSHOT_DYNAMIPS, () -> {
                this.snapshot(element, snapshotId, snapshotName, description);
            });
        }
    }

    private void snapshot(CloudPluginSceneElementBO element, String snapshotId, String snapshotName, String description) {
        
        Map<String, Object> extInfo = Collections.singletonMap("sceneId", getPluginHandler().getSourceSceneId());
        var result = virtualMachine.snapshot(element.getResourceId(), element.getImage(),
                element.getImageUuid(), snapshotName, description, extInfo);
        SceneElementSnapshotBO snapshotBO = new SceneElementSnapshotBO();
        snapshotBO.setId(snapshotId);
        snapshotBO.setName(snapshotName);
        snapshotBO.setSourceName(SnapshotAddressUtils.parseImageName(element.getImage(), element.getImageUuid()));
        snapshotBO.setAddress(SnapshotAddressUtils.buildAddress(result.getImageName(), result.getUuid()));
        snapshotBO.setDescription(description);
        element.setImage(result.getImageName());
        getPluginHandler().updateElements(element.getSrcElement().getId());
        var model = snapshotBO.toModel();
        model.setScene(getPluginHandler().getSourceSceneId());
        model.setSceneElement(element.getSrcElement().getId());
        var snapshotMapper = BeanCommon.getBean(SceneElementSnapshotMapper.class);
        snapshotMapper.create(model);
        this.setSnapshotNum(element);
    }

    private void setSnapshotNum(CloudPluginSceneElementBO element) {
        if (StringUtils.isNotEmpty(element.getImageUuid())) {
            try {
                DeviceSnapshotParam param = new DeviceSnapshotParam();
                param.setDeviceUuid(element.getImageUuid());
                var result = remote.getHttpClient().get(CloudUrlConstant.DEVICE_SNAPSHOT, param, DeviceSnapshotResultData.class);
                if (result.getItems() != null) {
                    element.setImageSnapshotNum(result.getItems().size());
                }
            } catch (Exception e) {
                log.error("failed to get image snapshot num , image uuid is {}", element.getImageUuid(), e);
            }
        }
    }
}
