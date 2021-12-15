package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneDataBO;
import cn.cyberpeace.mp.scene.model.*;
import lombok.Data;

@Data
public class SceneDataBO extends BaseSceneDataBO<
        SceneModel,
        SceneAreaModel,
        SceneElementModel,
        SceneElementPortModel,
        SceneElementPortConnectionModel,
        ScenePieceModel,
        SceneBO,
        SceneAreaBO,
        SceneElementPortBO,
        SceneElementBO,
        SceneElementPortConnectionBO,
        ScenePieceBO,
        SceneTopologyBO,
        SceneDataBO> {

    public SceneConfigDataBO exportToSceneConfig() {
        return export(SceneConfigDataBO.class, SceneConfigTopologyBO.class, SceneConfigBO.class,
                SceneConfigAreaBO.class, SceneConfigElementBO.class, SceneConfigElementPortBO.class,
                SceneConfigElementPortConnectionBO.class, SceneConfigPieceBO.class);
    }
}
