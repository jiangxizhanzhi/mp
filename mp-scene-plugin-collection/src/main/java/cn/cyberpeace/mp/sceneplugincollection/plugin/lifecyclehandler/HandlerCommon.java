package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.flowcollect.FlowCollector;
import lombok.Getter;

public class HandlerCommon extends PluginSceneEventHandlerCommon<CollectionScenePluginHandler> {

    @Getter
    private FlowCollector flowCollector;

    public HandlerCommon(CollectionScenePluginHandler pluginHandler, TaskPool taskPool, int event) {
        super(pluginHandler, taskPool, event);
        flowCollector = BeanUtils.getBean(FlowCollector.class);
    }

}
