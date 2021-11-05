package cn.cyberpeace.mp.sceneplugincloud.plugin.modelhandler;

import cn.cyberpeace.mp.base.util.basic.stereotype.BeanFieldClasses;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginModelHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementPortModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudSceneConfigPluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConfigElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConfigElementPortMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConfigMapper;


/**
 * 场景实例的处理
 * 场景实例的功能由本类对外提供
 */
@ClassFieldClasses({
        CloudPluginSceneConfigBO.class,
        CloudPluginSceneConfigElementBO.class,
        CloudPluginSceneConfigElementPortBO.class,
        CloudPluginSceneConfigModel.class,
        CloudPluginSceneConfigElementModel.class,
        CloudPluginSceneConfigElementPortModel.class,
})
@BeanFieldClasses({
        CloudPluginSceneConfigMapper.class,
        CloudPluginSceneConfigElementMapper.class,
        CloudPluginSceneConfigElementPortMapper.class,
})
public class CloudSceneConfigPluginModelHandler extends BasePluginModelHandler<
        SceneConfigBO,
        SceneConfigElementBO,
        SceneConfigElementPortBO,
        CloudPluginSceneConfigModel,
        CloudPluginSceneConfigElementModel,
        CloudPluginSceneConfigElementPortModel,
        CloudPluginSceneConfigBO,
        CloudPluginSceneConfigElementBO,
        CloudPluginSceneConfigElementPortBO,
        CloudPluginSceneConfigMapper,
        CloudPluginSceneConfigElementMapper,
        CloudPluginSceneConfigElementPortMapper,
        CloudSceneConfigPluginHandler> {

    public CloudSceneConfigPluginModelHandler(CloudSceneConfigPluginHandler pluginHandler) {
        init(pluginHandler);
    }
}
