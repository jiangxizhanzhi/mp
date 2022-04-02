package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon;
import cn.cyberpeace.mp.scene.repository.SceneElementPluginMapper;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.flowcollect.FlowCollector;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.ProbeCollector;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneProbeRouterPortMapper;
import lombok.Getter;

public class HandlerCommon extends PluginSceneEventHandlerCommon<CollectionScenePluginHandler> {

    @Getter
    private FlowCollector flowCollector;

    @Getter
    private ProbeCollector probeCollector;

    @Getter
    private CollectionPluginSceneProbeRouterPortMapper sceneProbeRouterPortMapper;

    @Getter
    private SceneElementPluginMapper sceneElementPluginMapper ;

    public HandlerCommon(CollectionScenePluginHandler pluginHandler, TaskPool taskPool, int event) {
        super(pluginHandler, taskPool, event);
        flowCollector = BeanCommon.getBean(FlowCollector.class);
        probeCollector = BeanCommon.getBean(ProbeCollector.class);
        sceneProbeRouterPortMapper = BeanCommon.getBean(CollectionPluginSceneProbeRouterPortMapper.class);
        sceneElementPluginMapper = BeanCommon.getBean(SceneElementPluginMapper.class);
    }

}
