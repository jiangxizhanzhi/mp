package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CreateHandler extends HandlerCommon {


    public static class SceneProcess {
        public static final EventProcess CREATE_FLOW_COLLECTOR = new EventProcess("create_flow_collector", "sceneplugincollection.event.create_flow_collector");
        public static final EventProcess BIND_FLOW_COLLECT_PORTS = new EventProcess("bind_flow_collect_ports", "sceneplugincollection.event.bind_flow_collect_ports");
        public static final EventProcess BIND_PROBE_COLLECT_AGENTS = new EventProcess("bind_probe_collect_agents", "sceneplugincollection.event.bind_probe_collect_agents");
    }

    public static class ElementProcess {
    }

    public CreateHandler(CollectionScenePluginHandler pluginHandler) {
        super(pluginHandler, CreateTaskPool.getInstance(), SceneModel.Event.CREATE);
    }

    public void create() {
        prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);


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
        if (!flowCollectMacAddrs.isEmpty()) {
            // 创建流量采集器
            runSceneProgress(SceneProcess.CREATE_FLOW_COLLECTOR, () -> {
                var flowCollectorId = getFlowCollector().createCollector();
                getPluginHandler().getScene().setFlowCollector(flowCollectorId);
            });

            // 绑定网络端口到流量采集器
            var flowCollectorId = getPluginHandler().getScene().getFlowCollector();
            runSceneProgress(SceneProcess.BIND_FLOW_COLLECT_PORTS, () -> {
                getFlowCollector().bindNetworkPorts(flowCollectorId, flowCollectMacAddrs);
            });
        }

        // 绑定探针agent到探针采集器
        var probeCollectorId = getPluginHandler().getScene().getProbeCollector();
        if (!StringUtils.isEmpty(probeCollectorId)) {
            var probeCollectionElements = getPluginHandler().getElementMap().values()
                    .stream()
                    .filter(collectionElement -> !CollectionUtils.isEmpty(collectionElement.getProbes()))
                    .collect(Collectors.toList());
            if (!probeCollectionElements.isEmpty()) {
                runSceneProgress(SceneProcess.BIND_PROBE_COLLECT_AGENTS, () -> {
                    getProbeCollector().bindAgents(probeCollectorId);
                });
            }
        }
    }

}
