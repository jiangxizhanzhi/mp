 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneConfigElementPortConnectionBO.java.
 // File size 746.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortConnectionBO;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortConnectionModel;
import lombok.Data;

/**
 * @ClassName SceneConfigElementPortConnectionBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneElementPortConnectionBO
 */
@Data
public class SceneConfigElementPortConnectionBO
        extends BaseSceneElementPortConnectionBO<SceneConfigElementPortConnectionModel,
        SceneConfigElementPortConnectionBO> {

    @Override
    public SceneConfigElementPortConnectionModel createModel() {
        return new SceneConfigElementPortConnectionModel();
    }

}
