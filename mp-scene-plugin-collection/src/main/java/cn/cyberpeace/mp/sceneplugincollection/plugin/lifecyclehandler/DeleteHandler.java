package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool.DeleteTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DeleteHandler extends HandlerCommon {

    public static class SceneProcess {
        public static final EventProcess UNBIND_FLOW_COLLECT_PORTS = new EventProcess("unbind_flow_collect_ports", "sceneplugincollection.event.unbind_flow_collect_ports");
        public static final EventProcess UNBIND_PROBE_COLLECT_AGENTS = new EventProcess("unbind_probe_collect_agents", "sceneplugincollection.event.unbind_probe_collect_agents");
        public static final EventProcess DELETE_FLOW_COLLECTOR = new EventProcess("delete_flow_collector", "sceneplugincollection.event.delete_flow_collector");
        public static final EventProcess DELETE_PROBE_COLLECTOR = new EventProcess("delete_probe_collector", "sceneplugincollection.event.delete_probe_collector");
    }

    public static class ElementProcess {
    }

    public DeleteHandler(CollectionScenePluginHandler pluginHandler) {
        super(pluginHandler, DeleteTaskPool.getInstance(), SceneModel.Event.DELETE);
    }

    public void delete() {
        prepareEventProcess(SceneModel.Event.DELETE, SceneProcess.class, ElementProcess.class);

        var scene = getPluginHandler().getScene();
        var flowCollectorId = scene.getFlowCollector();
        if (!StringUtils.isEmpty(flowCollectorId)) {
            List<String> flowCollectMacAddrs = new ArrayList<>();
            getPluginHandler().getElementPortMap().forEach((portId, collectionPort) -> {
                if (!Boolean.TRUE.equals(collectionPort.getEnableFlowCollect())) {
                    return;
                }
                var macAttr = collectionPort.getSrcPort().getMacAddr();
                if (!StringUtils.isEmpty(macAttr)) {
                    flowCollectMacAddrs.add(macAttr);
                }
            });
            runSceneProgress(SceneProcess.UNBIND_FLOW_COLLECT_PORTS, () -> {
                getFlowCollector().unbindNetworkPorts(flowCollectorId, flowCollectMacAddrs);
            });
            runSceneProgress(SceneProcess.DELETE_FLOW_COLLECTOR, () -> {
                getFlowCollector().deleteCollector(flowCollectorId);
            });
        }

        var probeCollectorId = scene.getProbeCollector();
        if (!StringUtils.isEmpty(probeCollectorId)) {
            runSceneProgress(SceneProcess.UNBIND_PROBE_COLLECT_AGENTS, () -> {
                getProbeCollector().unbindAgents(probeCollectorId);
            });
            runSceneProgress(SceneProcess.DELETE_PROBE_COLLECTOR, () -> {
                getProbeCollector().deleteCollector(probeCollectorId);
            });
        }
    }

}
