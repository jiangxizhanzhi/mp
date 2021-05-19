package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaModel;
import lombok.Data;

@Data
public abstract class BaseSceneAreaBO<M extends BaseSceneAreaModel> extends BaseSceneNodeBO<M> {
    private Boolean open;
}
