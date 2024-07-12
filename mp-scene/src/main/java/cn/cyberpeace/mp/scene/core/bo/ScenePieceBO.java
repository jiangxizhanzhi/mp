 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ScenePieceBO.java.
 // File size 526.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseScenePieceBO;
import cn.cyberpeace.mp.scene.model.ScenePieceModel;
import lombok.Data;

/**
 * @ClassName ScenePieceBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseScenePieceBO
 */
@Data
public class ScenePieceBO extends BaseScenePieceBO<ScenePieceModel, ScenePieceBO> {

    @Override
    public ScenePieceModel createModel() {
        return new ScenePieceModel();
    }

}
