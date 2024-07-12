 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneConfigDataBO.java.
 // File size 1250.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
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
