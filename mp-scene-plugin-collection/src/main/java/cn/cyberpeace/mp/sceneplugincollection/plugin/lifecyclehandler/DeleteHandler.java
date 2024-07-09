package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.CloudApiProxyBuilder;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualRouter;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterDeleteData;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.DeleteFlowCollectBo;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool.DeleteTaskPool;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @ClassName DeleteHandler
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see HandlerCommon
 */
@Slf4j
public class DeleteHandler extends HandlerCommon {

    public static class SceneProcess {
        public static final EventProcess UNBIND_FLOW_COLLECT_PORTS = new EventProcess("unbind_flow_collect_ports", "sceneplugincollection.event.unbind_flow_collect_ports");
        public static final EventProcess UNBIND_PROBE_COLLECT_AGENTS = new EventProcess("unbind_probe_collect_agents", "sceneplugincollection.event.unbind_probe_collect_agents");
        public static final EventProcess DELETE_FLOW_COLLECTOR = new EventProcess("delete_flow_collector", "sceneplugincollection.event.delete_flow_collector");
        public static final EventProcess DELETE_PROBE_COLLECTOR = new EventProcess("delete_probe_collector", "sceneplugincollection.event.delete_probe_collector");
        public static final EventProcess DELETE_PROBE_ROUTER = new EventProcess("delete_probe_router", "sceneplugincollection.event.delete_probe_router");
    }

    public static class ElementProcess {
        public static final EventProcess UNBIND_PROBE_COLLECT_AGENT = new EventProcess("unbind_probe_collect_agent", "sceneplugincollection.event.unbind_probe_collect_agent");
    }

    /**
     * DeleteHandler
     *
     * @param pluginHandler
     */
    public DeleteHandler(CollectionScenePluginHandler pluginHandler) {
        super(pluginHandler, DeleteTaskPool.getInstance(), SceneModel.Event.DELETE);
    }

    /**
     * deleteProbeRouter
     *
     * @param probeRouterId
     * @return boolean
     */
    protected boolean deleteProbeRouter(String probeRouterId) {
        VirtualRouter vrClient = CloudApiProxyBuilder.build(new VirtualRouter(), VirtualRouter.class);
        VirtualRouterDeleteData deleteData = new VirtualRouterDeleteData();
        deleteData.setIds(List.of(probeRouterId));
        return vrClient.delete(deleteData);
    }

    /**
     * delete
     */
    public void delete() {
        prepareEventProcess(SceneModel.Event.DELETE, SceneProcess.class, ElementProcess.class);

        CollectionPluginSceneBO scene = getPluginHandler().getScene();
        String flowCollectorId = scene.getFlowCollector();
        if (!StringUtils.isEmpty(flowCollectorId)) {
            // 先保存流量数据 todo
//            BeanCommon.getBean(ScenePluginCollectionSceneService.class).saveFlowPcap(getPluginHandler().getSourceSceneId(), flowCollectorId);

            runSceneProgress(SceneProcess.DELETE_FLOW_COLLECTOR, () -> {
                getFlowCollector().deleteCollector(new DeleteFlowCollectBo()
                        .setFlowMirrorId(scene.getFlowMirrorId())
                        .setSceneId(getPluginHandler().getSceneHandler().getSceneId())
                        .setCollectorInstanceId(scene.getFlowCollectorContainerId()));
            });
        }

        String probeCollectorId = scene.getProbeCollector();
        if (!StringUtils.isEmpty(probeCollectorId)) {
            runSceneProgress(SceneProcess.DELETE_PROBE_COLLECTOR, () -> {
                getProbeCollector().deleteCollector(probeCollectorId);
            });
        }

        String probeRouterId = scene.getProbeRouter();
        if (!StringUtils.isEmpty(probeRouterId)) {
            runSceneProgress(SceneProcess.DELETE_PROBE_ROUTER, () -> {
                deleteProbeRouter(probeRouterId);
            });
        }

    }
}
