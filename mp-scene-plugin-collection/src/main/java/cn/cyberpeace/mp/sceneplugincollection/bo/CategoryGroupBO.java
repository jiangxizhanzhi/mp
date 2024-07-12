 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CategoryGroupBO.java.
 // File size 549.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CategoryGroupBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class CategoryGroupBO {
    private int category;
    private List<SceneElementBO> elements;

    public CategoryGroupBO(int category) {
        this.category = category;
        this.elements = new ArrayList<>();
    }
}
