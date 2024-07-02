package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseScenePieceBO;
import cn.cyberpeace.mp.scene.model.SceneConfigPieceModel;
import lombok.Data;

/**
 * @ClassName SceneConfigPieceBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseScenePieceBO
 */
@Data
public class SceneConfigPieceBO extends BaseScenePieceBO<SceneConfigPieceModel, SceneConfigPieceBO> {

    @Override
    public SceneConfigPieceModel createModel() {
        return new SceneConfigPieceModel();
    }

}
