package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.WazuhCommon;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
public class InitiateHandler extends HandlerCommon {


    public static class SceneProcess {
        public static final EventProcess INITIATE_FLOW_COLLECTION = new EventProcess("initiate_flow_collection", "sceneplugincollection.event.initiate_flow_collection");
        public static final EventProcess CREATE_PROBE_COLLECTOR = new EventProcess("create_probe_collector", "sceneplugincollection.event.create_probe_collector");
        public static final EventProcess INITIATE_PROBES = new EventProcess("initiate_probes", "sceneplugincollection.event.initiate_probes");
    }

    public static class ElementProcess {
    }

    private void addCollectionPort(String portId, CollectionPluginSceneElementPortBO collectionPort) {
        getPluginHandler().getElementIds().add(portId);
        getPluginHandler().getElementPortMap().put(portId, collectionPort);

        var srcPort = getPluginHandler().getSrcPort(portId);
        collectionPort.setSrcPort(srcPort);
    }

    public InitiateHandler(CollectionScenePluginHandler pluginHandler) {
        super(pluginHandler, CreateTaskPool.getInstance(), SceneModel.Event.INITIATE);
    }

    public void initiate() {
        prepareEventProcess(SceneModel.Event.INITIATE, SceneProcess.class, ElementProcess.class);

        var scene = getPluginHandler().getScene();
        // 全局开启流量采集
        if (Boolean.TRUE.equals(scene.getEnableFlowCollect())) {
            runSceneProgress(SceneProcess.INITIATE_FLOW_COLLECTION, () -> {
                getPluginHandler().getSceneHandler().getElementPortMap().forEach((portId, srcPort) -> {
                    var collectionPort = getPluginHandler().getPort(portId);
                    if (collectionPort == null) {
                        collectionPort = new CollectionPluginSceneElementPortBO();
                        addCollectionPort(portId, collectionPort);
                    }
                    collectionPort.setEnableFlowCollect(true);
                });
            });
        }

        var probeCollectionElements = getPluginHandler().getElementMap().values()
                .stream()
                .filter(collectionElement -> !CollectionUtils.isEmpty(collectionElement.getProbes()))
                .collect(Collectors.toList());
        if (!probeCollectionElements.isEmpty()) {
            // 创建采集器
            runSceneProgress(SceneProcess.CREATE_PROBE_COLLECTOR, () -> {
                var collectionId = getProbeCollector().createCollector();
                getPluginHandler().getScene().setProbeCollector(collectionId);
            });

            // 初始化安装探针脚本
            var collectionId = getPluginHandler().getScene().getProbeCollector();
            runSceneProgress(SceneProcess.INITIATE_PROBES, () -> {
                probeCollectionElements.forEach(collectionElement -> {
                    var srcElement = collectionElement.getSrcElement();
                    String systemType = SystemTypeUtils.getStandardSystemType(srcElement.getSystemType());
                    var probeInstallScript = getProbeCollector().generateProbeInstallScript(systemType,
                            srcElement.getEid(), collectionId);
                    var initCommands = srcElement.getInitCommands();
                    if (initCommands == null) {
                        initCommands = new ArrayList<>();
                    }
                    initCommands.add(probeInstallScript);
                    srcElement.setInitCommands(initCommands);
                });
            });
        }

    }

}
