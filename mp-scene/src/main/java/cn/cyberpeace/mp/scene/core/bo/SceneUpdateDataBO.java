package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneDataBO;
import cn.cyberpeace.mp.scene.model.SceneAreaModel;
import cn.cyberpeace.mp.scene.model.SceneConfigAreaModel;
import cn.cyberpeace.mp.scene.model.SceneConfigElementModel;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortConnectionModel;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortModel;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import cn.cyberpeace.mp.scene.model.SceneConfigPieceModel;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel;
import cn.cyberpeace.mp.scene.model.SceneElementPortModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.ScenePieceModel;
import lombok.Data;

@Data
public class SceneUpdateDataBO extends BaseSceneDataBO<
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
        SceneUpdateTopologyBO,
        SceneUpdateDataBO> {


    public SceneDataBO exportToScene() {
        var result = export(SceneDataBO.class, SceneTopologyBO.class, SceneBO.class,
                SceneAreaBO.class, SceneElementBO.class, SceneElementPortBO.class,
                SceneElementPortConnectionBO.class, ScenePieceBO.class);
        return result;
    }
}
