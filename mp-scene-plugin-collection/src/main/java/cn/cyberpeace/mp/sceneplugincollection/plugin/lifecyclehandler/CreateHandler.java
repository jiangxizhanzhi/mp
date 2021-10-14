package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CreateHandler extends HandlerCommon {


    public static class SceneProcess {
        public static final EventProcess CREATE_FLOW_COLLECTION = new EventProcess("create_flow_collection", "sceneplugincollection.event.create_flow_collection");
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
            runSceneProgress(SceneProcess.CREATE_FLOW_COLLECTION, () -> {
                var flowCollectorId = getFlowCollector().createCollector(flowCollectMacAddrs);
                getPluginHandler().getScene().setFlowCollector(flowCollectorId);
            });
        }
    }

}
