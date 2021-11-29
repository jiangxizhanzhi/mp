package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.model.SceneElementPortModel;
import lombok.Data;

@Data
public class SceneElementPortBO extends BaseSceneElementPortBO<SceneElementPortModel, SceneElementPortBO, SceneElementBO> {

    @Override
    public SceneElementPortModel createModel() {
        return new SceneElementPortModel();
    }

}
