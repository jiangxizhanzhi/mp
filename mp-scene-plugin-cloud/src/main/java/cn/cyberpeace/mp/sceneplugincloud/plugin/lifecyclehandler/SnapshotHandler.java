package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneElementSnapshotBO;
import cn.cyberpeace.mp.scene.repository.SceneElementSnapshotMapper;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.ResumeTaskPool;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/28
 */
@Slf4j
public class SnapshotHandler extends HandlerCommon {

    private final VirtualMachine virtualMachine = new VirtualMachine();

    public static class SceneProcess {
    }

    public static class ElementProcess {
    }

    public SnapshotHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, ResumeTaskPool.getInstance(), event);
    }

    public void snapshotElement(CloudPluginSceneElementBO element, String snapshotName, String desc) {
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
