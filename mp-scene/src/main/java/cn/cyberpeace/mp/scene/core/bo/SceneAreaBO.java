package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneAreaBO;
import cn.cyberpeace.mp.scene.model.SceneAreaModel;
import lombok.Data;

@Data
public class SceneAreaBO extends BaseSceneAreaBO<SceneAreaModel> {

    @Override
    public SceneAreaModel createModel() {
        return new SceneAreaModel();
    }

}
