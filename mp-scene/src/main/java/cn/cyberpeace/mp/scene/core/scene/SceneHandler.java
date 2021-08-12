package cn.cyberpeace.mp.scene.core.scene;

import cn.cyberpeace.mp.base.annotation.BeanFieldClasses;
import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.base.BaseSceneHandler;
import cn.cyberpeace.mp.scene.core.bo.*;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.model.*;
import cn.cyberpeace.mp.scene.repository.*;

/**
 * 场景实例的处理
 * 场景实例的功能由本类对外提供
 */
@ClassFieldClasses({
        SceneDataBO.class,
        SceneTopologyBO.class,
        SceneBO.class,
        SceneAreaBO.class,
        SceneElementBO.class,
        SceneElementPortBO.class,
        SceneElementPortConnectionBO.class,
        SceneModel.class,
        SceneAreaModel.class,
        SceneElementModel.class,
        SceneElementPortModel.class,
        SceneElementPortConnectionModel.class,
        ScenePluginHandler.class,
        SceneHandler.class,
})
@BeanFieldClasses({
        SceneMapper.class,
        SceneAreaMapper.class,
        SceneElementMapper.class,
        SceneElementPortMapper.class,
        SceneElementPortConnectionMapper.class,
        SceneElementPluginMapper.class,
})
public class SceneHandler extends BaseSceneHandler<
        SceneModel,
        SceneAreaModel,
        SceneElementModel,
        SceneElementPortModel,
        SceneElementPortConnectionModel,
        SceneElementPluginModel,
        SceneBO,
        SceneAreaBO,
        SceneElementPortBO,
        SceneElementBO,
        SceneElementPortConnectionBO,
        SceneTopologyBO,
        SceneDataBO,
        SceneMapper,
        SceneAreaMapper,
        SceneElementMapper,
        SceneElementPortMapper,
        SceneElementPortConnectionMapper,
        SceneElementPluginMapper,
        ScenePluginHandler<?, ?, ?>,
        SceneHandler> {

    public SceneHandler(SceneDataBO sceneDataBO) throws SceneNotExistException {
        init(sceneDataBO, false);
    }

    public SceneHandler(SceneDataBO sceneDataBO, boolean isAreaMode) throws SceneNotExistException {
        init(sceneDataBO, isAreaMode);
    }

    public SceneHandler(String sceneId, String areaId) throws SceneNotExistException {
        init(sceneId, areaId, false);
    }

    public SceneHandler(String sceneId, String areaId, boolean isAreaMode) throws SceneNotExistException {
        init(sceneId, areaId, isAreaMode);
    }

}
