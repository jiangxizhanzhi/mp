 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectionSceneConfigPluginModelHandler.java.
 // File size 3498.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.plugin.modelhandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
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
 * @ClassName CollectionSceneConfigPluginModelHandler
 * @Description 场景实例的处理
 * 场景实例的功能由本类对外提供
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePluginModelHandler
 */
public class CollectionSceneConfigPluginModelHandler extends BasePluginModelHandler<
        SceneConfigBO,
        SceneConfigElementBO,
        SceneConfigElementPortBO,
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

    /**
     * CollectionSceneConfigPluginModelHandler
     *
     * @param pluginHandler
     */
    public CollectionSceneConfigPluginModelHandler(CollectionSceneConfigPluginHandler pluginHandler) {
        init(pluginHandler);
    }

    /**
     * initClass
     */
    @Override
    protected void initClass() {
        super.pluginSceneBOClass = CollectionPluginSceneConfigBO.class;
        super.pluginSceneElementBOClass = CollectionPluginSceneConfigElementBO.class;
        super.pluginSceneElementPortBOClass = CollectionPluginSceneConfigElementPortBO.class;
        super.pluginSceneModelClass = CollectionPluginSceneConfigModel.class;
        super.pluginSceneElementModelClass = CollectionPluginSceneConfigElementModel.class;
        super.pluginSceneElementPortModelClass = CollectionPluginSceneConfigElementPortModel.class;
    }

    /**
     * initMapper
     */
    @Override
    protected void initMapper() {
        super.pluginSceneMapper = BeanCommon.getBean(CollectionPluginSceneConfigMapper.class);
        super.pluginSceneElementMapper = BeanCommon.getBean(CollectionPluginSceneConfigElementMapper.class);
        super.pluginSceneElementPortMapper = BeanCommon.getBean(CollectionPluginSceneConfigElementPortMapper.class);
    }


}
