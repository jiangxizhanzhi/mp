package cn.cyberpeace.mp.scene.core.plugin;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PluginSceneEventHandlerCommon<H extends ScenePluginHandler<?, ?, ?, ?>> {

    private H pluginHandler;

    private TaskPool taskPool;

    private Integer event;

    public PluginSceneEventHandlerCommon(H pluginHandler, TaskPool taskPool, int event) {
        this.pluginHandler = pluginHandler;
        this.taskPool = taskPool;
        this.event = event;
    }

    public void prepareEventProcess(int event, Class<?> sceneProcessClass, Class<?> elementProcessClass) {
        List<EventProcess> sceneEventProcesses = new ArrayList<>(ClassUtils.getClassConstantFieldMap(sceneProcessClass,
                EventProcess.class).values());
        getPluginHandler().prepareSceneProcess(event, sceneEventProcesses);

        List<EventProcess> elementEventProcesses = new ArrayList<>(ClassUtils.getClassConstantFieldMap(
                elementProcessClass, EventProcess.class).values());
        getPluginHandler().getElementMap().keySet().forEach(nodeId -> {
            getPluginHandler().prepareElementProcess(nodeId, event, elementEventProcesses);
        });
    }

    public void runTasks(List<Runnable> tasks) {
        var poolTasks = taskPool.runTasks(tasks);
        for (var poolTask: poolTasks) {
            if (poolTask.getException() != null) {
                throw new MpRuntimeException(poolTask.getException().getMessage());
            }
        }
    }

    public void runSceneProgress(EventProcess eventProcess, Runnable runner) {
        getPluginHandler().progressSceneProcess(event, eventProcess.getProcess());
        runner.run();
        getPluginHandler().overSceneProcess(event);
    }

    public void runElementProgress(String nodeId, EventProcess eventProcess, Runnable runner) {
        getPluginHandler().progressElementProcess(nodeId, event, eventProcess.getProcess());
        try {
            runner.run();
        } catch (Exception e) {
            getPluginHandler().errorElement(nodeId, event, e.getMessage());
            throw e;
        }
        getPluginHandler().overElementProcess(nodeId, event);
    }

}
