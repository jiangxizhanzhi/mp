 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudSceneConfigPluginModelHandler.java.
 // File size 3055.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.modelhandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
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
 * @ClassName CloudSceneConfigPluginModelHandler
 * @Description 场景实例的处理
 * 场景实例的功能由本类对外提供
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePluginModelHandler
 */
public class CloudSceneConfigPluginModelHandler extends BasePluginModelHandler<SceneConfigBO, SceneConfigElementBO, SceneConfigElementPortBO, CloudPluginSceneConfigModel, CloudPluginSceneConfigElementModel, CloudPluginSceneConfigElementPortModel, CloudPluginSceneConfigBO, CloudPluginSceneConfigElementBO, CloudPluginSceneConfigElementPortBO, CloudPluginSceneConfigMapper, CloudPluginSceneConfigElementMapper, CloudPluginSceneConfigElementPortMapper, CloudSceneConfigPluginHandler> {

    public CloudSceneConfigPluginModelHandler(CloudSceneConfigPluginHandler pluginHandler) {
        init(pluginHandler);
    }

    /**
     * initClass
     */
    @Override
    protected void initClass() {
        super.pluginSceneBOClass = CloudPluginSceneConfigBO.class;
        super.pluginSceneElementBOClass = CloudPluginSceneConfigElementBO.class;
        super.pluginSceneElementPortBOClass = CloudPluginSceneConfigElementPortBO.class;
        super.pluginSceneModelClass = CloudPluginSceneConfigModel.class;
        super.pluginSceneElementModelClass = CloudPluginSceneConfigElementModel.class;
        super.pluginSceneElementPortModelClass = CloudPluginSceneConfigElementPortModel.class;
    }

    /**
     * initMapper
     */
    @Override
    protected void initMapper() {
        super.pluginSceneMapper = BeanCommon.getBean(CloudPluginSceneConfigMapper.class);
        super.pluginSceneElementMapper = BeanCommon.getBean(CloudPluginSceneConfigElementMapper.class);
        super.pluginSceneElementPortMapper = BeanCommon.getBean(CloudPluginSceneConfigElementPortMapper.class);
    }

}
