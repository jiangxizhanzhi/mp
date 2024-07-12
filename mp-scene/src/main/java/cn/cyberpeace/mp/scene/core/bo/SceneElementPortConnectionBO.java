 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneElementPortConnectionBO.java.
 // File size 695.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortConnectionBO;
import cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel;
import lombok.Data;

/**
 * @ClassName SceneElementPortConnectionBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneElementPortConnectionBO
 */
@Data
public class SceneElementPortConnectionBO
        extends BaseSceneElementPortConnectionBO<SceneElementPortConnectionModel, SceneElementPortConnectionBO> {

    @Override
    public SceneElementPortConnectionModel createModel() {
        return new SceneElementPortConnectionModel();
    }

}
