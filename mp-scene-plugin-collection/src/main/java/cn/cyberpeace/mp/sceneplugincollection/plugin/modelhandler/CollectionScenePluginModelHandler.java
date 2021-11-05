package cn.cyberpeace.mp.sceneplugincollection.plugin.modelhandler;

import cn.cyberpeace.mp.base.util.basic.stereotype.BeanFieldClasses;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginModelHandler;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneElementPortModel;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneElementMapper;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneElementPortMapper;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneMapper;


@ClassFieldClasses({
        CollectionPluginSceneBO.class,
        CollectionPluginSceneElementBO.class,
        CollectionPluginSceneElementPortBO.class,
        CollectionPluginSceneModel.class,
        CollectionPluginSceneElementModel.class,
        CollectionPluginSceneElementPortModel.class,
})
@BeanFieldClasses({
        CollectionPluginSceneMapper.class,
        CollectionPluginSceneElementMapper.class,
        CollectionPluginSceneElementPortMapper.class,
})
public class CollectionScenePluginModelHandler extends BasePluginModelHandler<
        SceneBO,
        SceneElementBO,
        SceneElementPortBO,
        CollectionPluginSceneModel,
        CollectionPluginSceneElementModel,
        CollectionPluginSceneElementPortModel,
        CollectionPluginSceneBO,
        CollectionPluginSceneElementBO,
        CollectionPluginSceneElementPortBO,
        CollectionPluginSceneMapper,
        CollectionPluginSceneElementMapper,
        CollectionPluginSceneElementPortMapper,
        CollectionScenePluginHandler> {
    public CollectionScenePluginModelHandler(CollectionScenePluginHandler pluginHandler) {
        init(pluginHandler);
    }
}
