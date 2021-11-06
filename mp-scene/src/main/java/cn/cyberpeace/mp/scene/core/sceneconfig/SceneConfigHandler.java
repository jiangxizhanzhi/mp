package cn.cyberpeace.mp.scene.core.sceneconfig;

import cn.cyberpeace.mp.base.common.stereotype.BeanFieldClasses;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.base.BaseSceneHandler;
import cn.cyberpeace.mp.scene.core.bo.*;
import cn.cyberpeace.mp.scene.core.plugin.SceneConfigPluginHandler;
import cn.cyberpeace.mp.scene.model.*;
import cn.cyberpeace.mp.scene.repository.*;

/**
 * 场景模板的处理
 * 场景模板的功能由本类对外提供
 */
@ClassFieldClasses({
        SceneConfigDataBO.class,
        SceneConfigTopologyBO.class,
        SceneConfigBO.class,
        SceneConfigAreaBO.class,
        SceneConfigElementBO.class,
        SceneConfigElementPortBO.class,
        SceneConfigElementPortConnectionBO.class,
        SceneConfigModel.class,
        SceneConfigAreaModel.class,
        SceneConfigElementModel.class,
        SceneConfigElementPortModel.class,
        SceneConfigElementPortConnectionModel.class,
        SceneConfigElementPluginModel.class,
        SceneConfigPluginHandler.class,
        SceneConfigHandler.class,
})
@BeanFieldClasses({
        SceneConfigMapper.class,
        SceneConfigAreaMapper.class,
        SceneConfigElementMapper.class,
        SceneConfigElementPortMapper.class,
        SceneConfigElementPortConnectionMapper.class,
        SceneConfigElementPluginMapper.class,
})
public class SceneConfigHandler extends BaseSceneHandler<
        SceneConfigModel,
        SceneConfigAreaModel,
        SceneConfigElementModel,
        SceneConfigElementPortModel,
        SceneConfigElementPortConnectionModel,
        SceneConfigElementPluginModel,
        SceneConfigBO,
        SceneConfigAreaBO,
        SceneConfigElementPortBO,
        SceneConfigElementBO,
        SceneConfigElementPortConnectionBO,
        SceneConfigTopologyBO,
        SceneConfigDataBO,
        SceneConfigMapper,
        SceneConfigAreaMapper,
        SceneConfigElementMapper,
        SceneConfigElementPortMapper,
        SceneConfigElementPortConnectionMapper,
        SceneConfigElementPluginMapper,
        SceneConfigContextBO,
        SceneConfigPluginHandler<?, ?, ?, ?>,
        SceneConfigHandler> {

    public SceneConfigHandler() {}

    public SceneConfigHandler(SceneConfigDataBO sceneConfigDataBO, SceneConfigContextBO context) {
        init(sceneConfigDataBO, false, context);
    }

    public SceneConfigHandler(SceneConfigDataBO sceneConfigDataBO, boolean isAreaMode, SceneConfigContextBO context) {
        init(sceneConfigDataBO, isAreaMode, context);
    }

    public SceneConfigHandler(String sceneId, String areaId, SceneConfigContextBO context) {
        init(sceneId, areaId, false, context);
    }

    public SceneConfigHandler(String sceneId, String areaId, boolean isAreaMode, SceneConfigContextBO context) {
        init(sceneId, areaId, isAreaMode, context);
    }

    @Override
    public void beforeInitPlugin(SceneConfigPluginHandler<?, ?, ?, ?> plugin) {
        plugin.setSceneHandler(this);
    }

}
