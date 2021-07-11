package cn.cyberpeace.mp.sceneplugingns3.plugin.modelhandler;

import cn.cyberpeace.mp.base.common.stereotype.BeanFieldClasses;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginModelHandler;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementModel;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementPortModel;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneModel;
import cn.cyberpeace.mp.sceneplugingns3.bo.Gns3PluginSceneConfigBO;
import cn.cyberpeace.mp.sceneplugingns3.bo.Gns3PluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugingns3.bo.Gns3PluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneConfigElementModel;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneConfigElementPortModel;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneConfigModel;
import cn.cyberpeace.mp.sceneplugingns3.plugin.Gn3SceneConfigPluginHandler;
import cn.cyberpeace.mp.sceneplugingns3.repository.Gns3PluginSceneConfigElementMapper;
import cn.cyberpeace.mp.sceneplugingns3.repository.Gns3PluginSceneConfigElementPortMapper;
import cn.cyberpeace.mp.sceneplugingns3.repository.Gns3PluginSceneConfigMapper;



@ClassFieldClasses({
        Gns3PluginSceneConfigBO.class,
        Gns3PluginSceneConfigElementBO.class,
        Gns3PluginSceneConfigElementPortBO.class,
        BasePluginSceneModel.class,
        BasePluginSceneElementModel.class,
        BasePluginSceneElementPortModel.class,
})
@BeanFieldClasses({
        Gns3PluginSceneConfigMapper.class,
        Gns3PluginSceneConfigElementMapper.class,
        Gns3PluginSceneConfigElementPortMapper.class,
})
public class Gns3SceneConfigPluginModelHandler extends BasePluginModelHandler<
        SceneConfigBO,
        SceneConfigElementBO,
        SceneConfigElementPortBO,
        Gns3PluginSceneConfigModel,
        Gns3PluginSceneConfigElementModel,
        Gns3PluginSceneConfigElementPortModel,
        Gns3PluginSceneConfigBO,
        Gns3PluginSceneConfigElementBO,
        Gns3PluginSceneConfigElementPortBO,
        Gns3PluginSceneConfigMapper,
        Gns3PluginSceneConfigElementMapper,
        Gns3PluginSceneConfigElementPortMapper,
        Gn3SceneConfigPluginHandler> {

    public Gns3SceneConfigPluginModelHandler(Gn3SceneConfigPluginHandler pluginHandler) {
        init(pluginHandler);
    }
}
