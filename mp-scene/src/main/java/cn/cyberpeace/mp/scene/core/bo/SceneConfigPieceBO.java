package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseScenePieceBO;
import cn.cyberpeace.mp.scene.model.SceneConfigPieceModel;
import lombok.Data;

@Data
public class SceneConfigPieceBO extends BaseScenePieceBO<SceneConfigPieceModel, SceneConfigPieceBO> {

    @Override
    public SceneConfigPieceModel createModel() {
        return new SceneConfigPieceModel();
    }

}
