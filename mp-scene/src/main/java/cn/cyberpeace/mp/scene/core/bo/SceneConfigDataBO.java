package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneDataBO;
import cn.cyberpeace.mp.scene.model.*;
import lombok.Data;

@Data
public class SceneConfigDataBO extends BaseSceneDataBO<
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
        SceneConfigDataBO> {

    public SceneDataBO exportToScene() {
        return export(SceneDataBO.class, SceneTopologyBO.class, SceneBO.class,
                SceneAreaBO.class, SceneElementBO.class, SceneElementPortBO.class,
                SceneElementPortConnectionBO.class);
    }
}
