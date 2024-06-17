package cn.cyberpeace.mp.scenepluginconfig.plugin.modelhandler;

import cn.cyberpeace.mp.base.common.stereotype.BeanFieldClasses;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginModelHandler;
import cn.cyberpeace.mp.scenepluginconfig.bo.ConfigPluginSceneConfigBO;
import cn.cyberpeace.mp.scenepluginconfig.bo.ConfigPluginSceneConfigElementBO;
import cn.cyberpeace.mp.scenepluginconfig.bo.ConfigPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigPluginSceneConfigElementModel;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigPluginSceneConfigElementPortModel;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigPluginSceneConfigModel;
import cn.cyberpeace.mp.scenepluginconfig.plugin.ConfigSceneConfigPluginHandler;
import cn.cyberpeace.mp.scenepluginconfig.repository.ConfigPluginSceneConfigElementMapper;
import cn.cyberpeace.mp.scenepluginconfig.repository.ConfigPluginSceneConfigElementPortMapper;
import cn.cyberpeace.mp.scenepluginconfig.repository.ConfigPluginSceneConfigMapper;


/**
 * 场景实例的处理
 * 场景实例的功能由本类对外提供
 */
@ClassFieldClasses({
        ConfigPluginSceneConfigBO.class,
        ConfigPluginSceneConfigElementBO.class,
        ConfigPluginSceneConfigElementPortBO.class,
        ConfigPluginSceneConfigModel.class,
        ConfigPluginSceneConfigElementModel.class,
        ConfigPluginSceneConfigElementPortModel.class,
})
@BeanFieldClasses({
        ConfigPluginSceneConfigMapper.class,
        ConfigPluginSceneConfigElementMapper.class,
        ConfigPluginSceneConfigElementPortMapper.class,
})
public class ConfigSceneConfigPluginModelHandler extends BasePluginModelHandler<
        SceneConfigBO,
        SceneConfigElementBO,
        SceneConfigElementPortBO,
        ConfigPluginSceneConfigModel,
        ConfigPluginSceneConfigElementModel,
        ConfigPluginSceneConfigElementPortModel,
        ConfigPluginSceneConfigBO,
        ConfigPluginSceneConfigElementBO,
        ConfigPluginSceneConfigElementPortBO,
        ConfigPluginSceneConfigMapper,
        ConfigPluginSceneConfigElementMapper,
        ConfigPluginSceneConfigElementPortMapper,
        ConfigSceneConfigPluginHandler> {

    public ConfigSceneConfigPluginModelHandler(ConfigSceneConfigPluginHandler pluginHandler) {
        init(pluginHandler);
    }
}
