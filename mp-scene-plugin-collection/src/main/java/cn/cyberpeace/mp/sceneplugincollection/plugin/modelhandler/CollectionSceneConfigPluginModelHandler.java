package cn.cyberpeace.mp.sceneplugincollection.plugin.modelhandler;

import cn.cyberpeace.mp.base.annotation.BeanFieldClasses;
import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginModelHandler;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneConfigElementModel;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneConfigElementPortModel;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneConfigModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionSceneConfigPluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneConfigElementMapper;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneConfigElementPortMapper;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneConfigMapper;


/**
 * 场景实例的处理
 * 场景实例的功能由本类对外提供
 */
@ClassFieldClasses({
        CollectionPluginSceneConfigBO.class,
        CollectionPluginSceneConfigElementBO.class,
        CollectionPluginSceneConfigElementPortBO.class,
        CollectionPluginSceneConfigModel.class,
        CollectionPluginSceneConfigElementModel.class,
        CollectionPluginSceneConfigElementPortModel.class,
})
@BeanFieldClasses({
        CollectionPluginSceneConfigMapper.class,
        CollectionPluginSceneConfigElementMapper.class,
        CollectionPluginSceneConfigElementPortMapper.class,
})
public class CollectionSceneConfigPluginModelHandler extends BasePluginModelHandler<
        CollectionPluginSceneConfigModel,
        CollectionPluginSceneConfigElementModel,
        CollectionPluginSceneConfigElementPortModel,
        CollectionPluginSceneConfigBO,
        CollectionPluginSceneConfigElementBO,
        CollectionPluginSceneConfigElementPortBO,
        CollectionPluginSceneConfigMapper,
        CollectionPluginSceneConfigElementMapper,
        CollectionPluginSceneConfigElementPortMapper,
        CollectionSceneConfigPluginHandler> {

    public CollectionSceneConfigPluginModelHandler(CollectionSceneConfigPluginHandler pluginHandler) {
        init(pluginHandler);
    }
}
