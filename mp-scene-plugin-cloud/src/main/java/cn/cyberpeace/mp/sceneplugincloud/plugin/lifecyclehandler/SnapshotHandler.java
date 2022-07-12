package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneElementSnapshotBO;
import cn.cyberpeace.mp.scene.repository.SceneElementSnapshotMapper;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.ResumeTaskPool;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/28
 */
@Slf4j
public class SnapshotHandler extends HandlerCommon {

    public static class SceneProcess {
    }

    public static class ElementProcess {
    }

    public SnapshotHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, ResumeTaskPool.getInstance(), event);
    }

    public void snapshotElement(CloudPluginSceneElementBO element, String snapshotName) {
        // 调用云平台接口 snapshot
        String address = "asdfasdfasdf";

        element.getResourceId();
        element.getImage();

        SceneElementSnapshotBO snapshotBO = new SceneElementSnapshotBO();
        snapshotBO.setName(snapshotName);
        snapshotBO.setAddress(address);
        var model = snapshotBO.toModel();
        model.setScene(getPluginHandler().getSourceSceneId());
        model.setSceneElement(element.getSrcElement().getId());
        var snapshotMapper = BeanCommon.getBean(SceneElementSnapshotMapper.class);
        snapshotMapper.save(model);
    }
}
