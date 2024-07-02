package cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.scene.constant.CommonSceneStage;
import cn.cyberpeace.mp.scene.core.event.SceneStageRecorder;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RecreateTaskPool;
import cn.cyberpeace.mp.scenepluginflow.constant.FlowSceneStage;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/25
 */
@Slf4j
public class PrepareHandler extends HandlerCommon {

    /**
     * PrepareHandler
     *
     * @param pluginHandler
     */
    public PrepareHandler(FlowScenePluginHandler pluginHandler) {
        super(pluginHandler, RecreateTaskPool.getInstance(), SceneElementModel.Event.INITIATE);
    }

    /**
     * prepare
     *
     * @param partialStart
     */
    public void prepare(boolean partialStart) {
        SceneStageRecorder stageRecorder = getPluginHandler().getSceneHandler().getSceneStageRecorder();
        stageRecorder.addStage(CommonSceneStage.INIT);

        stageRecorder.addStage(FlowSceneStage.CREATE_FLOW_SIMULATE);
        List<EventProcess> sceneEventProcesses = new ArrayList<>(ClassUtils.getClassConstantFieldMap(CreateHandler.SceneProcess.class, EventProcess.class).values());
        stageRecorder.submitProcess(sceneEventProcesses);
    }
}
