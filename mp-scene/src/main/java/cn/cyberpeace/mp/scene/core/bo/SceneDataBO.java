 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneDataBO.java.
 // File size 1221.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneDataBO;
import cn.cyberpeace.mp.scene.model.*;
import lombok.Data;

/**
 * @ClassName SceneDataBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneDataBO
 */
@Data
public class SceneDataBO extends BaseSceneDataBO<
        SceneModel,
        SceneAreaModel,
        SceneElementModel,
        SceneElementPortModel,
        SceneElementPortConnectionModel,
        SceneAreaNodeConnectionModel,
        ScenePieceModel,
        SceneBO,
        SceneAreaBO,
        SceneElementPortBO,
        SceneElementBO,
        SceneElementPortConnectionBO,
        SceneAreaNodeConnectionBO,
        ScenePieceBO,
        SceneTopologyBO,
        SceneDataBO> {

    public SceneConfigDataBO exportToSceneConfig() {
        return export(SceneConfigDataBO.class, SceneConfigTopologyBO.class, SceneConfigBO.class,
                      SceneConfigAreaBO.class, SceneConfigElementBO.class, SceneConfigElementPortBO.class,
                      SceneConfigElementPortConnectionBO.class, SceneConfigAreaNodeConnectionBO.class, SceneConfigPieceBO.class);
    }
}
