package cn.cyberpeace.mp.scene.core.vo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaModel;
import lombok.Data;

@Data
public abstract class BaseSceneAreaVO<M extends BaseSceneAreaModel> extends BaseSceneNodeVO<M> {
    private Boolean open;
}
