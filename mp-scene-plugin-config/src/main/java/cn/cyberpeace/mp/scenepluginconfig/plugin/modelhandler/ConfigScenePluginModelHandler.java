package cn.cyberpeace.mp.scenepluginconfig.plugin.modelhandler;

import cn.cyberpeace.mp.base.common.stereotype.BeanFieldClasses;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginModelHandler;
import cn.cyberpeace.mp.scenepluginconfig.bo.ConfigPluginSceneBO;
import cn.cyberpeace.mp.scenepluginconfig.bo.ConfigPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginconfig.bo.ConfigPluginSceneElementPortBO;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigPluginSceneElementModel;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigPluginSceneElementPortModel;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigPluginSceneModel;
import cn.cyberpeace.mp.scenepluginconfig.plugin.ConfigScenePluginHandler;
import cn.cyberpeace.mp.scenepluginconfig.repository.ConfigPluginSceneElementMapper;
import cn.cyberpeace.mp.scenepluginconfig.repository.ConfigPluginSceneElementPortMapper;
import cn.cyberpeace.mp.scenepluginconfig.repository.ConfigPluginSceneMapper;


@ClassFieldClasses({
        ConfigPluginSceneBO.class,
        ConfigPluginSceneElementBO.class,
        ConfigPluginSceneElementPortBO.class,
        ConfigPluginSceneModel.class,
        ConfigPluginSceneElementModel.class,
        ConfigPluginSceneElementPortModel.class,
})
@BeanFieldClasses({
        ConfigPluginSceneMapper.class,
        ConfigPluginSceneElementMapper.class,
        ConfigPluginSceneElementPortMapper.class,
})
public class ConfigScenePluginModelHandler extends BasePluginModelHandler<
        SceneBO,
        SceneElementBO,
        SceneElementPortBO,
        ConfigPluginSceneModel,
        ConfigPluginSceneElementModel,
        ConfigPluginSceneElementPortModel,
        ConfigPluginSceneBO,
        ConfigPluginSceneElementBO,
        ConfigPluginSceneElementPortBO,
        ConfigPluginSceneMapper,
        ConfigPluginSceneElementMapper,
        ConfigPluginSceneElementPortMapper,
        ConfigScenePluginHandler> {
    public ConfigScenePluginModelHandler(ConfigScenePluginHandler pluginHandler) {
        init(pluginHandler);
    }
}
