package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

/**
 * @ClassName BaseScenePieceModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneNodeModel
 */
@Data
public class BaseScenePieceModel extends BaseSceneNodeModel {
    @DefaultStr
    private String type;

    public static class Type {
        public static final String TEXT = "text";

        private Type() {}
    }
}
