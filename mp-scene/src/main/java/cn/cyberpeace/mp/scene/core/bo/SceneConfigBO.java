 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneConfigBO.java.
 // File size 518.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import lombok.Data;

/**
 * @ClassName SceneConfigBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneBO
 */
@Data
public class SceneConfigBO extends BaseSceneBO<SceneConfigModel, SceneConfigBO> {

    @Override
    public SceneConfigModel createModel() {
        return new SceneConfigModel();
    }

}
