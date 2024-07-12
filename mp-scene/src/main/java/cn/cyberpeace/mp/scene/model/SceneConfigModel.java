 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneConfigModel.java.
 // File size 848.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName SceneConfigModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_scene_scene_config")
public class SceneConfigModel extends BaseSceneModel {
    @DefaultInt(Type.NORMAL)
    private Integer type;

    public static class Type {
        public static final int PURE = 1;
        public static final int NORMAL = 2;
        public static final int SNAPSHOT = 3;

        private Type() {}
    }
}
