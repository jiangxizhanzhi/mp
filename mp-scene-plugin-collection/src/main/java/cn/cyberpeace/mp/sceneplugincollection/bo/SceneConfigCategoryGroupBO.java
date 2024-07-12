 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneConfigCategoryGroupBO.java.
 // File size 594.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SceneConfigCategoryGroupBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class SceneConfigCategoryGroupBO {
    private int category;
    private List<SceneConfigElementBO> elements;

    public SceneConfigCategoryGroupBO(int category) {
        this.category = category;
        this.elements = new ArrayList<>();
    }
}
