package cn.cyberpeace.mp.scene.core.scene;

import cn.cyberpeace.mp.base.annotation.BeanFieldClasses;
import cn.cyberpeace.mp.scene.core.base.BaseSceneHandler;
import cn.cyberpeace.mp.scene.core.bo.*;
import cn.cyberpeace.mp.scene.model.*;
import cn.cyberpeace.mp.scene.repository.*;

@BeanFieldClasses({
        SceneMapper.class,
        SceneAreaMapper.class,
        SceneElementMapper.class,
        SceneElementPortMapper.class,
        SceneElementPortConnectionMapper.class,
})
public class SceneHandler extends BaseSceneHandler<
        SceneModel,
        SceneAreaModel,
        SceneElementModel,
        SceneElementPortModel,
        SceneElementPortConnectionModel,
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
        SceneElementPortConnectionMapper> {

    public SceneHandler(SceneDataBO sceneDataBO) {
        init(sceneDataBO, false);
    }

    public SceneHandler(SceneDataBO sceneDataBO, boolean isAreaMode) {
        init(sceneDataBO, isAreaMode);
    }

    @Override
    public SceneDataBO buildDataBO() {
        return new SceneDataBO();
    }

    @Override
    public SceneTopologyBO buildTopologyBO() {
        return new SceneTopologyBO();
    }
}
