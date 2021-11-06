package cn.cyberpeace.mp.scenepluginflow.plugin.modelhandler;

import cn.cyberpeace.mp.base.common.stereotype.BeanFieldClasses;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
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


@ClassFieldClasses({
        FlowPluginSceneBO.class,
        FlowPluginSceneElementBO.class,
        FlowPluginSceneElementPortBO.class,
        FlowPluginSceneModel.class,
        FlowPluginSceneElementModel.class,
        FlowPluginSceneElementPortModel.class,
})
@BeanFieldClasses({
        FlowPluginSceneMapper.class,
        FlowPluginSceneElementMapper.class,
        FlowPluginSceneElementPortMapper.class,
})
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
    public FlowScenePluginModelHandler(FlowScenePluginHandler pluginHandler) {
        init(pluginHandler);
    }
}
