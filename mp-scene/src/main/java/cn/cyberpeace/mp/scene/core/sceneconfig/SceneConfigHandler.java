package cn.cyberpeace.mp.scene.core.sceneconfig;

import cn.cyberpeace.mp.base.annotation.BeanFieldClasses;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.scene.core.base.BaseSceneHandler;
import cn.cyberpeace.mp.scene.core.bo.*;
import cn.cyberpeace.mp.scene.model.*;
import cn.cyberpeace.mp.scene.repository.*;

@BeanFieldClasses({
        SceneConfigMapper.class,
        SceneConfigAreaMapper.class,
        SceneConfigElementMapper.class,
        SceneConfigElementPortMapper.class,
        SceneConfigElementPortConnectionMapper.class,
})
public class SceneConfigHandler extends BaseSceneHandler<
        SceneConfigModel,
        SceneConfigAreaModel,
        SceneConfigElementModel,
        SceneConfigElementPortModel,
        SceneConfigElementPortConnectionModel,
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
        SceneConfigElementPortConnectionMapper> {

    public SceneConfigHandler(SceneConfigDataBO sceneConfigDataBO) {
        BeanUtils.loadBeanFields(this);
        initFromSceneDataBO(sceneConfigDataBO);
    }
}
