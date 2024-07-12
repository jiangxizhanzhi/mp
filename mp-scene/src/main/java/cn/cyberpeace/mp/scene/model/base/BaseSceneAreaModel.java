 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseSceneAreaModel.java.
 // File size 689.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import lombok.Data;

/**
 * @ClassName BaseSceneAreaModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneNodeModel
 */
@Data
public class BaseSceneAreaModel extends BaseSceneNodeModel {
    @Deprecated
    @DefaultBool
    private Boolean open;

    @DefaultInt
    private Integer type;

    public static class Type {
        private Type() {

        }

        public static final int COMMON = 0;

        public static final int LAYER = 1;
    }
}
