 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowScenePluginModelHandler.java.
 // File size 2750.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.plugin.modelhandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginModelHandler;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneElementPortBO;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneElementModel;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneElementPortModel;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneModel;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.repository.FlowPluginSceneElementMapper;
import cn.cyberpeace.mp.scenepluginflow.repository.FlowPluginSceneElementPortMapper;
import cn.cyberpeace.mp.scenepluginflow.repository.FlowPluginSceneMapper;

public class FlowScenePluginModelHandler extends BasePluginModelHandler<
        SceneBO,
        SceneElementBO,
        SceneElementPortBO,
        FlowPluginSceneModel,
        FlowPluginSceneElementModel,
        FlowPluginSceneElementPortModel,
        FlowPluginSceneBO,
        FlowPluginSceneElementBO,
        FlowPluginSceneElementPortBO,
        FlowPluginSceneMapper,
        FlowPluginSceneElementMapper,
        FlowPluginSceneElementPortMapper,
        FlowScenePluginHandler> {
    /**
     * FlowScenePluginModelHandler
     *
     * @param pluginHandler
     */
    public FlowScenePluginModelHandler(FlowScenePluginHandler pluginHandler) {
        init(pluginHandler);
    }

    /**
     * initClass
     */
    @Override
    protected void initClass() {
        super.pluginSceneBOClass = FlowPluginSceneBO.class;
        super.pluginSceneElementBOClass = FlowPluginSceneElementBO.class;
        super.pluginSceneElementPortBOClass = FlowPluginSceneElementPortBO.class;
        super.pluginSceneModelClass = FlowPluginSceneModel.class;
        super.pluginSceneElementModelClass = FlowPluginSceneElementModel.class;
        super.pluginSceneElementPortModelClass = FlowPluginSceneElementPortModel.class;
    }

    /**
     * initMapper
     */
    @Override
    protected void initMapper() {
        super.pluginSceneMapper = BeanCommon.getBean(FlowPluginSceneMapper.class);
        super.pluginSceneElementMapper = BeanCommon.getBean(FlowPluginSceneElementMapper.class);
        super.pluginSceneElementPortMapper = BeanCommon.getBean(FlowPluginSceneElementPortMapper.class);
    }
}
