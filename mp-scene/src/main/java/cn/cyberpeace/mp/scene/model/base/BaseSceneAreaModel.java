package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import lombok.Data;

@Data
public class BaseSceneAreaModel extends BaseSceneNodeModel {
    @DefaultBool
    private Boolean open;
}
