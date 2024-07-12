 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HandlerCommon.java.
 // File size 1872.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
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

/**
 * @ClassName HandlerCommon
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see PluginSceneEventHandlerCommon
 */
public class HandlerCommon extends PluginSceneEventHandlerCommon<CollectionScenePluginHandler> {

    @Getter
    private FlowCollector flowCollector;

    @Getter
    private ProbeCollector probeCollector;

    @Getter
    private CollectionPluginSceneProbeRouterPortMapper sceneProbeRouterPortMapper;

    @Getter
    private SceneElementPluginMapper sceneElementPluginMapper ;

    /**
     * HandlerCommon
     *
     * @param pluginHandler
     * @param taskPool
     * @param event
     */
    public HandlerCommon(CollectionScenePluginHandler pluginHandler, TaskPool taskPool, int event) {
        super(pluginHandler, taskPool, event);
        flowCollector = BeanCommon.getBean(FlowCollector.class);
        probeCollector = BeanCommon.getBean(ProbeCollector.class);
        sceneProbeRouterPortMapper = BeanCommon.getBean(CollectionPluginSceneProbeRouterPortMapper.class);
        sceneElementPluginMapper = BeanCommon.getBean(SceneElementPluginMapper.class);
    }

}
