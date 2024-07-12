 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneElementPortBO.java.
 // File size 749.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.model.SceneElementPortModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @ClassName SceneElementPortBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneElementPortBO
 */
@Data
public class SceneElementPortBO extends BaseSceneElementPortBO<SceneElementPortModel, SceneElementPortBO, SceneElementBO> {

    @JsonIgnore
    private boolean using = true;

    private boolean change = false ;

    @Override
    public SceneElementPortModel createModel() {
        return new SceneElementPortModel();
    }

}
