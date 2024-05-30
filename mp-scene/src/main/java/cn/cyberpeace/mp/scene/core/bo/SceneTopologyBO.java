package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneTopologyBO;
import cn.cyberpeace.mp.scene.model.*;
import lombok.Data;

@Data
public class SceneTopologyBO extends BaseSceneTopologyBO<
        SceneModel,
        SceneAreaModel,
        SceneElementModel,
        SceneElementPortModel,
        SceneElementPortConnectionModel,
        ScenePieceModel, SceneAreaNodeConnectionModel,
        SceneBO,
        SceneAreaBO,
        SceneElementPortBO,
        SceneElementBO,
        SceneElementPortConnectionBO, SceneAreaNodeConnectionBO,
        ScenePieceBO,
        SceneTopologyBO> {
}
