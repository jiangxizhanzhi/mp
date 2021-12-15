package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

@Data
public class BaseScenePieceModel extends BaseSceneNodeModel {
    @DefaultStr
    private String type;

    public static class Type {
        public static final String TEXT = "text";

        private Type() {}
    }
}
