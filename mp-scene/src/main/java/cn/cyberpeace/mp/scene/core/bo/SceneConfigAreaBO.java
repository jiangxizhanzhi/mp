package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneAreaBO;
import cn.cyberpeace.mp.scene.model.SceneConfigAreaModel;
import lombok.Data;

@Data
public class SceneConfigAreaBO extends BaseSceneAreaBO<SceneConfigAreaModel> {

    @Override
    public SceneConfigAreaModel createModel() {
        return new SceneConfigAreaModel();
    }

}
