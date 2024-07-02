package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.scene.constant.CommonSceneStage;
import cn.cyberpeace.mp.scene.core.event.SceneStageRecorder;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RecreateTaskPool;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSceneStage;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName PrepareHandler
 * @Description
 * @Author maojun
 * @Date 2021/10/25
 * @Version 1.0.0
 * @see HandlerCommon
 */
@Slf4j
public class PrepareHandler extends HandlerCommon {


    /**
     * PrepareHandler
     *
     * @param pluginHandler
     */
    public PrepareHandler(CollectionScenePluginHandler pluginHandler) {
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
        List<EventProcess> sceneEventProcesses = new ArrayList<>(ClassUtils.getClassConstantFieldMap(InitiateHandler.SceneProcess.class, EventProcess.class).values());
        stageRecorder.submitProcess(sceneEventProcesses);

        stageRecorder.addStage(CollectionSceneStage.CREATE_FLOW_COLLECT);
        stageRecorder.addStage(CollectionSceneStage.CREATE_DATA_COLLECT);
        sceneEventProcesses = new ArrayList<>(ClassUtils.getClassConstantFieldMap(CreateHandler.SceneProcess.class, EventProcess.class).values());
        stageRecorder.submitProcess(sceneEventProcesses);
    }
}
