package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import lombok.Data;

@Data
public class BaseSceneAreaModel extends BaseSceneNodeModel {
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
