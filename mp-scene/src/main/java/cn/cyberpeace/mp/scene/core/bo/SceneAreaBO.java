 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneAreaBO.java.
 // File size 516.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneAreaBO;
import cn.cyberpeace.mp.scene.model.SceneAreaModel;
import lombok.Data;

/**
 * @ClassName SceneAreaBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneAreaBO
 */
@Data
public class SceneAreaBO extends BaseSceneAreaBO<SceneAreaModel, SceneAreaBO> {

    @Override
    public SceneAreaModel createModel() {
        return new SceneAreaModel();
    }

}
