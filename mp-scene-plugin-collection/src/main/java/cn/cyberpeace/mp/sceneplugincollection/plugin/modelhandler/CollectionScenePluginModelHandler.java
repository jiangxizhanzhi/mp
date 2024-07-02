package cn.cyberpeace.mp.sceneplugincollection.plugin.modelhandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
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
    /**
     * CollectionScenePluginModelHandler
     *
     * @param pluginHandler
     */
    public CollectionScenePluginModelHandler(CollectionScenePluginHandler pluginHandler) {
        init(pluginHandler);
    }

    /**
     * initClass
     */
    @Override
    protected void initClass() {
        super.pluginSceneBOClass = CollectionPluginSceneBO.class;
        super.pluginSceneElementBOClass = CollectionPluginSceneElementBO.class;
        super.pluginSceneElementPortBOClass = CollectionPluginSceneElementPortBO.class;
        super.pluginSceneModelClass = CollectionPluginSceneModel.class;
        super.pluginSceneElementModelClass = CollectionPluginSceneElementModel.class;
        super.pluginSceneElementPortModelClass = CollectionPluginSceneElementPortModel.class;
    }

    /**
     * initMapper
     */
    @Override
    protected void initMapper() {
        super.pluginSceneMapper = BeanCommon.getBean(CollectionPluginSceneMapper.class);
        super.pluginSceneElementMapper = BeanCommon.getBean(CollectionPluginSceneElementMapper.class);
        super.pluginSceneElementPortMapper = BeanCommon.getBean(CollectionPluginSceneElementPortMapper.class);
    }
}
