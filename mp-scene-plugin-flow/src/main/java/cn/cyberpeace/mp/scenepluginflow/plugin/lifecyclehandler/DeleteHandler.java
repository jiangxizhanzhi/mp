package cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler.taskpool.DeleteTaskPool;
import cn.cyberpeace.mp.scenepluginflow.util.SceneHandlerCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DeleteHandler extends HandlerCommon {

    public static class SceneProcess {
        public static final EventProcess DELETE_EXECUTOR = new EventProcess("delete_executor", "scenepluginflow.event.delete_executor");
        public static final EventProcess DELETE_FLOW = new EventProcess("delete_flow", "scenepluginflow.event.delete_flow");
    }

    public static class ElementProcess {
        public static final EventProcess DELETE_FLOW = new EventProcess("delete_flow", "scenepluginflow.event.delete_flow");
    }

    public DeleteHandler(FlowScenePluginHandler pluginHandler) {
        super(pluginHandler, DeleteTaskPool.getInstance(), SceneModel.Event.DELETE);
    }

    public void delete() {
        prepareEventProcess(SceneModel.Event.DELETE, SceneProcess.class, ElementProcess.class);

        // 删除流量任务
        FlowPluginSceneBO scene = getPluginHandler().getScene();
        List<Runnable> deleteFlowTasks = new ArrayList<>();
        getPluginHandler().getElementMap().forEach((nodeId, flowElement) -> {
            List<Flow> flows = flowElement.getFlows();
            if (flows == null) {
                return;
            }
            flows.forEach(flow -> deleteFlowTasks.add(this.createDeleteFlowTask(nodeId, flow)));
        });
        runSceneProgress(SceneProcess.DELETE_FLOW, () -> runTasks(deleteFlowTasks));

        // 删除执行器
        String executorId = scene.getExecutor();
        if (StringUtils.isNotBlank(executorId)) {
            runSceneProgress(SceneProcess.DELETE_EXECUTOR, () -> {
                getHandlerUtils().deleteExecutor(executorId);
            });
        }

        // 清理缓存
        SceneHandlerCacheUtil.clear(getPluginHandler().getSourceSceneId());
    }

    private Runnable createDeleteFlowTask(String nodeId, Flow flow) {
        return () -> runElementProgress(nodeId, ElementProcess.DELETE_FLOW, () -> getHandlerUtils().deleteFlow(flow));
    }

}
