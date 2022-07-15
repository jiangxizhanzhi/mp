package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneElementSnapshotBO;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.repository.SceneElementSnapshotMapper;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.ResumeTaskPool;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/28
 */
@Slf4j
public class SnapshotHandler extends HandlerCommon {

    private final VirtualMachine virtualMachine = new VirtualMachine();

    public static class SceneProcess {
        public static final EventProcess SNAPSHOT_TERMINALS = new EventProcess("snapshot_terminals", "sceneplugincloud.event.snapshot_terminals");
    }

    public static class ElementProcess {
        public static final EventProcess SNAPSHOT_VIRTUAL_MACHINE = new EventProcess("snapshot_virtual_machine", "sceneplugincloud.event.snapshot_virtual_machine");
        public static final EventProcess SNAPSHOT_CONTAINER = new EventProcess("snapshot_container", "sceneplugincloud.event.snapshot_container");
    }

    public SnapshotHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, ResumeTaskPool.getInstance(), event);
    }

    public void snapshotElement(CloudPluginSceneElementBO element, String snapshotName, String desc) {
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
                this.snapshot(element, snapshotName, desc);
            });
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
            getPluginHandler().prepareElementProcess(nodeId, SceneElementModel.Event.SNAPSHOT, Arrays.asList(ElementProcess.SNAPSHOT_CONTAINER));
            runElementProgress(nodeId, SceneElementModel.Event.SNAPSHOT, ElementProcess.SNAPSHOT_CONTAINER, () -> {
                this.snapshot(element, snapshotName, desc);
            });
        }
    }

    private void snapshot(CloudPluginSceneElementBO element, String snapshotName, String desc) {
        // 调用云平台接口 snapshot
        var result = virtualMachine.snapshot(element.getResourceId(), element.getImage(), snapshotName);
        SceneElementSnapshotBO snapshotBO = new SceneElementSnapshotBO();
        snapshotBO.setName(snapshotName);
        snapshotBO.setAddress(result.getImageName());
        snapshotBO.setDescription(desc);
        element.setImage(result.getImageName());
        var model = snapshotBO.toModel();
        model.setScene(getPluginHandler().getSourceSceneId());
        model.setSceneElement(element.getSrcElement().getId());
        var snapshotMapper = BeanCommon.getBean(SceneElementSnapshotMapper.class);
        getPluginHandler().updateElements(element.getSrcElement().getId());
        snapshotMapper.save(model);
    }
}
