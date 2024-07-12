 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneConfigPieceBO.java.
 // File size 568.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseScenePieceBO;
import cn.cyberpeace.mp.scene.model.SceneConfigPieceModel;
import lombok.Data;

/**
 * @ClassName SceneConfigPieceBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseScenePieceBO
 */
@Data
public class SceneConfigPieceBO extends BaseScenePieceBO<SceneConfigPieceModel, SceneConfigPieceBO> {

    @Override
    public SceneConfigPieceModel createModel() {
        return new SceneConfigPieceModel();
    }

}
