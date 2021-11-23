package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class CreateHandler extends HandlerCommon {


    public static class SceneProcess {
        public static final EventProcess CREATE_FLOW_COLLECTOR = new EventProcess("create_flow_collector", "sceneplugincollection.event.create_flow_collector");
        public static final EventProcess BIND_FLOW_COLLECT_PORTS = new EventProcess("bind_flow_collect_ports", "sceneplugincollection.event.bind_flow_collect_ports");
        public static final EventProcess BIND_PROBE_COLLECT_AGENTS = new EventProcess("bind_probe_collect_agents", "sceneplugincollection.event.bind_probe_collect_agents");
    }

    public static class ElementProcess {
        public static final EventProcess BIND_PROBE_COLLECT_AGENT = new EventProcess("bind_probe_collect_agent", "sceneplugincollection.event.bind_probe_collect_agent");
    }

    public CreateHandler(CollectionScenePluginHandler pluginHandler) {
        super(pluginHandler, CreateTaskPool.getInstance(), SceneModel.Event.CREATE);
    }

    protected void bindProbeCollectAgent(CollectionPluginSceneElementBO collectionElement, String probeCollectorId) {
        var srcElement = collectionElement.getSrcElement();
        CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        CloudPluginSceneElementBO cloudElement = cloudPluginHandler.getElement(srcElement.getId());
        if (cloudElement == null) {
            return;
        }

        var cloudResourceId = cloudElement.getResourceId();
        if (cloudResourceId == null) {
            return;
        }
        getProbeCollector().bindAgent(probeCollectorId, cloudResourceId);
    }

    public void create() {
        prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);

        // TODO 流量镜像应该是基于网络端口的, 这里是基于节点的
        Set<String> vmInstanceIds = new HashSet<>();
        Set<String> containerInstanceIds = new HashSet<>();
        CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        getPluginHandler().getElementPortMap().forEach((portId, collectionPort) -> {
            if (!Boolean.TRUE.equals(collectionPort.getEnableFlowCollect())) {
                return;
            }
            var srcPort = collectionPort.getSrcPort();
            var srcElement = srcPort.getElement();
            var cloudElement = cloudPluginHandler.getElement(srcElement.getId());
            if (cloudElement != null && cloudElement.getVirtualType() != null
                    && !StringUtils.isEmpty(cloudElement.getResourceId())) {
                if (cloudElement.getVirtualType().equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                    vmInstanceIds.add(cloudElement.getResourceId());
                } else if (cloudElement.getVirtualType().equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                    containerInstanceIds.add(cloudElement.getResourceId());
                }
            }
        });
        if (!vmInstanceIds.isEmpty() || !containerInstanceIds.isEmpty()) {
            // 创建流量采集器
            runSceneProgress(SceneProcess.CREATE_FLOW_COLLECTOR, () -> {
                var flowCollectorId = getFlowCollector().createCollector();
                getPluginHandler().getScene().setFlowCollector(flowCollectorId);
            });

            // 绑定网络端口到流量采集器
            var flowCollectorId = getPluginHandler().getScene().getFlowCollector();
            runSceneProgress(SceneProcess.BIND_FLOW_COLLECT_PORTS, () -> {
                getFlowCollector().bindNetworkPorts(flowCollectorId, new ArrayList<>(vmInstanceIds),
                        new ArrayList<>(containerInstanceIds));
            });
        }

        // 绑定探针agent到探针采集器
        var probeCollectorId = getPluginHandler().getScene().getProbeCollector();
        if (!StringUtils.isEmpty(probeCollectorId)) {
            var probeCollectionElements = getPluginHandler().getElementMap().values()
                    .stream()
                    .filter(collectionElement -> !CollectionUtils.isEmpty(collectionElement.getProbes()))
                    .collect(Collectors.toList());
            var bindProbeCollectAgentTasks = probeCollectionElements.stream()
                    .map(collectionElement -> (Runnable)() -> bindProbeCollectAgentProcess(collectionElement,
                            probeCollectorId))
                    .collect(Collectors.toList());
            if (!bindProbeCollectAgentTasks.isEmpty()) {
                runSceneProgress(SceneProcess.BIND_PROBE_COLLECT_AGENTS, () -> runTasks(bindProbeCollectAgentTasks));
            }
        }
    }

    public void bindProbeCollectAgentProcess(CollectionPluginSceneElementBO collectionElement, String probeCollectorId) {
        runElementProgress(collectionElement.getSrcElement().getId(), ElementProcess.BIND_PROBE_COLLECT_AGENT,
                () -> bindProbeCollectAgent(collectionElement, probeCollectorId));
    }

}
