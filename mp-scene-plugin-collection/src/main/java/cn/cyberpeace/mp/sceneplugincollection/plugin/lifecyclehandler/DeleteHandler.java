package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.CloudApiProxyBuilder;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualRouter;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterDeleteData;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool.DeleteTaskPool;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public DeleteHandler(CollectionScenePluginHandler pluginHandler) {
        super(pluginHandler, DeleteTaskPool.getInstance(), SceneModel.Event.DELETE);
    }

    protected void unbindProbeCollectAgent(CollectionPluginSceneElementBO collectionElement, String probeCollectorId) {
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
        getProbeCollector().unbindAgent(probeCollectorId, cloudResourceId);
    }

    protected boolean deleteProbeRouter(String probeRouterId) {
        var vrClient = CloudApiProxyBuilder.build(new VirtualRouter(), VirtualRouter.class);
        var deleteData = new VirtualRouterDeleteData();
        deleteData.setIds(List.of(probeRouterId));
        return vrClient.delete(deleteData);
    }

    public void delete() {
        prepareEventProcess(SceneModel.Event.DELETE, SceneProcess.class, ElementProcess.class);

        var scene = getPluginHandler().getScene();
        var flowCollectorId = scene.getFlowCollector();
        if (!StringUtils.isEmpty(flowCollectorId)) {
            
            BeanCommon.getBean(ScenePluginCollectionSceneService.class).saveFlowPcap(getPluginHandler().getSourceSceneId(), flowCollectorId);
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


            
            Set<String> collectorVmInstanceIds = new HashSet<>();
            Set<String> collectorContainerInstanceIds = new HashSet<>();
            Set<String> mirrorVmInstanceIds = new HashSet<>();
            Set<String> mirrorContainerInstanceIds = new HashSet<>();
            CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                    .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
            boolean enableFlowCollect = false;
            boolean enableFlowMirror = false;
            for (Map.Entry<String, CollectionPluginSceneElementPortBO> entry : getPluginHandler().getElementPortMap().entrySet()) {
                String portId = entry.getKey();
                CollectionPluginSceneElementPortBO collectionPort = entry.getValue();
                if (Boolean.TRUE.equals(collectionPort.getEnableFlowCollect())) {
                    enableFlowCollect = true;
                    var srcPort = collectionPort.getSrcPort();
                    var srcElement = srcPort.getElement();
                    var cloudElement = cloudPluginHandler.getElement(srcElement.getId());
                    if (cloudElement != null && cloudElement.getVirtualType() != null
                            && !StringUtils.isEmpty(cloudElement.getResourceId())) {
                        if (cloudElement.getVirtualType().equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                            collectorVmInstanceIds.add(cloudElement.getResourceId());
                        } else if (cloudElement.getVirtualType().equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                            collectorContainerInstanceIds.add(cloudElement.getResourceId());
                        }
                    }
                }
                if (Boolean.TRUE.equals(collectionPort.getEnableFlowMirror())) {
                    enableFlowMirror = true;
                    var srcPort = collectionPort.getSrcPort();
                    var srcElement = srcPort.getElement();
                    var cloudElement = cloudPluginHandler.getElement(srcElement.getId());
                    if (cloudElement != null && cloudElement.getVirtualType() != null && !StringUtils.isEmpty(cloudElement.getResourceId())) {
                        if (cloudElement.getVirtualType().equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                            mirrorVmInstanceIds.add(cloudElement.getResourceId());
                        } else if (cloudElement.getVirtualType().equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                            mirrorContainerInstanceIds.add(cloudElement.getResourceId());
                        }
                    }
                }
            }
            boolean finalEnableFlowCollect = enableFlowCollect;
            boolean finalEnableFlowMirror = enableFlowMirror;
            runSceneProgress(SceneProcess.DELETE_FLOW_COLLECTOR, () -> {
                if (finalEnableFlowCollect) {
                    getFlowCollector().deleteCollector(flowCollectorId, new ArrayList<>(collectorVmInstanceIds), new ArrayList<>(collectorContainerInstanceIds));
                } else if (finalEnableFlowMirror) {
                    getFlowCollector().deleteEntityMirror(new ArrayList<>(mirrorVmInstanceIds), new ArrayList<>(mirrorContainerInstanceIds));
                }
            });
        }

        var probeCollectorId = scene.getProbeCollector();
        if (!StringUtils.isEmpty(probeCollectorId)) {











            runSceneProgress(SceneProcess.DELETE_PROBE_COLLECTOR, () -> {
                getProbeCollector().deleteCollector(probeCollectorId);
            });
        }

        var probeRouterId = scene.getProbeRouter();
        if (!StringUtils.isEmpty(probeRouterId)) {
            runSceneProgress(SceneProcess.DELETE_PROBE_ROUTER, () -> {
                deleteProbeRouter(probeRouterId);
            });
        }

    }

    public void unbindProbeCollectAgentProcess(CollectionPluginSceneElementBO collectionElement, String probeCollectorId) {
        runElementProgress(collectionElement.getSrcElement().getId(), ElementProcess.UNBIND_PROBE_COLLECT_AGENT,
                () -> unbindProbeCollectAgent(collectionElement, probeCollectorId));
    }
}
