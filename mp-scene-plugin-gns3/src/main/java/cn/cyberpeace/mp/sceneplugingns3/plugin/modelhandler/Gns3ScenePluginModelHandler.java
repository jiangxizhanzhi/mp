package cn.cyberpeace.mp.sceneplugingns3.plugin.modelhandler;

import cn.cyberpeace.mp.base.common.stereotype.BeanFieldClasses;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.base.BasePluginModelHandler;
import cn.cyberpeace.mp.sceneplugingns3.bo.Gns3PluginSceneBO;
import cn.cyberpeace.mp.sceneplugingns3.bo.Gns3PluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugingns3.bo.Gns3PluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneElementPortModel;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneModel;
import cn.cyberpeace.mp.sceneplugingns3.plugin.Gns3ScenePluginHandler;
import cn.cyberpeace.mp.sceneplugingns3.repository.Gns3PluginSceneElementMapper;
import cn.cyberpeace.mp.sceneplugingns3.repository.Gns3PluginSceneElementPortMapper;
import cn.cyberpeace.mp.sceneplugingns3.repository.Gns3PluginSceneMapper;


@ClassFieldClasses({
        Gns3PluginSceneBO.class,
        Gns3PluginSceneElementBO.class,
        Gns3PluginSceneElementPortBO.class,
        Gns3PluginSceneModel.class,
        Gns3PluginSceneElementModel.class,
        Gns3PluginSceneElementPortModel.class,
})
@BeanFieldClasses({
        Gns3PluginSceneMapper.class,
        Gns3PluginSceneElementMapper.class,
        Gns3PluginSceneElementPortMapper.class,
})
public class Gns3ScenePluginModelHandler extends BasePluginModelHandler<
        SceneBO,
        SceneElementBO,
        SceneElementPortBO,
        Gns3PluginSceneModel,
        Gns3PluginSceneElementModel,
        Gns3PluginSceneElementPortModel,
        Gns3PluginSceneBO,
        Gns3PluginSceneElementBO,
        Gns3PluginSceneElementPortBO,
        Gns3PluginSceneMapper,
        Gns3PluginSceneElementMapper,
        Gns3PluginSceneElementPortMapper,
        Gns3ScenePluginHandler> {
    public Gns3ScenePluginModelHandler(Gns3ScenePluginHandler pluginHandler) {
        init(pluginHandler);
    }
}
