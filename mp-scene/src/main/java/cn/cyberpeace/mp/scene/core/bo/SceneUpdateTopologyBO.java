package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneTopologyBO;
import cn.cyberpeace.mp.scene.model.SceneAreaModel;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel;
import cn.cyberpeace.mp.scene.model.SceneElementPortModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.ScenePieceModel;
import lombok.Data;

@Data
public class SceneUpdateTopologyBO extends BaseSceneTopologyBO<
        SceneModel,
        SceneAreaModel,
        SceneElementModel,
        SceneElementPortModel,
        SceneElementPortConnectionModel,
        ScenePieceModel,
        SceneUpdateBO,
        SceneAreaBO,
        SceneUpdateElementPortBO,
        SceneUpdateElementBO,
        SceneElementPortConnectionBO,
        ScenePieceBO,
        SceneUpdateTopologyBO> {
}
