package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneDataBO;
import cn.cyberpeace.mp.scene.model.*;
import lombok.Data;

/**
 * @ClassName SceneConfigDataBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneDataBO
 */
@Data
public class SceneConfigDataBO extends BaseSceneDataBO<
        SceneConfigModel,
        SceneConfigAreaModel,
        SceneConfigElementModel,
        SceneConfigElementPortModel,
        SceneConfigElementPortConnectionModel,
        SceneConfigAreaNodeConnectionModel,
        SceneConfigPieceModel,
        SceneConfigBO,
        SceneConfigAreaBO,
        SceneConfigElementPortBO,
        SceneConfigElementBO,
        SceneConfigElementPortConnectionBO,
        SceneConfigAreaNodeConnectionBO,
        SceneConfigPieceBO,
        SceneConfigTopologyBO,
        SceneConfigDataBO> {

    public SceneDataBO exportToScene() {
        return export(SceneDataBO.class, SceneTopologyBO.class, SceneBO.class,
                SceneAreaBO.class, SceneElementBO.class, SceneElementPortBO.class,
                SceneElementPortConnectionBO.class,SceneAreaNodeConnectionBO.class, ScenePieceBO.class);
    }
}
