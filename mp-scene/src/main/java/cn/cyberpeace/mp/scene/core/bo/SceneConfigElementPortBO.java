 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneConfigElementPortBO.java.
 // File size 659.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortModel;
import lombok.Data;

/**
 * @ClassName SceneConfigElementPortBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneElementPortBO
 */
@Data
public class SceneConfigElementPortBO
        extends BaseSceneElementPortBO<SceneConfigElementPortModel, SceneConfigElementPortBO, SceneConfigElementBO> {

    @Override
    public SceneConfigElementPortModel createModel() {
        return new SceneConfigElementPortModel();
    }

}
