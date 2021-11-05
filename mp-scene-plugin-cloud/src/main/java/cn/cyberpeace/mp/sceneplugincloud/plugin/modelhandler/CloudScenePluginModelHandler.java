package cn.cyberpeace.mp.sceneplugincloud.plugin.modelhandler;

import cn.cyberpeace.mp.base.util.basic.stereotype.BeanFieldClasses;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginModelHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementPortModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementPortMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneMapper;


@ClassFieldClasses({
        CloudPluginSceneBO.class,
        CloudPluginSceneElementBO.class,
        CloudPluginSceneElementPortBO.class,
        CloudPluginSceneModel.class,
        CloudPluginSceneElementModel.class,
        CloudPluginSceneElementPortModel.class,
})
@BeanFieldClasses({
        CloudPluginSceneMapper.class,
        CloudPluginSceneElementMapper.class,
        CloudPluginSceneElementPortMapper.class,
})
public class CloudScenePluginModelHandler extends BasePluginModelHandler<
        SceneBO,
        SceneElementBO,
        SceneElementPortBO,
        CloudPluginSceneModel,
        CloudPluginSceneElementModel,
        CloudPluginSceneElementPortModel,
        CloudPluginSceneBO,
        CloudPluginSceneElementBO,
        CloudPluginSceneElementPortBO,
        CloudPluginSceneMapper,
        CloudPluginSceneElementMapper,
        CloudPluginSceneElementPortMapper,
        CloudScenePluginHandler> {
    public CloudScenePluginModelHandler(CloudScenePluginHandler pluginHandler) {
        init(pluginHandler);
    }
}
