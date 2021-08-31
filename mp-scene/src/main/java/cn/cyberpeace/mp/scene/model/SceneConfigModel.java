package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
