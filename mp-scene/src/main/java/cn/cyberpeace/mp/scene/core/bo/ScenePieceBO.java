package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseScenePieceBO;
import cn.cyberpeace.mp.scene.model.ScenePieceModel;
import lombok.Data;

@Data
public class ScenePieceBO extends BaseScenePieceBO<ScenePieceModel, ScenePieceBO> {

    @Override
    public ScenePieceModel createModel() {
        return new ScenePieceModel();
    }

}
