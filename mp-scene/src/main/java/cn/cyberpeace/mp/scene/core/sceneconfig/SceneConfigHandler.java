package cn.cyberpeace.mp.scene.core.sceneconfig;

import cn.cyberpeace.mp.base.annotation.BeanFieldClasses;
import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.base.BaseSceneHandler;
import cn.cyberpeace.mp.scene.core.bo.*;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
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
        SceneConfigPluginHandler<?, ?, ?>,
        SceneConfigHandler> {

    public SceneConfigHandler(SceneConfigDataBO sceneConfigDataBO) throws SceneNotExistException {
        init(sceneConfigDataBO, false);
    }

    public SceneConfigHandler(SceneConfigDataBO sceneConfigDataBO, boolean isAreaMode) throws SceneNotExistException {
        init(sceneConfigDataBO, isAreaMode);
    }

    public SceneConfigHandler(String sceneId, String areaId) throws SceneNotExistException {
        init(sceneId, areaId, false);
    }

    public SceneConfigHandler(String sceneId, String areaId, boolean isAreaMode) throws SceneNotExistException {
        init(sceneId, areaId, isAreaMode);
    }

}
