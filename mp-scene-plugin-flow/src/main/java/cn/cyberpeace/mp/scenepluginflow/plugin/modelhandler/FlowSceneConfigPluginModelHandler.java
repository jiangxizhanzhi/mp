package cn.cyberpeace.mp.scenepluginflow.plugin.modelhandler;

import cn.cyberpeace.mp.base.common.stereotype.BeanFieldClasses;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginModelHandler;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneConfigBO;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneConfigElementBO;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneConfigElementModel;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneConfigElementPortModel;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneConfigModel;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowSceneConfigPluginHandler;
import cn.cyberpeace.mp.scenepluginflow.repository.FlowPluginSceneConfigElementMapper;
import cn.cyberpeace.mp.scenepluginflow.repository.FlowPluginSceneConfigElementPortMapper;
import cn.cyberpeace.mp.scenepluginflow.repository.FlowPluginSceneConfigMapper;


/**
 * 场景实例的处理
 * 场景实例的功能由本类对外提供
 */
@ClassFieldClasses({
        FlowPluginSceneConfigBO.class,
        FlowPluginSceneConfigElementBO.class,
        FlowPluginSceneConfigElementPortBO.class,
        FlowPluginSceneConfigModel.class,
        FlowPluginSceneConfigElementModel.class,
        FlowPluginSceneConfigElementPortModel.class,
})
@BeanFieldClasses({
        FlowPluginSceneConfigMapper.class,
        FlowPluginSceneConfigElementMapper.class,
        FlowPluginSceneConfigElementPortMapper.class,
})
public class FlowSceneConfigPluginModelHandler extends BasePluginModelHandler<
        SceneConfigBO,
        SceneConfigElementBO,
        SceneConfigElementPortBO,
        FlowPluginSceneConfigModel,
        FlowPluginSceneConfigElementModel,
        FlowPluginSceneConfigElementPortModel,
        FlowPluginSceneConfigBO,
        FlowPluginSceneConfigElementBO,
        FlowPluginSceneConfigElementPortBO,
        FlowPluginSceneConfigMapper,
        FlowPluginSceneConfigElementMapper,
        FlowPluginSceneConfigElementPortMapper,
        FlowSceneConfigPluginHandler> {

    public FlowSceneConfigPluginModelHandler(FlowSceneConfigPluginHandler pluginHandler) {
        init(pluginHandler);
    }
}
