package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseScenePieceModel;
import lombok.Data;

@Data
public abstract class BaseScenePieceBO<M extends BaseScenePieceModel, T extends BaseScenePieceBO<M, T>>
        extends BaseSceneNodeBO<M, T> {
}
