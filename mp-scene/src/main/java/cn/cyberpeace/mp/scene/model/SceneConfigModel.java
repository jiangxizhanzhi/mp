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
    @DefaultInt(2)
    private Integer type;

    public static class Type {
        public static final Integer PURE = 1;
        public static final Integer NORMAL = 2;
        public static final Integer SNAPSHOT = 3;

        private Type() {}
    }
}
