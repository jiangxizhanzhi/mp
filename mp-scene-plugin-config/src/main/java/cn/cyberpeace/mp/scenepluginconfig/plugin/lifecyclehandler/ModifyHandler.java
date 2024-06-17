package cn.cyberpeace.mp.scenepluginconfig.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scenepluginconfig.plugin.ConfigScenePluginHandler;
import cn.cyberpeace.mp.scenepluginconfig.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

@Slf4j
public class ModifyHandler extends HandlerCommon {

    private ModifyCreateHandler modifyCreateHandler;

    public ModifyHandler(ConfigScenePluginHandler originPlugin, ConfigScenePluginHandler newPlugin) {
        super(newPlugin, CreateTaskPool.getInstance(), SceneModel.Event.MODIFY);
        this.modifyCreateHandler = new ModifyCreateHandler(newPlugin, getTaskPool());
    }

    public void modify() {
        this.modifyCreateHandler.create();
    }

    private static class ModifyCreateHandler extends CreateHandler {
        public ModifyCreateHandler(ConfigScenePluginHandler pluginHandler, TaskPool taskPool) {
            super(pluginHandler);
            setEvent(SceneModel.Event.MODIFY);
            setTaskPool(taskPool);
        }

        @Override
        public void create() {
            if (CollectionUtils.isNotEmpty(getPluginHandler().getSceneHandler().getCreateVirtualMachineTasks())) {
                runSceneProgress(SceneProcess.CREATE_VIRTUAL_MACHINE, () ->
                        runTasks(new ArrayList<>(getPluginHandler().getSceneHandler().getCreateVirtualMachineTasks())));
            }
        }
    }
}
