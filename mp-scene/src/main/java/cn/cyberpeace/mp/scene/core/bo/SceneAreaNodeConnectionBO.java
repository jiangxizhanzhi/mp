 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneAreaNodeConnectionBO.java.
 // File size 686.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneAreaNodeConnectionBO;
import cn.cyberpeace.mp.scene.model.SceneAreaNodeConnectionModel;
import lombok.Data;

/**
 * @ClassName SceneAreaNodeConnectionBO
 * @Description 场景节点连接bo
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneAreaNodeConnectionBO
 */
@Data
public class SceneAreaNodeConnectionBO
        extends BaseSceneAreaNodeConnectionBO<SceneAreaNodeConnectionModel, SceneAreaNodeConnectionBO> {

    @Override
    public SceneAreaNodeConnectionModel createModel() {
        return new SceneAreaNodeConnectionModel();
    }

}
