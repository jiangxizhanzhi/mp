 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneConfigTopologyBO.java.
 // File size 903.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneTopologyBO;
import cn.cyberpeace.mp.scene.model.*;
import lombok.Data;

/**
 * @ClassName SceneConfigTopologyBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneTopologyBO
 */
@Data
public class SceneConfigTopologyBO extends BaseSceneTopologyBO<
        SceneConfigModel,
        SceneConfigAreaModel,
        SceneConfigElementModel,
        SceneConfigElementPortModel,
        SceneConfigElementPortConnectionModel,
        SceneConfigPieceModel, SceneConfigAreaNodeConnectionModel,
        SceneConfigBO,
        SceneConfigAreaBO,
        SceneConfigElementPortBO,
        SceneConfigElementBO,
        SceneConfigElementPortConnectionBO, SceneConfigAreaNodeConnectionBO,
        SceneConfigPieceBO,
        SceneConfigTopologyBO> {
}
